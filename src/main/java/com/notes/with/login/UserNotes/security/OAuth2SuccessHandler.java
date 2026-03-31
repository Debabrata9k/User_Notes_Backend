package com.notes.with.login.UserNotes.security;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.notes.with.login.UserNotes.entity.User;
import com.notes.with.login.UserNotes.repository.UserRepository;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 🔥 CREATE USER IF NOT EXISTS
        User user = userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword("GOOGLE_LOGIN");
                    return userRepo.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getEmail());

        String redirectUrl =
                "usernotes://oauth?token=" + URLEncoder.encode(token, "UTF-8")
                        + "&name=" + URLEncoder.encode(user.getName(), "UTF-8")
                        + "&email=" + URLEncoder.encode(user.getEmail(), "UTF-8");

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}