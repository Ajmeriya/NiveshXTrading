package com.nivesh.service;

import com.nivesh.model.TwoFactorOTP;
import com.nivesh.model.User;
import org.springframework.stereotype.Service;

@Service
public interface TwoFactorOtpService {

    TwoFactorOTP createOtpService(User user,String otp,String jwt);

    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp);

    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);

}
