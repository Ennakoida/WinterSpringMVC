package com.winter.spring.member.store;

import org.apache.ibatis.session.SqlSession;

import com.winter.spring.member.domain.Member;

public interface MemberStore {

	int insertMember(SqlSession session, Member member);

	int updateMember(SqlSession session, Member member);

	int deleteMember(SqlSession session, String memberId);

	int checkMemberLogin(SqlSession session, Member member);

	Member getMemberById(SqlSession session, String memberId);

}
