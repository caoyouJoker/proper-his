package jdo.ind;

import java.util.List;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

public class IndService implements IIndService {

	public String onSaveDispenseDM(IndDispensem indDispensem){
		
		if(indDispensem == null ){
			return  "fail";
		}
		TParm parm = new TParm();
		
		//主表
		TParm parmM = new TParm() ;
		parmM.setData("DISPENSE_NO", indDispensem.getDispenseNo());
        parmM.setData("REQTYPE_CODE", indDispensem.getReqtypeCode());
        parmM.setData("REQUEST_NO", indDispensem.getRequestNo());
        parmM.setData("REQUEST_DATE", indDispensem.getRequestDate());
        parmM.setData("APP_ORG_CODE", indDispensem.getAppOrgCode());
        parmM.setData("TO_ORG_CODE", indDispensem.getToOrgCode());
        parmM.setData("URGENT_FLG", indDispensem.getUrgentFlg());
        parmM.setData("DESCRIPTION", indDispensem.getDescription());
        parmM.setData("DISPENSE_DATE", indDispensem.getDispenseDate());
        parmM.setData("DISPENSE_USER",indDispensem.getDispenseUser());
        parmM.setData("WAREHOUSING_DATE", indDispensem.getWarehousingDate());
        parmM.setData("WAREHOUSING_USER",indDispensem.getWarehousingUser());
        parmM.setData("REASON_CHN_DESC", indDispensem.getReasonChnDesc());
        parmM.setData("UNIT_TYPE", indDispensem.getUnitType());
        parmM.setData("UPDATE_FLG", indDispensem.getUpdateFlg());
        parmM.setData("OPT_USER", indDispensem.getOptUser());
        parmM.setData("OPT_DATE", indDispensem.getOptDate());
        parmM.setData("OPT_TERM", indDispensem.getOptTerm());
        parmM.setData("REGION_CODE", indDispensem.getRegionCode());
        parm.setData("OUT_M", parmM.getData());
        
        List<IndDispensed> list = indDispensem.getIndDispenseds() ;
        //子表
        TParm parmD = new TParm() ;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            IndDispensed indDispensed = (IndDispensed)list.get(i);
            parmD.setData("DISPENSE_NO", count, indDispensed.getDispenseNo());
            parmD.setData("SEQ_NO", count, indDispensed.getSeqNo());
            parmD.setData("REQUEST_SEQ", count,indDispensed.getRequestSeq());
            parmD.setData("ORDER_CODE", count, indDispensed.getOrderCode());
            parmD.setData("QTY", count, indDispensed.getQty());
            parmD.setData("UNIT_CODE", count, indDispensed.getUnitCode());
            parmD.setData("RETAIL_PRICE", count, indDispensed.getRetailPrice());
            parmD.setData("STOCK_PRICE", count, indDispensed.getStockPrice());
            parmD.setData("ACTUAL_QTY", count, indDispensed.getActualQty());
            parmD.setData("PHA_TYPE", count, indDispensed.getPhaType());
            parmD.setData("BATCH_SEQ",count,indDispensed.getBatchSeq());
            parmD.setData("BATCH_NO", count, indDispensed.getBatchNo());
            parmD.setData("VALID_DATE", count,indDispensed.getValidDate());
            parmD.setData("DOSAGE_QTY", count, indDispensed.getDosageQty());
            parmD.setData("OPT_USER", count, indDispensed.getOptUser());
            parmD.setData("OPT_DATE", count, indDispensed.getOptDate());
            parmD.setData("OPT_TERM", count, indDispensed.getOptTerm());
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        
        Server.autoInit(this);
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                "onInsertOutOn", parm);
        
        //判断是否成功
        if (parm == null || parm.getErrCode() < 0) {
             return  "fail";
        }
		return "success";
	}
	
}
