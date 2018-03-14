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
 * Title:�渶�����ӳ�����
 * </p>
 */
public class INSADVANCEControl extends TControl {
    private String nhi_hosp_code; //ҽ��ҽԺ����   
	private Pat pat; // ��������	
	private boolean insFlg = false;// ҽ���������ɹ��ܿ�
	private String insType; // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
	private TParm insParm;//�ָ�����
	Reg reg;// reg����
	private TParm regionParm; // ���ҽ���������
	private TParm parmEKT; // ��ȡҽ�ƿ���Ϣ
	// ҳǩ
	private TTabbedPane tabbedPane;
	public void onInit() {
		super.onInit();
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // ҳǩ
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
      //�����¼�
		callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		onClear();

	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {
		if(tabbedPane.getSelectedIndex()==2){
		String mrno = getValue("MR_NO").toString();
		if(mrno!="")			
		onQuery();
		}		 
	}
	/**
	 * �����ʷ����
	 * @param order
	 */
	public void onTableClicked(int row){
		TTable table1 = (TTable) this.getComponent("TABLE1");
		TParm Parm = table1.getParmValue();// SYS_FEEҽ��
		//��ֵ����
		setValue("MR_NO", Parm.getValue("MR_NO", row));
		setValue("PAT_NAME",Parm.getValue("PAT_NAME", row));
		setValue("ID_NO",Parm.getValue("ID_NO", row));
		setValue("TEL_NO", Parm.getValue("TEL_NO", row));
	}
	/**
	 * ��ȡҽ�ƿ�
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
	 * ��ȡҽ�ƿ�(����������)
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
	 * ��ѯ
	 */
	public void onQuery() {
		 if(this.getValue("START_DATE").equals("")){
			 this.messageBox("���÷���ʱ�䲻��Ϊ��");
             return;         
		 }
		 if(this.getValue("MR_NO").equals("")){
			 this.messageBox("�����Ų���Ϊ��");
            return;         
		 }	 
		 onQueryNO();
	}
	/**
	 * ��ѯ����
	 */
	public void onQueryNO() {
		String mrno = PatTool.getInstance().checkMrno(
			TypeTool.getString(getValue("MR_NO")));
//		System.out.println("mrno=====:"+mrno);
		onPatNO(mrno);							
		if(tabbedPane.getSelectedIndex()==0){
		//�渶�����ӳ������ϴ�(��ѯ�����Һ���Ϣ)
		onRegpatadm(mrno);
		}
        else if(tabbedPane.getSelectedIndex()==1){
        //�渶�����ӳ���������(δ���ͨ��)
        onInsadvance(mrno,"0","0");	
        
		}
        else if(tabbedPane.getSelectedIndex()==2){
        //�渶���÷��Ž���ϴ�(�����ͨ��)
        onInsadvance(mrno,"1","1");		
		}
	}
	/**
	 * ��ѯ������Ϣ
	 */
	public void onPatNO(String mrNo) {
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			setValue("MR_NO", "");
			setValue("PAT_NAME","");
			setValue("ID_NO","");
			setValue("TEL_NO","");
			return;
		}
		// modify by huangtt 20160930 EMPI���߲�����ʾ start
		 mrNo =  PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160930 EMPI���߲�����ʾ end
		
		//������ʾ
		setValue("MR_NO",mrNo);
		setValue("PAT_NAME", pat.getName().trim());
		setValue("ID_NO", pat.getIdNo());
		setValue("TEL_NO", pat.getTelHome());
		
	}
	/**
	 * �渶�����ӳ������ϴ�(��ѯ�����Һ���Ϣ)
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
		
		//����Ƿ�ҽ���Һ�
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
//			messageBox("�˲���Ϊҽ���Һ�,���������ϴ�");
//			return;
//		}
		//��ѯ����
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
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//��������
			((TTable) getComponent("TABLE1")).removeRowAll();
			return;
		}
		((TTable) getComponent("TABLE1")).setParmValue(result);	
		
	}
	/**
	 * �渶�����ӳ��������ء����Ž���ϴ�
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
		  " CASE WHEN A.INS_TYPE='01' THEN '��ְ' " +
    	  " WHEN A.INS_TYPE='02' THEN '����' END AS INS_TYPE_DESC," +
    	  " CASE WHEN A.PAT_TYPE='01' THEN '����' " +
    	  " WHEN A.PAT_TYPE='02' THEN '����' END AS PAT_TYPE_DESC," +
    	  " CASE WHEN A.CARD_FLG='01' THEN '�п�' " +
    	  " WHEN A.CARD_FLG='02' THEN '�޿�' END AS CARD_FLG_DESC," +
    	  " CASE WHEN A.PAY_FLG='0' THEN 'δ����' " +
    	  " WHEN A.PAY_FLG='1' THEN '�ѷ���' END AS PAY_FLG_DESC," +
    	  " CASE WHEN A.APPROVE_TYPE='0' THEN 'δ���' " +
    	  " WHEN A.APPROVE_TYPE='1' THEN '�����' END AS APPROVE_TYPE_DESC " +
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
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//��������
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
	 * �����ϴ�
	 */
	public void onUpload() {
		TTable table1 = (TTable) this.getComponent("TABLE1");	 
		 if(this.getValue("INS_TYPE").equals("")){
			 this.messageBox("ҽ�����ֲ���Ϊ��");
             return;         
		 }
		 if(this.getValue("PAT_TYPE").equals("")){
			 this.messageBox("���������Ϊ��");
             return;         
		 }
		 if(this.getValue("CARD_FLG").equals("")){
			 this.messageBox("�ֿ����Ͳ���Ϊ��");
             return;         
		 }	
		 TParm parm = table1.getParmValue();//�������
		 int count= parm.getCount("CASE_NO");
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm:====="+parm);
//	     System.out.println("count:====="+count);
	     int j=0;
		TParm result = new TParm();
		TParm UpParm = new TParm();
//		 System.out.println("rrrrrr====="+count);	 
		 UpParm.addData("INS_TYPE", this.getValue("INS_TYPE"));//����
		 UpParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//ҽԺ����		 
		 UpParm.addData("CARD_FLG",  this.getValue("CARD_FLG"));//���
		 UpParm.addData("PAT_TYPE",  this.getValue("PAT_TYPE"));//�������
		 UpParm.addData("ID_NO", parm.getValue("ID_NO",0));//���֤����
		 Timestamp date1 = StringTool.getTimestamp(parm.getValue("ADM_DATE",0), "yyyy-MM-dd HH:mm:ss");
 		 String billdate = StringTool.getString(date1, "yyyyMMdd");
		 UpParm.addData("BILL_DATE",  billdate);//���÷���ʱ��
		 UpParm.addData("TEL_NO",  parm.getValue("TEL_NO",0));//��ϵ��ʽ
		 UpParm.setData("PIPELINE", "DataDown_yb");	
		 UpParm.setData("PLOT_TYPE", "V");		 
		 UpParm.addData("PARM_COUNT", 7);//������� 
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
	    		String admdate = StringTool.getString(date, "yyyyMMddHHmmss"); //���÷���ʱ��	    		
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
              // �жϴ���ֵ
               if (result1.getErrCode() >= 0) {
                   j++;
                   }
	           }
				 onInsadvance(parm.getValue("MR_NO",0),"0","0");
				 this.messageBox("�����ϴ��ɹ�");
		  }		 
//		 if(count>1){
//			 this.messageBox("�����ϴ��ɹ�"+j+"��");
//			 onInsadvance("","0","0");
//		 }		 
	}

	/**
	 * ��������
	 */
	public void onDownload() {
		TTable table2 = (TTable) this.getComponent("TABLE2"); 	    
		 TParm parm = table2.getParmValue();//�������	
		 int count= parm.getCount("CASE_NO");
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm=====:"+parm);
//	     System.out.println("count=====:"+count);
	     int j=0;
         TParm downParm = new TParm();
    	 TParm result = new TParm();
    	 downParm.addData("APPLY_NO",parm.getValue("APPLY_NO",0));//����˳���
    	 downParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//ҽԺ����
    	 downParm.addData("INS_TYPE", parm.getValue("INS_TYPE",0));//����
    	 downParm.addData("CARD_FLG", parm.getValue("CARD_FLG",0));//���
    	 downParm.addData("PAT_TYPE", parm.getValue("PAT_TYPE",0));//�������
    	 downParm.addData("PARM_COUNT", 5);//�������       	   	   
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
	                // �жϴ���ֵ
	                if (result1.getErrCode() >= 0) {
	                    j++;
	                }	                	
	        }
	     }
		 //��ѯ�����ص����ݣ�����ˣ�
		 ondata();	
		 this.messageBox("�������سɹ�"); 	 
		 ((TTable) getComponent("TABLE2")).removeRowAll();
	} 
	/**
	 * ��ҽ����
	 */
	public void onReadInsCard() {
		TTable table4 = (TTable) this.getComponent("TABLE4");
    	int Row = table4.getSelectedRow();//����
//    	System.out.println("Row=====:"+Row);
		//��û�����ݷ���
		if (Row < 0){
			messageBox("��ѡ������");
			  return;
		}		    
		TParm data = table4.getParmValue().getRow(Row);//�������	
		Timestamp date1 = StringTool.getTimestamp(
				data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
 		String billdate = StringTool.getString(date1, "yyyyMMdd");//���÷���ʱ��
// 		 System.out.println("billdate===========" + billdate);
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // ������������ 2���Һ�
		//ҽԺ����@���÷���ʱ��@���
		String advancecode = nhi_hosp_code+"@"+billdate+"@"+"2";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_PERSONAL_NO",data.getValue("PERSONAL_NO").trim());//���˱��루�޿���Ա��
		parm.setData("ADVANCE_TYPE","2");//�ӳٵ渶
	    insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
//	    System.out.println("insParm===========" + insParm);
		if (null == insParm) {
			return;
		}		
		int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			return;
		}
		// ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����	
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
			this.messageBox("��ò�����Ϣʧ��");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("��ҽ������������ҽ�ƿ���Ϣ,\nҽ����Ϣ:���֤����:"
					+ opbReadCardParm.getValue("SID") + "\nҽ����������:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
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
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		}		
		insFlg = true; // ҽ������ȡ�ɹ�
//		 System.out.println("insType===========" + insType);
//		 System.out.println("ctz1code===========" + ctz1code);
//		 System.out.println("confirmNo===========" + confirmNo);
//		 System.out.println("insFlg===========" + insFlg);
	}
	
	/**
	 * �渶�ӳٹҺ�
	 */
	public void onReg() {
		 if(this.getValue("REG_TYPE").equals("")){
			 this.messageBox("�Һ����Ͳ���Ϊ��");
             return;         
		 }	
		if(!insFlg){
			 this.messageBox("δ��ҽ�������ҽ����ʧ��");
             return; 
		}
		TTable table4 = (TTable) this.getComponent("TABLE4");
    	int Row = table4.getSelectedRow();//����
//    	System.out.println("Row=====:"+Row);
		//��û�����ݷ���
		if (Row < 0){
			messageBox("��ѡ������");
			  return;
		}		    
		TParm data = table4.getParmValue().getRow(Row);//�������
		String admType =data.getValue("ADM_TYPE");//�ż���
		String clinictypecode = data.getValue("CLINICTYPE_CODE");//�ű�
		String caseNo = data.getValue("CASE_NO");//�����
		//�ж��Ƿ������վ�
		String bilsql = " SELECT * FROM  BIL_OPB_RECP"+
                     " WHERE CASE_NO ='"+caseNo+"'"+
			         " AND RESET_RECEIPT_NO IS NULL"+
			         " AND TOT_AMT>0" +
			         " AND PAY_INS_CARD=0";
        TParm bilopbrecp= new TParm(TJDODBTool.getInstance().select(bilsql));
        if (bilopbrecp.getErrCode() < 0) {
        	err(bilopbrecp.getErrCode() + " " + bilopbrecp.getErrText());
			this.messageBox("ִ�в���ʧ��");
			return ;
		}
        if (bilopbrecp.getCount()> 0) {
			messageBox("��������Ʊ��");
			return;
		}
      //��þ���ʱ��
        Timestamp billdate = StringTool.getTimestamp(
        		data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
		String admdate = StringTool.getString(billdate, "yyyyMMddHHmmss");
	//��ѯ�ϴ�����
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
		// �Һŷ�
		double reg_fee = 0.0;
		// ���� 
		double clinic_fee = 0.0;
		TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
				regFeesql));
		if (regFeeParm.getErrCode() < 0) {
			err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
			this.messageBox("ҽ��ִ�в���ʧ��");
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
		TParm result = TXsaveINSCard(regFeeParm, caseNo,data); // ִ��ҽ������
		if (null == result)
			return;
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("ҽ��ִ�в���ʧ��");
			return;
		}
//		System.out.println("result=========="+result);
		//���йҺ������߽��д���
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
			//�йҺ�������(����ִ���˹ң������¹Һ�����REG_PATADM)
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onUnRegForAdvance", unRegParm);	
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return;
			}
			//��OPD_ORDER��������һ������			
			String RxNo = SystemTool.getInstance().getNo("ALL", "ODO",
					"RX_NO", "RX_NO");
			//��������վݷ��ô���
			String SQL = " SELECT OPD_CHARGE_CODE FROM SYS_CHARGE_HOSP"+
                         " WHERE CHARGE_HOSP_CODE =" +
                         "'"+ regFeeParm.getValue("CHARGE_HOSP_CODE",0)+ "'";
			TParm REXP= new TParm(TJDODBTool.getInstance().select(SQL));
//			 System.out.println("REXP=========="+REXP);
//			 System.out.println("OPD_CHARGE_CODE=========="+REXP.getValue("OPD_CHARGE_CODE",0));
			String rexpcode = REXP.getValue("OPD_CHARGE_CODE",0);
			Timestamp date = StringTool.getTimestamp(data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
    		String bildate = StringTool.getString(date, "yyyyMMddHHmmss"); //���÷���ʱ��
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
      // �жϴ���ֵ
       if (result1.getErrCode() < 0) {
            messageBox(result1.getErrText());
              return;
           }			
		}
		//����REG_PATADM��(���ԷѲ��˱��ҽ������,�����շѽ���)
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
			this.messageBox("�Һųɹ�");
		}
	}	
	/**
	 * ҽ�����������
     */
	private TParm TXsaveINSCard(TParm parm, String caseNo,TParm data) {
		TParm result = new TParm();
		insParm.setData("REG_PARM", parm.getData()); // ҽ����Ϣ
		insParm.setData("DEPT_CODE", data.getValue("DEPT_CODE")); // ���Ҵ���
		insParm.setData("MR_NO", data.getValue("MR_NO")); // ������
		insParm.setData("RECP_TYPE", "REG"); // ���ͣ�REG / OPB
		insParm.setData("CASE_NO", data.getValue("CASE_NO"));
		insParm.setData("REG_TYPE", "1"); // �Һű�־:1 �Һ�0 �ǹҺ�
		insParm.setData("OPT_USER", Operator.getID());
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("DR_CODE", data.getValue("DR_CODE"));// ҽ������
		if (data.getValue("ADM_TYPE").equals("E")) {
			insParm.setData("EREG_FLG", "1"); // ����
		} else {
			insParm.setData("EREG_FLG", "0"); // ��ͨ
		}

		insParm.setData("PRINT_NO", "111111"); // Ʊ��(Ĭ��)
		insParm.setData("QUE_NO", data.getValue("QUE_NO"));
		//��þ���ʱ��
        Timestamp date = StringTool.getTimestamp(
        		data.getValue("BILL_DATE"), "yyyy-MM-dd HH:mm:ss");
		String admdate = StringTool.getString(date, "yyyyMMdd");
		insParm.setData("ADM_DATE", admdate);
//		System.out.println("insParm=========="+insParm);
		//ҽ������
		TParm returnParm = insExeFee(true,data);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.ʧ�� 1. �ɹ�
		if (returnType == 0 || returnType == -1) { // ȡ������
			return null;
		}

		insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�����
		insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // ���ý���

//		 System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(insParm.getData());

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * ҽ����ִ�з�����ʾ����  true�� �����̲���

	 */
	private TParm insExeFee(boolean flg,TParm data) {
		TParm insFeeParm = new TParm();
	    insFeeParm.setData("insParm", insParm.getData()); // ҽ����Ϣ
		insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		insFeeParm.setData("NAME", data.getValue("PAT_NAME"));//����
		insFeeParm.setData("MR_NO", data.getValue("MR_NO")); // ������
		insFeeParm.setData("FeeY", 0.0); // Ӧ�ս��
		insFeeParm.setData("PAY_TYPE", true); // ֧����ʽ
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // �������
		insFeeParm.setData("FEE_FLG", flg); // �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
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
	 * ����ϴ�
	 */
	public void onResultload() {
		TTable table3 = (TTable) this.getComponent("TABLE3");		    
		 TParm parm = table3.getParmValue();//�������
		 parm.setData("OPT_USER", Operator.getID());
	     parm.setData("OPT_TERM", Operator.getIP());
//	     System.out.println("parm=====:"+parm);
		 Timestamp date1 = StringTool.getTimestamp(parm.getValue("PAY_DATE",0), "yyyy-MM-dd HH:mm:ss");
 		 String paydate = StringTool.getString(date1, "yyyy/MM/dd");
//		 System.out.println("PAY_DATE=====:"+paydate);		 
         TParm resultUpParm = new TParm();
    	 TParm result = new TParm();
    	 resultUpParm.addData("APPLY_NO",parm.getValue("APPLY_NO",0));//����˳���
    	 resultUpParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//ҽԺ����
    	 resultUpParm.addData("INS_TYPE", parm.getValue("INS_TYPE",0));//����
    	 resultUpParm.addData("CARD_FLG", parm.getValue("CARD_FLG",0));//���
    	 resultUpParm.addData("PAT_TYPE", parm.getValue("PAT_TYPE",0));//�������
    	 resultUpParm.addData("PAY_FLG", parm.getValue("PAY_FLG",0));//����״̬
    	 resultUpParm.addData("PAY_DATE", paydate);//����ʱ��
    	 resultUpParm.addData("PARM_COUNT", 7);//�������       	   	   
    	 resultUpParm.setData("PIPELINE", "DataDown_yb");
    	 resultUpParm.setData("PLOT_TYPE", "Y");	  	    
//         System.out.println("resultUpParm:"+resultUpParm);
         result = InsManager.getInstance().safe(resultUpParm);
//         System.out.println("result===========" + result);
         if (result.getErrCode() < 0) {
        	 this.messageBox(result.getErrText());
				return;
         }	        		
		this.messageBox("����ϴ��ɹ�"); 
	}
	/**
	 * ��ѯ����������
	 */
	public void ondata() {
		String mrno = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
//		System.out.println("mrno=====:"+mrno);			
		ondownloaddata(mrno);

	}
	/**
	 *  ��ѯ����������
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
		  " CASE WHEN A.INS_TYPE='01' THEN '��ְ' " +
    	  " WHEN A.INS_TYPE='02' THEN '����' END AS INS_TYPE_DESC," +
    	  " CASE WHEN A.PAT_TYPE='01' THEN '����' " +
    	  " WHEN A.PAT_TYPE='02' THEN '����' END AS PAT_TYPE_DESC," +
    	  " CASE WHEN A.CARD_FLG='01' THEN '�п�' " +
    	  " WHEN A.CARD_FLG='02' THEN '�޿�' END AS CARD_FLG_DESC," +
    	  " CASE WHEN A.PAY_FLG='0' THEN 'δ����' " +
    	  " WHEN A.PAY_FLG='1' THEN '�ѷ���' END AS PAY_FLG_DESC," +
    	  " CASE WHEN A.APPROVE_TYPE='0' THEN 'δ���' " +
    	  " WHEN A.APPROVE_TYPE='1' THEN '�����' END AS APPROVE_TYPE_DESC," +
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
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {			
			messageBox("E0008");//��������
			((TTable) getComponent("TABLE4")).removeRowAll();
			return;
		}
		((TTable) getComponent("TABLE4")).setParmValue(result);	
	}
	/**
	 * ���没����Ϣ
	 */
	public void SavePat() {
		if(this.getValue("MR_NO").equals("")){
			 this.messageBox("�����Ų���Ϊ��");
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
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            messageBox("����ɹ�");          
            //���¸�������              	
            onRegpatadm(this.getValue("MR_NO").toString());
	}
	/**
	 * ���
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
		insFlg = false;// ҽ���������ɹ��ܿ�
		insType =""; // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		insParm = null;//�ָ�����
		reg = null;// reg����
		pat = null;//��������	
		((TTable) getComponent("TABLE1")).removeRowAll();
		((TTable) getComponent("TABLE2")).removeRowAll();
		((TTable) getComponent("TABLE3")).removeRowAll();
		((TTable) getComponent("TABLE4")).removeRowAll();
		this.setValue("START_DATE",SystemTool.getInstance().getDate());
	}
}
