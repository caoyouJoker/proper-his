package jdo.spc;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.ElectronicTagUtil;

/**
 * 物联网tool 
 * @author liyh
 *
 */
public class SPCTool extends TJDOTool {

	private String code;

	/**
	 * 实例
	 */
	public static SPCTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SPCTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCTool() {
		onInit();
	}
	
	/**
	 * 物联网药品编码转为HIS
	 */
	public String getHisOrderCodeBySpcOrderCode(String orderCode,String region){
		String hisOrderCode = "";
//		System.out.println("-物联网药品编码转为HIS------sql:"+SPCSQL.getSpcOrderCodeByHisOrderCode(orderCode,region,""));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getSpcOrderCodeByHisOrderCode(orderCode,region,"")));
		if(null != parm && parm.getCount() > 0){
			 hisOrderCode = parm.getValue("HIS_ORDER_CODE", 0);
		}
		return hisOrderCode;
	}
	
	/**
	 * 得到药品对应的包药机部门编码
	 * @param orderCode
	 * @return
	 */
	public TParm getOrgCodeOfAtc(String orgCode){
//		System.out.println("-----------SPCSQL.getOrgCodeOfAtc(orderCode):"+SPCSQL.getOrgCodeOfAtc(orgCode));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgCodeOfAtc(orgCode)));
		return parm;
		
	}

	/**
	 * 查询当前库存更新电子标签
	 * @param orgCode
	 * @param orderCode
	 * @param lightNum
	 */
	public void sendEleTag(String orgCode,String orderCode,int lightNum){
		//查询当前库存
//		System.out.println("----------查询当前库存 sql:"+SPCSQL.getIndStockQtyAndEleTag(orgCode, orderCode));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getIndStockQtyAndEleTag(orgCode, orderCode)));
//		System.out.println("-------parm:"+parm);
		if(null != parm){
			//更新电子标签
			ElectronicTagUtil.getInstance().sendEleTag(parm.getValue("ELETAG_CODE", 0), parm.getValue("ORDER_DESC", 0), 
					parm.getValue("SPECIFICATION", 0),  parm.getValue("STOCK_QTY", 0), lightNum);	
		}
	}

    /**
     * 门急诊配药-修改执行科室-OPD_ORDER
     * @param orderCode
     * @return
     */
    public TParm  updateExecDeptCode(String caseNo,String rxNo,String seqNo,String orgCode){
    	String sql = SPCSQL.updateExecDeptCode(caseNo,rxNo,seqNo,orgCode);
//		System.out.println("-----------updateExecDeptCode(orderCode):"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		return parm;
		
	}
    
    /**
     * 得到最大批次-BATCH_SEQ
     * @param orderCode
     * @return
     */
    public String   getMaxBatchSeq(String orgCode,String orderCode,String validDate,String price,String batchNo,String supCode ){
    	String sql = SPCSQL.getMaxBatchSeq(orgCode,orderCode,validDate,price,batchNo,supCode);
//		System.out.println("-----------getMaxBatchSeq(orderCode):"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("", 0);
	}    
	/**
	 * 查询当前库存
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static boolean checkStockQty(TParm parm){
		//默认值true 库存量足
		boolean flg = true;
	    String orgCode = parm.getValue("ORG_CODE");
	    String orderCode = parm.getValue("ORDER_CODE");
	    double dosageQty = parm.getDouble("DOSAGE_QTY");
	    //查询当前库存
//	    System.out.println("--------checkStockQty.sql:"+SPCSQL.getStockQtyByOrgCodeAndOrderCode(orgCode, orderCode));
	    TParm result = new TParm(TJDODBTool.getInstance().select(SPCSQL.getStockQtyByOrgCodeAndOrderCode(orgCode, orderCode)));
//	    System.out.println("----checkStockQty----dosageQty"+dosageQty+",查询当前库存----result:"+result);
	    double stockQty = result.getDouble("STOCK_QTY", 0);
	    if(stockQty < dosageQty){//配药量大于库存量时 返回值 false  库存量不足
	    	flg = false;
	    }
	    return flg;
	}
	
	/**
	 * 保存盘点量
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static void updateQtyCheck(double modiQty,double acturlCheckQty,String orgCode,String frozenDate,String orderCode,
			String batchSeq,String optUser,String optTerm){
		String sql = SPCSQL.updateQtyCheck(modiQty, acturlCheckQty, optUser, orgCode, frozenDate, orderCode, batchSeq, optUser, optTerm);
//		System.out.println("------sql: "+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
	}	
    
}
