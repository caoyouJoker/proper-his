package com.javahis.ui.ins;

import org.hibernate.sql.Template;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
/**
 * 
 * <p>
 * Title:转外就医登记历史记录查询
 * </p>
 * 
 * <p>
 * Description:转外就医登记历史记录查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-8-14
 * @version 4.0
 */
public class INSSearchTransHospControl extends TControl{

	private TParm regionParm;
	private TTable table;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		table=(TTable) this.getComponent("TABLE");
		this.setValue("CROWD_TYPE", "1");
	}
	public void onQuery(){
		String Sql = 
			 " SELECT A.*,CASE WHEN A.REG_TYPE='0' THEN '已审核' WHEN A.REG_TYPE='1' THEN '已撤销' END REG_TYPE_DESC FROM INS_TRANS_HOSP A "+
			 " WHERE A.MR_NO='"+this.getValue("MR_NO")+"' ";
		if(!this.getValue("REG_CODE").equals("")){
			Sql = Sql+"AND A.REG_CODE='"+this.getValue("REG_CODE")+"' ";
		}
//			 System.out.println("regSql==="+Sql);
			TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

			if (tabParm.getCount("REG_CODE") < 0) {
				this.messageBox("没有要查询的数据！");
//				onClear();
				return;
			}
		table.setParmValue(tabParm);
	}
	/**
	 * 查询病患名称/身份证号码
	 */
	public void onMrNo(){
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("无此病案号!");
            return;
        }
        this.setValue("MR_NO",pat.getMrNo());
        this.setValue("IDNO",pat.getIdNo());
        this.setValue("PAT_NAME",pat.getName());
	}
	public void onClear(){
		this.setValue("MR_NO", "");
		this.setValue("IDNO", "");
		this.setValue("PAT_NAME", "");
		this.setValue("REG_CODE", "");
		table.removeRowAll();
		this.setValue("CROWD_TYPE", "1");
	}
	/**
	 * 双击事件
	 */
	public void onTableDoubleClicked(){
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		TParm parmRow=parm.getRow(row);
		this.setReturnValue(parmRow);
		this.closeWindow();
	}
}
