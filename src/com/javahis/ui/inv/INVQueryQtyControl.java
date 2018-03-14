package com.javahis.ui.inv;

import java.util.HashMap;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * Title:医疗物资供应室库存查询界面
 * </p>
 * 
 * <p>
 * Description: 医疗物资供应室库存查询界面
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-11-27
 * @version 1.0
 */
public class INVQueryQtyControl extends TControl{
	
	private TTabbedPane tabPane;
	/**
	 * 初始化
	 */
	public void onInit() {
	
		tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
		
		TParm parm = new TParm();
        //设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        //定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        
        TParm invParm = new TParm();
        //设置物资弹出窗口
        getTextField("INV_CODE").setPopupMenuParameter("INVBASE",
                                        getConfigParm().newConfig(
                                            "%ROOT%\\config\\inv\\INVBasePopup.x"), invParm);
        //定义接受返回值方法
        getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                 "invReturn");


        //设置默认科室
        TTextFormat tf = (TTextFormat)getComponent("DEPT_CODE");
        tf.setValue(Operator.getDept());
        tf = (TTextFormat)getComponent("DEPT_CODE_SEC");
        tf.setValue(Operator.getDept());
        
        this.initTable("1", "");
 //       tf = (TTextFormat)getComponent("DEPT_CODE_THD");
//        tf.setValue(Operator.getDept());
        
	}
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_desc))
            getTextField("PACK_DESC").setValue(pack_desc);
 
    }
    
    public void invReturn(String tag, Object obj){
    	
    	TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_DESC").setValue(inv_desc);
    	
    }

    public void onClear(){
    	
    	if(tabPane.getSelectedIndex() == 0){
    		getTextField("PACK_CODE").setValue("");
        	getTextField("PACK_DESC").setValue("");
        	this.setValue("MATERIAL_LOCATION", "");
        	((TTable) getComponent("TABLEM")).setParmValue(new TParm());
//        	((TTable) getComponent("TABLEM")).removeRowAll();
        	this.initTable("1", "");
    	}else if(tabPane.getSelectedIndex() == 1){
    		getTextField("INV_CODE").setValue("");
        	getTextField("INV_DESC").setValue("");
        	((TTable) getComponent("TABLEMSEC")).removeRowAll();
    	}
    	
    }
    
    public void onQuery(){
   
    	if(tabPane.getSelectedIndex() == 0){
    		TTable table = (TTable) getComponent("TABLEM");
    		boolean tag = false;	//判断是否查询具体料位诊疗包数量
    		
    		//查询诊疗包的全部种类
    		String sql = "SELECT PACK_CODE,PACK_DESC FROM INV_PACKM WHERE SEQ_FLG = '0' ";
    		if(null != this.getValueString("PACK_CODE") && !"".equals(this.getValueString("PACK_CODE")) && this.getValueString("PACK_CODE").length() > 0){
    			sql = sql + " AND PACK_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    		}
    		TParm pack = new TParm(TJDODBTool.getInstance().select(sql));
    		
    		//查询诊疗包在供应室的库存量
    		sql = this.getSupPackQtySQL();
    		TParm resultPack = new TParm(TJDODBTool.getInstance().select(sql));
    		
    		//查询诊疗包在料位上的库存量
    		sql = this.getMaterialPackQtySQL();
    		TParm resultMaterial = new TParm(TJDODBTool.getInstance().select(sql));
    		
    		//查询诊疗包在具体料位上的库存量
    		TParm oneMaterila = new TParm();
    		if(null != this.getValueString("MATERIAL_LOCATION") && !"".equals(this.getValueString("MATERIAL_LOCATION")) && this.getValueString("MATERIAL_LOCATION").length() > 0){
    			tag = true;
    			sql = this.getOneMaterialPackQtySQL();
    			oneMaterila = new TParm(TJDODBTool.getInstance().select(sql));
    		}else{
    			tag = false;
    		}
    		
    		
    		if( null!=pack && pack.getCount("PACK_CODE")>0 ){
    			//诊疗包种类循环
    			boolean packQtyFlg = false;
    			boolean materialQtyFlg = false;
    			boolean oneMaterialQtyFlg = false;
    			for(int i=0;i<pack.getCount("PACK_CODE");i++){
    				
    				packQtyFlg = false;
    				materialQtyFlg = false;
    				oneMaterialQtyFlg = false;
    				
    				//组合供应室库存量
    				for(int j=0;j<resultPack.getCount("PACK_CODE");j++){
    					if( pack.getValue("PACK_CODE", i).equals(resultPack.getValue("PACK_CODE", j)) ){
    						pack.setData("SUP_QTY", i, resultPack.getDouble("SUP_QTY", j));
    						packQtyFlg = true;
    					}
    				}
    				if(!packQtyFlg){
    					pack.setData("SUP_QTY", i, 0);
    				}
    				
    				//组合料位库存量
    				for(int m=0;m<resultMaterial.getCount("PACK_CODE");m++){
    					if( pack.getValue("PACK_CODE", i).equals(resultMaterial.getValue("PACK_CODE", m)) ){
    						pack.setData("MATERIAL_QTY", i, resultMaterial.getDouble("MATERIAL_QTY", m));
    						materialQtyFlg = true;
    					}
    				}
    				if(!materialQtyFlg){
    					pack.setData("MATERIAL_QTY", i, 0);
    				}
    				
    				//计算总库存
    				pack.setData("TOTAL_QTY", i, pack.getDouble("SUP_QTY", i)+pack.getDouble("MATERIAL_QTY", i) );
    				
    				
    				
    				if(tag){
    					//组合具体料位库存量
        				for(int m=0;m<oneMaterila.getCount("PACK_CODE");m++){
        					if( pack.getValue("PACK_CODE", i).equals(oneMaterila.getValue("PACK_CODE", m)) ){
        						pack.setData("ONELOCATION_QTY", i, oneMaterila.getDouble("ONELOCATION_QTY", m));
        						oneMaterialQtyFlg = true;
        					}
        				}
        				if(!oneMaterialQtyFlg){
        					pack.setData("ONELOCATION_QTY", i, 0);
        				}
        				
        				String locationName = ((TTextFormat) getComponent("MATERIAL_LOCATION")).getText();
        				this.initTable("2", locationName);
        				
    				}else{
    					this.initTable("1", "");
    				}
    				
    			}
    		}
    		((TTable) getComponent("TABLEM")).setParmValue(pack);
    		
    	}else if(tabPane.getSelectedIndex() == 1){
    		
    		//查询供应室物资的库存量
    		TParm supInv = new TParm(TJDODBTool.getInstance().select(this.getSupInvQtySQL()));
    		//查询诊疗包中的物资数量
    		TParm packageInv = new TParm(TJDODBTool.getInstance().select(this.getPackageInvQty()));
    		
    		//查询料位上诊疗包中的物资数量
    		TParm materialPackageInv = new TParm(TJDODBTool.getInstance().select(this.getMaterialPackageInvQty()));
    		
    		if( null!=supInv && supInv.getCount("INV_CODE")>0 ){
    			
    			boolean packInvFlg = false;
    			boolean materialPackInvFlg = false;
    			
    			for(int i=0; i<supInv.getCount("INV_CODE");i++){
    				
    				packInvFlg = false;
    				materialPackInvFlg = false;
    				//组合诊疗包内物资库存量
    				for(int j=0;j<packageInv.getCount("INV_CODE");j++){
    					if( supInv.getValue("INV_CODE", i).equals(packageInv.getValue("INV_CODE", j)) ){
    						supInv.setData("PACKAGE_QTY", i, packageInv.getDouble("QTY", j));
    						packInvFlg = true;
    					}
    				}
    				if(!packInvFlg){
    					supInv.setData("PACKAGE_QTY", i, 0);
    				}
    				
    				//组合料位诊疗包内物资库存量
    				for(int j=0;j<materialPackageInv.getCount("INV_CODE");j++){
    					if( supInv.getValue("INV_CODE", i).equals(materialPackageInv.getValue("INV_CODE", j)) ){
    						supInv.setData("MATERIAL_PACKAGE_QTY", i, materialPackageInv.getDouble("QTY", j));
    						materialPackInvFlg = true;
    					}
    				}
    				if(!materialPackInvFlg){
    					supInv.setData("MATERIAL_PACKAGE_QTY", i, 0);
    				}
    			
    				//计算总库存
    				supInv.setData("TOTAL_QTY", i, supInv.getDouble("STOCK_QTY", i)+supInv.getDouble("PACKAGE_QTY", i)+supInv.getDouble("MATERIAL_PACKAGE_QTY", i) );
    				
    			}
    			
    		}
    		((TTable) getComponent("TABLEMSEC")).setParmValue(supInv);
    		
    	}

    }
    
    //查询供应室物资的库存量
    private String getSupInvQtySQL(){
    	
    	String sql = "";
    	
    	sql = " SELECT M.INV_CODE, B.INV_ABS_DESC AS INV_CHN_DESC, B.DESCRIPTION, B.STOCK_UNIT, M.STOCK_QTY  FROM INV_STOCKM M LEFT JOIN INV_BASE B ON M.INV_CODE = B.INV_CODE WHERE M.ORG_CODE = '" + this.getValueString("DEPT_CODE_SEC") + "' ";
    	
    	if(null != this.getValueString("INV_CODE") && !"".equals(this.getValueString("INV_CODE")) && this.getValueString("INV_CODE").length() > 0){
    		sql = sql + " AND M.INV_CODE = '" + this.getValueString("INV_CODE") + "' ";
    	}

    	return sql;
    	
    }
    
    
    //查询诊疗包中的物资数量
    private String getPackageInvQty(){
    	String sql = "";
    	
    	sql = " SELECT D.INV_CODE, SUM(D.QTY) AS QTY FROM INV_PACKSTOCKD D GROUP BY D.INV_CODE ";
    	
    	return sql;
    }
    
    
    //查询在料位中诊疗包中的物资数量
    private String getMaterialPackageInvQty(){
    	String sql = "";
    	
    	sql = " SELECT DD.INV_CODE, SUM(DD.QTY) AS QTY FROM INV_SUP_DISPENSEDD DD WHERE SUBSTR(DD.BARCODE, 7, 6) = '000000' GROUP BY DD.INV_CODE ";
    	
    	return sql;
    }
    
    
    

    //查询诊疗包在供应室的库存数量SQL
    private String getSupPackQtySQL(){
    	
    	String sql = "";
    	
    	sql = " SELECT P.PACK_CODE, P.PACK_DESC, CASE WHEN SUM(PM.QTY) IS NULL THEN 0 ELSE SUM(PM.QTY) END AS SUP_QTY " 
    		+ " FROM INV_PACKM P LEFT JOIN INV_PACKSTOCKM PM ON PM.PACK_CODE = P.PACK_CODE " 
    		+ " WHERE P.SEQ_FLG = '0' "; 

    	sql = sql + " AND PM.ORG_CODE = '" + this.getValueString("DEPT_CODE") + "' ";

    	if(null != this.getValueString("PACK_CODE") && !"".equals(this.getValueString("PACK_CODE")) && this.getValueString("PACK_CODE").length() > 0){
    		sql = sql + " AND P.PACK_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    	}
    	
    	sql = sql + " GROUP BY P.PACK_CODE, P.PACK_DESC ";
    	
    	return sql;
    	
    }
    
    //查询诊疗包在料位上的库存数量SQL
    private String getMaterialPackQtySQL(){
    	
    	String sql = "";
    	
    	sql = " SELECT P.PACK_CODE, P.PACK_DESC, CASE WHEN SUM(SD.ACTUAL_QTY) IS NULL THEN 0 ELSE SUM(SD.ACTUAL_QTY) END AS MATERIAL_QTY " 
    		+ " FROM INV_PACKM P LEFT JOIN INV_SUP_DISPENSED SD ON SD.INV_CODE = P.PACK_CODE " 
    		+ " WHERE P.SEQ_FLG = '0' ";
    	  
    	if(null != this.getValueString("PACK_CODE") && !"".equals(this.getValueString("PACK_CODE")) && this.getValueString("PACK_CODE").length() > 0){
    		sql = sql + " AND P.PACK_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    	}
    	
    	sql = sql + " GROUP BY P.PACK_CODE, P.PACK_DESC ";
    	
    	return sql;

    	
    }
    
    //查询诊疗包在具体料位上的库存量
    private String getOneMaterialPackQtySQL(){
    	
    	String sql = "";
    	
    	sql = " SELECT P.PACK_CODE, P.PACK_DESC, CASE WHEN SUM(SD.ACTUAL_QTY) IS NULL THEN 0 ELSE SUM(SD.ACTUAL_QTY) END AS ONELOCATION_QTY " 
    		+ " FROM INV_PACKM P LEFT JOIN INV_SUP_DISPENSED SD ON SD.INV_CODE = P.PACK_CODE " 
    		+ " WHERE P.SEQ_FLG = '0' ";
    	  
    	if(null != this.getValueString("PACK_CODE") && !"".equals(this.getValueString("PACK_CODE")) && this.getValueString("PACK_CODE").length() > 0){
    		sql = sql + " AND P.PACK_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    	}
    	
    	if(null != this.getValueString("MATERIAL_LOCATION") && !"".equals(this.getValueString("MATERIAL_LOCATION")) && this.getValueString("MATERIAL_LOCATION").length() > 0){
    		sql = sql + " AND SD.MATERIAL_LOCATION = '" + this.getValueString("MATERIAL_LOCATION") + "' ";
    	}
    	
    	sql = sql + " GROUP BY P.PACK_CODE, P.PACK_DESC ";
    	
    	return sql;
    	
    }
    

    /**
	 * table初始化
	 */
	private void initTable(String tag, String locationName) {
		TTable table = (TTable) getComponent("TABLEM");
		
		if(tag.equals("1")){
			Map map = this.getTableHeader();
			table.setHeader(map.get("header").toString());
			table.setParmMap(map.get("parmMap").toString());
			table.setColumnHorizontalAlignmentData(map.get("align").toString());
			table.setLockColumns("0,1,2,3,4");
		}else if(tag.equals("2")){
			Map map = this.getFullTableHeader(locationName);
			table.setHeader(map.get("header").toString());
			table.setParmMap(map.get("parmMap").toString());
			table.setColumnHorizontalAlignmentData(map.get("align").toString());
			table.setLockColumns("0,1,2,3,4,5");
		}
		
		
	}
    // 获得表头
	private Map getTableHeader() {
		String header = "诊疗包编码,150;诊疗包名称,200;供应室库存量,80,double,##;料位库存量,80,double,##;库存总数,80,double,##";
		String parmMap = "PACK_CODE;PACK_DESC;SUP_QTY;MATERIAL_QTY;TOTAL_QTY";
		String align = "0,left;1,left;2,right;3,right;4,right";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("header", header);
		map.put("parmMap", parmMap);
		map.put("align", align);
		return map;
	}
	// 获得表头
	private Map getFullTableHeader(String locationName) {
		String header = "诊疗包编码,150;诊疗包名称,200;供应室库存量,80,double,##;料位库存量,80,double,##;库存总数,80,double,##;其中"+locationName+"库存量,180,double,##";
		String parmMap = "PACK_CODE;PACK_DESC;SUP_QTY;MATERIAL_QTY;TOTAL_QTY;ONELOCATION_QTY";
		String align = "0,left;1,left;2,right;3,right;4,right;5,right";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("header", header);
		map.put("parmMap", parmMap);
		map.put("align", align);
		return map;
	}
	
    
	 /**
	  * 导出excel
	  * */
	 public void onExcel(){

		 if(tabPane.getSelectedIndex() == 0){
			 TTable table = (TTable) this.getComponent("TABLEM");
		     if (table.getRowCount() > 0){
		    	 ExportExcelUtil.getInstance().exportExcel(table, "诊疗包库存查询");
		     }
		 }else if(tabPane.getSelectedIndex() == 1){
			 TTable table = (TTable) this.getComponent("TABLEMSEC");
		     if (table.getRowCount() > 0){
		    	 ExportExcelUtil.getInstance().exportExcel(table, "供应室物资库存查询");
		     }
		 }

	 }

	
	
//	private void setTimes(){
//		//初始化    退货日期查询区间
//		Timestamp date = new Timestamp(new Date().getTime());
//		this.setValue("START_DATE", 
//				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
//					.substring(0, 10).replace("-", "/") + " 00:00:00");
//		this.setValue("END_DATE", 
//				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
//	}
	
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
