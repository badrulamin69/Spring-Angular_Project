package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/librarian")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
public class LibraryDashboardController {

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final BookReturnRepository bookReturnRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("issuedBooks", bookIssueRepository.count());
        stats.put("overdueBooks", 0);
        stats.put("recentIssues", bookIssueRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
