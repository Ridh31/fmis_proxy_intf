package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

public interface AIService {

    /**
     * Chat with AI using optional database context.
     *
     * @param question The user question
     * @return AI-generated answer
     */
    String chat(String question);
}