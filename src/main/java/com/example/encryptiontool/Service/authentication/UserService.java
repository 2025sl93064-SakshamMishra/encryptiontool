package com.example.encryptiontool.Service.authentication;

import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.OtpVerifyRequest;
import com.example.encryptiontool.dto.SignupRequest;

public interface UserService {

  String registerUser(SignupRequest request);

  AuthResponse loginUser(LoginRequest request);

  String verifyOtp(OtpVerifyRequest request);

  String resendOtp(String email);
}
