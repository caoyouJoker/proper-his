package com.javahis.ui.adm;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:������Ժ���������ٻز�ѯ
 * </p>
 *
 * <p>
 * Description:������Ժ���������ٻز�ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author chenx
 * @version 4.0
 */
public class ANMQueryWaitPatControl extends TControl{
	private TTable table ;
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit() ;
		table=(TTable)this.getComponent("TABLE") ;
		this.onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String mrNo ="" ;
		String whereSql = "" ;
		String sql = "SELECT A.*,B.PAT_NAME FROM ADM_WAIT_TRANS A ,SYS_PATINFO B " +
				    " WHERE A.MR_NO = B.MR_NO " ;
	   if(this.getValueString("MR_NO").length()>0){
		   mrNo =  PatTool.getInstance().checkMrno(this.getValueString("MR_NO")) ;
		  whereSql = "  AND A.MR_NO ='"+mrNo+"'"  ;
	   }
	   sql +=whereSql ;
//	   System.out.println("SQL========"+sql);
	   TParm parm = new  TParm(TJDODBTool.getInstance().select(sql)) ;
	   if(parm.getCount()<0){
		   this.messageBox("��������") ;
		   this.onClear() ;
		   return ;
	   }
	   table.setParmValue(parm) ;
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("MR_NO;PAT_NAME") ;
		this.onInit() ;
	}
	/**
	 * �����Żس��¼�
	 */
	public void onMrnoAction(){
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO")) ;
		this.setValue("MR_NO", mrNo) ;
//		String patName = PatTool.getInstance().getNameForMrno(mrNo);
//		this.setValue("PAT_NAME", patName) ;
		
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			setValue("MR_NO", pat.getMrNo());

		}
		this.setValue("PAT_NAME", pat.getName()) ;
		// modify by huangtt 20160928 EMPI���߲�����ʾ end
		
		this.onQuery();
		
	}
}