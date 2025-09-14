package com.nivesh.model;

import com.nivesh.domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnable=false;
    private VerificationType sendTo;
}
