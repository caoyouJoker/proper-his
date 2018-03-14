package com.javahis.ui.med;

import java.sql.*;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.dongyang.ui.TTextField;
import com.javahis.system.textFormat.TextFormatRegClinicRoom;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.system.combo.TComboADMType;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;

import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;

import jdo.sys.PatTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdo.hl7.Hl7Communications;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class MEDReSendControl extends TControl {
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 部门
	 */
	private String deptCode;
	/**
	 * 病区
	 */
	private String stationCode;
	/**
	 * 诊区
	 */
	private String clinicarea;
	/**
	 * 诊室
	 */
	private String clinicRoom;
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 医令分类
	 */
	private String cat1Type;
	/**
	 * 医令细分类
	 */
	private String orderCat1Code;
	/**
	 * 条码号
	 */
	private String applayNo;
	/**
	 * 起始时间
	 */
	private Timestamp startDate;
	/**
	 * 终止时间
	 */
	private Timestamp endDate;
	/**
	 * 初始状态类型
	 */
	private int initTypeStatus = -1;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		Object obj = this.getParameter();
		// this.messageBox_(obj);
		if (obj != null) {
			// 调用
			if (obj instanceof TParm) {
				initTypeStatus = 0;
				TParm parm = (TParm) obj;
				this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setValue("ADM_TYPE", parm.getValue("ADM_TYPE"));
				this.setDeptCode(parm.getValue("DEPT_CODE"));
				this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setValue("MR_NO", parm.getValue("MR_NO"));
				this.setApplayNo(parm.getValue("LAB_NO"));
				this.setValue("LAB_NO", parm.getValue("LAB_NO"));
			} else {
				initTypeStatus = 1;
				/**
				 * 门急住别
				 */
				this.setValue("ADM_TYPE", "" + obj);
				this.setAdmType("" + obj);
			}
		}
		// else{
		// initTypeStatus = 1;
		// this.setValue("ADM_TYPE","I");
		// this.setAdmType("I");
		// }
		// 初始化页面
		initPage();
		/**
		 * 初始化事件
		 */
		initEvent();
	}

	/**
	 * 初始化事件
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
	}

	/**
	 * 点选事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("APPLICATION_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp toDate = StringTool.getTimestamp(
				StringTool.getString(sysDate, "yyyy-MM-dd 00:00:00"),
				"yyyy-MM-dd HH:mm:ss");
		if (initTypeStatus == 0) {
			((TComboADMType) this.getComponent("ADM_TYPE")).setEnabled(false);
			// 起始时间
			this.setValue("START_DATE", toDate);
			// 终止时间
			this.setValue("END_DATE", StringTool.rollDate(toDate, 1));
			if ("O".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("E".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("I".equals(this.getAdmType())) {
				// 诊区
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// 诊间
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
			}
			if ("H".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
		}
		if (initTypeStatus == 1) {
			((TComboADMType) this.getComponent("ADM_TYPE")).setEnabled(false);
			// 起始时间
			this.setValue("START_DATE", toDate);
			// 终止时间
			this.setValue("END_DATE", StringTool.rollDate(toDate, 1));
			if ("O".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("E".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("I".equals(this.getAdmType())) {
				// 诊区
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// 诊间
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
			}
			if ("H".equals(this.getAdmType())) {
				// 病区
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// 诊区
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// 诊间
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
				// 住院号
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
		}
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (this.getValueString("MR_NO").trim().length() != 0) {
			// modify by huangtt 20160929 EMPI患者查重提示 start
			String mrNo = PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO"));
			this.setValue("MR_NO", mrNo);
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("MR_NO", pat.getMrNo());// 病案号

			}
			// modify by huangtt 20160929 EMPI患者查重提示 end
			
			
		}
		if (this.getValueString("IPD_NO").trim().length() != 0) {
			this.setValue(
					"IPD_NO",
					PatTool.getInstance().checkIpdno(
							this.getValueString("IPD_NO")));
		}
		String sqlStr = getSQL();
		// System.out.println(""+sqlStr);
		TParm tableParm = new TParm(this.getDBTool().select(sqlStr));
		TParm parm = new TParm();
		int count=0;
		if (this.admType.equals("I")) {
			for (int i = 0; i < tableParm.getCount(); i++) {
				TParm parmRow = tableParm.getRow(i);
				if (!this.getBillFlg(parmRow))
					continue;
				parm.addData("FLG", parmRow.getData("FLG"));
				parm.addData("PAT_NAME", parmRow.getData("PAT_NAME"));
				parm.addData("ORDER_DESC", parmRow.getData("ORDER_DESC"));
				parm.addData("ORDER_CAT1_CODE", parmRow.getData("ORDER_CAT1_CODE"));
				parm.addData("EXEC_DEPT_CODE", parmRow.getData("EXEC_DEPT_CODE"));
				parm.addData("ORDER_DR_CODE", parmRow.getData("ORDER_DR_CODE"));
				parm.addData("MR_NO", parmRow.getData("MR_NO"));
				parm.addData("IPD_NO", parmRow.getData("IPD_NO"));
				parm.addData("CASE_NO", parmRow.getData("CASE_NO"));
				parm.addData("APPLICATION_NO", parmRow.getData("APPLICATION_NO"));
				parm.addData("SEQ_NO", parmRow.getData("SEQ_NO"));
				parm.addData("ORDER_NO", parmRow.getData("ORDER_NO"));
				parm.addData("ADM_TYPE", parmRow.getData("ADM_TYPE"));
				parm.addData("CAT1_TYPE", parmRow.getData("CAT1_TYPE"));
			}
		}else{
			parm=tableParm;
		}
		// this.messageBox_(tableParm);
		this.getTTable(TABLE).setParmValue(parm);
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 得到查询语句
	 * 
	 * @return String
	 */
	public String getSQL() {
		int count = 0;
		String sql = "SELECT 'N' AS FLG,PAT_NAME,ORDER_DESC,ORDER_CAT1_CODE,EXEC_DEPT_CODE,ORDER_DR_CODE,MR_NO,IPD_NO,CASE_NO,APPLICATION_NO,ORDER_NO,SEQ_NO,ADM_TYPE,CAT1_TYPE FROM MED_APPLY";
		// 医令分类
		if (this.getValueString("CAT1_TYPE").length() != 0) {
			sql += " WHERE CAT1_TYPE='" + this.getValueString("CAT1_TYPE")
					+ "' ";
			count++;
		}
		// 门急住别
		if (this.getValueString("ADM_TYPE").length() != 0) {
			if (count > 0) {
				sql += " AND ADM_TYPE='" + this.getValueString("ADM_TYPE")
						+ "' ";
			} else {
				sql += " WHERE ADM_TYPE='" + this.getValueString("ADM_TYPE")
						+ "' ";
			}
			count++;
		}
		// 医令细分类
		if (this.getValueString("ORDER_CAT1_CODE").length() != 0) {
			if (count > 0) {
				sql += " AND ORDER_CAT1_CODE='"
						+ this.getValueString("ORDER_CAT1_CODE") + "' ";
			} else {
				sql += " WHERE ORDER_CAT1_CODE='"
						+ this.getValueString("ORDER_CAT1_CODE") + "' ";
			}
			count++;
		}
		// 住院号
		if (this.getValueString("MR_NO").length() != 0) {
			if (count > 0) {
				sql += " AND MR_NO='" + this.getValueString("MR_NO") + "' ";
			} else {
				sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "' ";
			}
			count++;
		}
		// 条码号
		if (this.getValueString("LAB_NO").length() != 0) {
			if (count > 0) {
				sql += " AND APPLICATION_NO='" + this.getValueString("LAB_NO")
						+ "' ";
			} else {
				sql += " WHERE APPLICATION_NO='"
						+ this.getValueString("LAB_NO") + "' ";
			}
			count++;
		}
		// 科室
		if (this.getValueString("DEPT_CODE").length() != 0) {
			if (count > 0) {
				sql += " AND DEPT_CODE='" + this.getValueString("DEPT_CODE")
						+ "' ";
			} else {
				sql += " WHERE DEPT_CODE='" + this.getValueString("DEPT_CODE")
						+ "' ";
			}
			count++;
		}
		// 起始时间
		if (this.getValueString("START_DATE").length() != 0
				&& this.getValueString("END_DATE").length() != 0) {
			String sDate = StringTool.getString(
					(Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(
					(Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");
			if (count > 0) {
				sql += " AND ORDER_DATE BETWEEN TO_DATE('" + sDate
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
						+ "','YYYYMMDDHH24MISS') ";
			} else {
				sql += " WHERE ORDER_DATE BETWEEN TO_DATE('" + sDate
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
						+ "','YYYYMMDDHH24MISS') ";
			}
			count++;
		}
		// 门急部分
		if ("O".equals(this.getValueString("ADM_TYPE"))) {
			// 诊区
			if (this.getValueString("CLINICAREA_CODE").length() != 0) {
				if (count > 0) {
					sql += " AND CLINICAREA_CODE='"
							+ this.getValueString("CLINICAREA_CODE") + "' ";
				} else {
					sql += " WHERE CLINICAREA_CODE='"
							+ this.getValueString("CLINICAREA_CODE") + "' ";
				}
				count++;
			}
			// 诊间
			if (this.getValueString("CLINICROOM_NO").length() != 0) {
				if (count > 0) {
					sql += " AND CLINICROOM_NO='"
							+ this.getValueString("CLINICROOM_NO") + "' ";
				} else {
					sql += " WHERE CLINICROOM_NO='"
							+ this.getValueString("CLINICROOM_NO") + "' ";
				}
				count++;
			}
		}
		// 住院部分
		if ("I".equals(this.getValueString("ADM_TYPE"))) {
			// 住院号
			if (this.getValueString("IPD_NO").length() != 0) {
				if (count > 0) {
					sql += " AND IPD_NO='" + this.getValueString("IPD_NO")
							+ "' ";
				} else {
					sql += " WHERE IPD_NO='" + this.getValueString("IPD_NO")
							+ "' ";
				}
				count++;
			}
			// 病区
			if (this.getValueString("STATION_CODE").length() != 0) {
				if (count > 0) {
					sql += " AND STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "' ";
				} else {
					sql += " WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "' ";
				}
				count++;
			}
		}
		// 已发送
		if (this.getTRadioButton("SENDTRUE").isSelected()) {
			if (count > 0) {
				sql += " AND SEND_FLG >= 2 AND STATUS <> 9";
			} else {
				sql += " WHERE SEND_FLG >= 2 AND STATUS <> 9";
			}
			count++;
		}
		// 未发送
		if (this.getTRadioButton("SENDFALSE").isSelected()) {
			if (count > 0) {
				sql += " AND SEND_FLG < 2 AND STATUS <> 9";
			} else {
				sql += " WHERE SEND_FLG < 2 AND STATUS <> 9";
			}

			count++;
		}
		if (!admType.equals("I")) {
			if (count > 0) {
				sql += " AND BILL_FLG='Y'";
			} else {
				sql += " WHERE  BILL_FLG='Y' ";
			}
		}
		// System.out.println("SQL=="+sql);
		// System.out.println("SQLSEND"+sql);
		return sql;
	}

	private boolean getBillFlg(TParm parm) {
		boolean flg = false;
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String orderSeq = parm.getValue("SEQ_NO");
		String sql = " SELECT BILL_FLG FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
				+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + orderSeq
				+ "'";
		TParm inparm = new TParm(this.getDBTool().select(sql));
		flg = inparm.getBoolean("BILL_FLG", 0);
		return flg;
	}

	/**
	 * 得到TRdioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 重送
	 */
	public void onSendRe() {
		List orderList = new ArrayList();
		TParm parm = this.getTTable(TABLE).getParmValue();
		if (parm == null)
			return;
        // ======================// add by wanglong 20130311
        Set<String> mrSet = new HashSet<String>();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            mrSet.add(parm.getValue("CASE_NO", i));
        }
        TParm coverTparm = new TParm();
        if ("H".equals(this.getAdmType())) {
            String mrList = "";
            String sql =
                    "SELECT B.CASE_NO, A.MR_NO, A.PAT_NAME FROM HRM_CONTRACTD A, HRM_PATADM B "
                            + " WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + " AND A.COVER_FLG <> 'Y' AND B.CASE_NO IN (#)";
            for (String string : mrSet) {
                mrList += "'" + string + "',";
            }
            mrList = mrList.substring(0, mrList.length() - 1);
            sql = sql.replaceFirst("#", mrList);
            // System.out.println("==============sql============" + sql);
            coverTparm = new TParm(TJDODBTool.getInstance().select(sql));
            if (coverTparm.getErrCode() != 0) {
                this.messageBox("查询病患报到情况出错");
                return;
            }
            if (coverTparm.getCount() > 0) {
                for (int i = 0; i < coverTparm.getCount(); i++) {
                    this.messageBox(coverTparm.getValue("PAT_NAME", i) + " 未报到，其条码不能重送");
                    String caseNo = coverTparm.getValue("CASE_NO", i);
                    for (int j = parm.getCount() - 1; j > -1; j--) {
                        if (parm.getValue("CASE_NO", j).equals(caseNo)) {
                            parm.removeRow(j);
                        }
                    }
                }
                parm.setCount(parm.getCount("CASE_NO"));
            }
        }
        // ===============add end
		int rowCount = parm.getCount("CASE_NO");
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			TParm action = new TParm();
			action.setData("ADM_TYPE", temp.getValue("ADM_TYPE"));
			action.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			action.setData("PAT_NAME", temp.getValue("PAT_NAME"));
			action.setData("CASE_NO", temp.getValue("CASE_NO"));
			action.setData("LAB_NO", temp.getValue("APPLICATION_NO"));
			action.setData("ORDER_NO", temp.getValue("ORDER_NO"));
			action.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			action.setData("FLG", "0");
			orderList.add(action);
		}
		if (orderList.size() <= 0) {
			this.messageBox("没有需要发送的消息！");
			return;
		}
		TParm result = Hl7Communications.getInstance().Hl7Message(orderList);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		} else {
			this.messageBox("发送成功！");
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("CAT1_TYPE;ORDER_CAT1_CODE;MR_NO;IPD_NO;DEPT_CODE;STATION_CODE;CLINICAREA_CODE;CLINICROOM_NO;LAB_NO;ALLCHECK");
		onInit();
		this.getTRadioButton("SENDFALSE").setSelected(true);
		this.getTTable(TABLE).removeRowAll();
	}

	/**
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

//	/**
//	 * 全选
//	 */
//	public void onAll() {
//		TParm parm = this.getTTable(TABLE).getParmValue();
//		int rowCount = parm.getCount();
//		for (int i = 0; i < rowCount; i++) {
//			if (this.getTCheckBox("ALLCHECK").isSelected())
//				parm.setData("FLG", i, "Y");
//			else
//				parm.setData("FLG", i, "N");
//		}
//		this.getTTable(TABLE).setParmValue(parm);
//
//	}
//	
	 /**
     * 全选
     */
    public void onAll() {
    	TTable table = (TTable) this.getComponent("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
        	getTCheckBox("ALL_CHECK").setSelected(false);
            return;
        }
        TParm parm = table.getParmValue();
        if (getTCheckBox("ALL_CHECK").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("FLG", i, "Y");
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("FLG", i, "N");
            }
        }
        table.setParmValue(parm);
    }

	public String getAdmType() {
		return admType;
	}

	public String getApplayNo() {
		return applayNo;
	}

	public String getCat1Type() {
		return cat1Type;
	}

	public String getClinicarea() {
		return clinicarea;
	}

	public String getClinicRoom() {
		return clinicRoom;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getOrderCat1Code() {
		return orderCat1Code;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setApplayNo(String applayNo) {
		this.applayNo = applayNo;
	}

	public void setCat1Type(String cat1Type) {
		this.cat1Type = cat1Type;
	}

	public void setClinicarea(String clinicarea) {
		this.clinicarea = clinicarea;
	}

	public void setClinicRoom(String clinicRoom) {
		this.clinicRoom = clinicRoom;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setOrderCat1Code(String orderCat1Code) {
		this.orderCat1Code = orderCat1Code;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}
}
