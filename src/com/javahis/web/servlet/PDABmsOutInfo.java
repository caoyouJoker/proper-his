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

//血袋接收pda - Servlet

public class PDABmsOutInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDABmsOutInfo() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 出库单 回车
		String OutNo = request.getParameter("OutNo");
		String sql = "SELECT COUNT(A.BLOOD_NO) AS NUM,C.CHN_DESC || ' ' || B.BLOOD_RH_TYPE AS BLD_TYPE,A.MR_NO,B.PAT_NAME "
				+ " FROM BMS_BLOOD A,SYS_PATINFO B,SYS_DICTIONARY C "
				+ " WHERE A.MR_NO=B.MR_NO  AND B.BLOOD_TYPE=C.ID AND C.GROUP_ID='SYS_BLOOD' AND A.OUT_NO='"
				+ OutNo
				+ "' GROUP BY A.MR_NO,B.PAT_NAME,C.CHN_DESC,B.BLOOD_RH_TYPE ";
		// System.out.println("============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		String resql = "SELECT A.BLOOD_NO "
				+ " FROM BMS_BLOOD A,SYS_PATINFO B,SYS_DICTIONARY C "
				+ " WHERE A.MR_NO=B.MR_NO  AND B.BLOOD_TYPE=C.ID AND C.GROUP_ID='SYS_BLOOD' AND A.OUT_NO='"
				+ OutNo + "' AND RECEIVED_USER IS NOT NULL";
		// System.out.println("======resql======="+resql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(resql));
		// 已经接收数量RNUM
		result.setData("RNUM", 0, parm.getCount() <= 0 ? 0 : parm.getCount());

		Map data = result.getData();

		JSONArray array = JSONArray.fromObject(data);

		JSONObject obj = new JSONObject();

		// 血品科室核收 barcode 回车
		TParm resultBar = new TParm();
		String BarCode = request.getParameter("BarCode");
		String checksql = "SELECT B.BLDCODE_DESC,A.BLOOD_VOL,A.BLOOD_NO,C.CHN_DESC || ' ' || A.RH_FLG AS BLD_TYPE,RECEIVED_USER "
				+ " FROM BMS_BLOOD A,BMS_BLDCODE B,SYS_DICTIONARY C "
				+ " WHERE A.BLD_CODE=B.BLD_CODE AND A.BLD_TYPE=C.ID AND C.GROUP_ID='SYS_BLOOD' "
				+ " AND RECEIVED_DATE IS NULL  "
				+ " AND RECEIVED_USER IS NULL "
				+ " AND A.OUT_NO='"
				+ OutNo
				+ "' AND A.BLOOD_NO='" + BarCode + "'";
		// 不带出库号 的血品基本信息
		// String sql =
		// "SELECT B.BLDCODE_DESC,A.BLOOD_VOL||D.UNIT_CHN_DESC AS BLOOD_VOL,A.BLOOD_NO,C.CHN_DESC || ' ' || A.RH_FLG AS BLD_TYPE "
		// + " FROM BMS_BLOOD A,BMS_BLDCODE B,SYS_DICTIONARY C,SYS_UNIT D "
		// +
		// " WHERE A.BLD_CODE=B.BLD_CODE AND A.BLD_TYPE=C.ID AND C.GROUP_ID='SYS_BLOOD'"
		// + " AND B.UNIT_CODE=D.UNIT_CODE AND A.BLOOD_NO='" + barCode + "'";
		// System.out.println("=======checksql======="+checksql);
		resultBar = new TParm(TJDODBTool.getInstance().select(checksql));
		Map dataBar = resultBar.getData();

		JSONArray arrayBar = JSONArray.fromObject(dataBar);

		obj.put("bmsOutInfo", array);

		obj.put("bmsBarInfo", arrayBar);

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
