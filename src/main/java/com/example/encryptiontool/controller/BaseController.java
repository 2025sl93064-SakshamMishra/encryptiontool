package com.example.encryptiontool.controller;

import com.example.encryptiontool.exception.AppException;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    @Autowired
    protected UserRepository userRepository;

    protected User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }
}
