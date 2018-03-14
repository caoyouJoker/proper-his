package com.javahis.ui.ins;

import java.sql.Timestamp;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;


/**
 * 
 * <p>
 * Title:资格确认书延迟申请
 * </p>
 */
public class INSADVANCEADMControl extends TControl {  
    private String nhi_hosp_code; //医保医院代码   
	private Pat pat; // 病患对象	
	Reg reg;// reg对象

	// 页签
	private TTabbedPane tabbedPane;  
	public void onInit() {
		super.onInit();
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
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
	 * 查询
	 */
	public void onQuery() {
		 if(this.getValue("START_DATE").equals("")){
			 this.messageBox("住院时间不能为空");
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
		//资格确认书延迟申请上传(查询病患挂号信息)
		onAdminp(mrno);
		}
        else if(tabbedPane.getSelectedIndex()==1){
        //资格确认书延迟申请下载(未审核通过)
        onInsadvance(mrno,"0","0");	
        
		}
        else if(tabbedPane.getSelectedIndex()==2){
        //资格确认书延迟发放结果上传(审核已通过)
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
		//界面显示
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName().trim());
		setValue("ID_NO", pat.getIdNo());
		setValue("TEL_NO", pat.getTelHome());
		
	}
	/**
	 * 资格确认书延迟申请上传(查询病患住院信息)
	 */
	public void onAdminp(String mrNo) {
		String sql1 ="";		
		if(!mrNo.equals("")){
			 sql1 = " AND A.MR_NO ='"+ mrNo+ "'";

		}	
		TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"235959";
		//查询病患
		String sql =
		  " SELECT A.MR_NO,B.PAT_NAME,A.CASE_NO,B.IDNO AS ID_NO,B.TEL_HOME AS TEL_NO,A.IN_DATE " +
		  " FROM ADM_INP A,SYS_PATINFO B"+
		  " WHERE A.MR_NO = B.MR_NO" +
		  sql1 +
		  " AND A.IN_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
		  " AND A.CANCEL_FLG = 'N'";
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
	 * 资格确认书延迟下载、发放结果上传
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
    	  " CASE WHEN A.PAT_TYPE='03' THEN '住院' END AS PAT_TYPE_DESC," +
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
		  " AND A.PAT_TYPE ='03'";
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
		 Timestamp date1 = StringTool.getTimestamp(parm.getValue("IN_DATE",0), "yyyy-MM-dd HH:mm:ss");
 		 String billdate = StringTool.getString(date1, "yyyyMMdd");
		 UpParm.addData("BILL_DATE",  billdate);//住院时间
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
	        	Timestamp date = StringTool.getTimestamp(parm.getValue("IN_DATE",i), "yyyy-MM-dd HH:mm:ss");
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
			 sql1 = " AND A.MR_NO ='"+ mrNo+ "'";
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
    	  " CASE WHEN A.PAT_TYPE='03' THEN '住院' END AS PAT_TYPE_DESC," +
    	  " CASE WHEN A.CARD_FLG='01' THEN '有卡' " +
    	  " WHEN A.CARD_FLG='02' THEN '无卡' END AS CARD_FLG_DESC," +
    	  " CASE WHEN A.PAY_FLG='0' THEN '未发放' " +
    	  " WHEN A.PAY_FLG='1' THEN '已发放' END AS PAY_FLG_DESC," +
    	  " CASE WHEN A.APPROVE_TYPE='0' THEN '未审核' " +
    	  " WHEN A.APPROVE_TYPE='1' THEN '已审核' END AS APPROVE_TYPE_DESC," +
    	  " 'I' AS ADM_TYPE,'' AS CLINICTYPE_CODE,'' AS QUE_NO,B.DEPT_CODE," +
    	  " B.VS_DR_CODE,A.PERSONAL_NO " +
		  " FROM INS_ADVANCE_OUT A,ADM_INP B,SYS_PATINFO C" +
          " WHERE A.CASE_NO = B.CASE_NO"+ 
          " AND A.MR_NO = C.MR_NO"+
          sql1+
		  " AND A.BILL_DATE  BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
		  " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		  " AND A.APPROVE_TYPE = '1'" +
		  " AND A.PAY_FLG = '0'" +
		  " AND A.PAT_TYPE ='03'";
//		System.out.println("sql=====:"+sql);	
		result = new TParm(TJDODBTool.getInstance().select(sql));
//	    System.out.println("result=====:"+result);
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
            onAdminp(this.getValue("MR_NO").toString());
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
		this.setValue("PAT_TYPE", "03");
		this.setValue("CARD_FLG", "01");
		reg = null;// reg对象
		pat = null;//病患对象	
		((TTable) getComponent("TABLE1")).removeRowAll();
		((TTable) getComponent("TABLE2")).removeRowAll();
		((TTable) getComponent("TABLE3")).removeRowAll();
		((TTable) getComponent("TABLE4")).removeRowAll();
		this.setValue("START_DATE",SystemTool.getInstance().getDate());
	}
}
