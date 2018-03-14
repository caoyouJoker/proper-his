package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;  
import java.util.Map;     

import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestMTool;  
import jdo.dev.DevOutRequestDTool;  
import jdo.dev.DevOutStorageTool;  
import jdo.dev.DevPurChaseTool;
import jdo.dev.DevTypeTool; 
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl; 
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;



/**  
 * <p>Title: 请领作业（请领单）</p>  
 *
 * <p>DESCRIPTION: 请领作业</p>   
 * 
 * <p>Copyright: Copyright (c) 2009</p>
 * 
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0      
 */ 
public class DevRequestControl extends TControl {  
    //出库明细缓冲区 
    /**
     * 初始化方法 
     */
    public void onInit() { 
         super.onInit();  
         addEventListener("DEV_REQUEST->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
         getTTable("DEV_REQUEST").addEventListener(
        		 TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
         initTableD();   
         onInitComponent();    
         onInitOperatorDept();  
         //完成状态下不可保存,删除
         if (getRadioButton("UPDATE_FLG_B").isSelected()) {
             ( (TMenuItem) getComponent("delete")).setEnabled(false);  
             ( (TMenuItem) getComponent("save")).setEnabled(false);
         } 
    }
     
    /** 
     * 设置科室下拉框权限   
     */
    private void onInitOperatorDept(){  
        // 显示全院设备部门
        if (getPopedem("deptAll"))   
            return;   
//        ((TextFormatDEVOrg)getComponent("OUT_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("REQUESTOUT_DEPT")).setOperatorId(Operator.getID());
    }
    /**  
     * 初始化界面默认值
     */ 
    public void onInitComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("REQUEST_DATE_BEGIN",timestamp);//请领开始时间
        setValue("REQUEST_DATE_END",timestamp);//请领结束时间
        setValue("OUT_DEPT",Operator.getDept());//供应科室
        setValue("IN_DEPT","");//申请科室
        setValue("REQUEST_NO","");//申请单号        
            
        setValue("REQUESTOUT_DEPT",Operator.getDept());//供应科室
        setValue("INREQUEST_DEPT","");//请领科室 
        setValue("FLG",'N');//紧急标记初始为N
        setValue("REQUEST_REASON","");//请领原因
        setValue("REMARK","");//备注
        setValue("REQUEST_STATUE","");//申请状态  
        setValue("OPERATOR",Operator.getName());//操作人员  
        //加入空行
        addDRow();
    }          
    /**
     * 清空方法
     */
    public void onClear(){
        onInitComponent();
        //确认PARMMAP的行
        initTableD();   
        addDRow(); 
        ((TTable)getComponent("REQUEST_TABLE")).removeRowAll();
    }     
    /**  
     * 初始化设备请领单表格  
     */
    public void initTableD(){   
        String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;" +
        		        "DEV_CODE;DEV_CHN_DESC;" +
        		        "SPECIFICATION;QTY;STORGE_QTY;" + 
        		        "SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE";  
        String stringMap[] = StringTool.parseLine(column,";");  
        TParm tableDParm = new TParm();  
        for(int i = 0;i<stringMap.length;i++){      
            tableDParm.addData(stringMap[i],"");
        }     
        ((TTable)getComponent("DEV_REQUEST")).setParmValue(tableDParm);   
        ((TTable)getComponent("DEV_REQUEST")).removeRow(0);           
        //待修改  
//        ((TTable)getComponent("DEV_REQUEST")).setLockColumns("1,2,3,4," +
//                                                             "5,6,7,8,9,"+
//                                                             "10,11,12"); 
    }
    /**
     * 查询方法
     */   
    public void onQuery(){    
        TParm parm = new TParm(); 
      //未完成
      if (getRadioButton("UPDATE_FLG_A").isSelected()) {
    	  parm.setData("FINAL_FLG", "N");
      }
      //已完成 
      else if(getRadioButton("UPDATE_FLG_B").isSelected()){
    	  parm.setData("FINAL_FLG", "Y");
      }  
      if (getValueString("REQUEST_NO").length() != 0)
        parm.setData("REQUEST_NO", getValue("REQUEST_NO"));
      if (getValueString("REQUEST_DATE_BEGIN").length() != 0)  
        parm.setData("REQUEST_DATE_BEGIN", getValue("REQUEST_DATE_BEGIN"));
      if (getValueString("REQUEST_DATE_END").length() != 0)
        parm.setData("REQUEST_DATE_END", getValue("REQUEST_DATE_END"));
      if (getValueString("OUT_DEPT").length() != 0)
        parm.setData("APP_ORG_CODE", getValue("OUT_DEPT"));  
      if (getValueString("IN_DEPT").length() != 0)
        parm.setData("TO_ORG_CODE", getValue("IN_DEPT"));
      if (parm.getNames().length == 0)   
         {                   
      	this.messageBox("无查询数据！");  
      	return;  
      }	            
      parm = DevOutRequestMTool.getInstance().queryRequestM(parm);
      if (parm.getErrCode() < 0){   
      	this.messageBox("查询数据有误！");  
          return; 
      }  
      ((TTable)getComponent("REQUEST_TABLE")).setParmValue(parm);
  	  }
       
    /**
     * 检核是否存在需要保存数据
     * @return boolean
     */
    private boolean onSaveCheck(){
        int rowCount = 0;
        for(int i = 0;i<((TTable)getComponent("DEV_REQUEST")).getRowCount();i++){
            if(("" + ((TTable)getComponent("DEV_REQUEST")).getValueAt(i,0)).equals("N")&&
               ("" + ((TTable)getComponent("DEV_REQUEST")).getValueAt(i,4)).length()!=0)
                rowCount++;     
        }
        if(((TTable)getComponent("REQUEST_TABLE")).getSelectedRow() < 0 &&
           rowCount == 0){
            messageBox("无保存信息");
            return true;
        }    
        return false;
   }
    /**
     * 删除动作
     */
    public void onDelete(){      	
    	TTable tableM = ((TTable)getComponent("REQUEST_TABLE")); 
    	TTable tableD = ((TTable)getComponent("DEV_REQUEST")); 
    	//parm = DevOutRequestMTool.getInstance().deletedevrequestmm(parm);
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            this.messageBox("已完成入库不可删除");
        }
  
        else {
            if (tableM.getSelectedRow() > -1) {
                if (this.messageBox("删除", "确定是否删除请领单", 2) == 0) {
                    TParm parm = new TParm();
                    parm = tableM.getParmValue(); 
                    // 细项信息
                    if (tableD.getRowCount() > 0) {
                    	tableD.removeRowAll();
                        String deleteSql[] = tableD.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // 主项信息
                    // 请领单号  
                    parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDVerifyinAction", "onDeleteM", parm);
                    // 删除判断
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    tableM.removeRow(
                    tableM.getSelectedRow());
                    this.messageBox("删除成功");
                }
            }
            else {
                if (this.messageBox("删除", "确定是否删除验收细项", 2) == 0) {
                    int row = tableD.getSelectedRow();
                    tableD.removeRow(row);
                    // 细项保存判断        
                    if (!tableD.update()) {
                        messageBox("E0001");  
                        return;
                    }
                    messageBox("P0001");
                    tableD.setDSValue();
                }
            }
        }
    	//删除请领表数据
    }
    
   /**
    * 保存动作
    */
   public void onSave(){
        getTTable("REQUEST_TABLE").acceptText();
        getTTable("DEV_REQUEST").acceptText();
        if(onSaveCheck())
            return;
//        if(getValueString("REQUEST_REASON").length() == 0){
//            messageBox("请领原因不可为空");
//            return;
//        }  
        if(getValueString("INREQUEST_DEPT").length() == 0){
            messageBox("请领科室不可为空");
            return;  
        }   
        TTable tableD = (TTable)getComponent("DEV_REQUEST");
        for(int i = 0;i < tableD.getRowCount()-1;i++){     
        	if((tableD.getValueAt(i,3) + "").length() == 0){
                messageBox("第"+(i+1)+"行设备编码不可为空");
                return;
            } 
            if((tableD.getValueAt(i,6) + "").length() == 0){
                messageBox("第"+(i+1)+"行请领数量不可为空");  
                return;    
            }  
            if(Integer.parseInt ("" + tableD.getValueAt(i,6))>Integer.parseInt ("" + tableD.getValueAt(i,7))){
            	 messageBox("第"+(i+1)+"行请领数量不可大于库存量");  
                 return;   
            } 
        }     
       //判断新增还是修改
       //TTable table = (TTable)getComponent("EXWAREHOUSE_TABLE");
       TTable table = (TTable)getComponent("REQUEST_TABLE");
       if(table.getSelectedRow() < 0)
    	   //主TABLE无数据则新增
           onNew();
       else
           onUpdate();  
   }
   /**
    * 保存修改动作(fux modify)
    */           
   public void onUpdate(){
	    //TParm parmD = new TParm(); 
	    boolean flg = this.getValueBoolean("FLG"); 
	 //fux modify 
        TTable tableM = ((TTable)getComponent("REQUEST_TABLE"));
        int row = tableM.getSelectedRow();
        if(row < 0)  
            return;
        TTable dTable = ((TTable)getComponent("DEV_REQUEST"));
        if(dTable.getRowCount() <= 0)
            return;
        TParm parm = dTable.getParmValue();  
        System.out.println("parm"+parm);    
        TParm parmDTransport = new TParm();  
        Timestamp timestamp = SystemTool.getInstance().getDate();    
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");   
        //删除系表明细项    
        for (int i = 0; i < dTable.getRowCount(); i++) {
            if("Y".equals(dTable.getItemString(i, "DEL_FLG"))){  
         	   boolean flgtiis = parm.getBoolean("DEL_FLG");
         	   boolean fig = StringTool.getBoolean(parm.getValue("DEL_FLG", i));
         	   System.out.println("fig"+fig); //true
         	   System.out.println("flgtiis"+flgtiis); //false      
         	   System.out.println("删除细表");              
         	   parm = DevOutRequestDTool.getInstance().deletedevrequestdd(parm);
         	   messageBox("删除成功");
         	   return;     
                }           
            }      
        for(int i = 0;i<parm.getCount("DEV_CODE");i++){  
//            if(compareTo(parmD,parm,i))                
//                continue;                 
            cloneTParm(parm,parmDTransport,i);
            TParm devBase = getDevBase("" + dTable.getItemData(i, "DEV_CODE"));
            TParm devStorge = getDevSTORGE("" + dTable.getItemData(i, "DEV_CODE"));
            parmDTransport.addData("SEQMAN_FLG", devBase.getData("SEQMAN_FLG",0)); 
            parmDTransport.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE")); 
            parmDTransport.addData("DEV_CODE ",dTable.getItemData(i, "DEV_CODE"));  
            parmDTransport.addData("DEV_CHN_DESC",dTable.getItemData(i, "DEV_CHN_DESC")); 
            parmDTransport.addData("SPECIFICATION",dTable.getItemData(i, "SPECIFICATION"));
            //不要忘记保存时校验QTY<STORGE_QTY     
            //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
            //SPECIFICATION;QTY;STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
            parmDTransport.addData("QTY",dTable.getItemData(i, "QTY"));   
            parmDTransport.addData("STORGE_QTY",devStorge.getData("QTY",0));
            parmDTransport.addData("SETDEV_CODE",dTable.getItemData(i, "SETDEV_CODE"));
            parmDTransport.addData("UNIT_CODE",dTable.getItemData(i, "UNIT_CODE")); 
            parmDTransport.addData("UNIT_PRICE",dTable.getItemData(i, "UNIT_PRICE"));
            parmDTransport.addData("TOT_VALUE",dTable.getItemData(i, "TOT_VALUE"));  
            parmDTransport.addData("UPDATE_FLG",flg);  
            parmDTransport.addData("OPT_USER",Operator.getID()); 
            parmDTransport.addData("OPT_DATE",timestamp);  
            parmDTransport.addData("OPT_TERM",Operator.getIP());  
        }    
          
        TParm actionParm = TIOM_AppServer.executeAction("action.dev.DevAction",
                           "UpdateRequest", parmDTransport);    
        System.out.println("actionParmUpdateRequest==="+actionParm);  
        if(actionParm.getErrCode() < 0){
        	messageBox("E0001");  
            return;      
        }
        messageBox("P0001"); 
        //tableM.getValueAt(row,1)是请领单号
        onPrintAction(""+tableM.getValueAt(row,1));
        TParm parmD = new TParm();  
        onTableMClick();
    } 
   /**
    * 保存新增动作
    */
   private void onNew(){   
	    TParm parmD = new TParm();  
	    boolean flg = this.getValueBoolean("FLG"); 
       String requestoutNo = DevOutRequestMTool.getInstance().getRequestNo();
       Timestamp timestamp = SystemTool.getInstance().getDate();
       String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
       "yyyyMMdd");  
       TParm dParm = new TParm();   
       TParm mParm = new TParm();
       TTable dTable = ((TTable)getComponent("DEV_REQUEST"));  
       System.out.println("i"+dTable.getRowCount());
       //dTable:(根据TABLE值改变事件，将参数Parm放入TTable对象) 
       for(int i = 0;i<dTable.getRowCount();i++){
    	   System.out.println("i"+i); 
    	   //设备编号 
           if(("" + dTable.getValueAt(i,3)).length() == 0)
               continue;
           //删除标记为Y  
           if(("" + dTable.getValueAt(i,0)).equals("Y")) 
               continue;
           //设置设备入库明细信息  
           //dTable.getValueAt(i,2) == dev_code     
           TParm devBase = getDevBase("" + dTable.getItemData(i, "DEV_CODE")); 
           TParm devStorge = getDevSTORGE("" + dTable.getItemData(i, "DEV_CODE"));
           dParm.addData("REQUEST_NO",requestoutNo); 
           dParm.addData("SEQMAN_FLG",dTable.getItemData(i, "SEQMAN_FLG")); 
           dParm.addData("DEVPRO_CODE",dTable.getItemData(i, "DEVPRO_CODE"));
           dParm.addData("DEV_CODE",dTable.getItemData(i, "DEV_CODE"));        
           dParm.addData("DEV_CHN_DESC",dTable.getItemData(i, "DEV_CHN_DESC")); 
           dParm.addData("SPECIFICATION",dTable.getItemData(i, "SPECIFICATION")); 
           //不要忘记保存时校验QTY<STORGE_QTY   
           dParm.addData("QTY",dTable.getItemData(i, "QTY"));  
           //fux modify     
           dParm.addData("STORGE_QTY",dTable.getItemData(i, "STORGE_QTY"));
           dParm.addData("SETDEV_CODE",dTable.getItemData(i, "SETDEV_CODE")); 
           //dParm.addData("SETDEV_CODE",dTable.getValueAt(i,8));
           dParm.addData("UNIT_CODE",dTable.getItemData(i, "UNIT_CODE"));
           dParm.addData("UNIT_PRICE",dTable.getItemData(i, "UNIT_PRICE")); 
           //fux modify 
           dParm.addData("TOT_VALUE", 1000); 
           //dParm.addData("TOT_VALUE", dTable.getItemData(i, "TOT_VALUE"));  
           dParm.addData("UPDATE_FLG",flg);   
           dParm.addData("OPT_USER",Operator.getID());                 
           dParm.addData("OPT_DATE",timestamp); 
           dParm.addData("OPT_TERM",Operator.getIP());    
           //0未完成  1未完全完成 3完全完成
           dParm.addData("FINA_TYPE","0"); 

       } 

       //设置入库单主表信息       
       mParm.addData("REQUEST_NO",requestoutNo);        
       mParm.addData("REQUEST_DATE",StringTool.getTimestampDate(timestamp));    
       mParm.addData("APP_ORG_CODE",getValue("REQUESTOUT_DEPT"));
       mParm.addData("TO_ORG_CODE",getValue("INREQUEST_DEPT"));  
       mParm.addData("REQUEST_USER",getValue("OPERATOR"));//操作人  
       mParm.addData("REQUEST_REASON",getValue("REQUEST_REASON"));
       mParm.addData("OPT_USER",Operator.getID());  
       mParm.addData("OPT_DATE",timestamp);    
       mParm.addData("OPT_TERM",Operator.getIP());       
       mParm.addData("REGION_CODE",Operator.getRegion()); 
       //N 未完成 Y已完成  
       mParm.addData("FINAL_FLG","N"); 
       System.out.println("mParm"+mParm);   
       System.out.println("dParm"+dParm);     
       TParm parm = new TParm();    
       parm.setData("DEV_REQUESTMM",mParm.getData());   
       parm.setData("DEV_REQUESTDD",dParm.getData());
       TParm actionParm = TIOM_AppServer.executeAction(        
           "action.dev.DevAction","InsertRequest", parm);     
       System.out.println("actionParmInsertRequest==="+actionParm); 
     if(actionParm.getErrCode() < 0){
    	 messageBox("E0001");    
           return;   
      }
     else if(mParm.getValue("REQUEST_USER").equals("")){
    	   messageBox("请填写请领人员");   
           return;   
     }
         messageBox("P0001");  
     for(int i = 0;i < mParm.getCount("REQUEST_NO");i++){
           onPrintAction(mParm.getValue("REQUEST_NO",i));
      }
       onClear();
       onQuery();
   }  

   private TParm getDevSTORGE(String devCode) {
	   String SQL="SELECT DEPT_CODE,DEV_CODE,BATCH_SEQ,QTY FROM DEV_STOCKM WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;  
}
   private TParm getDevDSTORGE(String devCode) {
	   String SQL="SELECT DEPT_CODE,DEV_CODE,BATCH_SEQ,QTY FROM DEV_STOCKDWHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
}
   /**
    * 得到设备基本属性信息
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBase(String devCode){
       String SQL=" SELECT * FROM DEV_BASE " +
       		" WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
    }  
   /**
    * 得到设备基本属性信息(主件取得附件信息)
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBaseSet(String devCode){
       String SQL=" SELECT * FROM DEV_BASE "+
       		" WHERE SETDEV_CODE = '"+devCode+"'"; 
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm; 
    } 
   
   /**
    * 得到设备基本属性信息(主件取得附件信息)
    * @param devCode String
    * @return TParm
    */
   public TParm getDevStock(String devCode,String deptCode){
       String SQL=" SELECT DEPT_CODE,DEV_CODE,QTY FROM DEV_STOCKM "+
       		" WHERE DEV_CODE = '"+devCode+"'" +
       	    " AND DEPT_CODE = '"+deptCode+"'"; 
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
    }
   /**
    * 得到设备编码规则信息
    * @param code String
    * @return String
    */
   private String getDevTypeCode(String code){
      TParm parm = DevTypeTool.getInstance().getDevRule();
      int classify1 = parm.getInt("CLASSIFY1",0);
      return code.substring(0,classify1);
   }
    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
        String names[] = from.getNames();
        for(int i = 0;i < names.length;i++){
            Object obj = from.getData(names[i],row);
            if(obj == null)
                obj = "";
            to.addData(names[i],obj);
        }
     }

     /**
      * 比较相同列TParm的值是否改变
      * @param parmA TParm
      * @param parmB TParm
      * @param row int
      * @return boolean
      */
     private boolean compareTo(TParm parmA,TParm parmB,int row){
        String names[] = parmA.getNames();
        for(int i = 0;i < names.length;i++){
            if(parmA.getValue(names[i],row).equals(parmB.getValue(names[i],row)))
                continue;
            return false;
        }
        return true;
    }
     /**
      * 设备出库单主表单击事件(有问题)
      */
     public void onTableMClick(){        
 	    TParm parmD = new TParm();    
	    boolean flg = this.getValueBoolean("FLG"); 
         TTable tableM = ((TTable)getComponent("REQUEST_TABLE"));
         int row = tableM.getSelectedRow();  
         TParm tableMParm = tableM.getParmValue();
         setValue("REQUEST_NUMBLE",tableMParm.getData("REQUEST_NO",row));
         System.out.println("resqus_no=="+tableMParm.getData("REQUEST_NO",row));
//         setValue("TO_ORG_CODE",tableMParm.getData("TO_ORG_CODE",row));
//         setValue("APP_ORG_CODE",tableMParm.getData("APP_ORG_CODE",row));
//         ((TTextFormat)getComponent("REQUEST_NUMBLE")).setEnabled(false);
//         ((TTextFormat)getComponent("REQUESTOUT_DEPT")).setEnabled(false);
//         ((TTextFormat)getComponent("INREQUEST_DEPT")).setEnabled(false); 
         TParm result = DevOutRequestDTool.getInstance().queryRequestD(tableMParm.getValue("REQUEST_NO",row));
         if(result.getErrCode() < 0)     
             return;        
         ((TTable)getComponent("DEV_REQUEST")).setParmValue(result);
//         ((TTable)getComponent("DEV_REQUEST")).setLockColumns("0,1,2,3,4,5,6,7,"+
//                                                                   "8,9,10,14,15,16,"+
//                                                                      "17,18,19,20,23");
         for(int i = 0;i<result.getCount();i++)
            cloneTParm(result,parmD,i);  
     }


    /**
     * 设备出库明细表格编辑事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //fux modify 
        //请领数量量编辑事件
        if(onTableQty(node))
            return true;
        //设备编码编辑事件
        if(onDevCode(node))
            return true;
        //设备属性编辑事件
        if(onDevProCode(node))
            return true;  
//        //领用科室编辑事件
//        if(onInExwarehouseDept(node))
//            return true;
        return false;
   }
//   /**
//    * 领用科室编辑事件
//    * @param node TTableNode
//    * @return boolean
//    */
//   public boolean onInExwarehouseDept(TTableNode node){
//       if(node.getColumn() != 10)
//           return false;
//       if(node.getValue().equals(getValue("EXWAREHOUSE_DEPT")))
//           return true;
//       return false;
//   }
   /**
    * 设备编码编辑事件
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevCode(TTableNode node){
       if(node.getColumn() != 4)
            return false;
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(("" + table.getValueAt(node.getRow(),5)).length() != 0)
            return true;
        return false;
   }
   /**
    * 设备属性编辑事件
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevProCode(TTableNode node){
       if(node.getColumn() != 3)  
            return false;
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(("" + table.getValueAt(node.getRow(),5)).length() != 0)
            return true;
        return false;
   }
   /** 
    * 设备入库量编辑事件
    * @param node TTableNode
    * @return boolean
    */
   public boolean onTableQty(TTableNode node){
       if(node.getColumn() != 7)
            return false;    
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(Integer.parseInt(node.getValue() + "") == 0){
            messageBox("请领量不可零");
            return true; 
        }    
        if(Integer.parseInt(node.getValue() + "") > 
           Integer.parseInt("" +table.getValueAt(node.getRow(),9))){
            messageBox("请领量不可大于库存量");
            return true;
        }
        //String tableTag,int row,int column,Object obj
        //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
        //SPECIFICATION;QTY;STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
        updateTableData("DEV_REQUEST",node.getRow(),17,   
                        Integer.parseInt(node.getValue() + "") *
                        Double.parseDouble("" +table.getValueAt(node.getRow(),16)));
        return false;
   }

   /**
    * 更新表格数据
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       ((TTable)getComponent(tableTag)).getParmValue().setData(getFactColumnName(tableTag,column),row,obj);
   }
   /**
    * 加入设备出库明细表格空行
    */
   public void addDRow(){   
	   //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;STORGE_QTY;INREQUEST_DEPT;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
       String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
       		           "SPECIFICATION;QTY;STORGE_QTY;INREQUEST_DEPT;" +
       		           "SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE";
       String stringMap[] = StringTool.parseLine(column,";");
       TParm tableDParm = new TParm();  
       for(int i = 0;i<stringMap.length;i++){  
          if(stringMap[i].equals("DEVPRO_CODE"))   
             tableDParm.setData(stringMap[i],"A"); 
           else  
             tableDParm.setData(stringMap[i],"");        
          } 
       ((TTable)getComponent("DEV_REQUEST")).addRow(tableDParm);  
   }   
   /**
    * 设备出库单打印
    */
   public void onPrint(){   
       TTable table = ((TTable)getComponent("REQUEST_TABLE"));
       if(table.getSelectedRow() < 0)
           return;
       onPrintAction("" + table.getValueAt(table.getSelectedRow(),0));
   }  
   /**
    * 打印动作
    * @param exWarehoseNo String
    */
   public void onPrintAction(String requestoutNo){  
       if(requestoutNo == null ||
    		   requestoutNo.length() == 0)
           return; 
       TParm printParm = new TParm();   
  
        TParm  parm = DevOutRequestMTool.getInstance().queryRequestM(requestoutNo);   
       if(parm.getErrCode() < 0)
           return;
       if(parm.getCount("REQUEST_NUMBLE") <= 0){
           messageBox("无打印资料");
           return;  
       }
       //以下都未实现
       //处理空值(为空时放空，加入属性中文说明)
         clearNullAndCode(parm);
       //设置表头  
       printParm.setData("HOSP_NAME",Operator.getHospitalCHNShortName());
       printParm.setData("REQUEST_NO",parm.getValue("REQUEST_NO",0));
       printParm.setData("REQUEST_DATE",parm.getValue("REQUEST_DATE",0).substring(0,10).replace('-','/'));
       printParm.setData("APP_ORG_CODE",getDeptDesc(parm.getValue("APP_ORG_CODE",0)));
       printParm.setData("REQUEST_USER",getOperatorName(parm.getValue("REQUEST_USER",0)));
       printParm.setData("TO_ORG_CODE",getDeptDesc(parm.getValue("TO_ORG_CODE",0)));
       //设置表格信息
       //DEV_REQUESTMM   
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
       parm.addData("SYSTEM", "COLUMNS", "APP_ORG_CODE");
       parm.addData("SYSTEM", "COLUMNS", "TO_ORG_CODE");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_DATE");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_USER");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_REASON");
       //DEV_REQUESTDD
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
       parm.addData("SYSTEM", "COLUMNS", "SEQMAN_FLG");
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "STORGE_QTY");
       parm.addData("SYSTEM", "COLUMNS", "SETDEV_CODE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");     
       printParm.setData("RECEIPT",parm.getData());  
       //openPrintWindow("%ROOT%\\config\\prt\\dev\\DevRequest.jhw",printParm);
  }  
  
  /**
   * 取得设备属性中文描述
   * @param devProCode String
   * @return String
   */
  public String getDevProDesc(String devProCode){
       TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                 " WHERE GROUP_ID = 'DEVPRO_CODE'"+
                                                 " AND   ID = '"+devProCode+"'"));
       return parm.getValue("CHN_DESC",0);
    }

    /**
     * 取得数据库访问类
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }
    /**
     * 得到操作人员姓名
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
       TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
       if(parm.getCount() <= 0)
           return "";
       return parm.getValue("USER_NAME",0);
    }
    /**
     * 得到设备中文描述
     * @param devCode String
     * @return String
     */
    public String getDevDesc(String devCode){
        String SQL="SELECT DEV_CHN_DESC FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getCount() <= 0)
            return "";
        return parm.getValue("DEV_CHN_DESC",0);
    }
    /**
     * 得到科室中文描述
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 处理空值
     * @param parm TParm
     */
    private void clearNullAndCode(TParm parm){
      String names[] = parm.getNames();
      for(int i = 0;i < names.length;i++){
          for(int j = 0 ; j < parm.getCount(names[i]) ; j++){
              if(parm.getData(names[i],j) == null)
                  parm.setData(names[i],j,"");
              if("DEVPRO_CODE".equals(names[i]))
                  parm.setData(names[i],j,getDevProDesc(parm.getValue(names[i],j)));
          }
      }
   } 
    /**
     * 设备录入事件
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //设备属性
        String devProCode = ""+getTTable("DEV_REQUEST").getValueAt(row,1);
        //状态条显示
        callFunction("UI|setSysStatus","");             
        //拿到列名
        String columnName = getFactColumnName("DEV_REQUEST",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_REQUEST").getValueAt(row,column)).length() != 0)
            return;
        TParm parm = new TParm(); 
        parm.setData("DEVPRO_CODE",devProCode);
        parm.setData("ACTIVE","Y");
        //设置弹出菜单
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    } 
 /**
  * 请领设备录入返回参数   
  * @param tag String
  * @param obj Object  
  */                 
    public void popReturn(String tag,Object obj){
     //判断对象是否为空和是否为TParm类型
       if (obj == null && !(obj instanceof TParm)) {
           return;  
     }     
       
     //类型转换成TParm     
     TParm parm = (TParm)obj;  
     callFunction("UI|setSysStatus", new Object[] { parm.getValue("DEV_CODE") + ":"
    		 + parm.getValue("DEV_CHN_DESC") + parm.getValue("SPECIFICATION") });
     getTTable("DEV_REQUEST").acceptText();   
     TParm tableParm = new TParm(); 
     //附件关联主件parm  
     TParm tableParmSet = new TParm();  
     String devCode = parm.getValue("DEV_CODE").replace("[", "").replace("]", "");
     //附件parm
     TParm parmSet = getDevBaseSet(devCode); 
     //供应科室的库存量(根据供应科室和DEV_CODE获得QTY)
     TParm parmStockM = getDevStock(devCode,this.getValueString("REQUESTOUT_DEPT"));
     //主键信息   1个 
     parm = getDevBase(devCode); 
         //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;
         //STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
     if(parmStockM.getValue("QTY",0)==null ||"".equals(parmStockM.getValue("QTY",0))){
    	 this.messageBox("此物资库存不足！");  
    	 return;
     } 
     //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;
     //STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
         tableParm.setData("DEL_FLG","N");     
         tableParm.setData("SEQMAN_FLG",parm.getValue("SEQMAN_FLG",0)); 
         tableParm.setData("DEVPRO_CODE",parm.getValue("DEVPRO_CODE",0));
         tableParm.setData("DEV_CODE",devCode);   
         tableParm.setData("DEV_CHN_DESC",parm.getValue("DEV_CHN_DESC",0));
         tableParm.setData("SPECIFICATION",parm.getValue("SPECIFICATION",0)); 
         tableParm.setData("QTY",parm.getValue("QTY",0));             
         tableParm.setData("STORGE_QTY", parmStockM.getValue("QTY",0)); 
         //非附件无主设备 
         tableParm.setData("SETDEV_CODE","");     
         tableParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
         tableParm.setData("UNIT_PRICE",  parm.getDouble("UNIT_PRICE",0));
         //差了一个总额            
//         int qty = Integer.parseInt ("" + parm.getData("QTY"));
//         System.out.println("qty"+qty);  
//         System.out.println("price"+price);    
         //fux need modify  
         System.out.println("tableParm"+tableParm);        
//         tableParm.setData("TOT_VALUE",Integer.parseInt ("" + tableParm.getData("STORGE_QTY")) * 
//                                      Double.parseDouble("" + parm.getDouble("UNIT_PRICE",0)));
         getTTable("DEV_REQUEST").removeRow(getTTable("DEV_REQUEST").getSelectedRow());
         getTTable("DEV_REQUEST").addRow(tableParm); 
//         ((TTable)getComponent("DEV_REQUEST")).setLockColumns("1,4,5,6,7,"9,14,15,16,17");   "+  
    if("".equals(parmSet.getValue("DEV_CODE"))){                                                           
         addDRow();  
     }   
   //如果是带附件的主件，就要把附件带出来而且附件数量要大于主件！！！ 
     else{  
    	 for(int i=0; i<parmSet.getCount() ;i++){ 
    		 //多个附件
    		 String devSetCode = parmSet.getValue("DEV_CODE",i).replace("[", "").replace("]", "");
    		 parmStockM = getDevStock(devSetCode,this.getValueString("REQUESTOUT_DEPT"));
    	     tableParmSet.setData("DEL_FLG","N");    
    	     tableParmSet.setData("SEQMAN_FLG",parmSet.getValue("SEQMAN_FLG",i));  
    	     tableParmSet.setData("DEVPRO_CODE",parmSet.getValue("DEVPRO_CODE",i));
    	     tableParmSet.setData("DEV_CODE",devSetCode);    
    	     tableParmSet.setData("DEV_CHN_DESC",parmSet.getValue("DEV_CHN_DESC",i));
    	     tableParmSet.setData("SPECIFICATION",parmSet.getValue("SPECIFICATION",i)); 
    	     tableParmSet.setData("QTY",parmStockM.getValue("QTY",i));  
    	     tableParmSet.setData("STORGE_QTY", parmStockM.getValue("QTY",i) );     
    	     tableParmSet.setData("SETDEV_CODE",parmSet.getValue("SETDEV_CODE",i));  
    	     tableParmSet.setData("UNIT_CODE",parmSet.getValue("UNIT_CODE",i));  
    	     tableParmSet.setData("UNIT_PRICE",parmSet.getValue("UNIT_PRICE",i)); 
    	     tableParmSet.setData("TOT_VALUE",Integer.parseInt ("" + parmSet.getData("STORGE_QTY")) * 
                     Double.parseDouble("" + parmSet.getDouble("UNIT_PRICE",0)));  
    	     tableParmSet.setData("PARM",tableParmSet.getData());
    	     //tableParmSet.getData("PARM", i); 
    	     TParm tableParmM = (TParm)tableParmSet.getData("PARM",i);
    	     getTTable("DEV_REQUEST").addRow(tableParmM);   
    	 }    
    	     addDRow();    
     }
     
      
} 
      
   /**
    * 得到表格栏位名
    * @param tableTag String
    * @param column int
    * @return String
    */
   public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }
    /**
     * 得到表格栏位索引
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){  
        return getTTable("DEV_REQUEST").getColumnModel().getColumnIndex(column);
    }
    /**
     * 拿到TTable
     * @param tag String 
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)getComponent(tag);
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
 
}
