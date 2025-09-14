package com.nivesh.repository;

import com.nivesh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
}
