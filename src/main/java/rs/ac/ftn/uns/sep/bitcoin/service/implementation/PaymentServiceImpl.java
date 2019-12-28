package rs.ac.ftn.uns.sep.bitcoin.service.implementation;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.model.Seller;
import rs.ac.ftn.uns.sep.bitcoin.properties.BitcoinProperties;
import rs.ac.ftn.uns.sep.bitcoin.repository.PaymentRepository;
import rs.ac.ftn.uns.sep.bitcoin.service.PaymentService;
import rs.ac.ftn.uns.sep.bitcoin.service.SellerService;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.ApiResponseDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PreparedPaymentDto;
import rs.ac.uns.ftn.sep.commons.helper.UrlHelper;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String TEST_ORDER = "Test order";
    private static final String BITCOIN = "BTC";
    private static final String URL_PATH_SUCCESS = "paymentSuccessful";
    private static final String URL_PATH_CANCEL = "paymentCanceled";

    private static final String STATUS_PAID = "paid";

    private final PaymentRepository paymentRepository;
    private final SellerService sellerService;
    private final BitcoinProperties properties;

    @Override
    public PreparedPaymentDto preparePayment(KpRequest kpRequest) {
        LOGGER.info("Preparing payment...");

        PreparedPaymentDto preparedPaymentDto = new PreparedPaymentDto();

        Seller seller = sellerService.findByEmail(kpRequest.getMerchantName());

        Payment savedPayment = paymentRepository.save(new Payment());

        preparedPaymentDto.setPaymentId(savedPayment.getId());

        preparedPaymentDto.setAmount(kpRequest.getAmount());
        preparedPaymentDto.setApiToken(seller.getApiToken());

        preparedPaymentDto.setCurrency(BITCOIN);

        preparedPaymentDto.setTitle(TEST_ORDER);

        preparedPaymentDto.setSuccessUrl(getSuccessUrl(savedPayment.getId()));

        preparedPaymentDto.setCancelUrl(getCancelUrl(savedPayment.getId()));

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

    @Override
    public boolean getStatus(Long id) {
        Payment payment = paymentRepository.getOne(id);
        return Objects.equals(payment.getStatus(), STATUS_PAID);
    }

    private String getSuccessUrl(Long id) {
        String baseUrl = properties.getUrl();

        String successUrl = UrlHelper.addPathVariables(baseUrl, URL_PATH_SUCCESS, id.toString());

        return successUrl;
    }

    private String getCancelUrl(Long id) {
        String baseUrl = properties.getUrl();

        String cancelUrl = UrlHelper.addPathVariables(baseUrl, URL_PATH_CANCEL, id.toString());

        return cancelUrl;
    }
}
