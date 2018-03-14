package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

public class SPCNarTrunkBindTool extends TJDOTool {
	
    /**
     * 实例
     */
    public static SPCNarTrunkBindTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCNarTrunkBindTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCNarTrunkBindTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCNarTrunkBindTool() {
    	setModuleName("spc\\SPCPoisonBaleModule.x");		
        onInit();
    }
    
    /**
     * 查询已打包容器
     * @param parm
     * @return
     */
    public TParm queryNarTrunkBind(TParm tparm){		
        TParm result = new TParm();
        result = query("queryNarTrunkBind",tparm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result ;
    }
    
    /**
     * 查询请领部门
     * @param parm
     * @return
     */
    public TParm queryOrg(TParm tparm){		
        TParm result = new TParm();
        result = query("queryOrg",tparm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result ;
    }
    
    
    /**
     * 查询已打包容器
     * @param parm
     * @return
     */
    public TParm queryNarTrunkBindMX(TParm tparm){		
        TParm result = new TParm();
        result = query("queryNarTrunkBindMX",tparm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    public TParm updateINDDispensed(TParm parm){	
    	String isPutaway = parm.getValue("IS_BOXED");
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String seqNo = parm.getValue("SEQ_NO");
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String boxedUser = parm.getValue("BOXED_USER");
    	String containerId = parm.getValue("CONTAINER_ID");
    	if(StringUtil.isNullString(dispenseNo) || StringUtil.isNullString(isPutaway) || StringUtil.isNullString(seqNo)){
    		return new TParm();
    	}
    				
    	String sql = " UPDATE IND_TOXICM  A SET IS_BOXED='"+isPutaway+"',BOX_ESL_ID='" +boxEslId+"',BOXED_USER='"+boxedUser+"',BOXED_DATE=sysdate "+
    			     " WHERE A.DISPENSE_NO='"+dispenseNo+"'  AND A.DISPENSE_SEQ_NO='"+seqNo+"' AND CONTAINER_ID='"+containerId+"'";		
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }
	
}
