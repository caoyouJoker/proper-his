package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.bms.BMSApplyMTool;
import jdo.bms.BMSApplyDTool;

import com.itextpdf.text.List;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.StringUtil;

import jdo.sys.IReportTool;
import jdo.sys.OperatorTool;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;

import java.sql.Timestamp;

import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

import jdo.bms.BMSBloodTool;
import jdo.sys.PatTool;

import com.dongyang.ui.TCheckBox;

import jdo.sys.Operator;






import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.dongyang.util.TypeTool;

import java.util.Set;
import java.util.Iterator;

import org.firebirdsql.jdbc.parser.JaybirdSqlParser.arraySpec_return;

import jdo.adm.ADMTool;
import jdo.bms.BMSBldStockTool;
import jdo.bms.BMSSQL;
import jdo.ibs.IBSOrderD;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.util.Manager;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;

/** 
 * <p>
 * Title: ѪҺ����
 * </p>
 *
 * <p>
 * Description: ѪҺ����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSBloodOutControl
    extends TControl {

    // �ⲿ���ô���
    private TParm parm;

    private TTable table_m;

    private TTable table_d;

    private String case_no = "";

    private String adm_type = "";

    private String apply_no = "";

    private String blood_type = "";
    private IBSOrderD order;
    public BMSBloodOutControl() {
    }

    public void onInit() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            parm = (TParm) obj;
            apply_no = parm.getValue("APPLY_NO");
            this.setValue("APPLY_NO", apply_no);
            onTakeNo();
        }
        
        //add by yangjj 20160612
        ((TTextField)this.getComponent("TAKE_NO")).grabFocus();
    	this.openDialog("%ROOT%\\config\\sys\\SYSOpenAndCloseDialog.x");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("TAKE_NO"))) {
            this.messageBox("������ȡѪ����");
            return;
        }
        onTakeNo();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;BLOOD_TEXT;DEPT_CODE;BLOOD_NO;TAKE_NO;"
            + "BLD_CODE;BLD_TYPE;SUBCAT_CODE;SELECT_ALL;RH_A;RH_B;BED_NO;STATION_CODE;CLNCPATH_CODE;SCHD_CODE";
        this.clearValue(clearStr);
        //this.getRadioButton("RH_A").setSelected(true);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        table_m.removeRowAll();
        table_d.removeRowAll();
        case_no = "";
        adm_type = "";
        apply_no = "";
        blood_type = "";
        ((TTextField)this.getComponent("TAKE_NO")).grabFocus();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        
        //add by yangjj
        TParm checkUserParm = checkPW1();
    	if(!"OK".equals(checkUserParm.getData("RESULT")) ){
    		return;
    	}
        String optUser = checkUserParm.getValue("USER_ID");
        
        

        TParm parm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        table_m.acceptText() ;
        //modify by lim 2012/05/22 begin
        String out_no = SystemTool.getInstance().getNo("ALL", "BMS",
                "OUT_NO", "No");    
      //modify by lim 2012/05/22 begin
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if (!"N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
            	//add by yangjj 20150416 ����Ƿ����
            	if(!"1".equals(table_m.getItemString(i, "RESULT"))){
            		this.messageBox("��ⲻ�ܳ���");
            		return;
            	}
            	
            	
                // ����Ƿ��ѳ���
                TParm bloodParm = new TParm(TJDODBTool.getInstance().select(
                    BMSSQL.getBMSBlood(table_m.getItemString(i, "BLOOD_NO"))));
                if (!"".equals(bloodParm.getValue("OUT_NO", 0))) {
                    this.messageBox("ѪƷ��" + table_m.getItemString(i, "BLOOD_NO") +
                                    "�ѳ���");
                    return;
                }
                // ����ѪƷ������Ϣ
                parm.addData("BLOOD_NO", table_m.getItemData(i, "BLOOD_NO"));
                parm.addData("STATE_CODE", "2");
                
                //add by yangjj 20150429����ȡѪ��
                parm.addData("TAKE_NO", this.getValueString("TAKE_NO"));

                parm.addData("OUT_NO", out_no);
                parm.addData("OUT_DATE", date);
                parm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
                parm.addData("STATION_CODE", this.getValueString("STATION_CODE"));
                parm.addData("OUT_USER", optUser);
                parm.addData("OPT_USER", optUser);
                parm.addData("OPT_DATE", date);
                parm.addData("OPT_TERM", Operator.getIP());
                // ����Ѫ����
                parm.addData("BLD_CODE", table_m.getItemData(i, "BLD_CODE"));
                parm.addData("BLD_SUBCAT", table_m.getItemData(i, "SUBCAT_CODE"));
                parm.addData("BLD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
                TParm inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
                    getBMSBldVol(table_m.getItemString(i, "BLD_CODE"),
                                 table_m.getItemString(i, "SUBCAT_CODE"))));
                parm.addData("BLD_VOL", inparm.getData("BLD_VOL", 0));
                //  add  by  chenxi  20130425
                parm.addData("CASE_NO", case_no) ;
                parm.addData("ADM_TYPE", adm_type) ;
                
                //modify by yangjj 20150525 �����ɲ���Ա���ң���Ϊ�ɱ����Ŀ���
                TParm p = new TParm(TJDODBTool.getInstance().select("SELECT COST_CENTER_CODE FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"'"));
                parm.addData("DEPT", p.getValue("COST_CENTER_CODE", 0)) ;
                parm.addData("REGION", Operator.getRegion()) ;
                parm.addData("STATION", Operator.getStation()) ;
            }
        }
        parm.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));
        parm.setData("SCHD_CODE", this.getValue("SCHD_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdateBloodOut", parm);  
        // ������ж�
        
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        TParm feeParm = new TParm() ;
        feeParm.setData( "CASE_NO", case_no);
        feeParm.setData( "PAT_NAME", this.getValueString("PAT_NAME")); 
//        this.messageBox("�Զ��Ʒѳɹ�,�Ʒѽ��Ϊ"+result.getDouble("FEE",0)) ;
        onPrint();
        onClear();
        //TParm feeResult = (TParm) openWindow("%ROOT%\\config\\bms\\BMSQueryFee.x",feeParm);
        
    }

    /**
     * ��ӡ���ⵥ
     */
    public void onPrint() {
        if (table_m.getRowCount() <= 0) {
            this.messageBox("û�г�����Ϣ");
            return;
        }
        boolean flg = false;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("û�г�����Ϣ");
            return;
        }

        table_m.acceptText();
        TParm parmTable = new TParm();
        for(int i = 0; i < table_m.getRowCount(); i++){
        	if("Y".equals(table_m.getItemString(i, "SELECT_FLG"))){
        		
        		TParm p = table_m.getParmValue().getRow(i);
        		parmTable.addData("SELECT_FLG", p.getValue("SELECT_FLG"));
        		parmTable.addData("ORG_BARCODE", p.getValue("ORG_BARCODE"));
        		parmTable.addData("BLOOD_NO", p.getValue("BLOOD_NO"));
        		parmTable.addData("BLD_CODE", p.getValue("BLD_CODE"));
        		parmTable.addData("SUBCAT_CODE", p.getValue("SUBCAT_CODE"));
        		parmTable.addData("BLOOD_VOL", p.getValue("BLOOD_VOL"));
        		parmTable.addData("BLD_TYPE", p.getValue("BLD_TYPE"));
        		parmTable.addData("RH_FLG", p.getValue("RH_FLG"));
        		parmTable.addData("SHIT_FLG", p.getValue("SHIT_FLG"));
        		parmTable.addData("CROSS_MATCH_L", p.getValue("CROSS_MATCH_L"));
        		parmTable.addData("CROSS_MATCH_S", p.getValue("CROSS_MATCH_S"));
        		parmTable.addData("ANTI_A", p.getValue("ANTI_A"));
        		parmTable.addData("ANTI_B", p.getValue("ANTI_B"));
        		parmTable.addData("RESULT", p.getValue("RESULT"));
        		parmTable.addData("TEST_DATE", p.getValue("TEST_DATE"));
        		parmTable.addData("TEST_USER", p.getValue("TEST_USER"));
        		parmTable.addData("RECHECK_USER", p.getValue("RECHECK_USER"));
        		parmTable.addData("RECHECK_TIME", p.getValue("RECHECK_TIME"));
        		parmTable.addData("OUT_DATE", p.getValue("OUT_DATE"));
        		parmTable.addData("OUT_USER", p.getValue("OUT_USER"));
        	}
        }
//        this.messageBox(""+parmTable);
//        this.messageBox(""+parmTable.getValue("BLOOD_NO"));
//        this.messageBox(""+parmTable.getValue("BLOOD_NO",0));
        
//        this.messageBox(""+parmTable.getValue("TEST_USER"));
        
        HashSet set = new HashSet();
        for(int i = 0 ;i<parmTable.getCount("SELECT_FLG");i++){

        	set.add(parmTable.getValue("TEST_USER",i));
        }
        int count = set.size();
//        this.messageBox(count+"");
        Iterator<String> it = set.iterator();
        String str = "";
        while (it.hasNext()) {  
        	  str += it.next()+",";  
        	  System.out.println(str);  
        } 
        String[] strs = str.split(",");
//        this.messageBox(""+Arrays.toString(strs));
        
		for (int i = 0; i < count; i++) {

			TParm date = new TParm();
			date.setData("TITLE", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()) + "ѪƷ���ⵥ");
			date.setData("APPLY_NO", "TEXT",
					"ȡѪ����: " + this.getValueString("TAKE_NO"));
			date.setData("PAT_NAME", "TEXT",
					" ��Ѫ��: " + this.getValueString("PAT_NAME"));
			date.setData("AGE", "TEXT", "����: " + this.getValueString("AGE"));
			date.setData("SEX", "TEXT", "�Ա�: " + this.getValueString("SEX"));
			date.setData("BLD_TYPE", "TEXT",
					" ABOѪ��: " + this.getValueString("BLOOD_TEXT"));
			String rh_type = "";
			if ((this.getRadioButton("RH_A").isSelected())) {
				rh_type = "����";
			} else if ((this.getRadioButton("RH_B").isSelected())) {
				rh_type = "����";
			}
			date.setData("RH_TYPE", "TEXT", "RHѪ��: " + rh_type);
			date.setData("MR_NO", "TEXT",
					" ������: " + this.getValueString("MR_NO"));

			date.setData("DEPT_CODE", "TEXT",
					"�Ʊ�: " + this.getComboBox("DEPT_CODE").getSelectedName());

			date.setData("IPD_NO", "TEXT",
					"סԺ��: " + this.getValueString("IPD_NO"));
			date.setData(
					"STATION_CODE",
					"TEXT",
					"����: "
							+ ((TTextFormat) this.getComponent("STATION_CODE"))
									.getText());
			date.setData(
					"BED_NO",
					"TEXT",
					"��λ: "
							+ ((TTextFormat) this.getComponent("BED_NO"))
									.getText());

			String outNo = "";// ���ⵥ��
			String reason = "";// ��Ѫԭ��
			String deptCode = ""; // ��Ѫ����
			String diag = "";// ���
			String optDate = "";// �ռ�����
			String outDate = "";// ��������
			String caseNo = "";
			// �������
			TParm parm = new TParm();
			String blood_no = "";
			TParm inparm = new TParm();
			String s = strs[i];
			String check = "";
			String match = "";
			for (int j = 0; j < parmTable.getCount("SELECT_FLG"); j++) {
				if (s.equals(parmTable.getValue("TEST_USER", j))) {
					blood_no = parmTable.getValue("BLOOD_NO", j);
					parm.addData("BLOOD_NO", blood_no);

					inparm = new TParm(TJDODBTool.getInstance().select(
							BMSSQL.getBMSBloodOut(blood_no)));

					if ("".equals(outNo)) {
						outNo = inparm.getValue("OUT_NO", 0);
					}
					if ("".equals(reason)) {
						reason += this.getTransReason(inparm.getValue(
								"TRANRSN_CODE1", 0)) + " ";
						reason += this.getTransReason(inparm.getValue(
								"TRANRSN_CODE2", 0)) + " ";
						reason += this.getTransReason(inparm.getValue(
								"TRANRSN_CODE3", 0)) + " ";
					}
					if ("".equals(deptCode)) {
						deptCode = inparm.getValue("DEPT_CHN_DESC", 0);
					}
					if ("".equals(diag)) {
						diag += this.getDiagDesc(inparm.getValue("DIAG_CODE1",
								0)) + " ";
						diag += this.getDiagDesc(inparm.getValue("DIAG_CODE2",
								0)) + " ";
						diag += this.getDiagDesc(inparm.getValue("DIAG_CODE3",
								0)) + " ";
					}
					if ("".equals(optDate)) {
						optDate = inparm.getValue("OPT_DATE", 0);
					}
					if ("".equals(outDate)) {
						outDate = inparm.getValue("OUT_DATE", 0);
					}

					if ("".equals(caseNo)) {
						caseNo = inparm.getValue("CASE_NO", 0);
					}

					parm.addData("ORG_BARCODE",
							inparm.getValue("ORG_BARCODE", 0));// Ժ������

					// modify by lim 2012/05/06 begin
					// parm.addData("BLD_CODE", inparm.getValue("BLDCODE_DESC",
					// 0));//Ժ������
					// parm.addData("SUBCAT_CODE",
					// inparm.getValue("SUBCAT_DESC", 0));//���
					parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));// Ժ������
					parm.addData("SUBCAT_CODE", "");// ���
					// modify by lim 2012/05/06 end

					parm.addData("BLD_TYPE", parmTable.getValue("BLD_TYPE", j));// Ѫ��
					parm.addData("RH_FLG", parmTable.getValue("RH_FLG", j));// RHѪ��
					// parm.addData("SHIT_FLG", table_m.getItemString(i,
					// "SHIT_FLG"));//����
					String crossL = " ";
					if ("0".equals(parmTable.getValue("CROSS_MATCH_L", j))) {
						crossL = "����������Ѫ";
					} else if ("1".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "����������Ѫ";
					} else if ("2".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "����������Ѫ";
					} else if ("3".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "����������Ѫ";
					}
					parm.addData("CROSS_MATCH_L", crossL);

					String crossS = " ";
					if ("0".equals(parmTable.getValue("CROSS_MATCH_S", j))) {
						crossS = "����������Ѫ";
					} else if ("1".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "����������Ѫ";
					} else if ("2".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "����������Ѫ";
					} else if ("3".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "����������Ѫ";
					}
					parm.addData("CROSS_MATCH_S", crossS);
					// parm.addData("ANTI_A", table_m.getItemString(i,
					// "ANTI_A"));
					// parm.addData("ANTI_B", table_m.getItemString(i,
					// "ANTI_B"));
					parm.addData("RESULT", "1".equals(parmTable.getValue(
							"RESULT", j)) ? "���" : "���");
					// parm.addData("TEST_DATE",
					// table_m.getItemString(i, "TEST_DATE").substring(0, 10).
					// replace('-', '/'));
					parm.addData("TEST_DATE",
							parmTable.getValue("TEST_DATE", j).substring(0, 16)
									.replace('-', '/'));
					check = parmTable.getValue("RECHECK_USER", j);// ������
					match = parmTable.getValue("TEST_USER", j);// ��Ѫ��
				}

			}
			parm.setCount(parm.getCount("BLOOD_NO"));
			parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
			parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
			parm.addData("SYSTEM", "COLUMNS", "BLD_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SUBCAT_CODE");
			parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
			parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
			// parm.addData("SYSTEM", "COLUMNS", "SHIT_FLG");
			parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_L");
			parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_S");
			// parm.addData("SYSTEM", "COLUMNS", "ANTI_A");
			// parm.addData("SYSTEM", "COLUMNS", "ANTI_B");
			parm.addData("SYSTEM", "COLUMNS", "RESULT");
			parm.addData("SYSTEM", "COLUMNS", "TEST_DATE");

			date.setData("TABLE", parm.getData());

			// modify by lim 2012/05/17 begin
			String deptDesc = "";
			String sql = "SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE AND A.CASE_NO='"
					+ caseNo + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				deptDesc = "";
			} else {
				deptDesc = result.getValue("DEPT_CHN_DESC", 0);
			}
			// modify by lim 2012/05/17 end
			// TParm inparm2 =
			// SYSOperatorTool.getInstance().selectdata(table_m.getItemString(0,
			// "TEST_USER")) ;
			// wuxy 20170622 start
			int index = table_m.getSelectedRow();
			TParm parmValue = new TParm();
			parmValue = table_m.getParmValue().getRow(index);

			// modified by wangqing 20171225  ����Ҫ�󣬲���ʾ��Ѫ�ˡ������ˡ���Ѫ
//			date.setData("OUT_USER", "TEXT", Operator.getName());// ��Ѫ��
			String sqlName = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='X'";
			System.out.println("99999"
					+ sqlName.replace("X", parmValue.getValue("RECHECK_USER")));
			String checkName = new TParm(TJDODBTool.getInstance().select(
					sqlName.replace("X", check))).getValue("USER_NAME", 0);// �����
			String matchName = new TParm(TJDODBTool.getInstance().select(
					sqlName.replace("X", match))).getValue("USER_NAME", 0);// ��Ѫ��
//			date.setData("TEXT_USER", "TEXT", matchName);// ��Ѫ��
//			date.setData("RECHECK_USER", "TEXT", checkName);// ������
			// wuxy 20170622 end
			date.setData("OUT_NO", "TEXT", outNo);
			date.setData("REASON", "TEXT", "��Ѫԭ��:" + reason);
			date.setData("YXKS", "TEXT", "��Ѫ����:" + deptDesc);
			// date.setData("YXKS", "TEXT", "��Ѫ����:"+deptCode) ;
			String recpDate = "";
			if (null != optDate && !"".equals(optDate)) {
				recpDate = optDate.substring(0, 16).replace('-', '/');
			}
			date.setData("TEST_DATE", "TEXT", " �ռ�����:" + recpDate);
			date.setData("JWSH", "TEXT", "���: " + diag);
			if (!"".equals(outDate) && outDate != null) {
				outDate = outDate.substring(0, 19).replace("-", "/");
			}
			date.setData("OUT_DATE", "TEXT", outDate);
			// date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodOut.jhw",
					date);
		}
//        
//        
//        
//        
//        TParm date = new TParm();
//        date.setData("TITLE", "TEXT", Manager.getOrganization().
//                     getHospitalCHNFullName(Operator.getRegion()) +
//                     "ѪƷ���ⵥ");
//        date.setData("APPLY_NO", "TEXT", "ȡѪ����: " + this.getValueString("TAKE_NO"));
//        date.setData("PAT_NAME", "TEXT", " ��Ѫ��: " + this.getValueString("PAT_NAME"));
//        date.setData("AGE", "TEXT", "����: " + this.getValueString("AGE"));
//        date.setData("SEX", "TEXT", "�Ա�: " + this.getValueString("SEX"));
//        date.setData("BLD_TYPE", "TEXT", " ABOѪ��: " + this.getValueString("BLOOD_TEXT"));
//        String rh_type = "";
//        if ( (this.getRadioButton("RH_A").isSelected())) {
//            rh_type = "����";
//        }
//        else if ( (this.getRadioButton("RH_B").isSelected())) {
//            rh_type = "����";
//        }
//        date.setData("RH_TYPE", "TEXT", "RHѪ��: " + rh_type);
//        date.setData("MR_NO", "TEXT", " ������: " + this.getValueString("MR_NO"));
//   
//        date.setData("DEPT_CODE", "TEXT",
//                     "�Ʊ�: " + this.getComboBox("DEPT_CODE").getSelectedName());
//
//        date.setData("IPD_NO", "TEXT", "סԺ��: " + this.getValueString("IPD_NO"));
//        date.setData("STATION_CODE", "TEXT",
//                     "����: " +
//                     ( (TTextFormat)this.getComponent("STATION_CODE")).getText());
//        date.setData("BED_NO", "TEXT",
//                     "��λ: " + ( (TTextFormat)this.getComponent("BED_NO")).getText());
//       
//
//        String outNo = "" ;//���ⵥ��
//        String reason = "" ;//��Ѫԭ��
//        String deptCode = "" ; //��Ѫ����
//        String diag = "" ;//���
//        String optDate = "" ;//�ռ�����
//        String outDate = "" ;//��������
//        String caseNo = "" ;
//        // �������
//        TParm parm = new TParm();
//        String blood_no = "";
//        TParm inparm = new TParm();
//        table_m.acceptText() ;
//        for (int i = 0; i < table_m.getRowCount(); i++) {
//            if (!"Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
//                continue;
//            }
//            blood_no = table_m.getItemString(i, "BLOOD_NO");
//            parm.addData("BLOOD_NO", blood_no);
//            inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
//                getBMSBloodOut(blood_no)));
//            
//            if("".equals(outNo)){
//                outNo = inparm.getValue("OUT_NO", 0) ;
//            }
//            if("".equals(reason)){
//                reason += this.getTransReason(inparm.getValue("TRANRSN_CODE1", 0)) +" ";
//                reason += this.getTransReason(inparm.getValue("TRANRSN_CODE2", 0)) +" ";
//                reason += this.getTransReason(inparm.getValue("TRANRSN_CODE3", 0)) +" ";
//            }
//            if("".equals(deptCode)){
//                deptCode = inparm.getValue("DEPT_CHN_DESC", 0) ;
//            }
//            if("".equals(diag)){
//                diag += this.getDiagDesc(inparm.getValue("DIAG_CODE1", 0)) +" ";
//                diag += this.getDiagDesc(inparm.getValue("DIAG_CODE2", 0)) +" ";
//                diag += this.getDiagDesc(inparm.getValue("DIAG_CODE3", 0)) +" ";
//            }
//            if("".equals(optDate)){
//                optDate = inparm.getValue("OPT_DATE", 0) ;
//            }
//            if("".equals(outDate)){
//                outDate = inparm.getValue("OUT_DATE", 0) ;
//            }
//            
//            if("".equals(caseNo)){
//                caseNo = inparm.getValue("CASE_NO", 0) ;
//            }
//            
//            parm.addData("ORG_BARCODE", inparm.getValue("ORG_BARCODE",0)) ;//Ժ������
//            
//            //modify by lim 2012/05/06 begin
//            //parm.addData("BLD_CODE", inparm.getValue("BLDCODE_DESC", 0));//Ժ������
//            //parm.addData("SUBCAT_CODE", inparm.getValue("SUBCAT_DESC", 0));//���
//            parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));//Ժ������
//            parm.addData("SUBCAT_CODE", "");//���
//            //modify by lim 2012/05/06 end
//            
//            parm.addData("BLD_TYPE", table_m.getItemString(i, "BLD_TYPE"));//Ѫ��
//            parm.addData("RH_FLG", table_m.getItemString(i, "RH_FLG"));//RHѪ��
////            parm.addData("SHIT_FLG", table_m.getItemString(i, "SHIT_FLG"));//����
//            String crossL = " " ;
//            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "����������Ѫ" ;
//            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "����������Ѫ" ;
//            }else if("2".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "����������Ѫ" ;
//            }else if("3".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "����������Ѫ" ;
//            }
//            parm.addData("CROSS_MATCH_L",crossL);
//            
//            String crossS = " " ;
//            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "����������Ѫ" ;
//            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "����������Ѫ" ;
//            }else if("2".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "����������Ѫ" ;
//            }else if("3".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "����������Ѫ" ;
//            }            
//            parm.addData("CROSS_MATCH_S",crossS);
////            parm.addData("ANTI_A", table_m.getItemString(i, "ANTI_A"));
////            parm.addData("ANTI_B", table_m.getItemString(i, "ANTI_B"));
//            parm.addData("RESULT",
//                         "1".equals(table_m.getItemString(i, "RESULT")) ? "���" :
//                         "���");
////            parm.addData("TEST_DATE",
////                         table_m.getItemString(i, "TEST_DATE").substring(0, 10).
////                         replace('-', '/'));
//            parm.addData("TEST_DATE",
//                    table_m.getItemString(i, "TEST_DATE").substring(0, 16).
//                    replace('-', '/'));
//        }
//        
//        parm.setCount(parm.getCount("BLOOD_NO"));
//        parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
//        parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
//        parm.addData("SYSTEM", "COLUMNS", "BLD_CODE");
//        parm.addData("SYSTEM", "COLUMNS", "SUBCAT_CODE");
//        parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
//        parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
////        parm.addData("SYSTEM", "COLUMNS", "SHIT_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_L");
//        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_S");
////        parm.addData("SYSTEM", "COLUMNS", "ANTI_A");
////        parm.addData("SYSTEM", "COLUMNS", "ANTI_B");
//        parm.addData("SYSTEM", "COLUMNS", "RESULT");
//        parm.addData("SYSTEM", "COLUMNS", "TEST_DATE");
//
//        date.setData("TABLE", parm.getData());
//        
//        //modify by lim 2012/05/17 begin
//        String deptDesc = "" ;
//        String sql = "SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE AND A.CASE_NO='"+caseNo+"'" ;
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
//        if(result.getErrCode()<0 || result.getCount()<=0){
//            deptDesc = "" ;
//        }else{
//            deptDesc = result.getValue("DEPT_CHN_DESC", 0) ;
//        }
//        //modify by lim 2012/05/17 end
//        
//        
//        TParm inparm2 = SYSOperatorTool.getInstance().selectdata(table_m.getItemString(0, "TEST_USER"))  ;      
//        //wuxy 20170622 start
//        int index  = table_m.getSelectedRow();
//        TParm parmValue = new TParm(); 
//        parmValue = table_m.getParmValue().getRow(index);
//        date.setData("OUT_USER","TEXT",Operator.getName()) ;//��Ѫ��
//        String sqlName = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='X'";
//        System.out.println("99999"+sqlName.replace("X", parmValue.getValue("RECHECK_USER")));
//        String checkName = new TParm(TJDODBTool.getInstance().select(sqlName.replace("X", parmValue.getValue("RECHECK_USER")))).getValue("USER_NAME", 0);//�����
//        String matchName = new TParm(TJDODBTool.getInstance().select(sqlName.replace("X", parmValue.getValue("TEST_USER")))).getValue("USER_NAME", 0);//��Ѫ��
//        date.setData("TEXT_USER","TEXT",matchName) ;//��Ѫ��
//        date.setData("RECHECK_USER","TEXT",checkName) ;//������
//        //wuxy 20170622 end
//        date.setData("OUT_NO", "TEXT",  outNo);
//        date.setData("REASON", "TEXT","��Ѫԭ��:"+reason) ;
//        date.setData("YXKS", "TEXT", "��Ѫ����:"+deptDesc) ;
////        date.setData("YXKS", "TEXT", "��Ѫ����:"+deptCode) ;
//        String recpDate = "" ;
//        if(null!=optDate && !"".equals(optDate)){
//            recpDate =optDate.substring(0, 16).replace('-', '/') ;
//        }
//        date.setData("TEST_DATE","TEXT"," �ռ�����:"+recpDate) ;
//        date.setData("JWSH","TEXT","���: "+diag) ;
//        if(!"".equals(outDate)&& outDate!=null){
//            outDate = outDate.substring(0, 19).replace("-", "/") ;
//        }
//        date.setData("OUT_DATE","TEXT",outDate) ;
////        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
//        // ���ô�ӡ����
//        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodOut.jhw", date);
    }
    
    private String getDiagDesc(String diagCode){
        String desc = "" ;
        if(diagCode!=null && !"".equals(diagCode)){
            String sql = "SELECT A.ICD_CHN_DESC FROM SYS_DIAGNOSIS A WHERE A.ICD_CODE='"+diagCode+"'" ;
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getErrCode()<0){
                return desc ;
            }
            if(result.getCount() <= 0){
                return desc ;
            }
            desc = result.getValue("ICD_CHN_DESC", 0) ; 
        }
        return desc ;
    }
    
    private String getTransReason(String transReasonCode){
        String desc = "" ;
        if(transReasonCode!=null && !"".equals(transReasonCode)){
            String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='BMS_TRANRSN' AND ID='"+transReasonCode+"'" ;
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if(result.getErrCode()<0){
                return desc ;
            }
            if(result.getCount() <= 0){
                return desc ;
            }
            desc = result.getValue("CHN_DESC", 0) ; 
        }
        return desc ;
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
            setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
            setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
            setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
            if ("+".equals(table.getItemString(row, "RH_FLG"))) {
                this.getRadioButton("RH_TYPE_A").setSelected(true);
            }
            else {
                this.getRadioButton("RH_TYPE_B").setSelected(true);
            }
        }
    }
    
    /**
     * ɨ��Ժ������ţ���ѡTABLE_M
     */
    //add by yangjj 20150423
    public void onBloodNo(){
    	TTable table = getTable("TABLE_M");
    	table.acceptText();
    	TParm parm = table.getParmValue();
    	String bloodNo = this.getValueString("BLOOD_NO");
    	if("".equals(bloodNo)){
    		this.messageBox("��ɨ��Ժ�����룡");
    		setValue("BLOOD_NO", "");
    		return ;
    	}
    	for(int i = 0 ; i <= parm.getCount() ; i++){
    		if(i == parm.getCount()){
    			this.messageBox("Ժ�����벻���ڣ�");
    			setValue("BLOOD_NO", "");
    			return;
    		}
    		
    		//Ժ����������Ժ������ͬ
    		if(bloodNo.equals(parm.getValue("BLOOD_NO", i))){
    			
    			//�����ۻ���
    			if(!"Y".equals(parm.getValue("SELECT_FLG", i))){
    				table_d.acceptText();
    				TParm dParm = table_d.getParmValue();
    				for(int j = 0 ; j <= dParm.getCount() ; j++){
    					if( j == dParm.getCount()){
    						this.messageBox("��ѪƷ����ȡѪ����,��������⣡");
    						setValue("BLOOD_NO", "");
    						return;
    					}
    					if(table.getItemData(i, "BLD_CODE").equals(dParm.getValue("BLD_CODE", j))){
    						String take_d = dParm.getValue("OUT_QTY", j);
    						double double_take_d = Double.parseDouble(take_d);
    						
    						String take_m = table.getItemString(i, "BLOOD_VOL");
    						double double_take_m = Double.parseDouble(take_m);
    						
    						double d = double_take_d+double_take_m;
    						dParm.setData("OUT_QTY", j, d);
    						table_d.setParmValue(dParm);
    						break;
    					}
    				}
    			}
    			
                setValue("BLD_CODE", table.getItemData(i, "BLD_CODE"));
                setValue("SUBCAT_CODE", table.getItemData(i, "SUBCAT_CODE"));
                setValue("BLD_TYPE", table.getItemData(i, "BLD_TYPE"));
                if ("+".equals(table.getItemString(i, "RH_FLG"))) {
                    this.getRadioButton("RH_TYPE_A").setSelected(true);
                }
                else {
                    this.getRadioButton("RH_TYPE_B").setSelected(true);
                }
                parm.setData("SELECT_FLG", i, "Y");
    			table.setParmValue(parm);
    			
    			
    			TParm printParm = new TParm();
    	    	table_m.acceptText();
    			//ȡѪ����Ϣ
    			printParm.setData("BLOOD_NO", table_m.getItemString(i, "BLOOD_NO"));
    			printParm.setData("MR_NO", getValueString("MR_NO"));
    			printParm.setData("NAME", getValueString("PAT_NAME"));
    			printParm.setData("DEPT_CODE", this.getComboBox("DEPT_CODE").getSelectedName());
    			printParm.setData("BED_NO", getText("BED_NO"));
    			printParm.setData("SEX", getValueString("SEX"));
    			printParm.setData("PATIENT_BLOOD", getValueString("BLOOD_TEXT"));
    			printParm.setData("AGE",getValueString("AGE"));
    			
    			
    			//Ѫ����Ϣ
    			printParm.setData("BLOOD_CODE", table_m.getItemData(i, "BLD_CODE"));
    			printParm.setData("BLOOD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
    			printParm.setData("RH", "RH:"+table_m.getItemData(i, "RH_FLG"));
    			printParm.setData("CROSS_TIME", table_m.getItemData(i, "TEST_DATE"));
    			printParm.setData("TEST_USER", table_m.getItemData(i, "TEST_USER"));
    			printParm.setData("RECHECK_USER", table_m.getItemData(i, "RECHECK_USER"));
    			printParm.setData("OUT_USER", Operator.getName());
    			printParm.setData("SUBCAT_CODE",table_m.getItemData(i, "SUBCAT_CODE"));
    			String r = "1".equals(table_m.getItemString(i, "RESULT"))?"���":"���";
    			printParm.setData("RESULT",r);
    			printParm.setData("OrgBarCode","Ժ����:"+table_m.getItemString(i, "ORG_BARCODE"));
    			
    			
    			printBarCode(printParm);
    			setValue("BLOOD_NO", "");
    			break;
    		}
    	}
    }

    
    

    /**
     * ȡѪ���Ų�ѯ(�س��¼�)
     */
    public void onTakeNo() {
        String takeNo = this.getValueString("TAKE_NO");
        if (!"".equals(takeNo)) {
            TParm parm = new TParm();
            parm.setData("TAKE_NO", takeNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(getTakeSql(takeNo)));
            if (result.getCount("TAKE_NO") == 0 || result.getCount() <= 0) {
                this.messageBox("ȡѪ��������");
                this.setValue("TAKE_NO", "");
                return;
            }
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));

            // ��ѯ������Ϣ
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            this.setValue("BED_NO", result.getValue("BED_NO", 0));
            this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
            this.setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            String rh_type = pat.getBloodRHType();
            if ("-".equals(rh_type)) {
                getRadioButton("RH_B").setSelected(true);
            }
            else if ("+".equals(rh_type)) {
                getRadioButton("RH_A").setSelected(true);
            }
            else {
                getRadioButton("RH_A").setSelected(false);
                getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());

            case_no = result.getValue("CASE_NO", 0);
            adm_type = result.getValue("ADM_TYPE", 0);
            blood_type = pat.getBloodType();

            result = new TParm(TJDODBTool.getInstance().select(getTakeDSql(takeNo)));
            if (result != null && result.getCount() > 0) {
                table_d.setParmValue(result);
            }
            
            result = new TParm(TJDODBTool.getInstance().select(getBloodSql(getValueString("MR_NO"))));
            table_m.setParmValue(result);
            
            //add by yangjj 20160612
            ((TTextField)this.getComponent("BLOOD_NO")).grabFocus();
            String schdCodeInit = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+case_no+"'";
			//=========pangben 2016-10-01 �޸����²�ѯ�ٴ�·������
			TParm schdCodeParm = new TParm(TJDODBTool.getInstance().select(schdCodeInit));
			String schdCode = schdCodeParm.getValue("SCHD_CODE", 0);// ʱ�̴���
			String clncpathCode = schdCodeParm.getValue("CLNCPATH_CODE", 0);// �ٴ�·������
			if(null!=clncpathCode && clncpathCode.length()>0){
				callFunction("UI|SCHD_CODE|setEnabled", true);
				this.setValue("CLNCPATH_CODE", clncpathCode);
				if(null!=schdCode && schdCode.length()>0){
					this.setValue("SCHD_CODE", schdCode);
				}
				TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
				combo_schd.setClncpathCode(clncpathCode);
		        combo_schd.onQuery();
			}else{
				this.setValue("SCHD_CODE","");
				callFunction("UI|SCHD_CODE|setEnabled", false);
			}
			
        }
    }

    /**
     * �����Ų�ѯ(�س��¼�)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm resultParm = BMSApplyMTool.getInstance().onApplyQuery(parm);
        if (resultParm.getCount("APPLY_NO") == 0 || resultParm.getCount() <= 0) {
            this.messageBox("�����ڸò����ı�Ѫ��");
            this.setValue("MR_NO", "");
            return;
        }
        //modify by liming 2012/03/09 begin
        parm.setData("CASE_NO",resultParm.getData("CASE_NO", 0)) ;
        //modify by liming 2012/03/09 end
        Object result = openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            this.setValue("APPLY_NO", addParm.getValue("APPLY_NO"));
            onTakeNo();
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table_m.acceptText();
        if (table_m.getRowCount() <= 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", false);
                table_d.setItem(i, "OUT_QTY", 0);
            }
        }
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean CheckData() {
        // ѪҺ��Ϣ���
        if (table_m.getRowCount() == 0) {
            this.messageBox("û�г�����Ϣ");
            return false;
        }
        boolean flg = true;
        
        String rh_type = "";
        if ( (this.getRadioButton("RH_A").isSelected())) {
            rh_type = "+";
        }
        else if ( (this.getRadioButton("RH_B").isSelected())) {
            rh_type = "-";
        }        
        
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if (!"N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
            // ѪҺ�벡��������ѪҺ����
            boolean check_flg = true;
            for (int j = 0; j < table_d.getRowCount(); j++) {
                if (table_d.getItemString(j, "BLD_CODE").equals(table_m.
                    getItemString(i, "BLD_CODE"))) {
                    check_flg = false;
                }
            }
            if (check_flg) {
                if (this.messageBox("��ʾ",
                                    "Ѫ������ " +
                                    table_m.getItemString(i, "BLOOD_NO") +
                                    " ��ѪҺ�벡��������ѪҺ����,�Ƿ���⣿", 2) != 0) {
                    return false;
                }
            }

            // Ѫ�ͱȽ�
            if (!blood_type.equals(table_m.getItemString(i, "BLD_TYPE"))) {
            	if (this.messageBox("��ʾ",
                        "Ѫ������ " +
                        table_m.getItemString(i, "BLOOD_NO") +
                        " ��Ѫ���벡����Ѫ�Ͳ���,������ѡ��", 2) != 0) {
            		return false;
            	}
            }

            //modify by lim 2012/04/26 begin
            // ������
//            if ("2".equals(table_m.getItemString(i, "RESULT"))) {
//                this.messageBox("Ѫ������ " + table_m.getItemString(i, "BLOOD_NO") +
//                                " �ļ������ж�Ϊ'���',���ܳ���");
//                return false;
//            }
            
            //RHѪ��     chenxi modify 20130407   
            if(!rh_type.equals(table_m.getItemString(i, "RH_FLG"))){
                 int check =    messageBox("��Ϣ","Ѫ������ " + table_m.getItemString(i, "BLOOD_NO") +
                            " ��RHѪ���벡��RHѪ�Ͳ���,���ܳ��⡣�Ƿ����?", 0) ;  
                 if(check==0){   //����У��      chenxi 
                     if (!checkPW()) {
                            return false;
                        }
                 }
                 if(check!=0){
                        return false ;
                        }
            }
            //modify by lim 2012/04/26 end
        }
        if (flg) {
            this.messageBox("��ѡ�����ѪҺ");
            return false;
        }

        // ѪҺ������
        Map mapOut = new HashMap();
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if (mapOut.isEmpty()) {
                mapOut.put(table_m.getItemString(i, "BLD_CODE"), 1);
            }
            else {
                if (mapOut.containsKey(table_m.getItemString(i, "BLD_CODE"))) {
                    double qty = TypeTool.getDouble(mapOut.get(table_m.
                        getItemString(i, "BLD_CODE"))) + 1;
                    mapOut.put(table_m.getItemString(i, "BLD_CODE"), qty);
                }
                else {
                    mapOut.put(table_m.getItemString(i, "BLD_CODE"), 1);
                }
            }
        }
        // ѪҺ������
        Map mapReq = new HashMap();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (mapReq.isEmpty()) {
                mapReq.put(table_d.getItemString(i, "BLD_CODE"), 1);
            }
            else {
                if (mapReq.containsKey(table_d.getItemString(i, "BLD_CODE"))) {
                    double qty = TypeTool.getDouble(mapReq.get(table_d.
                        getItemString(i, "BLD_CODE"))) + 1;
                    mapReq.put(table_d.getItemString(i, "BLD_CODE"), qty);
                }
                else {
                    mapReq.put(table_d.getItemString(i, "BLD_CODE"), 1);
                }
            }
        }

//        // ѪҺ���������������Ƚ�
//        Set set = mapReq.keySet();
//        Iterator iterator = set.iterator();
//        String bld_code = "";
//        double req_qty = 0;
//        double out_qty = 0;
//        while (iterator.hasNext()) {
//            bld_code = TypeTool.getString(iterator.next());
//            req_qty = TypeTool.getDouble(mapReq.get(bld_code));
//            out_qty = TypeTool.getDouble(mapOut.get(bld_code));
//            if (out_qty > req_qty) {
//                this.messageBox(bld_code + " �ĳ������Ѵ���������,������ѡ��!");
//                return false;
//            }
//        }

        // �жϿ����
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            TParm parm = new TParm();
            parm.setData("BLD_CODE", table_m.getItemData(i, "BLD_CODE"));
            parm.setData("BLD_SUBCAT", table_m.getItemData(i, "BLD_SUBCAT"));
            parm.setData("BLD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
            TParm result = BMSBldStockTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("ѪƷ" + table_m.getItemData(i, "BLD_CODE") + "��治��");
                return false;
            }
        }

        // ������Ϣ���
        if ("I".equals(adm_type)) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", case_no);
            TParm result = ADMTool.getInstance().getADM_INFO(parm);
            // �жϸò����Ƿ��Ժ
            if (!"".equals(result.getValue("DS_DATE", 0))) {
                this.messageBox("�˲����ѳ�Ժ");
                return false;
            }
        }
        //================pangben 2016-09-30 ����ٴ�·��У��ʱ��
        if(null!=this.getValue("CLNCPATH_CODE")&&this.getValue("CLNCPATH_CODE").toString().length()>0){
        	if(null==this.getValue("SCHD_CODE")||this.getValue("SCHD_CODE").toString().length()<=0){
        		this.messageBox("��ѡ����·��ʱ��");
        		return false;
        	}
        }
        return true;
    }
    
    /*
     * Ժ�����ǩ�����¼�
     * */
    public void onPrintBarCode(){
    	int i = table_m.getSelectedRow();
    	if(i < 0 ){
    		this.messageBox("��ѡ����Ҫ����ı�ǩ��");
    		return;
    	}
    	
    	TParm printParm = new TParm();
    	table_m.acceptText();
		//ȡѪ����Ϣ
		printParm.setData("BLOOD_NO", table_m.getItemString(i, "BLOOD_NO"));
		printParm.setData("MR_NO", getValueString("MR_NO"));
		printParm.setData("NAME", getValueString("PAT_NAME"));
		printParm.setData("DEPT_CODE", this.getComboBox("DEPT_CODE").getSelectedName());
		printParm.setData("BED_NO", getText("BED_NO"));
		printParm.setData("SEX", getValueString("SEX"));
		printParm.setData("PATIENT_BLOOD", getValueString("BLOOD_TEXT"));
		printParm.setData("AGE",getValueString("AGE"));
		
		
		//Ѫ����Ϣ
		printParm.setData("BLOOD_CODE", table_m.getItemData(i, "BLD_CODE"));
		printParm.setData("BLOOD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
		printParm.setData("RH", "RH:"+table_m.getItemData(i, "RH_FLG"));
		printParm.setData("CROSS_TIME", table_m.getItemData(i, "TEST_DATE"));
		printParm.setData("TEST_USER", table_m.getItemData(i, "TEST_USER"));
		printParm.setData("RECHECK_USER", table_m.getItemData(i, "RECHECK_USER"));
		printParm.setData("OUT_USER", Operator.getName());
		printParm.setData("SUBCAT_CODE",table_m.getItemData(i, "SUBCAT_CODE"));
		String r = "1".equals(table_m.getItemString(i, "RESULT"))?"���":"���";
		printParm.setData("RESULT",r);
		printParm.setData("OrgBarCode","Ժ����:"+table_m.getItemString(i, "ORG_BARCODE"));
		printBarCode(printParm);
    }
    
    
    
    public void printBarCode(TParm p){ 
    	TParm result = new TParm();
    	
    	String bloodNo = p.getValue("BLOOD_NO");
    	String mrNo = p.getValue("MR_NO");
    	String name = p.getValue("NAME");
    	String deptCode = p.getValue("DEPT_CODE");
    	String bedNo = p.getValue("BED_NO");
    	String sex = p.getValue("SEX");
    	String patientBlood = p.getValue("PATIENT_BLOOD");
    	String age = p.getValue("AGE");
    	
    	result = new TParm(TJDODBTool.getInstance().select("SELECT BLDCODE_DESC AS BLD_NAME FROM BMS_BLDCODE WHERE BLD_CODE = '"+p.getValue("BLOOD_CODE")+"'"));
    	String bloodCode = result.getValue("BLD_NAME", 0);
    	String bloodType = p.getValue("BLOOD_TYPE");
    	String rh = p.getValue("RH");
    	String crossTime = "��Ѫ����:"+p.getValue("CROSS_TIME").replace(".0", "").replace("-", "/");
    	result = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME AS NAME FROM SYS_OPERATOR WHERE USER_ID = '"+p.getValue("TEST_USER")+"'"));
    	String testUser = "��Ѫ��:"+result.getValue("NAME", 0);
    	result = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME AS NAME FROM SYS_OPERATOR WHERE USER_ID = '"+p.getValue("RECHECK_USER")+"'"));
    	String recheckUser = "�����:"+result.getValue("NAME", 0);
    	String outUser = "��Ѫ��:"+p.getValue("OUT_USER");
//    	String recheckUser = "�����:"+p.getValue("OUT_USER");
//    	String testUser = "��Ѫ��:"+p.getValue("OUT_USER");
    	result = new TParm(TJDODBTool.getInstance().select("SELECT SUBCAT_DESC AS SUBCAT_NAME FROM BMS_BLDSUBCAT WHERE SUBCAT_CODE = '"+p.getValue("SUBCAT_CODE")+"'"));
    	String subcat = result.getValue("SUBCAT_NAME", 0);
    	String crossResult = p.getValue("RESULT");
    	String orgBarCode = p.getValue("OrgBarCode");
    	
    	
    	
    	
    	TParm parm = new TParm();
    	parm.setData("BarCode", "TEXT" ,bloodNo);
    	parm.setData("MrNo", "TEXT" ,mrNo);
    	parm.setData("Name", "TEXT" ,name);
    	parm.setData("BedNo", "TEXT" ,bedNo);
    	parm.setData("DeptCode", "TEXT" ,deptCode);
    	parm.setData("Sex", "TEXT" ,sex);
    	parm.setData("Subcat", "TEXT",subcat);
    	parm.setData("PatientBlood","TEXT",patientBlood);
    	parm.setData("AGE","TEXT",age);
    	parm.setData("Result","TEXT",crossResult);
    	parm.setData("BloodCode", "TEXT" ,bloodCode);
    	parm.setData("BloodType", "TEXT" ,bloodType);
    	parm.setData("RH", "TEXT" ,rh);
    	parm.setData("CrossTime", "TEXT" ,crossTime);
    	parm.setData("TestUser", "TEXT" ,testUser);
    	parm.setData("ReckeckUser", "TEXT" ,recheckUser);
    	parm.setData("OutUser", "TEXT" ,outUser);
    	parm.setData("OrgBarCode","TEXT",orgBarCode);
    	this.openPrintDialog("%ROOT%\\config\\prt\\BMS\\BMSOutBarCode.jhw", parm, true);
    	//this.openPrintDialog(IReportTool.getInstance()
          //      .getReportPath("BMS\\bmsBMSOutBarCode.jhw"), parm, true);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
    * �õ�ComboBox����
    *
    * @param tagName
    *            Ԫ��TAG����
    * @return
    */
   private TComboBox getComboBox(String tagName) {
       return (TComboBox) getComponent(tagName);
   }

   //=========================  chenxi modify 201305
   /**
     * ����������֤
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String bmsExe = "bmsExe";
        String value = (String) this.openDialog(
                "%ROOT%\\config\\inw\\passWordCheck.x", bmsExe); 
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }   
    
    public TParm checkPW1() {
		String singleExe = "singleExe";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", singleExe);
		return parm;
	}
    
    public String getTakeSql(String takeNo){
    	String sql = "";
    	sql += " SELECT " +
    				" BLOOD_TANO AS TAKE_NO, " +
    				" CASE_NO, " + 
    				" MR_NO, " +
    				" IPD_NO, " +
    				" ADM_TYPE, " +
    				" BED_NO, " +
    				" DEPT_CODE, " +
    				" STATION_CODE " +
    			" FROM " +
    				" BMS_BLDTAKEM " + 
    			" WHERE " + 
    				" BLOOD_TANO = '"+takeNo+"'";
    	return sql;			
    }
    
    public String getTakeDSql(String takeNo){
    	String sql = "";
    	sql += " SELECT " +
    				" BLD_CODE, " +
    				" APPLY_QTY, " +
    				" 0.0 AS OUT_QTY, " +
    				" UNIT_CODE " +
    			" FROM " +
    				" BMS_BLDTAKED " +
    			" WHERE " +
    				" BLOOD_TANO = '"+takeNo+"'";
    	return sql;
    }
    
    public String getBloodSql(String mrNo){
    	String sql = "";
    	sql += " SELECT " +
    				" 'N' AS SELECT_FLG, "+
    				" ORG_BARCODE, "+
    				" BLOOD_NO, " +
    				" BLD_CODE, " +
    				" SUBCAT_CODE, " +
    				" BLOOD_VOL, "+
    				" BLD_TYPE, " +
    				" RH_FLG, " + 
    				" SHIT_FLG, " +
    				" CROSS_MATCH_L, " +
    				" CROSS_MATCH_S, " +
    				" ANTI_A, " +
    				" ANTI_B, " +
    				" RESULT, " +
    				" TEST_DATE, " +
    				" TEST_USER, " +
    				" RECHECK_USER, "+
    				" RECHECK_TIME, " +
    				" OUT_DATE, " +
    				" OUT_USER " +
		 	   " FROM " +
		 	   		" BMS_BLOOD " +
		 	   " WHERE " +
		 	   		" (OUT_NO IS NULL OR OUT_NO = '') "+
		 	   		" AND MR_NO = '"+mrNo+"'";
    	
    	return sql;
    }
    
    
}
