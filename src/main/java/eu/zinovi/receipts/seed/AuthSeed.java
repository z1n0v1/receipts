package eu.zinovi.receipts.seed;


import eu.zinovi.receipts.domain.model.entity.Capability;
import eu.zinovi.receipts.domain.model.entity.Category;
import eu.zinovi.receipts.domain.model.entity.Role;
import eu.zinovi.receipts.domain.model.entity.User;
import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;
import eu.zinovi.receipts.repository.CapabilityRepository;
import eu.zinovi.receipts.repository.CategoryRepository;
import eu.zinovi.receipts.repository.RoleRepository;
import eu.zinovi.receipts.repository.UserRepository;
import eu.zinovi.receipts.service.StatisticsService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static eu.zinovi.receipts.domain.model.enums.CapabilityEnum.*;

@Component
public class AuthSeed implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CapabilityRepository capabilityRepository;
    private final CategoryRepository categoryRepository;
    private final StatisticsService statisticsService;
    private final PasswordEncoder passwordEncoder;
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthSeed.class);

    public AuthSeed(UserRepository userRepository, RoleRepository roleRepository, CapabilityRepository capabilityRepository, CategoryRepository categoryRepository, StatisticsService statisticsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.capabilityRepository = capabilityRepository;
        this.categoryRepository = categoryRepository;
        this.statisticsService = statisticsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) {

//        Capability capability = new Capability();
//        capability.setName(CAP_EDIT_RECEIPT);
//        capability.setDescription(CAP_EDIT_RECEIPT.getDescription());
//        capabilityRepository.save(capability);

        Capability capability = capabilityRepository.getByName(CAP_EDIT_RECEIPT).orElse(null);
        if (capability == null) {
            capability = new Capability();
            capability.setName(CAP_EDIT_RECEIPT);
            capability.setDescription(CAP_EDIT_RECEIPT.getDescription());
            capabilityRepository.save(capability);
        }
        Role role = roleRepository.getByName("ADMIN").orElseThrow();
        if (!role.getCapabilities().contains(capability)) {
            role.getCapabilities().add(capability);
            roleRepository.save(role);
        }
        role = roleRepository.getByName("USER").orElseThrow();
        if (!role.getCapabilities().contains(capability)) {
            role.getCapabilities().add(capability);
            roleRepository.save(role);
        }

        // Calculate the statistics in case there was problem with the cron job
        statisticsService.calculateStatistics();

        if (categoryRepository.count() == 0) {
            LOGGER.info("Seeding categories");
            categoryRepository.save(new Category("Храна и напитки", "#e40101"));
            categoryRepository.save(new Category("Дрехи", "#00cc00"));
            categoryRepository.save(new Category("Здраве", "#ae00ff"));
            categoryRepository.save(new Category("Електроника", "#0084ff"));
            categoryRepository.save(new Category("Други", "#474747"));
        }

        if (userRepository.count() == 0) {

            LOGGER.info("Seeding initial data");

            User admin = new User();
//            System.out.println(admin);
            admin.setFirstName("Zinovi");
            admin.setLastName("Boyadjiev");
            admin.setEmail("zinovi@gmail.com");
            admin.setDisplayName("z");
            admin.setLastSeen(LocalDateTime.now());
            admin.setRegisteredOn(LocalDateTime.now());
            admin.setEnabled(true);

            Role adminRole = new Role();
            adminRole.setName("ADMIN");

            for (CapabilityEnum privilege : CapabilityEnum.values()) {
                Capability currentCapability = new Capability();
                currentCapability.setName(privilege);
                currentCapability.setDescription(privilege.getDescription());
                capabilityRepository.save(currentCapability);
                adminRole.getCapabilities().add(currentCapability);
            }

            Role userRole = new Role();
            userRole.setName("USER");

            roleAddCapabilities(userRole,
                    CAP_VIEW_HOME, CAP_ADD_RECEIPT, CAP_VIEW_RECEIPT,
                    CAP_LIST_RECEIPTS, CAP_DELETE_RECEIPT, CAP_DELETE_ITEM,
                    CAP_ADD_ITEM, CAP_EDIT_ITEM, CAP_LIST_ITEMS,
                    CAP_CHANGE_PASSWORD, CAP_EDIT_USER, CAP_VIEW_USER_DETAILS);

            /*
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_ADD_RECEIPT).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_LIST_RECEIPTS).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_VIEW_RECEIPT).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_DELETE_RECEIPT).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_ADD_ITEM).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_EDIT_ITEM).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_LIST_ITEMS).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
//                    getByName(CapabilityEnum.CAP_DELETE_ITEM).orElse(null));
//            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_VIEW_HOME).orElse(null));
            userRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_EDIT_USER).orElse(null));
*/

            User demoUser = new User();
            demoUser.setFirstName("Demo");
            demoUser.setLastName("User");
            demoUser.setEmail("demo@demo");
            demoUser.setDisplayName("Demo User");
            demoUser.setPicture("/images/blank-avatar.png");
            demoUser.setLastSeen(LocalDateTime.now());
            demoUser.setRegisteredOn(LocalDateTime.now());

            // No creds here, move along
            demoUser.setPassword(passwordEncoder.encode(System.getenv("DEMO_USER_PASSWORD")));
            demoUser.setEnabled(true);
            demoUser.setEmailVerified(true);
            demoUser.setEmailLoginDisabled(false);

            Role demoRole = new Role();
            demoRole.setName("DEMO");

            roleAddCapabilities(demoRole, CAP_ADMIN,
                    CAP_ADMIN_LIST_RECEIPTS, CAP_ADMIN_VIEW_RECEIPT,
                    CAP_VIEW_USER_DETAILS, CAP_LIST_USERS,
                    CAP_LIST_ROLES, CAP_LIST_CAPABILITIES, CAP_LIST_CATEGORIES,
                    CAP_VIEW_HOME, CAP_ADD_RECEIPT, CAP_VIEW_RECEIPT,
                    CAP_LIST_RECEIPTS, CAP_LIST_ITEMS);

            /*
            demoRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_LIST_RECEIPTS).orElse(null));
            demoRole.getCapabilities().add(capabilityRepository.
                    getByName(CapabilityEnum.CAP_DELETE_ITEM).orElse(null));
*/
            roleRepository.save(demoRole);
            roleRepository.save(userRole);
            roleRepository.save(adminRole);

            admin.getRoles().add(adminRole);
            demoUser.getRoles().add(demoRole);
            userRepository.save(admin);
            userRepository.save(demoUser);

            LOGGER.info("Seeding initial data done");
        }
    }

    private void roleAddCapabilities(Role role, CapabilityEnum... capabilities) {

        for (CapabilityEnum capability : capabilities) {
            role.getCapabilities().add(capabilityRepository.
                    getByName(capability).orElse(null));
        }
        roleRepository.save(role);
    }
}
