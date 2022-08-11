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

    boolean existsById(UUID id);

    @Transactional
    ReceiptDetailsView getReceiptDetails(UUID id);

    List<ReceiptListView> getUserReceiptsWithDate();

    @Transactional
    List<ReceiptListView> getAllReceiptsWithDate();

    @Transactional
    ToDatatable getUserReceipts(FromDatatable fromDatatable);

    @Transactional
    ToDatatable getAllReceipts(FromDatatable fromDatatable);

    @Transactional
    AdminReceiptView getAdminReceipt(UUID receiptId);

    @Transactional
    void saveReceipt(ReceiptEditServiceModel receiptEditServiceModel);

    @Transactional
    void deleteReceipt(ReceiptDeleteServiceModel receiptDeleteServiceModel);

    @Transactional
    void addItem(ItemAddServiceModel itemAddServiceModel);

    @Transactional
    void deleteItem(ItemDeleteServiceModel itemDeleteServiceModel);
}
