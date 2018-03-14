package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdo.dev.DevCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.device.Uitltool;

/** 
 * <p>Title: 设备扫描</p>
 *
 * <p>Description: 设备扫描</p>       
 * 
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author yuhb   
 * @version 1.0
 */
public class DevCheckControl extends TControl{
	private static List<String> dev_nos = new ArrayList<String>();
	// 自动保存定时器
	private Timer timer;
	// 自动保存任务
	private TimerTask task;
	// 定时器时间间隔
	private long period = 1000;
	// 定时器延时时间
	private long delay = 1000;
	// IP地址
	private String ip = "192.168.1.100";
	// RFID标签操作对象
	AlienClass1Reader reader; 
	//FLG 标记
	Boolean flg;
	
	/**
	 * 初始化
	 */
	public void onInit() {  
		getTTable("SEQCHECK_TABLE").addEventListener
		(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
		super.init();
	}
	   /**  
	    * 请领设备录入事件
	    * @param com Component
	    * @param row int
	    * @param column int
	    */      
	   public void onCreateEditComoponent(Component com,int row,int column){
	       //状态条显示
	       callFunction("UI|setSysStatus","");
	       if(!(com instanceof TTextField))
 	           return;            
	       TTextField textFilter = (TTextField)com;
	       textFilter.onInit();         
	       TParm parm = new TParm();     
	       //设置弹出菜单        
	       textFilter.setPopupMenuParameter("DEV_STARTCODE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
	       //定义接受返回值方法         
	       textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
	   } 
	   /**
	    * 请领设备录入返回参数
	    * @param tag String
	    * @param obj Object
	    */                 
	   public void popReturn(String tag,Object obj){
	       //判断对象是否为空和是否为TParm类型
	       if (obj == null && !(obj instanceof TParm)) {
	           return;
	       }  
	       //类型转换成TParm  
	       TParm parm = (TParm) obj;    
	       TParm tableParm = new TParm();   
	       tableParm.setData("DEV_CODE",parm.getData("DEV_CODE"));  
	       tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
	  }


	/**
	 * 自动任务初始化
	 */
	public void schedule() {
		this.task = new TimerTask() {
			@Override
			public void run() {
				try {
				//	autoCheck();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		this.timer.schedule(task, this.delay, this.period);
	}
	
	
	/**
	 * 初始化定时器
	 */
	public void initTimer() {
		this.timer = new Timer();
		this.delay = this.period;
		this.task = new TimerTask() {
			@Override
			public void run() {
			}
		};
	}
	
	/**
	 * 取消自动定时任务
	 */
	public void cancel() {
		this.timer.cancel();
	}
	
	/**
	 * 开始
	 * @throws AlienReaderException
	 */
	public void onStart() throws AlienReaderException{
		this.clearValue("SEQCHECK_TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		this.callFunction("UI|START|setEnabled", false);
		this.callFunction("UI|END|setEnabled", true);
		reader = new AlienClass1Reader(ip, 23);
		reader.open();
		reader.setFactorySettings();
		reader.setDHCP(0);
		reader.setRFAttenuation(20);
		initTimer();
		schedule();
	}  
	
	/**
	 * 结束
	 */
	public void onEnd() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		reader.close();
		if (dev_nos.size() > 0) {
			TTable table = (TTable)getComponent("SEQCHECK_TABLE");
			TParm parm = table.getParmValue();
			String dev_no;
			for(int i=0; i<dev_nos.size(); i++) {
				dev_no = dev_nos.get(i);
				for(int j=0; j<parm.getCount(); j++) {
					if (dev_no.equals(parm.getRow(j).getValue("RFID"))) {
						parm.getRow(j).setData("REAL_NUM",1);
						table.setParmValue(parm);
						break;
					}
				}
			}
		}
		cancel();
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
	}
	
	
	public void onEnter() {
		String dev_no = getValueString("DEV_NOS");
		if (null == dev_no) {
			return;
		}
		for (int i = 0; i < dev_nos.size(); i++) {
			if (dev_no.equals(dev_nos.get(i))) {
				return;
			}
			TTable table = (TTable)getComponent("SEQCHECK_TABLE");
			TParm parm = table.getParmValue();
			for (int j = 0; j < parm.getCount(); j++) {
				if (dev_no.equals(parm.getRow(i).getData("RFID"))) {
					parm.getRow(j).setData("REAL_NUM",1);
					table.setParmValue(parm);
					break;
				}
			}
		}
	}
	
	/**
	 * 读取RFID
	 * @throws AlienReaderException
	 */
	public void autoCheck() throws AlienReaderException{
		reader.clearTagList();
		Tag[]  tagList = reader.getTagList();
		if (null != tagList) {
			for(int i=0; i<tagList.length; i++) {
				setValue("DEV_NOS", Uitltool.decode(tagList[i].getTagID().replace(
						" ", "")));
				onEnter();
			}
		}
		else {
			setValue("DEV_NOS", "");
		}
	}
	
	/**
	 * 保存盘点结果
	 */
	public void onSave() {
		TTable table = (TTable)getComponent("SEQCHECK_TABLE");
		TParm  parm = table.getParmValue();
		List<TParm> parmList = new ArrayList<TParm>();  // 需要更新的parm集合
		TParm tempParm = null; // 每一行parm
		String tempNo = null; // 每一行中的 REAL_NO值
		int tempQTY = 0;  // 每一行库存量的值
		for (int i = 0, n=parm.getCount(); i < n; i++) {
			tempParm = parm.getRow(i);   
//			  ORG_CODE          
//			  DEV_CODE             
//			  BATCH_SEQ             
//			  CHECKREASON_CODE      
//			  CHECK_TYPE         
//			  DOSAGE_UNIT          
//			  RETAIL_PRICE        
//			  STOCK_QTY           
//			  MODI_QTY             
//			  ACTUAL_CHECKQTY_DATE  
//			  ACTUAL_CHECK_QTY    
//			  ACTUAL_CHECKQTY_USER  
//			  OPT_USER             
//			  OPT_DATE              
//			  OPT_TERM   
			
			String devCode = tempParm.getValue("DEV_CODE");
			String sql = " SELECT B.DEPT_CODE AS ORG_CODE " +
					" A.DEV_CODE,B.BATCH_SEQ,A.UNIT_PRICE AS DOSAGE_UNIT" +  
					" FROM DEV_BASE A,DEV_STOCKD B " +
					" WHEER A.DEV_CODE = '"+devCode+"'" + 
					" AND A.DEV_CODE = B.DEV_CODE " +
					"";      
			TParm tparm = new TParm(TJDODBTool.getInstance().select("sql"));
			tempParm.setData("ORG_CODE",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("OPT_USER", Operator.getID()); 
			tempParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			tempParm.setData("OPT_TERM", Operator.getIP()); 
			//DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;
			//REAL_NO;OPT_DATE;GUAREP_DATE

//			  ORG_CODE              
//			  DEV_CODE              
//			  BATCH_SEQ             
//			  CHECKREASON_CODE      
//			  CHECK_TYPE            
//			  DOSAGE_UNIT          
//			  RETAIL_PRICE          
//			  STOCK_QTY             
//			  MODI_QTY             
//			  ACTUAL_CHECKQTY_DATE  
//			  ACTUAL_CHECK_QTY      
//			  ACTUAL_CHECKQTY_USER  
//			  OPT_USER               基本         
//			  OPT_DATE               基本 
//			  OPT_TERM               基本 
			tempNo = tempParm.getValue("REAL_NO");
			// 如果盘点值为空
			if (0 == tempNo.length()) {
				messageBox("第" + i + "行，请录入盘点数量！");
				return;
			}else { // 盘点值不为空，如果盘点数和库存数相等，不动作，否则添加进更新列表中。
				int no = tempNo.matches("\\d+") ? Integer.parseInt(tempNo) : -1;
				tempQTY = Integer.parseInt(tempParm.getValue("QTY"));
				switch (no) {
				case 0:
				case 1:
					if (no == tempQTY) {
						parmList.add(tempParm);
					}
					continue;
				case -1:
					messageBox("第" + i + "行，请输入数字！");
					return;
				default:
					messageBox("第" + i + "行，只能输入0或者1！");
					return;
				}
			}
		}
		TParm updateParm = new TParm();
		updateParm.setData("update", parmList);
		parm = TIOM_AppServer.executeAction(
		           "action.dev.DevAction","updateStockByCheck", updateParm);
       if(parm.getErrCode() < 0){
           messageBox("保存失败");
           return;
       }
       messageBox("保存成功");
	}
	
	
	
	/**
	 * 查询前判断
	 * @return
	 */
	public boolean onQueryCheck() {
		String strStartCode = getValueString("DEV_STARTCODE");
		String strEndCode = getValueString("DEV_ENDCODE");
		// 起止设备编码需全不填写或者全部填写
		if (0 == strStartCode.length() && 
				0 == strEndCode.length()) {
			return false;
		}
		if (0 != strStartCode.length() && 
				0 != strEndCode.length()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 查询 
	 */
	public void onQuery() {
		if (onQueryCheck()) {
			return;
		}  
		String strQueryParm = "DEV_KEEPDEPT;DEV_LOCATIONDEPT;DEV_STARTCODE;DEV_ENDCODE";
		TParm parm = getQueryParm(strQueryParm);
		if ("Y".equals(getValue("QTY"))) {
			parm.setData("QTY",0);
		}
		parm = DevCheckTool.getInstance().selectSeqDevInf(parm);
		if (parm.getErrCode() < 0) {
			messageBox("查询失败");
			return;
		}
		TTable table = (TTable)getComponent("SEQCHECK_TABLE");
		table.setParmValue(parm);
	//	table.setmo  
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		// 清空查询条件
		String str = "DEV_KEEPDEPT;DEV_LOCATIONDEPT;DEV_CHECKDATE;DEV_STARTCODE;DEV_ENDCODE;QTY";
		clear(str);
		
		// 清空table
		((TTable)getComponent("SEQCHECK_TABLE")).removeRowAll();
	}
	/**
	 * 构造查询parm
	 * @param strParm
	 * @return
	 */
	public TParm getQueryParm(String strQueryParm) {
		String[] strParms = strQueryParm.split(";");
		TParm parm = new TParm();
		for (String string : strParms) {
			if(0 != getValueString(string).length()) {
				parm.setData(string,getValue(string));
			}
		}
		return parm;
	}
	
	/**
	 * 替换parm中的0、1值为异常、正常
	 * @param parm
	 * @param strs
	 * @param strName
	 * @return
	 */
	public TParm replaceParm(TParm parm, String[] strs, String strName) {
		if (0 != parm.getValue(strName).length()) {
			String[] dataStrings = parm.getValue(strName).replaceAll("\\[|\\]|\\s", "").split(","); 
			parm.removeData(strName);
			for (String string : dataStrings) {
				parm.addData(strName, strs[Integer.parseInt(string)]);
			}
		}
		return parm;
	}
	
	/**
	 * 将传入的字符串各字段清空 
	 * @param str
	 */
	public void clear(String str) {
		String[] strs = str.split(";");
		for (String string : strs) {
			setValue(string, "");
		}
	}
	   /**
	    * 得到表格栏位名
	    * @param tableTag String
	    * @param column int
	    * @return String
	    */
	   public String getFactColumnName(String tableTag,int column){
	        int col = getThisColumnIndex(column);
	        return getTTable(tableTag).getDataStoreColumnName(col);
	    }
	   /**  
	     * 得到表格栏位索引
	     * @param column int
	     * @return int
	     */
	    public int getThisColumnIndex(int column){  
	        return getTTable("DEV_REQUEST").getColumnModel().getColumnIndex(column);
	    }
	   /**
	    * 拿到TTable
	    * @param tag String
	    * @return TTable
	    */
	   public TTable getTTable(String tag){
	       return (TTable)getComponent(tag);
	    }
}
