package vn.hunter.job.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for formatting i18n error messages from validation
 * Resolves validation error messages using MessageSource based on current
 * locale
 */
@Component
public class I18nErrorFormatter {

    private final MessageSource messageSource;

    public I18nErrorFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Format validation errors with i18n messages
     * 
     * @param bindingResult The binding result containing validation errors
     * @return Map of field names to translated error messages
     */
    public Map<String, String> formatErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();

        for (FieldError error : bindingResult.getFieldErrors()) {
            String fieldName = error.getField();
            String message = resolveMessage(error, locale);
            errors.put(fieldName, message);
        }

        return errors;
    }

    /**
     * Resolve a single error message using MessageSource
     * 
     * @param error  The field error
     * @param locale The locale to use for translation
     * @return The translated error message
     */
    private String resolveMessage(FieldError error, Locale locale) {
        // First try to resolve using the error message as key (if it's in i18n format
        // like {key})
        String errorMessage = error.getDefaultMessage();

        // If message starts with { and ends with }, treat it as a key to resolve
        if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
            String messageKey = errorMessage.substring(1, errorMessage.length() - 1);
            try {
                return messageSource.getMessage(messageKey, null, messageKey, locale);
            } catch (Exception e) {
                // If key not found, return the key name for debugging
                return messageKey;
            }
        }

        // Otherwise, return the hardcoded message as-is (for backwards compatibility)
        return errorMessage;
    }

    /**
     * Get the current locale as language code (en, vi, etc.)
     * 
     * @return Language code (e.g., "en", "vi")
     */
    public String getCurrentLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.getLanguage();
    }

    /**
     * Resolve a message key to its translated value
     * 
     * @param key          The message key (without curly braces)
     * @param defaultValue The default value if key is not found
     * @return The translated message
     */
    public String getMessage(String key, String defaultValue) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, null, defaultValue, locale);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
