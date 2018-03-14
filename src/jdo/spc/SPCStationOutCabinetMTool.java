package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

public class SPCStationOutCabinetMTool  extends TJDOTool {
	

	/**
     * 实例
     */
    public static SPCStationOutCabinetMTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCStationOutCabinetMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCStationOutCabinetMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCStationOutCabinetMTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    /**
     * 根据统药单取得对应的详细药品数据
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm){
    	
    	String intgmedNo = parm.getValue("INTGMED_NO");
    	if(StringUtil.isNullString(intgmedNo )){
    		return new TParm();
    	}
    	
    	String sql = " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, "
    			   + " A.ORDER_DESC || ' ' || CASE WHEN A.SPECIFICATION IS NOT NULL THEN A.SPECIFICATION ELSE '' END  AS ORDER_DESC ,  A.SPECIFICATION, "
    			   + " TO_CHAR(A.MEDI_QTY ,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
    			   + " TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || E.UNIT_CHN_DESC AS DISPENSE_QTY,A.OWN_AMT "
    			   + " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E  "
    			   + " WHERE A.INTGMED_NO ='" + intgmedNo + "'  AND A.LINK_NO IS NULL " 
    			   + "       AND B.UNIT_CODE=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE AND E.UNIT_CODE=A.DISPENSE_UNIT "
    			   + " ORDER BY A.ORDER_CODE ";
    	//System.out.println("sql==============:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
}
