package com.nivesh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivesh.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TwoFactorOTP {
    @Id
    private String id;

    private String otp;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwt;



}
