<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<title>로그인</title>
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>환영합니다.</h1>
		<c:if test="${ memberId eq null }">
		<form action="/member/login.kh" method="post">
			<fieldset>
				<legend>로그인</legend>
				ID : <input type="text" name="memberId"> <br>
				PW : <input type="password" name="memberPw">
			</fieldset>
			<div>
				<input type="submit" value="로그인">
			</div>
		</form>
		</c:if>
		<c:if test="${ memberId ne null }">
			${ memberId }님 환영합니다. <a href="/member/logout.kh">로그아웃</a><br>
			<!-- a태그 .kh 뒤에 ? 가 필요한가 고민. 쿼리문 생각해보면 필요함 -->
			<a href="/member/mypage.kh?memberId=${ memberId }">마이페이지</a>
		</c:if>
	</body>
</html>
