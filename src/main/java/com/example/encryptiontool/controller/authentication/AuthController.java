package com.example.encryptiontool.controller.authentication;

import com.example.encryptiontool.controller.BaseController;
import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.OtpVerifyRequest;
import com.example.encryptiontool.dto.SignupRequest;
import com.example.encryptiontool.Service.authentication.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController extends BaseController {

  @Autowired
  private UserService userService;

  @PostMapping("/signup")
  public String registerUser(@RequestBody SignupRequest request) {
    return userService.registerUser(request);
  }

  @PostMapping("/login")
  public AuthResponse loginUser(@RequestBody LoginRequest request) {
    return userService.loginUser(request);
  }

  @PostMapping("/verify-otp")
  public String verifyOtp(@RequestBody OtpVerifyRequest request) {
    return userService.verifyOtp(request);
  }

  @PostMapping("/resend-otp")
  public String resendOtp(@RequestBody Map<String, String> body) {
    return userService.resendOtp(body.get("email"));
  }
}
