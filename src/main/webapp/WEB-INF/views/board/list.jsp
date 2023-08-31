<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>게시글 목록</title>
		<link rel="stylesheet" href="../resources/css/board/board.css">
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>게시글 목록</h1>
		<table>
			<colgroup>
				<col width="10%">
				<col width="35%">
				<col width="10%">
				<col width="25%">
				<col width="10%">
			</colgroup>
			<thead>
				<tr>
					<th>번호</th>
					<th>제목</th>
					<th>작성자</th>
					<th>작성날짜</th>
					<th>첨부파일</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="board" items="${ bList }" varStatus="i">
					<tr>
						<td>${ i.count }</td> 
						<c:url var="detailUrl" value="/board/detail.kh">
							<c:param name="boardNo" value="${ board.boardNo }"></c:param>
						</c:url>
						<td><a href="${ detailUrl }">${ board.boardTitle }</a></td>
						<td>${ board.boardWriter }</td>
						<td>
							<fmt:formatDate pattern="yyyy-MM-dd" value="${ board.bCreateDate }"/> <br>
						</td>
						<td>
							<c:if test="${!empty board.boardFileName }">O</c:if>
							<c:if test="${empty board.boardFileName }">X</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr align="center">
					<td colspan="5">
						<c:if test="${ pInfo.startNavi != 1 }">
							<c:url var="prevUrl" value="/board/list.kh">
								<c:param name="page" value="${ pInfo.startNavi - 1 }"></c:param>
							</c:url>
							<a href="${ prevUrl }">[이전]</a>						
						</c:if>
						<c:forEach begin="${ pInfo.startNavi }" end="${ pInfo.endNavi }" var="p">
							<c:url var="pageUrl" value="/board/list.kh">
								<c:param name="page" value="${ p }"></c:param>
							</c:url>
							<a href="${ pageUrl }">${ p }</a>&nbsp;
						</c:forEach>
						<c:if test="${ pInfo.endNavi != pInfo.naviTotalCount }">
							<c:url var="nextUrl" value="/board/list.kh">
								<c:param name="page" value="${ pInfo.endNavi + 1 }"></c:param>
							</c:url>
							<a href="${ nextUrl }">[다음]</a>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<form action="/board/search.kh" method="get">
							<select name="searchCondition">
								<option value="all">전체</option>
								<option value="writer">작성자</option>
								<option value="title">제목</option>
								<option value="content">내용</option>
							</select>
							<input type="text" name="searchKeyword">
							<input type="submit" value="검색">
						</form>
					</td>
					<td>
						<button type="button" onclick="insertBoard();">글쓰기</button>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<script>
			function insertBoard() {
				location.href="/board/write.kh";
			}
		</script>
	</body>
</html>