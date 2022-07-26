package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.view.ReceiptListView;
import eu.zinovi.receipts.domain.model.entity.ReceiptImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReceiptImageToListViewMapper {

    ReceiptImageToListViewMapper INSTANCE = Mappers.getMapper(ReceiptImageToListViewMapper.class);

    @Mapping(source = "receipt.id", target = "receiptId")
    ReceiptListView receiptListView (ReceiptImage receiptImage);

}
