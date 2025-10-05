package com.fmis.fmis_proxy_intf.fmis_proxy_intf.seed;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.RoleRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * Seeds the User table with a default SUPER_ADMIN user
 * after roles and partners have been initialized.
 */
@Configuration
@Order(3)
public class UserDataSeeder {

    /**
     * Seeds the default SUPER_ADMIN user if not already present.
     *
     * @param userRepository    repository for user operations
     * @param roleRepository    repository to fetch role data
     * @param partnerRepository repository to fetch partner data
     * @return a CommandLineRunner that inserts the default user on startup
     */
    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository,
                                RoleRepository roleRepository,
                                PartnerRepository partnerRepository) {

        return args -> {
            if (!userRepository.existsByUsername("FMIS")) {

                // Fetch the SUPER_ADMIN role
                Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                        .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

                // Fetch the FMIS partner
                Partner fmisPartner = partnerRepository.findByCode("FMIS")
                        .orElseThrow(() -> new RuntimeException("FMIS partner not found"));

                // Create and populate the new user
                User newUser = new User();
                newUser.setUsername("FMIS");
                newUser.setPassword(new BCryptPasswordEncoder().encode("Fmis#Proxy$"));
                newUser.setRole(superAdminRole);
                newUser.setPartner(fmisPartner);
                newUser.setEnabled(true);
                newUser.setAccountNonExpired(true);
                newUser.setCredentialsNonExpired(true);
                newUser.setAccountNonLocked(true);
                newUser.setCreatedDate(LocalDateTime.now());
                newUser.setLastModifiedDate(LocalDateTime.now());

                // Save the user to the database
                userRepository.save(newUser);
            }
        };
    }
}