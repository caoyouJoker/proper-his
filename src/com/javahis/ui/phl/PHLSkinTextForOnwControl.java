package com.javahis.ui.phl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.ekt.EKTIO;
import jdo.phl.PHLSQL;
import jdo.phl.PhlExecuteTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 静点室执行
 * </p>
 *
 * <p>
 * Description: 静点室执行
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
 * @author yanj 2014.08.18
 * @version 1.0
 */
public class PHLSkinTextForOnwControl extends TControl{
	public PHLSkinTextForOnwControl() {
    }
	private TTable table;//医嘱表格
	private String admType = "O";
	private List<Integer> list;
	 /**
     * 皮试批号
     */
    private  String batchno;
    private  String skinflg;
    private String deptCode;
    private String rxNo;
	private String orderCode;
	private String seqNo;
	private String orderNo;
	private String caseNo;
	TTextFormat clinicAreaCode;//诊区add by yanjing 20151104
    public String getSkinflg() {
		return skinflg;
	}

	public void setSkinflg(String skinflg) {
		this.skinflg = skinflg;
	}
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}
    public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
    public String getRxNo() {
		return rxNo;
	}
	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}
    public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
    public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
    public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	 admType = (String)this.getParameter(); //从界面接参 “O”门诊 “E”急诊
         if (admType == null || "".equals(admType)) {
             this.messageBox("未设置门急参数");
         }
         if("O".equals(admType)){
        	 this.setTitle("门诊注射医嘱执行");
         }else if("E".equals(admType)){
        	 this.setTitle("急诊注射医嘱执行");
         }
    	table = this.getTable("TABLE");
    	callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
    	onInitUI();
    	this.onQuery();
    }
    private void onInitUI(){
    	this.setValue("ADM_TYPE",admType);//"O":门诊；“E”急诊
    	 Timestamp date = SystemTool.getInstance().getDate();
 		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
 				"/")
 				+ " 00:00:00");
 		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
 				"/")
 				+ " 23:59:59");
 		//初始化诊区
 		this.setValue("CLINIC_ARER", Operator.getStation());
 		clinicAreaCode  = (TTextFormat) this.getComponent("CLINIC_ARER");// 执行科室
        TextFormatRegClinicArea combo_clinicarea = (TextFormatRegClinicArea) this
		.getComponent("CLINIC_ARER");
        combo_clinicarea.setDrCode(Operator.getID());
        combo_clinicarea.onQuery();
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
     * 医疗卡读卡
     */
    public void onEkt(){
    	TParm ektParm = EKTIO.getInstance().TXreadEKT();
    	if (null == ektParm || ektParm.getErrCode() < 0
				|| ektParm.getValue("MR_NO").length() <= 0) {
			this.messageBox(ektParm.getErrText());
			ektParm = null;
			return;
		}
    	String mrNo = ektParm.getValue("MR_NO");
    	setValue("MR_NO", mrNo);
    	onMrNoAction();
    }
    /**
     * 病案号查询(回车事件)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        this.setValue("MR_NO", mr_no);
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getPHLRegisterList(mr_no)));
        TParm patInfoParm = PatTool.getInstance().getInfoForMrno(mr_no);
        if (parm == null || parm.getCount("START_DATE") <= 0) {
//            this.messageBox("没有报道信息");
//            return;
        }
        String patName = patInfoParm.getValue("PAT_NAME", 0);
        String sexCode = patInfoParm.getValue("SEX_CODE", 0);
        Timestamp birthDate = patInfoParm.getTimestamp("BIRTH_DATE", 0);
        setValue("PAT_NAME", patName);
        setValue("SEX_CODE", sexCode);
        TParm admInpParm = new TParm();
//        admInpParm.setData("CASE_NO", caseNo);
        TParm admInpInfoParm = ADMInpTool.getInstance().selectall(admInpParm);
        Timestamp inDate = admInpInfoParm.getTimestamp("IN_DATE", 0);
        String AGE = StringUtil.showAge(birthDate, inDate);
        setValue("AGE", AGE);
        onQuery();
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
//        table.removeRowAll();
	        TParm parm = new TParm();
	    parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
	    parm.setData("S_DATE",getValueString("S_DATE").replace("-", "/").substring(0, 19));
	    parm.setData("E_DATE",getValueString("E_DATE").replace("-", "/").substring(0, 19));
//	    System.out.println("====+++++======33333 is ::"+getValueString("S_DATE").replace("-", "/").substring(0, 19));
        if (!"".equals(getValueString("MR_NO"))) {//病案号
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(getValueString("DEPT_CODE"))) {//科别
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(getValueString("CLINIC_ARER"))) {//诊区
            parm.setData("CLINIC_ARER", getValueString("CLINIC_ARER"));
        }
        if (!"".equals(getValueString("CLINIC_ROOM"))) {//诊室
            parm.setData("CLINIC_ROOM", getValueString("CLINIC_ROOM"));
        }
        if (!"".equals(getValueString("DR_CODE"))) {//看诊医生
            parm.setData("DR_CODE", getValueString("DR_CODE"));
        }
//        System.out.println("parm parm parm is ::"+parm);
        TParm result = new TParm();
        if(this.getRadioButton("ALL").isSelected()){//全部
//        	shm
//        	result = PhlExecuteTool.getInstance().onQueryForOnw(parm);
        	table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,21,22,23");
        	result = PhlExecuteTool.getInstance().onQueryForOnw(parm);
        }else if(this.getRadioButton("EXEC_NO").isSelected()){//未执行
        	table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,21,22,23");
        	result = PhlExecuteTool.getInstance().onQueryForOnwNo(parm);
        }else if(this.getRadioButton("EXEC_YES").isSelected()){//已执行
        	table.setLockColumns("1,2,3,4,5,6,7,8,9,10,14,15,16,17,18,19,20,21,22,23");
        	result = PhlExecuteTool.getInstance().onQueryForOnwYes(parm);
        	for(int i=0;i<result.getCount();i++){
        		result.setData("NS_EXEC_CODE",i,Operator.getID());//添加执行人 add by huangjw 20141005
        	}
        }

//        	result = PhlExecuteTool.getInstance().onQueryForOnw(parm);
        if (result == null || result.getCount() <= 0) {
//            this.messageBox("没有查询数据");
//            table.removeRowAll();
//            this.onClear();
        	this.callFunction("UI|TABLE|removeRowAll");
        	this.setValue("MR_NO", "");
        	this.setValue("PAT_NAME", "");
        	this.setValue("SEX_CODE", "");
        	this.setValue("AGE", "");
//        	this.setValue("CLINIC_ARER", "");
        	this.setValue("CLINIC_ROOM", "");
        	this.setValue("DR_CODE", "");
        	this.setValue("DEPT_CODE", "");
            return;
        }
        
         list=new ArrayList<Integer>();
       for(int i=0;i<result.getCount();i++){
              if("PS".equals(result.getValue("ROUTE_CODE", i))){
      	        	  table.setLockCell(i, "BATCH_NO", false);
           	          table.setLockCell(i, "SKINTEST_FLG", false);
      	        	  list.add(i);//如是皮试药品&&用法为皮试用法加list
              }else{
             	      table.setLockCell(i, "BATCH_NO", true);
             	      table.setLockCell(i, "SKINTEST_FLG", true);
              }
              if(!"Y".equals(result.getValue("LINKMAIN_FLG", i))&&"0".equals(result.getValue("LINK_NO", i))){//不是连接医嘱连接号应为空，不能是0 为空
     	    	 result.setData("LINK_NO",i, "");
     	       } 
              
         }
         for(int i=0;i<result.getCount();i++){
          result.setData("SKINTEST_FLG", i, this.getSkin(result.getValue("SKINTEST_FLG", i)));
        	 
         }
        table.setParmValue(result);
        if(result.getCount()==1){
        	table.setSelectedRow(0);
        	this.onTABLEClicked(0);
        	
        }
    
    }
    /**
	 * 查询数据显示汉字
	 * @param skin
	 * @return
	 */
	public String getSkin(String skin){
		String skinDesc="";
		if("0".equals(skin)){
			skinDesc="(-)阴性";
		}else if("1".equals(skin)){
			skinDesc="(+)阳性";
		}
		return skinDesc;
	}
	/**
     * add caoyong 20130114 
     * 点击事件
     */
    /**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		 TParm result = table.getParmValue().getRow(row);
		 this.setValue("ADM_TYPE", result.getValue("ADM_TYPE"));
		 this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));//科别
		 this.setValue("CLINIC_ARER", result.getValue("CLINICAREA_CODE"));//诊区
		 this.setValue("CLINIC_ROOM", result.getValue("CLINICROOM_NO"));//诊室
		 this.setValue("DR_CODE", result.getValue("DR_CODE"));//看诊医生
//       this.setValue("MR_NO", result.getValue("MR_NO", 0));
		 caseNo = result.getValue("CASE_NO");
       this.setValue("MR_NO", result.getValue("MR_NO"));
       this.setDeptCode(result.getValue("DEPT_CODE"));
       this.setRxNo(result.getValue("RX_NO"));
       this.setSeqNo(result.getValue("SEQ_NO"));
       this.setOrderCode(result.getValue("ORDER_CODE"));
       this.setOrderNo(result.getValue("RX_NO"));
       Pat pat = Pat.onQueryByMrNo( result.getValue("MR_NO"));
       this.setValue("PAT_NAME", pat.getName());
       this.setValue("SEX_CODE", pat.getSexCode());
       Timestamp date = SystemTool.getInstance().getDate();
       this.setValue("AGE",DateUtil.showAge(pat.getBirthday(), date));// modify caoyong 20140401
       this.setBatchno(result.getValue("BATCH_NO"));//药房方药后生成的皮试批号
       this.setSkinflg(result.getValue("SKINTEST_FLG"));
	}
	
    /**
	 * 皮试结果 
	 */
	public void onSkiResult(){
		//TParm resultParm=table.getParmValue();
		
		int row =table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择一行数据");
			return;
			
		}
		boolean flag=false;
		
		for(int i=0;i<list.size();i++){
			int a=TypeTool.getInt(list.get(i));
			if(row==a){
				flag=true;
				break;
			}
		 }
		
		if(!flag){
		  this.messageBox("此药品用法不是皮试用法");	
		  return;
		}
		TParm parm=new TParm();
		parm.setData("PHL", "PHL");
		parm.setData("BATCH_NO", this.getBatchno());
		parm.setData("SKINTEST_FLG",this.getSkinflg());
//		parm.setData("DEPT_CODE",this.getDeptCode());
		parm.setData("CASE_NO",caseNo);
		parm.setData("RX_NO",this.getRxNo());
		parm.setData("SEQ_NO",this.getSeqNo());
		parm.setData("ORDER_NO",this.getOrderNo());
		parm.setData("ORDER_CODE",this.getOrderCode());
		Object obj = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWSkiResult.x", parm, true);
		TParm result = (TParm) obj;
		String psResult="";
		if(result.getValue("SKINTEST_NOTE",0).equals("0")){
			psResult = "(-)阴性";
		}else if(result.getValue("SKINTEST_NOTE",0).equals("1")){
			psResult = "(+)阳性";
		}
		 //result.getValue("BATCH_NO",0)
		table.setItem(row, "BATCH_NO", result.getValue("BATCH_NO",0));
		table.setItem(row, "SKINTEST_FLG", psResult);
		table.acceptText() ;
	}
	 /**
     * 全选事件
     */
    public void onSelectAllAction() {
        String flg = "N";
        if ("N".equals(this.getValueString("ALL_SELECTED"))) {
            flg = "N";
        }
        else {
            flg = "Y";
        }
        for (int i = 0; i < table.getRowCount(); i++) {
           table.setItem(i, "SELECTED", flg);
       }
    }
	 /**
     * 保存方法
     */
    public void onSave() {
        boolean flg = true;
        table.acceptText();
        for (int i = 0; i < table.getRowCount(); i++) {
            //this.messageBox(table.getItemString(i, "EXEC_STATUS"));
            if ("Y".equals(table.getItemString(i, "SELECTED"))) {
                flg = false;
                break;
            }
        }
        if (table.getRowCount() == 0||flg) {
            this.messageBox("没有执行数据");
            return;
        }

        //调用操作员输入密码界面
        Object resultData = openDialog("%ROOT%\\config\\phl\\PHLOPTCheck.x");
        String operator_id = "";
        if (resultData != null) {
            TParm resultParm = (TParm) resultData;
            //System.out.println("resultParm=="+resultParm);
            operator_id = resultParm.getValue("USER_ID");
        }
        else {
            return;
        }

        TParm parm = table.getParmValue();
        Timestamp date = SystemTool.getInstance().getDate();
        TParm orderParm = new TParm();
        String PS="";
        int count=0;
            // 保存执行医嘱
        if (this.getRadioButton("EXEC_NO").isSelected()||
        		this.getRadioButton("EXEC_YES").isSelected()) {//未执行
            for (int i = 0; i < parm.getCount(); i++) {
                if ("N".equals(table.getItemString(i, "SELECTED"))) {
                    continue;
                }
                if(this.getRadioButton("EXEC_NO").isSelected()&&
                		"PS".equals(parm.getValue("ROUTE_CODE", i))){
                	if("".equals(table.getItemString(i, "BATCH_NO"))){
                		this.messageBox("请输入皮试批号");
                		return;
                	}
                	if("".equals(table.getItemString(i, "SKINTEST_FLG"))){
                		this.messageBox("请输入皮试结果");
                		return;
                	}
                 }
                count++;
                orderParm.addData("RX_NO", parm.getValue("RX_NO", i));
                orderParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
                orderParm.addData("CASE_NO", parm.getValue("CASE_NO", i));
                orderParm.addData("ORDER_NO", parm.getValue("RX_NO", i));
                orderParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
                orderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
                orderParm.addData("EXEC_STATUS", "1");
                orderParm.addData("EXEC_USER", operator_id);
                orderParm.addData("EXEC_DATE", date);
                orderParm.addData("NS_NOTE", parm.getValue("NS_NOTE", i));
                orderParm.addData("OPT_USER", operator_id);
                orderParm.addData("OPT_DATE", date);
                orderParm.addData("OPT_TERM", Operator.getIP());
                //add caoyong 20140328 更新opd_order 表皮试结果和批号
		        if("(+)阳性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
		      		PS="1";
		        }else if("(-)阴性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
		      		PS="0";
		      	}else {
		      		PS=table.getItemString(i, "SKINTEST_FLG");
		      		   }
                orderParm.addData("BATCH_NO",table.getItemString(i, "BATCH_NO"));
                orderParm.addData("SKINTEST_FLG",PS);
                
            }
            orderParm.setCount(count);
        }else if(this.getRadioButton("ALL").isSelected()){
        	this.messageBox("全部状态下不可保存");
        	return;
        }
        
        TParm parmData = new TParm();
        parmData.setData("ORDER_PARM", orderParm.getData());
        TParm result = TIOM_AppServer.executeAction("action.phl.PHLAction",
            "onPhlExecuteForOnw", parmData);
        
        if (result.getErrCode() < 0) {
           this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        //=============================更新PHL_ORDER 表，同步静点执行室add by huangjw  20141107 start
        for(int i=0;i<orderParm.getCount();i++){
        	TParm param=orderParm.getRow(i);
        	
        	TParm opdParm=getOpdOrderParm(param);
        	if(opdParm==null)
        		continue;
        	param.setData("BAR_CODE",getBarCode(param.getValue("RX_NO"),opdParm.getValue("LINK_NO",0)));//设置瓶签号
//        	String sql="UPDATE PHL_ORDER SET EXEC_STATUS='1', EXEC_USER='"+operator_id+"', EXEC_DATE=TO_DATE('"+date.toString().substring(0,19)+"','yyyy/MM/dd HH24:MI:SS') " 
//        	+" WHERE ADM_TYPE='"+orderParm.getValue("ADM_TYPE",i)+"'"
//        	+" AND CASE_NO='"+orderParm.getValue("CASE_NO",i)+"' AND SEQ_NO='"+orderParm.getValue("SEQ_NO",i)+"'"
//        	+" AND ORDER_CODE='"+orderParm.getValue("ORDER_CODE",i)+"' AND ORDER_NO='"+orderParm.getValue("ORDER_NO",i)+"'";
//        	//System.out.println("ssssss:::::"+sql);
//        	TParm newparm=new TParm(TJDODBTool.getInstance().update(sql));
//        	if(newparm.getErrCode()<0){
//        		this.messageBox(newparm.getErrText());
//        	}
        	getPhlOrderParm(param,opdParm);
        	
        }
      //=============================更新PHL_ORDER 表，同步静点执行室add by huangjw  20141107 end
        this.onQuery();
    }
    
    /**
     * 获取OPD_ORDER表中的数据 add by huangjw 20141111
     * @param parm
     * @return
     */
    public TParm getOpdOrderParm(TParm parm){
    	String sql="SELECT MR_NO,ADM_TYPE,LINKMAIN_FLG,LINK_NO,DR_CODE,ORDER_DATE,ROUTE_CODE,FREQ_CODE,TAKE_DAYS,DR_NOTE,NS_NOTE,DC_DR_CODE,DC_ORDER_DATE"
    		+" FROM OPD_ORDER WHERE SEQ_NO="+parm.getInt("SEQ_NO")+" AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("RX_NO")+"'";
    	//System.out.println("opdSql:::::"+sql);
    	TParm opdParm=new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(opdParm.getCount()<0){
    		return null;
    	}
    	return opdParm;
    }
    /**
     * 将数据同步到phl_order表中相当于，静点报道和静点执行同时执行的 add by huangjw 20141111
     * @param parm
     * @param opdparm
     */
    public void getPhlOrderParm(TParm parm,TParm opdparm){
    	String date=parm.getTimestamp("EXEC_DATE").toString();
    	String date1=date.substring(0,4)+date.substring(5,7)+date.substring(8,10)+date.substring(11,13)+date.substring(14,16)+date.substring(17,19);
    	String orderdate=opdparm.getTimestamp("ORDER_DATE",0).toString();
    	String neworderdate=orderdate.substring(0,4)+orderdate.substring(5,7)+
    			orderdate.substring(8,10)+orderdate.substring(11,13)+orderdate.substring(14,16)+orderdate.substring(17,19);
    	//String dcdate=opdparm.getTimestamp("DC_ORDER_DATE")
    	String sql="INSERT INTO PHL_ORDER(START_DATE,ADM_TYPE,CASE_NO,ORDER_NO,SEQ_NO,ORDER_CODE," 
    			+" MR_NO,DR_CODE,ORDER_DTTM,LINK_MAIN_FLG,LINK_NO,ROUTE_CODE,FREQ_CODE,TAKE_DAYS," 
    			+" BAR_CODE,BAR_CODE_PRINT_FLG,EXEC_STATUS,EXEC_USER,EXEC_DATE,DR_NOTE,NS_NOTE," 
    			+" OPT_USER,OPT_DATE,OPT_TERM)" 
    			+" VALUES('"+date1+"','"+opdparm.getValue("ADM_TYPE",0)+"','"+parm.getValue("CASE_NO")+"','"+parm.getValue("ORDER_NO")+"'," 
    			+" "+parm.getValue("SEQ_NO")+",'"+parm.getValue("ORDER_CODE")+"','"+opdparm.getValue("MR_NO",0)+"',"
    			+" '"+opdparm.getValue("DR_CODE",0)+"',TO_DATE('"+neworderdate+"','yyyyMMdd hh24miss'),'"+opdparm.getValue("LINKMAIN_FLG",0)+"','"+opdparm.getInt("LINK_NO",0)+"'," 
    			+" '"+opdparm.getValue("ROUTE_CODE",0)+"','"+opdparm.getValue("FREQ_CODE",0)+"',"+opdparm.getValue("TAKE_DAYS",0)+","
    			+" '"+parm.getValue("BAR_CODE")+"','Y','1','"+parm.getValue("EXEC_USER")+"',TO_DATE('"+date1+"','yyyyMMdd hh24miss'),'"+opdparm.getValue("DR_NOTE",0)+"',"
    			+" '"+opdparm.getValue("NS_NOTE",0)+"',"
    			+" '"+Operator.getID()+"',TO_DATE('"+date1+"','yyyyMMdd hh24miss'),'"+Operator.getIP()+"')";
    	//System.out.println("phlSql:::::"+sql);
    	TParm phlParm=new TParm(TJDODBTool.getInstance().update(sql));
    	if(phlParm.getErrCode()<0){
    		this.messageBox(phlParm.getErrText());
    	}
    	//return phlParm;
    }
    
    /**
     * 取得瓶签号add by huangjw 2014141111
     * @param orderNo String
     * @param linkNo String
     * @return String
     */
    private String getBarCode(String orderNo, String linkNo) {
        if ("".equals(linkNo)) {
            return "";
        }
        linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
        return orderNo + linkNo;
    }
    
    /**
     * 清空
     */
    public void onClear() {
    	this.onInitUI();
    	this.callFunction("UI|TABLE|removeRowAll");
    	this.setValue("MR_NO", "");
    	this.setValue("PAT_NAME", "");
    	this.setValue("SEX_CODE", "");
    	this.setValue("AGE", "");
//    	this.setValue("CLINIC_ARER", "");
    	this.setValue("CLINIC_ROOM", "");
    	this.setValue("DR_CODE", "");
    	this.setValue("DEPT_CODE", "");
    	
    }
}
