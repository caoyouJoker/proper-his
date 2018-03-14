package action.pha;

import java.sql.Timestamp;

import jdo.ind.INDTool;
import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.pha.PHAOrderTool;
import jdo.pha.PhaOrderHistoryTool;
import jdo.reg.PatAdmTool;
import jdo.util.Medicine;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: 门诊药房动作类</p>
 *
 * <p>Description: 门诊药房动作类</p>
 *
 * <p>Copyright: JavaHis (c) 深圳中航 2008 更改 2011</p>
 *
 * @author ZangJH 2008-09-26 ZhenQin 2011-04-26
 * @version 1.0
 */

public class PHAAction
    extends TAction {
    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {

        TParm result = new TParm();
        TParm orderresult = new TParm();

        // 查询医嘱
        orderresult = OrderTool.getInstance().queryForPHA(parm);
        if (orderresult.getErrCode() != 0) {
            return err(orderresult);
        }

        result.setData("ORDER", orderresult.getData());

        return orderresult;
    }

    /**
     * 药房保存入口
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        boolean returnFlg = false;
        //创建一个连接，在多事物的时候连接各个操作使用
        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm orderparm = new TParm();
        // 保存医嘱
        if (parm.existData("ORDER")) {
            orderparm = parm.getParm("ORDER");
            result = PHAOrderTool.getInstance().onSave(orderparm, connection);
        }
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        //检测“参数”的DATA域是否有数值
        if (parm.existData("HISTORY")) {
            //有history项目的时候是退药
            returnFlg = true;
            TParm historyparm = parm.getParm("HISTORY");
            result = PhaOrderHistoryTool.getInstance().onInsert(historyparm,
                connection);
        }
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        if (parm.getBoolean("forIND")) {
            TParm parmCostAmt = new TParm();
            //在这里构造一个parmCostAmt,以后需要用addParm的方式,需要这四笔数据,如果不添加,则需要用一个for循环用addData的方式添加
            parmCostAmt.addData("CASE_NO",null);
            parmCostAmt.addData("RX_NO", null);
            parmCostAmt.addData("SEQ_NO",null);
            parmCostAmt.addData("COST_AMT", null);
            //***************************************************************
            //luhai 2012-1-27 add //加入其他的扣库参数来构造改TParm begin
            //***************************************************************
            parmCostAmt.addData("BATCH_SEQ1", null);
            parmCostAmt.addData("BATCH_SEQ2", null);
            parmCostAmt.addData("BATCH_SEQ3", null);
            parmCostAmt.addData("VERIFYIN_PRICE1", null);
            parmCostAmt.addData("VERIFYIN_PRICE2", null);
            parmCostAmt.addData("VERIFYIN_PRICE3", null);
            parmCostAmt.addData("DISPENSE_QTY1", null);
            parmCostAmt.addData("DISPENSE_QTY2", null);
            parmCostAmt.addData("DISPENSE_QTY3", null);
            //***************************************************************
            //luhai 2012-1-27 add //加入其他的扣库参数来构造改TParm end
            //***************************************************************
            //luhai 2012-1-27 end 
            //取得系统时间
            Timestamp optTime = TJDODBTool.getInstance().getDBTime();
            //保存库存
            TParm parmToIND = new TParm();
            //取得病人的处方签
            TParm forIndParm = orderparm.getParm(OrderList.MODIFIED);
            /**
             * 原 trimedforIndParm - forIndParm (增加了cleanUp)
             * 更改ZhenQin - 2011-04-26
             */
            if(result.getErrCode() < 0){
                connection.close();
                System.out.println(result.getErrText());
                return result;

            }

            /**
             * 更改ZhenQin - 2011-04-26
             */
            //经过整理过的处方签,每笔药品不重复
            TParm trimedforIndParm = cleanUp(forIndParm);
            String message = "";
            for (int i = 0; i < trimedforIndParm.getCount(); i++) {
                //获得一条药品的参数--传给IND
                parmToIND.setData("ORG_CODE", trimedforIndParm.getData("EXEC_DEPT_CODE", i));
                parmToIND.setData("ORDER_CODE",
                                  trimedforIndParm.getData("ORDER_CODE", i));
                parmToIND.setData("DOSAGE_QTY",
                                  trimedforIndParm.getData("DOSAGE_QTY", i));
                parmToIND.setData("OPT_DATE", optTime);
                parmToIND.setData("OPT_USER",trimedforIndParm.getData("OPT_USER", i));
                parmToIND.setData("OPT_TERM",trimedforIndParm.getData("OPT_TERM", i));
                //IND接口类
                Medicine subInd = new Medicine();
                //当取消退药(finishFlag->Y)
                if (returnFlg && parm.getBoolean("finishFlag") &&
                    !subInd.checkIndStockQty(parmToIND)) {
                    message += (trimedforIndParm.getData("ORDER_DESC", i) + "\n");
                }
                //为假的时候--非退药
                if (!returnFlg && !parm.getBoolean("ISUNDISP_FLG") &&
                    !subInd.checkIndStockQty(parmToIND)) {
                    message += (trimedforIndParm.getData("ORDER_DESC", i) + "\n");
                }
            }
            if(message.length() != 0){
                connection.rollback();
                connection.close();
                result.setErr( -1, message + "药品库存不足\n不可扣库！");
                return result;
            }
            //*******************************************************************************
            // luhai modify 扣库后还需回写门诊的batchSeq begin
            //*******************************************************************************
//            //扣库明细,这个列表中包含所有的扣库回传值
//            TParm costAwtList = new TParm();
//            for (int i = 0; i < trimedforIndParm.getCount(); i++) {
//
//                //获得一条药品的参数--传给IND
//                parmToIND.setData("ORG_CODE", trimedforIndParm.getData("EXEC_DEPT_CODE", i));
//                parmToIND.setData("ORDER_CODE",
//                                  trimedforIndParm.getData("ORDER_CODE", i));
//                parmToIND.setData("DOSAGE_QTY",
//                                  trimedforIndParm.getData("DOSAGE_QTY", i));
//                parmToIND.setData("CASE_NO",trimedforIndParm.getData("CASE_NO", i));
//                parmToIND.setData("OPT_DATE",optTime);
//                parmToIND.setData("OPT_USER",trimedforIndParm.getData("OPT_USER", i));
//                parmToIND.setData("OPT_TERM",trimedforIndParm.getData("OPT_TERM", i));
//                //IND接口类
//                Medicine subInd = new Medicine();
//                //当取消退药(finishFlag->Y)
//                if (returnFlg && parm.getBoolean("finishFlag") &&
//                    !subInd.checkIndStockQty(parmToIND)) {
//                    System.out.println(result.getErrText());
//                    connection.close();
//                    result.setErr( -1, "药品库存不足\n不可扣库！");
//                    return result;
//                }
//                //取得CASE_NO
//                String caseNo = parmToIND.getValue("CASE_NO");
//                //通过CASE_NO得到病患信息
//                TParm caseNoParm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
//
//                //为假的时候--非退药
//                if (!returnFlg) {
//                    if (parm.getBoolean("ISUNDISP_FLG")) {
//                        result = INDTool.getInstance().unReduceIndStock(
//                            parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0), connection);
//                        parmCostAmt.addData("CASE_NO",caseNo);
//                        parmCostAmt.addData("RX_NO", trimedforIndParm.getData("RX_NO", i));
//                        parmCostAmt.addData("SEQ_NO",
//                                        trimedforIndParm.getData("SEQ_NO", i));
//                        parmCostAmt.addData("COST_AMT", 0);
//                } else {
//                    //处方上需要一个ORG_CODE,ORDER_CODE,DOSAGE_QTY,OPT_USER,OPT_DATE,OPT_TERM必须需要这六个数据
//                    //验证库存
//                    if (!subInd.checkIndStockQty(parmToIND)) {
//                        System.out.println(result.getErrText());
//                        result.setErr( -1, "药品库存不足\n不可扣库！");
//                        return result;
//                    }
//                    //扣库动作
//                    result = INDTool.getInstance().reduceIndStock(parmToIND,
//                        caseNoParm.getValue("SERVICE_LEVEL", 0),
//                        connection);
//                    for(int n = 0; n < result.getCount("ORDER_CODE"); n++){
//                        costAwtList.addData("CASE_NO",    caseNo);
//                        costAwtList.addData("ORDER_CODE", result.getData("ORDER_CODE",   n));
//                        costAwtList.addData("ORG_CODE",   result.getData("ORG_CODE",   n));
//                        costAwtList.addData("PRICE",      result.getData("PRICE",   n));
//                        costAwtList.addData("QTY",        result.getData("QTY",   n));
//                        costAwtList.addData("RX_NO",      trimedforIndParm.getData("RX_NO", i));
//                        costAwtList.addData("SEQ_NO",     trimedforIndParm.getData("SEQ_NO", i));
//                        costAwtList.addData("COST_AMT",   result.getData("COST_AMT"));
//                    }
//                }
//            } else {
//                //退药--上库动作
//                result = INDTool.getInstance().regressIndStock(parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0),
//                    connection);
//
//                parmCostAmt.addData("CASE_NO", caseNo);
//                parmCostAmt.addData("RX_NO", trimedforIndParm.getData("RX_NO", i));
//                parmCostAmt.addData("SEQ_NO", trimedforIndParm.getData("SEQ_NO", i));
//                parmCostAmt.addData("COST_AMT", 0);
//            }
//
//
//                /*
//                 * 原  ZhenQin-110426更改
//                 *
//                 */
//
//                /*
//                //为假的时候--非退药
//                if (!returnFlg) {
//                    if (parm.getBoolean("ISUNDISP_FLG")) {
//                        result = INDTool.getInstance().unReduceIndStock(
//                            parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0), connection);
//                        parmCostAmt.addData("CASE_NO",forIndParm.getData("CASE_NO",i));
//                        parmCostAmt.addData("RX_NO",forIndParm.getData("RX_NO",i));
//                        parmCostAmt.addData("SEQ_NO",forIndParm.getData("SEQ_NO",i));
//                        parmCostAmt.addData("COST_AMT",0);
//                    }
//                    else {
//                        if (!subInd.checkIndStockQty(parmToIND)) {
//                            System.out.println(result.getErrText());
//                            connection.close();
//                            result.setErr( -1, "药品库存不足\n不可扣库！");
//                            return result;
//                        }
//                        //扣库动作
//                        result = new TParm(subInd.reduceIndStock(parmToIND,caseNoParm.getValue("SERVICE_LEVEL",0),
//                            connection));
//
//                        parmCostAmt.addData("CASE_NO",forIndParm.getData("CASE_NO",i));
//                        parmCostAmt.addData("RX_NO",forIndParm.getData("RX_NO",i));
//                        parmCostAmt.addData("SEQ_NO",forIndParm.getData("SEQ_NO",i));
//                        parmCostAmt.addData("COST_AMT",result.getData("COST_AMT"));
//                    }
//                }
//                else {
//                    //退药--上库动作
//                    result = INDTool.getInstance().regressIndStock(parmToIND,caseNoParm.getValue("SERVICE_LEVEL",0),
//                        connection);
//                    parmCostAmt.addData("CASE_NO",forIndParm.getData("CASE_NO",i));
//                    parmCostAmt.addData("RX_NO",forIndParm.getData("RX_NO",i));
//                    parmCostAmt.addData("SEQ_NO",forIndParm.getData("SEQ_NO",i));
//                    parmCostAmt.addData("COST_AMT",0);
//                }
//
//                if (result.getErrCode() < 0) {
//                    System.out.println(result.getErrText());
//                    connection.close();
//                    return result;
//                }
//                */
//            }
//            //计算成本金额,result中包含ORG_CODE,ORDER_CODE,QTY,PRICE
//
//            /**
//             * tmpParm:{Data={CASE_NO=[110426000005, 110426000005, 110426000005, 110426000005],
//             * ORG_NO=[1101030903, 1101030903, 1101030903, 1101030903],
//             * RX_NO=[110426000002, 110426000002, 110426000002, 110426000002],
//             * SEQ_NO=[1, 2, 3, 4]}
//             */
//            TParm tmpParm = calculateCostAwt(costAwtList, forIndParm);
//            parmCostAmt.addParm(tmpParm);
//            parmCostAmt.removeRow(0);
//
//
//            /*
//             * 原,没有变
//             */
//
//            for(int k = 0; k < parmCostAmt.getCount("CASE_NO"); k++){
//                result = PHAOrderTool.getInstance().updateCostAmt(parmCostAmt.getRow(k), connection);
//                if (result.getErrCode() < 0) {
//                    System.out.println(result.getErrText());
//                    connection.close();
//                    return result;
//                }
//            }
//        }
//        connection.commit();
//        connection.close();
//        return result;
            
            //扣库明细,这个列表中包含所有的扣库回传值
            TParm costAwtList = new TParm();
            for (int i = 0; i < trimedforIndParm.getCount(); i++) {
                //获得一条药品的参数--传给IND
                parmToIND.setData("ORG_CODE", trimedforIndParm.getData("EXEC_DEPT_CODE", i));
                parmToIND.setData("ORDER_CODE",
                                  trimedforIndParm.getData("ORDER_CODE", i));
                parmToIND.setData("DOSAGE_QTY",
                                  trimedforIndParm.getData("DOSAGE_QTY", i));
                parmToIND.setData("CASE_NO",trimedforIndParm.getData("CASE_NO", i));
                parmToIND.setData("OPT_DATE",optTime);
                parmToIND.setData("OPT_USER",trimedforIndParm.getData("OPT_USER", i));
                parmToIND.setData("OPT_TERM",trimedforIndParm.getData("OPT_TERM", i));
                //IND接口类
                Medicine subInd = new Medicine();
                //当取消退药(finishFlag->Y)
                if (returnFlg && parm.getBoolean("finishFlag") &&
                    !subInd.checkIndStockQty(parmToIND)) {
                    System.out.println(result.getErrText());
                    connection.close();
                    result.setErr( -1, "药品库存不足\n不可扣库！");
                    return result;
                }
                //取得CASE_NO
                String caseNo = parmToIND.getValue("CASE_NO");
                //通过CASE_NO得到病患信息
                TParm caseNoParm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);

                //为假的时候--非退药
                if (!returnFlg) {
                    if (parm.getBoolean("ISUNDISP_FLG")) {
                        result = INDTool.getInstance().unReduceIndStock(
                            parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0), connection);
                        parmCostAmt.addData("CASE_NO",caseNo);
                        parmCostAmt.addData("RX_NO", trimedforIndParm.getData("RX_NO", i));
                        parmCostAmt.addData("SEQ_NO",
                                        trimedforIndParm.getData("SEQ_NO", i));
                        parmCostAmt.addData("COST_AMT", 0);
                } else {
                    //处方上需要一个ORG_CODE,ORDER_CODE,DOSAGE_QTY,OPT_USER,OPT_DATE,OPT_TERM必须需要这六个数据
                    //验证库存
                    if (!subInd.checkIndStockQty(parmToIND)) {
                        System.out.println(result.getErrText());
                        result.setErr( -1, "药品库存不足\n不可扣库！");
                        return result;
                    }
                    //扣库动作
                    result = INDTool.getInstance().reduceIndStock(parmToIND,
                        caseNoParm.getValue("SERVICE_LEVEL", 0),
                        connection);
                    for(int n = 0; n < result.getCount("ORDER_CODE"); n++){
                        costAwtList.addData("CASE_NO",    caseNo);
                        costAwtList.addData("ORDER_CODE", result.getData("ORDER_CODE",   n));
                        costAwtList.addData("ORG_CODE",   result.getData("ORG_CODE",   n));
                        costAwtList.addData("PRICE",      result.getData("PRICE",   n));
                        costAwtList.addData("QTY",        result.getData("QTY",   n));
                        costAwtList.addData("RX_NO",      trimedforIndParm.getData("RX_NO", i));
                        costAwtList.addData("SEQ_NO",     trimedforIndParm.getData("SEQ_NO", i));
                        costAwtList.addData("COST_AMT",   result.getData("COST_AMT"));
                        //luhai add batch_seq 
                        costAwtList.addData("BATCH_SEQ",   result.getData("BATCH_SEQ",   n));
                    }
                }
            }
                //luhai delete 2012-1-31 退药时根据实际的发药详细记录进行扣库，不进行order的group by  处理  begin
//                else {
//                //退药--上库动作
//                result = INDTool.getInstance().regressIndStock(parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0),
//                    connection);
//
//                parmCostAmt.addData("CASE_NO", caseNo);
//                parmCostAmt.addData("RX_NO", trimedforIndParm.getData("RX_NO", i));
//                parmCostAmt.addData("SEQ_NO", trimedforIndParm.getData("SEQ_NO", i));
//                parmCostAmt.addData("COST_AMT", 0);
//            }
              //luhai delete 2012-1-31 退药时根据实际的发药详细记录进行扣库，不进行order的group by  处理 end
            }
            //*******************************************
            //退药操作 begin luhai 2012-1-31 begin
            //*******************************************
            if(returnFlg){
            	//取得CASE_NO
            	String caseNo = parmToIND.getValue("CASE_NO");
            	//通过CASE_NO得到病患信息
            	TParm caseNoParm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
            	result = reduceMed(forIndParm,caseNoParm.getValue("SERVICE_LEVEL",0),parmCostAmt,caseNo,optTime,connection);
            	if(result.getErrCode()<0){
                    System.out.println(result.getErrText());
                    connection.close();
                    result.setErr( -1, "药品扣库异常！");
                    return result;
            	}
            }
            //*******************************************
            //退药操作 end luhai 2012-1-31 end
            //*******************************************
            
            //计算成本金额,result中包含ORG_CODE,ORDER_CODE,QTY,PRICE

            /**
             * tmpParm:{Data={CASE_NO=[110426000005, 110426000005, 110426000005, 110426000005],
             * ORG_NO=[1101030903, 1101030903, 1101030903, 1101030903],
             * RX_NO=[110426000002, 110426000002, 110426000002, 110426000002],
             * SEQ_NO=[1, 2, 3, 4]}
             */
            TParm tmpParm = calculateCostAwt(costAwtList, forIndParm);
            parmCostAmt.addParm(tmpParm);
            parmCostAmt.removeRow(0);


            /*
             * 原,没有变
             */

            for(int k = 0; k < parmCostAmt.getCount("CASE_NO"); k++){
                result = PHAOrderTool.getInstance().updateCostAmt(parmCostAmt.getRow(k), connection);
                if (result.getErrCode() < 0) {
                    System.out.println(result.getErrText());
                    connection.close();
                    return result;
                }
                //luhai add 2012-31 更新实际的发药数据
                //此判断条件为当做配药保存操作且不是配药后保存的情况才做扣库细项的更新
                if(!returnFlg&&(!parm.getBoolean("ISUNDISP_FLG"))){
                	result = PHAOrderTool.getInstance().updateDispenseDetailForPHA(parmCostAmt.getRow(k), connection);
                	if (result.getErrCode() < 0) {
                		System.out.println(result.getErrText());
                		connection.close();
                		return result;
                	}
                }
            }
        }
        connection.commit();
        connection.close();
        return result;
        //*******************************************************************************
        // luhai modify 扣库后还需回写门诊的batchSeq end
        //*******************************************************************************
    }
    /**
     * 
     * 退药方法
     * luhai 2012-1-31
     * @param reduceParm   退药的处方信息
     * @param serviceLevel 服务等级
     * @param case_no
     * @param Timestamp optTime
     * @param TConnection  connection
     * @return
     */
    private TParm reduceMed(TParm forIndParm,String serviceLevel,TParm parmCostAmt,String caseNo,Timestamp optTime,TConnection  connection){
    	TParm returnParm = new TParm();
    	for (int i = 0; i < forIndParm.getCount(); i++) {
    		String rxNo=forIndParm.getValue("RX_NO", i);
    		String seqNo=forIndParm.getValue("SEQ_NO", i);
    		TParm queryParm = new TParm();
    		queryParm.setData("CASE_NO",caseNo);
    		queryParm.setData("RX_NO",rxNo);
    		queryParm.setData("SEQ_NO",seqNo);
    		TParm phaDispenseParm = PHAOrderTool.getInstance().queryDispenseDetailForPHA(queryParm,connection);
    		if(phaDispenseParm.getErrCode()<0){
    			return phaDispenseParm;
    		}
    		String orgCode=phaDispenseParm.getValue("EXEC_DEPT_CODE",0);
    		String orderCode=phaDispenseParm.getValue("ORDER_CODE",0);
    		String optUser=forIndParm.getValue("OPT_USER",i);
    		String optTerm=forIndParm.getValue("OPT_TERM",i);
    		for(int j=1;j<=3;j++){
    			int batchSeq=phaDispenseParm.getInt("BATCH_SEQ"+j,0);
    			// modify by wangb 2015/09/18 退药量由仅支持整数修改为真正扣库数量
    			double dosageQty=phaDispenseParm.getDouble("DISPENSE_QTY"+j,0);
    			if(dosageQty<=0){
    				continue;
    			}
    			returnParm=INDTool.getInstance().regressIndStockWithBatchSeq(orgCode, orderCode, batchSeq, dosageQty, optUser, optTime, optTerm, serviceLevel, connection);
    			if(returnParm.getErrCode()<0){
    				return returnParm;
    			}
    		}
//    		result = INDTool.getInstance().regressIndStock(parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0),
//    				connection);
    		parmCostAmt.addData("CASE_NO", caseNo);
    		parmCostAmt.addData("RX_NO", forIndParm.getData("RX_NO", i));
    		parmCostAmt.addData("SEQ_NO", forIndParm.getData("SEQ_NO", i));
    		parmCostAmt.addData("COST_AMT", 0);
    	}
    	return returnParm;
    }

    /**
     * 计算成本金额,成本金额中costAwtList,包含所有的回传的ORG_CODE,ORDER_CODE,QTY,PRICE
     * 这个回传的结果集中包含了所有的扣库约束,如: QTY = 10, 回传可能是8和2.
     * 这样的话扣库并不是从一个库中直接扣除.而是从多个库扣除了一个总和
     * 在扣库时,我们把一张处方签上所有的相同的处方的量合并成一个.扣库需要这个总和,并且是用这个总和扣除
     * 但是在回写成本金额时,需要每一个处方的单个值.
     * 该方法就是把扣库时回写的数据分配到每一条处方(这个处方可能包含相同的数据)
     * result中包含ORG_CODE,ORDER_CODE,QTY,PRICE
     * @param costAwtList TParm
     * @param forIndParm TParm
     * @return TParm
     */
    private TParm calculateCostAwt(TParm costAwtList, TParm forIndParm) {
        //构造一个新的TParm,用它记录每一条处方的成本金额
        TParm costAwts = new TParm();
        if (costAwtList == null || costAwtList.getCount("ORDER_CODE") == 0) {
            return costAwts;
        }
        // 该笔药的order_code
        String order_code = "";
        // 药房代码
        String org_code = "";
        // 该笔药的总量(没有合并的单笔数据)
        double singleCount = 0;
        // 成本金额
        double price = 0;
        //遍历每一个forIndParm,因为需要回写每一张处方签上的处方
        for (int i = 0; i < forIndParm.getCount("ORDER_CODE"); i++) {
            // 该笔药的order_code
            order_code = forIndParm.getValue("ORDER_CODE", i);
            // 药房代码
            org_code = forIndParm.getValue("EXEC_DEPT_CODE", i);

            costAwts.addData("CASE_NO", forIndParm.getValue("CASE_NO", i));
            costAwts.addData("RX_NO", forIndParm.getData("RX_NO", i));
            costAwts.addData("SEQ_NO", forIndParm.getData("SEQ_NO", i));
            // 单笔药品的总量
            singleCount = forIndParm.getDouble("DOSAGE_QTY", i);
            //成本金额
            price = 0;
           //luhai 2012-1-26 add begin
            String batch_seq1="null";
            String batch_seq2="null";
            String batch_seq3="null";
            String dispense_qty1="0";
            String dispense_qty2="0";
            String dispense_qty3="0";
            String verifyin_price1="0";
            String verifyin_price2="0";
            String verifyin_price3="0";
           //用于记录发药字段记录到哪一个batch_seq1，batch_seq2，batch_seq3
           //若超出预留的字段，不计算
           int index=1;
           //luhai 2012-1-26  end
            //同笔药的单价列表
            for (int j = 0; j < costAwtList.getCount("ORDER_CODE"); j++) {
                //当一个处方签上存在相同的org_code和order_code,则表示是相同的处方
                if (costAwtList.getValue("ORDER_CODE", j).equals(order_code)
                    && costAwtList.getValue("ORG_CODE", j).equals(org_code)) {
                	String tmpDispenseQty="0";
                	String tmpBatchSeq="null";
                	String tmpVerifyinPrice="0";
                	boolean isContinue=false;
                    //取得处方签的单个处方扣库量
                    double tmpQty = Double.parseDouble(costAwtList.getValue("QTY", j));
                    //当单笔药的用量大于一个回传量,则表示扣库时该笔药从两个或多个库中扣除
                    if (singleCount > tmpQty) {
                        //扣库的第一笔tmpQty
                        price += tmpQty *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        //设置costAwtList.setData("QTY", j, 0);
                        costAwtList.setData("QTY", j, 0);
                        //在处方用量减去回传量,知道处方量singleCount = 0.
                        singleCount -= tmpQty;
                        //更新实际的发药字段值
                        tmpDispenseQty=tmpQty+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
//                        continue;
                        isContinue=true;
                        //当singleCount == tmpQty表示该处方在一个库存中扣除.
                    }  else if (singleCount == tmpQty) {
                        price += singleCount *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        costAwtList.setData("QTY", j, 0);
                        singleCount = 0;
                        //更新实际的发药字段值
                        tmpDispenseQty=tmpQty+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
                        //当处方用量小于回传量,则表示该笔药品全部在一个库存中扣除
                    } else {
                        price += singleCount *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        costAwtList.setData("QTY", j, costAwtList.getDouble("QTY", j) - singleCount);
                        //更新实际的发药字段值
                        tmpDispenseQty=singleCount+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
                        singleCount = 0;
                    }
                    //处理实际的发药batchSeq luhai begin
                    if(index==1){
                    	batch_seq1=tmpBatchSeq;
                    	dispense_qty1=tmpDispenseQty;
                    	verifyin_price1=tmpVerifyinPrice;
                    }else if(index==2){
                    	batch_seq2=tmpBatchSeq;
                    	dispense_qty2=tmpDispenseQty;
                    	verifyin_price2=tmpVerifyinPrice;
                    }else if(index==3){
                    	batch_seq3=tmpBatchSeq;
                    	dispense_qty3=tmpDispenseQty;
                    	verifyin_price3=tmpVerifyinPrice;
                    }
                    index++;
                    if(isContinue){
                    	continue;
                    }else{
                    	break;
                    }
                    //处理实际的发药batchSeq luhai end
                }
            }
            //当一次循环,立即设置成本金额
            costAwts.addData("COST_AMT", price);
            //luhai add begin
            costAwts.addData("BATCH_SEQ1", "null".equals(batch_seq1)?-1:batch_seq1);
            costAwts.addData("VERIFYIN_PRICE1", verifyin_price1);
            costAwts.addData("DISPENSE_QTY1", dispense_qty1);
            costAwts.addData("BATCH_SEQ2", "null".equals(batch_seq2)?-1:batch_seq2);
            costAwts.addData("VERIFYIN_PRICE2", verifyin_price2);
            costAwts.addData("DISPENSE_QTY2", dispense_qty2);
            costAwts.addData("BATCH_SEQ3", "null".equals(batch_seq3)?-1:batch_seq3);
            costAwts.addData("VERIFYIN_PRICE3", verifyin_price3);
            costAwts.addData("DISPENSE_QTY3", dispense_qty3);
//            costAwts.addData("BATCH_SEQ1", "null".equals(batch_seq1)?new TNull(int.class):batch_seq1);
//            costAwts.addData("VERIFYIN_PRICE1", verifyin_price1);
//            costAwts.addData("DISPENSE_QTY1", dispense_qty1);
//            costAwts.addData("BATCH_SEQ2", "null".equals(batch_seq2)?new TNull(int.class):batch_seq2);
//            costAwts.addData("VERIFYIN_PRICE2", verifyin_price2);
//            costAwts.addData("DISPENSE_QTY2", dispense_qty2);
//            costAwts.addData("BATCH_SEQ3", "null".equals(batch_seq3)?new TNull(int.class):batch_seq3);
//            costAwts.addData("VERIFYIN_PRICE3", verifyin_price3);
//            costAwts.addData("DISPENSE_QTY3", dispense_qty3);
            //luhai add end
            
        }
//        System.out.println("=================>> costAwts" + costAwts);
        return costAwts;
    }

    /**
     * 用一个列表保存已经处理过的药品 <br>
     * 在同一张处方签上处理过一个药品时,把该药品的org_code和order_code保存起来<br>
     * 在检查下一个药品时,首先判断是否已经处理过该药品<br>
     * 如果某个药品的org_code和order_code相同时,则表示两种药品属于同一种.<br>
     * 存在相同的药品时,需要把相同药品的DOSAGE_QTY加和再同数据库库存中对比,判断是否库存不足<br>
     * 并在新的TParm中减去相同的药品
     *
     * @param parm
     * @return 返回一个经过整理的TParm,这个新的TParm长度一定不会比parm的count大
     */
    private TParm cleanUp(TParm parm) {
        if (parm == null) {
            return parm;
        }
        if (parm.getCount() <= 1) {
            return parm;
        }
        //一个新的TParm
        TParm newParm = new TParm();
        int count = parm.getCount();
        String order_code = "";
        String org_code = "";
        newParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        newParm.addData("ORDER_CODE", parm.getData("ORDER_CODE", 0));
        // add by wangb 2015/06/18 因原有线上使用版本与SVN版本不一致造成版本合并后调配提示库存不足时显示的药品名称为null
        newParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", 0));
        newParm.addData("EXEC_DEPT_CODE", parm.getData("EXEC_DEPT_CODE", 0));
        newParm.addData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", 0));
        newParm.addData("OPT_USER", parm.getData("OPT_USER", 0));
        newParm.addData("OPT_TERM", parm.getData("OPT_TERM", 0));
        newParm.addData("SEQ_NO", parm.getData("SEQ_NO", 0));
        newParm.addData("RX_NO", parm.getData("RX_NO", 0));
        for (int i = 1; i < count; i++) {
            //定义一个标志位
            boolean flg = true;
            order_code = parm.getValue("ORDER_CODE", i);
            org_code = parm.getValue("EXEC_DEPT_CODE", i);
            for (int j = 0; j < newParm.getCount("ORDER_CODE"); j++) {
                // 如果已经存在同一种药品
                if (order_code.equals(newParm.getValue("ORDER_CODE", j)) &&
                    org_code.equals(newParm.getValue("EXEC_DEPT_CODE", j))) {
                    // 原来的值
                    double counted = Double.parseDouble(newParm.getValue(
                        "DOSAGE_QTY", j));
                    // 当前列的新值
                    double newcount = Double.parseDouble(parm.getValue(
                        "DOSAGE_QTY", i));
                    newParm.setData("DOSAGE_QTY", j, counted + newcount);
                    newParm.setData("SEQ_NO", j, parm.getData("SEQ_NO", 0));
                    newParm.setData("RX_NO", j, parm.getData("RX_NO", 0));
                    flg = false;
                    break;
                }
            }
            //如果内层的循环走完,flg不是true,说明没有相同的药品
            if (flg) {
                newParm.addData("CASE_NO", parm.getData("CASE_NO", i));
                newParm.addData("ORDER_CODE", parm.getData("ORDER_CODE", i));
                // add by wangb 2015/06/18 因原有线上使用版本与SVN版本不一致造成版本合并后调配提示库存不足时显示的药品名称为null
                newParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", i));
                newParm.addData("EXEC_DEPT_CODE", parm.getData("EXEC_DEPT_CODE", i));
                newParm.addData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", i));
                newParm.addData("OPT_USER", parm.getData("OPT_USER", i));
                newParm.addData("OPT_TERM", parm.getData("OPT_TERM", i));
                newParm.addData("SEQ_NO", parm.getData("SEQ_NO", i));
                newParm.addData("RX_NO", parm.getData("RX_NO", i));
            }
        }
        newParm.setCount(newParm.getCount("ORDER_CODE"));
        return newParm;
    }
}
