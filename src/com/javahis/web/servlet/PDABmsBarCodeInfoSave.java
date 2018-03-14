package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdo.sys.SystemTool;
import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

//��Ѫ��ȫ�˲�pda - Servlet  --- ���� ope_bms_statues��

public class PDABmsBarCodeInfoSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDABmsBarCodeInfoSave() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		// ʵ����Ѫ��

		Map map = new HashMap();
		String FactVol = request.getParameter("fact_vol");
		String BarCode = request.getParameter("BarCode");
		// exec_user
		String BldTransCode = request.getParameter("user1");
		String password1 = request.getParameter("password1");
		String enPass = encrypt(password1);
		//��Ѫ��Ӧ 20170405 lij add
		String reaction = request.getParameter("reaction");

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
		String sqlSeq = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + BldTransCode + "' "
				+ "AND USER_PASSWORD='" + enPass + "' "
				+ "AND END_DATE>TO_DATE('" + dstr + "','yy-mm-dd hh24:mi:ss')";
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlSeq));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		/** ������� */
		if (0 == count) {
			map.put("request", "ִ�����������,��ȷ�ϣ�");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("bmsInfoSave", array);
			// ���� һ�� ����ɹ� ������ json��
			out.print("callback(" + obj.toString() + ")");
			out.flush();
			out.close();
			return;
		}

		Timestamp now = SystemTool.getInstance().getDate();
		// String time = StringTool.getString(now, "yyyyMMddHHmmss");
		// Timestamp now = SystemTool.getInstance().getDate();
		// String time = StringTool.getString(noaaaw, "yyyyMMddHHmmss");
		// ִ��ʱ�� fux modify 20161116 BLDTRANS_END_TIME(��Ѫ����ʱ��)
		// ϵͳʱ��
		String time = StringTool.getString(now, "yyyyMMddHHmmss");
		String roomNo = request.getParameter("roomNo");
		// ����ѡ��
		// String time = request.getParameter("EXEC_TIME").replace('-',
		// '/')+":"+"00";
		String sql = "UPDATE BMS_BLOOD SET RECHECK_TIME=TO_DATE('" + time//20170612 lij add��Ѫ�˲�ʱ��
				+ "','yy/mm/dd hh24:mi:ss')," + "" + "BLDTRANS_END_TIME=TO_DATE('" + time
				+ "','yy/mm/dd hh24:mi:ss')," + "" + "BLDTRANS_END_USER='"
				+ BldTransCode + "' , FACT_VOL = '" + FactVol + " ' , TRANSFUSION_REACTION = '" + reaction//20170406 lij add
				+ "' WHERE BLOOD_NO='" + BarCode + "'";
//		 System.out.println("=======getBmsCodeDetail=======" + sql);
		result = new TParm(TJDODBTool.getInstance().update(sql));
		// 2�Ǻ���״̬
		String sqlUpdate = "UPDATE OPE_BMS_STATUES SET STATUES='2' "
				+ "  WHERE BLOOD_NO='" + BarCode + "' AND ROOM_NO = '" + roomNo
				+ "' ";
		TParm resultUpdate = new TParm(TJDODBTool.getInstance().update(
				sqlUpdate));
		// System.out.println("=======sqlUpdate=======" + sqlUpdate);
		if (result.getErrCode() < 0 || resultUpdate.getErrCode() < 0) {
			// ʵ����map����
			map.put("request", "����ʧ�ܣ�");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("bmsInfoSave", array);
			// ���� һ�� ����ɹ� ������ json��
			out.print("callback(" + obj.toString() + ")");
			out.flush();
			out.close();
			return;
		}

		// ʵ����map����
		map.put("request", "����ɹ���");
		JSONArray array = JSONArray.fromObject(map);
		JSONObject obj = new JSONObject();
		obj.put("bmsInfoSave", array);
		// ���� һ�� ����ɹ� ������ json��
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

	/**
	 * �����ִ�
	 * 
	 * @param text
	 *            String Դ�ִ�
	 * @return String ���ܺ��ִ�
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

}
