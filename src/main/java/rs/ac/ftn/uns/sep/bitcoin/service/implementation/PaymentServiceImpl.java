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

import java.util.List;
import java.util.Objects;

import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.getOrder;
import static rs.ac.ftn.uns.sep.bitcoin.utils.PaymentUtils.postOrder;
import static rs.ac.ftn.uns.sep.bitcoin.utils.globals.PaymentConstants.Info.CURRENCY;
import static rs.ac.ftn.uns.sep.bitcoin.utils.globals.PaymentConstants.Info.TITLE;
import static rs.ac.ftn.uns.sep.bitcoin.utils.globals.PaymentConstants.Url.CANCEL_URL;
import static rs.ac.ftn.uns.sep.bitcoin.utils.globals.PaymentConstants.Url.SUCCESS_URL;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

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
    public String findRedirectUrl(Long paymentId) {
        Payment payment = paymentRepository.getOne(paymentId);

        ResponseEntity<ApiResponseDto> response = getOrder(payment);
        String status = Objects.requireNonNull(response.getBody()).getStatus();

        LOGGER.info("Basic payment info: " + Objects.requireNonNull(response.getBody()).toString());

        if (!payment.getStatus().equalsIgnoreCase(status)) {
            LOGGER.info("Changing payment status into: " + status);
            changeStatus(payment, status);
        }

        return payment.getRedirectUrl();
    }

    @Override
    public void checkStatus() {
        List<Payment> notCompletedPayments = paymentRepository.findAllByStatusNotPaid();

        for (Payment payment : notCompletedPayments) {
            String status = Objects.requireNonNull(getOrder(payment).getBody()).getStatus();

            if (!payment.getStatus().equalsIgnoreCase(status)) {
                LOGGER.info(String.format("Status of payment with order id %d set to: %s", payment.getOrderId(), status));
                changeStatus(payment, status);
            }
        }
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

    private void changeStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);
    }
}
