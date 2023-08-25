<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>공지사항 상세 조회</title>
		<link rel="stylesheet" href="../resources/css/notice/notice.css">
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>공지사항 상세 조회</h1>
			<ul>
				<li>
					<label>제목</label>
					<span>${ notice.noticeSubject }</span> 
				</li>
				<li>
					<label>작성자</label>
					<span>${ notice.noticeWriter }</span> 
				</li>
				<li>
					<label>내용</label>
					<p>${ notice.noticeContent }</p>
				</li>
				<li>
					<label>첨부파일</label>
					<!-- 이미지 보이게 -->
					<img alt="첨부파일" src="../resources/nuploadFiles/${ notice.noticeFileRename }">
					<!-- 다운로드 -->
					<a href="../resources/nuploadFiles/${ notice.noticeFileRename }" download>${ notice.noticeFilename }</a>
				</li>
			</ul>
			<div>
				<button type="button" onclick="showModifyPage();">수정하기</button>
				<button>삭제하기</button>
				<button type="button" onclick="showList();">목록으로</button>
			</div>		
			
			<script>
				function showModifyPage() {
					const noticeNo = "${ notice.noticeNo }";
					location.href="/notice/modify.kh?noticeNo=" + noticeNo;
				}
				
				function showList(){
					location.href="/notice/list.kh";
				}
			</script>
	</body>
</html>