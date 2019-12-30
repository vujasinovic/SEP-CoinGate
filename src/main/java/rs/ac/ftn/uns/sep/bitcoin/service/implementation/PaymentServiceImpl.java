package rs.ac.ftn.uns.sep.bitcoin.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.model.Seller;
import rs.ac.ftn.uns.sep.bitcoin.repository.PaymentRepository;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.service.SellerService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.ApiResponseDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PaymentUrlDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PreparedPaymentDto;

import java.util.Objects;

import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.getOrder;
import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.postOrder;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String TITLE = "Test order";
    private static final String CURRENCY = "BTC";
    private static final String SUCCESS_URL = "http://localhost:8080/paymentSuccessful/";
    private static final String CANCEL_URL = "http://localhost:8080/paymentCanceled/";

    private final PaymentRepository paymentRepository;

    private final SellerService sellerService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, SellerService sellerService) {
        this.paymentRepository = paymentRepository;
        this.sellerService = sellerService;
    }

    @Override
    public PaymentUrlDto sendOrder(KpRequest kpRequest) {
        PreparedPaymentDto preparedPaymentDto = prepare(kpRequest);

        ResponseEntity<ApiResponseDto> response = postOrder(preparedPaymentDto);

        String paymentUrl = Objects.requireNonNull(response.getBody()).getPaymentUrl();

        LOGGER.info("Payment URL: " + paymentUrl);

        persist(response.getBody(), preparedPaymentDto);

        return PaymentUrlDto.builder().paymentUrl(paymentUrl).build();
    }

    @Override
    public String getRedirectUrl(Long paymentId) {
        Payment payment = paymentRepository.getOne(paymentId);

        LOGGER.info("Getting payment information..");
        ResponseEntity<ApiResponseDto> response = getOrder(payment);
        LOGGER.info("Basic payment info: " + Objects.requireNonNull(response.getBody()).toString());

        String status = response.getBody().getStatus();

        LOGGER.info("Changing payment status into: " + status);
        payment.setStatus(status);

        LOGGER.info("Persisting payment");
        Payment persistedPayment = paymentRepository.save(payment);
        LOGGER.info("Payment persisted: " + persistedPayment.toString());

        return payment.getRedirectUrl();
    }

    private void persist(ApiResponseDto apiResponseDto, PreparedPaymentDto preparedPaymentDto) {
        Payment payment = paymentRepository.getOne(preparedPaymentDto.getPaymentId());

        payment.setOrderId(apiResponseDto.getId());

        payment.setSeller(sellerService.findByApiToken(preparedPaymentDto.getApiToken()));

        payment.setAmount(preparedPaymentDto.getAmount());

        payment.setRedirectUrl(preparedPaymentDto.getRedirectUrl());

        payment.setStatus(apiResponseDto.getStatus());

        LOGGER.info("Persisting payment: " + payment.toString());

        paymentRepository.save(payment);
    }

    private PreparedPaymentDto prepare(KpRequest kpRequest) {
        Seller seller = sellerService.findByEmail(kpRequest.getEmail());

        Long paymentId = paymentRepository.save(new Payment()).getId();

        PreparedPaymentDto preparedPaymentDto = PreparedPaymentDto.builder()
                .amount(kpRequest.getAmount()).apiToken(seller.getApiToken()).title(TITLE).currency(CURRENCY)
                .successUrl(SUCCESS_URL + paymentId).cancelUrl(CANCEL_URL + paymentId)
                .redirectUrl(kpRequest.getRedirectUrl()).paymentId(paymentId)
                .build();

        LOGGER.info("Prepared payment info: " + preparedPaymentDto.toString());
        return preparedPaymentDto;
    }
}
