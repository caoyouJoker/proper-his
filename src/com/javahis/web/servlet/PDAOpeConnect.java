package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

//手术室交接pda - Servlet
public class PDAOpeConnect extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeConnect() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/** modified by WangQing 20170410，打开数据库连接 */
		Server.autoInit(this);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		String roomNo = request.getParameter("roomNo");

		//
		String sql = " SELECT B.CASE_NO,B.MR_NO,C.PAT_NAME,C.SEX_CODE,D.CHN_DESC,C.BIRTH_DATE,F.DEPT_CHN_DESC "
				+ " FROM OPE_IPROOM A, OPE_OPBOOK B,SYS_PATINFO C,SYS_DICTIONARY D,ADM_INP E,SYS_DEPT F   "
				+
				// --,SYS_DEPT
				" WHERE A.ROOM_NO = '"
				+ roomNo
				+ "' AND A.OPBOOK_SEQ = B.OPBOOK_SEQ "
				+ " AND B.MR_NO = C.MR_NO  AND D.GROUP_ID = 'SYS_SEX' "
				+ " AND C.SEX_CODE = D.ID AND B.MR_NO = E.MR_NO AND E.DEPT_CODE = F.DEPT_CODE ";
		// System.out.println("fux_________sql:" + sql);
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		Map data = result.getData();

		JSONArray array = JSONArray.fromObject(data);

		request.setAttribute("OpePatConnectJson", "callback" + "("
				+ array.toString() + ")");

		// 病案号
		String mr_no = request.getParameter("mrNo");
		// 日期
		// String dateStr = request.getParameter("dateStr");

		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateStr = format.format(date);

		// liuyalin 20170405 modify
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		int day1 = c1.get(Calendar.DATE);
		c1.set(Calendar.DATE, day1 - 1);
		String dateStr1 = new SimpleDateFormat("yyyyMMdd").format(c1.getTime());

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		int day2 = c2.get(Calendar.DATE);
		c2.set(Calendar.DATE, day2 + 1);
		String dateStr2 = new SimpleDateFormat("yyyyMMdd").format(c2.getTime());

		String sql2 = "SELECT A.CASE_NO,A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ " (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE   B.GROUP_ID = 'OPE_OPROOM' "
				+ " AND A.ROOM_NO = B.ID(+)) AS  OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,X.DEPT_CHN_DESC,Y.STATION_DESC,"
				+ "(SELECT Z.CHN_DESC FROM SYS_DICTIONARY Z  WHERE   Z.GROUP_ID = 'OPE_SITE'  AND A.PART_CODE = Z.ID(+)) AS  PART_CODE"
				+
				// fux modify 20151029
				" ,A.GDVAS_CODE,A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,O.ALLERGY,A.TRANSFER_USER,A.HANDOVER_USER  "
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
				+ "AND A.EXTRA_USER1 = N.USER_ID(+) "
				+ "AND A.CASE_NO = O.CASE_NO(+) "
				+ "AND A.MR_NO='"
				+ mr_no
				+ "' "
				+ "AND OP_DATE BETWEEN "
				// + "TO_DATE('"
				// + dateStr
				// + " 00:00:00','yyyymmdd hh24:mi:ss')"
				// + "AND "
				// + "TO_DATE('"
				// + dateStr
				// + " 23:59:59','yyyymmdd hh24:mi:ss')"

				// liuyalin 20170405 modify
				+ "TO_DATE('"
				+ dateStr1
				+ " 00:00:00','yyyymmdd hh24:mi:ss')"
				+ "AND "
				+ "TO_DATE('"
				+ dateStr2
				+ " 23:59:59','yyyymmdd hh24:mi:ss')"
				// fux modify 20160523 如果手术申请重复了 倒排序 取最近的一条数据
				// 根据iproom 还要判断不同手术类型
				+ " AND A.ROOM_NO= '"
				+ roomNo
				+ "' "
				+ " AND A.CANCEL_FLG <> 'Y' " + "ORDER BY OPBOOK_SEQ DESC ";

		// System.out.println("fux_________sql2:" + sql2);
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));

		Map data2 = result2.getData();

		JSONArray array2 = JSONArray.fromObject(data2);

		// fux modify 加入 字段 过敏备注 ALLERGIC_MARK
		String sql3 = " SELECT  A.AGE,A.BED,A.FROM_DEPT AS FROM_DEPT1 ,A.TO_DEPT AS TO_DEPT1,B.DEPT_CHN_DESC AS TO_DEPT,"
				+ " C.DEPT_CHN_DESC AS FROM_DEPT,TO_CHAR(A.TRANSFER_DATE,'YYYY-MM-DD HH:MM:SS') AS TRANSFER_DATE,A.TEMPERATURE,PULSE,"
				+ " A.RESPIRE,A.SBP AS SYSTOLICPRESSURE, A.DBP AS DIASTOLICPRESSURE,A.ACTIVE_TOOTH_FLG,A.FALSE_TOOTH_FLG,"
				+ " A.GENERAL_MARK,A.ALLERGIC_FLG,A.ALLERGIC_MARK,A.INFECT_FLG,A.WEIGHT AS WEIGHT_MON,A.SKIN_BREAK_FLG,"
				+ " A.SKIN_BREAK_POSITION,A.BLOOD_TYPE,A.RHPOSITIVE_FLG,A.CROSS_MATCHUY AS CROSS_MATCH ,A.OPE_PRE_MARK,"
				+ " A.OPE_INFORM_FLG,A.ANA_SINFORM_FLG,A.BLOOD_INFORM_FLG,A.SKIN_PREPARATION_FLG,A.CROSSMATCH_FLG,"
				+ " A.SKIN_TEST_FLG,A.PREPARE_EDUCATION_FLG,A.BOWEL_PREPARATION_FLG,A.DENTAL_CARE_FLG,A.NASAL_CARE_FLG,"
				+ " A.INFECT_SCR_RESULT_CONT,A.RHPOSITIVE_FLG,A.ALLERGIC_MARK "
				+ " FROM  INW_TransferSheet_WO A,SYS_DEPT B,SYS_DEPT C ,OPE_OPBOOK D   WHERE  A.MR_NO= '"
				+ mr_no
				+ "' "
				+ " AND A.TRANSFER_DATE BETWEEN "
				// + "TO_DATE('"
				// + dateStr
				// + " 00:00:00','yyyymmdd hh24:mi:ss')"
				// + "AND "
				// + "TO_DATE('"
				// + dateStr
				// + " 23:59:59','yyyymmdd hh24:mi:ss')"

				// liuyalin 20170405 modify
				+ "TO_DATE('"
				+ dateStr1
				+ " 00:00:00','yyyymmdd hh24:mi:ss') "
				+ "AND "
				+ "TO_DATE('"
				+ dateStr2
				+ " 23:59:59','yyyymmdd hh24:mi:ss') "
				+ " AND A.TO_DEPT = B.DEPT_CODE "
				+ " AND A.FROM_DEPT = C.DEPT_CODE "
				+ " AND D.ROOM_NO = '"
				+ roomNo + "' " + " " + " AND A.OPBOOK_SEQ = D.OPBOOK_SEQ ";

		// System.out.println("fux_________sql3:" + sql3);
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));

		Map data3 = result3.getData();

		JSONArray array3 = JSONArray.fromObject(data3);

		request.setAttribute("OpeConnectOpJson", "callback" + "("
				+ array2.toString() + ")");

		JSONObject obj = new JSONObject();
		obj.put("OpePatConnectJson", array);
		obj.put("OpeConnectOpJson", array2);
		obj.put("OpeConnectOpInwJson", array3);
		out.print("callback(" + obj.toString() + ")");
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
