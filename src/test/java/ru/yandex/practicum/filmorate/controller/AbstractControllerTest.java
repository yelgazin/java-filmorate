package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@JsonTest
public abstract class AbstractControllerTest {

    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    @Autowired
    private ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    protected RequestBuilder getGetRequestBuilder(String path) {
        return MockMvcRequestBuilders
                .get(path)
                .accept(MediaType.APPLICATION_JSON);
    }

    protected <T> RequestBuilder getPostRequestBuilder(String path, T body) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON);
    }

    protected <T> RequestBuilder getPutRequestBuilder(String path, T body) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON);
    }

    protected RequestBuilder getDeleteRequestBuilder(String path) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    protected <T> T fromResult(MvcResult result, Class<T> classType) throws Exception {
        String json = result.getResponse().getContentAsString(DEFAULT_ENCODING);
        return objectMapper.readValue(json, classType);
    }

    protected <T> T fromResult(MvcResult result, TypeReference<T> typeReference) throws Exception {
        String json = result.getResponse().getContentAsString(DEFAULT_ENCODING);
        return objectMapper.readValue(json, typeReference);
    }
}
