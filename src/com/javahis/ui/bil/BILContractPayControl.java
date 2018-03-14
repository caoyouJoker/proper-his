package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILContractPayTool;
import jdo.bil.BILPayTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;




/**
 * 
 * <p>
 * Title: 合同单位预交金和信誉额度维护
 * </p>
 * 
 * <p>
 * Description: 合同单位预交金和信誉额度维护
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
public class BILContractPayControl extends TControl {

	// action的路径
	private static final String actionName = "action.bil.BILContractAction";


	private TTable table;
	String startInvno = "";
	String printNo ="";
	public BILContractPayControl() {

	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		//得到表
		table =(TTable) this.getComponent("Table");
		// 查询全部信息
		this.onQuery();
		// 添加表的监听信息
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable");
	}

	/**
	 * 合同单位联动查询
	 * */
	public void onContractCompany(){
		String contract_code = this.getValueString("CONTRACT_CODE");
		//查询合同单位基本信息,并将信息显示到界面上
		String sql = "SELECT REGION_CODE,CONTRACT_CODE,CONTACT,TEL2,LIMIT_AMT,PREPAY_AMT"+
						" FROM BIL_CONTRACTM"+
						" WHERE CONTRACT_CODE = '"+contract_code+"'";			
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(selParm.getCount()<=0){
			this.onClear();
			return;
		}
		this.setValue("REGION_CODE", selParm.getData("REGION_CODE",0));
		this.setValue("CONTRACT_CODE", selParm.getData("CONTRACT_CODE",0));
		this.setValue("CONTACT", selParm.getData("CONTACT",0));
		this.setValue("TEL", selParm.getData("TEL2",0));
		this.setValue("LIMIT_AMT", selParm.getData("LIMIT_AMT",0)==null ? 0.0 : selParm.getData("LIMIT_AMT",0).toString());//信用额度		
		this.setValue("PRE_AMT", selParm.getData("PREPAY_AMT",0)==null ? 0.0 : selParm.getData("PREPAY_AMT",0).toString());//预交金余额							
		onQuery();
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
	 * 监听表
	 * */
	public void onTableClickedForTable(int row){
		if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		table.acceptText();		
		
		/*
		 * 获取选中的数据，将这些数据设置到各个输入控件中
		 */
		String receipt_no = nullToString(String.valueOf(table.getParmValue().getData("RECEIPT_NO",row)));
		String sql = "SELECT CONTRACT_CODE FROM BIL_CONTRACT_PAY WHERE RECEIPT_NO = '"+receipt_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<=0){
			return;
		}
		String contract_code = parm.getValue("CONTRACT_CODE",0);
		
		String sql1 = " SELECT A.PAY_TYPE AS PAY_TYPE,A.REMARK AS REMARK,A.RECEIPT_NO AS RECEIPT_NO,A.CHARGE_DATE AS CHARGE_DATE,A.PRE_AMT AS PRE_PAY,A.BUSINESS_TYPE," +
				" B.REGION_CODE AS REGION_CODE,B.CONTRACT_CODE AS CONTRACT_CODE ,B.TEL2 AS TEL,B.LIMIT_AMT AS LIMIT_AMT,B.PREPAY_AMT AS PRE_AMT,B.CONTACT"+
				" FROM BIL_CONTRACT_PAY A,BIL_CONTRACTM B "+
		        " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
		        " AND A.CONTRACT_CODE = '"+contract_code+"'" +
        		" AND A.RECEIPT_NO = '"+receipt_no+"' ";
		
		TParm parms = new TParm(TJDODBTool.getInstance().select(sql1));		
		this.setValue("REGION_CODE",parms.getData("REGION_CODE", 0));
		this.setValue("CONTRACT_CODE", parms.getData("CONTRACT_CODE", 0));
		this.setValue("CONTACT", parms.getData("CONTACT", 0));
		this.setValue("TEL", parms.getData("TEL", 0));
		this.setValue("LIMIT_AMT",parms.getValue("LIMIT_AMT", 0).toString());
		this.setValue("PRE_AMT",parms.getValue("PRE_AMT",0).toString());
		//System.out.println(parms.getData("PRE_PAY", 0));
		this.setValue("PRE_PAY", parms.getValue("PRE_PAY",0).toString());
		this.setValue("PAY_TYPE",parms.getData("PAY_TYPE", 0));
		this.setValue("REMARK",parms.getData("REMARK", 0));
		
	}
	/**
	 * 查询
	 * */
	public TParm onQuery() {
		//查询某个合同单位下的所有预交金信息
		String contract_code = this.getValueString("CONTRACT_CODE");
		String sql = " SELECT A.PAY_TYPE AS PAY_TYPE,A.REMARK AS REMARK,A.RECEIPT_NO AS RECEIPT_NO,A.CHARGE_DATE AS CHARGE_DATE,A.PRE_AMT AS PRE_PAY,A.BUSINESS_TYPE," +
						" B.REGION_CODE AS REGION_CODE,B.CONTRACT_CODE AS CONTRACT_CODE ,B.TEL2 AS TEL,B.LIMIT_AMT AS LIMIT_AMT,B.PREPAY_AMT AS PRE_AMT,A.REFUND_CODE,TO_CHAR(A.REFUND_DATE,'yyyy/MM/dd') AS REFUND_DATE,A.OFF_RECP_NO"+
						" FROM BIL_CONTRACT_PAY A,BIL_CONTRACTM B "+
                        " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
                        " AND A.CONTRACT_CODE = '"+contract_code+"'";
		 
		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("selParm:"+sql);
		 this.callFunction("UI|Table|setParmValue", selParm);	
		 return selParm;
	}
	/**
	 * 保存
	 * */
	public void onSave(){
		BilInvoice bilInvoice = new BilInvoice();
		//初始化票据
       	if (!initBilInvoice(bilInvoice.initBilInvoice("HPAY"))) {//票据类型还需要确认
            return;
        }      
        if (this.getValueString("CONTRACT_CODE") == null ||
            this.getValueString("CONTRACT_CODE").length() == 0) {
            this.messageBox("请输入合同单位");
            return;
        }
        if (getValue("PRE_PAY") == null ||
            TypeTool.getDouble(getValue("PRE_PAY")) <= 0) {
            this.messageBox("输入金额有误!");
            return;
        }
        //不能输入空值
        if (!this.emptyTextCheck("PAY_TYPE")){       	
        	return;
        }
        //支付方式不能为记账
        if(this.getValue("PAY_TYPE").equals("PAY_DEBIT")){
        	this.messageBox("支付方式不能为记账");
        	return;
        }
     
       //组合参数    
            TParm parm = new TParm();
            //2-15为bil_contract_pay的参数
            parm.setData("CONTRACT_CODE",this.getValue("CONTRACT_CODE"));//2
            parm.setData("BUSINESS_TYPE","1");//3
            parm.setData("CASHIER_CODE", Operator.getID());//4
            parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());//5
            parm.setData("PRE_PAY",this.getValue("PRE_PAY"));//6
            parm.setData("OFF_RECP_NO","");//10
            parm.setData("REFUND_DATE","");//9
            parm.setData("REFUND_CODE","");//8         
            parm.setData("REFUND_FLG", "N");//7
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());//13
            parm.setData("OPT_USER", Operator.getID());//12
            parm.setData("OPT_TERM", Operator.getIP());//14            
            parm.setData("REMARK",this.getValue("REMARK"));//11
            parm.setData("PAY_TYPE",getValue("PAY_TYPE"));//15
            parm.setData("TRANSACT_TYPE", "01");          
            parm.setData("START_INVNO", startInvno);          
            parm.setData("INV_NO",printNo); 
            System.out.println("startInvno:::"+startInvno);
            System.out.println("printNo:::"+printNo);
            TParm result = TIOM_AppServer.executeAction(actionName,
                "onContractBillPay", parm);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return;
            }  
            this.messageBox("合同单位预交成功！");
            parm.setData("RECEIPT_NO",result.getValue("RECEIPT_NO",0));
            parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
            parm.setData("TEL",this.getValue("TEL"));
            parm.setData("LIMIT_AMT",this.getValue("LIMIT_AMT"));
            parm.setData("PRE_AMT",this.getValueDouble("LIMIT_AMT")+this.getValueDouble("PRE_PAY"));
            parm.setData("REFUND_CODE","");
            parm.setData("REFUND_DATE","");
            parm.setData("OFF_RECP_NO","");
            //table上加入新增的数据显示
           //CHARGE_DATE;RECEIPT_NO;PAY_TYPE;PRE_PAY;REMARK;REGION_CODE;CONTRACT_CODE;TEL;LIMIT_AMT;PRE_AMT 
            callFunction("UI|Table|addRow", parm,
                         "CHARGE_DATE;RECEIPT_NO;PAY_TYPE;BUSINESS_TYPE;PRE_PAY;REMARK;REFUND_CODE;REFUND_DATE;OFF_RECP_NO;REGION_CODE;CONTRACT_CODE;TEL;LIMIT_AMT;PRE_AMT");
            //打印预交金收据
            TParm forPrtParm = new TParm();
            String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
                    getDouble(getValue("PRE_PAY")));
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
                               TypeTool.getDouble(this.getValueString("PRE_PAY"))));
            forPrtParm.setData("SEQ_NO", "TEXT",
                               "流水号:" + this.getValueString("RECEIPT_NO"));
            forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
            if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {

            	forPrtParm.setData("WAY","TEXT","现金") ;
            }
            else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {

                    forPrtParm.setData("WAY","TEXT","银行卡") ;
            }
            else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {

                    forPrtParm.setData("WAY","TEXT","支票") ;
            }
            else {

            	forPrtParm.setData("WAY","TEXT","其他") ;
            }

            this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
                                 forPrtParm,true);
            this.onClear();
            
	}
	
	
	/**
	 * 初始化票据
	 * 
	 * ===zhangp 20120731
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		
		if (BILTool.getInstance().compareUpdateNo("HPAY", Operator.getID(),
				Operator.getRegion(), bilInvoice.getUpdateNo())) {
			printNo= bilInvoice.getUpdateNo();			
			startInvno = bilInvoice.getStartInvno();
		} else {
			messageBox("票据已用完");
			return false;
		}
		return true;
	}
	
	/**
	 * 退费
	 * */
	public void onReturnFee(){
		 int row = (Integer) callFunction("UI|Table|getSelectedRow");
	        if (row < 0)
	            return;
	        TParm parm = (TParm) callFunction("UI|Table|getParmValue");
	        String receiptNo = parm.getValue("RECEIPT_NO", row);
	        TParm actionParm = new TParm();
	        actionParm.setData("RECEIPT_NO",receiptNo);
	        actionParm =  BILContractPayTool.getInstance().selectData("selectByReceipt",actionParm);//???????????????
	        TParm endParm = new TParm();
	        endParm = actionParm.getRow(0);
	        endParm.setData("RESET_BIL_PAY_NO", parm.getData("RECEIPT_NO", row));
	        endParm.setData("REFUND_CODE", Operator.getID());	      
	        endParm.setData("CASHIER_CODE", Operator.getID());
	        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());	       
	        endParm.setData("REFUND_DATE", SystemTool.getInstance().getDate());
	        endParm.setData("OPT_USER", Operator.getID());
	        endParm.setData("OPT_TERM", Operator.getIP());
	        endParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
	        endParm.setData("PAY_TYPE",parm.getData("PAY_TYPE", row));
	        endParm.setData("PRINT_NO",parm.getData("PRINT_NO",row));
	        TParm endDate = TIOM_AppServer.executeAction(actionName,
	            "onReturnBillContractPay", endParm);
	        if (endDate.getErrCode() < 0) {
	            err(endDate.getErrCode() + " " + endDate.getErrText());
	            return;
	        }
	        this.messageBox("P0005");
	        //查询数据
	        TParm allParm = new TParm();
	        allParm = this.onQuery();
	        //整理数据
	        //给table配参
	        this.callFunction("UI|Table|setParmValue", allParm);
	}
	
	/**
	 * 补印
	 * */
	public void onPrint(){
		TTable table = (TTable)this.getComponent("Table");
		
        if (table.getSelectedRow() < 0) {
            this.messageBox("请点选数据");
            return;
        }
        String sql = "SELECT CONTRACT_DESC FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+this.getValue("CONTRACT_CODE")+"'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        String contract_desc = selParm.getValue("CONTRACT_DESC",0);
        //打印预交金收据
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
            getDouble(getValue("PRE_PAY")));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|Table|getParmValue");
        forPrtParm.setData("Data", "TEXT",StringTool.getString(parm.
                                                getTimestamp("CHARGE_DATE", row),
                                                "yyyy/MM/dd HH:mm:ss"));
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        forPrtParm.setData("Name", "TEXT", contract_desc);
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("CONTRACT_CODE"));     
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
                           TypeTool.getDouble(this.getValueString("PRE_PAY"))));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "流水号:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
        	forPrtParm.setData("WAY","TEXT","现金") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
            forPrtParm.setData("WAY","TEXT","银行卡") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
                forPrtParm.setData("WAY","TEXT","支票") ;
        }
        else {
        	forPrtParm.setData("WAY","TEXT","其他") ;
        }

        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
                             forPrtParm,true);
        this.onClear();
	}
	/**
	 * 清空
	 * */
	public void onClear(){
		this.setValue("REGION_CODE", "");
		this.setValue("CONTRACT_CODE", "");
		this.setValue("CONTACT", "");
		this.setValue("TEL", "");
		this.setValue("LIMIT_AMT", "");
		this.setValue("PRE_AMT", "");
		this.setValue("PRE_PAY", "");
		this.setValue("PAY_TYPE", "");
		this.setValue("REMARK", "");		
	}
	

}
