package action.ins;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;

/**
 * <p>Title: 医保动作类(天津)</p>
 *
 * <p>Description: 医保动作类(天津)</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSActionTJ
    extends INSInterface {
    public INSActionTJ() {
    }

    public TParm regFeePartition(Object parm) {
        log("regFeePartition", "start input object");
        TParm result = new TParm();
        //检核参数是否为空
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsAction.regFeePartition->Err:参数为空");
            return result;
        }
        //检核参数类型是否正确
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsAction.regFeePartition->Err:参数类型错误");
            return result;
        }
        TParm acParm = (TParm) parm;
        return regFeePartition(acParm);
    }

    public TParm uploadDetail(Object parm) {
        log("uploadDetail", "start input object");
        TParm result = new TParm();
        //检核参数是否为空
        if (parm == null) {
            result.setErr( -1,
                          "action.ins.InsAction.uploadDetail->Err:参数为空");
            return result;
        }
        //检核参数类型是否正确
        if (! (parm instanceof TParm)) {
            result.setErr( -1,
                          "action.ins.InsAction.uploadDetail->Err:参数类型错误");
            return result;
        }
        TParm acParm = (TParm) parm;
        return uploadDetail(acParm);
    }

    public TParm createREGDetil(TParm parm) {
        TParm result = new TParm();
        //取挂号医保码和费用
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "取挂号医保码和费用->" + nhiCodeParm.getErrName());
            return result;
        }

        int count = nhiCodeParm.getCount("NHI_ORDER_CODE");
        for (int row = 0; row < count; row++) {
            TParm inData = new TParm();
            //加挂号医保码和费用
            inData.setRowData(row, nhiCodeParm);
            //加传入参数
            inData.setRowData(parm);
            //加序号
            inData.setData("SEQ_NO", row);
            //保存明细
            inData.setData("ACTION_ID", "INS_INS_DETAIL");
//      result = InsUpdateEngine.getInstance().doAction(inData);
            result = new TParm();
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 费用分割
     * @param parm TParm
     * @return TParm
     */
    public TParm regFeePartition(TParm parm) {
        log("regFeePartition", "start input TParm");
        TParm result = new TParm();

        //取挂号医保码和费用
        parm.setData("ACTION_ID", "INS_GET_NHI_CODE");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "取挂号医保码和费用->" + nhiCodeParm.getErrName());
            return result;
        }
        log("regFeePartition", "INS_GET_NHI_CODE ok");

        //最高床位费
        parm.setData("ACTION_ID", "INS_GETTIP_BEDPRICE");
//    TParm tipBedpriceParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm tipBedpriceParm = new TParm();
        if (tipBedpriceParm.getErrCode() != 0) {
            result.setErr( -1, "取最高床位费失败->" + tipBedpriceParm.getErrName());
            return result;
        }
        double tipBedprice = tipBedpriceParm.getDouble("TIP_BEDPRICE", 0);
        log("regFeePartition", "INS_GETTIP_BEDPRICE ok");

        //循环分割
        for (int i = 0; i < nhiCodeParm.getCount("ORDER_CODE"); i++) {
            log("regFeePartition", "for 分割 " + i);
            TParm inparm = new TParm();
            inparm.setData("PIPELINE", "DataDown_sp1");
            inparm.setData("PLOT_TYPE", "B");
            inparm.setData("HOSP_AREA", parm.getValue("HOSP_AREA"));
            //收费项目编码
            inparm.setData("NHI_ORDER_CODE", 0,
                           nhiCodeParm.getData("ORDER_CODE", i));
            //人员类别
            inparm.setData("CTZ1_CODE", 0, parm.getValue("CTZ1_CODE"));
            //数量
            inparm.setData("QTY", 0, 1);
            //发生金额
            inparm.setData("TOTAL_AMT", 0, nhiCodeParm.getData("TOTAL_AMT", i));
            //最高床位费
            inparm.setData("TIPTOP_BED_AMT", 0, tipBedprice);
            //增负药品标志
            inparm.setData("PHAADD_FLG", 0, "0");
            //全自费标志
            inparm.setData("FULL_OWN_FLG", 0, "0");
            //医院编码
            inparm.setData("HOSP_NHI_NO", 0, parm.getValue("HOSP_NHI_NO"));
            //住院开始时间
            inparm.setData("HOSP_START_DATE", 0,
                           SystemTool.getInstance().getDate());
            TParm returnResult = safe(inparm);
            if (returnResult.getErrCode() != 0)
                return returnResult;
            //收费项目编码
            result.addData("NHI_ORDER_CODE",
                           nhiCodeParm.getData("ORDER_CODE", i));
            //人员类别
            result.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE"));
            //数量
            result.addData("QTY", 1);
            //发生金额
            result.addData("TOTAL_AMT", nhiCodeParm.getData("TOTAL_AMT", i));
            //最高床位费
            result.addData("TIPTOP_BED_AMT", tipBedprice);
            //医院编码
            result.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));
            //程序执行状态
            result.addData("PROGRAM_STATE",
                           returnResult.getData("PROGRAM_STATE", 0));
            //程序执行信息
            result.addData("PROGRAM_MESSAGE",
                           returnResult.getData("PROGRAM_MESSAGE", 0));
            //全自费金额
            result.addData("OWN_AMT", returnResult.getData("OWN_AMT", 0));
            //增负金额
            result.addData("ADDPAY_AMT", returnResult.getData("ADDPAY_AMT", 0));
            //申报金额
            result.addData("NHI_AMT", returnResult.getData("NHI_AMT", 0));
            //自负比例
            result.addData("OWN_RATE", returnResult.getData("OWN_RATE", 0));
            //累计增负标志
            result.addData("ADDPAY_FLG", returnResult.getData("ADDPAY_FLG", 0));
            //统计代码
            result.addData("NHI_ORD_CLASS_CODE",
                           returnResult.getData("NHI_ORD_CLASS_CODE", 0));
            //门急住别
            result.addData("ADM_TYPE", parm.getValue("ADM_TYPE"));
            //号别
            result.addData("CLINIC_TYPE_CODE", parm.getValue("CLINIC_TYPE_CODE"));
        }
        return result;
    }

    /**
     * 费用明细上传
     * @param parm TParm HOSP_AREA,CASE_NO,SEQ_NO,HOSP_NHI_NO
     * @return TParm
     */
    public TParm uploadDetail(TParm parm) {
        log("uploadDetail", "start input TParm");
        TParm result = new TParm();
        //取费用明细
        parm.setData("ACTION_ID", "INS_GET_UPLOAD_DETAIL");
//    TParm nhiCodeParm = InsUpdateEngine.getInstance().doAction(parm);
        TParm nhiCodeParm = new TParm();
        if (nhiCodeParm.getErrCode() != 0) {
            result.setErr( -1, "取费用明细->" + nhiCodeParm.getErrName());
            return result;
        }
        log("uploadDetail", "INS_GET_UPLOAD_DETAIL ok");
        //取医院编码
        result.setData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));
        //传送医保接口
        result.setData("PIPELINE", "DataUpload");
        result.setData("PLOT_TYPE", "B");
        return safe(result);
    }

    public static void main(String args[]) {
        INSActionTJ driver = new INSActionTJ();
        TParm parm = new TParm();
        //-----测试挂号费用分割----//
        parm.setData("HOSP_AREA", "HIS");
        parm.setData("REGION_CODE", "101");
        //人员类别
        parm.setData("CTZ1_CODE", "51");
        //门急住别
        parm.setData("ADM_TYPE", "O");
        //号别
        parm.setData("CLINICTYPE_CODE", "03");

        parm.setData("CASE_NO", "001");
        parm.setData("OPT_USER", "tiis");
        parm.setData("OPT_TERM", "1.1.1.2");
        //医院编码
        //parm.setCommitData("HOSP_NHI_NO","000551");
        //费用分割
        //System.out.println(driver.regFeePartition(parm));

        //System.out.println(driver.createREGDetil(parm));
    }
}
