package rs.ac.ftn.uns.sep.bitcoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PaymentUrlDto;

@RestController
@RequestMapping("/")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentUrlDto postPreparePayment(KpRequest kpRequest) {
        LOGGER.info("Handling KP request.");
        return paymentService.sendOrder(kpRequest);
    }

    @GetMapping("/paymentSuccessful/{paymentId}")
    public ResponseEntity<?> getPaymentSuccess(@PathVariable Long paymentId) {
        LOGGER.info("Handling successful payment");
        return getRedirectUrlDto(paymentId);
    }

    @GetMapping("/paymentCanceled/{paymentId}")
    public ResponseEntity<?> getPaymentCanceled(@PathVariable Long paymentId) {
        LOGGER.info("Handling invalid payment");
        return getRedirectUrlDto(paymentId);
    }

    private ResponseEntity<?> getRedirectUrlDto(Long paymentId) {
        String redirectUrl = paymentService.getRedirectUrl(paymentId);

        LOGGER.info("Redirecting to: " + redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }
}
