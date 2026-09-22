package com.kroste.twitchchatbackend;

import com.kroste.twitchchatbackend.fishing.FishingSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
@EnableConfigurationProperties(FishingSettings.class)
public class TwitchChatBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitchChatBackendApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}
