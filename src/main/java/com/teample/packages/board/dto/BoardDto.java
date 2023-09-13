package com.teample.packages.board.dto;

import com.teample.packages.board.domain.entity.BoardEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Long id; //게시글 id
    private Long authorId;
    private String title;
    private String content;
    private String member;
    private String field;
    private int view;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

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
    public BoardEntity toEntity(){
        BoardEntity boardEntity=BoardEntity.builder()
                .id(id)
                .authorId(authorId)
                .title(title)
                .content(content)
                .field(field)
                .member(member)
                .view(view)
                .build();
        return boardEntity;
    }



    @Builder
    public BoardDto(Long id,Long authorId, String title, String content, String member, String field, int view, LocalDateTime createdDate, LocalDateTime modifiedDate){
        this.id=id;
        this.authorId=authorId;
        this.title=title;
        this.content=content;
        this.field=field;
        this.member=member;
        this.view=view;
        this.createdDate=createdDate;
        this.modifiedDate=modifiedDate;
    }

}