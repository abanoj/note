package com.abanoj.note.checklist.controller;

import com.abanoj.note.checklist.dto.ChecklistUpdateRequestDto;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.checklist.dto.ChecklistResponseDto;
import com.abanoj.note.checklist.dto.ChecklistCreateRequestDto;
import com.abanoj.note.checklist.mapper.ChecklistMapper;
import com.abanoj.note.checklist.service.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checklists")
@RequiredArgsConstructor
@Tag(name = "Checklist", description = "Checklist management")
public class ChecklistController {

    private final ChecklistService checklistService;
    private final ChecklistMapper checklistMapper;

    @GetMapping
    @Operation(summary = "Get all checklist for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checklist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<ChecklistResponseDto>> getAll(){
        List<ChecklistResponseDto> checklistResponseDtoList = checklistService.findAllChecklist()
                .stream()
                .map(checklistMapper::toChecklistResponseDto)
                .toList();
        return ResponseEntity.ok(checklistResponseDtoList);
    }

    @GetMapping("/{checklistId}")
    @Operation(summary = "Get a checklist by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checklist retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Checklist not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ChecklistResponseDto> getChecklist(
            @Parameter(description = "Checklist ID") @PathVariable("checklistId") Long id){
        Checklist checklist = checklistService.findChecklist(id);
        ChecklistResponseDto checklistResponseDto = checklistMapper.toChecklistResponseDto(checklist);
        return ResponseEntity.ok(checklistResponseDto);
    }

    @PostMapping
    @Operation(summary = "Create a new checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Checklist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ChecklistResponseDto> createChecklist(@Valid @RequestBody ChecklistCreateRequestDto checklistRequestDto){
        Checklist checklist = checklistMapper.toChecklist(checklistRequestDto);
        ChecklistResponseDto checklistResponseDto = checklistMapper.toChecklistResponseDto(checklistService.createChecklist(checklist));
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistResponseDto);
    }

    @PutMapping("/{checklistId}")
    @Operation(summary = "Update an existing checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checklist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Checklist not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ChecklistResponseDto> updateChecklist(
            @Parameter(description = "Checklist ID") @PathVariable("checklistId") Long id,
            @Valid @RequestBody ChecklistUpdateRequestDto checklistRequestDto){
        Checklist checklist = checklistMapper.toChecklist(checklistRequestDto);
        ChecklistResponseDto checklistUpdated = checklistMapper.toChecklistResponseDto(checklistService.updateChecklist(id, checklist));
        return ResponseEntity.ok(checklistUpdated);
    }

    @DeleteMapping("/{checklistId}")
    @Operation(summary = "Delete a checklist and all its tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Checklist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Checklist not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteChecklist(
            @Parameter(description = "Checklist ID") @PathVariable("checklistId") Long id){
        checklistService.deleteChecklist(id);
        return ResponseEntity.noContent().build();
    }
}
