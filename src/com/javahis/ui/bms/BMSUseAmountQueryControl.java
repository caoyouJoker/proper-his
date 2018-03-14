package com.javahis.ui.bms;

import java.sql.Timestamp;

import jdo.bms.BMSBloodTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ��Ѫ��ͳ��
 * </p>
 * 
 * <p>
 * Description: ��Ѫ��ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.4.11
 * @version 1.0
 */
public class BMSUseAmountQueryControl extends TControl {

	private TTable table;
	private String in_date_s = "";
	private String in_date_e = "";
	private String out_date_s = "";
	private String out_date_e = "";
	private String bld_code = "";
	private String bld_type= "";
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.onInitPage();
	}
	
	/**
	 * ҳ��ؼ���ʼ��
	 */
	private void onInitPage() {
		this.clearValue("BLD_CODE;BLOOD_TYPE;IN_DATE_S;IN_DATE_E");
		table.setParmValue(new TParm());
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("OUT_DATE_S", now);
		this.setValue("OUT_DATE_E", now);
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryParm = this.getQueryParm();
		
		// ��ѯ��Ѫ����ϸ
		TParm detailResult = BMSBloodTool.getInstance().queryBloodUseDetail(queryParm);
		
		if (detailResult.getErrCode() < 0) {
			this.messageBox("��ѯ��Ѫ����ϸ���ݴ���");
			err("ERR:" + detailResult.getErrCode() + detailResult.getErrText()
	                + detailResult.getErrName());
			return;
		} else if (detailResult.getCount() > 0) {
			// ���ѪƷΪ������������ϸ������������
			if (StringUtils.isEmpty(this.getValueString("BLD_CODE"))) {
				queryParm.setData("BLD_CODE", "02");
			}
			// ��Ѫ������ͳ�Ʋ�ѯ(��������С����)
			TParm amountResult = BMSBloodTool.getInstance().queryBloodUseAmount(queryParm);
			
			if (amountResult.getErrCode() < 0) {
				this.messageBox("��ѯ��Ѫ���������ݴ���");
				err("ERR:" + amountResult.getErrCode() + amountResult.getErrText()
		                + amountResult.getErrName());
				return;
			}
			
			TParm sortParm = new TParm();
			int amountCount = amountResult.getCount();
			int detailCount = detailResult.getCount();
			String caseNo = "";
			
			// ����ִ��ѪƷ���͵��ܼƴ�С��������
			for (int i = 0; i < amountCount; i++) {
				caseNo = "";
				for (int j = 0; j < detailCount; j++) {
					if (StringUtils.equals(detailResult.getValue("CASE_NO", j),
							amountResult.getValue("CASE_NO", i))) {
						// �ҵ���ƥ�������
						caseNo = amountResult.getValue("CASE_NO", i);
						
						sortParm.addData("BLOOD_NO", detailResult.getValue("BLOOD_NO", j));
						sortParm.addData("BLD_CODE", detailResult.getValue("BLD_CODE", j));
						sortParm.addData("BLDCODE_DESC", detailResult.getValue("BLDCODE_DESC", j));
						sortParm.addData("BLOOD_VOL", detailResult.getValue("BLOOD_VOL", j));
						sortParm.addData("UNIT_CHN_DESC", detailResult.getValue("UNIT_CHN_DESC", j));
						sortParm.addData("BLD_TYPE", detailResult.getValue("BLD_TYPE", j));
						sortParm.addData("MR_NO", detailResult.getValue("MR_NO", j));
						sortParm.addData("CASE_NO", detailResult.getValue("CASE_NO", j));
						sortParm.addData("PAT_NAME", detailResult.getValue("PAT_NAME", j));
						sortParm.addData("IN_DATE", detailResult.getTimestamp("IN_DATE", j));
						sortParm.addData("OUT_DATE", detailResult.getTimestamp("OUT_DATE", j));
						sortParm.addData("OUT_USER", detailResult.getValue("OUT_USER", j));
					} else {
						// ����detailResult�ǰ���CASE_NO����ģ����ҵ���ƥ������ݺ󣬱���caseNo������ֵ
						// ��֮�����ҵ���ƥ���������Ϊ��һ���˵ģ�����ȥ��ѭ���򲻱���ִ�У������Ч��
						if (StringUtils.isNotEmpty(caseNo)) {
							break;
						}
					}
				}
			}
			
			sortParm.setCount(sortParm.getCount("BLOOD_NO"));
			
			// ����ѪƷ���з���ϼ�
			TParm result = this.sumBloodByCode(sortParm);
			
			table.setParmValue(result);
		} else {
			this.messageBox("��������");
			table.setParmValue(new TParm());
			return;
		}
	}
	
	/**
	 * ��ȡ����ؼ�����
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isNotEmpty(this.getValueString("BLD_CODE"))) {
			parm.setData("BLD_CODE", this.getValueString("BLD_CODE"));
			this.setBld_code( this.getValueString("BLD_CODE"));
		} else {
			this.setBld_code("");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("BLOOD_TYPE"))) {
			parm.setData("BLOOD_TYPE", this.getValueString("BLOOD_TYPE"));
			this.setBld_type(this.getText("BLOOD_TYPE"));
		} else {
			this.setBld_type("");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("IN_DATE_S"))) {
			parm.setData("IN_DATE_S", this.getValueString("IN_DATE_S")
					.substring(0, 10).replace("-", ""));
			this.setIn_date_s(this.getValueString("IN_DATE_S").substring(0,10));
		} else {
			this.setIn_date_s("");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("IN_DATE_E"))) {
			parm.setData("IN_DATE_E", this.getValueString("IN_DATE_E")
					.substring(0, 10).replace("-", ""));
			this.setIn_date_e(this.getValueString("IN_DATE_E").substring(0,10));
		} else {
			this.setIn_date_e("");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("OUT_DATE_S"))) {
			parm.setData("OUT_DATE_S", this.getValueString("OUT_DATE_S")
					.substring(0, 10).replace("-", ""));
			this.setOut_date_s(this.getValueString("OUT_DATE_S").substring(0,10));
		} else {
			this.setOut_date_s("");
		}

		if (StringUtils.isNotEmpty(this.getValueString("OUT_DATE_E"))) {
			parm.setData("OUT_DATE_E", this.getValueString("OUT_DATE_E")
					.substring(0, 10).replace("-", ""));
			this.setOut_date_e(this.getValueString("OUT_DATE_E").substring(0,10));
		} else {
			this.setOut_date_e("");
		}
		
		return parm;
	}
	
	/**
	 * ����ѪƷ���з���ϼ�
	 */
	private TParm sumBloodByCode(TParm parm) {
		TParm result = new TParm();
		
		double sumVol = 0;
		int count = parm.getCount();
		for (int k = 0; k < count; k++) {
			result.addData("BLOOD_NO", parm.getValue("BLOOD_NO", k));
			result.addData("BLD_CODE", parm.getValue("BLD_CODE", k));
			result.addData("BLDCODE_DESC", parm.getValue("BLDCODE_DESC", k));
			result.addData("BLOOD_VOL", parm.getValue("BLOOD_VOL", k));
			result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", k));
			result.addData("BLD_TYPE", parm.getValue("BLD_TYPE", k));
			result.addData("MR_NO", parm.getValue("MR_NO", k));
			result.addData("CASE_NO", parm.getValue("CASE_NO", k));
			result.addData("PAT_NAME", parm.getValue("PAT_NAME", k));
			result.addData("IN_DATE", parm.getTimestamp("IN_DATE", k));
			result.addData("OUT_DATE", parm.getTimestamp("OUT_DATE", k));
			result.addData("OUT_USER", parm.getValue("OUT_USER", k));
			
			if (k == 0) {
				sumVol = parm.getDouble("BLOOD_VOL", k);
			}
			
			if (k < count - 1) {
				if (StringUtils.equals(parm.getValue("CASE_NO", k),
						parm.getValue("CASE_NO", k + 1))
						&& StringUtils.equals(parm
								.getValue("BLD_CODE", k), parm.getValue(
								"BLD_CODE", k + 1))) {
					sumVol = sumVol + parm.getDouble("BLOOD_VOL", k + 1);
				} else {
					result.addData("BLOOD_NO", "");
					result.addData("BLD_CODE", "");
					result.addData("BLDCODE_DESC", "�ϼ�");
					result.addData("BLOOD_VOL", sumVol);
					result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", k));
					result.addData("BLD_TYPE", "");
					result.addData("MR_NO", "");
					result.addData("CASE_NO", "");
					result.addData("PAT_NAME", "");
					result.addData("IN_DATE", "");
					result.addData("OUT_DATE", "");
					result.addData("OUT_USER", "");
					
					sumVol = parm.getDouble("BLOOD_VOL", k + 1);
				}
			} else {
				result.addData("BLOOD_NO", "");
				result.addData("BLD_CODE", "");
				result.addData("BLDCODE_DESC", "�ϼ�");
				result.addData("BLOOD_VOL", sumVol);
				result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", k));
				result.addData("BLD_TYPE", "");
				result.addData("MR_NO", "");
				result.addData("CASE_NO", "");
				result.addData("PAT_NAME", "");
				result.addData("IN_DATE", "");
				result.addData("OUT_DATE", "");
				result.addData("OUT_USER", "");
			}
		}
		
		return result;
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.onInitPage();
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿ɴ�ӡ���ݣ�");
			return;
		}
		TParm data = new TParm();
		//���ñ�ͷʱ��
		//�������ʱ��
		String in_date = "";
		if(this.getIn_date_s().length() > 0) {
			in_date += this.getIn_date_s() + " �� ";
		}
		if(this.getIn_date_e().length() > 0) {
			if(in_date.length() <= 0) {
				in_date += "0000-00-00 �� ";
			}
			in_date += this.getIn_date_e();
		} 
		
		
		String out_date = "";
		if(this.getOut_date_s().length() > 0) {
			out_date += this.getOut_date_s() + " �� ";
		}
		if(this.getOut_date_e().length() > 0) {
			if(out_date.length() <= 0) {
				out_date += "0000-00-00 �� ";
			}
			out_date += this.getOut_date_e();
		}
		data.setData("TITLE", "TEXT", "��Ѫ��ͳ�Ʊ�");
		data.setData("IN_DATE", "TEXT", in_date);
		data.setData("OUT_DATE", "TEXT", out_date);
		data.setData("BLD_CODE","TEXT",this.getBld_code());
		data.setData("BLOOD_TYPE","TEXT",this.getBld_type());
		
		//���ñ������ "BLDCODE_DESC;BLOOD_VOL;UNIT_CHN_DESC;BLD_TYPE;MR_NO;IN_DATE;PAT_NAME;OUT_DATE;OUT_USER"
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for(int i = 0; i< tableParm.getCount(); i++) {
			parm.addData("BLDCODE_DESC", tableParm.getData("BLDCODE_DESC", i));
			parm.addData("BLOOD_VOL", tableParm.getData("BLOOD_VOL", i));
			parm.addData("UNIT_CHN_DESC", tableParm.getData("UNIT_CHN_DESC", i));
			parm.addData("BLD_TYPE", tableParm.getData("BLD_TYPE", i));
			parm.addData("MR_NO", tableParm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
			parm.addData("IN_DATE", tableParm.getData("IN_DATE", i));
			parm.addData("OUT_DATE", tableParm.getData("OUT_DATE", i));
			parm.addData("OUT_USER", tableParm.getData("OUT_USER", i));
		}
		parm.setCount(parm.getCount("BLDCODE_DESC"));
		parm.addData("SYSTEM","COLUMNS","BLDCODE_DESC");
		parm.addData("SYSTEM","COLUMNS","BLOOD_VOL");
		parm.addData("SYSTEM","COLUMNS","UNIT_CHN_DESC");
		parm.addData("SYSTEM","COLUMNS","BLD_TYPE");
		parm.addData("SYSTEM","COLUMNS","MR_NO");
		parm.addData("SYSTEM","COLUMNS","PAT_NAME");
		parm.addData("SYSTEM","COLUMNS","IN_DATE");
		parm.addData("SYSTEM","COLUMNS","OUT_DATE");
		parm.addData("SYSTEM","COLUMNS","OUT_USER");
		data.setData("TABLE", parm.getData());
	    
		//���ñ�β����
		data.setData("OPT_USER", "TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		this.openPrintDialog("%ROOT%\\config\\prt\\bms\\BMSUseAmountQuery.jhw", data);
	}
	
	/**
	 * ����Excel
	 */
	public void onExport(){
		if(table.getRowCount() <= 0) {
			this.messageBox("�޿ɵ���Excel�����ݣ�");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "��Ѫ��ͳ�Ʊ�");
	}

	public String getIn_date_s() {
		return in_date_s;
	}

	public void setIn_date_s(String in_date_s) {
		this.in_date_s = in_date_s;
	}

	public String getIn_date_e() {
		return in_date_e;
	}

	public void setIn_date_e(String in_date_e) {
		this.in_date_e = in_date_e;
	}

	public String getOut_date_s() {
		return out_date_s;
	}

	public void setOut_date_s(String out_date_s) {
		this.out_date_s = out_date_s;
	}

	public String getOut_date_e() {
		return out_date_e;
	}

	public void setOut_date_e(String out_date_e) {
		this.out_date_e = out_date_e;
	}

	public String getBld_code() {
		return bld_code;
	}

	public void setBld_code(String bld_code) {
		this.bld_code = bld_code;
	}

	public String getBld_type() {
		return bld_type;
	}

	public void setBld_type(String bld_type) {
		this.bld_type = bld_type;
	}
	
	
}
