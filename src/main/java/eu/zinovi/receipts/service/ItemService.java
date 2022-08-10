package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.entity.Item;
import eu.zinovi.receipts.domain.model.service.ItemEditServiceModel;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<Item> getItems(UUID receiptId);

    ToDatatable getItemListDatatable(UUID receiptId, FromDatatable fromDatatable);

    @Transactional
    void updateItem(ItemEditServiceModel itemEditServiceModel);
}
