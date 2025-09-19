package com.project.pillpal.user;

import com.project.pillpal.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestUserUtils {

    @Value("${test.user.id}")
    private Long testUserId;

    @Value("${test.user.username}")
    private String testUsername;

    @Value("${test.user.email}")
    private String testEmail;

    public User getTestUser() {
        User user = new User();
        user.setId(testUserId);
        user.setUsername(testUsername);
        user.setEmail(testEmail);
        user.setPassword("password");
        return user;
    }

    public Long getTestUserId() {
        return testUserId;
    }
}
