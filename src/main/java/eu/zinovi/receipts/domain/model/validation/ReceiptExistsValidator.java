package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.impl.ReceiptsServiceImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;
import java.util.regex.Pattern;

public class ReceiptExistsValidator implements ConstraintValidator<ReceiptExists, String> {
    private final ReceiptsServiceImpl receiptsServiceImpl;

    public ReceiptExistsValidator(ReceiptsServiceImpl receiptsServiceImpl) {
        this.receiptsServiceImpl = receiptsServiceImpl;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (Pattern.compile(
                "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
                .matcher(value).matches()) {
            return receiptsServiceImpl.existsById(UUID.fromString(value));
        }

        return false;
    }
}
