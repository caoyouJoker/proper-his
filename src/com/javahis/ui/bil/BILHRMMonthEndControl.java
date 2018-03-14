package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;

import jdo.sys.SystemTool;

import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import jdo.bil.BILComparator;
import jdo.bil.BILSysParmTool;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Vector;

/**
 * <p>Title: ��Ժ����ҽ�Ʒ����ܱ�</p>
 *
 * <p>Description: ��Ժ����ҽ�Ʒ����ܱ�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhanglei 2009.11.20
 * @version 1.0
 */
public class BILHRMMonthEndControl extends TControl {
	// ==========modify-begin (by wanglong 20120710)===============
	private TTable table;//��ȡ������
	
	// ==========modify-end========================================
	//���ڸ�ʽ�� 20150120 wangjingchun add
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	
    public void onInit() {
        super.onInit();
        initPage();
    }

  
 
    //��������
    Timestamp startDate ;
    /**
     * ��ʼ������
     */
    public void initPage() {
        startDate = getDateForInit(queryFirstDayOfLastMonth(StringTool.
                getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
        setValue("S_DATE", startDate);  
        Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.
                getInstance().getDate()), -1);
        setValue("E_DATE", formateDate.format(rollDay)+" 23:59:59");
		 table = (TTable) this.getComponent("Table");
        }
      

  

    /**
     * ��ӡ
     */
    public void onPrint() {
        this.messageBox("��" + this.getValue("S_DATE"));
        this.messageBox("ֹ" + this.getValue("E_DATE"));
    }


    /**
     * ��ѯ
     */
    public void onQuery() {
    	double sm = 0.0;
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	DecimalFormat dfdf = new DecimalFormat("##########0.0000");
        String sql =
                "SELECT SUM(AR_AMT) AR_AMT,DEPT_CODE,ORDER_CODE,ORDER_DESC,OWN_PRICE FROM HRM_ORDER WHERE CAT1_TYPE = 'PHA' " + 
                		"AND ORDER_DATE BETWEEN TO_DATE ('" + this.getValueString("S_DATE").substring(0, 10)
   					.replaceAll("-", "") + "000000" + "', 'YYYYMMDDHH24MISS') " + 
                		"AND TO_DATE ('" + this.getValueString("E_DATE").substring(0, 10)
    					.replaceAll("-", "") + "235959" + "', 'YYYYMMDDHH24MISS') " + 
                		"GROUP BY DEPT_CODE,OWN_PRICE,ORDER_CODE,ORDER_DESC";
         System.out.println("sql1:"+sql);
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        for (int i = 0; i < selParm.getCount("DEPT_CODE"); i++) {
        	selParm.setData("AR_AMT", i, df.format(selParm.getDouble("AR_AMT",i)));
        	sm += selParm.getDouble("AR_AMT",i);
        	selParm.setData("OWN_PRICE", i, dfdf.format(selParm.getDouble("OWN_PRICE",i)));
        }
        String b = df.format(sm);
        String a = "�ϼ�:" + b;
        //this.messageBox(a);
        selParm.addData("DEPT_CODE", "");
        selParm.addData("AR_AMT", a);
        
        this.callFunction("UI|Table|setParmValue", selParm);
        
        // System.out.println("selParm" + selParm);
       // this.getTTable("Table").setParmValue(selParm);
    }
    
	/**
	 * ��ȡTTable
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	

    /**
     * ���Excel
     */
    public void onExport() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "��Ժ����ҽ�Ʒ����ܱ�");
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("STATION_CODE;DEPT_CODE");
    }





    /**
     * �õ��ϸ���
     * @param dateStr String
     * @return Timestamp
     */
    public Timestamp queryFirstDayOfLastMonth(String dateStr) {
        DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = defaultFormatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }

    /**
     * ��ʼ��ʱ������
     * @param date Timestamp
     * @return Timestamp
     */
    public Timestamp getDateForInit(Timestamp date) {
        String dateStr = StringTool.getString(date, "yyyyMMdd");
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
        String monThCycle = "" + monthM;
        dateStr = dateStr.substring(0, 6) + monThCycle;
        Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
        return result;
    }
    
	
}
