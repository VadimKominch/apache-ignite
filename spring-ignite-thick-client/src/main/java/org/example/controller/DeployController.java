package org.example.controller;

import org.example.service.DeployService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/deploy")
public class DeployController {

    private DeployService deployService;

    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @PostMapping
    public String deployService(@RequestParam(name = "name") Optional<String> serviceName) {
        deployService.deploy(serviceName.orElse("eventHandlerService"));
        return "ok";
    }
}
