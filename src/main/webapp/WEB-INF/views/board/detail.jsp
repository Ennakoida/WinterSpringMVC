<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>게시글 상세 조회</title>
		<link rel="stylesheet" href="../resources/css/board/board.css">
		<link rel="stylesheet" href="../resources/css/main.css">
	</head>
	<body>
		<h1>게시글 상세 조회</h1>
			<ul>
				<li>
					<label>제목</label>
					<span>${ board.boardTitle }</span> 
				</li>
				<li>
					<label>작성자</label>
					<span>${ board.boardWriter }</span> 
				</li>
				<li>
					<label>내용</label>
					<p>${ board.boardContent }</p>
				</li>
				<li>
					<label>첨부파일</label>
					<!-- 이미지 보이게 -->
					<img alt="첨부파일" src="../resources/buploadFiles/${ board.boardFileRename }">
					<!-- 다운로드 -->
					<a href="../resources/nuploadFiles/${ board.boardFileRename }" download>${ board.boardFileName }</a>
				</li>
			</ul>
			<div>
				<button type="button" onclick="showModifyPage();">수정하기</button>
				<button>삭제하기</button>
				<button type="button" onclick="showList();">목록으로</button>
			</div>		
			<hr>
			<!-- 댓글 등록 -->
			<form action="/reply/add.kh" method="post">
				<input type="hidden" name="refBoardNo" value="${ board.boardNo }">
				<table width="700" boarder="1">
					<tr>
						<td>
							<textarea rows="3" cols="55" name="replyContent"></textarea>
						</td>
						<td>
							<input type="submit" value="등록">
						</td>
					</tr>
				</table>
			</form>
		
			<!-- 댓글 목록 -->
			<table width="700" boarder="1">
				<c:forEach var="reply" items="${ rList }">
					<tr>
						<td>${ reply.replyWriter }</td>
						<td>${ reply.replyContent }</td>
						<td>${ reply.rCreateDate }</td>
						<td>
							<a href="javascript:void(0)" onclick="showModifyForm(this, '${ reply.replyContent }');">수정하기</a>
<!-- 							<a href="javascript:void(0)" onclick="showModifyForm(this);">수정하기</a> -->
							<a href="#">삭제하기</a>
						</td>
					</tr>
					<tr style="display: none;">
<!-- 							html form 태그를 이용 -->
<!-- 						<form action="/reply/update.kh" method="post"> -->
<%-- 							<input type="hidden" name="replyNo" value="${ reply.replyNo }"> --%>
<%-- 							<input type="hidden" name="refBoardNo" value="${ reply.refBoardNo }"> --%>
<%-- 							<td colspan="3"><textarea rows="3" cols="55" name="replyContent">${ reply.replyContent }</textarea> --%>
<!-- 							<td><input type="submit" value="수정 완료"></td> -->
<!-- 						</form> -->

<!-- 							dom을 이용 -->
							<td colspan="3"><textarea rows="3" cols="55" name="replyContent" id="replyContent">${ reply.replyContent }</textarea>
							<td><input type="button" onclick="replyModify('${ reply.replyNo }', '${ reply.refBoardNo }', this);" value="수정 완료"></td>
						</form>
					</tr>
				</c:forEach>
			</table>		
			
			<script>
				function showModifyPage() {
					const boardNo = "${ board.boardNo }";
					location.href="/board/modify.kh?boardNo=" + boardNo;
				}
				
				function showList(){
					location.href="/board/list.kh";
				}
			</script>
			
			<!-- 수정하기 입력창 보이기 -->
			<script>
				// 1. HTML 태그, display:none을 이용하는 방법
				function showModifyForm(obj) {
					var obj = obj.parentElement.parentElement.nextElementSibling;
					if(obj.style.display == "none") obj.style.display = "";
					else obj.style.display = "none";
				}

				// 2. DOM 프로그래밍을 이용하는 방법
// 				function showModifyForm(obj, replyContent) {
// 					const trTag = document.createElement("tr");
// 					const tdTag1 = document.createElement("td");
// 					tdTag1.colSpan = 3;
// 					const textareaTag = document.createElement("textarea");
// 					textareaTag.rows = 3;
// 					textareaTag.cols = 55;
// 					textareaTag.textContent = replyContent;
// 					tdTag1.appendChild(textareaTag);
					
// 					const tdTag2 = document.createElement("td");
// 					const inputTag = document.createElement("input");
// 					inputTag.type = "button";
// 					inputTag.value = "완료";
// 					tdTag2.appendChild(inputTag);
					
// 					trTag.appendChild(tdTag1);
// 					trTag.appendChild(tdTag2);
// 					console.log(trTag);
					
// 					// 클릭한 a를 포함하고 있는 tr 다음에 수정폼이 있는 tr 추가하기
// 					const prevTrTag = obj.parentElement.parentElement;
// 					if(prevTrTag.nextElementSibling == null || !prevTrTag.nextElementSibling.querySelector("input"))
// 						prevTrTag.parentNode.insertBefore(trTag, prevTrTag.nextSibling);
// 				}
				
				//DOM 프로그래밍을 이용해 form 추가
				function replyModify(replyNo, refBoardNo, obj){
					const form = document.createElement("form");
					form.action="/reply/update.kh";
					form.method="post";
					
					const inputTag1 = document.createElement("input");
					inputTag1.type="hidden";
					inputTag1.value=replyNo;
					inputTag1.name="replyNo";
					const inputTag2 = document.createElement("input");
					inputTag2.type="hidden";
					inputTag2.value=refBoardNo;
					inputTag2.name="refBoardNo";
					const inputTag3 = document.createElement("input");
					inputTag3.type="text";
					// this를 이용하여 수정해야함. 이대로 쓰면 다 같은 textarea value만 가져옵니다.
// 					inputTag3.value=document.getElementById("replyContent").value;
					inputTag3.value=obj.parentElement.nextElementSibling.querySelector('textarea').value;
					inputTag3.name="replyContent";
					
					form.appendChild(inputTag1);
					form.appendChild(inputTag2);
					form.appendChild(inputTag3);
					
					document.body.appendChild(form);
					form.submit();
				}
				
			</script>
	</body>
</html>