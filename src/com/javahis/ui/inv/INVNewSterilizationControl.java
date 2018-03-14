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

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;


/**
 * INV_PACKSTOCKM
 * <p>
 * Title:灭菌
 * </p>
 * 
 * <p>
 * Description: 灭菌
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
 * @author wangming 2013-7-1
 * @version 1.0  
 */
public class INVNewSterilizationControl extends TControl{

	private TTable tableSter;			//灭菌单表
	
	private TTable tablePackM;			//手术包主表
	
	private TTable tablePackD;			//手术包明细
	
	private String sterilizationNo;		//初始化灭菌单号
	
	private boolean isImport = false;		//是否是导入打包单标记  	true：是		false：不是
	
	private boolean isNew = false;			//是否是新建灭菌单	true：是		false：不是
	
	private String repackNo;			//传回的打包单号
	
	
	/**
	 * 初始化
	 */
	public void onInit() {
	
		tableSter = (TTable) getComponent("TABLE_STER");
		tablePackM = (TTable) getComponent("TABLE_PACKM");
		tablePackD = (TTable) getComponent("TABLED");
		
		this.onInitDate();
		
		tableSter.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBoxClicked");
		
		//弹出手术包类型选择窗口
		TParm parm = new TParm();
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // 定义接受返回值方法
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");
		
		//设置默认科室
		TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
        //20150107 wangjingchun add start
        //设置默认审核人员
        TTextFormat audit_user = (TTextFormat) this.getComponent("AUDIT_USER");
        audit_user.setValue(Operator.getID());
        //20150107 wangjingchun add end

	}
	
	
	
	/**
	 * 扫描条码
	 */
	public void onScream() {
		
		if(isImport){
			messageBox("引入回收单状态下不能添加手术包！");
			return ;
		}
		
		int r = tableSter.getSelectedRow();
		if(r<0){
			messageBox("请选择灭菌单！");
			return ;
		}
		
		if(isNew){
			String packageCode = getValueString("SCREAM");
			if (packageCode == null || packageCode.length() == 0) {
				return;
			}
//			setValue("PACK_CODE", packageCode.substring(0, packageCode.length() - 4));
//			setValue("PACK_SEQ_NO",
//					packageCode.substring((packageCode.length() - 4), packageCode.length()));
			
			this.addPackage();	//向灭菌单中添加手术包
			this.grabFocus("SCREAM");	//定位焦点
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * 向灭菌单中添加手术包方法
	 */
	public void addPackage(){
		 
		int r = tableSter.getSelectedRow();
		if(r<0){
			messageBox("请选择灭菌单！");
			return ;
		} 
		    
		boolean tag = false;
		tag = this.checkConditions();
		if(!tag){		//输入条件不完整
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("BARCODE", this.getValueString("SCREAM"));
		//在库时是0
		parm.setData("STATUS",  "0");  
//		parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
//		parm.setData("PACK_SEQ_NO", this.getValueString("PACK_SEQ_NO"));
		TParm result = new TParm();  
		result = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);	//查询手术包信息（根据条码）
		if(result.getCount()<=0){
			return;
		}
		if(result.getData("STATUS",0).equals("5")){
			messageBox("手术包已处于维修状态，无法灭菌！");
			return;
		}
		//20150305 wangjingchun add start
		//检查包装材料与锅号是否对应
		String sql = "SELECT B.ID FROM INV_PACKM A,SYS_DICTIONARY B "
						+" WHERE B.GROUP_ID = 'BZCL' "
						+" AND A.PACK_CODE = '"+result.getValue("PACK_CODE",0)
						+"' AND A.BAG_MATERIAL = B.ID";
		TParm bag_parm = new TParm(TJDODBTool.getInstance().select(sql));
		String sql1 = "SELECT HL_FLG FROM INV_POTSEQ WHERE POTSEQ = '"+this.getValue("STERILIZATION_NUM")+"'";
		TParm potseq_parm = new TParm(TJDODBTool.getInstance().select(sql1));
		if(potseq_parm.getValue("HL_FLG", 0).equals("H")){
			if(bag_parm.getValue("ID",0).equals("4")){
				this.messageBox("请选择低温锅号");
				return;
			}
		}
		if(potseq_parm.getValue("HL_FLG", 0).equals("L")){
			if(bag_parm.getValue("ID",0).equals("1")||
					bag_parm.getValue("ID",0).equals("2")||
					bag_parm.getValue("ID",0).equals("3")){
				this.messageBox("请选择高温锅号");
				return;
			}
		}
		//20150305 wangjingchun add end
//		if(result.getData("STATUS",0).equals("3")){
//			messageBox("手术包已处于灭菌状态！");
//			return;
//		}
//		if(result.getData("STATUS",0).equals("2")){
//			messageBox("手术包已处于回收状态！");
//			return;
//		}
		//fux modify 20140401 在库状态才可以灭菌
//		if(result.getData("STATUS",0).equals("0")){
//			if(this.messageBox("提示信息", "手术包处于在库状态，是否再次消毒？", this.YES_NO_OPTION)==1){
//				return;
//			}
//		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString()); 	//有效期限   一般是“天”单位  
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
        cal.add(cal.DATE,valid);//把日期往后增加N天
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(d);

        
        TParm tp = new TParm();
		tp = tablePackM.getParmValue();
		int row = -1;
		if(tp == null){
			tp = new TParm();
			row = 0;
		}else{
			row = tp.getCount();
		}
//		tp.setData("BARCODE", row, result.getData("BARCODE",0));
//		tp.setData("PACK_DESC", row, result.getData("PACK_DESC",0));
//		tp.setData("PACK_CODE", row, result.getData("PACK_CODE",0));
//		tp.setData("PACK_SEQ_NO", row, result.getData("PACK_SEQ_NO",0));
//		tp.setData("QTY", row, result.getData("QTY",0));
//		tp.setData("STATUS", row, "灭菌");  
//		tp.setData("AUDIT_DATE", row, date.toString().substring(0,19));
//		tp.setData("AUDIT_USER", row, this.getValueString("AUDIT_USER"));
//		tp.setData("STERILIZATION_POTSEQ", row, this.getValueString("STERILIZATION_POTSEQ"));
//		tp.setData("STERILLZATION_PROGRAM", row, this.getValueString("STERILIZATION_PROGRAM"));
//		tp.setData("STERILLZATION_DATE", row, date.toString().substring(0,19));
//		tp.setData("STERILLZATION_USER", row, Operator.getID());
		//tablePackM.setParmValue(tp)
		tp.setData("BARCODE", result.getData("BARCODE",0));
		tp.setData("PACK_DESC", result.getData("PACK_DESC",0));
		tp.setData("PACK_CODE", result.getData("PACK_CODE",0));
		tp.setData("PACK_SEQ_NO", result.getData("PACK_SEQ_NO",0));
		tp.setData("QTY", result.getData("QTY",0));
		tp.setData("STATUS", "灭菌");    
		tp.setData("AUDIT_DATE", date.toString().substring(0,19)); 
		tp.setData("AUDIT_USER", this.getValueString("AUDIT_USER"));
		tp.setData("STERILIZATION_POTSEQ", this.getValueString("STERILIZATION_POTSEQ"));
		tp.setData("STERILLZATION_PROGRAM", this.getValueString("STERILIZATION_PROGRAM"));
		//fux modify 加入锅号       
		tp.setData("STERILIZATION_NUM", this.getValueString("STERILIZATION_NUM"));
		tp.setData("STERILLZATION_DATE", date.toString().substring(0,19)); 
		tp.setData("STERILLZATION_USER", Operator.getID());
//		System.out.println("tp===>"+tp);
		tablePackM.addRow(tp);
//		TParm rowRecord = new TParm();
//		rowRecord.setData("PACK_DESC",result.getData("PACK_DESC",0));
//		rowRecord.setData("PACK_CODE",result.getData("PACK_CODE",0));
//		rowRecord.setData("PACK_SEQ_NO",result.getData("PACK_SEQ_NO",0));
//		rowRecord.setData("QTY",result.getData("QTY",0));
//		rowRecord.setData("STATUS","灭菌");
//		rowRecord.setData("STERILLZATION_DATE",date.toString().substring(0,19));
//		rowRecord.setData("STERILLZATION_USER",Operator.getID());
////		rowRecord.setData("PACK_DATE",date.toString().substring(0,19));
////		rowRecord.setData("PACK_USER",Operator.getID());
//		rowRecord.setData("AUDIT_DATE",date.toString().substring(0,19));
//		rowRecord.setData("AUDIT_USER",this.getValueString("AUDIT_USER"));
//		rowRecord.setData("STERILIZATION_POTSEQ",this.getValueString("STERILIZATION_POTSEQ"));
//		rowRecord.setData("STERILLZATION_PROGRAM",this.getValueString("STERILIZATION_PROGRAM"));
//		rowRecord.setData("BARCODE", result.getData("BARCODE",0));
		
		this.grabFocus("PACK_SEQ_NO");	//定位焦点
	}
	
	/**
	 * 新增回收单方法
	 * */
	public void onNew(){
		
		if(isImport||isNew){
			messageBox("不能同时新建两个灭菌单！");
			return;
		}

		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("供应室部门不能为空！");
			return;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("审核人员不能为空！");
			return;
		} 
		  
		isNew = true;				//灭菌单新建状态
		this.clearTable(tableSter);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		
		sterilizationNo = this.getSterilizationNo();
		this.createNewSterilization();    //新增一条回收单记录
		
	}
	
	public void onSave(){
		
		if(isImport){	//引入回收单
			boolean tag = true;	//tag为true时说明所有手术包打包成功
			
			TParm sterTable = tableSter.getShowParmValue();
			TParm packageMTable = tablePackM.getParmValue();
			
			for(int i=0;i<packageMTable.getCount();i++){
				//fux modify 加入锅号
				String potseq = packageMTable.getData("STERILIZATION_POTSEQ", i).toString();
				String program = packageMTable.getData("STERILLZATION_PROGRAM", i).toString();
				String num = packageMTable.getData("STERILIZATION_NUM", i).toString();
				if(potseq == null || potseq.length() == 0 ){
					messageBox("锅次信息输入不完整！");
					return;
				}
				if( program == null ||  program.length() == 0){
					messageBox("程序信息输入不完整！");
					return;
				}
				if(num == null || num.length() == 0 ){
					messageBox("锅号信息输入不完整！");
					return;  
				}
			}
			
			sterTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("STERILLZATION_USER", i, Operator.getID());
				packageMTable.setData("PACK_USER", i, Operator.getID());
			}
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount();i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("REPACK_NO", repackNo);
				
				parm.setData("STERILIZATIONTABLE", sterTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("REPACK_NO", temp.getData());
				
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewSterilizationAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					tag = false;
					messageBox("保存失败！");
				}
//				if(result.getData("ENOUGH").equals("NO")){
//					tag = false;
//				}
			}
			if(tag){
				messageBox("保存成功！");
			}else{
				messageBox("保存失败！");
			}
//暂时			if(!tag){
//				messageBox("有部分手术包未打包成功，请确认！");
//			}else{
//			messageBox("保存成功！");
//			}
			isImport = false;
			repackNo = "";
			this.onClear();	//清空界面，标记赋初始值
		}
		if(isNew){
			boolean tag = true;	//tag为true时说明所有手术包打包成功
			
			TParm sterTable = tableSter.getShowParmValue();
			//fux modify 20140402
			TParm packageMTable = tablePackM.getShowParmValue();
			
			if(packageMTable.getCount("BARCODE") == 0){
				messageBox("灭菌单明细项不能为空！");
				return;
			}  
			sterTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("STERILLZATION_USER", i, Operator.getID());
				packageMTable.setData("PACK_USER", i, Operator.getID());
			}
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				parm = new TParm();
				TParm temp = new TParm();   
				temp.addData("REPACK_NO", repackNo);  
				parm.setData("STERILIZATIONTABLE", sterTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("REPACK_NO", temp.getData());
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewSterilizationAction",
			            "onInsert", parm); 
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					tag = false;
					messageBox("保存失败！");
				}
//				if(result.getData("ENOUGH").equals("NO")){
//					tag = false;
//				}
			}
			if(tag){
				messageBox("保存成功！");
			}else{
				messageBox("保存失败！");
			}
//暂时			if(!tag){
//				messageBox("有部分手术包未打包成功，请确认！");
//			}else{
//				messageBox("保存成功！");
//			}
			isNew = false;
			repackNo = "";
			this.onClear();	//清空界面，标记赋初始值
		}
		
		
		
		
		
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
	 * 带出打包单
	 */
	public void onSelectBill(){
		
		if(isNew||isImport){
			messageBox("不能同时新建两个灭菌单！");
			return;
		}
		if(!checkConditions()){
			return;
		}
//		if (this.getValueString("ORG_CODE").length() == 0) {
//			messageBox("请输入供应室部门！");
//			return;
//		}
//		if (this.getValueString("AUDIT_USER").length() == 0) {
//			messageBox("审核人员不能为空！");
//			return;
//		}
		Object obj = openDialog("%ROOT%\\config\\inv\\INVChooseRepackBill.x");
		TParm parm = (TParm)obj;
		
		if(parm.getErrCode() < 0){
			this.messageBox("无传回数据！");
			return;
		}
		
		repackNo = parm.getData("REPACK_NO").toString();
		
		TParm result = INVNewSterilizationTool.getInstance().queryRepackByRepackNo(parm);
		
		if(result.getErrCode() < 0){
			this.messageBox("无查询数据！");
			return;
		}
		
		//20150305 wangjingchun add start
		//检查包装材料与锅号是否对应
		String sql = "SELECT B.ID FROM INV_PACKM A,SYS_DICTIONARY B "
						+" WHERE B.GROUP_ID = 'BZCL' "
						+" AND A.PACK_CODE = '"+result.getValue("PACK_CODE",0)
						+"' AND A.BAG_MATERIAL = B.ID";
		TParm bag_parm = new TParm(TJDODBTool.getInstance().select(sql));
		String sql1 = "SELECT HL_FLG FROM INV_POTSEQ WHERE POTSEQ = '"+this.getValue("STERILIZATION_NUM")+"'";
		TParm potseq_parm = new TParm(TJDODBTool.getInstance().select(sql1));
		if(potseq_parm.getValue("HL_FLG", 0).equals("H")){
			if(bag_parm.getValue("ID",0).equals("4")){
				this.messageBox("请选择低温锅号");
				return;
			}
		}
		if(potseq_parm.getValue("HL_FLG", 0).equals("L")){
			if(bag_parm.getValue("ID",0).equals("1")||
					bag_parm.getValue("ID",0).equals("2")||
					bag_parm.getValue("ID",0).equals("3")){
				this.messageBox("请选择高温锅号");
				return;
			}
		}
		//20150305 wangjingchun add end
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		
		this.clearTable(tableSter);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		sterilizationNo = this.getSterilizationNo();
		this.createNewSterilization();
		
		
		for(int i=0;i<result.getCount();i++){
			//fux modify 加入锅号
			//锅次
			result.setData("STERILIZATION_POTSEQ", i, this.getValue("STERILIZATION_POTSEQ"));
			//程序
			result.setData("STERILLZATION_PROGRAM", i, this.getValue("STERILIZATION_PROGRAM"));
			//锅号
			result.setData("STERILIZATION_NUM", i, this.getValue("STERILIZATION_NUM")); 
			result.setData("STATUS", i, "灭菌");
			result.setData("STERILLZATION_DATE", i, date.toString().substring(0,19));
			result.setData("STERILLZATION_USER", i, Operator.getID());
//			result.setData("PACK_DATE", i, date.toString().substring(0,19));
//			result.setData("PACK_USER", i, Operator.getID());
			result.setData("AUDIT_DATE", i, date.toString().substring(0,19));
			result.setData("AUDIT_USER", i, this.getValueString("AUDIT_USER"));
			result.setData("OPT_DATE",i,date.toString().substring(0,19));
			result.setData("OPT_USER",i,Operator.getID());
			result.setData("OPT_TERM",i,Operator.getIP());
			
			result.setData("ORG_CODE",i,this.getValueString("ORG_CODE"));
//			result.setData("STA", i, "2");		//回收的状态为2
		}
		tablePackM.setParmValue(result);
		isImport = true;
	}
	
	/**
	 * 主表点击事件
	 */
	public void onTableMClicked() {
		
		int row = tablePackM.getSelectedRow();
		String packCode = tablePackM.getShowParmValue().getValue("PACK_CODE", row);
		String packSeqNo = tablePackM.getShowParmValue().getValue("PACK_SEQ_NO", row);
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		parm.setData("PACK_SEQ_NO", packSeqNo);
		
		TParm result = new TParm();
		result = INVNewSterilizationTool.getInstance().queryPackageDetailInfo(parm);
		
		
		tablePackD.setParmValue(result);
	}
	
	public void onTableSterClicked(){
		
		if(!isImport&&!isNew){
			int row = tableSter.getSelectedRow();
			String steriliaztionNo = "";
			if(row>=0){
				steriliaztionNo = tableSter.getItemData(row, "STERILIZATION_NO").toString();
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("STERILIZATION_NO", steriliaztionNo);
			
			if(this.getRadioButton("radioYes").isSelected()){
				parm.setData("FINISH_FLG", "Y");
			}else if(this.getRadioButton("radioNo").isSelected()){
				parm.setData("FINISH_FLG", "N");
			}
			
			TParm result = INVNewSterilizationTool.getInstance().querySterilizationByNo(parm);
			
			if(result.getCount()>0){
				for(int i=0;i<result.getCount();i++){
					result.setData("STATUS", i, "灭菌");
				}
			}
			tablePackM.removeRowAll();
			tablePackM.setParmValue(result);
			tablePackD.removeRowAll();
		}
		
	}
	
	public void onQuery(){
		
		if(isImport){
			int count = tablePackM.getRowCount();
			if(count>0){
				if(this.messageBox("提示信息", "新建灭菌单信息尚未保存，是否保存？", this.YES_NO_OPTION)==0){
					//保存
					this.onSave();
				}else{
					this.onClear();
				}
			}
		}

		String startDate = getValueString("START_STER_DATE");	//起始时间
		String endDate = getValueString("END_STER_DATE");		//终止时间

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("请输入起止时间!");
			return;
		}
		
		
		TParm parm = new TParm();			//查询条件
		if(this.getRadioButton("radioYes").isSelected()){
			parm.setData("FINISH_FLG", "Y");
		}else if(this.getRadioButton("radioNo").isSelected()){
			parm.setData("FINISH_FLG", "N");
		}
		if(!getValueString("STERILIZATION_NO").equals("")){
			parm.setData("STERILIZATION_NO", getValueString("STERILIZATION_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewSterilizationTool.getInstance().querySterilization(parm);
		
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		tableSter.setParmValue(result);
		
	}
	
	
	public void onDelete(){
		
		if(isImport){	//引入回收单状态时进入
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//判断如果不是删除回收表，是否是删除手术包信息
				
				messageBox("请选择灭菌单！");
				return;
			}else{//如果是删除回收表
				this.onClear();
			}
		}else if(isNew){	//非引入回收单状态时进入
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//判断如果不是删除灭菌表，是否是删除手术包信息

				int row = tablePackM.getSelectedRow();
				if(row>=0){
					tablePackM.removeRow(row);
					clearTable(tablePackD);
					return;
				}
				
				messageBox("请选择灭菌单！");
				return;
			}else{//如果是删除灭菌表
				this.onClear();
			}
		}else{
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("Y")){
				messageBox("已完成的灭菌单不能删除！");
				return;
			}
			int row = tablePackM.getSelectedRow();
			if(row>=0){
				if(tablePackM.getParmValue().getRow(row).getData("DELETE_FLG").equals("Y")){
					messageBox("已完成的灭菌单明细不能删除！");
					return;
				}
			}
		}
	}
	/**
	 * 打印灭菌单
	 */
	public void onPrint(){
		
		int row = tableSter.getSelectedRow();
		if (row < 0){
			messageBox("请选择灭菌单！");
			return;
		}
		String SterilizationNo = tableSter.getItemString(row, "STERILIZATION_NO");
		String SterilizationOrgCode = tableSter.getItemString(row, "ORG_CODE");
		String SterilizationOptUser = tableSter.getItemString(row, "OPT_USER");
		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", SterilizationOrgCode);
		
		//获得部门名称
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", SterilizationOptUser);
		
		//获得用户名称
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
		//开单日期
		String optDate = tableSter.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("STERILIZATIONNO", "TEXT", SterilizationNo);	//灭菌单号
		reportTParm.setData("STERILIZATIONDEPT", "TEXT", deptInfo.getData("ORG_DESC",0));	//灭菌部门名称
		reportTParm.setData("STERILIZATIONDATE", "TEXT", optDate);	//开单日期
		
		//表格数据
        TParm tableParm = new TParm();
		for(int i=0;i<tablePackM.getRowCount();i++){
			tableParm.addData("BARCODE", tablePackM.getItemString(i, "BARCODE"));
			tableParm.addData("PACK_DESC", tablePackM.getItemString(i, "PACK_DESC"));
			
//			String packCode = tablePackM.getItemString(i, "PACK_CODE");
//			String strPackSeqNo = tablePackM.getItemString(i, "PACK_SEQ_NO");
//			String packCodeSeq = strPackSeqNo;
//			for (int m = packCodeSeq.length(); m < 4; m++) {
//				packCodeSeq = "0" + packCodeSeq;
//			}
//			packCodeSeq = "" + packCodeSeq;
			
			
			tableParm.addData("PACKAGENNO",  tablePackM.getItemString(i, "PACK_CODE"));
			tableParm.addData("STERILLZATION_DATE", tablePackM.getItemString(i, "STERILLZATION_DATE").substring(0, 10));
			//fux modify 加入锅号
			tableParm.addData("STERILIZATION_POTSEQ", tablePackM.getItemString(i, "STERILIZATION_POTSEQ"));
			tableParm.addData("STERILLZATION_PROGRAM", tablePackM.getItemString(i, "STERILLZATION_PROGRAM"));
			tableParm.addData("STERILIZATION_NUM", tablePackM.getItemString(i, "STERILIZATION_NUM"));
		}
		
		tableParm.setCount(tableParm.getCount("PACK_DESC"));
		
		tableParm.addData("SYSTEM", "COLUMNS", "BARCODE");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACKAGENNO");
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILLZATION_DATE");
	     //fux modify 加入锅号  修改报表
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILIZATION_POTSEQ");
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILLZATION_PROGRAM"); 
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILIZATION_NUM");  
	      
	    reportTParm.setData("TABLE", tableParm.getData());
		  
	    Timestamp date = SystemTool.getInstance().getDate();
	    
	    reportTParm.setData("OPTUSER", "TEXT", Operator.getName());
	    reportTParm.setData("OPTDATE", "TEXT", date.toString().substring(0, 10));
		
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSterilization.jhw",
				reportTParm);
		
		
		
	}
	
	
	
	
	
	
	/**
	 * 打印条码
	 */
	public void onBarcode(){
		
		TParm parm = new TParm();
		int row = tablePackM.getSelectedRow();
		if (row < 0){
			return;
		}
		String packCode = tablePackM.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("手术包选择错误!");
			return;
		}
		// 手术包序号
		String strPackSeqNo = tablePackM.getItemString(row, "PACK_SEQ_NO");
		
		String packCodeSeq = strPackSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;
		
		int sterRow = tableSter.getSelectedRow();
		if(sterRow<0){
			return;
		}
		
		String sterilizationNo = tableSter.getItemString(sterRow, "STERILIZATION_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE", packCode);
		conditions.setData("PACK_SEQ_NO", Integer.parseInt(strPackSeqNo));
		conditions.setData("STERILIZATION_NO", sterilizationNo);
		
		//查询条码所需信息
		TParm result = INVNewSterilizationTool.getInstance().queryBarcodeInfo(conditions);
//		TParm result = INVNewRepackTool.getInstance().queryBarcodeInfo(conditions.getRow(0));
		
		// 灭菌日期    打包日期	
		String sterilizationDate = StringTool.getString((Timestamp)result.getData("STERILLZATION_DATE",0),"yyyy/MM/dd");
		String packageDate = StringTool.getString((Timestamp)result.getData("STERILLZATION_DATE",0),"yyyy/MM/dd");
		
		
		TParm vParm = InvPackStockMTool.getInstance().getPackDate(packCode,Integer.parseInt(strPackSeqNo));
		int valid = Integer.parseInt(vParm.getData("VALUE_DATE",0).toString());
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8)));
        cal.add(cal.DATE,valid);//把日期往后增加N天
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
		
		
        TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", packCode + packCodeSeq);
        reportParm.setData("PACK_DESC","TEXT",tablePackM.getItemString(row, "PACK_DESC"));
        reportParm.setData("PACK_DEPT","TEXT","(" + result.getData("ORG_DESC",0) + ")");
        //fux modify 加入锅号
        reportParm.setData("POTSEQ","TEXT",tablePackM.getItemString(row, "STERILIZATION_POTSEQ"));
        reportParm.setData("PROGRAM","TEXT",tablePackM.getItemString(row, "STERILLZATION_PROGRAM"));
        reportParm.setData("NUM","TEXT",tablePackM.getItemString(row, "STERILIZATION_NUM"));
        reportParm.setData("STERILIZATION_DATE","TEXT",sterilizationDate);
        reportParm.setData("VALUE_DATE","TEXT",dateString);
        reportParm.setData("OPT_USER","TEXT",result.getData("USER_NAME",0));
	
        reportParm.setData("PACK_DATE","TEXT",packageDate);
        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", vParm.getData("BARCODE", 0));

		// 调用打印方法
//		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewSterilizationBarcode.jhw", reportParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGeneralPackageBarcode.jhw", reportParm);
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
	 * 向灭菌单table新添一条灭菌单记录
	 */
	private void createNewSterilization(){
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("STERILIZATION_NO",0, sterilizationNo);
		parm.setData("OPT_DATE",0,date.toString().substring(0,19));
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		
		tableSter.setParmValue(parm);
		tableSter.setSelectedRow(0);
	}
	
	/** 
	 * 生成灭菌单号 
	 *  */
	private String getSterilizationNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_STERILIZATION", "No");
		return recyleNo;
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
		if (this.getValueString("STERILIZATION_POTSEQ").length() == 0) {
			messageBox("请输入锅次！");
			return false;
		}
		if (this.getValueString("STERILIZATION_PROGRAM").length() == 0) {
			messageBox("请输入程序！");
			return false;
		}
		if (this.getValueString("STERILIZATION_NUM").length() == 0) {
			messageBox("请输入锅号！");
			return false;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("请输入审核人员！");
			return false;
		}
		return true;
	}
	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空属性
		clearText();
		// 清空主表
		clearTable(tableSter);
		// 清空手术报
		clearTable(tablePackM);
		// 清空明细表
		clearTable(tablePackD);
		isImport = false;
		isNew = false;
		//设置默认审核人员
        TTextFormat audit_user = (TTextFormat) this.getComponent("AUDIT_USER");
        audit_user.setValue(Operator.getID());
	}
	/**
	 * 清空控件值
	 */ 
	private void clearText() {
		this.clearValue("STERILIZATION_NO;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;STERILIZATION_NUM;STERILIZATION_POTSEQ;STERILIZATION_PROGRAM;AUDIT_USER");
	}
	/** 获得TTextField类型控件 **/
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);  
    }
	/** 获得TRadioButton类型控件 **/
	private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
	/**清空table**/
	private void clearTable(TTable table) {
		table.removeRowAll();
	}
	/** 初始化日期控件数据 **/
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_STER_DATE", date);
		this.setValue("END_STER_DATE", date);
	}
}
