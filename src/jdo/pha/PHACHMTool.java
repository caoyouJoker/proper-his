package jdo.pha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.hl7.Hl7Tool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title:普华中草药数据交互接口
 * </p>
 * 
 * <p>
 * Description:普华中草药数据交互接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author wangb
 * @version 1.0
 */
public class PHACHMTool extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static PHACHMTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PHACHMTool
	 */
	public static PHACHMTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new PHACHMTool();
		}
		return instanceObject;
	}
	
    /**
     * 中草药房接口_插入
     * 
     * @param parm TParm
     * @return TParm
     */
	public TParm executeInsert(TParm parm, Connection conn) {
		PreparedStatement pstmM = null;
		PreparedStatement pstmD = null;
		TParm result = new TParm();
		TParm parmM = parm.getParm("PARM_M");
		TParm parmD = parm.getParm("PARM_D");
		if (parmM == null) {
			result.setErr(-1, "传参失败", "医嘱主项传参失败");
			return result;
		}
		
		if (parmD == null) {
			result.setErr(-1, "传参失败", "医嘱细项传参失败");
			return result;
		}
		
		try {
			conn.setAutoCommit(false);
			
			// 插入主表
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("INSERT INTO DATA_PRESCRIPTION(ID,REGISTER_ID,NAME,SEX,AGE,TELE,EMAIL,");
			sbSql.append("DEPARTMENT_NAME,DOCTOR_NAME,PRESCRIPTION_NAME,PRESCRIBE_TIME,CREATOR_NAME,CREATION_TIME,");
			sbSql.append("VALUE_SN,VALUER_NAME,VALUATION_TIME,PRICE,QUANTITY,QUANTITY_DAY,PRICE_TOTAL,");
			sbSql.append("PAYMENT_TYPE,PAYMENT_STATUS,DATA_SOURCE,DEVICE_ID,PROCESS_STATUS,DESCRIPTION) ");
			sbSql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstmM = conn.prepareStatement(sbSql.toString());
			pstmM.setString(1, parmM.getValue("ID"));
			pstmM.setString(2, parmM.getValue("REGISTER_ID"));
			pstmM.setString(3, parmM.getValue("NAME"));
			pstmM.setString(4, parmM.getValue("SEX"));
			pstmM.setInt(5, parmM.getInt("AGE"));
			pstmM.setString(6, parmM.getValue("TELE"));
			pstmM.setString(7, parmM.getValue("EMAIL"));
			pstmM.setString(8, parmM.getValue("DEPARTMENT_NAME"));
			pstmM.setString(9, parmM.getValue("DOCTOR_NAME"));
			pstmM.setString(10, parmM.getValue("PRESCRIPTION_NAME"));
			pstmM.setTimestamp(11, parmM.getTimestamp("PRESCRIBE_TIME"));
			pstmM.setString(12, parmM.getValue("CREATOR_NAME"));
			pstmM.setTimestamp(13, parmM.getTimestamp("CREATION_TIME"));
			pstmM.setString(14, parmM.getValue("VALUE_SN"));
			pstmM.setString(15, parmM.getValue("VALUER_NAME"));
			pstmM.setTimestamp(16, parmM.getTimestamp("VALUATION_TIME"));
			pstmM.setDouble(17, parmM.getDouble("PRICE"));
			pstmM.setFloat(18, parmM.getFloat("QUANTITY"));
			pstmM.setFloat(19, parmM.getFloat("QUANTITY_DAY"));
			pstmM.setDouble(20, parmM.getDouble("PRICE_TOTAL"));
			pstmM.setString(21, parmM.getValue("PAYMENT_TYPE"));
			pstmM.setString(22, parmM.getValue("PAYMENT_STATUS"));
			pstmM.setString(23, parmM.getValue("DATA_SOURCE"));
			pstmM.setString(24, null);
			pstmM.setString(25, parmM.getValue("PROCESS_STATUS"));
			pstmM.setString(26, parmM.getValue("DESCRIPTION"));
			
			pstmM.executeUpdate();
			
			
			// 插入明细表
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO DATA_PRESCRIPTION_DETAIL(ID,\"NO\",GRANULE_ID,GRANULE_NAME,DOSE_HERB,EQUIVALENT,DOSE,PRICE) ");
			sbSql.append("VALUES(?,?,?,?,?,?,?,?)");
			
			pstmD = conn.prepareStatement(sbSql.toString());
			
			for (int i = 0; i < parmD.getCount("NO"); i++) {
				pstmD.setString(1, parmD.getValue("ID", i));
				pstmD.setInt(2, parmD.getInt("NO", i));
				pstmD.setString(3, parmD.getValue("GRANULE_ID", i));
				pstmD.setString(4, parmD.getValue("GRANULE_NAME", i));
				pstmD.setDouble(5, parmD.getDouble("DOSE_HERB", i));
				pstmD.setDouble(6, parmD.getDouble("EQUIVALENT", i));
				pstmD.setDouble(7, parmD.getDouble("DOSE", i));
				pstmD.setDouble(8, parmD.getDouble("PRICE", i));
				
				pstmD.addBatch();
				
			}
			pstmD.executeBatch();
			
			conn.commit();
			conn.setAutoCommit(true);
			pstmM.close();
			pstmD.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "插入失败", e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				result.setErr(-1, "事务回滚失败", e.getMessage());
				e1.printStackTrace();
			}
			
			// 关闭声明
            if (pstmM != null) {
                try {
                	pstmM.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            // 关闭声明
            if (pstmD != null) {
                try {
                	pstmD.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
 
            // 关闭链接对象
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            return result;
		} finally {
			// 关闭声明
            if (pstmM != null) {
                try {
                	pstmM.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            // 关闭声明
            if (pstmD != null) {
                try {
                	pstmD.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
 
            // 关闭链接对象
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
		}
		
		return result;
	}
	
	/**
	 * 查询医嘱处方数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryOrderInfo(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT A.*,B.PAT_NAME AS PATNAME,");
		sbSql.append(" CASE B.SEX_CODE WHEN '1' THEN '男' WHEN '2' THEN '女' ELSE '空' END AS SEX,");
		sbSql.append(" B.BIRTH_DATE AS BIRTHDATE,B.TEL_HOME,B.E_MAIL ");
		sbSql.append(" FROM OPD_ORDER A, SYS_PATINFO B WHERE 1 = 1  ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("RX_NO"))) {
			sbSql.append(" AND RX_NO = '");
			sbSql.append(parm.getValue("RX_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("SEQ_NO"))) {
			sbSql.append(" AND SEQ_NO IN (");
			sbSql.append(parm.getValue("SEQ_NO"));
			sbSql.append(") ");
		}

		sbSql.append(" AND A.MR_NO = B.MR_NO ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
     * 组装中草药插入数据
     * 
     * @param parm TParm
     * @return TParm
     */
	public TParm getChmInsertData(TParm parm) {
		TParm chmParm = new TParm();
		TParm parmM = new TParm();
		TParm parmD = new TParm();
		// 保留至小数点后两位
		DecimalFormat df = new DecimalFormat("0.00");
		TParm queryParm = new TParm();
		queryParm.setData("NHI_CTZ_FLG", "Y");
		// 查询身份数据
		TParm ctzParm = this.querySysCtz(queryParm);
		// 该处方签下的所有药品编码
		String orderCodes = "'"
				+ parm.getValue("ORDER_CODE").replaceAll("\\[", "").replaceAll(
						"\\]", "").replaceAll(",", "','").replaceAll(" ", "") + "'";
		queryParm.setData("ORDER_CODE", orderCodes);
		// 查询药品转换率
		TParm phaTransUnitParm = this.queryPhaTransunit(queryParm);
		
		// 实时处方（待处理处方）
		parmM.setData("AUTOID", null); // 自动编号
		parmM.setData("ID", parm.getValue("RX_NO", 0)); // 处方号
		parmM.setData("REGISTER_ID", ""); // 挂号单号
		parmM.setData("NAME", parm.getValue("PATNAME", 0)); // 患者姓名
		parmM.setData("SEX", parm.getValue("SEX", 0)); // 患者性别
		
		// 根据出生日期计算当前年龄
		String age = OdiUtil.showAge(parm.getTimestamp("BIRTHDATE", 0), SystemTool
				.getInstance().getDate());
		parmM.setData("AGE", age.split("岁")[0]); // 患者年龄
		parmM.setData("TELE", parm.getValue("TEL_HOME", 0)); // 联系电话
		parmM.setData("EMAIL", parm.getValue("E_MAIL", 0)); // 电邮
		parmM.setData("DEPARTMENT_NAME", Hl7Tool.getInstance().getDeptDesc(
				parm.getValue("DEPT_CODE", 0))); // 科室
		parmM.setData("DOCTOR_NAME", Hl7Tool.getInstance().getDrName(
				parm.getValue("DR_CODE", 0))); // 医生
		parmM.setData("PRESCRIPTION_NAME", ""); // 协定处方名称
		parmM.setData("PRESCRIBE_TIME", parm.getTimestamp("ORDER_DATE", 0)); // 开具处方时间
		parmM.setData("CREATOR_NAME", null); // 录入员
		parmM.setData("CREATION_TIME", null); // 录入时间
		parmM.setData("VALUE_SN", ""); // 划价单号
		parmM.setData("VALUER_NAME", ""); // 划价员名称
		parmM.setData("VALUATION_TIME", null); // 划价时间
		parmM.setData("PRICE", null); // 每剂单价
		parmM.setData("QUANTITY", parm.getInt("TAKE_DAYS", 0)*2); // 剂数（袋数）
		parmM.setData("QUANTITY_DAY", parm.getInt("TAKE_DAYS", 0)); // 剂数（付数）
		
		double sumPrice = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			sumPrice = sumPrice + parm.getDouble("AR_AMT", i);
		}
		parmM.setData("PRICE_TOTAL", df.format(sumPrice)); // 处方总价
		
		String paymentType = "自费";
		// OPD_ORDER表中三个身份只要有一个为医保身份则支付方式为医保
		List<String> nhiCtzList = new ArrayList<String>();
		for (int i = 0; i < ctzParm.getCount("CTZ_CODE"); i++) {
			nhiCtzList.add(ctzParm.getValue("CTZ_CODE", i));
		}
		if (nhiCtzList.contains(parm.getValue("CTZ1_CODE", 0))
				|| nhiCtzList.contains(parm.getValue("CTZ2_CODE", 0))
				|| nhiCtzList.contains(parm.getValue("CTZ3_CODE", 0))) {
			paymentType = "医保";
		}
		
		parmM.setData("PAYMENT_TYPE", paymentType); // 支付方式
		parmM.setData("PAYMENT_STATUS", "PAYED"); // 支付状态
		parmM.setData("DATA_SOURCE", "HIS"); // 处方数据来源
		parmM.setData("DEVICE_ID", null); // 调剂设备
		parmM.setData("PROCESS_STATUS", "NEW"); // 处理状态
		parmM.setData("DESCRIPTION", ""); // 说明

		// 当量
		double equivalent = 1;
		for (int i = 0; i < parm.getCount(); i++) {
			equivalent = 1;
			// 实时处方明细
			parmD.addData("ID", parm.getValue("RX_NO", i)); // 处方号
			parmD.addData("NO", parm.getValue("SEQ_NO", i)); // 序号
			parmD.addData("GRANULE_ID", parm.getValue("ORDER_CODE", i)); // 颗粒ID
			parmD.addData("GRANULE_NAME", parm.getValue("ORDER_DESC", i)); // 颗粒名称
			parmD.addData("DOSE_HERB", parm.getDouble("MEDI_QTY", i)); // 饮片剂量
			
			for (int j = 0; j < phaTransUnitParm.getCount(); j++) {
				if (StringUtils.equals(parm.getValue("ORDER_CODE", i),
						phaTransUnitParm.getValue("ORDER_CODE", j))) {
					// 根据该药品字典的开药配药转换率获得该药品当量
					equivalent = phaTransUnitParm.getDouble("MEDI_QTY", j);
					break;
				}
			}
			
			parmD.addData("EQUIVALENT", equivalent); // 当量
			parmD.addData("DOSE", parm.getDouble("MEDI_QTY", i) / equivalent); // 剂量
			parmD.addData("PRICE", df.format(parm.getDouble("OWN_PRICE", i)
					* (parm.getDouble("MEDI_QTY", i) / equivalent))); // 价格
		}
		
		chmParm.setData("PARM_M", parmM.getData());
		chmParm.setData("PARM_D", parmD.getData());
		return chmParm;
	}
	
	/**
	 * 查询身份数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm querySysCtz(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT * FROM SYS_CTZ WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("NHI_CTZ_FLG"))) {
			sbSql.append(" AND NHI_CTZ_FLG = '");
			sbSql.append(parm.getValue("NHI_CTZ_FLG"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 查询药品转换率
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryPhaTransunit(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT * FROM PHA_TRANSUNIT WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE IN (");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append(") ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
}
