package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.ekt.EKTIO;
import jdo.ins.INSTJReg;

import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * Title:垫付费用延迟申请
 * </p>
 */
public class INSADVANCEControl extends TControl {
    private String nhi_hosp_code; //医保医院代码   
	private Pat pat; // 病患对象	
	private boolean insFlg = false;// 医保卡读卡成功管控
	private String insType; // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特
	private TParm insParm;//分割数据
	Reg reg;// reg对象
	private TParm regionParm; // 获得医保区域代码
	private TParm parmEKT; // 读取医疗卡信息
	// 页签
	private TTabbedPane tabbedPane;
	public void onInit() {
		super.onInit();
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
      //单击事件
		callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		onClear();

	}
	/**
	 * 页签点击事件
	 */
	public void onChangeTab() {
		if(tabbedPane.getSelectedIndex()==2){
		String mrno = getValue("MR_NO").toString();
		if(mrno!="")			
		onQuery();
		}		 
	}
	/**
	 * 获得历史数据
	 * @param order
	 */
	public void onTableClicked(int row){
		TTable table1 = (TTable) this.getComponent("TABLE1");
		TParm Parm = table1.getParmValue();// SYS_FEE医嘱
		//赋值界面
		setValue("MR_NO", Parm.getValue("MR_NO", row));
		setValue("PAT_NAME",Parm.getValue("PAT_NAME", row));
		setValue("ID_NO",Parm.getValue("ID_NO", row));
		setValue("TEL_NO", Parm.getValue("TEL_NO", row));
	}
	/**
	 * 读取医疗卡
	 */
	public void onEKT() {
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		onQuery();
	}
	/**
	 * 读取医疗卡(已下载数据)
	 */
	public void onEKTCARD() {
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		ondata();	
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		 if(this.getValue("START_DATE").equals("")){
			 this.messageBox("费用发生时间不能为空");
             return;         
		 }
		 if(this.getValue("MR_NO").equals("")){
			 this.messageBox("病案号不能为空");
            return;         
		 }	 
		 onQueryNO();
	}
	/**
	 * 查询数据
	 */
	public void onQueryNO() {
		String mrno = PatTool.getInstance().checkMrno(
			TypeTool.getString(getValue("MR_NO")));
//		System.out.println("mrno=====:"+mrno);
		onPatNO(mrno);							
		if(tabbedPane.getSelectedIndex()==0){
		//垫付费用延迟申请上传(查询病患挂号信息)
		onRegpatadm(mrno);
		}
        else if(tabbedPane.getSelectedIndex()==1){
        //垫付费用延迟申请下载(未审核通过)
        onInsadvance(mrno,"0","0");	
        
		}
        else if(tabbedPane.getSelectedIndex()==2){
        //垫付费用发放结果上传(审核已通过)
        onInsadvance(mrno,"1","1");		
		}
	}
	/**
	 * 查询病患信息
	 */
	public void onPatNO(String mrNo) {
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("无此病案号!");
			setValue("MR_NO", "");
			setValue("PAT_NAME","");
			setValue("ID_NO","");
			setValue("TEL_NO","");
			return;
		}
		// modify by huangtt 20160930 EMPI患者查重提示 start
		 mrNo =  PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160930 EMPI患者查重提示 end
		
		//界面显示
		setValue("MR_NO",mrNo);
		setValue("PAT_NAME", pat.getName().trim());
		setValue("ID_NO", pat.getIdNo());
		setValue("TEL_NO", pat.getTelHome());
		
	}
	/**
	 * 垫付费用延迟申请上传(查询病患挂号信息)
	 */
	public void onRegpatadm(String mrNo) {
		String sql1 ="";		
		if(!mrNo.equals("")){
//			String caseNo = reg.caseNo();
			 sql1 = " AND A.MR_NO ='"+ mrNo+ "'";
//					" AND A.CASE_NO = '"+ caseNo+ "'";
		}	
		TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"235959";
		
		//检核是否医保挂号
//		String SQL =  " SELECT C.*" +
//		  " FROM REG_PATADM A,SYS_PATINFO B,BIL_OPB_RECP C" +
//		  " WHERE A.MR_NO = B.MR_NO" + 
//		  sql1 +
//		  " AND A.CASE_NO = C.CASE_NO" +
//		  " AND C.PAY_INS_CARD =0" +
//		  " AND C.AR_AMT>0" +
//		  " AND C.RESET_RECEIPT_NO IS NULL" +
//		  " AND A.CONFIRM_NO IS NOT NULL" +
//		  " AND A.ADM_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
//		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
//		  " AND A.ARRIVE_FLG = 'Y'" + 
//		  " AND A.REGCAN_USER IS NULL";
//		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
//		if (parm.getCount()> 0) {
//			messageBox("此病患为医保挂号,无需申请上传");
//			return;
//		}
		//查询病患
//		String sql =
//		  " SELECT A.MR_NO,B.PAT_NAME,A.CASE_NO,B.IDNO AS ID_NO,B.TEL_HOME AS TEL_NO,A.ADM_DATE " +
//		  " FROM REG_PATADM A,SYS_PATINFO B"+
//		  " WHERE A.MR_NO = B.MR_NO" +
//		  sql1 +
//		  " AND A.CONFIRM_NO IS NULL"+
//		  " AND A.ADM_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
//		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
//		  " AND A.ARRIVE_FLG = 'Y'" +
//		  " AND A.REGCAN_USER IS NULL";
		
		String sql =
		    " SELECT DISTINCT A.MR_NO,B.PAT_NAME,A.CASE_NO,B.IDNO AS ID_NO," +
		    " B.TEL_HOME AS TEL_NO,A.ADM_DATE,C.PRINT_NO"+ 
		    " FROM REG_PATADM A,SYS_PATINFO B,BIL_OPB_RECP C"+ 
			" WHERE A.MR_NO = B.MR_NO"+  
			 sql1 +
			" AND A.CASE_NO = C.CASE_NO"+ 
			" AND C.PAY_INS_CARD =0"+  
			" AND C.AR_AMT>0"+   
			" AND A.CONFIRM_NO IS NOT NULL"+ 
			" AND A.ADM_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+  
			" AND A.ARRIVE_FLG = 'Y'"+ 
			" AND A.REGCAN_USER IS NULL"+ 
			" UNION ALL"+ 
			" SELECT DISTINCT A.MR_NO,B.PAT_NAME,A.CASE_NO,B.IDNO AS ID_NO," +
			" B.TEL_HOME AS TEL_NO,A.ADM_DATE,C.PRINT_NO"+ 
			" FROM REG_PATADM A,SYS_PATINFO B,BIL_OPB_RECP C"+  
			" WHERE A.MR_NO = B.MR_NO"+  
			sql1 +
			" AND A.CASE_NO = C.CASE_NO "+ 
			" AND C.PAY_INS_CARD =0"+ 
			" AND C.AR_AMT>0"+ 
			" AND A.CONFIRM_NO IS NULL"+ 
			" AND A.ADM_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
			" AND A.ARRIVE_FLG = 'Y'"+  
			" AND A.REGCAN_USER IS NULL"; 		
//		System.out.println("sql=====:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=====:"+result);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE1")).removeRowAll();
			return;
		}
		((TTable) getComponent("TABLE1")).setParmValue(result);	
		
	}
	/**
	 * 垫付费用延迟申请下载、发放结果上传
	 */
	public void onInsadvance(String mrNo,String approveType,String payFlg) {
		String sql1 ="";
		if(!mrNo.equals("")){
//			String caseNo = reg.caseNo();
			 sql1 = " AND A.MR_NO ='"+ mrNo+ "'";
//					" AND A.CASE_NO = '"+ caseNo+ "'";
		}	
		TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"235959";
		String sql =
		  " SELECT A.MR_NO,C.PAT_NAME,A.CASE_NO,A.APPLY_NO,A.INS_TYPE,A.PAT_TYPE," +
		  " A.CARD_FLG,A.ID_NO,A.BILL_DATE,A.TEL_NO,A.PAY_FLG,A.PAY_DATE,A.APPROVE_TYPE," +
		  " CASE WHEN A.INS_TYPE='01' THEN '城职' " +
    	  " WHEN A.INS_TYPE='02' THEN '城乡' END AS INS_TYPE_DESC," +
    	  " CASE WHEN A.PAT_TYPE='01' THEN '门诊' " +
    	  " WHEN A.PAT_TYPE='02' THEN '门特' END AS PAT_TYPE_DESC," +
    	  " CASE WHEN A.CARD_FLG='01' THEN '有卡' " +
    	  " WHEN A.CARD_FLG='02' THEN '无卡' END AS CARD_FLG_DESC," +
    	  " CASE WHEN A.PAY_FLG='0' THEN '未发放' " +
    	  " WHEN A.PAY_FLG='1' THEN '已发放' END AS PAY_FLG_DESC," +
    	  " CASE WHEN A.APPROVE_TYPE='0' THEN '未审核' " +
    	  " WHEN A.APPROVE_TYPE='1' THEN '已审核' END AS APPROVE_TYPE_DESC " +
		  " FROM INS_ADVANCE_OUT A,SYS_PATINFO C " +
		  " WHERE A.BILL_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		  " AND A.MR_NO = C.MR_NO"+
		  sql1+
		  " AND A.APPROVE_TYPE = '"+ approveType+ "'" +
		  " AND A.PAY_FLG = '"+ payFlg+ "'" +
		  " AND A.PAT_TYPE !='03'";
//		System.out.println("sql=====:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=====:"+result);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			if(approveType.equals("0"))
			((TTable) getComponent("TABLE2")).removeRowAll();
			else if(approveType.equals("1"))
			((TTable) getComponent("TABLE3")).removeRowAll();			
			return;
		}
		if(approveType.equals("0"))
		((TTable) getComponent("TABLE2")).setParmValue(result);
		else if(approveType.equals("1")){
		((TTable) getComponent("TABLE3")).setParmValue(result);	
		((TTable) getComponent("TABLE4")).removeRowAll();
		}
	}
	/**
	 * 申请上传
	 */
	public void onUpload() {
		TTable table1 = (TTable) this.getComponent("TABLE1");	 
		 if(this.getValue("INS_TYPE").equals("")){
			 this.messageBox("医保险种不能为空");
             return;         
		 }
		 if(this.getValue("PAT_TYPE").equals("")){
			 this.messageBox("费用类别不能为空");
             return;         
		 }
		 if(this.getValue("CARD_FLG").equals("")){
			 this.messageBox("持卡类型不能为空");
             return;         
		 }	
		 TParm parm = table1.getParmValue();//获得数据
		 int count= parm.getCount("CASE_NO");
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm:====="+parm);
//	     System.out.println("count:====="+count);
	     int j=0;
		TParm result = new TParm();
		TParm UpParm = new TParm();
//		 System.out.println("rrrrrr====="+count);	 
		 UpParm.addData("INS_TYPE", this.getValue("INS_TYPE"));//险种
		 UpParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//医院编码		 
		 UpParm.addData("CARD_FLG",  this.getValue("CARD_FLG"));//类别
		 UpParm.addData("PAT_TYPE",  this.getValue("PAT_TYPE"));//费用类别
		 UpParm.addData("ID_NO", parm.getValue("ID_NO",0));//身份证号码
		 Timestamp date1 = StringTool.getTimestamp(parm.getValue("ADM_DATE",0), "yyyy-MM-dd HH:mm:ss");
 		 String billdate = StringTool.getString(date1, "yyyyMMdd");
		 UpParm.addData("BILL_DATE",  billdate);//费用发生时间
		 UpParm.addData("TEL_NO",  parm.getValue("TEL_NO",0));//联系方式
		 UpParm.setData("PIPELINE", "DataDown_yb");	
		 UpParm.setData("PLOT_TYPE", "V");		 
		 UpParm.addData("PARM_COUNT", 7);//入参数量 
//         System.out.println("UpParm:====="+UpParm);
	     result = InsManager.getInstance().safe(UpParm);
//	        System.out.println("result=============" + result);
//	        System.out.println("getErrCode=============" + result.getErrCode());	        
	        if (result.getErrCode() < 0) {	        	
	        	this.messageBox(result.getErrText());
				return;
	        }else{
	        	String applyno = result.getValue("APPLY_NO");
//	        	 System.out.println("applyno:====="+applyno);
	        	 for(int i=0;i<count;i++){
	        	Timestamp date = StringTool.getTimestamp(parm.getValue("ADM_DATE",i), "yyyy-MM-dd HH:mm:ss");
	    		String admdate = StringTool.getString(date, "yyyyMMddHHmmss"); //费用发生时间	    		
	        	String sql= " INSERT INTO INS_ADVANCE_OUT"+
		            " (CASE_NO,MR_NO,CARD_FLG,ID_NO,BILL_DATE,TEL_NO,APPLY_NO,"+
		            " INS_TYPE,APPROVE_TYPE,PAY_FLG,OPT_USER,OPT_TERM,OPT_DATE,PAT_TYPE)"+ 
	                " VALUES ('"+ parm.getValue("CASE_NO",i)+ "','"+ parm.getValue("MR_NO",i)+ "'," +
	                " '"+ this.getValue("CARD_FLG")+ "','"+ parm.getValue("ID_NO",i)+ "',to_date('"+
	                 admdate+"','yyyyMMddHH24MISS'),'"+ parm.getValue("TEL_NO",i)+ "','" +
	                 applyno+ "','"+ this.getValue("INS_TYPE")+ "','0','0','" +	                 
	                 parm.getValue("OPT_USER")+ "','" +
	                 parm.getValue("OPT_TERM")+ "',SYSDATE,'"+ this.getValue("PAT_TYPE")+ "')";
                TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
//                System.out.println("result1=========="+result1);  
              // 判断错误值
               if (result1.getErrCode() >= 0) {
                   j++;
                   }
	           }
				 onInsadvance(parm.getValue("MR_NO",0),"0","0");
				 this.messageBox("申请上传成功");
		  }		 
//		 if(count>1){
//			 this.messageBox("申请上传成功"+j+"笔");
//			 onInsadvance("","0","0");
//		 }		 
	}

	/**
	 * 申请下载
	 */
	public void onDownload() {
		TTable table2 = (TTable) this.getComponent("TABLE2"); 	    
		 TParm parm = table2.getParmValue();//获得数据	
		 int count= parm.getCount("CASE_NO");
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm=====:"+parm);
//	     System.out.println("count=====:"+count);
	     int j=0;
         TParm downParm = new TParm();
    	 TParm result = new TParm();
    	 downParm.addData("APPLY_NO",parm.getValue("APPLY_NO",0));//申请顺序号
    	 downParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//医院编码
    	 downParm.addData("INS_TYPE", parm.getValue("INS_TYPE",0));//险种
    	 downParm.addData("CARD_FLG", parm.getValue("CARD_FLG",0));//类别
    	 downParm.addData("PAT_TYPE", parm.getValue("PAT_TYPE",0));//费用类别
    	 downParm.addData("PARM_COUNT", 5);//入参数量       	   	   
    	 downParm.setData("PIPELINE", "DataDown_yb");
    	 downParm.setData("PLOT_TYPE", "W");	  	    
//         System.out.println("downParm:"+downParm);
         result = InsManager.getInstance().safe(downParm);
//         System.out.println("result===========" + result);
         if (result.getErrCode() < 0) {      
        		this.messageBox(result.getErrText());
 				return;
	        }else{
	        	String cardflg = "01";
	        	String instype = "01";
	        	if(result.getValue("PERSONAL_NO").trim().length()!=0)
	        		cardflg ="02";
	        	if(result.getValue("INS_TYPE").equals("2"))
	        		instype ="02";
	        	 for(int i=0;i<count;i++){
	        	String sql = "UPDATE INS_ADVANCE_OUT SET APPROVE_TYPE ='"
	        		+ result.getValue("APPROVE_TYPE") + "',PERSONAL_NO ='"
	        		+ result.getValue("PERSONAL_NO").trim() + "',INS_TYPE ='"
	        		+ instype + "',CARD_FLG ='"
	        		+ cardflg+ "',OPT_USER ='"
	        		+ parm.getValue("OPT_USER") + "',OPT_TERM ='"
	        		+ parm.getValue("OPT_TERM") + "',OPT_DATE = SYSDATE" +
	        		" WHERE MR_NO = '"
	        		+ parm.getValue("MR_NO",i) + "'" +
	        		" AND APPLY_NO='"
	        		+ parm.getValue("APPLY_NO",i) + "'";
	                TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
	                // 判断错误值
	                if (result1.getErrCode() >= 0) {
	                    j++;
	                }	                	
	        }
	     }
		 //查询已下载的数据（已审核）
		 ondata();	
		 this.messageBox("申请下载成功"); 	 
		 ((TTable) getComponent("TABLE2")).removeRowAll();
	} 
	/**
	 * 读医保卡
	 */
	public void onReadInsCard() {
		TTable table4 = (TTable) this.getComponent("TABLE4");
    	int Row = table4.getSelectedRow();//行数
//    	System.out.println("Row=====:"+Row);
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}		    
		TParm data = table4.getParmValue().getRow(Row);//获得数据	
		Timestamp date1 = StringTool.getTimestamp(
				data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
 		String billdate = StringTool.getString(date1, "yyyyMMdd");//费用发生时间
// 		 System.out.println("billdate===========" + billdate);
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // 读卡请求类型 2：挂号
		//医院编码@费用发生时间@类别
		String advancecode = nhi_hosp_code+"@"+billdate+"@"+"2";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_PERSONAL_NO",data.getValue("PERSONAL_NO").trim());//个人编码（无卡人员）
		parm.setData("ADVANCE_TYPE","2");//延迟垫付
	    insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
//	    System.out.println("insParm===========" + insParm);
		if (null == insParm) {
			return;
		}		
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		// 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特	
		insType = insParm.getValue("INS_TYPE");
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (insPresonParm.getErrCode() < 0) {
			this.messageBox("获得病患信息失败");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("此医保病患不存在医疗卡信息,\n医保信息:身份证号码:"
					+ opbReadCardParm.getValue("SID") + "\n医保病患名称:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		} else if (insPresonParm.getCount("MR_NO") > 1) {
			int flg = -1;
			if (this.getValue("MR_NO").toString().length() > 0) {
				for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
					if (insPresonParm.getValue("MR_NO", i).equals(
							this.getValue("MR_NO"))) {
						flg = i;
						break;
					}
				}
				if (flg == -1) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		}		
		insFlg = true; // 医保卡读取成功
//		 System.out.println("insType===========" + insType);
//		 System.out.println("ctz1code===========" + ctz1code);
//		 System.out.println("confirmNo===========" + confirmNo);
//		 System.out.println("insFlg===========" + insFlg);
	}
	
	/**
	 * 垫付延迟挂号
	 */
	public void onReg() {
		 if(this.getValue("REG_TYPE").equals("")){
			 this.messageBox("挂号类型不能为空");
             return;         
		 }	
		if(!insFlg){
			 this.messageBox("未读医保卡或读医保卡失败");
             return; 
		}
		TTable table4 = (TTable) this.getComponent("TABLE4");
    	int Row = table4.getSelectedRow();//行数
//    	System.out.println("Row=====:"+Row);
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}		    
		TParm data = table4.getParmValue().getRow(Row);//获得数据
		String admType =data.getValue("ADM_TYPE");//门急别
		String clinictypecode = data.getValue("CLINICTYPE_CODE");//号别
		String caseNo = data.getValue("CASE_NO");//就诊号
		//判断是否作废收据
		String bilsql = " SELECT * FROM  BIL_OPB_RECP"+
                     " WHERE CASE_NO ='"+caseNo+"'"+
			         " AND RESET_RECEIPT_NO IS NULL"+
			         " AND TOT_AMT>0" +
			         " AND PAY_INS_CARD=0";
        TParm bilopbrecp= new TParm(TJDODBTool.getInstance().select(bilsql));
        if (bilopbrecp.getErrCode() < 0) {
        	err(bilopbrecp.getErrCode() + " " + bilopbrecp.getErrText());
			this.messageBox("执行操作失败");
			return ;
		}
        if (bilopbrecp.getCount()> 0) {
			messageBox("请先作废票据");
			return;
		}
      //获得就诊时间
        Timestamp billdate = StringTool.getTimestamp(
        		data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
		String admdate = StringTool.getString(billdate, "yyyyMMddHHmmss");
	//查询上传数据
		String regFeesql = " SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O," +
				" B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"+
			    " B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS," +
			    " '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"+
			    " C.DOSE_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,B.CHARGE_HOSP_CODE " +
			    " FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C " +
				" WHERE A.ORDER_CODE=B.ORDER_CODE(+) "+
			    " AND A.ORDER_CODE=C.ORDER_CODE(+) " +
			    " AND A.ADM_TYPE='"+admType+"'"+
			    " AND A.CLINICTYPE_CODE='"+ clinictypecode + "'" +
			    " AND '" + admdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
		// 挂号费
		double reg_fee = 0.0;
		// 诊查费 
		double clinic_fee = 0.0;
		TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
				regFeesql));
		if (regFeeParm.getErrCode() < 0) {
			err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
			this.messageBox("医保执行操作失败");
			return;
		}
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
				regFeeParm.setData("AR_AMT", i, reg_fee);
			}
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
				regFeeParm.setData("AR_AMT", i, clinic_fee);
			}
		}
		TParm result = TXsaveINSCard(regFeeParm, caseNo,data); // 执行医保操作
		if (null == result)
			return;
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("医保执行操作失败");
			return;
		}
//		System.out.println("result=========="+result);
		//对有挂号条患者进行处理
		if(this.getValue("REG_TYPE").equals("01")){
			TParm unRegParm = new TParm();
			String optUser = Operator.getID();
			String optTerm = Operator.getIP();
			TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
					caseNo);
			String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
			TParm inInvRcpParm = new TParm();
			inInvRcpParm.setData("RECEIPT_NO", recpNo);
			inInvRcpParm.setData("CANCEL_FLG", 0);
			inInvRcpParm.setData("RECP_TYPE", "REG");
			TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
					inInvRcpParm);
			unRegParm.setData("CASE_NO", caseNo);
			unRegParm.setData("REGCAN_USER", optUser);
			unRegParm.setData("OPT_USER", optUser);
			unRegParm.setData("OPT_TERM", optTerm);
			unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
			unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
			//有挂号条操作(类似执行退挂，不更新挂号主档REG_PATADM)
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onUnRegForAdvance", unRegParm);	
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return;
			}
			//在OPD_ORDER表里增加一笔诊查费			
			String RxNo = SystemTool.getInstance().getNo("ALL", "ODO",
					"RX_NO", "RX_NO");
			//获得门诊收据费用代码
			String SQL = " SELECT OPD_CHARGE_CODE FROM SYS_CHARGE_HOSP"+
                         " WHERE CHARGE_HOSP_CODE =" +
                         "'"+ regFeeParm.getValue("CHARGE_HOSP_CODE",0)+ "'";
			TParm REXP= new TParm(TJDODBTool.getInstance().select(SQL));
//			 System.out.println("REXP=========="+REXP);
//			 System.out.println("OPD_CHARGE_CODE=========="+REXP.getValue("OPD_CHARGE_CODE",0));
			String rexpcode = REXP.getValue("OPD_CHARGE_CODE",0);
			Timestamp date = StringTool.getTimestamp(data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
    		String bildate = StringTool.getString(date, "yyyyMMddHHmmss"); //费用发生时间
			String sql= " INSERT INTO OPD_ORDER"+
            " (CASE_NO,MR_NO,RX_NO,SEQ_NO,REGION_CODE,ADM_TYPE,ORDER_CODE,ORDER_DESC,"+
            " MEDI_QTY,TAKE_DAYS,DOSAGE_QTY,OWN_PRICE,OWN_AMT,AR_AMT," +
            " BILL_FLG,BILL_TYPE,BILL_USER,PRINT_FLG,EXEC_FLG,RECEIPT_FLG," +
            " ORDER_CAT1_CODE,CAT1_TYPE,REXP_CODE,HEXP_CODE," +
            " BILL_DATE,ORDER_DATE,OPT_USER,OPT_TERM,OPT_DATE)"+ 
            " VALUES ('"+ caseNo+ "','"+ data.getValue("MR_NO")+ "'," +
            " '"+ RxNo+ "',1,'"+Operator.getRegion()+ "','"+ admType+ "'," +
            " '"+ regFeeParm.getValue("ORDER_CODE",0)+ "'," +
            " '"+ regFeeParm.getValue("ORDER_DESC",0)+ "',0,1,1," +
            " "+ regFeeParm.getDouble("OWN_PRICE",0)+ "," +
            " "+ regFeeParm.getDouble("OWN_PRICE",0)+ "," +
            " "+ regFeeParm.getDouble("OWN_PRICE",0)+ "," +
            " 'Y','E','"+optUser+ "','N','N','N'," +
            " '"+ regFeeParm.getValue("ORDER_CAT1_CODE",0)+ "'," +
            " '"+ regFeeParm.getValue("CAT1_TYPE",0)+ "','"+ rexpcode+ "'," +
            " '"+ regFeeParm.getValue("CHARGE_HOSP_CODE",0)+ "'," +
            " to_date('"+bildate+"','yyyyMMddHH24MISS')," +
            " to_date('"+bildate+"','yyyyMMddHH24MISS'),"+                 
            " '"+optUser+ "',"+
            " '"+optTerm+ "',SYSDATE)";		
//		System.out.println("sql=========="+sql); 	
        TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
//        System.out.println("result1=========="+result1);  
      // 判断错误值
       if (result1.getErrCode() < 0) {
            messageBox(result1.getErrText());
              return;
           }			
		}
		//更新REG_PATADM表(将自费病人变成医保病人,用于收费界面)
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
//		System.out.println("opbReadCardParm=====:"+opbReadCardParm);
		if (null != opbReadCardParm
				&& null != opbReadCardParm.getValue("CONFIRM_NO")
				&& opbReadCardParm.getValue("CONFIRM_NO").length() > 0) {
			String sql = "UPDATE REG_PATADM SET CONFIRM_NO ='"
					+ opbReadCardParm.getValue("CONFIRM_NO")
					+ "', INS_PAT_TYPE='" + insParm.getValue("INS_TYPE")
					+ "' WHERE CASE_NO='" + caseNo + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
			if (updateParm.getErrCode() < 0) {
				return ;
			}
			this.messageBox("挂号成功");
		}
	}	
	/**
	 * 医保卡保存操作
     */
	private TParm TXsaveINSCard(TParm parm, String caseNo,TParm data) {
		TParm result = new TParm();
		insParm.setData("REG_PARM", parm.getData()); // 医嘱信息
		insParm.setData("DEPT_CODE", data.getValue("DEPT_CODE")); // 科室代码
		insParm.setData("MR_NO", data.getValue("MR_NO")); // 病患号
		insParm.setData("RECP_TYPE", "REG"); // 类型：REG / OPB
		insParm.setData("CASE_NO", data.getValue("CASE_NO"));
		insParm.setData("REG_TYPE", "1"); // 挂号标志:1 挂号0 非挂号
		insParm.setData("OPT_USER", Operator.getID());
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("DR_CODE", data.getValue("DR_CODE"));// 医生代码
		if (data.getValue("ADM_TYPE").equals("E")) {
			insParm.setData("EREG_FLG", "1"); // 急诊
		} else {
			insParm.setData("EREG_FLG", "0"); // 普通
		}

		insParm.setData("PRINT_NO", "111111"); // 票号(默认)
		insParm.setData("QUE_NO", data.getValue("QUE_NO"));
		//获得就诊时间
        Timestamp date = StringTool.getTimestamp(
        		data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
		String admdate = StringTool.getString(date, "yyyyMMdd");
		insParm.setData("ADM_DATE", admdate);
//		System.out.println("insParm=========="+insParm);
		//医保操作
		TParm returnParm = insExeFee(true,data);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
		if (returnType == 0 || returnType == -1) { // 取消操作
			return null;
		}

		insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // 费用结算

//		 System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(insParm.getData());

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 医保卡执行费用显示操作  true： 正流程操作

	 */
	private TParm insExeFee(boolean flg,TParm data) {
		TParm insFeeParm = new TParm();
	    insFeeParm.setData("insParm", insParm.getData()); // 医嘱信息
		insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		insFeeParm.setData("NAME", data.getValue("PAT_NAME"));//姓名
		insFeeParm.setData("MR_NO", data.getValue("MR_NO")); // 病患号
		insFeeParm.setData("FeeY", 0.0); // 应收金额
		insFeeParm.setData("PAY_TYPE", true); // 支付方式
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // 区域代码
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = new TParm();
		returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
				insFeeParm);
		if (returnParm == null
				|| null == returnParm.getValue("RETURN_TYPE")
				|| returnParm.getInt("RETURN_TYPE") == 0) {
			return null;
		}
		return returnParm;
	}
	/**
	 * 结果上传
	 */
	public void onResultload() {
		TTable table3 = (TTable) this.getComponent("TABLE3");		    
		 TParm parm = table3.getParmValue();//获得数据
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm=====:"+parm);
		 Timestamp date1 = StringTool.getTimestamp(parm.getValue("PAY_DATE",0), "yyyy-MM-dd HH:mm:ss");
 		 String paydate = StringTool.getString(date1, "yyyy/MM/dd");
//		 System.out.println("PAY_DATE=====:"+paydate);		 
         TParm resultUpParm = new TParm();
    	 TParm result = new TParm();
    	 resultUpParm.addData("APPLY_NO",parm.getValue("APPLY_NO",0));//申请顺序号
    	 resultUpParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//医院编码
    	 resultUpParm.addData("INS_TYPE", parm.getValue("INS_TYPE",0));//险种
    	 resultUpParm.addData("CARD_FLG", parm.getValue("CARD_FLG",0));//类别
    	 resultUpParm.addData("PAT_TYPE", parm.getValue("PAT_TYPE",0));//费用类别
    	 resultUpParm.addData("PAY_FLG", parm.getValue("PAY_FLG",0));//发放状态
    	 resultUpParm.addData("PAY_DATE", paydate);//发放时间
    	 resultUpParm.addData("PARM_COUNT", 7);//入参数量       	   	   
    	 resultUpParm.setData("PIPELINE", "DataDown_yb");
    	 resultUpParm.setData("PLOT_TYPE", "Y");	  	    
//         System.out.println("resultUpParm:"+resultUpParm);
         result = InsManager.getInstance().safe(resultUpParm);
//         System.out.println("result===========" + result);
         if (result.getErrCode() < 0) {
        	 this.messageBox(result.getErrText());
				return;
         }	        		
		this.messageBox("结果上传成功"); 
	}
	/**
	 * 查询已下载数据
	 */
	public void ondata() {
		String mrno = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
//		System.out.println("mrno=====:"+mrno);			
		ondownloaddata(mrno);

	}
	/**
	 *  查询已下载数据
	 */
	public void ondownloaddata(String mrNo) {
		String sql1 ="";
		if(!mrNo.equals("")){
//			String caseNo = reg.caseNo();
			 sql1 = " AND A.MR_NO ='"+ mrNo+ "'";
//					" AND A.CASE_NO = '"+ caseNo+ "'";
		}	
		TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"235959";
		String sql =
		  " SELECT A.MR_NO,C.PAT_NAME,A.CASE_NO,A.APPLY_NO,A.INS_TYPE,A.PAT_TYPE," +
		  " A.CARD_FLG,A.ID_NO,A.BILL_DATE,A.TEL_NO,A.PAY_FLG,A.PAY_DATE,A.APPROVE_TYPE," +
		  " CASE WHEN A.INS_TYPE='01' THEN '城职' " +
    	  " WHEN A.INS_TYPE='02' THEN '城乡' END AS INS_TYPE_DESC," +
    	  " CASE WHEN A.PAT_TYPE='01' THEN '门诊' " +
    	  " WHEN A.PAT_TYPE='02' THEN '门特' END AS PAT_TYPE_DESC," +
    	  " CASE WHEN A.CARD_FLG='01' THEN '有卡' " +
    	  " WHEN A.CARD_FLG='02' THEN '无卡' END AS CARD_FLG_DESC," +
    	  " CASE WHEN A.PAY_FLG='0' THEN '未发放' " +
    	  " WHEN A.PAY_FLG='1' THEN '已发放' END AS PAY_FLG_DESC," +
    	  " CASE WHEN A.APPROVE_TYPE='0' THEN '未审核' " +
    	  " WHEN A.APPROVE_TYPE='1' THEN '已审核' END AS APPROVE_TYPE_DESC," +
    	  " B.ADM_TYPE,B.CLINICTYPE_CODE,B.QUE_NO,B.DEPT_CODE,B.DR_CODE,A.PERSONAL_NO " +
		  " FROM INS_ADVANCE_OUT A,REG_PATADM B,SYS_PATINFO C" +
          " WHERE A.CASE_NO = B.CASE_NO"+ 
          " AND A.MR_NO = C.MR_NO"+
          sql1+
		  " AND A.BILL_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		  " AND A.APPROVE_TYPE = '1'" +
		  " AND A.PAY_FLG = '0'" +
		  " AND A.PAT_TYPE !='03'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=====:"+result);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {			
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE4")).removeRowAll();
			return;
		}
		((TTable) getComponent("TABLE4")).setParmValue(result);	
	}
	/**
	 * 保存病患信息
	 */
	public void SavePat() {
		if(this.getValue("MR_NO").equals("")){
			 this.messageBox("病案号不能为空");
            return;         
		 }
		 TParm parm = new TParm();
		 parm.setData("MR_NO", this.getValue("MR_NO"));
		 parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
	     parm.setData("ID_NO", this.getValue("ID_NO"));
	     parm.setData("TEL_NO", this.getValue("TEL_NO"));
		String sql = "UPDATE SYS_PATINFO SET PAT_NAME ='"
			+ parm.getValue("PAT_NAME") + "',IDNO ='"
    		+ parm.getValue("ID_NO") + "',TEL_HOME ='"
    		+ parm.getValue("TEL_NO") + "'" +
    		" WHERE MR_NO = '"
    		+ parm.getValue("MR_NO") + "'";
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            // 判断错误值
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            messageBox("保存成功");          
            //重新更新数据              	
            onRegpatadm(this.getValue("MR_NO").toString());
	}
	/**
	 * 清空
	 */
	public void onClear() {		
		this.setValue("MR_NO", "");
		this.setValue("PAT_NAME", "");
		this.setValue("ID_NO", "");
		this.setValue("TEL_NO", "");
		this.setValue("INS_TYPE", "01");
		this.setValue("PAT_TYPE", "01");
		this.setValue("CARD_FLG", "01");
		this.setValue("REG_TYPE", "02");
		insFlg = false;// 医保卡读卡成功管控
		insType =""; // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特
		insParm = null;//分割数据
		reg = null;// reg对象
		pat = null;//病患对象	
		((TTable) getComponent("TABLE1")).removeRowAll();
		((TTable) getComponent("TABLE2")).removeRowAll();
		((TTable) getComponent("TABLE3")).removeRowAll();
		((TTable) getComponent("TABLE4")).removeRowAll();
		this.setValue("START_DATE",SystemTool.getInstance().getDate());
	}
}
