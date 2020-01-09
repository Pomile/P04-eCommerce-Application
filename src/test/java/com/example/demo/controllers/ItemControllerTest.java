package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableSpringDataWebSupport
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired private JacksonTester<CreateUserRequest> json;
    @Autowired private Filter securityFilterChain;
    @Autowired private WebApplicationContext context;
    @MockBean private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(securityFilterChain)
                .build();
    }

    @DisplayName("It should get all items")
    @Test
    void getItems() throws Exception {

        given(itemRepository.findAll()).willReturn(items());
        mvc.perform(get(new URI("/api/item"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Blueband"));
    }

    @DisplayName("It should get an item by id")
    @Test
    void getItemById() throws Exception {

        given(itemRepository.findById(1L)).willReturn(item());
        mvc.perform(get(new URI("/api/item/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Blueband"));
    }

    @DisplayName("It should get items by name")
    @Test
    void getItemsByName() throws Exception {

        given(itemRepository.findByName(any())).willReturn(items());
        mvc.perform(get(new URI("/api/item/name/Blueband"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value( "Blueband"));
    }

    private Optional<Item> item(){
        Optional<Item> item = Optional.of(new Item());
        item.get().setDescription("500mg");
        item.get().setName("Blueband");
        item.get().setPrice(BigDecimal.valueOf(10));
        item.get().setId(1L);
        return item;
    }

    private List<Item> items(){
        List<Item> items = new ArrayList<>();
        items.add(item().get());
        return items;
    }
}