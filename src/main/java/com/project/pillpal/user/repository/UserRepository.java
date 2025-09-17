package com.project.pillpal.user.repository;

import com.project.pillpal.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
