package com.winter.spring.board.store;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.winter.spring.board.domain.Board;
import com.winter.spring.board.domain.PageInfo;

public interface BoardStore {

	/**
	 * 게시글 등록 Store
	 * @param session
	 * @param board
	 * @return
	 */
	int insertBoard(SqlSession session, Board board);

	/**
	 * 게시글 전체 갯수 Store
	 * @param session
	 * @return
	 */
	int selectListCount(SqlSession session);

	/**
	 * 게시글 전체 조회 Store
	 * @param session
	 * @param pInfo
	 * @return
	 */
	List<Board> selectBoardList(SqlSession session, PageInfo pInfo);

	/**
	 * 번호로 게시글 조회
	 * @param session
	 * @param boardNo
	 * @return
	 */
	Board selectBoardByNo(SqlSession session, int boardNo);

	/**
	 * 게시글 삭제 Store
	 * @param session
	 * @param boardNo
	 * @return
	 */
	int deleteBoard(SqlSession session, int boardNo);

}
