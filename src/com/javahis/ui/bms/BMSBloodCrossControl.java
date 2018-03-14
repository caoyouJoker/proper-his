package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Pat;
import jdo.sys.SystemTool; 
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.dongyang.ui.TRadioButton;
import jdo.bms.BMSApplyDTool;
import jdo.bms.BMSBloodTool;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.PatTool;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;
import jdo.bms.BMSSQL;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title: 交叉配学
 * </p>
 *
 * <p>
 * Description: 交叉配学
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
public class BMSBloodCrossControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    private String case_no;

    private String adm_type;

    private String bld_type;

    public BMSBloodCrossControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();

        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            this.setValue("APPLY_NO", parm.getValue("APPLY_NO"));
            this.onApplyNoAction();
        }
        
      //add by yangjj 20150413增加交叉配血审核界面
        String s = "";
        s = this.getParameter()+"";
        if("CHECK".equals(s)){
        	//删除
        	callFunction("UI|delete|setEnabled", false);
        	//TMenuItem deleteBtn = (TMenuItem) this.getComponent("delete");
        	//deleteBtn.setEnabled(false);
        	
        	//库存查询
        	callFunction("UI|stock|setEnabled", false);
        	//TMenuItem stockBtn = (TMenuItem) this.getComponent("stock");
        	//stockBtn.setEnabled(false);
        	
        	//血液计价
        	callFunction("UI|pay|setEnabled", false);
        	//TMenuItem payBtn = (TMenuItem) this.getComponent("pay");
        	//payBtn.setEnabled(false);
        	
        	//血液出库
        	callFunction("UI|dispense|setEnabled", false);
        	//TMenuItem dispenseBtn = (TMenuItem) this.getComponent("dispense");
        	//dispenseBtn.setEnabled(false);
        	 
        	
        	//检验报告
        	callFunction("UI|lis|setEnabled", false);
        	//TMenuItem lisBtn = (TMenuItem) this.getComponent("lis");
        	//lisBtn.setEnabled(false);
        	
        	//检查报告
        	callFunction("UI|ris|setEnabled", false);
        	//TMenuItem risBtn = (TMenuItem) this.getComponent("ris");
        	//risBtn.setEnabled(false);
        	 
        	//糖尿病
        	callFunction("UI|tnb|setEnabled", false);
        	//TMenuItem tnbBtn = (TMenuItem) this.getComponent("tnb");
        	//tnbBtn.setEnabled(false);
        	
        	callFunction("UI|TEST_BLD_A|setEnabled", false);
        	callFunction("UI|TEST_BLD_B|setEnabled", false);
        	callFunction("UI|RH_TYPE_A|setEnabled", false);
        	callFunction("UI|RH_TYPE_B|setEnabled", false);
        	callFunction("UI|ANTI_A|setEnabled", false);
        	callFunction("UI|ANTI_B|setEnabled", false);
        	callFunction("UI|CROSS_MATCH_L|setEnabled", false);
        	callFunction("UI|CROSS_MATCH_S|setEnabled", false);
        	callFunction("UI|SHIT_FLG|setEnabled", false);
        	callFunction("UI|RESULT|setEnabled", false);
        	callFunction("UI|TEST_DATE|setEnabled", false);
        	callFunction("UI|ADD_BUTTON|setEnabled", false);
        	callFunction("UI|UI|setTitle", "配血审核");
        	//审核时标题栏变更
        	((TTable)getComponent("TABLE_M")).setHeader("院内条形码,90;血液,110,BLD_CODE;血液规格,100,SUBCAT_CODE;血型,40,BLD_TYPE;RH血型,60;抗体,40;主侧,95,CROSS_MATCH_L;次侧,95,CROSS_MATCH_S;Anti-A,60;Anti-B,60;配血结果,80,RESULT;配血日期,100;配血人员,90,USER;审核日期,100;审核人员,90,USER");
        	((TTable)getComponent("TABLE_M")).setParmMap("BLOOD_NO;BLD_CODE;SUBCAT_CODE;BLD_TYPE;RH_FLG;SHIT_FLG;CROSS_MATCH_L;CROSS_MATCH_S;ANTI_A;ANTI_B;RESULT;TEST_DATE;TEST_USER;RECHECK_TIME;RECHECK_USER");
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        
        Timestamp date = SystemTool.getInstance().getDate();
        //add by yangjj 20150423
        String s = "";
        s = this.getParameter()+"";
        if("CHECK".equals(s)){
        	//审核人员身份确认
        	TParm checkUserParm = checkPW1();
        	if(!"OK".equals(checkUserParm.getData("RESULT")) ){
        		return;
        	}
        	
        	String userCode = checkUserParm.getData("USER_ID")+"";
        	String apply_no = this.getValueString("APPLY_NO");
        	
        	TParm checkParm = new TParm();
        	String d = date.toString();
        	d = d.substring(0, d.indexOf(".")).replace("-", "").replace(":", "").trim();
//        	checkParm.setData("RECHECK_TIME", d);
//        	checkParm.setData("RECHECK_USER", Operator.getID());
//        	checkParm.setData("APPLY_NO", apply_no);
//        	this.messageBox(table_m.getParmValue()+"");
        	TParm parm = table_m.getParmValue();
        	int row = parm.getCount();
        	checkParm.setData("USER_ID", Operator.getID());
    		checkParm.setData("TIME", d);
        	for(int i = 0 ;i<row;i++){
        		
        		checkParm.addData("APPLY_NO", apply_no);
        		checkParm.addData("BLOOD_NO", parm.getValue("BLOOD_NO", i));
        		if(StringUtil.isNullString(parm.getValue("RECHECK_USER", i))){
        			checkParm.addData("RECHECK_USER", Operator.getID());
        		}else{
        			checkParm.addData("RECHECK_USER", parm.getValue("RECHECK_USER", i));
        		}
        		if(StringUtil.isNullString(parm.getValue("RECHECK_TIME", i))){
        			checkParm.addData("RECHECK_TIME", d);
        		}else{
        			
        			String str = parm.getValue("RECHECK_TIME", i).substring(0, parm.getValue("RECHECK_TIME", i).length()-2).
        					replace("-", "").replace(":", "").trim();
        			
        			checkParm.addData("RECHECK_TIME", str);
        		}
        	}
        	//this.messageBox(checkParm+"");
        	TParm result = TIOM_AppServer.executeAction(
                    "action.bms.BMSBloodAction", "onUpdateBloodCrossRecheck", checkParm);
        	
        	if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            onClear();
        	return ;
        }
        
        
        TParm parm = new TParm();
        
        for (int i = 0; i < table_m.getRowCount(); i++) {
            parm.addData("BLOOD_NO", table_m.getItemData(i, "BLOOD_NO"));
            parm.addData("STATE_CODE", "1");
            parm.addData("APPLY_NO", this.getValue("APPLY_NO"));
            parm.addData("MR_NO", this.getValue("MR_NO"));
            parm.addData("IPD_NO", this.getValue("IPD_NO"));
            parm.addData("CASE_NO", case_no);
            parm.addData("ID_NO", this.getValue("ID_NO"));
            parm.addData("CROSS_MATCH_L",
                         table_m.getItemString(i, "CROSS_MATCH_L"));            
            parm.addData("CROSS_MATCH_S",
                         table_m.getItemString(i, "CROSS_MATCH_S"));
            
            //add by yangjj 20150420
            parm.addData("SHIT_FLG", table_m.getItemString(i, "SHIT_FLG"));
            
            //parm.addData("ANTI_A", table_m.getItemData(i, "ANTI_A"));
            
            parm.addData("ANTI_A", table_m.getItemString(i, "ANTI_A"));
            
            //System.out.println("==ANTI_A=="+table_m.getItemData(i, "ANTI_A"));
            
            //System.out.println("==ANTI_B=="+table_m.getItemData(i, "ANTI_B"));

            //parm.addData("ANTI_B", table_m.getItemData(i, "ANTI_B"));
            parm.addData("ANTI_B", table_m.getItemString(i, "ANTI_B"));

            parm.addData("RESULT", table_m.getItemData(i, "RESULT"));
            
            parm.addData("RH_FLG", table_m.getItemData(i, "RH_FLG"));//==========pangben modify 20110628
            
            //System.out.println("==RH_FLG=="+table_m.getItemData(i, "RH_FLG"));      
            //start  machao   20170713  检验人员与时间修改
            parm.addData("TEST_DATE", table_m.getItemTimestamp(i, "TEST_DATE"));
            parm.addData("TEST_USER", table_m.getItemString(i, "TEST_USER"));
            //end machao
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_DATE", date);
            parm.addData("OPT_TERM", Operator.getIP());
        }
        
        // add by wangb 2016/2/19 首次保存时，自动弹出计费窗口提醒计费 START
        int count = table_m.getRowCount();
        boolean flag = false;
        String bloodNo = "";
        
        for (int i = 0; i < count; i++) {
        	bloodNo = bloodNo + "'" + table_m.getItemString(i, "BLOOD_NO") + "'";
        	if (i < count - 1) {
        		bloodNo = bloodNo + ",";
        	}
        }
        
        TParm queryParm = new TParm();
        queryParm.setData("BLOOD_NO", bloodNo);
        
        // 查询交叉配血
        TParm resultM = BMSBloodTool.getInstance().queryBloodCrossData(queryParm);
        
        if (resultM.getErrCode() > -1) {
        	for (int i = 0; i < count; i++) {
            	if (StringUtils.isEmpty(resultM.getValue("CASE_NO", i))) {
            		flag = true;
            		break;
            	}
            }
        }
        // add by wangb 2016/2/19 首次保存时，自动弹出计费窗口提醒计费 END
        
        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdateBloodCross", parm);
        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        
        // add by wangb 2016/2/19 首次保存时，自动弹出计费窗口提醒计费 START
        if (flag) {
        	this.onPay(); 
        }
        // add by wangb 2016/2/19 首次保存时，自动弹出计费窗口提醒计费 END
        
        onClear();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有选中项");
            return;
        }
        /*
        if ("".equals(table_m.getItemString(row, "ANTI_A"))) {
            // 未保存项
            table_m.removeRow(row);
        }
        */
        
        //add by yangjj 20150515增加是否出库判断，出库则不允许解除绑定
        /*String bloodNo = table_m.getItemData(row, "BLOOD_NO")+"";
        TParm r = new TParm(TJDODBTool.getInstance().select("SELECT OUT_NO FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodNo+"'"));
        if(r.getErrCode() < 0){
        	this.messageBox("获取血袋出库信息失败！");
        	return;
        }
        if(!"".equals(r.getValue("OUT_NO", 0))){
        	this.messageBox("血袋已出库不允许解除绑定！");
        	return ;
        }else */
        
        {
        	TParm tableMparm = table_m.getShowParmValue() ;
        	String rhFlg = tableMparm.getValue("RH_FLG", row) ;
            // 已保存项
            if (this.messageBox("删除", "确定是否删除", 2) == 0) {
                TParm parm = new TParm();
                Timestamp date = SystemTool.getInstance().getDate();
                TNull tnull = new TNull(Timestamp.class);
                parm.addData("BLOOD_NO", table_m.getItemData(row, "BLOOD_NO"));
                parm.addData("STATE_CODE", "0");
                parm.addData("APPLY_NO", "");
                parm.addData("MR_NO", "");
                parm.addData("IPD_NO", "");
                parm.addData("CASE_NO", "");
                parm.addData("ID_NO", "");
                parm.addData("CROSS_MATCH_L", "");
                parm.addData("CROSS_MATCH_S", "");
                parm.addData("ANTI_A", "");
                parm.addData("ANTI_B", ""); 
                parm.addData("RESULT", "");
                
                //add by yangjj 20150514
                parm.addData("SHIT_FLG","");
                
                parm.addData("TEST_DATE", tnull); 
                parm.addData("TEST_USER", "");
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", date);
                parm.addData("OPT_TERM", Operator.getIP());
                parm.addData("RH_FLG", rhFlg) ; 
                // modified by wangqing 20180108 start 
                // 交叉配血界面，删除交叉配信息时，同步清空配血审核信息
//                TParm result = TIOM_AppServer.executeAction(
//                    "action.bms.BMSBloodAction", "onUpdateBloodCross", parm);
                TParm result = TIOM_AppServer.executeAction(
                        "action.bms.BMSBloodAction", "onUpdateBmsBlood", parm);
                // modified by wangqing 20180108 end
                // 主项保存判断
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                this.messageBox("删除成功");
                table_m.removeRow(row);
            }
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;BLD_TYPE;BLD_CODE;"
            + "SUBCAT_CODE;BLOOD_NO;SHIT_FLG;CROSS_MATCH_S;CROSS_MATCH_L;"
            + "ANTI_A;ANTI_B;RESULT;TEST_DATE";
        this.clearValue(clearStr);
        this.getRadioButton("RH_A").setSelected(true);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
        table_m.removeRowAll();
        table_d.removeRowAll();
        ( (TMenuItem) getComponent("stock")).setEnabled(false);
        case_no = "";
        adm_type = "";
        bld_type = "";
    }
    //===================  chenxi modify 20121009
    /**
     * 糖尿病报告
     */
    public void onTnb(){
    	 if ("".equals(this.getValueString("MR_NO"))) {
             this.messageBox("病案号不能为空");
             return ;
         }
    	 String  mrNo = this.getValueString("MR_NO") ;
  	   SystemTool.getInstance().OpenTnbWeb(mrNo);
    }
    /**
     * RIS报告
     */
    public void onRis(){
    	if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("病案号不能为空");
            return ;
        }
   	 String  mrNo = this.getValueString("MR_NO") ;
 	   SystemTool.getInstance().OpenRisWeb(mrNo) ;
    }
    /**
     * lis报告
     * 
     */
    public void onLis(){
    	if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("病案号不能为空");
            return ;
        }
   	 String  mrNo = this.getValueString("MR_NO") ;
 	   SystemTool.getInstance().OpenLisWeb(mrNo);
    }
    //================    chenxi modify  20121009
    /**
     * 库存查询
     */
    public void onStock() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }

        parm.setData("BLD_TYPE", bld_type);

        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        
        //modify by lim 2012/05/03 begin
        Object result = openDialog("%ROOT%\\config\\bms\\BMSStockQuery.x", parm); 
        if (result != null) {
            List<TParm> addParm = (List<TParm>) result;
            if (addParm == null) {
                return;
            }        

	          TTable table1 = getTable("TABLE_M");
	          // 判断是否有重复数据 begin
	          Set<String> set = new HashSet<String>() ;
	          for (int i = 0; i < table1.getRowCount(); i++) {
	        	  set.add(table1.getItemString(i,"BLOOD_NO")) ;
	          }	          
	          for (Iterator iterator = addParm.iterator(); iterator.hasNext();) {
				TParm tParm = (TParm) iterator.next();
				if(set.contains(tParm.getValue("BLOOD_NO"))){
	                  this.messageBox("血品"+tParm.getValue("BLOOD_NO")+"已存在，不可重复配血");
	                  return;					
				}else{
					set.add(tParm.getValue("BLOOD_NO")) ;
				}
			  }  
	          // 判断是否有重复数据 end
            
            for (Iterator iterator = addParm.iterator(); iterator.hasNext();) {
				TParm tParm = (TParm) iterator.next();
	            TTable table = getTable("TABLE_M");
	            int row = table.addRow();
	            table.setItem(row, "BLOOD_NO", tParm.getValue("BLOOD_NO"));
	            table.setItem(row, "BLD_CODE", tParm.getValue("BLD_CODE"));
	            table.setItem(row, "SUBCAT_CODE", tParm.getValue("SUBCAT_CODE"));
	            table.setItem(row, "BLD_TYPE", tParm.getValue("BLD_TYPE"));
	            table.setItem(row, "RH_FLG", tParm.getValue("RH_FLG"));
	            table.setItem(row, "SHIT_FLG", tParm.getValue("SHIT_FLG"));   				
			}
        }
        //modify by lim 2012/05/03 end

//        Object result = openDialog("%ROOT%\\config\\bms\\BMSStockQuery.x", parm); 
//        if (result != null) {
//            TParm addParm = (TParm) result;
//            if (addParm == null) {
//                return;
//            }
//
//            TTable table = getTable("TABLE_M");
//            // 判断是否有重复数据
//            for (int i = 0; i < table.getRowCount(); i++) {
//                if (addParm.getValue("BLOOD_NO").equals(table.getItemString(i,
//                    "BLOOD_NO"))) {
//                    this.messageBox("血品已存在，不可重复配血");
//                    return;
//                }
//            }
//            //this.messageBox_(addParm);
//
//            // 判断（配血量 + 出库量）是否大于申请量
////            TParm resultParm = new TParm();
////            parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
////            parm.setData("BLD_CODE", addParm.getValue("BLD_CODE"));
////            // 申请量  shibl 20120410 modify
////            resultParm = BMSApplyDTool.getInstance().onApplyQuery(parm);
////            if (resultParm == null || resultParm.getCount() <= 0) {
////                this.messageBox("病患未申请该类型血液");
////                return;
////            }
//            // 配血量 + 出库量
////            parm.setData("SUBCAT_CODE", addParm.getValue("SUBCAT_CODE"));
////            parm.setData("BLD_TYPE", addParm.getValue("BLD_TYPE"));
////            resultParm = BMSBloodTool.getInstance().getSumTot(parm);
//            // TABLE中已添加的配血量
////            double sum_qty = 0;
////            for (int i = 0; i < table_m.getRowCount(); i++) {
////                if (table_m.getItemString(i,
////                                          "BLD_CODE").equals(addParm.getValue(
////                                              "BLD_CODE"))) {
////                    TParm parmVol = new TParm(TJDODBTool.getInstance().select(
////                        BMSSQL.getBMSBldVol(table_m.getItemString(i, "BLD_CODE"),
////                                            table_m.getItemString(i,
////                        "SUBCAT_CODE"))));
////                    sum_qty += parmVol.getDouble("BLD_VOL", 0);
////                }
////            }
////            if (resultParm.getDouble("SUM_TOT", 0) + sum_qty >
////                resultParm.getDouble("APPLY_QTY", 0)) {
////                this.messageBox("配血量大于申请量");
////                return;
////            }
//            int row = table.addRow();
//            table.setItem(row, "BLOOD_NO", addParm.getValue("BLOOD_NO"));
//            table.setItem(row, "BLD_CODE", addParm.getValue("BLD_CODE"));
//            table.setItem(row, "SUBCAT_CODE", addParm.getValue("SUBCAT_CODE"));
//            table.setItem(row, "BLD_TYPE", addParm.getValue("BLD_TYPE"));
//            table.setItem(row, "RH_FLG", addParm.getValue("RH_FLG"));
//            table.setItem(row, "SHIT_FLG", addParm.getValue("SHIT_FLG"));
//        }
    }

    /**
     * 补充计价
     */
    public void onPay() {
        TParm parm = new TParm();
        if ("I".equals(adm_type)) {
            // 呼叫住院补充计价
            parm.setData("IBS", "CASE_NO", case_no);
            TParm adm_info = new TParm();
            adm_info.setData("CASE_NO", case_no);
            TParm result = ADMTool.getInstance().getADM_INFO(adm_info);
            parm.setData("IBS", "VS_DR_CODE",result.getValue("VS_DR_CODE", 0));
            parm.setData("IBS", "IPD_NO", this.getValueString("IPD_NO"));
            parm.setData("IBS", "MR_NO", this.getValueString("MR_NO"));
            parm.setData("IBS", "PAT_NAME", this.getValueString("PAT_NAME"));

            parm.setData("IBS", "YELLOW_SIGN", result.getData("YELLOW_SIGN",0));
            parm.setData("IBS", "RED_SIGN", result.getData("RED_SIGN",0));
            parm.setData("IBS", "STATION_CODE", result.getData("STATION_CODE",0));
            parm.setData("IBS", "GREENPATH_VALUE", result.getData("GREENPATH_VALUE",0));
            parm.setData("IBS", "CTZ2_CODE", result.getData("CTZ2_CODE",0));
            parm.setData("IBS", "TOTAL_AMT", result.getData("TOTAL_AMT",0));
            parm.setData("IBS", "DEPT_CODE", result.getData("DEPT_CODE",0));
            parm.setData("IBS", "BED_NO", result.getData("BED_NO",0));
            parm.setData("IBS", "CTZ3_CODE", result.getData("CTZ3_CODE",0));
            ///////////////////////
            parm.setData("IBS", "ORG_CODE", "");
            parm.setData("IBS", "CTZ1_CODE", result.getData("CTZ1_CODE", 0));
            parm.setData("IBS", "SAVE_FLG", true);
            parm.setData("IBS", "STOP_BILL_FLG",
                         result.getData("STOP_BILL_FLG", 0));
            parm.setData("IBS", "TYPE", "BMS");

            parm.setData("IBS", "CLNCPATH_CODE", "");
            //System.out.println("parm==="+parm);
            openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", parm);
        }
        else {
            // 呼叫门诊补充计价
            if ("".equals(case_no)) {
                this.messageBox("请选择病患");
            }
            parm.setData("MR_NO", this.getValue("MR_NO"));
            parm.setData("CASE_NO", case_no);
            parm.setData("SYSTEM","ONW");
            parm.setData("IBS", "CLNCPATH_CODE", "");
            
            openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);
        }
    }

    /**
     * 血液出库
     */
    public void onDispense() {
        // 呼叫血液出库
        onApplyNoAction();
        String apply_no = this.getValueString("APPLY_NO");
        TParm parm = new TParm();
        parm.setData("APPLY_NO", apply_no);
//        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x",
//                                          parm);
        openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x", parm);
    }

    /**
     * 单笔保存事件
     */
    public void onAddAction() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有选中项");
            return;
        }
        if (!checkAddData()) {
            return;
        }
        table_m.setItem(row, "CROSS_MATCH_S", this.getValue("CROSS_MATCH_S"));
        table_m.setItem(row, "CROSS_MATCH_L", this.getValue("CROSS_MATCH_L"));
        
        //add by yangjj 20150424
        table_m.setItem(row, "SHIT_FLG", this.getValueString("SHIT_FLG"));
        
        table_m.setItem(row, "ANTI_A", this.getValue("ANTI_A"));
        table_m.setItem(row, "ANTI_B", this.getValue("ANTI_B"));
        table_m.setItem(row, "RESULT", this.getValue("RESULT"));
        table_m.setItem(row, "TEST_DATE", this.getValue("TEST_DATE"));
        table_m.setItem(row, "TEST_USER", Operator.getID());
        //===========pangben modify 20110628 start RH血型保存
        if (this.getRadioButton("RH_TYPE_A").isSelected())
            table_m.setItem(row, "RH_FLG", "+");
        else
            table_m.setItem(row, "RH_FLG", "-");
         //===========pangben modify 20110628 stop
        clearAddData();
    }

    /**
     * 单击保存数据检核
     * @return boolean
     */
    private boolean checkAddData() {
    	TTable tableM = (TTable)this.getComponent("TABLE_M") ;
    	int selectedRow = tableM.getSelectedRow() ;
    	String bldCode = table_m.getItemString(selectedRow,"BLD_CODE") ;
    	String sql = "SELECT FRONTPG_TYPE FROM BMS_BLDCODE WHERE BLD_CODE='"+bldCode+"'" ;
    	TParm result2 = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	String frontPgCode = "" ;
    	if(result2.getCount()>0){
    		frontPgCode =result2.getValue("FRONTPG_TYPE", 0) ; //FRONTPG_TYPE为03的情况，为血浆，血浆不做交叉配血.
    	}
    	//modify by lim 2012/05/03 begin
        String rh_type = "";
        if ( (this.getRadioButton("RH_A").isSelected())) {
            rh_type = "+";
        }
        else if ( (this.getRadioButton("RH_B").isSelected())) {
            rh_type = "-";
        }  
        //RH血型
        String rhFlg = table_m.getItemString(selectedRow,"RH_FLG") ;
    	//modify by lim 2012/05/03 end
    	
    	if((!"03".equals(frontPgCode)) && (!"02".equals(frontPgCode)) && (!"05".equals(frontPgCode))){//非血浆的状态
        	
            if ("".equals(this.getValueString("CROSS_MATCH_S"))) {
                this.messageBox("小交叉不能为空");
                return false;
            }
            if ("".equals(this.getValueString("CROSS_MATCH_L"))) {
                this.messageBox("大交叉不能为空");
                return false;
            }
            if ("".equals(this.getValueString("ANTI_A"))) {
                this.messageBox("Anti-A不能为空");
                return false;
            }
            if ("".equals(this.getValueString("ANTI_B"))) {
                this.messageBox("Anti-B不能为空");
                return false;
            }
            if ("".equals(this.getValueString("RESULT"))) {
                this.messageBox("配血结果不能为空");
                return false;
            }
            if ("".equals(this.getValueString("TEST_DATE"))) {
                this.messageBox("检验日期不能为空");
                return false;
            }
            String s_BldType = this.getValueString("BLD_TYPE");
            String s_Anti_A = this.getValueString("ANTI_A");
            String s_Anti_B = this.getValueString("ANTI_B");
            if (s_BldType.equals("AB") &&
                (s_Anti_A.equals("-") || s_Anti_B.equals("-"))) {
                this.messageBox("血型与Anti-A或Anti-B检验结果不符！");
                return false;
            }
            if (s_BldType.equals("A") &&
                (s_Anti_A.equals("-") || s_Anti_B.equals("+"))) {
                this.messageBox("血型与Anti-A或Anti-B检验结果不符！");
                return false;
            }
            if (s_BldType.equals("B") &&
                (s_Anti_A.equals("+") || s_Anti_B.equals("-"))) {
                this.messageBox("血型与Anti-A或Anti-B检验结果不符！");
                return false;
            }
            if (s_BldType.equals("O") &&
                (s_Anti_A.equals("+") || s_Anti_B.equals("+"))) {
                this.messageBox("血型与Anti-A或Anti-B检验结果不符！");
                return false;
            } 		
    	}else{//血浆的情况.
            if ("".equals(this.getValueString("RESULT"))) {
                this.messageBox("配血结果不能为空");
                return false;
            }

    	}
    	  if(!rh_type.equals(rhFlg)){
         int check = 	messageBox("消息","该血袋:血袋号码 " + table_m.getItemString(selectedRow, "BLOOD_NO") +
                          " 的RH血型与病患RH血型不符,不能进行交叉配血,是否继续？",0) ;
         if(check==0){  //密码校验   chenxi  modify  20130503
        	 if (!checkPW()) {
 				return false;
 			}
         }
           if(check!=0){
           this.clearValue("BLOOD_NO;BLD_CODE;SUBCAT_CODE") ;
           tableM.clearSelection() ;
          	return false ;
          	}
          }    
    	return true ;
    }

    /**
     * 清空配血数据区
     */
    private void clearAddData() {
        String clearStr = "RESULT;BLD_TYPE;BLD_CODE;SUBCAT_CODE;BLOOD_NO;"
            + "SHIT_FLG;CROSS_MATCH_S;CROSS_MATCH_L;ANTI_A;ANTI_B;TEST_DATE";
        this.clearValue(clearStr);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
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
            //this.messageBox_(table.getItemData(row, "SHIT_FLG"));
            setValue("SHIT_FLG", table.getItemData(row, "SHIT_FLG"));
            setValue("CROSS_MATCH_L", table.getItemData(row, "CROSS_MATCH_L"));
            setValue("CROSS_MATCH_S", table.getItemData(row, "CROSS_MATCH_S"));
            setValue("ANTI_A", table.getItemData(row, "ANTI_A"));
            setValue("ANTI_B", table.getItemData(row, "ANTI_B"));
            setValue("RESULT", table.getItemData(row, "RESULT"));
            if (!"".equals(table.getItemString(row, "TEST_DATE"))) {
                setValue("TEST_DATE", table.getItemData(row, "TEST_DATE"));
            }
        }
    }

    /**
     * 备血单号查询(回车事件)
     */
    public void onApplyNoAction() {
        String apply_no = this.getValueString("APPLY_NO");
        if (!"".equals(apply_no)) {
            TParm parm = new TParm();
            parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            //System.out.println("result==="+result);
            if (result.getCount("APPLY_NO") <= 0) {
                this.messageBox("备血单不存在");
                this.setValue("APPLY_NO", "");
                return;
            }
            
            // add by wangqing 20171212 start
            // 如果备血单号没有医师审核，也不是交叉配血，则不可以执行执行交叉配血操作
            String checkFlg = result.getValue("CHECK_FLG", 0);
            String urgFlg = result.getValue("URG_FLG", 0);
            if((checkFlg!=null && checkFlg.equals("Y")) || (urgFlg!=null && urgFlg.equals("Y"))){
            	
            }else{
            	this.messageBox("此备血单号没有审核，也不是紧急用血，所以不可以执行交叉配血操作");
            	return;
            }
            // add by wangqing 20171212 end
            
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            // 查询病患信息
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            String rh_type = pat.getBloodRHType();
            //System.out.println("rh_type===="+rh_type);
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
            this.setValue("BLD_TYPE", pat.getBloodType());

            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            case_no = result.getValue("CASE_NO", 0);
            adm_type = result.getValue("ADM_TYPE", 0);
            bld_type = pat.getBloodType();

            result = BMSApplyDTool.getInstance().onApplyQuery(parm);
            if (result != null && result.getCount() > 0) {
                table_d.setParmValue(result);
            }
            parm.setData("STATE_CODE", "1");
            //modify by lim 2012/04/27 begin
            //result = BMSBloodTool.getInstance().onQueryBloodCross(parm);
            result = BMSBloodTool.getInstance().onQueryBloodCrossExceptOut(parm);
            //modify by lim 2012/04/27 end
            //this.messageBox(result+"");
            if (result != null && result.getCount() > 0) {
                table_m.setParmValue(result);
            }
            ( (TMenuItem) getComponent("stock")).setEnabled(true);
        }
    }

    /**
     * 病案号查询(回车事件)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
     // modify by huangtt 20160928 EMPI患者查重提示 start
        this.setValue("MR_NO", mr_no);
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			mr_no =  pat.getMrNo();
			this.setValue("MR_NO", mr_no);
		}
     // modify by huangtt 20160928 EMPI患者查重提示 end
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm resultParm = BMSApplyMTool.getInstance().onApplyQuery(parm);
        //System.out.println("resultParm==="+resultParm);
        if (resultParm.getCount("APPLY_NO") <= 0) {
            this.messageBox("不存在该病患的备血单");
            this.setValue("MR_NO", "");
            return;
        }
        if (resultParm.getCount("APPLY_NO") == 1) {
            parm.setData("CASE_NO", resultParm.getValue("CASE_NO", 0));
        }
        else {
            TParm resultData = (TParm) openDialog(
                "%ROOT%\\config\\bms\\BMSQueryPat.x",
                parm);
            if (resultData == null) {
                return;
            }
            parm.setData("CASE_NO", resultData.getValue("CASE_NO"));
        }
        Object result = openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            this.setValue("APPLY_NO", addParm.getValue("APPLY_NO"));
            onApplyNoAction();
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
        ( (TMenuItem) getComponent("stock")).setEnabled(false);
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
     * 数据检核
     * @return boolean
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("备血单号不能为空");
            return false;
        }
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("病案号不能为空");
            return false;
        }

        int count = table_m.getRowCount();
        if (count == 0) {
            this.messageBox("不存在保存数据");
            return false;
        }
        
        // 是有存在未做单笔保存的数据
        for (int i = 0; i < count; i++) {
            if ("".equals(table_m.getItemString(i, "RESULT"))) {
                this.messageBox("存在未做单笔保存的数据");
                return false;
            }
        }

//        // （配血量 + 出库量）是否大于申请量
//        Map map = new HashMap();
//        for (int i = 0; i < count; i++) {
//            TParm parm = new TParm();
//            parm.setData("BLD_CODE",table_m.getItemString(i, "BLD_CODE"));
//            if (map.isEmpty()) {
//
//            }
//            else {
//
//            }
//        }
        return true;
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
}
