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

//pda 登陆界面 - Servlet
public class PDALogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDALogin() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 
		/** modified by WangQing 20170410，打开数据库连接*/
		Server.autoInit(this); 
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		
		 String userId = request.getParameter("userId");
         String password = request.getParameter("password");
         String roomNo = request.getParameter("roomNo");
		// =====================================================================================================
		String enPass = encrypt(password);
  
		StringBuffer strBuf = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + userId + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";  

		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		  
	    Map data = result.getData();
	    
		JSONArray array = JSONArray.fromObject(data);
		
		
		
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		// System.out.println(count);
		/** 密码错误 */
		if (0 == count) {
			strBuf.append("<ERR>");
			strBuf.append("1");
			strBuf.append("</ERR>");

		} else {
			strBuf.append("<ERR>");
			strBuf.append("0");
			strBuf.append("</ERR>");
			// return "0";
		}

		// =====================================================================================================



		String onQueryOptUser = onQueryOptUser(userId);

		strBuf.append(onQueryOptUser);
//		List<DropDownList> regions = EMRSearchTool.getInstance()
//		.getAllRegionList();
//		request.setAttribute("regionList", this.buildSelect(regions));
        request.setAttribute("loginJson","callback" + "("+array.toString()+")");

		String sqlRoomNo = "SELECT A.TYPE_CODE "
			+ " FROM OPE_IPROOM A,SYS_DICTIONARY B " 
			+ " WHERE A.TYPE_CODE = B.ID" +
			  " AND B.GROUP_ID = 'OPE_TYPE'"
			+ " AND A.ROOM_NO = '"   
			+ roomNo + "'";                 
		Server.autoInit(this);
		TParm resultRoomNo = new TParm(TJDODBTool.getInstance().select(sqlRoomNo));
		
	    Map dataRoomNo = resultRoomNo.getData(); 
	    
		JSONArray arrayRoomNo = JSONArray.fromObject(dataRoomNo);
		JSONObject obj = new JSONObject();
		obj.put("Login", array);
        obj.put("RoomType", arrayRoomNo);  
		
		out.print("callback("+obj.toString()+")");         
//		out.flush();
//		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {
	}
	
	/**
	 * 构造select
	 * 
	 * @param dropDownList
	 *            List
	 * @return String
	 */
	private String buildSelect(List<DropDownList> dropDownList) {
		StringBuilder strSelect = new StringBuilder();
		strSelect.append("<option value=''>----请选择----</option>");
		for (int i = 0; i < dropDownList.size(); i++) {
			strSelect.append("<option value='" + dropDownList.get(i).getValue()
					+ "'>");
			strSelect.append(dropDownList.get(i).getTitle());
			strSelect.append("</option>");
		}
		return strSelect.toString();
	}
	
	/**
	 * 登录
	 * */

	public String onLogin(String userId, String password) {

		// =====================================================================================================
		String enPass = encrypt(password);

		StringBuffer strBuf = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + userId + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";

		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		// System.out.println(count);
		/** 密码错误 */
		if (0 == count) {
			strBuf.append("<ERR>");
			strBuf.append("1");
			strBuf.append("</ERR>");

		} else {
			strBuf.append("<ERR>");
			strBuf.append("0");
			strBuf.append("</ERR>");
			// return "0";
		}

		// =====================================================================================================

		String onQueryOptUser = onQueryOptUser(userId);

		strBuf.append(onQueryOptUser);

		// System.out.println("登录返回数据---》"+strBuf.toString());
		return strBuf.toString();
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
	 * 根据用户名查询科室和病区,区域
	 * */

	public String onQueryOptUser(String userId) {

		String sqlDept = "SELECT A.DEPT_CODE,B.DEPT_CHN_DESC,MAIN_FLG "
				+ " FROM SYS_OPERATOR_DEPT A ,SYS_DEPT B "
				+ " WHERE A.DEPT_CODE = B.DEPT_CODE" + " AND A.USER_ID = '"
				+ userId + "'";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(sqlDept));
		StringBuffer strBuf = new StringBuffer();
		int deptCount = deptParm.getCount();

		strBuf.append("<DEPT_COUNT>");
		strBuf.append(deptCount);
		strBuf.append("</DEPT_COUNT>");

		String mainDeptCode = "";
		String mainDeptDesc = "";
		String dept_code = "";
		String dept_desc = "";

		for (int i = 0; i < deptCount; i++) {
			String main_flg = deptParm.getData("MAIN_FLG", i).toString();
			if (main_flg.equals("Y")) {
				mainDeptCode = deptParm.getData("DEPT_CODE", i).toString();
				mainDeptDesc = deptParm.getData("DEPT_CHN_DESC", i).toString();
			} else {
				if (i == deptCount - 1) {
					dept_code += deptParm.getData("DEPT_CODE", i).toString();
					dept_desc += deptParm.getData("DEPT_CHN_DESC", i)
							.toString();
				} else {
					dept_code += deptParm.getData("DEPT_CODE", i).toString()
							+ ",";
					dept_desc += deptParm.getData("DEPT_CHN_DESC", i)
							.toString()
							+ ",";
				}

			}
		}

		strBuf.append("<MAIN_DEPT_CODE>");
		strBuf.append(mainDeptCode);
		strBuf.append("</MAIN_DEPT_CODE>");

		strBuf.append("<MAIN_DEPT_CHN_DESC>");
		strBuf.append(mainDeptDesc);
		strBuf.append("</MAIN_DEPT_CHN_DESC>");

		strBuf.append("<DEPT_CODE>");
		strBuf.append(dept_code);
		strBuf.append("</DEPT_CODE>");

		strBuf.append("<DEPT_CHN_DESC>");
		strBuf.append(dept_desc);
		strBuf.append("</DEPT_CHN_DESC>");

		deptParm.getValue("DEPT_CODE");

		String sqlStation = " SELECT B.STATION_CODE,B.STATION_DESC,MAIN_FLG "
				+ " FROM SYS_OPERATOR_STATION A ,SYS_STATION B "
				+ " WHERE A.STATION_CLINIC_CODE = B.STATION_CODE "
				+ " AND A.USER_ID = '" + userId + "'";

		TParm stationParm = new TParm(TJDODBTool.getInstance().select(
				sqlStation));

		int stationCount = stationParm.getCount();

		strBuf.append("<STATION_CODE>");
		strBuf.append(stationCount);
		strBuf.append("</STATION_CODE>");

		String mainStationCode = "";
		String mainStationDesc = "";
		String stationCode = "";
		String stationDesc = "";

		for (int i = 0; i < stationCount; i++) {
			String main_flg = stationParm.getData("MAIN_FLG", i).toString();
			if (main_flg.equals("Y")) {
				mainStationCode = stationParm.getData("STATION_CODE", i)
						.toString();
				mainStationDesc = stationParm.getData("STATION_DESC", i)
						.toString();
			} else {
				if (i == stationCount - 1) {
					stationCode += stationParm.getData("STATION_CODE", i)
							.toString();
					stationDesc += stationParm.getData("STATION_DESC", i)
							.toString();
				} else {
					stationCode += stationParm.getData("STATION_CODE", i)
							.toString()
							+ ",";
					stationDesc += stationParm.getData("STATION_DESC", i)
							.toString()
							+ ",";
				}
			}
		}
		strBuf.append("<MAIN_STATION_CODE>");
		strBuf.append(mainStationCode);
		strBuf.append("</MAIN_STATION_CODE>");

		strBuf.append("<MAIN_STATION_DESC>");
		strBuf.append(mainStationDesc);
		strBuf.append("</MAIN_STATION_DESC>");

		strBuf.append("<STATION_CODE>");
		strBuf.append(stationCode);
		strBuf.append("</STATION_CODE>");

		strBuf.append("<STATION_DESC>");
		strBuf.append(stationDesc);
		strBuf.append("</STATION_DESC>");

		String sqlRegion = " SELECT REGION_CODE FROM SYS_OPERATOR  WHERE USER_ID = '"
				+ userId + "'";
		TParm regionParm = new TParm(TJDODBTool.getInstance().select(sqlRegion));
		int countRegion = regionParm.getCount();
		if (countRegion > 0) {
			strBuf.append("<REGION_CODE>");
			strBuf.append(regionParm.getData("REGION_CODE", 0));
			strBuf.append("</REGION_CODE>");
		}
		return strBuf.toString();
	}


}
