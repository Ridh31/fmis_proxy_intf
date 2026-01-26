package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.BankStatementRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.SarmisInterfaceRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.UserRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.AIService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

/**
 * Implementation of the AIService interface.
 *
 * This service provides AI-powered responses based on FMIS system data,
 * including users, partners, bank statements, and SARMIS interface records.
 */
@Service
public class AIServiceImpl implements AIService {

    private final ChatClient chatClient;
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final BankStatementRepository bankStatementRepository;
    private final SarmisInterfaceRepository sarmisInterfaceRepository;
    private final RestTemplate proxyRestTemplate;

    /**
     * Constructor for AIServiceImpl.
     *
     * @param builder                   Builder for creating the ChatClient
     * @param bankStatementRepository   Repository for accessing bank statements
     * @param sarmisInterfaceRepository Repository for accessing SARMIS interface records
     * @param partnerRepository         Repository for accessing partner data
     * @param userRepository            Repository for accessing user data
     */
    public AIServiceImpl(ChatClient.Builder builder,
                         BankStatementRepository bankStatementRepository,
                         SarmisInterfaceRepository sarmisInterfaceRepository,
                         PartnerRepository partnerRepository,
                         UserRepository userRepository,
                         @Qualifier("proxyRestTemplate") RestTemplate proxyRestTemplate) {

        this.chatClient = builder.build();
        this.partnerRepository = partnerRepository;
        this.userRepository = userRepository;
        this.bankStatementRepository = bankStatementRepository;
        this.sarmisInterfaceRepository = sarmisInterfaceRepository;
        this.proxyRestTemplate = proxyRestTemplate;
    }

    /**
     * Generates an AI response to the provided user question using FMIS data context.
     *
     * @param question The user-provided question to ask the AI
     * @return AI-generated answer as a String
     */
    @Override
    public String chat(String question) {

        String systemPrompt = """
                You are an FMIS system analyst.
                Use ONLY the provided data.
                Never guess or fabricate missing information.
                If data is unavailable, say so clearly.
                """;

        String finalPrompt = """
                USERS
                %s

                PARTNERS
                %s

                BANK STATEMENT
                %s

                SARMIS INTERFACE
                %s

                USER QUESTION
                %s
                """.formatted(
                buildUserContext(),
                buildPartnerContext(),
                buildBankStatementContext(),
                buildSarmisContext(),
                question
        );

        return chatClient.prompt()
                .system(systemPrompt)
                .user(finalPrompt)
                .call()
                .content();
    }

    /**
     * Builds context information for partners to include in AI prompts.
     *
     * @return Formatted partner context or message if no records found
     */
    private String buildPartnerContext() {
        var page = partnerRepository.getAllPartners(PageRequest.of(0, 5));

        if (page.isEmpty()) {
            return "No partner records found.";
        }

        return page.getContent().stream()
                .map(p -> """
                        Identifier=%s
                        Name=%s
                        SystemCode=%s
                        IsBank=%s
                        Status=%s
                        """.formatted(
                        safe(p.getIdentifier()),
                        safe(p.getName()),
                        safe(p.getSystemCode()),
                        safe(p.getIsBank()),
                        safe(p.getStatus())
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Builds context information for users to include in AI prompts.
     *
     * @return Formatted user context or message if no records found
     */
    private String buildUserContext() {

        var page = userRepository.getAllUsers(null, PageRequest.of(0, 5));

        if (page.isEmpty()) {
            return "No active users found.";
        }

        return page.getContent().stream()
                .map(u -> """
                        Username=%s
                        Email=%s
                        Enabled=%s
                        Partner=%s
                        Role=%s
                        """.formatted(
                        safe(u.getUsername()),
                        safe(u.getEmail()),
                        safe(u.isEnabled()),
                        safe(u.getPartner() != null ? u.getPartner().getIdentifier() : "N/A"),
                        safe(u.getRole() != null ? u.getRole().getName() : "N/A")
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Builds context information for bank statements to include in AI prompts.
     *
     * @return Formatted bank statement context or message if no records found
     */
    private String buildBankStatementContext() {
        var page = bankStatementRepository.getAllBankStatements(PageRequest.of(0, 5));

        if (page.isEmpty()) {
            return "No bank statement records found.";
        }

        return page.getContent().stream()
                .map(bs -> """
                        Statement ID=%s
                        Account Number=%s
                        Statement Date=%s
                        Status=%s
                        """.formatted(
                        safe(bs.getStatementId()),
                        safe(bs.getBankAccountNumber()),
                        safe(bs.getStatementDate()),
                        safe(bs.getStatus())
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Builds context information for SARMIS interfaces to include in AI prompts.
     *
     * @return Formatted SARMIS interface context or message if no records found
     */
    private String buildSarmisContext() {
        var page = sarmisInterfaceRepository.findFilteredSarmisInterface(
                null, null, null, null, null,
                PageRequest.of(0, 5)
        );

        if (page.isEmpty()) {
            return "No SARMIS interface records found.";
        }

        return page.getContent().stream()
                .map(si -> """
                        InterfaceCode=%s
                        Endpoint=%s
                        CreatedDate=%s
                        Status=%s
                        """.formatted(
                        safe(si.getInterfaceCode()),
                        safe(si.getEndpoint()),
                        safe(si.getCreatedDate()),
                        safe(si.getStatus())
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Safely converts an object to String, returning "N/A" for null values.
     *
     * @param value The object to convert
     * @return String representation or "N/A" if null
     */
    private String safe(Object value) {
        return value == null ? "N/A" : value.toString();
    }
}