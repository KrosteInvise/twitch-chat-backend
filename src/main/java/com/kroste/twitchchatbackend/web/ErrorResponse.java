package com.kroste.twitchchatbackend.web;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime time
) {
}
