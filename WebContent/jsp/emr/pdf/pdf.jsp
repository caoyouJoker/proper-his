<%@ page language="java" import="java.util.*,java.io.DataOutputStream,java.io.DataInputStream " pageEncoding="UTF-8"%> 
<%@ page language="java" import="java.io.FileInputStream ,java.io.File "%>
<%@page import="java.io.FileOutputStream"%> 
<HTML>
	<HEAD>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<META http-equiv="Content-Style-Type" content="text/css">
		<META http-equiv="Content-Script-Type" content="text/javascript">
		<TITLE>电子病历浏览</TITLE>
		
    </HEAD>
    <BODY>
    	<%--   <% 

   out.clear();
   out = pageContext.pushBody();
   response.setContentType("application/pdf");

   try {
   
    String strPdfPath = new String("D:/12.pdf");
    //判断该路径下的文件是否存在
    File file = new File(strPdfPath);
    if (file.exists()) {
    
     DataOutputStream temps = new DataOutputStream(new FileOutputStream(strPdfPath) );
     DataInputStream in = new DataInputStream(new FileInputStream(strPdfPath));

     byte[] b = new byte[2048];
     while ((in.read(b)) != -1) {
      temps.write(b);
      temps.flush();
     }

     in.close();
     temps.close();
    } else {
     out.print(strPdfPath + " 文件不存在!");
    }

   } catch (Exception e) {
    out.println(e.getMessage());
   }
%>
--%> 
        
      <DIV id="IfNoAcrobat" style="display:none">
          <a href="http://get.adobe.com/cn/reader/">你需要先安装Adobe Reader才能正常浏览文件，请点击这里下载Adobe Reader.</a>   
      </DIV>
      <OBJECT type="application/pdf" width=0 height=0 style="display:none">
            <DIV id="PDFNotKnown" style="display:none">&nbsp;</DIV>
      </OBJECT>
  	  <DIV id=showdiv style="Z-INDEX: 0; LEFT:10px; WIDTH: 990px; POSITION: absolute; TOP: -8px; HEIGHT: 10px">
		<object classid="clsid:CA8A9780-280D-11CF-A24D-444553540000" width="990" height="700" border="0" top="-10" name="pdf">
			<param name="toolbar" value="false">
			<param name="_Version" value="65539">
			<param name="_ExtentX" value="20108">
			<param name="_ExtentY" value="10866">
			<param name="_StockProps" value="0">
			<param name="SRC" value="${pdfPath}">
		</object>
	</DIV>

</BODY>
</HTML>