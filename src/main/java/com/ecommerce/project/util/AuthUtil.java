package com.ecommerce.project.util;


import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

     @Autowired
    UserRepository userRepository;

    public String loggedInEmail() {
        return getAuthenticatedUser().getEmail();
    }

    public Long loggedInUserId(){
        return getAuthenticatedUser().getUserId();
    }

    public User loggedInUser(){
        return getAuthenticatedUser();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Fallback for testing when security is disabled or user is anonymous
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return userRepository.findByUserName("user1")
                    .orElseThrow(() -> new RuntimeException("Test user 'user1' not found. Please ensure initData ran."));
        }

        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
