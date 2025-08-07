package com.shivam.MyWeb.Service;

import com.shivam.MyWeb.Model.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;

@Service
public class UserService {

    public List<User> getAllUsers(){
        return Arrays.asList(
            new User(1,"Shivam","shivam@gmail.com"),
            new User(2,"Raj","raj@gmail.com"),
            new User(3,"Rajesh","rajesh@gmail.com")
        );
    }

}
