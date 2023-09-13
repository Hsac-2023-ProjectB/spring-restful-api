package com.teample.packages.board.controller;

import com.teample.packages.board.dto.BoardDto;
import com.teample.packages.board.service.BoardFilteringService;
import com.teample.packages.board.service.BoardService;
import com.teample.packages.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Controller
@AllArgsConstructor
@Slf4j
public class BoardController {
    private BoardService boardService;
    private BoardFilteringService boardFilteringService;

    /* 게시글 목록 */
    @GetMapping("/board")
    public String list( Model model, @RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
        List<BoardDto> boardList = boardService.getBoardlist(pageNum);
        Integer[] pageList = boardService.getPageList(pageNum);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pageList", pageList);

        return "board/project.html";
    }

    /* 게시글 필터링 목적
    @GetMapping("/board")
    public String listOrFilter(
            Model model,
            @PageableDefault(sort="createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String member) {

        Page<BoardEntity> boardPage;

        if (field == null && member == null) {
            List<BoardDto> boardList = boardService.getBoardlist(pageNum);
            Integer[] pageList = boardService.getPageList(pageNum);

            model.addAttribute("boardList", boardList);
            model.addAttribute("pageList", pageList);
        } else {
            boardPage = boardFilteringService.addFilter(pageable, field, member);
            model.addAttribute("boardList", boardPage.getContent());
        }

        return "board/project.html";
    }*/


    /* 게시글 작성 페이지 */
    @GetMapping("/post")
    public String list() {
        return "board/CreateBoard.html";
    }

    /* 게시글 작성 처리 */
    @PostMapping("post")
    public String write(@Validated @ModelAttribute("BoardDto") BoardDto boardDto, BindingResult bindingResult, @SessionAttribute (name="loginMember", required=true) Member loginMember) throws IOException {
        log.info("BoardDto = {} ", boardDto);
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "board/project.html";
        }

        boardDto.setAuthorId(loginMember.getId());

        log.info("boardDto = {}", boardDto);

        // 게시글 저장 로직
        boardService.savePost(boardDto);
        return "redirect:/board";
    }


    /* 게시글 상세 페이지 */
    @GetMapping("/post/{no}")
    public String detail(@PathVariable("no") Long no, Model model) throws Exception {
        BoardDto boardDTO = boardService.getPost(no);

        model.addAttribute("boardDto", boardDTO);
        return "board/BoardDetail.html";
    }

    /* 게시글 수정 페이지 */
    @GetMapping("/post/edit/{no}")
    public String edit(@PathVariable("no") Long no, Model model,@SessionAttribute (name="loginMember", required=true) Member loginMember) throws Exception {
        BoardDto boardDTO = boardService.getPost(no);
        if(!boardDTO.getAuthorId().equals(loginMember.getId())){
            return "board/project.html";
        }
        model.addAttribute("boardDto", boardDTO);

        return "board/BoardEdit.html";
    }

    /* 게시글 수정 처리*/
    @PutMapping("/post/edit/{no}")
    public String update(BoardDto boardDTO, @SessionAttribute (name="loginMember", required=true) Member loginMember, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "board/project.html";
        }
        boardDTO.setAuthorId(loginMember.getId());
        boardService.savePost(boardDTO);
        return "redirect:/board";
    }

    /* 게시글 삭제 처리 */
    @DeleteMapping("/board/post/{no}")
    public String delete(BoardDto boardDto, @PathVariable("no") Long no, @SessionAttribute (name="loginMember", required=true) Member loginMember) {
        boardDto.setAuthorId(loginMember.getId());
        if(!boardDto.getAuthorId().equals(loginMember.getId())){
            return "redirect: board/BoardDetail.html";
        }
        boardService.deletePost(no);
        return "redirect:/board";
    }

    /* 게시글 검색 */
    @GetMapping("/board/search")
    public String search(@RequestParam(value = "keyword") String keyword, Model model) {
        List<BoardDto> boardDtoList = boardService.searchPosts(keyword);

        model.addAttribute("boardList", boardDtoList);

        return "board/project.html";
    }

    /* 내가 작성한 모집글 */
    @GetMapping("board/myPosts")
    public String getMyPosts(Model model, @SessionAttribute (name="loginMember", required=true) Member loginMember){
        List<BoardDto>myBoards= boardService.myPosts(loginMember.getId());

        model.addAttribute("myBoards",myBoards);
        return "board/postView";
    }

    /* 조회수 Top3 게시글 표시
    @GetMapping("/board") */
}