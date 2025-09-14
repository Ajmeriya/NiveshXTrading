package com.nivesh.model;


import com.nivesh.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String otp;

    @ManyToOne
    private User user;

    private String email;

    private String mobile;

    private VerificationType verificationType;
}
