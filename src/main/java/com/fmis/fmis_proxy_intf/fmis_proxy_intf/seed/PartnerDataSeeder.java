package com.fmis.fmis_proxy_intf.fmis_proxy_intf.seed;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Seeder class for populating the partner_intf table with initial data.
 * This class ensures that if the table is empty, a default partner is inserted.
 */
@Configuration
@Order(1)
public class PartnerDataSeeder {

    /**
     * Seeds the partner_intf table with a default partner if it doesn't exist already.
     * The partner is created with the name "FMIS", description "Financial Management Information System", etc.
     *
     * @param partnerRepository the repository to access the partner data
     * @return CommandLineRunner that runs at startup to check and seed the partner
     */
    @Bean
    CommandLineRunner seedPartner(PartnerRepository partnerRepository) {
        return args -> {
            // Check if a partner with code "FMIS" already exists
            partnerRepository.findByCode("FMIS").ifPresentOrElse(
                    existingPartner -> {
                        // If the partner exists, do nothing
                    },
                    () -> {
                        // If the partner does not exist, create a new one
                        Partner fmis = new Partner(
                                "FMIS",
                                "Financial Management Information System",
                                "000000",
                                "FMIS",
                                "FMIS",
                                "UNYFV+t5QH+VeF+JtxSCvmflQfRi0mi85hgFj3xKiq29S3YkLWcgHehy9LkvLGEvBxySkazPQIgSHs+TPVPLb4U43ZHRYyXQoj5LHGM58VSXYaEzaHfS5PrfnSQ2F50uUShNO7RyfajwVf7s7BEuAjEsa9wUCTHVXa9pcL9wvYrYgdl5E/YR7RVFRoKuHDldBgetNmDsfl+m+Ay0DDaifzFla0YJjk/mJVDGCHZOQtCSIxbfrdrL9qRfNWpaotsBZZTeWxLPfz59Spu6tmW1BHmcWPahPD+wdteUDjpDxbTcrPbIBYkO/bPT0lbY7OQPXVft0hH6oNfnP/VFrBNBVg==",
                                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1DSYWpgFnj3B5IJCrmtPPswvumxnXfw8sP7SD82AZ3OUbK83BHIATjKIvJRGZmDJgJhvP+RfB7ca3re1eRtxbYaXGc5w8xobn5IKZhQYxpkamKcaDp2bZdxZKyykEVeYBFfhpMrQWdQxDrdNqNEoV9txlXgjcy/YEJiG839P4mCJyZlYGv8PUx5eOUI3HIBFLe9FYqiQgj2IdcsgPnGGHe/vp4WEoCrp74VOPVH4sKKn2oAzwgckRNPvRByEZ/KgxvFaA7Cp/e481B/opy2GsKBIKB8W+RIG0CPDaYtcr9l0oaF7z0FuE1ng+Vs3cpgzUIWYDbWdcV2TkTmy4efqZAgMBAAECggEAGT8d5FMeR4VkQvPEULWsmx4lyA5HRnpQkJRKlQrTSA/6P7V2Qds6Td+Svx29bExhm/A8eR4MTs3cI8FkY/zgHfsKHZgB/aSWa9A82PEIidRcri+Nn86wZBs7186oJ9NdmSGp6+t22tnUDbz3yevEN/deSOFZvYZQhkgV9MtGePfrdidmfay8rsZnnje6jnkiQgZsy9gBAK3IaphaKDHfhO0wMhkIz1c1SyKBFvLtJzHxHnkh5M+8D8Z+AaKWajXQd2Y5zjAqtM53sXPI76jlG3SsvD0kuOa2LbncosBl++/dun+3Lt1nYoyZIAEO0P7xY94xgKNU5DZO1gEZvW4tPQKBgQC+IvL8gFuYPOb5MCf292+/ve6vj2ivBhW0sX/4n1pLAod9prHbdhHxkXAL1IOBqEWN1e4hpcCrwbmrp8AlZbmpb6dA9Qoft+hm9yXC66ezt4IACw9aWlKkYJ7TxwIzT/GEx9oeLSWWbVA3mqVH1bpusiS6bY3SakxxEPs5gEOkTQKBgQDzxIpAwLM5zWScIysYdj0+T5NJJnrsLDv6H6CvMdhj9zvLuvCi9wBWZAef29CSAgg7VECKuinvI3HEVwFUGoBPS5rRSDu9yan1nkTA0GehbJkC1iqctI9SMGeW3T4CiiKlXrq7XvDoCfW1rh2y/FhqLoUxdlmP0WVBtKrA6dJFfQKBgQCfO5utVT3gKBjzVZ4SmTRwKvtK8dokwf7JneFjcOjoaszXsRPAjIgIe9mPPsGHg0oEt8W/ThYmgY4iMNGdQC0VthknME/zMkyidpacQEjpHGT17k1ESOJvIS/CgibYnDnTDqAfp4WUDiYg+xUyLM7+R5i0vG0Ka9Vq12LdzApZ/QKBgBYASCYd/k8aPZlmBy63BUIJdpaklXWZbHRfxGXrjsc++jOiFLPwrSQe9R01frae3lVUflrHAJ/nyA3beHA50BjYDwoShenUOLXFahko9iF4prq9z6zDEyhs2/yezUj3ZLcePKZKpF2dAIM8SsvrwKMcvcLAFsdHM+dhQgQVVngVAoGABgy7J2tGJ23dHLrPpr6PRXEh/ZbE3OeF6+WI0Sn5A3noRmlUrChDz98Koy3snY6LZj0LFI2GGGbdqt4gbj5rdiZxOT+uvtvpVDe7TTRxr7CE2OW2OXYZqwmz9H2k9NQcWdQMZGQP7sAkYk2ACzI8YtxZ4c6JmSlGDYC+L6R85CY=",
                                true,
                                false,
                                1L
                        );

                        // Save the fmis to the database
                        partnerRepository.save(fmis);
                    }
            );
        };
    }
}