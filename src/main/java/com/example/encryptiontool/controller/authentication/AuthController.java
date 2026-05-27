package com.example.encryptiontool.controller.authentication;

import com.example.encryptiontool.controller.BaseController;
import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.SignupRequest;
import com.example.encryptiontool.Service.authentication.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
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
}