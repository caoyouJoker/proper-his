package com.javahis.ui.bms;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jdo.bms.BMSBldCodeTool;
import jdo.bms.BMSFeeTool;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.javahis.util.StringUtil;
import com.tiis.ui.TiEdit;


/**
 * <p>Title:血品费用字典
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 2013.04.23
 * @version 4.0
 */
public class BMSFeeControl extends TControl{
	 TiEdit TF_AntiCode = new TiEdit();
     private TTable  table  ;
     private int  selectRow = -1 ;
     private String tag = "BLD_CODE;SUBCAT_CODE;REMARK;DAY_END;BILL_CODE;MANG_CODE;CROSS_CODE;" +
     		              "ANTI_CODE;ORDER_CODE;BLETEST_CODE;SERV_CODE;R_FILTER_CODE;B_FILTER_CODE;" +
     		              "UNIT;UITOML;MANG_UNIT;OPT_USER;OPT_DATE;OPT_TERM;BILL_DESC;MANG_DESC;" +
     		              "CROSS_DESC;ANTI_DESC;ORDER_DESC;BLETEST_DESC;SERV_DESC;B_FILTER_DESC;R_FILTER_DESC" ;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}
	
	/**
	 * 初始化页面
	 *
	 */
	public void onInitPage(){
		 table = (TTable)this.getComponent("TABLE");
		 callFunction("UI|TABLE|addEventListener",
                 "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		 this.onInitTag() ;
         onQuery();
	}
	  /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
    	String nameArray[] = StringTool.parseLine(tag, ";");
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField(nameArray[0]).setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField(nameArray[1]).setValue(order_desc);
    
    }
	/**
	 * 添加对table 的监听事件
	 * @param row
	 */
	public void  onTABLEClicked(int row){
		if (row < 0)
            return;
		TParm  data = table.getParmValue() ;
		setValueForParm(tag, data, row) ;
		selectRow = row ;
	}
	
	/**
	 * 查询BLD_CODE;SUBCAT_CODE
	 */
	public void onQuery(){
		TParm selectParm = getParmForTag("BLD_CODE;SUBCAT_CODE",true); //查询条件
		TParm parm = BMSFeeTool.getInstance().selectData(selectParm) ;
		if(parm.getCount()<0){
			messageBox("查无数据") ;
			return ;
		}
		table.setParmValue(parm) ;
		selectRow =-1 ;
	}
	
	/**
	 * 保存
	 */
	public void onSave(){
		if(selectRow==-1){
			this.onInsert() ;
			return ;
		}
		else  this.onUpdate() ;
	}
	
	/**
	 * 新增
	 */
	
	public void onInsert(){
		if(!onCheck()){
			this.messageBox("重复数据，执行失败") ;
			return ;
		}
		TParm saveParm = getParmForTag(tag) ; //得到页面控件的值
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction("action.bms.BMSFeeAction", "onInsert", saveParm);
		if(result.getErrCode()<0){
			messageBox("执行失败") ;
			return ;
		}
		    messageBox("执行成功") ;
		    this.onClear();
	}
	/**
	 * 更新
	 */
	public void onUpdate(){
		TParm updateParm = getParmForTag(tag) ;
		updateParm.setData("OPT_USER", Operator.getID());
		updateParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction("action.bms.BMSFeeAction", "onUpdate", updateParm);
		if(result.getErrCode()<0){
			messageBox("执行失败") ;
			return ;
		}
		    messageBox("执行成功") ;
		    this.onClear();
	}
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue(tag) ;
		this.onQuery() ;
		selectRow = -1 ;
	}
	
	/**
	 * 删除
	 */
	public void onDelete(){
		
		 if (this.messageBox("询问", "是否删除", 2) == 0){
			 if(selectRow == -1){
				 this.messageBox("选择删除数据") ;
				 return ;
			 }
			 TParm deleteParm = new TParm() ;
			 deleteParm.setData("BLD_CODE", this.getValueString("BLD_CODE")) ;
			 deleteParm.setData("SUBCAT_CODE", this.getValueString("SUBCAT_CODE")) ;
			 TParm result = TIOM_AppServer.executeAction("action.bms.BMSFeeAction", "onDelete", deleteParm);
				if(result.getErrCode()<0){
					messageBox("删除失败") ;
					return ;
				}
				    messageBox("删除成功") ;
				    this.onClear() ;
		 }
		 else {
			 return ;
		 } 
	}	
	 /**
     * 变更血品
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(
            bld_code);
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
        TParm parm = new TParm();
        parm.setData("BLD_CODE", bld_code);
        TParm result = BMSBldCodeTool.getInstance().onQuery(parm);
//        this.setValue("UNIT", result.getValue("UNIT_CODE", 0));
        this.setValue("DAY_END", result.getDouble("VALUE_DAYS", 0));
    }
    /**
     * 血品规格事件
     */
    public void onQueryUnit(){
    	String subcat_code = this.getValueString("SUBCAT_CODE") ;
    	 String bld_code = getComboBox("BLD_CODE").getSelectedID();
    	  TParm parm = new TParm();
          parm.setData("SUBCAT_CODE", subcat_code);
          parm.setData("BLD_CODE", bld_code);
          TParm result =BMSFeeTool.getInstance().selectBmsSubCat(parm) ;
          this.setValue("UNIT", result.getValue("UNIT_CODE", 0));
          this.setValue("UITOML", result.getValue("BLD_VOL", 0));
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
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
    /**
     * 初始化医嘱控件
     * B_FILTER_CODE,B_FILTER_DESC;R_FILTER_CODE,R_FILTER_DESC;
	 * SERV_CODE,SERV_DESC;BLETEST_CODE,BLETEST_DESC
	 * ANTI_CODE,ANTI_DESC;CROSS_CODE,CROSS_DESC     BLETEST_CODE
	 * MANG_CODE,MANG_DESC;BILL_CODE,BILL_DESC;ORDER_CODE,ORDER_DESC
     */
    @SuppressWarnings("unchecked")
	public void onInitTag(){
    	Map  mapTag = new HashMap() ;
    	mapTag.put("BILL_CODE", "BILL_DESC") ;
    	mapTag.put("SERV_CODE", "SERV_DESC") ;
//    	mapTag.put("B_FILTER_CODE", "B_FILTER_DESC") ;
//    	mapTag.put("R_FILTER_CODE", "R_FILTER_DESC") ;
//    	mapTag.put("ANTI_CODE", "ANTI_DESC") ;
//    	mapTag.put("CROSS_CODE", "CROSS_DESC") ;
//    	mapTag.put("MANG_CODE", "MANG_DESC") ;
//    	mapTag.put("BLETEST_CODE", "BLETEST_DESC") ;
//    	mapTag.put("ORDER_CODE", "ORDER_DESC") ;  
    	 TParm parm = new TParm();
	        parm.setData("CAT1_TYPE", "OTH");
	        Iterator  ite = mapTag.entrySet().iterator();
	        while(ite.hasNext()){
	        	Map.Entry entry = (Entry) ite.next() ;
	       	 // 设置弹出菜单
		        getTextField(entry.getKey().toString()).setPopupMenuParameter(entry.getKey().toString()+";"+entry.getValue().toString(),
		            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
		            parm);
		        // 定义接受返回值方法     
		        getTextField(entry.getKey().toString()).addEventListener(
		            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	        	
	        }
    }
    /**
     * 重复数据的校验
     */
    public boolean onCheck(){
		String bldCode = "" ;
    	String subcatCode = "" ;
    	if(this.getValueString("BLD_CODE").length()>0)
    		 bldCode = this.getValueString("BLD_CODE") ;
    		
    
    		
    	if(this.getValueString("SUBCAT_CODE").length()>0)
    		subcatCode = this.getValueString("SUBCAT_CODE") ;
    		
    	String sql ="SELECT BLD_CODE,SUBCAT_CODE FROM BMS_BLDFEE " +
    			    " WHERE BLD_CODE ='"+bldCode+"'  AND " +
    			    " SUBCAT_CODE='"+subcatCode+"'"   ;
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	if(parm.getCount()>0){
    		return false ;
    	}
    	
    	return true ;
      
    }
}
