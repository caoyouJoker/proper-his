package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.bms.BMSApplyMTool;
import jdo.sys.Operator;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSApplyNoControl
    extends TControl {

    // 外部调用传参
    private TParm parm;

    private String mr_no;

    private String adm_type = "";

    private String case_no = "";
    
    private TParm result;

    public BMSApplyNoControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            mr_no = parm.getValue("MR_NO");
            case_no = parm.getValue("CASE_NO");
            
            if("No".equals(parm.getValue("new"))){
            	( (TMenuItem) getComponent("new")).setEnabled(false);
            }
            
            if (parm.existData("ADM_TYPE")) {
                adm_type = parm.getValue("ADM_TYPE");
            }
            else {
                ( (TMenuItem) getComponent("new")).setEnabled(false);
            }
        }

        TParm inparm = new TParm(); 
        inparm.setData("MR_NO", mr_no);
        inparm.setData("CASE_NO", case_no);
        if (!"".equals(adm_type)) {
            inparm.setData("ADM_TYPE", adm_type);
        }
        result = BMSApplyMTool.getInstance().onApplyQuery(inparm);
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * 新建申请单
     */
    public void onNew() {
    	// mofified by wangqing 20171211 start
    	// 屏蔽此处代码   	
    	/*    	// 20170621 wuxy start
    	String str = Operator.getPosition();
    	if(str.equals("231") || str.equals("232") || str.equals("233")){
    	//	20170621 wuxy end 
    		parm.setData("FROM_FLG", "1");
    		//parm.setData("BMS_TYPE",str);
    		TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
    				parm);
    		this.closeWindow();
    	}else{
    		this.messageBox("没有权限");
    	}*/
    	// modified by wangqing 20171211 end
    	
    	// add by wangqing 20171211 start
    	if(adm_type!=null && adm_type.trim().length()>0){
    		parm.setData("FROM_FLG", "1");
    		openDialog("%ROOT%\\config\\bms\\BMSApply.x", parm);
    		this.closeWindow();
    	}else{
    		this.messageBox("bug:::adm_type is null");
    		return;
    	}
    	// add by wangqing 20171211 end
			
    }

    /**
     * 返回方法
     */
      public void onReturn() {
//        TTable table = this.getTable("TABLE");
//        //String str = Operator.getPosition();
//        //System.out.println("11111"+str);
//        if (table.getSelectedRow() < 0) {
//            this.messageBox("E0134");
//            return;
//        }
//        String BmsFlg = result.getValue("BMS_FLG",0);
//        
//        if(BmsFlg.equals("Y")){
//        	TParm inparm = new TParm();
//        	inparm.setData("APPLY_NO",
//        			table.getItemString(table.getSelectedRow(), "APPLY_NO"));
//        	inparm.setData("FROM_FLG", "2");
//        	//inparm.setData("BMS_TYPE",str);
//        	if (!"".equals(adm_type)) {
//        		TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
//        				inparm);
//        	}
//        	else {
//        		this.setReturnValue(inparm);
//        	}
//        	this.closeWindow();
//        }else{
//        	this.messageBox("没有审核，请向上级审核");
//        }
//        
//        
    	  TTable table = this.getTable("TABLE");
          //String str = Operator.getPosition();
          //System.out.println("11111"+str);
          if (table.getSelectedRow() < 0) {
              this.messageBox("E0134");
              return;
          }
          TParm inparm = new TParm();
          inparm.setData("APPLY_NO",
                         table.getItemString(table.getSelectedRow(), "APPLY_NO"));
          inparm.setData("FROM_FLG", "2");
          //inparm.setData("BMS_TYPE",str);
          if (!"".equals(adm_type)) {
              TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
                                                inparm);
          }
          else {
              this.setReturnValue(inparm);
          }
          this.closeWindow();
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


}
