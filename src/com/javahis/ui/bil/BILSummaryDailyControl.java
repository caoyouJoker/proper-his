package com.javahis.ui.bil;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import jdo.bil.BILAccountTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.ArrayList;
import java.util.List;
import com.javahis.util.ExportExcelUtil;
import jdo.bil.BILSysParmTool;
/**
 * <p>Title: 住院每日票据核销汇总</p>
 *
 * <p>Description: 住院每日票据核销汇总</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yyn 201709
 * @version 1.0
 */

public class BILSummaryDailyControl extends TControl{
	TTable table;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		table = (TTable)this.getComponent("Table");
		initPage();//初始化界面
	}
	
	/**
	 * 初始化界面
	 */
	public void initPage(){
		// 初始化院区
		setValue("REGION_CODE", Operator.getRegion());
		// 初始化查询起时,迄时
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
		Timestamp today = SystemTool.getInstance().getDate();
		setValue("S_DATE", yesterday);
		setValue("E_DATE", today);
		String todayTime = StringTool.getString(today, "HH:mm:ss");
		String accountTime = todayTime;
		if (getAccountDate().length() != 0) {
			accountTime = getAccountDate();
			accountTime = accountTime.substring(0, 2) + ":"
					+ accountTime.substring(2, 4) + ":"
					+ accountTime.substring(4, 6);
		}
		
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.getValueString("REGION_CODE")));

		setValue("S_TIME", accountTime);
		setValue("E_TIME", accountTime);
		setValue("TYPE", "I");
	}
	
	/**
	 * 得到结账时间点
	 *
	 * @return String
	 */
	public String getAccountDate() {
		String accountDate = "";
		TParm accountDateParm = new TParm();
		accountDateParm = BILSysParmTool.getInstance().getDayCycle("I");
		accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
		return accountDate;
	}
	
	/**
     * 查询
     */
    public void onQuery(){
    	TParm result = new TParm();
    	String user = "";//收费员姓名
    	int billall = 0;//票据张数汇总
    	double armall = 0;//票据金额汇总
    	int count = 0;
    	
    	String start = getTime("S_TIME");
		if (start.length() == 0) {
			messageBox("报表查询起日的时间不正确!");
			return;
		}
		String end = getTime("E_TIME");
		if (end.length() == 0) {
			messageBox("报表查询迄日的时间不正确!");
			return;
		}
		
		if (getValue("S_DATE") == null ||
		    getValueString("S_DATE").equals("")) {
		    messageBox("请选择开始时间!");
		    return;
		}
		
		if (getValue("E_DATE") == null ||
	        getValueString("E_DATE").equals("")) {
	        messageBox("请选择结束时间!");
	        return;
	    }
		
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		startTime = startTime + start;
		endTime = endTime + end;
		
		if(getValueString("TYPE").equals("I")){//住院
			TParm dataparm = new TParm();
			TParm selAccountData = new TParm();
			
			selAccountData.setData("ACCOUNT_TYPE", "IBS");
			selAccountData.setData("S_TIME", startTime);
			selAccountData.setData("E_TIME", endTime);
			selAccountData.setData("REGION_CODE", Operator.getRegion());			
			result = BILAccountTool.getInstance().accountQuery(selAccountData);
			count = result.getCount();
	        if(count <= 0){
	        	messageBox("住院数据不存在");
	        	table.setParmValue(result);
				return;
			}
			for(int i = 0;i < count;i++){
				String sql = "SELECT USER_NAME FROM SYS_OPERATOR"
							+ " WHERE USER_ID = '"+result.getValue("ACCOUNT_USER", i)+"'";				
				TParm username = new TParm(TJDODBTool.getInstance().select(sql));
				if (username.getErrCode() < 0) {
					System.out.println("收费员姓名查询错误  " + username.getErrText());
					return;
				}
				if(username.getCount() < 0){
					System.out.println("收费员不存在");
					return;
				}
				user = username.getValue("USER_NAME");
				user = user.substring(1, (user.length()-1));//收费员姓名
				result.setData("USER_NAME", i,user);
				//System.out.println(user);
				dataparm = getIBSparm(result.getValue("ACCOUNT_SEQ",i));//得到住院数据
				result.setData("USER_NAME", i,user);
				result.setData("PRINT_NO", i,dataparm.getValue("PRINT_NO") );
				result.setData("BIL_COUNT", i, dataparm.getInt("BIL_COUNT"));
				billall = billall + result.getInt("BIL_COUNT",i);//票据张数汇总
				armall = armall + result.getDouble("AR_AMT", i);//票据金额汇总
				//System.out.println(billall);
				//System.out.println(armall);		
			}
			//票据合计
	        result.setData("ACCOUNT_SEQ",count, "票据总计");
	        result.setData("USER_NAME",count, "-");
			result.setData("PRINT_NO",count, "-");
			result.setData("BIL_COUNT",count, billall);
			result.setData("AR_AMT",count, armall);	
		}		
		else if(getValueString("TYPE").equals("H")) {//健检
			TParm hparm = new TParm();
			String sql = "SELECT A.AR_AMT,B.USER_NAME,A.ACCOUNT_SEQ"
        		+ "		FROM BIL_ACCOUNT A,SYS_OPERATOR B,SYS_OPERATOR_DEPT C"
        		+ "		WHERE A.ACCOUNT_TYPE='OPB' AND A.ACCOUNT_USER=C.USER_ID(+)  AND C.MAIN_FLG='Y' " 
        		+ "	AND A.ACCOUNT_USER=B.USER_ID AND A.REGION_CODE='H01' AND A.ADM_TYPE = 'H'";
			
			sql += " AND A.ACCOUNT_DATE BETWEEN TO_DATE('"+ startTime + "','YYYYMMDDHH24MISS')"
    			+ " AND TO_DATE('" +endTime+ "','YYYYMMDDHH24MISS')";
    		sql += " ORDER BY A.ACCOUNT_SEQ";    		
    		//System.out.println(sql);   		
    		result = new TParm(TJDODBTool.getInstance().select(sql));
    		//System.out.println("result count"+result.getCount());
            if (result.getErrCode() < 0) {
            	messageBox("健检数据查询错误  ");
            	System.out.println("健检数据查询错误  " + result.getErrText());
    			return;
    		}
            if(result.getCount() <= 0){
            	messageBox("健检数据不存在");
            	table.setParmValue(result);
				return;
			}            
            count = result.getCount();            
            for(int i = 0;i < count; i++){
                hparm = getPrintNo(result.getValue("ACCOUNT_SEQ", i));//得到健检数据 
                result.setData("PRINT_NO", i,hparm.getData("PRINTNOS"));
				result.setData("BIL_COUNT", i, hparm.getData("COUNT"));
				billall = billall + result.getInt("BIL_COUNT",i);//票据张数汇总
				armall = armall + result.getDouble("AR_AMT", i);//票据金额汇总
				//System.out.println("billall "+billall);
				//System.out.println("armall "+armall);				
            }
          //票据合计
            result.setData("ACCOUNT_SEQ",count, "票据总计");
            result.setData("USER_NAME",count, "-");
    		result.setData("PRINT_NO",count, "-");
    		result.setData("BIL_COUNT",count, billall);
    		result.setData("AR_AMT",count, armall);	
		}
		else {
			messageBox("请选择票据类别!");
		}
		table.setParmValue(result);		
    }
    
    /**
     * 得到住院数据
     * @param accountSeq String
     * @return TParm
     */
    public TParm getIBSparm(String accountSeq){
    	TParm IBSparm = new TParm();
    	String s1 = "";
    	String s2 = "";    	
    	
    	String printNoSQL = "SELECT INV_NO FROM BIL_INVRCP "
    				+ " WHERE ACCOUNT_SEQ = '"+accountSeq+"' "
    				+ " AND STATUS = '0' AND RECP_TYPE = 'IBS' ORDER BY INV_NO";
    	//System.out.println(printNoSQL);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(printNoSQL));
    	if (parm.getErrCode() < 0) {
			System.out.println("日结数据查询错误  " + parm.getErrText());
			return IBSparm;
		}
    	if(parm.getCount() < 0){
			System.out.println("数据不存在");
			return IBSparm;
		}
    	    	
    	String selRecpNo = "SELECT START_INVNO,END_INVNO FROM BIL_INVOICE WHERE RECP_TYPE = 'IBS'";
    	//System.out.println(selRecpNo);
		TParm invNoParm = new TParm(TJDODBTool.getInstance().select(selRecpNo));
		String recp_no = "";
		for (int i = 0; i < invNoParm.getCount(); i++) {
			TParm p = new TParm();
			String startNo = invNoParm.getData("START_INVNO", i).toString();
			String endNo = invNoParm.getData("END_INVNO", i).toString();
			//System.out.println(startNo+"                   "+endNo);
			for (int j = 0; j < parm.getCount("INV_NO"); j++) {
				String invNo = parm.getData("INV_NO", j).toString();
				if ((invNo.compareTo(startNo) >= 0 )&& (invNo.compareTo(endNo) <= 0)) {
					p.addData("INV_NO", invNo);
					//System.out.println("i"+i+"j"+j+"invNo"+invNo);
				}
			}
			if (p.getCount("INV_NO") > 1) {
				if((! p.getData("INV_NO", 0).equals(s1)) && (!p.getData("INV_NO", p.getCount("INV_NO") - 1).equals(s2)))//查重
				{	
					recp_no += "," + p.getData("INV_NO", 0) + " ~ "
							+ p.getData("INV_NO", p.getCount("INV_NO") - 1);
					s1 = p.getValue("INV_NO", 0);
					s2 = p.getValue("INV_NO", p.getCount("INV_NO") - 1);
				}
			}
			if (p.getCount("INV_NO") > 0 && p.getCount("INV_NO") <= 1) {
				if((! p.getData("INV_NO", 0).equals(s1)) && (!p.getData("INV_NO", p.getCount("INV_NO") - 1).equals(s2)))//查重
				{
					recp_no = "," + p.getData("INV_NO", 0);
					s1 = p.getValue("INV_NO", 0);
					s2 = p.getValue("INV_NO", p.getCount("INV_NO") - 1);
				}
			}	
		}
		if (recp_no.length() > 0) {
			recp_no = recp_no.substring(1, recp_no.length());
		}
		//System.out.println(recp_no);
		IBSparm.setData("BIL_COUNT", parm.getCount());//票据张数汇总
		IBSparm.setData("PRINT_NO", recp_no);//票据金额汇总		
    	return IBSparm;    	
    }
    
    /**
     * 得到健检数据
     * @param accountSeq String
     * @return TParm
     */
    
    public TParm getPrintNo(String accountSeq) {
    	TParm returnParm = new TParm();
    	String recp_no = "";
    	int count = 0;
    	
        TParm recParm = getOPBparm(accountSeq);//取得收费票号及张数
        for (int i = 0; i < recParm.getCount("PRINT_USER"); i++) {
        	recp_no += recParm.getValue("INV_NOS",i) + ";";
        	count += recParm.getInt("INV_COUNT", i);
		}
        returnParm.setData("PRINTNOS", recp_no);
        returnParm.setData("COUNT", count);
        return returnParm;
    }
    
    /**
	 * 取得健检票号及张数
	 * @param account_seq String
	 * @return TParm
	 */
    private TParm getOPBparm(String account_seq) {
		String sql = " SELECT   '收费' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER " +
						" FROM BIL_INVRCP A , SYS_OPERATOR B " +
						" WHERE A.ACCOUNT_SEQ IN (" + account_seq + ")" +
						" AND A.PRINT_USER = B.USER_ID " +
						" AND A.RECP_TYPE = 'OPB'";
		sql += " AND LENGTH (A.INV_NO) < 12";//add by wanglong 20121112 过滤掉12位的建行机器的票据号
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));		
		//System.out.println("收费 sql"+sql);				
		if (result.getCount() < 0) {
			return result;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm opbParm = new TParm();
		List<String> opblist = new ArrayList<String>();
		opblist.add(result.getValue("INV_NO", 0));
		int opbcount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("PRINT_USER", i).equals(print_user)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += opblist.get(0) + "~"
							+ opblist.get(opblist.size() - 1) + ",";
					opbcount += opblist.size();
					opblist = new ArrayList<String>();
				}
			} else {
				inv_nos += opblist.get(0) + "~"
						+ opblist.get(opblist.size() - 1) + ",";
				opbcount += opblist.size();
				opblist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				opbParm.addData("PRINT_USER", print_user);
				opbParm.addData("INV_NOS", inv_nos);
				opbParm.addData("INV_COUNT", opbcount);
				inv_nos = "";
				opbcount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			opblist.add(result.getValue("INV_NO", i));
		}
		if (opblist.size() > 0) {
			inv_nos += opblist.get(0) + "~" + opblist.get(opblist.size() - 1)
					+ ",";
		}
		opbcount += opblist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		opbParm.addData("PRINT_USER", print_user);
		opbParm.addData("INV_NOS", inv_nos);
		opbParm.addData("INV_COUNT", opbcount);
		return opbParm;
	}
    
    /**
	 * 比较票号
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// 去非数字
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// 去数字
		String latestInv_no_num = latestInv_no.replaceAll("[^0-9]", "");
		String latestInv_no_word = latestInv_no.replaceAll("[0-9]", "");
		if (inv_no_word.equals(latestInv_no_word)
				&& Long.valueOf(inv_no_num)
						- Long.valueOf(latestInv_no_num) == 1) {
			return true;
		} else {
			return false;
		}
	}
    
    /**
     * 清空
     */
    public void onClear(){
    	onInit();
    	table.removeRowAll();   	
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport(){
		String title = "";
		if(getValueString("TYPE").equals("I"))
			title = "住院每日票据核销汇总表";
		else if(getValueString("TYPE").equals("H"))
			title = "健检每日票据核销汇总表";
		if (table.getRowCount() > 0)   		
		ExportExcelUtil.getInstance().exportExcel(table,title);		
	}
	
	public String getTime(String name) {
		String time = getText(name);
		if (time.length() != 8)
			return "";
		try {
			if (!checkTime(time.substring(0, 2), 23))
				return "";
			if (!checkTime(time.substring(3, 5), 59))
				return "";
			if (!checkTime(time.substring(6), 59))
				return "";
		} catch (Exception e) {
			return "";
		}
		return time.substring(0, 2) + time.substring(3, 5) + time.substring(6);
	}
	
	public boolean checkTime(String s, int max) {
		if (s.substring(0, 1).equals("0"))
			s = s.substring(1);
		int x = Integer.parseInt(s);
		return x >= 0 && x <= max;
	}
}
