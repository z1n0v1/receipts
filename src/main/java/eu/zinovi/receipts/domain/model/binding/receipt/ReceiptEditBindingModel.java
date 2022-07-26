package eu.zinovi.receipts.domain.model.binding.receipt;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceiptEditBindingModel {

    @NotNull(message = "Не е подаден идентификатор на касовия бележка")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            message = "Не е подаден валиден идентификатор на касовия бележка")
    private String id;

    @NotNull(message = "Не е подаден ЕИК на компанията")
    @Pattern(regexp = "^[0-9]{9}$", message = "ЕИК-то трябва да е съставен от 9 цифри")
    private String eik;

    @NotBlank(message = "Не е подадено името на магазина")
    private String name;

    @NotNull(message = "Не е подадена сумата на касовата бележка")
    @PositiveOrZero(message = "Не е подадена сума на касовата бележка")
    private BigDecimal total;


    @NotBlank(message = "Не е подаден адреса на магазина")
    private String address;

    @NotNull(message = "Не е подадена дата на касовата бележка")
    @PastOrPresent(message = "Датата на касовата бележка не може да бъде в бъдещето")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
}
