package com.example.encryptiontool.Service.authentication;

import com.example.encryptiontool.Service.authentication.UserService;
import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.SignupRequest;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public String registerUser(SignupRequest request) {

    // Check if email already exists
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

    if (existingUser.isPresent()) {
      return "Email already exists";
    }

    // Create new user
    User user = new User();

    user.setName(request.getName());

    user.setEmail(request.getEmail());

    // Encrypt password
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // Generate verification token
    user.setVerificationToken(UUID.randomUUID().toString());

    user.setVerified(false);

    // Save user
    userRepository.save(user);

    return "User registered successfully";
  }

  @Override
  public AuthResponse loginUser(LoginRequest request) {

    Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

    // Check if user exists
    if (optionalUser.isEmpty()) {
      return new AuthResponse(null, "Invalid Email");
    }

    User user = optionalUser.get();

    // Check password
    boolean isPasswordMatch =
        passwordEncoder.matches(request.getPassword(), user.getPassword());

    if (!isPasswordMatch) {
      return new AuthResponse(null, "Invalid Password");
    }

    // JWT will come later
    String dummyToken = "JWT_TOKEN";

    return new AuthResponse(dummyToken, "Login Successful");
  }
}