package rs.ac.ftn.uns.sep.bitcoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.*;

import java.util.Objects;

import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.getOrder;
import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.postOrder;

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

        PaymentUrlDto paymentUrlDto = new PaymentUrlDto();

        PreparedPaymentDto preparedPaymentDto = paymentService.preparePayment(kpRequest);

        ResponseEntity<ApiResponseDto> response = postOrder(preparedPaymentDto);

        String paymentUrl = Objects.requireNonNull(response.getBody()).getPaymentUrl();

        LOGGER.info("Payment URL: " + paymentUrl);

        paymentService.persist(response.getBody(), preparedPaymentDto);

        paymentUrlDto.setPaymentUrl(paymentUrl);

        return paymentUrlDto;
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

    private ResponseEntity<?> getRedirectUrlDto(@PathVariable Long paymentId) {

        Payment payment = paymentService.getOne(paymentId);

        LOGGER.info("Getting payment information..");
        ResponseEntity<ApiResponseDto> response = getOrder(payment);
        LOGGER.info("Basic payment info: " + Objects.requireNonNull(response.getBody()).toString());

        String status = response.getBody().getStatus();

        LOGGER.info("Changing payment status into: " + status);
        payment.setStatus(status);

        LOGGER.info("Persisting payment");
        Payment persistedPayment = paymentService.save(payment);
        LOGGER.info("Payment persisted: " + persistedPayment.toString());

        String redirectUrl = payment.getRedirectUrl();

        LOGGER.info("Redirecting to: " + redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }
}
