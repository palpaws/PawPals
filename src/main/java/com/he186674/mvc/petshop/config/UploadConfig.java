package com.he186674.mvc.petshop.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadConfig
        implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        registry.addResourceHandler(
                        "/uploads/**"
                )
                .addResourceLocations(
                        "file:///C:/Users/DELL/Desktop/PET_EXE/PawPals/uploads/"
                );

    }

}