package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: RACHS���յȼ�ѡ��
 *
 * <p>Description: RACHS���յȼ�ѡ��
 *
 * @author yyn
 * 
 * @version 1.0
 */

public class EMRRCatUIControl extends TControl{
	String accountSeq = "";
	int num = 0;//��ѡ�е�����
	private TParm rcatdata;
	TTable table;
    TParm parm = new TParm();//���ص�����
   
	
	/**
     * ��ʼ������
     */
    public void onInit(){
    	super.onInit();
    	//table���������¼�
    	num = 0;
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
    	table = (TTable)this.getComponent("Table");
    	//table����checkBox�¼�
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
      //��ʼ��ҳ��
        initPage();
    }
    
    /**
     * ��ʼ��ҳ��
     */
    public void initPage(){
        Object obj = this.getParameter();
        if(obj != null&&obj instanceof TParm){
        	rcatdata = (TParm)obj;
        }
    	String rcat_type = rcatdata.getValue("CHOOSE_NAME");
    	//System.out.println("rcat_type = "+rcat_type);
    	String sql="";
    	if(rcat_type.equals("9")){//�������������ѡ��
    		sql = "SELECT 'N' AS FLG,RACHS_CODE,RACHS_DESC,RACHS_ENG_DESC,"
    			+ " DESCRIPT,PY,SEQ,'-' AS RACHS_LEVEL FROM SYS_RACHS WHERE RACHS_LEVEL IN( '"+rcat_type+"','0')";
    	}
    	else{
    		sql = "SELECT 'N' AS FLG,RACHS_CODE,RACHS_DESC,RACHS_ENG_DESC,"
    			+ " DESCRIPT,PY,SEQ,RACHS_LEVEL FROM SYS_RACHS WHERE RACHS_LEVEL IN( '"+rcat_type+"','0')";
    	}
    	TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if (dataParm.getErrCode() < 0) {
			System.out.println("EMRRCatUIControl.print Err:"+ dataParm.getErrText());
			return;
		}
    	table.setParmValue(dataParm);
    }
    
    
    /**
     * ����
     */
    public void onSave(){
    	this.setReturnValue(parm);//��������
    	this.closeWindow();
    }
    
    /**
     * table����checkBox�¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
    	accountSeq = new String();
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int allRow = table.getRowCount();
        StringBuffer allSeq = new StringBuffer();
        for (int i = 0; i < allRow; i++) {
            String seq = "";
            if ("Y".equals(tableParm.getValue("FLG", i))) {//��ѡ���������ʽ
            	num++;//������ʽ������
                //System.out.println("num = "+num);
                seq = tableParm.getValue("RACHS_DESC", i);
                if (allSeq.length() > 0)
                    allSeq.append(",");
                allSeq.append(seq);
                if(tableParm.getValue("RACHS_DESC", i).equals("��")){//ȥ��ѡ�� �ޡ������
                	num--;
                }
            }
        }
        accountSeq = allSeq.toString();
        String row_num = Integer.toString(num);
        parm.setData("RACHS_DESC", accountSeq);//��ѡ�����������
        parm.setData("ROW_NUM",row_num);//��ѡ�е���������
    	return true;
    }
}
