package com.kroste.twitch_chat_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GoldResponse {

    private int newBalance;
}
