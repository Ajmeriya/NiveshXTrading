package com.nivesh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public String home() {
        return "WellCome to Treading Platform";
    }

    @GetMapping("/api")
    public String Secure()
    {
        return "Welcome to treading platform secure";
    }

}
