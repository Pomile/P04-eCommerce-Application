package com.example.demo.controllers;

import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.Filter;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableSpringDataWebSupport
public class CartControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired private JacksonTester<ModifyCartRequest> json;
    @Autowired private Filter securityFilterChain;
    @Autowired private WebApplicationContext context;
    @MockBean private CartRepository cartRepository;
    @MockBean private ItemRepository itemRepository;
    @MockBean private UserRepository userRepository;
    private ModifyCartRequest modifyCartRequest;
    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(securityFilterChain)
                .build();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername("Aduns");
        this.modifyCartRequest = modifyCartRequest;
        Cart cart = new Cart();
        cart.setId(2L);
        cart.setItems(items());


        AppUser appUser = new AppUser();
        appUser.setUsername("Aduns");
        appUser.setPassword("password");
        appUser.setId(1L);

        cart.setUser(appUser);
        appUser.setCart(cart);

        given(itemRepository.findById(1L)).willReturn(item());
        given(userRepository.findByUsername(any())).willReturn(appUser);
        given(cartRepository.save(any())).willReturn(cart);
    }

    @DisplayName("It should add an item to the cart")
    @Test
    public void addTocart() throws Exception {

        mvc.perform(post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));

    }
    @DisplayName("It should not add an item to the cart if user does not exist")
    @Test
    public void ShouldNotAddTocartWithUserThatDoesNotexist() throws Exception {
        given(userRepository.findByUsername(any())).willReturn(null);
        mvc.perform(post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }
    @DisplayName("It should not add an item to the cart if item does not exist")
    @Test
    public void ShouldNotAddTocartWithItemThatDoesNotExist() throws Exception {
        Optional<Item> item = Optional.ofNullable(null);
        given(itemRepository.findById(1L)).willReturn(item);
        mvc.perform(post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }
    @DisplayName("It should remove items from the cart")
    @Test
    void removeFromcart() throws Exception {

     mvc.perform(post(new URI("/api/cart/removeFromCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @DisplayName("It should remove items from the cart that does not exist")
    @Test
    void removeFromCartWithUserThatDoesNotExist() throws Exception {
        given(userRepository.findByUsername(any())).willReturn(null);
        mvc.perform(post(new URI("/api/cart/removeFromCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("It should not remove items from a cart if item does not exist")
    @Test
    void removeFromCartWithItemThatDoesNotExist() throws Exception {
        Optional<Item> item = Optional.ofNullable(null);
        given(itemRepository.findById(any())).willReturn(item);
        mvc.perform(post(new URI("/api/cart/removeFromCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
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