package com.winter.spring.notice.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.winter.spring.notice.domain.Notice;
import com.winter.spring.notice.domain.PageInfo;
import com.winter.spring.notice.service.NoticeService;

@Controller
public class NoticeController {

	@Autowired
	private NoticeService service;
	
	@RequestMapping(value="/notice/insert.kh", method=RequestMethod.GET)
	public String showInsertForm() {
		return "notice/insert";
	}
	
	@RequestMapping(value="/notice/insert.kh", method=RequestMethod.POST)
	public String InsertNotice(@ModelAttribute Notice notice
							   , @RequestParam(value="uploadFile", required=false) MultipartFile uploadFile
							   , HttpServletRequest request
							   , Model model) {
		try {
			if(!uploadFile.getOriginalFilename().equals("")) { // 파일 이름을 체크해서 파일 유무 확인
				// ========== 파일 이름 ===========
				String fileName = uploadFile.getOriginalFilename();
				
				// (내가 저장한 후 그 경로를 DB에 저장하도록 준비하는 것)
				String root = request.getSession().getServletContext().getRealPath("resources"); // resource의 경로 가져옴
				// 내가 업로드한 파일을 저장할 폴더 (nuploadFiles)가 없을 경우 폴더 자동 생성
				// nuploadFiles 폴더는 고정된 이름이 아님! notice라서 임의로 준 n이다.
				String saveFolder = root + "\\nuploadFiles"; // 폴더
				File folder = new File(saveFolder); // java.io
				if(!folder.exists()) { // 폴더가 없을 경우
					folder.mkdir(); // 폴더 생성
				}
				
				// =========== 파일 경로 ===========
				String savePath = saveFolder + "\\" + fileName;
				File file = new File(savePath);
				// =========== 파일 저장 ===========
				uploadFile.transferTo(file); // transferTo : 진짜 파일을 저장하는 메소드
				
				// =========== 파일 크기 ===========
				long fileLength = uploadFile.getSize();
				
				// DB에 저장하기 위해 notice에 데이터를 set하는 부분
				notice.setNoticeFilename(fileName);
				notice.setNoticeFilepath(savePath);
				notice.setNoticeFilelength(fileLength);
			}
			int result = service.insertNotice(notice);
			if(result > 0) {
				return "redirect:/notice/list.kh";
			} else {
				model.addAttribute("msg", "공지사항 등록이 완료되지 않았습니다.");
				model.addAttribute("error", "공지사항 등록 실패");
				model.addAttribute("url", "/notice/insert.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/notice/insert.kh");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="/notice/list.kh", method=RequestMethod.GET)
	public String showNoticeList( // int 대신 Integer를 쓰면 null값을 체크할 수 있다.
								  @RequestParam(value="page", required=false, defaultValue="1") Integer currentPage
								  // ---> int currentPage = page != 0 ? page : 1;
								  , Model model) {
		try {
			// SELECT COUNT(*) FROM NOTICE_TBL
			int totalCount = service.getListCount();
			PageInfo pInfo = this.getPageInfo(currentPage, totalCount);
			List<Notice> nList = service.selectNoticeList(pInfo);
			// List 데이터의 유효성 체크 방법 2가지
			// 1. isEmpty()
			// 2. size()
			if(!nList.isEmpty()) {
				model.addAttribute("pInfo", pInfo);
				model.addAttribute("nList", nList);
				return "notice/list";
			} else {
				model.addAttribute("msg", "데이터 조회가 완료되지 않았습니다.");
				model.addAttribute("error", "공지사항 목록 조회 실패");
				model.addAttribute("url", "/notice/insert.kh");
				return "common/errorPage";
			}
			
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/notice/insert.kh");
			return "common/errorPage";
		}
	}
	
	// 이전에 StoreLogic에서 선언했던 페이징 변수 부분을 전부 controller로 가져옴
	public PageInfo getPageInfo(int currentPage, int totalCount) {
		PageInfo pi = null;
		int recordCountPerPage = 10;
		int naviCountPerPage = 10; // 한 페이지당 보여줄 네비게이터의 갯수
		int naviTotalCount;
		int startNavi;
		int endNavi;
		
		naviTotalCount = (int)((double)totalCount / recordCountPerPage + 0.9); // 형변환
		// + 0.9 -> 나머지를 버리는 처리를 하기 위해서...?
		// = Math.ceil((double)totalCount/recordCountPerPage)
		// currentPage값이 1~5 일때 startNavi가 1로 고정되도록 구해주는 식
		startNavi = (((int)((double)currentPage / naviCountPerPage + 0.9)) - 1) * naviCountPerPage + 1;
		endNavi = startNavi + naviCountPerPage - 1;
		// endNavi는 startNavi에 무조건 naviCountPerPage값을 더하므로 실제 최댓값보다 커질 수 있음
		if(endNavi > naviTotalCount) {
			endNavi = naviTotalCount;
		}
		
		pi = new PageInfo(currentPage, recordCountPerPage, naviCountPerPage, startNavi, endNavi, totalCount, naviTotalCount);
		return pi;
	}
	
	@RequestMapping(value="/notice/search.kh", method=RequestMethod.GET)
	public String searchNoticeList(@RequestParam(value="page", required=false, defaultValue="1") Integer currentPage
								 , @RequestParam("searchCondition") String searchCondition
								 , @RequestParam("searchKeyword") String searchKeyword
								 , Model model) {
		
		try {
			// ============ 동적쿼리로 바꿔주면서 switch case를 할 필요가 없어짐 =========
			// 대신 switch case가 mapper로 옮겨갔다. (choose when)
//			List<Notice> searchList = new ArrayList<Notice>();
//			switch(searchCondition) {
//				case "all" : 
//					// SELECT * FROM NOTICE_TBL WHERE NOTICE_SUBJECT = ? OR NOTICE_CONTENT = ? OR NOTICE_WRITER = ?
//					searchList = service.searchNoticesByAll(searchKeyword);
//					break;
//				case "writer" : 
//					// SELECT * FROM NOTICE_TBL WHERE NOTICE_WRITER = ?
//					searchList = service.searchNoticesByWriter(searchKeyword);
//					break;
//				case "title" : 
//					// SELECT * FROM NOTICE_TBL WHERE NOTICE_SUBJECT LIKE %?%
//					searchList = service.searchNoticesByTitle(searchKeyword);
//					break;
//				case "content" :
//					// SELECT * FROM NOTICE_TBL WHERE NOTICE_CONTENT = ?
//					searchList = service.searchNoticesByContent(searchKeyword);
//					break;
//			}
			
			// ====== searchCondition 값을 넘겨준다고 이런 식으로 쓰면 안됨!! =======
			/* 이유 : searchCondition, searchKeyword 두 개의 값을 넘겨줄 경우, StoreLogic에서 오류가 난다.
			 * 		  왜냐하면 StoreLogic의 selectList는 (String, Object, RowBounds)의 형식으로 적어줘야함.
			 * 
			 * 해결방법 : < 2개의 값을 하나의 변수로 다루는 방법 >
			 * 			  1. VO 클래스 만들기 
			 * 			  2. HashMap 사용
			 * StoreLogic에 가면 HashMap이 적용된 것 확인
			 * List<Notice> searchList = service.searchNoticesByKeyword(searchCondition, searchKeyword);
			 */
			
			// ====== HashMap을 애초에 Controller에서 만들어서 넘겨주는 방법 ======
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("searchCondition", searchCondition);
			paramMap.put("searchKeyword", searchKeyword);
			
			int totalCount = service.getListCount(paramMap);
			PageInfo pInfo = this.getPageInfo(currentPage, totalCount);
			
			List<Notice> searchList = service.searchNoticesByKeyword(pInfo, paramMap);
			
			if(!searchList.isEmpty()) {
				// 검색 결과 페이징 처리를 위해 searchCondition과 searchKeyword를 넘겨줌 
				model.addAttribute("searchCondition", searchCondition);
				model.addAttribute("searchKeyword", searchKeyword);
				model.addAttribute("pInfo", pInfo);
				model.addAttribute("sList", searchList);
				return "notice/search";
			} else {
				model.addAttribute("msg", "데이터 조회가 완료되지 않았습니다.");
				model.addAttribute("error", "공지사항 " + searchCondition + "으로 검색 조회 실패");
				model.addAttribute("url", "/notice/list.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/notice/list.kh");
			return "common/errorPage";
		}
	}
}
