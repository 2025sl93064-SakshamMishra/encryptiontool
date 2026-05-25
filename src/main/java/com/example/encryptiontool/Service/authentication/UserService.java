package com.example.encryptiontool.Service.authentication;

import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.SignupRequest;

public interface UserService {

  String registerUser(SignupRequest request);

  AuthResponse loginUser(LoginRequest request);
}