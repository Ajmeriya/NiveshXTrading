package com.nivesh.repository;

import com.nivesh.model.ForgotPasswordToken;
import com.nivesh.service.ForgotPasswordService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken,String> {

    ForgotPasswordToken findByUserId(Long userId);

}
