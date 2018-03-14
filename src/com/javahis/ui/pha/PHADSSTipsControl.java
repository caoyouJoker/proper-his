package com.javahis.ui.pha;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: 门急诊药房知识库提示
 * </p>
 * 
 * <p>
 * Description: 门急诊药房知识库提示
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
	private TParm parameterParm; // 页面传输参数
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			} else {
				this.messageBox("传参错误");
				return;
			}
		} else {
			this.messageBox("传参错误");
			return;
		}
		
		this.onInitPage();
    }
    
    /**
	 * 初始化页面
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
				this.messageBox("查询错误");
				err("ERR:" + parm.getErrCode() + parm.getErrText());
				return;
			}
			
			// 数据过滤，相同知识库提示只需要显示一次
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
