package com.javahis.ui.ins;

import jdo.ins.INSTJTool;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;


/**
 * 
 * <p>
 * Title: ������Ŀ��챸����Ϣ����
 * </p>
 */
public class INSDiagnosisControl extends TControl {
	private TTable table;// table����
    //ҽ��ҽԺ����
    private String nhi_hosp_code;
    //ҽԺ����
    private String nhi_hosp_desc;

	public void onInit() {
		super.onInit();
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		 table = (TTable) this.getComponent("Table");
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
		onClear();

	}

	/**
	 * ���Ӷ�Table�ļ���
	 * 
	 * @param row
	 */
	public void onTableClicked() {
		int row = (Integer) callFunction("UI|Table|getClickedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|Table|getThis");
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TParm result = new TParm();
		 if ("".equals(this.getValue("MR_NO"))) {
	            messageBox("�����Ų���Ϊ��");
	            return;
	        }
		 setValue("MR_NO", PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO"))));
		queryTParm.setData("MR_NO", this.getValue("MR_NO"));		
		String sql =
		  " SELECT A.MR_NO,A.PAT_NAME,A.CASE_NO,A.ADM_SEQ,"+
          " A.IN_DATE,A.HIS_CTZ_CODE"+
          " FROM INS_ADM_CONFIRM A"+
          " WHERE A.MR_NO  = '"+ queryTParm.getData("MR_NO")+ "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("result=====:"+result);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//ִ��ʧ��
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//��������
			return;
		}
		((TTable) getComponent("Table")).setParmValue(result);
	}
	/**
	 * �ϴ�
	 */
	public void onUpload() {
		 TParm UpParm = new TParm();
		 TParm result = new TParm();
		 UpParm.addData("NHI_HOSP_NO", "000551");//ҽԺ����
		 UpParm.addData("NHI_TYPE", "2");//����
		 UpParm.addData("NHI_CODE", "002242");//�շ���Ŀ����
		 UpParm.addData("PERSONAL_NO", "B915109123");//���˱���
		 UpParm.addData("ADM_SEQ",  "L0141405080013");//˳���
		 UpParm.addData("OUTEXM_HOSP_NO", "000168");//��Ŀ����ҽԺ����
		 UpParm.addData("OUTEXM_REASON", "���");//���ԭ��Ŀ��
		 UpParm.setData("PIPELINE", "DataDown_zjks");	
		 UpParm.setData("PLOT_TYPE", "U");
		 UpParm.addData("PARM_COUNT", 7);//������� 
         System.out.println("UpParm:====="+UpParm);
	     result = InsManager.getInstance().safe(UpParm);
	        System.out.println("result" + result);
	        if (result.getErrCode() < 0) {
	            this.messageBox(result.getErrText());
	            return;
	        }
	        this.messageBox("�ϴ��ɹ�");
	     //������Ŀ��챸����Ϣ�ϴ�
        //�õ�������Ŀ��챸����Ϣ������
//		 TParm parm = new TParm();
//        TParm Diagnosis1 = INSUpLoadTool.getInstance().getDiagnosisData1(parm);
//        System.out.println("�õ�������Ŀ��챸����ϢDiagnosis1====" + Diagnosis1);
//        System.out.println("Diagnosis1====ORDER_CODE" + Diagnosis1.getData("ORDER_CODE"));
//        if (Diagnosis1.getErrCode() < 0) {
//            this.messageBox(Diagnosis1.getErrText());
//            return;
//        }
//        if(Diagnosis1.getData("ORDER_CODE")!=null){
//        	 //���˱���
//            String personalNo = parm.getValue("PERSONAL_NO");
//            if (this.DataUpload_U(Diagnosis1,personalNo).getErrCode() < 0)
//                return;   	 
//      }	
	}
    /**
     * ������Ŀ��챸����Ϣ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_U(TParm Diagnosis1, String personalNo) {
	    TParm Parm = new TParm();
	    TParm result = new TParm();
	    TParm UpParm = new TParm();
	    String hisctzCode = Diagnosis1.getValue("HIS_CTZ_CODE",0);
	    System.out.println("hisctzCode=====:"+hisctzCode);
	    String nhiType = "";
	    //�������
	    if(hisctzCode.equals("11")||
	       hisctzCode.equals("12")||
	       hisctzCode.equals("13")){
	          	nhiType = "1";//��ְ
	    }else 
	    if(hisctzCode.equals("21")||
	       hisctzCode.equals("22")||
	       hisctzCode.equals("23")){
	          	nhiType = "2";//����
	     }
	    int count = Diagnosis1.getCount("CASE_NO");
    	 for (int i = 0; i < count; i++) {
    	 Parm.setData("ORDER_CODE", Diagnosis1.getData("ORDER_CODE",i));
    	 Parm.setData("ADM_SEQ", Diagnosis1.getData("ADM_SEQ",i));
    	 TParm Diagnosis2 = INSUpLoadTool.getInstance().getDiagnosisData2(Parm);
    	 System.out.println("�õ�������Ŀ��챸����ϢDiagnosis2====" + Diagnosis2);
     	 if(Diagnosis2.getData("ORDER_CODE")==null)
    		 return result;
    	 int cnt = Diagnosis2.getCount("ORDER_CODE");
    	 for (int j = 0; j < cnt; j++) {
    		 UpParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//ҽԺ����
    		 UpParm.addData("NHI_TYPE", nhiType);//����
    		 UpParm.addData("NHI_CODE", Diagnosis2.getData("NHI_ORDER_CODE",j));//�շ���Ŀ����
    		 UpParm.addData("PERSONAL_NO", personalNo);//���˱���
    		 UpParm.addData("ADM_SEQ",  Diagnosis1.getData("ADM_SEQ",i));//˳���
    		 UpParm.addData("OUTEXM_HOSP_NO", Diagnosis1.getData("TRANS_HOSP_CODE",i));//��Ŀ����ҽԺ����
    		 UpParm.addData("OUTEXM_REASON", Diagnosis1.getData("DR_NOTE",i));//���ԭ��Ŀ��
    		 UpParm.setData("PIPELINE", "DataDown_zjks");	
    		 UpParm.setData("PLOT_TYPE", "U");
    		 UpParm.addData("PARM_COUNT", 7);//������� 
             System.out.println("UpParm:====="+UpParm);
    	     result = InsManager.getInstance().safe(UpParm);
    	        System.out.println("result" + result);
    	        if (result.getErrCode() < 0) {
    	            this.messageBox(result.getErrText());
    	            return result;
    	        }   	        
    	  }   	 
     } 
    	 this.messageBox("�ϴ��ɹ�");
    	 return result;
  }
	
	/**
	 * ����
	 */
	public void onDownload() {
    	int Row = table.getSelectedRow();//����
    	System.out.println("Row=====:"+Row);
		//��û�����ݷ���
		if (Row < 0){
			messageBox("��ѡ������");
			  return;
		}		    
		TParm parm = table.getParmValue().getRow(Row);//�������
		System.out.println("parm=====:"+parm);
        String admSeq = parm.getValue("ADM_SEQ");
        String hisctzCode = parm.getValue("HIS_CTZ_CODE");
        System.out.println("hisctzCode=====:"+hisctzCode);
        String nhiType = "";
    	//�������
        if(hisctzCode.equals("11")||
           hisctzCode.equals("12")||
           hisctzCode.equals("13")){
        	nhiType = "1";//��ְ
        }else 
        if(hisctzCode.equals("21")||
   	       hisctzCode.equals("22")||
   	       hisctzCode.equals("23")){
        	nhiType = "2";//����
        }
        System.out.println("nhiType=====:"+nhiType);
         TParm downParm = new TParm();
    	 TParm result = new TParm();
    	 downParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//ҽԺ����
    	 downParm.addData("NHI_TYPE", nhiType);//����
    	 downParm.addData("ADM_SEQ", admSeq);//˳���
    	 downParm.addData("PARM_COUNT", 3);//�������       	   	   
    	 downParm.setData("PIPELINE", "DataDown_zjkd");
    	 downParm.setData("PLOT_TYPE", "K");	  	    
         System.out.println("downParm:"+downParm);
         result = InsManager.getInstance().safe(downParm,"");
         System.out.println("result===========" + result);
         if (!INSTJTool.getInstance().getErrParm(result)) {
        	 messageBox(result.getErrText());
 			return;
 		}
	     this.messageBox("���سɹ�");
	     ((TTable) getComponent("TableDown")).setParmValue(result);  
	}
	/**
	 * ���
	 */
	public void onClear() {
		((TTable) getComponent("Table")).removeRowAll();
		((TTable) getComponent("TableDown")).removeRowAll();
		this.setValue("MR_NO", "");

	}	    	 
}
