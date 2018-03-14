package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.dongyang.util.StringTool;
import com.javahis.web.bean.DropDownList;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import com.javahis.web.jdo.EMRSearchTool;
import com.javahis.web.util.CommonUtil;
import com.sun.mail.handlers.message_rfc822;
import com.sun.media.ui.MessageBox;

//���밲ȫ�˲�pda - Servlet

public class PDAOpeInterOpsSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeInterOpsSave() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Map map=new HashMap();
		String roomNo = request.getParameter("roomNo");
		
		String opeBookSeq = request.getParameter("opeBookSeq");
		//��½��
		String user = request.getParameter("USER");
		
		StringBuffer str = new StringBuffer();
	

		Server.autoInit(this);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
//		
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("UPDATE OPE_OPBOOK SET ");     
        //fux modify ��Ϊ���빫����������    (�ֽ׶� ���� �������ӻ��ǰ�ȫ�˲�)

		/** timeoutUser �˲���*/
		if (!this.isBlank(user)) {  
			strBuf.append("TIMEOUT_USER='");
			strBuf.append(user);
			strBuf.append("', ");
		}


		/** timeoutDate    �˲�ʱ��*/
		if (!this.isBlank(dstr)) {
			strBuf.append(" TIMEOUT_DATE=TO_DATE('");
			strBuf.append(dstr);
			strBuf.append("','yy-mm-dd hh24:mi:ss')");
		}
//		/** drConformFlg */
//		if (!this.isBlank(drConformFlg)) {
//			strBuf.append("DR_CONFORM_FLG='");
//			strBuf.append(drConformFlg);
//			strBuf.append("',");
//		}
//		/** anaConformFlg */
//		if (!this.isBlank(anaConformFlg)) {  
//			strBuf.append("ANA_CONFORM_FLG='");
//			strBuf.append(anaConformFlg);
//			strBuf.append("', ");
//		}

		strBuf.append(" WHERE OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("'");

		Server.autoInit(this);

		// System.out.println("---strBuf.toString()--:"+strBuf.toString());
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				strBuf.toString()));   

		if (result.getErrCode() < 0) {  
			map.put("request","����ʧ�ܣ�");
			JSONArray array = JSONArray.fromObject(map);
	        JSONObject obj = new JSONObject();   
	        obj.put("OpeInterJsonSave", array);  
	        //���� һ�� ����ɹ� ������ json��
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
			 


  	
				
		map.put("request","����ɹ���");		
		JSONArray array = JSONArray.fromObject(map);
        JSONObject obj = new JSONObject();
        obj.put("OpeInterJsonSave", array);    
        obj.put("OpeInterJsonSaveStrBuf", strBuf.toString());
        
        //���� һ�� ����ɹ� ������ json��
		out.print("callback("+obj.toString()+")");       
		out.flush();
		out.close();
	}  
	
	/** У���Ƿ�Ϊ�� */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
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

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {

	}


}
