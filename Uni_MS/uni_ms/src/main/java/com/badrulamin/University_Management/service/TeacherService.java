package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Teacher;
import com.badrulamin.University_Management.repository.TeacherRepository;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public Page<Teacher> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public Page<Teacher> searchTeachers(String search, Long departmentId, Long facultyId, String designation, String status, Pageable pageable) {
        return teacherRepository.searchTeachers(search, departmentId, facultyId, designation, status, pageable);
    }

    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, Teacher teacher) {
        Teacher existing = findById(id);
        if (teacher.getFirstName() != null) existing.setFirstName(teacher.getFirstName());
        if (teacher.getLastName() != null) existing.setLastName(teacher.getLastName());
        if (teacher.getEmail() != null) existing.setEmail(teacher.getEmail());
        if (teacher.getPhone() != null) existing.setPhone(teacher.getPhone());
        if (teacher.getGender() != null) existing.setGender(teacher.getGender());
        if (teacher.getDateOfBirth() != null) existing.setDateOfBirth(teacher.getDateOfBirth());
        if (teacher.getBloodGroup() != null) existing.setBloodGroup(teacher.getBloodGroup());
        if (teacher.getNationality() != null) existing.setNationality(teacher.getNationality());
        if (teacher.getReligion() != null) existing.setReligion(teacher.getReligion());
        if (teacher.getMaritalStatus() != null) existing.setMaritalStatus(teacher.getMaritalStatus());
        if (teacher.getPhoto() != null) existing.setPhoto(teacher.getPhoto());
        if (teacher.getNationalId() != null) existing.setNationalId(teacher.getNationalId());
        if (teacher.getPassport() != null) existing.setPassport(teacher.getPassport());
        if (teacher.getEmergencyContact() != null) existing.setEmergencyContact(teacher.getEmergencyContact());
        if (teacher.getPresentAddress() != null) existing.setPresentAddress(teacher.getPresentAddress());
        if (teacher.getPermanentAddress() != null) existing.setPermanentAddress(teacher.getPermanentAddress());
        if (teacher.getTeacherCode() != null) existing.setTeacherCode(teacher.getTeacherCode());
        if (teacher.getUniqueCode() != null) existing.setUniqueCode(teacher.getUniqueCode());
        if (teacher.getJoiningDate() != null) existing.setJoiningDate(teacher.getJoiningDate());
        if (teacher.getEmploymentStatus() != null) existing.setEmploymentStatus(teacher.getEmploymentStatus());
        if (teacher.getEmploymentType() != null) existing.setEmploymentType(teacher.getEmploymentType());
        if (teacher.getDesignation() != null) existing.setDesignation(teacher.getDesignation());
        if (teacher.getDepartment() != null) existing.setDepartment(teacher.getDepartment());
        if (teacher.getFaculty() != null) existing.setFaculty(teacher.getFaculty());
        if (teacher.getOfficeRoom() != null) existing.setOfficeRoom(teacher.getOfficeRoom());
        if (teacher.getCampus() != null) existing.setCampus(teacher.getCampus());
        if (teacher.getHighestDegree() != null) existing.setHighestDegree(teacher.getHighestDegree());
        if (teacher.getUniversity() != null) existing.setUniversity(teacher.getUniversity());
        if (teacher.getSpecialization() != null) existing.setSpecialization(teacher.getSpecialization());
        if (teacher.getExperience() != null) existing.setExperience(teacher.getExperience());
        if (teacher.getCertifications() != null) existing.setCertifications(teacher.getCertifications());
        if (teacher.getAssignedCourses() != null) existing.setAssignedCourses(teacher.getAssignedCourses());
        if (teacher.getSections() != null) existing.setSections(teacher.getSections());
        if (teacher.getSemester() != null) existing.setSemester(teacher.getSemester());
        if (teacher.getCreditLoad() != null) existing.setCreditLoad(teacher.getCreditLoad());
        if (teacher.getGoogleScholar() != null) existing.setGoogleScholar(teacher.getGoogleScholar());
        if (teacher.getOrcid() != null) existing.setOrcid(teacher.getOrcid());
        if (teacher.getSalaryGrade() != null) existing.setSalaryGrade(teacher.getSalaryGrade());
        if (teacher.getBasicSalary() != null) existing.setBasicSalary(teacher.getBasicSalary());
        if (teacher.getBankInformation() != null) existing.setBankInformation(teacher.getBankInformation());
        if (teacher.getTaxId() != null) existing.setTaxId(teacher.getTaxId());
        if (teacher.getStatus() != null) existing.setStatus(teacher.getStatus());
        if (teacher.getUser() != null) existing.setUser(teacher.getUser());
        return teacherRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        teacherRepository.deleteById(id);
    }
}
