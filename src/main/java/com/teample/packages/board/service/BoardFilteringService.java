package com.teample.packages.board.service;

import com.teample.packages.board.domain.entity.BoardEntity;
import com.teample.packages.board.domain.repository.BoardFilterlingRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardFilteringService {
    private final BoardFilterlingRepository boardFilterlingRepository;

    /*
    @Transactional(readOnly = true)
    public Page<BoardEntity> addFilter(Pageable pageable, String field, String member){
        Page<BoardEntity>boardList=boardFilterlingRepository.findAll(pageable);

        if(field!="" && member==""){
            boardList=boardFilterlingRepository.findByField(field, pageable);
        }
        else if(field=="" && member!=""){
            boardList=boardFilterlingRepository.findByMember(member, pageable);
        }
        else if(field!="" && member!=""){
            boardList=boardFilterlingRepository.findByFieldAndMember(field, member, pageable);
        }
        return boardList;
    } */

    @Transactional(readOnly = true)
    public Page<BoardEntity> addFilter(Pageable pageable, String field, String member){
        if (!StringUtils.isEmpty(field) && StringUtils.isEmpty(member)) {
            return boardFilterlingRepository.findByField(field, pageable);
        } else if (StringUtils.isEmpty(field) && !StringUtils.isEmpty(member)) {
            return boardFilterlingRepository.findByMember(member, pageable);
        } else if (!StringUtils.isEmpty(field) && !StringUtils.isEmpty(member)) {
            return boardFilterlingRepository.findByFieldAndMember(field, member, pageable);
        }
        return boardFilterlingRepository.findAll(pageable);
    }

}
