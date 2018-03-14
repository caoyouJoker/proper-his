package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import jdo.sys.SystemTool;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.web.bean.DropDownList;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import com.javahis.web.jdo.EMRSearchTool;
import com.javahis.web.util.CommonUtil;
import com.sun.mail.handlers.message_rfc822;
import com.sun.media.ui.MessageBox;

//介入安全核查pda - Servlet

public class PDAOpeInter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeInter() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		
		String roomNo = request.getParameter("roomNo");
        
        //病案号
        String mr_no = request.getParameter("mrNo");
        //日期
        //String dateStr = request.getParameter("dateStr");
        
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyyMMdd");
        String dateStr=format.format(date);
        
        
        
    	String sql = "SELECT A.CASE_NO,A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
			+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
			+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
			+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
			+ " (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE   B.GROUP_ID = 'OPE_OPROOM' "
			+ " AND A.ROOM_NO = B.ID(+)) AS  OP_ROOM, C.BIRTH_DATE ,ROUND(MONTHS_BETWEEN(sysdate,C.BIRTH_DATE)/12) AS AGE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
			+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
			+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
			+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,X.DEPT_CHN_DESC,Y.STATION_DESC,"
			+ "(SELECT Z.CHN_DESC FROM SYS_DICTIONARY Z  WHERE   Z.GROUP_ID = 'OPE_SITE'  AND A.PART_CODE = Z.ID(+)) AS  PART_CODE"
			//fux modify 20151029 
			+ " ,A.GDVAS_CODE,A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,O.ALLERGY  "  
			+ "FROM OPE_OPBOOK A,SYS_PATINFO C,SYS_DEPT X,SYS_STATION Y,SYS_DICTIONARY F,"  
			+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"    
			+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N ,ADM_INP O "
			+ "WHERE  A.MR_NO = C.MR_NO (+) "
			+ "AND A.OP_DEPT_CODE=X.DEPT_CODE "
			+ "AND A.OP_STATION_CODE=Y.STATION_CODE "
			+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "
			+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   "
			+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
			+ "AND A.MAIN_SURGEON = I.USER_ID(+) "
			+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
			+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
			+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "  
			+ "AND A.ANA_USER1 = M.USER_ID(+) "
			+ "AND A.EXTRA_USER1 = N.USER_ID(+) " +
			  "AND A.CASE_NO = O.CASE_NO(+) "
			+ "AND A.MR_NO='"
			+ mr_no
			+ "' "
			+ "AND OP_DATE BETWEEN "
			+ "TO_DATE('"
			+ dateStr
			+ " 00:00:00','yyyymmdd hh24:mi:ss')"
			+ "AND "
			+ "TO_DATE('"
			+ dateStr
			+ " 23:59:59','yyyymmdd hh24:mi:ss')"
			//fux modify 20160523   如果手术申请重复了  倒排序 取最近的一条数据
			//根据iproom 还要判断不同手术类型 
			+ "AND A.CANCEL_FLG <> 'Y' " + "ORDER BY OPBOOK_SEQ DESC ";
        
        
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));  
		
	    Map data = result.getData();
	    
		JSONArray array = JSONArray.fromObject(data);
    	
        JSONObject obj = new JSONObject();
        obj.put("OpeInterJson", array);  
		out.print("callback("+obj.toString()+")");       
		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {

	}


}
