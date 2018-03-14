package com.javahis.ui.inw;

import org.apache.commons.lang.StringUtils;

import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.AnimationWindowUtils;
import com.sun.awt.AWTUtilities;



/**
 * Title: ����һ����
 * Description:����һ����
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class INWTransferSheetControl extends TControl {
	private TParm parm;
	private TTable table;
	private String runFlg = "";//����ϵͳ����
	private String srceenWidth = "";
	String transferfilepath = "";//���ӵ��ļ��洢·��
	String transferfilename = "";//���ӵ��ļ��洢����
	private String mrNo;//������	
	private String caseNo;//�����	
	private String opBookSeq;//�������뵥��
	
	private TComboBox combobox;
    public void onInit() {
        super.onInit();
        parm = (TParm) getParameter();//��ѯ��Ϣ
		//���޴���Ϣ����
		if (null == parm) {
			return;
		}
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		opBookSeq = parm.getValue("OPBOOK_SEQ");
//		System.out.println("---mrNo----------------"+mrNo);
//		System.out.println("---caseNo----------------"+caseNo);
        table = (TTable) this.getComponent("TABLE");
        callFunction("UI|MR_NO|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onExeQuery");
        callFunction("UI|TABLE|addEventListener",
                "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        if(!mrNo.equals("")){
        	 onTableAdd(mrNo);
        }
    	   
        //��������
//        combobox = (TComboBox) this.getComponent("TRANSFER_CLASS");
//     // ����ϵͳ����
//		Object obj = this.getParameter();
//		if (obj != null) {
//			String strParameter = this.getParameter().toString();
//			String sysID = "";
//			// ����;���
//			if (strParameter.indexOf(";") != -1) {
//				sysID = strParameter.split(";")[0];
//				this.setSrceenWidth(strParameter.split(";")[1]);
//			} else {
//				sysID = this.getParameter().toString();
//			}
//
//			this.setRunFlg(sysID);
//		} else {
//			this.setRunFlg("");
//		}
//		// ���ݻ�ʿ���ͳ�ʼ����������
//		this.setTransfer();
    }
    /**
	 * ���ݻ�ʿ���ͳ�ʼ����������
	 */
    public void setTransfer() {
//    	[[id,text],[,],[WW,����-����],[WT,����-����],
//    	[WO,����-������],[TC/TW,����-CCU/����],[OI,������-ICU]] 	
      if (this.getRunFlg().equals("W")) {
          combobox.setStringData("[[id,text],[,],[WW,����-����],[WT,����-����]," +
          		"[WO,����-������],[TC/TW,����-CCU/����]]");	
      }else if (this.getRunFlg().equals("T")) {
          combobox.setStringData("[[id,text],[,],[WT,����-����],[TC/TW,����-CCU/����]]");	
      }else if (this.getRunFlg().equals("O")) {
          combobox.setStringData("[[id,text],[,],[WO,����-������],[OI,������-ICU]]");	
      }else if (this.getRunFlg().equals("I")) {
          combobox.setStringData("[[id,text],[,],[OI,������-ICU]]");	
          this.setValue("TRANSFER_CLASS","OI");	
      }else if (this.getRunFlg().equals("C")) {
    	  combobox.setStringData("[[id,text],[,],[TC/TW,����-CCU/����]]");
    	  this.setValue("TRANSFER_CLASS","TC/TW");	
      }
    }
    /**
	 * ���ݼ��
	 */
	private boolean checkdata(){
//		if(this.getValue("START_DATE").equals("")){
//    		this.messageBox("����ʱ�䲻��Ϊ��");
//    		return true;
//    	}
//    	if(this.getValue("END_DATE").equals("")){
//    		this.messageBox("����ʱ�䲻��Ϊ��");
//    		return true;
//    	}
    	if(this.getValue("MR_NO").equals("")){
    		this.messageBox("�����Ų���Ϊ��");
    		return true;
    	}
	    return false; 
	}
    /**
     * ��ѯ
     */
    public void onQuery() {
    	//���ݼ��
    	if(checkdata())
		    return;
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
//    	System.out.println("mrno=====:"+mrno);
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("�ǲ������ӵ�,���ʵ������Ϣ");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
  }   
    /**
     * ���ӵ�
     */
    public void onTransfer() {
    	//���ݼ��
//    	if(checkdata())
//		    return;
    	int Row = table.getSelectedRow();//����
		//��û�����ݷ���
		if (Row < 0){
			messageBox("��ѡ������");
			  return;
		}
    	TParm action = new TParm();
    	TParm data = table.getParmValue().getRow(Row);//�������
//    	System.out.println("---data----------------"+data);
    	

    	
    	//����-������WO
    	if(data.getValue("TRANSFER_CLASS_CODE").equals("WO")){
//	    	action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//���ӵ���  		
//	    	//���ó���
//	    	action.setData("MR_NO", data.getValue("MR_NO"));//������ zhanglei 2017.05.02 �����ռ��������
//			action.setData("CASE_NO", data.getValue("CASE_NO"));//����� zhanglei 2017.05.02 �����ռ��������
//			//this.messageBox("MR_NO" + action.getValue("MR_NO") + "--CASE_NO" + action.getValue("CASE_NO"));
//	     	this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action);
	     	
	    	String opbookSeq = data.getValue("OPBOOK_SEQ");
	    	String sql = "SELECT TYPE_CODE FROM OPE_OPBOOk WHERE OPBOOK_SEQ = '"+data.getValue("OPBOOK_SEQ")+"'";
//	    	System.out.println(">>>>>>>>>>"+sql);
	    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	    	if(result == null || result.getCount() <= 0){
	    		this.messageBox("ȱ������");
	    		System.out.println("δ��OPE_OPBOOk�в�ѯ������");
	    		return;
	    	}
	    	if("1".equals(result.getValue("TYPE_CODE", 0))){//������
	    		TParm parm = new TParm();
	    		parm.setData("OPBOOK_SEQ", opbookSeq);
	    		parm.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//���ӵ���  	
//	    		this.openWindow("%ROOT%/config/pda/PDAOpeConnectUI.x", parm);
	    		TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%/config/pda/PDAOpeConnectUI.x"));
				if (parm != null)
					frame.setParameter(parm);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
	    	}else if("2".equals(result.getValue("TYPE_CODE", 0))){//������
	    		action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//���ӵ���  		
		    	//���ó���
		    	action.setData("MR_NO", data.getValue("MR_NO"));//������ zhanglei 2017.05.02 �����ռ��������
				action.setData("CASE_NO", data.getValue("CASE_NO"));//����� zhanglei 2017.05.02 �����ռ��������
				//this.messageBox("MR_NO" + action.getValue("MR_NO") + "--CASE_NO" + action.getValue("CASE_NO"));
//				openWindow("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action, true);
				TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%\\config\\inw\\INWTransferSheetWo.x"));
				if (action != null)
					frame.setParameter(action);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
//		     	this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x", action);
	    	}
    	}else{   	
	    	action.setData("TRANSFER_CODE",data.getValue("TRANSFER_CODE"));//���ӵ���
	    	action.setData("TRANSFER_FILE_PATH",
					data.getValue("TRANSFER_FILE_PATH"));//���ӵ��ļ��洢·��
	    	action.setData("TRANSFER_FILE_NAME",
					data.getValue("TRANSFER_FILE_NAME"));//���ӵ��ļ��洢����
			action.setData("MR_NO", data.getValue("MR_NO"));//������
			action.setData("CASE_NO", data.getValue("CASE_NO"));//�����
			action.setData("PAT_NAME", data.getValue("PAT_NAME"));//����
			if (StringUtils.isEmpty(opBookSeq)) {
				opBookSeq = data.getValue("OPBOOK_SEQ");
			}
			action.setData("OPBOOK_SEQ", opBookSeq);//�������뵥��
			action.setData("FLG",true);//�������ɲ������
	//         System.out.println("---action----------------"+action);
	 	    //����ģ�� 
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
			frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
  		}
  		
		
    	 //ˢ������
 	    onTableAdd(data.getValue("MR_NO")); 	
  }   
    /**
     * ���
     */
    public void onClear() {
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
 	    this.setValue("MR_NO","");
 	    this.setValue("TRANSFER_CLASS","");	
   	    this.setValue("STATUS_FLG","");
   	    this.setValue("FROM_DEPT","");
   	    this.setValue("TO_DEPT","");
//    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
//    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    
    /**
     * TABLE��˫���¼�
     */
   public void onSelect() {
	   onTransfer();
   }
    /**
     * �����Żس��¼�
     */
    public void onExeQuery() {
       	//���ݼ��
    	if(checkdata())
		    return;
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
//    	System.out.println("mrno=====:"+mrno);
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("�ǲ������ӵ�,���ʵ������Ϣ");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
    }
    /**
     * ������ɨ���¼�
     */
    public void onScream() {
       	//���ݼ��
    	if(checkdata())
		    return; 
    	String mrno = PatTool.getInstance().checkMrno(
    			TypeTool.getString(getValue("MR_NO")));
    	if(!mrNo.equals("")&&!mrNo.equals(mrno)){
    		messageBox("�ǲ������ӵ�,���ʵ������Ϣ");
    		 return;
    	} 
    	onTableAdd(mrno);
    	this.setValue("MR_NO", mrno);
    	TTextField no = ((TTextField) getComponent("MR_NO"));
    	no.grabFocus();
    }
    
    public void onTableAdd(String mrno) {
//    	System.out.println("onTableAdd=====:1");
    	TParm result = new TParm();
    	String sql1 ="";
    	String sql2 ="";
    	String sql3 ="";
    	String sql4 ="";
    	String sql5 ="";
    	String transferclass = getValue("TRANSFER_CLASS").toString();//��������
//    	System.out.println("transferclass=====:"+transferclass);
		String statusflg = getValue("STATUS_FLG").toString();//����״̬
//		System.out.println("statusflg====="+statusflg);
		if(!transferclass.equals(""))
			sql1 =" AND A.TRANSFER_CLASS = '"+ transferclass + "'";				
		if(!statusflg.equals(""))
			sql2 =" AND A.STATUS_FLG = '"+ statusflg + "'";
//		System.out.println("FROM_DEPT====="+getValue("FROM_DEPT"));
		//ת������
		if(this.getValue("FROM_DEPT")==null)			
		sql3 = "";
		else if(this.getValue("FROM_DEPT").equals(""))
		sql3 = "";
		else
		sql3 =" AND A.FROM_DEPT = '"+ getValue("FROM_DEPT") + "'";	
		//ת�����
		if(this.getValue("TO_DEPT")==null)			
			sql4 ="";
		else if(this.getValue("TO_DEPT").equals(""))
			sql4 ="";
		else
			sql4 =" AND A.TO_DEPT = '"+ getValue("TO_DEPT") + "'";	
		
		if(!caseNo.equals(""))
			sql5 =" AND A.CASE_NO = '"+ caseNo + "'";
//		String startDate = StringTool.getString(TypeTool
//				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
//		String endDate = StringTool.getString(TypeTool
//				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
//		System.out.println("onTableAdd=====:3");
		//��ѯ����
		String sql =" SELECT A.TRANSFER_CODE,A.TRANSFER_FILE_PATH," +
				" A.TRANSFER_FILE_NAME,A.CASE_NO," +
		" CASE WHEN A.TRANSFER_CLASS ='WW' THEN '����-����'"+
		" WHEN A.TRANSFER_CLASS ='WT' THEN '����-����'" + 
		" WHEN A.TRANSFER_CLASS ='WO' THEN '����-������'" + 
		" WHEN A.TRANSFER_CLASS ='TC/TW' THEN '����-CCU/����'" + 
		/* modified by WangQing 20170518*/
		" WHEN A.TRANSFER_CLASS ='OC/OW' THEN '������-CCU/����'" +
		" WHEN A.TRANSFER_CLASS ='OI' THEN '������-ICU'" +
		
		" WHEN A.TRANSFER_CLASS ='IW' THEN 'ICU-����' " +
		" WHEN A.TRANSFER_CLASS ='ET' THEN '����-����' " +
		" WHEN A.TRANSFER_CLASS ='EO' THEN '����-������' " +
		" WHEN A.TRANSFER_CLASS ='EW' THEN '����-����' END AS  TRANSFER_CLASS," +
		" CASE WHEN A.STATUS_FLG ='4' THEN '������'" +
		" WHEN A.STATUS_FLG ='5' THEN '�ѽ���' END AS  STATUS_FLG," +
		" B.DEPT_CHN_DESC AS FROM_DEPT,C.DEPT_CHN_DESC AS TO_DEPT," +
		" A.MR_NO,A.PAT_NAME,CASE WHEN D.SEX_CODE ='1' THEN '��'" +
		" WHEN D.SEX_CODE ='2' THEN 'Ů' END AS SEX_CODE,E.USER_NAME AS CRE_USER," +
		" A.CRE_DATE,F.USER_NAME AS FROM_USER,G.USER_NAME AS TO_USER," +
		" A.TRANSFER_DATE,A.TRANSFER_CLASS AS TRANSFER_CLASS_CODE,A.OPBOOK_SEQ " +
		" FROM INW_TRANSFERSHEET A,SYS_DEPT B,SYS_DEPT C,SYS_PATINFO D,SYS_OPERATOR E," +
		" SYS_OPERATOR F,SYS_OPERATOR G" +
		" WHERE A.MR_NO = '"+ mrno + "'" +
		sql1+
		sql2+
		sql3+
		sql4+
		sql5+
		" AND A.FROM_DEPT = B.DEPT_CODE(+)" +
		" AND A.TO_DEPT = C.DEPT_CODE(+)" +
		" AND A.MR_NO = D.MR_NO" +
		" AND A.CRE_USER =E.USER_ID(+)" +
		" AND A.FROM_USER =F.USER_ID(+)" +
		" AND A.TO_USER =G.USER_ID(+)" +
		" ORDER BY A.CRE_DATE DESC";		
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
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);	
		TTextField no = ((TTextField) getComponent("MR_NO"));
		no.grabFocus();
    }
    /**
	 * ����ϵͳ����
	 */
	public void setRunFlg(String runFlg) {
		this.runFlg = runFlg;
	}
	/**
	 * �õ�ϵͳ����
	 */
	public String getRunFlg() {
		return runFlg;
	}
	public String getSrceenWidth() {
		return srceenWidth;
	}

	public void setSrceenWidth(String srceenWidth) {
		this.srceenWidth = srceenWidth;
	}
}
