package com.abanoj.note.checklist.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.checklist.repository.ChecklistRepository;
import com.abanoj.note.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Checklist findChecklist(Long checklistId) {
        User user = securityUtils.getCurrentUser();
        return checklistRepository
                .findByIdAndUser(checklistId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist with id: " + checklistId + " not found!"));
    }

    @Override
    public Page<Checklist> findAllChecklist(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        return checklistRepository.findAllByUser(user, pageable);
    }

    @Override
    @Transactional
    public Checklist createChecklist(Checklist checklist) {
        User user = securityUtils.getCurrentUser();
        if(checklist.getId() != null) throw new IllegalArgumentException("Checklist already has and ID!");
        LocalDateTime now = LocalDateTime.now();
        checklist.setCreated(now);
        checklist.setUpdated(now);
        checklist.setUser(user);
        Checklist savedChecklist = checklistRepository.save(checklist);
        log.debug("Checklist created with id {}", savedChecklist.getId());
        return savedChecklist;
    }

    @Override
    @Transactional
    public Checklist updateChecklist(Long id, Checklist checklist) {
        User user = securityUtils.getCurrentUser();
        if(checklist.getId() == null) throw new IllegalArgumentException("Checklist must have an ID");
        if(!Objects.equals(checklist.getId(), id)) throw new IllegalArgumentException("Id and Checklist id do not match");

        Checklist checklistToUpdate = checklistRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist with id " + id + " not found!"));

        checklistToUpdate.setTitle(checklist.getTitle());
        checklistToUpdate.setUpdated(LocalDateTime.now());

        log.debug("Checklist {} updated", id);
        return checklistRepository.save(checklistToUpdate);
    }

    @Override
    @Transactional
    public void deleteChecklist(Long id) {
        User user = securityUtils.getCurrentUser();
        Checklist checklist = checklistRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist with id " + id + " not found!"));
        checklistRepository.delete(checklist);
        log.debug("Checklist {} deleted", id);
    }

}
