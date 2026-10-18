# Agent 探针链路排查报告

排查日期：2026-10-18。

后续分析接入已扩展默认业务采集范围并新增 DatabaseService 示例，最新接口、分析能力与回归方式见 [ANALYSIS_API.md](ANALYSIS_API.md)。下文保留本次初始故障排查时的记录。

## 已复现的故障

原有 JAR 使用 `-javaagent` 启动 Spring Boot 服务后，`/api/test-agent` 返回 200 和“Agent 已收集数据”，但 `/api/metrics/slow-methods` 返回空数组，Dashboard 返回固定的 `DEBUG` 数据。

给 Byte Buddy 加上错误监听后，启动时实际出现：

```text
[Byte Buddy] ERROR com.demo.monitor.server.service.UserService
java.lang.NoClassDefFoundError: com/demo/monitor/core/model/Span
Caused by: java.lang.ClassNotFoundException: com.demo.monitor.core.model.Span
```

`monitor-agent` 将 `monitor-core` 声明成 provided，core 没有进入探针 JAR。Agent 在系统类加载器中加载，Spring Boot 内嵌的 core 在子类加载器中；Agent 解析 TraceAdvice 的方法签名时无法找到 Span，增强失败。由于原先没有错误监听，故障被隐藏。

## 修复内容

1. **加载和增强**：将 core 及必要依赖打入 Agent；排除宿主自行提供的 SLF4J，避免系统类加载器抢先初始化 NOPLogger 导致 Spring Boot 日志启动失败。core 补齐源码直接使用的 Byte Buddy 依赖，完整清理后也能构建。
2. **保留业务行为**：使用 Advice visitor 包裹原方法，排除构造器、抽象方法、native 方法和编译器生成方法；跳过代理生成类，避免重复采集。增加增强错误输出；Advice 采集异常不会覆盖业务异常。默认匹配示例 UserService，可通过 Agent 参数指定业务包前缀。
3. **采集与聚合**：首次使用队列时启动唯一的后台守护消费者。根 Span 完成后入队，消费者读取该 trace 的全部 Span，统一更新方法窗口、累计指标和分析索引；全局指标只统计根 Span，嵌套方法不会重复增加全局请求数。控制台导出包含完整父子调用链。
4. **接口与推送**：Dashboard 使用真实聚合结果并填写时间戳；Trace 接口显式指定路径参数名称。WebSocket 定时广播统一由 MetricsPushService 发出，每条消息同时包含真实 JVM 数据与 Dashboard 指标，移除随机 CPU、重复定时广播及 hello 测试消息；发送操作按连接串行处理。
5. **消除误导**：测试接口只说明测试调用已完成，提示通过指标接口确认采集结果，不再无条件声称探针采集成功。

修复后的链路：

```text
-javaagent → TraceAdvice → TraceManager / TraceSpanStorage
           → 根 Span 队列 → BatchTraceWorker → MetricsRegistry / ObservabilityBridge / GlobalSlidingWindow
           → HTTP Dashboard / 方法指标 / Trace 查询 + WebSocket
```

## 验证结果

- `mvn -q clean package` 完整构建成功。
- 独立 JVM 使用打包后的 Agent 执行 `AgentSmokeCheck`：core 可见性、嵌套父子关系、私有方法增强、返回值不变、业务异常对象不变、异常后上下文清理、方法各计数一次、全局只统计根调用均通过。
- 新启动的 Spring Boot 可执行 JAR 执行 `ServerSmokeCheck`：两次测试调用后，login / queryUser / queryDb 的计数分别为 2 / 2 / 4；Dashboard 返回真实方法；WebSocket 收到包含真实 JVM 和相同方法指标的完整消息。
- `/api/trace/{traceId}` 实测返回 queryUser 和内部 queryDb 两个 Span，traceId 相同且 parentSpanId 正确。
- 最终 Agent JAR 包含 `com/demo/monitor/core/model/Span.class`，不包含 `org/slf4j/LoggerFactory.class`。

Smoke check 是显式运行的 Java 校验程序，不是由 Surefire 自动发现的 JUnit 测试。

## 启动与复验

在项目根目录执行：

```powershell
mvn clean package
java -javaagent:monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar -jar monitor-server/target/monitor-server-1.0-SNAPSHOT.jar
```

访问 `/api/test-agent` 触发业务，再检查 `/api/metrics/slow-methods` 和 `/api/dashboard/overview`；WebSocket 地址为 `/ws/metrics`。

使用其他业务包时，Agent 参数是包前缀，例如 `-javaagent:monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar=com.example.service.`。

独立探针回归：

```powershell
java -javaagent:monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar=com.demo.fixture. -cp "monitor-agent/target/test-classes;monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar" com.demo.agent.AgentSmokeCheck
```

端到端回归需要一个新启动、没有触发过测试调用的服务，示例使用 `--server.port=18080`：

```powershell
java -cp "monitor-agent/target/test-classes;monitor-agent/target/monitor-agent-1.0-SNAPSHOT.jar" com.demo.agent.ServerSmokeCheck http://127.0.0.1:18080
```

## 当前实现边界

- 当前工程通过同一 JVM 的静态存储共享指标，没有跨进程 HTTP 上报和接收协议。探针挂到独立应用时，该应用内的采集和控制台导出可运行，但独立 monitor-server 不会自动收到它的数据。本次验证覆盖 Agent 挂载到 monitor-server 的现有使用方式。
- 保留现有接口数值口径：耗时单位是纳秒；全局 qps 是 60 秒均值并使用整数除法，少于 60 次时可显示 0；慢方法中的 qps 字段实际承载窗口调用数。这些字段命名和精度尚未统一。
- Trace 与分析索引仍为进程内内存结构，没有新增持久化或保留期限机制。

本次未操作 Git，排查启动的测试进程已关闭。
