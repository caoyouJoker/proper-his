package jdo.spc.inf;
import java.util.ArrayList;
import java.util.List;

import jdo.spc.SPCSysFeeTool;
import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.spc.inf.dto.SPCIndRequestd;
import jdo.spc.inf.dto.SPCIndRequestm;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;


/**
 * <p>
 * Title:住院药房请领接口
 * </p>
 * 
 * <p>
 * Description:住院药房请领接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author fuwj 2012.12.20
 * @version 1.0
 */
public class SpcRequestDaoImpl extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static SpcRequestDaoImpl instanceObject;
	
	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SpcRequestDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcRequestDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcRequestDaoImpl() {
		onInit();
	}
	
	/**
	 * 保存住院药房请领信息
	 * @param requestM
	 * @return
	 */
	public String onSaveSpcRequest(SPCIndRequestm requestM){
		TParm parm = new TParm();
		TParm parmM = new TParm();     	
		parmM.setData("REQUEST_NO",requestM.getRequestNo());
		parmM.setData("REQTYPE_CODE",requestM.getReqtypeCode());
		parmM.setData("APP_ORG_CODE",requestM.getAppOrgCode());
		parmM.setData("TO_ORG_CODE",requestM.getToOrgCode());
		parmM.setData("REQUEST_DATE",StringTool.getTimestamp(requestM.getRequestDate(), "yyyy-MM-dd HH:mm:ss"));
		parmM.setData("REQUEST_USER",requestM.getRequestUser());
		parmM.setData("REASON_CHN_DESC",requestM.getReasonChnDesc());
		parmM.setData("DESCRIPTION",requestM.getDescription());
		parmM.setData("UNIT_TYPE",requestM.getUnitType());
		parmM.setData("URGENT_FLG",requestM.getUrgentFlg());
		parmM.setData("OPT_USER",requestM.getOptUser());
		parmM.setData("OPT_DATE", StringTool.getTimestamp(requestM.getOptDate(), "yyyy-MM-dd HH:mm:ss"));
		parmM.setData("OPT_TERM",requestM.getOptTerm());
		parmM.setData("REGION_CODE",requestM.getRegionCode());
		parmM.setData("DRUG_CATEGORY",requestM.getDrugCategory());
		parmM.setData("APPLY_TYPE",requestM.getApplyType());
		parm.setData("OUT_M", parmM.getData());
		 
		List<SPCIndRequestd> list = requestM.getIndRequestds();
		TParm parmD = new TParm();
		int count=0;
		List errorList = new ArrayList();
		for(int i=0;i<list.size();i++) {
			SPCIndRequestd requestD = list.get(i);
			String hisOrderCode = requestD.getOrderCode();
			TParm searchParm = new TParm();
			searchParm.setData("HIS_ORDER_CODE",hisOrderCode);
			TParm result = SPCSysFeeTool.getInstance().querySysFeeSpc(searchParm);
			if (result.getErrCode() < 0||result.getCount()<=0) {
				err(result.getErrCode() + " " + result.getErrText());			
				//return "该药品编码:"+hisOrderCode+"在物联网无对照";		
				errorList.add(hisOrderCode);
				continue;
			} 
			parmD.setData("REQUEST_NO",count,requestD.getRequestNo());
			parmD.setData("SEQ_NO",count,requestD.getSeqNo());
			String orderCode = (String) result.getData("ORDER_CODE",0);
			parmD.setData("ORDER_CODE",count,orderCode);
			String bathNo;
			if(requestD.getBatchNo()==null) {
				bathNo="";
			}else {
				bathNo= requestD.getBatchNo();
			}
			parmD.setData("BATCH_NO",count,bathNo);
			parmD.setData("VALID_DATE",count,"");			
			parmD.setData("QTY",count,requestD.getQty());		
			parmD.setData("UNIT_CODE",count,requestD.getUnitCode());
			parmD.setData("RETAIL_PRICE",count,requestD.getRetailPrice());
			parmD.setData("STOCK_PRICE",count,requestD.getStockPrice() );
			parmD.setData("ACTUAL_QTY",count,requestD.getActualQty());
			parmD.setData("UPDATE_FLG",count,requestD.getUpdateFlg());
			parmD.setData("OPT_USER",count,requestD.getOptUser());
			parmD.setData("OPT_DATE",count,StringTool.getTimestamp(requestD.getOptDate(), "yyyy-MM-dd HH:mm:ss"));
			parmD.setData("OPT_TERM",count,requestD.getOptTerm());
			parmD.setData("VERIFYIN_PRICE",count,requestD.getVerifyinPrice());
			parmD.setData("BATCH_SEQ",count,requestD.getBatchNo());
			count++;
		}
		String str = "";
		if(errorList.size()>0) {
			for(int i=0;i<errorList.size();i++) {
				str +=errorList.get(i)+";";
			}
			return str;
		}
		parmD.setCount(list.size());
		parm.setData("OUT_D", parmD.getData());
		Server.autoInit(this);
		TParm result = Server.executeAction("action.spc.SpcRequestAction",
                "onInsertRequest",parm);
		//判断是否成功  
        if (result == null || result.getErrCode() < 0) {
             return  "保存失败";                       				
        }   
		return "success";                              
	}  
	
	
}
