package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCOdiDspnTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCOdiDspnTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOdiDspnTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOdiDspnTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOdiDspnTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    /**
     * 查询ODI_DSPND表
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm ){
    	
//    	System.out.println("进入查询电子标签程序");
    	String caseNo = parm.getValue("CASE_NO");
    	String sql = "SELECT A.STATION_CODE,A.ELETAG_CODE  AS BOX_ESL_ID   FROM IND_INPATBOX  A  WHERE A.CASE_NO='"+caseNo+"'  ";
    	
    	TParm result =  new TParm(TJDODBTool.getInstance().select(sql));
//    	System.out.println("result---:"+result);
    	return result;
    }
}
