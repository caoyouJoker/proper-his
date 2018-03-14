package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import jdo.sys.SYSBedTool;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import jdo.ibs.IBSTool;  
import jdo.sys.SYSBedFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SYSOrderSetDetailTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSSQL;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMAutoBillTool
    extends TJDOTool {  
    /**
     * 实例
     */
    public static ADMAutoBillTool instanceObject;
       
    /**
     * 得到实例  
     * @return RegMethodTool
     */
    public static ADMAutoBillTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMAutoBillTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMAutoBillTool() {
        setModuleName("adm\\ADMAutoBillModule.x");
        onInit();
    }

    /**
     * 查询全数据
     * @param parm TParm
     * @return TParm
     */
    public TParm queryData(TParm parm) {
        TParm result = new TParm();
        result = query("querydata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }
    /**
     * 查询全数据
     * @return TParm
     */
    public TParm queryData() {
        TParm result = new TParm();
        result = query("querydata");
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String orderCode = parm.getValue("ORDER_CODE");
        if (existsORDER(orderCode)) {
            result.setErr( -1, "收费项目 " + " 已经存在!");
            return result;
        }
        result = update("insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("update", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = update("deletedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断是否存在自动计费项目
     * @param orderCode String
     * @return boolean
     */
    public boolean existsORDER(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        return getResultInt(query("existsORDER", parm), "COUNT") > 0;
    }
    /**
     * 固定费用自动过账
     * @param parm TParm  必须参数：  STATION_CODE：病区code；
     *                               DATE：日期；
     *                               OPT_USER：操作人员；
     *                               OPT_TERM：操作IP
     * @param conn TConnection
     * @return TParm
     */
    public TParm postAutoBill(TParm parm,TConnection conn){
        String StationCode = parm.getValue("STATION_CODE");
        Timestamp date = StringTool.rollDate(parm.getTimestamp("DATE"),-1);
        String OPT_USER = parm.getValue("OPT_USER");
        String OPT_TERM = parm.getValue("OPT_TERM");
        String dateString = StringTool.getString(date,"yyyyMMdd");
        double PATCH_AMT = 0;//删除IBS_ORDD表的总金额
        TParm result = new TParm();
        //查询该病区的所有病床
        TParm bed = new TParm();
        bed.setData("STATION_CODE",StationCode);
        TParm bedList = SYSBedTool.getInstance().queryAll(bed);
//        System.out.println("查询该病区的所有病床"+bedList);
        if (bedList.getErrCode() < 0) {
            err("ERR:" + bedList.getErrCode() + bedList.getErrText() +
                bedList.getErrName());
            return bedList;
        }
        //取 adm_autobil 中的order_code计算
        TParm autoBill = this.queryData();//查询全部自动计费的项目
        //循环每张病床进行检核  注意要检查包床，将包床费用计算在一张账单上
        for(int i=0;i<bedList.getCount();i++){
            //如果病床住有病患
            String CASE_NO = "";
            //检查包床助记 如果是包床 那么这张床不是实际住有病人的床位，但是该床位的固定费用要计算在包床的病人账单上
            if(!bedList.getValue("BED_OCCU_FLG",i).equals("Y"))
                CASE_NO = bedList.getValue("CASE_NO",i);
            //查询该病患是否包床
            TParm BED_OCCU_CODE = new TParm();//记录该病患所占用的床位的信息
            for(int j=0;j<bedList.getCount();j++){
                //CASE_NO相同并且包床标记为Y 表示该病患包床
                if(CASE_NO.equals(bedList.getValue("CASE_NO",j))&&"Y".equals(bedList.getValue("BED_OCCU_FLG",j))){
//                    System.out.println("是包床信息========"+bedList);
                    BED_OCCU_CODE.addRowData(bedList,j);//加入病患包床的信息
                }
            }
            //加入病患实际所在床位的信息
            BED_OCCU_CODE.addRowData(bedList,i);
            if(CASE_NO.length()>0){
                String dateS = StringTool.getString(parm.getTimestamp("DATE"), "yyyyMMdd");
                String EXEsql =
                        " SELECT B.PAT_NAME,A.STATION_CODE, A.BED_NO,A.IPD_NO,A.MR_NO,A.CASE_NO " +
                        "   FROM ADM_INP A,SYS_PATINFO B" +
                        "  WHERE DS_DATE IS NULL" +
                        "    AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)" +
                        "    AND A.IN_DATE < TO_DATE('" + dateS + "','YYYYMMDD') " +
                        "    AND A.BED_NO IS NOT NULL " +
                        "    AND A.MR_NO = B.MR_NO" +
                        "    AND A.CASE_NO = '"+CASE_NO+"' "+
                        "  ORDER BY A.MR_NO ";
//                System.out.println("校验sql"+EXEsql);
                 TParm caseParm = new TParm(TJDODBTool.getInstance().select(EXEsql));
                 if(caseParm.getErrCode()<0){
                     err("ERR:" + caseParm.getErrCode() + caseParm.getErrText() +
                         caseParm.getErrName());
                     return caseParm;
                 }
                 if (caseParm.getCount() <= 0)
                     continue;
                //检核床日档是否已经有记录
                TParm dailyRecParm = new TParm();
                dailyRecParm.setData("CASE_NO",CASE_NO);
                dailyRecParm.setData("POST_DATE",dateString);//日档日期
                TParm daulyRec = ADMDailyRecTool.getInstance().selectDailyRec(dailyRecParm);
                //如果床日档存在信息，删除该日的信息和已经过帐的财务信息(ibs_ordd,ibs_ordm)
                if(daulyRec.getCount()>0){
                    TParm delRec = new TParm();
                    delRec.setData("CASE_NO",CASE_NO);
                    delRec.setData("POST_DATE",dateString);//日档日期
                    result = ADMDailyRecTool.getInstance().delDailRec(delRec,conn);//删除病患的床日档
                    if(result.getErrCode()<0){
                        err("ERR:" + result.getErrCode() + result.getErrText() +
                            result.getErrName());
                        return result;
                    }
                    //删除IBS的费用记录  调用IBSTool提供的接口
//                    System.out.println("date:"+StringTool.getString(date, "yyyyMMdd"));
                    result = IBSTool.getInstance().deleteOrderForPatch(CASE_NO,
                        StringTool.getString(date, "yyyyMMdd"), conn);
                    PATCH_AMT = result.getDouble("PATCH_AMT");
                    if(result.getErrCode()<0){
                        err("ERR:" + result.getErrCode() + result.getErrText() +
                            result.getErrName());
                        return result;
                    }
                }
                //查询ADM_INP病患住院信息
                TParm inp = new TParm();
                inp.setData("CASE_NO",CASE_NO);
                TParm admInp = ADMInpTool.getInstance().selectall(inp);
                if (admInp.getErrCode() < 0) {
                    err("ERR:" + admInp.getErrCode() + admInp.getErrText() +
                        admInp.getErrName());
                    return admInp;
                }
                if(admInp.getCount()<=0){
                    admInp.setErr(-1,"查无该病患住院信息");
                    return admInp;
                }
              //===zhangp 20120828 start 生成ibs_ordd时执行科室为成本中心代码
                String cccsql = 
                	" SELECT COST_CENTER_CODE" +
                	" FROM SYS_DEPT" +
                	" WHERE DEPT_CODE = '" + admInp.getValue("DEPT_CODE", 0) + "'";
                TParm cccParm = new TParm(TJDODBTool.getInstance().select(cccsql));
                if (cccParm.getErrCode() < 0) {
                    err("ERR:" + cccParm.getErrCode() + cccParm.getErrText() +
                    		cccParm.getErrName());
                    String stsql =
                    	" SELECT COST_CENTER_CODE" +
                    	" FROM SYS_STATION" +
                    	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
                    TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
                    if (stParm.getErrCode() < 0) {
                        err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                        		stParm.getErrName());
                        stParm.addData("COST_CENTER_CODE", "");
                    }
                    if (stParm.getCount() < 0) {
                    	stParm.addData("COST_CENTER_CODE", "");
                    }
                    cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));//防dept_code空
                }
                if (cccParm.getCount() <= 0) {
                	String stsql =
                    	" SELECT COST_CENTER_CODE" +
                    	" FROM SYS_STATION" +
                    	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
                    TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
                    if (stParm.getErrCode() < 0) {
                        err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                        		stParm.getErrName());
                        stParm.addData("COST_CENTER_CODE", "");
                    }
                    if (stParm.getCount() < 0) {
                    	stParm.addData("COST_CENTER_CODE", "");
                    }
                    cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));//防dept_code空
                }
                //====zhangp 20130819 start
                String stsql =
                	" SELECT COST_CENTER_CODE" +
                	" FROM SYS_STATION" +
                	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
                TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
                if (stParm.getErrCode() < 0) {
                    err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                    		stParm.getErrName());
                    stParm.addData("COST_CENTER_CODE", "");
                }
                if (stParm.getCount() < 0) {
                	stParm.addData("COST_CENTER_CODE", "");
                }
                //====zhangp 20130819 end
                //===zhangp 20120828 end
                //插入床日档
                TParm insertRec = new TParm();
                insertRec.setData("POST_DATE",dateString);
                insertRec.setData("CASE_NO",CASE_NO);
                insertRec.setData("MR_NO",admInp.getValue("MR_NO",0));
                insertRec.setData("IPD_NO",admInp.getValue("IPD_NO",0));
                insertRec.setData("DEPT_CODE",admInp.getValue("DEPT_CODE",0));
                insertRec.setData("STATION_CODE",admInp.getValue("STATION_CODE",0));
                insertRec.setData("BED_NO",admInp.getValue("BED_NO",0));
                insertRec.setData("VS_DR_CODE",admInp.getValue("VS_DR_CODE",0));
                insertRec.setData("NURSING_CLASS",admInp.getValue("NURSING_CLASS",0));//护理等级
                insertRec.setData("PATIENT_CONDITITION",admInp.getValue("PATIENT_CONDITION",0));//病情状态
                insertRec.setData("CARE_NUM",admInp.getInt("CARE_NUM",0));//陪护人数
                insertRec.setData("OPT_USER",OPT_USER);
                insertRec.setData("OPT_TERM",OPT_TERM);
                result = ADMDailyRecTool.getInstance().insertDailyRec(insertRec,
                    conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
                //生成IBS参数
                TParm orderList = new TParm();
                for(int h=0;h<autoBill.getCount();h++){
                    //病人所有床位的费用计算
                    for(int k=0;k<BED_OCCU_CODE.getCount("BED_NO");k++){
                        //判断此床位是否是病人的包床
                        if(BED_OCCU_CODE.getBoolean("BED_OCCU_FLG",k)){//是包床
                            //判断此项费用是否对包床计算
                            if(!autoBill.getBoolean("OCCUFEE_FLG",h)){
                                //如果不对包床进行计算 那么调过本次循环
                                continue;
                            }
                        }
                        //判断该病床是否是婴儿床
                        if(BED_OCCU_CODE.getBoolean("BABY_BED_FLG",k)){//是婴儿床
                            //判断此项费用是否对婴儿床计算
                            if(!autoBill.getBoolean("BABY_FLG",h)){
                                //如果不对婴儿床进行计算 那么调过本次循环
                                continue;
                            }
                        }
                        Timestamp now = SystemTool.getInstance().getDate();
                        String year = StringTool.getString(now, "yyyy");
                        int nextYear = Integer.valueOf(year) + 1;
                        String start = autoBill.getValue("START_DATE", h);
                        String end = autoBill.getValue("END_DATE", h);
                        long tiemNow =TypeTool.getLong(StringTool.getString(now,"yyyyMMddHHmmss"));//当前时间
                        //如果起始月份 早于 截止月份 那么 起始时间和截止时间在同一年
                        if (start.compareTo(end) < 0) {
                            start = year + start + "000000";
                            end = year + end + "235959";
                        }
                        else {
                            start = year + start + "000000";
                            end = nextYear + end + "235959";
                        }
                        //判断当前时间是否在 固定费用的计算范围内
                        if(tiemNow<TypeTool.getLong(start)||tiemNow>TypeTool.getLong(end)){
                            continue;
                        }
                        TParm sysFee = this.getSysFee(autoBill.getValue("ORDER_CODE", h));
                        orderList.addData("CASE_NO", CASE_NO);
                        //数量 将包床的数量计算在内（乘以包床数）
                        orderList.addData("DOSAGE_QTY",
                                          autoBill.getInt("DOSEAGE_QTY", h));
                        orderList.addData("DOSAGE_UNIT",
                                          sysFee.getValue("UNIT_CODE"));
                        orderList.addData("ORDER_CODE",
                                          autoBill.getValue("ORDER_CODE", h));
                        orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
                        orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
                        orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("ORDER_DEPT_CODE",
                                          admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        //=====zhangp 20130819 start
                        if(autoBill.getBoolean("DEPT_FLG", h)){
                        	orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
                        }else{
                        	orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
                        }
                        //=====zhangp 20130819 end
                        orderList.addData("EXEC_STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
                        orderList.addData("DISPENSE_EFF_DATE",now);
                        orderList.addData("DISPENSE_END_DATE",now);
                        orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); //开药数量
                        orderList.addData("MEDI_UNIT",sysFee.getValue("UNIT_CODE"));
                        orderList.addData("TAKE_DAYS", 1); //开药天数默认为1
                        orderList.addData("OPT_USER", OPT_USER);
                        orderList.addData("OPT_TERM", OPT_TERM);
                        orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
                        orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
                        orderList.addData("HIDE_FLG", "N");//集合医嘱细项隐藏标记
                        orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                        orderList.addData("BILL_FLG", "Y");
                        orderList.addData("REQUEST_FLG", "N");//请领注记
                        TParm parmDetail = SYSOrderSetDetailTool.getInstance()
                            .selectByOrderSetCode(autoBill.getValue("ORDER_CODE", h));
                        if(parmDetail.getCount()>0){
                            orderList.addData("ORDERSET_GROUP_NO", h);//集合医嘱组号
                            orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));//集合医嘱主项CODE
                        }else{
                            orderList.addData("ORDERSET_GROUP_NO","");//集合医嘱组号
                            orderList.addData("ORDERSET_CODE", "");//集合医嘱主项CODE
                        }
                        for(int x=0;x<parmDetail.getCount();x++){
                            TParm sysFeeD = this.getSysFee(parmDetail.getValue("ORDER_CODE", x));
                            orderList.addData("CASE_NO", CASE_NO);
                            //数量 将包床的数量计算在内（乘以包床数）
                            orderList.addData("DOSAGE_QTY",
                                              autoBill.getInt("DOSEAGE_QTY", h));
                            orderList.addData("DOSAGE_UNIT",
                                          sysFeeD.getValue("UNIT_CODE"));
                            orderList.addData("ORDER_CODE",
                                              parmDetail.getValue("ORDER_CODE", x));
                            orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
                            orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
                            orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                            orderList.addData("ORDER_DEPT_CODE",
                                              admInp.getValue("DEPT_CODE", 0));
                            orderList.addData("STATION_CODE",
                                              admInp.getValue("STATION_CODE", 0));
                            if(autoBill.getBoolean("DEPT_FLG", h)){
                            	orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
                            }else{
                            	orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
                            }
                            orderList.addData("EXEC_STATION_CODE",
                                              admInp.getValue("STATION_CODE", 0));
                            orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
                            orderList.addData("DISPENSE_EFF_DATE",now);
                            orderList.addData("DISPENSE_END_DATE",now);
                            orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); //开药数量
                            orderList.addData("MEDI_UNIT",sysFeeD.getValue("UNIT_CODE"));
                            orderList.addData("TAKE_DAYS", 1); //开药天数默认为0
                            orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));
                            orderList.addData("OPT_USER", OPT_USER);
                            orderList.addData("OPT_TERM", OPT_TERM);
                            orderList.addData("ORDER_CAT1_CODE", sysFeeD.getValue("ORDER_CAT1_CODE"));
                            orderList.addData("CAT1_TYPE", sysFeeD.getValue("CAT1_TYPE"));
                            orderList.addData("ORDERSET_GROUP_NO", h);//集合医嘱组号
                            orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));//集合医嘱主项CODE
                            orderList.addData("HIDE_FLG", "Y");//集合医嘱细项隐藏标记
                            orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                            orderList.addData("BILL_FLG", "Y");
                            orderList.addData("REQUEST_FLG", "N");//请领注记
                        }
                    }
                }
                //根据床位等级查询 sys_bedfee 床位费用字典 计算该床位的费用
                for(int k=0;k<BED_OCCU_CODE.getCount("BED_NO");k++){
                    TParm bedBill = SYSBedFeeTool.getInstance().getSYSBedFeeOccu(
                        BED_OCCU_CODE.getValue("BED_CLASS_CODE", k),BED_OCCU_CODE.getValue("BED_OCCU_FLG", k));
//                    System.out.println("床位等级："+BED_OCCU_CODE.getValue("BED_CLASS_CODE", k));
//                    System.out.println("床位费用："+bedBill);
                    Timestamp now = SystemTool.getInstance().getDate();
                    for (int h = 0; h < bedBill.getCount(); h++) {
                        TParm sysFee = this.getSysFee(bedBill.getValue("ORDER_CODE", h));
                        orderList.addData("CASE_NO", CASE_NO);
                        orderList.addData("DOSAGE_QTY", "1"); //床位费用默认总量为1
                        orderList.addData("DOSAGE_UNIT",
                                          sysFee.getValue("UNIT_CODE"));
                        orderList.addData("ORDER_CODE",
                                          bedBill.getValue("ORDER_CODE", h));
                        orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
                        orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
                        orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("ORDER_DEPT_CODE",
                                          admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        orderList.addData("EXEC_DEPT_CODE",
                        				  stParm.getValue("COST_CENTER_CODE", 0));
                        orderList.addData("EXEC_STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
                        String start = autoBill.getValue("START_DATE", h);
                        String end = autoBill.getValue("END_DATE", h);
                        String year = StringTool.getString(SystemTool.
                            getInstance().getDate(), "yyyy");
                        //如果起始月份 早于 截止月份 那么 起始时间和截止时间在同一年
                        if (start.compareTo(end) < 0) {
                            start = year + start + "000000";
                            end = year + end + "235959";
                        }
                        else {
                            start = year + start + "000000";
                            int nextYear = Integer.valueOf(year) + 1;
                            end = nextYear + end + "235959";
                        }
                        orderList.addData("DISPENSE_EFF_DATE",now);
                        orderList.addData("DISPENSE_END_DATE",now);
                        orderList.addData("MEDI_QTY", 1); //开药数量默认为1
                        orderList.addData("MEDI_UNIT",sysFee.getValue("UNIT_CODE"));
                        orderList.addData("TAKE_DAYS", 1); //开药天数默认为1
                        orderList.addData("OPT_USER", OPT_USER);
                        orderList.addData("OPT_TERM", OPT_TERM);
                        orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
                        orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
                        orderList.addData("ORDERSET_GROUP_NO", "");//集合医嘱组号
                        orderList.addData("ORDERSET_CODE", "");//集合医嘱主项CODE
                        orderList.addData("HIDE_FLG", "N");//集合医嘱细项隐藏标记
                        orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                        orderList.addData("BILL_FLG", "Y");
                        orderList.addData("REQUEST_FLG", "N");//请领注记
                    }
                }
                TParm ibsParm = new TParm();
                ibsParm.setData("M",orderList.getData());
                ibsParm.setData("CTZ1_CODE",admInp.getValue("CTZ1_CODE",0));
                ibsParm.setData("CTZ2_CODE",admInp.getValue("CTZ2_CODE",0));
                ibsParm.setData("CTZ3_CODE",admInp.getValue("CTZ3_CODE",0));
                ibsParm.setData("FLG","ADD");
                //取回用于插入的IBS参数
                TParm IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);


                TParm ibsIN = new TParm();  
                ibsIN.setData("M",IBSInsert.getData());
                ibsIN.setData("DATA_TYPE","0");
                ibsIN.setData("FLG","ADD");   
				Timestamp dateNew = StringTool.rollDate(parm.getTimestamp("DATE"),0);
                String dateStringNew = StringTool.getString(dateNew,"yyyyMMdd");  
                ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateStringNew+"000000","yyyyMMddHHmmss")); 
                ibsIN.setData("PATCH_AMT",PATCH_AMT);    
//                System.out.println("自动过账入参===="+ibsIN);
                result = IBSTool.getInstance().insertIBSOrder(ibsIN,conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
            }
        }
        return result; 
    }
    /**  
     * c（以个人为单位补帐）
     * @param parm TParm  必须参数：CASE_NO:就诊号；
     *                             DATE:日期；
     *                             OPT_USER：操作人员；
     *                             OPT_TERM：操作IP
     * @param conn TConnection
     * @return TParm 
     */
    public TParm postAutoBillOfMen(TParm parm,TConnection conn){
    	//System.out.println("补固定费用自动过账");   
//        System.out.println("自动计费入参："+parm);  
        String CASE_NO = parm.getValue("CASE_NO");
        Timestamp date = StringTool.rollDate(parm.getTimestamp("DATE"),-1);
        String OPT_USER = parm.getValue("OPT_USER");
        String OPT_TERM = parm.getValue("OPT_TERM");
        
        String dateString = StringTool.getString(date,"yyyyMMdd");
        
        double PATCH_AMT = 0;//删除IBS_ORDD表的总金额
        TParm result = new TParm(); 
        //查询该病患的所有病床
        TParm bed = new TParm();  
        bed.setData("CASE_NO",CASE_NO);
        TParm bedList = SYSBedTool.getInstance().queryAll(bed);
        if (bedList.getErrCode() < 0) {
            err("ERR:" + bedList.getErrCode() + bedList.getErrText() +
                bedList.getErrName());
            return bedList;
        } 
        if(bedList.getCount("CASE_NO")<=0)
            return bedList;
//        System.out.println("查询该病患的所有病床："+bedList);
//        System.out.println("CASE_NO："+CASE_NO);
        if (CASE_NO.length() > 0) {
            //检核床日档是否已经有记录
            TParm dailyRecParm = new TParm();
            dailyRecParm.setData("CASE_NO", CASE_NO);
            dailyRecParm.setData("POST_DATE", dateString); //日档日期
            TParm daulyRec = ADMDailyRecTool.getInstance().selectDailyRec(
                dailyRecParm);
            //如果床日当存在信息，删除该日的信息和已经过帐的财务信息(ibs_ordd,ibs_ordm)
            if (daulyRec.getCount() > 0) {
                TParm delRec = new TParm();
                delRec.setData("CASE_NO", CASE_NO);
                delRec.setData("POST_DATE", dateString); //日档日期
                result = ADMDailyRecTool.getInstance().delDailRec(delRec, conn); //删除病患的床日档
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
                //删除IBS的费用记录  调用IBSTool提供的借口
//                System.out.println("删除账务的时间："+StringTool.getString(date, "yyyyMMdd"));
                result = IBSTool.getInstance().deleteOrderForPatch(CASE_NO,
                    StringTool.getString(date, "yyyyMMdd"), conn);
                PATCH_AMT = result.getDouble("PATCH_AMT");
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
            }
            //查询ADM_INP病患住院信息
            TParm inp = new TParm();
            inp.setData("CASE_NO", CASE_NO);
            TParm admInp = ADMInpTool.getInstance().selectall(inp);
            if (admInp.getErrCode() < 0) {
                err("ERR:" + admInp.getErrCode() + admInp.getErrText() +
                    admInp.getErrName());
                return admInp;
            }
            if (admInp.getCount() <= 0) {
                admInp.setErr( -1, "查无该病患住院信息");
                return admInp;
            }
            //===zhangp 20120828 start 生成ibs_ordd时执行科室为成本中心代码
            String cccsql = 
            	" SELECT COST_CENTER_CODE" +
            	" FROM SYS_DEPT" +
            	" WHERE DEPT_CODE = '" + admInp.getValue("DEPT_CODE", 0) + "'";
            TParm cccParm = new TParm(TJDODBTool.getInstance().select(cccsql));
            if (cccParm.getErrCode() < 0) {
                err("ERR:" + cccParm.getErrCode() + cccParm.getErrText() +
                		cccParm.getErrName());
                String stsql =
                	" SELECT COST_CENTER_CODE" +
                	" FROM SYS_STATION" +
                	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
                TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
                if (stParm.getErrCode() < 0) {
                    err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                    		stParm.getErrName());
                    stParm.addData("COST_CENTER_CODE", "");
                }
                if (stParm.getCount() < 0) {
                	stParm.addData("COST_CENTER_CODE", "");
                }
                cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));//防dept_code空
            }
            if (cccParm.getCount() <= 0) {
            	String stsql =
                	" SELECT COST_CENTER_CODE" +
                	" FROM SYS_STATION" +
                	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
                TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
                if (stParm.getErrCode() < 0) {
                    err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                    		stParm.getErrName());
                    stParm.addData("COST_CENTER_CODE", "");
                }
                if (stParm.getCount() < 0) {
                	stParm.addData("COST_CENTER_CODE", "");
                }
                cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));//防dept_code空
            }
            //====zhangp 20130819 start
            String stsql =
            	" SELECT COST_CENTER_CODE" +
            	" FROM SYS_STATION" +
            	" WHERE STATION_CODE = '" + admInp.getValue("STATION_CODE", 0) + "'";
            TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
            if (stParm.getErrCode() < 0) {
                err("ERR:" + stParm.getErrCode() + stParm.getErrText() +
                		stParm.getErrName());
                stParm.addData("COST_CENTER_CODE", "");
            }
            if (stParm.getCount() < 0) {
            	stParm.addData("COST_CENTER_CODE", "");
            }
            //====zhangp 20130819 end
            //===zhangp 20120828 end
            //插入床日档
            TParm insertRec = new TParm();
            insertRec.setData("POST_DATE", dateString);
            insertRec.setData("CASE_NO", CASE_NO);
            insertRec.setData("MR_NO", admInp.getValue("MR_NO", 0));
            insertRec.setData("IPD_NO", admInp.getValue("IPD_NO", 0));
            insertRec.setData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
            insertRec.setData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
            insertRec.setData("BED_NO", admInp.getValue("BED_NO", 0));
            insertRec.setData("VS_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
            insertRec.setData("NURSING_CLASS",
                              admInp.getValue("NURSING_CLASS", 0)); //护理等级
            insertRec.setData("PATIENT_CONDITITION",
                              admInp.getValue("PATIENT_CONDITION", 0)); //病情状态
            insertRec.setData("CARE_NUM",admInp.getInt("CARE_NUM",0));//陪护人数
            insertRec.setData("OPT_USER", OPT_USER);
            insertRec.setData("OPT_TERM", OPT_TERM);
            result = ADMDailyRecTool.getInstance().insertDailyRec(insertRec,
                conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            //生成IBS参数
            TParm orderList = new TParm();
            //取 adm_autobil 中的order_code计算
            TParm autoBill = this.queryData(); //查询全部自动计费的项目
            for (int h = 0; h < autoBill.getCount(); h++) {
                //病人所有床位的费用计算
                for(int k=0;k<bedList.getCount("BED_NO");k++){
                    //判断此床位是否是病人的包床
                    if(bedList.getBoolean("BED_OCCU_FLG",k)){//是包床
                        //判断此项费用是否对包床计算
                        if(!autoBill.getBoolean("OCCUFEE_FLG",h)){
                            //如果不对包床进行计算 那么调过本次循环
                            continue;
                        }
                    }
                    //判断该病床是否是婴儿床
                    if(bedList.getBoolean("BABY_BED_FLG",k)){//是婴儿床
                        //判断此项费用是否对婴儿床计算
                        if(!autoBill.getBoolean("BABY_FLG",h)){
                            //如果不对婴儿床进行计算 那么调过本次循环
                            continue;
                        }
                    }
                    Timestamp now = SystemTool.getInstance().getDate();
                    String year = StringTool.getString(now, "yyyy");
                    int nextYear = Integer.valueOf(year) + 1;
                    String start = autoBill.getValue("START_DATE", h);
                    String end = autoBill.getValue("END_DATE", h);
                    long tiemNow =TypeTool.getLong(StringTool.getString(now,"yyyyMMddHHmmss"));//当前时间
                    //如果起始月份 早于 截止月份 那么 起始时间和截止时间在同一年
                    if (start.compareTo(end) < 0) {
                        start = year + start + "000000";
                        end = year + end + "235959";
                    }
                    else {
                        start = year + start + "000000";
                        end = nextYear + end + "235959";
                    }
                    //判断当前时间是否在 固定费用的计算范围内
                    if(tiemNow<TypeTool.getLong(start)||tiemNow>TypeTool.getLong(end)){
                        continue;
                    }
                    TParm sysFee = this.getSysFee(autoBill.getValue("ORDER_CODE", h));
                    orderList.addData("CASE_NO", CASE_NO);
                    //数量
                    orderList.addData("DOSAGE_QTY",
                                      autoBill.getInt("DOSEAGE_QTY", h));
                    orderList.addData("DOSAGE_UNIT",
                                          sysFee.getValue("UNIT_CODE"));
                    orderList.addData("ORDER_CODE",
                                      autoBill.getValue("ORDER_CODE", h));
                    orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
                    orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
                    orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                    orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                    orderList.addData("STATION_CODE",
                                      admInp.getValue("STATION_CODE", 0));
                    //===zhangp 20120828 start 生成ibs_ordd时执行科室为成本中心代码
//                    orderList.addData("EXEC_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                    //=====zhangp 20130819 start
                    if(autoBill.getBoolean("DEPT_FLG", h)){
                    	orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
                    }else{
                    	orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
                    }
                    //=====zhangp 20130819 end
                    //===zhangp 20120828 end
                    orderList.addData("EXEC_STATION_CODE",
                                      admInp.getValue("STATION_CODE", 0));
                    orderList.addData("BED_NO", bedList.getValue("BED_NO", k));
                    orderList.addData("DISPENSE_EFF_DATE",now);
                    orderList.addData("DISPENSE_END_DATE",now);
                    orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); //开药数量默认为0
                    orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
                    orderList.addData("TAKE_DAYS", 1); //开药天数默认为0
                    orderList.addData("OPT_USER", OPT_USER);
                    orderList.addData("OPT_TERM", OPT_TERM);
                    orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
                    orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
                    orderList.addData("HIDE_FLG", "N");//集合医嘱细项隐藏标记
                    orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                    orderList.addData("BILL_FLG", "Y");
                    orderList.addData("REQUEST_FLG", "N");//请领注记
                    TParm parmDetail = SYSOrderSetDetailTool.getInstance()
                        .selectByOrderSetCode(autoBill.getValue("ORDER_CODE", h));
                    if(parmDetail.getCount()>0){
                        orderList.addData("ORDERSET_GROUP_NO", h);//集合医嘱组号
                        orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));//集合医嘱主项CODE
                    }else{
                        orderList.addData("ORDERSET_GROUP_NO", "");//集合医嘱组号
                        orderList.addData("ORDERSET_CODE", "");
                    }
                    for(int x=0;x<parmDetail.getCount();x++){
                        TParm sysFeeD = this.getSysFee(parmDetail.getValue("ORDER_CODE", x));
                        orderList.addData("CASE_NO", CASE_NO);
                        //数量 将包床的数量计算在内（乘以包床数）
                        orderList.addData("DOSAGE_QTY",
                                          autoBill.getInt("DOSEAGE_QTY", h));
                        orderList.addData("DOSAGE_UNIT",
                                          sysFeeD.getValue("UNIT_CODE"));
                        orderList.addData("ORDER_CODE",
                                          parmDetail.getValue("ORDER_CODE", x));
                        orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
                        orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
                        orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("ORDER_DEPT_CODE",
                                              admInp.getValue("DEPT_CODE", 0));
                        orderList.addData("STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        if(autoBill.getBoolean("DEPT_FLG", h)){
                        	orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
                        }else{
                        	orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
                        }
                        orderList.addData("EXEC_STATION_CODE",
                                          admInp.getValue("STATION_CODE", 0));
                        orderList.addData("BED_NO", bedList.getValue("BED_NO", k));

                        orderList.addData("DISPENSE_EFF_DATE",now);
                        orderList.addData("DISPENSE_END_DATE",now);
                        orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); //开药数量
                        orderList.addData("MEDI_UNIT", sysFeeD.getValue("UNIT_CODE"));
                        orderList.addData("TAKE_DAYS", 1); //开药天数默认为1
                        orderList.addData("OPT_USER", OPT_USER);
                        orderList.addData("OPT_TERM", OPT_TERM);
                        orderList.addData("ORDER_CAT1_CODE", sysFeeD.getValue("ORDER_CAT1_CODE"));
                        orderList.addData("CAT1_TYPE", sysFeeD.getValue("CAT1_TYPE"));
                        orderList.addData("ORDERSET_GROUP_NO", h);//集合医嘱组号
                        orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));//集合医嘱主项CODE
                        orderList.addData("HIDE_FLG", "Y");//集合医嘱细项隐藏标记
                        orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                        orderList.addData("BILL_FLG", "Y");
                        orderList.addData("REQUEST_FLG", "N");//请领注记
                    }
                }
            }
            //====zhangp 20131224 分开床位费和固定费用
            TParm ibsParm = new TParm();
            ibsParm.setData("M", orderList.getData());
            ibsParm.setData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
            ibsParm.setData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
            ibsParm.setData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
            ibsParm.setData("FLG", "ADD");
            TParm IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);
            TParm ibsIN = new TParm();
            System.out.println(IBSInsert.getCount("CASE_NO"));
            for (int i = 0; i < IBSInsert.getCount("CASE_NO"); i++) {
				IBSInsert.setDataN("CASE_NO_SEQ", i, IBSInsert.getInt("CASE_NO_SEQ", i)+1);
			}
            ibsIN.setData("M", IBSInsert.getData());
            ibsIN.setData("DATA_TYPE", "0");
            ibsIN.setData("FLG", "ADD");
            //fux modify 20140219
        	//Timestamp dateNew = StringTool.rollDate(parm.getTimestamp("DATE"),0);
            //String dateStringNew = StringTool.getString(dateNew,"yyyyMMdd"); 
            String today = parm.getValue("TODAY"); 
            if("Y".equals(today)){                
            //fux modify 20140911    
            //出院时间  
            Timestamp DsDate = StringTool.rollDate(parm.getTimestamp("OUT_DATE"),0);
            String DsDateString = StringTool.getString(DsDate,"yyyyMMddHHmmss"); 
            //还要判断出院召回状态
            //ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateStringNew+"235959","yyyyMMddHHmmss"));
              ibsIN.setData("BILL_DATE",StringTool.getTimestamp(DsDateString,"yyyyMMddHHmmss"));
            } else{    
            ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateString+"235959","yyyyMMddHHmmss"));
            }
            ibsIN.setData("PATCH_AMT",PATCH_AMT);
//            System.out.println("自动过账入参》》》》"+ibsIN);  
            result = IBSTool.getInstance().insertIBSOrder(ibsIN, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            orderList = new TParm();  
            
            //根据床位等级查询 sys_bedfee 床位费用字典 计算该床位的费用
//            System.out.println("bedList~~~~~~~~~~~~~:"+bedList);
//            System.out.println("病患信息："+admInp);
            for(int k=0;k<bedList.getCount("BED_NO");k++){
//                System.out.println("BED_CLASS_CODE~~~~~~~~~:"+bedList.getValue("BED_CLASS_CODE", k));
                TParm bedBill = SYSBedFeeTool.getInstance().getSYSBedFeeOccu(bedList.
                    getValue("BED_CLASS_CODE", k),bedList.getValue("BED_OCCU_FLG",k));
                Timestamp now = SystemTool.getInstance().getDate();
//                System.out.println("bedBill==============:"+bedBill);
                for (int h = 0; h < bedBill.getCount(); h++) {
                    TParm sysFee = this.getSysFee(bedBill.getValue("ORDER_CODE", h));
                    orderList.addData("CASE_NO", CASE_NO);
                    orderList.addData("DOSAGE_QTY", "1"); //床位费用默认总量为1
                    orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
                    orderList.addData("ORDER_CODE",
                                      bedBill.getValue("ORDER_CODE", h));
                    orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
                    orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
                    orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                    orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
                    orderList.addData("STATION_CODE",
                                      admInp.getValue("STATION_CODE", 0));
                    orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
                    orderList.addData("EXEC_STATION_CODE",
                                      admInp.getValue("STATION_CODE", 0));
                    orderList.addData("BED_NO", bedList.getValue("BED_NO", k));
                    String start = bedBill.getValue("START_DATE", h);
                    String end = bedBill.getValue("END_DATE", h);
                    if(start.length()>0)
                        start = start.replace("/", "") + "000000";
                    if(end.length()>0)
                        end = end.replace("/", "") + "235959";
                    orderList.addData("DISPENSE_EFF_DATE",now);
                    orderList.addData("DISPENSE_END_DATE",now);
                    orderList.addData("MEDI_QTY", 1); //开药数量默认为1
                    orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
                    orderList.addData("TAKE_DAYS", 1); //开药天数默认为1
                    orderList.addData("OPT_USER", OPT_USER);
                    orderList.addData("OPT_TERM", OPT_TERM);
                    orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
                    orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
                    orderList.addData("ORDERSET_GROUP_NO", "");//集合医嘱组号
                    orderList.addData("ORDERSET_CODE", "");//集合医嘱主项CODE
                    orderList.addData("HIDE_FLG", "N");//集合医嘱细项隐藏标记
                    orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
                    orderList.addData("BILL_FLG", "Y");
                    orderList.addData("REQUEST_FLG", "N");//请领注记
                }
            }
            ibsParm = new TParm();
            ibsParm.setData("M", orderList.getData());
            ibsParm.setData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
            ibsParm.setData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
            ibsParm.setData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
            ibsParm.setData("FLG", "ADD");
//            System.out.println("ibsParm:"+ibsParm);
            //取回用于插入的IBS参数 
            IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);
            ibsIN = new TParm();
            ibsIN.setData("M", IBSInsert.getData());
            ibsIN.setData("DATA_TYPE", "0");
            ibsIN.setData("FLG", "ADD");
            //fux modify 20140219
            if("Y".equals(today)){   
                //fux modify 20140911    
                //出院时间
                Timestamp DsDate = StringTool.rollDate(parm.getTimestamp("OUT_DATE"),0);
                String DsDateString = StringTool.getString(DsDate,"yyyyMMddHHmmss"); 
            //ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateStringNew+"235959","yyyyMMddHHmmss"));
              ibsIN.setData("BILL_DATE",StringTool.getTimestamp(DsDateString,"yyyyMMddHHmmss"));
            } else{    
            ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateString+"235959","yyyyMMddHHmmss"));
            }
            ibsIN.setData("PATCH_AMT",PATCH_AMT);
//            System.out.println("自动过账入参》》》》"+ibsIN);
            result = IBSTool.getInstance().insertIBSOrder(ibsIN, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * 查询sys_fee信息
     * @param orderCode String
     * @return TParm
     */
    public TParm getSysFee(String orderCode){
        TParm result = new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSFee(orderCode)));
        return result.getRow(0);
    }
}
