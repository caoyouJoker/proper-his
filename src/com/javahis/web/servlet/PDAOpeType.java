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

//获得手术类型pda - Servlet
public class PDAOpeType extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeType() {
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
		String sql = "SELECT A.TYPE_CODE,A.ROOM_NO "
			+ " FROM OPE_IPROOM A,SYS_DICTIONARY B " 
			+ " WHERE A.TYPE_CODE = B.ID" +
			  " AND B.GROUP_ID = 'OPE_TYPE'"
			+ " AND A.ROOM_NO = '"   
			+ roomNo + "'";                 
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
	    Map data = result.getData();
	    
		JSONArray array = JSONArray.fromObject(data);
		
		
		int count = Integer.parseInt(result.getValue("COUNT", 0));

        request.setAttribute("loginJson","callback" + "("+array.toString()+")");
		out.print("callback("+array.toString()+")");  

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
