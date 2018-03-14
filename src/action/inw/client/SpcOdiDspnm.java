
package action.inw.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOdiDspnm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOdiDspnm">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acumOutboundQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="agencyOrgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="antibioticCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="atcFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="barCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchSeq1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="batchSeq2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="batchSeq3" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="bedNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="boxEslId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cancelDosageQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="cancelrsnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cashierDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cashierUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cat1Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctrldrugclassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcDrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcNsCheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcTot" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dctTakeQty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dctagentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dctagentFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dctexcepCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctRemark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="degreeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountRate" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dispenseEffDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dispenseQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dispenseQty1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dispenseQty2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dispenseQty3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dispenseUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dosageQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dosageUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="doseType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dspnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dspnKind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dspnUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execDeptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="finalType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="freqCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giveboxFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodsDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hideFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ibsCaseNoSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ibsSeqNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="injpracGroup" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="intgmedNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipdNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isIntg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkmainFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lisReDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lisReUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mediQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="mediUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nhiPrice" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="nsExecCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDcCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDcDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optitemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCat1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDeptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ordersetCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ordersetGroupNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownAmt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ownPrice" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="packageAmt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentCaseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parentOrderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parentOrderSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentStartDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prescriptNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="presrtNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnQty1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="returnQty2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="returnQty3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="routeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rpttypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rtnDosageQty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="rtnDosageUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rtnNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rtnNoSeq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="rxNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendDctDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendDctUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendOrgDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendOrgUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendatcDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendatcFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="setmainFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spcOdiDspnds" type="{http://services.spc.action/}spcOdiDspnd" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="specification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDttm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="takeDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="takemedNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="takemedOrg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totAmt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="transmitRsnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="turnEslId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urgentFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verifyinPrice1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="verifyinPrice2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="verifyinPrice3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="vsDrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOdiDspnm", propOrder = {
    "acumOutboundQty",
    "agencyOrgCode",
    "antibioticCode",
    "atcFlg",
    "barCode",
    "batchSeq1",
    "batchSeq2",
    "batchSeq3",
    "bedNo",
    "billFlg",
    "boxEslId",
    "cancelDosageQty",
    "cancelrsnCode",
    "caseNo",
    "cashierDate",
    "cashierUser",
    "cat1Type",
    "ctrldrugclassCode",
    "dcDate",
    "dcDrCode",
    "dcNsCheckDate",
    "dcTot",
    "dctTakeQty",
    "dctagentCode",
    "dctagentFlg",
    "dctexcepCode",
    "decoctCode",
    "decoctDate",
    "decoctRemark",
    "decoctUser",
    "degreeCode",
    "deptCode",
    "discountRate",
    "dispenseEffDate",
    "dispenseEndDate",
    "dispenseFlg",
    "dispenseQty",
    "dispenseQty1",
    "dispenseQty2",
    "dispenseQty3",
    "dispenseUnit",
    "dosageQty",
    "dosageUnit",
    "doseType",
    "drNote",
    "dspnDate",
    "dspnKind",
    "dspnUser",
    "endDttm",
    "execDeptCode",
    "finalType",
    "freqCode",
    "giveboxFlg",
    "goodsDesc",
    "hideFlg",
    "ibsCaseNoSeq",
    "ibsSeqNo",
    "injpracGroup",
    "intgmedNo",
    "ipdNo",
    "isIntg",
    "linkNo",
    "linkmainFlg",
    "lisReDate",
    "lisReUser",
    "mediQty",
    "mediUnit",
    "mrNo",
    "nhiPrice",
    "nsExecCode",
    "nsExecDate",
    "nsExecDcCode",
    "nsExecDcDate",
    "nsUser",
    "optDate",
    "optTerm",
    "optUser",
    "optitemCode",
    "orderCat1Code",
    "orderCode",
    "orderDate",
    "orderDeptCode",
    "orderDesc",
    "orderDrCode",
    "orderNo",
    "orderSeq",
    "ordersetCode",
    "ordersetGroupNo",
    "ownAmt",
    "ownPrice",
    "packageAmt",
    "parentCaseNo",
    "parentOrderNo",
    "parentOrderSeq",
    "parentStartDttm",
    "phaCheckCode",
    "phaCheckDate",
    "phaDispenseCode",
    "phaDispenseDate",
    "phaDispenseNo",
    "phaDosageCode",
    "phaDosageDate",
    "phaRetnCode",
    "phaRetnDate",
    "phaType",
    "prescriptNo",
    "presrtNo",
    "regionCode",
    "returnQty1",
    "returnQty2",
    "returnQty3",
    "routeCode",
    "rpttypeCode",
    "rtnDosageQty",
    "rtnDosageUnit",
    "rtnNo",
    "rtnNoSeq",
    "rxNo",
    "sendDctDate",
    "sendDctUser",
    "sendOrgDate",
    "sendOrgUser",
    "sendatcDttm",
    "sendatcFlg",
    "serviceLevel",
    "setmainFlg",
    "spcOdiDspnds",
    "specification",
    "startDttm",
    "stationCode",
    "takeDays",
    "takemedNo",
    "takemedOrg",
    "totAmt",
    "transmitRsnCode",
    "turnEslId",
    "urgentFlg",
    "verifyinPrice1",
    "verifyinPrice2",
    "verifyinPrice3",
    "vsDrCode"
})
public class SpcOdiDspnm {

    protected double acumOutboundQty;
    protected String agencyOrgCode;
    protected String antibioticCode;
    protected String atcFlg;
    protected String barCode;
    protected int batchSeq1;
    protected int batchSeq2;
    protected int batchSeq3;
    protected String bedNo;
    protected String billFlg;
    protected String boxEslId;
    protected double cancelDosageQty;
    protected String cancelrsnCode;
    protected String caseNo;
    protected String cashierDate;
    protected String cashierUser;
    protected String cat1Type;
    protected String ctrldrugclassCode;
    protected String dcDate;
    protected String dcDrCode;
    protected String dcNsCheckDate;
    protected double dcTot;
    protected int dctTakeQty;
    protected String dctagentCode;
    protected String dctagentFlg;
    protected String dctexcepCode;
    protected String decoctCode;
    protected String decoctDate;
    protected String decoctRemark;
    protected String decoctUser;
    protected String degreeCode;
    protected String deptCode;
    protected double discountRate;
    protected String dispenseEffDate;
    protected String dispenseEndDate;
    protected String dispenseFlg;
    protected double dispenseQty;
    protected double dispenseQty1;
    protected double dispenseQty2;
    protected double dispenseQty3;
    protected String dispenseUnit;
    protected double dosageQty;
    protected String dosageUnit;
    protected String doseType;
    protected String drNote;
    protected String dspnDate;
    protected String dspnKind;
    protected String dspnUser;
    protected String endDttm;
    protected String execDeptCode;
    protected String finalType;
    protected String freqCode;
    protected String giveboxFlg;
    protected String goodsDesc;
    protected String hideFlg;
    protected int ibsCaseNoSeq;
    protected int ibsSeqNo;
    protected int injpracGroup;
    protected String intgmedNo;
    protected String ipdNo;
    protected String isIntg;
    protected String linkNo;
    protected String linkmainFlg;
    protected String lisReDate;
    protected String lisReUser;
    protected double mediQty;
    protected String mediUnit;
    protected String mrNo;
    protected double nhiPrice;
    protected String nsExecCode;
    protected String nsExecDate;
    protected String nsExecDcCode;
    protected String nsExecDcDate;
    protected String nsUser;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String optitemCode;
    protected String orderCat1Code;
    protected String orderCode;
    protected String orderDate;
    protected String orderDeptCode;
    protected String orderDesc;
    protected String orderDrCode;
    protected String orderNo;
    protected int orderSeq;
    protected String ordersetCode;
    protected String ordersetGroupNo;
    protected double ownAmt;
    protected double ownPrice;
    protected int packageAmt;
    protected String parentCaseNo;
    protected String parentOrderNo;
    protected int parentOrderSeq;
    protected String parentStartDttm;
    protected String phaCheckCode;
    protected String phaCheckDate;
    protected String phaDispenseCode;
    protected String phaDispenseDate;
    protected String phaDispenseNo;
    protected String phaDosageCode;
    protected String phaDosageDate;
    protected String phaRetnCode;
    protected String phaRetnDate;
    protected String phaType;
    protected String prescriptNo;
    protected String presrtNo;
    protected String regionCode;
    protected double returnQty1;
    protected double returnQty2;
    protected double returnQty3;
    protected String routeCode;
    protected String rpttypeCode;
    protected double rtnDosageQty;
    protected String rtnDosageUnit;
    protected String rtnNo;
    protected int rtnNoSeq;
    protected String rxNo;
    protected String sendDctDate;
    protected String sendDctUser;
    protected String sendOrgDate;
    protected String sendOrgUser;
    protected String sendatcDttm;
    protected String sendatcFlg;
    protected String serviceLevel;
    protected String setmainFlg;
    @XmlElement(nillable = true)
    protected List<SpcOdiDspnd> spcOdiDspnds;
    protected String specification;
    protected String startDttm;
    protected String stationCode;
    protected int takeDays;
    protected String takemedNo;
    protected String takemedOrg;
    protected double totAmt;
    protected String transmitRsnCode;
    protected String turnEslId;
    protected String urgentFlg;
    protected double verifyinPrice1;
    protected double verifyinPrice2;
    protected double verifyinPrice3;
    protected String vsDrCode;

    /**
     * Gets the value of the acumOutboundQty property.
     * 
     */
    public double getAcumOutboundQty() {
        return acumOutboundQty;
    }

    /**
     * Sets the value of the acumOutboundQty property.
     * 
     */
    public void setAcumOutboundQty(double value) {
        this.acumOutboundQty = value;
    }

    /**
     * Gets the value of the agencyOrgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgencyOrgCode() {
        return agencyOrgCode;
    }

    /**
     * Sets the value of the agencyOrgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgencyOrgCode(String value) {
        this.agencyOrgCode = value;
    }

    /**
     * Gets the value of the antibioticCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAntibioticCode() {
        return antibioticCode;
    }

    /**
     * Sets the value of the antibioticCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAntibioticCode(String value) {
        this.antibioticCode = value;
    }

    /**
     * Gets the value of the atcFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtcFlg() {
        return atcFlg;
    }

    /**
     * Sets the value of the atcFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtcFlg(String value) {
        this.atcFlg = value;
    }

    /**
     * Gets the value of the barCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * Sets the value of the barCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCode(String value) {
        this.barCode = value;
    }

    /**
     * Gets the value of the batchSeq1 property.
     * 
     */
    public int getBatchSeq1() {
        return batchSeq1;
    }

    /**
     * Sets the value of the batchSeq1 property.
     * 
     */
    public void setBatchSeq1(int value) {
        this.batchSeq1 = value;
    }

    /**
     * Gets the value of the batchSeq2 property.
     * 
     */
    public int getBatchSeq2() {
        return batchSeq2;
    }

    /**
     * Sets the value of the batchSeq2 property.
     * 
     */
    public void setBatchSeq2(int value) {
        this.batchSeq2 = value;
    }

    /**
     * Gets the value of the batchSeq3 property.
     * 
     */
    public int getBatchSeq3() {
        return batchSeq3;
    }

    /**
     * Sets the value of the batchSeq3 property.
     * 
     */
    public void setBatchSeq3(int value) {
        this.batchSeq3 = value;
    }

    /**
     * Gets the value of the bedNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBedNo() {
        return bedNo;
    }

    /**
     * Sets the value of the bedNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBedNo(String value) {
        this.bedNo = value;
    }

    /**
     * Gets the value of the billFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillFlg() {
        return billFlg;
    }

    /**
     * Sets the value of the billFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillFlg(String value) {
        this.billFlg = value;
    }

    /**
     * Gets the value of the boxEslId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoxEslId() {
        return boxEslId;
    }

    /**
     * Sets the value of the boxEslId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoxEslId(String value) {
        this.boxEslId = value;
    }

    /**
     * Gets the value of the cancelDosageQty property.
     * 
     */
    public double getCancelDosageQty() {
        return cancelDosageQty;
    }

    /**
     * Sets the value of the cancelDosageQty property.
     * 
     */
    public void setCancelDosageQty(double value) {
        this.cancelDosageQty = value;
    }

    /**
     * Gets the value of the cancelrsnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancelrsnCode() {
        return cancelrsnCode;
    }

    /**
     * Sets the value of the cancelrsnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelrsnCode(String value) {
        this.cancelrsnCode = value;
    }

    /**
     * Gets the value of the caseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * Sets the value of the caseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseNo(String value) {
        this.caseNo = value;
    }

    /**
     * Gets the value of the cashierDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCashierDate() {
        return cashierDate;
    }

    /**
     * Sets the value of the cashierDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCashierDate(String value) {
        this.cashierDate = value;
    }

    /**
     * Gets the value of the cashierUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCashierUser() {
        return cashierUser;
    }

    /**
     * Sets the value of the cashierUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCashierUser(String value) {
        this.cashierUser = value;
    }

    /**
     * Gets the value of the cat1Type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCat1Type() {
        return cat1Type;
    }

    /**
     * Sets the value of the cat1Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCat1Type(String value) {
        this.cat1Type = value;
    }

    /**
     * Gets the value of the ctrldrugclassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtrldrugclassCode() {
        return ctrldrugclassCode;
    }

    /**
     * Sets the value of the ctrldrugclassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtrldrugclassCode(String value) {
        this.ctrldrugclassCode = value;
    }

    /**
     * Gets the value of the dcDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcDate() {
        return dcDate;
    }

    /**
     * Sets the value of the dcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcDate(String value) {
        this.dcDate = value;
    }

    /**
     * Gets the value of the dcDrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcDrCode() {
        return dcDrCode;
    }

    /**
     * Sets the value of the dcDrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcDrCode(String value) {
        this.dcDrCode = value;
    }

    /**
     * Gets the value of the dcNsCheckDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcNsCheckDate() {
        return dcNsCheckDate;
    }

    /**
     * Sets the value of the dcNsCheckDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcNsCheckDate(String value) {
        this.dcNsCheckDate = value;
    }

    /**
     * Gets the value of the dcTot property.
     * 
     */
    public double getDcTot() {
        return dcTot;
    }

    /**
     * Sets the value of the dcTot property.
     * 
     */
    public void setDcTot(double value) {
        this.dcTot = value;
    }

    /**
     * Gets the value of the dctTakeQty property.
     * 
     */
    public int getDctTakeQty() {
        return dctTakeQty;
    }

    /**
     * Sets the value of the dctTakeQty property.
     * 
     */
    public void setDctTakeQty(int value) {
        this.dctTakeQty = value;
    }

    /**
     * Gets the value of the dctagentCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDctagentCode() {
        return dctagentCode;
    }

    /**
     * Sets the value of the dctagentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDctagentCode(String value) {
        this.dctagentCode = value;
    }

    /**
     * Gets the value of the dctagentFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDctagentFlg() {
        return dctagentFlg;
    }

    /**
     * Sets the value of the dctagentFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDctagentFlg(String value) {
        this.dctagentFlg = value;
    }

    /**
     * Gets the value of the dctexcepCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDctexcepCode() {
        return dctexcepCode;
    }

    /**
     * Sets the value of the dctexcepCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDctexcepCode(String value) {
        this.dctexcepCode = value;
    }

    /**
     * Gets the value of the decoctCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoctCode() {
        return decoctCode;
    }

    /**
     * Sets the value of the decoctCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoctCode(String value) {
        this.decoctCode = value;
    }

    /**
     * Gets the value of the decoctDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoctDate() {
        return decoctDate;
    }

    /**
     * Sets the value of the decoctDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoctDate(String value) {
        this.decoctDate = value;
    }

    /**
     * Gets the value of the decoctRemark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoctRemark() {
        return decoctRemark;
    }

    /**
     * Sets the value of the decoctRemark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoctRemark(String value) {
        this.decoctRemark = value;
    }

    /**
     * Gets the value of the decoctUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecoctUser() {
        return decoctUser;
    }

    /**
     * Sets the value of the decoctUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecoctUser(String value) {
        this.decoctUser = value;
    }

    /**
     * Gets the value of the degreeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDegreeCode() {
        return degreeCode;
    }

    /**
     * Sets the value of the degreeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDegreeCode(String value) {
        this.degreeCode = value;
    }

    /**
     * Gets the value of the deptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * Sets the value of the deptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeptCode(String value) {
        this.deptCode = value;
    }

    /**
     * Gets the value of the discountRate property.
     * 
     */
    public double getDiscountRate() {
        return discountRate;
    }

    /**
     * Sets the value of the discountRate property.
     * 
     */
    public void setDiscountRate(double value) {
        this.discountRate = value;
    }

    /**
     * Gets the value of the dispenseEffDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseEffDate() {
        return dispenseEffDate;
    }

    /**
     * Sets the value of the dispenseEffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseEffDate(String value) {
        this.dispenseEffDate = value;
    }

    /**
     * Gets the value of the dispenseEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseEndDate() {
        return dispenseEndDate;
    }

    /**
     * Sets the value of the dispenseEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseEndDate(String value) {
        this.dispenseEndDate = value;
    }

    /**
     * Gets the value of the dispenseFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseFlg() {
        return dispenseFlg;
    }

    /**
     * Sets the value of the dispenseFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseFlg(String value) {
        this.dispenseFlg = value;
    }

    /**
     * Gets the value of the dispenseQty property.
     * 
     */
    public double getDispenseQty() {
        return dispenseQty;
    }

    /**
     * Sets the value of the dispenseQty property.
     * 
     */
    public void setDispenseQty(double value) {
        this.dispenseQty = value;
    }

    /**
     * Gets the value of the dispenseQty1 property.
     * 
     */
    public double getDispenseQty1() {
        return dispenseQty1;
    }

    /**
     * Sets the value of the dispenseQty1 property.
     * 
     */
    public void setDispenseQty1(double value) {
        this.dispenseQty1 = value;
    }

    /**
     * Gets the value of the dispenseQty2 property.
     * 
     */
    public double getDispenseQty2() {
        return dispenseQty2;
    }

    /**
     * Sets the value of the dispenseQty2 property.
     * 
     */
    public void setDispenseQty2(double value) {
        this.dispenseQty2 = value;
    }

    /**
     * Gets the value of the dispenseQty3 property.
     * 
     */
    public double getDispenseQty3() {
        return dispenseQty3;
    }

    /**
     * Sets the value of the dispenseQty3 property.
     * 
     */
    public void setDispenseQty3(double value) {
        this.dispenseQty3 = value;
    }

    /**
     * Gets the value of the dispenseUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseUnit() {
        return dispenseUnit;
    }

    /**
     * Sets the value of the dispenseUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseUnit(String value) {
        this.dispenseUnit = value;
    }

    /**
     * Gets the value of the dosageQty property.
     * 
     */
    public double getDosageQty() {
        return dosageQty;
    }

    /**
     * Sets the value of the dosageQty property.
     * 
     */
    public void setDosageQty(double value) {
        this.dosageQty = value;
    }

    /**
     * Gets the value of the dosageUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDosageUnit() {
        return dosageUnit;
    }

    /**
     * Sets the value of the dosageUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDosageUnit(String value) {
        this.dosageUnit = value;
    }

    /**
     * Gets the value of the doseType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDoseType() {
        return doseType;
    }

    /**
     * Sets the value of the doseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDoseType(String value) {
        this.doseType = value;
    }

    /**
     * Gets the value of the drNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrNote() {
        return drNote;
    }

    /**
     * Sets the value of the drNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrNote(String value) {
        this.drNote = value;
    }

    /**
     * Gets the value of the dspnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDspnDate() {
        return dspnDate;
    }

    /**
     * Sets the value of the dspnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDspnDate(String value) {
        this.dspnDate = value;
    }

    /**
     * Gets the value of the dspnKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDspnKind() {
        return dspnKind;
    }

    /**
     * Sets the value of the dspnKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDspnKind(String value) {
        this.dspnKind = value;
    }

    /**
     * Gets the value of the dspnUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDspnUser() {
        return dspnUser;
    }

    /**
     * Sets the value of the dspnUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDspnUser(String value) {
        this.dspnUser = value;
    }

    /**
     * Gets the value of the endDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDttm() {
        return endDttm;
    }

    /**
     * Sets the value of the endDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDttm(String value) {
        this.endDttm = value;
    }

    /**
     * Gets the value of the execDeptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecDeptCode() {
        return execDeptCode;
    }

    /**
     * Sets the value of the execDeptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecDeptCode(String value) {
        this.execDeptCode = value;
    }

    /**
     * Gets the value of the finalType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinalType() {
        return finalType;
    }

    /**
     * Sets the value of the finalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinalType(String value) {
        this.finalType = value;
    }

    /**
     * Gets the value of the freqCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreqCode() {
        return freqCode;
    }

    /**
     * Sets the value of the freqCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreqCode(String value) {
        this.freqCode = value;
    }

    /**
     * Gets the value of the giveboxFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGiveboxFlg() {
        return giveboxFlg;
    }

    /**
     * Sets the value of the giveboxFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGiveboxFlg(String value) {
        this.giveboxFlg = value;
    }

    /**
     * Gets the value of the goodsDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoodsDesc() {
        return goodsDesc;
    }

    /**
     * Sets the value of the goodsDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoodsDesc(String value) {
        this.goodsDesc = value;
    }

    /**
     * Gets the value of the hideFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHideFlg() {
        return hideFlg;
    }

    /**
     * Sets the value of the hideFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHideFlg(String value) {
        this.hideFlg = value;
    }

    /**
     * Gets the value of the ibsCaseNoSeq property.
     * 
     */
    public int getIbsCaseNoSeq() {
        return ibsCaseNoSeq;
    }

    /**
     * Sets the value of the ibsCaseNoSeq property.
     * 
     */
    public void setIbsCaseNoSeq(int value) {
        this.ibsCaseNoSeq = value;
    }

    /**
     * Gets the value of the ibsSeqNo property.
     * 
     */
    public int getIbsSeqNo() {
        return ibsSeqNo;
    }

    /**
     * Sets the value of the ibsSeqNo property.
     * 
     */
    public void setIbsSeqNo(int value) {
        this.ibsSeqNo = value;
    }

    /**
     * Gets the value of the injpracGroup property.
     * 
     */
    public int getInjpracGroup() {
        return injpracGroup;
    }

    /**
     * Sets the value of the injpracGroup property.
     * 
     */
    public void setInjpracGroup(int value) {
        this.injpracGroup = value;
    }

    /**
     * Gets the value of the intgmedNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntgmedNo() {
        return intgmedNo;
    }

    /**
     * Sets the value of the intgmedNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntgmedNo(String value) {
        this.intgmedNo = value;
    }

    /**
     * Gets the value of the ipdNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpdNo() {
        return ipdNo;
    }

    /**
     * Sets the value of the ipdNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpdNo(String value) {
        this.ipdNo = value;
    }

    /**
     * Gets the value of the isIntg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsIntg() {
        return isIntg;
    }

    /**
     * Sets the value of the isIntg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsIntg(String value) {
        this.isIntg = value;
    }

    /**
     * Gets the value of the linkNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkNo() {
        return linkNo;
    }

    /**
     * Sets the value of the linkNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkNo(String value) {
        this.linkNo = value;
    }

    /**
     * Gets the value of the linkmainFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkmainFlg() {
        return linkmainFlg;
    }

    /**
     * Sets the value of the linkmainFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkmainFlg(String value) {
        this.linkmainFlg = value;
    }

    /**
     * Gets the value of the lisReDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLisReDate() {
        return lisReDate;
    }

    /**
     * Sets the value of the lisReDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLisReDate(String value) {
        this.lisReDate = value;
    }

    /**
     * Gets the value of the lisReUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLisReUser() {
        return lisReUser;
    }

    /**
     * Sets the value of the lisReUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLisReUser(String value) {
        this.lisReUser = value;
    }

    /**
     * Gets the value of the mediQty property.
     * 
     */
    public double getMediQty() {
        return mediQty;
    }

    /**
     * Sets the value of the mediQty property.
     * 
     */
    public void setMediQty(double value) {
        this.mediQty = value;
    }

    /**
     * Gets the value of the mediUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediUnit() {
        return mediUnit;
    }

    /**
     * Sets the value of the mediUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediUnit(String value) {
        this.mediUnit = value;
    }

    /**
     * Gets the value of the mrNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMrNo() {
        return mrNo;
    }

    /**
     * Sets the value of the mrNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMrNo(String value) {
        this.mrNo = value;
    }

    /**
     * Gets the value of the nhiPrice property.
     * 
     */
    public double getNhiPrice() {
        return nhiPrice;
    }

    /**
     * Sets the value of the nhiPrice property.
     * 
     */
    public void setNhiPrice(double value) {
        this.nhiPrice = value;
    }

    /**
     * Gets the value of the nsExecCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecCode() {
        return nsExecCode;
    }

    /**
     * Sets the value of the nsExecCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecCode(String value) {
        this.nsExecCode = value;
    }

    /**
     * Gets the value of the nsExecDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDate() {
        return nsExecDate;
    }

    /**
     * Sets the value of the nsExecDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDate(String value) {
        this.nsExecDate = value;
    }

    /**
     * Gets the value of the nsExecDcCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDcCode() {
        return nsExecDcCode;
    }

    /**
     * Sets the value of the nsExecDcCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDcCode(String value) {
        this.nsExecDcCode = value;
    }

    /**
     * Gets the value of the nsExecDcDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDcDate() {
        return nsExecDcDate;
    }

    /**
     * Sets the value of the nsExecDcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDcDate(String value) {
        this.nsExecDcDate = value;
    }

    /**
     * Gets the value of the nsUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsUser() {
        return nsUser;
    }

    /**
     * Sets the value of the nsUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsUser(String value) {
        this.nsUser = value;
    }

    /**
     * Gets the value of the optDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptDate() {
        return optDate;
    }

    /**
     * Sets the value of the optDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptDate(String value) {
        this.optDate = value;
    }

    /**
     * Gets the value of the optTerm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptTerm() {
        return optTerm;
    }

    /**
     * Sets the value of the optTerm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptTerm(String value) {
        this.optTerm = value;
    }

    /**
     * Gets the value of the optUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * Sets the value of the optUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptUser(String value) {
        this.optUser = value;
    }

    /**
     * Gets the value of the optitemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptitemCode() {
        return optitemCode;
    }

    /**
     * Sets the value of the optitemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptitemCode(String value) {
        this.optitemCode = value;
    }

    /**
     * Gets the value of the orderCat1Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderCat1Code() {
        return orderCat1Code;
    }

    /**
     * Sets the value of the orderCat1Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderCat1Code(String value) {
        this.orderCat1Code = value;
    }

    /**
     * Gets the value of the orderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * Sets the value of the orderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderCode(String value) {
        this.orderCode = value;
    }

    /**
     * Gets the value of the orderDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDate(String value) {
        this.orderDate = value;
    }

    /**
     * Gets the value of the orderDeptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDeptCode() {
        return orderDeptCode;
    }

    /**
     * Sets the value of the orderDeptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDeptCode(String value) {
        this.orderDeptCode = value;
    }

    /**
     * Gets the value of the orderDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDesc() {
        return orderDesc;
    }

    /**
     * Sets the value of the orderDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDesc(String value) {
        this.orderDesc = value;
    }

    /**
     * Gets the value of the orderDrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDrCode() {
        return orderDrCode;
    }

    /**
     * Sets the value of the orderDrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDrCode(String value) {
        this.orderDrCode = value;
    }

    /**
     * Gets the value of the orderNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * Sets the value of the orderNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    /**
     * Gets the value of the orderSeq property.
     * 
     */
    public int getOrderSeq() {
        return orderSeq;
    }

    /**
     * Sets the value of the orderSeq property.
     * 
     */
    public void setOrderSeq(int value) {
        this.orderSeq = value;
    }

    /**
     * Gets the value of the ordersetCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrdersetCode() {
        return ordersetCode;
    }

    /**
     * Sets the value of the ordersetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrdersetCode(String value) {
        this.ordersetCode = value;
    }

    /**
     * Gets the value of the ordersetGroupNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrdersetGroupNo() {
        return ordersetGroupNo;
    }

    /**
     * Sets the value of the ordersetGroupNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrdersetGroupNo(String value) {
        this.ordersetGroupNo = value;
    }

    /**
     * Gets the value of the ownAmt property.
     * 
     */
    public double getOwnAmt() {
        return ownAmt;
    }

    /**
     * Sets the value of the ownAmt property.
     * 
     */
    public void setOwnAmt(double value) {
        this.ownAmt = value;
    }

    /**
     * Gets the value of the ownPrice property.
     * 
     */
    public double getOwnPrice() {
        return ownPrice;
    }

    /**
     * Sets the value of the ownPrice property.
     * 
     */
    public void setOwnPrice(double value) {
        this.ownPrice = value;
    }

    /**
     * Gets the value of the packageAmt property.
     * 
     */
    public int getPackageAmt() {
        return packageAmt;
    }

    /**
     * Sets the value of the packageAmt property.
     * 
     */
    public void setPackageAmt(int value) {
        this.packageAmt = value;
    }

    /**
     * Gets the value of the parentCaseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentCaseNo() {
        return parentCaseNo;
    }

    /**
     * Sets the value of the parentCaseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentCaseNo(String value) {
        this.parentCaseNo = value;
    }

    /**
     * Gets the value of the parentOrderNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentOrderNo() {
        return parentOrderNo;
    }

    /**
     * Sets the value of the parentOrderNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentOrderNo(String value) {
        this.parentOrderNo = value;
    }

    /**
     * Gets the value of the parentOrderSeq property.
     * 
     */
    public int getParentOrderSeq() {
        return parentOrderSeq;
    }

    /**
     * Sets the value of the parentOrderSeq property.
     * 
     */
    public void setParentOrderSeq(int value) {
        this.parentOrderSeq = value;
    }

    /**
     * Gets the value of the parentStartDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentStartDttm() {
        return parentStartDttm;
    }

    /**
     * Sets the value of the parentStartDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentStartDttm(String value) {
        this.parentStartDttm = value;
    }

    /**
     * Gets the value of the phaCheckCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaCheckCode() {
        return phaCheckCode;
    }

    /**
     * Sets the value of the phaCheckCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaCheckCode(String value) {
        this.phaCheckCode = value;
    }

    /**
     * Gets the value of the phaCheckDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaCheckDate() {
        return phaCheckDate;
    }

    /**
     * Sets the value of the phaCheckDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaCheckDate(String value) {
        this.phaCheckDate = value;
    }

    /**
     * Gets the value of the phaDispenseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseCode() {
        return phaDispenseCode;
    }

    /**
     * Sets the value of the phaDispenseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseCode(String value) {
        this.phaDispenseCode = value;
    }

    /**
     * Gets the value of the phaDispenseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseDate() {
        return phaDispenseDate;
    }

    /**
     * Sets the value of the phaDispenseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseDate(String value) {
        this.phaDispenseDate = value;
    }

    /**
     * Gets the value of the phaDispenseNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDispenseNo() {
        return phaDispenseNo;
    }

    /**
     * Sets the value of the phaDispenseNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDispenseNo(String value) {
        this.phaDispenseNo = value;
    }

    /**
     * Gets the value of the phaDosageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageCode() {
        return phaDosageCode;
    }

    /**
     * Sets the value of the phaDosageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageCode(String value) {
        this.phaDosageCode = value;
    }

    /**
     * Gets the value of the phaDosageDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaDosageDate() {
        return phaDosageDate;
    }

    /**
     * Sets the value of the phaDosageDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaDosageDate(String value) {
        this.phaDosageDate = value;
    }

    /**
     * Gets the value of the phaRetnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnCode() {
        return phaRetnCode;
    }

    /**
     * Sets the value of the phaRetnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnCode(String value) {
        this.phaRetnCode = value;
    }

    /**
     * Gets the value of the phaRetnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaRetnDate() {
        return phaRetnDate;
    }

    /**
     * Sets the value of the phaRetnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaRetnDate(String value) {
        this.phaRetnDate = value;
    }

    /**
     * Gets the value of the phaType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaType() {
        return phaType;
    }

    /**
     * Sets the value of the phaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaType(String value) {
        this.phaType = value;
    }

    /**
     * Gets the value of the prescriptNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescriptNo() {
        return prescriptNo;
    }

    /**
     * Sets the value of the prescriptNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescriptNo(String value) {
        this.prescriptNo = value;
    }

    /**
     * Gets the value of the presrtNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPresrtNo() {
        return presrtNo;
    }

    /**
     * Sets the value of the presrtNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPresrtNo(String value) {
        this.presrtNo = value;
    }

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionCode(String value) {
        this.regionCode = value;
    }

    /**
     * Gets the value of the returnQty1 property.
     * 
     */
    public double getReturnQty1() {
        return returnQty1;
    }

    /**
     * Sets the value of the returnQty1 property.
     * 
     */
    public void setReturnQty1(double value) {
        this.returnQty1 = value;
    }

    /**
     * Gets the value of the returnQty2 property.
     * 
     */
    public double getReturnQty2() {
        return returnQty2;
    }

    /**
     * Sets the value of the returnQty2 property.
     * 
     */
    public void setReturnQty2(double value) {
        this.returnQty2 = value;
    }

    /**
     * Gets the value of the returnQty3 property.
     * 
     */
    public double getReturnQty3() {
        return returnQty3;
    }

    /**
     * Sets the value of the returnQty3 property.
     * 
     */
    public void setReturnQty3(double value) {
        this.returnQty3 = value;
    }

    /**
     * Gets the value of the routeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRouteCode() {
        return routeCode;
    }

    /**
     * Sets the value of the routeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRouteCode(String value) {
        this.routeCode = value;
    }

    /**
     * Gets the value of the rpttypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRpttypeCode() {
        return rpttypeCode;
    }

    /**
     * Sets the value of the rpttypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRpttypeCode(String value) {
        this.rpttypeCode = value;
    }

    /**
     * Gets the value of the rtnDosageQty property.
     * 
     */
    public double getRtnDosageQty() {
        return rtnDosageQty;
    }

    /**
     * Sets the value of the rtnDosageQty property.
     * 
     */
    public void setRtnDosageQty(double value) {
        this.rtnDosageQty = value;
    }

    /**
     * Gets the value of the rtnDosageUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRtnDosageUnit() {
        return rtnDosageUnit;
    }

    /**
     * Sets the value of the rtnDosageUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRtnDosageUnit(String value) {
        this.rtnDosageUnit = value;
    }

    /**
     * Gets the value of the rtnNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRtnNo() {
        return rtnNo;
    }

    /**
     * Sets the value of the rtnNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRtnNo(String value) {
        this.rtnNo = value;
    }

    /**
     * Gets the value of the rtnNoSeq property.
     * 
     */
    public int getRtnNoSeq() {
        return rtnNoSeq;
    }

    /**
     * Sets the value of the rtnNoSeq property.
     * 
     */
    public void setRtnNoSeq(int value) {
        this.rtnNoSeq = value;
    }

    /**
     * Gets the value of the rxNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRxNo() {
        return rxNo;
    }

    /**
     * Sets the value of the rxNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRxNo(String value) {
        this.rxNo = value;
    }

    /**
     * Gets the value of the sendDctDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendDctDate() {
        return sendDctDate;
    }

    /**
     * Sets the value of the sendDctDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendDctDate(String value) {
        this.sendDctDate = value;
    }

    /**
     * Gets the value of the sendDctUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendDctUser() {
        return sendDctUser;
    }

    /**
     * Sets the value of the sendDctUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendDctUser(String value) {
        this.sendDctUser = value;
    }

    /**
     * Gets the value of the sendOrgDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendOrgDate() {
        return sendOrgDate;
    }

    /**
     * Sets the value of the sendOrgDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendOrgDate(String value) {
        this.sendOrgDate = value;
    }

    /**
     * Gets the value of the sendOrgUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendOrgUser() {
        return sendOrgUser;
    }

    /**
     * Sets the value of the sendOrgUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendOrgUser(String value) {
        this.sendOrgUser = value;
    }

    /**
     * Gets the value of the sendatcDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendatcDttm() {
        return sendatcDttm;
    }

    /**
     * Sets the value of the sendatcDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendatcDttm(String value) {
        this.sendatcDttm = value;
    }

    /**
     * Gets the value of the sendatcFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendatcFlg() {
        return sendatcFlg;
    }

    /**
     * Sets the value of the sendatcFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendatcFlg(String value) {
        this.sendatcFlg = value;
    }

    /**
     * Gets the value of the serviceLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceLevel() {
        return serviceLevel;
    }

    /**
     * Sets the value of the serviceLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceLevel(String value) {
        this.serviceLevel = value;
    }

    /**
     * Gets the value of the setmainFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetmainFlg() {
        return setmainFlg;
    }

    /**
     * Sets the value of the setmainFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetmainFlg(String value) {
        this.setmainFlg = value;
    }

    /**
     * Gets the value of the spcOdiDspnds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcOdiDspnds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcOdiDspnds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcOdiDspnd }
     * 
     * 
     */
    public List<SpcOdiDspnd> getSpcOdiDspnds() {
        if (spcOdiDspnds == null) {
            spcOdiDspnds = new ArrayList<SpcOdiDspnd>();
        }
        return this.spcOdiDspnds;
    }

    /**
     * Gets the value of the specification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * Sets the value of the specification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecification(String value) {
        this.specification = value;
    }

    /**
     * Gets the value of the startDttm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDttm() {
        return startDttm;
    }

    /**
     * Sets the value of the startDttm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDttm(String value) {
        this.startDttm = value;
    }

    /**
     * Gets the value of the stationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * Sets the value of the stationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStationCode(String value) {
        this.stationCode = value;
    }

    /**
     * Gets the value of the takeDays property.
     * 
     */
    public int getTakeDays() {
        return takeDays;
    }

    /**
     * Sets the value of the takeDays property.
     * 
     */
    public void setTakeDays(int value) {
        this.takeDays = value;
    }

    /**
     * Gets the value of the takemedNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTakemedNo() {
        return takemedNo;
    }

    /**
     * Sets the value of the takemedNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTakemedNo(String value) {
        this.takemedNo = value;
    }

    /**
     * Gets the value of the takemedOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTakemedOrg() {
        return takemedOrg;
    }

    /**
     * Sets the value of the takemedOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTakemedOrg(String value) {
        this.takemedOrg = value;
    }

    /**
     * Gets the value of the totAmt property.
     * 
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * Sets the value of the totAmt property.
     * 
     */
    public void setTotAmt(double value) {
        this.totAmt = value;
    }

    /**
     * Gets the value of the transmitRsnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransmitRsnCode() {
        return transmitRsnCode;
    }

    /**
     * Sets the value of the transmitRsnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransmitRsnCode(String value) {
        this.transmitRsnCode = value;
    }

    /**
     * Gets the value of the turnEslId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTurnEslId() {
        return turnEslId;
    }

    /**
     * Sets the value of the turnEslId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTurnEslId(String value) {
        this.turnEslId = value;
    }

    /**
     * Gets the value of the urgentFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrgentFlg() {
        return urgentFlg;
    }

    /**
     * Sets the value of the urgentFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrgentFlg(String value) {
        this.urgentFlg = value;
    }

    /**
     * Gets the value of the verifyinPrice1 property.
     * 
     */
    public double getVerifyinPrice1() {
        return verifyinPrice1;
    }

    /**
     * Sets the value of the verifyinPrice1 property.
     * 
     */
    public void setVerifyinPrice1(double value) {
        this.verifyinPrice1 = value;
    }

    /**
     * Gets the value of the verifyinPrice2 property.
     * 
     */
    public double getVerifyinPrice2() {
        return verifyinPrice2;
    }

    /**
     * Sets the value of the verifyinPrice2 property.
     * 
     */
    public void setVerifyinPrice2(double value) {
        this.verifyinPrice2 = value;
    }

    /**
     * Gets the value of the verifyinPrice3 property.
     * 
     */
    public double getVerifyinPrice3() {
        return verifyinPrice3;
    }

    /**
     * Sets the value of the verifyinPrice3 property.
     * 
     */
    public void setVerifyinPrice3(double value) {
        this.verifyinPrice3 = value;
    }

    /**
     * Gets the value of the vsDrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVsDrCode() {
        return vsDrCode;
    }

    /**
     * Sets the value of the vsDrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVsDrCode(String value) {
        this.vsDrCode = value;
    }

}
