package jdo.sta;

import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: STA_IN_04医院医疗诊断质量报表</p>
 *
 * <p>Description: STA_IN_04医院医疗诊断质量报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-21
 * @version 1.0
 */
public class STAIn_04Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_04Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAIn_04Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_04Tool();
        return instanceObject;
    }

    public STAIn_04Tool() {
        setModuleName("sta\\STAIn_04Module.x");
        onInit();
    }
    /**
     * 查询首页试图中符合条件的“诊断符合”数据
     * @param type String 调用的SQL语句名称
     * @param p TParm 必须参数：DATE_S:起始日期；DATE_E:截止日期；
     * @return TParm
     */
    public TParm selectRecordData(String type,TParm parm){
        TParm result = this.query(type, parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 生成数据(以4级科室为单位)
     * @param p TParm
     * @return TParm
     */
    public TParm selectData(TParm p){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        if(p==null){
            result.setErr(-1,"参数不能为空");
            return result;
        }
        String StartDate = p.getValue("DATE_S"); // 起始日期
		String EndDate = p.getValue("DATE_E"); // 截止日期
        //门诊与住院
        TParm QUYCHK_OI = this.selectRecordData("selectQUYCHK_OI",p);
        if (QUYCHK_OI.getErrCode() < 0) {
            err("ERR:" + QUYCHK_OI.getErrCode() + QUYCHK_OI.getErrText() +
                QUYCHK_OI.getErrName());
            return QUYCHK_OI;
        }
        //入院与出院
        TParm QUYCHK_INOUT = this.selectRecordData("selectQUYCHK_INOUT",p);
        if (QUYCHK_INOUT.getErrCode() < 0) {
            err("ERR:" + QUYCHK_INOUT.getErrCode() + QUYCHK_INOUT.getErrText() +
                QUYCHK_INOUT.getErrName());
            return QUYCHK_INOUT;
        }
        //术前术后
        TParm QUYCHK_OPBFAF = this.selectRecordData("selectQUYCHK_OPBFAF",p);
        if (QUYCHK_OPBFAF.getErrCode() < 0) {
            err("ERR:" + QUYCHK_OPBFAF.getErrCode() + QUYCHK_OPBFAF.getErrText() +
                QUYCHK_OPBFAF.getErrName());
            return QUYCHK_OPBFAF;
        }
        //临床与病理
        TParm QUYCHK_CLPA = this.selectRecordData("selectQUYCHK_CLPA",p);
        if (QUYCHK_CLPA.getErrCode() < 0) {
            err("ERR:" + QUYCHK_CLPA.getErrCode() + QUYCHK_CLPA.getErrText() +
                QUYCHK_CLPA.getErrName());
            return QUYCHK_CLPA;
        }
        //查询出院人数
        TParm out = this.selectRecordData("selectOut",p);
        if (out.getErrCode() < 0) {
            err("ERR:" + out.getErrCode() + out.getErrText() +
            		out.getErrName());
            return out;
        }
        //判断查询条件中是否包含部门，如果指定了部门那么只统计该部门的信息 其他部门忽略
        TParm deptList = new TParm();
        if(p.getValue("DEPT_CODE").trim().length()<=0){
            //查询所有四级部门
            deptList = STATool.getInstance().getDeptByLevel(new String[]{"4"},p.getValue("REGION_CODE"));//=========pangben modify 20110524
        }
        else{
            //查询指定的部门 根据IPD_DEPT_CODE 查询
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(p.getValue("DEPT"),p.getValue("REGION_CODE"));//=========pangben modify 20110524
        }
        String deptCode = "";//记录科室CODE
        String IPD_DEPT_CODE = "";//记录住院科室CODE
//        String STATION_CODE = "";//住院病区CODE
        for(int i=0;i<deptList.getCount();i++){
            deptCode = deptList.getValue("DEPT_CODE",i);
            IPD_DEPT_CODE = deptList.getValue("IPD_DEPT_CODE",i);//住院科室CODE
//            STATION_CODE = deptList.getValue("STATION_CODE",i);//住院病区CODE
            if(IPD_DEPT_CODE.trim().length()<=0){//判断是否是住院科室 如果不是就进行下一次循环
                continue;
            }
            //定义各个字段的变量
            int DATA_01 = 0;
            int DATA_02 = 0;
            int DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            double DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            int DATA_10 = 0;
            double DATA_11 = 0;
            double DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            double DATA_17 = 0;
            double DATA_18 = 0;
            int DATA_19 = 0;
            int DATA_20 = 0;
            int DATA_21 = 0;
            int DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int outCount = 0;
            String stationCode = IPD_DEPT_CODE;//记录病区CODE
            //循环统计门诊与住院的数据
            for(int j=0;j<QUYCHK_OI.getCount();j++){
                //如果出院部门CODE等于住院部门CODE 则计数
                if(QUYCHK_OI.getValue("OUT_DEPT",j).equals(IPD_DEPT_CODE)){
                    if(QUYCHK_OI.getValue("QUYCHK_OI",j).equals("0")){//未做  不计算
                        continue;
                    }else if(QUYCHK_OI.getValue("QUYCHK_OI",j).equals("1")){//符合
                        DATA_02 = QUYCHK_OI.getInt("NUM",j);
                        DATA_01 += QUYCHK_OI.getInt("NUM",j);//门诊与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_OI.getValue("QUYCHK_OI",j).equals("2")){//不符合
                        DATA_03 = QUYCHK_OI.getInt("NUM",j);
                        DATA_01 += QUYCHK_OI.getInt("NUM",j);//门诊与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_OI.getValue("QUYCHK_OI",j).equals("3")){//不肯定 “未确诊”
                        DATA_04 = QUYCHK_OI.getInt("NUM",j);
                        DATA_01 += QUYCHK_OI.getInt("NUM",j);//门诊与出院 合计  累加同一部门的各种状态的人数
                    }
                }
            }
            //计算符合率 分母不可为零
            if((DATA_01-DATA_04)!=0){
                DATA_05 = (double) DATA_02 / (double) (DATA_01 - DATA_04) * 100;
                //计算不符合率
                DATA_06 = (double) DATA_03 / (double) (DATA_01 - DATA_04) * 100;
            }
            //循环统计“入院与出院”的数据
            for(int j=0;j<QUYCHK_INOUT.getCount();j++){
                //如果出院部门CODE等于住院部门CODE 则计数
                if(QUYCHK_INOUT.getValue("OUT_DEPT",j).equals(IPD_DEPT_CODE)){
//                    &&STATION_CODE.equals(QUYCHK_INOUT.getValue("OUT_STATION",j))){
                    if(QUYCHK_INOUT.getValue("QUYCHK_INOUT",j).equals("0")){//未做  不计算
                        continue;
                    }else if(QUYCHK_INOUT.getValue("QUYCHK_INOUT",j).equals("1")){//符合
                        DATA_08 = QUYCHK_INOUT.getInt("NUM",j);
                        DATA_07 += QUYCHK_INOUT.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_INOUT.getValue("QUYCHK_INOUT",j).equals("2")){//不符合
                        DATA_09 = QUYCHK_INOUT.getInt("NUM",j);
                        DATA_07 += QUYCHK_INOUT.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_INOUT.getValue("QUYCHK_INOUT",j).equals("3")){//不肯定 “未确诊”
                        DATA_10 = QUYCHK_INOUT.getInt("NUM",j);
                        DATA_07 += QUYCHK_INOUT.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }
                }
            }
            //计算符合率 分母不可为零
            if((DATA_07-DATA_10)!=0){
                DATA_11 = (double) DATA_08 / (double) (DATA_07-DATA_10) * 100;
                //计算不符合率
                DATA_12 = (double) DATA_09 / (double) (DATA_07-DATA_10) * 100;
            }
            //循环统计“临床与病理”的数据
            for(int j=0;j<QUYCHK_CLPA.getCount();j++){
                //如果出院部门CODE等于住院部门CODE 则计数
                if(QUYCHK_CLPA.getValue("OUT_DEPT",j).equals(IPD_DEPT_CODE)){
//                    &&STATION_CODE.equals(QUYCHK_CLPA.getValue("OUT_STATION",j))){
                    if(QUYCHK_CLPA.getValue("QUYCHK_CLPA",j).equals("0")){//未做  累加到“未确诊”
                        continue;
                    }else if(QUYCHK_CLPA.getValue("QUYCHK_CLPA",j).equals("1")){//符合
                        DATA_14 = QUYCHK_CLPA.getInt("NUM",j);
                        DATA_13 += QUYCHK_CLPA.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_CLPA.getValue("QUYCHK_CLPA",j).equals("2")){//不符合
                        DATA_15 = QUYCHK_CLPA.getInt("NUM",j);
                        DATA_13 += QUYCHK_CLPA.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_CLPA.getValue("QUYCHK_CLPA",j).equals("3")){//不肯定 累加到“未确诊”
                        DATA_16 = QUYCHK_CLPA.getInt("NUM",j);
                        DATA_13 += QUYCHK_CLPA.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }
                }
            }
            //计算符合率 分母不可为零
            if((DATA_13-DATA_16)!=0){
                DATA_17 = (double) DATA_14 / (double) (DATA_13-DATA_16) * 100;
                //计算不符合率
                DATA_18 = (double) DATA_15 / (double) (DATA_13-DATA_16) * 100;
            }
            //循环统计“手术前与手术后”的数据
            for(int j=0;j<QUYCHK_OPBFAF.getCount();j++){
                //如果出院部门CODE等于住院部门CODE 则计数
                if(QUYCHK_OPBFAF.getValue("OUT_DEPT",j).equals(IPD_DEPT_CODE)){
//                    &&STATION_CODE.equals(QUYCHK_OPBFAF.getValue("OUT_STATION",j))){
                    if(QUYCHK_OPBFAF.getValue("QUYCHK_OPBFAF",j).equals("0")){//未做  不计算
                        continue;
                    }else if(QUYCHK_OPBFAF.getValue("QUYCHK_OPBFAF",j).equals("1")){//符合
                        DATA_20 = QUYCHK_OPBFAF.getInt("NUM",j);
                        DATA_19 += QUYCHK_OPBFAF.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_OPBFAF.getValue("QUYCHK_OPBFAF",j).equals("2")){//不符合
                        DATA_21 = QUYCHK_OPBFAF.getInt("NUM",j);
                        DATA_19 += QUYCHK_OPBFAF.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }else if(QUYCHK_OPBFAF.getValue("QUYCHK_OPBFAF",j).equals("3")){//不肯定 “未确诊”
                        DATA_22 += QUYCHK_OPBFAF.getInt("NUM",j);
                        DATA_19 += QUYCHK_OPBFAF.getInt("NUM",j);//入院与出院 合计  累加同一部门的各种状态的人数
                    }
                }
            }
            //计算符合率 分母不可为零
            if((DATA_19-DATA_22)!=0){
                DATA_23 = (double) DATA_20 / (double) (DATA_19 - DATA_22) * 100;
                //计算不符合率
                DATA_24 = (double) DATA_21 / (double) (DATA_19 - DATA_22) * 100;
            }
            //出院人数
            for (int j = 0; j < out.getCount("OUT"); j++) {
				if(out.getData("OUT", j) != null && out.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)){
					outCount += out.getInt("OUT", j);
					break;
				}
			}
            //填充数据
            result.addData("STA_DATE",StartDate + "-" + EndDate);
            result.addData("DEPT_CODE",deptCode);
            result.addData("STATION_CODE",null==stationCode || stationCode.length()==0? IPD_DEPT_CODE:stationCode);//=========pangben modify 20110524
            result.addData("DATA_01",DATA_01==0?"":DATA_01);
            result.addData("DATA_02",DATA_02==0?"":DATA_02);
            result.addData("DATA_03",DATA_03==0?"":DATA_03);
            result.addData("DATA_04",DATA_04==0?"":DATA_04);
            result.addData("DATA_05",DATA_05==0?"":df.format(DATA_05));
            result.addData("DATA_06",DATA_06==0?"":df.format(DATA_06));
            result.addData("DATA_07",DATA_07==0?"":DATA_07);
            result.addData("DATA_08",DATA_08==0?"":DATA_08);
            result.addData("DATA_09",DATA_09==0?"":DATA_09);
            result.addData("DATA_10",DATA_10==0?"":DATA_10);
            result.addData("DATA_11",DATA_11==0?"":df.format(DATA_11));
            result.addData("DATA_12",DATA_12==0?"":df.format(DATA_12));
            result.addData("DATA_13",DATA_13==0?"":DATA_13);
            result.addData("DATA_14",DATA_14==0?"":DATA_14);
            result.addData("DATA_15",DATA_15==0?"":DATA_15);
            result.addData("DATA_16",DATA_16==0?"":DATA_16);
            result.addData("DATA_17",DATA_17==0?"":df.format(DATA_17));
            result.addData("DATA_18",DATA_18==0?"":df.format(DATA_18));
            result.addData("DATA_19",DATA_19==0?"":DATA_19);
            result.addData("DATA_20",DATA_20==0?"":DATA_20);
            result.addData("DATA_21",DATA_21==0?"":DATA_21);
            result.addData("DATA_22",DATA_22==0?"":DATA_22);
            result.addData("DATA_23",DATA_23==0?"":df.format(DATA_23));
            result.addData("DATA_24",DATA_24==0?"":df.format(DATA_24));
            result.addData("DATA_25",outCount == 0? "" : outCount);
            result.addData("CONFIRM_FLG","N");
            result.addData("CONFIRM_USER","");
            result.addData("CONFIRM_DATE","");
            result.addData("OPT_USER","");
            result.addData("OPT_TERM","");
        }
        return result;
    }

	/**
     * 删除表STA_IN_04数据
     * @param STA_DATE String
     * @return TParm
     * ================pangben modify 20110524 添加区域参数
     */
    public TParm deleteSTA_IN_04(String STA_DATE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //================pangben modify 20110524 start
        parm.setData("REGION_CODE",regionCode);
        //================pangben modify 20110524 stop
        TParm result = this.update("deleteSTA_IN_04",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入STA_IN_04数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_04(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_IN_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 导入STA_IN_04表信息
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
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE不可为空");
            return result;
        }
        //===========pangben modify 20110524
        result = this.deleteSTA_IN_04(STA_DATE, parm.getValue("REGION_CODE",0),  conn); //删除该日期的数据
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
            insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
            //=============pangben modfiy 20110524 start
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
            //=============pangben modfiy 20110524 stop
            result = this.insertSTA_IN_04(insert, conn); //插入新数据
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * 修改STA_IN_04数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_IN_04(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询STA_IN_04表数据
     * @param STA_DATE String
     * @return TParm
     */
    public TParm selectSTA_IN_04(String STA_DATE) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        TParm result = this.query("selectSTA_IN_04", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
