package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.config.SecurityConfig;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.request.ShopExchangeRequest;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.ShopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShopController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ShopControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShopService shopService;

    @Test
    void items_shouldReturnArray() throws Exception {
        when(shopService.listItems()).thenReturn(List.of());

        mockMvc.perform(get("/api/shop/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void exchange_shouldReturnOrderId() throws Exception {
        ShopExchangeRequest req=new ShopExchangeRequest();
        req.setItemId(1L);
        when(shopService.exchange(1L)).thenReturn(99L);

        mockMvc.perform(post("/api/shop/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(99));
    }

    @Test
    void addItem_shouldReturnNewItemId() throws Exception {
        when(shopService.addItem(any(NewShopItemRequest.class))).thenReturn(7L);

        NewShopItemRequest req = new NewShopItemRequest();
        req.setName("n");
        req.setPrice(1);
        req.setStock(2);
        req.setDescription("d");

        mockMvc.perform(post("/api/shop/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(7));
    }
}

