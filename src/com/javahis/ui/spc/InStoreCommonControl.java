package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;

public class InStoreCommonControl extends TControl {


    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;
    
	// 单据类型
	private String request_type;

	// 申请单号
	private String request_no;

	// 使用单位
	private String u_type;

	// 出库部门
	private String out_org_code;

	// 入库部门
	private String in_org_code;

	// 是否出库
	private boolean out_flg;

	// 是否入库
	private boolean in_flg;

	// 入库单号
	private String dispense_no;
	
	int  seq = 1;

	private static final InStoreCommonControl INSTANCE =new InStoreCommonControl();
	
	public InStoreCommonControl() {

	}

	public static InStoreCommonControl getInstance(){
	     return INSTANCE;
	}
	

	/**
	 * 得到主表与子表的所有参数
	 * @param parm    返回的参数
	 * @param mParm   主表对应数据
	 * @param resultParm   子表对应的数据
	 * @param updateFlg    更新状态
	 * @param table        子表UI table名称
	 * @return
	 */
	public TParm onSave(TParm mParm,TParm resultParm,TTable table){
		/**
		 * 初始化数据
		 */
		initParm(mParm,table) ;
		
		TParm parm = new TParm();
		
		 // 2.出库批号判断
        String batchvalid = getBatchValid(request_type);
		parm = getDispenseOutIn(out_org_code, in_org_code, batchvalid, out_flg, in_flg, parm, mParm, resultParm);
		return parm;
	}
	
	 /**
     * 入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private TParm getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg ,TParm parm,TParm mParm ,TParm resultParm) {
    	
    	 parm = getDispenseMParm(parm, mParm,"3");
    	// 细项信息(OUT_D)
         if (!CheckDataD()) {
             return new TParm();
         }
         parm = getDispenseDParm(parm,resultParm);
         if (!"".equals(in_org_code)) {
             // 入库部门(IN_ORG)
             parm.setData("IN_ORG", in_org_code);
             // 是否入库(IN_FLG)
             parm.setData("IN_FLG", in_flg);
         }
         // 执行数据新增
         TParm result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                             "onInsertIn", parm);
         
         return result;
    	 
    	
    }
    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm,TParm mParm, String updateFlg) {
        // 药库参数信息
        TParm sysParm = getSysParm();
        // 是否回写购入价格
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        
        // 入库单号
        String  dispense_no = "";
        
        dispense_no = mParm.getValue("DISPENSE_NO",0);
       
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE",0));
        parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO",0));
        parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE",0));
        parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE",0));
        parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE",0));
        parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG",0));
        parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION",0));
        parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE",0));
        parmM.setData("DISPENSE_USER", Operator.getID());
       
        if (!"1".equals(updateFlg)) {
            parmM.setData("WAREHOUSING_DATE", getValue("WAREHOUSING_DATE"));
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        
        parmM.setData("DRUG_CATEGORY",mParm.getValue("DRUG_CATEGORY",0));
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        parmM.setData("APPLY_TYPE",mParm.getValue("REQTYPE_CODE",0));
        
        parmM.setData("REASON_CHN_DESC", mParm.getValue("REASON_CHN_DESC",0));
        parmM.setData("UNIT_TYPE", u_type);
        parmM.setData("UPDATE_FLG", updateFlg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //luhai 2012-01-13 add region_code begin
        parmM.setData("REGION_CODE", Operator.getRegion());
        //luhai 2012-01-13 add region_code end
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }
    
    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm,TParm resultParm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            
            parmD.setData("DISPENSE_NO", count, dispense_no);
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count, seq + count);
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, resultParm.getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    

	public void initParm(TParm mparm,TTable table) {
		table_d = table ;
		request_type = mparm.getValue("REQTYPE_CODE",0);
		if ("TEC".equals(request_type) || "EXM".equals(request_type)
				|| "COS".equals(request_type)) {
			u_type = "1";
		} else if ("DEP".equals(request_type)) {
			u_type = IndSysParmTool.getInstance().onQuery().getValue(
					"UNIT_TYPE", 0);
		}

		if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
			out_org_code =mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		} else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = true;
		} else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = false;
		} else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = false;
		} else if ("THI".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = false;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		}
	}
	
	 /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD() {
        // 判断细项是否有被选中项
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // 判断数据正确性
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("入库数数量不能小于或等于0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }
        }
        return true;
    }
    
    /**
     * 出库批号判断: 1,不指定批号和效期;2,指定批号和效期
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

	 /**
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }
}
