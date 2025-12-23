package com.pixelbloom.auth_server.service;


import com.pixelbloom.auth_server.model.MyUser;
import com.pixelbloom.auth_server.repository.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    MyUserRepository myUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> user= myUserRepository.findUserByUserName(username);

        var userObject= user.get();
        if(user.isPresent()){
        return User.builder()
                .username(userObject.getUserName())
                .password(userObject.getPassword())
                .roles(getRoles(userObject))
                .build();
        }else
        {
            throw  new UsernameNotFoundException("User is not Present");
        }

    }

    private String[] getRoles(MyUser userObject) {
        if(userObject.getRole()==null){
            return new String[]{"USER"};
        }
        return userObject.getRole().split(",");
    }
}
