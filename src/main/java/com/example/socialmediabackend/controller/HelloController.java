package com.example.socialmediabackend.controller;

// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {

    @RequestMapping("/api/hello")
    // @PreAuthorize("permitAll()")
    public String hello() {
        return "Hello World RESTful with Spring Boot";
    }

    @GetMapping("/api/public/test")
    public String test() {
        return "Backend is running!";
    }
}