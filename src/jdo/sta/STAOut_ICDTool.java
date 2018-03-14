package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;

/**
 * <p>Title: 单病种医疗质量综合统计表</p>
 *
 * <p>Description: 单病种医疗质量综合统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-3
 * @version 1.0
 */
public class STAOut_ICDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOut_ICDTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOut_ICDTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STAOut_ICDTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public STAOut_ICDTool()
    {
//        setModuleName("sta\\STADeptListModule.x");
        onInit();
    }
    /**
     * 查询住院天数
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInDays(TParm parm){
        TParm result = new TParm();
        String deptSql = "";
        //判断是否存在部门条件
        if(parm.getValue("DEPTCODE").trim().length()>0){
            deptSql = " AND OUT_DEPT = '"+parm.getValue("DEPTCODE").trim()+"' ";
        }
        String icdSql = "";
        TParm icd = parm.getParm("ICD");//获取要查询的ICD列表
        TParm rowData = new TParm();
        for(int i=0;i<icd.getCount("ICD");i++){
            icdSql = icd.getValue("CONDITION",i);
            String sql =
                    "SELECT   MIN (REAL_STAY_DAYS) AS MINDAYS, MAX (REAL_STAY_DAYS) AS MAXDAYS, " +
                    " AVG (REAL_STAY_DAYS) AS AVGDAYS,COUNT(CASE_NO) AS NUM " +
                    " FROM MRO_RECORD " +
                    " WHERE OUT_DATE BETWEEN TO_DATE ('" +
                    parm.getValue("DATE_S") + "', 'YYYYMMDD') " + //起始日期
                    " AND TO_DATE ('" + parm.getValue("DATE_E") +
                    "' || '235959', 'YYYYMMDDHH24MISS') " + //截止日期
                    deptSql +
                    " AND " + icdSql ; //截取最后一位的逗号
//            System.out.println("selectInDays:"+sql);
            rowData.setData(TJDODBTool.getInstance().select(sql));
            if (rowData.getErrCode() < 0) {
                err("ERR:" + rowData.getErrCode() + rowData.getErrText() +
                    rowData.getErrName());
                return rowData;
            }
            result.setRowData(i,rowData.getRow(0));
            result.setData("ICD",i,icd.getValue("ICD",i));
        }
        return result;
    }
    /**
     * 查询住院费用
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINCharge(TParm parm){
        TParm result = new TParm();
        String deptSql = "";
        //判断是否存在部门条件
        if(parm.getValue("DEPTCODE").trim().length()>0){
            deptSql = " AND OUT_DEPT = '"+parm.getValue("DEPTCODE").trim()+"' ";
        }
        String icdSql = "";
        TParm icd = parm.getParm("ICD");//获取要查询的ICD列表
        TParm rowData = new TParm();
        for(int i=0;i<icd.getCount("ICD");i++){
            icdSql = icd.getValue("CONDITION",i);

            String sql = "SELECT   MAX (CHARGESUM) AS MAXCHARGE, MIN (CHARGESUM) AS MINCHARGE, AVG (CHARGESUM) AS AVGCHARGE " +
                         " FROM (SELECT   NVL (SUM_TOT, 0)  AS CHARGESUM, " +
                         "  OUT_DEPT " +
                         " FROM MRO_RECORD " +
                         " WHERE OUT_DATE BETWEEN TO_DATE ('" +
                         parm.getValue("DATE_S") + "', 'YYYYMMDD') " + //起始日期
                         " AND TO_DATE ('" + parm.getValue("DATE_E") +
                         "' || '235959', " + //截止日期
                         " 'YYYYMMDDHH24MISS' " +
                         "  ) " +
                         deptSql +
                         " AND " +
                         icdSql + " ) MRO ";
//            System.out.println("sql2:"+sql);
            rowData.setData(TJDODBTool.getInstance().select(sql));
            if (rowData.getErrCode() < 0) {
                err("ERR:" + rowData.getErrCode() + rowData.getErrText() +
                    rowData.getErrName());
                return rowData;
            }
            result.setRowData(i,rowData.getRow(0));
            result.setData("ICD",i,icd.getValue("ICD",i));
        }
        return result;
    }
    /**
     * 查询药费
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDrugCharge(TParm parm){
        TParm result = new TParm();
        String deptSql = "";
        //判断是否存在部门条件
        if(parm.getValue("DEPTCODE").trim().length()>0){
            deptSql = " AND OUT_DEPT = '"+parm.getValue("DEPTCODE").trim()+"' ";
        }
        String icdSql = "";
        TParm icd = parm.getParm("ICD");//获取要查询的ICD列表
        TParm rowData = new TParm();
        for (int i = 0; i < icd.getCount("ICD"); i++) {
            icdSql = icd.getValue("CONDITION", i);

            String sql = "SELECT   MAX (CHARGESUM) AS MAXCHARGE, MIN (CHARGESUM) AS MINCHARGE, AVG (CHARGESUM) AS AVGCHARGE " +
                         " FROM (SELECT   NVL (CHARGE_16, 0) " +
                         " + NVL (CHARGE_17, 0) " +
                         " + NVL (CHARGE_18, 0) " +
                         " + NVL (CHARGE_19, 0) AS CHARGESUM, " +
                         "  OUT_DEPT " +
                         " FROM MRO_RECORD " +
                         " WHERE OUT_DATE BETWEEN TO_DATE ('" +
                         parm.getValue("DATE_S") + "', 'YYYYMMDD') " + //起始日期
                         " AND TO_DATE ('" + parm.getValue("DATE_E") +
                         "' || '235959', " + //截止日期
                         " 'YYYYMMDDHH24MISS' " +
                         " ) " +
                         deptSql + //部门条件
                         " AND " + icdSql + " ) MRO "; //加入诊断的判定条件
//            System.out.println("selectDrugCharge:"+sql);
            rowData.setData(TJDODBTool.getInstance().select(sql));
            if (rowData.getErrCode() < 0) {
                err("ERR:" + rowData.getErrCode() + rowData.getErrText() +
                    rowData.getErrName());
                return rowData;
            }
            result.setRowData(i,rowData.getRow(0));
            result.setData("ICD",i,icd.getValue("ICD",i));
        }
        return result;
    }
    /**
     * 查询单病种出院状况
     * @param parm TParm
     * @param OUT_DIAG_CODE String
     * @return TParm
     */
    public TParm selectOUT_STATUS(TParm parm,String OUT_DIAG_CODE){
        TParm result = new TParm();
        String deptSql = "";
        //判断是否存在部门条件
        if(parm.getValue("DEPTCODE").trim().length()>0){
            deptSql = " AND OUT_DEPT = '"+parm.getValue("DEPTCODE").trim()+"' ";
        }
        String sql = "SELECT   COUNT (CASE_NO) AS NUM, CODE1_STATUS " +
            " FROM MRO_RECORD " +
            " WHERE OUT_DATE BETWEEN TO_DATE ('"+parm.getValue("DATE_S")+"', 'YYYYMMDD') " +
            " AND TO_DATE ('"+parm.getValue("DATE_E")+"' || '235959', 'YYYYMMDDHH24MISS') " +
            deptSql +
            " AND " + OUT_DIAG_CODE +
            " GROUP BY CODE1_STATUS";
//        System.out.println("selectOUT_STATUS:"+sql);
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 生成数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        TParm ICD = parm.getParm("ICD");
        if(ICD==null){
            result.setErr(-1,"病种参数不存在");
            return result;
        }
        
        //查询在院天数信息
        TParm selectInDays = this.selectInDays(parm);
        if (selectInDays.getErrCode() < 0) {
            err("ERR:" + selectInDays.getErrCode() + selectInDays.getErrText() +
                selectInDays.getErrName());
            return selectInDays;
        }
//        System.out.println("-=----------1-----------"+selectInDays);
        //查询住院费用
        TParm selectINCharge = this.selectINCharge(parm);
        if (selectINCharge.getErrCode() < 0) {
            err("ERR:" + selectINCharge.getErrCode() + selectINCharge.getErrText() +
                selectINCharge.getErrName());
            return selectINCharge;
        }
//        System.out.println("-=----------2-----------"+selectINCharge);
        //查询药品费用
        TParm selectDrugCharge = this.selectDrugCharge(parm);
        if (selectDrugCharge.getErrCode() < 0) {
            err("ERR:" + selectDrugCharge.getErrCode() + selectDrugCharge.getErrText() +
                selectDrugCharge.getErrName());
            return selectDrugCharge;
        }
//        System.out.println("-=----------selectDrugCharge-----------"+selectDrugCharge);
        int count = ICD.getCount("ICD");
        for(int i=0;i<count;i++){
            String ICD_CODE = ICD.getValue("ICD",i);
            String ICD_NAME = ICD.getValue("DESC",i);
            String CONDITION = ICD.getValue("CONDITION",i);
            //定义各个字段的变量
            int DATA_01 = 0;
            int DATA_02 = 0;
            int DATA_03 = 0;
            double DATA_04 = 0;
            double DATA_05 = 0;
            double DATA_06 = 0;
            double DATA_07 = 0;
            double DATA_08 = 0;
            double DATA_09 = 0;
            double DATA_10 = 0;
            double DATA_11 = 0;
            for(int j=0;j<selectInDays.getCount("ICD");j++){
                if(selectInDays.getValue("ICD",j).equals(ICD_CODE)){
                    DATA_01 = selectInDays.getInt("NUM", j); //总例数
                    DATA_02 = selectInDays.getInt("MINDAYS", j); //最短住院天数
                    DATA_03 = selectInDays.getInt("MAXDAYS", j); //最长住院天数
                    DATA_04 = selectInDays.getDouble("AVGDAYS", j); //平均住院天数
                }
            }
            for(int j=0;j<selectINCharge.getCount("ICD");j++){
                if(selectINCharge.getValue("ICD",j).equals(ICD_CODE)){
                    DATA_05 = selectINCharge.getDouble("MINCHARGE", j); //最低住院费用
                    DATA_06 = selectINCharge.getDouble("MAXCHARGE", j); //最高住院费用
                    DATA_07 = selectINCharge.getDouble("AVGCHARGE", j); //平均住院费用
                }
            }
            for(int j=0;j<selectDrugCharge.getCount("ICD");j++){
                if(selectDrugCharge.getValue("ICD",j).equals(ICD_CODE)){
                    DATA_08 = selectDrugCharge.getDouble("MINCHARGE", j); //最低药费
                    DATA_09 = selectDrugCharge.getDouble("MAXCHARGE", j); //最高药费
                    DATA_10 = selectDrugCharge.getDouble("AVGCHARGE", j); //平均药费
                }
            }
            //计算治愈有效率  分母不可为零
            if(DATA_01>0){
                //统计治愈和好转的人数
                TParm selectOUT_STATUS = this.selectOUT_STATUS(parm, CONDITION);
//                System.out.println("-=----------selectOUT_STATUS-----------"+selectOUT_STATUS);
                if (selectOUT_STATUS.getErrCode() < 0) {
                    err("ERR:" + selectOUT_STATUS.getErrCode() +
                        selectOUT_STATUS.getErrText() +
                        selectOUT_STATUS.getErrName());
                    return selectOUT_STATUS;
                }
                int Status1 = 0; //治愈人数
                int Status2 = 0; //好转人数
                for (int h = 0; h < selectOUT_STATUS.getCount("NUM"); h++) {
                    if (selectOUT_STATUS.getValue("CODE1_STATUS", h).trim().
                        equals("1")) { //治愈
                        Status1 = selectOUT_STATUS.getInt("NUM", h);
                    }
                    else if (selectOUT_STATUS.getValue("CODE1_STATUS", h).trim().
                             equals("2")) { //好转
                        Status2 = selectOUT_STATUS.getInt("NUM", h);
                    }
                }
                DATA_11 = (double)(Status1+Status2)/(double)DATA_01*100;//治愈有效率
            }
            result.addData("ICD_DESC",ICD_NAME);//病种名称
            result.addData("DATA_01",DATA_01);
            result.addData("DATA_02",DATA_02);
            result.addData("DATA_03",DATA_03);
            result.addData("DATA_04",df.format(DATA_04));
            result.addData("DATA_05",df.format(DATA_05));
            result.addData("DATA_06",df.format(DATA_06));
            result.addData("DATA_07",df.format(DATA_07));
            result.addData("DATA_08",df.format(DATA_08));
            result.addData("DATA_09",df.format(DATA_09));
            result.addData("DATA_10",df.format(DATA_10));
            result.addData("DATA_11",df.format(DATA_11));
        }
        return result;
    }
    /**
     * 查询开放病床数
     * @param Dept String
     * @return TParm
     */
    public TParm selectDedNO(String Dept){
        String deptSelect = "";
        if(Dept.trim().length()>0){
            deptSelect = " AND STATION_CODE ='"+ Dept +"'";
        }
        String sql = "SELECT COUNT(BED_NO) AS NUM "+
            " FROM SYS_BED "+
            " WHERE OCCU_RATE_FLG ='Y' "+
            " AND USE_FLG ='Y' "+deptSelect;
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
