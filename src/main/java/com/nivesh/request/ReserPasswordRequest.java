package com.nivesh.request;

import lombok.Data;

@Data
public class ReserPasswordRequest {

    private String otp;
    private String newPassword;
}
