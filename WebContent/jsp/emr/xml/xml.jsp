<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="../include.jsp"%>
<html xmlns:v="urn:schemas-microsoft-com:vml">
	<head>
		<STYLE>
			v\: * { Behavior: url(#default#VML) }
		</STYLE>
		<script type="text/javascript">
			/**
			 * 构造VML构造图像类；
			 */
			function VmlData(elemID,vmlObject)
			{
				//追加的元素名称；
				this.elemID=elemID;
				//需追加的vml对象；
				this.vmlObject=vmlObject;
			}
			
			var drawArray = new Array();
			/**
			 * 画线方法；
			 */
	     	function drawline(fromX, fromY, widthX, heightY,elemID)
	     	{
				var toX=parseInt(fromX)+parseInt(widthX);
	     		var toY=parseInt(fromY)+parseInt(heightY);
	     		//alert("===toX==="+toX);
	     		//alert("===toY==="+toY);
	     		var strElement = "<v:Line from='" + fromX + "," + fromY + "' to='" + toX + "," + toY + "' strokecolor='black'>";
			    strElement += "</v:Line>";
				var newPoint = document.createElement(strElement);
				//var elemID=document.getElementById(elemID);
				var vmlData=new VmlData();
				vmlData.elemID=elemID;
				vmlData.vmlObject=newPoint;
				//放置到VML对像数组中
				drawArray.push(vmlData);
				//elemID.appendChild(newPoint);
	     	}
	     
	     	/**
			 * 画线方法(GLINE)；
			 */
			function drawChildLine(fromParentX,fromParentY,fromX, fromY, widthX, heightY,elemID)
			{
				//
				fromX=parseInt(fromParentX)+parseInt(fromX);
				fromY=parseInt(fromParentY)+parseInt(fromY);
				var toX=parseInt(fromX)+parseInt(widthX);
				var toY=parseInt(fromY)+parseInt(heightY);
				//alert("===toX==="+toX);
				//alert("===toY==="+toY);
				var strElement = "<v:Line from='" + fromX + "," + fromY + "' to='" + toX + "," + toY + "' strokecolor='black'>";
		        strElement += "</v:Line>";
		        var newPoint = document.createElement(strElement);
				//var elemID=document.getElementById(elemID);
				var vmlData=new VmlData();
				vmlData.elemID=elemID;
				vmlData.vmlObject=newPoint;
				//放置到VML对像数组中
				drawArray.push(vmlData);
		        //elemID.appendChild(newPoint);
	     	}
	     
	     	/**
			 * 画矩形方法eimageID
			 */
			function drawRect(x,y,width,height,elemID)
			{
				//alert("elemID+++"+elemID);
				//alert("x"+x);
	     		//alert("y"+y);
	     		//alert("width"+width);
	     		//alert("height"+height);
				var strElement3 = "<v:Rect style='left:"+x+"px;top:"+y+"px;width:"+width+"px;height:"+height+"px;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=100,finishOpacity=50);'></v:Rect>";
				var newPoint3 = document.createElement(strElement3);
	       		//newPoint3.fillColor="#FF0000";
	       		//newPoint3.Ael("fill");
	       		//d.opacity=0.1;
	       		//var elemID=document.getElementById(elemID);
	       		//elemID.appendChild(newPoint3);
	       		var vmlData=new VmlData();
				vmlData.elemID=elemID;
				vmlData.vmlObject=newPoint3;
				//放置到VML对像数组中
				drawArray.push(vmlData);
			} 
	    
			/**
			 * 画矩形方法；eimageID下的子元素GBLOCK；
			 */
			function drawChildRect(parentX,parentY,x,y,width,height,elemID)
			{
				x=parseInt(parentX)+parseInt(x);
				y=parseInt(parentY)+parseInt(y);	
				var strElement3 = "<v:Rect style='left:"+x+"px;top:"+y+"px;width:"+width+"px;height:"+height+"px;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=100,finishOpacity=50);'></v:Rect>";
				var newPoint3 = document.createElement(strElement3);
	       		var vmlData=new VmlData();
				vmlData.elemID=elemID;
				vmlData.vmlObject=newPoint3; 
				//放置到VML对像数组中
				drawArray.push(vmlData);
			} 
	    
			/**
			 * 最后将VML对象，每个VML绘图；
			 */
			function draws()
			{
				//alert(drawArray.length);
				for(var i=0;i<drawArray.length;i++)
				{
					var elemID=document.getElementById(drawArray[i].elemID);
					elemID.appendChild(drawArray[i].vmlObject);
				}
			}
			
			/**
			 * 初始化调用
			 */
			function init() 
			{
				var xmlPath="EMRWebViewFileServlet?VIEW_TYPE=XML&xmlPath=${xmlPath}";
				var xslPath="EMRWebViewFileServlet?VIEW_TYPE=XSL&xslPath=${xslPath}";
				var xml;
				var xsl;
				//第一行创建空的微软 XML 文档对象
				if(typeof window.ActiveXObject != 'undefined') {
				    xml = new ActiveXObject("Microsoft.XMLDOM");
				    xsl = new ActiveXObject("Microsoft.XMLDOM");
				} else if(document.implementation && document.implementation.createDocument) {    //mozilla
				    xml = document.implementation.createDocument("", "", null);
				    xsl = document.implementation.createDocument("", "", null);
				}
				//ajax动态收到xml提交到servlet动态生成XML输出;
				// Load XML 
				//第二行关闭异步加载，这样可确保在文档完整加载之前，解析器不会继续执行脚本
				//xmlDoc.loadXML(txt) 动态加载XML文件;
				xml.async = false;
				xml.load(encodeURI(encodeURI(xmlPath, "UTF-8")));
				//xml.load("test20110506.xml");
				//xml.loadXML(text);
				// Load XSL
				xsl.async = false;
				xsl.load(xslPath);
				// Transform
				if(typeof window.ActiveXObject != 'undefined')
				{
				    document.write(xml.transformNode(xsl));
				} 
				else if(document.implementation && document.implementation.createDocument) 
				{
					//mozilla
				    var xsltProcessor = new XSLTProcessor();
				    xsltProcessor.importStylesheet(xsl);
				    // transformToDocument方式
				    var result = xsltProcessor.transformToDocument(xml);
				    var xmls = new XMLSerializer();
				    document.write(xmls.serializeToString(result));
				}
				draws();
			}	
		</script>
	</head>
	<body>
		<script>
			init();
		</script>
	</body>
</html>