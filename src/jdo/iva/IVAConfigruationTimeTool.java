package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:静脉输液配置时间设定工具类
 * </p>
 * 
 * <p>
 * Description:静脉输液配置时间设定工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2013.6.4
 * @version 1.0
 */
public class IVAConfigruationTimeTool extends TJDOTool {

	/** 单例模式 */
	private static IVAConfigruationTimeTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static IVAConfigruationTimeTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVAConfigruationTimeTool();
		}
		return instanceObject;
	}

	/**
	 * 保存
	 * 
	 * @return
	 */
	public TParm insertInfo(TParm parm) {
		String sql = "INSERT INTO ODI_BATCHTIME(BATCH_CODE,BATCH_CHN_DESC,"
				+ "IVA_TIME,DCCHECK_TIME,TREAT_START_TIME,TREAT_END_TIME,"
				+ "STAT_FLG,FIRST_FLG,UD_FLG,OP_FLG,PN_FLG,"
				+ "REMARK,OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ parm.getValue("BATCH_CODE")
				+ "',"
				+ "'"
				+ parm.getValue("BATCH_CHN_DESC")
				+ "','"
				+ parm.getValue("IVA_TIME")
				+ "',"
				+ "'"
				+ parm.getValue("DCCHECK_TIME")
				+ "','"
				+ parm.getValue("TREAT_START_TIME")
				+ "',"
				+ "'"
				+ parm.getValue("TREAT_END_TIME")
				+ "','"
				+ parm.getValue("STAT_FLG")
				+ "','"
				+ parm.getValue("FIRST_FLG")
				+ "','"
				+ parm.getValue("UD_FLG")
				+ "','"
				+ parm.getValue("OP_FLG")
				+ "','"
				+ parm.getValue("PN_FLG")
				+ "','"
				+ parm.getValue("REMARK")
				+ "',"
				+ "'"
				+ parm.getValue("OPT_USER")
				+ "',TO_DATE('"
				+ parm.getValue("OPT_DATE")
				+ "','yyyy-mm-dd hh24:mi:ss'),"
				+ "'" + parm.getValue("OPT_TERM") + "')";

//		System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	public TParm updateInfo(TParm parm) {
		String sql = "UPDATE ODI_BATCHTIME SET BATCH_CHN_DESC='"
				+ parm.getValue("BATCH_CHN_DESC") 
				+ "',IVA_TIME='"
				+ parm.getValue("IVA_TIME") 
				+ "',DCCHECK_TIME='"
				+ parm.getValue("DCCHECK_TIME") 
				+ "' ,TREAT_START_TIME='"
				+ parm.getValue("TREAT_START_TIME") 
				+ "',TREAT_END_TIME='"
				+ parm.getValue("TREAT_END_TIME")
				+ "',STAT_FLG='"
				+ parm.getValue("STAT_FLG")
				+ "',FIRST_FLG='"
				+ parm.getValue("FIRST_FLG")
				+ "',UD_FLG='"
				+ parm.getValue("UD_FLG")
				+ "',OP_FLG='"
				+ parm.getValue("OP_FLG")
				+ "',PN_FLG='"
				+ parm.getValue("PN_FLG")
				+ "',REMARK='"
				+ parm.getValue("REMARK") 
				+ "', OPT_USER='"
				+ parm.getValue("OPT_USER")
				+ "',OPT_DATE=SYSDATE, "
				+ " OPT_TERM='"
				+ parm.getValue("OPT_TERM")
				+ "' WHERE BATCH_CODE='"
				+ parm.getValue("BATCH_CODE") + "'";
//		System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	public TParm deleteInfo(TParm parm) {

		String sql = "DELETE FROM ODI_BATCHTIME WHERE BATCH_CODE='"
				+ parm.getValue("BATCH_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 查询
	 * 
	 * @return
	 */
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT BATCH_CODE,BATCH_CHN_DESC,IVA_TIME,DCCHECK_TIME,"
				+ "TREAT_START_TIME,TREAT_END_TIME,REMARK,"
				+ "STAT_FLG,FIRST_FLG,UD_FLG,OP_FLG,PN_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM FROM ODI_BATCHTIME";
		if(parm.getValue("BATCH_CODE") !=null && !"".equals(parm.getValue("BATCH_CODE"))){
			sql+=	" WHERE BATCH_CODE='"
				+ parm.getValue("BATCH_CODE") + "'";
		}
//				System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询是首批还是尾批
	 * @return
	 */
	public TParm queryFirstOrLastF(){
		String sql = "SELECT IVA_TIME FROM " 
				+"(SELECT IVA_TIME FROM ODI_BATCHTIME ORDER BY IVA_TIME) WHERE ROWNUM=1";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询是首批还是尾批
	 * @return
	 */
	public TParm queryFirstOrLastL(){
		String sql = "SELECT IVA_TIME FROM " 
				+"(SELECT IVA_TIME FROM ODI_BATCHTIME ORDER BY IVA_TIME DESC) WHERE ROWNUM=1";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
