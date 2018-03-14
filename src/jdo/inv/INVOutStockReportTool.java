package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: ���ư��˰��˶Ա���</p>
 *
 * <p>Description: ���ư��˰��˶Ա���</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.11.13
 * @version 1.0
 */
public class INVOutStockReportTool extends TJDOTool{
	  /**
     * ʵ��
     */
    public static INVOutStockReportTool instanceObject;
	
	/**
     * �õ�ʵ��
     * @return INVVerifyinTool
     */
    public static INVOutStockReportTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVOutStockReportTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INVOutStockReportTool() {
        setModuleName("inv\\INVOutStockReportModule.x");
        onInit();
    }
    
    /**
     * ��ѯ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm getSelectdata(TParm parm) {
    	
    	
    	String sql="SELECT A.DISPENSE_NO,A.FROM_ORG_CODE,A.TO_ORG_CODE,B.INV_CODE, B.QTY, C.STOCK_UNIT, C.DESCRIPTION, "+
			       "C.INV_CHN_DESC,C.MAN_CODE, C.SUP_CODE,C.UP_SUP_CODE, "+
			       "F.CONTRACT_PRICE,SF.OWN_PRICE AS COST_PRICE,( F.CONTRACT_PRICE*B.QTY ) AS CONTRACT_AMT, "+
			       "( SF.OWN_PRICE*B.QTY ) AS COST_AMT, "+
			       "( SF.OWN_PRICE - F.CONTRACT_PRICE ) AS DIFFERENCE_AMT,C.INV_KIND "+
			       "FROM "+
			       "INV_DISPENSEM A, "+
			       "INV_DISPENSED B, "+ 
			       "INV_BASE C, "+
		           "INV_AGENT F, "+
		           "INV_ORG G, "+
		           "INV_ORG H, "+
		           "SYS_FEE SF "+
		           "WHERE "+ 
			       "A.DISPENSE_NO=B.DISPENSE_NO AND "+
			       "B.INV_CODE=C.INV_CODE AND "+
			       "B.INV_CODE=F.INV_CODE AND "+
			       "A.FROM_ORG_CODE=G.ORG_CODE AND "+
			       "A.TO_ORG_CODE=H.ORG_CODE AND "+
			       "C.ORDER_CODE=SF.ORDER_CODE AND "+
			       "B.IO_FLG='2' AND " +
		           "A.DISPENSE_DATE  BETWEEN TO_DATE('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
			       "AND  TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMMDDHH24MISS') ";
    	
    	//���ʱ���
    	if(parm.getValue("INV_CODE").length()>0){
    		sql+="AND B.INV_CODE='"+parm.getValue("INV_CODE")+"' ";
    	}
    	//��Ӧ��
    	if(parm.getValue("SUP_CODE").length()>0){
    		sql+="AND C.SUP_CODE='"+parm.getValue("SUP_CODE")+"' ";
    	}
    	//�ϼ���Ӧ��
    	if(parm.getValue("UP_SUP_CODE").length()>0){
    		sql+="AND C.UP_SUP_CODE='"+parm.getValue("UP_SUP_CODE")+"' ";
    	}
    	//��ⲿ��
    	if(parm.getValue("FROM_ORG_CODE").length()>0){
    		sql+="AND A.FROM_ORG_CODE='"+parm.getValue("FROM_ORG_CODE")+"' ";
    	}
    	//���ⲿ��
    	if(parm.getValue("TO_ORG_CODE").length()>0){
    		sql+="AND A.TO_ORG_CODE='"+parm.getValue("TO_ORG_CODE")+"' ";
    	}
    	//���ʷ���
    	if(parm.getValue("INV_KIND").length()>0){
    		sql+="AND C.INV_KIND='"+parm.getValue("INV_KIND")+"' ";
    	}
    	//��ֵ
    	if("Y".equals(parm.getValue("H_FLG"))){
    		sql+="AND C.SEQMAN_FLG='Y' AND C.EXPENSIVE_FLG='Y' ";
    	}
    	
    	//��ֵ
    	if("Y".equals(parm.getValue("L_FLG"))){
    		sql+="AND ( C.SEQMAN_FLG<>'Y' OR C.EXPENSIVE_FLG<>'Y' )  ";
    	}
    	
    	//����
    	if("Y".equals(parm.getValue("CONSIGN_FLG"))){
    		sql+="AND G.CON_FLG='Y' ";
    	}else{
    		sql+="AND (G.CON_FLG='N' OR  G.CON_FLG IS NULL ) ";
    	}
    	
    	//���ⵥ��
    	if(parm.getValue("DISPENSE_NO").length()>0){
    		sql+="AND A.DISPENSE_NO='"+parm.getValue("DISPENSE_NO")+"' ";
    	}
    	
//    	System.out.println("================"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
       // TParm result = this.query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm getSelectdetail(TParm parm) {
    	TParm result = this.query("selectdetail", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }
}

