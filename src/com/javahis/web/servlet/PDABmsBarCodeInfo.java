package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

//输血安全核查pda - Servlet

public class PDABmsBarCodeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDABmsBarCodeInfo() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		TParm resultPat = new TParm();
		TParm resultBms = new TParm();
		TParm resultBlo = new TParm();
		String roomNo = request.getParameter("roomNo");
		String BarCode = request.getParameter("BarCode");
		// 术间执行输血(查询病患)
		String sqlPat = "SELECT D.PAT_NAME,D.SEX_CODE,D.BIRTH_DATE,E.CHN_DESC,D.BLOOD_TYPE || ' ' || D.BLOOD_RH_TYPE AS BLD_TYPE "
				+ " FROM BMS_BLOOD A,OPE_OPBOOK B,ADM_INP C ,SYS_PATINFO D,SYS_DICTIONARY E "
				+ " WHERE  A.CASE_NO = C.CASE_NO "
				+ " AND A.MR_NO = D.MR_NO "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND E.GROUP_ID = 'SYS_SEX'"
				+ " AND D.SEX_CODE = E.ID "
				+ " AND A.BLOOD_NO='"
				+ BarCode
				+ "' " + " AND B.ROOM_NO ='" + roomNo + "' ";
		// System.out.println("=======getBmsandPatQuery======="+sql);
		resultPat = new TParm(TJDODBTool.getInstance().select(sqlPat));

		String sqlBms = "SELECT B.BLDCODE_DESC||A.BLOOD_VOL||D.UNIT_CHN_DESC AS BLDCODE_DESC," 
//				+ " A.BLOOD_VOL AS FACT_VOL, "
				+ " CASE WHEN A.FACT_VOL IS NULL THEN A.BLOOD_VOL WHEN A.FACT_VOL IS NOT NULL THEN TO_NUMBER(A.FACT_VOL) END AS FACT_VOL,"
				+ " D.UNIT_CHN_DESC,A.BLOOD_NO "
				+ ",A.RECEIVED_USER,F.USER_ID,F.USER_NAME,A.RECEIVED_DATE,A.BLD_TYPE || ' ' || A.RH_FLG AS BLD_TYPE,"
				+ " CASE A.RESULT WHEN  '1' THEN '相合'  WHEN  '2' THEN '相斥'   ELSE NULL END AS  RESULT  ,F.USER_PASSWORD "
				+ " ,A.TRANSFUSION_REACTION "//20170401 lij add 输血反应
				+ " FROM BMS_BLOOD A,BMS_BLDCODE B,SYS_DICTIONARY C ,SYS_UNIT D,OPE_OPBOOK E,SYS_OPERATOR F "
				+ " WHERE A.BLD_CODE=B.BLD_CODE "
				+ " AND A.BLD_TYPE=C.ID "
				+ " AND C.GROUP_ID='SYS_BLOOD' "
				+ " AND B.UNIT_CODE=D.UNIT_CODE "
				+ " AND A.CASE_NO = E.CASE_NO "
				+ " AND A.RECEIVED_USER = F.USER_ID "
				+ " AND A.BLOOD_NO='"
				+ BarCode
				+ "' "
				+ " AND E.ROOM_NO ='"
				+ roomNo
				+ "' "
				+ " AND A.RECEIVED_USER IS NOT NULL "
				+ " AND A.BLDTRANS_END_USER IS NULL  ";

		resultBms = new TParm(TJDODBTool.getInstance().select(sqlBms));
		
		String sqlBlo = " SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'TRANSFUSION_REACTION' ";
		resultBlo = new TParm(TJDODBTool.getInstance().select(sqlBlo));
		
		Map dataPat = resultPat.getData();
		Map dataBms = resultBms.getData();
		Map dataBlo = resultBlo.getData();
		JSONArray arrayPat = JSONArray.fromObject(dataPat);
		JSONArray arrayBms = JSONArray.fromObject(dataBms);
		JSONArray arrayBlo = JSONArray.fromObject(dataBlo);
		JSONObject obj = new JSONObject();
		obj.put("PatInfo", arrayPat);
		obj.put("bmsInfo", arrayBms);
		obj.put("bloInfo", arrayBlo);
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
