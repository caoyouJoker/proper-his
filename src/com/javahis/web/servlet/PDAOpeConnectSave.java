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
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.web.bean.DropDownList;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import com.javahis.web.jdo.EMRSearchTool;
import com.javahis.web.util.CommonUtil;
import com.sun.mail.handlers.message_rfc822;
import com.sun.media.ui.MessageBox;

//手术室交接pda(保存动作) - Servlet
public class PDAOpeConnectSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PDAOpeConnectSave() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Server.autoInit(this);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		// 基本信息start
		String user = request.getParameter("user");// 登录人员
		String mrNo = request.getParameter("mrNo");// 病案号
		String roomNo = request.getParameter("roomNo");// 术间
		String opeBookSeq = request.getParameter("opeBookSeq");// 手术单号
		// 基本信息end

		// 交接查验表start
		String handover_skin_preparation_flg = request.getParameter("handover_skin_preparation_flg");// 皮肤准备
		String handover_crossmatch_flg = request.getParameter("handover_crossmatch_flg");// 交叉配血
		String handover_skin_test_flg = request.getParameter("handover_skin_test_flg");// 皮试
		String handover_bowel_preparation_flg = request.getParameter("handover_bowel_preparation_flg");// 肠道准备
		String handover_prepare_education_flg = request.getParameter("handover_prepare_education_flg");// 术前宣教
		String handover_dental_care_flg = request.getParameter("handover_dental_care_flg");// 口腔清洁
		String handover_nasal_care_flg = request.getParameter("handover_nasal_care_flg");// 鼻腔清洁
		// 交接查验表end

		// 交接班信息start
		String transfer_Date = request.getParameter("TRANSFER_DATE");// 交接时间
		String handover_to_dept = request.getParameter("handover_to_dept");// 转入科室
		String handover_icd_chn_desc = request.getParameter("handover_icd_chn_desc");// 入院诊断
		String handover_opt_chn_desc = request.getParameter("handover_opt_chn_desc");// 拟行手术
		// 交接班信息end

		// 患者一般情况start
		String handover_temperature = request.getParameter("handover_temperature");// T
		String handover_pulse = request.getParameter("handover_pulse");// P
		String handover_respire = request.getParameter("handover_respire");// R
		String handover_bp = request.getParameter("handover_bp");// BP--------需要拆分
		String handover_general_mark = request.getParameter("handover_general_mark");// 备注（一般备注）
		String handover_infect_flg = request.getParameter("handover_infect_flg");// 传染病
		String handover_allergy = request.getParameter("handover_allergy");// 过敏
		// 患者一般情况end

		// 手术当日start
		String handover_weight_mon = request.getParameter("handover_weight_mon");// 术晨体重
		String handover_skin_break_flg = request.getParameter("handover_skin_break_flg");// 皮肤破损
		String handover_skin_break_position = request.getParameter("handover_skin_break_position");// 皮肤破损部位
		// 手术当日end

		// 备血start
		String handover_blood_type = request.getParameter("handover_blood_type");// 血型
		String handover_rh = request.getParameter("handover_rh");// Rh血型
		String handover_ope_pre_mark = request.getParameter("handover_ope_pre_mark");// 术前备注
		// 备血end

		// 病历交接同意书start
		String handover_ope_inform_flg = request.getParameter("handover_ope_inform_flg");// 术前同意书
		String handover_ana_sinform_flg = request.getParameter("handover_ana_sinform_flg");// 麻醉同意书
		String handover_blood_inform_flg = request.getParameter("handover_blood_inform_flg");// 输血同意书
		// 病历交接同意书end

		// 其他start
		String handover_active_tooth_flg = request.getParameter("handover_active_tooth_flg");// 活动牙齿
		String handover_false_tooth_flg = request.getParameter("handover_false_tooth_flg");// 义齿
		// 其他end

		// 交接人员start
		String user1 = request.getParameter("user1");// 交班人员
		String password1 = request.getParameter("password1");// 交班人员密码	
		String user2 = request.getParameter("user2");// 接班人员	
		String password2 = request.getParameter("password2");// 接班人员密码
		String enPass = encrypt(password1);
		String enPass2 = encrypt(password2);
		// 交接人员end

		// 参数校验
		if(this.isBlank(user1)){
			Map map=new HashMap();
			map.put("request","交班人员不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		if(this.isBlank(password1)){
			Map map=new HashMap();
			map.put("request","交班人员密码不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		if(this.isBlank(user2)){
			Map map=new HashMap();
			map.put("request","接班人员不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		if(this.isBlank(password2)){
			Map map=new HashMap();
			map.put("request","接班人员密码不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		if(this.isBlank(transfer_Date)){
			Map map=new HashMap();
			map.put("request","交接时间不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		if(this.isBlank(handover_to_dept)){
			Map map=new HashMap();
			map.put("request","转入科室不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}	
		if(this.isBlank(roomNo)){
			Map map=new HashMap();
			map.put("request","术间不能为空！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		

		// 检验交接班人员的账户密码start
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String dstr = sysDate.toString().substring(0, 19);// 系统时间
		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + user1 + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";
		String sql2 = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + user2 + "' " + "AND USER_PASSWORD='"
				+ enPass2 + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		int count2 = Integer.parseInt(result2.getValue("COUNT", 0));
		/** 密码错误 */
		if (0 == count) {  
			Map map=new HashMap();
			map.put("request","交班人密码错误,请确认！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		} else if(0 == count2)  {
			Map map=new HashMap();
			map.put("request","接班人密码错误,请确认！");
			JSONArray array = JSONArray.fromObject(map);
			JSONObject obj = new JSONObject();
			obj.put("OpePatConnectJson", array);  
			//返回 一个 保存成功 字样的 json串
			out.print("callback("+obj.toString()+")");       
			out.flush();
			out.close();
			return;
		}
		// 检验交接班人员的账户密码end

		String roleSql = "SELECT ROLE_ID FROM SYS_OPERATOR WHERE USER_ID ='"+user+"' ";
		TParm resultRoleId = new TParm(TJDODBTool.getInstance().select(roleSql));
		String roleId = resultRoleId.getValue("ROLE_ID", 0);
		System.out.println("===roleId: "+roleId);
		if(roleId != null){
			if(roleId.equals("OPE")){// 手术室护士长可以修改除病患信息外的所有信息
				TConnection conn= TDBPoolManager.getInstance().getConnection();

				//1.update OPE_OPBOOK
				StringBuffer strBuf001 = new StringBuffer();
				strBuf001.append("UPDATE OPE_OPBOOK SET ");

				/** transferUser 交班人*/
				strBuf001.append("HANDOVER_USER='");
				strBuf001.append(user1);
				strBuf001.append("', "); 

				/** transfer_Date  交班时间*/     
				strBuf001.append("HANDOVER_DATE=TO_DATE('");
				strBuf001.append(transfer_Date+":"+"00");
				strBuf001.append("','yy-mm-dd hh24:mi:ss'),");

				/** transferUser 接班人*/
				strBuf001.append("TRANSFER_USER='");
				strBuf001.append(user2);
				strBuf001.append("', ");

				/** transfer_Date  接班时间*/    
				strBuf001.append("TRANSFER_DATE=TO_DATE('");
				strBuf001.append(transfer_Date+":"+"00");
				strBuf001.append("','yy-mm-dd hh24:mi:ss'), ");

				/** 转入科室 */
				String deptSql = "SELECT DEPT_CODE FROM SYS_DEPT WHERE DEPT_CHN_DESC ='"+handover_to_dept+"' ";
				TParm deptData = new TParm(TJDODBTool.getInstance().select(deptSql));
				String dept = deptData.getValue("DEPT_CODE", 0);
				strBuf001.append("OP_DEPT_CODE='");
				strBuf001.append(dept);
				strBuf001.append("', ");
				
				/** 术间  */
				strBuf001.append("ROOM_NO='");
				strBuf001.append(roomNo);
				strBuf001.append("' ");

				/** 入院主诊断*/
				if(!this.isBlank(handover_icd_chn_desc)){
					strBuf001.append(", ");
					String icdSql = "SELECT ICD_CODE FROM SYS_DIAGNOSIS WHERE ICD_CHN_DESC ='"+handover_icd_chn_desc+"' ";
					TParm icdData = new TParm(TJDODBTool.getInstance().select(icdSql));
					String icd = icdData.getValue("ICD_CODE", 0);
					strBuf001.append("DIAG_CODE1='");
					strBuf001.append(icd);
					strBuf001.append("' ");
				}
	
				/** 拟行手术*/
				if(!this.isBlank(handover_opt_chn_desc)){
					strBuf001.append(", ");
					String optSql = "SELECT OPERATION_ICD FROM SYS_OPERATIONICD WHERE OPT_CHN_DESC ='"+handover_opt_chn_desc+"' ";
					TParm optData = new TParm(TJDODBTool.getInstance().select(optSql));
					String opt = optData.getValue("OPERATION_ICD", 0);
					strBuf001.append("OP_CODE1='");
					strBuf001.append(opt);
					strBuf001.append("' ");
				}
			
				strBuf001.append(" WHERE OPBOOK_SEQ='");
				strBuf001.append(opeBookSeq);
				strBuf001.append("' AND CANCEL_FLG <> 'Y' ");
				TParm result001 = new TParm(TJDODBTool.getInstance().update(
						strBuf001.toString(), conn)); 
				if (result001.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 2. update INW_TransferSheet_WO
				StringBuffer strBuf002 = new StringBuffer();
				strBuf002.append("UPDATE INW_TransferSheet_WO SET ");   
				//交接时间
				strBuf002.append("TRANSFER_DATE=TO_DATE('");
				strBuf002.append(transfer_Date+":"+"00");
				strBuf002.append("','yy-mm-dd hh24:mi:ss'), " );	
				/** 转入科室 */
				strBuf002.append("TO_DEPT='");
				strBuf002.append(dept);
				strBuf002.append("' ");

				/** 入院主诊断*/
				if(!this.isBlank(handover_icd_chn_desc)){
					strBuf002.append(", ");
					String icdSql = "SELECT ICD_CODE FROM SYS_DIAGNOSIS WHERE ICD_CHN_DESC ='"+handover_icd_chn_desc+"' ";
					TParm icdData = new TParm(TJDODBTool.getInstance().select(icdSql));
					String icd = icdData.getValue("ICD_CODE", 0);
					strBuf002.append("DIAGNOSIS='");
					strBuf002.append(icd);
					strBuf002.append("' ");
				}
				

				/** 拟行手术*/
				if(!this.isBlank(handover_opt_chn_desc)){
					strBuf002.append(", ");
					String optSql = "SELECT OPERATION_ICD FROM SYS_OPERATIONICD WHERE OPT_CHN_DESC ='"+handover_opt_chn_desc+"' ";
					TParm optData = new TParm(TJDODBTool.getInstance().select(optSql));
					String opt = optData.getValue("OPERATION_ICD", 0);
					strBuf001.append("OP_CODE1='");
					strBuf002.append("OPERATION_CODE='");
					strBuf002.append(opt);
					strBuf002.append("' ");
				}
				
				strBuf002.append(", ");
				// 交接查验表所有要修改的数据start
				// 皮肤准备
				strBuf002.append("SKIN_PREPARATION_FLG='");
				strBuf002.append(handover_skin_preparation_flg);
				strBuf002.append("', ");
				// 交叉配血
				strBuf002.append("CROSSMATCH_FLG='");
				strBuf002.append(handover_crossmatch_flg);
				strBuf002.append("', ");
				// 皮试
				strBuf002.append("SKIN_TEST_FLG='");
				strBuf002.append(handover_skin_test_flg);
				strBuf002.append("', ");
				// 肠道准备
				strBuf002.append("BOWEL_PREPARATION_FLG='");
				strBuf002.append(handover_bowel_preparation_flg);
				strBuf002.append("', ");
				// 术前宣教
				strBuf002.append("PREPARE_EDUCATION_FLG='");
				strBuf002.append(handover_prepare_education_flg);
				strBuf002.append("', ");
				// 口腔清洁
				strBuf002.append("DENTAL_CARE_FLG='");
				strBuf002.append(handover_dental_care_flg);
				strBuf002.append("', ");
				// 鼻腔清洁
				strBuf002.append("NASAL_CARE_FLG='");
				strBuf002.append(handover_nasal_care_flg);
				strBuf002.append("', ");
				// 交接查验表所有要修改的数据end

				// 病历交接同意书start
				// 术前同意书
				strBuf002.append("OPE_INFORM_FLG='");
				strBuf002.append(handover_ope_inform_flg);
				strBuf002.append("', ");
				// 麻醉同意书
				strBuf002.append("ANA_SINFORM_FLG='");
				strBuf002.append(handover_ana_sinform_flg);
				strBuf002.append("', ");
				// 输血同意书
				strBuf002.append("BLOOD_INFORM_FLG='");
				strBuf002.append(handover_blood_inform_flg);
				strBuf002.append("', ");
				// 病历交接同意书end

				// 其他start
				// 活动牙齿
				strBuf002.append("ACTIVE_TOOTH_FLG='");
				strBuf002.append(handover_active_tooth_flg);
				strBuf002.append("', ");
				// 义齿
				strBuf002.append("FALSE_TOOTH_FLG='");
				strBuf002.append(handover_false_tooth_flg);
				strBuf002.append("' ");
				// 其他end

				// 患者一般情况start
				// T
				if(!this.isBlank(handover_temperature)){
					strBuf002.append(", ");
					strBuf002.append("TEMPERATURE='");
					strBuf002.append(handover_temperature);
					strBuf002.append("' ");
				}

				// P
				if(!this.isBlank(handover_pulse)){
					strBuf002.append(", ");
					strBuf002.append("PULSE='");
					strBuf002.append(handover_pulse);
					strBuf002.append("' ");
				}

				// R
				if(!this.isBlank(handover_respire)){
					strBuf002.append(", "); 
					strBuf002.append("RESPIRE='");
					strBuf002.append(handover_respire);
					strBuf002.append("' ");
				}

				// BP之SYSTOLICPRESSURE
				// BP之DIASTOLICPRESSURE
				if(!this.isBlank(handover_bp)){
					strBuf002.append(", "); 
					String[] sourceStrArray = handover_bp.split("/"); 
					strBuf002.append("SBP='");
					strBuf002.append(sourceStrArray[0]);
					strBuf002.append("', "); 

					strBuf002.append("DBP='");
					strBuf002.append(sourceStrArray[1]);
					strBuf002.append("' ");
				}

				// 备注（一般备注）
				if(!this.isBlank(handover_general_mark)){
					strBuf002.append(", "); 
					strBuf002.append("GENERAL_MARK='");
					strBuf002.append(handover_general_mark);
					strBuf002.append("' ");
				}

				// 传染病
				if(!this.isBlank(handover_infect_flg)){
					strBuf002.append(", "); 
					strBuf002.append("INFECT_FLG='");
					strBuf002.append(handover_infect_flg);
					strBuf002.append("' ");
				}

				// 过敏
				if(!this.isBlank(handover_allergy)){
					strBuf002.append(", ");
					strBuf002.append("ALLERGIC_FLG='");
					strBuf002.append(handover_allergy);
					strBuf002.append("' ");
				}
				// 患者一般情况end

				// 手术当日start
				// 术晨体重
				if(!this.isBlank(handover_weight_mon)){
					strBuf002.append(", ");
					strBuf002.append("WEIGHT='");
					strBuf002.append(handover_weight_mon);
					strBuf002.append("' ");
				}

				// 皮肤破损
				if(!this.isBlank(handover_skin_break_flg)){
					strBuf002.append(", ");
					strBuf002.append("SKIN_BREAK_FLG='");
					strBuf002.append(handover_skin_break_flg);
					strBuf002.append("' ");
				}

				// 皮肤破损部位
				if(!this.isBlank(handover_skin_break_position)){
					strBuf002.append(", ");
					strBuf002.append("SKIN_BREAK_POSITION='");
					strBuf002.append(handover_skin_break_position);
					strBuf002.append("' ");
				}

				// 手术当日end

				// 备血start
				// 血型
				if(!this.isBlank(handover_blood_type)){
					strBuf002.append(", ");
					strBuf002.append("BLOOD_TYPE='");
					strBuf002.append(handover_blood_type);
					strBuf002.append("' ");
				}

				// Rh
				if(!this.isBlank(handover_rh)){
					strBuf002.append(", ");
					strBuf002.append("RHPOSITIVE_FLG='");
					strBuf002.append(handover_rh);
					strBuf002.append("' ");
				}

				// 术前备注
				if(!this.isBlank(handover_ope_pre_mark)){
					strBuf002.append(", ");
					strBuf002.append("OPE_PRE_MARK='");
					strBuf002.append(handover_ope_pre_mark);
					strBuf002.append("' ");
				}
				// 备血end

				Date date=new Date();
				DateFormat format=new SimpleDateFormat("yyyyMMdd");
				String dateStr=format.format(date);

				String str001 = "WHERE MR_NO='"
						+ mrNo
						+ "' "
						+ "AND TRANSFER_DATE BETWEEN "
						+ "TO_DATE('"
						+ dateStr
						+ " 00:00:00','yyyymmdd hh24:mi:ss')"
						+ "AND "
						+ "TO_DATE('"
						+ dateStr
						+ " 23:59:59','yyyymmdd hh24:mi:ss') ";
				strBuf002.append(str001);
				TParm result002 = new TParm(TJDODBTool.getInstance().update(
						strBuf002.toString(), conn));

				if (result002.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 3.update INW_TRANSFERSHEET
				StringBuffer strBuf003 = new StringBuffer();
				strBuf003.append("UPDATE INW_TRANSFERSHEET SET ");   
				//状态STATUS_FLG 为 5
				strBuf003.append("STATUS_FLG='");
				strBuf003.append(5);
				strBuf003.append("', ");
				//交班人
				strBuf003.append("FROM_USER='");
				strBuf003.append(user1);
				strBuf003.append("', ");
				//接班人
				strBuf003.append("TO_USER='");
				strBuf003.append(user2);
				strBuf003.append("', ");
				//交接时间
				strBuf003.append("TRANSFER_DATE=TO_DATE('");
				strBuf003.append(transfer_Date+":"+"00");
				strBuf003.append("','yy-mm-dd hh24:mi:ss') " );
				/** 转入科室 */
				strBuf003.append(", ");
				strBuf003.append("TO_DEPT='");
				strBuf003.append(dept);
				strBuf003.append("' ");
				//根据病案号 更新所有 接受状态
				//YYYY-MM-DD HH:MM:SS
				String dstrYMD = sysDate.toString().substring(0, 11);
				strBuf003.append(" WHERE TRANSFER_CLASS= 'WO' AND STATUS_FLG = '4'  " );
				//CRE_DATE    
				strBuf003.append(" AND CRE_DATE BETWEEN TO_DATE('"+dstrYMD+"00:00:00','yy-mm-dd hh24:mi:ss') ");
				strBuf003.append(" AND TO_DATE('"+dstrYMD+"23:59:59','yy-mm-dd hh24:mi:ss') AND MR_NO = '"+mrNo+"' ");
				TParm result003 = new TParm(TJDODBTool.getInstance().update(
						strBuf003.toString(), conn)); 
				if (result003.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 4.update OPE_IPROOM
				StringBuffer strBuf004 = new StringBuffer();
				strBuf004.append("UPDATE OPE_IPROOM SET ");
				/** roomNo */
				strBuf004.append("OPBOOK_SEQ='");
				strBuf004.append(opeBookSeq);          
				strBuf004.append("' ");
				
				strBuf004.append(" WHERE ROOM_NO='");
				strBuf004.append(roomNo);      
				strBuf004.append("'");
				//System.out.println("---strBuf.toString()--:"+strBuf.toString());
				TParm result004 = new TParm(TJDODBTool.getInstance().update(
						strBuf004.toString(), conn));
				if (result004.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}
				conn.commit();
				conn.close();
				Map map=new HashMap();
				map.put("request","护士长权限，保存成功！");		
				JSONArray array = JSONArray.fromObject(map);
				JSONObject obj = new JSONObject();
				obj.put("OpePatConnectJson", array);    
				//返回 一个 保存成功 字样的 json串
				out.print("callback("+obj.toString()+")");       
				out.flush();
				out.close();
			}else{// 非护士长权限
				TConnection conn= TDBPoolManager.getInstance().getConnection();
				//1.update OPE_OPBOOK
				StringBuffer strBuf001 = new StringBuffer();
				strBuf001.append("UPDATE OPE_OPBOOK SET ");

				/** transferUser 交班人*/
				strBuf001.append("HANDOVER_USER='");
				strBuf001.append(user1);
				strBuf001.append("', "); 

				/** transfer_Date  交班时间*/     
				strBuf001.append("HANDOVER_DATE=TO_DATE('");
				strBuf001.append(transfer_Date+":"+"00");
				strBuf001.append("','yy-mm-dd hh24:mi:ss'),");

				/** transferUser 接班人*/
				strBuf001.append("TRANSFER_USER='");
				strBuf001.append(user2);
				strBuf001.append("', ");

				/** transfer_Date  接班时间*/    
				strBuf001.append("TRANSFER_DATE=TO_DATE('");
				strBuf001.append(transfer_Date+":"+"00");
				strBuf001.append("','yy-mm-dd hh24:mi:ss') ");
				
				strBuf001.append(" WHERE OPBOOK_SEQ='");
				strBuf001.append(opeBookSeq);
				strBuf001.append("' AND CANCEL_FLG <> 'Y' ");
				TParm result001 = new TParm(TJDODBTool.getInstance().update(
						strBuf001.toString(), conn)); 
				if (result001.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 2. update INW_TransferSheet_WO
				StringBuffer strBuf002 = new StringBuffer();
				strBuf002.append("UPDATE INW_TransferSheet_WO SET ");
				//交接时间
				strBuf002.append("TRANSFER_DATE=TO_DATE('");
				strBuf002.append(transfer_Date+":"+"00");
				strBuf002.append("','yy-mm-dd hh24:mi:ss') " );
				
				Date date=new Date();
				DateFormat format=new SimpleDateFormat("yyyyMMdd");
				String dateStr=format.format(date);

				String str001 = "WHERE MR_NO='"
						+ mrNo
						+ "' "
						+ "AND TRANSFER_DATE BETWEEN "
						+ "TO_DATE('"
						+ dateStr
						+ " 00:00:00','yyyymmdd hh24:mi:ss')"
						+ "AND "
						+ "TO_DATE('"
						+ dateStr
						+ " 23:59:59','yyyymmdd hh24:mi:ss') ";
				strBuf002.append(str001);
				TParm result002 = new TParm(TJDODBTool.getInstance().update(
						strBuf002.toString(), conn));

				if (result002.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 3.update INW_TRANSFERSHEET
				StringBuffer strBuf003 = new StringBuffer();
				strBuf003.append("UPDATE INW_TRANSFERSHEET SET ");   
				//状态STATUS_FLG 为 5
				strBuf003.append("STATUS_FLG='");
				strBuf003.append(5);
				strBuf003.append("', ");
				//交班人
				strBuf003.append("FROM_USER='");
				strBuf003.append(user1);
				strBuf003.append("', ");
				//接班人
				strBuf003.append("TO_USER='");
				strBuf003.append(user2);
				strBuf003.append("', ");
				//交接时间
				strBuf003.append("TRANSFER_DATE=TO_DATE('");
				strBuf003.append(transfer_Date+":"+"00");
				strBuf003.append("','yy-mm-dd hh24:mi:ss') " );
				
				//根据病案号 更新所有 接受状态
				//YYYY-MM-DD HH:MM:SS
				String dstrYMD = sysDate.toString().substring(0, 11);
				strBuf003.append(" WHERE TRANSFER_CLASS= 'WO' AND STATUS_FLG = '4'  " );
				//CRE_DATE    
				strBuf003.append(" AND CRE_DATE BETWEEN TO_DATE('"+dstrYMD+"00:00:00','yy-mm-dd hh24:mi:ss') ");
				strBuf003.append(" AND TO_DATE('"+dstrYMD+"23:59:59','yy-mm-dd hh24:mi:ss') AND MR_NO = '"+mrNo+"' ");
				TParm result003 = new TParm(TJDODBTool.getInstance().update(
						strBuf003.toString(), conn)); 
				if (result003.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}

				// 4.update OPE_IPROOM
				StringBuffer strBuf004 = new StringBuffer();
				strBuf004.append("UPDATE OPE_IPROOM SET ");
				/** roomNo */
				strBuf004.append("OPBOOK_SEQ='");
				strBuf004.append(opeBookSeq);          
				strBuf004.append("' ");
				
				strBuf004.append(" WHERE ROOM_NO='");
				strBuf004.append(roomNo);      
				strBuf004.append("'");
				//System.out.println("---strBuf.toString()--:"+strBuf.toString());
				TParm result004 = new TParm(TJDODBTool.getInstance().update(
						strBuf004.toString(), conn));
				if (result004.getErrCode() < 0) {
					conn.rollback();
					conn.close();		
					Map map=new HashMap();
					map.put("request","保存失败！");
					JSONArray array = JSONArray.fromObject(map);
					JSONObject obj = new JSONObject();
					obj.put("OpePatConnectJson", array);  
					out.print("callback("+obj.toString()+")");       
					out.flush();
					out.close();
					return;
				}
				conn.commit();
				conn.close();
				Map map=new HashMap();
				map.put("request","非护士长权限，保存成功！");		
				JSONArray array = JSONArray.fromObject(map);
				JSONObject obj = new JSONObject();
				obj.put("OpePatConnectJson", array);    
				//返回 一个 保存成功 字样的 json串
				out.print("callback("+obj.toString()+")");       
				out.flush();
				out.close();
			}
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
