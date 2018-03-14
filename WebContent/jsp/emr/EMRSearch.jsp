<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="include.jsp"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
	<head>
		<title>电子病历高级搜索</title>
    <script type="text/javascript"
			src="${ctx}/js/jQuery/form/jquery.form.js"></script>	
    <script type="text/javascript"
		src="${ctx}/js/jscalendar/calendar.js"></script>
	<script type="text/javascript"
		src="${ctx}/js/jscalendar/lang/calendar-en.js"></script>
	<script type="text/javascript"
		src="${ctx}/js/jscalendar/lang/calendar-zh.js"></script>
	<script type="text/javascript"
		src="${ctx}/js/jscalendar/calendar-setup.js"></script>
        
	<link rel="stylesheet" type="text/css" media="all"	href="${ctx}/js/jscalendar/skins/aqua/theme.css" title="Aqua" />
	<link rel="stylesheet" type="text/css" href="${ctx}/css/common.css" />
     
        
		<script type="text/javascript">	
		/**
	     * 根路径
	     */
	     var rootPath = getRootPath();
	    /**
		 * 获取根路径
		 */
		function getRootPath(){
			
			var strFullPath = window.document.location.href;
			
			var strPath = window.document.location.pathname;
			
			var pos = strFullPath.indexOf(strPath);
			
			var prePath = strFullPath.substring(0, pos);
			
			var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
			
			return postPath;
		} 
		
			$().ready(
		    	function() {
		    		init();
					
					document.onkeydown=function(){
						//回车默认查询事件
						if(event.keyCode==13){
							doSearch();
						//Ctr+A增加条件
						}else if((event.ctrlKey)&&(event.keyCode==65)){
							addRow();
						}
					}
		    		
		    	}
			);
			//当前选择的元数据;
			var currentChoseMetaData=null;
			function chooseMetaData(elm){
				currentChoseMetaData=elm;
				
			}
			
			function doSearchPage(){
				$('#checkedEMRForms').attr("value",ermTree.getAllChecked());
				var stationCode =  $('#stationCode').val();
				if(stationCode == null || stationCode == ""){
					alert("请选择病区");
					return false;
				}
				
				var metaData = $('#metaData').val();
				var searchValue = $('#searchValue').val();
				if((metaData == null || metaData == "") || (searchValue == null || searchValue == "")){
					alert("请选择元数据或者填写查询值");
					return false;
				}
				var form = document.getElementById("search_form");
   			    form.submit();
			}
			
			/**
			/*
			/* 调用查询功能;
			/*
			**/		
			function doSearch(){
				//alert("do search come in...");
				//alert("ermTree.getAllChecked()===="+ermTree.getAllChecked());
				//var allERMForms=ermTree.getAllChecked().split(",");
				$('#checkedEMRForms').attr("value",ermTree.getAllChecked());
				
					var options = {   
						url : rootPath+'/EMRSearchServlet?method=doSearch',
						cache : false,
						type : 'post',
						dataType : 'json',  
						beforeSubmit : validateForm, 
						error :
							function() {
								alert('查询失败，请重试！');
							},
						success : callbackResult
		   			 };
		    
				     $('#search_form').ajaxSubmit(options);		
			}
			//检验;
			function validateForm(){
			  //value 是否填写了;
			  /**$('#searchValue').each( function(i) {
			  		if ($(this).attr("value")==""){
						alert("查询值不能为空，请填写!");
						$(this).focus();
						return false;				
					}
			  
			  
			  });**/
			
				return true;
			}
			/**
			/** 阅读病历;
			**/
			function openRead(caseNo,fileSeq){
				  var url = "EMRWebViewFileServlet";
				  var fileType=$("input[type=radio]:checked").val();								
					var params = new Object();
					params.VIEW_TYPE = 'ONE';				
					params.VIEW_PATTERN = fileType;
					params.CASE_NO = caseNo;
					params.FILE_SEQ = fileSeq;
					var returnMethod = function(data) {
						showFile(data);
					}
					postAjax(url, params, returnMethod);

				
			}
			
			/**
			* 打开阅读的文件;
			**/
			function showFile(data){
				
				if (data.viewType == "ONE") 
				{
					//PDF
					if(data.viewPattern == "PDF"){
						initPDF(data.pdfPath);
					}
					//HTML
					if(data.viewPattern == "HTML"){
						initXml(data.xmlPath, data.xslPath);
					}
					
					//JHW
					if(data.viewPattern == "JHW")
					{
						initJhw();
					}
					 
					
				}				
				
			}
			function initPDF(pdfPath){
				   //alert("pdf path==="+pdfPath);
					 	if(pdfPath==undefined || pdfPath==null || pdfPath=="") 
						{
								alert("pdf文件不存在！");
								return;
						}

				var xml = "EMRWebViewFileServlet?VIEW_TYPE=PDF&pdfPath=" + pdfPath ;

				window.open(encodeURI(encodeURI(xml)), '_blank');
			}
			
			function initXml(xmlPath, xslPath){
				if(xmlPath==undefined || xmlPath==null || xmlPath=="")
				{
					alert("xml文件不存在！");
					return;
				}
				//alert("xmlPath===="+xmlPath);
				
				//alert("xslPath===="+xslPath);
				
				var xml = "EMRWebViewFileServlet?VIEW_TYPE=HTML&xmlPath=" + xmlPath + "&xslPath=" + xslPath;
				 
				window.open(encodeURI(encodeURI(xml, "UTF-8")), '_blank');
			}
			
			function initJhw(){
				var jhw = "jsp\\emr\\jhw\\jhw.jsp";
				window.open(jhw, '_blank');
			}
			
			
			//返回结果;
			function callbackResult(result){
				 //alert("result"+result);
			   //
			   $('#searchResult').attr("innerHTML","");
			   if(result!=null){
				   if(result.EMRSearchResult!=null&&result.EMRSearchResult.length!=0){
				      var strResult="";
					  for(i=0;i<result.EMRSearchResult.length;i++){
							//alert("文件名："+result.EMRSearchResult[i].fileName);
							//alert("文件类型："+$("input[type=radio]:checked").val());
							
							//alert("file path===="+result.EMRSearchResult[i].pdfFilePath);
							var caseNo=result.EMRSearchResult[i].caseNo;
							var fileSeq=result.EMRSearchResult[i].fileSeq;
							//PDF 阅读;
					
							strResult+="<a href='javascript:openRead("+caseNo+","+fileSeq+");'>"+result.EMRSearchResult[i].fileName+"</a><br/>"+result.EMRSearchResult[i].desc+"......<br/><br/>";
							
							
					   }
					   
					   $('#searchResult').attr("innerHTML",strResult)					   
					 }
				 
				 }
			
			}

		
			function init()
			{
				$('#btn_Toggle').click(btn_Toggle_Click);
				
				$('#btn_top').click(btn_updown_Click);
				
				//元数据分类
				loadDataCategoryTree();
				//电子病历表单分类
				loadEMRCategoryTree();
				
			}
			
			/**
			** 重新建立查询
			*/
			function doNew(){
				this.location="${ctx}/EMRSearchServlet?method=init";
			
			}
			
			/**
			*加载元数据分类树
			*
			**/
			var tree=null;
			var ermTree=null;
			function loadDataCategoryTree(){
			
				tree = new dhtmlXTreeObject('categoryTree', '100%', '100%', 'root');			
				tree.setSkin('dhx_skyblue');
				tree.setImagePath('${ctx}/js/dhtmlxTree/codebase/imgs/csh_dhx_skyblue/');
				//双击
				//tree.attachEvent('onClick', 'treeNodeSelected');
				tree.setOnDblClickHandler('treeNodeSelected');
				tree.setXMLAutoLoading('${ctx}/EMRSearchServlet?method=loadDataCateTree');
				tree.loadXML('${ctx}/EMRSearchServlet?method=loadDataCateTree');
			
			}
			
			/**
			*加载EMR表单分类树
			*
			**/
			function loadEMRCategoryTree(){
				ermTree = new dhtmlXTreeObject("emrCategoryTree", "100%", "100%",'root');
				ermTree.setSkin('dhx_skyblue');
				ermTree.setImagePath('${ctx}/js/dhtmlxTree/codebase/imgs/csh_dhx_skyblue/');
				ermTree.enableCheckBoxes(1);
				ermTree.attachEvent('onCheck', 
					function(id,state){
							//alert("id==="+id);
							//ermTree.selectItem(id, true);
							//alert("ermTree.getSelectedItemId()1"+ermTree.getSelectedItemId());
							ermTree.enableThreeStateCheckboxes(true);
							if(ermTree.isItemChecked(id)){
								ermTree.setCheck(id,true);
							}else{								
								ermTree.setCheck(id,false);
							}
							ermTree.enableThreeStateCheckboxes(false);
					}
	
				);
                //alert("====come in.======");
				ermTree.setXMLAutoLoading('${ctx}/EMRSearchServlet?method=loadEMRCateTree');
				ermTree.loadXML('${ctx}/EMRSearchServlet?method=loadEMRCateTree');			
			}
			
			function treeNodeSelected(id){
			   //alert(" come in. "+id);
			   //alert(tree.hasChildren(id));
			   var childCount=tree.hasChildren(id)
			   if(childCount==0){
			     //alert("可以加入条件.");
			     
			     if(currentChoseMetaData!=null){
			     	    //alert(tree.getSelectedItemId());
			     	    //alert(tree.getSelectedItemText());
						//alert("===id==="+currentChoseMetaData.attr("id"));
						//alert("currentChoseMetaData.value"+currentChoseMetaData.value);
						if(currentChoseMetaData.value==undefined){
			     	  		currentChoseMetaData.attr("value",tree.getSelectedItemText());
						}else{
							currentChoseMetaData.value=tree.getSelectedItemText();
						}
			     	  	//
			     	  	var tdId=$(currentChoseMetaData).parent("td").attr('id');
			     	  	//alert("tdId==="+tdId);
			     	  	
			     	  	var currentTd=$("#"+tdId);
			     	  	//alert("currentTd"+currentTd.html());
			     	  	
			     	  	var test=$("#"+tdId).children("#metaCode");
			     	  	//var test=$("#"+tdId+":metaCode");
			     	  	//alert("test"+test.html());
			     	  	
			     	  	
			     	  	var currentMetaCode=currentTd.children("#metaCode");
			     	  	//alert("currentMetaCode"+currentMetaCode);
			     	  	
			     	  	currentMetaCode.attr("value",tree.getSelectedItemId());
			     	  	
			     }else{
			     	  alert("请选择对应的元数据!");
			     	  return;
			     	
			     }
			     
			     
			   }

			
			
			}
			
			function selectFirstNode(){
				if (tree.hasChildren('root')) {
					tree.selectItem(tree.getSubItems('root').split(',')[0], true);
				} 
			
			}
			
			
			
			
			
			function btn_Toggle_Click() {		  	
				if ($('#treeZone').is(':hidden')) {
					$('#treeZone').show();
					$('#toggleBar').attr('src', '${ctx}/image/click_left.gif');
				} else {
					$('#treeZone').hide();
					$('#toggleBar').attr('src', '${ctx}/image/click_right.gif');
				}
			}
			
			function btn_updown_Click(){
				if ($('#searchKeyDiv').is(':hidden')) {
					$('#searchKeyDiv').show();
					$('#clickTop').attr('src', '${ctx}/image/click_top.gif');
				} else {
					$('#searchKeyDiv').hide();
					$('#clickTop').attr('src', '${ctx}/image/click_down.gif');
				}
			
			}
			
			/**
			*
			**/
			function changeDeptByRegion(elm){
			       // alert("dd"+elm.value);
					$.ajax({
						url: rootPath + '/EMRSearchServlet?method=getDeptsByRegion&regionCode='+elm.value,
						async: true,
						cache : false,
						traditional : true,
						type : 'post',
						dataType : 'text',
						error:function(){
							alert("科室加载失败！");
						},
						beforeSend: beforeLoadDept,
						success: afterLoadDept
					});	
			}
			 
			 function beforeLoadDept(){
			 	return true;
			 }
			 
			 function afterLoadDept(result){
			 	//alert("result======="+result);
			 	$('#deptCode').attr("innerHTML","")
				$('#deptCode').append(result);
			 
			 }

			/**
			**
			**/
			function changeStationByDept(elm){
				$.ajax({
						url: rootPath + '/EMRSearchServlet?method=getStationsByDept&deptCode='+elm.value,
						async: true,
						cache : false,
						traditional : true,
						type : 'post',
						dataType : 'text',
						error:function(){
							alert("病区加载失败！");
						},
						beforeSend: beforeLoadStation,
						success: afterLoadStation
					});	
			}
			
			function beforeLoadStation(){
				return true;
			}
			
			function afterLoadStation(result){
			 	//alert("result======="+result);
			 	$('#stationCode').attr("innerHTML","")
				$('#stationCode').append(result);
			 
			 }
			 
			 function addRow(){
			    //
			  var id="searchtab";
			 	var timestamp=new Date().getTime();
				var row='<tr id="tr_'+timestamp+'">';
					//1,meta
					row+='<td id="metaDataTd_'+timestamp+'">';
					  row+='<input type="hidden" value="" id="metaCode" name="metaCode" />';
						row+='<input id="metaData" name="metaData" style="width:100%" onclick="chooseMetaData(this)"/>';
					row+='</td>';
					//2,op
					row+='<td id="opTd_'+timestamp+'">';
						row+='<select id="op" name="op" style="width:100px;"><option value="EQ">等于</option><option value="BQ">大于等于</option><option value="LQ">小于等于</option><option value="B">大于</option><option value="L">小于</option></select>';
					row+='</td>';
					//3.search value
					row+='<td id="searchValueTd_'+timestamp+'">';
						row+='<input id="searchValue" name="searchValue" style="width:100%"></>';
					row+='</td>';
					
					//4.连接关系;
					row+='<td id="joinTd_'+timestamp+'">';
						row+='<select id="join" name="join" style="width:100px;"><option value="AND">并且</option><option value="OR">或者</option><option value="NOT">不包括</option></select>';
					row+='</td>';
					
					//操作
					row+='<td id="btnTd_'+timestamp+'">';
						row+='<input name="btn_delete" id="btn_delete" type="button" class="button_content" value="删除" onclick="deleteRow('+timestamp+')"/>';
					row+='</td>';
					//
					row+='</tr>';
					
					$('#'+id).append(row);
					
					//取metaDataTd_'+timestamp+'下的metaData元数据对象;
					var currentTdObj=$('#metaDataTd_'+timestamp);
					var currentMetaDate=currentTdObj.children("#metaData");
					//设置焦点
					currentMetaDate.focus();
					
					//alert("currentMetaDate++"+currentMetaDate.attr('id'));
					
					//选择当前元数据
					chooseMetaData(currentMetaDate);
					
			 
			 }
			 
			 function deleteRow(id){
			 	//alert("id"+id);
				var theTr='tr_'+id;
				$('#'+theTr).remove();
			 
			 }
				
			
			
		</script>

<style>
html,body{
	height:100%;
}
</style>

	</head>
	<body style="background-color:#a1dce6;">
   <form id="search_form" name="search_form" method="post" action="${ctx}/EMRSearchServlet?method=doSearch1" target="searchResult1">
   	
   <input type="hidden" name="checkedEMRForms" id="checkedEMRForms" value="" />
    <input type="hidden" name="toPage" id="toPage" />
	<div class="content_title" style="padding-left:10px;" align="left">
			当前位置：电子病历 &gt;电子病历高级查询
		</div>
	<table width="100%" height="100%">
		<tr>
				<td width="20%" valign="top" id="treeZone">
                <div class="contentModuleB">
                	   <table width="100%" class="DoubleColorTable">
                       	<tr>
								<th align="left" colspan="8">
									<span class="blue">
										查询元数据范围：
									</span>
								</th>
						</tr>
                        <tr  >
                        	<td>
                            	<div id="categoryTree" style="width:100%; height:250px;" >
									
                                    
								</div>
                            </td>
                        
                        </tr>
                        
                        	<tr>
								<th align="left" colspan="8">
									<span class="blue">
										查询病历范围：
									</span>
								</th>
						</tr>
                        <tr>
                        	<td>
                            	<div id="emrCategoryTree" style="width:100%; height:350px">
                                  
                        		</div>
                            </td>
                        
                        </tr>
                 </table>
                 </div>  
                     
				</td>
				<td style="background:url(${ctx}/image/bg_click.gif) repeat-y right; width: 5px; " rowspan="2">
					<a id="btn_Toggle" href="#"><img id="toggleBar" name="toggleBar" src="${ctx}/image/click_left.gif" /> </a>
				</td>
				<td valign="top"  id="searchKeyTD">
                <div id="searchKeyDiv" >
					<table width="100%" class="contentModule">
                    <tr>
					<td>
                    <input name="btn_new" id="btn_new" type="button" class="button_content" value="新建查询" onClick="doNew()"/>
                     <input name="btn_search" id="btn_search" type="button" class="button_content" value="查询" onClick="doSearchPage();"/>
					<div class="contentModuleB">
                     
                       <table width="100%" cellspacing="1" class="DoubleColorTable">
                       	<tr>
								<th align="left" colspan="9">
									<span class="blue">
										查询条件设置：
									</span>
								</th>
						</tr>
						<tr>
								<td align="right" width="10%">
									 查询区间:
								</td>
                                <td align="left" width="15%">
                                	<input name="startDate" id="startDate" class="input" value="<c:out value='${startDate}'/>"
										size="10" maxlength="10"/><img src="${ctx}/image/calender.gif"
										alt="" name="change1" align="middle"
										id="change3" style="cursor: hand" />&nbsp;至&nbsp;<input name="endDate" id="endDate" class="input" value="<c:out value='${endDate}'/>"
										size="10" maxlength="10"/><img src="${ctx}/image/calender.gif"
										alt="" name="change2" align="middle"
										id="change3" style="cursor: hand" />
                                </td>
                                <td align="right" width="8%">
                                   区域:
                                </td>
                                <td align="left" width="10%">
                                   <select name="regionCode" id="regionCode" onChange="changeDeptByRegion(this);">
                                   	 <c:out value='${regionList}' escapeXml='false' />
                                   </select>
                                 </td>
                                 <td align="right" width="8%">
                                   科室:
                                </td>
                                <td align="left" width="15%">
                                   <select name="deptCode" id="deptCode" onChange="changeStationByDept(this);">
                                   	<option value=''>----请选择----</option>
                                   </select>
                                 </td>
                                  <td align="right" width="8%">
                                   病区:
                                </td>
                                <td align="left" width="15%">
                                   <select name="stationCode" id="stationCode">
                                   	<option value=''>----请选择----</option>
                                   </select>
                                 </td>
                                 <td>&nbsp;
                                 
                                 </td>
                                
						</tr>
                        </table>
                        </div>
                        </td>
                        </tr>
					</table>
        
                    <table width="100%" class="contentModule">
                    <tr>
					<td>
                   
                    <div class="contentModuleB">
                        <table cellspacing="1" class="DoubleColorTable" width="100%">
                        <tr>
                            <th  align="left" width="100%">
                                <span class="blue">
                                    元数据条件设置： <input name="btn_add" id="btn_add" type="button" class="button_content" value="新增条件" onClick="addRow();"/>
                                 </span>
            
                            </th>
                        </tr>
                        <tr>
                        	<td>
                            	<table   id="searchtab"  width="100%">
                                	<tr>
                                    	<td width="30%">
                                             元数据条件
                                        </td>
                                        <td width="10%">
                                             逻辑条件
                                        </td >
                                        <td  width="30%">
                                             查询值
                                        </td>
                                         <td width="10%">
                                             连接条件
                                        </td>
                                         <td width="20%">
                                             操作
                                        </td>
                                	</tr>
                                    
                                    <tr>
                                    	<td id="metaDataTd_1">
                                    		<input type="hidden" value="" id="metaCode" name="metaCode" />
                                            <input type="text" value="" id="metaData" name="metaData" style="width:100%" onClick="chooseMetaData(this)"/>
                                        </td>
                                        <td id="opTd_1">
                                             <select id="op" name="op" style="width:100px;">
                                             	<option value="EQ">等于</option>
                                                <option value="BQ">大于等于</option>
                                                <option value="LQ">小于等于</option>
                                                <option value="B">大于</option>
                                                 <option value="L">小于</option>
                                             </select>
                                        </td>
                                        <td  id="searchValueTd_1">
                                              <input type="text" value="" id="searchValue" name="searchValue" style="width:100%"/>
                                        </td>
                                         <td id="joinTd_1">
                                             <select id="join" name="join" style="width:100px;">
                                             	<option value="">请选择</option>
                                             	<option value="AND">并且</option>
                                                <option value="OR">或者</option>
                                                <option value="NOT">不包括</option>
                                             </select>
                                        </td>
                                        <td id="btnTd_1">&nbsp;                                         
                                           
                                        </td>
                                	</tr>
                                    
                                    
                                </table>
                            </td>
                        </tr>
                        </table>
                    </div>
                    </td>
                    </tr>
                   </table> 
                  </div>
                  <table width="100%" border="0">
                  	<tr>
                    	<td style="background:url(${ctx}/image/bg_click.gif) repeat-x rigth; height: 5px;" align="center"> 
                        	<a id="btn_top" href="#"><img id="clickTop" name="clickTop" src="${ctx}/image/click_top.gif" /> </a>
                        </td>
                    </tr>
                  </table>
                    <div class="contentModuleB">
                   <table id="searchResulttab" cellspacing="1" class="DoubleColorTable" width="100%">
                        <tr>
                            <th  align="left" width="100%">
                                <span class="blue">
                                    	浏览方式:  <input type="radio" name="radio" id="fileType" value="PDF" checked/>PDF &nbsp;<input type="radio" name="radio" id="fileType" value="HTML" />HTML&nbsp;<input type="radio" name="radio" id="fileType" value="JHW" />JHW
                                 </span>        
                            </th>
                        </tr>
                        <tr>
                            <th  align="left" width="100%">
                                <span class="blue">
                                    查询结果：
                                 </span>        
                            </th>
                        </tr>
                        
                        <tr>
                        	<td  id="searchResult" style="height:400">
                        		<iframe name="searchResult1" id="searchResult1" src="" height="100%" width="100%"></iframe>
                          	</td>
                        
                        </tr>
                                               
                   </table>
                   </div>            
				</td>              
		</tr>
			
	</form>	
	</table>
	</body>
    
    <script type="text/javascript">
		    Calendar.setup({
		        inputField     :    "startDate",      // id of the input field
		        ifFormat       :    "%Y-%m-%d",       // format of the input field
		        showsTime      :    true,            // will display a time selector
		        button         :    "change1",   // trigger for the calendar (button ID)
		        singleClick    :    true,           // double-click mode
		        step           :    1            // show all years in drop-down boxes (instead of every other year as default)
		    });
	</script>
    
    <script type="text/javascript">
		    Calendar.setup({
		        inputField     :    "endDate",      // id of the input field
		        ifFormat       :    "%Y-%m-%d",       // format of the input field
		        showsTime      :    true,            // will display a time selector
		        button         :    "change2",   // trigger for the calendar (button ID)
		        singleClick    :    true,           // double-click mode
		        step           :    1            // show all years in drop-down boxes (instead of every other year as default)
		    });
	</script>
	
</html>
		
		
		