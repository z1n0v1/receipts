package eu.zinovi.receipts.service;

import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.service.AdminUserSaveServiceModel;
import eu.zinovi.receipts.domain.model.view.admin.AdminCapabilityView;
import eu.zinovi.receipts.domain.model.view.admin.AdminUserView;

import javax.transaction.Transactional;
import java.util.List;

public interface AdminService {
    @Transactional
    ToDatatable listReceipts(FromDatatable fromDatatable);

    @Transactional
    ToDatatable listUsers(FromDatatable fromDatatable);

    @Transactional
    AdminUserView getUserDetails(String email);

    @Transactional
    void updateUser(AdminUserSaveServiceModel adminUserSaveServiceModel);

    @Transactional
    List<AdminCapabilityView> getCapabilities();
}
