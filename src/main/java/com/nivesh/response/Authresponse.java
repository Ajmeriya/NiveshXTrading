package com.nivesh.response;

import lombok.Data;

@Data
public class Authresponse {

    private String jwt;
    private boolean status;
    private String message;
    private boolean isTwoFactorAuthEnabled;
    private String session;

}
