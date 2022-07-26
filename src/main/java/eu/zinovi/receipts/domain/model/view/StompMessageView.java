package eu.zinovi.receipts.domain.model.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class StompMessageView {
    private String message;
    private String type;
}
