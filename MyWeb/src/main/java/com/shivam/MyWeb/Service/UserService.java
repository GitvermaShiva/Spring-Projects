package com.shivam.MyWeb.Service;

import com.shivam.MyWeb.Model.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class UserService {

    List<User> users=new ArrayList<>(Arrays.asList(new User(1,"Shivam","shivam@gmail.com"),new User(2,"Raj","raj@gmail.com"), new User(3,"Rajesh","rajesh@gmail.com")));

    public List<User> getAllUsers(){
        return users;
    }

    public User getUser(int userId){
        return users.stream()
                    .filter(u -> u.getId() == userId)
                    .findFirst().get();
    }    

    public void addUser(User user){

    }

}
