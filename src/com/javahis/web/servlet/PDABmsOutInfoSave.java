package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdo.sys.SystemTool;
import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;

//血袋接收pda - Servlet --- 插入 ope_bms_statues表

public class PDABmsOutInfoSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDABmsOutInfoSave() {
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
		String roomNo = request.getParameter("roomNo");
		// System.out.println("roomNo:" + roomNo);
		String BarCode = request.getParameter("BarCode");
		String AcceptCode = request.getParameter("EXEC_USER");
		Map map = new HashMap();

		TParm result = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		// fux modify 20161116 BLDTRANS_TIME 输血开始

		// System.out.println("UPDATE======onBmsDeptAccept========="+sql);

		String sqlResult = " SELECT RECEIVED_USER FROM BMS_BLOOD  WHERE BLOOD_NO='"
				+ BarCode + "'  ";
		result = new TParm(TJDODBTool.getInstance().select(sqlResult));
		// 实例化map对象
		
//		if (result.getCount("RECEIVED_USER") > 0) {
//			map.put("request", "此血品已核收！");
//		} 
		//20170406 lij 改
		if(!StringUtils.isEmpty(result.getValue("RECEIVED_USER",0))){
			map.put("request", "此血品已核收！");
		}else {
			String sql = "UPDATE BMS_BLOOD SET BLDTRANS_TIME=TO_DATE('" + time
					+ "','yy/mm/dd hh24:mi:ss'), RECEIVED_DATE=TO_DATE('"
					+ time + "','YYYYMMDDHH24MISS')," + "RECEIVED_USER='"
					+ AcceptCode + "',BLDTRANS_USER ='" + AcceptCode
					+ "' WHERE BLOOD_NO='" + BarCode + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			// 插入ope_bms_statues表
			// ROOM_NO
			// BLOOD_NO
			// OPBOOK_SEQ
			// STATUES
			// OPT_USER
			// OPT_TERM
			// OPT_DATE
			// INSERT INTO Persons (LastName, Address) VALUES ('Wilson',
			// 'Champs-Elysees')
			// 1是核收状态
			String sqlInsert = " INSERT INTO OPE_BMS_STATUES (ROOM_NO,BLOOD_NO,OPBOOK_SEQ,STATUES,OPT_USER,OPT_TERM,OPT_DATE)"
					+ " VALUES ('"
					+ roomNo
					+ "', '"
					+ BarCode
					+ "','','1','"
					+ AcceptCode
					+ "','127.0.0.1',TO_DATE('"
					+ time
					+ "','yy/mm/dd hh24:mi:ss')) ";
			TParm resultInsert = new TParm(TJDODBTool.getInstance().update(
					sqlInsert));
			if (result.getErrCode() < 0 || resultInsert.getErrCode() < 0) {
				// 实例化map对象
				map.put("request", "保存失败！");
				JSONArray array = JSONArray.fromObject(map);
				JSONObject obj = new JSONObject();
				obj.put("bmsBarInfoSave", array);
				// 返回 一个 保存成功 字样的 json串
				out.print("callback(" + obj.toString() + ")");
				out.flush();
				out.close();
				return;
			}
			map.put("request", "保存成功！");
		}

		JSONArray array = JSONArray.fromObject(map);
		JSONObject obj = new JSONObject();
		obj.put("bmsBarInfoSave", array);
		// 返回 一个 保存成功 字样的 json串
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
