package com.abanoj.note.textnote.controller;

import com.abanoj.note.config.JwtService;
import com.abanoj.note.exception.GlobalExceptionHandler;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.textnote.dto.TextNoteCreateRequestDto;
import com.abanoj.note.textnote.dto.TextNoteResponseDto;
import com.abanoj.note.textnote.dto.TextNoteUpdateRequestDto;
import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.textnote.mapper.TextNoteMapper;
import com.abanoj.note.textnote.service.TextNoteService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TextNoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TextNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TextNoteService textNoteService;

    @MockitoBean
    private TextNoteMapper textNoteMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LogoutHandler logoutHandler;

    private final LocalDateTime now = LocalDateTime.now();
    private final TextNoteResponseDto sampleResponse = new TextNoteResponseDto(1L, "My Note", "Content", now, now);

    @Test
    void getAllTextNotesShouldReturn200WithPage() throws Exception {
        TextNote textNote = TextNote.builder().id(1L).title("My Note").build();
        Page<TextNote> page = new PageImpl<>(List.of(textNote));

        when(textNoteService.findAllTextNote(any(Pageable.class))).thenReturn(page);
        when(textNoteMapper.toTextNoteResponseDto(any())).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/text-notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("My Note"));
    }

    @Test
    void getAllTextNotesShouldUseDefaultPagination() throws Exception {
        Page<TextNote> page = new PageImpl<>(List.of());

        when(textNoteService.findAllTextNote(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/text-notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(textNoteService).findAllTextNote(any(Pageable.class));
    }

    @Test
    void getTextNoteShouldReturn200() throws Exception {
        TextNote textNote = TextNote.builder().id(1L).title("My Note").content("Content").build();

        when(textNoteService.findTextNoteById(1L)).thenReturn(textNote);
        when(textNoteMapper.toTextNoteResponseDto(textNote)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/text-notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("My Note"));
    }

    @Test
    void getTextNoteShouldReturn404WhenNotFound() throws Exception {
        when(textNoteService.findTextNoteById(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/text-notes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createTextNoteShouldReturn201() throws Exception {
        TextNoteCreateRequestDto requestDto = new TextNoteCreateRequestDto("New Note", "Content");
        TextNote mapped = TextNote.builder().title("New Note").content("Content").build();
        TextNote saved = TextNote.builder().id(1L).title("New Note").content("Content").build();

        when(textNoteMapper.toTextNote(any(TextNoteCreateRequestDto.class))).thenReturn(mapped);
        when(textNoteService.createTextNote(mapped)).thenReturn(saved);
        when(textNoteMapper.toTextNoteResponseDto(saved)).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/text-notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTextNoteShouldReturn400WhenBlankTitle() throws Exception {
        TextNoteCreateRequestDto requestDto = new TextNoteCreateRequestDto("", "Content");

        mockMvc.perform(post("/api/v1/text-notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTextNoteShouldReturn200() throws Exception {
        TextNoteUpdateRequestDto requestDto = new TextNoteUpdateRequestDto(1L, "Updated", "New content");
        TextNote mapped = TextNote.builder().title("Updated").content("New content").build();
        TextNote updated = TextNote.builder().id(1L).title("Updated").content("New content").build();
        TextNoteResponseDto responseDto = new TextNoteResponseDto(1L, "Updated", "New content", now, now);

        when(textNoteMapper.toTextNote(any(TextNoteUpdateRequestDto.class))).thenReturn(mapped);
        when(textNoteService.updateTextNote(eq(1L), eq(mapped))).thenReturn(updated);
        when(textNoteMapper.toTextNoteResponseDto(updated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/text-notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateTextNoteShouldReturn400WhenInvalidData() throws Exception {
        TextNoteUpdateRequestDto requestDto = new TextNoteUpdateRequestDto(null, "", null);

        mockMvc.perform(put("/api/v1/text-notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTextNoteShouldReturn404WhenNotFound() throws Exception {
        TextNoteUpdateRequestDto requestDto = new TextNoteUpdateRequestDto(99L, "Not found", "Content");
        TextNote mapped = TextNote.builder().title("Not found").content("Content").build();

        when(textNoteMapper.toTextNote(any(TextNoteUpdateRequestDto.class))).thenReturn(mapped);
        when(textNoteService.updateTextNote(eq(99L), eq(mapped)))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/text-notes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTextNoteShouldReturn204() throws Exception {
        doNothing().when(textNoteService).deleteTextNote(1L);

        mockMvc.perform(delete("/api/v1/text-notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTextNoteShouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(textNoteService).deleteTextNote(99L);

        mockMvc.perform(delete("/api/v1/text-notes/99"))
                .andExpect(status().isNotFound());
    }
}
