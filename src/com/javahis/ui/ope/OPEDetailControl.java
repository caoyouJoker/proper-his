package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboOPERoom;
import com.javahis.util.StringUtil;

import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.ope.OPEOpBookTool;
import jdo.reg.SysParmTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: ������Ա����</p>
 *
 * <p>Description: ������Ա����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 4.0
 */
public class OPEDetailControl
    extends TControl {
	
	TParm inParm = null ;
	String mr_no = "";
	String case_no = "";
	String opbookSeq = "";
	TTable table ;
    public void onInit(){
        super.onInit();
       
        Object obj = this.getParameter();
        if(obj instanceof TParm){
            inParm = (TParm)obj;
        }else{
            return;
        }
       mr_no = inParm.getValue("MR_NO");
       case_no = inParm.getValue("CASE_NO");
       opbookSeq = inParm.getValue("OPBOOK_SEQ");
       
       //messageBox_(mr_no+"    "+opbookSeq); 
       String sql = "SELECT * "+
    		   		"FROM OPE_OPBOOK A, SYS_PATINFO B, SYS_OPERATIONICD C "+
    		   		"WHERE A.MR_NO = B.MR_NO "+
//    		        "AND A.MR_NO = '"+mr_no+"' "+
					"AND A.CASE_NO = '"+case_no+"' "+
       				"AND A.OP_CODE1 = C.OPERATION_ICD "+
    		        "AND A.STATE <> '0'";
//       String opeStatus = inParm.getValue("opeStatus");
//       if (StringUtils.isNotEmpty(opeStatus)) {
//       	// 0,����;1,�ų����;2,�ӻ���;3,�����ҽ���;4,�����ȴ�;5,������ʼ;6,����;7,��������;8,���ز���
//			sql = sql + " AND A.STATE IN (" + opeStatus + ") ";
//       }
       
       sql = sql + " ORDER BY A.OPBOOK_SEQ DESC ";
//       System.out.println("yyyy:"+sql);
       table = (TTable)this.getComponent("TABLE");
       TParm p = new TParm(TJDODBTool.getInstance().select(sql));
       table.setParmValue(p);	         
    }
    
    public void onOk(){
    	int num = table.getSelectedRow();
    	if(num<0){
    		this.messageBox("��ѡ��Ҫ���ص�����");
    		return;
    	}
    	TParm p = table.getParmValue();
    	
		this.setReturnValue(p.getRow(num));
		this.closeWindow();
    }
	
}
