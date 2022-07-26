package eu.zinovi.receipts.domain.model.mapper;

import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.domain.model.view.admin.AdminCapabilityView;
import eu.zinovi.receipts.domain.model.view.admin.AdminRoleView;
import eu.zinovi.receipts.domain.model.view.admin.AdminRoleUserView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdminRoleToView {

    @Mappings({
        @Mapping(source = "name", target = "role"),
    })
    AdminRoleView map(Role Role);

    default List<AdminCapabilityView> capabilitiesToAdminView(Collection<Capability> capabilities) {
        List<AdminCapabilityView> adminCapabilityViews = new ArrayList<>();

        for (CapabilityEnum capabilityEnum :CapabilityEnum.values()) {


            AdminCapabilityView adminCapabilityView = new AdminCapabilityView();
            adminCapabilityView.setCapability(capabilityEnum.toString());
            adminCapabilityView.setDescription(capabilityEnum.getDescription());
            adminCapabilityView.setActive(false);
            for (Capability capability : capabilities) {
                if (capability.getName() == capabilityEnum) {
                    adminCapabilityView.setActive(true);
                    break;
                }
            }


            adminCapabilityViews.add(adminCapabilityView);
        }
        return adminCapabilityViews;
    }

    default List<AdminRoleUserView> usersToAdminView(Collection<User> users) {
        return users.stream()
                .map(ar -> {
                    AdminRoleUserView view = new AdminRoleUserView();
                    view.setEmail(ar.getEmail());
                    return view;
                })
                .collect(Collectors.toList());
    }

}
