package com.teample.packages.board.controller;

import com.teample.packages.board.domain.entity.BoardEntity;
import com.teample.packages.board.service.BoardFilteringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BoardFilteringController {
    private final BoardFilteringService boardFilteringService;

    @GetMapping("/filter")
    public ResponseEntity<Page<BoardEntity>> getFilteredList(
            @PageableDefault(sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String member) {

        return ResponseEntity.ok(boardFilteringService.addFilter(pageable, field, member));
    }
}
