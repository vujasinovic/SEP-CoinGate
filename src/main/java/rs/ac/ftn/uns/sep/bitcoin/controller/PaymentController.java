package rs.ac.ftn.uns.sep.bitcoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PaymentUrlDto;

@RestController
@RequestMapping("/")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private static final String EVERY_30_SECONDS = "0/30 * * * * ?";

    private final PaymentService paymentService;

    private final TaskScheduler scheduler;

    public PaymentController(PaymentService paymentService, TaskScheduler scheduler) {
        this.paymentService = paymentService;
        this.scheduler = scheduler;
    }

    @PostMapping
    public PaymentUrlDto postPreparePayment(KpRequest kpRequest) {
        LOGGER.info("Handling KP request.");
        return paymentService.sendOrder(kpRequest);
    }

    @GetMapping("/paymentSuccessful/{paymentId}")
    public ResponseEntity<?> getPaymentSuccess(@PathVariable Long paymentId) {
        LOGGER.info("Handling successful payment");
        return findRedirectUrl(paymentId);
    }

    @GetMapping("/paymentCanceled/{paymentId}")
    public ResponseEntity<?> getPaymentCanceled(@PathVariable Long paymentId) {
        LOGGER.info("Handling invalid payment");
        return findRedirectUrl(paymentId);
    }

    @Scheduled(cron = EVERY_30_SECONDS)
    public void changeStatus() {
        paymentService.checkStatus();
    }

    private ResponseEntity<?> findRedirectUrl(Long paymentId) {
        String redirectUrl = paymentService.findRedirectUrl(paymentId);

        LOGGER.info("Redirecting to: " + redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }
}
