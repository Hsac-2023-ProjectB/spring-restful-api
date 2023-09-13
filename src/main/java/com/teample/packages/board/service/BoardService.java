package com.teample.packages.board.service;

import com.teample.packages.board.domain.entity.BoardEntity;
import com.teample.packages.board.domain.repository.BoardRepository;
import com.teample.packages.board.dto.BoardDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;
    private static final int BLOCK_PAGE_NUM_COUNT = 5; // 블럭에 존재하는 페이지 번호 수
    private static final int PAGE_POST_COUNT = 4; // 한 페이지에 존재하는 게시글 수
    @Transactional
    public List<BoardDto> getBoardlist(Integer pageNum) {
        Page<BoardEntity> page = boardRepository.findAll(PageRequest.of(pageNum - 1, PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC, "createdDate")));

        List<BoardEntity> boardEntities = page.getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity boardEntity : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }

        return boardDtoList;
    }

    @Transactional
    public BoardDto getPost(Long id) throws Exception {
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(id);
        if(boardEntityWrapper.isPresent()){
            BoardEntity boardEntity = boardEntityWrapper.get();

            // 조회수 증가 로직 추가
            boardEntity.increaseViewCnt();
            boardRepository.save(boardEntity); // 조회수 업데이트

            BoardDto boardDTO = BoardDto.builder()
                    .id(boardEntity.getId())
                    .authorId(boardEntity.getAuthorId())
                    .title(boardEntity.getTitle())
                    .content(boardEntity.getContent())
                    .field(boardEntity.getField())
                    .member(boardEntity.getMember())
                    .createdDate(boardEntity.getCreatedDate())
                    .view(boardEntity.getView()) // 업데이트된 조회수를 가져옴
                    .build();

            return boardDTO;
        }
        else{
            throw new Exception("해당 게시물을 찾지 못했습니다.");
        }
    }

    @Transactional
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public List<BoardDto> searchPosts(String keyword) {
        List<BoardEntity> boardEntities = boardRepository.findByContentContaining(keyword);
        List<BoardDto> boardDtoList = new ArrayList<>();

        if (boardEntities.isEmpty()) return boardDtoList;

        for (BoardEntity boardEntity : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }

        return boardDtoList;
    }

    /*
    @Transactional
    public List<BoardDto> getTop3Posts(){
        List<BoardEntity> boardEntities=boardRepository.findTop3ByViewDesc();
        List<BoardDto> boardDtoList= new ArrayList<>();

        if(boardEntities.isEmpty()) return boardDtoList;

        for(BoardEntity boardEntity : boardEntities){
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }

        return boardDtoList;
    } */

    @Transactional
    public List<BoardDto> myPosts(Long currentuser){
        List<BoardEntity> boardEntities=boardRepository.findByAuthorId(currentuser);
        List<BoardDto>boardDtoList=new ArrayList<>();

        if(boardEntities.isEmpty()) return boardDtoList;

        for(BoardEntity boardEntity : boardEntities){
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }

        return boardDtoList;
    }

    private BoardDto convertEntityToDto(BoardEntity boardEntity) {
        return BoardDto.builder()
                .id(boardEntity.getId())
                .authorId(boardEntity.getAuthorId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .field(boardEntity.getField())
                .member(boardEntity.getMember())
                .view(boardEntity.getView())
                .createdDate(boardEntity.getCreatedDate())
                .build();
    }
    @Transactional
    public Long getBoardCount() {
        return boardRepository.count();
    }

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        // 총 게시글 갯수
        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        // 총 게시글 기준으로 계산한 마지막 페이지 번호 계산 (올림으로 계산)
        Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount/PAGE_POST_COUNT)));

        // 현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        // 페이지 시작 번호 조정
        curPageNum = (curPageNum <= 3) ? 1 : curPageNum - 2;

        // 페이지 번호 할당
        for (int val = curPageNum, idx = 0; val <= blockLastPageNum; val++, idx++) {
            pageList[idx] = val;
        }

        return pageList;
    }
}