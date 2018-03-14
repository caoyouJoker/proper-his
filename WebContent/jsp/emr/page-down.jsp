<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link href="${ctx}/skins/skin01/css/css.css" rel="stylesheet"
	type="text/css" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<style type="text/css">
a {
	color: Blue;
	text-decoration: none;
}
</style>
<div class="page">
	<span class="page_now"> <c:set var="page" value="${page}"></c:set>

		<c:if test="${page != null}">


			<c:choose>
				<c:when test="${page.prevPage eq false}">
					【首页】
                   	【上一页】
				</c:when>
				<c:otherwise>
					<a href="#" onclick="eventSelect('1')">【首页】</a>
                    <a href="#" onclick="eventSelect'${page.toPage-1}')">【上一页】</a>
				</c:otherwise>
			</c:choose>


			<c:choose>
				<c:when test="${page.nextPage eq false}">
					【下一页】
                  【尾页】
				</c:when>
				<c:otherwise>
					<a href="#" onclick="eventSelect('${page.toPage+1}')">【下一页】</a>
                   <a href="#" onclick="eventSelect('${page.totalPage}')">【尾页】</a>
				</c:otherwise>
			</c:choose>

			<!-- 当前页数/总页数 -->
    ${page.toPage} /${page.totalPage}
       
    <span><input type="text" id="tiaozhuan2" name="tiaozhuan2"
					onkeyup="if(isNaN(value))execCommand('undo')"
					onafterpaste="if(isNaN(value))execCommand('undo')" size="3" class="form"/>
			</span>
			<a href="#" onclick="return selectPage()" >跳转到
			</a>
		</c:if> </span> 共有${page.totalRecords}条信息，当前第
	<font> ${page.toPage} </font> 页，共
	<font> ${page.totalPage}</font> 页
	<input type="hidden" name="isButton" id="isButton" value="0" />
	<input type="hidden" name="toPage" id="toPage" value="${page.toPage}" />
</div>

<script language="javascript">
    //控制跳转按钮  
	function eventSelect(toPage){
		parent.document.getElementById("toPage").value=toPage;
		document.getElementById("isButton").value="1";
		document.forms["search_form"].submit();
	}

	function selectPage(){
		
	    
	    var toPage = document.getElementById("tiaozhuan2").value;
	    var totalPage = "${page.totalPage}";
	    totalPage = "" == totalPage ? 0 :　totalPage;
	    
	    //robo加判断输入的是否是数字
	   
	    if(!isFullNum(toPage)){
			alert("页码必须为数字！");		
			return ;
		}

	    if(toPage < 1){
	        alert("跳转页不能小于0");
	        return false;
	    }else if(toPage > totalPage){
	        alert("跳转页不能大于总页数");
	        return false;
	    }else{
	        document.getElementById("toPage").value=toPage;
	        document.getElementById("isButton").value="1";
		    document.forms[0].submit();
		    return true;
	    }
	}
	
	// 验证是不是整数
	function isFullNum(num) {
		var numExp = /^[0-9]+$/;
		var numReg = new RegExp(numExp);
		return numReg.test(num);
	}
</script>