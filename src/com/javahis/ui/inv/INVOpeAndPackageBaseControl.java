package com.javahis.ui.inv;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 手术对应手术包</p>
 *    
 * <p>Description: </p>  
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p> 
 *
 * @author fux
 * @version 4.0
 */
public class INVOpeAndPackageBaseControl extends TControl{
	private TTable table; 
    /**  
     * 初始化方法
     *
     * @param tag
     * @param obj
     */
	public void onInit(){
        TParm parmPack = new TParm(); 
        table = this.getTable("TABLE");
        parmPack.setData("PACK_CODE", "");
        // 设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVPackPopup.x"),
            parmPack);
        // 定义接受返回值方法    
        getTextField("PACK_CODE").addEventListener( 
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnPack");
        
        TParm parmIcd = new TParm(); 
        parmIcd.setData("OPERATION_ICD", "");
        // 设置弹出菜单
        getTextField("OPERATION_ICD").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\sysOpICD.x"),
            parmIcd);  
        // 定义接受返回值方法 
        getTextField("OPERATION_ICD").addEventListener( 
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnIcd");
		onQuery();
	}  
    /**
     * 接受返回值方法(pack)
     *
     * @param tag
     * @param obj
     */
    public void popReturnPack(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;
        } 
        String pack_code = parm.getValue("PACK_CODE"); 
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC"); 
        if (!StringUtil.isNullString(pack_desc))  
            getTextField("PACK_DESC").setValue(pack_desc);
    }
    
    
    /**
     * 接受返回值方法(icd)
     *
     * @param tag
     * @param obj
     */
    public void popReturnIcd(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;
        }
        String operation_code = parm.getValue("OPERATION_ICD");
        if (!StringUtil.isNullString(operation_code))
            getTextField("OPERATION_ICD").setValue(operation_code);
        String opt_desc = parm.getValue("OPT_CHN_DESC");
        if (!StringUtil.isNullString(opt_desc)) 
            getTextField("OPT_CHN_DESC").setValue(opt_desc);
    }
	
    /**
     * 清空
     */ 
	public void onClear() {
		this.clearValue("OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;GDVAS_CODE");
		table = getTable("TABLE");
		table.removeRowAll();
        getTextField("OPERATION_ICD").setEnabled(true);  
        getTextField("OPT_CHN_DESC").setEnabled(false);
        getTextField("PACK_CODE").setEnabled(true);   
        getTextField("PACK_DESC").setEnabled(false);
       
	}
 
    /**
     * 保存方法
     */
    public void onSave() {
        // INV_AGENT保存数据
        TParm parmAgent = new TParm();
        parmAgent = getParmAgent(parmAgent);
        TParm result = new TParm(); 
        if (table.getSelectedRow() < 0) {
        	String flg = "INSERT";
            if (!CheckData(flg)) {
                return;
            }
        	messageBox("新增");
            // 新增数据
            result = TIOM_AppServer.executeAction(
                "action.inv.INVOpeAndPackageAction", "onInsert", parmAgent);
        }
        else {  
        	String flg = "UPDATE";
            if (!CheckData(flg)) {
                return;
            }
        	messageBox("更新");
            // 更新数据
            result = TIOM_AppServer.executeAction(
                "action.inv.INVOpeAndPackageAction", "onUpdate", parmAgent);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        //this.onClear();
        onQuery();
    }


    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckData(String flg) {
    	if ("INSERT".equals(flg)) {
    		   if ("".equals(getValueString("OPERATION_ICD"))) {
    	            this.messageBox("手术编码不能为空");
    	            return false;
    	        }
    	        if ("".equals(getValueString("OPT_CHN_DESC"))) {
    	            this.messageBox("手术名称不能为空");
    	            return false;
    	        }
    	        if ("".equals(getValueString("PACK_CODE"))) {
    	            this.messageBox("手术包编码不能为空");
    	            return false;
    	        }
    	        if ("".equals(getValueString("PACK_DESC"))) {
    	            this.messageBox("手术包名称不能为空");
    	            return false;
    	        }
		}  
        if ("".equals(getValueString("QTY"))) {
            this.messageBox("数量不能小于或等于0");
            return false;
        }
        return true;
    }
    
    
    /**
     * INV_AGENT保存数据
     * @param parm TParm
     * @return TParm
     */ 
    public TParm getParmAgent(TParm parm) {
        String packCode = this.getValueString("PACK_CODE");
        String sql = " SELECT SEQ_FLG " +  
        		" FROM INV_PACKM " +
        		" WHERE　PACK_CODE　= '"+packCode+"'　";
        TParm parmSel = new TParm(TJDODBTool.getInstance().select(sql));
        
        parm.setData("OPERATION_ICD", this.getValueString("OPERATION_ICD"));
        parm.setData("OPT_CHN_DESC", this.getValueString("OPT_CHN_DESC"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        parm.setData("PACK_DESC", this.getValueString("PACK_DESC"));
        parm.setData("QTY", this.getValueDouble("QTY"));  
        parm.setData("SEQ_FLG", parmSel.getValue("SEQ_FLG", 0));
        //入录血管
        parm.setData("GDVAS_CODE",  this.getValueString("GDVAS_CODE"));
        parm.setData("OPT_USER", Operator.getID()); 
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
    
    /**
     * 表格(CLNDIAG_TABLE)单击事件????
     */
    public void onTableClicked() {
        getTextField("OPERATION_ICD").setEnabled(false);
        getTextField("OPT_CHN_DESC").setEnabled(false);
        getTextField("PACK_CODE").setEnabled(false);
        getTextField("PACK_DESC").setEnabled(false);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow()); 
//        String opIcd = parm.getValue("OPERATION_ICD");
//        String packCode = parm.getValue("PACK_CODE"); 
        String textStr = "OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;GDVAS_CODE"; 
        this.setValueForParm(textStr, parm);
    } 
     
	
    /**
     * 删除方法
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择删除项");
            return;
        }
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD", this.getValueString("OPERATION_ICD"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        TParm result = TIOM_AppServer.executeAction(  
            "action.inv.INVOpeAndPackageAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;    
        }
        this.messageBox("删除成功");
        //this.onClear();
        onQuery();
    }
	
    /**
     * 查询方法
     */
	public void onQuery(){
		String opIcd = this.getValueString("OPERATION_ICD");
		String packCode = this.getValueString("PACK_CODE");
		String gdvasCode = this.getValueString("GDVAS_CODE");
		String why = "";

		String sql = " SELECT OPERATION_ICD,OPT_CHN_DESC,PACK_CODE,QTY,PACK_DESC," +
				" SEQ_FLG,GDVAS_CODE " +
				" FROM OPE_ICDPACKAGE" +
				" WHERE 1=1" +
				"";           
        StringBuffer SQL = new StringBuffer();   
        SQL.append(sql);    
		if(!"".equals(opIcd)){
			SQL.append(" AND OPERATION_ICD = '"+opIcd+ "'");  
		}      
		if(!"".equals(packCode)){
			SQL.append(" AND PACK_CODE = '"+packCode+ "'");  
		}
		if(!"".equals(gdvasCode)){
			SQL.append(" AND GDVAS_CODE = '"+gdvasCode+ "'");  
		}
		System.out.println("SQL:"+SQL);  
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString())); 
        if (parm.getCount() <= 0) {  
            this.messageBox("没有查询数据");
            return;  
        }
        table.setParmValue(parm);
	}
	//OP_ICD OP_DESC
	//PACK_CODE PACK_DESC
    //QTY
	 
	//TABLE:   OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;SEQ_FLG
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
     * 得到TComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */ 
    private TComboBox getTCombox(String tagName) {
        return (TComboBox) getComponent(tagName);  
    }
}
