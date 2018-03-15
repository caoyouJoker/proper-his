package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 容器交易</p>
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
public class SPCToxicTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static SPCToxicTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SPCToxicTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCToxicTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCToxicTool() {
		onInit();
	}
	
	
	public TParm onQuery(TParm parm){
		
		String  dispenseNo  = parm.getValue("DISPENSE_NO");
		String  seq  = parm.getValue("SEQ_NO");
		String orderCode = parm.getValue("ORDER_CODE");
		if( (dispenseNo == null || dispenseNo.equals("")) || (seq== null || seq.equals(""))){
			return new TParm();
		}
		String sql = " SELECT ROWNUM ROW_NUM, B.CONTAINER_DESC,B.CONTAINER_ID,C.ORDER_CODE,C.BATCH_NO, "+
		 			" 		  C.VALID_DATE,C.VERIFYIN_PRICE,C.BATCH_SEQ,C.TOXIC_ID,D.ORDER_DESC "+
		 			" FROM IND_TOXICD A,IND_CONTAINERM B,IND_CONTAINERD C,PHA_BASE D "+
		 			" WHERE  A.CONTAINER_ID = B.CONTAINER_ID  AND " +
		 			"        B.CONTAINER_ID = C.CONTAINER_ID  AND " +
		 			"		 A.TOXIC_ID = C.TOXIC_ID  AND " +
		 			"        D.ORDER_CODE=C.ORDER_CODE  AND "+
		 			"        A.DISPENSE_NO='"+dispenseNo+"' AND " +
		 			"        A.DISPENSE_SEQ_NO="+seq+" AND " +
		 			"        A.ORDER_CODE ='"+orderCode+"' ";
		 
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	public TParm onQueryBy(TParm parm){
		
		String  dispenseNo  = parm.getValue("DISPENSE_NO");
		String  seq  = parm.getValue("SEQ_NO");
		String orderCode = parm.getValue("ORDER_CODE");
		if( (dispenseNo == null || dispenseNo.equals("")) ){
			return new TParm();
		}
		String sql = " SELECT ROWNUM ROW_NUM, B.CONTAINER_DESC,B.CONTAINER_ID,C.ORDER_CODE,C.BATCH_NO, "+
		 			" 		  C.VALID_DATE,C.VERIFYIN_PRICE,C.BATCH_SEQ,C.TOXIC_ID,D.ORDER_DESC ,D.SPECIFICATION  "+
		 			" FROM IND_TOXICD A,IND_CONTAINERM B,IND_CONTAINERD C,PHA_BASE D "+
		 			" WHERE  A.CONTAINER_ID = B.CONTAINER_ID  AND " +
		 			"        B.CONTAINER_ID = C.CONTAINER_ID  AND " +
		 			"		 A.TOXIC_ID = C.TOXIC_ID  AND " +
		 			"        D.ORDER_CODE=C.ORDER_CODE  AND "+
		 			"        A.DISPENSE_NO='"+dispenseNo+"'  ";
		if(seq != null && !seq.equals("")){
			sql += "    AND     A.DISPENSE_SEQ_NO="+seq+"  " ;
		}
		if(orderCode != null && !orderCode.equals("")){
			sql += "    AND  A.ORDER_CODE ='"+orderCode+"' ";
		}
		 		 
		 
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询主表里是否已保存
	 * @param parm
	 * @return
	 */
	public TParm onQueryToxicM(TParm parm ){
		String  dispenseNo  = parm.getValue("DISPENSE_NO");
		String  seq  = parm.getValue("DISPENSE_SEQ_NO");
		String  containerId = parm.getValue("CONTAINER_ID");
		if( (dispenseNo == null || dispenseNo.equals(""))|| (seq==null || seq.equals("")) || (containerId==null || containerId.equals(""))){
			return new TParm();
		}
		
		String  sql = "SELECT  CONTAINER_ID FROM IND_TOXICM A WHERE A.DISPENSE_NO='"+dispenseNo+"' AND A.DISPENSE_SEQ_NO='"+seq+"' AND A.CONTAINER_ID='"+containerId+"' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
		
	}
	
	/**
	 * 保存主表
	 * @param parm
	 * @return
	 */
	public TParm onSaveToxicM(TParm parm,TConnection conn){
		
		String appOrgCode = parm.getValue("APP_ORG_CODE");
		String sql = " INSERT INTO IND_TOXICM  " +
					 " ( DISPENSE_NO,DISPENSE_SEQ_NO,CONTAINER_ID,CONTAINER_DESC,IS_BOXED," +
					 "	 BOXED_USER,BOXED_DATE,BOX_ESL_ID,CABINET_ID,OPT_USER," +
					 "   OPT_DATE,OPT_TERM ,ORDER_CODE,APP_ORG_CODE " +
					 "  ) "+
					 " VALUES "+
					 " ('"+parm.getValue("DISPENSE_NO")+"','"+parm.getValue("DISPENSE_SEQ_NO")+"','"+parm.getValue("CONTAINER_ID")+"','"+parm.getValue("CONTAINER_DESC")+"','"+parm.getValue("IS_BOXED")+"', "+
					 "  '"+parm.getValue("BOXED_USER")+"',SYSDATE,'"+parm.getValue("BOX_ESL_ID")+"','"+parm.getValue("CABINET_ID")+"','"+parm.getValue("OPT_USER")+"',"+
					 "  SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("ORDER_CODE")+"' , '"+appOrgCode+"'  "+
					 "  )";
		//System.out.println("sql----------------:"+sql);
		return new TParm(TJDODBTool.getInstance().update(sql,conn));
	}
	
	/**
	 * 保存明细
	 * @param parm
	 * @return
	 */
	public TParm onSaveToxicD(TParm parm,TConnection conn){
		
		String validDate = parm.getValue("VALID_DATE");
		if(validDate == null || validDate.equals("")){
			validDate = "";
		}else{
			
			if(validDate != null && !validDate.equals("") ){
				validDate = validDate.substring(0,validDate.length()-2);
			}
			validDate = " TO_DATE('"+validDate+"','YYYY-MM-DD HH24:MI:SS')";
		}
		String cabinetId = parm.getValue("CABINET_ID");
		if(cabinetId == null || cabinetId.equals("") || cabinetId.equals("null")){
			cabinetId = "";
		}
		
		String sql = " INSERT INTO IND_TOXICD  " +
					 " ( DISPENSE_NO, DISPENSE_SEQ_NO, CONTAINER_ID, TOXIC_ID, ORDER_CODE, " +
					 "	 BATCH_NO, VALID_DATE, VERIFYIN_PRICE, BATCH_SEQ,  " +
					 "	 OPT_USER, OPT_DATE, OPT_TERM,UNIT_CODE ,CABINET_ID " +
					 "  ) "+
					 " VALUES "+
					 " ('"+parm.getValue("DISPENSE_NO")+"','"+parm.getValue("DISPENSE_SEQ_NO")+"','"+parm.getValue("CONTAINER_ID")+"','"+parm.getValue("TOXIC_ID")+"','"+parm.getValue("ORDER_CODE")+"', "+
					 "  '"+parm.getValue("BATCH_NO")+"'," +validDate+ ",'"+parm.getValue("VERIFYIN_PRICE")+"','"+parm.getValue("BATCH_SEQ")+"',"+
					 "  '"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"', '"+parm.getValue("UNIT_CODE")+"', '" +cabinetId+"' "+
					 "  )";
		return new TParm(TJDODBTool.getInstance().update(sql,conn));
	}
	
	public TParm onQueryByValidDate(TParm parm ){
		
		String cabinetId = parm.getValue("CABINET_ID");
		String orderCode = parm.getValue("ORDER_CODE");
		if(StringUtil.isNullString(cabinetId)|| StringUtil.isNullString(orderCode)){
			return new TParm();
		}
		
		String sql = " SELECT MIN(A.VALID_DATE) VALID_DATE "+
		             " FROM IND_CONTAINERD A "+
		             " WHERE A.CABINET_ID='"+cabinetId+"' AND A.ORDER_CODE='"+orderCode+"'  ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	public TParm onQueryDByCount(TParm parm){
		String dispenseNO = parm.getValue("DISPENSE_NO");
    	String seqNO = parm.getValue("DISPENSE_SEQ_NO");
    	String containerId = parm.getValue("CONTAINER_ID");
    	if( (dispenseNO== null || dispenseNO.equals("")) || (containerId == null || containerId.equals(""))){
    		return new TParm();
    	}
    	
    	String sql = " SELECT COUNT(A.CONTAINER_ID) NUM  FROM IND_TOXICD A " +
    	 			 " WHERE A.CONTAINER_ID='"+containerId+"' " +
    	 			 "       AND A.DISPENSE_NO='"+dispenseNO+"' " +
    	 			 "       AND A.DISPENSE_SEQ_NO='"+seqNO+"'  ";
    	//System.out.println("onQueryDByCount=========:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    	
	}
	

  public TParm onDeleteToxic(TParm parm){
    TParm result = new TParm();
    String dispenseNo = parm.getValue("DISPENSE_NO");
    if ((dispenseNo == null) || (dispenseNo.equals(""))) {
      return new TParm();
    }

    String sql = " DELETE FROM IND_TOXICM A WHERE  A.DISPENSE_NO='" + dispenseNo + "' ";
    result = new TParm(TJDODBTool.getInstance().update(sql));
    if (result.getErrCode() < 0) {
      return result;
    }

    String sql1 = " DELETE FROM IND_TOXICD A WHERE A.DISPENSE_NO='" + dispenseNo + "' ";
    result = new TParm(TJDODBTool.getInstance().update(sql1));
    return result;
  }
}
