package com.example.tounip.tounip;

import com.example.tounip.tounip.live.config.LiveKitProperties;
import com.example.tounip.tounip.translation.config.TranslationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({
        LiveKitProperties.class,
        TranslationProperties.class
})
public class TounipApplication {

    public static void main(String[] args) {
        SpringApplication.run(TounipApplication.class, args);
    }
}
