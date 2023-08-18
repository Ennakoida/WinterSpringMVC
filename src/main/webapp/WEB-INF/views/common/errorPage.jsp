<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>에러페이지</title>
	</head>
	<body>
		<h1>${ error }</h1>
		<a href="${ url }">페이지 이동</a>
		<script>
			const msg = "${ msg }";
			if(msg != "") alert(msg);
		</script>
	</body>
</html>