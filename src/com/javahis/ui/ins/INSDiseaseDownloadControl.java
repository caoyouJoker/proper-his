package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;


/**
 * <p>Title: 1.8.22��������Ϣ����</p>
 *
 * <p>Description:1.8.22��������Ϣ����</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yufh
 */
public class INSDiseaseDownloadControl extends TControl {

	//ҽ��ҽԺ����
    private String nhi_hosp_code;
    /**
     * ��ʼ��
     */
    public void onInit() {
    	 TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                 getRegion());
         this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
         onQuery();
    }

    /**
     * ��ѯ
     */
    public void onQuery() { 
    	TParm result = new TParm();
    	 String sql = "";
        sql = " SELECT  ID AS DISEASES_CODE,CHN_DESC AS DISEASES_DESC "+  
              " FROM SYS_DICTIONARY"+  
              " WHERE GROUP_ID = 'SIN_DISEASE'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
    		messageBox(result.getErrText());
    		messageBox("ִ��ʧ��");
    		return;
    	}
    	if (result.getCount()<= 0) {
    		messageBox("��������");
    		((TTable) getComponent("TABLE")).removeRowAll();			
    		return;
    	}			
        ((TTable) getComponent("TABLE")).setParmValue(result);
    }
    /**
     * ����
     */
    public void onDownload() {
    	TParm parm = new TParm();
    	parm.setData("PIPELINE", "DataDown_zjkd");
    	parm.setData("PLOT_TYPE", "W");
    	parm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//ҽԺ����
    	parm.addData("PARM_COUNT", 1);//�������
    	TParm result = InsManager.getInstance().safe(parm, "");
    	System.out.println("result" + result);
    	System.out.println("result2======" + result.getCount("DISEASES_CODE"));
    	 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return;
         }
	 
    	 //��������    	
//    	 int count = result.getCount("DISEASES_CODE");
//    	 for(int i=0;i<count;i++){
//    		 String sql = " INSERT INTO SYS_DICTIONARY " +
//    		 " (GROUP_ID,ID,CHN_DESC,SEQ,DESCRIPTION,OPT_USER,OPT_TERM,OPT_DATE)" +
//    		 " VALUES ('SIN_DISEASE','"+ result.getValue("DISEASES_CODE",i)+ "')";	
//    		 ;
//    		 
//    	 }
    	 onQuery(); 
    }



    /**
     * ���
     */
    public void onClear() {
    	 ((TTable) getComponent("TABLE")).removeRowAll();
    }
		
	
}
