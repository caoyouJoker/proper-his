package action.spc;

import java.util.ArrayList;
import java.util.List;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndRequestMTool;


import action.ind.client.SpcIndRequestd;
import action.ind.client.SpcIndRequestm;
import action.ind.client.SpcService_SpcServiceImplPort_Client;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.spc.IndRequestDTool;

/**
 * <p>
 * Title:申请单
 * </p>
 *
 * <p>
 * Description: 申请单
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.05.24
 * @version 1.0
 */
public class INDRequestAction
    extends TAction {

    /**
     * 查询申请主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询申请主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryMAcnt(TParm parm) {//wanglong add 20150202
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onQueryAcnt(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertMAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onInsertAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateMAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onUpdateAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 删除申请单(主项及细项)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteRequestM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 删除申请单(主项及细项)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteMAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteSPCRequestM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 更新明细
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新明细
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateDAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdateAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 卫耗材请领
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertCosOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertCosOut(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 科室备药生成查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDeptExm(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryDeptExm(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 生成请领单
     * @param parm TParm
     * @return TParm
     */
    public TParm onCreateDeptExmRequest(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 生成请领单，传给物联网[科室备药生成]
     * @param parm
     * @return
     */
    public TParm onCreateDeptExmRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
         TParm reqParm =  parm.getParm("REQUEST_M");
         SpcIndRequestm indRequestm = getInsertM(reqParm);
        // System.out.println("reqParm---------:"+reqParm);
         TParm reqDParm = parm.getParm("REQUEST_D");
        // System.out.println("reqDParm---------:"+reqDParm);
         indRequestm = getInsertD(indRequestm, reqDParm);
         try{
 	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);
 	        
 	        if( out != null &&  !out.equals("success") ){
 	        	result.setErrCode(-1);
 	        	result.setErrText(out);
 	        	err("ERR:" + result.getErrCode() + result.getErrText()
 	                    + result.getErrName());
 	            conn.close();
 	            return result;
 	        }
         }catch (Exception e) {
 			// TODO: handle exception
         	e.printStackTrace() ;
         	err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
         	result.setErrCode(-1);
         	result.setErrText("调用服务端保存失败");
             conn.close();
             return result;
 		}
         conn.commit();
         conn.close();
         return result;
    }

    /**
     * 更新申请单状态
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateFlg(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdateFlg(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
      /*  String reurnString =this.onStopDispenseOut(parm);
        if("fail".equals(reurnString)) {
 			result.setErrCode(-1);
 			return result;
 		}  */ 
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 更新申请单状态
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateFlgAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdateFlgAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
      /*  String reurnString =this.onStopDispenseOut(parm);
        if("fail".equals(reurnString)) {
            result.setErrCode(-1);
            return result;
        }  */ 
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 组装传给物联网的IND_REQUESTM表数据
     * @param obj
     * @return
     */
    private SpcIndRequestm getInsertM(TParm parm ){
    	SpcIndRequestm obj = new SpcIndRequestm() ;
    	obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
        obj.setAppOrgCode(parm.getValue("APP_ORG_CODE"));
        obj.setToOrgCode(parm.getValue("TO_ORG_CODE"));
        obj.setRequestDate(parm.getValue("REQUEST_DATE"));
        obj.setRequestUser(parm.getValue("REQUEST_USER"));
        obj.setReasonChnDesc(parm.getValue("REASON_CHN_DESC"));
        obj.setDescription(parm.getValue("DESCRIPTION"));
         
        obj.setUnitType(parm.getValue("UNIT_TYPE"));
        obj.setUrgentFlg(parm.getValue("URGENT_FLG"));
        obj.setOptUser(parm.getValue("OPT_USER"));
         
        obj.setOptDate( parm.getValue("OPT_DATE"));
        obj.setOptTerm(parm.getValue("OPT_TERM"));
        obj.setDrugCategory(parm.getValue("DRUG_CATEGORY"));
        obj.setRequestNo(parm.getValue("REQUEST_NO"));
        obj.setRegionCode(parm.getValue("REGION_CODE"));
        obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
        obj.setApplyType("1");
    	return obj ;
    }
    
    /**
     * 组装传给物联网的IND_REQUESTD表数据
     * @param obj
     * @return
     */
    private SpcIndRequestm getInsertD(SpcIndRequestm obj,TParm parm){
    	
    	int count = parm.getCount("ORDER_CODE");
    	List<SpcIndRequestd> list = new ArrayList<SpcIndRequestd>();
    	for (int i = 0; i < count; i++) {
    		TParm rowParm = parm.getRow(i);
            SpcIndRequestd objD = new SpcIndRequestd() ;
            
            String order_code = rowParm.getValue("ORDER_CODE") ;
            objD.setOrderCode(order_code);
            
            TParm orderParm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getPHAInfoByOrder(order_code)));
            
            String unitCode = (String) orderParm.getData("DOSAGE_UNIT", 0);
            double retailPrice =  orderParm.getDouble("RETAIL_PRICE", 0);
            double stockPrice =  orderParm.getDouble("STOCK_PRICE", 0);
            
            objD.setUnitCode(unitCode);
            objD.setStockPrice(stockPrice);
            objD.setRetailPrice(retailPrice);
            
            objD.setQty(rowParm.getDouble("QTY"));
            objD.setActualQty(rowParm.getDouble( "ACTUAL_QTY"));
            objD.setRequestNo(rowParm.getValue("REQUEST_NO"));
            objD.setSeqNo(rowParm.getInt("SEQ_NO"));
            objD.setBatchNo("");
            objD.setValidDate("");
            
            objD.setUpdateFlg(rowParm.getValue("UPDATE_FLG"));
            objD.setOptDate(rowParm.getValue("OPT_DATE"));
            objD.setOptTerm(rowParm.getValue("OPT_TERM"));
            objD.setOptUser(rowParm.getValue("OPT_USER"));
            list.add(objD);
        }
    	obj.setIndRequestds(list);
    	return obj;
    }
    
    
    /**
     * 自动拨补-生成请领单
     *
     * @param parmM 
     * @param parmD 
     * @return TParm
     */
    public TParm onSaveRequestMD(TParm parmM,TParm parmD) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onSaveRequestMD(parmM, parmD, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
  /*  public String onStopDispenseOut(TParm parm) {
    	IndDispensem dispenseM = new IndDispensem();
    	dispenseM.setRequestNo(parm.getValue("REQUEST_NO"));
    	dispenseM.setUpdateFlg(parm.getValue("UPDATE_FLG"));
    	dispenseM.setOptUser(parm.getValue("OPT_USER"));
    	dispenseM.setOptTerm(parm.getValue("OPT_TERM"));
    	dispenseM.setOptDate(parm.getValue("OPT_DATE"));
    	String returnString = IndService_IndServiceImplPort_Client.onStop(dispenseM);
    	return returnString;
    }
    */
    
    /**
     * 生成请领单，传给物联网[科室备药生成]
     * @param parm
     * @return
     */
    public TParm onCreateDeptOdiRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptOdiRequestSpc(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
      /*   TParm reqParm =  parm.getParm("REQUEST_M");
         SpcIndRequestm indRequestm = getInsertM(reqParm);
//         System.out.println("reqParm---------:"+reqParm);
         TParm reqDParm = parm.getParm("REQUEST_D");
//         System.out.println("reqDParm---------:"+reqDParm);
         indRequestm = getInsertD(indRequestm, reqDParm);
        try{
 	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);
 	        
 	        if( out != null &&  !out.equals("success") ){
 	        	result.setErrCode(-1);
 	        	result.setErrText(out);
 	        	err("ERR:" + result.getErrCode() + result.getErrText()
 	                    + result.getErrName());
 	            conn.close();
 	            return result;
 	        }
         }catch (Exception e) {
 			// TODO: handle exception
         	e.printStackTrace() ;
         	err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
         	result.setErrCode(-1);
         	result.setErrText("调用服务端保存失败");
             conn.close();
             return result;
 		}*/
         conn.commit();
         conn.close();
         return result;
    }
    
    /**
     * 生成请领单，传给物联网[科室备药生成]
     * @param parm
     * @return
     */
    public TParm onCreateDeptOpdRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptOPDRequestSpc(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         conn.commit();
         conn.close();
         return result;
    }
    
    /**
     * 生成请领单，传给物联网[手术介入麻精备药申请]
     * @param parm
     * @author shendr 2013.07.30
     * @return
     */
    public TParm onCreateDeptOpiRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptOpiRequestSpc(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         conn.commit();
         conn.close();
         return result;
    }
    
}
