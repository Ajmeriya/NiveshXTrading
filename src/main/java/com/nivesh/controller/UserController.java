package com.nivesh.controller;

import com.nivesh.response.ApiResponse;
import com.nivesh.response.Authresponse;
import com.nivesh.domain.VerificationType;
import com.nivesh.model.ForgotPasswordToken;
import com.nivesh.model.User;
import com.nivesh.model.VerificationCode;
import com.nivesh.repository.UserRepository;
import com.nivesh.request.ForgotPasswordTokenRequest;
import com.nivesh.request.ReserPasswordRequest;
import com.nivesh.service.EmailService;
import com.nivesh.service.ForgotPasswordService;
import com.nivesh.service.UserService;
import com.nivesh.service.VerificationCodeService;
import com.nivesh.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    private String jwt;


    // 👉 Get User Profile API
    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        // Step 1: Find user using JWT
        User user = userService.findUserByJwt(jwt);

        // Step 2: Return user details
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }


    // 👉 Send Verification OTP API
    @PostMapping("/api/user/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        // Step 1: Find user from JWT
        User user = userService.findUserByJwt(jwt);

        // Step 2: Check if OTP already exists for this user
        VerificationCode verificationCode =
                verificationCodeService.getVerificationCodeByUser((long) user.getId());

        // Step 3: If not exists, generate new OTP and save
        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        // Step 4: If verification type is EMAIL, send OTP to user’s email
        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOtp());
        }

        // Step 5: Return success message
        return new ResponseEntity<>("verification otp sent successfully", HttpStatus.OK);
    }


    // 👉 Enable Two-Factor Authentication API
    @PatchMapping("/api/user/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAthentication(@RequestHeader("Authorization") String jwt, @PathVariable String Otp) throws Exception {
        // Step 1: Get user from JWT
        User user = userService.findUserByJwt(jwt);

        // Step 2: Fetch verification code for this user
        VerificationCode verificationCode =
                verificationCodeService.getVerificationCodeByUser((long) user.getId());

        // Step 3: Identify whether OTP was sent to email or mobile
        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL) ?
                verificationCode.getEmail() : verificationCode.getMobile();

        // Step 4: Compare input OTP with stored OTP
        boolean isVerified = verificationCode.getOtp().equals(Otp);

        // Step 5: If OTP matches
        if (isVerified) {
            // Enable 2FA for user
            User updatedUser = userService.enableTwofactorAuthentication(
                    verificationCode.getVerificationType(), sendTo, user);

            // Delete used verification code
            verificationCodeService.deleteVerificationCode(verificationCode);

            // Return updated user
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        // Step 6: If OTP doesn’t match, throw error
        throw new Exception("wrong otp");
    }


    // 👉 Send Forgot-Password OTP API
    @PostMapping("/auth/user/reset-password/send-otp")
    public ResponseEntity<Authresponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        // Step 1: Find user by email/phone
        User user=userService.findUserByEmail(req.getSendTo());

        // Step 2: Generate random OTP
        String otp= OtpUtils.generateOtp();

        // Step 3: Create unique session ID using UUID
        UUID uuid=UUID.randomUUID();
        String id=uuid.toString();

        // Step 4: Check if ForgotPasswordToken already exists for this user
        ForgotPasswordToken token=forgotPasswordService.findByUserId(user.getId());

        // Step 5: If not, create a new token
        if(token==null)
        {
            token=forgotPasswordService.createToken(user,id,otp,req.getVerificationType(),req.getSendTo());
        }

        // Step 6: If verification type is EMAIL, send OTP
        if(req.getVerificationType().equals(VerificationType.EMAIL))
        {
            emailService.sendVerificationEmail(user.getEmail(), token.getOtp());
        }

        // Step 7: Prepare response with session ID
        Authresponse auth=new Authresponse();
        auth.setSession(token.getId());
        auth.setMessage("Forgot-Password OTP sent successfully");

        // Step 8: Return success response
        return new ResponseEntity<>(auth, HttpStatus.OK);
    }


    // 👉 Reset Password API
    @PatchMapping("/auth/user/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id,
                                                     @RequestBody ReserPasswordRequest req,
                                                     @RequestHeader("Authorization") String jwt) throws Exception {
        // Step 1: Find user by JWT
        User user = userService.findUserByJwt(jwt);

        // Step 2: Get ForgotPasswordToken by session ID
        ForgotPasswordToken forgotPasswordToken =
                forgotPasswordService.findById(id);

        // Step 3: Compare provided OTP with stored OTP
        boolean isVerified=forgotPasswordToken.getOtp().equals(req.getOtp());

        // Step 4: If OTP is valid
        if(isVerified)
        {
            // Update user password
            userService.updatePassword(forgotPasswordToken.getUser(),req.getNewPassword());

            // Prepare success response
            ApiResponse response=new ApiResponse();
            response.setMessage("password updated successfully");

            // Return response
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }

        // Step 5: If OTP invalid, throw error
        throw new Exception("wrong otp");

    }


}
