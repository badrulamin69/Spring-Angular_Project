package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.FacultyName;
import com.badrulamin.University_Management.service.FacultyNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facultiesname")
@RequiredArgsConstructor
public class FacultyNameController {

    private final FacultyNameService facultyNameService;

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public FacultyName save(@RequestBody FacultyName faculty) {
        return facultyNameService.save(faculty);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public List<FacultyName> findAll() {
        return facultyNameService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public FacultyName findById(@PathVariable Long id) {
        return facultyNameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public FacultyName update(@PathVariable Long id, @RequestBody FacultyName faculty) {
        return facultyNameService.update(id, faculty);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public void delete(@PathVariable Long id) {
        facultyNameService.delete(id);
    }
}
