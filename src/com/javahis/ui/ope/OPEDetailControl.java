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
 * <p>Title: 手术人员安排</p>
 *
 * <p>Description: 手术人员安排</p>
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
//       	// 0,申请;1,排程完毕;2,接患者;3,手术室交接;4,手术等待;5,手术开始;6,关胸;7,手术结束;8,返回病房
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
    		this.messageBox("请选择要返回的数据");
    		return;
    	}
    	TParm p = table.getParmValue();
    	
		this.setReturnValue(p.getRow(num));
		this.closeWindow();
    }
	
}
