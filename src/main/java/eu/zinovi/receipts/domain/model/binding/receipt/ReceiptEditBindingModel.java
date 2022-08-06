package eu.zinovi.receipts.domain.model.binding.receipt;

import eu.zinovi.receipts.domain.model.validation.ReceiptExists;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor @AllArgsConstructor
public class ReceiptEditBindingModel {

    @NotNull(message = REQUIRED_RECEIPT_ID)
    @ReceiptExists
    private String id;

    @NotNull(message = REQUIRED_EIK)
    @Pattern(regexp = "^[0-9]{9}$", message = INVALID_EIK)
    private String eik;

    @NotBlank(message = REQUIRED_STORE_NAME)
    private String name;

    @NotNull(message = REQUIRED_RECEIPT_SUM)
    @PositiveOrZero(message = INVALID_RECEIPT_SUM)
    private BigDecimal total;


    @NotBlank(message = REQUIRED_STORE_ADDRESS)
    private String address;

    @NotNull(message = REQUIRED_RECEIPT_DATE)
    @PastOrPresent(message = INVALID_RECEIPT_DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
}
