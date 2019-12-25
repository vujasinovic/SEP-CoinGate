package rs.ac.ftn.uns.sep.bitcoin.service;

import rs.ac.ftn.uns.sep.bitcoin.model.Payment;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.ApiResponseDto;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PreparedPaymentDto;

public interface PaymentService {
    PreparedPaymentDto preparePayment(KpRequest kpRequest);

    Payment persist(ApiResponseDto apiResponseDto, PreparedPaymentDto preparedPaymentDto);

    Payment getOne(Long id);

    Payment save(Payment payment);
}
