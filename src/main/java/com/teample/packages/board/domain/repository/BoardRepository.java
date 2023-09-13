package com.teample.packages.board.domain.repository;

import com.teample.packages.board.domain.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByContentContaining(String keyword);
    List<BoardEntity> findByAuthorId(Long CurrentUser);
    //List<BoardEntity> findTop3ByViewDesc();
}