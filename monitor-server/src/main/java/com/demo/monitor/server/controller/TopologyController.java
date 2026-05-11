package com.demo.monitor.server.controller;

import com.demo.monitor.core.topology.TopologyRegistry;
import com.demo.monitor.core.topology.model.ServiceRelation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class TopologyController {

    @GetMapping("/api/topology")
    public Collection<ServiceRelation> topology() {
        return TopologyRegistry.relations();
    }
}