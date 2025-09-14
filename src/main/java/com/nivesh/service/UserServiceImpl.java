package com.nivesh.service;

import com.nivesh.config.JwtProvider;
import com.nivesh.domain.VerificationType;
import com.nivesh.model.TwoFactorAuth;
import com.nivesh.model.User;
import com.nivesh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserByJwt(String jwt) throws Exception {
        String email= JwtProvider.getEmailFromToken(jwt);
        User user=userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not Found");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {

        User user=userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not Found");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user=userRepository.findById(userId);

        if(user.isEmpty())
        {
            throw new Exception("User Not Found");
        }
        return user.get();
    }

    @Override
    public User enableTwofactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth=new TwoFactorAuth();
        twoFactorAuth.setEnable(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);

        return userRepository.save(user);
    }


    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
