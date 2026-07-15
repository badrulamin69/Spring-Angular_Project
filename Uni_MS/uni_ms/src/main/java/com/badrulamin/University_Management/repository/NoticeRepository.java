package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CONCAT(n.postedBy.firstName, ' ', n.postedBy.lastName)) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:noticeType IS NULL OR :noticeType = '' OR n.noticeType = :noticeType) AND " +
           "(:status IS NULL OR :status = '' OR n.status = :status) AND " +
           "(:priority IS NULL OR :priority = '' OR n.priority = :priority)")
    Page<Notice> searchNotices(
            @Param("search") String search,
            @Param("noticeType") String noticeType,
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable);
}
