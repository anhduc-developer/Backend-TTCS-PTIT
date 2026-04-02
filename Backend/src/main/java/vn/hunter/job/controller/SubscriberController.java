package vn.hunter.job.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Subscriber;
import vn.hunter.job.service.SubscriberService;
import vn.hunter.job.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;

    }

    @PostMapping("/subscribers")
    @ApiMessage("Ghi nhận đăng ký bản tin thành công")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) {
        Subscriber res = this.subscriberService.upsert(subscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
