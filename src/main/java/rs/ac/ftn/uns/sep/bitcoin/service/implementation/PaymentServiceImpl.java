package rs.ac.ftn.uns.sep.bitcoin.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.model.Seller;
import rs.ac.ftn.uns.sep.bitcoin.repository.PaymentRepository;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.service.SellerService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.ApiResponseDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PreparedPaymentDto;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String TEST_ORDER = "Test order";
    private static final String BITCOIN = "BTC";
    private static final String SUCCESS_URL = "http://localhost:8080/paymentSuccessful/";
    private static final String CANCEL_URL = "http://localhost:8080/paymentCanceled/";

    private final PaymentRepository paymentRepository;

    private final SellerService sellerService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, SellerService sellerService) {
        this.paymentRepository = paymentRepository;
        this.sellerService = sellerService;
    }

    @Override
    public PreparedPaymentDto preparePayment(KpRequest kpRequest) {
        LOGGER.info("Preparing payment...");

        PreparedPaymentDto preparedPaymentDto = new PreparedPaymentDto();

        Seller seller = sellerService.findByEmail(kpRequest.getEmail());

        Payment savedPayment = paymentRepository.save(new Payment());

        preparedPaymentDto.setPaymentId(savedPayment.getId());

        preparedPaymentDto.setAmount(kpRequest.getAmount());
        preparedPaymentDto.setApiToken(seller.getApiToken());

        preparedPaymentDto.setCurrency(BITCOIN);

        preparedPaymentDto.setTitle(TEST_ORDER);

        preparedPaymentDto.setSuccessUrl(SUCCESS_URL + savedPayment.getId());

        preparedPaymentDto.setCancelUrl(CANCEL_URL + savedPayment.getId());

        preparedPaymentDto.setRedirectUrl(kpRequest.getRedirectUrl());

        LOGGER.info("Prepared payment info: " + preparedPaymentDto.toString());
        return preparedPaymentDto;
    }

    @Override
    public Payment persist(ApiResponseDto apiResponseDto, PreparedPaymentDto preparedPaymentDto) {
        Payment payment = paymentRepository.getOne(preparedPaymentDto.getPaymentId());

        Seller seller = sellerService.findByApiToken(preparedPaymentDto.getApiToken());

        payment.setOrderId(apiResponseDto.getId());
        payment.setSeller(seller);
        payment.setAmount(preparedPaymentDto.getAmount());
        payment.setRedirectUrl(preparedPaymentDto.getRedirectUrl());
        payment.setStatus(apiResponseDto.getStatus());

        LOGGER.info("Persisting payment: " + payment.toString());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getOne(Long id) {
        return paymentRepository.getOne(id);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
