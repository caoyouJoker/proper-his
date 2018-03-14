package com.javahis.ui.ins;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 天津市基本医疗保险住院药品明细表</p>
 *
 * <p>Description:天津市基本医疗保险住院药品明细表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedInsurancePhaDetailControl extends TControl {
	
	/**
	 * 初始化
	 */
	 public void onInit(){
		 onClear();
	}
	
	/**
	 * 
	 * 检验用户是否输入.
	 * @param mrNo
	 * @param outDate
	 * @return
	 */
	private boolean checkInput(String mrNo,String outDate){
		if("".equals(mrNo)){
			this.messageBox("病案号不能为空");
			((TTextField) this.getComponent("MR_NO")).grabFocus() ;
			return false ;
		}
		if("".equals(outDate)){
			this.messageBox("出院日期不能为空");
			((TTextField) this.getComponent("OUT_DATE")).grabFocus() ;
			return false ;			
		}
		return true ;
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		String mrNo = this.getValueString("MR_NO") ;
		String outDate = this.getValueString("OUT_DATE") ;
		if(!checkInput(mrNo, outDate)){
			return ;
		}
		
		String[] outDateArray = outDate.split(" ") ;
		String[] newOutDateArray = outDateArray[0].split("-") ;
		String newOutDate = newOutDateArray[0]+"/"+newOutDateArray[1]+"/"+newOutDateArray[2] ;
		
		String sqlMaster = "Select REGION_CODE,YEAR_MON,MR_NO,PAT_NAME,IDNO,ADM_SEQ " +
				     "from INS_IBS " +
				     "where  mr_no='"+mrNo+"' and to_char(ds_date,'yyyy/mm/dd') ='"+newOutDate+"'" ;


		TParm result = new TParm(TJDODBTool.getInstance().select(sqlMaster));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("查无数据");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		String admSeq = result.getValue("ADM_SEQ", 0) ;
		String sqlDetail = "Select d.NHI_ORDER_CODE,d.ORDER_DESC,d.SEQ_NO,d.QTY,d.PRICE,d.TOTAL_AMT ,d.TOTAL_NHI_AMT, (d.TOTAL_NHI_AMT- d.REFUSE_AMT) as APPROVAL_AMT, d.REFUSE_AMT , d.REFUSE_REASON_NOTE " +
				           "from INS_IBSORDER_DOWNLOAD d ,sys_fee f " +
				           "where d.adm_seq='"+admSeq+"' and   d.order_code = f.order_code " +
				           "and  f.ACTIVE_FLG='Y' and  f.order_cat1_code='PHA' " +
				           "order by  seq_no" ;
		
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sqlDetail));
		if (result1.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result1.getCount() <= 0) {
			messageBox("查无数据");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TTABLE|setParmValue", result1);
	}

    /**
     * 病案号查询(回车事件)
     */
	public void onMrNoAction(){
		String mrNo = this.getValueString("MR_NO") ;
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		
		 // modify by huangtt 20160930 EMPI患者查重提示 start
	        Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
	        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				mrNo = pat.getMrNo();
			}
	     // modify by huangtt 20160930 EMPI患者查重提示 end
	        
	    this.setValue("MR_NO", mrNo) ;		
	}

	/**
	 * 清空
	 */
	public void onClear(){
		this.setValue("MR_NO", "") ;//住院日期
		this.setValue("OUT_DATE", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());	
		((TTextField) this.getComponent("MR_NO")).grabFocus() ;
	}
	
	/**
	 * 汇出
	 */
    public void onExport(){
    	TTable table = (TTable)this.getComponent("TTABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        } 
        ExportExcelUtil.getInstance().exportExcel(table, "天津市基本医疗保险住院药品明细表");   	
    }
}
