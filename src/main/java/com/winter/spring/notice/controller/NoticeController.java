package com.winter.spring.notice.controller;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
			if(uploadFile != null && !uploadFile.getOriginalFilename().equals("")) { // 파일 이름을 체크해서 파일 유무 확인
				Map<String , Object> nMap = this.saveFile(uploadFile, request);
				
				String fileName = (String)nMap.get("fileName"); // .get(saveFile()에서 생성한 map의 key값)
				String fileRename = (String)nMap.get("fileRename");
				String savePath = (String)nMap.get("filePath");
				long fileLength = (long)nMap.get("fileLength");
				
				// DB에 저장하기 위해 notice에 데이터를 set하는 부분
				notice.setNoticeFilename(fileName);
				notice.setNoticeFileRename(fileRename);
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
	
	@RequestMapping(value="/notice/detail.kh", method=RequestMethod.GET)
	public String showNoticeDetail(Integer noticeNo, Model model) {
		Notice noticeOne = service.selectNoticeByNo(noticeNo);
		model.addAttribute("notice", noticeOne);
		return "notice/detail";
		
	}
	
	@RequestMapping(value="/notice/modify.kh", method=RequestMethod.GET)
	public String showModifyForm(Integer noticeNo, Model model) {
		Notice noticeOne = service.selectNoticeByNo(noticeNo);
		model.addAttribute("notice", noticeOne);
		return "notice/modify";
	}
	
	@RequestMapping(value="/notice/modify.kh", method=RequestMethod.POST)
	public String updateNotice(@ModelAttribute Notice notice // @ModelAttribute는 name값이 필드명과 같아야 쓸 수 있다.
							, @RequestParam(value="uploadFile", required = false) MultipartFile uploadFile
							, Model model // 값 jsp로 넘겨줄 때 사용
							, HttpServletRequest request) { // resources의 경로를 가져올 때 사용  
		try {
			if(uploadFile != null && !uploadFile.isEmpty()) {
				// 수정
				// 1. 대체 2. 삭제 후 등록
				// 기존 업로드 된 파일 존재 여부 체크 후 
				String fileName =  notice.getNoticeFileRename(); // Rename으로 바꿔줘야한다!
				if(fileName != null) {
					// 있으면 기존 파일 삭제
//					File file = new File("");
//					file.delete();
					this.deleteFile(request, fileName);
				}
				// 없으면 새로 업로드 하려는 파일 저장
				Map<String, Object> infoMap = this.saveFile(uploadFile, request);
				// 어떤 문장으로 써도 상관 X
				String noticeFilename = (String)infoMap.get("fileName");
				String noticeFileRename = (String)infoMap.get("fileRename");
				long noticeFileLength = (long)infoMap.get("fileLength");
				notice.setNoticeFilename(noticeFilename);
				notice.setNoticeFilepath((String)infoMap.get("filePath"));
				notice.setNoticeFilelength(noticeFileLength);
				notice.setNoticeFileRename(noticeFileRename);
			}
			int result = service.updateNotice(notice);
			if(result > 0) {
				return "redirect:/notice/detail.kh?noticeNo=" + notice.getNoticeNo();
			} else {
				model.addAttribute("msg", "공지사항 수정이 완료되지 않았습니다.");
				model.addAttribute("error", "공지사항 수정 실패");
				model.addAttribute("url", "/notice/list.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/notice/insert.kh");
			return "common/errorPage";
		}
	}
	
	public Map<String, Object> saveFile(MultipartFile uploadFile, HttpServletRequest request) throws Exception {
		// 넘겨야 하는 값이 여러개일 때 사용하는 방법
		// 1. VO 클래스를 만드는 방법
		// 2. HashMap을 사용하는 방법
		Map<String, Object> infoMap = new HashMap<String, Object>();
		
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
		// ====== 랜덤한 수로 rename하기 ======
//		Random rand = new Random();
//		String strResult = "N"; // notice니까 N으로 시작합시다~ (임의로 주는 규칙. 안줘도 됨)
//		for(int i = 0; i < 7; i++) {
//			int result = rand.nextInt(100)+1;
//			strResult += result + ""; // 랜덤한 수로 파일을 만들기 위해
//		}
		
		// ====== 시간으로 rename하기 ======
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 나중에 SS랑 ss 비교
		String strResult = sdf.format(new Date(System.currentTimeMillis()));
		
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1); // 확장자명 추출 (=png) // . 을 포함하지 않고 자르기 위해 +1
		String fileRename = "N" + strResult + "." + ext; // 랜덤한 수를 이용한 중복X 파일 생성
		String savePath = saveFolder + "\\" + fileRename;
		File file = new File(savePath);
		// =========== 파일 저장 ===========
		uploadFile.transferTo(file); // transferTo : 진짜 파일을 저장하는 메소드
		
		// =========== 파일 크기 ===========
		long fileLength = uploadFile.getSize();
		// 파일 이름, 경로, 크기를 넘겨주기 위해 Map에 정보를 저장한 후 return함
		// 왜 return하는가? DB에 저장하기 위해서 필요한 정보니까
		infoMap.put("fileName", fileName);
		infoMap.put("fileRename", fileRename);
		infoMap.put("filePath", savePath);
		infoMap.put("fileLength", fileLength);
		return infoMap;
	}

	private void deleteFile(HttpServletRequest request, String fileName) {
		// HttpServletRequest request 가 있으면
		// D:\\KH\\springworkspace\\WinterSpringMVC\\src\\main\\webapp\\resources 까지 가져올 수 있음
		// -> request.getSession().getServletContext().getRealPath("resources"); 
		
		String root = request.getSession().getServletContext().getRealPath("resources");
		String delFilepath = root + "\\nuploadFiles\\" + fileName;
		File file = new File(delFilepath);
		if(file.exists()) { // 파일이 존재하면
			// 삭제해라
			file.delete();
		}
	}
}
