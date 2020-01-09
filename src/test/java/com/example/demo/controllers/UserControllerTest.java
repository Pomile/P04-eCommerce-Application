package com.example.demo.controllers;

import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import com.example.demo.security.JWTAuthenticationFilter;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.servlet.Filter;

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
public class UserControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private JacksonTester<CreateUserRequest> json;
    @Autowired private JacksonTester<AppUser> json2;
    @Autowired private Filter securityFilterChain;
    @Autowired private WebApplicationContext context;
    @MockBean private CartRepository cartRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean private AuthenticationManager authenticatonManager;
    private CreateUserRequest userRequest;
    private AppUser appUser;
    @BeforeEach
    public void Setup(){
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(securityFilterChain)
                .build();

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Adun");
        createUserRequest.setPassword("pomile44");
        createUserRequest.setConfirmPassword("pomile44");

        this.userRequest = createUserRequest;
        AppUser appUser = new AppUser();

        appUser.setUsername(createUserRequest.getUsername());
        appUser.setPassword(createUserRequest.getPassword());
        this.appUser = appUser;
        appUser.setId(1);

        given(bCryptPasswordEncoder.encode(any())).willReturn("thisishashpassword");
        given(cartRepository.save(any())).willReturn(new Cart());
        given(userRepository.save(any())).willReturn(appUser);
        given(userRepository.findByUsername(any())).willReturn(appUser);
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(appUser));

    }


    @Test
    public void findById() throws Exception {
        mvc.perform(get(new URI("/api/user/id/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Adun"));
    }

    @Test
    public void findByUserName() throws Exception {

        mvc.perform(get(new URI("/api/user/adun"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Adun"));
    }

    @Test
    public void createUser() throws Exception {

        mvc.perform(post(new URI("/api/user/create"))
                .content(json.write(userRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Adun"));
    }

    @Test
    public void shouldNotCreateAuserWithInvalidPasswordLength() throws Exception {
        userRequest.setPassword("me");
        userRequest.setConfirmPassword("me");
        mvc.perform(post(new URI("/api/user/create"))
                .content(json.write(userRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}