package vn.hunter.job.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public ResponseEntity<String> getHomepage() {
        return ResponseEntity.ok().body("Hello Spring Boot");
    }
}
