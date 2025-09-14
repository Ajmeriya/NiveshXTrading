package com.nivesh.service;

import com.nivesh.domain.VerificationType;
import com.nivesh.model.User;
import com.nivesh.model.VerificationCode;
import com.nivesh.repository.VerificationCodeRepository;
import com.nivesh.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {


    @Autowired
    private VerificationCodeRepository verificationCodeRepository;




    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode1=new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOtp());
        verificationCode1.setVerificationType(verificationType);

        verificationCode1.setUser(user);

        return verificationCodeRepository.save(verificationCode1);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long Id) throws Exception {

        Optional<VerificationCode>verificationCode=
                verificationCodeRepository.findById(Id);

        if(verificationCode.isPresent()){
            return verificationCode.get();
        }

        throw new Exception("verification code not found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long UserId) {
        return verificationCodeRepository.findByUserId(UserId);
    }

    @Override
    public void deleteVerificationCode(VerificationCode verificationCode) {

        verificationCodeRepository.delete(verificationCode);
    }
}
