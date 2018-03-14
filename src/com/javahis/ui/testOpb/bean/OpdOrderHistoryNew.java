package com.javahis.ui.testOpb.bean;


import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.annotation.PKey;
import com.javahis.ui.testOpb.annotation.Table;

import com.javahis.ui.testOpb.tools.Type;


/**
 *
 * @author whao
 * @author zhangp
 */
@Table(tableName = "OPD_ORDER_HISTORY_NEW")
public class OpdOrderHistoryNew extends BasePOJO{

	@PKey(name = "HISTORY_ID", type = Type.CHAR)
     public java.lang.String historyId = null;

     @Column(name = "VERIFYIN_PRICE3", type = Type.NUM)
     public java.math.BigDecimal verifyinPrice3 = null;

     @Column(name = "VERIFYIN_PRICE2", type = Type.NUM)
     public java.math.BigDecimal verifyinPrice2 = null;

     @Column(name = "VERIFYIN_PRICE1", type = Type.NUM)
     public java.math.BigDecimal verifyinPrice1 = null;

     @Column(name = "URGENT_FLG", type = Type.CHAR)
     public java.lang.String urgentFlg = null;

     @Column(name = "TRADE_ENG_DESC", type = Type.CHAR)
     public java.lang.String tradeEngDesc = null;

     @Column(name = "TOXIC_ID3", type = Type.CHAR)
     public java.lang.String toxicId3 = null;

     @Column(name = "TOXIC_ID2", type = Type.CHAR)
     public java.lang.String toxicId2 = null;

     @Column(name = "TOXIC_ID1", type = Type.CHAR)
     public java.lang.String toxicId1 = null;

     @Column(name = "TOXIC_ID", type = Type.CHAR)
     public java.lang.String toxicId = null;

     @Column(name = "TOT_AMT2", type = Type.NUM)
     public java.math.BigDecimal totAmt2 = null;

     @Column(name = "TEMPORARY_FLG", type = Type.CHAR)
     public java.lang.String temporaryFlg = null;

     @Column(name = "TAKE_DAYS", type = Type.NUM)
     public java.math.BigDecimal takeDays = null;

     @Column(name = "SPECIFICATION", type = Type.CHAR)
     public java.lang.String specification = null;

     @Column(name = "SKINTEST_FLG", type = Type.CHAR)
     public java.lang.String skintestFlg = null;

     @Column(name = "SEX_TYPE", type = Type.CHAR)
     public java.lang.String sexType = null;

     @Column(name = "SETMAIN_FLG", type = Type.CHAR)
     public java.lang.String setmainFlg = null;

     @Column(name = "SEQ_NO", type = Type.NUM)
     public java.math.BigDecimal seqNo = null;

     @Column(name = "SEND_ORG_USER", type = Type.CHAR)
     public java.lang.String sendOrgUser = null;

     @Column(name = "SEND_ORG_DATE", type = Type.DATE)
     public java.lang.String sendOrgDate = null;

     @Column(name = "SEND_DCT_USER", type = Type.CHAR)
     public java.lang.String sendDctUser = null;

     @Column(name = "SEND_DCT_DATE", type = Type.DATE)
     public java.lang.String sendDctDate = null;

     @Column(name = "SENDATC_DATE", type = Type.DATE)
     public java.lang.String sendatcDate = null;

     @Column(name = "RX_TYPE", type = Type.CHAR)
     public java.lang.String rxType = null;

     @Column(name = "RX_NO", type = Type.CHAR)
     public java.lang.String rxNo = null;

     @Column(name = "RPTTYPE_CODE", type = Type.CHAR)
     public java.lang.String rpttypeCode = null;

     @Column(name = "ROUTE_CODE", type = Type.CHAR)
     public java.lang.String routeCode = null;

     @Column(name = "REXP_CODE", type = Type.CHAR)
     public java.lang.String rexpCode = null;

     @Column(name = "REQUEST_NO", type = Type.CHAR)
     public java.lang.String requestNo = null;

     @Column(name = "REQUEST_FLG", type = Type.CHAR)
     public java.lang.String requestFlg = null;

     @Column(name = "RELEASE_FLG", type = Type.CHAR)
     public java.lang.String releaseFlg = null;

     @Column(name = "REGION_CODE", type = Type.CHAR)
     public java.lang.String regionCode = null;

     @Column(name = "RECLAIM_USER", type = Type.CHAR)
     public java.lang.String reclaimUser = null;

     @Column(name = "RECLAIM_DATE", type = Type.DATE)
     public java.lang.String reclaimDate = null;

     @Column(name = "RECEIPT_NO", type = Type.CHAR)
     public java.lang.String receiptNo = null;

     @Column(name = "RECEIPT_FLG", type = Type.CHAR)
     public java.lang.String receiptFlg = null;

     @Column(name = "QE_BILL_FLG", type = Type.CHAR)
     public java.lang.String qeBillFlg = null;

     @Column(name = "PSY_FLG", type = Type.CHAR)
     public java.lang.String psyFlg = null;

     @Column(name = "PRINT_NO", type = Type.CHAR)
     public java.lang.String printNo = null;

     @Column(name = "PRINT_FLG", type = Type.CHAR)
     public java.lang.String printFlg = null;

     @Column(name = "PRINTTYPEFLG_INFANT", type = Type.CHAR)
     public java.lang.String printtypeflgInfant = null;

     @Column(name = "PRESRT_NO", type = Type.CHAR)
     public java.lang.String presrtNo = null;

     @Column(name = "PRESCRIPT_NO", type = Type.NUM)
     public java.math.BigDecimal prescriptNo = null;

     @Column(name = "PICK_TMIE", type = Type.DATE)
     public java.lang.String pickTmie = null;

     @Column(name = "PHA_TYPE", type = Type.CHAR)
     public java.lang.String phaType = null;

     @Column(name = "PHA_RETN_DATE", type = Type.DATE)
     public java.lang.String phaRetnDate = null;

     @Column(name = "PHA_RETN_CODE", type = Type.CHAR)
     public java.lang.String phaRetnCode = null;

     @Column(name = "PHA_DOSAGE_DATE", type = Type.DATE)
     public java.lang.String phaDosageDate = null;

     @Column(name = "PHA_DOSAGE_CODE", type = Type.CHAR)
     public java.lang.String phaDosageCode = null;

     @Column(name = "PHA_DISPENSE_DATE", type = Type.DATE)
     public java.lang.String phaDispenseDate = null;

     @Column(name = "PHA_DISPENSE_CODE", type = Type.CHAR)
     public java.lang.String phaDispenseCode = null;

     @Column(name = "PHA_CHECK_DATE", type = Type.DATE)
     public java.lang.String phaCheckDate = null;

     @Column(name = "PHA_CHECK_CODE", type = Type.CHAR)
     public java.lang.String phaCheckCode = null;

     @Column(name = "PAT_NAME", type = Type.CHAR)
     public java.lang.String patName = null;

     @Column(name = "PACKAGE_TOT", type = Type.NUM)
     public java.math.BigDecimal packageTot = null;

     @Column(name = "OWN_PRICE", type = Type.NUM)
     public java.math.BigDecimal ownPrice = null;

     @Column(name = "OWN_AMT", type = Type.NUM)
     public java.math.BigDecimal ownAmt = null;

     @Column(name = "ORDER_DESC", type = Type.CHAR)
     public java.lang.String orderDesc = null;

     @Column(name = "ORDER_DATE", type = Type.DATE)
     public java.lang.String orderDate = null;

     @Column(name = "ORDER_CODE", type = Type.CHAR)
     public java.lang.String orderCode = null;

     @Column(name = "ORDER_CAT1_CODE", type = Type.CHAR)
     public java.lang.String orderCat1Code = null;

     @Column(name = "ORDERSET_GROUP_NO", type = Type.NUM)
     public java.math.BigDecimal ordersetGroupNo = null;

     @Column(name = "ORDERSET_CODE", type = Type.CHAR)
     public java.lang.String ordersetCode = null;

     @Column(name = "OPT_USER", type = Type.CHAR)
     public java.lang.String optUser = null;

     @Column(name = "OPT_TYPE", type = Type.CHAR)
     public java.lang.String optType = null;

     @Column(name = "OPT_TERM", type = Type.CHAR)
     public java.lang.String optTerm = null;

     @Column(name = "OPT_DATE", type = Type.DATE)
     public java.lang.String optDate = null;

     @Column(name = "OPTITEM_CODE", type = Type.CHAR)
     public java.lang.String optitemCode = null;

     @Column(name = "OPD_OPT_USER", type = Type.CHAR)
     public java.lang.String opdOptUser = null;

     @Column(name = "OPD_OPT_TERM", type = Type.CHAR)
     public java.lang.String opdOptTerm = null;

     @Column(name = "OPD_OPT_DATE", type = Type.DATE)
     public java.lang.String opdOptDate = null;

     @Column(name = "OPB_RECP_NO", type = Type.CHAR)
     public java.lang.String opbRecpNo = null;

     @Column(name = "NS_NOTE", type = Type.CHAR)
     public java.lang.String nsNote = null;

     @Column(name = "NS_EXEC_DEPT", type = Type.CHAR)
     public java.lang.String nsExecDept = null;

     @Column(name = "NS_EXEC_DATE", type = Type.DATE)
     public java.lang.String nsExecDate = null;

     @Column(name = "NS_EXEC_CODE", type = Type.CHAR)
     public java.lang.String nsExecCode = null;

     @Column(name = "NHI_PRICE", type = Type.NUM)
     public java.math.BigDecimal nhiPrice = null;

     @Column(name = "MZCONFIRM_NO", type = Type.CHAR)
     public java.lang.String mzconfirmNo = null;

     @Column(name = "MR_NO", type = Type.CHAR)
     public java.lang.String mrNo = null;

     @Column(name = "MR_CODE", type = Type.CHAR)
     public java.lang.String mrCode = null;

     @Column(name = "MED_REPRESENTOR", type = Type.CHAR)
     public java.lang.String medRepresentor = null;

     @Column(name = "MED_APPLY_NO", type = Type.CHAR)
     public java.lang.String medApplyNo = null;

     @Column(name = "MEDI_UNIT", type = Type.CHAR)
     public java.lang.String mediUnit = null;

     @Column(name = "MEDI_QTY", type = Type.NUM)
     public java.math.BigDecimal mediQty = null;

     @Column(name = "LOCATION", type = Type.CHAR)
     public java.lang.String location = null;

     @Column(name = "LINK_NO", type = Type.CHAR)
     public java.lang.String linkNo = null;

     @Column(name = "LINKMAIN_FLG", type = Type.CHAR)
     public java.lang.String linkmainFlg = null;

     @Column(name = "LAST_HISTORY_ID", type = Type.CHAR)
     public java.lang.String lastHistoryId = null;

     @Column(name = "IS_UPDATE", type = Type.CHAR)
     public java.lang.String isUpdate = null;

     @Column(name = "IS_RECLAIM", type = Type.CHAR)
     public java.lang.String isReclaim = null;

     @Column(name = "INSPAY_TYPE", type = Type.CHAR)
     public java.lang.String inspayType = null;

     @Column(name = "HIDE_FLG", type = Type.CHAR)
     public java.lang.String hideFlg = null;

     @Column(name = "HEXP_CODE", type = Type.CHAR)
     public java.lang.String hexpCode = null;

     @Column(name = "GOODS_DESC", type = Type.CHAR)
     public java.lang.String goodsDesc = null;

     @Column(name = "GIVEBOX_FLG", type = Type.CHAR)
     public java.lang.String giveboxFlg = null;

     @Column(name = "FROM_FLG", type = Type.CHAR)
     public java.lang.String fromFlg = null;

     @Column(name = "FREQ_CODE", type = Type.CHAR)
     public java.lang.String freqCode = null;

     @Column(name = "FINAL_TYPE", type = Type.CHAR)
     public java.lang.String finalType = null;

     @Column(name = "FILE_NO", type = Type.NUM)
     public java.math.BigDecimal fileNo = null;

     @Column(name = "EXPENSIVE_FLG", type = Type.CHAR)
     public java.lang.String expensiveFlg = null;

     @Column(name = "EXM_EXEC_END_DATE", type = Type.DATE)
     public java.lang.String exmExecEndDate = null;

     @Column(name = "EXEC_FLG", type = Type.CHAR)
     public java.lang.String execFlg = null;

     @Column(name = "EXEC_DR_DESC", type = Type.CHAR)
     public java.lang.String execDrDesc = null;

     @Column(name = "EXEC_DR_CODE", type = Type.CHAR)
     public java.lang.String execDrCode = null;

     @Column(name = "EXEC_DEPT_CODE", type = Type.CHAR)
     public java.lang.String execDeptCode = null;

     @Column(name = "EKT_HISTORY_NO", type = Type.CHAR)
     public java.lang.String ektHistoryNo = null;

     @Column(name = "DR_NOTE", type = Type.CHAR)
     public java.lang.String drNote = null;

     @Column(name = "DR_CODE", type = Type.CHAR)
     public java.lang.String drCode = null;

     @Column(name = "DOSE_TYPE", type = Type.CHAR)
     public java.lang.String doseType = null;

     @Column(name = "DOSAGE_UNIT", type = Type.CHAR)
     public java.lang.String dosageUnit = null;

     @Column(name = "DOSAGE_QTY", type = Type.NUM)
     public java.math.BigDecimal dosageQty = null;

     @Column(name = "DISPENSE_UNIT", type = Type.CHAR)
     public java.lang.String dispenseUnit = null;

     @Column(name = "DISPENSE_QTY3", type = Type.NUM)
     public java.math.BigDecimal dispenseQty3 = null;

     @Column(name = "DISPENSE_QTY2", type = Type.NUM)
     public java.math.BigDecimal dispenseQty2 = null;

     @Column(name = "DISPENSE_QTY1", type = Type.NUM)
     public java.math.BigDecimal dispenseQty1 = null;

     @Column(name = "DISPENSE_QTY", type = Type.NUM)
     public java.math.BigDecimal dispenseQty = null;

     @Column(name = "DISCOUNT_RATE", type = Type.NUM)
     public java.math.BigDecimal discountRate = null;

     @Column(name = "DEV_CODE", type = Type.CHAR)
     public java.lang.String devCode = null;

     @Column(name = "DEPT_CODE", type = Type.CHAR)
     public java.lang.String deptCode = null;

     @Column(name = "DEGREE_CODE", type = Type.CHAR)
     public java.lang.String degreeCode = null;

     @Column(name = "DECOCT_USER", type = Type.CHAR)
     public java.lang.String decoctUser = null;

     @Column(name = "DECOCT_REMARK", type = Type.CHAR)
     public java.lang.String decoctRemark = null;

     @Column(name = "DECOCT_DATE", type = Type.DATE)
     public java.lang.String decoctDate = null;

     @Column(name = "DECOCT_CODE", type = Type.CHAR)
     public java.lang.String decoctCode = null;

     @Column(name = "DC_ORDER_DATE", type = Type.DATE)
     public java.lang.String dcOrderDate = null;

     @Column(name = "DC_DR_CODE", type = Type.CHAR)
     public java.lang.String dcDrCode = null;

     @Column(name = "DC_DEPT_CODE", type = Type.CHAR)
     public java.lang.String dcDeptCode = null;

     @Column(name = "DCT_TAKE_QTY", type = Type.NUM)
     public java.math.BigDecimal dctTakeQty = null;

     @Column(name = "DCTEXCEP_CODE", type = Type.CHAR)
     public java.lang.String dctexcepCode = null;

     @Column(name = "DCTAGENT_FLG", type = Type.CHAR)
     public java.lang.String dctagentFlg = null;

     @Column(name = "DCTAGENT_CODE", type = Type.CHAR)
     public java.lang.String dctagentCode = null;

     @Column(name = "CTZ3_CODE", type = Type.CHAR)
     public java.lang.String ctz3Code = null;

     @Column(name = "CTZ2_CODE", type = Type.CHAR)
     public java.lang.String ctz2Code = null;

     @Column(name = "CTZ1_CODE", type = Type.CHAR)
     public java.lang.String ctz1Code = null;

     @Column(name = "CTRLDRUGCLASS_CODE", type = Type.CHAR)
     public java.lang.String ctrldrugclassCode = null;

     @Column(name = "COUNTER_NO", type = Type.NUM)
     public java.math.BigDecimal counterNo = null;

     @Column(name = "COST_CENTER_CODE", type = Type.CHAR)
     public java.lang.String costCenterCode = null;

     @Column(name = "COST_AMT", type = Type.NUM)
     public java.math.BigDecimal costAmt = null;

     @Column(name = "CONTRACT_CODE", type = Type.CHAR)
     public java.lang.String contractCode = null;

     @Column(name = "CAT1_TYPE", type = Type.CHAR)
     public java.lang.String cat1Type = null;

     @Column(name = "CASE_NO", type = Type.CHAR)
     public java.lang.String caseNo = null;

     @Column(name = "BUSINESS_NO", type = Type.CHAR)
     public java.lang.String businessNo = null;

     @Column(name = "BIRTH_DATE", type = Type.DATE)
     public java.lang.String birthDate = null;

     @Column(name = "BILL_USER", type = Type.CHAR)
     public java.lang.String billUser = null;

     @Column(name = "BILL_TYPE", type = Type.CHAR)
     public java.lang.String billType = null;

     @Column(name = "BILL_FLG", type = Type.CHAR)
     public java.lang.String billFlg = null;

     @Column(name = "BILL_DATE", type = Type.DATE)
     public java.lang.String billDate = null;

     @Column(name = "BED_NO", type = Type.CHAR)
     public java.lang.String bedNo = null;

     @Column(name = "BATCH_SEQ3", type = Type.NUM)
     public java.math.BigDecimal batchSeq3 = null;

     @Column(name = "BATCH_SEQ2", type = Type.NUM)
     public java.math.BigDecimal batchSeq2 = null;

     @Column(name = "BATCH_SEQ1", type = Type.NUM)
     public java.math.BigDecimal batchSeq1 = null;

     @Column(name = "BAR_CODE", type = Type.CHAR)
     public java.lang.String barCode = null;

     @Column(name = "ATC_FLG", type = Type.CHAR)
     public java.lang.String atcFlg = null;

     @Column(name = "AR_AMT", type = Type.NUM)
     public java.math.BigDecimal arAmt = null;

     @Column(name = "AGENCY_ORG_CODE", type = Type.CHAR)
     public java.lang.String agencyOrgCode = null;

     @Column(name = "ADM_TYPE", type = Type.CHAR)
     public java.lang.String admType = null;

     @Column(name = "ACUM_OUTBOUND_QTY", type = Type.NUM)
     public java.math.BigDecimal acumOutboundQty = null;

     @Column(name = "ACTIVE_FLG", type = Type.CHAR)
     public java.lang.String activeFlg = null;


}
