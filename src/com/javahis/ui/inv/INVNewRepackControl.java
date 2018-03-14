package com.javahis.ui.inv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
import jdo.inv.INVRepackHelpTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>  
 * Title:打包 
 * </p>
 * 
 * <p> 
 * Description: 打包
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
 * @author wangming 2013-8-2
 * @version 1.0
 */
public class INVNewRepackControl extends TControl{

	private TTable tableRepack;			//打包单表
	
	private TTable tablePackM;			//手术包主表
	
	private TTable tablePackD;			//手术包明细
	
	private String repackNo;			//初始化打包单号                  
	
	private boolean isImport = false;		//是否是导入回收单标记  	true：是		false：不是
	
	private boolean isNew = false;			//是否是新建回收单	true：是		false：不是
	
	private String recycleNo;			//传回的回收单号          
	
	int row = -1;//扫描条码初始行数
	
	String str = "PRINT";
	/**
	 * 初始化
	 */
	public void onInit() {
	
		tableRepack = (TTable) getComponent("TABLE_REPACK");
		tablePackM = (TTable) getComponent("TABLE_PACKM");
		tablePackD = (TTable) getComponent("TABLED");
		
		this.onInitDate();
		
		tableRepack.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
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
        //20150121 wangjingchun add start
        //设置默认审核人员
        TTextFormat audit_user = (TTextFormat) this.getComponent("AUDIT_USER");
        audit_user.setValue(Operator.getID());
        //20150121 wangjingchun add end
		
	}
	
	
	
	/**
	 * 扫描条码
	 */
	public void onScream() {
		
		if(isImport){
			messageBox("引入回收单状态下不能添加手术包！");
			return ;
		}
		
		int r = tableRepack.getSelectedRow();
		if(r<0){
			messageBox("请选择打包单！");
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
			
			this.addPackage();	//向打包单中添加手术包
			this.grabFocus("SCREAM");	//定位焦点
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * 向回收单中添加手术包方法
	 */
	public void addPackage(){
		
		int r = tableRepack.getSelectedRow();
		if(r<0){
			messageBox("请选择打包单！");
			return ;
		}
		
		boolean tag = false;  
		tag = this.checkConditions();
		if(!tag){		//输入条件不完整
			return;
		}
		TParm parm = new TParm();
		parm.setData("BARCODE", this.getValueString("SCREAM"));
		//回收时是2
		parm.setData("STATUS",  "2");      
//		parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
//		parm.setData("PACK_SEQ_NO", this.getValueString("PACK_SEQ_NO"));
		 
		TParm result = new TParm();   
		result = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);	//查询手术包信息（根据条码）
		
		if(result.getCount()<=0){  
			return;                  
		}  
		
		if(result.getData("STATUS",0).equals("5")){
			messageBox("手术包已处于维修状态，无法回收！");
			return;
		}
//		if(result.getData("STATUS",0).equals("3")){
//			messageBox("手术包已处于灭菌状态！");
//			return;
//		}
//		if(result.getData("STATUS",0).equals("2")){
//			messageBox("手术包已处于回收状态！");
//			return;
//		}
		if(result.getData("STATUS",0).equals("0")){
			if(this.messageBox("提示信息", "手术包处于在库状态，是否再次打包？", this.YES_NO_OPTION)==1){
				return;
			}
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString()); 	//有效期限   单位：天
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
        cal.add(cal.DATE,valid);//把日期往后增加相应的天数
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(d);

//		TParm rowRecord = new TParm();
//		rowRecord.setData("BARCODE",result.getData("BARCODE",0));
//		rowRecord.setData("PACK_DESC",result.getData("PACK_DESC",0));
//		rowRecord.setData("PACK_CODE",result.getData("PACK_CODE",0));
//		rowRecord.setData("PACK_SEQ_NO",result.getData("PACK_SEQ_NO",0));
//		rowRecord.setData("QTY",result.getData("QTY",0));
//		rowRecord.setData("STATUS","打包");
//		rowRecord.setData("AUDIT_DATE",date);				//审核日期
//		rowRecord.setData("AUDIT_USER",this.getValueString("AUDIT_USER"));	//审核人员
//		rowRecord.setData("REPACK_DATE",date);				//打包日期		
//		rowRecord.setData("REPACK_USER",Operator.getID());	//打包人员
//		tablePackM.addRow(rowRecord);
		
		TParm tp = new TParm();
		tp = tablePackM.getParmValue(); 
		
		if(tp == null){
			tp = new TParm();
			row = 0;  
		}else{
			row = tp.getCount();
		}
		tp.setData("BARCODE", row,result.getData("BARCODE",0));
		tp.setData("PACK_DESC", row,result.getData("PACK_DESC",0));
		tp.setData("PACK_CODE", row,result.getData("PACK_CODE",0));
		tp.setData("PACK_SEQ_NO", row,result.getData("PACK_SEQ_NO",0));
		tp.setData("QTY", row,result.getData("QTY",0));
		tp.setData("STATUS", row,"打包"); 
		tp.setData("AUDIT_DATE", row,date);
		tp.setData("AUDIT_USER", row,this.getValueString("AUDIT_USER"));
		tp.setData("REPACK_DATE", row,date);  
		tp.setData("REPACK_USER", row,Operator.getID());
		row++;
		tablePackM.setParmValue(tp);    
		this.grabFocus("PACK_SEQ_NO");	//定位焦点
	}
	
	/**
	 * 新增回收单方法
	 * */
	public void onNew(){
		
		if(isImport||isNew){
			messageBox("不能同时新建两个回收单！");
			return;
		}

		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("供应室部门不能为空！");
			return;
		}
		
		isNew = true;				//灭菌单新建状态
		this.clearTable(tableRepack);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		
		repackNo = this.getRepackNo();
		this.createNewRepack();    //新增一条打包单记录
		
	}
	/**
	 * 保存
	 * */
	public void onSave(){
		str = "SAVE";
		if(isImport){	//引入回收单
			boolean tag = true;	//tag为true时说明所有手术包打包成功
			
			TParm repackTable = tableRepack.getParmValue();
			TParm packageMTable = tablePackM.getParmValue();
			repackTable.setData("OPT_USER",0, Operator.getID());

		///////原start  
			TParm condition = new TParm();
			String barcodes = "";
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				barcodes = barcodes + packageMTable.getRow(i).getValue("BARCODE") + ",";
				barcodes = barcodes.substring(0,barcodes.lastIndexOf(","));
				condition.setData("BARCODE",0, barcodes);
			}   
			TParm allMaterial = new TParm();
			allMaterial = INVRepackHelpTool.getInstance().queryHighOnceMaterial(condition.getRow(0));//获得有序管且一次性物资的手术包条码
		///////原end
			
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount();i++){
				parm = new TParm();
				TParm temp = new TParm();  
				temp.addData("RECYCLE_NO", recycleNo);
				
				parm.setData("REPACK", repackTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("RECYCLENO", temp.getData());
				
				
				
				//处理序管且一次性物资start
				boolean isHO = false;//是否有序管且一次性物资
				if(allMaterial!=null){
					for(int j=0;j<allMaterial.getCount();j++){
						String barcode = packageMTable.getRow(i).getValue("BARCODE");
						if(allMaterial.getRow(j).getValue("BARCODE").equals(barcode)){
							isHO = true;
						}
					}
				}
				TParm highOnce = new TParm();	//存放当次打包需要的序管且一次性物资信息
				if(isHO){//有序管且一次性物资的手术包打包时进入选择序管且一次性物资
					TParm hoParm = INVRepackHelpTool.getInstance().queryHighOnceMaterial(packageMTable.getRow(i));
					if(hoParm!=null){
						for(int j=0;j<hoParm.getCount("BARCODE");j++){
							Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",hoParm.getRow(j));
							if (obj != null) {
					            TParm materialRFID = (TParm) obj;
					            for(int m=0;m<materialRFID.getCount("RFID");m++){
//					            	highOnce.addParm(materialRFID.getRow(m));
					            	int rowC = -1;
					            	if(highOnce==null){
					            		rowC = 0;
					            	}else if(highOnce.getCount("RFID") == -1){
					            		rowC = 0;
					            	}
					            	highOnce.setData("RFID", rowC,  materialRFID.getRow(m).getValue("RFID"));
					            	highOnce.setData("PACK_CODE", rowC, hoParm.getRow(j).getValue("PACK_CODE"));
					            	highOnce.setData("PACK_SEQ_NO", rowC, hoParm.getRow(j).getValue("PACK_SEQ_NO"));
					            }
					        }else{
					        	tag = false;	//有部分物资没有库存
					        }
						}
						parm.setData("HOMATERIAL", highOnce.getData());	//该手术包中所有选中的序管且一次性物资
					}
				}
				//处理序管且一次性物资end
				System.out.println("引入回收单parm:"+parm);       
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewRepackAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					messageBox("保存失败！");
				}
				if(result.getData("ENOUGH").equals("NO")){
					tag = false;
				}
			}
			if(!tag){
				messageBox("有部分手术包未打包成功，请确认！");
			}else{
				messageBox("保存成功！");
			}
			//fux modify 20140526
			//打印2次配置单 
//			onPrint(); 
//			onPrint();
			onBarcode();  
			isImport = false;  
			recycleNo = "";  
			this.onClear();	//清空界面，标记赋初始值  
		} 
		if(isNew){
			boolean tag = true;	//tag为true时说明所有手术包打包成功
			
			TParm repackTable = tableRepack.getParmValue();
			TParm packageMTable = tablePackM.getParmValue(); 
			if(packageMTable.getCount("BARCODE") == 0){  
				messageBox("打包单明细项不能为空！");
				return;
			} 
			
			repackTable.setData("OPT_USER",0, Operator.getID());

			
			///////原start
			TParm condition = new TParm();
			String barcodes = "";
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				barcodes = barcodes + packageMTable.getRow(i).getValue("BARCODE") + ",";
				barcodes = barcodes.substring(0,barcodes.lastIndexOf(","));
				condition.setData("BARCODE",0, barcodes);
			}
			TParm allMaterial = new TParm();
			allMaterial = INVRepackHelpTool.getInstance().queryHighOnceMaterial(condition.getRow(0));//获得有序管且一次性物资的手术包条码
			
//			List rfidList = new ArrayList();
//			if(allMaterial!=null){
//				for(int j=0;j<allMaterial.getCount();j++){
//					TParm diaParm = new TParm();
//					diaParm.setData("Material", allMaterial.getRow(j));
//					diaparm.setData("List",rfidList);
//					Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",allMaterial.getRow(j));
//					if (obj != null) {
//			            TParm temp = (TParm) obj;
//			        }
//				}
//			}
			///////原end
			
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("RECYCLE_NO", recycleNo);
				
				parm.setData("REPACK", repackTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("RECYCLENO", temp.getData());
				
				
				//处理序管且一次性物资start
				boolean isHO = false;//是否有序管且一次性物资
				if(allMaterial!=null){
					for(int j=0;j<allMaterial.getCount();j++){
						String barcode = packageMTable.getRow(i).getValue("BARCODE");
						if(allMaterial.getRow(j).getValue("BARCODE").equals(barcode)){
							isHO = true;
						}
					}
				}
				TParm highOnce = new TParm();	//存放当次打包需要的序管且一次性物资信息
				if(isHO){//有序管且一次性物资的手术包打包时进入选择序管且一次性物资
					TParm hoParm = INVRepackHelpTool.getInstance().queryHighOnceMaterial(packageMTable.getRow(i));
					if(hoParm!=null){
						for(int j=0;j<hoParm.getCount("BARCODE");j++){
							Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",hoParm.getRow(j));
							if (obj != null) {
					            TParm materialRFID = (TParm) obj;
					            for(int m=0;m<materialRFID.getCount("RFID");m++){
//					            	highOnce.addParm(materialRFID.getRow(m));
					            	int rowC = -1;
					            	if(highOnce==null){
					            		rowC = 0;
					            	}else if(highOnce.getCount("RFID") == -1){
					            		rowC = 0;
					            	}
					            	highOnce.setData("RFID", rowC,  materialRFID.getRow(m).getValue("RFID"));
					            	highOnce.setData("PACK_CODE", rowC, hoParm.getRow(j).getValue("PACK_CODE"));
					            	highOnce.setData("PACK_SEQ_NO", rowC, hoParm.getRow(j).getValue("PACK_SEQ_NO"));
					            }
					        }else{
					        	tag = false;	//有部分物资没有库存
					        }
						}
						parm.setData("HOMATERIAL", highOnce.getData());	//该手术包中所有选中的序管且一次性物资
					}
				}
				
				
				//处理序管且一次性物资end
				 
			    System.out.println("parm:::"+parm);
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewRepackAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					messageBox("保存失败！");
				}
				System.out.println("result:"+result);
				if(result.getData("ENOUGH").equals("NO")){ 
					tag = false;
				}
			}
			if(!tag){
				messageBox("有部分手术包未打包成功，请确认！");
			}else{
				messageBox("保存成功！");
			}
			//fux modify 20140526
			//打印2次配置单
			onPrint();
			onPrint();   
			onBarcode();
			isNew = false;  
			recycleNo = "";
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
	 * 带出回收单
	 */
	public void onSelectBill(){
		
		if(isNew||isImport){
			messageBox("不能同时新建两个打包单！");
			return;
		}
		
		if (this.getValueString("ORG_CODE").length() == 0) {        
			messageBox("请输入供应室部门！");
			return;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("请输入审核人员部门！");
			return;
		}
		
		Object obj = openDialog("%ROOT%\\config\\inv\\INVChooseRecycleBill.x");
		TParm parm = (TParm)obj;
		
		if(parm.getErrCode() < 0){
			this.messageBox("无传回数据！");
			return;
		}
		
		recycleNo = parm.getData("RECYCLE_NO").toString();
		
		TParm result = INVNewRepackTool.getInstance().queryDisnfectionByNo(parm);
		
		if(result.getErrCode() < 0){
			this.messageBox("无查询数据！");
			return;
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		
		this.clearTable(tableRepack);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		repackNo = this.getRepackNo();
		this.createNewRepack();
		
		
		for(int i=0;i<result.getCount();i++){
			result.setData("STATUS", i, "打包");
			result.setData("AUDIT_DATE", i, date);
			result.setData("AUDIT_USER", i, this.getValueString("AUDIT_USER"));
			result.setData("REPACK_DATE", i, date);
			result.setData("REPACK_USER", i, Operator.getID());
			
			result.setData("OPT_DATE",i,date);
			result.setData("OPT_USER",i,Operator.getID());
			result.setData("OPT_TERM",i,Operator.getIP());
			
			result.setData("ORG_CODE",i,this.getValueString("ORG_CODE"));
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
	
	public void onTableRepackClicked(){
		
		if(!isImport&&!isNew){
			int row = tableRepack.getSelectedRow();
			String repackNo = "";
			if(row>=0){
				repackNo = tableRepack.getItemData(row, "REPACK_NO").toString();
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("REPACK_NO", repackNo);
			
			if(this.getRadioButton("radioYes").isSelected()){
				parm.setData("FINISH_FLG", "Y");
			}else if(this.getRadioButton("radioNo").isSelected()){
				parm.setData("FINISH_FLG", "N");
			}
			
			TParm result = INVNewRepackTool.getInstance().queryRepackByNo(parm);
			
			if(result.getCount()>0){
				for(int i=0;i<result.getCount();i++){
					result.setData("STATUS", i, "打包");
				}
			}
			tablePackM.removeRowAll();
			tablePackM.setParmValue(result);
			tablePackD.removeRowAll();
		}
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		
		if(isImport){
			int count = tablePackM.getRowCount();
			if(count>0){
				if(this.messageBox("提示信息", "新建打包单信息尚未保存，是否保存？", this.YES_NO_OPTION)==0){
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
		if(!getValueString("REPACK_NO").equals("")){
			parm.setData("REPACK_NO", getValueString("REPACK_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewRepackTool.getInstance().queryRepack(parm);
		if(result.getErrCode()<0){
			messageBox("无查询数据!");
			return;
		}
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		tableRepack.setParmValue(result);
		
	}
	
	/**
	 * 删除
	 */
	public void onDelete(){
		
		if(isImport){	//引入回收单状态时进入
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//判断如果不是删除回收表，是否是删除手术包信息
				
				messageBox("请选择打包单！");
				return;
			}else{//如果是删除回收表
				this.onClear();
			}
		}else if(isNew){	//非引入回收单状态时进入
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//判断如果不是删除灭菌表，是否是删除手术包信息

				int row = tablePackM.getSelectedRow();
				if(row>=0){
					tablePackM.removeRow(row);
					clearTable(tablePackD);
					return;
				}
				
				messageBox("请选择打包单！");
				return;
			}else{//如果是删除灭菌表
				this.onClear();
			}
		}else{
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("Y")){
				messageBox("已完成的打包单不能删除！");
				return;
			}
			int row = tablePackM.getSelectedRow();
			if(row>=0){
				if(tablePackM.getParmValue().getRow(row).getData("DELETE_FLG").equals("Y")){
					messageBox("已完成的打包单明细不能删除！");
					return;
				}
			}
		}
	}
	
	
	/**
	 * 打印打包单   
	 */
	public void onPrint(){
		
		
		if(str == "PRINT"){
		int row = tableRepack.getSelectedRow();  
		//打印时提示,保存后打印时不提示
		if (row < 0){      
			//messageBox("str111"+str);
			
			messageBox("请选择打包单！");
			return;  
		}                         
		
		  
		int rowM = tablePackM.getSelectedRow();
		if (rowM < 0){  
			//messageBox("str222"+str);
			//if(str == "PRINT"){
			messageBox("请选择手术包！");
			//}
			return;
		}   
		               
		String RepackNo = tableRepack.getItemString(row, "REPACK_NO");
		String RepackNoOrgCode = tableRepack.getItemString(row, "ORG_CODE");
		String RepackNoOptUser = tableRepack.getItemString(row, "OPT_USER");
		
		String barcode = tablePackM.getItemString(rowM, "BARCODE");//选中手术包的条码
		String packCode= tablePackM.getItemString(rowM, "PACK_CODE");//手术包类型
		
		TParm conditions = new TParm();
		conditions.setData("BARCODE",barcode);  
		conditions.setData("REPACK_NO", RepackNo);
		conditions.setData("PACK_CODE", packCode);
		
		
		TParm packList = new TParm();
		packList = INVNewRepackTool.getInstance().queryPackList(conditions);
		
		
		 
		TParm parm = new TParm();
		parm.setData("ORG_CODE", RepackNoOrgCode);
		//获得部门名称
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", RepackNoOptUser);
		
		//获得用户名称  
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
//		//开单日期
//		String optDate = tableRepack.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("PACK_DESC", "TEXT", tablePackM.getItemString(rowM, "PACK_DESC"));	//包名
		reportTParm.setData("PACK_BARCODE", "TEXT", barcode);	//条码  
		reportTParm.setData("PACK_USER", "TEXT", userInfo.getData("USER_NAME", 0));	//打包人员
		reportTParm.setData("PACK_DATE", "TEXT", tableRepack.getItemString(row, "OPT_DATE").substring(0, 10));	//打包日期
		
		//int tag=packList.getCount("QTY")%2;
		
		
		int allCount = packList.getCount("QTY");
		int remainder = allCount%80;//余数
		int addCount = 0;
		if(remainder!=0){
			addCount = 80 - remainder;
			for (int i = 0; i < addCount; i++){
				packList.addData("QTY", "");   
				packList.addData("INV_CHN_DESC", "");
			}
		}
		//表格数据
        TParm tableParm = new TParm();  
/*		if(packList.getCount("QTY") == 1){ 
			tableParm.addData("SEQ_F", 1);
			tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
			tableParm.addData("QTY_F", packList.getData("QTY", 0));
			tableParm.addData("SEQ_S", "");
			tableParm.addData("CHN_DESC_S", "");
			tableParm.addData("QTY_S", "");
		}
       else if(packList.getCount("QTY") > 1){
			
			if(tag == 0){
				for(int i=0;i<packList.getCount("QTY");){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
			}
			if(tag == 1){
				for(int i=0;i<packList.getCount("QTY")-1;){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
				tableParm.addData("SEQ_F", packList.getCount("QTY"));
				tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", packList.getCount("QTY")-1));
				tableParm.addData("QTY_F", packList.getData("QTY", packList.getCount("QTY")-1));
				tableParm.addData("SEQ_S", "");
				tableParm.addData("CHN_DESC_S", "");
				tableParm.addData("QTY_S", "");
			}
		}*/
        DecimalFormat df1 = new DecimalFormat("0");
		if (packList.getCount("QTY") == 1) {
			tableParm.addData("SEQ_F", 1);
			tableParm
					.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
			tableParm.addData("QTY_F", packList.getData("QTY", 0));
			tableParm.addData("SEQ_S", "");
			tableParm.addData("CHN_DESC_S", "");
			tableParm.addData("QTY_S", "");
		}
        else if (packList.getCount("QTY") > 1) {       
			for (int i = 0; i < packList.getCount("QTY") / 2; i++) {
				tableParm.addData("SEQ_F", i + 1);
				tableParm.addData("CHN_DESC_F", packList.getData(
						"INV_CHN_DESC", i));

				if (null == packList.getData("QTY", i)
						|| "".equals(packList.getData("QTY", i))) {
					tableParm.addData("QTY_F", "");
				} else {
					tableParm.addData("QTY_F", df1.format(packList.getDouble(
							"QTY", i)));
				}                         

				tableParm.addData("SEQ_S", (i + 1) + 40);
				tableParm.addData("CHN_DESC_S", packList.getData(
						"INV_CHN_DESC", (i + 1) + 40));

				if (null == packList.getData("QTY", (i + 1) + 40)
						|| "".equals(packList.getData("QTY", (i + 1) + 40))) {
					tableParm.addData("QTY_S", "");
				} else {
					tableParm.addData("QTY_S", df1.format(packList.getDouble(
							"QTY", (i + 1) + 40)));
				}
			}
		}
		tableParm.setCount(tableParm.getCount("SEQ_F"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");  

	    reportTParm.setData("TABLE", tableParm.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
				reportTParm,true);    
		}
		
		
		else{
			    for (int j = 0; j < tablePackM.getRowCount(); j++) {
			    //默认第0行
				String RepackNo = tableRepack.getItemString(0, "REPACK_NO");
				String RepackNoOrgCode = tableRepack.getItemString(0, "ORG_CODE");
				String RepackNoOptUser = tableRepack.getItemString(0, "OPT_USER");
				
				String barcode = tablePackM.getItemString( j, "BARCODE");//选中手术包的条码
				String packCode= tablePackM.getItemString( j, "PACK_CODE");//手术包类型
				
				TParm conditions = new TParm();
				conditions.setData("BARCODE",barcode);         
				conditions.setData("REPACK_NO", RepackNo);
				conditions.setData("PACK_CODE", packCode);
				
				
				TParm packList = new TParm();
				packList = INVNewRepackTool.getInstance().queryPackList(conditions);
				
				
				 
				TParm parm = new TParm();
				parm.setData("ORG_CODE", RepackNoOrgCode);
				//获得部门名称
				TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
				
				parm = new TParm();
				parm.setData("USER_ID", RepackNoOptUser);
				
				//获得用户名称  
				TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);  
//				//开单日期
//				String optDate = tableRepack.getItemString(row, "OPT_DATE").substring(0, 10);
				
				TParm reportTParm = new TParm();  
				reportTParm.setData("PACK_DESC", "TEXT", tablePackM.getItemString(j, "PACK_DESC"));	//包名
				reportTParm.setData("PACK_BARCODE", "TEXT", barcode.substring(0, 9));	//条码  
				reportTParm.setData("PACK_USER", "TEXT", userInfo.getData("USER_NAME", 0));	//打包人员
				reportTParm.setData("PACK_DATE", "TEXT", tableRepack.getItemString(0, "OPT_DATE").substring(0, 10));	//打包日期
				
				//int tag=packList.getCount("QTY")%2;
				
				
				int allCount = packList.getCount("QTY");
				int remainder = allCount%80;//余数
				int addCount = 0;
				if(remainder!=0){
					addCount = 80 - remainder;
					for (int i = 0; i < addCount; i++){
						packList.addData("QTY", "");   
						packList.addData("INV_CHN_DESC", "");
					}
				}
				//表格数据
		        TParm tableParm = new TParm();  
		/*		if(packList.getCount("QTY") == 1){ 
					tableParm.addData("SEQ_F", 1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
					tableParm.addData("QTY_F", packList.getData("QTY", 0));
					tableParm.addData("SEQ_S", "");
					tableParm.addData("CHN_DESC_S", "");
					tableParm.addData("QTY_S", "");
				}
		       else if(packList.getCount("QTY") > 1){
					
					if(tag == 0){
						for(int i=0;i<packList.getCount("QTY");){
							tableParm.addData("SEQ_F", i+1);
							tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
							tableParm.addData("QTY_F", packList.getData("QTY", i));
							tableParm.addData("SEQ_S", i+2);
							tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
							tableParm.addData("QTY_S", packList.getData("QTY", i+1));
							i=i+2;
						}
					}
					if(tag == 1){
						for(int i=0;i<packList.getCount("QTY")-1;){
							tableParm.addData("SEQ_F", i+1);
							tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
							tableParm.addData("QTY_F", packList.getData("QTY", i));
							tableParm.addData("SEQ_S", i+2);
							tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
							tableParm.addData("QTY_S", packList.getData("QTY", i+1));
							i=i+2;
						}
						tableParm.addData("SEQ_F", packList.getCount("QTY"));
						tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", packList.getCount("QTY")-1));
						tableParm.addData("QTY_F", packList.getData("QTY", packList.getCount("QTY")-1));
						tableParm.addData("SEQ_S", "");
						tableParm.addData("CHN_DESC_S", "");
						tableParm.addData("QTY_S", "");
					}
				}*/
		        DecimalFormat df1 = new DecimalFormat("0");
				if (packList.getCount("QTY") == 1) {
					tableParm.addData("SEQ_F", 1);
					tableParm
							.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
					tableParm.addData("QTY_F", packList.getData("QTY", 0));
					tableParm.addData("SEQ_S", "");
					tableParm.addData("CHN_DESC_S", "");
					tableParm.addData("QTY_S", "");
				}
		        else if (packList.getCount("QTY") > 1) {       
					for (int i = 0; i < packList.getCount("QTY") / 2; i++) {
						tableParm.addData("SEQ_F", i + 1);
						tableParm.addData("CHN_DESC_F", packList.getData(
								"INV_CHN_DESC", i));

						if (null == packList.getData("QTY", i)
								|| "".equals(packList.getData("QTY", i))) {
							tableParm.addData("QTY_F", "");
						} else {
							tableParm.addData("QTY_F", df1.format(packList.getDouble(
									"QTY", i)));
						}

						tableParm.addData("SEQ_S", (i + 1) + 40);
						tableParm.addData("CHN_DESC_S", packList.getData(
								"INV_CHN_DESC", (i + 1) + 40));

						if (null == packList.getData("QTY", (i + 1) + 40)
								|| "".equals(packList.getData("QTY", (i + 1) + 40))) {
							tableParm.addData("QTY_S", "");
						} else {
							tableParm.addData("QTY_S", df1.format(packList.getDouble(
									"QTY", (i + 1) + 40)));
						}
					}
				}
				tableParm.setCount(tableParm.getCount("SEQ_F"));
				
			    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
			    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
			    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
			    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
			    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
			    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");  
   
			    reportTParm.setData("TABLE", tableParm.getData());  
				
				this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
						reportTParm,true);    
		}
		}
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
		
		int sterRow = tableRepack.getSelectedRow();
		if(sterRow<0){
			return;
		}
		                                
		String sterilizationNo = tableRepack.getItemString(sterRow, "REPACK_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE",0, packCode);
		conditions.setData("PACK_SEQ_NO",0, Integer.parseInt(strPackSeqNo));
		conditions.setData("REPACK_NO",0, sterilizationNo);
		
		//查询条码所需信息                  
		TParm result = INVNewRepackTool.getInstance().queryBarcodeInfo(conditions.getRow(0));
		
		// 灭菌日期    打包日期	
		String sterilizationDate = StringTool.getString((Timestamp)result.getData("VALID_DATE",0),"yyyy/MM/dd");
		//TypeTool.getTimestamp(StringTool.getTimestamp(table_d. 
        //getItemData(i, "VALID_DATE").toString(), "yyyy/MM/dd"))                   
		String packageDate = StringTool.getString((Timestamp)result.getData("REPACK_DATE",0),"yyyy/MM/dd");
		   
		TParm vParm = InvPackStockMTool.getInstance().getPackDate(packCode,Integer.parseInt(strPackSeqNo));
		int valid = Integer.parseInt(vParm.getData("VALUE_DATE",0).toString());  
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8,10)));
        cal.add(cal.DATE,valid);//把日期往后增加N天
        Date d=cal.getTime();       
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
          
        TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", vParm.getData("BARCODE", 0));
        reportParm.setData("PACK_DESC","TEXT",tablePackM.getItemString(row, "PACK_DESC"));
        reportParm.setData("PACK_DEPT","TEXT","(" + result.getData("ORG_DESC",0) + ")");
        reportParm.setData("POTSEQ","TEXT",tablePackM.getItemString(row, "STERILIZATION_POTSEQ"));
        reportParm.setData("PROGRAM","TEXT",tablePackM.getItemString(row, "STERILLZATION_PROGRAM"));
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
	 * 向打包单table新添一条打包单记录
	 */
	private void createNewRepack(){
		TParm parm = new TParm();
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("REPACK_NO",0, repackNo);    
		parm.setData("OPT_DATE",0,date);
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		 
		tableRepack.setParmValue(parm);
		tableRepack.setSelectedRow(0);
	}
	
	/** 
	 * 生成打包单号 
	 *  */
	private String getRepackNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_REPACK", "No");
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
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("请输入审核人员！");
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
//		if (this.getValueString("STERILIZATION_POTSEQ").length() == 0) {
//			messageBox("请输入锅次！");
//			return false;
//		}
//		if (this.getValueString("STERILIZATION_PROGRAM").length() == 0) {
//			messageBox("请输入程序！");
//			return false;
//		}
		return true;
	}
	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空属性
		clearText();
		// 清空主表
		clearTable(tableRepack);
		// 清空手术报
		clearTable(tablePackM);
		// 清空明细表
		clearTable(tablePackD);
		isImport = false;
		isNew = false;
		str = "PRINT";
	}  
	/**
	 * 清空控件值
	 */
	private void clearText() {
		this.clearValue("REPACK_NO;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;AUDITUSER");
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
