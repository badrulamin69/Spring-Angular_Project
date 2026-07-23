package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Message;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.badrulamin.University_Management.payload.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Message>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Message> paged = messageService.findAll(pageable);
        PagedResponse<Message> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(messageService.findById(id)));
    }

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @PostMapping
    public ResponseEntity<ApiResponse<Message>> save(@Valid @RequestBody Message message) {
        return ResponseEntity.ok(ApiResponse.success(messageService.save(message)));
    }

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> update(@PathVariable Long id, @Valid @RequestBody Message message) {
        return ResponseEntity.ok(ApiResponse.success(messageService.update(id, message)));
    }

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
