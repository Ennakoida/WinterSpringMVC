package com.winter.spring.member.service;

import com.winter.spring.member.domain.Member;

public interface MemberService {

	int insertMember(Member member);

	int updateMember(Member member);

	int deleteMember(String memberId);

	int checkMemberLogin(Member member);

	Member getMemberById(String memberId);

}
