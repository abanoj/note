package com.abanoj.note.item.controller;

import com.abanoj.note.config.JwtService;
import com.abanoj.note.exception.GlobalExceptionHandler;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.item.dto.ItemDto;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.item.mapper.ItemMapper;
import com.abanoj.note.item.service.ItemService;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private ItemMapper itemMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LogoutHandler logoutHandler;

    private final ItemDto sampleDto = new ItemDto(1L, "Buy milk", ItemStatus.PENDING, ItemPriority.HIGH);

    @Test
    void getAllItemsShouldReturn200() throws Exception {
        Item item = new Item(1L, "Buy milk", ItemStatus.PENDING, ItemPriority.HIGH, null, LocalDateTime.now(), LocalDateTime.now());

        when(itemService.findAllItems(1L)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/checklists/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Buy milk"));
    }

    @Test
    void getItemShouldReturn200() throws Exception {
        Item item = new Item(1L, "Buy milk", ItemStatus.PENDING, ItemPriority.HIGH, null, LocalDateTime.now(), LocalDateTime.now());

        when(itemService.findItem(1L, 1L)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/checklists/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy milk"));
    }

    @Test
    void getItemShouldReturn404WhenNotFound() throws Exception {
        when(itemService.findItem(1L, 99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/checklists/1/items/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItemShouldReturn201() throws Exception {
        ItemDto requestDto = new ItemDto(null, "New Item", ItemStatus.PENDING, ItemPriority.HIGH);
        Item mapped = new Item(null, "New Item", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);
        Item saved = new Item(1L, "New Item", ItemStatus.PENDING, ItemPriority.HIGH, null, LocalDateTime.now(), LocalDateTime.now());

        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(mapped);
        when(itemService.createItem(eq(1L), eq(mapped))).thenReturn(saved);
        when(itemMapper.toItemDto(saved)).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/checklists/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createItemShouldReturn400WhenBlankTitle() throws Exception {
        ItemDto requestDto = new ItemDto(null, "", ItemStatus.PENDING, ItemPriority.HIGH);

        mockMvc.perform(post("/api/v1/checklists/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemShouldReturn200() throws Exception {
        ItemDto requestDto = new ItemDto(1L, "Updated", ItemStatus.DONE, ItemPriority.LOW);
        Item mapped = new Item(1L, "Updated", ItemStatus.DONE, ItemPriority.LOW, null, null, null);
        Item updated = new Item(1L, "Updated", ItemStatus.DONE, ItemPriority.LOW, null, LocalDateTime.now(), LocalDateTime.now());
        ItemDto responseDto = new ItemDto(1L, "Updated", ItemStatus.DONE, ItemPriority.LOW);

        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(mapped);
        when(itemService.updateItem(eq(1L), eq(1L), eq(mapped))).thenReturn(updated);
        when(itemMapper.toItemDto(updated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/checklists/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateItemShouldReturn400WhenInvalidData() throws Exception {
        ItemDto requestDto = new ItemDto(1L, "", ItemStatus.PENDING, ItemPriority.HIGH);

        mockMvc.perform(put("/api/v1/checklists/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemShouldReturn404WhenNotFound() throws Exception {
        ItemDto requestDto = new ItemDto(99L, "Not found", ItemStatus.PENDING, ItemPriority.HIGH);
        Item mapped = new Item(99L, "Not found", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(mapped);
        when(itemService.updateItem(eq(1L), eq(99L), eq(mapped)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/checklists/1/items/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItemShouldReturn204() throws Exception {
        doNothing().when(itemService).deleteItem(1L, 1L);

        mockMvc.perform(delete("/api/v1/checklists/1/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItemShouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(itemService).deleteItem(1L, 99L);

        mockMvc.perform(delete("/api/v1/checklists/1/items/99"))
                .andExpect(status().isNotFound());
    }
}
