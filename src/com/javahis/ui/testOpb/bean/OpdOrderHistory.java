

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
@Table(tableName = "OPD_ORDER_HISTORY")
public class OpdOrderHistory extends BasePOJO{

     @PKey(name = "SEQ_NO", type = Type.NUM)
     public java.math.BigDecimal seqNo = null;

     @PKey(name = "RX_NO", type = Type.CHAR)
     public java.lang.String rxNo = null;

     @PKey(name = "DC_ORDER_DATE", type = Type.CHAR)
     public java.lang.String dcOrderDate = null;

     @PKey(name = "CASE_NO", type = Type.CHAR)
     public java.lang.String caseNo = null;

     @Column(name = "URGENT_FLG", type = Type.CHAR)
     public java.lang.String urgentFlg = null;

     @Column(name = "TAKE_DAYS", type = Type.NUM)
     public java.math.BigDecimal takeDays = null;

     @Column(name = "SPECIFICATION", type = Type.CHAR)
     public java.lang.String specification = null;

     @Column(name = "SETMAIN_FLG", type = Type.CHAR)
     public java.lang.String setmainFlg = null;

     @Column(name = "RX_TYPE", type = Type.CHAR)
     public java.lang.String rxType = null;

     @Column(name = "RPTTYPE_CODE", type = Type.CHAR)
     public java.lang.String rpttypeCode = null;

     @Column(name = "ROUTE_CODE", type = Type.CHAR)
     public java.lang.String routeCode = null;

     @Column(name = "RELEASE_FLG", type = Type.CHAR)
     public java.lang.String releaseFlg = null;

     @Column(name = "REGION_CODE", type = Type.CHAR)
     public java.lang.String regionCode = null;

     @Column(name = "PRINTTYPEFLG_INFANT", type = Type.CHAR)
     public java.lang.String printtypeflgInfant = null;

     @Column(name = "PRESRT_NO", type = Type.CHAR)
     public java.lang.String presrtNo = null;

     @Column(name = "PRESCRIPT_NO", type = Type.NUM)
     public java.math.BigDecimal prescriptNo = null;

     @Column(name = "PHA_TYPE", type = Type.CHAR)
     public java.lang.String phaType = null;

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

     @Column(name = "OPT_TERM", type = Type.CHAR)
     public java.lang.String optTerm = null;

     @Column(name = "OPT_DATE", type = Type.DATE)
     public java.lang.String optDate = null;

     @Column(name = "OPTITEM_CODE", type = Type.CHAR)
     public java.lang.String optitemCode = null;

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

     @Column(name = "MR_NO", type = Type.CHAR)
     public java.lang.String mrNo = null;

     @Column(name = "MR_CODE", type = Type.CHAR)
     public java.lang.String mrCode = null;

     @Column(name = "MEDI_UNIT", type = Type.CHAR)
     public java.lang.String mediUnit = null;

     @Column(name = "MEDI_QTY", type = Type.NUM)
     public java.math.BigDecimal mediQty = null;

     @Column(name = "LINK_NO", type = Type.CHAR)
     public java.lang.String linkNo = null;

     @Column(name = "LINKMAIN_FLG", type = Type.CHAR)
     public java.lang.String linkmainFlg = null;

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

     @Column(name = "FREQ_CODE", type = Type.CHAR)
     public java.lang.String freqCode = null;

     @Column(name = "FILE_NO", type = Type.NUM)
     public java.math.BigDecimal fileNo = null;

     @Column(name = "EXPENSIVE_FLG", type = Type.CHAR)
     public java.lang.String expensiveFlg = null;

     @Column(name = "EXM_EXEC_END_DATE", type = Type.DATE)
     public java.lang.String exmExecEndDate = null;

     @Column(name = "EXEC_DR_CODE", type = Type.CHAR)
     public java.lang.String execDrCode = null;

     @Column(name = "EXEC_DEPT_CODE", type = Type.CHAR)
     public java.lang.String execDeptCode = null;

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

     @Column(name = "DC_DR_CODE", type = Type.CHAR)
     public java.lang.String dcDrCode = null;

     @Column(name = "DC_DEPT_CODE", type = Type.CHAR)
     public java.lang.String dcDeptCode = null;

     @Column(name = "DCT_TAKE_QTY", type = Type.NUM)
     public java.math.BigDecimal dctTakeQty = null;

     @Column(name = "DCTEXCEP_CODE", type = Type.CHAR)
     public java.lang.String dctexcepCode = null;

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

     @Column(name = "CONTRACT_CODE", type = Type.CHAR)
     public java.lang.String contractCode = null;

     @Column(name = "BUSINESS_NO", type = Type.CHAR)
     public java.lang.String businessNo = null;

     @Column(name = "AR_AMT", type = Type.NUM)
     public java.math.BigDecimal arAmt = null;

     @Column(name = "ADM_TYPE", type = Type.CHAR)
     public java.lang.String admType = null;
     
     @Column(name = "RECEIPT_NO", type = Type.CHAR)
     public java.lang.String receiptNo = null;
     
     @Column(name = "BILL_FLG", type = Type.CHAR)
     public java.lang.String billFlg = null;
     
     @Column(name = "BILL_DATE", type = Type.DATE)
     public java.lang.String billDate = null;

     @Column(name = "REXP_CODE", type = Type.CHAR)
     public java.lang.String rexpCode = null;

}
