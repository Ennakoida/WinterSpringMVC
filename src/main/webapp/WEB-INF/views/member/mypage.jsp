<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>마이페이지</title>
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>마이페이지</h1>
			<fieldset>
				<legend>마이페이지</legend>
				<ul>
					<li>
						<label>아이디 *</label>
						<span>${ member.memberId }</span>
					</li>
					<li>
						<label>이름 *</label>
						<span>${ member.memberName }</span>
					</li>
					<li>
						<label>나이</label>
						<span>${ member.memberAge }</span>
					</li>
					<li>
						<label>성별</label>
						<span>${ member.memberGender }</span>
					</li>
					<li>
						<label>이메일</label>
						<span>${ member.memberEmail }</span>
					</li>
					<li>
						<label>전화번호</label>
						<span>${ member.memberPhone }</span>
					</li>
					<li>
						<label>주소</label>
						<span>${ member.memberAddr }</span>
					</li>
					<li>
						<label>취미</label>
						<span>${ member.memberHobby }</span>
					</li>
				</ul>
			</fieldset>
			<a href="/index.jsp">메인으로 이동</a>
			<a href="/member/update.kh?memberId=${ memberId }">수정하기</a>
			<a href="/member/delete.kh?memberId=${ memberId }">삭제하기</a>
<!-- 			<a href="/member/allMembers.do">전체 회원 조회</a> -->
	</body>
</html>