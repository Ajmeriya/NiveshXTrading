package com.nivesh.controller;

import com.nivesh.config.Authresponse;
import com.nivesh.config.JwtProvider;
import com.nivesh.model.TwoFactorOTP;
import com.nivesh.model.User;
import com.nivesh.repository.UserRepository;
import com.nivesh.service.CustomeUserDetailsService;
import com.nivesh.service.EmailService;
import com.nivesh.service.TwoFactorOtpService;
import com.nivesh.utils.OtpUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomeUserDetailsService customeUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;


    /*
     =========================================
     Steps before building Authentication APIs
     =========================================
     👉 1. Collect user details (email, password, name)
     👉 2. Validate if user already exists
     👉 3. Save user to database if new
     👉 4. Create Authentication object for login
     👉 5. Store authentication in Spring Security Context
     👉 6. Generate JWT token with user info
     👉 7. If 2FA is enabled ➝ generate OTP & return response
     👉 8. If not ➝ directly return JWT
     */


    // Signup API (Register New User)
    @PostMapping("/signup")
    public ResponseEntity<Authresponse> registerUser(@RequestBody User user) throws Exception {

        // 👉 Step 1: Check if email already exists
        User isEmailExist=userRepository.findByEmail(user.getEmail());
        if(isEmailExist!=null){
            throw new Exception("email is already exist");
        }

        // 👉 Step 2: Create new user object
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword()); // password not encoded here
        newUser.setFullName(user.getFullName());

        // 👉 Step 3: Save user in DB
        User savedUser = userRepository.save(newUser);

        // 👉 Step 4: Create Authentication object
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        // 👉 Step 5: Store Authentication in Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 👉 Step 6: Generate JWT token
        String jwt= JwtProvider.generateToken(auth);

        // 👉 Step 7: Prepare final response
        Authresponse res=new Authresponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Register successfully");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }



    // Signin API (Login Existing User)
    @PostMapping("/signin")
    public ResponseEntity<Authresponse> login(@RequestBody User user) throws Exception {

        String username=user.getEmail();
        String password=user.getPassword();

        System.out.println(username + " ----- " + password);

        // 👉 Step 1: Authenticate user
        Authentication auth=authenticate(username,password);

        // 👉 Step 2: Store Authentication in Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 👉 Step 3: Generate JWT token
        String jwt= JwtProvider.generateToken(auth);

        // 👉 Step 4: Fetch logged-in user from DB
        User authuser=userRepository.findByEmail(username);

        // 👉 Step 5: If 2FA enabled ➝ generate & send OTP
        if(user.getTwoFactorAuth().isEnable())
        {
            String otp= OtpUtils.generateOtp();

            // check & delete old OTP if exists
            TwoFactorOTP oldTwoFactorOTP=twoFactorOtpService.findByUser((long) authuser.getId());
            if(oldTwoFactorOTP!=null)
            {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }

            // save new OTP to DB against this user
            TwoFactorOTP newTwoFactorOTP=twoFactorOtpService.createOtpService(authuser,otp,jwt);

            //get otp for verification
            emailService.sendVerificationEmail(username,otp);

            // return response for OTP verification step
            Authresponse res=new Authresponse();
            res.setMessage("Two factor auth is enabled, please verify otp");
            res.setTwoFactorAuthEnabled(true);
            res.setSession(newTwoFactorOTP.getId());

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        // 👉 Step 6: If no 2FA ➝ return success with JWT
        Authresponse res=new Authresponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login successfully");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    // Helper method for authentication
    private Authentication authenticate(String username, String password) throws Exception {

        // 👉 Step 1: Load user details from DB
        UserDetails userDetails = customeUserDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("invalid username");
        }

        // 👉 Step 2: Compare raw password with DB password
        if (!password.trim().equals(userDetails.getPassword().trim())) {
            throw new BadCredentialsException("invalid password");
        }

        // 👉 Step 3: Return authenticated user object
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }


    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<Authresponse> verifySigninOtp(
            @PathVariable String otp,
            @RequestParam String id ) throws Exception {
        TwoFactorOTP twoFactorOTP=twoFactorOtpService.findById(id);

        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp))
        {
            Authresponse res=new Authresponse();
            res.setMessage("Two Factor auth verified successfully");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());

            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        throw new Exception("Invelid Otp");
    }






}
