package com.shivam.MyWeb.Controller;

import com.shivam.MyWeb.Model.User;
import com.shivam.MyWeb.Service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users")
    public List<User> getAllUsers(){
       return userService.getAllUsers();
    }

}
