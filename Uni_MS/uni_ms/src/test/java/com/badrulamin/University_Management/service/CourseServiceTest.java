package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EntityUpdateUtil entityUpdateUtil;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findById_existingCourse_returnsCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        course.setCode("TC001");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.findById(1L);

        assertEquals("Test Course", result.getName());
        assertEquals("TC001", result.getCode());
        verify(courseRepository).findById(1L);
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> courseService.findById(999L));

        assertTrue(ex.getMessage().contains("Course"));
        verify(courseRepository).findById(999L);
    }

    @Test
    void save_course_returnsSavedCourse() {
        Course course = new Course();
        course.setName("New Course");
        course.setCode("NC001");
        Course saved = new Course();
        saved.setId(1L);
        saved.setName("New Course");
        saved.setCode("NC001");
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        Course result = courseService.save(course);

        assertEquals("New Course", result.getName());
        assertEquals("NC001", result.getCode());
        verify(courseRepository).save(course);
    }

    @Test
    void findAll_returnsPageOfCourses() {
        Course c1 = new Course();
        c1.setId(1L);
        c1.setName("Course A");
        Course c2 = new Course();
        c2.setId(2L);
        c2.setName("Course B");
        Page<Course> page = new PageImpl<>(List.of(c1, c2));
        when(courseRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Course> result = courseService.findAll(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(courseRepository).findAll(any(Pageable.class));
    }

    @Test
    void delete_existingCourse_deletesSuccessfully() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).deleteById(1L);

        courseService.delete(1L);

        verify(courseRepository).findById(1L);
        verify(courseRepository).deleteById(1L);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.delete(999L));
        verify(courseRepository, never()).deleteById(anyLong());
    }
}
