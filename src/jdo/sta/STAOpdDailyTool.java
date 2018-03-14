package jdo.sta;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: 门急诊中间档</p>
 *
 * <p>Description: 门急诊中间档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STAOpdDailyTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOpdDailyTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOpdDailyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOpdDailyTool();
        return instanceObject;
    }

    public STAOpdDailyTool() {
        setModuleName("sta\\STAOpdDailyModule.x");
        onInit();
    }

    /**
     * 获取要查询的各项目的数据
     * @param parm TParm
     * @param type String  输入要查询的SQL语句名称
     * @return TParm
     */
    public Map getNum(String ADMDATE, String type) {
        TParm parm = new TParm();
        parm.setData("ADMDATE",ADMDATE);
        Map map = new HashMap();
        TParm result = this.query(type, parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return map;
        }
        for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
            map.put(result.getData("DEPT_CODE", i),
                    result.getData("O_NUM", i));
        }
        return map;
    }

    /**
     * 插入中间表
     * @param parm ActionParm
     * @return ActionParm
     */
    public TParm insertSTA_OPD_DAILY(TParm parms, TConnection conn) {
        TParm parm;
        TParm result = new TParm();
        if (parms == null) {
            result.setErr( -1, "参数不能为NULL");
            return result;
        }
        //循环提取数据生成SQL语句 进行保存
        for (int i = 0; i < parms.getCount("STA_DATE"); i++) {
            parm = new TParm();
            parm.setData("STA_DATE", parms.getData("STA_DATE", i));
            parm.setData("DEPT_CODE", parms.getData("DEPT_CODE", i));
            parm.setData("OUTP_NUM", parms.getData("OUTP_NUM", i));
            parm.setData("ERD_NUM", parms.getData("ERD_NUM", i));
            parm.setData("HRM_NUM", parms.getData("HRM_NUM", i));
            parm.setData("OTHER_NUM", parms.getData("OTHER_NUM", i));
            parm.setData("GET_TIMES", parms.getData("GET_TIMES", i));
            parm.setData("PROF_DR", parms.getData("PROF_DR", i));
            parm.setData("COMM_DR", parms.getData("COMM_DR", i));
            parm.setData("DR_HOURS", parms.getData("DR_HOURS", i));
            parm.setData("SUCCESS_TIMES", parms.getData("SUCCESS_TIMES", i));
            parm.setData("OBS_NUM", parms.getData("OBS_NUM", i));
            parm.setData("ERD_DIED_NUM", parms.getData("ERD_DIED_NUM", i));
            parm.setData("OBS_DIED_NUM", parms.getData("OBS_DIED_NUM", i));
            parm.setData("OPE_NUM", parms.getData("OPE_NUM", i));
            parm.setData("FIRST_NUM", parms.getData("FIRST_NUM", i));
            parm.setData("FURTHER_NUM", parms.getData("FURTHER_NUM", i));
            parm.setData("APPT_NUM", parms.getData("APPT_NUM", i));
            parm.setData("ZR_DR_NUM", parms.getData("ZR_DR_NUM", i));
            parm.setData("ZZ_DR_NUM", parms.getData("ZZ_DR_NUM", i));
            parm.setData("ZY_DR_NUM", parms.getData("ZY_DR_NUM", i));
            parm.setData("ZX_DR_NUM", parms.getData("ZX_DR_NUM", i));
            parm.setData("OPT_USER", parms.getData("OPT_USER", i));
            parm.setData("OPT_TERM", parms.getData("OPT_TERM", i));
            //=============pangben modify 20110519
            parm.setData("REGION_CODE", parms.getData("REGION_CODE", i));
            parm.setData("CONFIRM_FLG","N");//2009-7-7加入提交状态字段 默认为‘N’
            //删除原有数据
            result = this.deleteSTA_OPD_DATA(parms.getValue("STA_DATE", i),parms.getValue("DEPT_CODE", i),parms.getValue("REGION_CODE", i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName()+"行数："+i+"  日期部门："+parms.getData("STA_DATE", i)+" "+parms.getData("DEPT_CODE", i));
                return result;
            }
            result = this.update("Insert_STA_OPD_DAILY", parm, conn);
            // 判断错误值
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName()+"行数："+i+"  日期部门："+parms.getData("STA_DATE", i)+" "+parms.getData("DEPT_CODE", i));
                return result;
            }
        }
        return result;
    }

    /**
     * 插入 门急诊中间档 数据
     * @param parm TParm  sql语句参数
     * @param dept TParm  部门列表
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertSTA_OPD_DATA(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL");//SQL参数
        TParm dept = parmObj.getParm("DEPT");//部门参数
        Map OUTP_NUM = this.getNum(parm.getValue("ADMDATE"), "OUTP_NUM"); //门诊人数
        Map ERD_NUM = this.getNum(parm.getValue("ADMDATE"), "ERD_NUM"); //急诊人数
        Map HRM_NUM = this.getNum(parm.getValue("ADMDATE"), "HRM_NUM"); //健康检查人数
        Map GET_TIMES = this.getNum(parm.getValue("ADMDATE"), "GET_TIMES"); //抢救人次
        Map PROF_DR = this.getNum(parm.getValue("ADMDATE"), "PROF_DR"); //专家诊<主任副主任>人次
        Map COMM_DR = this.getNum(parm.getValue("ADMDATE"), "COMM_DR"); //普通诊
        Map DR_HOURS_4 = this.getNum(parm.getValue("ADMDATE"), "DR_HOURS_4"); //工作小时(半天诊)
        Map DR_HOURS_8 = this.getNum(parm.getValue("ADMDATE"), "DR_HOURS_8"); //工作小时(全天诊)
//        Map DEATH_NUM = this.getNum(parm.getValue("ADMDATE"), "DEATH_NUM"); //死亡人数 用于计算抢救成功次数
        Map DEATH_NUM = new HashMap();//目前系统中没有关于门诊死亡的记录
        Map OBS_NUM = this.getNum(parm.getValue("ADMDATE"), "OBS_NUM"); //留观人数
//        Map ERD_DIED_NUM = this.getNum(parm.getValue("ADMDATE"), "ERD_DIED_NUM"); //急诊死亡人数
        Map ERD_DIED_NUM = new HashMap();//目前系统中没有关于门诊死亡的记录
//        Map OBS_DIED_NUM = this.getNum(parm.getValue("ADMDATE"), "OBS_DIED_NUM"); //留观死亡人数
        Map FIRST_NUM = this.getNum(parm.getValue("ADMDATE"), "FIRST_NUM"); //初诊人数
        Map FURTHER_NUM = this.getNum(parm.getValue("ADMDATE"), "FURTHER_NUM"); //复诊人数
        Map APPT_NUM = this.getNum(parm.getValue("ADMDATE"), "APPT_NUM"); //预约人数
        Map ZR_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZR_DR_NUM"); //主任副主任医师人数
        Map ZZ_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZZ_DR_NUM"); //主治医师人数
        Map ZY_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZY_DR_NUM"); //住院医师人数
//        Map ZX_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZX_DR_NUM"); //进修医师人数
        Map OBS_DIED_NUM = new HashMap();
        TParm data = new TParm();
        //循环搜索 将对应科室的数据存储
        for (int i = 0; i < dept.getCount("DEPT_CODE"); i++) {
            String Dc = dept.getValue("OE_DEPT_CODE", i); //科室门急诊代码
            data.addData("STA_DATE", parm.getValue("ADMDATE")); //统计日期
            data.addData("DEPT_CODE", dept.getValue("DEPT_CODE", i)); //部门代码
            data.addData("OUTP_NUM",
                         OUTP_NUM.get(Dc) == null ? 0 : OUTP_NUM.get(Dc));
            data.addData("ERD_NUM",
                         ERD_NUM.get(Dc) == null ? 0 : ERD_NUM.get(Dc));
            data.addData("HRM_NUM",
                         HRM_NUM.get(Dc) == null ? 0 : HRM_NUM.get(Dc));
            data.addData("OTHER_NUM", 0); //其他   --暂时没有计算
            data.addData("GET_TIMES",
                         GET_TIMES.get(Dc) == null ? 0 : GET_TIMES.get(Dc));
            data.addData("PROF_DR",
                         PROF_DR.get(Dc) == null ? 0 : PROF_DR.get(Dc));
            data.addData("COMM_DR",
                         COMM_DR.get(Dc) == null ? 0 : COMM_DR.get(Dc));
            int n1 = 0; //半日诊 小时数
            int n2 = 0; //全日诊 小时数
            if (DR_HOURS_4.get(Dc) != null)
                n1 = Integer.valueOf(DR_HOURS_4.get(Dc).toString());
            if (DR_HOURS_8.get(Dc) != null)
                n2 = Integer.valueOf(DR_HOURS_8.get(Dc).toString());
            data.addData("DR_HOURS", n1 + n2); //工作小时数
            n1 = 0; //死亡数
            n2 = 0; //抢救数
            if (DEATH_NUM.get(Dc) != null)
                n1 = Integer.valueOf(DEATH_NUM.get(Dc).toString()); //死亡现在系统无记录
            if (GET_TIMES.get(Dc) != null)
                n2 = Integer.valueOf(GET_TIMES.get(Dc).toString());
            data.addData("SUCCESS_TIMES", n2 - n1); //抢救成功人次 = 抢救次数-死亡数
            data.addData("OBS_NUM",
                         OBS_NUM.get(Dc) == null ? 0 : OBS_NUM.get(Dc));//留观人数
            data.addData("ERD_DIED_NUM",
                         ERD_DIED_NUM.get(Dc) == null ? 0 : ERD_DIED_NUM.get(Dc));//急诊死亡人数 现在系统无记录
            data.addData("OBS_DIED_NUM",
                         OBS_DIED_NUM.get(Dc) == null ? 0 : OBS_DIED_NUM.get(Dc));//留观死亡人数 现在系统无记录
            data.addData("OPE_NUM", "");//手术人数(手工)
            //----shibl 20120516 modify 
            data.addData("FIRST_NUM", FIRST_NUM.get(Dc) == null ? 0 : FIRST_NUM.get(Dc));//初诊人数
            data.addData("FURTHER_NUM", FURTHER_NUM.get(Dc) == null ? 0 : FURTHER_NUM.get(Dc));//复诊人数
            data.addData("APPT_NUM", APPT_NUM.get(Dc) == null ? 0 : APPT_NUM.get(Dc));//预约人数
            data.addData("ZR_DR_NUM", ZR_DR_NUM.get(Dc) == null ? 0 : ZR_DR_NUM.get(Dc));//主任副主任
            data.addData("ZZ_DR_NUM", ZZ_DR_NUM.get(Dc) == null ? 0 : ZZ_DR_NUM.get(Dc));//主治
            data.addData("ZY_DR_NUM", ZY_DR_NUM.get(Dc) == null ? 0 : ZY_DR_NUM.get(Dc));//住院
            data.addData("ZX_DR_NUM", 0);//进修
            data.addData("OPT_USER", parm.getValue("OPT_USER")); //临时数据 字段非空
            data.addData("OPT_TERM", parm.getValue("OPT_TERM")); //临时数据 字段非空
            //===========pangben modify 20110519
            data.addData("REGION_CODE", parm.getValue("REGION_CODE")); //临时数据 字段非空
        }
        //插入数据
        TParm result = this.insertSTA_OPD_DAILY(data, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    public TParm ceshi(TParm parm, String type) {
        TParm result = this.query(type, parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除现有日期的数据
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteSTA_OPD_DATA(TParm parmObj, TConnection conn){
        TParm result = this.update("delete_STA_OPD_DAILY",parmObj,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除现有日期的数据
     * @param parmObj TParm
     * @param conn TConnection
     * @param regionCode 区域
     * @return TParm
     * =========pangben modify 20110520 添加区域参数
     */
    public TParm deleteSTA_OPD_DATA(String STA_DATE,String Dept_code,String regionCode, TConnection conn){
        TParm parm = new TParm();
        parm.setData("ADMDATE",STA_DATE);
        parm.setData("DEPT_CODE",Dept_code);
        //=========pangben modify 20110520 start
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.update("delete_STA_OPD_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 导入门诊中间表信息
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parmObj, TConnection conn){
        TParm result = new TParm();
//        //删除原有信息
//        TParm parm = parmObj.getParm("SQL");//删除参数
//        TParm dept = parmObj.getParm("DEPT"); //部门参数
//        for(int i=0;i<dept.getCount("DEPT_CODE");i++){
//            result = this.deleteSTA_OPD_DATA(parm.getValue("ADMDATE"),dept.getValue("DEPT_CODE",i), conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        //插入新数据
        result = this.insertSTA_OPD_DATA(parmObj,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 自动批次导入数据调用的方法
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * ============pangben modify 20110519 此方法暂时没有用到，如果需要调用在第一个参数中添加区域值
     */
    public TParm batchData(TParm p,TConnection conn){
        //获取昨天的日期
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),-1);
        String yestodate = StringTool.getString(time,"yyyyMMdd");
        TParm parm = new TParm();//日期参数
        parm.setData("ADMDATE",yestodate);
        TParm dept = new TParm();//部门列表  获取中间对照部门中的住院部门
        dept = STADeptListTool.getInstance().selectOE_DEPT(p.getValue("REGION_CODE"));//========pangben modify 20110519 添加参数
        TParm parmObj = new TParm();//总参数
        parmObj.setData("SQL",parm.getData());
        parmObj.setData("DEPT",dept.getData());

        TParm result = new TParm();
//        //删除原有信息
//        result = this.deleteSTA_OPD_DATA(parm,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        //插入新数据
        result = this.insertSTA_OPD_DATA(parmObj,conn);
        if (result.getErrCode() < 0) {
            err("STA门诊批次ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询STA_OPD_DAILY （门诊中间表）的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm select_STA_OPD_DAILY(TParm parm){
        TParm result = this.query("select_STA_OPD_DAILY",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   
}
