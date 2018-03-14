package jdo.inf;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 检测结果录入</p>
 *
 * <p>Description: 检测结果录入</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFExamTool extends TJDOTool{

    /**
     * 构造器
     */
    public INFExamTool() {
        setModuleName("inf\\INFExamModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static INFExamTool instanceObject;

    /**
     * 得到实例
     * @return INFExamTool
     */
    public static INFExamTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new INFExamTool();
        return instanceObject;
    }

    /**
     * 根据检测标准编码查询检测标准以及检测项目对照关系
     * @param examStandCode String
     * @return TParm
     */
    public TParm selectINFExamStand(String examStandCode){
        TParm parm = new TParm();
        parm.setData("EXAMSTAND_CODE",examStandCode);
        return query("selectINFExamStand",parm);
    }

    /**
     * 取检测记录序号
     * @return String
     */
    public String getExamNo(){
        return SystemTool.getInstance().getNo("ALL", "INF",
                "EXAM_NO", "EXAM_NO");
    }

    /**
     * 查询某科室一天最大检测记录序号
     * @param deptCode String
     * @param examPeriod String
     * @return String
     */
    public String getDeptExamPeriodMaxSeq(String deptCode,String examPeriod){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("EXAM_PERIOD",examPeriod);
        parm = query("getDeptExamPeriodMaxSeq",parm);
        if(parm.getErrCode() < 0)
            return "";
        if(parm.getCount() <= 0)
            return "01";
        if(parm.getValue("EXAM_PERIOD",0) == null ||
           parm.getValue("EXAM_PERIOD",0).length() ==0)
            return "01";
        int seq = Integer.parseInt(parm.getValue("EXAM_PERIOD",0)) + 1;
        if(("" + seq).length() >= 2)
            return "" + seq;
        return "0" + seq;
    }

    /**
     * 写入感染控制监测记录档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertINFDeptExamM(TParm parm,TConnection connection){
        return update("insertINFDeptExamM",parm,connection);
    }

    /**
     * 取得监测记录主信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINFExamRecordD(TParm parm){
        return query("selectINFExamRecordD",parm);
    }

    /**
     * 取得监测记录主信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINFExamRecordM(TParm parm){
      return query("selectINFExamRecordM",parm);
    }

    /**
     * 更新感染控制监测记录档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateINFExamRecord(TParm parm,TConnection connection){
        return update("updateINFExamRecord",parm,connection);
    }

    /**
     * 删除感染控制监测记录档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteINFExamRecord(TParm parm,TConnection connection){
        return update("deleteINFExamRecord",parm,connection);
    }
}
