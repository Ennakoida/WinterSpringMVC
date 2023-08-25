package com.winter.spring.notice.store.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.winter.spring.notice.domain.Notice;
import com.winter.spring.notice.domain.PageInfo;
import com.winter.spring.notice.store.NoticeStore;

@Repository
public class NoticeStoreLogic implements NoticeStore {

	@Override
	public int insertNotice(SqlSession session, Notice notice) {
		int result = session.insert("NoticeMapper.insertNotice", notice);
		return result;
	}

	@Override
	public int updateNotice(SqlSession session, Notice notice) {
		int result = session.update("NoticeMapper.updateNotice", notice);
		return result;
	}

	@Override
	public List<Notice> selectNoticeList(SqlSession session, PageInfo pInfo) {
		int limit = pInfo.getRecordCountPerPage(); // 가져오는 행의 갯수
		int offset = (pInfo.getCurrentPage() - 1) * limit; // 변하는 default값...
		RowBounds rowBounds = new RowBounds(offset, limit);
		// rowBounds는 무조건 세번째 변수로 들어간다. (고정)
		List<Notice> nList = session.selectList("NoticeMapper.selectNoticeList", null, rowBounds);
		return nList;
	}
	
//	public void generatePageNavi() {
//	}

	@Override
	public int selectListCount(SqlSession session) {
		int result = session.selectOne("NoticeMapper.selectListCount");
		return result;
	}

//	@Override
//	public List<Notice> selectNoticesByAll(SqlSession session, String searchKeyword) {
//		List<Notice> searchList = session.selectList("NoticeMapper.selectNoticesByAll", searchKeyword);
//		return searchList;
//	}
//
//	@Override
//	public List<Notice> selectNoticesByWriter(SqlSession session, String searchKeyword) {
//		List<Notice> searchList = session.selectList("NoticeMapper.selectNoticesByWriter", searchKeyword);
//		return searchList;
//	}
//
//	@Override
//	public List<Notice> selectNoticesByTitle(SqlSession session, String searchKeyword) {
//		List<Notice> searchList = session.selectList("NoticeMapper.selectNoticesByTitle", searchKeyword);
//		return searchList;
//	}
//
//	@Override
//	public List<Notice> selectNoticesByContent(SqlSession session, String searchKeyword) {
//		List<Notice> searchList = session.selectList("NoticeMapper.selectNoticesByContent", searchKeyword);
//		return searchList;
//	}

	@Override
	public List<Notice> selectNoticesByKeyword(SqlSession session, PageInfo pInfo, Map<String, String> paramMap) {
//		public List<Notice> selectNoticesByKeyword(SqlSession session, String searchCondition, String searchKeyword) {
//		// 2개의 값을 하나의 변수로 다루는 방법
//		// 1. VO 클래스 만들기 
//		// 2. HashMap 사용
//		Map<String, String> paramMap = new HashMap<String, String>();
//		// put() 메소드를 사용해서 key-value 설정을 하는데 
//		// key값 (파란색)이 mapper.xml에서 사용됨
//		paramMap.put("searchCondition", searchCondition);
//		paramMap.put("searchKeyword", searchKeyword);
		
		int limit = pInfo.getRecordCountPerPage();
		int offset = (pInfo.getCurrentPage() - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		List<Notice> searchList = session.selectList("NoticeMapper.selectNoticesByKeyword", paramMap, rowBounds);
		return searchList;
	}

	@Override
	public int selectListCount(SqlSession session, Map<String, String> paramMap) {
		int result = session.selectOne("NoticeMapper.selectListByKeywordCount", paramMap);
		return result;
	}

	@Override
	public Notice selectNoticeByNo(SqlSession session, Integer noticeNo) {
		Notice noticeOne = session.selectOne("NoticeMapper.selectNoticeByNo", noticeNo);
		return noticeOne;
	}

}
