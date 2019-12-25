package rs.ac.ftn.uns.sep.bitcoin.utils.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PreparedPaymentDto {
    String apiToken;

    BigDecimal amount;

    String successUrl;

    String cancelUrl;

    String currency;

    String title;

    String redirectUrl;

     Long paymentId;
}
