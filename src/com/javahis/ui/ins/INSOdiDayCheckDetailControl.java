//package com.javahis.ui.ins;
//
//public class INSOdiDayCheckDetailControl {
//
//}
package com.javahis.ui.ins;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
/**
 * <p>Title: סԺҽ���ն���ϸ�ʲ�ƽ��Ϣ </p>
 *
 * <p>Description:  סԺҽ���ն���ϸ�ʲ�ƽ��Ϣ</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120211
 * @version 1.0
 */
public class INSOdiDayCheckDetailControl extends TControl{
	
	TParm acceptData = new TParm(); //�Ӳ�
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		//�Ӳ�
    	Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
//            �����뱾�����ݲ���,120;�����м�¼���Ķ�û��,140;���Ķ��м�¼����û��,140;
//            ��ҽ˳���,130;�ʸ�ȷ������,120;�ں�,80;����,90;���ط������,160,double,#########0.00;
//            ���Ķ˷������,160,double,#########0.00;�����걨���,160,double,#########0.00;
//            ���Ķ��걨���,160,double,#########0.00;����ȫ�Էѽ��,160,double,#########0.00;
//            ���Ķ�ȫ�Էѽ��,160,double,#########0.00;�����������,160,double,#########0.00;
//            ���Ķ��������,160,double,#########0.00
//            STATUS_ONE;STATUS_TWO;STATUS_THREE;ADM_SEQ;CONFIRN_NO;YEAR_MON;NAME;TOT_AMT_LOCAL;TOT_AMT_CENTER;NHI_AMT_LOCAL;NHI_AMT_CENTER;OWN_AMT_LOCAL;OWN_AMT_CENTER;ADD_AMT_LOCAL;ADD_AMT_CENTER
            this.callFunction("UI|TABLE|setParmValue", acceptData);
        }
  }
	public void  insDayRevoke(){
		TTable table1 = (TTable) this.getComponent("TABLE");// TABLE
		if (table1.getParmValue() == null) {
			messageBox("δѡ���賷���ļ�¼");
			return;
		}
		// ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		TParm tableParm1 = table1.getParmValue();
		int ctz=Integer.valueOf(acceptData.getValue("CTZ_CODE"));

			for(int i=0;i<tableParm1.getCount();i++){
				this.dataDown_ssks_C(tableParm1.getData("ADM_SEQ", i).toString(),
						tableParm1.getData("CONFIRM_NO", i).toString(),
						tableParm1.getData("BILL_DATE", i).toString(), 
						ctz);
			}

		
	}
private void dataDown_ssks_C(String admSeq,String confirnNo,
		String billDate,int ctz){
	TParm parm = new TParm();
	TParm regionParm = SYSRegionTool.getInstance().selectdata(
			Operator.getRegion());
	String hospital = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
	// ����
	if(ctz==1){
	    parm.setData("PIPELINE", "DataDown_ssks");
	    parm.setData("PLOT_TYPE", "C");	
	}else if(ctz==2){
		parm.setData("PIPELINE", "DataDown_csks");
		parm.setData("PLOT_TYPE", "C");	
	}
	parm.addData("CONFIRN_NO", confirnNo);// ��ʼʱ��
	parm.addData("BILL_DATE", billDate.substring(0, 4)+"-"+
                              billDate.substring(4, 6)+"-"+
                              billDate.substring(6, 8));// ����ʱ��
	parm.addData("HOSP_NHI_NO", hospital);// ҽԺ����
	parm.addData("PARM_COUNT", 3);
	TParm result = InsManager.getInstance().safe(parm, "");// ҽ�����ӿڷ���(����)
	if (result.getErrCode() < 0) {
		messageBox(result.getErrText());
		return;
	}
	String sql=" UPDATE INS_IBS_UPLOAD SET "+
    " INS_FLG='0',UP_FLG='3', "+
    " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
    " WHERE ADM_SEQ='"+admSeq+"' " +
    " AND CHARGE_DATE between TO_DATE('"+billDate+"000000','YYYYMMDDHH24MISS') "+
    " AND TO_DATE('"+billDate+"235959','YYYYMMDDHH24MISS') "+
//    " AND A.INS_FLG='3' "+
    " AND UP_FLG='2' ";

// System.out.println("onInsItemRegDown_sql:"+sql);
 TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
// System.out.println("onInsItemRegDown_sql:"+result1);

 if (result1.getErrCode() < 0) {
	 this.messageBox("ҽ���ӿڵ��óɹ�,����״̬����ʧ��");
 }else{
	 this.messageBox("�����ɹ�");
 }
		
	
}

}
