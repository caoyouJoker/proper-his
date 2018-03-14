package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 *
 * <p>Title:门诊医保审核工具类 </p>
 *
 * <p>Description:门诊医保审核工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class INSOpdApproveTool extends TJDOTool {
    /**
     * 实例
     */
    public static INSOpdApproveTool instanceObject;
    /**
     * 得到实例
     * @return INSOpdApproveTool
     */
    public static INSOpdApproveTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSOpdApproveTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSOpdApproveTool() {
        setModuleName("ins\\INSOpdApproveModule.x");
        onInit();
    }

    /**
     * 得到界面信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selPatInfo(TParm parm) {
        TParm initparm = new TParm();
        initparm.setData("ADM_DATE", parm.getValue("ADM_DATE"));
        initparm.setData("MR_NO", parm.getValue("MR_NO"));
        TParm result = new TParm();
        result = this.query("selPatInfo", initparm);
        return result;
    }

    /**
     * 更新门诊医保审核信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection conn) {
        TParm result = update("updatedata", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询审核主档信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入审核主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //查询OPB_TRAN中所有order
        TParm parm1 = new TParm();
        parm1.setRowData(parm);
        TParm data1 = query("selOpbTran", parm1);
        if (data1.getErrCode() < 0) {
            err("ERR:" + data1.getErrCode() + data1.getErrText()
                + data1.getErrName());
            result.setErr(data1);
            return result;
        }
        int count1 = data1.getCount();
        for (int i = 0; i < count1; i++) {
            //医嘱代码
            String billDate = StringTool.getString((Timestamp) data1.getData(
                    "ORDER_TIME", i), "yyyyMMdd");
            String orderNo = data1.getValue("ORDER_NO", i);
            int orderSeq = data1.getInt("ORDER_SEQ", i);
            String orderCode = data1.getValue("ORDER_CODE", i);
            String orderSet = data1.getValue("ORDERSET_CODE", i);
            String ordsetGroupNo = data1.getValue("ORDSET_GROUP_NO", i);
            String deptcode = data1.getValue("DEPT_CODE", i);
            String drCode = data1.getValue("DR_CODE", i);
            String rborderDeptCode = data1.getValue("RBORDER_DEPT_CODE", i);
            String doseCode = data1.getValue("DOSE_CODE", i);
            double price = data1.getDouble("OWN_PRICE", i);
            double totQty = data1.getDouble("TOT_QTY", i);
            double totalAmt = data1.getDouble("TOT_AMT", i);
            //查询支付标准中的所有未审核order
            TParm parm2 = new TParm();
            parm2.setData("ORDER_CODE", orderCode);
            parm2.setData("CTZ1_CODE", parm1.getValue("CTZ1_CODE"));
            if (parm1.getValue("CTZ2_CODE") == null ||
                parm1.getValue("CTZ2_CODE").length() < 1) {
                parm2.setData("CZT2_CODENULL", "00");
            } else {
                parm2.setData("CTZ2_CODE", parm1.getValue("CTZ2_CODE"));
            }
            TParm data2 = query("getOrderInRule", parm2);
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return result;
            }
            if (data2.getCount() <= 0)
                continue;
            TParm parm4 = new TParm();
            TParm endData = new TParm();

            parm4.setData("CASE_NO",parm.getData("CASE_NO"));
            parm4.setData("ORDER_CODE", orderCode);
            parm4.setData("ORDER_NO", orderNo);
            parm4.setData("ORDER_SEQ", orderSeq);
            //判断是否已存在于审核主档中
            endData = this.existsOrder(parm4);
            if (endData.getCount() > 0)
                continue;
            TParm parm5 = new TParm(); //插入审核档数据
            parm5.setData("CASE_NO", data1.getData("CASE_NO", 0)); //就诊序号
            parm5.setData("BILL_DATE", billDate); //明细帐日期时间
            parm5.setData("ORDER_NO", orderNo); //处方签号
            parm5.setData("ORDER_SEQ", orderSeq); //医嘱序号
            parm5.setData("ORDER_CODE", orderCode); //医嘱代码
            parm5.setData("NHI_ORDER_CODE", orderCode); //医保医嘱代码
            parm5.setData("ORDERSET_CODE", orderSet); //集合医嘱
            parm5.setData("ORDER_GROUP_NO", ordsetGroupNo); //集合医嘱群组
            parm5.setData("DEPT_CODE", deptcode); //开单科室
            parm5.setData("DR_CODE", drCode); //开单医生
            parm5.setData("EXE_DEPT_CODE", rborderDeptCode); //执行科室
            parm5.setData("APPROVE_FLG", parm.getData("APPROVE_FLG")); //审核状态

            TParm parm6 = new TParm(); //
            parm6.setData("ORDER_CODE", orderCode);
            TParm getOrderInfo1 = this.getOrderInfo1(parm6);
            parm5.setData("ORDER_DESC", getOrderInfo1.getValue("ORDER_DESC", 0)); //医嘱名称
            parm5.setData("STANDARD", getOrderInfo1.getValue("DESCRIPTION", 0)); //规格
            TParm parm7 = new TParm();
            parm7.setData("DOSE_CODE", doseCode);
            TParm getOrderInfo2 = this.getOrderInfo2(parm7);
            parm5.setData("DOSE_CODE", doseCode); //剂型代码
            parm5.setData("DOSE_DESC",
                          getOrderInfo2.getValue("DOSE_CHN_DESC", 0)); //剂型名称
            TParm parm8 = new TParm();
            parm8.setData("CTZ1_CODE", parm1.getData("CTZ1_CODE"));
            if (parm1.getValue("CTZ2_CODE") == null ||
                parm1.getValue("CTZ2_CODE").length() < 1) {
                parm8.setData("CZT2_CODENULL", "00");
            } else {
                parm8.setData("CTZ2_CODE", parm1.getValue("CTZ2_CODE"));
            }
            parm8.setData("ORDER_CODE", orderCode);
            TParm getOrderInfo3 = this.getOrderInfo3(parm8);
            parm5.setData("OWN_RATE", getOrderInfo3.getDouble("OWN_RATE", 0)); //自付比例
            double ownRate = getOrderInfo3.getDouble("OWN_RATE", 0);
            double ownAmt = ownRate * totalAmt;
            double nhiAmt = totalAmt - ownAmt;
//            System.out.println("1111"+ownRate);
//            System.out.println("2222"+ownAmt);
//            System.out.println("3333"+nhiAmt);
            parm5.setData("PRICE", StringTool.round(price, 4)); //单价
            parm5.setData("QTY", StringTool.round(totQty, 2)); //数量
            parm5.setData("OWN_AMT", StringTool.round(ownAmt, 2)); //自付金额
            parm5.setData("NHI_AMT", StringTool.round(nhiAmt, 2)); //医保支付金额
            parm5.setData("TOTAL_AMT", StringTool.round(totalAmt, 2)); //医保申报金额
            parm5.setData("OPT_USER", parm.getData("OPT_USER")); //操作人员
            parm5.setData("OPT_TERM", parm.getData("OPT_TERM")); //操作端末
            result = this.update("insertInfo", parm5, conn);

        }
        return result;
    }

    /**
     * 从支付标准中查出需审核的医嘱代码
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInRule(TParm parm) {
        TParm result = query("getOrderInRule", parm);
        return result;
    }

    /**
     * 通过病案号得到就诊序号集
     * @param parm TParm
     * @return TParm
     */
    public TParm getCaseNoByMrNo(TParm parm) {
        TParm data = new TParm();
        data.setData("ADM_DATE", parm.getValue("ADM_DATE"));
        data.setData("MR_NO", parm.getValue("MR_NO"));
        TParm result = query("getCaseNoByMrNo", data);
        return result;
    }

    /**
     * 判断order是否已存在
     * @param parm TParm
     * @return TParm
     */
    public TParm existsOrder(TParm parm) {
        TParm result = query("existsOrder", parm);
        return result;
    }

    /**
     * order信息缺省字段查询,医嘱名称,剂型名称,规格
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo1(TParm parm) {
        TParm result = query("selOrderInfo", parm);
        return result;
    }

    /**
     * order信息缺省字段查询,剂型名称
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo2(TParm parm) {
        TParm result = query("selDoseDesc", parm);
        return result;
    }

    /**
     * order信息缺省字段查询,自付比例
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInfo3(TParm parm) {
        TParm result = query("selOwnRate", parm);
        return result;
    }

    /**
     * 根据主身份得到特约请款机关代码
     * @param ctzCode String
     * @return TParm
     */
    public TParm getCompanyByCtz(String ctzCode) {
        TParm data = new TParm();
        data.setData("CTZ_CODE", ctzCode);
        TParm result = query("getCompanyByCtz", data);
        return result;
    }

    /**
     * 从挂号主档得到主身份
     * @param caseNo String
     * @return TParm
     */
    public TParm getCtzInRegPatadm(String caseNo) {
       // System.out.println("进入jdo");
        TParm data = new TParm();
        data.setData("CASE_NO", caseNo);
        TParm result = query("getCtzInRegPatadm", data);
       // System.out.println("result"+result);
        return result;
    }

    /**
     * 根据身份代码得到医保身份标记
     * @param ctzCode String
     * @return TParm
     */
    public TParm getFlgByCtz(String ctzCode) {
        TParm data = new TParm();
        data.setData("CTZ_CODE", ctzCode);
        TParm result = query("getFlgByCtz", data);
        return result;
    }

    /**
     * 查询支付标准中的备注
     * @param ctzCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm selDescription(String ctzCode, String ctzCode2,
                                String orderCode) {
        TParm data = new TParm();
        data.setData("CTZ1_CODE", ctzCode);
        data.setData("ORDER_CODE", orderCode);
        if (ctzCode2 == null || ctzCode2.length() < 1) {
            data.setData("CZT2_CODENULL", "00");
        } else {
            data.setData("CTZ2_CODE", ctzCode2);
        }
        TParm result = query("selDescription", data);
        return result;

    }

    /**
     * 更新界面身份
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCtz(TParm parm, TConnection conn) {
        TParm result = update("updateCtz", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除审核档数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delApprove(TParm parm, TConnection conn) {
        TParm result = update("delApprove", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新当前就诊病人所有order审核状态位
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAllApprove(TParm parm) {
        TParm result = update("updateAllApprove", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
