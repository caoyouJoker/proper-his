package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPrintListCombo;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndStockDTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import jdo.util.Manager;
import jdo.sys.SystemTool;
import com.javahis.util.ExportExcelUtil;

/**    
 * <p>
 * Title: 部门库存查询
 * </p>
 *
 * <p>
 * Description: 部门库存查询
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDOrgStockQueryControl
    extends TControl {
    public INDOrgStockQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
    	setValue("ACTIVE_FLG", "Y");
    	setValue("STOP_FLG", "N");    
//    	if ("Y".equalsIgnoreCase(this.getValueString("ACTIVE_FLG"))){
//    		callFunction("UI|STOP_FLG|setEnabled", false);
//    	}  
//    	else{
//    		callFunction("UI|ACTIVE_FLG|setEnabled", false);
//    	}
        // 显示全院药库部门   
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        String role = Operator.getRole();
        System.out.println("--ROLE:"+role);
        if(null != role && "YKJZZR".equals(role)){//如果是药剂科主任可见
    		// 初始化画面状态
        	this.callFunction("UI|save|setEnabled", true);
        }else {
        	// 初始化画面状态
    		  this.callFunction("UI|save|setEnabled", false);
		}
    }

    /**
     * 查询方法
     */
    public void onQuery() {
    	//加入启用和停用条件
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("查询部门不能为空");
            return;
        }
        this.getTable("TABLE").removeRowAll();
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
        if (!"".equals(this.getValueString("MAN_CODE"))) {
            parm.setData("MATERIAL_LOC_CODE", this.getValueString("MAN_CODE"));
        }
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if ("Y".equals(this.getValueString("SAFE_QTY"))) {
            parm.setData("SAFE_QTY", "SAFE_QTY");
        }
        if ("Y".equals(this.getValueString("CHECK_NOT_ZERO"))) {
            parm.setData("STOCK_QTY", "CHECK_NOT_ZERO");
        }

        if ("Y".equals(this.getValueString("ORDER_TYPE_W")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_C")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_G"))) {
            parm.setData("PHA_TYPE", "('W', 'C', 'G')");
        }
        if ("Y".equals(this.getValueString("ACTIVE_FLG"))){
        	parm.setData("ACTIVE_FLG",'Y');
        }   
        if  ("Y".equals(this.getValueString("STOP_FLG"))){
        	parm.setData("ACTIVE_FLG",'N');
        }   
        String mjFlg = this.getValueString("MJ_FLG");
        parm.setData("MJ_FLG",mjFlg);
        TParm result = new TParm();
        if (!"Y".equals(this.getValueString("SHOW_BATCH"))) {
          /*  result = IndStockDTool.getInstance().onQueryOrgStockQueryNotBatch(
                parm);*/
        	//修改 查询 低于最低库存量 by liyh 20120808不显示批号效期
        	//修改仅查询麻精药品功能 by fuwj 20130422
        	if("Y".equals(mjFlg)) {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLNew(parm)));
        	}else {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	}											
        }
        else {
        	//显示批号效期
        	if("Y".equals(mjFlg)) {
        		result = IndStockDTool.getInstance().getOrgStockDrugQuery(parm);
        	}else {
        		result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	}
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
//        Map map = new HashMap();
//        map.put("01", "西药");
//        map.put("02", "中成药");
//        map.put("03", "中草药");
//        map.put("04", "消毒剂");
//        map.put("05", "输液");
//        map.put("06", "制剂");
//        map.put("07", "试剂");
//        map.put("08", "原料");
        double stock_amt = 0;
        double own_amt = 0;
        for (int i = result.getCount() - 1; i >= 0; i--) {
            if ("N".equals(this.getValueString("ORDER_TYPE_W")) &&
                "W".equals(result.getValue("PHA_TYPE", i))&&!"Y".equals(result.getValue("CTRL_FLG", i))) {							
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_C")) &&
                "C".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_G")) &&
                "G".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
/*            if ("N".equals(this.getValueString("MJ_FLG")) &&
                    "Y".equals(result.getValue("CTRL_FLG", i))) {
                    result.removeRow(i);					
                    continue;								
            }*/
//            result.setData("TYPE_CODE", i,
//                           map.get(result.getData("TYPE_CODE", i)));
            stock_amt += result.getDouble("STOCK_AMT", i);
            own_amt += result.getDouble("OWN_AMT", i);
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }

        this.getTable("TABLE").setParmValue(result);
        this.setValue("STOCK_AMT", StringTool.round(stock_amt, 2));
        this.setValue("OWN_AMT", StringTool.round(own_amt, 2));
        this.setValue("DIFF_AMT", StringTool.round(own_amt - stock_amt, 2));
        this.setValue("SUM_TOT", this.getTable("TABLE").getRowCount());
    }
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;BATCH_NO;TYPE_CODE;"
            + "SAFE_QTY;CHECK_NOT_ZERO;SHOW_BATCH;MAN_CODE;STOCK_AMT;OWN_AMT;"
            + "DIFF_AMT;SUM_TOT";
        this.clearValue(clearStr);
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode("");
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        this.setValue("ORDER_TYPE_W", "Y");
        this.setValue("ORDER_TYPE_C", "Y");
        this.setValue("ORDER_TYPE_G", "Y");
        this.getTable("TABLE").removeRowAll();
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "药品库存统计表");
        date.setData("ORG_CODE", "TEXT",
                     "统计部门: " +
                     this.getTextFormat("ORG_CODE").getText());
        date.setData("DATE", "TEXT",
                     "统计时间: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 表格数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("MATERIAL_CHN_DESC",
                         tableParm.getValue("MATERIAL_CHN_DESC", i));
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        parm.addData("SYSTEM", "COLUMNS", "MATERIAL_CHN_DESC");
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("TOT", "TEXT", "合计： "+getValueDouble("OWN_AMT"));
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOrgStockQuery.jhw",
                             date);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "部门库存查询");
    }

    /**
     * 部门选择事件
     */
    public void onOrgAction() {
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode(
            TypeTool.getString(getTextFormat("ORG_CODE").getValue()));
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setSelectedIndex(
            0);
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    public void updateStockQty(){
    	String orgCode = getValueString("ORG_CODE");
        if ("".equals(orgCode)) {
            this.messageBox("查询部门不能为空");
            return;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        String updateDate =  date.toString().substring(0, 7).replace("-", "");
//        System.out.println("-------------查看是否已经结算sql:"+INDSQL.queryStockUpdate(updateDate,orgCode));
        //查询是否已经结算
        TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockUpdate(updateDate,orgCode)));
        if (parm.getCount()>0) {
			this.messageBox("本月已经结算，请勿重复结算");
			return ;
		}
        if (this.messageBox("提示", "是否结算库存", 2) == 0) {
        	//备份库存
//        	System.out.println("---------备份sql"+INDSQL.saveStockHistory(updateDate,orgCode));
        	TParm backParm = new TParm(TJDODBTool.getInstance().update(INDSQL.saveStockHistory(updateDate,orgCode)));
        }else {
			return;
		}        
//        System.out.println("--------------update sql:"+INDSQL.updateStockQty(orgCode));
        //批量出库
       TParm reultParm = updateStockQty(orgCode);//new TParm(TJDODBTool.getInstance().update(INDSQL.updateStockQty(orgCode)));
//       System.out.println("----------getErrCode: "+reultParm.getErrCode());
       if(reultParm.getErrCode()<0){
    	   this.messageBox("更新失败，请重新更新");
    	   return;
       }
       this.messageBox("更新成功");
//       System.out.println("--------------insert sql:"+INDSQL.insertStockUpdate(updateDate, orgCode));
       //保存出库记录
       TParm saveTParm = new TParm(TJDODBTool.getInstance().update(INDSQL.insertStockUpdate(updateDate, orgCode)));
       
    }
    
    /**
     * 批量扣库
     * @param orgCode
     */
    public TParm updateStockQty(String orgCode) {
    	TParm parm = new TParm();
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_TERM", Operator.getIP());
    	String sql = INDSQL.getIndAccountForRed("", "", "");
    //	System.out.println("----------sql:"+sql);
		TParm accountParm = new TParm(TJDODBTool.getInstance().select(sql));//INDTool.getInstance().queryIndAccout("",orgCode,"");
	//	System.out.println("----------------accountparm:"+accountParm);
		parm.setData("data", accountParm.getData());
		TParm result = new TParm();
		if (null != accountParm && accountParm.getCount()>0) {
	        // 执行数据新增
	        result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
	                                            "onUpdateStockQty", parm);
		}
		return result;
	}
/**
     * 同步物联网麻精药品库存
     */
    public void onSyn() {
    	TParm parm = new TParm();
    	TParm result = TIOM_AppServer.executeAction(                             
					"action.ind.INDStockSearchAction", "onSearchIndStock",parm);
    	 if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             this.messageBox("同步失败");
             return;
         }
    	 this.messageBox("同步成功");				
    	 this.onQuery();
    }
    
    /**
     * 麻精点击事件			
     */
    public void onClick() {
    	String mjFlg = this.getValueString("MJ_FLG");
    	if("Y".equals(mjFlg)) {
    		this.setValue("ORDER_TYPE_W", "N");
    		this.setValue("ORDER_TYPE_C", "N");
    		this.setValue("ORDER_TYPE_G", "N");
    	}else {
    		this.setValue("ORDER_TYPE_W", "Y");
    		this.setValue("ORDER_TYPE_C", "Y");
    		this.setValue("ORDER_TYPE_G", "Y");
    	}
    }
    
    public void onClickW() {
    	String flg = this.getValueString("ORDER_TYPE_W");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickC() {
    	String flg = this.getValueString("ORDER_TYPE_C");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("ORDER_TYPE_G");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
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
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


}
