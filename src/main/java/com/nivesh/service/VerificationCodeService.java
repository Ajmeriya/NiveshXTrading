package com.nivesh.service;

import com.nivesh.domain.VerificationType;
import com.nivesh.model.User;
import com.nivesh.model.VerificationCode;
import org.springframework.stereotype.Service;


@Service
public interface VerificationCodeService{
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long Id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long UserId);

    void deleteVerificationCode(VerificationCode verificationCode);


}
