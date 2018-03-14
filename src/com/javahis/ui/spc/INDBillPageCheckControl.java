package com.javahis.ui.spc;
   
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jdo.spc.INDBillPageTool;
import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 *
 * <strong>Title : INDBillPageControl<br></strong>
 * <strong>Description : </strong>ҩ����ҳ<br>  
 * <strong>Create on : 2012-1-25<br></strong>
 * <p>
 * <strong>Copyright (C) <br></strong>
 * <p>  
 * @author luhai<br>    
 * @version <strong>BlueCore</strong><br>
 * <br>
 * <strong>�޸���ʷ:</strong><br>
 * �޸���		�޸�����		�޸�����<br>
 * -------------------------------------------<br>
 * <br>
 * <br>
 */
public class INDBillPageCheckControl extends TControl {    
	//ҳ��ؼ�
	private TTable billTable;  
	//���ڸ�ʽ��
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	//��ҳ����
    /**   
     * ��ʼ������     
     */
    public void onInit() {   
//    	System.out.println("ϵͳ��ʼ��");  
    	initPage();
//    	System.out.println("ϵͳ��ʼ�����");
    }
    /**
     *
     * ��ʼ���ؼ�Ĭ��ֵ                 
     */
    public void initPage(){
    	//���ÿ���������ֵend  
//        Date d = new Date();  
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//        String dateNowStrStrat = sdf.format(d);   
//        dateNowStrStrat = dateNowStrStrat.substring(0, 7)+"-25 00:00:00";
//        
//        String dateNowStrEnd = sdf.format(d);   
//        dateNowStrEnd = dateNowStrEnd.substring(0, 7)+"-25 23:59:59";  
        // ��ʼ������ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("START_DATE", date.toString().substring(0, 7).replace('-', '/')+"/25");
        setValue("END_DATE", date.toString().substring(0, 7).replace('-', '/')+"/25");
		// ���´���ʼʱ��ͽ���ʱ�� begin luhai 2011-12-07  
		//��ʼ��Ĭ��ҩ��                                  
		//��ʼ��ҩƷ�б������ begin  
		//��ʼ��ҩƷ�б������ end
		//��ʼ��ҽ����textField        
        billTable = getTable("BIL_TABLE");
    }
    

      

    public void onQuery(){    
        String startDateQuery = this.getValueString("START_DATE").substring(0, 10).replace('-', '/')+" 00:00:00";  
        String endDateQuery =this.getValueString("END_DATE").substring(0, 10).replace('-', '/')+" 23:59:59";   
    	//ҩƷ        
        if(getRadioButton("PHA").isSelected()){      
        	String sqlPha =  " SELECT  '���' AS BILL_TYPE ,M.CHECK_DATE AS BILL_DATE, M.VERIFYIN_NO AS BIIL_NO  FROM SPC_VERIFYINM M  WHERE" +
	         "  M.CHECK_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
            " UNION ALL "+
            " SELECT '����' AS BILL_TYPE, M.DISPENSE_DATE AS BILL_DATE, M.DISPENSE_NO AS BIIL_NO   FROM SPC_DISPENSEM M  "+
            " WHERE M.DISPENSE_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
            " UNION ALL "+
            " SELECT '�˻�' AS BILL_TYPE, M.CHECK_DATE AS BILL_DATE, M.REGRESSGOODS_NO AS BIIL_NO   FROM SPC_REGRESSGOODSM M " +
            " WHERE M.CHECK_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
            "";
            TParm parmPha = new TParm(TJDODBTool.getInstance().select(sqlPha));  
            billTable.setParmValue(parmPha); 
        }      
        if(getRadioButton("COS").isSelected()){  
    	String sqlCos =  " SELECT DISTINCT '���' AS BILL_TYPE ,M.CHECK_DATE AS BILL_DATE, M.VERIFYIN_NO AS BIIL_NO" +
    			"  FROM IND_VERIFYINM M,IND_VERIFYIND D,PHA_BASE B  " +
    			" WHERE M.VERIFYIN_NO = D.VERIFYIN_NO AND D.ORDER_CODE = B.ORDER_CODE AND B.TYPE_CODE ='4'" +
                " AND M.CHECK_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
                " UNION ALL "+
                " SELECT DISTINCT '����' AS BILL_TYPE, M.DISPENSE_DATE AS BILL_DATE, M.DISPENSE_NO AS BIIL_NO" +
                " FROM IND_DISPENSEM M,IND_DISPENSED D,PHA_BASE B   "+
                " WHERE M.DISPENSE_NO = D.DISPENSE_NO AND D.ORDER_CODE = B.ORDER_CODE AND B.TYPE_CODE ='4' " +
                " AND M.DISPENSE_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
                " UNION ALL "+
                " SELECT DISTINCT '�˻�' AS BILL_TYPE, M.CHECK_DATE AS BILL_DATE, M.REGRESSGOODS_NO AS BIIL_NO  " +
                " FROM IND_REGRESSGOODSM M, IND_REGRESSGOODSD D,PHA_BASE B  " +
                " WHERE  M.REGRESSGOODS_NO = D.REGRESSGOODS_NO AND D.ORDER_CODE = B.ORDER_CODE AND B.TYPE_CODE ='4'" +
                " AND M.CHECK_DATE BETWEEN TO_DATE('"+startDateQuery+"','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"+endDateQuery+"','YYYY/MM/DD HH24:MI:SS')  "+
                " ";
                TParm parmCos = new TParm(TJDODBTool.getInstance().select(sqlCos));
                billTable.setParmValue(parmCos);
        }
    	
    }
  
		
		
	    /**
	     * ��շ���    
	     */
	    public void onClear() {
	    	billTable.removeRowAll();
	    }
	    
	    /**
	     * �õ�RadioButton����
	     *
	     * @param tagName
	     *            Ԫ��TAG����
	     * @return
	     */
	    private TRadioButton getRadioButton(String tagName) {
	        return (TRadioButton) getComponent(tagName);
	    }
	    
	    /**
	     * �õ�Table����
	     *
	     * @param tagName
	     *            Ԫ��TAG����
	     * @return
	     */
	    private TTable getTable(String tagName) {
	        return (TTable) getComponent(tagName);
	    }

    }
