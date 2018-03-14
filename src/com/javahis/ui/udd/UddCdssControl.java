package com.javahis.ui.udd;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.inw.InwUtil;
import jdo.pha.PassTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
 
/**
 * <p>
 * Title: סԺҩ����ҩ���
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ���  
 * </p>
 * 
 * <p>
 * Copyright: javahis 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>  
 * 
 * @author ehui
 * @version 1.0
 */
public class UddCdssControl extends TControl {
	
	/**
	 * ������Ҫ�ļ���
	 */
	TParm saveParm = new TParm();

	
	TTable tblDtl;
  
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
	
		tblDtl = (TTable) this.getComponent("tTable_0");
		  //�õ�ǰ̨���������ݲ���ʾ�ڽ����� 
		TParm parm = (TParm)this.getParameter();   
		onQueryDtl(parm);
	}

	
	
	/**
	 * ��ѯҩƷ��ϸ
	 */
	public void onQueryDtl(TParm parm) {
		//Color color = new Color(0, 0, 255);  
		String order_no = " AND A.ORDER_NO = '"
				+ parm.getValue("ORDER_NO") + "'";
		
		String order_seq = " AND A.ORDER_SEQ = '"
			+ parm.getValue("ORDER_SEQ") + "'";
		// String bed_no = " AND A.BED_NO = '" +
		// parm.getValue("BED_NO", tblPat.getSelectedRow()) + "'";
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		String sql = " SELECT A.CASE_NO , A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC||' '||A.GOODS_DESC|| '('|| A.SPECIFICATION ||')' ORDER_DESC, "  //lirui  2012-6-6   ҩ���ӹ��
				+ " B.ADVISE,B.RISK_LEVEL,B.BYPASS_REASON,B.LOG_DATE "
				+ " FROM ODI_ORDER A" 
			    + ",DSS_CKBLOG B"           
				+ " WHERE A.CASE_NO = '"    
				+ parm.getValue("CASE_NO") + "'"
				+ "	AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "  
				+ " AND A.CASE_NO = B.CASE_NO   "   
				+ " AND A.ORDER_NO = B.ORDER_NO "    
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ  "
			    + order_no + region + order_seq + // bed_no +        
				" ORDER BY B.RISK_LEVEL,LOG_DATE";
		//System.out.println("�����ϸ��ѯ----------------------"+sql);
		saveParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		// ���ݹ��ˣ���֪ͬʶ����ʾֻ��Ҫ��ʾһ��
		int count = saveParm.getCount();
		List<String> keyList = new ArrayList<String>();  
		String key = "";
		//���������ж�
		for (int i = count - 1; i > -1; i--) {  
			key = parm.getValue("ADVISE", i)
					+ parm.getValue("RISK_LEVEL", i)
					+ parm.getValue("BYPASS_REASON", i);
			if (!keyList.contains(key)) {
				keyList.add(key);
			} else { 
				saveParm.removeRow(i);
			}
		}
		
		tblDtl.setParmValue(saveParm);
//		for (int j = 0; j < saveParm.getCount("CASE_NO"); j++) {
//			tblDtl.setRowTextColor(j, color);
//		}
		
	}



	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
