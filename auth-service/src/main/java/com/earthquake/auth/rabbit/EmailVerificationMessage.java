package com.earthquake.auth.rabbit;

import java.io.Serializable;

public record EmailVerificationMessage(String email, String code) implements Serializable {
}
