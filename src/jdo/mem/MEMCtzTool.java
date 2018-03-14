package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: 会员身份管理Tool</p>
 *
 * <p>Description: 会员身份管理Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author duzhw 20131224
 * @version 4.5
 */
public class MEMCtzTool extends TJDOTool  {
	
	/**
	 * 实例
	 */
	private static MEMCtzTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return SYSRegionTool
	 */
	public static MEMCtzTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MEMCtzTool();
		return instanceObject;
	}
	
	public MEMCtzTool() {
		setModuleName("mem\\MEMCtzToolModule.x");
		onInit();
	}
	/**
	 * 保存操作
	 */
//	public TParm onSave(TParm parm) {
//		TConnection conn = getConnection();
//		TParm result = new TParm();
//		try {
//			//修改
//			
//			//删除
//			
//			// 新增信息
//			TParm insertParm = parm.getParm("INSERTDATA");
//			System.out.println("Tool中insertParm="+insertParm);
//			for (int i = 0; i < insertParm.getCount("ORDER_DESC"); i++) {
//				TParm inparm=insertParm.getRow(i);
//				String insertsql=this.getSql(inparm);
//				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
//				if (result.getErrCode() < 0) {
//					conn.rollback();
//					conn.close();
//					return result;
//				}
//			}
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			err(e.getMessage());
//		}
//		return result;
//	}
	/**
	 * 保存新增身份设置
	 */
	public TParm onSaveMemCtzInfoData(TParm parm) {
		
		TParm result = update("onSaveMemCtzInfoData",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 保存修改身份设置
	 */
	public TParm updateMemCtzInfoData(TParm parm){
		TParm result = update("updateMemCtzInfoData",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 校验身份代码
	 */
	public TParm checkCtzCode(TParm parm){
		TParm result = query("checkCtzCode",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 校验身份表是否可以删除-项目分类折扣（SYS_CHARGE_DETAIL）
	 */
	public TParm checkDelCtz(TParm parm){
		TParm result = query("queryCtzDetail",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 校验身份表是否可以删除-项目明细（SYS_CTZ_FEE）
	 */
	public TParm checkFeeCtzCode(TParm parm){
		TParm result = query("queryCtzFee",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 删除操作-身份表数据
	 */
	public TParm delCtzCode(TParm parm){
		TParm result = update("delCtzCode",parm);
        
        if(result.getErrCode() < 0)
        {  
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 删除操作-身份表明细数据
	 */
	public TParm delCtzDetailCode(TParm parm){   
		TParm result = update("delCtzDetailCode",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	

	/**
	 * 修改折扣率
	 * @param parm
	 * @return
	 */
	public TParm updateDiscountRate(TParm parm) {
		TParm result = update("updateDiscountRate",parm);
        
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}

	/**
	 * 插入语句
	 * sql
	 * @return
	 */
//	private String getSql(TParm parm) {
//		String sql = "INSERT INTO SYS_CTZ_FEE(CTZ_CODE,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,OPT_DATE," +
//		"OPT_USER,OPT_TERM) VALUES('"+parm.getValue("CTZ_CODE")+"'," +
//				"'"+parm.getValue("ORDER_CODE")+"'," +
//				"'"+parm.getValue("ORDER_DESC")+"'," +
//				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
//				"sysdate," +
//				"'"+parm.getValue("OPT_USER")+"'," +
//				"'"+parm.getValue("OPT_TERM")+"')";
//		System.out.println("新增数据sql="+sql);
//		return sql;
//	}
}
