package com.javahis.ui.erd;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 * <p>Title: �������ȴ�λ�󶨼����</p>
 *
 * <p>Description: �������ȴ�λ�󶨼����</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author wangqing 20170626
 *
 * @version 1.0
 */
public class ERDDynamicRcdMainContol2 extends TControl {
	/**
	 * ���˺�
	 */
	private String triageNo = "";
	/**
	 * �����
	 */
	private String caseNo = "";
	/**
	 * ������
	 */
	private String mrNo = "";

	/**
	 * ҽ�����á���ʿ���ñ�ǣ�NURSE����ʿ ��OPD��OPD_OUT������ҽ��
	 */
	private String flg = "";
	
	// ÿ�γ�ʼ������Ҫ��ʼ�������� 
	// the start
	/**
	 * �Ƿ�󶨴�λ
	 */
	private boolean isInsert = false;
	/**
	 * ����봲���󶨵���������
	 */
	private String erdregionCode = "";
	/**
	 * ����봲���󶨵Ĵ�λ
	 */
	private String bedNo = "";	
	// the end
	
	
	/**
	 * tab���
	 */
	private TTabbedPane tabPanel;
	// ��һҳǩ�Ŀؼ�
	/**
	 * ��λTTable
	 */
	private TTable table;	

	//�ڶ���ҳǩ�Ŀؼ�
	/**
	 * ��ԺTRadioButton
	 */
	private TRadioButton Radio0;
	/**
	 * תסԺTRadioButton
	 */
	private TRadioButton Radio1;
	/**
	 * �ٻ�TRadioButton
	 */
	private TRadioButton Radio2;
	/**
	 * ��Ժ��ʽTComboBox
	 */
	private TComboBox DISCHG_TYPE;
	/**
	 * ��Ժ����TTextFormat
	 */
	private TTextFormat DISCHG_DATE_DAY;
	/**
	 * ת��ҽԺTComboBox
	 */
	private TComboBox TRAN_HOSP;
	/**
	 * סԺ�Ʊ�TTextFormat
	 */
	private TTextFormat IPD_IN_DEPT;
	/**
	 * ת������TTextFormat
	 */
	private TTextFormat IPD_IN_DATE_DAY;
	/**
	 * �ٻ�����
	 */
	private TTextFormat RETURN_DATE;

	private TTextField DISCHG_DATE_TIME;
	private TTextField IPD_IN_DATE_TIME;


	// ���߻�����Ϣ
	/**
	 * ��������TTextField
	 */
	private TTextField PAT_NAME;
	/**
	 * �Ա�TComboBox
	 */
	private TComboBox SEX;
	/**
	 * ����TTextField
	 */
	private TTextField AGE;
	/**
	 * ����״��TComboBox
	 */
	private TComboBox MARRIGE;
	/**
	 * ְҵTTextFormat
	 */
	private TTextFormat OCCUPATION;
	/**
	 * ����TTextFormat
	 */
	private TTextFormat FOLK;
	/**
	 * ����TTextFormat
	 */
	private TTextFormat NATION;
	/**
	 * ����TTextFormat
	 */
	private TTextFormat BIRTH_DATE;
	/**
	 * ����ʡ��TTextField�����أ�
	 */
	private TTextField RESID_PROVICE;
	/**
	 * ����ʡ������
	 */
	private TTextField RESID_PROVICE_DESC;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat RESID_COUNTRY;	
	/**
	 * ���֤��TTextField
	 */
	private TTextField IDNO;
	/**
	 * ������TComboBox
	 */
	private TComboBox CTZ1_CODE;
	/**
	 * ��ϵ��TTextField
	 */
	private TTextField CONTACTER;
	/**
	 * ��ϵTTextFormat
	 */
	private TTextFormat RELATIONSHIP;
	/**
	 * ��ϵ�˵绰TTextField
	 */
	private TTextField CONT_TEL;
	/**
	 * ��ϵ�˵�ַTTextField
	 */
	private TTextField CONT_ADDRESS;
	/**
	 * ������λTTextField
	 */
	private TTextField OFFICE;
	/**
	 * ��λ�绰TTextField
	 */
	private TTextField O_TEL;
	/**
	 * ��λ�ʱ�TTextField
	 */
	private TTextField O_POSTNO;
	/**
	 * ��λ��ַTTextField
	 */
	private TTextField O_ADDRESS;
	/**
	 * ����סַTTextField
	 */
	private TTextField H_ADDRESS;
	/**
	 * �����ʱ�TTextField
	 */
	private TTextField H_POSTNO;


	/**
	 * ��������TTextFormat
	 */
	private TTextFormat IN_DATE;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat OUT_DATE;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat ERD_REGION;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat OUT_ERD_REGION;
	/**
	 * �����ȿ���TTextFormat
	 */
	private TTextFormat IN_DEPT;
	/**
	 * �����ȿ���TTextFormat
	 */
	private TTextFormat OUT_DEPT;
	/**
	 * ���������code TTextField�����ؿؼ�����ICD_CODEʹ��
	 */
	private TTextField HIDE_CODE;
	/**
	 * ���������desc TTextField
	 */
	private TTextField OUT_DIAG_CODE;
	/**
	 * ��ϱ�עTTextField
	 */
	private TTextField CODE_REMARK;
	/**
	 * ���ת��TComboBox
	 */
	private TComboBox CODE_STATUS;
	/**
	 * ���ϵȼ�TTextFormat
	 */
	private TTextFormat HEAL_LV;
	/**
	 * ������ICD TTextFormat
	 */
	private TTextFormat OP_CODE;
	/**
	 * ��������TTextFormat
	 */
	private TTextFormat OP_DATE;
	/**
	 * ������ԱTTextFormat
	 */
	private TTextFormat MAIN_SUGEON;
	/**
	 * �������ȼ�TComboBox
	 */
	private TComboBox OP_LEVEL;
	/**
	 * ���ȴ���TNumberTextField
	 */
	private TNumberTextField GET_TIMES;
	/**
	 * �ɹ�����TNumberTextField
	 */
	private TNumberTextField SUCCESS_TIMES;
	/**
	 * ����ҽʦTTextFormat
	 */
	private TTextFormat DR_CODE;
	/**
	 * ʵ������TNumberTextField
	 */
	private TNumberTextField REAL_STAY_DAYS;
	/**
	 * ��������TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_WEEK;
	/**
	 * ��������TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_MONTH;
	/**
	 * ��������TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_YEAR;
	/**
	 * ��������
	 */	
	private TTextFormat ACCOMP_DATE;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ��ȡϵͳ����
		Object o = this.getParameter();
		if(o!=null && o instanceof TParm){
			TParm sysParm = (TParm) o;
			triageNo = sysParm.getValue("TRIAGE_NO");
			flg = sysParm.getValue("FLG");
			// ҽ����ʱ��Ҫ�������źͲ�����
			caseNo = sysParm.getValue("CASE_NO");
			mrNo = sysParm.getValue("MR_NO");
			// У����˺ź�FLG
			if(triageNo==null || triageNo.trim().length()<=0 || flg==null || flg.trim().length()<=0){
				this.messageBox("ϵͳ��������2");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}
			// ���ʱҽ�����ã�У�����źͲ�����
			if((flg.equals("OPD") || flg.equals("OPD_OUT")) 
					&& (caseNo==null || caseNo.trim().length()<=0 || mrNo==null || mrNo.trim().length()<=0)){
				this.messageBox("ϵͳ��������3");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}	
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});
			this.messageBox("ϵͳ��������1");
			return;
		}		
		// tab���
		tabPanel = (TTabbedPane) this.getComponent("Tab");
		// ����ϵͳ�����������ĸ�ҳǩ����ʼ���ĸ�ҳǩ
		if(flg.equals("OPD") || flg.equals("OPD_OUT")){// ����ҽ������->�������� �� ->��Ժ
			tabPanel.setSelectedIndex(1);
//			tabPanel.setEnabledAt(0, false);// ����ҽ�����δ�λ�趨����
			
			// ҳǩ1��ʼ������
			this.myInitControler();
			// ���ý�������
			TParm erdRecordParm = getErdRecord(mrNo, caseNo, triageNo);
			if(erdRecordParm.getErrCode()<0){	
				this.messageBox(erdRecordParm.getErrText());
				return;
			}
			setErdRecord(erdRecordParm);
		}else if(flg.equals("NURSE")){// ���ﻤʿ����
			tabPanel.setSelectedIndex(0);
			tabPanel.setEnabledAt(1, false);// ���ﻤʿ���δ�λ�����
			
			// ҳǩ0��ʼ������
			table = (TTable) this.getComponent("TABLE");
			// ��tableע��CHECK_BOX_CLICKED��������¼�
			this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");
			// ��ѯ��λ����
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onQuery();
				}
			});			
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});
			this.messageBox("FLG�������");
			return;
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if(table==null){
			table = (TTable) this.getComponent("TABLE");
		}
		table.setParmValue(null);// ���table����
		// ��ȡ��ѯ����
		TParm selParm = this.getQueryParm();
		// ִ�в�ѯ
		TParm query = this.query(selParm);
		if(query.getErrCode()<0){
			this.messageBox(query.getErrText());
			return;
		}
		if (query.getCount() <= 0) {
			this.messageBox("û�����ȴ����ݣ�");
			return;
		}
		table.setParmValue(query);
	}

	/**
	 * �õ���ѯ����
	 * 
	 * @return TParm
	 */
	public TParm getQueryParm() {
		TParm parm = new TParm();
		// ������
		String erdRegion = this.getValueString("ERD_REGION_CODE");		
		if(erdRegion!=null && erdRegion.trim().length()>0){
			parm.setData("ERD_REGION_CODE", erdRegion);
		}
		// ռ��ע��
		if (this.getValueBoolean("OCCUPY_FLG")) {
			parm.setData("OCCUPY_FLG", "Y");
		}
		return parm;
	}

	/**
	 * ��ѯ���ȴ�
	 * @author wangqing 20170626
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm){
		StringBuffer buffer = new StringBuffer();
		// OCCUPY_FLG_2��������OCCUPY_FLG�Ƿ�仯	
		String sql = " SELECT A.OCCUPY_FLG, A.OCCUPY_FLG AS OCCUPY_FLG_2, A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.TRIAGE_NO, "
				+ "B.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_BED A, "
				+ "(SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_EVALUTION A, "
				+ "(SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME "
				+ "FROM REG_PATADM A, SYS_PATINFO B "
				+ "WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B "
				+ "WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL)B "
				+ "WHERE A.TRIAGE_NO=B.TRIAGE_NO(+) ";
		String sql2 = "";
		if(parm.getValue("ERD_REGION_CODE") != null && parm.getValue("ERD_REGION_CODE").trim().length()>0){
			sql2 = "AND A.ERD_REGION_CODE = '"+parm.getValue("ERD_REGION_CODE")+"' ";
		}
		String sql3 = "";
		if(parm.getValue("OCCUPY_FLG") != null && parm.getValue("OCCUPY_FLG").trim().length()>0){
			sql3 = "AND NOT(A.OCCUPY_FLG IS NOT NULL AND A.OCCUPY_FLG='Y') ";
		}
		String sql4 = "ORDER BY ERD_REGION_CODE, BED_NO ";
		buffer.append(sql);
		buffer.append(sql2);
		buffer.append(sql3);
		buffer.append(sql4);
		TParm result = new TParm(TJDODBTool.getInstance().select(buffer.toString()));    
		return result;
	}

	/**
	 * table�ϵ�checkBoxע�����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		TTable table = (TTable)obj;		
		table.acceptText();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();	
		TParm tblParm = table.getParmValue();	
		String sql;
		TParm result;	
		if (col == 0) {
			boolean flg = tblParm.getBoolean("OCCUPY_FLG", row);
			boolean flg2 = tblParm.getBoolean("OCCUPY_FLG_2", row);
			if(flg==flg2){// ���checkBoxû�з����仯
				return;
			}
			if (flg) {// �����ѡ
				// У�鲡���Ƿ�ռ�������Ѿ�����
				result = getErdInfo(triageNo);
				if(result.getErrCode()<0){
					this.messageBox("bug:::��ѯ����ռ�����ݳ���");					
					return;
				}
				if(result.getCount()>0){// ռ�����Ѿ�����
					this.messageBox("�ò������д�λ");
					table.setValueAt("N", row, col);
					tblParm.setData("OCCUPY_FLG", row, "N");
					return;
				}				
				// У�鲡���Ƿ�ת��
				result = this.getErdOutData(triageNo);
				if(result.getErrCode()<0){
					this.messageBox("bug:::��ѯ����ת�����ݳ���");
					return;
				}
				if(result.getCount()>0){
					this.messageBox("�ò�����ת��");
					table.setValueAt("N", row, col);
					tblParm.setData("OCCUPY_FLG", row, "N");
					return;
				}				
				// У�黼���Ƿ��Ѿ�ռ��������δ����
				for(int i=0; i<tblParm.getCount(); i++){
					if(tblParm.getValue("TRIAGE_NO", i)!=null && tblParm.getValue("TRIAGE_NO", i).equals(triageNo)){
						this.messageBox("�ò������д�λ\n�����ظ����ã�");
						table.setValueAt("N", row, col);
						tblParm.setData("OCCUPY_FLG", row, "N");
						return;
					}
				}
				// �󶨴�λ����������
				isInsert = true;
				erdregionCode = tblParm.getValue("ERD_REGION_CODE", row);
				bedNo = tblParm.getValue("BED_NO", row);	
				// ����OCCUPY_FLG_2
				tblParm.setData("OCCUPY_FLG_2", row, flg);

				// ���¼��˺�
				table.setValueAt(triageNo, row, 5);
				tblParm.setData("TRIAGE_NO", row, triageNo);
				// ���ݼ��˺Ų�ѯ����š������š���������
				sql = "SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL AND A.TRIAGE_NO='"+triageNo+"' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					this.messageBox("bug:::��ѯ�������ݴ���");
					return;
				}
				// ���²����š�����š���������
				if(result.getCount()>0){
					// ������
					table.setValueAt(result.getData("MR_NO", 0), row, 4);
					tblParm.setData("MR_NO", row, result.getData("MR_NO", 0));
					// �����
					tblParm.setData("CASE_NO", row, result.getData("CASE_NO", 0));
					// ��������
					table.setValueAt(result.getData("PAT_NAME", 0), row, 6);
					tblParm.setData("PAT_NAME", row, result.getData("PAT_NAME", 0));
				}	
			} else {// ȡ����ѡ
				// �ò����Ѿ��в��������Ѿ�����
				sql = "SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE OCCUPY_FLG='Y' AND TRIAGE_NO IS NOT NULL AND BED_NO='"+tblParm.getValue("BED_NO", row)+"' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					this.messageBox("bug:::��ѯ�������ݴ���");
					return;
				}
				if(result.getCount()>0){
					this.messageBox("�ò����Ѿ��в���,���ɲ���");
					table.setValueAt("Y", row, col);
					tblParm.setData("OCCUPY_FLG", row, "Y");
					return;
				}
				// ��������
				isInsert = false;
				erdregionCode = "";
				bedNo = "";
				// ����OCCUPY_FLG_2
				tblParm.setData("OCCUPY_FLG_2", row, flg);
				// ���¼��˺�
				table.setValueAt("", row, 5);
				tblParm.setData("TRIAGE_NO", row, "");
				// ������
				table.setValueAt("", row, 4);
				tblParm.setData("MR_NO", row, "");
				// �����
				tblParm.setData("CASE_NO", row, "");
				// ��������
				table.setValueAt("", row, 6);
				tblParm.setData("PAT_NAME", row, "");
			}
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// ��һ��ҳǩ
			if(isInsert){
				TParm parm = new TParm();
				parm.setData("TRIAGE_NO", triageNo);// ���˺�
				parm.setData("ERD_REGION_CODE", erdregionCode);// ������
				parm.setData("BED_NO", bedNo);// ���ȴ�
				
				// add by wangqing 20180201 start 
				// �봲����ʱ�������ʱ�����Ѿ��Һţ����ڸ���ERD_BED��ʱ��Ҳ�ø���CASE_NO��MR_NO��ͬʱ��ERD_RECORD���в���һ�����ݣ��������û�йҺţ�ά��ԭ�߼�
				parm.setData("CASE_NO", "");
				parm.setData("MR_NO", "");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
				TParm regP = getPatRegInfoByTriageNo(triageNo); 
				if(regP.getErrCode()<0){
					this.messageBox(regP.getErrText());
					return;
				}
				if(regP.getCount()>0){// ˵�������ѹҺ�
					parm.setData("CASE_NO", regP.getValue("CASE_NO", 0));
					parm.setData("MR_NO", regP.getValue("MR_NO", 0));
					// �ռ�������erd_record������					
					long currentTime = System.currentTimeMillis() ;
					Date date=new Date(currentTime);
					Timestamp inDate = new Timestamp(date.getTime());
					TParm erdP = this.copyPatDate(regP.getValue("MR_NO", 0), regP.getValue("CASE_NO", 0), inDate, erdregionCode, bedNo);
					if(erdP.getErrCode()<0){
						this.messageBox(erdP.getErrText());
						return;
					}
					parm.setData("erdP", erdP.getData());	
				}
				// add by wangqing 20180201 end
				
				TParm result =
						TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "setBedFinal", parm);
				if(result.getErrCode()<0){
					this.messageBox(result.getErrText());
					return;
				}
				this.messageBox("�󶨴�λ�ɹ�");
			}	
			break;
		case 1:// �ڶ���ҳǩ
			TParm parm1 = getPage1Data();
			TParm parm = new TParm();
			if(Radio0.isSelected() || Radio1.isSelected()){// ת����תסԺ
				parm = getErdInfo(triageNo);
				if(parm.getErrCode()<0){
					this.messageBox(parm.getErrText());
					return;
				}
				if(parm.getCount()<=0){
					parm = getErdOutData(triageNo);
					if(parm.getErrCode()<0){
						this.messageBox(parm.getErrText());
						return;
					}
					if(parm.getCount()>0){// ��ת��
						this.messageBox("�˲����Ѿ�ת��");
						return;
					}else{// δ�󶨴�λ
						this.messageBox("�˲���δ�󶨴�λ");
						return;
					}
				}	
				if(this.getValueString("OUT_DATE").trim().length()<=0){
					this.messageBox("���������������");
					return;
				}
				if(Radio0.isSelected()){
					
					if(this.getValueString("DISCHG_TYPE").trim().length()<=0){
						this.messageBox("��������Ժ��ʽ");
						return;
					}
					if(this.getValueString("DISCHG_DATE_DAY").trim().length()<=0){
						this.messageBox("��������Ժ����");
						return;
					}
				}else if(Radio1.isSelected()){
					if(this.getValueString("IPD_IN_DEPT").trim().length()<=0){
						this.messageBox("������סԺ�Ʊ�");
						return;
					}
					if(this.getValueString("IPD_IN_DATE_DAY").trim().length()<=0){
						this.messageBox("������ת������");
						return;
					}	
				}
					
				parm1.setData("OUT_FLG", "Y");// ת����תסԺ���
				parm1.setData("ERD_REGION_CODE", parm.getValue("ERD_REGION_CODE", 0));// ������
				parm1.setData("BED_NO", parm.getValue("BED_NO", 0));// ���ȴ�		
				parm1.setData("TRIAGE_NO", parm.getValue("TRIAGE_NO", 0));// ���˺�	
			}else if(Radio2.isSelected()){// �ٻ�
				parm = getErdOutData(triageNo);
				if(parm.getErrCode()<0){
					this.messageBox(parm.getErrText());
					return;
				}
				if(parm.getCount()<=0){
					parm = getErdInfo(triageNo);
					if(parm.getErrCode()<0){
						this.messageBox(parm.getErrText());
						return;
					}
					if(parm.getCount()>0){
						this.messageBox("�˲�������������");
						return;
					}else{
						this.messageBox("�˲���δ�󶨴�λ");
						return;
					}
				}
				if(this.getValueString("RETURN_DATE").trim().length()<=0){
					this.messageBox("�������ٻ�����");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\erd\\ERDBedSelUI2.x");
				if (obj == null){
					return;
				}else {
					parm1.setData("RETURN_FLG", "Y");// �ٻر��
					parm1.setData("ERD_REGION_CODE", ((TParm) obj).getValue("ERD_REGION_CODE"));// �ٻ�������
					parm1.setData("BED_NO", ((TParm) obj).getValue("BED_NO"));// �ٻش�λ
					parm1.setData("TRIAGE_NO", triageNo);
					parm1.setData("OUT_DATE", new TNull(Timestamp.class));// ת�������ÿ�
					parm1.setData("CASE_NO", caseNo);// �����
					parm1.setData("MR_NO", mrNo);// ������
					
					// �ٻ�ʱ������Ժ��ʽ����Ժʱ�䡢ת��ҽԺ��סԺ�Ʊ�סԺ���ڡ��������ա����������������ȿ����ÿ�
					parm1.setData("DISCHG_TYPE", new TNull(String.class));// ��Ժ��ʽ
					parm1.setData("DISCHG_DATE", new TNull(Timestamp.class));// ��Ժʱ��
					parm1.setData("TRAN_HOSP", new TNull(String.class));// ת��ҽԺ
					parm1.setData("IPD_IN_DEPT", new TNull(String.class));// סԺ�Ʊ�					
					parm1.setData("IPD_IN_DATE", new TNull(Timestamp.class));// סԺ����	
					parm1.setData("OUT_DATE", new TNull(Timestamp.class));// ��������
					parm1.setData("OUT_ERD_REGION", new TNull(String.class));// ��������
					parm1.setData("OUT_DEPT", new TNull(String.class));// �����ȿ���	
				}		
			}	
			// ����ʱ���ʽ�����sql��ͳһ������
			parm1.setData("OUT_DATE_2", StringTool.getString(TypeTool.getTimestamp(getValue("OUT_DATE")), "yyyy/MM/dd HH:mm:ss"));	
			parm1.setData("RETURN_DATE_2", StringTool.getString(TypeTool.getTimestamp(getValue("RETURN_DATE")), "yyyy/MM/dd HH:mm:ss"));	
			
			parm1 = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "cancelBedFinal", parm1);
			if (parm1.getErrCode() < 0) {
				this.messageBox(parm1.getErrText());
				return;
			}
			this.messageBox("����ɹ�");		
			break;
		default:
			break;
		}
	}

	/**
	 * ת������
	 */
	public void onTransfer() {
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ��һ���մ�");
			return;
		}
		// �ж��Ƿ��ǿմ�
		TParm tblParm = table.getParmValue();	
		boolean isOccupyFlg = tblParm.getBoolean("OCCUPY_FLG", row);
		if(isOccupyFlg){
			this.messageBox("��ѡ��һ���մ�");
			return;
		}
		String sql;
		TParm result;		
		// �ж��Ƿ��Ѿ��󶨴�λ�����Ѿ�����
		sql = " SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO "
				+ "FROM ERD_BED "
				+ "WHERE OCCUPY_FLG='Y' "
				+ "AND TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
		if(result.getCount()>0){// �Ѿ��󶨴�λ�����Ѿ�����
			TParm parm = new TParm();
			parm.setData("TRIAGE_NO", triageNo);
			parm.setData("ERD_REGION_CODE_FROM", result.getData("ERD_REGION_CODE", 0));// ԭ������
			parm.setData("BED_NO_FROM", result.getData("BED_NO", 0));// ԭ��λ
			parm.setData("ERD_REGION_CODE_TO", tblParm.getData("ERD_REGION_CODE", row));// Ŀ��������
			parm.setData("BED_NO_TO", tblParm.getData("BED_NO", row));// Ŀ�ش�λ
			
			// add by wangqing 20180201 start 
			// ת��ʱ������ԭERD_BED��CASE_NO��MR_NOΪnull;�������Ѿ��Һţ�������´�λERD_BED��CASE_NO��MR_NO����ERD_RECORDû�����ݣ������
			parm.setData("CASE_NO", "");
			parm.setData("MR_NO", "");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			TParm regP = getPatRegInfoByTriageNo(triageNo); 
			if(regP.getErrCode()<0){
				this.messageBox(regP.getErrText());
				return;
			}		
			if(regP.getCount()>0){// ˵�������ѹҺ�
				parm.setData("CASE_NO", regP.getValue("CASE_NO", 0));
				parm.setData("MR_NO", regP.getValue("MR_NO", 0));
				TParm erdRecord = getErdRecord(regP.getValue("CASE_NO", 0));
				if(erdRecord.getErrCode()<0){
					this.messageBox(erdRecord.getErrText());
					return;
				}
				if(erdRecord.getCount()<=0){
					// �ռ�������erd_record������
					TParm erdP = this.copyPatDate(regP.getValue("MR_NO", 0), regP.getValue("CASE_NO", 0), triageNo);
					if(erdP.getErrCode()<0){
						this.messageBox(erdP.getErrText());
						return;
					}
					parm.setData("erdP", erdP.getData());
				}				
			}
			// add by wangqing 20180201 end
			
			result = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "onTransferFinal", parm);
			if(result.getErrCode()<0){
				this.messageBox("ת��ʧ��");
				return;
			}
			this.messageBox("ת���ɹ�");	
			// ת���ɹ������²�ѯ��ˢ������
			this.onQuery();
		}else{
			sql = " SELECT TRIAGE_NO, CASE_NO, ERD_REGION_CODE, BED_NO, OUT_DATE "
					+ "FROM ERD_EVALUTION "
					+ "WHERE TRIAGE_NO='"+triageNo+"' "
					+ "AND CASE_NO IS NOT NULL "
					+ "AND ERD_REGION_CODE IS NOT NULL "
					+ "AND BED_NO IS NOT NULL "
					+ "AND OUT_DATE IS NOT NULL ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				this.messageBox(result.getErrText());
				return;
			}
			if(result.getCount()>0){// �˲����Ѿ�ת��
				this.messageBox("�˲�����ת�������������ȣ�ҽ������ִ���ٻز���");
				return;
			}else{// �˲�����δ�󶨴�λ
				this.messageBox("�˲�����δ�󶨴�λ������ת��");
			}	
		}		
	}

	/**
	 * ���
	 */
	public void onClear() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// ��һ��ҳǩ
			this.clearValue("ERD_REGION_CODE;OCCUPY_FLG");
			table.removeRowAll();		
			isInsert = false;
			erdregionCode = "";
			bedNo = "";		
			break;
		case 1:// �ڶ���ҳǩ
			break;
		default:
			break;
		}
	}

	/**
	 * ��ѯ�մ�
	 */
	public void onEmptyBed() {
		isInsert = false;
		erdregionCode = "";
		bedNo = "";
		onQuery();
	}

	/**
	 * �л�Tabҳ��ʱ������Ŀǰֻ��ҽ����Ȩ��
	 */
	public void onChangeTab() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// ��һ��ҳǩ
			// ҳǩ0��ʼ������
			table = (TTable) this.getComponent("TABLE");
			// ��tableע��CHECK_BOX_CLICKED��������¼�
			this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");
			// note��erd_record��Ч�ֶΣ�����š������š�ת����ʽ����Ժ|תסԺ|�ٻأ�����Ժ���ڣ�����Ժ����ת��ҽԺ��סԺ���ҡ�סԺ���ڡ��ٻ�����
			// ��ѯ��λ����
			onQuery();
			// ���α��水ť��ת����ť
			callFunction("UI|save|setVisible", false);
			callFunction("UI|TRANSFER|setVisible", false);
			break;
		case 1:// �ڶ���ҳǩ
			// ���α��水ť��ת����ť
			callFunction("UI|save|setVisible", true);
			callFunction("UI|TRANSFER|setVisible", false);
			break;
		default:
			break;
		}
	}

	/**
	 * ��ʼ��ҳǩ1�ؼ������󶨼���
	 */
	public void myInitControler() {
		Radio0 = (TRadioButton) this.getComponent("Radio0");
		// ��Ժʱ��Ĭ��ѡ����Ժ
		Radio0.setSelected(true);
		onOutInReturn("Out");
		
		Radio1 = (TRadioButton) this.getComponent("Radio1");
		Radio2 = (TRadioButton) this.getComponent("Radio2");
		DISCHG_TYPE = (TComboBox) this.getComponent("DISCHG_TYPE");
		DISCHG_DATE_DAY = (TTextFormat) this.getComponent("DISCHG_DATE_DAY");
		TRAN_HOSP = (TComboBox) this.getComponent("TRAN_HOSP");
		IPD_IN_DEPT = (TTextFormat) this.getComponent("IPD_IN_DEPT");
		IPD_IN_DATE_DAY = (TTextFormat) this.getComponent("IPD_IN_DATE_DAY");
		RETURN_DATE = (TTextFormat) this.getComponent("RETURN_DATE");

		DISCHG_DATE_TIME = (TTextField) this.getComponent("DISCHG_DATE_TIME");
		IPD_IN_DATE_TIME = (TTextField) this.getComponent("IPD_IN_DATE_TIME");

		PAT_NAME = (TTextField) this.getComponent("PAT_NAME");
		SEX = (TComboBox) this.getComponent("SEX");
		AGE = (TTextField) this.getComponent("AGE");
		MARRIGE = (TComboBox) this.getComponent("MARRIGE");
		OCCUPATION = (TTextFormat) this.getComponent("OCCUPATION");
		FOLK = (TTextFormat) this.getComponent("FOLK");
		NATION = (TTextFormat) this.getComponent("NATION");
		BIRTH_DATE = (TTextFormat) this.getComponent("BIRTH_DATE");
		RESID_PROVICE = (TTextField) this.getComponent("RESID_PROVICE");
		RESID_PROVICE_DESC = (TTextField) this.getComponent("RESID_PROVICE_DESC");
		RESID_COUNTRY = (TTextFormat) this.getComponent("RESID_COUNTRY");
		IDNO = (TTextField) this.getComponent("IDNO");
		CTZ1_CODE = (TComboBox) this.getComponent("CTZ1_CODE");

		CONTACTER = (TTextField) this.getComponent("CONTACTER");
		RELATIONSHIP = (TTextFormat) this.getComponent("RELATIONSHIP");
		CONT_TEL = (TTextField) this.getComponent("CONT_TEL");
		CONT_ADDRESS = (TTextField) this.getComponent("CONT_ADDRESS");		
		OFFICE = (TTextField) this.getComponent("OFFICE");
		O_TEL = (TTextField) this.getComponent("O_TEL");
		O_POSTNO = (TTextField) this.getComponent("O_POSTNO");
		O_ADDRESS = (TTextField) this.getComponent("O_ADDRESS");
		H_ADDRESS = (TTextField) this.getComponent("H_ADDRESS");
		H_POSTNO = (TTextField) this.getComponent("H_POSTNO");

		IN_DATE = (TTextFormat) this.getComponent("IN_DATE");
		ERD_REGION = (TTextFormat) this.getComponent("ERD_REGION");
		IN_DEPT = (TTextFormat) this.getComponent("IN_DEPT");
		OUT_DATE = (TTextFormat) this.getComponent("OUT_DATE");
		OUT_ERD_REGION = (TTextFormat) this.getComponent("OUT_ERD_REGION");		
		OUT_DEPT = (TTextFormat) this.getComponent("OUT_DEPT");

		OUT_DIAG_CODE = (TTextField) this.getComponent("OUT_DIAG_CODE");
		HIDE_CODE = (TTextField) this.getComponent("HIDE_CODE");
		CODE_REMARK = (TTextField) this.getComponent("CODE_REMARK");
		CODE_STATUS = (TComboBox) this.getComponent("CODE_STATUS");
		HEAL_LV = (TTextFormat) this.getComponent("HEAL_LV");

		OP_CODE = (TTextFormat) this.getComponent("OP_CODE");
		OP_DATE = (TTextFormat) this.getComponent("OP_DATE");
		MAIN_SUGEON = (TTextFormat) this.getComponent("MAIN_SUGEON");
		OP_LEVEL = (TComboBox) this.getComponent("OP_LEVEL");

		GET_TIMES = (TNumberTextField) this.getComponent("GET_TIMES");
		SUCCESS_TIMES = (TNumberTextField) this.getComponent("SUCCESS_TIMES");
		DR_CODE = (TTextFormat) this.getComponent("DR_CODE");
		REAL_STAY_DAYS = (TNumberTextField) this.getComponent("REAL_STAY_DAYS");

		ACCOMPANY_WEEK = (TNumberTextField) this.getComponent("ACCOMPANY_WEEK");
		ACCOMPANY_MONTH = (TNumberTextField) this.getComponent("ACCOMPANY_MONTH");
		ACCOMPANY_YEAR = (TNumberTextField) this.getComponent("ACCOMPANY_YEAR");
		ACCOMP_DATE = (TTextFormat) this.getComponent("ACCOMP_DATE");

		// ������������õ����˵�
		OUT_DIAG_CODE.setPopupMenuParameter("", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ��������϶�����ܷ���ֵ����
		OUT_DIAG_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ���ó����ص�����
		callFunction("UI|RESID_PROVICE_DESC|setPopupMenuParameter", "aaa", "%ROOT%\\config\\sys\\SYSHOMEPLACEPopup.x");
		// textfield���ܻش�ֵ
		callFunction("UI|RESID_PROVICE_DESC|addEventListener", TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
	}

	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		String icd_code = parm.getValue("ICD_CODE");
		OUT_DIAG_CODE.setValue("");
		HIDE_CODE.setValue("");
		if (!StringUtil.isNullString(icd_code)) {
			OUT_DIAG_CODE.setValue(icd_desc);
			HIDE_CODE.setValue(icd_code);		
		}
	}

	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("RESID_PROVICE", parm.getValue("HOMEPLACE_CODE"));
		this.setValue("RESID_PROVICE_DESC", parm.getValue("HOMEPLACE_DESC"));
	}

	/**
	 * ����2��ֵ
	 */
	public void setErdRecord(TParm pat){
		// STATUS: 0�������У�1����Ժ��2��תסԺ��3���ٻ�
		if(pat.getValue("STATUS", 0)!=null){
			if(pat.getValue("STATUS", 0).equals("0")){

			}else if(pat.getValue("STATUS", 0).equals("1")){
				onOutInReturn("Out");
				Radio0.setSelected(true);
				DISCHG_TYPE.setValue((String) pat.getData("DISCHG_TYPE", 0));
				DISCHG_DATE_DAY.setValue((Timestamp) pat.getData("DISCHG_DATE", 0));
				TRAN_HOSP.setValue((String) pat.getData("TRAN_HOSP", 0));
				DISCHG_DATE_TIME.setValue((String) pat.getData("DISCHG_DATE_TIME", 0));
			}else if(pat.getValue("STATUS", 0).equals("2")){
				onOutInReturn("In");
				Radio1.setSelected(true);
				IPD_IN_DEPT.setValue((String) pat.getData("IPD_IN_DEPT", 0));
				IPD_IN_DATE_DAY.setValue((Timestamp) pat.getData("IPD_IN_DATE", 0));
				IPD_IN_DATE_TIME.setValue((String) pat.getData("IPD_IN_DATE_TIME", 0));
			}else if(pat.getValue("STATUS", 0).equals("3")){
				onOutInReturn("Return");
				Radio2.setSelected(true);
				RETURN_DATE.setValue((Timestamp) pat.getData("RETURN_DATE", 0));
			}	
		}else{
			return;
		}			
		PAT_NAME.setValue((String) pat.getData("PAT_NAME", 0));// ��������
		SEX.setValue((String) pat.getData("SEX", 0));// �Ա�
		AGE.setValue((String) pat.getData("AGE", 0));// ����
		MARRIGE.setValue((String) pat.getData("MARRIGE", 0));// ����״��
		OCCUPATION.setValue((String) pat.getData("OCCUPATION", 0));// ְҵ
		FOLK.setValue((String) pat.getData("FOLK", 0));// ����
		NATION.setValue((String) pat.getData("NATION", 0));// ����
		BIRTH_DATE.setValue((Timestamp) pat.getData("BIRTH_DATE", 0));// ����
		
		RESID_PROVICE.setValue((String) pat.getData("RESID_PROVICE", 0));// ʡ�У����أ�
		RESID_PROVICE_DESC.setValue(getPatHome(getValueString("RESID_PROVICE")).getValue("HOMEPLACE_DESC", 0));// ʡ������
				
		RESID_COUNTRY.setValue((String) pat.getData("RESID_COUNTRY", 0));// ����		
		IDNO.setValue((String) pat.getData("IDNO", 0));// ���֤��
		CTZ1_CODE.setValue((String) pat.getData("CTZ1_CODE", 0));// ������
		CONTACTER.setValue((String) pat.getData("CONTACTER", 0));// ��ϵ��
		RELATIONSHIP.setValue((String) pat.getData("RELATIONSHIP", 0));// ��ϵ�˹�ϵ
		CONT_TEL.setValue((String) pat.getData("CONT_TEL", 0));// ��ϵ�˵绰
		CONT_ADDRESS.setValue((String) pat.getData("CONT_ADDRESS", 0));// ��ϵ�˵�ַ
		OFFICE.setValue((String) pat.getData("OFFICE", 0));// ������λ	
		O_TEL.setValue((String) pat.getData("O_TEL", 0));// ��λ�绰
		O_POSTNO.setValue((String) pat.getData("O_POSTNO", 0));// ��λ�ʱ�
		O_ADDRESS.setValue((String) pat.getData("O_ADDRESS", 0));// ��λ��ַ
		H_ADDRESS.setValue((String) pat.getData("H_ADDRESS", 0));// ��ͥסַ
		H_POSTNO.setValue((String) pat.getData("H_POSTNO", 0));// ��ͥ�ʱ�
		IN_DATE.setValue((Timestamp) pat.getData("IN_DATE", 0));// ��������
		ERD_REGION.setValue((String) pat.getData("ERD_REGION", 0));// ��������
		IN_DEPT.setValue((String) pat.getData("IN_DEPT", 0));// �����ȿ���
		OUT_DATE.setValue((Timestamp) pat.getData("OUT_DATE", 0));// ��������
		OUT_ERD_REGION.setValue((String) pat.getData("OUT_ERD_REGION", 0));// ��������		
		OUT_DEPT.setValue((String) pat.getData("OUT_DEPT", 0));// �����ȿ���
		HIDE_CODE.setValue((String) pat.getData("OUT_DIAG_CODE", 0));// ���������code
		if ( pat.getValue("OUT_DIAG_CODE", 0)!=null && pat.getValue("OUT_DIAG_CODE", 0).trim().length()>0) {
			String sql =
					" SELECT ICD_CHN_DESC FROM  SYS_DIAGNOSIS WHERE ICD_CODE='"
							+ pat.getValue("OUT_DIAG_CODE", 0) + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			OUT_DIAG_CODE.setValue(parm.getValue("ICD_CHN_DESC", 0));// �������������
		} else {
			OUT_DIAG_CODE.setValue("");// �������������
		}
		CODE_REMARK.setValue((String) pat.getData("CODE_REMARK", 0));// ��ϱ�ע
		CODE_STATUS.setValue((String) pat.getData("CODE_STATUS", 0));// ���ת��
		HEAL_LV.setValue((String) pat.getData("HEAL_LV", 0));// ���ϵȼ�

		OP_CODE.setValue((String) pat.getData("OP_CODE", 0));// ������ICD
		OP_DATE.setValue((Timestamp) pat.getData("OP_DATE", 0));// ��������
		MAIN_SUGEON.setValue((String) pat.getData("MAIN_SUGEON", 0));// ������Ա
		OP_LEVEL.setValue((String) pat.getData("OP_LEVEL", 0));// �������ȼ�

		GET_TIMES.setValue(pat.getData("GET_TIMES", 0) + ""); // ���ȴ���
		SUCCESS_TIMES.setValue(pat.getData("SUCCESS_TIMES", 0) + "");// �ɹ�����
		DR_CODE.setValue((String) pat.getData("DR_CODE", 0));// ����ҽ��
		// ����ʵ������������δ��->��ǰʱ��-�������գ��ѳ�->��Ժʱ��-��������
		REAL_STAY_DAYS.setValue(getRealInDays(pat.getData("OUT_DATE", 0) + "", pat));

		ACCOMPANY_WEEK.setValue(pat.getData("ACCOMPANY_WEEK", 0) + "");// ��������
		ACCOMPANY_MONTH.setValue(pat.getData("ACCOMPANY_MONTH", 0) + "");// ��������
		ACCOMPANY_YEAR.setValue(pat.getData("ACCOMPANY_YEAR", 0) + "");	// ��������
		ACCOMP_DATE.setValue((Timestamp) pat.getData("ACCOMP_DATE", 0));// ��������	
	}

	/**
	 * �õ�ʵ����������
	 * 
	 * @param startDate
	 *            Timestamp
	 * @param endDate
	 *            Timestamp
	 * @return int
	 */
	public int getRealInDays(String outDate, TParm data) {
		Timestamp endDate = TJDODBTool.getInstance().getDBTime();
		// ��û����Ժ�յ�ʱ��IN_DATE=����
		Timestamp startDate =
				data.getTimestamp("IN_DATE", 0) == null ? endDate : data.getTimestamp("IN_DATE", 0);
		// ��û��ת��ס��ǵĵ�ʱ��
		if (!(outDate.trim().length() == 0 || "null".equals(outDate))) {
			endDate = data.getTimestamp("OUT_DATE", 0);
		}
		int diff = StringTool.getDateDiffer(endDate, startDate);
		return diff;
	}

	/**
	 * ת����תסԺ���ٻ�radioѡ���¼�
	 * @param status
	 */
	public void onOutInReturn(String status) {
		this.clearValue("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE_DAY;RETURN_DATE");
		this.setEnabled("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE_DAY;RETURN_DATE", false);	
		if (status.equals("Out")) {// ��Ժ
			this.setEnabled("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP", true);
		} else if (status.equals("In")) {// תסԺ
			this.setEnabled("IPD_IN_DEPT;IPD_IN_DATE_DAY", true);
		} else if (status.equals("Return")) {// �ٻ�
			this.setEnabled("RETURN_DATE", true);
		}
	}

	/**
	 * �õ�����Ϊ1��ҳǩ�Ĳ���
	 * 
	 * @return TParm
	 */
	public TParm getPage1Data() {
		TParm result = new TParm();
		if(Radio0.isSelected()){
			result.setData("STATUS", "1");
		}else if(Radio1.isSelected()){
			result.setData("STATUS", "2");
		}else if(Radio2.isSelected()){
			result.setData("STATUS", "3");
		}else{
			result.setData("STATUS", "0");
		}
		result.setData("CASE_NO", caseNo);
		result.setData("MR_NO", mrNo);
		result.setData("ERD_NO", ""); // ��ʱ����
		result.setData("PAT_NAME", PAT_NAME.getValue() == null ? new TNull(String.class) : PAT_NAME
				.getValue());
		result.setData("AGE", AGE.getValue() == null ? new TNull(String.class) : AGE.getValue());
		result.setData("IDNO", IDNO.getValue() == null ? new TNull(String.class) : IDNO.getValue());
		result.setData("MARRIGE", MARRIGE.getValue() == null ? new TNull(String.class) : MARRIGE
				.getValue());
		result.setData("O_TEL", O_TEL.getValue() == null ? new TNull(String.class) : O_TEL
				.getValue());
		// result.setData("H_TEL",
		// H_TEL.getValue() == null ? new TNull(String.class) :
		// H_TEL.getValue());
		result.setData("O_POSTNO", O_POSTNO.getValue() == null ? new TNull(String.class) : O_POSTNO
				.getValue());
		result.setData("OFFICE", OFFICE.getValue() == null ? new TNull(String.class) : OFFICE
				.getValue());
		result.setData("O_ADDRESS", O_ADDRESS.getValue() == null ? new TNull(String.class)
				: O_ADDRESS.getValue());
		result.setData("H_ADDRESS", H_ADDRESS.getValue() == null ? new TNull(String.class)
				: H_ADDRESS.getValue());
		result.setData("H_POSTNO", H_POSTNO.getValue() == null ? new TNull(String.class) : H_POSTNO
				.getValue());
		result.setData("CONTACTER", CONTACTER.getValue() == null ? new TNull(String.class)
				: CONTACTER.getValue());
		result.setData("CONT_TEL", CONT_TEL.getValue() == null ? new TNull(String.class) : CONT_TEL
				.getValue());
		result.setData("CONT_ADDRESS", CONT_ADDRESS.getValue() == null ? new TNull(String.class)
				: CONT_ADDRESS.getValue());
		// �����ؿؼ����õ�
		result.setData("OUT_DIAG_CODE", HIDE_CODE.getValue() == null ? new TNull(String.class)
				: HIDE_CODE.getValue());
		result.setData("CODE_REMARK", CODE_REMARK.getValue() == null ? new TNull(String.class)
				: CODE_REMARK.getValue());
		result.setData("CODE_STATUS", CODE_STATUS.getValue() == null ? new TNull(String.class)
				: CODE_STATUS.getValue());
		result.setData("OP_CODE", OP_CODE.getValue() == null ? new TNull(String.class) : OP_CODE
				.getValue());
		result.setData("OP_LEVEL", OP_LEVEL.getValue() == null ? new TNull(String.class) : OP_LEVEL
				.getValue());
		result.setData("HEAL_LV", HEAL_LV.getValue() == null ? new TNull(String.class) : HEAL_LV
				.getValue());
		result.setData("SEX", SEX.getValue() == null ? new TNull(String.class) : SEX.getValue());
		result.setData("CTZ1_CODE", CTZ1_CODE.getValue() == null ? new TNull(String.class)
				: CTZ1_CODE.getValue());
		result.setData("OCCUPATION", OCCUPATION.getValue() == null ? new TNull(String.class)
				: OCCUPATION.getValue());
		result.setData("RESID_PROVICE", RESID_PROVICE.getValue() == null ? new TNull(String.class)
				: RESID_PROVICE.getValue());
		result.setData("RESID_COUNTRY", RESID_COUNTRY.getValue() == null ? new TNull(String.class)
				: RESID_COUNTRY.getValue());
		result.setData("FOLK", FOLK.getValue() == null ? new TNull(String.class) : FOLK.getValue());
		result.setData("RELATIONSHIP", RELATIONSHIP.getValue() == null ? new TNull(String.class)
				: RELATIONSHIP.getValue());
		result.setData("NATION", NATION.getValue() == null ? new TNull(String.class) : NATION
				.getValue());
		result.setData("ERD_REGION", ERD_REGION.getValue() == null ? new TNull(String.class)
				: ERD_REGION.getValue());
		result.setData("OUT_ERD_REGION", OUT_ERD_REGION.getValue() == null
				? new TNull(String.class) : OUT_ERD_REGION.getValue());// ��������
		result.setData("IN_DEPT", IN_DEPT.getValue() == null ? new TNull(String.class) : IN_DEPT
				.getValue());
		result.setData("OUT_DEPT", OUT_DEPT.getValue() == null ? new TNull(String.class) : OUT_DEPT
				.getValue());// �����ȿ���
		result.setData("DR_CODE", DR_CODE.getValue() == null ? new TNull(String.class) : DR_CODE
				.getValue());
		result.setData("MAIN_SUGEON", MAIN_SUGEON.getValue() == null ? new TNull(String.class)
				: MAIN_SUGEON.getValue());
		result.setData("REAL_STAY_DAYS", REAL_STAY_DAYS.getValue() == null ? 0.00 : REAL_STAY_DAYS
				.getValue());
		result.setData("GET_TIMES", GET_TIMES.getValue() == null ? 0.00 : GET_TIMES.getValue());
		result.setData("SUCCESS_TIMES", SUCCESS_TIMES.getValue() == null ? 0.00 : SUCCESS_TIMES
				.getValue());
		result.setData("ACCOMPANY_WEEK", ACCOMPANY_WEEK.getValue() == null ? 0.00 : ACCOMPANY_WEEK
				.getValue());
		result.setData("ACCOMPANY_YEAR", ACCOMPANY_YEAR.getValue() == null ? 0.00 : ACCOMPANY_YEAR
				.getValue());
		result.setData("ACCOMPANY_MONTH", ACCOMPANY_MONTH.getValue() == null ? 0.00
				: ACCOMPANY_MONTH.getValue());
		result.setData("BIRTH_DATE", BIRTH_DATE.getValue() == null ? new TNull(Timestamp.class)
				: BIRTH_DATE.getValue());
		result.setData("IN_DATE", IN_DATE.getValue() == null ? new TNull(Timestamp.class) : IN_DATE
				.getValue());
		result.setData("OUT_DATE", OUT_DATE.getValue() == null ? new TNull(Timestamp.class)
				: OUT_DATE.getValue());// ��������
		result.setData("OP_DATE", OP_DATE.getValue() == null ? new TNull(Timestamp.class) : OP_DATE
				.getValue());
		result.setData("ACCOMP_DATE", ACCOMP_DATE.getValue() == null ? new TNull(Timestamp.class)
				: ACCOMP_DATE.getValue());
		result.setData("DISCHG_TYPE", DISCHG_TYPE.getValue() == null ? new TNull(String.class)
				: DISCHG_TYPE.getValue());// ��Ժ��ʽ
		result.setData("IPD_IN_DEPT", IPD_IN_DEPT.getValue() == null ? new TNull(String.class)
				: IPD_IN_DEPT.getValue());// סԺ�Ʊ�
		result.setData("TRAN_HOSP", TRAN_HOSP.getValue() == null ? new TNull(String.class)
				: TRAN_HOSP.getValue());// ת��ҽԺ
		String dischDateString = DISCHG_DATE_DAY.getText();
		result.setData("DISCHG_DATE", dischDateString.equals("") ? new TNull(Timestamp.class)
				: StringTool.getTimestamp(dischDateString, "yyyy/MM/dd HH:mm:ss"));// ��Ժʱ��
		String ipdInDateString = IPD_IN_DATE_DAY.getText();
		result.setData("IPD_IN_DATE", ipdInDateString.equals("") ? new TNull(Timestamp.class)
				: StringTool.getTimestamp(ipdInDateString, "yyyy/MM/dd HH:mm"));// סԺ����
		TTextFormat RETURN_DATE = (TTextFormat) this.getComponent("RETURN_DATE");
		String returnDate = RETURN_DATE.getText();
		result.setData("RETURN_DATE", returnDate.length() == 0 ? new TNull(Timestamp.class)
				: getValue("RETURN_DATE"));
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_TERM", Operator.getIP());
		return result;
	}

	public static void main(String[] args) {
		//		JavaHisDebug.initClient();
		//		// JavaHisDebug.TBuilder();
		//		// JavaHisDebug.TBuilder();
		//		JavaHisDebug.runFrame("erd\\ERDDynamicRcd.x");
	}

	/**
	 * ����
	 */
	public void onTest(){
		System.out.println("---result="+this.getErdStartTime("111"));
	}
	
	
	// ------------------------------------add by wangqing 2080131 start------------------------------

	/**
	 * ��ȡERD_RECORD����
	 * @param caseNo
	 * @return
	 */
	public TParm getErdRecord(String caseNo){ 
		String sql = "SELECT * FROM ERD_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	
	
	/**
	 * ��ȡerd_record����
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 * @return
	 */
	public TParm getErdRecord(String mrNo, String caseNo, String triageNo){
		String sql = "SELECT * FROM ERD_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0){
			return parm;
		}
		String s =" SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO "
				+ "FROM ERD_BED "
				+ "WHERE TRIAGE_NO='"+triageNo+"' AND OCCUPY_FLG='Y' ";
		TParm r = new TParm(TJDODBTool.getInstance().select(s));
		if(r.getErrCode()<0){
			return r;
		}
		// �򿪳�Ժ����ʱ��Ϊ�˼��������ݣ�
		// ���ж�ERD_RECORD�Ƿ��л������ݣ����һ����Ƿ��ڴ�;
		// ��ERD_RECORDû�����ݲ��һ����ڴ��������һ��
		// ��ϵͳ�ȶ������ε������룻
		if(parm.getCount()<=0 && r.getCount()>0){
			parm = this.insertErdRecord(mrNo, caseNo, triageNo);
			if(parm.getErrCode()<0){
				return parm;
			}
		}
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * ��ERD_RECORD��¼����
	 * @author wangqing 20180131
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 */
	public TParm insertErdRecord(String mrNo, String caseNo, String triageNo){
		TParm parm = new TParm();
		parm = copyPatDate(mrNo, caseNo, triageNo);
		if(parm.getErrCode()<0){
			return parm;
		}
		parm = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "insertErdRecordFinal", parm);
		return parm;
	}
	
	/**
	 * ���ƻ��߻�����Ϣ
	 * @param mrNo
	 * @param caseNo
	 * @param inDate
	 * @param erdRegion
	 * @param bedNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, Timestamp inDate, String erdRegion, String bedNo) {
		// ���߻�����Ϣ
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
	/*	if(sysPatInfoParm.getErrCode()<0){
			return sysPatInfoParm;
		}*/
		
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("��ȡ���߻�����Ϣ����");
			return sysPatInfoParm;
		}	
		// ���߹Һ���Ϣ
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("��ȡ���߹Һ���Ϣ����");
			return sysRegPatAdm;
		}
				
		/*// ������Ϣ
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			return erdParm;
		}	
		// ��ʼ����ʱ��
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			return erdStartTimeParm;
		}*/	
				
		TParm result = new TParm();
		
		result.setData("CASE_NO", caseNo);
		
		result.setData("MR_NO", mrNo);
		
		result.setData("ERD_NO", "");// ����

		result.setData("STATUS", "0");// 0�������У�1����Ժ��2��תסԺ��3���ٻ�
		
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");

		// ���û��߻�����Ϣ
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		/*if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// ����������
		}else{
			result.setData("IN_DATE", "");// ����������
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// ��������
		// ���ȴ�
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));*/	
		result.setData("IN_DATE", inDate);// ����������
		
		result.setData("ERD_REGION", erdRegion);// ��������
		
		result.setData("BED_NO", bedNo);// ���ȴ�
				
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// �����ȿ���
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// ����һ��COPY���ݵ�ERD_RECORD���е�ʱ��Ĭ������-��1��

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());

		result.setData("OPT_TERM", Operator.getIP());
		return result;
	}
	
	/**
	 * ���ƻ��߻�����Ϣ
	 * @author WangQing
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, String triageNo) {
		// ���߻�����Ϣ
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("��ȡ���߻������ݴ���");
			return sysPatInfoParm;
		}	
		// ���߹Һ���Ϣ
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("��ȡ���߹Һ����ݴ���");
			return sysRegPatAdm;
		}		
		// ������Ϣ
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			erdParm.setErrCode(-1);
			erdParm.setErrText("��ȡ����������Ϣ����");
			return erdParm;
		}	
		// ��ʼ����ʱ��
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			erdStartTimeParm.setErrCode(-1);
			erdStartTimeParm.setErrText("��ȡ���߿�ʼ����ʱ�����");
			return erdStartTimeParm;
		}	
		
		TParm result = new TParm();
		result.setData("CASE_NO", caseNo);
		result.setData("MR_NO", mrNo);
		result.setData("ERD_NO", "");// ����
		result.setData("STATUS", "0");// 0�������У�1����Ժ��2��תסԺ��3���ٻ�
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");
		
		// ������Ϣ
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		// �����봲����
		if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// ����������
		}else{
			result.setData("IN_DATE", "");// ����������
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// ��������
		
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));// ���ȴ�
		
		/*result.setData("IN_DATE", inDate);// ����������
		result.setData("ERD_REGION", erdRegion);// ��������
		result.setData("BED_NO", bedNo);*/
		
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// �����ȿ���
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// ����һ��COPY���ݵ�ERD_RECORD���е�ʱ��Ĭ������-��1��

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());
		
		result.setData("OPT_TERM", Operator.getIP());

		return result;
	}

	/**
	 * ��ȡ���߻�����Ϣ
	 * @param mrNo
	 * @return
	 */
	public TParm getPatInfo(String mrNo){
		String sql = "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0 || parm.getCount()<=0){
			parm.setErrCode(-1);
			parm.setErrText("getPatInfo err");			
			return parm;
		}
		// �����ֶΣ���һ������
		parm.setData("SEX", 0, parm.getData("SEX_CODE", 0));
		parm.setData("MARRIGE", 0, parm.getData("MARRIAGE_CODE", 0));
		parm.setData("OCCUPATION", 0, parm.getData("OCC_CODE", 0));

		// 
		if(parm.getData("BIRTH_DATE", 0)!=null){
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			parm.setData("AGE", 0, StringUtil.showAge(parm.getTimestamp("BIRTH_DATE", 0), now));
		}else{
			parm.setData("AGE", 0, "");
		}

		// RESID_PROVICEʡ���
		if(parm.getValue("RESID_POST_CODE", 0)!=null && parm.getValue("RESID_POST_CODE", 0).trim().length()>=2){
			parm.setData("RESID_PROVICE", 0, parm.getValue("RESID_POST_CODE", 0).substring(0, 2));
		}else{
			parm.setData("RESID_PROVICE", 0, "");
		}
		// RESID_PROVICE_DESCʡ����
		if(parm.getValue("RESID_PROVICE", 0)!=null && parm.getValue("RESID_PROVICE", 0).trim().length()>0){
			parm.setData("RESID_PROVICE_DESC", 0, getPatHome(parm.getValue("RESID_PROVICE", 0)).getValue("HOMEPLACE_DESC", 0));		
		}else{
			parm.setData("RESID_PROVICE_DESC", 0, "");	
		}
		// �������	
		parm.setData("RESID_COUNTRY", 0, parm.getValue("RESID_POST_CODE", 0));
		
		parm.setData("FOLK", 0, parm.getValue("SPECIES_CODE", 0));
		parm.setData("NATION", 0, parm.getValue("NATION_CODE", 0));
		parm.setData("OFFICE", 0, parm.getValue("COMPANY_DESC", 0));

		parm.setData("CONTACTER", 0, parm.getValue("CONTACTS_NAME", 0));
		parm.setData("RELATIONSHIP", 0, parm.getValue("RELATION_CODE", 0));
		parm.setData("CONT_ADDRESS", 0, parm.getValue("CONTACTS_ADDRESS", 0));
		parm.setData("CONT_TEL", 0, parm.getValue("CONTACTS_TEL", 0));
		return parm;
	}

	/**
	 * ��ȡ���߹Һ���Ϣ
	 * @param caseNo
	 * @return
	 */
	public TParm getPatRegInfo(String caseNo) {
		String sql = " SELECT REALDR_CODE,ADM_DATE FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * У�黼���Ƿ�ռ��
	 * @param traigeNo ���˺�
	 * @return result�����result.getCount()>0��ռ��������û��ռ��
	 */
	public TParm getErdInfo(String triageNo){
		String sql =" SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE TRIAGE_NO='"+triageNo+"' AND OCCUPY_FLG='Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ��ѯ���ȿ�ʼʱ��
	 * @param triageNo
	 * @return
	 */
	public TParm getErdStartTime(String triageNo){
		String sql = " SELECT MIN(S_M_TIME) AS IN_DATE FROM AMI_E_S_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ������ֵ
	 * @param parm
	 * @param names
	 * @return
	 */
	public boolean setNull(TParm parm, String names){
		if(parm==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm.setData(nameArr[i], "");
		}
		return true;
	}

	/**
	 * ������ֵ
	 * @author wangqing 20171205
	 * @param parm0
	 * @param parm1
	 * @param names
	 * @return
	 */
	public boolean setValue(TParm parm0, TParm parm1, String names){
		if(parm0==null){
			return false;
		}
		if(parm1==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm1.setData(nameArr[i], parm0.getData(nameArr[i], 0));
		}
		return true;
	}

	/**
	 * ���������Ƿ�ɱ༭
	 * @param names
	 * @param isEnabled
	 */
	public void setEnabled(String names, boolean isEnabled){
		if(names==null || names.trim().length()<=0){
			return;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			JComponent c = (JComponent)this.getComponent(nameArr[i]);
			if(c!=null){
				c.setEnabled(isEnabled);	
			}else{

			}			
		}
	}
	
	/**
	 * ��ȡʡ������
	 * @param code
	 * @return
	 */
	public TParm getPatHome(String code) {
		String sql = "SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * ���ݼ��˺Ų�ѯ���߹Һ���Ϣ
	 * @param triageNo
	 * @return
	 */
	public TParm getPatRegInfoByTriageNo(String triageNo){
		String sql = "SELECT * FROM REG_PATADM WHERE TRIAGE_NO='"+triageNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * У�黼���Ƿ�ת��
	 * @param triageNo
	 * @return
	 */
	public TParm getErdOutData(String triageNo){
		String sql = " SELECT TRIAGE_NO, CASE_NO, ERD_REGION_CODE, BED_NO, OUT_DATE FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' "
				+ "AND ERD_REGION_CODE IS NOT NULL AND BED_NO IS NOT NULL AND OUT_DATE IS NOT NULL AND CASE_NO IS NOT NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	
	
	// ------------------------------------add by wangqing 2080131 end------------------------------
	
	
	
	
	
	
	

}
