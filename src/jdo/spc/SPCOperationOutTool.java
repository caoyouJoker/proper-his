package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCOperationOutTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCOperationOutTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOperationOutTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOperationOutTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOperationOutTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm){
    	
    	String  opmedCode = parm.getValue("OPMED_CODE");
    	String  operationIcd  = parm.getValue("OPERATION_ICD");
    	
    	String  flg = parm.getValue("FLG");
    	
    	String sql = "" ;
    	if("Y".equals(flg)){
    		sql = " SELECT B.OPMED_CODE,B.ORDER_CODE,B.SPECIFICATION,B.ORDER_DESC,B.QTY, "
	    		    +"    B.UNIT_CODE,E.UNIT_CHN_DESC  "
	    		    +" FROM SYS_ORDER_OPMEDM A,SYS_ORDER_OPMEDD B,PHA_BASE C,SYS_CTRLDRUGCLASS D,SYS_UNIT E "
					+" WHERE     A.OPMED_CODE = B.OPMED_CODE "
					+"        AND B.ORDER_CODE = C.ORDER_CODE "
					+"        AND B.UNIT_CODE=E.UNIT_CODE "
					+"        AND C.CTRLDRUGCLASS_CODE = D.CTRLDRUGCLASS_CODE "
					+"        AND D.CTRL_FLG = 'Y' " ;
    	}else{
    		sql = " SELECT B.OPMED_CODE,B.ORDER_CODE,B.SPECIFICATION,B.ORDER_DESC,B.QTY, "
    		    + "    B.UNIT_CODE,D.UNIT_CHN_DESC  "
    		    + " FROM SYS_ORDER_OPMEDM A,SYS_ORDER_OPMEDD B,PHA_BASE C ,SYS_UNIT D "
    		    + " WHERE A.OPMED_CODE = B.OPMED_CODE "
    		    +"    AND B.ORDER_CODE=C.ORDER_CODE "
    		    +"    AND B.UNIT_CODE=D.UNIT_CODE "
    		    +"    AND (C.CTRLDRUGCLASS_CODE IS NULL OR  "
    		    +"         C.CTRLDRUGCLASS_CODE IN ( SELECT D.CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS D WHERE C.CTRLDRUGCLASS_CODE=D.CTRLDRUGCLASS_CODE AND D.CTRL_FLG='N'))";
    	}			 
    	if(opmedCode !=null && !opmedCode.equals("")){
    		sql += " AND A.OPMED_CODE='"+opmedCode+"' " ;
    	}
    	if(operationIcd !=null && !operationIcd.equals("")){
    		sql += " AND A.OPERATION_ICD='"+operationIcd+"' ";
    	}
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
}
