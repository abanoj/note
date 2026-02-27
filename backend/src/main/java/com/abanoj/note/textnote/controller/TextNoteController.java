package com.abanoj.note.textnote.controller;

import com.abanoj.note.textnote.dto.TextNoteCreateRequestDto;
import com.abanoj.note.textnote.dto.TextNoteResponseDto;
import com.abanoj.note.textnote.dto.TextNoteUpdateRequestDto;
import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.textnote.mapper.TextNoteMapper;
import com.abanoj.note.textnote.service.TextNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/text-notes")
@RequiredArgsConstructor
public class TextNoteController {

    private final TextNoteService textNoteService;
    private final TextNoteMapper textNoteMapper;

    @GetMapping
    @Operation(summary = "Get all text notes for the authenticated user")
    public ResponseEntity<Page<TextNoteResponseDto>> getAllTextNote(
            @PageableDefault(size = 10, sort = "updated", direction = Sort.Direction.DESC) Pageable pageable){
        Page<TextNoteResponseDto> pageOfTextNoteResponseDto = textNoteService
                .findAllTextNote(pageable)
                .map(textNoteMapper::toTextNoteResponseDto);
        return ResponseEntity.ok(pageOfTextNoteResponseDto);
    }

    @GetMapping("/{textNoteId}")
    @Operation(summary = "Get a text note by ID")
    public ResponseEntity<TextNoteResponseDto> getTextNote(@Parameter(description = "Text note ID") @PathVariable("textNoteId") Long id){
        TextNote textNote = textNoteService.findTextNoteById(id);
        return ResponseEntity.ok(textNoteMapper.toTextNoteResponseDto(textNote));
    }

    @PostMapping
    @Operation(summary = "Create a new text note")
    public ResponseEntity<TextNoteResponseDto> createTextNote(@Valid @RequestBody TextNoteCreateRequestDto textNoteCreateRequestDto){
        TextNote textNote = textNoteMapper.toTextNote(textNoteCreateRequestDto);
        TextNoteResponseDto textNoteResponseDto = textNoteMapper.toTextNoteResponseDto(textNoteService.createTextNote(textNote));
        return ResponseEntity.status(HttpStatus.CREATED).body(textNoteResponseDto);
    }

    @PutMapping("/{textNoteId}")
    @Operation(summary = "Update an existing text note")
    public ResponseEntity<TextNoteResponseDto> updateTextNote(
            @Parameter(description = "Text note ID") @PathVariable("textNoteId") Long id,
            @Valid @RequestBody TextNoteUpdateRequestDto textNoteUpdateRequestDto){
        TextNote textNote = textNoteMapper.toTextNote(textNoteUpdateRequestDto);
        TextNoteResponseDto textNoteResponseDto = textNoteMapper.toTextNoteResponseDto(textNoteService.updateTextNote(id, textNote));
        return ResponseEntity.ok(textNoteResponseDto);
    }


    @DeleteMapping("/{textNoteId}")
    @Operation(summary = "Delete a text note by ID")
    public ResponseEntity<Void> deleteTextNote(@Parameter(description = "Text note ID") @PathVariable("textNoteId") Long id){
        textNoteService.deleteTextNote(id);
        return ResponseEntity.noContent().build();
    }

}
