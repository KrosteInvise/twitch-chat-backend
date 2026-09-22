package com.kroste.twitchchatbackend.fishing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "fishing")
public class FishingSettings {

    private int castCost = 5;
    private int minWaitMinutes = 5;
    private int maxWaitMinutes = 20;
}
