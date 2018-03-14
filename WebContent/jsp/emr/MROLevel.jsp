<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ include file="include.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>电子病历分级分块</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<style type="text/css">
		body,table,tr,td,form,iframe {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 9pt;
			margin: 0px;
		}
		
		.line {
			display: inline;
			padding: 5px;
			height: 100%
		}
		
		.line div,iframe{
			width:100%;
			height:100%;
			border :1px solid Silver;
		}
	</style>
	<script type="text/javascript">
		$(function(){
			var CASE_NO = getUrlParameter(window.location,"CASE_NO");
			var MR_NO = getUrlParameter(window.location,"MR_NO");
			var VIEW_PATTERN = getUrlParameter(window.location,"VIEW_PATTERN");
			var USER_ID = getUrlParameter(window.location,"USER_ID");
		
			var tree1; //第一棵树
			var tree2; //第二棵树
			
			//初始化界面
			onInit();
			
			function onInit(){
				initTree1();
				initTree2();
				query_cmd();
			}
			
			
			function query_cmd(){
				var url = "EMRWebQueryServlet?CASE_NO="+CASE_NO+"&ADM_TYPE_2=I&Mr_No="+MR_NO+"&TYPE=level";
				tree1.setXMLAutoLoading(url);
				tree1.refreshItem(0);
			}
			
			//第一棵树初始化
			function initTree1(){
				tree1 = new dhtmlXTreeObject("treeBox1", "100%", "100%", 0);
				tree1.setSkin("dhx_skyblue");
				tree1.setImagePath("./js/dhtmlxTree/samples/common/images/");
				tree1.enableDragAndDrop(0);
				tree1.enableTreeLines(true);
				tree1.setImageArrays("plus", "plus2.gif", "plus3.gif", "plus4.gif", "plus.gif", "plus5.gif");
				tree1.setImageArrays("minus", "minus2.gif", "minus3.gif", "minus4.gif", "minus.gif", "minus5.gif");
				tree1.setStdImages("book.gif", "books_open.gif", "books_close.gif");
				tree1.setOnClickHandler(onTree1Click);
			}
			
			//点击第一棵树，生成第二棵树
			function onTree1Click(nodeId) {
				if(nodeId != "Root"){
					var url = "EMRWebQueryFileIndexServlet?CASE_NO="+nodeId+"&Mr_No="+MR_NO+"&ADM_TYPE_2=I&TYPE=level&VIEW_PATTERN="+VIEW_PATTERN+"&USER_ID="+USER_ID;
					tree2.setXMLAutoLoading(url);
					tree2.refreshItem(0);
				}
			}
			
			//第二棵树初始化
			function initTree2(){
				tree2 = new dhtmlXTreeObject("treeBox2", "100%", "100%", 0);
				tree2.setSkin("dhx_skyblue");
				tree2.setImagePath("./js/dhtmlxTree/samples/common/images/");
				tree2.enableDragAndDrop(0);
				tree2.enableTreeLines(true);
				tree2.setImageArrays("plus", "plus2.gif", "plus3.gif", "plus4.gif", "plus.gif", "plus5.gif");
				tree2.setImageArrays("minus", "minus2.gif", "minus3.gif", "minus4.gif", "minus.gif", "minus5.gif");
				tree2.setStdImages("book.gif", "books_open.gif", "books_close.gif");
				tree2.setOnClickHandler(onTree2Click);
			}
			
			//点击第二棵树文件节点，查看单个病历
			function onTree2Click(nodeId) {
				var leaf = "";
				if(nodeId.indexOf(";") > 0){
					leaf = nodeId.split(";")[1];
				}
				
				if(leaf == "leaf"){
					
					if(VIEW_PATTERN == "JHW"){
						var params = new Object();
						params.VIEW_TYPE = 'ONE';
						params.VIEW_PATTERN = VIEW_PATTERN;
						params.LEVEL_FILE_PATH = nodeId.split(";")[0];
						params.TYPE = "LEVEL";
						
						var returnMethod = function(data) {
							showFile(data);
						}
						postAjax("EMRWebViewFileServlet", params, returnMethod);
					}else if(VIEW_PATTERN == "PDF"){
						showFile(nodeId.split(";")[0]);
					}
					
				}
				
				
			}
			
			//获取URL参数
			function getUrlParameter(url,parameter){
				try {
					var reg = new RegExp("(^|&)" + parameter + "=([^&]*)(&|$)", "i");
					var r = url.search.substr(1).match(reg);
					if (r != null){ 
						return unescape(r[2]); 
					}
					return "";
				} catch (e) {
					return "";
				}
				 
			}
			
			
			
			
			
			function showFile(data){
				if(VIEW_PATTERN == "PDF"){
					initPdf(data);
				}
				if(VIEW_PATTERN == "JHW"){
					initJhw(data);
				}
			}
				
			function initPdf(pdfPath){
				if(pdfPath==undefined || pdfPath==null || pdfPath==""){
					alert("pdf文件不存在！");
					return;
				}
				document.getElementById("emr").style.display = "";
				document.getElementById("emr").src = pdfPath;
			}
			
			function initJhw(){
				var jhw = "jsp\\emr\\jhw\\jhw.jsp";
				document.getElementById("emr").style.display = "";
				document.getElementById("emr").src = jhw;
			}
			
		})
	</script>

  </head>
  
  <body style="background-color:#a1dce6;">
  	<div style="margin: 10px 20px;border: 1px solid gray;height: 96%">
  		<div style="width: 15%;" class="line">
  			<div id="treeBox1"></div>
  		</div>
  		
  		<div style="width: 25%;" class="line">
  			<div id="treeBox2"></div>
  		</div>
  		
  		<div style="width: 60%;" class="line">
  			<iframe id="emr" name="emr"></iframe>
  		</div>
  	</div>
  </body>
</html>
