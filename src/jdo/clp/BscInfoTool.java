package jdo.clp;

import java.text.*;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import java.util.Map;

/**
 * <p>Title: 数据库操作工具类</p>
 *
 * <p>Description: 临床路径标准设定</p>
 *
 * <p>Copyright: Copyright (c) Zhang jianguo 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class BscInfoTool
    extends TJDOTool {
    /**
     * 构造方法私有化，加载SQL语句配置文件
     */
    private BscInfoTool() {
        this.setModuleName("clp\\CLPBscInfoModule.x");
        onInit();
    }

    /**
     * 声明静态数据库工具类实例
     */
    private static BscInfoTool instance = null;
    /**
     * 声明日期格式化类对象
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * 声明静态方法，返回此类的实例
     * @return BscInfoTool
     */
    public static BscInfoTool getInstance() {
        if (instance == null) {
            instance = new BscInfoTool();
        }
        return instance;
    }

    /**
     * 查询全部临床路径类别
     * @return TParm
     */
    public TParm getAllBscInfos(TParm parm) {
        TParm result = new TParm();
        result = query("queryAll",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	/**
	 * 将医嘱添加到历史表,开始时间为当前时间，结束时间为99991231235959 
	 * lilig 20140825 add
	 *@param parm TParm
     * @return TParm 
	 */
    public TParm insertPackHistory(TParm parm, TConnection conn){
    	  TParm result = new TParm();
          result = update("insertPackHistory",parm, conn);
          // 判断错误值
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;	
    }
    /**将已经修订的历史表医嘱替换正在使用的临床路径项目医嘱
	 * lilig 20140825 add
	 *@param parm TParm
     * @return TParm 
	 */
    public TParm insertPack(TParm parm, TConnection conn){
    	  TParm result = new TParm();
          result = update("insertPack",parm, conn);
          // 判断错误值
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;	
    }
    /**
     * 模糊查询临床路径类别
     * @param parm TParm
     * @return TParm
     */
    public TParm getBscInfoList(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT A.*,CASE  WHEN  B.ICD_CHN_DESC IS NULL THEN '' ELSE B.ICD_CHN_DESC END  AS ICD_DESC , ");
        sqlBuf.append(" CASE WHEN C.ICD_CHN_DESC IS NULL THEN '' ELSE C.ICD_CHN_DESC END AS OPE_DESC ");
        sqlBuf.append(" FROM CLP_BSCINFO A,SYS_DIAGNOSIS B,SYS_DIAGNOSIS C  WHERE 1=1 ");
        sqlBuf.append(" AND A.ICD_CODE=B.ICD_CODE(+) AND A.OPE_ICD_CODE=C.ICD_CODE(+) ");
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND A.REGION_CODE = '" + parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_CODE LIKE '%" +
                          parm.getValue("CLNCPATH_CODE") + "%'");
        }
        if (parm.getValue("CLNCPATH_PYCODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_PYCODE").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_PYCODE LIKE '%" +
                          parm.getValue("CLNCPATH_PYCODE") + "%'");
        }
        if (parm.getValue("CLNCPATH_DESC") != null &&
            !"".equals(parm.getValue("CLNCPATH_DESC").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_DESC LIKE '%" +
                          parm.getValue("CLNCPATH_DESC") + "%'");
        }
        if (parm.getValue("DEPT_CODE") != null &&
            !"".equals(parm.getValue("DEPT_CODE").trim())) {
            sqlBuf.append(" AND A.DEPT_CODE = '" + parm.getValue("DEPT_CODE") +
                          "'");
        }
        if (parm.getValue("STAYHOSP_DAYS") != null &&
            !"".equals(parm.getValue("STAYHOSP_DAYS").trim())) {
            sqlBuf.append(" AND A.STAYHOSP_DAYS = '" +
                          parm.getValue("STAYHOSP_DAYS") + "'");
        }
        if (parm.getValue("AVERAGECOST") != null &&
            !"".equals(parm.getValue("AVERAGECOST").trim())) {
            sqlBuf.append(" AND A.AVERAGECOST >= '" + parm.getValue("AVERAGECOST") +
                          "'");
        }
        if (parm.getValue("VERSION") != null &&
            !"".equals(parm.getValue("VERSION").trim())) {
            sqlBuf.append(" AND A.VERSION >= '" + parm.getValue("VERSION") + "'");
        }
        if (parm.getValue("ACTIVE_FLG") != null &&
            !"".equals(parm.getValue("ACTIVE_FLG").trim())) {
            sqlBuf.append(" AND A.ACTIVE_FLG >= '" + parm.getValue("ACTIVE_FLG") +
                          "'");
        }
        sqlBuf.append(" ORDER BY A.SEQ ");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 精确查询临床路径类别
     * @param parm TParm
     * @return TParm
     */
    public TParm getBscInfoObject(TParm parm) {
        TParm result = new TParm();
        result = query("queryBscInfo", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * update
     *
     * @param parm TParm
     */
    public TParm update(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("updateBscInfo", parm,conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * insert
     *标准主表
     * @param parm TParm
     */
    public TParm insert(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("insertBscInfo", parm,conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * insert
     *标准历史表
     * @param parm TParm
     * liling 20140827 add
     */
    public TParm insertHis(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("insertBscInfoHis", parm,conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * delete
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteBscInfo", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * delete
     *将历史表中的当前使用的版本号数据结束时间修改成当前时间
     * @param parm TParm
     * @return TParm
     * liling 20140829 add 
     */
    public TParm updateBscHis(TParm parm, TConnection conn){
    	TParm result = new TParm();
        result = this.update("updateBscInfoHis", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除医嘱套餐历史表数据
     * @param parm
     * @param conn
     * @return
     * liling 20140829 add 
     */
    public TParm deletePackHis(TParm parm, TConnection conn){
    	TParm result = new TParm();
//    	System.out.println("bscinfotool delete++"+parm.getData());
    	for (int i = 0; i < parm.getCount("REGION_CODE"); i++) {
        result = this.update("deletePackHis", parm.getRow(i), conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        }
        return result;
    }
    /**
     * 启用
     * @param parm
     * @param conn
     * @return
     * liling 20140910 add 
     */
    public TParm onUse(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("useBscInfo", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * use
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm use(TParm parm) {
        TParm result = new TParm();
        result = this.update("useBscInfo", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * use
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm use(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("forbidBscInfo", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据频次和医嘱类型判断频次是否合法
     * @param parm TParm
     * @return TParm
     */
    public TParm checkFreq(TParm parm){
        TParm result = new TParm();
        result = this.query("checkFreq", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 是否存在对应临床路径
     * @param clpPathCode String
     * @return TParm
     */
    public TParm existBscinfo(TParm parm) {
        TParm result = new TParm();
        result = this.query("existBscinfo", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectDiagICDList(TParm parm){
        StringBuffer sqlbf=new StringBuffer();
        sqlbf.append("SELECT A.CLNCPATH_CODE ,A.ICD_TYPE_BEGIN AS diagnose_icd_type_begin ,A.ICD_CODE_BEGIN AS diagnose_icd_begin ");
        sqlbf.append(" ,A.ICD_TYPE_END AS diagnose_icd_type_end,A.ICD_CODE_END AS diagnose_icd_end , ");
        sqlbf.append(" B.ICD_CHN_DESC AS diagnose_desc_begin ,C.ICD_CHN_DESC AS diagnose_desc_end ");
        sqlbf.append(" FROM CLP_CLNCPATHERDIAGICD A,SYS_DIAGNOSIS B,SYS_DIAGNOSIS C  ");
        sqlbf.append(" WHERE A.ICD_TYPE_BEGIN=B.ICD_TYPE(+) AND A.ICD_CODE_BEGIN=B.ICD_CODE(+) ");
        sqlbf.append(" AND  A.ICD_TYPE_END=C.ICD_TYPE(+) AND A.ICD_CODE_END=C.ICD_CODE(+) ");
        sqlbf.append(" AND  A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'");
        sqlbf.append(" ORDER BY A.SEQ ");
//        System.out.println("-----"+sqlbf.toString());
        Map resultMap= TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result=new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm selectOptICDList(TParm parm) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT A.OPERATION_ICD_BEGIN AS operator_diagnose_icd_begin  ,A.OPERATION_ICD_END AS operator_diagnose_icd_end ,B.OPT_CHN_DESC AS operator_diagnose_desc_begin ,C.OPT_CHN_DESC AS operator_diagnose_desc_end ");
        sqlbf.append(" FROM CLP_CLNCPATHEROPTICD A,SYS_OPERATIONICD B,SYS_OPERATIONICD C ");
        sqlbf.append(" WHERE A.OPERATION_ICD_BEGIN=B.OPERATION_ICD(+) ");
        sqlbf.append(" AND   A.OPERATION_ICD_END=C.OPERATION_ICD(+) ");
        sqlbf.append(" AND  A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'");
        sqlbf.append(" ORDER BY A.SEQ");
//        System.out.println("-----"+sqlbf.toString());
        Map resultMap = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
