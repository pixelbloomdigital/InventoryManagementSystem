package com.pixelbloom.oauth_social_login.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MainController {

    @RequestMapping("/")
    public String home(){
        return "Welcome!!";
    }

    @RequestMapping("/user")
    public Principal getUser(Principal user){
        return user;
    }
}
