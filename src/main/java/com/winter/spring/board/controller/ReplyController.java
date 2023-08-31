package com.winter.spring.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.winter.spring.board.domain.Reply;
import com.winter.spring.board.service.ReplyService;

@Controller
@RequestMapping("/reply") // 공용주소. 기능 주소에서 해당 부분은 생략한다.
public class ReplyController {
	
	@Autowired
	private ReplyService rService;
	
	@RequestMapping(value="/add.kh", method=RequestMethod.POST) // 공용주소를 적었기 때문에 /reply/add.kh -> /add.kh
	public ModelAndView insertReply(ModelAndView mv
									, @ModelAttribute Reply reply
									, HttpSession session) {
		String url = "";
		try {
			String replyWriter = (String)session.getAttribute("memberId"); // 작성자는 session에서 가져옴
			reply.setReplyWriter(replyWriter);
			int result = rService.insertReply(reply);
			url = "/board/detail.kh?boardNo=" + reply.getRefBoardNo();
			
			if(result > 0) {
				mv.setViewName("redirect:"+ url);
			} else {
				mv.addObject("msg", "댓글이 등록되지 않았습니다.");
				mv.addObject("error", "댓글 등록 실패");
				mv.addObject("url", url);
				mv.setViewName("common/errorPage");
			}
		} catch (Exception e) {
			mv.addObject("msg", "댓글이 등록되지 않았습니다.");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", url);
			mv.setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	@RequestMapping(value="/update.kh", method=RequestMethod.POST)
	public ModelAndView updateReply(ModelAndView mv
									, @ModelAttribute Reply reply
									, HttpSession session) {
		String url = "";
		try {
			String replyWriter = (String)session.getAttribute("memberId");
			if(!replyWriter.equals("")) { // 로그인을 했으면 (session id가 비어있지 않으면)
				reply.setReplyWriter(replyWriter);
				url = "/board/detail.kh?boardNo=" + reply.getRefBoardNo();
				int result = rService.updateReply(reply);
				mv.setViewName("redirect:" + url);
			} else {
				mv.addObject("msg", "로그인이 되지 않았습니다..");
				mv.addObject("error", "로그인 정보 확인 실패");
				mv.addObject("url", "/index.jsp");
				mv.setViewName("common/errorPage");
			}
		} catch (Exception e) {
			mv.addObject("msg", "관리자에게 문의 바랍니다..");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", url);
			mv.setViewName("common/errorPage");
		}
		return mv;
	}
	
	@RequestMapping(value="/delete.kh", method=RequestMethod.GET)
	public ModelAndView deleteReply(ModelAndView mv
							  , @RequestParam("replyNo") Integer replyNo
							  , @RequestParam("replyWriter") String replyWriter
							  , @RequestParam("refBoardNo") String refBoardNo
							  , HttpSession session ) {
		
		// DELETE FROM REPLY_TBL WHERE REPLY_NO = 샵{ replyNo } AND R_STATUS = 'Y'
		// UPDATE REPLY_TBL SET R_STATUS = 'N' WHERE REPLY_NO= 샵{ replyNo }
		
		String url = "";
		try {
			String memberId = (String)session.getAttribute("memberId"); 
			if(replyWriter != null && replyWriter.equals(memberId)) {
				url = "/board/detail.kh?boardNo=" + refBoardNo;
				Reply reply = new Reply();
				reply.setReplyNo(replyNo);
				reply.setReplyWriter(replyWriter);
				int result = rService.deleteReply(reply);
				if(result > 0) {
					mv.setViewName("redirect:" + url);
				} else {
					mv.addObject("msg", "댓글 삭제가 되지 않았습니다..");
					mv.addObject("error", "댓글 삭제 실패");
					mv.addObject("url", url);
					mv.setViewName("common/errorPage");
				}
			} else {
				mv.addObject("msg", "자신의 댓글만 삭제할 수 있습니다..");
				mv.addObject("error", "댓글 삭제 불가");
				mv.addObject("url", url);
				mv.setViewName("common/errorPage");
			}
		} catch (Exception e) {
			mv.addObject("msg", "관리자에게 문의 바랍니다..");
			mv.addObject("error", e.getMessage());
			mv.addObject("url", "/board/list.kh");
			mv.setViewName("common/errorPage");
		}
		return mv;
	}

}
