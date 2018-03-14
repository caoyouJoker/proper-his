<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="include.jsp"%>
<html>
	<head>
		<title>电子病历浏览</title>
		<script type="text/javascript">	
			var tree1; //第一棵树
			var tree2; //第二棵树
			
			//页面初始化
			function init()
			{
				sortChange(document.getElementById("SORT1"));
				sortChange(document.getElementById("SORT2"));
				initTree1();
				initTree2();
				document.getElementById("query").click();
			}
			
			//排序方式联动
			function sortChange(obj)
			{
				var sort1 = document.getElementById("SORT1");
				var sort2 = document.getElementById("SORT2");
				var sort3 = document.getElementById("SORT3");
				if("SORT1" == obj.name)
				{
					if("ADM_DATE" == sort1.value)
					{
						sort2.options[0] = new Option("门急住别", "ADM_TYPE");
						sort2.options[1] = new Option("就诊科室", "DEPT_CODE");
						sort3.options[0] = new Option("就诊科室", "DEPT_CODE");
					}
					if("ADM_TYPE" == sort1.value)
					{
						sort2.options[0] = new Option("就诊时间", "ADM_DATE");
						sort2.options[1] = new Option("就诊科室", "DEPT_CODE");
						sort3.options[0] = new Option("就诊科室", "DEPT_CODE");
					}
					if("DEPT_CODE" == sort1.value)
					{
						sort2.options[0] = new Option("就诊时间", "ADM_DATE");
						sort2.options[1] = new Option("门急住别", "ADM_TYPE");
						sort3.options[0] = new Option("门急住别", "ADM_TYPE");
					}
				}
				if("SORT2" == obj.name)
				{
					if("ADM_DATE" == sort1.value && "ADM_TYPE" == sort2.value)
					{
						sort3.options[0] = new Option("就诊科室", "DEPT_CODE");
					}
					if("ADM_TYPE" == sort1.value && "ADM_DATE" == sort2.value)
					{
						sort3.options[0] = new Option("就诊科室", "DEPT_CODE");
					}
					if("ADM_DATE" == sort1.value && "DEPT_CODE" == sort2.value)
					{
						sort3.options[0] = new Option("门急住别", "ADM_TYPE");
					}
					if("DEPT_CODE" == sort1.value && "ADM_DATE" == sort2.value)
					{
						sort3.options[0] = new Option("门急住别", "ADM_TYPE");
					}
					if("ADM_TYPE" == sort1.value && "DEPT_CODE" == sort2.value)
					{
						sort3.options[0] = new Option("就诊时间", "ADM_DATE");
					}
					if("DEPT_CODE" == sort1.value && "ADM_TYPE" == sort2.value)
					{
						sort3.options[0] = new Option("就诊时间", "ADM_DATE");
					}
				}
			}
			
			//获取请求URL
			function getUrl(servlet, nodeId)
			{
				var url = servlet;
				url += "?ADM_DATE_BEGIN=" + $("#ADM_DATE_BEGIN").val();
				url += "&ADM_DATE_END=" + $("#ADM_DATE_END").val();
				url += $("#ADM_TYPE_0").attr("checked") == true ? "&ADM_TYPE_0=" + $("#ADM_TYPE_0").val() : "";
				url += $("#ADM_TYPE_1").attr("checked") == true ? "&ADM_TYPE_1=" + $("#ADM_TYPE_1").val() : "";
				url += $("#ADM_TYPE_2").attr("checked") == true ? "&ADM_TYPE_2=" + $("#ADM_TYPE_2").val() : "";
				url += $("#ADM_TYPE_3").attr("checked") == true ? "&ADM_TYPE_3=" + $("#ADM_TYPE_3").val() : "";
				url += "&DEPT_CODE=" + $("#DEPT_CODE").val();
				url += "&SORT1=" + $("#SORT1").val();
				url += "&SORT2=" + $("#SORT2").val();
				url += "&SORT3=" + $("#SORT3").val();
				url += "&VIEW_PATTERN=" + $("#VIEW_PATTERN").val();
				url += "&Mr_No=" + "${Mr_No}";
				url += "&CASE_NO=" + nodeId;
				return url;
			}
			
			//第一棵树初始化
			function initTree1()
			{
				tree1 = new dhtmlXTreeObject("treeBox1", "100%", "100%", 0);
				tree1.setSkin("dhx_skyblue");
				tree1.setImagePath("./js/dhtmlxTree/samples/common/images/");
				tree1.enableDragAndDrop(0);
				tree1.enableTreeLines(true);
				tree1.setImageArrays("plus", "plus2.gif", "plus3.gif", "plus4.gif", "plus.gif", "plus5.gif");
				tree1.setImageArrays("minus", "minus2.gif", "minus3.gif", "minus4.gif", "minus.gif", "minus5.gif");
				tree1.setStdImages("book.gif", "books_open.gif", "books_close.gif");
				//var url = getUrl("EMRWebQueryServlet", "");
				//tree1.setXMLAutoLoading(url);
				//tree1.loadXML(url);
				tree1.setOnClickHandler(onTree1Click);
			}
			
			//查询数据，生成第一棵树
			function query_cmd()
			{
				var url = getUrl("EMRWebQueryServlet");
				tree1.setXMLAutoLoading(url);
				tree1.refreshItem(0);
				document.getElementById("treeTable").style.display = "";
			}
			
			//重置查询条件
			function reset_cmd() 
			{
				document.getElementById("SysEmrIndexForm").reset();
				sortChange(document.getElementById("SORT1"));
				sortChange(document.getElementById("SORT2"));
			}
			

			//第二棵树初始化
			function initTree2()
			{
				tree2 = new dhtmlXTreeObject("treeBox2", "100%", "100%", 0);
				tree2.setSkin("dhx_skyblue");
				tree2.setImagePath("./js/dhtmlxTree/samples/common/images/");
				tree2.enableDragAndDrop(0);
				tree2.enableTreeLines(true);
				tree2.setImageArrays("plus", "plus2.gif", "plus3.gif", "plus4.gif", "plus.gif", "plus5.gif");
				tree2.setImageArrays("minus", "minus2.gif", "minus3.gif", "minus4.gif", "minus.gif", "minus5.gif");
				tree2.setStdImages("book.gif", "books_open.gif", "books_close.gif");
				//var url = getUrl("EMRWebQueryFileIndexServlet", "");
				//tree2.setXMLAutoLoading(url);
				//tree2.loadXML(url);
				tree2.setOnClickHandler(onTree2Click);
				tree2.setOnDblClickHandler(onTree2DblClick);
			}
			
			//点击第一棵树，生成第二棵树
			function onTree1Click(nodeId) {
				if(nodeId != "Root")
				{
					var url = getUrl("EMRWebQueryFileIndexServlet", nodeId);
					tree2.setXMLAutoLoading(url);
					tree2.refreshItem(0);
				}
			}
			
			//点击第二棵树文件节点，查看单个病历
			function onTree2Click(nodeId) {
				if(nodeId.indexOf("EmrFileIndex") >= 0)
				{
					var array = nodeId.split("_");
					var url = "EMRWebViewFileServlet";
					var params = new Object();
					params.VIEW_TYPE = 'ONE';
					//alert("test"+$("#VIEW_PATTERN").val());
					params.VIEW_PATTERN = $("#VIEW_PATTERN").val();
					params.CASE_NO = array[1];
					params.FILE_SEQ = array[2];
					var returnMethod = function(data) {
						showFile(data);
					}
					postAjax(url, params, returnMethod);
				}
			}
			
			//点击第二棵树根节点，查看合并病历
			function onTree2DblClick(nodeId) {
				if(nodeId.indexOf("ROOT") >= 0)
				{
					var array = nodeId.split("_");
					var url = "EMRWebViewFileServlet";
					var params = new Object();
					params.VIEW_TYPE = 'ALL';
					params.VIEW_PATTERN = $("#VIEW_PATTERN").val();
					params.Mr_No = array[1];
					params.CASE_NO = array[2];
					params.FILE_SEQ = "0";
					var returnMethod = function(data) {
						showFile(data);
					}
					postAjax(url, params, returnMethod);
				}
			}
		</script>
		<style type="text/css">
body,table,tr,td,form,iframe {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	margin: 0px;
}
</style>
	</head>
	<body onload="init();" style="background-color:#a1dce6;">
	<table width="100%" height="100%">
		<tr height="12%">
			<td width="100%">
				<form id="SysEmrIndexForm" name="SysEmrIndexForm" method="post">
					<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
						<tr height="33%">
							<td width="9%">
								就诊时间（起始）：
							</td>
							<td width="16%">
								<input type="text" name="ADM_DATE_BEGIN" id="ADM_DATE_BEGIN"
									class="Wdate"
									onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									readonly="readonly" value="<c:out value='${admDateBegin}' escapeXml='false' />" />
							</td>
							<td width="9%">
								就诊时间（结束）：
							</td>
							<td width="16%">
								<input type="text" name="ADM_DATE_END" id="ADM_DATE_END"
									class="Wdate"
									onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									readonly="readonly" value="<c:out value='${admDateEnd}' escapeXml='false' />" />
							</td>
							<td width="7%">
								门急住别：
							</td>
							<td width="18%">
								<c:out value='${admTypeCheckBox}' escapeXml='false' />
							</td>
							<td width="7%">
								就诊科室：
							</td>
							<td width="18%">
								<select name="DEPT_CODE" id="DEPT_CODE" style="width:100px">
									<option value=""></option>
									<c:out value='${deptSelect}' escapeXml='false' />
								</select>
							</td>
						</tr>
						<tr height="33%">
							<td width="9%">
								排序方式1：
							</td>
							<td width="16%">
								<select name="SORT1" id="SORT1" style="width:100px" onchange="sortChange(this);">
									<c:out value='${sort1Select}' escapeXml='false' />
								</select>
							</td>
							<td width="9%">
								排序方式2：
							</td>
							<td width="16%">
								<select name="SORT2" id="SORT2" style="width:100px" onchange="sortChange(this);">
									<option value=""></option>
								</select>
							</td>
							<td width="7%">
								排序方式3：
							</td>
							<td width="18%">
								<select name="SORT3" id="SORT3" style="width:100px" onchange="sortChange(this);">
									<option value=""></option>
								</select>
							</td>
							<td width="7%">
								浏览格式：
							</td>
							<td width="18%">
								<select name="VIEW_PATTERN" id="VIEW_PATTERN" style="width:100px">
									<c:out value='${viewPatternSelect}' escapeXml='false' />
								</select>
							</td>
						</tr>
						<tr height="34%">
							<td align="center" colspan="8">
								<input type="button" value="查  询" id="query" onclick="query_cmd();" />
								&nbsp;&nbsp;
								<input type="reset" value="重  置" onclick="reset_cmd();"/>
							</td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
		<tr height="88%">
			<td width="100%">
				<table width="100%" height="100%" id="treeTable" style="display:none;">
					<tr height="100%">
						<td width="15%" valign="top">
							<div id="treeBox1" style="width:100%;height:100%;background-color:#f5f5f5;border :1px solid Silver;"></div><br>
						</td>
						<td width="25%" valign="top">
							<div id="treeBox2" style="width:100%;height:100%;background-color:#f5f5f5;border :1px solid Silver;"></div><br>
						</td>
						<td width="60%" valign="top">
							<iframe id="emr" name="emr" style="width:100%;height:100%;display:none" frameborder="0" scrolling="auto"></iframe>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
		<script type="text/javascript">
			function showFile(data)
			{
				//alert("data.viewPattern"+data.viewPattern);
				//alert("data.viewType"+data.viewType);
				if (data.viewType == "ALL") 
				{
					initPdf(data.pdfPath);
				}
				else if (data.viewType == "ONE") 
				{
					if(data.viewPattern == "HTML")
					{
						initXml(data.xmlPath, data.xslPath);
					}
					if(data.viewPattern == "PDF")
					{
						initPdf(data.pdfPath);
					}
					if(data.viewPattern == "JHW")
					{
						initJhw();
					}
					if(data.viewPattern == "CDA")
					{
						initCda(data.cdaXml);
					}
				} 
			}
			
			function initPdf(pdfPath)
			{
				if(pdfPath==undefined || pdfPath==null || pdfPath=="") 
				{
					alert("pdf文件不存在！");
					return;
				}
				document.getElementById("emr").style.display = "";
				document.getElementById("emr").src = encodeURI(pdfPath, "ISO-8859-1");
			}
			
			function initXml(xmlPath, xslPath)
			{
				if(xmlPath==undefined || xmlPath==null || xmlPath=="")
				{
					alert("xml文件不存在！");
					return;
				}
				var xml = "EMRWebViewFileServlet?VIEW_TYPE=HTML&xmlPath=" + xmlPath + "&xslPath=" + xslPath;
				//alert("xml==="+xml);
				document.getElementById("emr").style.display = "";
				document.getElementById("emr").src = encodeURI(xml, "ISO-8859-1");
			}
			function initJhw()
			{
				var jhw = "jsp\\emr\\jhw\\jhw.jsp";
				document.getElementById("emr").style.display = "";
				document.getElementById("emr").src = jhw;
			}
			function initCda(cdaXml)
			{
				if(cdaXml==undefined || cdaXml==null || cdaXml=="") {
					alert("CDA文件不存在！");
					return;					
				}
				document.getElementById("emr").style.display = "";
				document.frames("emr").document.body.innerText = cdaXml;
			}
		</script>
	</body>
</html>
