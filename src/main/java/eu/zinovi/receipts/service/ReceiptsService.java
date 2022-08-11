package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.service.ItemAddServiceModel;
import eu.zinovi.receipts.domain.model.service.ItemDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.ReceiptDeleteServiceModel;
import eu.zinovi.receipts.domain.model.service.ReceiptEditServiceModel;
import eu.zinovi.receipts.domain.model.view.ReceiptDetailsView;
import eu.zinovi.receipts.domain.model.view.ReceiptListView;
import eu.zinovi.receipts.domain.model.view.admin.AdminReceiptView;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface ReceiptsService {
    @Transactional
    UUID uploadReceipt(MultipartFile file) throws ReceiptProcessException;

    @Transactional
    List<ReceiptListView> getAllReceiptImagesWithDate();

    List<ReceiptListView> getReceiptImagesWithDate();

    @Transactional
    void deleteReceipt(ReceiptDeleteServiceModel receiptDeleteServiceModel);

    @Transactional
    ReceiptDetailsView getReceiptDetails(UUID id);

    @Transactional
    void deleteItem(ItemDeleteServiceModel itemDeleteServiceModel);

    @Transactional
    void addItem(ItemAddServiceModel itemAddServiceModel);

    @Transactional
    ToDatatable getAllReceipts(FromDatatable fromDatatable);

    @Transactional
    ToDatatable getReceipts(FromDatatable fromDatatable);

    @Transactional
    void saveReceipt(ReceiptEditServiceModel receiptEditServiceModel);

    boolean existsById(UUID id);

    @Transactional
    AdminReceiptView getAdminReceipt(UUID receiptId);
}
