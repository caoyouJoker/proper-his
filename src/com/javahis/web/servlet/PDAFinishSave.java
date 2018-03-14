package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

//手术室解除绑定 - Servlet  --- 先查询 ope_bms_statues表 然后删除
public class PDAFinishSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAFinishSave() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	// PDA解除绑定时校验血品安全核查是否进行操作，并进行提示（判断是否用血，用血强制进行血品安全核查后才能解除绑定）
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Map map = new HashMap();
		String roomNo = request.getParameter("roomNo");
		// fux modify 20170214 根据 room_no 查询 statues
		String sqlSelStatues = "SELECT BLOOD_NO FROM  OPE_BMS_STATUES WHERE  ROOM_NO='"
				+ roomNo + "' AND STATUES = '1' ";
		TParm resultSel = new TParm(TJDODBTool.getInstance().select(
				sqlSelStatues));
		// 判断 是否有用血数据
		if (resultSel.getCount("BLOOD_NO") <= 0) {
			// 如果有 则判断安全核查状态
			String sqliproom = "UPDATE OPE_IPROOM SET OPBOOK_SEQ = null"
					+ " WHERE ROOM_NO='" + roomNo + "' ";
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sqliproom.toString()));
			String sqlDelete = "DELETE OPE_BMS_STATUES " + " WHERE ROOM_NO='"
					+ roomNo + "'  ";
			TParm resultDel = new TParm(TJDODBTool.getInstance().update(
					sqlDelete.toString()));
			if (result.getErrCode() < 0 || resultDel.getErrCode() < 0) {
				// 实例化map对象
				map.put("request", "解除绑定失败！");
				JSONArray array = JSONArray.fromObject(map);
				JSONObject obj = new JSONObject();
				obj.put("OpeFinishSaveJson", array);
				// 返回 一个 保存成功 字样的 json串
				out.print("callback(" + obj.toString() + ")");
				out.flush();
				out.close();
				return;
			}
			map.put("request", "解除绑定成功！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpeFinishSaveJson", array);
			// 返回 一个 保存成功 字样的 json串
			out.print("callback(" + obj.toString() + ")");
			out.flush();
			out.close();
		} else {
			String message = "";
			String messStart = "条码号为：" + "\n";
			String messEnd = "血品已核收尚未核查，请完成核查！";
			List<String> success = new ArrayList<String>();
			for (int i = 0; i < resultSel.getCount("BLOOD_NO"); i++) {
				success.add(resultSel.getValue("BLOOD_NO", i));
			}
			String messMidd = "";
			if (success.size() > 0) {
				for (int i = 0; i < success.size(); i++) {
					messMidd = messMidd + success.get(i) + "\n";
				}
			}
			// /第一行\n第二行
			message = messStart + messMidd + messEnd;
			map.put("request", message);

			// for (int i = 0; i < resultSel.getCount("BLOOD_NO"); i++) {
			// map.put("request", "条码号为：" + resultSel.getValue("BLOOD_NO", i)+
			// "血品已核收尚未核查，请完成核查！");
			// }
			JSONArray array = JSONArray.fromObject(map);

			JSONObject obj = new JSONObject();
			obj.put("OpeFinishSaveJson", array);
			// 返回 一个 保存成功 字样的 json串
			out.print("callback(" + obj.toString() + ")");
			out.flush();
			out.close();
			return;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {

	}

	/**
	 * 加密字串
	 * 
	 * @param text
	 *            String 源字串
	 * @return String 加密后字串
	 */
	private String encrypt(String text) {
		String av_str = "";
		try {
			byte aa[] = text.getBytes("UTF-16BE");

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < aa.length; i++) {
				aa[i] = (byte) (~aa[i]);
				sb.append(Integer.toHexString(aa[i]).substring(6));
			}
			av_str = sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return av_str;
	}

	/**
	 * 保存接口返回报文 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnSaveMsg(int status) {

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PAD_SAVE_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("</OPE_PAD_SAVE_RESULT>");

		return strBuf.toString();
	}

	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

}
