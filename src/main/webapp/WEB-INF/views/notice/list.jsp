<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>공지사항 목록</title>
		<link rel="stylesheet" href="../resources/css/notice/notice.css">
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>공지사항 목록</h1>
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
				<!-- list 데이터는 items에 넣었고, var에서 설정한 변수로 list 데이터에서 꺼낸 값을 사용 -->
				<!-- i의 값은 varStatus로 사용 -->
				<c:forEach var="notice" items="${ nList }" varStatus="i">
					<tr>
						<td>${ i.count }</td> <!-- i.index도 가능하다. 단, index는 0부터, count는 1부터 -->
						<td>${ notice.noticeSubject }</td>
						<td>${ notice.noticeWriter }</td>
						<td>
							<!--  date format 설정 -->
							<fmt:formatDate pattern="yyyy-MM-dd" value="${ notice.nCreateDate }"/> <br>
<%-- 							${ notice.nCreateDate } --%>
						</td>
						<td>
							<!-- 첨부파일이 있으면 (없지 않으면) -->
							<c:if test="${!empty notice.noticeFilename }">O</c:if>
							<!-- 첨부파일이 없으면 -->
							<c:if test="${empty notice.noticeFilename }">X</c:if>
						</td>
<!-- 						<td> -->
<%-- 							<fmt:formatNumber pattern="##,###,###" value="1230000"> </fmt:formatNumber> --%>
<!-- 						</td> -->
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr align="center">
					<td colspan="5">
						<c:forEach begin="${ pInfo.startNavi }" end="${ pInfo.endNavi }" var="p">
							<!-- core tag로 url 관리하기 -->
							<!-- = <a href="/notice/list.kh?page=${ p }">${ p }</a>&nbsp; -->
							<c:url var="pageUrl" value="/notice/list.kh">
								<!-- c:param name="키값" value="value값" -->
								<c:param name="page" value="${ p }"></c:param>
							</c:url>
							<a href="${ pageUrl }">${ p }</a>&nbsp;
						</c:forEach>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<form action="/notice/search.kh" method="get">
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
						<button>글쓰기</button>
					</td>
				</tr>
			</tfoot>
		</table>
	</body>
</html>