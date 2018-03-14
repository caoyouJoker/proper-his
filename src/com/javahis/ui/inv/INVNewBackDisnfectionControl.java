package com.javahis.ui.inv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.manager.INVPackOberver;
import com.javahis.util.StringUtil;

/**f
 * 
 * <p>
 * Title:清洗,消毒,回收
 * </p>
 * 
 * <p>
 * Description: 清洗,消毒,回收
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
 * @author wangming 2013-6-19
 * @version 1.0
 */
public class INVNewBackDisnfectionControl extends TControl {
	
	private TTable table_packm;		//手术包主表

	private TTable table_dis;		//回收单显示表

	private TTable tableD;			//包内明细表     

	private String recycleNo;		//初始化回收单号
	
	private boolean isNew = false;	//是否是新建回收单标记  	true：是		false：不是

	private Map detailInfo = new HashMap();		//临时存放物资折损信息
	//在库状态不进行操作标记
	boolean flg ;
	//公共parm(查询基本信息)
	private TParm Publicresult ;
	/**
	 * 初始化
	 */
	public void onInit() {
		// 回收记录table 
		table_dis = (TTable) getComponent("TABLE_DIS");
		// 物资入库主表
		table_packm = (TTable) getComponent("TABLE_PACKM");
		// 物资入库明细
		tableD = (TTable) getComponent("TABLED");
		// tableD值改变事件
		this.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableDChangeValue");
		//table_dis中checkbox值发生改变事件
		table_dis.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBoxClicked");

		//弹出手术包类型选择窗口
		TParm parm = new TParm();
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",  
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // 定义接受返回值方法  
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");
		this.onInitDate(); 
		//设置默认科室
		TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
		
	}

	/**
	 * 手术包选择返回数据处理
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_DESC").setValue(pack_desc);
        this.grabFocus("PACK_SEQ_NO");	//定位焦点
	}
	
	/**
	 * 新增回收单方法
	 * */
	public void onNew(){
		
		if(isNew){
			messageBox("不能同时新建两个回收单！");
			return;
		}
		
		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("供应室部门不能为空！");
			return;
		}
		
		isNew = true;				//回收单新建状态
		this.clearTable(table_dis);
		this.clearTable(table_packm);
		this.clearTable(tableD);
		
		recycleNo = this.getRecycleNo();
		this.createNewDisinfection();    //新增一条回收单记录
		
	}
	
	/**
	 * 向回收单table新添一条回收单记录(扫码后向table_packm添加诊疗包(手术包)数据)
	 */
	private void createNewDisinfection(){
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("RECYCLE_NO",0, recycleNo);
		parm.setData("OPT_DATE",0,date.toString().substring(0,19));
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		
		
		table_dis.setParmValue(parm);
		table_dis.setSelectedRow(0);
	}
	
	/**
	 * 查看图片
	 * wangjingchun add 20150226
	 */
	public void onPicture(){

		TTable table = (TTable) this.getComponent("TABLE_PACKM");
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择手术包");
			return;
		}
		String packCode = table.getShowParmValue().getValue("PACK_CODE", row);
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		String photoName = parm.getValue("PACK_CODE")+ ".jpg";
		String root = TIOM_FileServer.getRoot();
		String dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
		dir = root + dir ;
		byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				dir + photoName);
		if(data == null){
			this.messageBox("此手术包未上传图片");
			return;
		}
		String pic_dir = "C:\\JavaHisFile\\OpePic\\";
		try {
			  File file = new File(pic_dir);
			  if(!file.exists()){
				  file.mkdir();
			  }
		      // 源文件  
		      FileOutputStream fileOutputStream = new FileOutputStream(new File(pic_dir+photoName));  
		      fileOutputStream.write(data);  
		      // 关闭流  
		      fileOutputStream.close();  
		      Runtime.getRuntime().exec("rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen "+pic_dir+photoName);
		  } catch (FileNotFoundException e) {  
		      e.printStackTrace();  
		  } catch (IOException e) {  
		      // TODO Auto-generated catch block  
		      e.printStackTrace();
		  }
	}
	
	/**
	 * 扫描条码
	 */
	public void onScream() {
		int r = table_dis.getSelectedRow();
		if(r<0){
//			messageBox("请选择回收单！");
//			return ;
			onNew();//20150130 wangjingchun add
		}
		
		if(isNew){
			String packageCode = getValueString("SCREAM");
			if (packageCode == null || packageCode.length() == 0) {
				return;
			}
//			setValue("PACK_CODE", packageCode.substring(0, packageCode.length() - 4));
//			setValue("PACK_SEQ_NO",
//					packageCode.substring((packageCode.length() - 4), packageCode.length()));
			this.addPackage();	//向回收单中添加手术包
			this.grabFocus("SCREAM");	//定位焦点
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * 向回收单中添加手术包方法
	 */
	public void addPackage(){
		
		int r = table_dis.getSelectedRow();
		if(r<0){
//			messageBox("请选择回收单！");
//			return ;
			onNew();//20150130 wangjingchun add
		}
		//20150130 wangjingchun 注释掉
//		boolean tag = false;
//		tag = this.checkConditions();
//		if(!tag){		//输入条件不完整
//			return;   
//		}    
		TParm parm = new TParm();  
		//出库状态是1
		parm.setData("BARCODE", this.getValueString("SCREAM"));
		parm.setData("STATUS",  "1");             
		for (int i = 0; i < table_packm.getRowCount(); i++) {
			String barcodeOle = table_packm.getShowParmValue().getValue("BARCODE", i);
			if(this.getValueString("SCREAM").equals(barcodeOle)){
				this.messageBox("此条码已在本次回收");  
				return;
			}
		}  
		      
//		parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
//		parm.setData("PACK_SEQ_NO", this.getValueString("PACK_SEQ_NO"));
		TParm Publicresult = new TParm(); 
		Publicresult = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);	//查询手术包信息（根据条码）
		if(Publicresult.getCount()<=0){
			return;
		}
		  
		if(Publicresult.getData("STATUS",0).equals("4")){ 
			messageBox("手术包已处于维修状态，无法回收！");
			return; 
		} 
		
		if(Publicresult.getData("STATUS",0).equals("3")){ 
			messageBox("手术包已处于已打包状态，无法回收！");
			return;  
		}  
//		if(result.getData("STATUS",0).equals("3")){
//			messageBox("手术包已处于灭菌状态，无法回收！");
//			return;
//		}
//		if(result.getData("STATUS",0).equals("2")){
//			messageBox("手术包已处于回收状态！");
//			return;
//		}
		if(Publicresult.getData("STATUS",0).equals("0")){
			if(this.messageBox("提示信息", "手术包处于在库状态，是否再次消毒？", this.YES_NO_OPTION)==1){
				return;
			}
		}
		 
		Timestamp date = SystemTool.getInstance().getDate();
		
		int valid = Integer.parseInt(Publicresult.getData("VALUE_DATE",0).toString()); 	//有效期限   一般是“天”单位  
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
        cal.add(cal.DATE,valid);//把日期往后增加N天(效期的天数)
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(d);

		TParm rowRecord = new TParm();
		rowRecord.setData("PACK_DESC",Publicresult.getData("PACK_DESC",0));
		rowRecord.setData("PACK_CODE",Publicresult.getData("PACK_CODE",0));
		rowRecord.setData("PACK_SEQ_NO",Publicresult.getData("PACK_SEQ_NO",0));
		rowRecord.setData("QTY",Publicresult.getData("QTY",0)); 
		//fux modify 标记更改
		rowRecord.setData("STATUS",Publicresult.getData("STATUS",0));
		rowRecord.setData("RECYCLE_DATE",date.toString().substring(0,19));
		rowRecord.setData("RECYCLE_USER",Operator.getID());  
		rowRecord.setData("WASH_DATE",date.toString().substring(0,19));
		rowRecord.setData("WASH_USER",Operator.getID());
		rowRecord.setData("DISINFECTION_DATE",date.toString().substring(0,19));
		rowRecord.setData("DISINFECTION_VALID_DATE",dateString);
		rowRecord.setData("DISINFECTION_USER",Operator.getID());   
		//fux modify 加入锅号
		rowRecord.setData("DISINFECTION_POTSEQ",this.getValueString("DISINFECTION_POTSEQ"));
		rowRecord.setData("DISINFECTION_PROGRAM",this.getValueString("DISINFECTION_PROGRAM"));
		rowRecord.setData("DISINFECTION_NUM",this.getValueString("DISINFECTION_NUM"));
		rowRecord.setData("BARCODE",Publicresult.getData("BARCODE",0));
		//fux modify 20140425
		//rowRecord.setData("USED_NUMBLE", Publicresult.getData("USED_NUMBLE",0));  
		System.out.println("rowRecord:"+rowRecord);
		table_packm.addRow(rowRecord);
		this.grabFocus("PACK_SEQ_NO");	//定位焦点  
	}
	 
	/**
	 * 主表点击事件
	 */
	public void onTableMClicked() {
		int row = table_packm.getSelectedRow();
		String packCode = table_packm.getShowParmValue().getValue("PACK_CODE", row);
		String packSeqNo = table_packm.getShowParmValue().getValue("PACK_SEQ_NO", row);
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		parm.setData("PACK_SEQ_NO", packSeqNo); 
		
		TParm result = new TParm();
		result = INVNewBackDisnfectionTool.getInstance().queryPackageDetailInfo(parm);
		if(result.getCount()>0&&detailInfo.size()>0){
			for(int i=0;i<result.getCount();i++){
				String tPackCode = result.getRow(i).getValue("PACK_CODE");
				String tPackSeqNo = result.getRow(i).getValue("PACK_SEQ_NO");
				String tInvCode = result.getRow(i).getValue("INV_CODE");
				String tInvSeqNo = result.getRow(i).getValue("INVSEQ_NO");
				
				String key = tPackCode + "-" + tPackSeqNo + "-" + tInvCode + "-" + tInvSeqNo;
				
				Object obj = detailInfo.get(key);
				if(obj!=null){ 
					result.setDataN("RECOUNT_TIME", i, obj);
				}
			}
		}
		tableD.setParmValue(result);
	}
	
	/**
	 * TableDis表点击事件
	 */
	public void onTableDisClicked(){  
		if(!isNew){    
			int row = table_dis.getSelectedRow();
			String recycleNo = "";  
			if(row>=0){
				recycleNo = table_dis.getItemData(row, "RECYCLE_NO").toString();   
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("RECYCLE_NO", recycleNo); 
			//查询INV_DISINFECTION 与INV_PACKM关联
			TParm result = INVNewBackDisnfectionTool.getInstance().queryDisnfectionByNo(parm); 
//			System.out.println("TableDis表点击事件---result"+result);
			if(result.getCount()>0){
				for(int i=0;i<result.getCount();i++){
					result.setData("STATUS", i, "回收");
				}
			}
			table_packm.removeRowAll();
			table_packm.setParmValue(result);
			tableD.removeRowAll();
		}
	}
	
	
	/**
	 * 细表值改变事件
	 */
	public void onTableDChangeValue(Object obj) {

		// 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null){
            return;
        }
        if (node.getValue().equals(node.getOldValue())){
        	return;
        }
		if(!isNew){	//如果不是新建回收单进入 		StringTool.fillLeft(" dsfsfd ", 4, "0");

		}else{
			int row = tableD.getSelectedRow();
			
			String packCode = tableD.getParmValue().getValue("PACK_CODE", row);
			String packSeqNo = tableD.getParmValue().getValue("PACK_SEQ_NO", row);
			String invCode = tableD.getParmValue().getValue("INV_CODE",row);
			String invSeqNo = tableD.getParmValue().getValue("INVSEQ_NO",row);

			String recountTime = node.getValue().toString();
			String key = packCode + "-" + packSeqNo + "-" + invCode + "-" + invSeqNo;
			
			this.fillDetailMap(key, recountTime);
		}
		
	}
	
	//记录折损次数
	private void fillDetailMap(String key,String value){
		
		if(detailInfo.size()>0){
			Object obj = "";
			obj = detailInfo.get(key);
			if(obj!=null){ 
				detailInfo.remove(key);
				detailInfo.put(key, value);
			}else{
				detailInfo.put(key, value);
			}
		}else{ 
			detailInfo.put(key, value);   
		}
		
	}
	
	
	public void onSave(){
		if(isNew){
			//fux modify 20140324 低值耐用品 回补库存
			TParm parm = new TParm();  
			
			TParm disTable = table_dis.getShowParmValue();
			TParm packageMTable = table_packm.getShowParmValue();
//			System.out.println("disTable:"+disTable);
//			System.out.println("packageMTable:"+packageMTable); 
			if(packageMTable.getCount() == 0){
				messageBox("回收单明细项不能为空！");   
				return;
			}
			//20150130 wangjingchun add start
			for(int j=0;j<packageMTable.getCount("PACK_CODE");j++){
				if(packageMTable.getValue("DISINFECTION_NUM",j).equals("")){
					this.messageBox("请填写锅号！");
					return;
				}
				if(packageMTable.getValue("DISINFECTION_POTSEQ",j).equals("")){
					this.messageBox("请填写锅次！");
					return;
				}
				if(packageMTable.getValue("DISINFECTION_PROGRAM",j).equals("")){
					this.messageBox("请填写程序！");
					return;
				}
			}
			//20150130 wangjingchun add end
			  
			disTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("RECYCLE_USER", i, Operator.getID());
				packageMTable.setData("WASH_USER", i, Operator.getID());
				packageMTable.setData("DISINFECTION_USER", i, Operator.getID());
			}
			
			parm.setData("DISNFECTIONTABLE", disTable.getData());
			parm.setData("PACKAGEMAINTABLE", packageMTable.getData());
			parm.setData("RECOUNTTIME",detailInfo); 
			TParm result = TIOM_AppServer.executeAction("action.inv.INVNewBackDisnfectionAction",
		            "onInsert", parm);
			if (result.getErrCode() < 0) {  
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            messageBox("保存失败！");
	        }else{
	        	messageBox("保存成功！");
	        }
			
			isNew = false;
			this.onClear();	//清空界面，标记赋初始值
		}
	}
	//删除回收单
	public void onDelete(){
		if(isNew){	//新建状态时进入
			TParm disTable = table_dis.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//判断如果不是删除回收表，是否是删除手术包信息
				int row = table_packm.getSelectedRow();
				if(row>=0){
					table_packm.removeRow(row);
					clearTable(tableD);
					return;
				}
				messageBox("请选择回收单！");
				return;
			}else{//如果是删除回收表
				this.onClear();
			}
		}else{	//非新建状态时进入
			
		}
	}
	
	/**
	 * 查询方法
	 */
	public void onQuery() {
		
		if(isNew){
			int count = table_packm.getRowCount();
			if(count>0){
				if(this.messageBox("提示信息", "新建回收单信息尚未保存，是否保存？", this.YES_NO_OPTION)==0){
					//保存
					this.onSave();
				}else{
					this.onClear();
				}
			}
		}

		String startDate = getValueString("START_RECYCLE_DATE");	//起始时间
		String endDate = getValueString("END_RECYCLE_DATE");		//终止时间

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("请输入起止时间!");
			return;
		}

		TParm parm = new TParm();			//查询条件
		if(!getValueString("SEL_RECYCLE_NO").equals("")){
			parm.setData("RECYCLE_NO", getValueString("SEL_RECYCLE_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewBackDisnfectionTool.getInstance().queryBackDisnfection(parm);
		
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		table_dis.setParmValue(result);
	}
	
	/** 
	 * 生成回收单号 
	 *  */
	private String getRecycleNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_DISINFECTION", "No");
		return recyleNo;
	}

	/**
	 * 回收单表中复选框值改变事件
	 */
	public void onCheckBoxClicked(Object obj){
		TTable table = (TTable) obj;
		int row = table.getSelectedRow();
		String tag = table.getItemString(row, "DELETE_FLG");
		
		if(tag.equals("Y")){
			table.setItem(row, "DELETE_FLG", "N");
		}else{
			table.setItem(row, "DELETE_FLG", "Y");
		}
		
	}
	
	/**
	 * 清空方法
	 */
	public void onClear() {
		
		// 清空属性
		clearText();
		// 清空主表
		clearTable(table_dis);
		// 清空手术报
		clearTable(table_packm);
		// 清空明细表
		clearTable(tableD);
		isNew = false;
		detailInfo = new HashMap();
	}
	/**
	 * 清空控件值
	 */
	private void clearText() { 
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM;DISINFECTION_NUM");
	}
	/**
	 *	验证输入条件 
	 */
	private boolean checkConditions(){
		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("请输入供应室部门！");
			return false;
		}
//		if (this.getValueString("PACK_CODE").length() == 0) {
//			messageBox("请输入手术包号！");
//			return false;
//		}
//		if (this.getValueString("PACK_SEQ_NO").length() == 0) {
//			messageBox("请输入手术包序号！");
//			return false;
//		}
		//fux modify 加入锅号
		if (this.getValueString("DISINFECTION_NUM").length() == 0) {
			messageBox("请输入锅号！");
			return false;
		}
		if (this.getValueString("DISINFECTION_POTSEQ").length() == 0) {
			messageBox("请输入锅次！");
			return false;
		}
		if (this.getValueString("DISINFECTION_PROGRAM").length() == 0) {
			messageBox("请输入程序！");
			return false;
		}
		return true;
	}
	/**清空table**/
	private void clearTable(TTable table) {
		table.removeRowAll();
	}
	/** 初始化日期控件数据 **/
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", date);
	}
	/** 获得TTextField类型控件 **/
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
	/**
	 * 打印条码
	 */
	public void onBarcode() {
		TParm parm = new TParm();
		int row = table_packm.getSelectedRow();
		if (row < 0){
			return;
		}
		String packCode = table_packm.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("手术包选择错误!");
			return;
		}
		// 手术包序号
		String strPackSeqNo = table_packm.getItemString(row, "PACK_SEQ_NO");
		String packCodeSeq = strPackSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;

		int recycleRow = table_dis.getSelectedRow();
		if(recycleRow<0){
			return;
		}
		String recycleNo = table_dis.getItemString(recycleRow, "RECYCLE_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE", packCode);
		conditions.setData("PACK_SEQ_NO", Integer.parseInt(strPackSeqNo));
		conditions.setData("RECYCLE_NO", recycleNo);
		
		//查询条码所需信息
		TParm result = INVNewBackDisnfectionTool.getInstance().queryBarcodeInfo(conditions);
		
		// 消毒日期    失效日期	
		String disinfectionDate = StringTool.getString((Timestamp)result.getData("DISINFECTION_DATE",0),"yyyy/MM/dd");
		String disinfectionValidDate = StringTool.getString((Timestamp)result.getData("DISINFECTION_VALID_DATE",0),"yyyy/MM/dd");
		
		
		TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", packCode + packCodeSeq);
        reportParm.setData("PACK_DESC","TEXT",table_packm.getItemString(row, "PACK_DESC"));
        reportParm.setData("PACK_DEPT","TEXT","(" + result.getData("ORG_DESC",0) + ")");
        //fux modify 加入锅号
        reportParm.setData("POTSEQ","TEXT",table_packm.getItemString(row, "DISINFECTION_POTSEQ"));
        reportParm.setData("PROGRAM","TEXT",table_packm.getItemString(row, "DISINFECTION_PROGRAM"));
        reportParm.setData("NUM","TEXT",table_packm.getItemString(row, "DISINFECTION_NUM")); 
        reportParm.setData("DISNFECTION_DATE","TEXT",disinfectionDate);
        reportParm.setData("VALUE_DATE","TEXT",disinfectionValidDate);
        reportParm.setData("OPT_USER","TEXT",result.getData("USER_NAME",0));

		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewDisinfectionBarcode.jhw", reportParm);
	}
	
	/**
	 * 打印回收单
	 */
	public void onPrint(){
		
		int row = table_dis.getSelectedRow();
		if (row < 0){
			messageBox("请选择回收单！");
			return;
		}
		String recycleNo = table_dis.getItemString(row, "RECYCLE_NO");
		String recycleOrgCode = table_dis.getItemString(row, "ORG_CODE");
		String recycleOptUser = table_dis.getItemString(row, "OPT_USER");
		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", recycleOrgCode);
		
		//获得部门名称
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", recycleOptUser);
		
		//获得用户名称
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
		//开单日期
		String optDate = table_dis.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("RECYCLENO", "TEXT", recycleNo);	//回收单号
		reportTParm.setData("RECYCLEDEPT", "TEXT", deptInfo.getData("ORG_DESC",0));	//回收部门名称
		reportTParm.setData("RECYCLEDATE", "TEXT", optDate);	//开单日期
		
		//表格数据
        TParm tableParm = new TParm();
		for(int i=0;i<table_packm.getRowCount();i++){
			tableParm.addData("PACK_DESC", table_packm.getItemString(i, "PACK_DESC"));
			
			String packCode = table_packm.getItemString(i, "PACK_CODE");
			String strPackSeqNo = table_packm.getItemString(i, "PACK_SEQ_NO");
			String packCodeSeq = strPackSeqNo;
			for (int m = packCodeSeq.length(); m < 4; m++) {
				packCodeSeq = "0" + packCodeSeq;
			}
			packCodeSeq = "" + packCodeSeq;
			
			
			tableParm.addData("PACKAGENNO", packCode+packCodeSeq);
			tableParm.addData("RECYCLE_DATE", table_packm.getItemString(i, "RECYCLE_DATE").substring(0,10));
			tableParm.addData("DISINFECTION_DATE", table_packm.getItemString(i, "DISINFECTION_DATE").substring(0, 10));
			tableParm.addData("DISINFECTION_VALID_DATE", table_packm.getItemString(i, "DISINFECTION_VALID_DATE").substring(0, 10));
			//fux modify 加入锅号
			tableParm.addData("DISINFECTION_POTSEQ", table_packm.getItemString(i, "DISINFECTION_POTSEQ"));
			tableParm.addData("DISINFECTION_PROGRAM", table_packm.getItemString(i, "DISINFECTION_PROGRAM"));
			tableParm.addData("DISINFECTION_NUM", table_packm.getItemString(i, "DISINFECTION_NUM"));
		}
		
		
		tableParm.setCount(tableParm.getCount("PACK_DESC"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACKAGENNO");
	    tableParm.addData("SYSTEM", "COLUMNS", "RECYCLE_DATE");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_DATE");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_VALID_DATE");
	    //fux modify 加入锅号
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_POTSEQ");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_PROGRAM");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_NUM"); 
	    
	    reportTParm.setData("TABLE", tableParm.getData());
		
	    Timestamp date = SystemTool.getInstance().getDate();
	        
	    reportTParm.setData("OPTUSER", "TEXT", Operator.getName());
	    reportTParm.setData("OPTDATE", "TEXT", date.toString().substring(0, 10));
		
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDisinfection.jhw",
				reportTParm);
		                       
	}

}
