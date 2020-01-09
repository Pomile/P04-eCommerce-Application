package com.example.demo.controllers;

import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableSpringDataWebSupport
public class OrderControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private JacksonTester<CreateUserRequest> json;
    @Autowired private Filter securityFilterChain;
    @Autowired private WebApplicationContext context;
    @MockBean UserRepository userRepository;
    @MockBean private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(securityFilterChain)
                .build();

    }

    @DisplayName("It should submit an order for a user")
    @Test
    public void submit() throws Exception {
        given(userRepository.findByUsername(any())).willReturn(user());
        given(orderRepository.save(any())).willReturn(order());
        mvc.perform(post(new URI("/api/order/submit/adun"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3));
    }

    @DisplayName("It should not submit an order for a user that does not exist")
    @Test
    public void shouldNotSubmit() throws Exception {
        given(userRepository.findByUsername(any())).willReturn(null);
        mvc.perform(post(new URI("/api/order/submit/adun"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("It should get orders for a user")
    @Test
    public void getOrdersForUser() throws Exception {
        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(order());
        given(userRepository.findByUsername(any())).willReturn(user());
        given(orderRepository.findByUser(any())).willReturn(userOrders);
        mvc.perform(get(new URI("/api/order/history/adun"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("It should not get an order for a user that does not exist")
    @Test
    public void ShouldNotGetOrdersForUser() throws Exception {

        given(userRepository.findByUsername(any())).willReturn(null);
        mvc.perform(get(new URI("/api/order/history/adun"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private AppUser user(){
        Item item = new Item();
        Cart cart = new Cart();
        AppUser user = new AppUser();
        List<Item> items = new ArrayList<>();
        item.setName("Blue Band");
        item.setDescription("500mg");
        item.setPrice(BigDecimal.valueOf(3.00));
        item.setId(1L);
        items.add(item);
        cart.setItems(items);
        cart.setId(1L);
        cart.setTotal(BigDecimal.valueOf(3));
        user.setUsername("Adun");
        user.setId(1L);
        user.setCart(cart);
        cart.setUser(user);


        return user;
    }

    private UserOrder order(){
        UserOrder userOrder = new UserOrder();
        userOrder.setItems(user().getCart().getItems());
        userOrder.setTotal(BigDecimal.valueOf(3));
        userOrder.setUser(user());

        return userOrder;

    }
}