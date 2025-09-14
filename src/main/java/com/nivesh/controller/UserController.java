package com.nivesh.controller;

import com.nivesh.domain.VerificationType;
import com.nivesh.model.User;
import com.nivesh.model.VerificationCode;
import com.nivesh.repository.UserRepository;
import com.nivesh.service.EmailService;
import com.nivesh.service.UserService;
import com.nivesh.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    private String jwt;


    // 👉 Steps for getUserProfile API:
    // 1. Receive JWT token from request header.
    // 2. Find user using the JWT.
    // 3. Return user details as response.
    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }


    // 👉 Steps for sendVerificationOtp API:
    // 1. Receive JWT token and verification type (EMAIL/MOBILE).
    // 2. Find user using JWT.
    // 3. Check if verification code exists for the user.
    // 4. If not, generate and save a new verification code.
    // 5. If type = EMAIL, send OTP to user’s email.
    // 6. Return success message.
    @PostMapping("/api/user/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode =
                verificationCodeService.getVerificationCodeByUser((long) user.getId());

        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOtp());
        }

        return new ResponseEntity<>("verification otp sent successfully", HttpStatus.OK);
    }


    // 👉 Steps for enableTwoFactorAthentication API:
    // 1. Receive JWT token and OTP from request.
    // 2. Find user using JWT.
    // 3. Fetch verification code for the user.
    // 4. Identify where OTP was sent (email or mobile).
    // 5. Compare received OTP with stored OTP.
    // 6. If valid → enable two-factor authentication (2FA) for the user.
    // 7. Delete used verification code.
    // 8. Return updated user as response.
    // 9. If OTP is invalid, throw error.
    @PatchMapping("/api/user/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAthentication(@RequestHeader("Authorization") String jwt, @PathVariable String Otp) throws Exception {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode =
                verificationCodeService.getVerificationCodeByUser((long) user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL) ?
                verificationCode.getEmail() : verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(Otp);

        if (isVerified) {
            User updatedUser = userService.enableTwofactorAuthentication(
                    verificationCode.getVerificationType(), sendTo, user);

            verificationCodeService.deleteVerificationCode(verificationCode);

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        throw new Exception("wrong otp");
    }


}
