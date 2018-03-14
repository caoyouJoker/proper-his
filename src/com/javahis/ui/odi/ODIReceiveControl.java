package com.javahis.ui.odi;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;



/**
 * Title: 包接收确认
 * Description:包接收确认
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */  
public class ODIReceiveControl extends TControl {
	private TTable table;
	private TTable table1;

    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        table1 = (TTable) this.getComponent("TABLE1");
        callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");      
        onClear();
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
    	if(this.getValue("BAR_CODE").equals("")){
    		this.messageBox("条码不能为空");
    		return true;
    	}
	    return false; 
	}
	 /**
     * 保存
     */
    public void onUpdate() {
    	String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		String OK = inParm.getValue("RESULT");
		if (!OK.equals("OK")) {
			return;
		}
    	TParm parmTable = table.getParmValue();
    	TParm parmparm = new TParm();
    	int row = table.getRowCount();
		if(row==0)
			return; 		
    	String receivedept = Operator.getDept();
		String receiveuser = Operator.getID();
		for (int i = 0; i < row; i++) {
		String barcode =  parmTable.getValue("BAR_CODE", i);
    	String sql = " UPDATE INV_SUP_DISPENSED SET RECEIVE_USER ='" + receiveuser + "',"+
    	             " RECEIVE_DATE = SYSDATE,"+
    	             " RECEIVE_DEPT ='" + receivedept + "'"+
    	             " WHERE BARCODE = '" + barcode + "'";
      //    System.out.println("sql=========="+sql); 		
	   TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
	      if (updateParm.getErrCode() < 0) {
		         return ;
	          }
	      
	    //显示数据
          TParm parm = onRefresh(barcode); 
          parmparm.addRowData(parm, 0);
         }
		  parmTable = parmparm; 
		 table.setParmValue(parmTable);
         this.messageBox("保存成功！");
    }
    /**
     * 查询（用于条码扫描）
     */
    public TParm onRefresh(String barcode) {
    	//ODIReceiveUI.x
    	String sql =" SELECT A.BARCODE AS BAR_CODE,B.PACK_DESC,A.QTY,"+
    	" C.DEPT_CHN_DESC AS RECEIVE_DEPT,D.USER_NAME AS RECEIVE_USER,A.RECEIVE_DATE,"+
    	" B.PACK_CODE,A.INVSEQ_NO AS PACK_SEQ_NO,A.DISPENSE_NO,A.RECEIVE_DATE+B.VALUE_DATE AS VALIE_DATE"+
    	//INV_SUP_DISPENSED 效期都是空。
    	" FROM INV_SUP_DISPENSED A, INV_PACKM B,SYS_DEPT C,SYS_OPERATOR D"+
    	" WHERE A.BARCODE = '" + barcode + "'"+
    	" AND A.INV_CODE = B.PACK_CODE "+
    	" AND A.RECEIVE_DEPT = C.DEPT_CODE(+)"+
    	" AND A.RECEIVE_USER = D.USER_ID(+)";
    	TParm parmTable = new TParm(TJDODBTool.getInstance().select(sql));
    	 if (parmTable.getCount() <= 0) {
             this.messageBox("没有对应数据！");
             return null;
         }
      return parmTable;    	
    }
    /**
     * 查询
     */
    public void onQuery() {
    	TParm result = new TParm();
    	if(this.getValue("RECEIVE_CODE").equals("")){
    		this.messageBox("接收科室不能为空");
    		return;
    	}
    	if(this.getValue("RECEIVE_USER").equals("")){
    		this.messageBox("接收人员不能为空");
    		return;
    	}
    	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return;
    	}
    	String receivedept = getValue("RECEIVE_CODE").toString();
		String receiveuser = getValue("RECEIVE_USER").toString();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//查询病患
		String sql =" SELECT A.BARCODE AS BAR_CODE,B.PACK_DESC,A.QTY,"+
			" C.DEPT_CHN_DESC AS RECEIVE_DEPT,D.USER_NAME AS RECEIVE_USER,A.RECEIVE_DATE,"+
			//A date b numble
			" B.PACK_CODE,A.INVSEQ_NO AS PACK_SEQ_NO,A.DISPENSE_NO,A.RECEIVE_DATE+B.VALUE_DATE AS VALIE_DATE"+
			" FROM INV_SUP_DISPENSED A, INV_PACKM B,SYS_DEPT C,SYS_OPERATOR D"+
			" WHERE  A.RECEIVE_DEPT ='" + receivedept + "'"+
			" AND A.RECEIVE_USER ='" + receiveuser + "'"+   
			" AND A.RECEIVE_DATE BETWEEN "+
			" TO_DATE('" + startDate + "','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('" + endDate + "','YYYYMMDDHH24MISS')"+
			" AND A.INV_CODE = B.PACK_CODE"+   
			" AND A.RECEIVE_DEPT = C.DEPT_CODE(+)"+
			" AND A.RECEIVE_USER = D.USER_ID(+) ";		
		
//		GregorianCalendar gcNew=new GregorianCalendar();
//	    gcNew.set(Calendar.MONTH, gcNew.get(Calendar.MONTH)-1);
//	    Date dtFrom=gcNew.getTime();
		
//		System.out.println("sql=====:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=====:"+result);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}  
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);		
		
  }
    
    /**
     * 取消接收
     */
    public void onCancel() {
    	String type = "singleExe";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		String OK = inParm.getValue("RESULT");
		if (!OK.equals("OK")) {
			return;
		}
    	int Row = table.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}		    
		TParm data = table.getParmValue().getRow(Row);//获得数据
		String barcode =data.getValue("BAR_CODE");//条码号
		String sql = " UPDATE INV_SUP_DISPENSED SET RECEIVE_USER ='',"+
        " RECEIVE_DATE ='',"+
        " RECEIVE_DEPT =''"+
        " WHERE BARCODE = '" + barcode + "'";
//        System.out.println("sql=========="+sql); 		
        TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
           if (updateParm.getErrCode() < 0) {
                   return ;
              }
        //显示数据
           TParm parmTable = onRefresh(barcode);
           table.setParmValue(parmTable);
    }  	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("RECEIVE_CODE",Operator.getDept());	
    	this.setValue("RECEIVE_USER",Operator.getID());
    	TTextField bar = ((TTextField) getComponent("BAR_CODE"));
 		bar.grabFocus();
 	    this.setValue("BAR_CODE","");
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
 	    this.callFunction("UI|TABLE1|setParmValue", new TParm());
    }
    /**
     * TABLE表单击事件显示包的明细
     */
    public void onTableClicked() {
    	int Row = table.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}		    
		TParm data = table.getParmValue().getRow(Row);//获得数据
		String packcode =data.getValue("PACK_CODE");//包号
		String packseqno =data.getValue("PACK_SEQ_NO");//序号
		String dispenseno =data.getValue("DISPENSE_NO");//出库单号
		String sql =" SELECT CASE  WHEN B.INV_ABS_DESC IS NULL THEN B.INV_CHN_DESC"+  
			" ELSE B.INV_ABS_DESC END AS INV_CHN_DESC,A.QTY," +
			" C.UNIT_CHN_DESC AS STOCK_UNIT,A.COST_PRICE "+
			" FROM INV_SUP_DISPENSEDD A,INV_BASE B,SYS_UNIT C "+
			" WHERE A.PACK_CODE = '" + packcode + "' "+
			" AND A.PACK_SEQ_NO = '" + packseqno + "' "+
			" AND A.DISPENSE_NO = '" + dispenseno + "'"+
		    " AND A.INV_CODE = B.INV_CODE" +
		    " AND A.STOCK_UNIT = C.UNIT_CODE" +
		    " ORDER BY A.SEQ_NO";
//		System.out.println("sql=========="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table1.setParmValue(result);		
		
    }
    /**
     * 条码扫描事件
     */
    public void onExeQuery() {
    	onTableAdd();
    }
    /**
     * 条码扫描回车事件
     */
    public void onScream() {
    	onTableAdd();
    }
    
    public void onTableAdd() {
    	//数据检核
    	if(checkdata())
		    return;
    	String barcode = getValue("BAR_CODE").toString();
    	//查询数据
    	TParm parmTable = onRefresh(barcode);
    	TParm tableParm =table.getParmValue();
        boolean flg = true;
        int row = 0;
        if (tableParm != null&&tableParm.getCount()>0) {
            for (int i = 0; i < tableParm.getCount("BAR_CODE"); i++) {
                if (tableParm.getValue("BAR_CODE", i).equals(parmTable.getValue("BAR_CODE", 0))) {
                    this.messageBox("已扫描此条码！");
                    row = i;
                    flg = false;
                    break;
                } else {
                    row = tableParm.getCount("BAR_CODE");
                }
            }
            if (flg) {
                tableParm.addParm(parmTable);
                table.setParmValue(tableParm);
                table.getTable().grabFocus();
            } else {
            	table.getTable().grabFocus();
                return;
            }
        } else {
        	table.setParmValue(parmTable);
        	table.getTable().grabFocus();
        	table.setSelectedRow(row);
        }
        TTextField bar = ((TTextField) getComponent("BAR_CODE"));
    	bar.grabFocus();
    	this.setValue("BAR_CODE", "");
    }
    
}
