package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import java.util.Vector;
import jdo.opd.TotQtyTool;

import java.util.List;
import jdo.inw.InwForOdiTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.data.TNull;
import java.util.Map;
import jdo.adm.ADMInpTool;

/**
 * <p>Title: 护士站维护档主Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwStationMaintainTool
    extends TJDOTool {

    /**
     * 实例
     */
    private static InwStationMaintainTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static InwStationMaintainTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InwStationMaintainTool();
        return instanceObject;
    }

    public InwStationMaintainTool() {
    }

    /**
     * 展开长期处置主方法
     * @param parm TParm
     * @return TParm CASE_NO OPT_USER OPT_DATE OPT_TERM
     */
    public TParm unfold(TParm parm, TConnection connection) {
//    	System.out.println("=========come in unfold=========");
        TParm result = new TParm();
        TParm inParm = new TParm();
        

        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String nowString = StringTool.getString(now, "yyyyMMdd") + "000000";

        //前台传的数据
        Vector VcaseNo = (Vector) parm.getData("CASE_NO");
        String optUser = (String) parm.getData("OPT_USER");
        Timestamp optDate = (Timestamp) parm.getData("OPT_DATE");
        String optTerm = (String) parm.getData("OPT_TERM");
        int countPat = VcaseNo.size();
        //支持多人执行
        //循环1--病人数：循环需要展开长期处置的病人
        for (int i = 0; i < countPat; i++) {
            //根据前台的CASE_NO找出每一个病患应该展开的长期处置医嘱
            TParm orderTRT = getOrderTRT(VcaseNo.get(i) + "",nowString);
            //循环每个病患的每一条长期处置数据，调用UDD接口生成需要展开的数据插入ODI_DSPND ODI_DSPNM
            int countTrt = orderTRT.getCount();
            //该病人没有长期处置
            if (countTrt <= 0)
                continue;
            TParm parmCase = new TParm();
            parmCase.setData("CASE_NO",VcaseNo.get(i) + "");
            parmCase = ADMInpTool.getInstance().selectall(parmCase);
            String serviceLevel = parmCase.getValue("SERVICE_LEVEL",0);
            //$$==== add by lx 2012/06/02  start ====$$//
            //String ctzCode = parmCase.getValue("CTZ1_CODE",0);
            //inParm.setData("CTZ_CODE", ctzCode);
            //$$==== add by lx 2012/06/02  end ====$$//
            
            //循环2--处置：(与M表1:1)数循环调用每一条医嘱的UDD接口，返回应该展开的数据
            for (int row_M = 0; row_M < countTrt; row_M++) {
                TParm orderToUddParm = new TParm();
                orderToUddParm = orderTRT.getRow(row_M);
                //返回需要插入的数据
                List dataFromUdd = TotQtyTool.getInstance().getOdiTrtUdQty(
                    orderToUddParm,nowString.substring(0,12),serviceLevel);
                if (dataFromUdd == null) {
                    result.setErrCode(-1);
                    err("ERR:" + orderToUddParm);
                    return result;
                }
                //应该插入ODI_DSPND的行数
                List orderDateTimeFromUdd = (List) dataFromUdd.get(0);
                int insertDCount = orderDateTimeFromUdd.size();
                if(insertDCount == 0)
                    continue;
                String startDttm=((List)dataFromUdd.get(2)).get(0)+"";
                String endDttm=((List)dataFromUdd.get(2)).get(1)+"";
                Timestamp lastDspnDate=(Timestamp)((Map)dataFromUdd.get(1)).get("ORDER_LAST_DSPN_DATE");
                //order表的LASTDSPN_QTY ORDER_LASTDSPN_QTY
                //order表的ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
                //order表的ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
                //M/ORDER表的dispenseQty M_DISPENSE_QTY
                //M/ORDER表的dispenseUnit M_DISPENSE_UNIT
                //M/ORDER表的dosageQty  M_DOSAGE_QTY
                //M/ORDER表的dosageUnit M_DOSAGE_UNIT
                //D表的MediQty D_MEDI_QTY
                //D表的MediUnit D_MEDI_UNIT
                //D表的dosageQty D_DOSAGE_QTY
                //D表的dosageUnit D_DOSAGE_UNIT
                //得到返回的UDD返回的值
                Map otherData = (Map) dataFromUdd.get(1);
                Double OlastDspnQty = (Double) otherData.get(
                    "ORDER_LASTDSPN_QTY");
                Double OacumDspnQty = (Double) otherData.get(
                    "ORDER_ACUMDSPN_QTY");
                Double OacumMediQty = (Double) otherData.get(
                    "ORDER_ACUMMEDI_QTY");
                Double MdispenseQty = (Double) otherData.get(
                    "M_DISPENSE_QTY");
                String MdispenseUnit = (String) otherData.get(
                    "M_DISPENSE_UNIT");
                Double MdosageQty = (Double) otherData.get(
                    "M_DOSAGE_QTY");
                String MdosageUnit = (String) otherData.get(
                    "M_DOSAGE_UNIT");
                Double DmediQty = (Double) otherData.get("D_MEDI_QTY");
                String DmediUnit = (String) otherData.get("D_MEDI_UNIT");
                Double DdosageQty = (Double) otherData.get(
                    "D_DOSAGE_QTY");
                String DdosageUnit = (String) otherData.get(
                    "D_DOSAGE_UNIT");
              //$$===========add by lx 2012/06/02 加入 start================$$//
                Double ownPrice=(Double) otherData.get(
                "OWN_PRICE");
                Double nhiPrice=(Double) otherData.get(
                "NHI_PRICE");
                Double discountRate=(Double) otherData.get(
                "DISCOUNT_RATE");
                Double ownAMT=(Double) otherData.get(
                "OWN_AMT");
                Double totAMT=(Double) otherData.get(
                "TOT_AMTM");            
              //$$===========add by lx 2012/06/02 加入 end================$$//
                String Mdcflg="";
                //循环3--处置细项：(插入D表的每一条数据)
                for (int row_D = 0; row_D < insertDCount; row_D++) {

                    //最后需要插入细表的数据
                    TParm finalDataToD = new TParm();
                    String orderDateTime = (String) orderDateTimeFromUdd.get(
                        row_D);
                	if(!orderToUddParm.getValue("DC_DATE").equals("")&&StringTool
							.getTimestamp(orderDateTime,"yyyyMMddHHmm").compareTo(orderToUddParm.getTimestamp("DC_DATE")) > 0)
						Mdcflg = "Y";
                    finalDataToD = getDataDspnd(dataFromUdd, orderToUddParm,
                                                orderDateTime,
                                                row_D, DmediQty, DmediUnit,
                                                DdosageQty, DdosageUnit,
                                                optUser, optDate,
                                                optTerm);
                    //插入ODI_DSPND
                    result = InwForOdiTool.getInstance().insertOdiDspnd(
                        finalDataToD,
                        connection);
                    if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                            + result.getErrName());
                        return result;
                    }
                }

                //插入ODI_DSPNM
                TParm finalDataToM = new TParm();
                //System.out.println("=======orderToUddParm========"+orderToUddParm);
                //$$===========add by lx 2012/06/02 加入处置 start================$$//
                orderToUddParm.setData("OWN_PRICE", ownPrice);
                orderToUddParm.setData("NHI_PRICE", nhiPrice);
                orderToUddParm.setData("DISCOUNT_RATE", discountRate);
                orderToUddParm.setData("OWN_AMT", ownAMT);
                orderToUddParm.setData("TOT_AMT", totAMT);            
                //$$===========add by lx 2012/06/02 加入处置 end================$$//
                finalDataToM = getDataDspnm(dataFromUdd, orderToUddParm, row_M,
                                            startDttm,endDttm, MdispenseQty,
                                            MdispenseUnit, MdosageQty,
                                            MdosageUnit,
                                            optUser, optDate,
                                            optTerm,Mdcflg);
                result = InwForOdiTool.getInstance().insertOdiDspnm(
                    finalDataToM,
                    connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }

                //更新ODI_ORDER
                TParm finalDataToOrder = new TParm();
                String caseNo = (String) orderToUddParm.getData("CASE_NO");
                String orderNo = (String) orderToUddParm.getData("ORDER_NO");
                int orderSeq = Integer.parseInt(orderToUddParm.getData(
                    "ORDER_SEQ") + "");
                //更新ODI_ORDER的字段
                finalDataToOrder = getDataOrder(orderToUddParm, caseNo, orderNo,
                                                orderSeq,lastDspnDate,//操作时间=>lastDspnDate
                                                OlastDspnQty, OacumDspnQty,
                                                OacumMediQty,
                                                MdispenseQty,
                                                MdispenseUnit,
                                                MdosageQty,
                                                MdosageUnit,
                                                optUser, optDate,
                                                optTerm);
                result = InwForOdiTool.getInstance().updateOdiOrderU(
                    finalDataToOrder,
                    connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }

            }

        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }



    /**
     * 整理需要更新ODI_ORDER的数据
     * @param fromUddData List 从UDD返回的需要的值
     * @param orderParm TParm 主表的基础数据
     * @return TParm
     */
    private TParm getDataOrder(TParm orderParm, String caseNo, String orderNo,
                               int orderSeq, Timestamp lastDspnDate,
                               double lastDspnQty, double acumDspnQty,
                               double acumMediQty, double dispenseQty,
                               String dispenseUnit, double dosageQty,
                               String dosageUnit,
                               String optUser,
                               Timestamp optDate, String optTerm) {
        TParm result = new TParm();
        result.setData("CASE_NO", caseNo);
        result.setData("ORDER_NO", orderNo);
        result.setData("ORDER_SEQ", orderSeq);
        result.setData("LASTDSPN_QTY",
                       orderParm.getData("LASTDSPN_QTY", 0) == null ?
                    		   0.00 :
                       lastDspnQty);
        result.setData("LAST_DSPN_DATE", lastDspnDate);
        result.setData("ACUMDSPN_QTY",
                       orderParm.getData("ACUMDSPN_QTY", 0) == null ?
                    		   0.00 :
                       acumDspnQty);
        result.setData("ACUMMEDI_QTY",
                       orderParm.getData("ACUMMEDI_QTY", 0) == null ?
                    		   0.00 :
                       acumMediQty);

        result.setData("DOSAGE_QTY", orderParm.getValue("DOSAGE_QTY", 0)== null ?
        		0.00:
                       dosageQty);
        result.setData("DOSAGE_UNIT", orderParm.getValue("DOSAGE_UNIT", 0)== null ?
        		0.00 :
                       dosageUnit);
        result.setData("DISPENSE_QTY", orderParm.getValue("DISPENSE_QTY", 0)== null ?
        		0.00:
                       dispenseQty);
        result.setData("DISPENSE_UNIT", orderParm.getValue("DISPENSE_UNIT", 0)== null ?
        		0.00 :
                       dispenseUnit);
        result.setData("NS_CHECK_CODE", optUser);
        result.setData("NS_CHECK_DATE", optDate);
        result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
        result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
        result.setData("OPT_DATE", optDate);
        result.setData("OPT_USER", optUser);
        result.setData("OPT_TERM", optTerm);

        //当没有DC_DATE的时候不更新DC_NS_CHECK_CODE DC_NS_CHECK_DATE
        Timestamp daDate = (Timestamp) orderParm.getData("DC_DATE");
        if (daDate == null) {
            result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
            result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
        }
        else {
            result.setData("DC_NS_CHECK_CODE", optUser);
            result.setData("DC_NS_CHECK_DATE", optDate);
        }
        return result;
    }


    /**
     * 整理需要插入ODI_DSPNM的数据
     * @param fromUddData List 从UDD返回的需要的值
     * @param orderParm TParm 主表的基础数据
     * @return TParm
     */
    private TParm getDataDspnm(List fromUddData, TParm orderParm, int row,
                               String startDttm, String endDttm,double dispenseQty,
                               String dispenseUnit, double dosageQty,
                               String dosageUnit, String optUser,
                               Timestamp optDate, String optTerm,String Mdcflg) {
        TParm result = new TParm();

        result.setData("CASE_NO", orderParm.getData("CASE_NO"));
        result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
        result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
        //起始时间START_DTTM就是现在操作的时间
        result.setData("START_DTTM", startDttm);
        //结束时间END_DTTM就是当天的2359
        result.setData("END_DTTM", endDttm);

        result.setData("REGION_CODE", orderParm.getData("REGION_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("REGION_CODE"));
        result.setData("STATION_CODE", orderParm.getData("STATION_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("STATION_CODE"));
        result.setData("DEPT_CODE", orderParm.getData("DEPT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DEPT_CODE"));
        result.setData("VS_DR_CODE", orderParm.getData("VS_DR_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("VS_DR_CODE"));
        result.setData("BED_NO", orderParm.getData("BED_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("BED_NO"));

        result.setData("IPD_NO", orderParm.getData("IPD_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("IPD_NO"));
        result.setData("MR_NO", orderParm.getData("MR_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("MR_NO"));
        result.setData("DSPN_KIND", orderParm.getData("RX_KIND") == null ?
                       new TNull(String.class) :
                       orderParm.getData("RX_KIND"));
        result.setData("DSPN_DATE", orderParm.getData("DSPN_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("DSPN_DATE"));
        result.setData("DSPN_USER", orderParm.getData("DSPN_USER") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DSPN_USER"));

        result.setData("DISPENSE_EFF_DATE",
                       orderParm.getData("DISPENSE_EFF_DATE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DISPENSE_EFF_DATE"));
        result.setData("RX_NO", orderParm.getData("RX_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("RX_NO"));
        result.setData("ORDER_CAT1_CODE",
                       orderParm.getData("ORDER_CAT1_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_CAT1_CODE"));
        result.setData("CAT1_TYPE",
                       orderParm.getData("CAT1_TYPE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("CAT1_TYPE"));
        result.setData("DISPENSE_END_DATE",
                       orderParm.getData("DISPENSE_END_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("DISPENSE_END_DATE"));

        result.setData("EXEC_DEPT_CODE",
                       orderParm.getData("EXEC_DEPT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("EXEC_DEPT_CODE"));
        //==== add by lx 2012-06-01 执行科室转成成本中心====$$//EXEC_DEPT_CODE为成本中心
//        result.setData("EXEC_DEPT_CODE", this.getCostCenter((String)result.getData("EXEC_DEPT_CODE")));
        
        //System.out.println("== exec dept code getDataDspnm=="+this.getCostCenter((String)result.getData("EXEC_DEPT_CODE")));
        //==== add by lx 2012-06-01 ====$$//
        result.setData("DISPENSE_FLG",
                       orderParm.getData("DISPENSE_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DISPENSE_FLG"));
        result.setData("AGENCY_ORG_CODE",
                       orderParm.getData("AGENCY_ORG_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("AGENCY_ORG_CODE"));
        result.setData("PRESCRIPT_NO",
                       orderParm.getData("PRESCRIPT_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PRESCRIPT_NO"));
        result.setData("LINKMAIN_FLG", orderParm.getData("LINKMAIN_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("LINKMAIN_FLG"));

        result.setData("LINK_NO", orderParm.getData("LINK_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("LINK_NO"));
        result.setData("ORDER_CODE", orderParm.getData("ORDER_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_CODE"));
        result.setData("ORDER_DESC", orderParm.getData("ORDER_DESC") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_DESC"));
        result.setData("GOODS_DESC", orderParm.getData("GOODS_DESC") == null ?
                       new TNull(String.class) :
                       orderParm.getData("GOODS_DESC"));
        result.setData("SPECIFICATION", orderParm.getData("SPECIFICATION") == null ?
                       new TNull(String.class) :
                       orderParm.getData("SPECIFICATION"));

        result.setData("MEDI_QTY", orderParm.getData("MEDI_QTY") == null ?
                       0.00 :
                       orderParm.getData("MEDI_QTY"));
        result.setData("MEDI_UNIT", orderParm.getData("MEDI_UNIT") == null ?
                       new TNull(String.class) :
                       orderParm.getData("MEDI_UNIT"));
        result.setData("FREQ_CODE", orderParm.getData("FREQ_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("FREQ_CODE"));
        result.setData("ROUTE_CODE", orderParm.getData("ROUTE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ROUTE_CODE"));
        result.setData("TAKE_DAYS", orderParm.getData("TAKE_DAYS") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("TAKE_DAYS"));

        result.setData("DOSAGE_QTY", orderParm.getData("DOSAGE_QTY") == null ?
                       0.00 :
                       dosageQty);
        result.setData("DOSAGE_UNIT", orderParm.getData("DOSAGE_UNIT") == null ?
                       new TNull(String.class) :
                       dosageUnit);
        result.setData("DISPENSE_QTY", orderParm.getData("DISPENSE_QTY") == null ?
                       0.00 :
                       dispenseQty);
        result.setData("DISPENSE_UNIT", orderParm.getData("DISPENSE_UNIT") == null ?
                       new TNull(String.class) :
                       dispenseUnit);
        result.setData("GIVEBOX_FLG", orderParm.getData("GIVEBOX_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("GIVEBOX_FLG"));

        result.setData("OWN_PRICE", orderParm.getData("OWN_PRICE") == null ?
                       0.00 :
                       orderParm.getData("OWN_PRICE"));
        result.setData("NHI_PRICE", orderParm.getData("NHI_PRICE") == null ?
                       0.00 :
                       orderParm.getData("NHI_PRICE"));
        result.setData("DISCOUNT_RATE", orderParm.getData("DISCOUNT_RATE") == null ?
                       0.00 :
                       orderParm.getData("DISCOUNT_RATE"));
        result.setData("OWN_AMT", orderParm.getData("OWN_AMT") == null ?
                       0.00 :
                       orderParm.getData("OWN_AMT")); //费用返回
        result.setData("TOT_AMT", orderParm.getData("TOT_AMT") == null ?
                       0.00 :
                       orderParm.getData("TOT_AMT"));
//        result.setData("ORDER_DATE", orderParm.getData("ORDER_DATE") == null ?
//                       new TNull(Timestamp.class) :
//                       orderParm.getData("ORDER_DATE"));
        result.setData("ORDER_DATE", orderParm.getData("EFF_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("EFF_DATE"));
        result.setData("ORDER_DEPT_CODE",
                       orderParm.getData("ORDER_DEPT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_DEPT_CODE"));
        result.setData("ORDER_DR_CODE", orderParm.getData("ORDER_DR_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_DR_CODE"));
        result.setData("DR_NOTE", orderParm.getData("DR_NOTE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DR_NOTE"));
        result.setData("ATC_FLG", orderParm.getData("ATC_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ATC_FLG"));

        result.setData("SENDATC_FLG", orderParm.getData("SENDATC_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("SENDATC_FLG"));
        result.setData("SENDATC_DTTM", orderParm.getData("SENDATC_DTTM") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("SENDATC_DTTM"));
        result.setData("INJPRAC_GROUP", orderParm.getData("INJPRAC_GROUP") == null ?
                       new TNull(String.class) :
                       orderParm.getData("INJPRAC_GROUP"));
        if (Mdcflg.equals("Y")) {
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
			result.setData("DC_DR_CODE",
					orderParm.getData("DC_DR_CODE") == null ? new TNull(
							String.class) : orderParm.getData("DC_DR_CODE"));
		}else{
			result.setData("DC_DATE", new TNull(Timestamp.class));
			result.setData("DC_DR_CODE",new TNull(String.class));
		}
        result.setData("DC_TOT", orderParm.getData("DC_TOT") == null ?
                       0.00 :
                       orderParm.getData("DC_TOT"));

        result.setData("RTN_NO", orderParm.getData("RTN_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("RTN_NO"));
        result.setData("RTN_NO_SEQ", orderParm.getData("RTN_NO_SEQ") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("RTN_NO_SEQ"));
        result.setData("RTN_DOSAGE_QTY", orderParm.getData("RTN_DOSAGE_QTY") == null ?
                       0.00 :
                       orderParm.getData("RTN_DOSAGE_QTY"));
        result.setData("RTN_DOSAGE_UNIT", orderParm.getData("RTN_DOSAGE_UNIT") == null ?
                       new TNull(String.class) :
                       orderParm.getData("RTN_DOSAGE_UNIT"));
        result.setData("CANCEL_DOSAGE_QTY",
                       orderParm.getData("CANCEL_DOSAGE_QTY") == null ?
                       0.00 :
                       orderParm.getData("CANCEL_DOSAGE_QTY"));

        result.setData("CANCELRSN_CODE", orderParm.getData("CANCELRSN_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("CANCELRSN_CODE"));
        result.setData("TRANSMIT_RSN_CODE",
                       orderParm.getData("TRANSMIT_RSN_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("TRANSMIT_RSN_CODE"));
        result.setData("PHA_RETN_CODE", orderParm.getData("PHA_RETN_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_RETN_CODE"));
        result.setData("PHA_RETN_DATE", orderParm.getData("PHA_RETN_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_RETN_DATE"));
        result.setData("PHA_CHECK_CODE", orderParm.getData("PHA_CHECK_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_CHECK_CODE"));

        result.setData("PHA_CHECK_DATE", orderParm.getData("PHA_CHECK_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_CHECK_DATE"));
        result.setData("PHA_DISPENSE_NO",
                       orderParm.getData("PHA_DISPENSE_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_DISPENSE_NO"));
        result.setData("PHA_DOSAGE_CODE",
                       orderParm.getData("PHA_DOSAGE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_DOSAGE_CODE"));
        result.setData("PHA_DOSAGE_DATE", orderParm.getData("PHA_DOSAGE_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_DOSAGE_DATE"));
        result.setData("PHA_DISPENSE_CODE",
                       orderParm.getData("PHA_DISPENSE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_DISPENSE_CODE"));

        result.setData("PHA_DISPENSE_DATE",
                       orderParm.getData("PHA_DISPENSE_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_DISPENSE_DATE"));
        result.setData("NS_EXEC_CODE", orderParm.getData("NS_EXEC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_EXEC_CODE"));
        result.setData("NS_EXEC_DATE", orderParm.getData("NS_EXEC_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("NS_EXEC_DATE"));
        result.setData("NS_EXEC_DC_CODE",
                       orderParm.getData("NS_EXEC_DC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_EXEC_DC_CODE"));
        result.setData("NS_EXEC_DC_DATE",
                       orderParm.getData("NS_EXEC_DC_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("NS_EXEC_DC_DATE"));

        result.setData("NS_USER", orderParm.getData("NS_USER") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_USER"));
        result.setData("CTRLDRUGCLASS_CODE",
                       orderParm.getData("CTRLDRUGCLASS_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("CTRLDRUGCLASS_CODE"));
        result.setData("PHA_TYPE", orderParm.getData("PHA_TYPE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_TYPE"));
        result.setData("DOSE_TYPE", orderParm.getData("DOSE_TYPE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DOSE_TYPE"));
        result.setData("DCTAGENT_CODE", orderParm.getData("DCTAGENT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DCTAGENT_CODE"));

        result.setData("DCTEXCEP_CODE", orderParm.getData("DCTEXCEP_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DCTEXCEP_CODE"));
        result.setData("DCT_TAKE_QTY", orderParm.getData("DCT_TAKE_QTY") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("DCT_TAKE_QTY"));
        result.setData("PACKAGE_AMT", orderParm.getData("PACKAGE_AMT") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("PACKAGE_AMT"));
        result.setData("DCTAGENT_FLG", orderParm.getData("DCTAGENT_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DCTAGENT_FLG"));
        result.setData("PRESRT_NO", orderParm.getData("PRESRT_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PRESRT_NO"));

        result.setData("DECOCT_CODE", orderParm.getData("DECOCT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DECOCT_CODE"));
        result.setData("URGENT_FLG", orderParm.getData("URGENT_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("URGENT_FLG"));
        result.setData("SETMAIN_FLG", orderParm.getData("SETMAIN_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("SETMAIN_FLG"));
        result.setData("ORDERSET_GROUP_NO",
                       orderParm.getData("ORDERSET_GROUP_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDERSET_GROUP_NO"));
        result.setData("ORDERSET_CODE", orderParm.getData("ORDERSET_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDERSET_CODE"));

        result.setData("RPTTYPE_CODE", orderParm.getData("RPTTYPE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("RPTTYPE_CODE"));
        result.setData("OPTITEM_CODE", orderParm.getData("OPTITEM_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("OPTITEM_CODE"));
        result.setData("HIDE_FLG", orderParm.getData("HIDE_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("HIDE_FLG"));
        result.setData("DEGREE_CODE", orderParm.getData("DEGREE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("DEGREE_CODE"));
        result.setData("BILL_FLG", orderParm.getData("BILL_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("BILL_FLG"));

        result.setData("CASHIER_USER", orderParm.getData("CASHIER_USER") == null ?
                       new TNull(String.class) :
                       orderParm.getData("CASHIER_USER"));
        result.setData("CASHIER_DATE", orderParm.getData("CASHIER_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("CASHIER_DATE"));
        result.setData("IBS_CASE_NO_SEQ",
                       orderParm.getData("IBS_CASE_NO_SEQ") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("IBS_CASE_NO_SEQ"));
        result.setData("IBS_SEQ_NO", orderParm.getData("IBS_SEQ_NO") == null ?
                       new TNull(Integer.class) :
                       orderParm.getData("IBS_SEQ_NO"));
        result.setData("ANTIBIOTIC_CODE", orderParm.getData("ANTIBIOTIC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ANTIBIOTIC_CODE"));
        result.setData("ANTIBIOTIC_WAY", new TNull(String.class));
        
        result.setData("TAKEMED_ORG", new TNull(String.class)); //20130227 shibl add
        
        result.setData("TAKEMED_NO", new TNull(String.class)); //20130227 shibl add
        
        result.setData("PUMP_CODE", new TNull(String.class));
        
        result.setData("INFLUTION_RATE", new TNull(String.class));
        
        result.setData("OPT_USER", optUser);
        result.setData("OPT_DATE", optDate);
        result.setData("OPT_TERM", optTerm);

        return result;
    }


    /**
     * 整理需要插入ODI_DSPND的数据
     * @param fromUddData List 从UDD返回的需要的值
     * @param orderParm TParm 主表的基础数据
     * @return TParm
     */
    private TParm getDataDspnd(List fromUddData, TParm orderParm,
                               String orderDateTime, int row, double mediQty,
                               String mediUnit,
                               double dosageQty, String dosageUnit,
                               String optUser,
                               Timestamp optDate, String optTerm) {

        TParm result = new TParm();
        result.setData("CASE_NO", orderParm.getData("CASE_NO"));
        result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
        result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
        result.setData("ORDER_DATE", orderDateTime.substring(0, 8));
        result.setData("ORDER_DATETIME", orderDateTime.substring(8));

        result.setData("BATCH_CODE", orderParm.getData("BATCH_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ANTIBIOTIC_CODE"));
        result.setData("TREAT_START_TIME", orderParm.getData("TREAT_START_TIME") == null ?
                       new TNull(String.class) :
                       orderParm.getData("TREAT_START_TIME"));
        result.setData("TREAT_END_TIME", orderParm.getData("TREAT_END_TIME") == null ?
                       new TNull(String.class) :
                       orderParm.getData("TREAT_END_TIME"));
        result.setData("NURSE_DISPENSE_FLG",
                       orderParm.getData("NURSE_DISPENSE_FLG") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NURSE_DISPENSE_FLG"));
        result.setData("BAR_CODE", orderParm.getData("BAR_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("BAR_CODE"));

        result.setData("ORDER_CODE", orderParm.getData("ORDER_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("ORDER_CODE"));
        result.setData("MEDI_QTY", orderParm.getData("MEDI_QTY") == null ?
                       0.00 :
                       mediQty);
        result.setData("MEDI_UNIT", orderParm.getData("MEDI_UNIT") == null ?
                       new TNull(String.class) :
                       mediUnit);
        result.setData("DOSAGE_QTY", orderParm.getData("DOSAGE_QTY") == null ?
                       0.00 :
                       dosageQty);
        result.setData("DOSAGE_UNIT", orderParm.getData("DOSAGE_UNIT") == null ?
                       new TNull(String.class) :
                       dosageUnit);

        result.setData("TOT_AMT",
                       orderParm.getData("TOT_AMT") == null ? 0.00 :
                       orderParm.getData("TOT_AMT"));
        if(!orderParm.getValue("DC_DATE").equals("")&&StringTool.getTimestamp(orderDateTime, "yyyyMMddHHmm").compareTo(orderParm.getTimestamp("DC_DATE"))>0){
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
		}else{
			result.setData("DC_DATE",new TNull(Timestamp.class));
		}
        result.setData("PHA_DISPENSE_NO",
                       orderParm.getData("PHA_DISPENSE_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_DISPENSE_NO"));
        result.setData("PHA_DOSAGE_CODE",
                       orderParm.getData("PHA_DOSAGE_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_DOSAGE_CODE"));
        result.setData("PHA_DOSAGE_DATE",
                       orderParm.getData("PHA_DOSAGE_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_DOSAGE_DATE"));

        result.setData("PHA_DISPENSE_CODE",
                       orderParm.getData("NS_EXEC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_EXEC_CODE"));
        result.setData("PHA_DISPENSE_DATE",
                       orderParm.getData("NS_EXEC_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("NS_EXEC_DATE"));
        result.setData("NS_EXEC_CODE",
                       orderParm.getData("NS_EXEC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_EXEC_CODE"));
        result.setData("NS_EXEC_DATE",
                       orderParm.getData("NS_EXEC_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("NS_EXEC_DATE"));
        result.setData("NS_EXEC_DC_CODE",
                       orderParm.getData("NS_EXEC_DC_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("NS_EXEC_DC_CODE"));

        result.setData("NS_EXEC_DC_DATE",
                       orderParm.getData("NS_EXEC_DC_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("NS_EXEC_DC_DATE"));
        result.setData("NS_USER",
                       orderParm.getData("NS_USER") == null ? new TNull(String.class) :
                       orderParm.getData("NS_USER"));
        result.setData("EXEC_NOTE",
                       orderParm.getData("EXEC_NOTE") == null ?
                       new TNull(String.class) : orderParm.getData("EXEC_NOTE"));
        result.setData("EXEC_DEPT_CODE",
                       orderParm.getData("EXEC_DEPT_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("EXEC_DEPT_CODE"));
        
        //==== add by lx 2012-06-01 执行科室转成成本中心====$$//
//        result.setData("EXEC_DEPT_CODE", this.getCostCenter((String)result.getData("EXEC_DEPT_CODE")));
        //System.out.println("== getDataDspnd exec dept code =="+this.getCostCenter((String)result.getData("EXEC_DEPT_CODE")));
        //==== add by lx 2012-06-01 ====$$//       
       
        result.setData("BILL_FLG",
                       orderParm.getData("BILL_FLG") == null ? new TNull(String.class) :
                       orderParm.getData("BILL_FLG"));

        result.setData("CASHIER_CODE",
                       orderParm.getData("CASHIER_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("CASHIER_CODE"));
        result.setData("CASHIER_DATE",
                       orderParm.getData("CASHIER_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("CASHIER_DATE"));
        result.setData("PHA_RETN_CODE",
                       orderParm.getData("PHA_RETN_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("PHA_RETN_CODE"));
        result.setData("PHA_RETN_DATE",
                       orderParm.getData("PHA_RETN_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("PHA_RETN_DATE"));
        result.setData("TRANSMIT_RSN_CODE",
                       orderParm.getData("TRANSMIT_RSN_CODE") == null ?
                       new TNull(String.class) :
                       orderParm.getData("TRANSMIT_RSN_CODE"));

        result.setData("STOPCHECK_USER",
                       orderParm.getData("STOPCHECK_USER") == null ?
                       new TNull(String.class) :
                       orderParm.getData("STOPCHECK_USER"));
        result.setData("STOPCHECK_DATE",
                       orderParm.getData("STOPCHECK_DATE") == null ?
                       new TNull(Timestamp.class) :
                       orderParm.getData("STOPCHECK_DATE"));
        result.setData("IBS_CASE_NO",
                       orderParm.getData("IBS_CASE_NO") == null ?
                       new TNull(String.class) :
                       orderParm.getData("IBS_CASE_NO"));
        result.setData("IBS_CASE_NO_SEQ",
                       orderParm.getData("IBS_CASE_NO_SEQ") == null ?
                       new TNull(String.class) :
                       orderParm.getData("IBS_CASE_NO_SEQ"));  
        result.setData("ANTIBIOTIC_WAY", new TNull(String.class));
       
        result.setData("TAKEMED_ORG", new TNull(String.class)); //20130227 shibl add
        
        
        result.setData("OPT_USER", optUser);
        result.setData("OPT_DATE", optDate);
        result.setData("OPT_TERM", optTerm);

        return result;

    }

    /**
     * 根据caseNo得到该病人的长期处置    *
     */
    private TParm getOrderTRT(String caseNo,String nowstr) {
        TParm result = new TParm();
        String SelSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo + 
        "' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') AND RX_KIND = 'UD' " + 
//      " AND LAST_DSPN_DATE <=TO_DATE('" + nowstr + "','YYYYMMDDHH24MISS')"+
        " AND NS_CHECK_DATE <=TO_DATE('" + nowstr + "','YYYYMMDDHH24MISS')" +
        " AND (EFF_DATE<=TO_DATE('" + nowstr + "','YYYYMMDDHH24MISS')+1) "+
        " AND (DC_DATE IS NULL OR DC_DATE > TO_DATE('"+ nowstr+ "','YYYYMMDDHH24MISS') )";//shibl 20121229 modify 关于隔日审核时筛选医嘱     
        //得到该病人所有该执行展开的处置
        result = new TParm(TJDODBTool.getInstance().select(SelSql));
        return result;
    }
    
	/**
	 * 获得成本中心
	 * 
	 * @return
	 */
	private String getCostCenter(String execDeptCode) {
		TParm result = new TParm();
		String sql = "SELECT COST_CENTER_CODE FROM SYS_DEPT WHERE DEPT_CODE='"
				+ execDeptCode + "'";
		//System.out.println("==sql=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("COST_CENTER_CODE", 0);
	}
	
	/**
	 * 
	 * 获取sys_fee相关保存信息;
	 * 
	 * @return
	 */
/*	private TParm getSysFeeData(TParm inParm) {
		// String mrNo,String orderCode, dosage_qty,ctz_code
		TParm result = new TParm();
		// OWN_PRICE NHI_PRICE DISCOUNT_RATE OWN_AMT TOT_AMT
		String sql = "SELECT CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
				+ inParm.getValue("orderCode") + "'";
		TParm sysFeeResult = new TParm(TJDODBTool.getInstance().select(sql));
		result.setData("CHARGE_HOSP_CODE", sysFeeResult.getData(
				"CHARGE_HOSP_CODE", 0));
		result.setData("OWN_PRICE", sysFeeResult.getData("OWN_PRICE", 0));
		result.setData("NHI_PRICE", sysFeeResult.getData("NHI_PRICE", 0));

		String sql1 = "SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE='"
				+ inParm.getValue("CTZ_CODE") + "'";
		sql1 += " AND CHARGE_HOSP_CODE='" + inParm.getValue("CHARGE_HOSP_CODE")
				+ "'";
		TParm rateResult = new TParm(TJDODBTool.getInstance().select(sql1));
		result.setData("DISCOUNT_RATE", rateResult.getData("DISCOUNT_RATE", 0));
		result.setData("OWN_AMT", StringTool.round((Double) sysFeeResult
				.getData("OWN_PRICE", 0)
				* (Double) sysFeeResult.getData("DOSEAGE_TOT", 0), 2));
		result.setData("TOT_AMT", StringTool.round((Double) result
				.getData("OWN_AMT")
				* (Double) result.getData("DISCOUNT_RATE"), 2));
		System.out.println("==result==" + result);
		return result;
	}*/
	
}
