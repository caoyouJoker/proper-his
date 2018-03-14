package com.javahis.ui.med;

import java.util.Date;
import jdo.med.MEDLISDICUITool;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
/**
 * <p> Title:项目编码与检验编码对应 </p>
 * 
 * <p> Description: 项目编码与检验编码对应 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 *@author 2014.03.10
 * @version 1.0
 */
public class MEDLISDICUIControl extends TControl {
	
	TTable table;
	public void onInit() {
		table=this.getTable("TABLE");
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
        		+ TTableEvent.CLICKED, this, "onTABLEClicked");
		onQueryAll();
		
	}

	/**
	 * 单击事件
	 */
	
 public void onTABLEClicked(int row) {
		 if(row<0){
			 return;
		 }
		 TParm parm=table.getParmValue().getRow(row);
		 callFunction("UI|MAP_ID|setEnabled", false);//
		 this.setValue("MAP_ID", parm.getValue("MAP_ID"));
		 this.setValue("MAP_DESC", parm.getValue("MAP_DESC"));
		 this.setValue("SEQ", parm.getValue("SEQ"));
		 this.setValue("LIS_ID", parm.getValue("LIS_ID"));
		 this.setValue("LIS_DESC", parm.getValue("LIS_DESC"));
		 this.setValue("TYPE", parm.getValue("TYPE"));
		 this.setValue("MAP_TYPE", parm.getValue("MAP_TYPE"));
		 this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION"));
		 this.setValue("PY1", parm.getValue("PY1"));
		 
		 
	    }
	 
	 
	/**
	 * 保存
	 */
	public void onSave() {
		if("".equals(this.getValueString("MAP_ID"))){
			this.messageBox("项目编码不能为空");
			return;
		}
		
		if("".equals(this.getValueString("MAP_DESC"))){
			this.messageBox("项目名称不能为空");
			return;
		}
		
			String upmess="";
			String errmess="";
			TParm parm=new TParm();
			TParm result=new TParm();
			parm.setData("MAP_ID", this.getValueString("MAP_ID"));
			parm.setData("MAP_DESC", this.getValueString("MAP_DESC"));
			parm.setData("SEQ", this.getValueString("SEQ"));
			parm.setData("LIS_ID", this.getValueString("LIS_ID"));
			parm.setData("LIS_DESC", this.getValueString("LIS_DESC"));
			parm.setData("TYPE", this.getValueString("TYPE"));
			parm.setData("MAP_TYPE", this.getValueString("MAP_TYPE"));
			parm.setData("PY1", this.getValueString("PY1"));
			parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			parm.setData("OPT_TERM", Operator.getID());
			result=MEDLISDICUITool.getInstance().selectData(parm);
			if(result.getCount()>0){//如果存在修改
				result=MEDLISDICUITool.getInstance().updateData(parm);
				 upmess="修改成功";
				 errmess="修改失败";
			}else{//不存在添加
				result=MEDLISDICUITool.getInstance().insertData(parm);
				 upmess="保存成功";
				 errmess="保存失败";
			}
			
		
		if (result.getErrCode() < 0) {
			this.messageBox(errmess);
			onQuery();
			return;
		}
		this.messageBox(upmess);
		onQueryAll();
	}
	
	/**
	 * 名称回车事件
	 */
	public void onUserNameAction() {
		String userName = getValueString("MAP_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	/**
	 * 删除
	 */
	public void onDelete() {
		TParm parm=new TParm();
		TParm parma=new TParm();
		TParm result=new TParm();
			 parm=table.getParmValue().getRow(table.getSelectedRow());
			 parma.setData("MAP_ID",parm.getValue("MAP_ID"));
			 result=MEDLISDICUITool.getInstance().deleteData(parma);
			
			if (result.getErrCode() < 0) {
				this.messageBox("删除失败");
				onQuery();
				return;
			}
			this.messageBox("删除成功");
			table.removeRow(table.getSelectedRow());
			onClear();
		    onQueryAll();
	}

	

	/**
	 * 查询
	 */
	public void onQueryAll() {
		
		TParm parm = MEDLISDICUITool.getInstance().selectDataAll();
		this.table.setParmValue(parm);
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm parm =new TParm();
			parm.setData("MAP_ID",this.getValueString("MAP_ID"));
			parm.setData("MAP_DESC",this.getValueString("MAP_DESC"));
			
		TParm result = MEDLISDICUITool.getInstance().selectDataid(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("查询错误");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询数据");
			return;
		}
		this.table.setParmValue(result);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("MAP_ID;MAP_DESC;SEQ;LIS_ID;LIS_DESC;TYPE;MAP_TYPE;DESCRIPTION;PY1");
		table.removeRowAll();
		 callFunction("UI|MAP_ID|setEnabled", true);//
		 onQueryAll();
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	

}
