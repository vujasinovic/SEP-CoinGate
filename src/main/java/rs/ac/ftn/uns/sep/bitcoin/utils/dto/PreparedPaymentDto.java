package rs.ac.ftn.uns.sep.bitcoin.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
