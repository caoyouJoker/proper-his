/**
 *
 */
package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSSpecialVeinTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

/**
*
* <p>Title: 静脉注射</p>
*
* <p>Description:静脉注射 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSSpecialVeinControl extends TControl {

	 TParm data;
	    int selectRow = -1;
	    public void onInit() {
	        super.onInit();
	        callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");
	        init();
	    }
	    /**
	     * 初始化界面，查询所有的数据
	     * @return TParm
	     */
	    public void onQuery() {

			 String EXCPHATYPE_CODE = getText("EXCPHATYPE_CODE");
			 if(EXCPHATYPE_CODE==null||"".equals(EXCPHATYPE_CODE)){
				 //System.out.println("EXCPHATYPE_CODE"+EXCPHATYPE_CODE);
				 init();
			 }else{
				 data = SYSSpecialVeinTool.getInstance().selectdata(EXCPHATYPE_CODE);
			        //System.out.println("result:" + data);
			        if(data.getErrCode() < 0)
			        {
			            messageBox(data.getErrText());
			            return;
			        }
			        this.callFunction("UI|TABLE|setParmValue",data,"SEQ;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG;OPT_USER;OPT_DATE");
			 }

		}
	    public void onTABLEClicked(int row){

	        //System.out.println("row=" + row);

	        if (row < 0) {
	        	return;
	        }
	        //System.out.println("data"+data);
	        setValueForParm("SEQ;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG",data,row);

	       selectRow = row;
	       callFunction("UI|EXCPHATYPE_CODE|setEnabled",false);

		}
	    /**
	     *清空
	     */
	    public void onClear() {
	        this.clearValue("SEQ;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG");
            callFunction("UI|TABLE|removeRowAll");
	        this.setValue("SEQ", "0");
	        this.setValue("PRINT_FLG", "N");
	        selectRow = -1;
	        callFunction("UI|EXCPHATYPE_CODE|setEnabled",true);
	    }
	    /**
	     * 初始化界面，查询所有的数据
	     * @return TParm
	     */
	    public void init(){
	    	  data = SYSSpecialVeinTool.getInstance().selectall();
		        //System.out.println("result:" + data);
		        if(data.getErrCode() < 0)
		        {
		            messageBox(data.getErrText());
		            return;
		        }
		        this.callFunction("UI|TABLE|setParmValue",data,"SEQ;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG;OPT_USER;OPT_DATE");
	    }
	    /**
	     * 新增
	     */
	    public void onInsert() {
	    	if(!this.emptyTextCheck("EXCPHATYPE_CODE,EXCPHATYPE_CHN_DESC")){
	    		return;
	    	}
	        TParm parm = getParmForTag("EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;EXCPHATYPE_ENG_DESC;PY1;PY2;SEQ:int;DESCRIPTION;PRINT_FLG");
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
	        SystemTool st=new SystemTool();
	        parm.setData("OPT_DATE",st.getDate());
//	        System.out.println("pram" + parm);
	        data = SYSSpecialVeinTool.getInstance().insertdata(parm);
	        if (data.getErrCode() < 0) {
	        	this.messageBox("E0002");
	        	onClear();
	        	init();
	        }else{
	        	this.messageBox("P0002");
	        	onClear();
	        	init();
	        }
	    }
	    /**
	     * 更新
	     */
	    public void onUpdate() {
	    	if(!this.emptyTextCheck("EXCPHATYPE_CODE,EXCPHATYPE_CHN_DESC")){
	    		return;
	    	}
	        TParm parm = getParmForTag("SEQ:int;EXCPHATYPE_CODE;EXCPHATYPE_CHN_DESC;PY1;PY2;EXCPHATYPE_ENG_DESC;DESCRIPTION;PRINT_FLG");
	        parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
	        SystemTool st=new SystemTool();
	        parm.setData("OPT_DATE",st.getDate());
//	        System.out.println("pram" + parm);
	        data = SYSSpecialVeinTool.getInstance().updatedata(parm);
	        if (data.getErrCode() < 0) {
	        	this.messageBox("E0001");
	        	onClear();
	        	init();
	            return;
	        }
	        int row = (Integer)callFunction("UI|TABLE|getSelectedRow");
	        if(row < 0){
	        	this.messageBox("E0001");
	        	onClear();
	        	init();
	        }else{
	        	this.messageBox("P0001");
	        	onClear();
	        	init();
	        }
	        //设置末行某列的值
//	        callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
//	        callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
	    }
	    /**
	     * 保存
	     */
	    public void onSave() {
	    	//System.out.println("selectRow:"+selectRow);

	    	//System.out.println("EXCPHATYPE_CODE"+this.getText("EXCPHATYPE_CODE"));
	        if(selectRow == -1)
	        {
	            onInsert();

	            return;
	        }
	        onUpdate();
	    }


	    /**
	     * 删除
	     */
	    public void onDelete() {
	        if(!this.emptyTextCheck("EXCPHATYPE_CODE")){
	    		return;
	    	}
	        String EXCPHATYPE_CODE = getText("EXCPHATYPE_CODE");
	        if(this.messageBox( "询问","确定删除",2)==0){
	        	data = SYSSpecialVeinTool.getInstance().deletedata(EXCPHATYPE_CODE);
	        }else{
	        	return ;
	        }

	        if(data.getErrCode() < 0)
	        {
	        	this.messageBox("E0003");
	        	onClear();
	        	init();
	            return;
	        }
	        int row = (Integer)callFunction("UI|TABLE|getSelectedRow");
	        if(row < 0){
	        	this.messageBox("E0003");
	        	onClear();
	        	init();
	            }
	        else{
	        	this.messageBox("P0003");
	        	onClear();
	        	init();
	        }
//	        //删除单行显示
//	        this.callFunction("UI|TABLE|removeRow",row);
//	        this.callFunction("UI|TABLE|setSelectRow",row);

	   }
	    public void onCode(){
	    	if("".equals(String.valueOf(this.getValue("EXCPHATYPE_CHN_DESC")))){
	    		return;
	    	}
	    	SystemTool st=new SystemTool();
	    	String value=st.charToCode(String.valueOf(this.getValue("EXCPHATYPE_CHN_DESC")));
	    	if(null==value||"".equals(value)){
	    		return;
	    	}
	    	this.setValue("PY1", value);
	    }


}
