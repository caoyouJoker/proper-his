package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title:普药药库下架装箱 </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCGenDrugPutDownTool  extends TJDOTool {
    /**
     * 实例
     */
    public static SPCGenDrugPutDownTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCGenDrugPutDownTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCGenDrugPutDownTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCGenDrugPutDownTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm ){
    	
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String isBoxed = parm.getValue("IS_BOXED");
    	
    	if(StringUtil.isNullString(dispenseNo)){
    		return new TParm();
    	}
    	
    	String sql = "SELECT 'N' CHOOSE, B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,B.BATCH_NO,B.VALID_DATE, " +
    			     	    "B.QTY,F.UNIT_CHN_DESC ,D.MATERIAL_LOC_CODE,D.ELETAG_CODE,B.BOX_ESL_ID, " +
    			     	    "B.SEQ_NO, C.ORG_CHN_DESC ,A.TO_ORG_CODE ORG_CODE   "+
    			     "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_ORG C,IND_STOCKM D,PHA_BASE E,SYS_UNIT F "+
    			     "WHERE A.DRUG_CATEGORY='1' ";
    	
    	if(!StringUtil.isNullString(dispenseNo)){
    		sql += " AND A.DISPENSE_NO='"+dispenseNo+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(isBoxed)){
    		sql += " AND B.IS_BOXED='"+isBoxed+"' ";
    	}
    	sql += " AND A.DISPENSE_NO=B.DISPENSE_NO "+
    		   " AND A.APP_ORG_CODE=C.ORG_CODE "+   
    		   " AND B.ORDER_CODE=D.ORDER_CODE "+ 
    		   " AND A.TO_ORG_CODE=D.ORG_CODE "+
    		   " AND B.ORDER_CODE=D.ORDER_CODE "+
    		   " AND D.ORDER_CODE=E.ORDER_CODE " +
    		   " AND B.UNIT_CODE= F.UNIT_CODE "+
    		   " ORDER BY D.MATERIAL_LOC_CODE ";
    	
    	    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 药库普药更新周转箱
     */
    public TParm updateINDDispensed(TParm parm){
    	String isBoxed = parm.getValue("IS_BOXED");
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String seqNo = parm.getValue("SEQ_NO");
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String boxedUser = parm.getValue("BOXED_USER");
    	if(StringUtil.isNullString(dispenseNo) || StringUtil.isNullString(isBoxed) || StringUtil.isNullString(seqNo)){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_DISPENSED A SET IS_BOXED='"+isBoxed+"',BOX_ESL_ID='" +boxEslId+"',BOXED_USER='"+boxedUser+"',BOXED_DATE=SYSDATE "+
    			     " WHERE A.DISPENSE_NO='"+dispenseNo+"'  AND A.SEQ_NO='"+seqNo+"' ";
    	
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }

    /**
     * 药库普药更新周转箱(演示清空)
     */
    public TParm updateINDDispensedShow(TParm parm){
    	String isBoxed = parm.getValue("IS_BOXED");
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String boxedUser = parm.getValue("BOXED_USER");
    	if(StringUtil.isNullString(dispenseNo) ){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_DISPENSED A SET IS_BOXED='"+isBoxed+"',BOX_ESL_ID='" +boxEslId+"',BOXED_USER='"+boxedUser+"',BOXED_DATE='' "+
    			     " WHERE A.DISPENSE_NO='"+dispenseNo+"'  ";
    	
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }

    
}
