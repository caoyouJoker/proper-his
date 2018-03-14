package jdo.ins;

import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: 医保工具类</p>
 *
 * <p>Description: 医保工具类</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.09.20
 * @version 1.0
 */
public class INSTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INSTool instanceObject;
    /**
     * 得到实例
     * @return INSTool
     */
    public static INSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSTool() {
        onInit();
    }

    /**
     * 医保挂号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveREG(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("医保挂号" + parm);
        String saveREG =
            " INSERT INTO INS_ORDM " +
            "        (HOSP_AREA,YEAR_MON,CASE_NO,OPT_USER,OPT_DATE,OPT_TERM) " +
            " VALUES ('" + "HIS" + "','" + "201109" + "','" + "20110920000001" +
            "','" + "tiis" + "',SYSDATE,'" + "192.168.1.106" + "' )";
        //System.out.println("saveREG" + saveREG);
        result = new TParm(TJDODBTool.getInstance().update(saveREG,
            connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 医保退挂
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onReturnREG(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("医保退挂" + parm);
        String returnREG =
            " INSERT INTO INS_ORDM " +
            "             (HOSP_AREA,YEAR_MON,CASE_NO = '" + "HIS" + "'," +
            "        YEAR_MON = '" + "201109" + "'," +
            "        CASE_NO = '" + "20110920000001" + "'," +
            "        OWN_RATE = '" + "201109" + "'," +
            "        DECREASE_RATE = 0.1," +
            "        REALOWN_RATE = '" + "201109" + "'," +
            "        INSOWN_RATE = '" + "201109" + "'," +
            "        ";
//        System.out.println("returnREG"+returnREG);
        result = new TParm(TJDODBTool.getInstance().update(returnREG,
            connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 医保标记位开关
     * 门诊医保运行状态
     * @param parm ActionParm HOSP_AREA
     * @return TParm NHI_DEBUG,REG_NHI_ONLINE,OPB_NHI_ONLINE<BR>
     * NHI_DEBUG <BR>
     * 0 标准：正常运行<BR>
     * 1 调试：输出各个医保动态函数调用的参数和结果到日志文件<BR>
     * REG_NHI_ONLINE<BR>
     * 0 分时链接：在挂号保存同时，只是将挂号数据写入门诊医保明细表，离院收据业务中处理医保业务<BR>
     * 1 实时链接：在挂号保存同时，不仅将挂号数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     * OPB_NHI_ONLINE<BR>
     * 0 分时链接：在收费保存同时，只是将费用数据写入门诊医保明细表<BR>
     * 1 实时链接：在收费保存同时，不仅将费用数据写入门诊医保明细表，还要调用门诊实时函数<BR>
     */
    public TParm getNhiFlg(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getNhiFlg(ActionParm)->Err:参数不能为NULL");
            return result;
        }
        if (parm.checkEmpty("HOSP_AREA", result))
            return result;
        String sql =
            " SELECT ACTIVE_FLG AS NHI_DEBUG,MAIN_FLG AS REG_NHI_ONLINE,MAIN_FLG AS OPB_NHI_ONLINE " +
            "   FROM SYS_REGION" +
            "  WHERE REGION_CODE='" + parm.getValue("HOSP_AREA") + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到医保基本参数
     * @param parm TParm
     * @return TParm
     */
    public TParm getSysParm(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getSysparm(ActionParm)->Err:参数不能为NULL");
            return result;
        }
        if (parm.checkEmpty("HOSP_AREA", result))
            return result;
        String sql =
            "SELECT CITY,SEPARATOR,NEWLINE,FINISH" +
            "  FROM INS_SYSPARM" +
            " WHERE REGION_CODE='" + parm.getValue("HOSP_AREA") + "'";
        //System.out.println("得到医保基本参数"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 读取医保接口函数信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getIOParm(TParm parm) {
        //System.out.println("sql入参"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getIOParm(ActionParm)->Err:参数不能为NULL");
            return result;
        }
        if (parm.checkEmpty("CITY,PIPELINE,PLOT_TYPE", result))
            return result;
        String sql =
            "SELECT ROW_COUNT" +
            "  FROM INS_IO_PARM" +
            " WHERE CITY='" + parm.getValue("CITY") + "'" +
            "   AND PIPELINE = '" + parm.getValue("PIPELINE") + "'" +
            "   AND PLOT_TYPE = '" + parm.getValue("PLOT_TYPE") + "'";
        //System.out.println("读取医保接口函数信息"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 读取医保数据参数格式
     * @param parm TParm CITY 城市,PIPELINE 端口,PLOT_TYPE 类型,IN_OUT IO方向
     * @return TParm
     */
    public TParm getIOData(TParm parm) {
        //System.out.println("读取医保数据参数格式parm"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getIOData(ActionParm)->Err:参数不能为NULL");
            return result;
        }
        if (parm.checkEmpty("CITY,PIPELINE,PLOT_TYPE,IN_OUT", parm))
            return result;
        String sql =
            "SELECT ID,COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION," +
            "       SCALE,NEED,DEFAULT_VALUE,FORMAT" +
            "  FROM INS_IO" +
            " WHERE CITY='" + parm.getValue("CITY") + "'" +
            "   AND PIPELINE = '" + parm.getValue("PIPELINE") + "'" +
            "   AND PLOT_TYPE = '" + parm.getValue("PLOT_TYPE") + "'" +
            "   AND IN_OUT = '" + parm.getValue("IN_OUT") + "'" +
            " ORDER BY ID";
        //System.out.println("sql"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * 得到医保参数
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsParm(TParm parm) {
        TParm result = new TParm();
        parm.setData("IN_OUT", "IN");
        result.setData("IN_COLUMN", this.getIOData(parm)); 
        parm.setData("IN_OUT", "OUT");
        result.setData("OUT_COLUMN", this.getIOData(parm));
        return result;

    }
    
    /**
     * 
     * 读取SYS_REGION表中的信息.
     * @param regionCode
     * @return TParm
     */
    public TParm getSysRegionInfo(TParm parm){
    	TParm result = new TParm();
    	String sql = "Select REGION_CHN_ABN, NHI_NO from SYS_REGION " +
                " Where REGION_CODE = '" + parm.getValue("REGION_CODE") + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }  
        return result ;
    }

	/**
	 * 获得病患历次医保数据
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryPatHistoryInfo(TParm parm) {//add by wanglong 20120921
		TParm result = new TParm();
		String mrNo = parm.getValue("MR_NO") ;
		String startDate = parm.getValue("START_DATE").split(" ")[0];
		String endDate = parm.getValue("END_DATE").split(" ")[0];
    	String sql =
           "SELECT A.MR_NO, A.PAT_NAME, B.CONFIRM_NO, B.INS_DATE,"+
    	         "(CASE WHEN B.UNREIM_AMT IS NULL THEN 0 ELSE B.UNREIM_AMT END) AS UNREIM_AMT,"+
    	         "(CASE WHEN B.OTOT_PAY_AMT IS NULL THEN 0 ELSE B.OTOT_PAY_AMT END) AS OTOT_PAY_AMT,"+
    	         "(CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT "+
    	    "FROM SYS_PATINFO A, INS_OPD B "+
    	   "WHERE     A.MR_NO = '" + mrNo + "' "+
    	         "AND A.MR_NO = B.MR_NO "+
    	         "AND B.INS_DATE BETWEEN TO_DATE ('" + startDate + " 00:00:00', 'YYYY/MM/DD hh24:mi:ss') "+
    	                            "AND TO_DATE ('" + endDate + " 23:59:59', 'YYYY/MM/DD hh24:mi:ss') "+
    	"ORDER BY B.INS_DATE";
        result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
}
