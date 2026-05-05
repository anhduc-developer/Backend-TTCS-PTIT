package vn.hunter.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Locale;

/**
 * Configuration for Internationalization (i18n) support
 * Supports English (en) and Vietnamese (vi) languages
 */
@Configuration
public class MessageSourceConfiguration implements WebMvcConfigurer {

    /**
     * Configure message source for language bundles
     * Loads messages_en.properties and messages_vi.properties
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        return messageSource;
    }

    /**
     * Configure locale resolver (en_US by default)
     * Can be changed by cookie locale parameter
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("en", "US"));
        resolver.setCookieName("locale");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    /**
     * Configure locale change interceptor
     * Allows changing language via locale parameter in request
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("locale");
        return interceptor;
    }

    /**
     * Register locale change interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
