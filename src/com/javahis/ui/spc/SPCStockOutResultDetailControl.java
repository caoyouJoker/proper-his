package com.javahis.ui.spc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdo.spc.SPCOutStoreTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

public class SPCStockOutResultDetailControl extends TControl {
	
	TTable table;
	
	//调用RFID接口
	private Timer timerRfid;
	
	private TimerTask taskRfid;

	private long period = 1* 1000;

	private long delay = 1 * 1000;
	
	int rtn1 ;
	
	/**
	 * 初始化　
	 */
	public void onInit() {
		super.onInit();
		TParm parm = (TParm)getParameter();
		table = (TTable)getComponent("TABLE");
		
		TParm result = SPCOutStoreTool.getInstance().onQueryContainer(parm);
		if(result.getErrCode() < 0 ){
			this.messageBox("智能柜里没有容器");
			return ;
		}
		table.setParmValue(result);
		this.scheduleRfid();
	}
	
	/**
	 * 定时器
	 */
	public void scheduleRfid() {
		this.timerRfid = new Timer();
		this.taskRfid = new TimerTask() {
			public void run() {
				onTestRfid();
			}
		};
		this.timerRfid.schedule(this.taskRfid, this.delay, this.period);
	}
	
	public static String TAGS_FILE = "c:\\tags.txt";

	public static String readString(File file) {
		// 读文件
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp = null;
		StringBuffer sb = new StringBuffer();
		try {
			temp = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (temp != null) {
			sb.append(temp + " ");
			try {
				temp = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// System.out.println("======result========" + sb.toString());
		return sb.toString();

	}

	
	/**
	 * @param args
	 * @throws IOException
	 */
	public List getList() {
		List<String> list=new ArrayList<String>();
		Runtime rt = Runtime.getRuntime();
		Process ps = null;
		try {
			ps = rt.exec("cmd.exe /C start /b zkrfid.bat");

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			ps.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int i = ps.exitValue();

		try {
			// 等待1秒，用来删除原生成的结果文件
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (i == 0) {
			System.out.println("执行完成.");
			File file = new File(TAGS_FILE);
			String epcString = "";
			while (true) {
				// file = new File(TAGS_FILE);
				if (file.exists() && !file.isDirectory() && file.canRead()) {
					// System.out.println(file.canRead() + "=====文件已创建======");
					epcString = readString(file);
					// 包含END,则续执行.
					if (epcString.indexOf("END") != -1) {
						break;
					}
				}
				// System.out.println("======文件不存在========" + file.canRead());

			}

			String tags[] = epcString.split(" ");
			// tags[]最后一个值是END，不用取.
			for (String tag : tags) {
				if (!tag.equals("END")) {
					//加入对应的tag.
					//System.out.println("=====tag====" + tag);
					list.add(tag);
					System.out.println("tag-------------:"+tag);
				}
			}

		} else {
			System.out.println("执行失败.");
		}
		System.out.println(list+"RFID线程======================================");
		return list;

	}
	
	public void onTestRfid(){
		table.acceptText() ;
		//智能柜里现实容器
		List list = getList();
		
		TParm tableParm = table.getParmValue();
		
		for(int i = 0 ; i < tableParm.getCount() ; i++ ){
			TParm rowParm = tableParm.getRow(i) ;
			String containerId = rowParm.getValue("CONTAINER_ID");
			
			//判断是否找到
			boolean b = false;
			for(int j = 0; j < list.size() ; j++ ){
				String id = (String)list.get(j);
				if(containerId.equals(id)){
					b = true;
					break;
				}
			}
			if(b){
				table.removeRow(i);
			}
		}
		
		/**
		//数据库数据与智能柜数据对比得到拿走的容器
		List newList = new ArrayList();
		TParm inParm = new TParm();
		inParm.setData("CABINET_ID",getValueString("CABINET_ID"));
		TParm parm = SPCOutStoreTool.getInstance().onQueryContainer(inParm);
		int count = parm.getCount() ;
		for(int i = 0 ; i < count ; i++ ){
			TParm rowParm = parm.getRow(i);
			boolean b = true ;
			String id = rowParm.getValue("CONTAINER_ID");
			for(int j = 0 ; j < list.size() ; j++ ){
				String containerId = (String)list.get(j);
				if(id.equals(containerId)){
					b = false;
					break;
				}
			}
			if(b){
				newList.add(id);
				System.out.println("containerId-----:"+id);
			}
		}
		
		System.out.println("newList==================:"+newList.size());
		
		TParm tableDParm = table_D.getParmValue() ;
		String seqNo = "";
		System.out.println("===============================开始写数据===================================");
		TParm inParmAll = new TParm();
		int count2 = 0 ;
		for(int k = 0 ; k < newList.size() ; k++ ){
			
			String containerId = (String)newList.get(k);
			//根据容器ID查询对应的麻精信息
			inParm.setData("CONTAINER_ID",containerId);
			
			//一个容器对应一个麻精药品
			TParm result = SPCOutStoreTool.getInstance().onQueryContainerDetail(inParm);
			String orderCode1 = result.getValue("ORDER_CODE",0);
			dispenseNo = getValueString("DISPENSE_NO");
			
			System.out.println("dispenseNo---------------:"+dispenseNo);
			System.out.println("orderCode1==============:"+orderCode1+":数量-----："+result.getCount());
			for(int i = 0 ; i < tableDParm.getCount("ORDER_CODE"); i++ ){
				String orderCode2 = tableDParm.getValue("ORDER_CODE",i) ;
				if(orderCode1.equals(orderCode2)){
					seqNo = tableDParm.getValue("SEQ_NO",i);
					break;
				}
			}
			
			//拿错药
			if(seqNo == null || seqNo.equals("")) {
				seqNo = "1000" ;
			}
			
			inParmAll.setData("DISPENSE_NO",dispenseNo);
			inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
			inParmAll.setData("CONTAINER_ID",containerId);
			inParmAll.setData("IS_BOXED","Y");
			inParmAll.setData("BOXED_USER",Operator.getID());
			inParmAll.setData("BOX_ESL_ID",boxEslId);
			inParmAll.setData("OPT_USER",Operator.getID());
			inParmAll.setData("OPT_TERM",Operator.getIP());
			inParmAll.setData("CABINET_ID",getValueString("CABINET_ID")); 
			TParm inParm2 = new TParm() ;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i) ;
				inParm2.setData("ORDER_CODE",count2,orderCode1);
				inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO",i));
				inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE",i));
				inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE",i));
				inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ",i));
				inParm2.setData("DISPENSE_NO",count2,dispenseNo);
				inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
				inParm2.setData("CABINET_ID",count2,""); 
				inParm2.setData("IS_BOXED",count2,"Y");
				inParm2.setData("BOXED_USER",count2,Operator.getID());
				inParm2.setData("BOX_ESL_ID",count2,boxEslId);
				inParm2.setData("CONTAINER_ID",count2,rowParm.getValue("CONTAINER_ID",i));
				inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC",i));
				inParm2.setData("OPT_USER",count2,Operator.getID());
				inParm2.setData("OPT_TERM",count2,Operator.getIP());
				count2++ ;
			}
			
			if(inParm2 != null ){
				inParmAll.setData("OUT_D",inParm2.getData());
			}
			//写数据到容器交易表主表与明细表,更新实际出库数量
			TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
			                                    "onInsertBatch", inParm);
		}*/
			
	}

	public void onSave(){
		
		TParm parm = table.getParmValue() ;
		setReturnValue(parm);
		timerRfid.cancel() ;
		taskRfid.cancel();
		this.closeWindow();
	}

}
