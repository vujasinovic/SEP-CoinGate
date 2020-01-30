package rs.ac.uns.ftn.sep.bitcoin.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlDto {
    private Long paymentId;
    private String redirect;
}
