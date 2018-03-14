package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: STA_IN_10住院医师医疗质量</p>
 *
 * <p>Description: STA_IN_10住院医师医疗质量</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-25
 * @version 1.0
 */
public class STAIn_10Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_10Tool instanceObject;

    /**
     * 得到实例
     * @return STAIn_10Tool
     */
    public static STAIn_10Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_10Tool();
        return instanceObject;
    }

    public STAIn_10Tool() {
        setModuleName("sta\\STAIn_10Module.x");
        onInit();
    }
    /**
     * 查询指定SQL的数据
     * @param type String  指定sql语句名称
     * @param DATE_S String 起始日期 格式yyyyMMdd
     * @param DATE_E String 截止日期 格式yyyyMMdd
     * @return TParm
     * ============pangben modify 20110525 添加区域参数
     */
    public TParm selectDataT(String type,String DATE_S,String DATE_E,String regionCode){
        TParm parm = new TParm();
        parm.setData("DATE_S",DATE_S);
        parm.setData("DATE_E",DATE_E);
        //============pangben modify 20110525 start
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //============pangben modify 20110525 stop
        TParm result = this.query(type,parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 整合数据
     * @param DATE_S String
     * @param DATE_E String
     * @param dr_list TParm
     * @return TParm
     * ==========pangben modify 20110525  添加区域参数
     */
    public TParm selectData(String DATE_S,String DATE_E,TParm dr_list,String regionCode){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        //出院总人次,平均住院日
        TParm selectDATA_01 = this.selectDataT("selectDATA_01",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_01.getErrCode() < 0) {
            err("ERR:" + selectDATA_01.getErrCode() + selectDATA_01.getErrText() +
                selectDATA_01.getErrName());
            return selectDATA_01;
        }
        //治愈人数
        TParm selectDATA_03 = this.selectDataT("selectDATA_03",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_03.getErrCode() < 0) {
            err("ERR:" + selectDATA_03.getErrCode() + selectDATA_03.getErrText() +
                selectDATA_03.getErrName());
            return selectDATA_03;
        }
        //好转人数
        TParm selectDATA_05 = this.selectDataT("selectDATA_05",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_05.getErrCode() < 0) {
            err("ERR:" + selectDATA_05.getErrCode() + selectDATA_05.getErrText() +
                selectDATA_05.getErrName());
            return selectDATA_05;
        }
        //死亡人数
        TParm selectDATA_07 = this.selectDataT("selectDATA_07",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_07.getErrCode() < 0) {
            err("ERR:" + selectDATA_07.getErrCode() + selectDATA_07.getErrText() +
                selectDATA_07.getErrName());
            return selectDATA_07;
        }
        //未愈人数
        TParm selectDATA_09 = this.selectDataT("selectDATA_09",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_09.getErrCode() < 0) {
            err("ERR:" + selectDATA_09.getErrCode() + selectDATA_09.getErrText() +
                selectDATA_09.getErrName());
            return selectDATA_09;
        }
        //其他人数
        TParm selectDATA_11 = this.selectDataT("selectDATA_11",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_11.getErrCode() < 0) {
            err("ERR:" + selectDATA_11.getErrCode() + selectDATA_11.getErrText() +
                selectDATA_11.getErrName());
            return selectDATA_11;
        }
        ////费用合计
        //modify by wanglong 20131012
//        TParm selectDATA_13 = this.selectDataT("selectDATA_13",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        TParm parm = new TParm();
        parm.setData("START_DATE", DATE_S);
        parm.setData("END_DATE", DATE_E);
        parm.setData("REGION_CODE", regionCode);
        parm.setData("RECP_TYPE", "IBS");
        TParm selectDATA_13 = selectDrHospCharge(parm);
        //modify end
        if (selectDATA_13.getErrCode() < 0) {
            err("ERR:" + selectDATA_13.getErrCode() + selectDATA_13.getErrText() +
                selectDATA_13.getErrName());
            return selectDATA_13;
        }
        // 大于90 分病历数
        TParm selectDATA_17 = this.selectDataT("selectDATA_17",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_17.getErrCode() < 0) {
            err("ERR:" + selectDATA_17.getErrCode() + selectDATA_17.getErrText() +
                selectDATA_17.getErrName());
            return selectDATA_17;
        }
        // 大于80 分病历数
        TParm selectDATA_18 = this.selectDataT("selectDATA_18",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_18.getErrCode() < 0) {
            err("ERR:" + selectDATA_18.getErrCode() + selectDATA_18.getErrText() +
                selectDATA_18.getErrName());
            return selectDATA_18;
        }
        // 大于70 分病历数
        TParm selectDATA_19 = this.selectDataT("selectDATA_19",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_19.getErrCode() < 0) {
            err("ERR:" + selectDATA_19.getErrCode() + selectDATA_19.getErrText() +
                selectDATA_19.getErrName());
            return selectDATA_19;
        }
        //70 分以下病历数
        TParm selectDATA_20 = this.selectDataT("selectDATA_20",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_20.getErrCode() < 0) {
            err("ERR:" + selectDATA_20.getErrCode() + selectDATA_20.getErrText() +
                selectDATA_20.getErrName());
            return selectDATA_20;
        }
        //病历平均分
        TParm selectDATA_21 = this.selectDataT("selectDATA_21",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectDATA_21.getErrCode() < 0) {
            err("ERR:" + selectDATA_21.getErrCode() + selectDATA_21.getErrText() +
                selectDATA_21.getErrName());
            return selectDATA_21;
        }
        //病历抽查次数
        TParm selectCheckNum = this.selectDataT("selectCheckNum",DATE_S,DATE_E,regionCode);//==pangben modify 20110525
        if (selectCheckNum.getErrCode() < 0) {
            err("ERR:" + selectCheckNum.getErrCode() + selectCheckNum.getErrText() +
                selectCheckNum.getErrName());
            return selectCheckNum;
        }
        int TDATA_01 = 0;
        double TDATA_02 = 0;
        int TDATA_03 = 0;
        double TDATA_04 = 0;
        int TDATA_05 = 0;
        double TDATA_06 = 0;
        int TDATA_07 = 0;
        double TDATA_08 = 0;
        int TDATA_09 = 0;
        double TDATA_10 = 0;
        int TDATA_11 = 0;
        double TDATA_12 = 0;
        double TDATA_13 = 0;
        double TDATA_14 = 0;
        double TDATA_15 = 0;
        double TDATA_16 = 0;
        int TDATA_17 = 0;
        int TDATA_18 = 0;
        int TDATA_19 = 0;
        int TDATA_20 = 0;
        double TDATA_21 = 0;
        double TDATA_22 = 0;
        double TDATA_23 = 0;//药费
        for(int i=0;i<dr_list.getCount("USER_ID");i++){
            String dr_code = dr_list.getValue("USER_ID", i); //获取医师CODE
            String dr_name = dr_list.getValue("USER_NAME", i); //获取医师姓名
            //定义各个字段的变量
            int DATA_01 = 0;
            double DATA_02 = 0;
            int DATA_03 = 0;
            double DATA_04 = 0;
            int DATA_05 = 0;
            double DATA_06 = 0;
            int DATA_07 = 0;
            double DATA_08 = 0;
            int DATA_09 = 0;
            double DATA_10 = 0;
            int DATA_11 = 0;
            double DATA_12 = 0;
            double DATA_13 = 0;
            double DATA_14 = 0;
            double DATA_15 = 0;
            double DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            int DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;//药费

            //循环提取数据
            for(int j=0;j<selectDATA_01.getCount();j++){
                if(selectDATA_01.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_01 = selectDATA_01.getInt("NUM",j);//出院总人次
                    DATA_02 = selectDATA_01.getDouble("INDAYS",j);//平均住院日
                }
            }
            for(int j=0;j<selectDATA_03.getCount();j++){
                if(selectDATA_03.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_03 = selectDATA_03.getInt("NUM",j);//治愈人数
                }
            }
            for(int j=0;j<selectDATA_05.getCount();j++){
                if(selectDATA_05.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_05 = selectDATA_05.getInt("NUM",j);//好转人数
                }
            }
            for(int j=0;j<selectDATA_07.getCount();j++){
                if(selectDATA_07.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_07 = selectDATA_07.getInt("NUM",j);//死亡人数
                }
            }
            for(int j=0;j<selectDATA_09.getCount();j++){
                if(selectDATA_09.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_09 = selectDATA_09.getInt("NUM",j);//未愈人数
                }
            }
            for(int j=0;j<selectDATA_11.getCount();j++){
                if(selectDATA_11.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_11 = selectDATA_11.getInt("NUM",j);//其他人数
                }
            }
            for(int j=0;j<selectDATA_13.getCount();j++){
                if(selectDATA_13.getValue("DR_CODE", j).equals(dr_code)) {//modify by wanglong 20131012
                    DATA_13 = selectDATA_13.getDouble("CHARGE_01",j)+
                        selectDATA_13.getDouble("CHARGE_03",j)+
                        selectDATA_13.getDouble("CHARGE_04",j)+
                        selectDATA_13.getDouble("CHARGE_05",j)+
                        selectDATA_13.getDouble("CHARGE_06",j)+
                        selectDATA_13.getDouble("CHARGE_07",j)+
                        selectDATA_13.getDouble("CHARGE_08",j)+
                        selectDATA_13.getDouble("CHARGE_10",j)+
                        selectDATA_13.getDouble("CHARGE_11",j);//费用合计
                    DATA_14 = selectDATA_13.getDouble("CHARGE_01",j);//床位费
                    DATA_15 = selectDATA_13.getDouble("CHARGE_10",j);//手术费
                    DATA_16 = selectDATA_13.getDouble("CHARGE_07",j)+
                              selectDATA_13.getDouble("CHARGE_08",j)+
                        selectDATA_13.getDouble("CHARGE_11",j);//检查治疗费 = 化验费+检查费+治疗费
                    DATA_23 = selectDATA_13.getDouble("CHARGE_03",j)+
                        selectDATA_13.getDouble("CHARGE_04",j)+
                        selectDATA_13.getDouble("CHARGE_05",j)+
                        selectDATA_13.getDouble("CHARGE_06",j);//药费 
//                    DATA_13 = DATA_13 / DATA_01;
//                    DATA_14 = DATA_14 / DATA_01;
//                    DATA_15 = DATA_15 / DATA_01;
//                    DATA_15 = DATA_15 / DATA_01;
//                    DATA_23 = DATA_23 / DATA_01;
                }
            }
            for(int j=0;j<selectDATA_17.getCount();j++){
                if(selectDATA_17.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_17 = selectDATA_17.getInt("NUM",j);//大于90 分病历数
                }
            }
            for(int j=0;j<selectDATA_18.getCount();j++){
                if(selectDATA_18.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_18 = selectDATA_18.getInt("NUM",j);//大于80 分病历数
                }
            }
            for(int j=0;j<selectDATA_19.getCount();j++){
                if(selectDATA_19.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_19 = selectDATA_19.getInt("NUM",j);//大于70 分病历数
                }
            }
            for(int j=0;j<selectDATA_20.getCount();j++){
                if(selectDATA_20.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_20 = selectDATA_20.getInt("NUM",j);//70 分以下病历数
                }
            }
            for(int j=0;j<selectDATA_21.getCount();j++){
                if(selectDATA_21.getValue("DR_CODE", j).equals(dr_code)) {
                    DATA_21 = selectDATA_21.getInt("NUM",j);//病历平均分
                }
            }
            for(int j=0;j<selectCheckNum.getCount();j++){
                if(selectCheckNum.getValue("DR_CODE", j).equals(dr_code)) {
                    if(DATA_01!=0)
                        DATA_22 = (double)selectCheckNum.getInt("NUM",j)/(double)DATA_01*100;//抽查率
                }
            }
            if(DATA_01!=0){
                DATA_04 = (double)DATA_03/(double)DATA_01*100;//治愈率
                DATA_06 = (double)DATA_05/(double)DATA_01*100;//好转率
                DATA_08 = (double)DATA_07/(double)DATA_01*100;//死亡率
                DATA_10 = (double)DATA_09/(double)DATA_01*100;//未愈率
                DATA_12 = (double)DATA_11/(double)DATA_01*100;//其他率
            }
            //填充数据
            result.addData("DR_NAME", dr_name);
            result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            result.addData("DATA_02", DATA_02 == 0 ? "" : df.format(DATA_02));
            result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            result.addData("DATA_04", DATA_04 == 0 ? "" : df.format(DATA_04));
            result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            result.addData("DATA_06", DATA_06 == 0 ? "" : df.format(DATA_06));
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : df.format(DATA_08));
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : df.format(DATA_10));
            result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
            result.addData("DATA_12", DATA_12 == 0 ? "" : df.format(DATA_12));
            result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
            result.addData("DATA_14", DATA_14 == 0 ? "" : df.format(DATA_14));
            result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
            result.addData("DATA_16", DATA_16 == 0 ? "" : df.format(DATA_16));
            result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
            result.addData("DATA_21", DATA_21 == 0 ? "" : df.format(DATA_21));
            result.addData("DATA_22", DATA_22 == 0 ? "" : df.format(DATA_22));
            result.addData("DATA_23", DATA_23 == 0 ? "" : df.format(DATA_23));
            TDATA_01 += DATA_01;
            TDATA_02 += DATA_02;
            TDATA_03 += DATA_03;
            TDATA_05 += DATA_05;
            TDATA_07 += DATA_07;
            TDATA_09 += DATA_09;
            TDATA_11 += DATA_11;
            TDATA_13 += DATA_13;
            TDATA_14 += DATA_14;
            TDATA_15 += DATA_15;
            TDATA_16 += DATA_16;
            TDATA_17 += DATA_17;
            TDATA_18 += DATA_18;
            TDATA_19 += DATA_19;
            TDATA_20 += DATA_20;    
            TDATA_21 += DATA_21;
            TDATA_22 += DATA_22;
            TDATA_23 += DATA_23;
        }
        if(TDATA_01!=0){
            TDATA_04 = (double)TDATA_03/(double)TDATA_01*100;//治愈率
            TDATA_06 = (double)TDATA_05/(double)TDATA_01*100;//好转率
            TDATA_08 = (double)TDATA_07/(double)TDATA_01*100;//死亡率
            TDATA_10 = (double)TDATA_09/(double)TDATA_01*100;//未愈率
            TDATA_12 = (double)TDATA_11/(double)TDATA_01*100;//其他率
        }
        result.addData("DR_NAME", "合计:");
        result.addData("DATA_01", TDATA_01 == 0 ? "" : TDATA_01);
        result.addData("DATA_02", TDATA_02 == 0 ? "" : df.format(TDATA_02));
        result.addData("DATA_03", TDATA_03 == 0 ? "" : TDATA_03);
        result.addData("DATA_04", TDATA_04 == 0 ? "" : df.format(TDATA_04));
        result.addData("DATA_05", TDATA_05 == 0 ? "" : TDATA_05);
        result.addData("DATA_06", TDATA_06 == 0 ? "" : df.format(TDATA_06));
        result.addData("DATA_07", TDATA_07 == 0 ? "" : TDATA_07);
        result.addData("DATA_08", TDATA_08 == 0 ? "" : df.format(TDATA_08));
        result.addData("DATA_09", TDATA_09 == 0 ? "" : TDATA_09);
        result.addData("DATA_10", TDATA_10 == 0 ? "" : df.format(TDATA_10));
        result.addData("DATA_11", TDATA_11 == 0 ? "" : TDATA_11);
        result.addData("DATA_12", TDATA_12 == 0 ? "" : df.format(TDATA_12));
        result.addData("DATA_13", TDATA_13 == 0 ? "" : df.format(TDATA_13));
        result.addData("DATA_14", TDATA_14 == 0 ? "" : df.format(TDATA_14));
        result.addData("DATA_15", TDATA_15 == 0 ? "" : df.format(TDATA_15));
        result.addData("DATA_16", TDATA_16 == 0 ? "" : df.format(TDATA_16));
        result.addData("DATA_17", TDATA_17 == 0 ? "" : TDATA_17);
        result.addData("DATA_18", TDATA_18 == 0 ? "" : TDATA_18);
        result.addData("DATA_19", TDATA_19 == 0 ? "" : TDATA_19);
        result.addData("DATA_20", TDATA_20 == 0 ? "" : TDATA_20);
        result.addData("DATA_21", TDATA_21 == 0 ? "" : df.format(TDATA_21));
        result.addData("DATA_22", TDATA_22 == 0 ? "" : df.format(TDATA_22));
        result.addData("DATA_23", TDATA_23 == 0 ? "" : df.format(TDATA_23));
        return result;
    }
    
    /**
     * 根据IBS系统返回的住院费用
     * 
     */
    public TParm selectDrHospCharge(TParm parm) {//add by wanglong 20131012
        TParm hospCharge = this.query("selectHospCharge", parm);
        if (hospCharge.getErrCode() < 0) {
            err("ERR:" + hospCharge.getErrCode() + hospCharge.getErrText() + hospCharge.getErrName());
            return hospCharge;
        }
        TParm chargeMapping = this.query("selectChargeMapping", parm);
        if (chargeMapping.getErrCode() < 0) {
            err("ERR:" + chargeMapping.getErrCode() + chargeMapping.getErrText()
                    + chargeMapping.getErrName());
            return chargeMapping;
        }
        Map chargeMap = new HashMap();
        for (int i = 0; i < chargeMapping.getNames().length; i++) {
            chargeMap.put(chargeMapping.getData(chargeMapping.getNames()[i], 0),chargeMapping.getNames()[i].replaceFirst("CHARGE", "CHARGE_"));
        }
        TParm result = new TParm();
        for (int i = 0; i < hospCharge.getCount(); i++) {
            if (chargeMap.containsKey(hospCharge.getData("IPD_CHARGE_CODE", i))) {
                hospCharge.setData("CHARGE_CODE", i, chargeMap.get(hospCharge.getData("IPD_CHARGE_CODE", i)));
            }
        }
        for (int i = 0; i < hospCharge.getCount(); i++) {
            if ((i == 0) || !hospCharge.getValue("DR_CODE", i).equals(hospCharge.getValue("DR_CODE", i - 1))) {
                for (int j = 0; j < 30; j++) {
                    result.addData("CHARGE_" + StringTool.fill("0", 2 - (j + "").length()) + j, 0);
                }
                result.addData("DR_CODE", hospCharge.getData("DR_CODE", i));
            }
            result.setData(hospCharge.getValue("CHARGE_CODE", i), result.getCount("CHARGE_01")-1, 
                           result.getDouble(hospCharge.getValue("CHARGE_CODE", i), result.getCount("CHARGE_01")-1) + hospCharge.getDouble("OWN_AMT", i));
        }
        result.setCount(result.getCount("DR_CODE"));
        return result;
    }
    
    /**
	 * 根据科室查询医师
	 */
	public TParm selectDrByDept(String dept_code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", dept_code);
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		TParm result = this.query("selectDrByDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询医师列表
	 */
	public TParm selectDr(String regionCode) {
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		TParm result = this.query("selectDr", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
