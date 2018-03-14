package action.spc;

import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndDispenseMTool;
import jdo.spc.IndStockDTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCSysFeeTool;
import jdo.util.Manager;
import action.spc.client.IndService_IndServiceImplPort_Client;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;

/**
 * <p> 
 * Title: 出入库管理
 * </p>
 *
 * <p>
 * Description: 出入库管理
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
 * @author liyh 2009.04.25
 * @version 1.0
 */
public class INDDispenseAction
    extends TAction {
    /**
     * 查询出库主档
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryOutM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryOutM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询出库主档
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryOutMAcnt(TParm parm) {//wanglong add 20150202
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryOutMAcnt(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询入库主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryInM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryInM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询入库主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryInMAcnt(TParm parm) {//wanglong add 20150202
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryInMAcnt(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 出库新增(在途) / 耗损、其它出库作业、卫耗材、科室备药(出库即入库)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
     /*   TParm parmM = parm.getParm("OUT_M");
        String appOrgCode = (String) parmM.getData("APP_ORG_CODE");
        if("040103".equals(appOrgCode)) {
        	 String reurnString = this.indDispenseOut(parm);
             if("fail".equals(reurnString)) {
      			result.setErrCode(-1);
      			conn.close();  
      			return result;
      		}  
        }*/
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 出库新增(在途) / 耗损、其它出库作业、卫耗材、科室备药(出库即入库)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOutOnAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutOnAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
     /*   TParm parmM = parm.getParm("OUT_M");
        String appOrgCode = (String) parmM.getData("APP_ORG_CODE");
        if("040103".equals(appOrgCode)) {
             String reurnString = this.indDispenseOut(parm);
             if("fail".equals(reurnString)) {
                result.setErrCode(-1);
                conn.close();  
                return result;
            }  
        }*/
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 出库主项更新(在途)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateMOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onUpdateM(parm, conn);
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
     * 出库主项更新(在途)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateMOutOnAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onUpdateMAcnt(parm, conn);
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
     * 出库即入库
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();            
        result = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
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
     * 出库即入库
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutInAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();            
        result = INDTool.getInstance().onInsertDispenseOutInAcnt(parm, conn);
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
     * 入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
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
     * 入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertInAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseInAcnt(parm, conn);
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
     * 入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertToxic(TParm parm) {   			    
        TConnection conn = getConnection();
        TParm result = new TParm();
	    result = INDTool.getInstance().onInsertDispenseInToxic(parm, conn);
	    if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText()
	               + result.getErrName()); 
	          conn.close();
	          return result;   			
	    }  
        result = SPCInStoreTool.getInstance().inStockYF(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 病区麻精入智能柜
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertToxicStation(TParm parm) {   			    
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = SPCInStoreTool.getInstance().inStock(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 其他入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOtherIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOtherIn(parm, conn);
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
     * 其他入库新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOtherInAcnt(TParm parm) {//wanglong modify 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOtherInAcnt(parm, conn);
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
     * 取消出库作业
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDipenseCancel(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateDipenseCancel(parm, conn);
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
     * 取消出库作业
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDipenseCancelAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateDipenseCancelAcnt(parm, conn);
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
     * 草药自动维护收费标准
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateGrpricePrice(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateGrpricePriceSpc(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        TIOM_Database.logTableAction("SYS_FEE");
        return result;
    }
    
    /**
     * 草药自动维护收费标准
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateGrpricePriceAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateGrpricePriceSpcAcnt(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        TIOM_Database.logTableAction("SYS_FEE");
        return result;
    }
    
	/**
	 * 组装参数调用his出库接口
	 * @param parm
	 * @return
	 */  
	public String indDispenseOut(TParm parm) {
		TParm parmM = parm.getParm("OUT_M");
		String dispenseNo = (String) parmM.getData("DISPENSE_NO");
		String reqtype = (String) parmM.getData("REQTYPE_CODE");
		String stockType = (String)parmM.getData("STOCK_TYPE");
		String REQUEST_NO = (String) parmM.getData("REQUEST_NO");
		String requestDate = parmM.getValue("REQUEST_DATE");
		String appOrgCode = (String) parmM.getData("APP_ORG_CODE");
		String toOrgCode = (String) parmM.getData("TO_ORG_CODE");
		String urgentFlg = (String) parmM.getData("URGENT_FLG");
		String DESCRIPTION = (String) parmM.getData("DESCRIPTION");
		String DISPENSE_DATE = parmM.getValue("DISPENSE_DATE");
		String DISPENSE_USER = (String) parmM.getData("DISPENSE_USER");
		String WAREHOUSING_DATE= "";
		if(parmM.getValue("WAREHOUSING_DATE")=="<TNULL>") {
			WAREHOUSING_DATE = "";
		}else {
			WAREHOUSING_DATE = parmM.getValue("WAREHOUSING_DATE");
		}
		String WAREHOUSING_USER = (String) parmM.getData("WAREHOUSING_USER");
		String REASON_CHN_DESC = (String) parmM.getData("REASON_CHN_DESC");
		String UNIT_TYPE = (String) parmM.getData("UNIT_TYPE");
		String UPDATE_FLG = (String) parmM.getData("UPDATE_FLG");
		String OPT_USER = (String) parmM.getData("OPT_USER");
		String OPT_DATE = parmM.getValue("OPT_DATE");
		String OPT_TERM = (String) parmM.getData("OPT_TERM");
		String REGION_CODE = (String) parmM.getData("REGION_CODE");
		String DRUG_CATEGORY = (String) parmM.getData("DRUG_CATEGORY");
		String xmlString ="<ROOT>";
		xmlString = xmlString+"<INDM>";
		xmlString = xmlString+"<DISPENSE_NO>";
		xmlString = xmlString+dispenseNo;
		xmlString = xmlString+"</DISPENSE_NO>";
		xmlString = xmlString+"<REQTYPE_CODE>";
		xmlString = xmlString+reqtype;
		xmlString = xmlString +"</REQTYPE_CODE>";
		xmlString = xmlString+"<REQUEST_NO>";
		xmlString = xmlString+REQUEST_NO;
		xmlString = xmlString +"</REQUEST_NO>";
		xmlString = xmlString+"<STOCK_TYPE>";
		xmlString = xmlString+stockType;
		xmlString = xmlString+"</STOCK_TYPE>";
		xmlString = xmlString+"<REQUEST_DATE>";
		xmlString = xmlString+requestDate;
		xmlString = xmlString+"</REQUEST_DATE>";
		xmlString = xmlString+"<APP_ORG_CODE>";
		xmlString = xmlString+appOrgCode;
		xmlString = xmlString+"</APP_ORG_CODE>";
		xmlString = xmlString+"<TO_ORG_CODE>";
		xmlString = xmlString+toOrgCode;
		xmlString = xmlString+"</TO_ORG_CODE>";
		xmlString = xmlString+"<URGENT_FLG>";
		xmlString = xmlString+urgentFlg;
		xmlString = xmlString+"</URGENT_FLG>";
		xmlString = xmlString+"<DESCRIPTION>";
		xmlString = xmlString+DESCRIPTION;
		xmlString = xmlString+"</DESCRIPTION>";
		xmlString = xmlString+"<DISPENSE_DATE>";
		xmlString = xmlString+DISPENSE_DATE;
		xmlString = xmlString+"</DISPENSE_DATE>";
		xmlString = xmlString+"<DISPENSE_USER>";
		xmlString = xmlString+DISPENSE_USER;
		xmlString = xmlString+"</DISPENSE_USER>";
		xmlString = xmlString+"<WAREHOUSING_DATE>";
		xmlString = xmlString+WAREHOUSING_DATE;
		xmlString = xmlString+"</WAREHOUSING_DATE>";
		xmlString = xmlString+"<WAREHOUSING_USER>";
		xmlString = xmlString+WAREHOUSING_USER;
		xmlString = xmlString+"</WAREHOUSING_USER>";
		xmlString = xmlString+"<REASON_CHN_DESC>";
		xmlString = xmlString+REASON_CHN_DESC;
		xmlString = xmlString+"</REASON_CHN_DESC>";
		xmlString = xmlString+"<UNIT_TYPE>";
		xmlString = xmlString+UNIT_TYPE;
		xmlString = xmlString+"</UNIT_TYPE>";
		xmlString = xmlString+"<UPDATE_FLG>";
		xmlString = xmlString+UPDATE_FLG;
		xmlString = xmlString+"</UPDATE_FLG>";
		xmlString = xmlString+"<OPT_USER>";
		xmlString = xmlString+OPT_USER;
		xmlString = xmlString+"</OPT_USER>";
		xmlString = xmlString+"<OPT_DATE>";
		xmlString = xmlString+OPT_DATE;
		xmlString = xmlString+"</OPT_DATE>";
		xmlString = xmlString+"<OPT_TERM>";
		xmlString = xmlString+OPT_TERM;
		xmlString = xmlString+"</OPT_TERM>";
		xmlString = xmlString+"<REGION_CODE>";
		xmlString = xmlString+REGION_CODE;
		xmlString = xmlString+"</REGION_CODE>";
		xmlString = xmlString+"<DRUG_CATEGORY>";
		xmlString = xmlString+DRUG_CATEGORY;
		xmlString = xmlString+"</DRUG_CATEGORY>";
		xmlString = xmlString+"</INDM>";
		xmlString = xmlString+"<INDDS>";
		TParm parmD = parm.getParm("OUT_D"); 
		double verifyin_price = 0;
		for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
			String orderCode = (String) parmD.getData("ORDER_CODE",i);
			String UNIT_CODE = (String) parmD.getData("UNIT_CODE",i);
			double actual_qty = 0;
			double qty_out = 0;										
		//	orderCode = parm.getValue("ORDER_CODE", i);
			if ("0".equals(UNIT_CODE)) {
				actual_qty = parmD.getDouble("ACTUAL_QTY", i) * getPhaTransUnitQty(orderCode, "2");
			} else {
				actual_qty = parmD.getDouble("ACTUAL_QTY", i);
			}
			if ("DEP".equals(reqtype) || "TEC".equals(reqtype) || "GIF".equals(reqtype) || "COS".equals(reqtype)
					|| "EXM".equals(reqtype)) {
				TParm stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(toOrgCode, orderCode, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
					if (qty >= actual_qty) {
						parmD.setData("BATCH_SEQ", i,stock_parm.getInt("BATCH_SEQ", j));
						parmD.setData("BATCH_NO", i,stock_parm.getValue("BATCH_NO", j));
						parmD.setData("VALID_DATE", i,stock_parm.getData("VALID_DATE", j));
						double tranUnitQty = getDosageQty(orderCode);
						if ("0".equals(UNIT_CODE)) {
							qty_out = actual_qty / tranUnitQty;
						} else {
							qty_out = actual_qty;
						}
						parmD.setData("QTY", i,qty_out);
						if ("TEC".equals(reqtype) || "EXM".equals(reqtype) || "COS".equals(reqtype)) {
							parmD.setData("RETAIL_PRICE", i,stock_parm.getDouble("STOCK_RETAIL_PRICE", j));
						}else {
							parmD.setData("RETAIL_PRICE", i,stock_parm.getDouble("STOCK_RETAIL_PRICE", j) * tranUnitQty);
						}
						double stock_price = stock_parm.getDouble("VERIFYIN_PRICE", j) * tranUnitQty;
						parmD.setData("STOCK_PRICE", i,stock_price);
						// luhai 2012-01-13 add verifyin_price begin
						if ("TEC".equals(reqtype) || "EXM".equals(reqtype) || "COS".equals(reqtype)) {
							verifyin_price = stock_parm.getDouble("VERIFYIN_PRICE", j);
						} else {
							verifyin_price = stock_parm.getDouble("VERIFYIN_PRICE", j) * tranUnitQty;
							// luhai modify 20120503 add
							// 验收价格的调整，若出库为库存单位则取值时从实际的验收表中进行取值 begin
							int batchSeq = stock_parm.getInt("BATCH_SEQ", j);
						//	String orderCode = parm.getValue("ORDER_CODE", i);
							double tmpVerifyPrice = getVerifyInStockPrice(toOrgCode, orderCode, batchSeq);// 修改取得验收价格的方法
							verifyin_price = tmpVerifyPrice > 0 ? tmpVerifyPrice : verifyin_price;

						}  
						parmD.setData("VERIFYIN_PRICE", i,verifyin_price);
						// luhai 2012-01-13 add verifyin_price end
						parmD.setData("ACTUAL_QTY", i,qty_out);
					}
				}
			}
			String DISPENSE_NO = (String) parmD.getData("DISPENSE_NO",i);	
			int SEQ_NO = parmD.getInt("SEQ_NO",i);
			int REQUEST_SEQ = parmD.getInt("REQUEST_SEQ",i);
			TParm searchParm = new TParm();    
			searchParm.setData("ORDER_CODE",orderCode); 
			searchParm.setData("REGION_CODE",REGION_CODE); 
			TParm result = SPCSysFeeTool.getInstance().queryHisOrderCode(searchParm);
			if (result.getErrCode() < 0||result.getCount()<=0) {
				err(result.getErrCode() + " " + result.getErrText());	
				continue;
			}
			String hisOrderCode = (String) result.getData("HIS_ORDER_CODE",0);
			int BATCH_SEQ = parmD.getInt("BATCH_SEQ",i);
			String BATCH_NO = (String) parmD.getData("BATCH_NO",i);
			String VALID_DATE ="";
			if("<TNULL>".equals(parmD.getValue("VALID_DATE",i))||parmD.getValue("VALID_DATE",i)=="<TNULL>") {
				VALID_DATE="";   
			}else {		
				VALID_DATE= parmD.getValue("VALID_DATE",i);
			}
			double QTY = parmD.getDouble("QTY",i);
			double RETAIL_PRICE =  parmD.getDouble("RETAIL_PRICE",i);
			double STOCK_PRICE = parmD.getDouble("STOCK_PRICE",i);
			double ACTUAL_QTY = parmD.getDouble("ACTUAL_QTY",i);
			String PHA_TYPE = (String) parmD.getData("PHA_TYPE",i);
			String OptUser = (String) parmD.getData("OPT_USER",i);
			String optDate = parmD.getValue("OPT_DATE",i);
			String optTerm = (String) parmD.getData("OPT_TERM",i);
		//	String IS_BOXED = (String) parmD.getData("IS_BOXED",i);
		//	String BOXED_USER = (String) parmD.getData("BOXED_USER",i);
		//	String BOX_ESL_ID = (String) parmD.getData("BOX_ESL_ID",i);
			xmlString = xmlString+"<INDD>";
			xmlString = xmlString+"<DISPENSE_NO>";
			xmlString = xmlString+DISPENSE_NO;
			xmlString = xmlString+"</DISPENSE_NO>";
			xmlString = xmlString+"<SEQ_NO>";
			xmlString = xmlString+SEQ_NO;
			xmlString = xmlString+"</SEQ_NO>";
			xmlString = xmlString+"<REQUEST_SEQ>";
			xmlString = xmlString+REQUEST_SEQ;
			xmlString = xmlString+"</REQUEST_SEQ>";
			xmlString = xmlString+"<ORDER_CODE>";
			xmlString = xmlString+hisOrderCode;
			xmlString = xmlString+"</ORDER_CODE>";
			xmlString = xmlString+"<BATCH_SEQ>";
			xmlString = xmlString+BATCH_SEQ;
			xmlString = xmlString+"</BATCH_SEQ>";
			xmlString = xmlString+"<BATCH_NO>";
			xmlString = xmlString+BATCH_NO;
			xmlString = xmlString+"</BATCH_NO>";
			xmlString = xmlString+"<VALID_DATE>";
			xmlString = xmlString+VALID_DATE;
			xmlString = xmlString+"</VALID_DATE>";
			xmlString = xmlString+"<QTY>";
			xmlString = xmlString+QTY;
			xmlString = xmlString+"</QTY>";
			xmlString = xmlString+"<UNIT_CODE>";
			xmlString = xmlString+UNIT_CODE;
			xmlString = xmlString+"</UNIT_CODE>";
			xmlString = xmlString+"<RETAIL_PRICE>";
			xmlString = xmlString+RETAIL_PRICE;
			xmlString = xmlString+"</RETAIL_PRICE>";
			xmlString = xmlString+"<STOCK_PRICE>";
			xmlString = xmlString+STOCK_PRICE;
			xmlString = xmlString+"</STOCK_PRICE>";
			xmlString = xmlString+"<ACTUAL_QTY>";
			xmlString = xmlString+ACTUAL_QTY;
			xmlString = xmlString+"</ACTUAL_QTY>";
			xmlString = xmlString+"<PHA_TYPE>";
			xmlString = xmlString+PHA_TYPE;
			xmlString = xmlString+"</PHA_TYPE>";
			xmlString = xmlString+"<OPT_USER>";
			xmlString = xmlString+OptUser;
			xmlString = xmlString+"</OPT_USER>";
			xmlString = xmlString+"<OPT_DATE>";
			xmlString = xmlString+optDate;
			xmlString = xmlString+"</OPT_DATE>";
			xmlString = xmlString+"<OPT_TERM>";
			xmlString = xmlString+optTerm;
			xmlString = xmlString+"</OPT_TERM>";
/*			xmlString = xmlString+"<IS_BOXED>";
			xmlString = xmlString+IS_BOXED;
			xmlString = xmlString+"</IS_BOXED>";
			xmlString = xmlString+"<BOXED_USER>";
			xmlString = xmlString+BOXED_USER;
			xmlString = xmlString+"</BOXED_USER>";
			xmlString = xmlString+"<BOX_ESL_ID>";
			xmlString = xmlString+BOX_ESL_ID;
			xmlString = xmlString+"</BOX_ESL_ID>";*/
			xmlString = xmlString+"<VERIFYIN_PRICE>";
			xmlString = xmlString+verifyin_price;      
			xmlString = xmlString+"</VERIFYIN_PRICE>";
			xmlString = xmlString+"</INDD>";
		}
		xmlString = xmlString+"</INDDS>";
		xmlString = xmlString+"</ROOT>";  
		String returnString = IndService_IndServiceImplPort_Client.onSaveDispense(xmlString);
		return returnString;             
	}        
	
	/**
	 * 取得药品转换率
	 * 
	 * @param order_code
	 *            药品代码
	 * @param qty_type
	 *            转换率类型 1:进货/库存 2:库存/配药 3:配药/开药 (大单位-->小单位)
	 * @return
	 */
	public double getPhaTransUnitQty(String order_code, String qty_type) {
		TParm parm = new TParm(Manager.getMedicine().getPhaTransUnit(order_code));
		if ("1".equals(qty_type)) {
			return parm.getDouble("STOCK_QTY", 0);
		} else if ("2".equals(qty_type)) {
			return parm.getDouble("DOSAGE_QTY", 0);
		} else if ("3".equals(qty_type)) {
			return parm.getDouble("MEDI_QTY", 0);
		}
		return 1;
	}
	
	/**
	 * 得到药品的发药-库存转换率
	 * 
	 * @return
	 */
	public double getDosageQty(String orderCode) {
		double dosageQty = 1;
		Map select = TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(orderCode));
		TParm orderParm = new TParm(select);
		if (orderParm.getCount() <= 0) {
			return 1;
		}
		dosageQty = orderParm.getDouble("DOSAGE_QTY", 0);
		return dosageQty;
	}
	
	/**
	 * 根据药品的batchseq ordercode 从ind_stock中得到验收时的价格 luhai add 2012-05-03
	 * 
	 * @return
	 */
	public double getVerifyInStockPrice(String orgCode, String orderCode, int batchSeq) {
		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndVerifyInPrice(orgCode, orderCode, batchSeq)));
		if (result.getErrCode() < 0) {
			return 0;
		} else if (result.getCount() <= 0) {
			return 0;
		} else {
			return result.getDouble("VERIFYIN_PRICE", 0);
		}
	}
	
	  /**
     * 盒装包药机下架装箱更新 
     * @param parm
     * @return
     */
    public TParm onOutPacked(TParm parm){
    	TConnection conn = getConnection() ;
    	TParm result = new TParm();
    	TParm parmM = parm.getParm("OUT_M");
    	TParm parmD = parm.getParm("OUT_D");
    	result = INDTool.getInstance().onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),parmD,conn);
    	if(result.getErrCode() <  0 ){
    		err("ERR:" + result.getErrCode() + result.getErrText()
    	                + result.getErrName());
    		conn.rollback() ;
    	    conn.close();
    	    return result;
    	}
    	conn.commit();
        conn.close();
        return result;
    }
}
