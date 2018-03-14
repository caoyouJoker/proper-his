package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.util.Calendar;
import jdo.sys.Operator;
import jdo.reg.REGAdmForDRTool;
import com.dongyang.util.*;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

/**
 * <p>Title: ҽ��ԤԼ�Һ���ϸ��ѯ</p>
 *
 * <p>Description:ҽ��ԤԼ�Һ���ϸ��ѯ </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 * 
 * <p>Company:javahis </p>  
 *
 * @author fux  
 * @version 1.0
 */
//%ROOT%\config\reg\REGAdmForDetailMenu.x  
//com.javahis.ui.reg.REGAdmForDetailControl
public class REGAdmForDetailControl
    extends TControl {
    /**  
     * ���table
     * @param tag String  
     * @return TTable
     */
    public TTable getTTable(String tag) { 
        return (TTable)this.getComponent(tag);
    }
    private static String MrNo; //������  
    
    private static String AdmType; //��������
    private static String AdmDate; //��������
    private static String sessionCode; //����ʱ��
    private static String ClinicRoomNO; //���
    private static String DrCode; //ҽ��
    /**
     * ��ʼ��
     */ 
    public void onInit() {   
        //��������   
        Object obj = getParameter();
        //���ܵ�ADM_TYPE ADM_DATE SESSION_CODE CLINICROOM_NO
        TParm t;
        MrNo = "";  
        if (obj != null) {   
            t = (TParm) obj; 
            AdmDate = t.getValue("ADM_DATE"); 
            sessionCode = t.getValue("SESSION_CODE");
            ClinicRoomNO = t.getValue("CLINICROOM_NO");
        }  
        this.setValue("ADM_DATE", AdmDate);   
        this.setValue("SESSION", sessionCode);   
        this.setValue("CLINICROOM_NO", ClinicRoomNO);
        this.setValue("DR_CODE", Operator.getID());
        // �õ���ǰʱ��   
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����       
        this.setValue("ADM_DATE", 
                      StringTool.rollDate(date,0).toString().substring(0, 10).
                      replace('-', '/'));   
        this.setValue("ADM_DATE_END", 
                StringTool.rollDate(date,0).toString().substring(0, 10).
                replace('-', '/')); 
        this.setValue("SESSION_CODE", sessionCode);  
        String w = this.getValue("ADM_DATE").toString().
            substring(0, 10).replace("-", "");
        TParm tparm = new TParm();    
        tparm =  onQuery(); 
        //this.getTTable("TABLE").setParmValue(tparm); 
        //this.setValue("cbx_DRName", Operator.getID());
    }




    /**
     * ��ѯԤԼ�Һ���ϸ
     * @param MR_NO String
     * @return TParm
     */ 
    public TParm onQuery() {  
    	TParm parm = new TParm();    
    	String con = ""; 
        AdmDate = this.getValueString("ADM_DATE").substring(0, 10).replace("-", "");
        String admDateEnd = this.getValueString("ADM_DATE_END").substring(0, 10).replace("-", "");
        sessionCode = this.getValueString("SESSION");
        ClinicRoomNO = this.getValueString("CLINICROOM_NO");
        DrCode = this.getValueString("DR_CODE");
    	//MrNo  
//    	if(MrNo!=null){        
//    	   con = con +" AND A.MR_NO = '"+MrNo+"' ";
//    	}   
    	//AdmDate
    	if(AdmDate!=null&&AdmDate!=""&&admDateEnd!=null&&admDateEnd!=""){
     	   con = con +" AND A.ADM_DATE BETWEEN  TO_DATE('"+AdmDate+"000000','YYYYMMDDHH24MISS') AND  TO_DATE('"+admDateEnd+"235959','YYYYMMDDHH24MISS')";
     	}          
    	//sessionCode                 
    	if(sessionCode!=null&&sessionCode!=""){    
      	   con = con +" AND A.SESSION_CODE = '"+sessionCode+"' ";
      	}  
    	//ClinicRoomNO
    	if(ClinicRoomNO!=null&&ClinicRoomNO!=""){  
       	   con = con +" AND A.CLINICROOM_NO = '"+ClinicRoomNO+"' ";
       	}
    	if(DrCode!=null&&DrCode!=""){    
        	   con = con +" AND A.DR_CODE = '"+DrCode+"' ";   
        	}
        String sql =       
            "SELECT A.CASE_NO,A.MR_NO,A.SESSION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO, " +
            "A.DEPT_CODE,A.DR_CODE,A.REALDEPT_CODE,A.REALDR_CODE,C.CTZ_DESC,A.QUE_NO, " +
            "A.ADM_DATE,A.REG_DATE, " + 
            "B.PAT_NAME,B.TEL_HOME,B.SEX_CODE,B.BIRTH_DATE " + 
            "FROM REG_PATADM A, SYS_PATINFO B ,SYS_CTZ C " +
            "WHERE A.MR_NO = B.MR_NO " +      
            "AND A.CTZ1_CODE = C.CTZ_CODE " +          
            "AND A.REGION_CODE = '" + Operator.getRegion() + "' " +
            "AND A.ADM_TYPE = 'O'  " + 
            "AND A.APPT_CODE = 'Y' " +    
             con +            
            "ORDER BY  SESSION_CODE,CLINICROOM_NO ";   
        //System.out.println("sql"+sql);      
        parm = new TParm(TJDODBTool.getInstance().select(sql));
        this.getTTable("TABLE").setParmValue(parm);    
        return parm;
    }
  
    /**
     * ��ѯԤԼ�Һ���ϸ     
     * @param MR_NO String
     * @return TParm
     */ 
    public void onClear() { 
        // �õ���ǰʱ��  
        Timestamp date = SystemTool.getInstance().getDate(); 
        this.setValue("ADM_DATE",StringTool.rollDate(date,0).toString().substring(0, 10).
                replace('-', '/'));    
        this.setValue("ADM_DATE_END",StringTool.rollDate(date,0).toString().substring(0, 10).
                replace('-', '/'));  
        this.setValue("SESSION", "");          
        this.setValue("CLINICROOM_NO", ""); 
        this.setValue("DR_CODE",""); 
        this.getTTable("TABLE").removeRowAll();   
    }
    
    /**
     * TABLE������¼�
     * @param date Timestamp 
     * @return int 
     */ 
    public void onTableClick() { 
        TTable table = getTTable("TABLE");  
        int row = getTTable("TABLE").getSelectedRow();
        this.setValue("ADM_DATE",table.getItemTimestamp(row, "ADM_DATE")); 
        this.setValue("SESSION_CODE",table.getItemData(row, "SESSION_CODE"));
        this.setValue("CLINICROOM_NO",table.getItemData(row, "CLINICROOM_NO"));
    }

 
    /**
     * ���ݵ�ǰ���ڣ���ý��������ڼ�
     * @param date Timestamp
     * @return int 
     */
    public int getWeek(Timestamp date) { 
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * ���ݵ�ǰ���ڣ���õ�ǰʱ��
     * @param date Timestamp
     * @return int
     */
    public String getSession_code(Timestamp date) { 
        String time = date.toString().substring(11, 19);
        String sql = "SELECT SESSION_CODE, START_REG_TIME, END_REG_TIME "
            + " FROM REG_SESSION WHERE REGION_CODE = '" +
            Operator.getRegion() + "' AND ADM_TYPE = 'O'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

        for (int i = 0; i < parm.getCount("SESSION_CODE"); i++) {
            if (time.compareTo(parm.getValue("START_REG_TIME", i)) >= 0 &&
                time.compareTo(parm.getValue("END_REG_TIME", i)) <= 0) {
                return parm.getValue("SESSION_CODE", i);
            }
        }
        return "";
    }
    
    /**
     * ���TComboBox
     * @param tagName String
     * @return TComboBox  
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
}
