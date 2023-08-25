package com.winter.spring.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.winter.spring.member.domain.Member;
import com.winter.spring.member.service.MemberService;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	@RequestMapping(value="/member/register.kh", method = RequestMethod.GET)
	public String showRegisterForm() {
		return "member/register";
	}

	@RequestMapping(value="/member/register.kh", method = RequestMethod.POST)
	public String registerMember(
//			@RequestParam("memberId") String memberId
			@ModelAttribute Member member // jsp의 name값과 Member의 값이 같으면 @ModelAttribute로 묶어서 쓸 수 있다. (@RequestParam * n개씩 안써도 됨)
			, Model model) {
		
		try {
			// INSERT INTO MEMBER_TBL
			int result = service.insertMember(member);
			if(result > 0) {
				// 성공하면 로그인 페이지
				// home.jsp가 로그인 할 수 있는 페이지가 되면 됨!
				return "redirect:/index.jsp";
			} else {
				// 실패하면 에러페이지로 이동
				model.addAttribute("msg", "회원가입이 완료되지 않았습니다.");
				model.addAttribute("error", "회원가입 실패");
				model.addAttribute("url", "/member/register.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/member/register.kh");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="/member/login.kh", method = RequestMethod.POST)
	public String memberLoginCheck(@ModelAttribute Member member
								 , HttpSession session // 쿼리스트링이 계속 따라다니는게 싫으면 어노테이션 말고 HttpSession 써주자
								 , Model model) {
		
		try {
			// SELECT * FROM MEMBER_TBL WHERE MEMBER_ID = ? AND MEMBER_PW = ?
			int result = service.checkMemberLogin(member);
			if(result > 0) {
				// 성공하면 메인페이지로 이동
				session.setAttribute("memberId", member.getMemberId());
				return "redirect:/index.jsp";
			} else {
				// 실패하면 에러페이지로 이동
				model.addAttribute("msg", "로그인이 완료되지 않았습니다.");
				model.addAttribute("error", "로그인 실패");
				model.addAttribute("url", "/member/register.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/member/register.kh");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="/member/logout.kh", method = RequestMethod.GET)
	public String memberLogout(HttpSession session, Model model) {
		if(session != null) {
			session.invalidate();
			return "redirect:/index.jsp";
		} else {
			model.addAttribute("msg", "로그아웃을 완료하지 못했습니다.");
			model.addAttribute("error", "로그아웃 실패");
			model.addAttribute("url", "/index.jsp");
			return "common/errorPage";
		}
	}
	
    @RequestMapping(value="/member/mypage.kh", method = {RequestMethod.GET, RequestMethod.POST}) // session을 이용하여 쿼리스트링 숨길때 // : GET, POST도 할거야. 다중으로 작성 가능
//	@RequestMapping(value="/member/mypage.kh", method = RequestMethod.POST) // form method=post를 이용하여 쿼리스트링 숨길때
	public String showMyPage(
							// 쿼리스트링 받기 위해 @RequestParam 써줌
							// @RequestParam("memberId") String memberId
							HttpSession session
							// 모델에 키와 값으로 데이터를 넣어주면 jsp에서 꺼내서 사용가능
							, Model model) {
		// SELECT * FROM MEMBER_tBL WHERE MEMBER_ID = ?
		// Exception 발생 시 에러메시지를 출력하기 위해 try ~ catch 설정
		try {
			String memberId = (String)session.getAttribute("memberId"); // session을 이용하여 쿼리스트링 숨길때
			Member member = service.getMemberById(memberId);
			if(member != null) {
				model.addAttribute("member", member);
				return "member/mypage";
			} else {
				model.addAttribute("msg", "마이페이지를 조회하지 못했습니다.");
				model.addAttribute("error", "마이페이지 조회 실패");
				model.addAttribute("url", "/member/register.kh");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/member/register.kh");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="/member/update.kh", method = RequestMethod.GET)
	public String showModifyForm(String memberId, Model model) { // @RequestParam이 같으면 생략가능
		Member member = service.getMemberById(memberId);
		model.addAttribute("member", member);
		return "member/modify";
	}
	
	@RequestMapping(value="/member/update.kh", method = RequestMethod.POST)
	public String modifyMember(@ModelAttribute Member member, Model model) {
		try {
			int result = service.updateMember(member);
			if(result > 0) {
				return "redirect:/index.jsp";
			} else {
				model.addAttribute("msg", "회원 정보 수정이 완료되지 않았습니다.");
				model.addAttribute("error", "회원 정보 수정 실패");
				model.addAttribute("url", "/member/mypage.kh?memberId"+member.getMemberId());
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/member/register.kh");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="/member/delete.kh", method = RequestMethod.GET)
	public String outServiceMember(@RequestParam("memberId") String memberId, Model model) {
		try {
			int result = service.deleteMember(memberId);
			if(result > 0) {
				return "redirect:/member/logout.kh";
			} else {
				model.addAttribute("msg", "회원 탈퇴가 완료되지 않았습니다.");
				model.addAttribute("error", "회원 탈퇴 실패");
				model.addAttribute("url", "/index.jsp");
				return "common/errorPage";
			}
		} catch (Exception e) {
			model.addAttribute("msg", "관리자에게 문의해주세요.");
			model.addAttribute("error", e.getMessage());
			model.addAttribute("url", "/index.jsp");
			return "common/errorPage";
		}
	}
}
