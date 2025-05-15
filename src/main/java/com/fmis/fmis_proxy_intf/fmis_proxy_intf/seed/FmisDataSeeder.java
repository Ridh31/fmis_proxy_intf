package com.fmis.fmis_proxy_intf.fmis_proxy_intf.seed;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.FmisRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;

/**
 * Seeds the FMIS configuration table if not already present.
 * This ensures there is exactly one FMIS config row in the database.
 */
@Configuration
@Order(4)
public class FmisDataSeeder {

    @Bean
    CommandLineRunner seedFmisConfig(FmisRepository fmisRepository) {
        return args -> {
            if (fmisRepository.count() == 0) {
                FMIS fmis = new FMIS();
                fmis.setBaseURL("https://dev.fmis.gov.kh/PSIGW/RESTListeningConnector/PSFT_EP");
                fmis.setUsername("HROUNGRIDH");
                fmis.setPassword("Fmis#2025");
                fmis.setContentType("text/xml");
                fmis.setDescription(
                        "This URL serves as the main entry point for the FMIS " +
                        "(Financial Management Information System) integration, " +
                        "specifically designed for the PSIGW (PeopleSoft Integration Gateway) RESTful service."
                );
                fmis.setCreatedBy(1);
                fmis.setCreatedDate(LocalDateTime.now());
                fmis.setStatus(true);
                fmis.setIsDeleted(false);

                fmisRepository.save(fmis);
            }
        };
    }
}