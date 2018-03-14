package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.manager.TCM_Transform;
import java.util.Vector;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title:门诊医保市医保分割工具类 </p>
 *
 * <p>Description:门诊医保市医保分割工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.12
 * @version JavaHis 1.0
 */
public class INSOpdOrderTool extends TJDOTool {
    /**
     * 实例
     */
    public static INSOpdOrderTool instanceObject;
    /**
     * 得到实例
     * @return INSOpdApproveTool
     */
    public static INSOpdOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSOpdOrderTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSOpdOrderTool() {
        setModuleName("ins\\INSOpdOrderModule.x");
        onInit();
    }

    /**
     * 插入分割主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String orderCode = parm.getValue("ORDER_CODE");
        String orderNo = parm.getValue("ORDER_NO");
        int orderSeq = TCM_Transform.getInt(parm.getValue("ORDER_SEQ"));
        String billDate = TCM_Transform.getString(parm.getValue("BILL_DATE"));
        String ordersetCode = parm.getValue("ORDERSET_CODE");
        int orderGroupNo = parm.getInt("ORDER_GROUP_NO");
        String deptCode = parm.getValue("DEPT_CODE");
        String drCode = parm.getValue("DR_CODE");
        String exeDeptCode = parm.getValue("EXE_DEPT_CODE");
        String doseCode = parm.getValue("DOSE_CODE");
        double price = parm.getDouble("PRICE");
        double totQty = parm.getDouble("QTY");
        double totalAmt = parm.getDouble("TOT_AMT");


        TParm getCtzInRegPatadm = INSOpdApproveTool.getInstance().
                                  getCtzInRegPatadm(caseNo);
        String ctzCode1 = getCtzInRegPatadm.getValue("CTZ1_CODE", 0);
        String ctzCode2 = getCtzInRegPatadm.getValue("CTZ2_CODE", 0);
        TParm parm1 = new TParm();
        parm1.setData("CASE_NO", caseNo);
        parm1.setData("ORDER_CODE", orderCode);
        parm1.setData("ORDER_NO", orderNo);
        parm1.setData("ORDER_SEQ", orderSeq);
        //查询支付标准中的所有需审核,且审核档中审核通过order
        TParm parm2 = new TParm();
        parm2.setData("CASE_NO",caseNo);
        parm2.setData("ORDER_CODE", orderCode);
        parm2.setData("CTZ1_CODE", ctzCode1);
        TJDODBTool tool = TJDODBTool.getInstance();
        TParm data2 = new TParm();
        if (ctzCode2 == null ||ctzCode2.trim().length() ==0|| ctzCode2.equals("null")){
            parm2.setData("CZT2_CODENULL", "00");
            String sql =
                    "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                    "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                    "      FROM INS_OPD_RULE A " +
                    " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" + caseNo + "' "+
                    "       AND A.ORDER_CODE = '" + orderCode +"' AND A.CTZ1_CODE='" + ctzCode1 +"' "+
                    "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00')  " +
                    "  AND ORDER_GROUP_NO= '"+orderGroupNo+"'"+
                    "  WHERE A.ORDER_CODE = '" + orderCode + "' AND A.CTZ1_CODE='" +ctzCode1 + "' " +
                    "  AND (A.CTZ2_CODE IS NULL OR A.CTZ2_CODE= '00') ";
//            System.out.println("==========="+sql);
             data2 = new TParm(tool.select(sql));
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return data2;
            }
        } else {
            parm2.setData("CTZ2_CODE", ctzCode2);
             data2 = new TParm(tool.select(
                    "    SELECT A.ORDER_CODE,A.OWN_RATE,A.ADDPAY_FLG,A.ADDPAY_RATE," +
                    "           A.APPROVE_FLG AS APPROVE_FLGA,NVL(B.APPROVE_FLG,'01') AS APPROVE_FLG " +
                    "      FROM INS_OPD_RULE A " +
                    " LEFT JOIN INS_OPD_APPROVE B ON A.ORDER_CODE=B.ORDER_CODE AND B.CASE_NO = '" + caseNo + "' "+
                    "       AND A.ORDER_CODE = '" + orderCode +"' AND A.CTZ1_CODE='" + ctzCode1 +"' "+
                    "  AND A.CTZ2_CODE='" + ctzCode2 +"' " +
                    "  AND ORDER_GROUP_NO= '"+orderGroupNo+"'"+
                    "  WHERE A.ORDER_CODE = '" + orderCode + "' AND A.CTZ1_CODE='" +ctzCode1 + "' " +
                    "  AND A.CTZ2_CODE='" + ctzCode2 +"' "));
            if (data2.getErrCode() < 0) {
                err("ERR:" + data2.getErrCode() + data2.getErrText()
                    + data2.getErrName());
                return data2;
            }
            }
            int rowCount = data2.getInt("ACTION","COUNT");
            if(rowCount==0){
                if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", 0)) == null ||
                    TCM_Transform.getString(data2.getData("APPROVE_FLG", 0)) == null ) {
                    Vector vctdata2 = new Vector();
                    vctdata2.add(0, 1);
                    data2.setData("OWN_RATE", vctdata2);
                }
                if (TCM_Transform.isNull(data2.getData("OWN_RATE", 0))) {
                    data2.addData("OWN_RATE", 1);
                    data2.addData("APPROVE_FLG", "04");
                }
            }
            for (int i = 0; i < rowCount; i++) {
//                System.out.println("每条ORDER1"+"ORDER_CODE"+data2.getData("ORDER_CODE",i)+data2.getData("APPROVE_FLGA", i));
//                System.out.println("每条ORDER2"+"ORDER_CODE"+data2.getData("ORDER_CODE",i)+data2.getData("APPROVE_FLG", i));
                if (TCM_Transform.getString(data2.getData("APPROVE_FLGA", i)) == null ||
                    TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                    equals("04") ||
                    (TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                     equals("01") &&
                     TCM_Transform.getString(data2.getData("APPROVE_FLGA",
                        i)).equals("Y")) ||
                    TCM_Transform.getString(data2.getData("APPROVE_FLG", i)) == null ||
                    TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                    length() == 0 ||
                    TCM_Transform.getString(data2.getData("APPROVE_FLG", i)).
                    equals("02")) {
                    Vector vctdata2 = new Vector();
                    vctdata2.add(0, 1);
                    data2.setData("OWN_RATE", vctdata2);
                }
                if (TCM_Transform.isNull(data2.getData("OWN_RATE", i))) {
                    data2.addData("OWN_RATE", 1);
                    data2.addData("APPROVE_FLG", "04");
                }
            }

//        TParm parm3 = new TParm();
//        parm3.setData("ORDER_CODE", orderCode);
//        parm3.setData("CTZ1_CODE", ctzCode1);
//        if (ctzCode2 == null || ctzCode2.length() < 1) {
//            parm2.setData("CZT2_CODENULL", "00");
//        } else {
//            parm2.setData("CTZ2_CODE", ctzCode2);
//            }
//        TParm data3 = this.getApproveFlg(parm3);
//        if (data3.getErrCode() < 0) {
//            err("ERR:" + data2.getErrCode() + data2.getErrText()
//                + data2.getErrName());
//            return result;
//        }
//        if (TCM_Transform.isNull(data3.getData("APPROVE_FLG"))) {
//            data3.addData("APPROVE_FLG", "01");
//        }
        TParm parm4 = new TParm();
        TParm endData = new TParm();
        parm4.setData("CASE_NO",caseNo);
        parm4.setData("ORDER_NO", orderNo);
        parm4.setData("ORDER_SEQ", orderSeq);
        //判断是否已存在于分割主档中
        endData = INSOpdOrderTool.getInstance().existsOrder(parm4);
       // System.out.println("存在个数"+endData.getCount());
        if (endData.getCount() > 0)
            return endData;
        TParm parm5 = new TParm();
        parm5.setData("CASE_NO", caseNo); //就诊序号
//        String billDate = StringTool.getString((Timestamp) data1.getData(
//                "ORDER_TIME", 0), "yyyyMMdd");//弃用数据
        parm5.setData("BILL_DATE", billDate); //明细帐日期时间
        parm5.setData("ORDER_NO", orderNo); //处方签号
        parm5.setData("ORDER_SEQ", orderSeq); //医嘱序号
        parm5.setData("ORDER_CODE", orderCode); //医嘱代码
        parm5.setData("NHI_ORDER_CODE", orderCode); //医保医嘱代码
//        parm5.setData("ORDERSET_CODE", data1.getValue("ORDERSET_CODE", 0)); //弃用数据
//        parm5.setData("ORDER_GROUP_NO", data1.getDouble("ORDSET_GROUP_NO", 0));//弃用数据
//        parm5.setData("DEPT_CODE", data1.getValue("DEPT_CODE", 0)); //弃用数据
//        parm5.setData("DR_CODE", data1.getValue("DR_CODE", 0));//弃用数据
//        parm5.setData("EXE_DEPT_CODE", data1.getValue("RBORDER_DEPT_CODE", 0));//弃用数据
        parm5.setData("ORDERSET_CODE",ordersetCode); //集合医嘱
        parm5.setData("ORDER_GROUP_NO", orderGroupNo); //集合医嘱群组
        parm5.setData("DEPT_CODE", deptCode); //开单科室
        parm5.setData("DR_CODE", drCode); //开单医生
        parm5.setData("EXE_DEPT_CODE",exeDeptCode); //执行科室
        parm5.setData("APPROVE_FLG", data2.getValue("APPROVE_FLG", 0)); //审核状态
        TParm parm6 = new TParm();
        parm6.setData("ORDER_CODE", orderCode);
        TParm getOrderInfo1 = INSOpdApproveTool.getInstance().getOrderInfo1(
                parm6);
//        System.out.println("order信息缺省字段查询,医嘱名称,剂型名称,规格"+getOrderInfo1);
        parm5.setData("ORDER_DESC", getOrderInfo1.getValue("ORDER_DESC", 0)); //医嘱名称
        parm5.setData("STANDARD", getOrderInfo1.getValue("DESCRIPTION", 0)); //规格
        TParm parm7 = new TParm();
        parm7.setData("DOSE_CODE", doseCode);
        TParm getOrderInfo2 = INSOpdApproveTool.getInstance().getOrderInfo2(
                parm7);
//        System.out.println(" order信息缺省字段查询,剂型名称"+getOrderInfo2);
        String doseDesc = TCM_Transform.getString(getOrderInfo2.getData(
                "DOSE_CHN_DESC", 0));
        parm5.setData("DOSE_CODE", doseCode); //剂型代码
        parm5.setData("DOSE_DESC", doseDesc); //剂型名称
        parm5.setData("OWN_RATE", data2.getDouble("OWN_RATE", 0)); //自付比例
        double ownRate = data2.getDouble("OWN_RATE", 0);
        double ownAmt = ownRate * totalAmt;
        double nhiAmt = totalAmt - ownAmt;
        String addpayFlg = TCM_Transform.getString(data2.getData("ADDPAY_FLG",0));
        double addpayRate = TCM_Transform.getDouble(data2.getData("ADDPAY_RATE",0));
        if(addpayFlg.equals("Y")){
            ownAmt = ownAmt + nhiAmt * addpayRate;
            nhiAmt = totalAmt - ownAmt ;
        }
//        double price = data1.getDouble("OWN_PRICE", 0);//弃用数据
//        double totQty = data1.getDouble("TOT_QTY", 0);//弃用数据
//        double totalAmt = data1.getDouble("TOT_AMT", 0);//弃用数据
        parm5.setData("PRICE", StringTool.round(price, 4)); //单价
        parm5.setData("QTY", StringTool.round(totQty, 2)); //数量
        parm5.setData("OWN_AMT", StringTool.round(ownAmt, 2)); //自付金额
        parm5.setData("NHI_AMT", StringTool.round(nhiAmt, 2)); //医保支付金额
        parm5.setData("TOTAL_AMT", StringTool.round(totalAmt, 2)); //医保申报金额
        parm5.setData("OPT_USER", parm.getData("OPT_USER")); //操作人员
        parm5.setData("OPT_TERM", parm.getData("OPT_TERM")); //操作端末
//        System.out.println("要更新的数据"+parm5);
        result = this.update("insertInfo", parm5, conn);

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
     * 查询opb_tran中要进行费用分割的order
     * @param parm TParm
     * @return TParm
     */
    public TParm selOpbTran(TParm parm) {
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
     * 从支付标准中查出需审核的医嘱代码
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderInRule(TParm parm) {
        TParm result = query("getOrderInRule", parm);
        return result;
    }

    /**
     * 判断order是否已存在在分割主档
     * @param parm TParm
     * @return TParm
     */
    public TParm existsOrder(TParm parm) {
        TParm result = query("existsOrder", parm);
        return result;
    }

    /**
     * 得到审核标记位
     * @param parm TParm
     * @return TParm
     */
    public TParm getApproveFlg(TParm parm) {
        TParm result = query("getApproveFlg", parm);
        return result;
    }
    /**
     * 查询分割档所有数据
     * @return TParm
     */
    public TParm selectalldata(TParm parm){
        TParm result = query("selectalldata",parm);
        return result;

    }
    /**
     * 插入结算主档
     * @param parm TParm
     * @return TParm
     */
    public TParm insertPayMInfo(TParm parm,TConnection conn) {
        TParm result = update("insertPayMInfo", parm,conn);
        return result;
    }
    /**
     * 为结算主档捞取数据
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataforpaym(TParm parm) {
        TParm result = query("seldataforpaym", parm);
        return result;
    }
    /**
     * 判断是否已存在于结算主档
     * @param parm TParm
     * @return TParm
     */
    public TParm existsPaym(TParm parm) {
        TParm result = query("existsPaym", parm);
        return result;
    }
    /**
     * 取得补充计价order_seq
     * @param parm TParm
     * @return TParm
     */
    public TParm getAddOrderSeq(TParm parm){
        TParm result = query("getAddOrderSeq", parm);
        return result;
    }
    /**
     * 删除分割主档
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteOrder(TParm parm){
        TParm result = update("deleteOrder", parm);
        return result;
    }
    /**
     * 删除结算主档
     * @param parm TParm
     * @return TParm
     */
    public TParm deletePaym(TParm parm){
        TParm result = update("deletePaym", parm);
        return result;
    }
    /**
     * 从支付字典中查询医保支付类别
     * @param parm TParm
     * @return TParm
     */
    public TParm selInsPayTypeInRule(TParm parm){
        TParm result = query("selInsPayTypeInRule", parm);
        return result;

    }
}
