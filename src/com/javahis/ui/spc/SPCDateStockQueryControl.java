package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:月结日静态库存查询control
 * </p>
 *  
 * <p>
 * Description: 月结日静态库存查询control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2015
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author wangjc 20150211
 * @version 1.0
 */
public class SPCDateStockQueryControl extends TControl {

	//日期格式化
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyyMMdd");
	//获取TCheckBox组件
	private TCheckBox check;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		String sql = "SELECT MONTH_CYCLE FROM BIL_SYSPARM WHERE ADM_TYPE = 'I' ";
		TParm monthCycleParm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(monthCycleParm.getInt("MONTH_CYCLE", 0));
		TTextFormat date = this.getTextFormat("TRANDATE");
		Calendar cdto = Calendar.getInstance();
		if(cdto.get(Calendar.DAY_OF_MONTH) < monthCycleParm.getInt("MONTH_CYCLE", 0)){
			if(cdto.MONTH == 1){
				cdto.add(Calendar.YEAR, -1);
				cdto.add(Calendar.MONTH, 11);
				cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
			}else{
				cdto.add(Calendar.MONTH, -1);
				cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
			}
		}else{
			cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
		}
		date.setValue(cdto.getTime());
		// 设置弹出菜单
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
        getTextField("ORDER_CODE_I").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE_I").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn_I");
        // TCheckBox组件
        check = (TCheckBox)getComponent("CHECK");
	}
	
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn_I(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_I").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_I").setValue(order_desc);
    }
	
    /**
     * 查询
     */
	public void onQuery(){
		TTable table = this.getTable("TABLE");
		TTextFormat date = this.getTextFormat("TRANDATE");
		String dateStr = this.formateDate.format(date.getValue());
		String orgCode = this.getValueString("ORG_CODE");
		String supCode = this.getValueString("SUP_CODE");
		String sqlStart = "SELECT A.SUP_ORDER_CODE, "//供应商药品编码
			       +" A.ORDER_CODE, "//HIS药品编码
			       +" B.ORDER_DESC, "//品名
			       +" B.SPECIFICATION, "//规格 
//			       +" D.UNIT_CHN_DESC, "//库存单位
			       +" A.BATCH_NO, "//批号
			       +" A.ORG_CHN_DESC, "//部门
			       +" A.SUP_CHN_DESC, "//供应商
//			       +" A.STOCK_QTY/C.DOSAGE_QTY||D.UNIT_CHN_DESC||CASE WHEN "
//			       +" MOD(A.STOCK_QTY,C.DOSAGE_QTY)=0 THEN '' ELSE "
//			       +" MOD(A.STOCK_QTY,C.DOSAGE_QTY)||E.UNIT_CHN_DESC END AS QTY, "//库存数量
			       +" ROUND(A.STOCK_QTY/C.DOSAGE_QTY,2) AS STOCK_QTY, "//库存数量,整体包装
			       +" D.UNIT_CHN_DESC, "//库存单位
			       +" FLOOR(A.STOCK_QTY/C.DOSAGE_QTY)||D.UNIT_CHN_DESC||CASE WHEN "
			       +" (A.STOCK_QTY - FLOOR(A.STOCK_QTY / C.DOSAGE_QTY) * C.DOSAGE_QTY)=0 THEN '' ELSE "
			       +" (A.STOCK_QTY - FLOOR(A.STOCK_QTY / C.DOSAGE_QTY) * C.DOSAGE_QTY)||E.UNIT_CHN_DESC END AS QTY, "//库存数量，分散包装
			       +" F.CONVERSION_RATIO,A.PRICE,ROUND(A.STOCK_QTY * A.PRICE1,2) AS AMT "//进货转换比  
			  +" FROM (SELECT G.SUP_CODE,G.SUP_ORDER_CODE,G.ORDER_CODE,G.BATCH_NO,"
			       		+" SUM(G.STOCK_QTY) AS STOCK_QTY, "  
			       		+" H.ORG_CHN_DESC,H.ORG_CODE,I.SUP_CHN_DESC,G.INVENT_PRICE AS PRICE,G.VERIFYIN_PRICE AS PRICE1 "
			       			+" FROM IND_DDSTOCK G,IND_ORG H,SYS_SUPPLIER I "
			       			+" WHERE G.TRANDATE='"+dateStr+"' "
			       					+" AND G.ORG_CODE=H.ORG_CODE "
									+" AND G.SUP_CODE=I.SUP_CODE ";  
		//部门
		if(!orgCode.equals("")){
			sqlStart += " AND G.ORG_CODE='"+orgCode+"' ";
		}
		//供应商
		if(!supCode.equals("")){
			sqlStart += " AND G.SUP_CODE='"+supCode+"' ";
		}
		//fux modify 20150908  是否为0    
		if(this.getValueBoolean("ZERO")){    
			sqlStart += " AND G.STOCK_QTY != 0 ";
		}      
		
		String sqlEnd = " GROUP BY G.SUP_CODE,G.SUP_ORDER_CODE,G.ORDER_CODE,G.BATCH_NO,H.ORG_CHN_DESC,H.ORG_CODE,I.SUP_CHN_DESC,G.INVENT_PRICE,G.VERIFYIN_PRICE) A, "
			           +" PHA_BASE B,PHA_TRANSUNIT C,SYS_UNIT D,SYS_UNIT E, "
			         +" IND_CODE_MAP F "
			 +" WHERE A.ORDER_CODE=B.ORDER_CODE "           
			   +" AND B.ORDER_CODE=C.ORDER_CODE "  
			   +" AND C.STOCK_UNIT=D.UNIT_CODE "
			   +" AND C.DOSAGE_UNIT=E.UNIT_CODE "
			   +" AND A.SUP_CODE=F.SUP_CODE "
			   +" AND A.SUP_ORDER_CODE=F.SUP_ORDER_CODE ";
		
		if(check.isSelected()){
			sqlEnd += " AND A.STOCK_QTY != '0' ORDER BY ORG_CODE,ORDER_CODE";
		}else{
			sqlEnd += " ORDER BY ORG_CODE,ORDER_CODE";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlStart+sqlEnd));
		if(result.getCount()<0){
			table.removeRowAll();
			this.messageBox("未查询到数据");
			return;
		}
		for(int i=0;i<result.getCount("SUP_ORDER_CODE");i++){
			String str1[] = result.getValue("UNIT_CHN_DESC", i).split("");
			String unitStr = "";
			for(int j=0;j<str1.length;j++){
				if(str1[j].equals("[") || str1[j].equals("]")){
					str1[j] = "";
				}
				unitStr += str1[j];
			}
			result.setData("UNIT_CHN_DESC", i, unitStr);
			String str2[] = result.getValue("QTY", i).split("");
			String qtyStr = "";
			for(int j=0;j<str2.length;j++){
				if(str2[j].equals("[") || str2[j].equals("]")){
					str2[j] = "";
				}
				qtyStr += str2[j];
			}
			result.setData("QTY", i, qtyStr);
		}
		table.setParmValue(result);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		String clearNames = "ORG_CODE;SUP_CODE;ORDER_CODE_I;ORDER_DESC_I";
		//清空文本框内容
		this.clearValue(clearNames);
		TTextFormat date = this.getTextFormat("TRANDATE");
		Calendar cdto = Calendar.getInstance();
		date.setValue(cdto.getTime());
		this.getTable("TABLE").removeRowAll();
	}
	
    /**
     * 定位药品功能
     */
    public void onOrientationAction() {
    	TTable table = this.getTable("TABLE");
        if ("".equals(this.getValueString("ORDER_CODE_I"))) {
            this.messageBox("请输入定位药品");
            return;
        }
        boolean flg = false;
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("ORDER_CODE_I");
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("ORDER_CODE"); i++) {
            if (order_code.equals(parm.getValue("ORDER_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("未找到定位药品");
        }
        else {
            table.setSelectedRow(row);
        }
    }
	
	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "结算日库存");
	}
	
	/**
	 * 获取TTable控件
	 * @param tag 元素TAG名称
	 * @return
	 */
	public TTable getTable(String tag){
		return (TTable) this.getComponent(tag);
	}
	
	/**
	 * 获取TTextFormat控件
	 * @param tag 元素TAG名称
	 * @return
	 */
	public TTextFormat getTextFormat(String tag){
		return (TTextFormat) this.getComponent(tag);
	}
	
    /**
     * 得到TextField对象
     * @param tag 元素TAG名称
     * @return
     */
    private TTextField getTextField(String tag) {
        return (TTextField) getComponent(tag);
    }
}
