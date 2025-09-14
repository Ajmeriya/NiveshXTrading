package com.nivesh.service;

import com.nivesh.domain.VerificationType;
import com.nivesh.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User findUserByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;

    public User enableTwofactorAuthentication(VerificationType verificationType,
                                              String sendTo,
                                              User user);

    User updatePassword(User user, String newPassword);
}
