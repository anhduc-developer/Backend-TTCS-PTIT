package vn.hunter.job.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API Controller for handling localization/internationalization (i18n)
 * Provides endpoints to retrieve localized messages
 */
@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageSource messageSource;

    /**
     * Get a single message by key in the current locale
     *
     * @param key message key (e.g., "validation.name.notblank")
     * @return localized message string
     */
    @GetMapping("/{key}")
    public ResponseEntity<?> getMessage(@PathVariable String key) {
        try {
            String message = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("key", key, "message", message));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("key", key, "message", key));
        }
    }

    /**
     * Get current locale/language
     *
     * @return current locale (e.g., "en_US", "vi")
     */
    @GetMapping("/locale")
    public ResponseEntity<?> getLocale() {
        return ResponseEntity.ok(Map.of("locale", LocaleContextHolder.getLocale().toString()));
    }

    /**
     * Get all messages for a specific language
     * Useful for frontend to load all translations at once
     *
     * @param language language code (en, vi)
     * @return Map of all message keys and values for the specified language
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMessages(
            @RequestParam(value = "lang", required = false) String language) {

        Map<String, String> messages = new HashMap<>();

        // Include common message keys
        String[] messageKeys = {
                // Header
                "header.dashboard", "header.company", "header.job", "header.resume",
                "header.permission", "header.role", "header.skill", "header.user",
                "header.profile", "header.logout", "header.login", "header.register",

                // Form
                "form.name", "form.email", "form.password", "form.confirmPassword",
                "form.age", "form.gender", "form.address", "form.phone",
                "form.description", "form.submitButton", "form.cancelButton",

                // Validation
                "validation.name.notblank", "validation.email.notblank", "validation.email.format",
                "validation.password.notblank", "validation.password.size",
                "validation.age.notblank", "validation.age.min", "validation.age.max",
                "validation.gender.notblank", "validation.location.notblank",
                "validation.oldpassword.notblank", "validation.newpassword.notblank",
                "validation.newpassword.size", "validation.apipath.notblank",
                "validation.method.notblank", "validation.module.notblank",

                // Response
                "response.success", "response.error", "response.warning", "response.info",
                "response.confirmDelete", "response.deleteSuccess", "response.createSuccess",
                "response.updateSuccess", "response.loading", "response.noData",
                "response.unauthorized", "response.forbidden", "response.notFound",
                "response.serverError"
        };

        Locale locale = LocaleContextHolder.getLocale();
        for (String key : messageKeys) {
            try {
                String message = messageSource.getMessage(key, null, locale);
                messages.put(key, message);
            } catch (Exception e) {
                messages.put(key, key); // Fallback to key if message not found
            }
        }

        return ResponseEntity.ok(messages);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "OK", "message", "Message service is running"));
    }
}
