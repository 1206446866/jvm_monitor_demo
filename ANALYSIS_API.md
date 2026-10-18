# 分析能力接入说明

本轮将已有分析代码接到真实 Agent 采集、异步聚合、HTTP 查询和 WebSocket 推送中。当前仍为同一个 JVM 内的分析，无跨进程上报或独立前端。

## 已接入的能力

| 能力 | 实现与输出 |
| --- | --- |
| 调用树 | 根据 spanId / parentSpanId 建树；支持乱序、缺失父节点和多根数据，环形引用不会进入输出树 |
| 火焰图 | 返回 name / x / y / width / selfCost；所有 x 坐标统一相对 Trace 起点，支持三层及更深调用 |
| 热点 | 按方法聚合 count / totalCost / selfCost / errorCount，按自身耗时降序排列 |
| 分位数 | 方法指标与 Dashboard、WebSocket 中填充最近 60 秒样本的 P95 / P99，采用 nearest-rank 算法 |
| 慢调用 | 统一为耗时严格大于 100 ms；提供慢 Span 和方法到慢 Trace 的下钻 |
| 指标与 Trace 关联 | 方法指标返回 traceIds / slowTraceIds，可进一步查询 Trace 摘要及分析结果 |
| 拓扑 | 消费完整 Trace 时记录跨业务类父子调用；同类内部调用不产生边 |

时间字段统一采用纳秒，分析结果、方法指标和摘要显式返回 `timeUnit: "ns"`。热点的 `totalCost` 包含子调用；`selfCost` 从本方法耗时中扣除直接子调用区间的并集，避免重叠调用重复扣减。

对于缺失父节点或多个根的调用链，调用树返回 `spanId: "virtual-root"`、`method: "[trace]"` 的展示根，以保留所有节点。

## HTTP 接口

| 接口 | 用途 |
| --- | --- |
| `GET /api/traces?limit=20` | 按调用开始时间倒序列出已完成根调用的 Trace 摘要 |
| `GET /api/trace/{traceId}` | 原始 Span 列表 |
| `GET /api/trace/{traceId}/tree` | 调用树，包含 depth、startTime、cost、selfCost 和 children |
| `GET /api/trace/{traceId}/flamegraph` | 可直接用于绘图的扁平火焰图节点 |
| `GET /api/trace/{traceId}/analysis` | 完整分析：summary、tree、flameGraph、hotspots、slowSpans |
| `GET /api/metrics/slow-methods?limit=10` | 慢方法列表，新增 P95/P99 和关联 Trace ID |
| `GET /api/metrics/method?method=完整方法名` | 单个方法的窗口统计、分位数及关联 Trace ID |
| `GET /api/metrics/traces?method=完整方法名&slowOnly=false&limit=20` | 根据方法查询关联 Trace 摘要；slowOnly=true 只查慢调用 |
| `GET /api/topology` | 按业务类聚合的调用关系及累计次数 |
| `GET /api/dashboard/overview` | 概览与增强后的慢方法指标 |

`limit` 范围为 1–100；非法范围、空白 method 返回 HTTP 400。不存在的 Trace 或单方法详情返回 HTTP 404；没有关联记录的列表返回空数组。

`/ws/metrics` 继续每秒推送真实 JVM 数据和 Dashboard 指标，其中 `topSlowMethods` 自动包含 P95/P99、traceIds 和 slowTraceIds，不广播整棵调用树。

## 启动和查询

在项目根目录执行：

```powershell
mvn clean package
java -javaagent:monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar -jar monitor-server/target/monitor-server-1.0-SNAPSHOT.jar --server.port=18080
```

Agent 默认增强示例业务 service 包，排除监控自身的 MetricsPushService。示例 DatabaseService 使用原有的模拟延迟，不连接外部数据库。调用路径为：

```text
UserService.queryUser
  └─ UserService.queryDb
       └─ DatabaseService.query
```

在另一个 PowerShell 终端中查询：

```powershell
$base = 'http://127.0.0.1:18080'
Invoke-RestMethod "$base/api/test-agent"
$method = [uri]::EscapeDataString('com.demo.monitor.server.service.UserService.queryUser')
$metric = Invoke-RestMethod "$base/api/metrics/method?method=$method"
$metric | ConvertTo-Json -Depth 10

$traceId = $metric.traceIds[0]
Invoke-RestMethod "$base/api/trace/$traceId/analysis" | ConvertTo-Json -Depth 20
Invoke-RestMethod "$base/api/topology" | ConvertTo-Json -Depth 10
```

聚合在后台执行。如果触发业务后立即查询时指标尚未出现，稍后再查询。

## 验证

`mvn clean package` 自动运行 monitor-core 的 8 个 JUnit 回归测试，覆盖乱序三层调用、断链、多根、环形引用、重叠子调用自身耗时、摘要与慢阈值、分位数窗口过期、索引去重与容量上限、时钟与业务类命名。

打包后的独立探针验证：

```powershell
java -javaagent:monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar=com.demo.fixture. -cp "monitor-agent/target/test-classes;monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar" com.demo.agent.AgentSmokeCheck
```

完整端到端验证需要新启动且没有触发测试调用的服务：

```powershell
java -cp "monitor-agent/target/test-classes;monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar" com.demo.agent.ServerSmokeCheck http://127.0.0.1:18080
```

本次实测上述检查通过。端到端校验覆盖：两次请求得到 6 条根 Trace，四个方法计数分别为 2 / 2 / 4 / 4；P95/P99 非零；慢调用下钻；三层调用树与火焰图根相对坐标；热点自身耗时；UserService → DatabaseService 的 4 次拓扑调用；HTTP 400/404；WebSocket 分析指标。

## 数据口径和边界

- P95/P99、平均耗时和方法调用数使用 60 秒窗口。Trace 索引是最近历史，独立于该窗口：每个方法最多保留 1000 个唯一 Trace ID 和 1000 个慢调用 Span，指标默认展示最近 20 个 ID。
- 本轮保留旧接口 `qps` 的数值口径：慢方法中仍代表窗口调用数，全局值仍为整数形式的 60 秒均值。
- 拓扑节点是 JVM 内的完整业务类名，不代表远端服务或真实数据库实例。
- 原始 Trace 存储仍在内存中，未增加全局过期机制。异步上下文工具仍需调用方显式包装任务；本轮未加入线程池自动增强。
