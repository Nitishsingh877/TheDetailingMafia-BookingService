package com.thederailingmafia.carwash.bookingservice.service;

import com.thederailingmafia.carwash.bookingservice.model.UserModel;
import com.thederailingmafia.carwash.bookingservice.model.UserRole;
import com.thederailingmafia.carwash.bookingservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email).orElseThrow(() ->  new UsernameNotFoundException("USer not " +
                "found "));


        //  UserModel has a getUserRoles() method that returns   roles
        UserRole role = user.getUserRole();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword() !=null ?
                user.getPassword(): "",authorities);
    }
}


