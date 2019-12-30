package rs.ac.ftn.uns.sep.bitcoin.service;

import rs.ac.ftn.uns.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.ftn.uns.sep.bitcoin.utils.dto.PaymentUrlDto;

public interface PaymentService {
    PaymentUrlDto sendOrder(KpRequest kpRequest);

    String getRedirectUrl(Long paymentId);
}
