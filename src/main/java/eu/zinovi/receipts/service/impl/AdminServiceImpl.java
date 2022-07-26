package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.ReceiptImage;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.datatable.FromDatatable;
import eu.zinovi.receipts.domain.model.datatable.ToDatatable;
import eu.zinovi.receipts.domain.model.mapper.CapabilityToAdminView;
import eu.zinovi.receipts.domain.model.mapper.UserToAdminView;
import eu.zinovi.receipts.domain.model.service.*;
import eu.zinovi.receipts.domain.model.view.admin.AdminCapabilityView;
import eu.zinovi.receipts.domain.model.view.admin.AdminUserRoleView;
import eu.zinovi.receipts.domain.model.view.admin.AdminUserView;
import eu.zinovi.receipts.domain.exception.AccessDeniedException;
import eu.zinovi.receipts.domain.exception.EntityNotFoundException;
import eu.zinovi.receipts.repository.*;
import eu.zinovi.receipts.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static eu.zinovi.receipts.util.constants.MessageConstants.*;

@Service
public class AdminServiceImpl implements AdminService {
    private final CapabilityToAdminView capabilityToAdminView;
    private final UserToAdminView userToAdminView;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CapabilityRepository capabilityRepository;
    private final ReceiptImageRepository receiptImageRepository;

    public AdminServiceImpl(
            CapabilityToAdminView capabilityToAdminView,
            UserToAdminView userToAdminView,
            RoleRepository roleRepository,
            UserRepository userRepository,
            CapabilityRepository capabilityRepository,
            ReceiptImageRepository receiptImageRepository) {
        this.capabilityToAdminView = capabilityToAdminView;
        this.userToAdminView = userToAdminView;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.capabilityRepository = capabilityRepository;
        this.receiptImageRepository = receiptImageRepository;
    }

    @Override
    @Transactional
    public ToDatatable listReceipts(FromDatatable fromDatatable) {

        String[][] sortOrder = fromDatatable.getSortOrder();
        Sort sort = null;

        for (String[] sortLine : sortOrder) {
            switch (sortLine[0]) {
                case "addedOn" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "addedOn") :
                        Sort.by(Sort.Direction.DESC, "addedOn");
                case "isProcessed" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "isProcessed") :
                        Sort.by(Sort.Direction.DESC, "isProcessed");
                default -> sort = Sort.by(Sort.Direction.ASC, "addedOn");
            }
        }

        Pageable pageable = PageRequest.of(fromDatatable.getStart() / fromDatatable.getLength(),
                fromDatatable.getLength(),
                sort);

        Page<ReceiptImage> page = receiptImageRepository.findAll(pageable);

        ToDatatable toDatatable = new ToDatatable();
        toDatatable.setRecordsTotal(page.getTotalElements());
        toDatatable.setDraw(fromDatatable.getDraw());
        toDatatable.setRecordsFiltered(page.getTotalElements());

        String[][] result = new String[page.getContent().size()][6];
        for (int i = 0; i < page.getContent().size(); i++) {
            ReceiptImage receiptImage = page.getContent().get(i);
            result[i][0] = receiptImage.getReceipt().getId().toString();
            result[i][1] = receiptImage.getAddedOn()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            result[i][2] = receiptImage.getUser().getEmail();
            result[i][3] = receiptImage.getIsProcessed() ? "Да" : "Не";
            result[i][4] = receiptImage.getReceipt().getTotal() == null ? "" :
                    receiptImage.getReceipt().getTotal().toString();
            result[i][5] = receiptImage.getReceipt().getItemsTotal() == null ? "" :
                    receiptImage.getReceipt().getItemsTotal().toString();
        }

        toDatatable.setData(result);
        return toDatatable;
    }

    @Override
    @Transactional
    public ToDatatable listUsers(FromDatatable fromDatatable) {
        String[][] sortOrder = fromDatatable.getSortOrder();
        Sort sort = null;

        for (String[] sortLine : sortOrder) {
            switch (sortLine[0]) {
                case "registeredOn" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "registeredOn") :
                        Sort.by(Sort.Direction.DESC, "registeredOn");
                case "lastSeen" -> sort = sortLine[1].equals("asc") ?
                        Sort.by(Sort.Direction.ASC, "lastSeen") :
                        Sort.by(Sort.Direction.DESC, "lastSeen");
                default -> sort = Sort.by(Sort.Direction.ASC, "lastSeen");
            }
        }

        Pageable pageable = PageRequest.of(fromDatatable.getStart() / fromDatatable.getLength(),
                fromDatatable.getLength(),
                sort);

        Page<User> page;

        if (fromDatatable.getSearch().getValue() == null || fromDatatable.getSearch().getValue().isEmpty()) {
            page = userRepository.findAll(pageable);
        } else {
            page = userRepository.findAllByEmailContaining(fromDatatable.getSearch().getValue(), pageable);
        }

        ToDatatable toDatatable = new ToDatatable();
        toDatatable.setRecordsTotal(page.getTotalElements());
        toDatatable.setDraw(fromDatatable.getDraw());
        toDatatable.setRecordsFiltered(page.getTotalElements());


        String[][] result = new String[page.getContent().size()][6];
        for (int i = 0; i < page.getContent().size(); i++) {
            User user = page.getContent().get(i);
//            result[i][0] = user.getId().toString();
            result[i][0] = user.getRegisteredOn()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            result[i][1] = user.getLastSeen()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            result[i][2] = user.getEmail();
            result[i][3] = String.format("%d", user.getReceipts().size());
            BigDecimal total = userRepository.sumByReceiptsTotalByUser(user.getId());
            result[i][4] = total == null ? "0" : total.toString();
        }

        toDatatable.setData(result);
        return toDatatable;
    }

    @Override
    @Transactional
    public AdminUserView getUserDetails(String email) {
        User user = userRepository.getByEmail(email).orElseThrow(EntityNotFoundException::new);
        AdminUserView adminUserView = userToAdminView.map(user);

        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        List<String> userRoles = user.getRoles().stream().map(Role::getName).toList();
        

        List<AdminUserRoleView> adminUserRoleViews = roles.stream()
                .map(role -> new AdminUserRoleView(role, userRoles.contains(role)))
                .toList();

        adminUserView.setRoles(adminUserRoleViews);

        adminUserView.setGoogleId(user.getGoogleId() != null && !user.getGoogleId().isEmpty());
        return adminUserView;
    }

    @Override
    @Transactional
    public void updateUser(AdminUserSaveServiceModel adminUserSaveServiceModel) {

        User user = userRepository.getByEmail(adminUserSaveServiceModel.getEmail())
                .orElseThrow(EntityNotFoundException::new);


        Role adminRole = roleRepository.getByName("ADMIN").orElseThrow(EntityNotFoundException::new);

        if (user.getRoles().contains(adminRole)) {
            throw new AccessDeniedException(NO_PERMISSION_EDIT_ADMIN);
        }

        Collection<Role> roles = new HashSet<>();
        for (AdminUserRoleServiceModel roleServiceModel : adminUserSaveServiceModel.getRoles()) {
            if (roleServiceModel.getSelected()) {
                Role role = roleRepository.getByName(roleServiceModel.getName())
                        .orElseThrow(EntityNotFoundException::new);
                roles.add(role);
            }
        }

        user.setFirstName(adminUserSaveServiceModel.getFirstName());
        user.setLastName(adminUserSaveServiceModel.getLastName());
        user.setDisplayName(adminUserSaveServiceModel.getDisplayName());
        user.setEnabled(adminUserSaveServiceModel.getEnabled());
        user.setEmailLoginDisabled(adminUserSaveServiceModel.getEmailLoginDisabled());
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<AdminCapabilityView> getCapabilities() {
        List<AdminCapabilityView> capabilities = capabilityRepository.findAll().stream()
                .map( capabilityToAdminView::map).toList();
        if (capabilities.isEmpty()) {
            throw new EntityNotFoundException(NOT_FOUND_CAPABILITY);
        }
        return capabilities;
    }
}
