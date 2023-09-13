package com.teample.packages.board.domain.repository;

import com.teample.packages.board.domain.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
public interface BoardFilterlingRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findByField(String field, Pageable pageable);

    Page<BoardEntity> findByMember(String member, Pageable pageable);

    @Query("select r from BoardEntity r where r.field = :field and r.member = :member")
    Page<BoardEntity> findByFieldAndMember(@Param("field") String field, @Param("member") String member, Pageable pageable);

} */

public interface BoardFilterlingRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findByField(String field, Pageable pageable);
    Page<BoardEntity> findByMember(String member, Pageable pageable);
    Page<BoardEntity> findByFieldAndMember(String field, String member, Pageable pageable);
}
