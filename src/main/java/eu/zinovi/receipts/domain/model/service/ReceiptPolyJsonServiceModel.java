package eu.zinovi.receipts.domain.model.service;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class ReceiptPolyJsonServiceModel {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final int x3;
    private final int y3;
    private final int x4;
    private final int y4;
    private final String text;
}
