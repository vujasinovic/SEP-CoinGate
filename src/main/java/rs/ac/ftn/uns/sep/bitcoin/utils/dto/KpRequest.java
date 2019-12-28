package rs.ac.ftn.uns.sep.bitcoin.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpRequest {
    private String merchantName;
    private BigDecimal amount;
    private String redirectUrl;
}
