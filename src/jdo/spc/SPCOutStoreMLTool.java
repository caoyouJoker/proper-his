package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>Title: 临时医嘱/首日量-麻精出住院药房智能柜 </p>
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
public class SPCOutStoreMLTool extends  TJDOTool {

	/**
     * 实例
     */
    public static SPCOutStoreMLTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOutStoreMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOutStoreMLTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOutStoreMLTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm ){
    	
    	/**
    	 * 调配单号
    	 */
    	String phaDispenseNo = parm.getValue("PHA_DISPENSE_NO");
    	
    	if(phaDispenseNo == null || phaDispenseNo.equals("")){
    		return new TParm();
    	}
    	
    	String sql =  " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.MR_NO, "
    				+ "        A.ORDER_DESC ,A.SPECIFICATION,F.PAT_NAME,A.ACUM_OUTBOUND_QTY ,A.DOSAGE_QTY,  "
    				+ " 	   TO_CHAR(A.MEDI_QTY,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
    				+ " 	   TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') AS DISPENSE_QTY , E.UNIT_CHN_DESC ,A.OWN_AMT,A.STATION_CODE,A.ORDER_CODE,A.TAKEMED_ORG  "
            + " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E,SYS_PATINFO F  "
            + " WHERE  " 
            + "         B.UNIT_CODE(+)=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE " 
            + "         AND E.UNIT_CODE(+)=A.DISPENSE_UNIT AND A.MR_NO=F.MR_NO "
            + "         AND ( A.CTRLDRUGCLASS_CODE IS NOT NULL OR "
			+ "							A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) "
            +"  AND A.PHA_DISPENSE_NO ='" + phaDispenseNo + "' "
            + " ORDER BY (A.DOSAGE_QTY-A.ACUM_OUTBOUND_QTY) DESC ";
    	
    	//System.out.println("sql----------:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onUpdateOdiDspnm(TParm parm,TConnection conn ){
    	String caseNo = parm.getValue("CASE_NO") ;
    	String  orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String startDttm = parm.getValue("START_DTTM");
    	
    	if(caseNo == null || caseNo.equals("") ||
    		orderNo == null || orderNo.equals("") || 
    		orderSeq == null || orderSeq.equals("") || 
    		startDttm == null || startDttm.equals("")){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE ODI_DSPNM A SET A.ACUM_OUTBOUND_QTY= NVL(A.ACUM_OUTBOUND_QTY,0) + 1 " +
    			     "    WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.ORDER_NO='"+parm.getValue("ORDER_NO")+"' "+
    			     "          AND A.ORDER_SEQ="+parm.getValue("ORDER_SEQ")+"  AND A.START_DTTM='"+parm.getValue("START_DTTM")+"' ";
    	//System.out.println("onUpdateOdiDspnm=====:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql, conn));
    }
    
    public TParm onUpdateIndCabdspn(TParm parm,TConnection conn ){
    	String caseNo = parm.getValue("CASE_NO") ;
    	String  orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String startDttm = parm.getValue("START_DTTM");
    	
    	String  toxicId =  parm.getValue("TOXIC_ID");
    	TParm result = new TParm() ;
    	
    	if(caseNo == null || caseNo.equals("") ||
    		orderNo == null || orderNo.equals("") || 
    		orderSeq == null || orderSeq.equals("") || 
    		startDttm == null || startDttm.equals("")){
    		return new TParm();
    	}
    	
    	/**
    	 * 先查询里面有没有绑定TOXIC_ID1=第一支麻精流水号,TOXIC_ID2=第二支麻精流水号,TOXIC_ID3=第三支麻精流水号
    	 */
    	String searchSql = " SELECT A.TOXIC_ID1,A.TOXIC_ID2,A.TOXIC_ID3 " +
    			           " FROM IND_CABDSPN A "+
    			           "  WHERE A.CASE_NO='"+caseNo+"' AND A.ORDER_NO='"+orderNo+"' "+
    			           "        AND A.ORDER_SEQ="+orderSeq+"  AND A.START_DTTM='"+startDttm+"' ";
    	TParm searchParm = new TParm(TJDODBTool.getInstance().select(searchSql));
    	
    	if(searchParm.getCount() < 0 ){
    		result.setErrCode(-1);
    		result.setErrText("护士审核数据没有传到物联网");
    		return result ;
    	}
    	
    	String filed = "";
    	//判断哪个字段没有值 ,需要绑定的辽段 
    	if(searchParm.getCount() > 0 ){
    		String toxidId1 = searchParm.getRow(0).getValue("TOXIC_ID1");
    		String toxidId2 = searchParm.getRow(0).getValue("TOXIC_ID2");
    		String toxidId3 = searchParm.getRow(0).getValue("TOXIC_ID3");
    		if(toxidId1 == null || toxidId1.equals("")  || toxidId1.equals("null")){
    			filed = "TOXIC_ID1";
    		}else if((toxidId2 == null || toxidId2.equals("") || toxidId2.equals("null")) && filed.equals("")){
    			filed = "TOXIC_ID2" ;
    		}else if( (toxidId3 == null || toxidId3.equals("") || toxidId3.equals("null")) && filed.equals("")  ){
    			filed = "TOXIC_ID3" ;
    		}
    	}
    	
    	if(filed.equals("")){
    		result.setErrCode(-1);
    		result.setErrText("麻精绑定病人已绑定三支麻精不能再绑定");
    		return result ;
    		
    	}

    	filed = " A."+filed ;
    	String sql = " UPDATE IND_CABDSPN A SET " + filed+"='"+toxicId+"'  "+
    			     " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.ORDER_NO='"+parm.getValue("ORDER_NO")+"' "+
    			     "          AND A.ORDER_SEQ="+parm.getValue("ORDER_SEQ")+"  AND A.START_DTTM='"+parm.getValue("START_DTTM")+"' ";
    	//System.out.println("onUpdateIndCabdspn=====:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql, conn));
    }
    
    
    public TParm onUpdateIndCabdspnNum(TParm parm,TConnection conn ){
    	String caseNo = parm.getValue("CASE_NO") ;
    	String  orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String startDttm = parm.getValue("START_DTTM");
    	
    	String  toxicId =  parm.getValue("TOXIC_ID");
    	String takemedUser = parm.getValue("TAKEMED_USER");
    	
    	TParm result = new TParm() ;
    	
    	if(caseNo == null || caseNo.equals("") ||
    		orderNo == null || orderNo.equals("") || 
    		orderSeq == null || orderSeq.equals("") || 
    		startDttm == null || startDttm.equals("")){
    		return new TParm();
    	}
    	
    	/**
    	 * 先查询里面有没有绑定TOXIC_ID1=第一支麻精流水号,TOXIC_ID2=第二支麻精流水号,TOXIC_ID3=第三支麻精流水号
    	 */
    	String searchSql = " SELECT A.TOXIC_ID1,A.TOXIC_ID2,A.TOXIC_ID3 " +
    			           " FROM IND_CABDSPN A "+
    			           "  WHERE A.CASE_NO='"+caseNo+"' AND A.ORDER_NO='"+orderNo+"' "+
    			           "        AND A.ORDER_SEQ="+orderSeq+"  AND A.START_DTTM='"+startDttm+"' ";
    	TParm searchParm = new TParm(TJDODBTool.getInstance().select(searchSql));
    	
    	
    	String filed = "";
    	//判断哪个字段没有值 ,需要绑定的辽段 
    	if(searchParm.getCount() > 0 ){
    		String toxidId1 = searchParm.getRow(0).getValue("TOXIC_ID1");
    		String toxidId2 = searchParm.getRow(0).getValue("TOXIC_ID2");
    		String toxidId3 = searchParm.getRow(0).getValue("TOXIC_ID3");
    		if(toxidId1 == null || toxidId1.equals("")){
    			filed = "TOXIC_ID1";
    		}else if((toxidId2 == null || toxidId2.equals("")) && filed.equals("")){
    			filed = "TOXIC_ID2" ;
    		}else if( (toxidId3 == null || toxidId3.equals("")) && filed.equals("")  ){
    			filed = "TOXIC_ID3" ;
    		}
    	}
    	
    	if(filed.equals("")){
    		result.setErrCode(-1);
    		result.setErrText("都有值不能绑定");
    		return result ;
    	}

    	filed = " A."+filed ;
    	String sql = " UPDATE IND_CABDSPN A SET " + filed+"='"+toxicId+"' , A.ACUM_OUTBOUND_QTY= NVL(A.ACUM_OUTBOUND_QTY,0) + 1, A.TAKEMED_USER='"+takemedUser+"' "+
    			     " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.ORDER_NO='"+parm.getValue("ORDER_NO")+"' "+
    			     "          AND A.ORDER_SEQ="+parm.getValue("ORDER_SEQ")+"  AND A.START_DTTM='"+parm.getValue("START_DTTM")+"' ";
    	//System.out.println("onUpdateIndCabdspnNum=====:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql, conn));
    }
    
}
