package com.demo.monitor.server.service;

import org.springframework.stereotype.Service;

/** 示例数据访问层，保留原来的模拟延迟，用于验证跨类调用拓扑。 */
@Service
public class DatabaseService {
    public void query() throws InterruptedException {
        Thread.sleep(50);
    }
}
