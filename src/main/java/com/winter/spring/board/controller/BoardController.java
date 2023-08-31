package com.winter.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.winter.spring.board.domain.Board;
import com.winter.spring.board.domain.PageInfo;
import com.winter.spring.board.domain.Reply;
import com.winter.spring.board.service.BoardService;
import com.winter.spring.board.service.ReplyService;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;
	@Autowired
	private ReplyService rService;
	
	@RequestMapping(value="/board/list.kh", method=RequestMethod.GET)
	public ModelAndView showBoardList(@RequestParam(value="page", required=false, defaultValue = "1") Integer currentPage
									 , ModelAndView mv) {
		int totalCount = bService.getListCount();
		PageInfo pInfo = this.getPageInfo(currentPage, totalCount);
		List<Board> bList = bService.selectBoardList(pInfo);	
		//mv.addObject("pInfo", pInfo);
		//mv.addObject("bList", bList);
		//mv.setViewName("board/list");
		
		mv.addObject("pInfo", pInfo).addObject("bList", bList).setViewName("board/list");
		return mv;
	}
	
	private PageInfo getPageInfo(Integer currentPage, int totalCount) {
		
		int recordCountPerPage = 10;
		int naviCountPerPage = 10;
		int naviTotalCount;
		
		naviTotalCount = (int)Math.ceil((double)totalCount/recordCountPerPage);
		int startNavi = ((int)((double)currentPage/naviCountPerPage + 0.9) - 1) * naviCountPerPage + 1;
		int endNavi = startNavi + naviCountPerPage - 1;
		
		if(endNavi > naviTotalCount) {
			endNavi = naviTotalCount;
		}
		
		PageInfo pInfo = new PageInfo(currentPage, totalCount, naviTotalCount, recordCountPerPage, naviCountPerPage, startNavi, endNavi);
		return pInfo;
	}

	@RequestMapping(value="/board/write.kh", method=RequestMethod.GET)
	public ModelAndView showWriteForm(ModelAndView mv) {
		
		mv.setViewName("board/write"); // ModelAndView를 이용한 페이지 이동
		// return "board/writer"
		return mv;		
	}
	
	@RequestMapping(value="/board/write.kh", method=RequestMethod.POST)
	public ModelAndView boardRegister(
			ModelAndView mv // ModelAndView를 쓰려면 ModelAndView 변수가 있어야함
			, @ModelAttribute Board board // name값이 Board와 같아야함
			, @RequestParam(value="uploadFile", required = false) MultipartFile uploadFile
			, HttpServletRequest request) {
		
		try {
			if(uploadFile != null && !uploadFile.getOriginalFilename().equals("")) {
				// 파일 정보 (이름, 리네임, 경로, 크기) 및 파일 저장
				Map<String, Object> tMap = this.saveFile(request, uploadFile);
				board.setBoardFileName((String)tMap.get("fileName"));
				board.setBoardFileRename((String)tMap.get("fileRename"));
				board.setBoardFilePath((String)tMap.get("filePath"));
				board.setBoardFileLength((long)tMap.get("fileLength"));
			}
			int result = bService.insertBoard(board);
			mv.setViewName("redirect:/board/list.kh");
		} catch (Exception e) {
			// model.addAttribute("msg", "등록이 완료되지 않았습니다.");
			mv.addObject("msg", "게시글이 등록되지 않았습니다.");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", "/board/write.kh");
			mv.setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	@RequestMapping(value="/board/detail.kh", method=RequestMethod.GET)
	public ModelAndView showDetail(int boardNo, ModelAndView mv) {
		try {
			Board board = bService.selectBoardByNo(boardNo);
			if(board != null) {
				List<Reply> replyList = rService.selectReplyList(boardNo);
				if(replyList.size() > 0) {
					mv.addObject("rList", replyList);
				}
				mv.addObject("board", board);
				mv.setViewName("board/detail");
			} else {
				mv.addObject("msg", "게시글 상세조회가 완료되지 않았습니다.");
				mv.addObject("error", "게시글 상세 조회 실패");
				mv.addObject("url", "/board/list.kh");
				mv.setViewName("common/errorPage");
			}
		} catch (Exception e) {
			mv.addObject("msg", "게시글 상세조회가 완료되지 않았습니다.");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", "/board/list.kh");
			mv.setViewName("common/errorPage");
		}
		return mv;
	}
	
	@RequestMapping(value="/board/delete.kh", method=RequestMethod.GET)
	public ModelAndView deleteBoard(int boardNo
									 , String boardWriter
									 , HttpSession session 
 									 , ModelAndView mv) {
		try {
			String memberId = (String)session.getAttribute("memberId");
			if(boardWriter != null && boardWriter.equals(memberId)) {
				int result = bService.deleteBoard(boardNo);
				if(result > 0) {
					mv.setViewName("redirect:/board/list.kh");
				} else {
					mv.addObject("msg", "게시글 삭제에 실패했습니다..");
					mv.addObject("error", "게시글 삭제 실패");
					mv.addObject("url", "/board/list.kh");
					mv.setViewName("common/errorPage");
				}
			} else {
				mv.addObject("msg", "게시글 작성자가 아닙니다..");
				mv.addObject("error", "게시글 삭제 실패");
				mv.addObject("url", "/board/list.kh");
				mv.setViewName("common/errorPage");
			}
		} catch (Exception e) {
			mv.addObject("msg", "게시글 삭제에 실패했습니다..");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", "/board/list.kh");
			mv.setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	
	public Map<String, Object> saveFile(HttpServletRequest request, MultipartFile uploadFile) throws Exception, IOException {
		Map<String, Object> fileMap = new HashMap<String, Object>();
		// resources 경로 구하기
		String root = request.getSession().getServletContext().getRealPath("resources");
		// 파일 저장 경로 구하기
		String savePath = root + "\\buploadFiles";
		// 파일 이름 구하기
		String fileName =  uploadFile.getOriginalFilename();
		// 파일 확장자 구하기
		String extension = fileName.substring(uploadFile.getOriginalFilename().lastIndexOf(".")+1);
		
		// 시간으로 파일 리네임하기
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileRename = sdf.format(new Date(System.currentTimeMillis())) + "." + extension;
		
		// 파일 저장 전 폴더 만들기
		File saveFolder = new File(savePath);
		if(!saveFolder.exists()) {
			saveFolder.mkdir();
		}
		
		// 파일저장
		File saveFile = new File(savePath + "\\" + fileRename);
		uploadFile.transferTo(saveFile);
		long fileLength = uploadFile.getSize();
		
		// 파일 정보 리턴
		fileMap.put("fileName", fileName);
		fileMap.put("fileRename", fileRename);
		fileMap.put("filePath", "../resources/boardFiled/" + fileRename);
		fileMap.put("fileLength", fileLength);
		
		return fileMap;
	}
}
