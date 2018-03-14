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

public class PDAOpeInterSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeInterSave() {
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
		
		String user1 = request.getParameter("user1");
		
		String password1 = request.getParameter("password1");
		
		String user2 = request.getParameter("user2");
		
		String password2 = request.getParameter("password2");
		
		String enPass = encrypt(password1);
		String enPass2 = encrypt(password2);
		
		StringBuffer str = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date(0));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		dstr = sysDate.toString().substring(0, 19);
		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + user1 + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";
		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		String sql2 = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
		+ "WHERE USER_ID = '" + user2 + "' " + "AND USER_PASSWORD='"
		+ enPass2 + "' " + "AND END_DATE>TO_DATE('" + dstr
		+ "','yy-mm-dd hh24:mi:ss')";
		
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		int count2 = Integer.parseInt(result2.getValue("COUNT", 0));
		// System.out.println(count);
		/** ������� */
		if (0 == count) {
			map.put("request","�������������,��ȷ�ϣ�");
			JSONArray array = JSONArray.fromObject(map);
	        JSONObject obj = new JSONObject();
	        obj.put("OpeInterJsonSave", array);    
	        //���� һ�� ����ɹ� ������ json��
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}else if(0 == count2)  {
			map.put("request","�Ӱ����������,��ȷ�ϣ�");
			JSONArray array = JSONArray.fromObject(map);
	        JSONObject obj = new JSONObject();
	        obj.put("OpeInterJsonSave", array);  
	        //���� һ�� ����ɹ� ������ json��
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		//�����action�� ��connect���� ����INW_TRANSFERSHEET ��Ľ������뽻��ʱ��  
//		/** У��opeBookSeq */
//		if (this.isBlank(opeBookSeq) || 1 == this.checkStr(opeBookSeq, "")) {
//			return this.returnSaveMsg(2);
//		}
//		/** У��roomNo */
//		if (!this.isBlank(roomNo) && 1 == this.checkStr(roomNo, "")) {
//			return this.returnSaveMsg(3);
//		}
//		/** У��transferUser */
//		if (!this.isBlank(transferUser) && 1 == this.checkStr(transferUser, "")) {
//			return this.returnSaveMsg(4);
//		}
//		/** У��transfer_Date */
//		if (!this.isBlank(transfer_Date)
//				&& 1 == this.checkStr(transfer_Date, "")) {
//			return this.returnSaveMsg(5);
//		}
//		/** У��timeoutUser */
//		if (!this.isBlank(timeoutUser) && 1 == this.checkStr(timeoutUser, "")) {
//			return this.returnSaveMsg(6);
//		}
//		/** У��timeoutDate */
//		if (!this.isBlank(timeoutDate) && 1 == this.checkStr(timeoutDate, "")) {
//			return this.returnSaveMsg(7);
//		}
//		/** У��drConformFlg */
//		if (!this.isBlank(drConformFlg) && 1 == this.checkStr(drConformFlg, "")) {
//			return this.returnSaveMsg(8);
//		}
//		/** У��anaConformFlg */
//		if (!this.isBlank(anaConformFlg)
//				&& 1 == this.checkStr(anaConformFlg, "")) {
//			return this.returnSaveMsg(9);
//		}
//
//		StringBuffer strBuf = new StringBuffer();
//		strBuf.append("UPDATE OPE_OPBOOK SET ");     
//        //fux modify ��Ϊ���빫����������    (�ֽ׶� ���� �������ӻ��ǰ�ȫ�˲�)
////		if(type.equals("SAVE")){
////		/** roomNo */
////		if (!this.isBlank(roomNo)) {
////			strBuf.append("ROOM_NO='");  
////			strBuf.append(roomNo);            
////			strBuf.append("',");
////		}			
////		}
//		/** transferUser */
//		if (!this.isBlank(transferUser)) {  
//			strBuf.append("TRANSFER_USER='");
//			strBuf.append(transferUser);
//			strBuf.append("', ");
//		}
//		/** transfer_Date */
//		if (!this.isBlank(transfer_Date)) {
//			strBuf.append("TRANSFER_DATE=TO_DATE('");
//			strBuf.append(transfer_Date);
//			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
//		}
//
//		/** timeoutUser */
//		if (!this.isBlank(timeoutUser)) {
//			strBuf.append("TIMEOUT_USER='");
//			strBuf.append(timeoutUser);
//			strBuf.append("', ");
//		}
//		/** timeoutDate */
//		if (!this.isBlank(timeoutDate)) {
//			strBuf.append(" TIMEOUT_DATE=TO_DATE('");
//			strBuf.append(timeoutDate);
//			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
//		}
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
//		strBuf.append("OPBOOK_SEQ='");
//		strBuf.append(opeBookSeq);
//		strBuf.append("' ");
//
//		strBuf.append(" WHERE OPBOOK_SEQ='");
//		strBuf.append(opeBookSeq);
//		strBuf.append("'");

		Server.autoInit(this);

//		
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("UPDATE OPE_OPBOOK SET ");     
        //fux modify ��Ϊ���빫����������    (�ֽ׶� ���� �������ӻ��ǰ�ȫ�˲�)
//		if(type.equals("SAVE")){
//		/** roomNo */
//		if (!this.isBlank(roomNo)) {
//			strBuf.append("ROOM_NO='");  
//			strBuf.append(roomNo);            
//			strBuf.append("',");
//		}			
//		}
		/** transferUser ������*/
		if (!this.isBlank(user2)) {  
			strBuf.append("TIMEOUT_USER='");
			strBuf.append(user2);
			strBuf.append("', ");
		}

//		/** timeoutUser */
//		if (!this.isBlank(user2)) {
//			strBuf.append("TIMEOUT_USER='");
//			strBuf.append(user2);
//			strBuf.append("', ");
//		}  
		/** transfer_Date  ����ʱ��*/
//		if (!this.isBlank(transfer_Date)) {
//		strBuf.append("TRANSFER_DATE=TO_DATE('");
//		strBuf.append(transfer_Date+":"+"00");
//		strBuf.append("','yy-mm-dd hh24:mi:ss'),");
//	}
//		/** timeoutDate    �˲�ʱ��*/
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
		
		result = new TParm(TJDODBTool.getInstance().update(
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
			 //���밲ȫ�˲�  �� + ���� ope_check��
		        StringBuffer strBufNew = new StringBuffer();
		        strBufNew.append("UPDATE OPE_IPROOM SET ");
				/** roomNo */
				if (!this.isBlank(roomNo)) {
					strBufNew.append("OPBOOK_SEQ='");
					strBufNew.append(opeBookSeq);          
					strBufNew.append("' ");
				}  
				strBufNew.append(" WHERE ROOM_NO='");
				strBufNew.append(roomNo);      
				strBufNew.append("'");
				Server.autoInit(this);
			    //System.out.println("---strBuf.toString()--:"+strBuf.toString());
			    result = new TParm(TJDODBTool.getInstance().update(
			    		strBufNew.toString()));   
				
				if (result.getErrCode() < 0) {
				//ʵ����map����
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
  
				//srr ���� �������� �˲�ʱ�����Ϣ
				// �Ƿ�Ӧ��ֻ�ܲ�ѯ����û�н�����Ա�� ��			
				String sqlSelect = " SELECT OPBOOK_SEQ FROM OPE_CHECK WHERE OPBOOK_SEQ = '"+opeBookSeq+"'  ";
				
				TParm resultquery = new TParm(TJDODBTool.getInstance().select(
						sqlSelect)); 
				TParm resultInsert = new TParm();
				
				StringBuffer strBufInsert = new StringBuffer();  
				//��ѯ�ĵ� ��ִ�в��붯�� 
				if(resultquery.getCount("OPBOOK_SEQ")>0){
					resultInsert = resultquery;
					
				}  
				//��ѯ���� ����ope_check�Ĳ��붯��
				else{
					String sqlInsert = "SELECT A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
						+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
						+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
						+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
						+ "B.CHN_DESC AS OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
						+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
						+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
						+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,A.GDVAS_CODE," +
						  " A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,A.OP_CODE1,O.ALLERGY,C.SEX_CODE "
						+ "FROM OPE_OPBOOK A,SYS_DICTIONARY B,SYS_PATINFO C,SYS_DICTIONARY F,"
						+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"
						+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N,ADM_INP O  "
						+ "WHERE B.GROUP_ID = 'OPE_OPROOM'AND A.ROOM_NO = B.ID(+) "
						+ "AND A.MR_NO = C.MR_NO (+) "
						+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "  
						+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   " 
						+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
						+ "AND A.MAIN_SURGEON = I.USER_ID(+) "    
						+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
						+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
						+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "  
						+ "AND A.ANA_USER1 = M.USER_ID(+) "           
						+ "AND A.EXTRA_USER1 = N.USER_ID(+)" +
						//"AND A.MR_NO = O.MR_NO(+) "  +
						  "AND A.CASE_NO = O.CASE_NO(+) "  
						+ "AND A.OPBOOK_SEQ = '"+opeBookSeq+"'  ORDER BY OPBOOK_SEQ";               
					// Server.autoInit(this);  

					    resultquery = new TParm(TJDODBTool.getInstance().select(
					    		sqlInsert)); 
					    
						String checkNo = SystemTool.getInstance().getNo("ALL", "OPE",
								"CHECK_NO", "CHECK_NO"); 
						
//						SystemTool.getInstance().getNo("ALL", "OPE", "OPBOOK_SEQ",
//				        "OPBOOK_SEQ");   
						System.out.println(""+resultquery.getValue("BIRTH_DATE",0));
					
						
						Timestamp now = SystemTool.getInstance().getDate();
						String time = StringTool.getString(now, "yyyyMMddHHmmss");    
						
						//TO_DATE('1958-06-08 00:00:00.0','YYYYMMDDHH24MISS')    
						strBufInsert.append("INSERT INTO OPE_CHECK (CHECK_NO,MR_NO,PAT_NAME,SEX,BIRTH_DATE,OPBOOK_SEQ,TYPE_CODE," +  
								"OPERATION_ICD,OPT_CHN_DESC,ALLERGIC_FLG, READY_FLG,VALID_DATE_FLG," +
								"SPECIFICATION_FLG,CHECK_DR_CODE,CHECK_NS_CODE, CHECK_DATE,OPT_USER,OPT_TERM,OPT_DATE)" +  
								" VALUES ('"+checkNo+"','"+resultquery.getValue("MR_NO",0)+"'," +
										"'"+resultquery.getValue("PAT_NAME",0)+"'," +
								"'"+resultquery.getValue("SEX_CODE",0)+"'," +
								"TO_DATE('"+ resultquery.getValue("BIRTH_DATE",0).replace('-', '/').substring(0, 10)+"','YYYY/MM/DD')," +
								" '"+opeBookSeq+"'," +  
										"'"+resultquery.getValue("GDVAS_CODE",0)+"'," + 
												"'"+resultquery.getValue("OP_CODE1",0)+"'," +    
								" '"+resultquery.getValue("OPT_CHN_DESC",0)+"'," +    
								" '"+resultquery.getValue("ALLERGY",0)+"'," +
										"'"+resultquery.getValue("READY_FLG",0)+"'," +  
												"'"+resultquery.getValue("VALID_DATE_FLG",0)+"'," +
								"'"+resultquery.getValue("SPECIFICATION_FLG",0)+"'," +
										"'"+resultquery.getValue("MAIN_SURGEON_ID",0)+"'," +
										"'"+user2+"'," +
								        "TO_DATE('"+time+"','YYYYMMDDHH24MISS')," +
										"'"+user2+"','127.0.0.1'," +
									    "TO_DATE('"+time+"','YYYYMMDDHH24MISS'))");
						resultInsert = new TParm(TJDODBTool.getInstance().update(
					    		 strBufInsert.toString()));  
				} 
				  

				if (resultInsert.getErrCode() < 0) {
				//ʵ����map����  
					map.put("request","����ʧ�ܣ�!!!!!!!!!");
					JSONArray array = JSONArray.fromObject(map);
			        JSONObject obj = new JSONObject();
			        obj.put("OpeInterJsonSave", array); 
			        obj.put("OpeInterJsonSaveStrBuf222", resultquery.getValue("BIRTH_DATE",0).replace('-', '/').substring(0, 10));
			        obj.put("OpeInterJsonSaveStrBuf", strBufInsert.toString());
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
        obj.put("OpeInterJsonSaveStrBufNew", strBufNew.toString());
        obj.put("OpeInterJsonSaveStrBuf", strBuf.toString());
        obj.put("OpeInterJsonSaveStrBuf", strBufInsert.toString());
        
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
