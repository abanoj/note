package com.abanoj.note.item.controller;

import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.dto.ItemDto;
import com.abanoj.note.item.mapper.ItemMapper;
import com.abanoj.note.item.service.ItemService;
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
@RequestMapping("/api/v1/checklists/{checklistId}/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Item management within a checklist")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    @Operation(summary = "Get all items from a checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<ItemDto>> getAllItems(
            @Parameter(description = "Item list ID") @PathVariable("checklistId") Long checklistId){
        List<ItemDto> itemDtoList = itemService.findAllItems(checklistId).stream().map(itemMapper::toItemDto).toList();
        return ResponseEntity.ok(itemDtoList);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Get a item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item or item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ItemDto> getItem(
            @Parameter(description = "Item list ID") @PathVariable("checklistId") Long checklistId,
            @Parameter(description = "Item ID") @PathVariable("itemId") Long itemId){
        Item item = itemService.findItem(checklistId, itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);
        return ResponseEntity.ok(itemDto);
    }

    @PostMapping
    @Operation(summary = "Create a new item")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Checklist not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ItemDto> createItem(
            @Parameter(description = "Item list ID") @PathVariable("checklistId") Long checklistId,
            @Valid @RequestBody ItemDto newItemDto){
        Item newItem = itemService.createItem(checklistId, itemMapper.toItem(newItemDto));
        ItemDto itemDto = itemMapper.toItemDto(newItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update an existing item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Item or item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ItemDto> updateItem(
            @Parameter(description = "Item list ID") @PathVariable("checklistId") Long checklistId,
            @Parameter(description = "Item ID") @PathVariable("itemId") Long itemId,
            @Valid @RequestBody ItemDto itemDtoToUpdate){
        Item itemUpdated = itemService.updateItem(checklistId, itemId, itemMapper.toItem(itemDtoToUpdate));
        ItemDto itemDtoUpdated = itemMapper.toItemDto(itemUpdated);
        return ResponseEntity.ok(itemDtoUpdated);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Delete a item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Item or checklist not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "Item list ID") @PathVariable("checklistId") Long checklistId,
            @Parameter(description = "Item ID") @PathVariable("itemId") Long itemId){
        itemService.deleteItem(checklistId, itemId);
        return ResponseEntity.noContent().build();
    }
}
