package com.javahis.ui.onw;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.reg.TablePublicTool;
import jdo.sys.Operator;

/**
 * <p>急诊医生站弹出口头医嘱</p>
 * 
 * @author wangqing 20170919
 *
 */
public class ONWOrderUIControl extends TControl {
	/**
	 * 系统参数
	 */
	private TParm sysParm;

	/**
	 * 检伤号
	 */
	private String triageNo;

	private TTable orderTable;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		orderTable = (TTable) this.getComponent("TABLE_ORDER");
		orderTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			sysParm = (TParm)o;
			triageNo = sysParm.getValue("TRIAGE_NO");
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
//				TFrame frame = (TFrame) getComponent();
//				frame.setPreferredSize(new Dimension(1200,800));
//				frame.pack();	
//				triageNo = "20170921002";
				onQuery();
			}
		});
	}

	/**
	 * 查询数据
	 */
	public void onQuery(){
		TParm onwOrderParm = new TParm();
		onwOrderParm.setData("TRIAGE_NO", triageNo);
		TParm onwOrderResult = new TParm();
		onwOrderResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", onwOrderParm);
		if(onwOrderResult.getErrCode()<0){
			System.out.println("err onwOrderResult");
			return;
		}
		for(int i=0; i<onwOrderResult.getCount(); i++){
			onwOrderResult.setData("SEL_FLG", i, "N");// 选
		}
		TablePublicTool.setParmValue(orderTable, onwOrderResult);
	}

	/**
	 * 医生签字
	 */
	public void onSign(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		for(int i=0; i<orderP.getCount(); i++){
			// 医生已经签名的不能签名
			if(orderP.getValue("SIGN_DR", i) != null 
					&& orderP.getValue("SIGN_DR", i).trim().length()>0 
					&& orderP.getValue("SEL_FLG", i) != null 
					&& orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("医生已经签名的不能签名");
				return;
			}
			// 护士未签名的不能签名
			if( (orderP.getValue("SIGN_NS", i) == null || orderP.getValue("SIGN_NS", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("护士未签名的不能签名");
				return;
			}	
		}
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null 
					&& orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// 选
				TablePublicTool.modifyRow(orderTable, i, 11, "SIGN_DR", orderP.getValue("SIGN_DR", i), Operator.getID());// 医生
			}		
		}
		// 保存
		if(this.onSave1()){
			this.messageBox("签名成功！！！");
		}else{
			this.messageBox("签名失败！！！");
		}	
	}

	/**
	 * 取消签字
	 */
	public void onCancelSign(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		for(int i=0; i<orderP.getCount(); i++){
			// 医生没有签名的不能取消签字
			if( (orderP.getValue("SIGN_DR", i) == null || orderP.getValue("SIGN_DR", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("医生没有签名的不能取消签字!!!");
				return;
			}
			// 护士没有签名的不能取消签字
			if( (orderP.getValue("SIGN_NS", i) == null || orderP.getValue("SIGN_NS", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("护士没有签名的不能取消签字!!!");
				return;
			}
			// 已经开立的医嘱不能取消签名
			if( (orderP.getValue("EXE_FLG", i) != null && orderP.getValue("EXE_FLG", i).equals("Y")) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("已经开立的医嘱不能取消签名!!!");
				return;
			}	
		}
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// 选
				TablePublicTool.modifyRow(orderTable, i, 11, "SIGN_DR", orderP.getValue("SIGN_DR", i), "");// 医生		
			}			
		}
		// 保存
		if(this.onSave2()){
			this.messageBox("取消签名成功！！！");
		}else{
			this.messageBox("取消签名失败！！！");
		}
	}

	public void onCheckBoxValue(Object obj){
		TTable table = (TTable)obj;		
		table.acceptText();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
//		System.out.println("======//////row="+row);
//		System.out.println("======//////col="+col);
		TParm orderP = table.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		if(col == 0){// 选	
			if(orderP.getValue("LINK_NO", row) != null 
					&& orderP.getValue("LINK_NO", row).trim().length()>0 ){// 如果是连嘱，同组的保持一致
				for(int i=0; i<orderP.getCount(); i++){			
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(orderP.getValue("LINK_NO", row))){// 同組
						TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", i), orderP.getValue("SEL_FLG", row));// 选	
					}
				}
			}
		}
	}
	
	/**
	 * 校验数据
	 */
	public void checkData(TParm parm, String names1, String names2){
		String [] nameArr1 = names1.split(";");
		String [] nameArr2 = names2.split(";");	
		for(int i=parm.getCount()-1; i>=0; i--){
			for(int j=0; j<nameArr1.length; j++){
				if(parm.getData(nameArr1[j], i)==null){
					parm.setData(nameArr1[j], i, "");
				}
			}
			for(int k=0; k<nameArr2.length; k++){
				if(parm.getData(nameArr2[k], i)==null || parm.getData(nameArr2[k], i).toString().trim().length()==0){
					parm.removeRow(i);
					break;
				}
			}	   
		}
	}

	/**
	 * 校验数据
	 * @param parm
	 * @param names2
	 */
	public void checkData(TParm parm, String names2){
		String[] names = parm.getNames(TParm.DEFAULT_GROUP);
		StringBuffer namesStr = new StringBuffer();
		for(int i=0; i<names.length; i++){
			if(namesStr.length()>0){
				namesStr.append(";");
			}
			namesStr.append(names[i]);
		}	
		String names1 = namesStr.toString();
		this.checkData(parm, names1, names2);
	}

	/**
	 * 医生签字
	 */
	public boolean onSave1(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return false;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return false;
		}
		// 校验数据
		checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC");
		// 处理时间格式
		for(int i=0; i<orderP.getCount(); i++){
			String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
			orderP.setData("NOTE_DATE", i, noteDate);
		}
		TParm saveParm = new TParm();
		TParm result = new TParm();
		saveParm.setData("#ORDER", orderP.getData());	
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder3", saveParm);
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return false;
		}
		// 刷新数据
		this.onQuery();
		return true;
	}
	
	/**
	 * 取消医生签字
	 */
	public boolean onSave2(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return false;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return false;
		}
		// 校验数据
		checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC");
		// 处理时间格式
		for(int i=0; i<orderP.getCount(); i++){
			String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
			orderP.setData("NOTE_DATE", i, noteDate);
		}
		TParm saveParm = new TParm();
		TParm result = new TParm();
		saveParm.setData("#ORDER", orderP.getData());	
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder4", saveParm);
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return false;
		}
		// 刷新数据
		this.onQuery();
		return true;
	}

	/**
	 * 全选
	 */
	public void onAllSelect(){
		if(orderTable == null){
			return;
		}
		TParm parm = orderTable.getParmValue();
		if(parm == null){
			return;
		}
		Map map = orderTable.getLockCellMap();	
		for(int i=0; i<parm.getCount(); i++){
			if(map != null && map.get(i+":0") != null && (Boolean) map.get(i+":0")){
				continue;
			}
			TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", parm.getBoolean("SEL_FLG", i), !parm.getBoolean("SEL_FLG", i));
		}
	}
	
}
