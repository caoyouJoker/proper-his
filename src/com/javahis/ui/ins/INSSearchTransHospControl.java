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
 * Title:ת���ҽ�Ǽ���ʷ��¼��ѯ
 * </p>
 * 
 * <p>
 * Description:ת���ҽ�Ǽ���ʷ��¼��ѯ
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
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		table=(TTable) this.getComponent("TABLE");
		this.setValue("CROWD_TYPE", "1");
	}
	public void onQuery(){
		String Sql = 
			 " SELECT A.*,CASE WHEN A.REG_TYPE='0' THEN '�����' WHEN A.REG_TYPE='1' THEN '�ѳ���' END REG_TYPE_DESC FROM INS_TRANS_HOSP A "+
			 " WHERE A.MR_NO='"+this.getValue("MR_NO")+"' ";
		if(!this.getValue("REG_CODE").equals("")){
			Sql = Sql+"AND A.REG_CODE='"+this.getValue("REG_CODE")+"' ";
		}
//			 System.out.println("regSql==="+Sql);
			TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

			if (tabParm.getCount("REG_CODE") < 0) {
				this.messageBox("û��Ҫ��ѯ�����ݣ�");
//				onClear();
				return;
			}
		table.setParmValue(tabParm);
	}
	/**
	 * ��ѯ��������/���֤����
	 */
	public void onMrNo(){
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("�޴˲�����!");
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
	 * ˫���¼�
	 */
	public void onTableDoubleClicked(){
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		TParm parmRow=parm.getRow(row);
		this.setReturnValue(parmRow);
		this.closeWindow();
	}
}
