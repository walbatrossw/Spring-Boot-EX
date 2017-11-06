package com.doubles.myboot07.controller;

import com.doubles.myboot07.domain.WebBoard;
import com.doubles.myboot07.domain.WebReply;
import com.doubles.myboot07.persistence.WebReplyRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/replies/*")
@Log
public class WebReplyController {

    @Autowired
    private WebReplyRepository replyRepo;

    // 특정 게시물의 댓글 등록처리, 목록처리
    @Transactional
    @PostMapping("/{bno}")
    public ResponseEntity<List<WebReply>> addReply(@PathVariable("bno")Long bno, @RequestBody WebReply reply) {
        log.info("Add Reply...");
        log.info("BNO : " + bno);
        log.info("REPLY : " + reply);
        WebBoard board = new WebBoard();
        board.setBno(bno);
        reply.setBoard(board);
        replyRepo.save(reply);
        return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED);
    }

    private List<WebReply> getListByBoard(WebBoard board) throws RuntimeException {
        log.info("getListByBoard..." + board);
        return replyRepo.getRepliesOfBoard(board);
    }
}