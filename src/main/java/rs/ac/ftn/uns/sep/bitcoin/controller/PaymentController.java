package rs.ac.ftn.uns.sep.bitcoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.*;

@RestController
@RequestMapping("/")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private static final String PARAMS = "parameters";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "Token ";
    private static final String API_ORDERS = "https://api-sandbox.coingate.com/v2/orders";
    private static final String PRICE_AMOUNT = "price_amount";
    private static final String PRICE_CURRENCY = "price_currency";
    private static final String RECEIVE_CURRENCY = "receive_currency";
    private static final String TITLE = "title";
    private static final String SUCCESS_URL = "success_url";
    private static final String CANCEL_URL = "cancel_url";

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentUrlDto postPreparePayment(KpRequest kpRequest) {
        PaymentUrlDto paymentUrlDto = new PaymentUrlDto();

        PreparedPaymentDto preparedPaymentDto = paymentService.preparePayment(kpRequest);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, TOKEN + preparedPaymentDto.getApiToken());

        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();

        bodyParams.add(PRICE_AMOUNT, preparedPaymentDto.getAmount().toString());
        bodyParams.add(PRICE_CURRENCY, preparedPaymentDto.getCurrency());
        bodyParams.add(RECEIVE_CURRENCY, preparedPaymentDto.getCurrency());
        bodyParams.add(TITLE, preparedPaymentDto.getTitle());
        bodyParams.add(SUCCESS_URL, preparedPaymentDto.getSuccessUrl());
        bodyParams.add(CANCEL_URL, preparedPaymentDto.getCancelUrl());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParams, headers);

        ResponseEntity<ApiResponseDto> response = restTemplate.postForEntity(API_ORDERS, request, ApiResponseDto.class);

        paymentService.persist(response.getBody(), preparedPaymentDto);

        paymentUrlDto.setPaymentUrl(response.getBody().getPaymentUrl());

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
        RedirectUrlDto redirectUrlDto= new RedirectUrlDto();

        Payment payment = paymentService.getOne(paymentId);
        redirectUrlDto.setRedirectUrl(payment.getRedirectUrl());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.add(AUTHORIZATION, TOKEN + payment.getSeller().getApiToken());

        HttpEntity<String> request = new HttpEntity<>(PARAMS, headers);

        ResponseEntity<ApiResponseDto> response = restTemplate.exchange(API_ORDERS + "/" + payment.getOrderId(), HttpMethod.GET, request, ApiResponseDto.class);

        payment.setStatus(response.getBody().getStatus());

        paymentService.save(payment);

        return redirectUrlDto;
    }
}
