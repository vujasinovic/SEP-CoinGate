package rs.ac.ftn.uns.sep.bitcoin.utils.dto;

import lombok.Data;

@Data
public class PaymentUrlDto {
    private Long paymentId;
    private String redirect;
}
