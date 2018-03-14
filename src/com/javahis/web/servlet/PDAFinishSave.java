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

//�����ҽ���� - Servlet  --- �Ȳ�ѯ ope_bms_statues�� Ȼ��ɾ��
public class PDAFinishSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAFinishSave() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	// PDA�����ʱУ��ѪƷ��ȫ�˲��Ƿ���в�������������ʾ���ж��Ƿ���Ѫ����Ѫǿ�ƽ���ѪƷ��ȫ�˲����ܽ���󶨣�
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Map map = new HashMap();
		String roomNo = request.getParameter("roomNo");
		// fux modify 20170214 ���� room_no ��ѯ statues
		String sqlSelStatues = "SELECT BLOOD_NO FROM  OPE_BMS_STATUES WHERE  ROOM_NO='"
				+ roomNo + "' AND STATUES = '1' ";
		TParm resultSel = new TParm(TJDODBTool.getInstance().select(
				sqlSelStatues));
		// �ж� �Ƿ�����Ѫ����
		if (resultSel.getCount("BLOOD_NO") <= 0) {
			// ����� ���жϰ�ȫ�˲�״̬
			String sqliproom = "UPDATE OPE_IPROOM SET OPBOOK_SEQ = null"
					+ " WHERE ROOM_NO='" + roomNo + "' ";
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sqliproom.toString()));
			String sqlDelete = "DELETE OPE_BMS_STATUES " + " WHERE ROOM_NO='"
					+ roomNo + "'  ";
			TParm resultDel = new TParm(TJDODBTool.getInstance().update(
					sqlDelete.toString()));
			if (result.getErrCode() < 0 || resultDel.getErrCode() < 0) {
				// ʵ����map����
				map.put("request", "�����ʧ�ܣ�");
				JSONArray array = JSONArray.fromObject(map);
				JSONObject obj = new JSONObject();
				obj.put("OpeFinishSaveJson", array);
				// ���� һ�� ����ɹ� ������ json��
				out.print("callback(" + obj.toString() + ")");
				out.flush();
				out.close();
				return;
			}
			map.put("request", "����󶨳ɹ���");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpeFinishSaveJson", array);
			// ���� һ�� ����ɹ� ������ json��
			out.print("callback(" + obj.toString() + ")");
			out.flush();
			out.close();
		} else {
			String message = "";
			String messStart = "�����Ϊ��" + "\n";
			String messEnd = "ѪƷ�Ѻ�����δ�˲飬����ɺ˲飡";
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
			// /��һ��\n�ڶ���
			message = messStart + messMidd + messEnd;
			map.put("request", message);

			// for (int i = 0; i < resultSel.getCount("BLOOD_NO"); i++) {
			// map.put("request", "�����Ϊ��" + resultSel.getValue("BLOOD_NO", i)+
			// "ѪƷ�Ѻ�����δ�˲飬����ɺ˲飡");
			// }
			JSONArray array = JSONArray.fromObject(map);

			JSONObject obj = new JSONObject();
			obj.put("OpeFinishSaveJson", array);
			// ���� һ�� ����ɹ� ������ json��
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

	/**
	 * ����ӿڷ��ر��� ��Σ����ص�״̬�� ���Σ�int���ͣ�0ͨ����1��ͨ��
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

	/** У���Ƿ�Ϊ�� */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

}
