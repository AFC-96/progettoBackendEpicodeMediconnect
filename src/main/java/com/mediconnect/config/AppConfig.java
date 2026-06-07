package com.mediconnect.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.web.client.RestTemplate;
// Configurazione dei bean condivisi: Thymeleaf per le email, ModelMapper per la conversione DTO-entità, RestTemplate per le API esterne

@Configuration
public class AppConfig {

    // Configura il motore Thymeleaf per comporre le email HTML
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    // Configura il mapper per la conversione automatica entità-DTO
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        var config = modelMapper.getConfiguration();
        config.setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        config.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        config.setFieldMatchingEnabled(true);
        config.setSkipNullEnabled(true);
        config.setMatchingStrategy(MatchingStrategies.STANDARD);

        return modelMapper;
    }

    // Crea il client HTTP per le chiamate alle API esterne (OpenFDA)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}