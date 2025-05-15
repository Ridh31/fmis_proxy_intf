package com.fmis.fmis_proxy_intf.fmis_proxy_intf.seed;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds the Role table with predefined roles on application startup.
 */
@Configuration
@Order(2)
public class RoleDataSeeder {

    /**
     * Seeds predefined roles if they do not already exist.
     *
     * @param roleRepository the repository used to access and manipulate role data
     * @return a CommandLineRunner that executes role seeding logic at startup
     */
    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            // Predefined roles
            List<RoleSeedData> rolesToSeed = List.of(
                    new RoleSeedData(
                            "SUPER_ADMIN",
                            "The Super Admin has the highest level of access within the system, including full control over all administrative and operational functionalities.",
                            1
                    ),
                    new RoleSeedData(
                            "ADMIN",
                            "The Admin role is responsible for overseeing the day-to-day management of the application. Admins have broad access to administrative features, including user management, content moderation, and application settings.",
                            2
                    ),
                    new RoleSeedData(
                            "MODERATOR",
                            "The Moderator role is primarily focused on maintaining the quality and integrity of content within the application.",
                            3
                    ),
                    new RoleSeedData(
                            "USER",
                            "The User role represents a standard, authenticated member of the application. Users have access to the core features and functionalities intended for regular participants, such as viewing, creating, and interacting with content.",
                            4
                    ),
                    new RoleSeedData(
                            "GUEST",
                            "The Guest role is assigned to unauthenticated or temporary users who access the application without creating an account or logging in.",
                            5
                    )
            );

            // Insert each role if it doesn't exist
            for (RoleSeedData roleData : rolesToSeed) {
                roleRepository.findByName(roleData.name())
                        .ifPresentOrElse(
                                existing -> {
                                    // Role already exists
                                },
                                () -> {
                                    Role newRole = new Role();
                                    newRole.setName(roleData.name());
                                    newRole.setDescription(roleData.description());
                                    newRole.setLevel(roleData.level());
                                    newRole.setCreatedBy(1L);
                                    newRole.setCreatedDate(LocalDateTime.now());
                                    newRole.setUpdatedDate(LocalDateTime.now());
                                    newRole.setStatus(true);
                                    newRole.setIsDeleted(false);

                                    roleRepository.save(newRole);
                                }
                        );
            }
        };
    }

    /**
     * Helper record to represent seed data for a Role.
     *
     * @param name        name of the role
     * @param description description of the role
     * @param level       hierarchical level of the role
     */
    private record RoleSeedData(String name, String description, int level) {}
}