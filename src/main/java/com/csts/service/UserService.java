package com.csts.service;

import com.csts.model.User;
import com.csts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        try {
            return userRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error Fetching all users: " + e.getMessage());
        }
    }
    public User getMyProfile(String id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User Not Found with ID: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error Fetching profile: " + e.getMessage());
        }
    }

    public User getUserById(String id){
        try{
            return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found with Id: " + id));
        }catch (Exception e){
            throw new RuntimeException("Error fetching user by ID: " + e.getMessage());
        }
    }
}