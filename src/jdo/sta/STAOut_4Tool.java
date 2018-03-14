package jdo.sta;

import java.util.HashMap;
import java.util.Map;

import jdo.ibs.IBSOrderdTool;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Description: 卫生部门县及县以上医院经费及收支情况报表（卫统2表3）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_4Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOut_4Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOut_4Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_4Tool();
        return instanceObject;
    }

    public STAOut_4Tool() {
        setModuleName("sta\\STAOut_4Module.x");
        onInit();
    }

    /**
     * 查询STA_OUT_01数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OUT_01(TParm parm) {
        TParm result = this.query("selectSTA_OUT_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询门诊费用信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOPB_RECEIPT(TParm parm) {
        TParm result = this.query("selectOPB_RECEIPT", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询挂号费用信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectREG_RECEIPT(TParm parm) {
        TParm result = this.query("selectREG_RECEIPT", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
	 * 根据IBS系统返回的住院费用
	 * 
	 */
	public TParm selectMRO_RECORD(TParm inparm) {
		TParm ibs = this.query("selectMRO_RECORD", inparm);
		if (ibs.getErrCode() < 0) {
			err("ERR:" + ibs.getErrCode() + ibs.getErrText() + ibs.getErrName());
			return ibs;
		}
		// 以键值对的形式存储财务数据
		Map charge = new HashMap();
		double sumTot = 0;
		double ownTot = 0;
		Map MrofeeCode = STATool.getInstance().getIBSChargeName();
		for (int i = 0; i < ibs.getCount(); i++) {
			if (ibs.getValue("IPD_CHARGE_CODE", i).length() > 0) {
				charge.put(ibs.getValue("IPD_CHARGE_CODE", i), ibs.getValue("TOT_AMT", i));
			}
		}
		String seq = "";
		String c_name = "";
		TParm parm = new TParm();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// I小于10 补零
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			parm.setData(c_name,
					charge.get(MrofeeCode.get(c_name)) == null ? 0 : charge
							.get(MrofeeCode.get(c_name)));
		}
        return parm;
    }
    /**
     * 生成要插入STA_OUT_04表的数据
     * @param sta_date String
     * @return TParm
     */
    public TParm selectData(String sta_date,String regionCode) {
        TParm result = new TParm();
        //检核参数
        if (sta_date.trim().length() <= 0) {
            result.setErr( -1, "参数不可为空");
            return result;
        }
        String dateStart = sta_date + "01";
        String dateEnd = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(sta_date),
                                              "yyyyMMdd");
        TParm parm1 = new TParm();
        parm1.setData("STA_DATE", sta_date);
        //=========pangben modify 20110523 start 添加区域条件
        parm1.setData("REGION_CODE", regionCode);
        TParm STA_OUT_01 = this.selectSTA_OUT_01(parm1); //STA_OUT_01信息
        if (STA_OUT_01.getErrCode() < 0) {
            err("ERR:" + STA_OUT_01.getErrCode() + STA_OUT_01.getErrText() +
                STA_OUT_01.getErrName());
            return STA_OUT_01;
        }
        if (STA_OUT_01.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "此月份的“卫统2表1”并未提交");
            return result;
        }
        TParm parm2 = new TParm();
        parm2.setData("CHARGE_DATE_START", dateStart); //月起始日期
        parm2.setData("CHARGE_DATE_END", dateEnd); //月最后一天日期
        parm2.setData("ADM_TYPE", "O");
         //=========pangben modify 20110523 start 添加区域条件
        parm2.setData("REGION_CODE", regionCode);
        TParm OPB = this.selectOPB_RECEIPT(parm2); //门诊费用信息
        if (OPB.getErrCode() < 0) {
            err("ERR:" + OPB.getErrCode() + OPB.getErrText() +
                OPB.getErrName());
            return OPB;
        }
        TParm REG = this.selectREG_RECEIPT(parm2); //挂号费用信息
        if (REG.getErrCode() < 0) {
            err("ERR:" + REG.getErrCode() + REG.getErrText() +
                REG.getErrName());
            return REG;
        }
        TParm parm3 = new TParm();
        parm3.setData("OUT_DATE_START", dateStart); //月起始日期
        parm3.setData("OUT_DATE_END", dateEnd); //月最后一天日期
        //=========pangben modify 20110523 start 添加区域条件
        parm3.setData("REGION_CODE", regionCode);
        TParm MRO = this.selectMRO_RECORD(parm3); //住院费用信息
        if (MRO.getErrCode() < 0) {
            err("ERR:" + MRO.getErrCode() + MRO.getErrText() +
                MRO.getErrName());
            return MRO;
        }
        //按照表结构生成数据集
        result.setData("STA_DATE", sta_date);
        result.setData("DATA_01", "1");
        result.setData("DATA_02", STA_OUT_01.getValue("DATA_18", 0)); //病床数
        result.setData("DATA_03", ""); //平均职工人数 (录入)
        result.setData("DATA_04", ""); //平均职工人数-医生(录入)
        //总诊疗人数  卫统2表1.<1> + <2>+<9>+<10>
        result.setData("DATA_05",
                       STA_OUT_01.getInt("DATA_01", 0) +
                       STA_OUT_01.getInt("DATA_02", 0) +
                       STA_OUT_01.getInt("DATA_09", 0) +
                       STA_OUT_01.getInt("DATA_10", 0));
        result.setData("DATA_06", STA_OUT_01.getValue("DATA_11", 0)); //出院人数   卫统2表1.<11>
        result.setData("DATA_07", STA_OUT_01.getValue("DATA_19", 0)); //实际开放总床日数   卫统2表1.<19>
        result.setData("DATA_08", STA_OUT_01.getValue("DATA_20", 0)); //平均开放病床数 卫统2表1.<20>
        result.setData("DATA_09", STA_OUT_01.getValue("DATA_21", 0)); //实际占用总床日数  卫统2表1.<21>
        result.setData("DATA_10", STA_OUT_01.getValue("DATA_22", 0)); //出院者占用总床日数 卫统2表1.<22>
        result.setData("DATA_11", STA_OUT_01.getValue("DATA_29", 0)); //出院者平均住院日  卫统2表1.<29>

        //门诊收费小计 sys_charge中的 17项费用总和 不计算药费 加上挂号费用
        double opbSum =OPB.getDouble("CHARGE05", 0) + OPB.getDouble("CHARGE06", 0) +
            OPB.getDouble("CHARGE07", 0) + OPB.getDouble("CHARGE08", 0) +
            OPB.getDouble("CHARGE09", 0) + OPB.getDouble("CHARGE10", 0) +
            OPB.getDouble("CHARGE11", 0) + OPB.getDouble("CHARGE12", 0) +
            OPB.getDouble("CHARGE13", 0) + OPB.getDouble("CHARGE14", 0) +
            OPB.getDouble("CHARGE15", 0) + OPB.getDouble("CHARGE17", 0) +
            OPB.getDouble("CHARGE18", 0) + OPB.getDouble("CHARGE19", 0) +
            REG.getDouble("REG_FEE_REAL", 0); //挂号费用

        result.setData("DATA_14", opbSum); //门诊收费小计
        result.setData("DATA_15", REG.getValue("REG_FEE_REAL", 0)); //挂号费
        result.setData("DATA_16", OPB.getValue("CHARGE9", 0)); //门诊检查费
        result.setData("DATA_17",
                       opbSum - OPB.getDouble("CHARGE9", 0) -
                       REG.getDouble("REG_FEE_REAL", 0)); //门诊其他费用
        
        //住院小计 不包含药费
        double admSum =MRO.getDouble("CHARGE01") + MRO.getDouble("CHARGE02") +
        MRO.getDouble("CHARGE07") + MRO.getDouble("CHARGE08") +
        MRO.getDouble("CHARGE09") + MRO.getDouble("CHARGE10") +
        MRO.getDouble("CHARGE11") + MRO.getDouble("CHARGE12") +
        MRO.getDouble("CHARGE13") + MRO.getDouble("CHARGE14") +
        MRO.getDouble("CHARGE15") + MRO.getDouble("CHARGE17") +
        MRO.getDouble("CHARGE18") + MRO.getDouble("CHARGE19")+ MRO.getDouble("CHARGE20");
        
        result.setData("DATA_18", admSum); //住院小计
        result.setData("DATA_19", MRO.getDouble("CHARGE01")); //床位费 
        result.setData("DATA_20", MRO.getDouble("CHARGE08")); //住院治疗费
        result.setData("DATA_21", MRO.getDouble("CHARGE07")); //住院检查费
        result.setData("DATA_22",admSum-(MRO.getDouble("CHARGE01")+MRO.getDouble("CHARGE07")+MRO.getDouble("CHARGE08"))); //住院其他费用
        //药品住院收入
        double admDrug = (MRO.getDouble("CHARGE06") + MRO.getDouble("CHARGE03") +
                MRO.getDouble("CHARGE04") + MRO.getDouble("CHARGE05"));
        double opdDrug =  OPB.getDouble("CHARGE01", 0)+OPB.getDouble("CHARGE02", 0) +
            OPB.getDouble("CHARGE03", 0) + OPB.getDouble("CHARGE04", 0);
        result.setData("DATA_23", admDrug + opdDrug);
        result.setData("DATA_24", admDrug); //药品住院收入
        result.setData("DATA_25", opdDrug); //药品门诊收入

        result.setData("DATA_12", opbSum + admSum + admDrug + opdDrug); //收入总计<12>
        result.setData("DATA_13", opbSum + admSum + admDrug + opdDrug); //业务收入合计

        result.setData("DATA_26", ""); //业务补助(录入)
        result.setData("DATA_27", ""); //专项补助(录入)
        result.setData("DATA_28", ""); //其他收入(录入)
        result.setData("DATA_29", ""); //支出总计
        result.setData("DATA_30", ""); //业务支出总计
        result.setData("DATA_31", ""); //人员经费小计(录入)
        result.setData("DATA_32", ""); //离退休人员经费(录入)
        result.setData("DATA_33", ""); //药品费(录入)
        result.setData("DATA_34", ""); //其他业务支出(录入)
        result.setData("DATA_35", ""); //其他支出(录入)
        result.setData("DATA_36", ""); //固定资产总金额合计(录入)
        result.setData("DATA_37", ""); //专业设备总金额(录入)
        result.setData("DATA_38", ""); //平均每床占固定资产 合计
        result.setData("DATA_39", ""); //每床占专业设备总金额
        result.setData("DATA_40", ""); //期内病人欠费总额(录入)
        result.setData("DATA_41", ""); //欠费率%
        //平均每天诊疗人次=总诊疗人次/月份数  DATA_05/月天数
        int days = STATool.getInstance().getDaysOfMonth(sta_date);
        if (days > 0)
            result.setData("DATA_42", (STA_OUT_01.getInt("DATA_01", 0) +
                                       STA_OUT_01.getInt("DATA_02", 0) +
                                       STA_OUT_01.getInt("DATA_09", 0) +
                                       STA_OUT_01.getInt("DATA_10", 0)) / days);
        else
            result.setData("DATA_42", "");
        result.setData("DATA_43", STA_OUT_01.getValue("DATA_28", 0)); //每床使用率  卫统2表1.<28>
        //病床周转次数  =出院人数/平均开放病床数
        if(STA_OUT_01.getDouble("DATA_20", 0)!=0)
            result.setData("DATA_44", STA_OUT_01.getDouble("DATA_11", 0)/STA_OUT_01.getDouble("DATA_20", 0));
        else
            result.setData("DATA_44", "");
        result.setData("DATA_45", STA_OUT_01.getValue("DATA_29", 0)); //目前暂时设定 等于住院天数 出院者平均住院日  卫统2表1.<29>
        result.setData("DATA_46", ""); //平均药品加成率(%)(录入)
        if (result.getDouble("DATA_05") != 0) {
            //平均每诊疗人次医疗费 药费
            result.setData("DATA_48",result.getDouble("DATA_25") /result.getDouble("DATA_05"));
            //平均每诊疗人次医疗费 检查费
            result.setData("DATA_49",result.getDouble("DATA_16") /result.getDouble("DATA_05"));
        }
        else {
            result.setData("DATA_48", "");
            result.setData("DATA_49", "");
        }
        if(result.getDouble("DATA_05")!=0){
            //平均每诊疗人次医疗费 检查费
            result.setData("DATA_47",
                           (result.getDouble("DATA_14") +
                            result.getDouble("DATA_25")) /
                           result.getDouble("DATA_05"));
        }
        else
            result.setData("DATA_47","");

        if (result.getDouble("DATA_06") != 0) {
            //平均每一出院者医疗费 床位费
            result.setData("DATA_51",
                           result.getDouble("DATA_19") /
                           result.getDouble("DATA_06"));
            //平均每一出院者医疗费 药费
            result.setData("DATA_52",
                           result.getDouble("DATA_24") /
                           result.getDouble("DATA_06"));
            //平均每一出院者医疗费 治疗费
            result.setData("DATA_53",
                           result.getDouble("DATA_20") /
                           result.getDouble("DATA_06"));
            //平均每一出院者医疗费 检查费
            result.setData("DATA_54",
                           result.getDouble("DATA_21") /
                           result.getDouble("DATA_06"));
        }
        else {
            result.setData("DATA_51", "");
            result.setData("DATA_52", "");
            result.setData("DATA_53", "");
            result.setData("DATA_54", "");
        }
        //平均每一出院者医疗费 合计
        result.setData("DATA_50",
                       result.getDouble("DATA_51") + result.getDouble("DATA_52") +
                       result.getDouble("DATA_53") + result.getDouble("DATA_54"));
        if (result.getDouble("DATA_10") != 0) {
            //出院者平均每天住院医疗费 【住院收入小计（DATA_18）+药品收入住院收入（DATA_24）】/出院者占用总床日数（DATA_10）
            result.setData("DATA_55",(result.getDouble("DATA_18")+result.getDouble("DATA_24"))/result.getDouble("DATA_10"));
        }else{
            result.setData("DATA_55","");
        }
//        TParm DrNum = this.selectDrNum(); //查询全院医生人数
//        TParm OpNum = this.selectOperatorNum(); //查询全院职工人数
//        double DrN = DrNum.getDouble("NUM", 0);
//        double OpN = OpNum.getDouble("NUM", 0);
        double OpN = 0;//全院职工人数 (手动录入)
        double DrN = 0;//全院医生人数 (手动录入)
        if (OpN != 0) {
            result.setData("DATA_56", result.getDouble("DATA_05") / OpN); //平均每一职工期内负担的诊疗人次
            result.setData("DATA_57", result.getDouble("DATA_10") / OpN); //期内负担的住院床日数
            result.setData("DATA_58", result.getDouble("DATA_13") / OpN); //期内业务收入
        }
        else {
            result.setData("DATA_56", "");
            result.setData("DATA_57", "");
            result.setData("DATA_58", "");
        }
        if (DrN != 0) {
            result.setData("DATA_59", result.getDouble("DATA_05") / DrN); //平均每一医生期内负担的诊疗人次
            result.setData("DATA_60", result.getDouble("DATA_10") / DrN); //期内负担的住院床日数
            result.setData("DATA_61", result.getDouble("DATA_13") / DrN); //期内业务收入
        }
        else {
            result.setData("DATA_59", "");
            result.setData("DATA_60", "");
            result.setData("DATA_61", "");
        }
        result.setData("CONFIRM_FLG", "");
        result.setData("CONFIRM_USER", "");
        result.setData("CONFIRM_DATE", "");
        result.setData("OPT_USER", "");
        result.setData("OPT_TERM", "");
        return result;
    }

    /**
     * 删除STA_OUT_04表数据
     * @param sta_date String
     * @return TParm
     */
    public TParm deleteSTA_OUT_04(String sta_date,String regionCode, TConnection conn) {
        TParm result = new TParm();
        if (sta_date.trim().length() <= 0) {
            result.setErr( -1, "参数不可为空");
        }
        TParm parm = new TParm();
        parm.setData("STA_DATE", sta_date);
        //======pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //======pangben modify 20110523 stop
        result = this.update("deleteSTA_OUT_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 添加STA_OUT_04表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_04(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_OUT_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 导入STA_OUT_04表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        String sta_date = parm.getValue("STA_DATE");
        //=============pangben modify 20110523 start
        String regionCode=parm.getValue("REGION_CODE");
        //=============pangben modify 20110523 stop
        TParm result = new TParm();
        result = this.deleteSTA_OUT_04(sta_date,regionCode, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_OUT_04(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改STA_OUT_04表信息 传入SQL数组
     * @param sql String[]
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_04bySQL(String[] sql, TConnection conn) {
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改STA_OUT_04中的数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_04(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getData("SQL1") == null || parm.getData("SQL2") == null ||
            parm.getData("SQL3") == null || parm.getData("SQL4") == null) {
            result.setErr( -1, "缺少参数");
            return result;
        }
        String[] sql1 = (String[]) parm.getData("SQL1");
        String[] sql2 = (String[]) parm.getData("SQL2");
        String[] sql3 = (String[]) parm.getData("SQL3");
        String[] sql4 = (String[]) parm.getData("SQL4");
        result = updateSTA_OUT_04bySQL(sql1, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql2, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql3, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql4, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询打印数据
     * @param STA_DATE String
     * @return TParm
     * ===========pangben modify 20110523 添加参数
     */
    public TParm selectPrint(String STA_DATE,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //===========pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //===========pangben modify 20110523 stop
        TParm result = this.query("selectPrint",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 全院医生人数
     * @return TParm
     * ===========pangben modify 20110525 添加区域参数
     */
    public TParm selectDrNum(String regionCode){
        //===========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //===========pangben modify 20110525 stop
        TParm result = this.query("selectDrNum",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 全院职工人数
     * @return TParm
     * ==========pangben modify 20110525
     */
    public TParm selectOperatorNum(String regionCode){
        // ==========pangben modify 20110525 start
        TParm parm = new TParm();
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        // ==========pangben modify 20110525 stop
        TParm result = this.query("selectOperatorNum", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
