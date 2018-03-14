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
 * Title: 血液出库
 * </p>
 *
 * <p>
 * Description: 血液出库
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

    // 外部调用传参
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
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("TAKE_NO"))) {
            this.messageBox("请输入取血单号");
            return;
        }
        onTakeNo();
    }

    /**
     * 清空方法
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
     * 保存方法
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
            	//add by yangjj 20150416 检查是否相斥
            	if(!"1".equals(table_m.getItemString(i, "RESULT"))){
            		this.messageBox("相斥不能出库");
            		return;
            	}
            	
            	
                // 检核是否已出库
                TParm bloodParm = new TParm(TJDODBTool.getInstance().select(
                    BMSSQL.getBMSBlood(table_m.getItemString(i, "BLOOD_NO"))));
                if (!"".equals(bloodParm.getValue("OUT_NO", 0))) {
                    this.messageBox("血品单" + table_m.getItemString(i, "BLOOD_NO") +
                                    "已出库");
                    return;
                }
                // 更新血品出库信息
                parm.addData("BLOOD_NO", table_m.getItemData(i, "BLOOD_NO"));
                parm.addData("STATE_CODE", "2");
                
                //add by yangjj 20150429增加取血单
                parm.addData("TAKE_NO", this.getValueString("TAKE_NO"));

                parm.addData("OUT_NO", out_no);
                parm.addData("OUT_DATE", date);
                parm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
                parm.addData("STATION_CODE", this.getValueString("STATION_CODE"));
                parm.addData("OUT_USER", optUser);
                parm.addData("OPT_USER", optUser);
                parm.addData("OPT_DATE", date);
                parm.addData("OPT_TERM", Operator.getIP());
                // 更新血库库存
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
                
                //modify by yangjj 20150525 科室由操作员科室，改为成本中心科室
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
        // 主项保存判断
        
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        TParm feeParm = new TParm() ;
        feeParm.setData( "CASE_NO", case_no);
        feeParm.setData( "PAT_NAME", this.getValueString("PAT_NAME")); 
//        this.messageBox("自动计费成功,计费金额为"+result.getDouble("FEE",0)) ;
        onPrint();
        onClear();
        //TParm feeResult = (TParm) openWindow("%ROOT%\\config\\bms\\BMSQueryFee.x",feeParm);
        
    }

    /**
     * 打印出库单
     */
    public void onPrint() {
        if (table_m.getRowCount() <= 0) {
            this.messageBox("没有出库信息");
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
            this.messageBox("没有出库信息");
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
					.getHospitalCHNFullName(Operator.getRegion()) + "血品出库单");
			date.setData("APPLY_NO", "TEXT",
					"取血单号: " + this.getValueString("TAKE_NO"));
			date.setData("PAT_NAME", "TEXT",
					" 受血者: " + this.getValueString("PAT_NAME"));
			date.setData("AGE", "TEXT", "年龄: " + this.getValueString("AGE"));
			date.setData("SEX", "TEXT", "性别: " + this.getValueString("SEX"));
			date.setData("BLD_TYPE", "TEXT",
					" ABO血型: " + this.getValueString("BLOOD_TEXT"));
			String rh_type = "";
			if ((this.getRadioButton("RH_A").isSelected())) {
				rh_type = "阳性";
			} else if ((this.getRadioButton("RH_B").isSelected())) {
				rh_type = "阴性";
			}
			date.setData("RH_TYPE", "TEXT", "RH血型: " + rh_type);
			date.setData("MR_NO", "TEXT",
					" 病案号: " + this.getValueString("MR_NO"));

			date.setData("DEPT_CODE", "TEXT",
					"科别: " + this.getComboBox("DEPT_CODE").getSelectedName());

			date.setData("IPD_NO", "TEXT",
					"住院号: " + this.getValueString("IPD_NO"));
			date.setData(
					"STATION_CODE",
					"TEXT",
					"病区: "
							+ ((TTextFormat) this.getComponent("STATION_CODE"))
									.getText());
			date.setData(
					"BED_NO",
					"TEXT",
					"床位: "
							+ ((TTextFormat) this.getComponent("BED_NO"))
									.getText());

			String outNo = "";// 出库单号
			String reason = "";// 输血原因
			String deptCode = ""; // 用血科室
			String diag = "";// 诊断
			String optDate = "";// 收检日期
			String outDate = "";// 出库日期
			String caseNo = "";
			// 表格数据
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
							inparm.getValue("ORG_BARCODE", 0));// 院外条码

					// modify by lim 2012/05/06 begin
					// parm.addData("BLD_CODE", inparm.getValue("BLDCODE_DESC",
					// 0));//院内条码
					// parm.addData("SUBCAT_CODE",
					// inparm.getValue("SUBCAT_DESC", 0));//规格
					parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));// 院内条码
					parm.addData("SUBCAT_CODE", "");// 规格
					// modify by lim 2012/05/06 end

					parm.addData("BLD_TYPE", parmTable.getValue("BLD_TYPE", j));// 血型
					parm.addData("RH_FLG", parmTable.getValue("RH_FLG", j));// RH血型
					// parm.addData("SHIT_FLG", table_m.getItemString(i,
					// "SHIT_FLG"));//抗体
					String crossL = " ";
					if ("0".equals(parmTable.getValue("CROSS_MATCH_L", j))) {
						crossL = "无凝集无溶血";
					} else if ("1".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "有凝集无溶血";
					} else if ("2".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "无凝集有溶血";
					} else if ("3".equals(parmTable
							.getValue("CROSS_MATCH_L", j))) {
						crossL = "有凝集有溶血";
					}
					parm.addData("CROSS_MATCH_L", crossL);

					String crossS = " ";
					if ("0".equals(parmTable.getValue("CROSS_MATCH_S", j))) {
						crossS = "无凝集无溶血";
					} else if ("1".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "有凝集无溶血";
					} else if ("2".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "无凝集有溶血";
					} else if ("3".equals(parmTable
							.getValue("CROSS_MATCH_S", j))) {
						crossS = "有凝集有溶血";
					}
					parm.addData("CROSS_MATCH_S", crossS);
					// parm.addData("ANTI_A", table_m.getItemString(i,
					// "ANTI_A"));
					// parm.addData("ANTI_B", table_m.getItemString(i,
					// "ANTI_B"));
					parm.addData("RESULT", "1".equals(parmTable.getValue(
							"RESULT", j)) ? "相合" : "相斥");
					// parm.addData("TEST_DATE",
					// table_m.getItemString(i, "TEST_DATE").substring(0, 10).
					// replace('-', '/'));
					parm.addData("TEST_DATE",
							parmTable.getValue("TEST_DATE", j).substring(0, 16)
									.replace('-', '/'));
					check = parmTable.getValue("RECHECK_USER", j);// 复核人
					match = parmTable.getValue("TEST_USER", j);// 配血人
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

			// modified by wangqing 20171225  宝龙要求，不显示配血人、复核人、发血
//			date.setData("OUT_USER", "TEXT", Operator.getName());// 发血人
			String sqlName = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='X'";
			System.out.println("99999"
					+ sqlName.replace("X", parmValue.getValue("RECHECK_USER")));
			String checkName = new TParm(TJDODBTool.getInstance().select(
					sqlName.replace("X", check))).getValue("USER_NAME", 0);// 审核人
			String matchName = new TParm(TJDODBTool.getInstance().select(
					sqlName.replace("X", match))).getValue("USER_NAME", 0);// 配血人
//			date.setData("TEXT_USER", "TEXT", matchName);// 配血人
//			date.setData("RECHECK_USER", "TEXT", checkName);// 复核人
			// wuxy 20170622 end
			date.setData("OUT_NO", "TEXT", outNo);
			date.setData("REASON", "TEXT", "输血原因:" + reason);
			date.setData("YXKS", "TEXT", "用血科室:" + deptDesc);
			// date.setData("YXKS", "TEXT", "用血科室:"+deptCode) ;
			String recpDate = "";
			if (null != optDate && !"".equals(optDate)) {
				recpDate = optDate.substring(0, 16).replace('-', '/');
			}
			date.setData("TEST_DATE", "TEXT", " 收检日期:" + recpDate);
			date.setData("JWSH", "TEXT", "诊断: " + diag);
			if (!"".equals(outDate) && outDate != null) {
				outDate = outDate.substring(0, 19).replace("-", "/");
			}
			date.setData("OUT_DATE", "TEXT", outDate);
			// date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// 调用打印方法
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
//                     "血品出库单");
//        date.setData("APPLY_NO", "TEXT", "取血单号: " + this.getValueString("TAKE_NO"));
//        date.setData("PAT_NAME", "TEXT", " 受血者: " + this.getValueString("PAT_NAME"));
//        date.setData("AGE", "TEXT", "年龄: " + this.getValueString("AGE"));
//        date.setData("SEX", "TEXT", "性别: " + this.getValueString("SEX"));
//        date.setData("BLD_TYPE", "TEXT", " ABO血型: " + this.getValueString("BLOOD_TEXT"));
//        String rh_type = "";
//        if ( (this.getRadioButton("RH_A").isSelected())) {
//            rh_type = "阳性";
//        }
//        else if ( (this.getRadioButton("RH_B").isSelected())) {
//            rh_type = "阴性";
//        }
//        date.setData("RH_TYPE", "TEXT", "RH血型: " + rh_type);
//        date.setData("MR_NO", "TEXT", " 病案号: " + this.getValueString("MR_NO"));
//   
//        date.setData("DEPT_CODE", "TEXT",
//                     "科别: " + this.getComboBox("DEPT_CODE").getSelectedName());
//
//        date.setData("IPD_NO", "TEXT", "住院号: " + this.getValueString("IPD_NO"));
//        date.setData("STATION_CODE", "TEXT",
//                     "病区: " +
//                     ( (TTextFormat)this.getComponent("STATION_CODE")).getText());
//        date.setData("BED_NO", "TEXT",
//                     "床位: " + ( (TTextFormat)this.getComponent("BED_NO")).getText());
//       
//
//        String outNo = "" ;//出库单号
//        String reason = "" ;//输血原因
//        String deptCode = "" ; //用血科室
//        String diag = "" ;//诊断
//        String optDate = "" ;//收检日期
//        String outDate = "" ;//出库日期
//        String caseNo = "" ;
//        // 表格数据
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
//            parm.addData("ORG_BARCODE", inparm.getValue("ORG_BARCODE",0)) ;//院外条码
//            
//            //modify by lim 2012/05/06 begin
//            //parm.addData("BLD_CODE", inparm.getValue("BLDCODE_DESC", 0));//院内条码
//            //parm.addData("SUBCAT_CODE", inparm.getValue("SUBCAT_DESC", 0));//规格
//            parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));//院内条码
//            parm.addData("SUBCAT_CODE", "");//规格
//            //modify by lim 2012/05/06 end
//            
//            parm.addData("BLD_TYPE", table_m.getItemString(i, "BLD_TYPE"));//血型
//            parm.addData("RH_FLG", table_m.getItemString(i, "RH_FLG"));//RH血型
////            parm.addData("SHIT_FLG", table_m.getItemString(i, "SHIT_FLG"));//抗体
//            String crossL = " " ;
//            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "无凝集无溶血" ;
//            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "有凝集无溶血" ;
//            }else if("2".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "无凝集有溶血" ;
//            }else if("3".equals(table_m.getItemString(i, "CROSS_MATCH_L"))){
//                crossL = "有凝集有溶血" ;
//            }
//            parm.addData("CROSS_MATCH_L",crossL);
//            
//            String crossS = " " ;
//            if("0".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "无凝集无溶血" ;
//            }else if("1".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "有凝集无溶血" ;
//            }else if("2".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "无凝集有溶血" ;
//            }else if("3".equals(table_m.getItemString(i, "CROSS_MATCH_S"))){
//                crossS = "有凝集有溶血" ;
//            }            
//            parm.addData("CROSS_MATCH_S",crossS);
////            parm.addData("ANTI_A", table_m.getItemString(i, "ANTI_A"));
////            parm.addData("ANTI_B", table_m.getItemString(i, "ANTI_B"));
//            parm.addData("RESULT",
//                         "1".equals(table_m.getItemString(i, "RESULT")) ? "相合" :
//                         "相斥");
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
//        date.setData("OUT_USER","TEXT",Operator.getName()) ;//发血人
//        String sqlName = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='X'";
//        System.out.println("99999"+sqlName.replace("X", parmValue.getValue("RECHECK_USER")));
//        String checkName = new TParm(TJDODBTool.getInstance().select(sqlName.replace("X", parmValue.getValue("RECHECK_USER")))).getValue("USER_NAME", 0);//审核人
//        String matchName = new TParm(TJDODBTool.getInstance().select(sqlName.replace("X", parmValue.getValue("TEST_USER")))).getValue("USER_NAME", 0);//配血人
//        date.setData("TEXT_USER","TEXT",matchName) ;//配血人
//        date.setData("RECHECK_USER","TEXT",checkName) ;//复核人
//        //wuxy 20170622 end
//        date.setData("OUT_NO", "TEXT",  outNo);
//        date.setData("REASON", "TEXT","输血原因:"+reason) ;
//        date.setData("YXKS", "TEXT", "用血科室:"+deptDesc) ;
////        date.setData("YXKS", "TEXT", "用血科室:"+deptCode) ;
//        String recpDate = "" ;
//        if(null!=optDate && !"".equals(optDate)){
//            recpDate =optDate.substring(0, 16).replace('-', '/') ;
//        }
//        date.setData("TEST_DATE","TEXT"," 收检日期:"+recpDate) ;
//        date.setData("JWSH","TEXT","诊断: "+diag) ;
//        if(!"".equals(outDate)&& outDate!=null){
//            outDate = outDate.substring(0, 19).replace("-", "/") ;
//        }
//        date.setData("OUT_DATE","TEXT",outDate) ;
////        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
//        // 调用打印方法
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
     * 主项表格(TABLE_M)单击事件
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
     * 扫描院内条码号，勾选TABLE_M
     */
    //add by yangjj 20150423
    public void onBloodNo(){
    	TTable table = getTable("TABLE_M");
    	table.acceptText();
    	TParm parm = table.getParmValue();
    	String bloodNo = this.getValueString("BLOOD_NO");
    	if("".equals(bloodNo)){
    		this.messageBox("请扫描院内条码！");
    		setValue("BLOOD_NO", "");
    		return ;
    	}
    	for(int i = 0 ; i <= parm.getCount() ; i++){
    		if(i == parm.getCount()){
    			this.messageBox("院内条码不存在！");
    			setValue("BLOOD_NO", "");
    			return;
    		}
    		
    		//院内码与表格中院内码相同
    		if(bloodNo.equals(parm.getValue("BLOOD_NO", i))){
    			
    			//增加累积量
    			if(!"Y".equals(parm.getValue("SELECT_FLG", i))){
    				table_d.acceptText();
    				TParm dParm = table_d.getParmValue();
    				for(int j = 0 ; j <= dParm.getCount() ; j++){
    					if( j == dParm.getCount()){
    						this.messageBox("该血品不在取血单中,不允许出库！");
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
    			//取血单信息
    			printParm.setData("BLOOD_NO", table_m.getItemString(i, "BLOOD_NO"));
    			printParm.setData("MR_NO", getValueString("MR_NO"));
    			printParm.setData("NAME", getValueString("PAT_NAME"));
    			printParm.setData("DEPT_CODE", this.getComboBox("DEPT_CODE").getSelectedName());
    			printParm.setData("BED_NO", getText("BED_NO"));
    			printParm.setData("SEX", getValueString("SEX"));
    			printParm.setData("PATIENT_BLOOD", getValueString("BLOOD_TEXT"));
    			printParm.setData("AGE",getValueString("AGE"));
    			
    			
    			//血袋信息
    			printParm.setData("BLOOD_CODE", table_m.getItemData(i, "BLD_CODE"));
    			printParm.setData("BLOOD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
    			printParm.setData("RH", "RH:"+table_m.getItemData(i, "RH_FLG"));
    			printParm.setData("CROSS_TIME", table_m.getItemData(i, "TEST_DATE"));
    			printParm.setData("TEST_USER", table_m.getItemData(i, "TEST_USER"));
    			printParm.setData("RECHECK_USER", table_m.getItemData(i, "RECHECK_USER"));
    			printParm.setData("OUT_USER", Operator.getName());
    			printParm.setData("SUBCAT_CODE",table_m.getItemData(i, "SUBCAT_CODE"));
    			String r = "1".equals(table_m.getItemString(i, "RESULT"))?"相合":"相斥";
    			printParm.setData("RESULT",r);
    			printParm.setData("OrgBarCode","院外码:"+table_m.getItemString(i, "ORG_BARCODE"));
    			
    			
    			printBarCode(printParm);
    			setValue("BLOOD_NO", "");
    			break;
    		}
    	}
    }

    
    

    /**
     * 取血单号查询(回车事件)
     */
    public void onTakeNo() {
        String takeNo = this.getValueString("TAKE_NO");
        if (!"".equals(takeNo)) {
            TParm parm = new TParm();
            parm.setData("TAKE_NO", takeNo);
            TParm result = new TParm(TJDODBTool.getInstance().select(getTakeSql(takeNo)));
            if (result.getCount("TAKE_NO") == 0 || result.getCount() <= 0) {
                this.messageBox("取血单不存在");
                this.setValue("TAKE_NO", "");
                return;
            }
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));

            // 查询病患信息
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
			//=========pangben 2016-10-01 修改重新查询临床路径代码
			TParm schdCodeParm = new TParm(TJDODBTool.getInstance().select(schdCodeInit));
			String schdCode = schdCodeParm.getValue("SCHD_CODE", 0);// 时程代码
			String clncpathCode = schdCodeParm.getValue("CLNCPATH_CODE", 0);// 临床路径代码
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
     * 病案号查询(回车事件)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm resultParm = BMSApplyMTool.getInstance().onApplyQuery(parm);
        if (resultParm.getCount("APPLY_NO") == 0 || resultParm.getCount() <= 0) {
            this.messageBox("不存在该病患的备血单");
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
     * 全选复选框选中事件
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
     * 数据检核
     * @return boolean
     */
    private boolean CheckData() {
        // 血液信息检核
        if (table_m.getRowCount() == 0) {
            this.messageBox("没有出库信息");
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
            // 血液与病患的申请血液不合
            boolean check_flg = true;
            for (int j = 0; j < table_d.getRowCount(); j++) {
                if (table_d.getItemString(j, "BLD_CODE").equals(table_m.
                    getItemString(i, "BLD_CODE"))) {
                    check_flg = false;
                }
            }
            if (check_flg) {
                if (this.messageBox("提示",
                                    "血袋号码 " +
                                    table_m.getItemString(i, "BLOOD_NO") +
                                    " 的血液与病患的申请血液不合,是否出库？", 2) != 0) {
                    return false;
                }
            }

            // 血型比较
            if (!blood_type.equals(table_m.getItemString(i, "BLD_TYPE"))) {
            	if (this.messageBox("提示",
                        "血袋号码 " +
                        table_m.getItemString(i, "BLOOD_NO") +
                        " 的血型与病患的血型不合,请重新选择", 2) != 0) {
            		return false;
            	}
            }

            //modify by lim 2012/04/26 begin
            // 检验结果
//            if ("2".equals(table_m.getItemString(i, "RESULT"))) {
//                this.messageBox("血袋号码 " + table_m.getItemString(i, "BLOOD_NO") +
//                                " 的检验结果判读为'相斥',不能出库");
//                return false;
//            }
            
            //RH血型     chenxi modify 20130407   
            if(!rh_type.equals(table_m.getItemString(i, "RH_FLG"))){
                 int check =    messageBox("消息","血袋号码 " + table_m.getItemString(i, "BLOOD_NO") +
                            " 的RH血型与病患RH血型不符,不能出库。是否继续?", 0) ;  
                 if(check==0){   //密码校验      chenxi 
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
            this.messageBox("请选择出库血液");
            return false;
        }

        // 血液出库量
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
        // 血液申请量
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

//        // 血液出库量和申请量比较
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
//                this.messageBox(bld_code + " 的出库量已大于申请量,请重新选择!");
//                return false;
//            }
//        }

        // 判断库存量
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
                this.messageBox("血品" + table_m.getItemData(i, "BLD_CODE") + "库存不足");
                return false;
            }
        }

        // 病患信息检核
        if ("I".equals(adm_type)) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", case_no);
            TParm result = ADMTool.getInstance().getADM_INFO(parm);
            // 判断该病人是否出院
            if (!"".equals(result.getValue("DS_DATE", 0))) {
                this.messageBox("此病人已出院");
                return false;
            }
        }
        //================pangben 2016-09-30 添加临床路径校验时程
        if(null!=this.getValue("CLNCPATH_CODE")&&this.getValue("CLNCPATH_CODE").toString().length()>0){
        	if(null==this.getValue("SCHD_CODE")||this.getValue("SCHD_CODE").toString().length()<=0){
        		this.messageBox("请选择临路径时程");
        		return false;
        	}
        }
        return true;
    }
    
    /*
     * 院内码标签补打事件
     * */
    public void onPrintBarCode(){
    	int i = table_m.getSelectedRow();
    	if(i < 0 ){
    		this.messageBox("请选择需要补打的标签！");
    		return;
    	}
    	
    	TParm printParm = new TParm();
    	table_m.acceptText();
		//取血单信息
		printParm.setData("BLOOD_NO", table_m.getItemString(i, "BLOOD_NO"));
		printParm.setData("MR_NO", getValueString("MR_NO"));
		printParm.setData("NAME", getValueString("PAT_NAME"));
		printParm.setData("DEPT_CODE", this.getComboBox("DEPT_CODE").getSelectedName());
		printParm.setData("BED_NO", getText("BED_NO"));
		printParm.setData("SEX", getValueString("SEX"));
		printParm.setData("PATIENT_BLOOD", getValueString("BLOOD_TEXT"));
		printParm.setData("AGE",getValueString("AGE"));
		
		
		//血袋信息
		printParm.setData("BLOOD_CODE", table_m.getItemData(i, "BLD_CODE"));
		printParm.setData("BLOOD_TYPE", table_m.getItemData(i, "BLD_TYPE"));
		printParm.setData("RH", "RH:"+table_m.getItemData(i, "RH_FLG"));
		printParm.setData("CROSS_TIME", table_m.getItemData(i, "TEST_DATE"));
		printParm.setData("TEST_USER", table_m.getItemData(i, "TEST_USER"));
		printParm.setData("RECHECK_USER", table_m.getItemData(i, "RECHECK_USER"));
		printParm.setData("OUT_USER", Operator.getName());
		printParm.setData("SUBCAT_CODE",table_m.getItemData(i, "SUBCAT_CODE"));
		String r = "1".equals(table_m.getItemString(i, "RESULT"))?"相合":"相斥";
		printParm.setData("RESULT",r);
		printParm.setData("OrgBarCode","院外码:"+table_m.getItemString(i, "ORG_BARCODE"));
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
    	String crossTime = "配血日期:"+p.getValue("CROSS_TIME").replace(".0", "").replace("-", "/");
    	result = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME AS NAME FROM SYS_OPERATOR WHERE USER_ID = '"+p.getValue("TEST_USER")+"'"));
    	String testUser = "配血人:"+result.getValue("NAME", 0);
    	result = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME AS NAME FROM SYS_OPERATOR WHERE USER_ID = '"+p.getValue("RECHECK_USER")+"'"));
    	String recheckUser = "审核人:"+result.getValue("NAME", 0);
    	String outUser = "发血人:"+p.getValue("OUT_USER");
//    	String recheckUser = "审核人:"+p.getValue("OUT_USER");
//    	String testUser = "配血人:"+p.getValue("OUT_USER");
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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
    * 得到ComboBox对象
    *
    * @param tagName
    *            元素TAG名称
    * @return
    */
   private TComboBox getComboBox(String tagName) {
       return (TComboBox) getComponent(tagName);
   }

   //=========================  chenxi modify 201305
   /**
     * 调用密码验证
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
