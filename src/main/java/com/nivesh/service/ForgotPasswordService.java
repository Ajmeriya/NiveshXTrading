package com.nivesh.service;

import com.nivesh.domain.VerificationType;
import com.nivesh.model.ForgotPasswordToken;
import com.nivesh.model.User;
import org.springframework.stereotype.Service;

@Service
public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUserId(String userId);
    void deleteToken(ForgotPasswordToken token);

}
