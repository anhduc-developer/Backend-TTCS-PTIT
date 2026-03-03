package vn.hunter.job.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Subscriber;
import vn.hunter.job.repository.SubscriberRepository;
import vn.hunter.job.service.SubscriberService;
import vn.hunter.job.util.SecurityUtil;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberRepository subscriberRepository;
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService, SubscriberRepository subscriberRepository) {
        this.subscriberService = subscriberService;
        this.subscriberRepository = subscriberRepository;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a Subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isExists = this.subscriberService.isExistsByEmail(subscriber.getEmail());
        if (isExists) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tổn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber subscriberDB = this.subscriberService.findById(subscriber.getId());
        if (subscriberDB == null) {
            throw new IdInvalidException("Id " + subscriber.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.subscriberService.update(subscriberDB, subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
}
