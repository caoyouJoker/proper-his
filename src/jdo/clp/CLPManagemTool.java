package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import java.util.Map;

import jdo.sys.SystemTool;

import com.dongyang.db.TConnection;

/**
 * <p>Title: 临床路径准进准出</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemTool extends TJDOTool {
    public CLPManagemTool() {
        setModuleName("clp\\CLPManagemModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static CLPManagemTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static CLPManagemTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPManagemTool();
        return instanceObject;
    }

    /**
     * 查询时程信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDurationData(TParm parm) {
//        System.out.println("时程信息查询方法");
        TParm result = this.query("selectDurationData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 保存时程信息
     * @return TParm
     */
    public TParm savaDurationData(TParm parm,TConnection conn){
//        System.out.println("保存时程信息查询方法");
        TParm result = this.update("saveDurationData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 展开时程
     * @return TParm
     */
    public TParm openDuraction(TParm parm, TConnection conn) {
//        System.out.println("展开时程方法");
        TParm result = this.update("openDuraction", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 删除时程信息
     * @return TParm
     */
    public TParm delDurationData(TParm parm) {
//        System.out.println("删除时程信息查询方法");
        TParm result = this.update("delDurationData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询准入信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
//        System.out.println("数据查询方法");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询溢出路径信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOverData(TParm parm) {
//        System.out.println("数据查询方法");
        TParm result = this.query("selectOverData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询作废路径信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selecCanceltData(TParm parm) {
//        System.out.println("数据查询方法");
        TParm result = this.query("selectCancelData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 将临床路径数据移动到历史表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm managermToHistory(TParm parm,TConnection conn) {
//        System.out.println("数据移动方法");
        TParm result = this.update("moveManagermData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 得到住院病人信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getPatientInfo(TParm parm) {
//        System.out.println("数据查询方法--查询病人信息");
        TParm result = this.query("selectPatientData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    
    public TParm insertManagem(TParm parm){
//       System.out.println("数据插入方法--插入临床路径");
       TParm result = this.update("insertPatientManagem",parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
    }
    /**
     * 进入临床路径AMD_INP
     * @return TParm
     */
    public TParm insertAMDINPIntoCLNCPathCode(TParm parm,TConnection conn){
//        System.out.println("数据插入方法--插入AMD_INP");
        TParm result = this.update("insertAMDINPIntoCLNCPathCode", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    public TParm insertManagemHistory(TParm parm,TConnection conn) {
        //system.out.println("数据插入方法--插入临床路径历史表");
        TParm result = this.update("insertManagemHistory", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 插入临床路径(带事务方式)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertManagem(TParm parm,TConnection conn) {
        //system.out.println("数据插入方法--插入临床路径");
        TParm result = this.update("insertPatientManagem", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 删除数据
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm) {
        TParm result = update("deleteData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm,TConnection conn) {
        TParm result = update("deleteData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm updateManagem(TParm parm,TConnection conn){
        //system.out.println("数据插入方法--编辑临床路径");
        StringBuffer sqlbf = new StringBuffer();
        String caseNo=parm.getValue("CASE_NO");
        String clncPathCode=parm.getValue("CLNCPATH_CODE");
        sqlbf.append("UPDATE CLP_MANAGEM SET ");
        //处理设置条件begin
        String startDttm=parm.getValue("START_DTTM");
        //system.out.println("进入"+startDttm);
        if(checkNullAndEmpty(startDttm)){
            sqlbf.append(" START_DTTM = TO_DATE('"+startDttm+"','YYYYMMDD') ,");
        }
        String deleteDttm = parm.getValue("DELETE_DTTM");
        //system.out.println("作废"+deleteDttm);
        if(checkNullAndEmpty(deleteDttm)){
            sqlbf.append(" DELETE_DTTM = TO_DATE('"+deleteDttm+"','YYYYMMDD') ,");
        }
        String endDttm = parm.getValue("END_DTTM");
        String outissu=parm.getValue("OUTISSUE_CODE");
        //system.out.println("溢出:"+endDttm);
        if(checkNullAndEmpty(endDttm)){
            sqlbf.append(" END_DTTM = TO_DATE('"+endDttm+"','YYYYMMDD') ,");
            sqlbf.append(" OUTISSUE_CODE='"+outissu+"' , ");
        }
        sqlbf.append(" STATUS = '"+parm.getValue("STATUS")+"' ");
        //处理设置条件end
        sqlbf.append(" WHERE CASE_NO = '"+caseNo+"' ");
        sqlbf.append("AND CLNCPATH_CODE = '"+clncPathCode+"'");
        //system.out.println("执行的sql语句:"+sqlbf.toString());
        Map mapresult=TJDODBTool.getInstance().update(sqlbf.toString(),conn);
        TParm result =new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
       return result;
    }
    public TParm moveDeleteAndEndManagemIntoHistory(TParm parm,TConnection conn){
        TParm result = update("moveDeleteAndEndManagemIntoHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * 更新原临床路径信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateChangedManagem(TParm parm , TConnection conn){
        //system.out.println("------------------------更新变更临床路径信息");
        TParm result = update("updateChangeManagem", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr){
        if(checkstr==null){
            return false;
        }
        if("".equals(checkstr)){
            return false;
        }
        return true;
    }
    /**
     * 插入临床路径的默认时程信息方法
     * @param clncPathCode String
     * @return TParm
     */
    public TParm insertDefaultDurationConfig(TParm inparm,TConnection conn){
        String caseNo=inparm.getValue("CASE_NO");
        String clncPathCode=inparm.getValue("CLNCPATH_CODE");
        String regionCode=inparm.getValue("REGION_CODE");
        String optUser=inparm.getValue("OPT_USER");
        String optDate=inparm.getValue("OPT_DATE");
        String optTerm=inparm.getValue("OPT_TERM");
        //入院时间
        String inDate="";
        TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM CLP_THRPYSCHDM_REAL WHERE ");
        sqlbf.append(" CASE_NO = '"+caseNo+"'");
        sqlbf.append(" AND CLNCPATH_CODE ='"+clncPathCode+"'");
        Map mapresult=TJDODBTool.getInstance().update(sqlbf.toString(),conn);
        result=new TParm(mapresult);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //查找入院时间
        sqlbf.delete(0,sqlbf.length());
        sqlbf.append(" SELECT TO_CHAR(IN_DATE,'YYYYMMDDHH24MISS') AS IN_DATE FROM ADM_INP WHERE CASE_NO ='"+caseNo+"'");
        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //==========pangben 2012-6-12 添加修改sql语句
        String inDateTemp="";
        if(result.getCount()>0){
            inDate = result.getValue("IN_DATE", 0);
            inDateTemp=result.getValue("IN_DATE", 0);
        }
        int seq=0;
//        sqlbf.delete(0,sqlbf.length());
//        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ)+1)) WHEN 0 THEN '0' ELSE TO_CHAR(MAX(SEQ)) END AS SEQ FROM CLP_THRPYSCHDM_REAL WHERE  CASE_NO ='"+caseNo+"' AND CLNCPATH_CODE='"+clncPathCode+"'");
//        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
//        result = new TParm(mapresult);
//        seq=result.getInt("SEQ",0);
        sqlbf.delete(0,sqlbf.length());
        //得到标准数据
        sqlbf.append(" SELECT CLNCPATH_CODE,SCHD_CODE,");
        sqlbf.append(caseNo + " AS CASE_NO, ");
        sqlbf.append("'" + regionCode + "'" + " AS REGION_CODE, ");
        sqlbf.append("SCHD_DAY,SUSTAINED_DAYS,");
        sqlbf.append("'" + optUser + "'" + " AS OPT_USER,");
        sqlbf.append("TO_CHAR(TO_DATE(" + optDate + ",'YYYYMMDD'),'YYYYMMDDHH24MISS')" + " AS OPT_DATE ,");
        sqlbf.append("'" + optTerm + "'" + " AS OPT_TERM , ");
        inDate=inDate.substring(0,8)+"000000";
        inDateTemp=inDate.substring(0,8)+"235959";
        sqlbf.append("TO_CHAR((TO_DATE(" + inDate +
                     ",'YYYYMMDDHH24MISS')+SUSTAINED_DAYS-1),'YYYYMMDDHH24MISS')" +
                     " AS START_DATE ,");    
        sqlbf.append("TO_CHAR((TO_DATE(" + inDateTemp +
                     ",'YYYYMMDDHH24MISS')+SUSTAINED_DAYS-2+SCHD_DAY),'YYYYMMDDHH24MISS')" +
                     " AS END_DATE ");//=======pangben 2012-6-12 从 SUSTAINED_DAYS-1 改SUSTAINED_DAYS-2
        sqlbf.append(" FROM CLP_THRPYSCHDM ");
        sqlbf.append(" WHERE REGION_CODE='" + regionCode + "'");
        sqlbf.append(" AND CLNCPATH_CODE='" + clncPathCode + "'");
        sqlbf.append(" ORDER BY SEQ ");
        //system.out.println("sqlbf:::"+sqlbf);
        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        result = new TParm(mapresult);
        for(int i=0;i<result.getCount();i++){
            //插入默认数据
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_THRPYSCHDM_REAL VALUES(");
            sqlbf.append("'"+result.getValue("CLNCPATH_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("SCHD_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("CASE_NO",i)+"',");
            sqlbf.append(""+seq+",");
            sqlbf.append("'"+result.getValue("REGION_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("SCHD_DAY",i)+"',");
            sqlbf.append("'"+result.getValue("SUSTAINED_DAYS",i)+"',");
            sqlbf.append("'"+result.getValue("OPT_USER",i)+"',");
            sqlbf.append("TO_DATE('"+result.getValue("OPT_DATE",i)+"','YYYYMMDDHH24MISS'),");
            sqlbf.append("'"+result.getValue("OPT_TERM",i)+"',");
             sqlbf.append("TO_DATE('"+result.getValue("START_DATE",i)+"','YYYYMMDDHH24MISS'),");
              sqlbf.append("TO_DATE('"+result.getValue("END_DATE",i)+"','YYYYMMDDHH24MISS')");
            sqlbf.append(")");
            mapresult = TJDODBTool.getInstance().update(sqlbf.toString(),conn);
            TParm insertResult = new TParm(mapresult);
            if (insertResult.getErrCode() < 0) {
                err("ERR:" + insertResult.getErrCode() + insertResult.getErrText() +
                    insertResult.getErrName());
                return insertResult;
            }
            seq++;
        }
        return result;
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }
    /**
     * 更新路径时程方法
    * @Title: updateIbsOrddSchdCode
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @author Dangzhang
    * @return
    * @throws
     */
	public TParm updateIbsOrddSchdCode(TParm parm, TConnection conn) {
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"
				+ parm.getValue("NEW_CLNCPATH_CODE") + "' ,SCHD_CODE='"
				+ parm.getValue("NEW_SCHD_CODE") + "' WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND CLNCPATH_CODE= '"
				+ parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE";
		if (parm.getValue("SCHD_CODE").length()>0 ) {
			sql+="='"+parm.getValue("SCHD_CODE") + "'";
		}else{
			sql+=" IS NULL ";
		}
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}
	/**
	 * 相同时程代码直接替换
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm  updateIbsOrddSameSchdCode(TParm parm, TConnection conn) {
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"
			+ parm.getValue("CLNCPATH_CODE") + "'  WHERE CASE_NO='"
			+ parm.getValue("CASE_NO") + "' AND SCHD_CODE IN ("+parm.getValue("sameSchdCode")+")";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			return result;
	}
	/**
	 * 完全匹配路径直接修改
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateIBsOrddClnPathCode(TParm parm, TConnection conn){
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"+ parm.getValue("CLNCPATH_CODE") + "' WHERE CASE_NO='"
			+ parm.getValue("CASE_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}
	/**
	 * 
	* @Title: queryClpManagem
	* @Description: TODO(查询路径路径主表数据)
	* @author pangben 2015-8-13
	* @param parm
	* @return
	* @throws
	 */
	public TParm queryClpManagem(TParm parm){
		TParm result = query("queryClpManagem", parm);
	    return result;
	}
}
