package com.movietrend.controller;

import com.movietrend.dto.LoginRequest;
import com.movietrend.dto.SignupRequest;
import com.movietrend.entity.User;
import com.movietrend.service.JwtService;
import com.movietrend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8800", allowCredentials = "true")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request received: {}", loginRequest);
        
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "아이디 누락");
            response.put("message", "아이디를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "비밀번호 누락");
            response.put("message", "비밀번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername().trim(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            User user = userService.findByUsername(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            response.put("name", user.getName());
            response.put("message", "로그인 성공");

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "인증 실패");
            response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        log.info("Signup request received: {}", signupRequest);
        
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "아이디 누락");
            response.put("message", "아이디를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "비밀번호 누락");
            response.put("message", "비밀번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "이메일 누락");
            response.put("message", "이메일을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        if (signupRequest.getName() == null || signupRequest.getName().trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "이름 누락");
            response.put("message", "이름을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            if (userService.existsByUsername(signupRequest.getUsername())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "아이디 중복");
                response.put("message", "이미 사용 중인 아이디입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            if (userService.existsByEmail(signupRequest.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "이메일 중복");
                response.put("message", "이미 사용 중인 이메일입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            User user = User.builder()
                .username(signupRequest.getUsername().trim())
                .email(signupRequest.getEmail().trim())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName().trim())
                .role("USER")
                .build();

            User savedUser = userService.createUser(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("username", savedUser.getUsername());
            response.put("name", savedUser.getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Signup failed for user: {}", signupRequest.getUsername(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "회원가입 실패");
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token.substring(7));
            User user = userService.findByUsername(username);
            
            Map<String, String> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user info", e);
            return ResponseEntity.status(401).body("인증되지 않은 요청입니다.");
        }
    }
} 