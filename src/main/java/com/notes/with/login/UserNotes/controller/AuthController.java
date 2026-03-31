package com.notes.with.login.UserNotes.controller;

import com.notes.with.login.UserNotes.dtos.LoginDto;
import com.notes.with.login.UserNotes.dtos.SingUpDto;
import com.notes.with.login.UserNotes.entity.User;
import com.notes.with.login.UserNotes.repository.UserRepository;
import com.notes.with.login.UserNotes.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import java.util.HashMap;
// import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SingUpDto dto) {



        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            return new ApiResponse("Signup Failed", null, null, null);
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        userRepo.save(user);
        return new ApiResponse("Sucessfull", user.getName(),
                user.getEmail(),
                null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {

        try {
            if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
                return ResponseEntity.badRequest().body("Invalid input");
            }

            Optional<User> optionalUser = userRepo.findByEmail(dto.getEmail());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = optionalUser.get();

            if (!dto.getPassword().equals(user.getPassword())) {
                return ResponseEntity.badRequest().body("Wrong password");
            }

            // 🔥 TEMP TEST (replace JWT)
            String token = jwtUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(
                    new ApiResponse(
                            "Login Successful",
                            user.getName(),
                            user.getEmail(),
                            token
                    )
            );

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 THIS WILL SHOW REAL ERROR IN CONSOLE
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}