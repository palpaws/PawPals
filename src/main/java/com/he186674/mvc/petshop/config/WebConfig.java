package com.he186674.mvc.petshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploads/pets directory as static resources
        Path uploadPath = Paths.get("uploads/pets");
        String absolutePath = uploadPath.toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/uploads/pets/**")
                .addResourceLocations("file:" + absolutePath + "/");

        // Serve uploads/community directory as static resources
        Path communityUploadPath = Paths.get("uploads/community");
        String communityAbsolutePath = communityUploadPath.toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/uploads/community/**")
                .addResourceLocations("file:" + communityAbsolutePath + "/");
    }
}
