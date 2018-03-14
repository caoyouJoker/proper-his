package action.mem;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 会员身份设置
 * </p>
 * 
 * <p>
 * Description: 会员身份设置
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author duzhw 2013.12.26
 * @version 4.5
 */
public class MEMCtzAction extends TAction{
	
	/**
	 * 执行保存事物
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//修改--分类主表修改数据
			TParm mainUpdateParm = parm.getParm("MAINUPDATEDATA");
			//System.out.println("Tool中mainUpdateParm="+mainUpdateParm);
			
			for (int n = 0; n < mainUpdateParm.getCount("CTZ_CODE"); n++) {
				TParm pdateparm=mainUpdateParm.getRow(n);
				String updatesql=this.getMainUpateSql(pdateparm);
				result = new TParm(TJDODBTool.getInstance().update(updatesql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			//修改--细项表修改数据
			TParm updateParm = parm.getParm("UPDATEDATA");
			//System.out.println("Tool中updateParm="+updateParm);
			for (int m = 0; m < updateParm.getCount("ORDER_CODE"); m++) {
				TParm pdateparm=updateParm.getRow(m);
				String updatesql=this.getUpateSql(pdateparm);
				result = new TParm(TJDODBTool.getInstance().update(updatesql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//删除
			TParm delParm = parm.getParm("DELDATA");
			//System.out.println("Tool中delParm="+delParm);
			for (int k = 0; k < delParm.getCount("CTZ_CODE"); k++) {
				TParm delparm=delParm.getRow(k);
				String delsql=this.getDelSql(delparm);
				result = new TParm(TJDODBTool.getInstance().update(delsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// 新增信息
			TParm insertParm = parm.getParm("INSERTDATA");
			//System.out.println("Tool中insertParm="+insertParm);
			for (int i = 0; i < insertParm.getCount("ORDER_DESC"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getSql(inparm);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 插入语句
	 * sql
	 * @return
	 
	private String getSql(TParm parm) {
		String sql = "INSERT INTO SYS_CTZ_FEE(CTZ_CODE,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,OPT_DATE," +
		"OPT_USER,OPT_TERM) VALUES('"+parm.getValue("CTZ_CODE")+"'," +
				"'"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"')";
		System.out.println("新增数据sql="+sql);
		return sql;
	} */
	
	/**
	 * 插入语句-项目明细表SYS_CTZ_ORDER_DETAIL
	 * sql
	 * @return
	 */
	private String getSql(TParm parm) {  
		String sql = "INSERT INTO SYS_CTZ_ORDER_DETAIL(CTZ_CODE,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,OPT_DATE," +
		"OPT_USER,OPT_TERM,SETMAIN_FLG,ORDERSET_CODE,HIDE_FLG,UNIT_PRICE) VALUES('"+parm.getValue("CTZ_CODE")+"'," +
				"'"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +  
				"sysdate," +                                         
				"'"+parm.getValue("OPT_USER")+"'," +  
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("SETMAIN_FLG")+"'," +  
				"'"+parm.getValue("ORDERSET_CODE")+"'," +
				"'"+parm.getValue("HIDE_FLG")+"'," +
				"'"+parm.getValue("UNIT_PRICE")+"' )";
		//System.out.println("新增数据sql="+sql);
		return sql;
	}  
	
	/**
	 * 删除sql
	 
	private String getDelSql(TParm parm) {
		String sql = "DELETE FROM SYS_CTZ_FEE WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' " +
				" AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("删除sql="+sql);
		return sql;
	}*/
	
	/**
	 * 删除sql--SYS_CTZ_ORDER_DETAIL
	 */
	private String getDelSql(TParm parm) {
		String sql = "DELETE FROM SYS_CTZ_ORDER_DETAIL WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' " +
				" AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("删除sql="+sql);
		return sql;
	}
	
	/**
	 * 修改sql-明细表
	 
	private String getUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CTZ_FEE SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
				" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
				" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("明细表修改sql="+sql);
		return sql;
	} */
	
	/**
	 * 修改sql-明细表 SYS_CTZ_ORDER_DETAIL
	 */
	private String getUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CTZ_ORDER_DETAIL SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
				" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
				" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("明细表修改sql="+sql);
		return sql;
	}
	
	/**
	 * 修改sql-分类主表
	 */
	private String getMainUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CHARGE_DETAIL SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
		" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
		" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND CHARGE_HOSP_CODE = '"+parm.getValue("CHARGE_HOSP_CODE")+"'";
		System.out.println("分类表修改sql="+sql);
		return sql;
	}
}
