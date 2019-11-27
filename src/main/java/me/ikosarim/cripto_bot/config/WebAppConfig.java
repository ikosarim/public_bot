package me.ikosarim.cripto_bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

@Configuration
@EnableWebMvc
public class WebAppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pages/**")
                .addResourceLocations("/pages");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/pages/**");
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver configurer = new ClassLoaderTemplateResolver();
        configurer.setPrefix("templates/pages/");
        configurer.setSuffix(".html");
        configurer.setTemplateMode(HTML);
        configurer.setCharacterEncoding("UTF-8");
        configurer.setOrder(0);
        configurer.setCacheable(false);
        configurer.setCheckExistence(true);
        return configurer;
    }
}