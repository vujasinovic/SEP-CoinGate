package rs.ac.uns.ftn.sep.bitcoin.service;

import rs.ac.uns.ftn.sep.bitcoin.utils.dto.KpRequest;
import rs.ac.uns.ftn.sep.bitcoin.utils.dto.PaymentUrlDto;

public interface PaymentService {
    /**
     * Method that receives request from KP and sends POST request to CoinGate API.
     *
     * @param kpRequest - contains information about seller email, amount that is being transferred and
     *                  redirect url
     * @return url where user can finish payment.
     */
    PaymentUrlDto sendOrder(KpRequest kpRequest);

    /**
     * Finds Payment with provided paymentId and checks if payment status has changed.
     * If status is changed, new status is set and persisted.
     *
     * @param paymentId
     * @return payment redirect url
     */
    String findRedirectUrl(Long paymentId);

    /**
     * Method that finds unfinished payments and communicate with CoinGate API.
     * After API response it changes payment status if needed.
     */
    void checkStatus();
}
