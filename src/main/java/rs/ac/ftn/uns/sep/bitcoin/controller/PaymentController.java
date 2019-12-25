package rs.ac.ftn.uns.sep.bitcoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        PaymentUrlDto paymentUrlDto = new PaymentUrlDto();

        PreparedPaymentDto preparedPaymentDto = paymentService.preparePayment(kpRequest);

        ResponseEntity<ApiResponseDto> response = postOrder(preparedPaymentDto);

        paymentService.persist(response.getBody(), preparedPaymentDto);

        paymentUrlDto.setPaymentUrl(Objects.requireNonNull(response.getBody()).getPaymentUrl());

        return paymentUrlDto;
    }

    @GetMapping("/paymentSuccessful/{paymentId}")
    public RedirectUrlDto getPaymentSuccess(@PathVariable Long paymentId) {
        return getRedirectUrlDto(paymentId);
    }

    @GetMapping("/paymentCanceled/{paymentId}")
    public RedirectUrlDto getPaymentCanceled(@PathVariable Long paymentId) {
        return getRedirectUrlDto(paymentId);
    }

    private RedirectUrlDto getRedirectUrlDto(@PathVariable Long paymentId) {
        RedirectUrlDto redirectUrlDto = new RedirectUrlDto();

        Payment payment = paymentService.getOne(paymentId);
        redirectUrlDto.setRedirectUrl(payment.getRedirectUrl());

        ResponseEntity<ApiResponseDto> response = getOrder(payment);

        payment.setStatus(Objects.requireNonNull(response.getBody()).getStatus());

        paymentService.save(payment);

        return redirectUrlDto;
    }
}
