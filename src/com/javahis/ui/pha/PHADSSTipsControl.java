package com.javahis.ui.pha;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: �ż���ҩ��֪ʶ����ʾ
 * </p>
 * 
 * <p>
 * Description: �ż���ҩ��֪ʶ����ʾ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2015.8.24
 * @version 1.0
 */
public class PHADSSTipsControl extends TControl {

	private TTable table;
	private TParm parameterParm; // ҳ�洫�����
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			} else {
				this.messageBox("���δ���");
				return;
			}
		} else {
			this.messageBox("���δ���");
			return;
		}
		
		this.onInitPage();
    }
    
    /**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		table = getTable("TABLE");
		
		if (null != parameterParm) {
			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" SELECT * FROM DSS_CKBLOG ");
			sbSql.append(" WHERE CASE_NO = '");
			sbSql.append(parameterParm.getValue("CASE_NO"));
			sbSql.append("' AND ORDER_NO = '");
			sbSql.append(parameterParm.getValue("RX_NO"));
			sbSql.append("' AND ORDER_CODE = '");
			sbSql.append(parameterParm.getValue("ORDER_CODE"));
			sbSql.append("' AND ORDER_SEQ = ");
			sbSql.append(parameterParm.getValue("SEQ_NO"));
			sbSql.append(" ORDER BY RISK_LEVEL,LOG_DATE");
			
			TParm parm = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
			
			if (parm.getErrCode() < 0) {
				this.messageBox("��ѯ����");
				err("ERR:" + parm.getErrCode() + parm.getErrText());
				return;
			}
			
			// ���ݹ��ˣ���֪ͬʶ����ʾֻ��Ҫ��ʾһ��
			int count = parm.getCount();
			List<String> keyList = new ArrayList<String>();
			String key = "";
			
			for (int i = count - 1; i > -1; i--) {
				key = parm.getValue("ADVISE", i)
						+ parm.getValue("RISK_LEVEL", i)
						+ parm.getValue("BYPASS_REASON", i);
				if (!keyList.contains(key)) {
					keyList.add(key);
				} else {
					parm.removeRow(i);
				}
			}
			
			table.setParmValue(parm);
		}
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
