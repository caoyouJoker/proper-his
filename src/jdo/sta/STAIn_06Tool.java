package jdo.sta;

import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: STA_IN_06危急患者疗效分析报表</p>
 *
 * <p>Description: STA_IN_06危急患者疗效分析报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-22
 * @version 1.0
 */
public class STAIn_06Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_06Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAIn_06Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_06Tool();
        return instanceObject;
    }

    public STAIn_06Tool() {
        setModuleName("sta\\STAIn_06Module.x");
        onInit();
    }

    /**
     * 查询首页视图 查询危急病患
     * @param parm TParm
     * @param IN_CONDITION String
     * @return TParm
     */
    public TParm selectMRO_RECORD(TParm parm, String IN_CONDITION) {
        parm.setData("IN_CONDITION", IN_CONDITION);
        TParm result = this.query("selectMRO_RECORD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询出院人数
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutNum(TParm parm){
        TParm result = this.query("selectOutNum", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 生成数据(以4级科室为单位)
     * @param sta_date String
     * @return TParm
     */
    public TParm selectData(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        if (parm==null) {
            result.setErr( -1, "参数不能为空");
            return result;
        }
        String StartDate = parm.getValue("DATE_S"); // 起始日期
		String EndDate = parm.getValue("DATE_E"); // 截止日期
        //病危患者
        TParm IN_CONDITION_1 = this.selectMRO_RECORD(parm,"1");
        if (IN_CONDITION_1.getErrCode() < 0) {
            err("ERR:" + IN_CONDITION_1.getErrCode() +
                IN_CONDITION_1.getErrText() +
                IN_CONDITION_1.getErrName());
            return IN_CONDITION_1;
        }
        //病急患者
        TParm IN_CONDITION_2 = this.selectMRO_RECORD(parm,"2");
        if (IN_CONDITION_2.getErrCode() < 0) {
            err("ERR:" + IN_CONDITION_2.getErrCode() +
                IN_CONDITION_2.getErrText() +
                IN_CONDITION_2.getErrName());
            return IN_CONDITION_2;
        }
        //查询出院人数
        TParm outNum = this.selectOutNum(parm);
        if (outNum.getErrCode() < 0) {
            err("ERR:" + outNum.getErrCode() +
                outNum.getErrText() +
                outNum.getErrName());
            return outNum;
        }

        //判断查询条件中是否包含部门，如果指定了部门那么只统计该部门的信息 其他部门忽略
        TParm deptList = new TParm();
        if(parm.getValue("DEPT_CODE").trim().length()<=0){
            //查询所有四级部门
            deptList = STATool.getInstance().getDeptByLevel(new String[]{"4"},parm.getValue("REGION_CODE"));//======pangben modify 20110525
        }
        else{
            //查询指定的部门 根据IPD_DEPT_CODE 查询
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(parm.getValue("DEPT"),parm.getValue("REGION_CODE"));//======pangben modify 20110525
            if(deptList.getErrCode()<0){
                return deptList;
            }
        }
        String deptCode = ""; //记录科室CODE
        String IPD_DEPT_CODE = ""; //记录住院科室CODE
//        String STATION_CODE = "";//住院病区CODE
        for (int i = 0; i < deptList.getCount(); i++) {
            deptCode = deptList.getValue("DEPT_CODE", i);
            IPD_DEPT_CODE = deptList.getValue("IPD_DEPT_CODE", i); //住院科室CODE
//            STATION_CODE = deptList.getValue("STATION_CODE", i); //住院病区CODE
            if (IPD_DEPT_CODE.trim().length() <= 0) { //判断是否是住院科室 如果不是就进行下一次循环
                continue;
            }
            //定义各个字段的变量
            int DATA_01 = 0;
            double DATA_02 = 0;
            double DATA_03 = 0;
            double DATA_04 = 0;
            int DATA_05 = 0;
            int DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            double DATA_10 = 0;
            double DATA_11 = 0;
            double DATA_12 = 0;
            double DATA_13 = 0;
            double DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int out_num = 0;//记录出院病人总数
            String stationCode = IPD_DEPT_CODE; //记录病区CODE
            int inDays1 = 0;//记录住院日数
            int inDays2 = 0;//记录住院日数
            //循环病危患者信息
            for (int j = 0; j < IN_CONDITION_1.getCount(); j++) {
                //如果出院部门CODE等于住院部门CODE 则计数
                if (IN_CONDITION_1.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(IN_CONDITION_1.getValue("OUT_STATION", j))
                    ) {
                    String STATUS = IN_CONDITION_1.getValue("CODE1_STATUS",j);//出院诊断状态
                    if(STATUS.equals("1")){//治愈
                        DATA_06 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_06;//病危合计累加
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("2")){//好转
                        DATA_07 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 +=DATA_07;//病危合计累加
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("3")){//未愈
                        DATA_08 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_08;//病危合计累加
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("4")){//死亡
                        DATA_09 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_09;//病危合计累加
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//住院日数
                    }
                }
            }
            if(DATA_05!=0){
                DATA_10 = (double) DATA_06 / (double) DATA_05 * 100; //治愈率
                DATA_11 = (double) DATA_07 / (double) DATA_05 * 100; //好转率
                DATA_12 = (double) DATA_08 / (double) DATA_05 * 100; //未愈率
                DATA_13 = (double) DATA_09 / (double) DATA_05 * 100; //病死率
                DATA_14 = (double) inDays1 / (double) DATA_05;//平均住院日数
            }
            //循环病急患者信息
            for (int j = 0; j < IN_CONDITION_2.getCount(); j++) {
                //如果出院部门CODE等于住院部门CODE 则计数
                if (IN_CONDITION_2.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(IN_CONDITION_2.getValue("OUT_STATION", j))
                    ) {
                    String STATUS = IN_CONDITION_2.getValue("CODE1_STATUS",j);//出院诊断状态
                    if(STATUS.equals("1")){//治愈
                        DATA_16 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_16;//病急合计累加
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("2")){//好转
                        DATA_17 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 +=DATA_17;//病急合计累加
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("3")){//未愈
                        DATA_18 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_18;//病急合计累加
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//住院日数
                    }else if(STATUS.equals("4")){//死亡
                        DATA_19 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_19;//病急合计累加
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//住院日数
                    }
                }
            }
            if(DATA_15!=0){
                DATA_20 = (double) DATA_16 / (double) DATA_15 * 100; //治愈率
                DATA_21 = (double) DATA_17 / (double) DATA_15 * 100; //好转率
                DATA_22 = (double) DATA_18 / (double) DATA_15 * 100; //未愈率
                DATA_23 = (double) DATA_19 / (double) DATA_15 * 100; //病死率
                DATA_24 = (double) inDays2 / (double) DATA_15;//平均住院日数
            }
            int out = 0;
            //循环每科室出院人数
            for(int j=0;j<outNum.getCount();j++){
                //如果出院部门CODE等于住院部门CODE 则计数
                if (outNum.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)) {
                    out = outNum.getInt("NUM",j);
                    out_num += out;
                }
            }
            DATA_01 = DATA_05 + DATA_15;//危急病人出院人数
            if(out_num!=0){
                DATA_02 = (double)(DATA_05 + DATA_15)/(double)out_num*100;//危急病人占百分比
                DATA_03 = (double)DATA_05/(double)out_num*100;//病危病人占出院病人百分比
                DATA_04 = (double)DATA_15/(double)out_num*100;//病急病人占出院病人百分比
            }
            //填充数据
            result.addData("STA_DATE", StartDate + "-" + EndDate);
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", null==stationCode || stationCode.length()==0? IPD_DEPT_CODE:stationCode);//===========pangben modify 20110525
            result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            result.addData("DATA_02", DATA_02 == 0 ? "" :df.format(DATA_02));
            result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
            result.addData("DATA_04", DATA_04 == 0 ? "" : df.format(DATA_04));
            result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : df.format(DATA_10));
            result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
            result.addData("DATA_12", DATA_12 == 0 ? "" : df.format(DATA_12));
            result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
            result.addData("DATA_14", DATA_14 == 0 ? "" : df.format(DATA_14));
            result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
            result.addData("DATA_21", DATA_21 == 0 ? "" : df.format(DATA_21));
            result.addData("DATA_22", DATA_22 == 0 ? "" : df.format(DATA_22));
            result.addData("DATA_23", DATA_23 == 0 ? "" : df.format(DATA_23));
            result.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
            //--------------------前台计算求和----------------------------
            result.addData("DATA_25", out_num);
            result.addData("DATA_26", inDays1);
            result.addData("DATA_27", inDays2);
        }
        return result;
    }
    /**
     * 删除表STA_IN_06数据
     * @param STA_DATE String
     * @return TParm
     * =============pangben modify 201110525 添加区域参数
     */
    public TParm deleteSTA_IN_06(String STA_DATE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //============pangben modify 201110525
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.update("deleteSTA_IN_06",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入STA_IN_06数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_06(TParm parm,TConnection conn){
        TParm result = this.update("insertSTA_IN_06",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 导入STA_IN_06表信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "没有可插入的数据");
            return result;
        }
        String STA_DATE = parm.getValue("STA_DATE", 0);
        //============pangben modify 20110525 start
         String regionCode = parm.getValue("REGION_CODE", 0);
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE不可为空");
            return result;
        }
        //============pangben modify 20110525 添加区域参数
        result = this.deleteSTA_IN_06(STA_DATE,regionCode, conn); //删除该日期的数据
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm insert = null;
        for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
            insert = new TParm();
            insert.setData("STA_DATE", parm.getValue("STA_DATE", i));
            insert.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
            insert.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
            insert.setData("DATA_01", parm.getValue("DATA_01", i));
            insert.setData("DATA_02", parm.getValue("DATA_02", i));
            insert.setData("DATA_03", parm.getValue("DATA_03", i));
            insert.setData("DATA_04", parm.getValue("DATA_04", i));
            insert.setData("DATA_05", parm.getValue("DATA_05", i));
            insert.setData("DATA_06", parm.getValue("DATA_06", i));
            insert.setData("DATA_07", parm.getValue("DATA_07", i));
            insert.setData("DATA_08", parm.getValue("DATA_08", i));
            insert.setData("DATA_09", parm.getValue("DATA_09", i));
            insert.setData("DATA_10", parm.getValue("DATA_10", i));
            insert.setData("DATA_11", parm.getValue("DATA_11", i));
            insert.setData("DATA_12", parm.getValue("DATA_12", i));
            insert.setData("DATA_13", parm.getValue("DATA_13", i));
            insert.setData("DATA_14", parm.getValue("DATA_14", i));
            insert.setData("DATA_15", parm.getValue("DATA_15", i));
            insert.setData("DATA_16", parm.getValue("DATA_16", i));
            insert.setData("DATA_17", parm.getValue("DATA_17", i));
            insert.setData("DATA_18", parm.getValue("DATA_18", i));
            insert.setData("DATA_19", parm.getValue("DATA_19", i));
            insert.setData("DATA_20", parm.getValue("DATA_20", i));
            insert.setData("DATA_21", parm.getValue("DATA_21", i));
            insert.setData("DATA_22", parm.getValue("DATA_22", i));
            insert.setData("DATA_23", parm.getValue("DATA_23", i));
            insert.setData("DATA_24", parm.getValue("DATA_24", i));
            insert.setData("DATA_25", parm.getValue("DATA_25", i));
            insert.setData("DATA_26", parm.getValue("DATA_26", i));
            insert.setData("DATA_27", parm.getValue("DATA_27", i));
            insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
            //===========pangben modify 20110525 start
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
            //===========pangben modify 20110525 stop
            result = this.insertSTA_IN_06(insert, conn); //插入新数据
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * 查询STA_IN_06表数据
     * @param STA_DATE String
     * @return TParm
     * =================pangben modify 20110525
     */
    public TParm selectSTA_IN_06(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("REGION_CODE", regionCode);
        TParm result = this.query("selectSTA_IN_06", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改STA_IN_06数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_IN_06(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_06", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
