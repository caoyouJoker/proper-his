<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
	<head>
		<title>电子病历高级搜索</title>
        
	<link rel="stylesheet" type="text/css" media="all"	href="${ctx}/js/jscalendar/skins/aqua/theme.css" title="Aqua" />
	<link rel="stylesheet" type="text/css" href="${ctx}/css/common.css" />
	
</head>
<body style="background-color:#a1dce6;">
	
	<table>
		<c:forEach items="${page.list}" var="emr" varStatus="status">
		<tr>
			<td>
				<a href='javascript:parent.openRead("${emr.caseNo }","${emr.fileSeq}");'>${emr.fileName}</a>
				<br>
				${emr.desc}
			</td>
		</tr>
		</c:forEach>
	</table>
	 <!--翻页start-->
	 <c:choose>

	   <c:when test="${not empty page.list  }"> 
	   		 <jsp:include page="page-down.jsp"></jsp:include>   
	   </c:when>
	   
	   <c:otherwise>   
	   		<p>没有查询到数据</p>
	   </c:otherwise>
  
    </c:choose>
   
    
    <!--翻页end-->
</body>
</html>