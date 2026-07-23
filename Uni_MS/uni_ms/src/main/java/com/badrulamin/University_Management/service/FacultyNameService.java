package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FacultyName;

import java.util.List;
import java.util.Optional;

public interface FacultyNameService {

    FacultyName save(FacultyName faculty);

    FacultyName update(Long id, FacultyName faculty);

    List<FacultyName> findAll();

    Optional<FacultyName> findById(Long id);

    void delete(Long id);

    boolean existsById(Long id);
}
