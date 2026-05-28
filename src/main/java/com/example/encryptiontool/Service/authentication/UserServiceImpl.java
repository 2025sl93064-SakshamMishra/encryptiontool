package com.example.encryptiontool.Service.authentication;

import com.example.encryptiontool.dto.AuthResponse;
import com.example.encryptiontool.dto.LoginRequest;
import com.example.encryptiontool.dto.OtpVerifyRequest;
import com.example.encryptiontool.dto.SignupRequest;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.UserRepository;
import com.example.encryptiontool.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Override
  public String registerUser(SignupRequest request) {
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
    if (existingUser.isPresent()) {
      return "Email already exists";
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setVerified(false);

    // Generate OTP and store it in verificationToken field
    String otp = generateOtp();
    user.setVerificationToken(otp);
    userRepository.save(user);

    // Send OTP email
    sendOtpEmail(request.getEmail(), request.getName(), otp);

    return "OTP sent to " + request.getEmail();
  }

  @Override
  public AuthResponse loginUser(LoginRequest request) {
    Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
    if (optionalUser.isEmpty()) {
      return new AuthResponse(null, "Invalid email or password");
    }

    User user = optionalUser.get();

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return new AuthResponse(null, "Invalid email or password");
    }

    if (!user.isVerified()) {
      return new AuthResponse(null, "Please verify your email before logging in");
    }

    String token = jwtUtil.generateToken(user.getEmail());
    return new AuthResponse(token, "Login successful");
  }

  @Override
  public String verifyOtp(OtpVerifyRequest request) {
    Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
    if (optionalUser.isEmpty()) {
      return "User not found";
    }

    User user = optionalUser.get();

    if (user.isVerified()) {
      return "Email is already verified";
    }

    if (user.getVerificationToken() == null || !user.getVerificationToken().equals(request.getOtp())) {
      return "Invalid OTP";
    }

    user.setVerified(true);
    user.setVerificationToken(null);
    userRepository.save(user);

    return "Email verified successfully";
  }

  @Override
  public String resendOtp(String email) {
    Optional<User> optionalUser = userRepository.findByEmail(email);
    if (optionalUser.isEmpty()) {
      return "User not found";
    }

    User user = optionalUser.get();

    if (user.isVerified()) {
      return "Email is already verified";
    }

    String otp = generateOtp();
    user.setVerificationToken(otp);
    userRepository.save(user);

    sendOtpEmail(email, user.getName(), otp);
    return "OTP resent to " + email;
  }

  // ---------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------

  private String generateOtp() {
    return String.format("%06d", new Random().nextInt(1000000));
  }

  private void sendOtpEmail(String toEmail, String name, String otp) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(senderEmail);
    message.setTo(toEmail);
    message.setSubject("Your KT Encryption Tool Verification Code");
    message.setText(
        "Hi " + name + ",\n\n" +
        "Your verification code is:\n\n" +
        "  " + otp + "\n\n" +
        "Enter this code on the verification page to activate your account.\n" +
        "This code is valid for this session only.\n\n" +
        "If you did not create this account, please ignore this email.\n\n" +
        "— KT Encryption Tool"
    );
    mailSender.send(message);
  }
}
