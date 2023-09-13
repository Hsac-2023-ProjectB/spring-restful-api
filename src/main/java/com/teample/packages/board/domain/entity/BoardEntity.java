package com.teample.packages.board.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="board")
public class BoardEntity extends TimeEntity{
    @Id
    @Column(name="id", length = 20, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="authorId", length = 20, nullable = false)
    private Long authorId;

    @Column(name="title", length = 100, nullable = false)
    private String title;

    @Column(name="content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name="member", length = 100, nullable = false)
    private String member;

    @Column(name="field", length = 100, nullable = false)
    private String field;

    @Column(name="view",length = 10, nullable = true)
    private int view;


    @Builder public BoardEntity(Long id,Long authorId,String title,String content, String member, String field, int view){
        this.id=id;
        this.authorId=authorId;
        this.title=title;
        this.content=content;
        this.member=member;
        this.field=field;
        this.view = view;
    }
    public void increaseViewCnt(){
        this.view++;
    }
}