package com.abanoj.note.checklist.service;

import com.abanoj.note.checklist.entity.Checklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChecklistService {
    Page<Checklist> findAllChecklist(Pageable pageable);
    Checklist findChecklist(Long checklistId);
    Checklist createChecklist(Checklist checklist);
    Checklist updateChecklist(Long id, Checklist checklist);
    void deleteChecklist(Long id);
}
