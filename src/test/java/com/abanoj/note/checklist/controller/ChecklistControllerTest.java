package com.abanoj.note.checklist.controller;

import com.abanoj.note.checklist.dto.ChecklistCreateRequestDto;
import com.abanoj.note.checklist.dto.ChecklistResponseDto;
import com.abanoj.note.checklist.dto.ChecklistUpdateRequestDto;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.checklist.mapper.ChecklistMapper;
import com.abanoj.note.checklist.service.ChecklistService;
import com.abanoj.note.config.JwtService;
import com.abanoj.note.exception.GlobalExceptionHandler;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.token.TokenRepository;
import com.abanoj.note.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChecklistController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChecklistService checklistService;

    @MockitoBean
    private ChecklistMapper checklistMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LogoutHandler logoutHandler;

    private final ChecklistResponseDto sampleResponse = new ChecklistResponseDto(1L, "Shopping", 2, 0.5, List.of());

    @Test
    void getAllShouldReturn200WithPage() throws Exception {
        Checklist checklist = Checklist.builder().id(1L).title("Shopping").build();
        Page<Checklist> page = new PageImpl<>(List.of(checklist));

        when(checklistService.findAllChecklist(any(Pageable.class))).thenReturn(page);
        when(checklistMapper.toChecklistResponseDto(any())).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/checklists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Shopping"));
    }

    @Test
    void getAllShouldUseDefaultPagination() throws Exception {
        Page<Checklist> page = new PageImpl<>(List.of());

        when(checklistService.findAllChecklist(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/checklists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(checklistService).findAllChecklist(any(Pageable.class));
    }

    @Test
    void getChecklistShouldReturn200() throws Exception {
        Checklist checklist = Checklist.builder().id(1L).title("Shopping").build();

        when(checklistService.findChecklist(1L)).thenReturn(checklist);
        when(checklistMapper.toChecklistResponseDto(checklist)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/checklists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Shopping"));
    }

    @Test
    void getChecklistShouldReturn404WhenNotFound() throws Exception {
        when(checklistService.findChecklist(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/checklists/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createChecklistShouldReturn201() throws Exception {
        ChecklistCreateRequestDto requestDto = new ChecklistCreateRequestDto("New List");
        Checklist mapped = Checklist.builder().title("New List").build();
        Checklist saved = Checklist.builder().id(1L).title("New List").build();

        when(checklistMapper.toChecklist(any(ChecklistCreateRequestDto.class))).thenReturn(mapped);
        when(checklistService.createChecklist(mapped)).thenReturn(saved);
        when(checklistMapper.toChecklistResponseDto(saved)).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createChecklistShouldReturn400WhenBlankTitle() throws Exception {
        ChecklistCreateRequestDto requestDto = new ChecklistCreateRequestDto("");

        mockMvc.perform(post("/api/v1/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateChecklistShouldReturn200() throws Exception {
        ChecklistUpdateRequestDto requestDto = new ChecklistUpdateRequestDto(1L, "Updated");
        Checklist mapped = Checklist.builder().id(1L).title("Updated").build();
        Checklist updated = Checklist.builder().id(1L).title("Updated").build();

        when(checklistMapper.toChecklist(any(ChecklistUpdateRequestDto.class))).thenReturn(mapped);
        when(checklistService.updateChecklist(eq(1L), eq(mapped))).thenReturn(updated);
        when(checklistMapper.toChecklistResponseDto(updated)).thenReturn(
                new ChecklistResponseDto(1L, "Updated", 0, null, List.of()));

        mockMvc.perform(put("/api/v1/checklists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateChecklistShouldReturn400WhenInvalidData() throws Exception {
        ChecklistUpdateRequestDto requestDto = new ChecklistUpdateRequestDto(null, "");

        mockMvc.perform(put("/api/v1/checklists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateChecklistShouldReturn404WhenNotFound() throws Exception {
        ChecklistUpdateRequestDto requestDto = new ChecklistUpdateRequestDto(99L, "Not found");
        Checklist mapped = Checklist.builder().id(99L).title("Not found").build();

        when(checklistMapper.toChecklist(any(ChecklistUpdateRequestDto.class))).thenReturn(mapped);
        when(checklistService.updateChecklist(eq(99L), eq(mapped)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/checklists/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteChecklistShouldReturn204() throws Exception {
        doNothing().when(checklistService).deleteChecklist(1L);

        mockMvc.perform(delete("/api/v1/checklists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteChecklistShouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(checklistService).deleteChecklist(99L);

        mockMvc.perform(delete("/api/v1/checklists/99"))
                .andExpect(status().isNotFound());
    }
}
