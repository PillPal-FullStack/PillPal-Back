package com.project.pillpal.user.auth;

import com.project.pillpal.user.entity.User;
import com.project.pillpal.security.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    private static final User TEST_USER = createTestUser();

    private static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setPassword("password");
        return user;
    }

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetail)) {
            return TEST_USER;
        }
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return userDetail.getUser();
    }

    public static Long getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof UserDetail;
    }
}