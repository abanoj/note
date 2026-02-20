package com.abanoj.note.checklist.service;

import com.abanoj.note.checklist.entity.Checklist;

import java.util.List;

public interface ChecklistService {
    List<Checklist> findAllChecklist();
    Checklist findChecklist(Long checklistId);
    Checklist createChecklist(Checklist checklist);
    Checklist updateChecklist(Long id, Checklist checklist);
    void deleteChecklist(Long id);
}
