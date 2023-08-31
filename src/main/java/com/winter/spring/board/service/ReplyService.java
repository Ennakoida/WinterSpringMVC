package com.winter.spring.board.service;

import java.util.List;

import com.winter.spring.board.domain.Reply;

public interface ReplyService {

	/**
	 * 게시글 댓글 등록 Service
	 * @param reply
	 * @return
	 */
	int insertReply(Reply reply);

	/**
	 * 게시글 댓글 수정 Service
	 * @param reply
	 * @return
	 */
	int updateReply(Reply reply);

	/**
	 * 게시글 댓글 삭제 Service
	 * @param reply
	 * @return
	 */
	int deleteReply(Reply reply);

	/** 
	 * 게시판 전체 조회
	 * @return
	 */
	List<Reply> selectReplyList(int refBoardNo);

}
