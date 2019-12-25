package rs.ac.ftn.uns.sep.bitcoin.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.ApiResponseDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PreparedPaymentDto;

public final class PaymentUtils {
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "Token ";
    private static final String API_ORDERS = "https://api-sandbox.coingate.com/v2/orders";
    private static final String PRICE_AMOUNT = "price_amount";
    private static final String PRICE_CURRENCY = "price_currency";
    private static final String RECEIVE_CURRENCY = "receive_currency";
    private static final String TITLE = "title";
    private static final String SUCCESS_URL = "success_url";
    private static final String CANCEL_URL = "cancel_url";
    private static final String PARAMS = "parameters";


    private PaymentUtils() {

    }

    public static ResponseEntity<ApiResponseDto> postOrder(PreparedPaymentDto preparedPaymentDto) {
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

        return restTemplate.postForEntity(API_ORDERS, request, ApiResponseDto.class);
    }

    public static ResponseEntity<ApiResponseDto> getOrder(Payment payment) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.add(AUTHORIZATION, TOKEN + payment.getSeller().getApiToken());

        HttpEntity<String> request = new HttpEntity<>(PARAMS, headers);

        return restTemplate.exchange(API_ORDERS + "/" + payment.getOrderId(), HttpMethod.GET, request, ApiResponseDto.class);
    }
}
