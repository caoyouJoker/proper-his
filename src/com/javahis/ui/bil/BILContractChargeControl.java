package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILContractPayTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.opb.OPBReceipt;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 合同单位缴费
 * </p>
 * 
 * <p>
 * Description: 合同单位缴费
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractChargeControl extends TControl{
	
	

	//action的路径
	private static final String actionName = "action.bil.BILContractAction";
	
	TParm endBillPayParm;
	
    //选项卡控制
	
	// 上次点选的页签索引
	private int lastSelectedIndex = 0;
	private TTabbedPane TabbedPane;
	/**
	 * 公共部分
	 * */ 
	private TComboBox REGION_CODE;
	private TTextFormat CONTRACT_CODE;
	private TTextFormat START_DATE;
	private TTextFormat END_DATE;
	private TComboBox CASHIER_CODE;
	
	int no = 1;
	double arAmt = 0;
	/**
	 * 第一个页签的控制    合同单位信息
	 * */
	private TTable table1;//合同单位表
	
	/**
	 * 第二个页签的控制    病人列表
	 * */  
	private TTable table2;//病人列表
    
	/**
	 * 第三个页签的控制   收据列表
	 * */
	private TTable table3;//收据表
	private TTextField PRINT_NO;
    
	/**
	 * 第四个页签的控制    结算信息
	 * */
	private TTextField COUNT;//收据数目
	private TTextField REFUND;//预交金冲销
	private TTextField CHARGE;//应收金额
	private TTextField PAY;//本次应缴
	private TTable table4;
	private TButton ADD;
	private TTable table_pay;//预交金表
	/**
	 * 第五个页签的控制   结算列表
	 * */
	private TTable table5;
	private TTable table6;
	
	 double bilPayAmt = 0.00;
	 double enArAmt = 0;
	 String startInvno = "";
	 String printNo ="";
	 
    int selectRow = -1;
    public BILContractChargeControl() {

    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //获得全部控件
        getAllComponent();        
        //初始化时间
        initDate();
        this.checkNo();
        //查询全部信息
        this.onQuery();
        //设置院区信息
        this.setValue("REGION_CODE", Operator.getRegion());
        //添加表的监听信息
        //合同单位信息表
		callFunction("UI|Table1|addEventListener", "Table1->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable1");		
		//预交金表的监听
		 getTTable("table_pay").addEventListener(
	                TTableEvent.CHECK_BOX_CLICKED, this, "onBillPayTableComponent");
		 //病人列表监听
		 getTTable("Table2").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableClickedForTable2");
		 //收据列表监听
		 getTTable("Table3").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableClickedForTable3");
		
    }
    
    /**
     * 得到TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 合同单位表监听事件
     * @param obj
     * @return void
     * */
    public void onTableClickedForTable1(int row){
    	if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		table1.acceptText();
		// 合同代码不可编辑
		CONTRACT_CODE.setEnabled(false);
		
		/*
		 * 获取选中的数据，将这些数据设置到各个输入控件中
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("REGION_CODE", row))));
		CONTRACT_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_CODE", row))));
		
    }
    
    /**
	 * 将null转换为空字符串
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}
    
    /**
     * 预交金table监听事件
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillPayTableComponent(Object obj) {
        TTable billPayTable = (TTable) obj;
        billPayTable.acceptText();
        TParm tableParm = billPayTable.getParmValue();
        endBillPayParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        //System.out.println("预交金明细"+tableParm);
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PRE_FLG", i)) {
                endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                        "RECEIPT_NO", i));
                endBillPayParm.addData("PRE_AMT", tableParm.getValue("PRE_AMT",
                        i));
            }
        }
        bilPayAmt = 0.0;
        int feeCount = endBillPayParm.getCount("PRE_AMT");
       
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
    
        setValue("REFUND", String.valueOf(bilPayAmt));//预交金冲销
     
        
        if(getValueDouble("CHARGE")>=0){
        	enArAmt = TypeTool.getDouble(getValue("CHARGE")) - bilPayAmt;
        }
        this.setValue("PAY", String.valueOf(enArAmt));//本次应缴纳

        return true;
    }
    /**
     * 病人列表监听
     * */
    public boolean onTableClickedForTable2(Object obj){
    	//System.out.println("病人列表监听");
    	  TTable Table2 = (TTable) obj;
    	  Table2.acceptText();
          TParm tableParm = Table2.getParmValue();
          endBillPayParm = new TParm();
          int count = tableParm.getCount();
      
          for (int i = 0; i < count; i++) {
              if (tableParm.getBoolean("STATUS", i)) {
                  endBillPayParm.addData("MR_NO", tableParm.getValue(
                          "MR_NO", i));
              }
          }


          return true;
    }
    /**
     * 收据列表监听
     * */
    public boolean onTableClickedForTable3(Object obj){
    	
    	  TTable Table3 = (TTable) obj;
    	  Table3.acceptText();
          TParm tableParm = Table3.getParmValue();
          endBillPayParm = new TParm();
          int count = tableParm.getCount();
        
          for (int i = 0; i < count; i++) {
              if (tableParm.getBoolean("FLG", i)) {
                  endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                          "RECEIPT_NO", i));
              }
          }


          return true;
    }
    
    /**
     * 初始化日期
     * */
    public void initDate(){
    	Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("START_DATE", yesterday);
		setValue("END_DATE", SystemTool.getInstance().getDate());
   }
    
    /**
     * 得到全部控件
     * */
    public void getAllComponent(){   	
    	/**
		 * 获得选项卡控件
		 */
		this.TabbedPane = (TTabbedPane) this.getComponent("TabbedPane");
		//公共部分
		START_DATE = (TTextFormat) this.getComponent("START_DATE");
		END_DATE = (TTextFormat) this.getComponent("END_DATE");
		REGION_CODE = (TComboBox)this.getComponent("REGION_CODE");
		CONTRACT_CODE = (TTextFormat)this.getComponent("CONTRACT_CODE");	   
	    CASHIER_CODE = (TComboBox)this.getComponent("CASHIER_CODE");
	    //合同单位
	    table1 = (TTable) this.getComponent("Table1");
	    //病人列表
		table2 = (TTable) this.getComponent("Table2");
		//收据列表
		table3 = (TTable) this.getComponent("Table3");
		PRINT_NO = (TTextField)this.getComponent("PRINT_NO");
		//结算信息
		table4 = (TTable) this.getComponent("Table4");
		COUNT =  (TTextField)this.getComponent("COUNT");//收据数目
		REFUND= (TTextField)this.getComponent("REFUND"); //预交金冲销
		CHARGE =  (TTextField)this.getComponent("CHARGE"); //应收金额
		PAY =  (TTextField) this.getComponent("PAY");//本次应缴		
		ADD =  (TButton)this.getComponent("ADD");//增加按钮
		table_pay = (TTable)this.getComponent("table_pay");//预交金表
		
		//结算列表
		table5 = (TTable) this.getComponent("Table5");
		table6 = (TTable) this.getComponent("Table6");
    }
   /**
    * 判断合同单位病患的门急诊预交金+信誉额度是否可以支付本次待缴费用
    *   传入参数：病案号、合同单位编号、待缴费用金额
    *   返回：布尔值 是或否 
    * */ 
    
    public boolean isEnough(TParm parm,int i){
        System.out.println("parm:"+parm);
    	boolean result = false;
    	String mr_no = parm.getValue("MR_NO",i);
    	String contract_code = parm.getValue("CONTRACT_CODE",i);
    	Double ar_amt = parm.getDouble("AR_AMT",i);
    	//1.查询病人合同单位预交金余额和信誉额度
    	String sql1 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+contract_code+"'";
    	TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
    	Double prepay_amt_contract = selParm1.getDouble("PREPAY_AMT",0);
    	Double limit_amt_contract = selParm1.getDouble("LIMIT_AMT",0);
    	//2.患者的预交金余额和信誉额度
    	String sql2 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTD WHERE CONTRACT_CODE = '"+contract_code+"' AND MR_NO = '"+mr_no+"'";
    	TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
    	Double prepay_amt = selParm2.getDouble("PREPAY_AMT",0);
    	Double limit_amt = selParm2.getDouble("LIMIT_AMT",0);
    	//3.判断
    	if(ar_amt <= prepay_amt_contract+limit_amt_contract+prepay_amt+limit_amt){
    		result = true;
    	}
    	return result;
    }
    /**
     * 补印
     * 
     * */
    public void onPrint(){
    	
    }
    
    /**
     * 作废账单
     * */
    public void onReturn(){
    	
    }
    /**
	 * 保存方法， 执行修改OPD_ORDER 表中 PRINT_FLG=Y AND PRINT_NO 保存值 修改 BIL_REG_RECP 表中
	 * PRINT_DATE 保存时间 PRINT_NO 保存值 添加 BIL_INVRCP 表数据 修改 BIL_INVOICE 表数据增加票据号操作
	 * 修改 BIL_OPB_RECP 表中 PRINT_DATE 保存时间 PRINT_NO 保存值
	 */
	public void onSave() {
		
//		BilInvoice bilInvoice = new BilInvoice();
//		//初始化票据
//       	if (!initBilInvoice(bilInvoice.initBilInvoice("OPB"))) {//票据类型选择门诊票据
//            return;
//        } 		
		if(!checkNo()){
			return;
		}
		if (null==this.getValue("CASHIER_CODE") || this.getValue("CASHIER_CODE").toString().length()<=0) {
			this.messageBox("请选择记账人员");
			this.grabFocus("CASHIER_CODE");
			return;		
		}	    
		TParm newParm=new TParm();				
		TParm tableParm = table3.getParmValue();
		boolean isY = false;
		int count=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				if ("Y".equals(tableParm.getValue("FLG", i))
						&& "2".equals(tableParm.getValue("BIL_STATUS",
								i))) {
					this.messageBox("账单号为:"+tableParm.getValue("RECEIPT_NO",i)+"已经完成,不可以修改");
					continue;
				}
				   //判断支付函数？？？？？？？？？？	
				if(!isEnough(tableParm,i)){
					this.messageBox("账单号为："+tableParm.getValue("RECEIPT_NO",i)+"的病人额度不足！");
					continue;
				}
				isY = true;
				newParm.setRowData(count,tableParm,i);
				count++;
			}
		}
		newParm.setCount(count);
		newParm.getRow(0);
		TParm payParm = new TParm();
		// 有执行的数据
		//*************************************
		TParm tablePayParms = table_pay.getParmValue();
		int payCount = tablePayParms.getCount("RECEIPT_NO");
	      
	        for (int i = 0; i < payCount; i++) {
	            if (tablePayParms.getBoolean("PRE_FLG", i)) {
	            	TParm actionPayParm = new TParm(); 
	            	actionPayParm.setData("RECEIPT_NO",tablePayParms.getValue(
	                        "RECEIPT_NO", i));//1	            		            		       	       
	            	actionPayParm =  BILContractPayTool.getInstance().selectData("selectByReceipt",actionPayParm);//根据RECEIPT_NO查询预交金信息
	       	      
	            	TParm endParm = new TParm();
	       	        endParm = actionPayParm.getRow(0);
	       	        endParm.setData("BUSINESS_TYPE","3");//冲销	       	            
	       	        endParm.setData("CASHIER_CODE", Operator.getID());
	       	        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());	       	       	     
	       	        endParm.setData("OPT_USER", Operator.getID());
	       	        endParm.setData("OPT_TERM", Operator.getIP());
	       	        endParm.setData("OPT_DATE",SystemTool.getInstance().getDate());	  	       	    
	       	        payParm.setRowData(payCount,endParm);
	       	        
	            }
	        }
	        payParm.setCount(payCount); 	    		      
		//*************************************
		if (isY) {
			TParm parm = new TParm();
			parm.setData("OPT_USER", this.getValue("CASHIER_CODE"));
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("recodeParm", newParm.getData());
			parm.setData("payParm",payParm.getData());
			//System.out.println("保存时的 parm：：："+parm);
			TParm result = TIOM_AppServer.executeAction(
					actionName, "onSaveContract", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("执行失败");
				return;
			}
			this.messageBox("执行成功");
			//打票	分两步部分：1.冲销预交金的票   2.交完费的票
	        TParm forPrtParm = new TParm();
	        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
	                getDouble(getValue("CHARGE")));
	        Timestamp printDate = (SystemTool.getInstance().getDate());
	        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
	        forPrtParm.setData("COPY", "TEXT", "");
	        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
	                           Operator.getHospitalCHNFullName());
	        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
	        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
	        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
	                           Operator.getHospitalENGFullName());

	        forPrtParm.setData("Data", "TEXT",  pDate);
	        String sql = "SELECT CONTRACT_DESC FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+this.getValue("CONTRACT_CODE")+"'";
	        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
	        String contract_desc = selParm.getValue("CONTRACT_DESC",0);
	        forPrtParm.setData("Name", "TEXT", contract_desc);
	    
	        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
	   
	        forPrtParm.setData("MR_N0", "TEXT", this.getValue("CONTRACT_CODE"));
	        forPrtParm.setData("Capital", "TEXT",  bilPayC);
	        DecimalFormat formatObject = new DecimalFormat("###########0.00");

	        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
	                           TypeTool.getDouble(this.getValueString("CHARGE"))));
	        forPrtParm.setData("SEQ_NO", "TEXT",
	                           "流水号:" + this.getValueString("RECEIPT_NO"));
	        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);

	        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
	                             forPrtParm,true);
	        //=================================
	     // 调用处理打印的方法
	        TParm receiptParm = new TParm();
			dealPrintData(receiptParm);
		} else
			this.messageBox("请选择要执行的数据");
		
		onQuery();

	}
	//============================
	/**
	 * 处理打印数据
	 * 
	 * @param receiptNo
	 *            String[]
	 */
	public void dealPrintData(TParm receiptParm) {

		TParm oneReceiptParm =  new TParm();
		receiptParm.setData("OPT_USER", Operator.getName());
		receiptParm.setData("OPT_ID", Operator.getID());
		receiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw",
				oneReceiptParm);
	
	}	
	//============================
    /*
     * 新增按钮
     * **/
	public void onAdd(){		
		if(table4.getRowCount()>0){
			this.messageBox("已经新增！");
			return;
		}
		this.getValue("COUNT");//收据数目
		this.getValue("REFUND");//预交金冲销
		this.getValue("CHARGE");//应收金额
		this.getValue("PAY");//本次应缴 
		TParm parm = new TParm();
		arAmt += Double.parseDouble(this.getValue("CHARGE").toString());		
		parm.setData("NO",String.valueOf(no));
		parm.setData("DEL_FLG","N");
		parm.setData("PAY_TYPE","现金");
		parm.setData("AR_AMT",this.getValue("CHARGE").toString());
		parm.setData("PRINT_NO",printNo);
		parm.setData("MARK","");		
		no ++;
		callFunction("UI|Table4|addRow", parm,
        "NO;DEL_FLG;PAY_TYPE;AR_AMT;PRINT_NO;MARK");	
		
	}
    
    /**
     * 查询
     * */
    public void onQuery(){
    	String region_code = this.getValueString("REGION_CODE");
    	String contract_code = this.getValueString("CONTRACT_CODE");
    	String cashier_code = this.getValueString("CASHIER_CODE");
    	String start_date = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String end_date = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		int count = 0 ;
		Integer counts = 0 ;
    	if(start_date==null || start_date.equals("") || end_date==null || end_date.equals("")){
    		this.messageBox("请选择开始和结束时间！");
    		return ;
    	}
    	// 获取页签索引
		int selectedIndex = TabbedPane.getSelectedIndex();
		//合同单位
    	if(selectedIndex == 0){
    		String sql = " SELECT CONTRACT_CODE,CONTRACT_DESC,DESCRIPTION,ADDRESS,TEL1,TEL2,CONTACT,REGION_CODE "+
    		" FROM BIL_CONTRACTM "+
    		" WHERE DEL_FLG = 'N'" ;			
        	if(region_code!=null && !region_code.equals("")){
        		sql += " AND REGION_CODE = '"+region_code+"'";
        	}
        	if(contract_code!=null && !contract_code.equals("")){
        		sql += " AND CONTRACT_CODE = '"+contract_code+"'";
        	}
        	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        	table1.setParmValue(selParm);
    	}
    
    	//病人列表
    	if(selectedIndex == 1){
    		String sql1 = " SELECT DISTINCT 'N' AS STATUS ,A.MR_NO,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,B.PAT_NAME,B.CTZ1_CODE,R.REG_DATE"+
						  " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C,REG_PATADM R "+
						  " WHERE A.MR_NO = B.MR_NO "+
						  " AND A.CASE_NO = R.CASE_NO "+
						  " AND A.CASHIER_CODE = C.USER_ID "+
						  " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
						  " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
						  " AND A.BIL_STATUS = '1'";
    		
		   
    		if(region_code!=null && !region_code.equals("")){
        		sql1 += " AND A.REGION_CODE = '"+region_code+"'";
        	}
        	if(contract_code!=null && !contract_code.equals("")){
        		sql1 += " AND A.CONTRACT_CODE= '"+contract_code+"'";
        	}  
        	sql1 += " ORDER BY A.CASE_NO";
        	// System.out.println("sql1::::"+sql1);	
        	TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));      	
           	table2.setParmValue(selParm1);
    	}
    	//收据列表
    	if(selectedIndex == 2){
    		boolean isY = false ;    	
    		TParm newParm = new TParm();
    		TParm tableParm = table2.getParmValue();
    		//System.out.println("tableParm:::::::"+tableParm);   		
    		for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getBoolean("STATUS", i)) {
    				isY = true;
    				newParm.setRowData(count,tableParm,i);
    				count++;
    			}
    		}
    		newParm.setCount(count);    
    		// 有执行的数据
    		if (isY) {
    			String mrNo= newParm.getValue("MR_NO", 0);	
    			for(int i=1;i<count;i++ ){
    				String mr_no = newParm.getValue("MR_NO", i);
    				mrNo += ","+mr_no;
    			}
    			
    			String sql2 = " SELECT 'N' AS FLG,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,"+
					  " A.CHARGE_DATE,A.RECEIPT_TYPE,A.MR_NO,A.AR_AMT AS AR_AMT,A.AR_AMT AS PAY,A.BIL_STATUS,A.CASHIER_CODE,"+
					  " B.PAT_NAME,C.USER_NAME,B.CTZ1_CODE,A.RECEIPT_FLG "+
				 " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C "+
				 " WHERE A.MR_NO=B.MR_NO(+) "+
					   " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
					   " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
					   " AND A.BIL_STATUS='1'"+
					   " AND A.CASHIER_CODE=C.USER_ID "+  			
					   " AND A.MR_NO IN ("+mrNo+")";
				if(contract_code!=null && !contract_code.equals("")){
			 		    sql2 += " AND  A.CONTRACT_CODE = '"+contract_code+"'";
			    } 
				if(cashier_code!=null && !cashier_code.equals("")){
						sql2 += " AND A.CASHIER_CODE = '"+cashier_code+"'"; 
				}
//				String print_no = this.getValueString("PRINT_NO");
//				if(print_no != null && !print_no.equals("")){
//						sql2 += " AND A.RECEIPT_NO = '"+print_no+"'";
//				}
				//选择,40,Boolean;就诊号,100;病案号,100;收据号,100;结束标志,80,Boolean;已打票标志,100,Boolean;应交款,80;自费总额,80
				TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));				
	    		table3.setParmValue(selParm2); 		    		
			  }    		
    		   		
    	}
    	//结算信息
    	if(selectedIndex == 3){
    		boolean isY = false ;
    		double ar_amt = 0;
    		TParm newParm = new TParm();
    		TParm tableParm = table3.getParmValue();    		
    		for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getBoolean("FLG", i)) {
    				isY = true;
    				newParm.setRowData(counts,tableParm,i);
    				counts++;   			
    				ar_amt += tableParm.getDouble("AR_AMT",i);
    			}
    		}
    		newParm.setCount(count);   	
    		//收据数目    	  
    		COUNT.setValue(String.valueOf(counts));
    		//预交金冲销
    		REFUND.setValue(String.valueOf(bilPayAmt));
    		//应收金额
    		CHARGE.setValue(String.valueOf(ar_amt));
    		enArAmt = ar_amt - bilPayAmt;
    		//本次应缴
    		PAY.setValue(String.valueOf(enArAmt));
    		
    		// 有执行的数据
    		if (isY) {
    			String mrNo= newParm.getValue("MR_NO", 0);	
    			for(int i=1;i<count;i++ ){
    				String mr_no = newParm.getValue("MR_NO", i);
    				mrNo += ","+mr_no;
    			}
    			String sql2 = " SELECT 'N' AS FLG,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,"+
					  " A.CHARGE_DATE,A.RECEIPT_TYPE,A.MR_NO,A.AR_AMT AS AR_AMT,A.AR_AMT AS PAY,A.BIL_STATUS,A.CASHIER_CODE,"+
					  " B.PAT_NAME,C.USER_NAME,B.CTZ1_CODE,A.RECEIPT_FLG "+
				 " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C "+
				 " WHERE A.MR_NO=B.MR_NO(+) "+
					   " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
					   " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
					   " AND A.BIL_STATUS='1'"+
					   " AND A.CASHIER_CODE=C.USER_ID "+  			
					   " AND A.MR_NO IN ("+mrNo+")";
				if(contract_code!=null && !contract_code.equals("")){
			 		sql2 += " AND  A.CONTRACT_CODE = '"+contract_code+"'";
			    } 
				if(cashier_code!=null && !cashier_code.equals("")){
						sql2 += " AND A.CASHIER_CODE = '"+cashier_code+"'"; 
				}
				String print_no = this.getValueString("PRINT_NO");
				if(print_no != null && !print_no.equals("")){
						sql2 += " AND A.RECEIPT_NO = '"+print_no+"'";
				}
				//预交金
				//选,50,boolean;票据号,100;收费人员,100;收费日期,100;预交金额,100;支付类型,100;备注,100
				//PRE_FLG;RECEIPT_NO;CASHIER_CODE;CHARGE_DATE;PRE_AMT;PAY_TYPE;REMARK
				String sql3 = "SELECT 'N' AS PRE_FLG , RECEIPT_NO,CASHIER_CODE,CHARGE_DATE,PRE_AMT,PAY_TYPE,REMARK "+
								" FROM BIL_CONTRACT_PAY "+
								" WHERE CONTRACT_CODE = '"+contract_code+"'"+
								 " AND BUSINESS_TYPE = 1 "+
								 " AND REFUND_FLG = 'N' " +
								 " AND OFF_RECP_NO IS NULL";
				TParm selParm3 = new TParm(TJDODBTool.getInstance().select(sql3));
				table_pay.setParmValue(selParm3);
				TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));			  
	    		table6.setParmValue(selParm2); 	
	    		
			  }
    	}
    	//结算列表
    	if(selectedIndex == 4){
    		String contractCode = this.getValueString("CONTRACT_CODE");
    		TParm parm = new TParm();
    		//CONTRACT_CODE;CONTRACT_DESC;PRINT_NO;AR_AMT;CASHIER_CODE
    		String sqls = "SELECT A.CONTRACT_CODE,A.CONTRACT_DESC,'' AS PRINT_NO,CASHIER_CODE " +
    				"FROM BIL_CONTRACTM A,BIL_CONTRACT_RECODE B "+
					" WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
					" AND A.CONTRACT_CODE = '"+contract_code+"' "+
					" GROUP BY A.CONTRACT_CODE,A.CONTRACT_DESC,B.CASHIER_CODE ";   		
    		TParm selParm = new TParm(TJDODBTool.getInstance().select(sqls));    		
    		TParm tableParm = new TParm();
    		tableParm.addData("CONTRACT_CODE",selParm.getData("CONTRACT_CODE",0));
    		tableParm.addData("CONTRACT_DESC",selParm.getData("CONTRACT_DESC",0));
    		tableParm.addData("PRINT_NO",printNo);   		
    		tableParm.addData("AR_AMT",enArAmt);
    		tableParm.addData("CASHIER_CODE",selParm.getData("CASHIER_CODE",0));   	
    		//table5.addRow(tableParm); 
    		table5.setParmValue(tableParm);
    	}
    	
    }
    /**
     * 清空
     * */
    public void onClear(){
    	this.setValue("REGION_CODE", "");
    	this.setValue("CONTRACT_CODE", "");
    	this.setValue("CASHIER_CODE", "");
    	CONTRACT_CODE.setEditBoard(true);
    	initDate();
    	// 获取页签索引
		int selectedIndex = TabbedPane.getSelectedIndex();
		
		//合同单位
    	if(selectedIndex == 0){
    		table1.removeRowAll();
    	}
    	//病人列表
    	if(selectedIndex == 1){
    		table2.removeRowAll();
    		
    	}
    	//收据列表
    	if(selectedIndex == 2){
    		table3.removeRowAll();
    		this.setValue("PRINT_NO", "");
 
    	}
    	//结算信息
    	if(selectedIndex == 3){
    		table4.removeRowAll();
       		this.setValue("COUNT", "");
    		this.setValue("REFUND", "");
    		this.setValue("CHARGE", "");
    		this.setValue("PAY", "");
    	}
    	//结算列表
    	if(selectedIndex == 4){
    		table5.removeRowAll();
    		table6.removeRowAll();
    	}
    }
   
    /**
	 * 当选项卡切换时 调用此方法
	 */
	public void onChange() {
		// 获取页签索引
		int selectedIndex = TabbedPane.getSelectedIndex();
		// 查询菜单控制
		//menuControl();
		// 若当前点选的页签索引为0（合同单位），则不作任何处理直接返回
		if (selectedIndex <= 0) {
			//区域不可编辑
			this.REGION_CODE.setEditable(false);
			// 记录此次点选的页签索引
			lastSelectedIndex = selectedIndex;
			
			return;
		}
		// 若当前点选的页签索引为1（病人列表），则验证是否选择了合同单位
		if (selectedIndex == 1) {
			if (table1.getSelectedRow() < 0) {
				this.messageBox("请选择合同单位信息！");
				TabbedPane.setSelectedIndex(0);
				
				return;
			}
			this.onQuery();
			//清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 0) {			
				// 模糊查询患者
			   this.onQuery();
			}
		}
		//若当前点选的页签索引为2（收据列表）,则验证是否选择了病患信息
		if (selectedIndex == 2) {
			if (table2.getSelectedRow() < 0) {
				this.messageBox("请选择合病患信息！");
				TabbedPane.setSelectedIndex(1);
				return;
			}
			//初始化部分控件
		    this.onQuery();
			//清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 1) {
				// 模糊查询患者
			    this.onQuery();
			}
		}
		//若当前点选的页签索引为3（结算信息）,则验证是否选择了收据列表
		if (selectedIndex == 3) {
			if (table3.getSelectedRow() < 0) {
				this.messageBox("请选择收据信息！");
				TabbedPane.setSelectedIndex(2);
				return;
			}
			//初始化部分控件
		    this.onQuery();
			//清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 2) {				
				// 模糊查询患者
			    this.onQuery();
			}
		}
		//若当前点选的页签索引为4（结算列表）,则验证是否选择了结算信息
		if (selectedIndex == 4) {
			if (table4.getSelectedRow() < 0) {
				this.messageBox("请选择结算信息！");
				TabbedPane.setSelectedIndex(3);
				return;
			}
			//初始化部分控件
		     this.onQuery();
			//清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 3) {			
				// 模糊查询患者
				this.onQuery();
			
			}
		}
	}
	/**
	 * 公用方法：记账方法
	 * 呼叫：由门急诊挂号、门急诊收费、健康检查报到(加项)时候呼叫，
	 * 将支付方式为记账该次收费记录到BIL_CONTRACT_RECODE 表中
	 * */
	public TParm onDebit(TParm parm){
		TParm result = new TParm();
    	//判断是否允许记账及额度管理
    	String contract_code = parm.getValue("CONTRACT_CODE");
    	String mr_no = parm.getValue("MR_NO");
    	Double pay = parm.getDouble("AR_AMT");
    	//1.查询个人限制金额
    	String sql = "SELECT LIMIT_AMT FROM BIL_CONTRACTD WHERE CONTRACT_CODE = '"+contract_code+"' AND MR_NO = '"+mr_no+"'";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	Double limit_amt = selParm.getDouble("LIMIT_AMT");
    	//个人限制金额不为空
    	//个人可用金额  = 个人限制金额 – 累计记账金额
    	if(limit_amt!=null &&limit_amt !=0){
    		//累计记账金额
    		String sql1 = "SELECT SUM(AR_AMT) AS AR_AMT FROM BIL_CONTRACT_RECORD WHERE MR_NO = '"+mr_no+"' AND BIL_STATUS = 1";
    		TParm selParm1 =  new TParm(TJDODBTool.getInstance().select(sql1));
    		Double ar_amt = selParm1.getDouble("AR_AMT");
    		//个人可用金额
    		Double sum = limit_amt - ar_amt;
    		//应收金额 - 个人可用金额 <= 0 	写入 bil_contract_recode , 记账
    		if(pay <= sum){
    			 result = TIOM_AppServer.executeAction(
    					"action.bil.BILContractRecordAction", "insertRecode", parm);
    			if (result.getErrCode() < 0) {
    				err(result.getErrCode() + " " + result.getErrText());
    				this.messageBox("记账失败");
    			} 
    		}else{
    			this.messageBox("额度不足，不可记账");
    		}
    	//个人限制金额为空
    	//企业可用额度  = 预交金余额 + 信用额度
    	}else{
    		String sql2 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+contract_code+"'";
    		TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		Double prepay_amt = selParm2.getDouble("PREPAY_AMT");
    		Double limit_amts = selParm2.getDouble("LIMIT_AMT");
    		Double sum = prepay_amt + limit_amts;
    		if(pay <= sum){
    			result = TIOM_AppServer.executeAction(
    					"action.bil.BILContractRecordAction", "insertRecode", parm);
    			if (result.getErrCode() < 0) {
    				err(result.getErrCode() + " " + result.getErrText());
    				this.messageBox("记账失败");
    			} 
    		}else{
    			this.messageBox("额度不足，不可记账");
    		}
    	}
    	
    	return result;
	}
	
//	/**
//	 * 初始化票据
//	 * 
//	 * ===zhangp 20120731
//	 * 
//	 * @param bilInvoice
//	 *            BilInvoice
//	 * @return boolean
//	 */
//	private boolean initBilInvoice(BilInvoice bilInvoice) {
//		// 检核开关帐
//		if (bilInvoice == null) {
//			this.messageBox_("你尚未开账!");
//			return false;
//		}
//		// 检核当前票号
//		if (bilInvoice.getUpdateNo().length() == 0
//				|| bilInvoice.getUpdateNo() == null) {
//			this.messageBox_("无可打印的票据!");
//			return false;
//		}
//		// 检核当前票号
//		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
//			this.messageBox_("最后一张票据!");
//		}
//		
//		if (BILTool.getInstance().compareUpdateNo("HPAY", Operator.getID(),
//				Operator.getRegion(), bilInvoice.getUpdateNo())) {
//			printNo= bilInvoice.getUpdateNo();	
//			System.out.println("printNo:::----->"+printNo);
//			startInvno = bilInvoice.getStartInvno();
//			System.out.println("startInvno:::------->"+startInvno);
//		} else {
//			messageBox("票据已用完");
//			return false;
//		}
//		return true;
//	}
	
	/**
     * 校验开关帐
     *
     * @return boolean
     */
    public boolean checkNo() {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", "OPB");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        printNo = noParm.getValue("UPDATE_NO", 0);
        startInvno = "";
        startInvno = noParm.getValue("START_INVNO", 0);
        if (printNo == null || printNo.length() == 0) {
            return false;
        }
        // 初始化下一票号
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("OPB");
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("票据已用完!");
            return false;
        }
        setValue("PRINT_NO", printNo);
        return true;
    }
 
//	/**
//	 * 全选按钮
//	 */
//	public void onSelectAll() {
//		TParm parm = table.getParmValue();
//		if (parm.getCount() <= 0) {
//			return;
//		}
//		// 全选勾选
//		if (this.getValueBoolean("CHK_ALL")) {
//			for (int i = 0; i < parm.getCount(); i++) {
//				parm.setData("FLG",i,"Y");
//				
//			}
//		} else {
//			for (int i = 0; i < parm.getCount(); i++) {
//				parm.setData("FLG",i,"N");	
//			}
//			
//		}
//		table.setParmValue(parm);
//	}
}
