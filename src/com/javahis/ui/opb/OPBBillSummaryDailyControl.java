package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.bil.BILSysParmTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: ����ÿ��Ʊ�ݺ�������</p>
 *
 * <p>Description: ����ÿ��Ʊ�ݺ�������</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yyn 201710
 * @version 1.0
 */


public class OPBBillSummaryDailyControl extends TControl{
	TTable table;
	String accountTime;//�ս�ʱ���

	 /**
     * ��ʼ��
     */
    public void onInit(){
    	super.onInit();
    	table = (TTable)this.getComponent("TABLE");
    	getAccountTime();//�õ��ս�ʱ���
    	initPage();//��ʼ��ҳ����Ϣ
    }
    
    /**
     * �õ��ս�ʱ���
     * @return String
     */
    public String getAccountTime() {
        accountTime = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMddHHmmss");
        return accountTime;
    }

    public String getAccountTimeS() {
        String accTime = "";
        String data = StringTool.getString(StringTool.rollDate(SystemTool.
            getInstance().getDate(), -1),
                                           "yyyyMMddHHmmSS").substring(0, 8);
        //��������ʱ��
        TParm parm = BILSysParmTool.getInstance().getDayCycle("O");
        if (parm.getErrCode() < 0) {
            out(parm.getErrText());
        }
        if (parm.getValue("DAY_CYCLE") == null ||
            parm.getValue("DAY_CYCLE").length() == 0)
            accTime = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMddHHmmSS");
        else
            accTime = data + parm.getValue("DAY_CYCLE", 0);       
        return accTime;
    }
        
    /**
     * ��ʼ��ҳ����Ϣ
     */
    public void initPage() {
        String accountDate =
            getAccountTime().substring(0, 4) + "/" +
            getAccountTime().substring(4, 6) + "/" +
            getAccountTime().substring(6, 8) ;
        String accountDateS =
            getAccountTimeS().substring(0, 4) + "/" +
            getAccountTimeS().substring(4, 6) + "/" +
            getAccountTimeS().substring(6, 8) ;
        setValue("ACCOUNT_DATE", accountDateS);//�����ѯ����
        setValue("ACCOUNT_DATEE", accountDate);//��ѯ����
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
    	TParm parm = new TParm();
    	TParm returnparm = new TParm();
    	String invalid = "";//����Ʊ�ݺ�
    	        
        if (getValue("ACCOUNT_DATE") == null ||
            getValueString("ACCOUNT_DATE").equals("")) {
            messageBox("��ѡ��ʼʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATE", StringTool.getString((Timestamp) getValue("ACCOUNT_DATE"),"yyyyMMdd")+this.getValueString("S_TIME").replace(":",""));//=======pangben modify 20110414
        
        if (getValue("ACCOUNT_DATEE") == null ||
            getValueString("ACCOUNT_DATEE").equals("")) {
            messageBox("��ѡ�����ʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATEE",StringTool.getString((Timestamp) getValue("ACCOUNT_DATEE"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":",""));//=======pangben modify 20110414
 
        String sql1 = "SELECT A.INV_NO,B.USER_NAME as PRINT_USER,C.AR_AMT,C.ACCOUNT_SEQ"//sql1������ѯ�ս�����
        			+ " FROM BIL_INVRCP A,SYS_OPERATOR B,( ";
             
        String sql = "SELECT A.AR_AMT,B.USER_NAME,A.ACCOUNT_SEQ"//sql������ѯ�ս���Ա
        		+ "			FROM JAVAHIS.BIL_ACCOUNT A,JAVAHIS.SYS_OPERATOR B,JAVAHIS.SYS_OPERATOR_DEPT C"
        		+ "		WHERE A.ACCOUNT_TYPE='OPB' AND A.ACCOUNT_USER=C.USER_ID(+)  AND C.MAIN_FLG='Y' " 
        		+ "	AND A.ACCOUNT_USER=B.USER_ID AND A.REGION_CODE='H01' AND";
        String admType = getValueString("ADM_TYPE");                
		if (admType != null && !admType.equals("")){
			sql += " A.ADM_TYPE='" + admType + "' AND ";
		}else{
			sql += " A.ADM_TYPE IN ('O','E') AND ";
		}
        	sql += " A.ACCOUNT_DATE BETWEEN TO_DATE('"
    			+ parm.getValue("ACCOUNT_DATE") + "','YYYYMMDDHH24MISS')"
    			+ " AND TO_DATE('" + parm.getValue("ACCOUNT_DATEE")
    			+ "','YYYYMMDDHH24MISS')";
    		sql += " ORDER BY A.ACCOUNT_SEQ";
    		
    	sql1 = sql1 + sql +") C "//sql1������ѯ�ս�����
    			+ " WHERE A.ACCOUNT_SEQ = C.ACCOUNT_SEQ"
    			+ " AND A.PRINT_USER = B.USER_ID AND A.RECP_TYPE = 'OPB' AND length(A.INV_NO)<12 "
    			+ " ORDER BY A.ACCOUNT_SEQ,A.INV_NO";
    			
        TParm result = new TParm(TJDODBTool.getInstance().select(sql1));//�ս�����
        //System.out.println("sql1 = "+sql1);
        
        if (result.getErrCode() < 0) {
        	System.out.println("err");
			return;
		}
        
        TParm nameparm = new TParm(TJDODBTool.getInstance().select(sql));//�ս���Ա
        if (nameparm.getErrCode() < 0) {
        	System.out.println("err1");
			return;
		}
        //����Ʊ��
        String sql2 =  " SELECT A.INV_NO,A.AR_AMT,B.ACCOUNT_SEQ FROM BIL_INVRCP A ,(" +sql+") B"+
        "  WHERE A.RECP_TYPE = 'OPB' "+
        "    AND  A.ACCOUNT_SEQ = B.ACCOUNT_SEQ"+
        "    AND A.CANCEL_FLG = '3' " +
        "    AND A.CANCEL_DATE < A.ACCOUNT_DATE";
        sql2 += " AND LENGTH (A.INV_NO) < 12";
        TParm data1 = new TParm(TJDODBTool.getInstance().select(sql2));
        
        
        //����Ʊ��
        String sql3 =  " SELECT A.INV_NO,A.AR_AMT,A.ACCOUNT_SEQ FROM BIL_INVRCP A ,(" +sql+") B"+
        "  WHERE A.RECP_TYPE = 'OPB' "+
        "    AND  A.ACCOUNT_SEQ = B.ACCOUNT_SEQ"+
        "    AND A.STATUS = '2' " ;
        sql3 += " AND LENGTH (A.INV_NO) < 12";
        TParm data2 = new TParm(TJDODBTool.getInstance().select(sql3));
        
        
        //�˷�Ʊ�� 
        String sql4 = " SELECT B.INV_NO, B.AR_AMT,C.ACCOUNT_SEQ" +
        " FROM BIL_OPB_RECP A, BIL_INVRCP B, (" +sql+") C"+
        " WHERE A.PRINT_NO = B.INV_NO" +
        " AND B.RECP_TYPE = 'OPB'" +
        " AND A.ACCOUNT_SEQ = C.ACCOUNT_SEQ" +
        " AND B.CANCEL_FLG = '1'" +
        " AND RESET_RECEIPT_NO IS NULL" ;
        sql4 += " AND LENGTH (B.INV_NO) < 12";
        TParm data3 = new TParm(TJDODBTool.getInstance().select(sql4));
        
        
        //�ս����ݲ�ѯ��Ʊ��������Ʊ����ֹ��
        List<String> opblist = new ArrayList<String>();
        int opbcount = 0;//Ʊ������
        String inv_nos = "";//Ʊ����ֹ��
        int j = 0;
        int i = 0;
        
        for(i = 0; i < nameparm.getCount();i++){
        	for(j = 1;j < result.getCount();j++){
        		if(result.getValue("ACCOUNT_SEQ", j-1).equals(nameparm.getValue("ACCOUNT_SEQ", i))){
        			opblist.add(result.getValue("INV_NO", j-1));
        			opbcount++;
        			if (result.getValue("PRINT_USER", j).equals(result.getValue("PRINT_USER", j-1))) {
        				if (!compareInvno(result.getValue("INV_NO", j), result.getValue("INV_NO", j-1))) {
        					inv_nos += opblist.get(0) + "~"+ opblist.get(opblist.size() - 1) + ",";
        					opblist = new ArrayList<String>();
        				}
        			}
        			else{
        				inv_nos += opblist.get(0) + "~"+ opblist.get(opblist.size() - 1) + ",";
        				opblist = new ArrayList<String>();
        			}
        		}
        	}
        	if(nameparm.getValue("ACCOUNT_SEQ", i).equals(result.getValue("ACCOUNT_SEQ", j-1))){//���һ������
            	opblist.add(result.getValue("INV_NO", j-1)); 
            	opbcount++;
        	}
        	
        	if (opblist.size() > 0) {
				inv_nos += opblist.get(0) + "~" + opblist.get(opblist.size() - 1)
						+ ",";
			}
        	inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
        	
        	for(j = 0;j < data1.getCount();j++){//����Ʊ��
        		if(data1.getValue("ACCOUNT_SEQ", j).equals(nameparm.getValue("ACCOUNT_SEQ", i))){
        			invalid += data1.getValue("INV_NO", j)+";";
        		}
        	}
        	
        	for(j = 0;j < data2.getCount();j++){//����Ʊ��
        		if(data2.getValue("ACCOUNT_SEQ", j).equals(nameparm.getValue("ACCOUNT_SEQ", i))){
        			invalid += data2.getValue("INV_NO", j)+";";
        		}
        	}
        	
        	for(j = 0;j < data3.getCount();j++){//�˷�Ʊ��
        		if(data3.getValue("ACCOUNT_SEQ", j).equals(nameparm.getValue("ACCOUNT_SEQ", i))){
        			invalid += data3.getValue("INV_NO", j)+";";
        		}
        	}
        	
        	returnparm.addData("ACCOUNT_SEQ", nameparm.getValue("ACCOUNT_SEQ", i));
        	returnparm.addData("USER_NAME", nameparm.getValue("USER_NAME", i));
        	returnparm.addData("AR_AMT", nameparm.getValue("AR_AMT", i));
        	returnparm.addData("COUNT", opbcount);
        	returnparm.addData("PRINTNOS", inv_nos);
        	returnparm.addData("INVALID", invalid);
        	opbcount = 0;
        	inv_nos = "";
        	invalid = "";
        	opblist = new ArrayList<String>();
        }    
        table.setParmValue(returnparm);        
    }

    
    /**
     * ���
     */
    public void onClear() {
        onInit();
        this.callFunction("UI|TABLE|removeRowAll");
    }

    
    /**
	 * �Ƚ�Ʊ��
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// ȥ������
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// ȥ����
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
	 * ���Excel
	 */
	public void onExport() {
		String title = "�շ�Աÿ��Ʊ�ݺ������ܱ�";
		if (table.getRowCount() > 0)   		
		ExportExcelUtil.getInstance().exportExcel(table,title);
	}
    
}
                                                  