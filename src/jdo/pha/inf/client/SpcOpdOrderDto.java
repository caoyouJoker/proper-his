
package jdo.pha.inf.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOpdOrderDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOpdOrderDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agencyOrgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="atcFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchSeq1" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="batchSeq2" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="batchSeq3" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="billDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="businessNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="caseNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cat1Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="costAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="costCenterCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="counterNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ctrldrugclassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz2Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ctz3Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcDeptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcDrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcOrderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dctTakeQty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="dctagentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dctagentFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dctexcepCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctRemark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoctUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="degreeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="devCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountRate" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dispenseQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dispenseQty1" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dispenseQty2" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dispenseQty3" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dispenseUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dosageQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dosageUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="doseType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execDeptCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execDrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execDrDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="exmExecEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expensiveFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="finalType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="freqCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giveboxFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodsDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hexpCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hideFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inspayType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkmainFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="medApplyNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mediQty" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="mediUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nhiPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="nsExecCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsExecDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nsNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optTerm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optitemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCat1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ordersetCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ordersetGroupNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ownAmt" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="ownPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="packageTot" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="patName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaCheckDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDispenseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaDosageDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaRetnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phaType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prescriptNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="presrtNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="printFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="printNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="printtypeflgInfant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="psyFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receiptNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="releaseFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rexpCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="routeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rpttypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rxNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rxType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendDctDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendDctUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendOrgDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendOrgUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendatcDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seqNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="setmainFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sexType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="specification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="takeDays" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="temporaryFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tradeEngDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urgentFlg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verifyinPrice1" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="verifyinPrice2" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="verifyinPrice3" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOpdOrderDto", propOrder = {
    "admType",
    "agencyOrgCode",
    "arAmt",
    "atcFlg",
    "batchSeq1",
    "batchSeq2",
    "batchSeq3",
    "billDate",
    "billFlg",
    "billType",
    "billUser",
    "birthDate",
    "businessNo",
    "caseNo",
    "cat1Type",
    "contractCode",
    "costAmt",
    "costCenterCode",
    "counterNo",
    "ctrldrugclassCode",
    "ctz1Code",
    "ctz2Code",
    "ctz3Code",
    "dcDeptCode",
    "dcDrCode",
    "dcOrderDate",
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
    "devCode",
    "discountRate",
    "dispenseQty",
    "dispenseQty1",
    "dispenseQty2",
    "dispenseQty3",
    "dispenseUnit",
    "dosageQty",
    "dosageUnit",
    "doseType",
    "drCode",
    "drNote",
    "execDeptCode",
    "execDrCode",
    "execDrDesc",
    "execFlg",
    "exmExecEndDate",
    "expensiveFlg",
    "fileNo",
    "finalType",
    "freqCode",
    "giveboxFlg",
    "goodsDesc",
    "hexpCode",
    "hideFlg",
    "inspayType",
    "linkNo",
    "linkmainFlg",
    "medApplyNo",
    "mediQty",
    "mediUnit",
    "mrCode",
    "mrNo",
    "nhiPrice",
    "nsExecCode",
    "nsExecDate",
    "nsExecDept",
    "nsNote",
    "optDate",
    "optTerm",
    "optUser",
    "optitemCode",
    "orderCat1Code",
    "orderCode",
    "orderDate",
    "orderDesc",
    "ordersetCode",
    "ordersetGroupNo",
    "ownAmt",
    "ownPrice",
    "packageTot",
    "patName",
    "phaCheckCode",
    "phaCheckDate",
    "phaDispenseCode",
    "phaDispenseDate",
    "phaDosageCode",
    "phaDosageDate",
    "phaRetnCode",
    "phaRetnDate",
    "phaType",
    "prescriptNo",
    "presrtNo",
    "printFlg",
    "printNo",
    "printtypeflgInfant",
    "psyFlg",
    "receiptFlg",
    "receiptNo",
    "regionCode",
    "releaseFlg",
    "requestFlg",
    "requestNo",
    "rexpCode",
    "routeCode",
    "rpttypeCode",
    "rxNo",
    "rxType",
    "sendDctDate",
    "sendDctUser",
    "sendOrgDate",
    "sendOrgUser",
    "sendatcDate",
    "seqNo",
    "setmainFlg",
    "sexType",
    "specification",
    "takeDays",
    "temporaryFlg",
    "tradeEngDesc",
    "urgentFlg",
    "verifyinPrice1",
    "verifyinPrice2",
    "verifyinPrice3"
})
public class SpcOpdOrderDto {

    protected String admType;
    protected String agencyOrgCode;
    protected Double arAmt;
    protected String atcFlg;
    protected Integer batchSeq1;
    protected Integer batchSeq2;
    protected Integer batchSeq3;
    protected String billDate;
    protected String billFlg;
    protected String billType;
    protected String billUser;
    protected String birthDate;
    protected String businessNo;
    protected String caseNo;
    protected String cat1Type;
    protected String contractCode;
    protected Double costAmt;
    protected String costCenterCode;
    protected Integer counterNo;
    protected String ctrldrugclassCode;
    protected String ctz1Code;
    protected String ctz2Code;
    protected String ctz3Code;
    protected String dcDeptCode;
    protected String dcDrCode;
    protected String dcOrderDate;
    protected Integer dctTakeQty;
    protected String dctagentCode;
    protected String dctagentFlg;
    protected String dctexcepCode;
    protected String decoctCode;
    protected String decoctDate;
    protected String decoctRemark;
    protected String decoctUser;
    protected String degreeCode;
    protected String deptCode;
    protected String devCode;
    protected Double discountRate;
    protected Double dispenseQty;
    protected Double dispenseQty1;
    protected Double dispenseQty2;
    protected Double dispenseQty3;
    protected String dispenseUnit;
    protected Double dosageQty;
    protected String dosageUnit;
    protected String doseType;
    protected String drCode;
    protected String drNote;
    protected String execDeptCode;
    protected String execDrCode;
    protected String execDrDesc;
    protected String execFlg;
    protected String exmExecEndDate;
    protected String expensiveFlg;
    protected Integer fileNo;
    protected String finalType;
    protected String freqCode;
    protected String giveboxFlg;
    protected String goodsDesc;
    protected String hexpCode;
    protected String hideFlg;
    protected String inspayType;
    protected String linkNo;
    protected String linkmainFlg;
    protected String medApplyNo;
    protected Double mediQty;
    protected String mediUnit;
    protected String mrCode;
    protected String mrNo;
    protected Double nhiPrice;
    protected String nsExecCode;
    protected String nsExecDate;
    protected String nsExecDept;
    protected String nsNote;
    protected String optDate;
    protected String optTerm;
    protected String optUser;
    protected String optitemCode;
    protected String orderCat1Code;
    protected String orderCode;
    protected String orderDate;
    protected String orderDesc;
    protected String ordersetCode;
    protected Integer ordersetGroupNo;
    protected Double ownAmt;
    protected Double ownPrice;
    protected Integer packageTot;
    protected String patName;
    protected String phaCheckCode;
    protected String phaCheckDate;
    protected String phaDispenseCode;
    protected String phaDispenseDate;
    protected String phaDosageCode;
    protected String phaDosageDate;
    protected String phaRetnCode;
    protected String phaRetnDate;
    protected String phaType;
    protected Integer prescriptNo;
    protected String presrtNo;
    protected String printFlg;
    protected String printNo;
    protected String printtypeflgInfant;
    protected String psyFlg;
    protected String receiptFlg;
    protected String receiptNo;
    protected String regionCode;
    protected String releaseFlg;
    protected String requestFlg;
    protected String requestNo;
    protected String rexpCode;
    protected String routeCode;
    protected String rpttypeCode;
    protected String rxNo;
    protected String rxType;
    protected String sendDctDate;
    protected String sendDctUser;
    protected String sendOrgDate;
    protected String sendOrgUser;
    protected String sendatcDate;
    protected Integer seqNo;
    protected String setmainFlg;
    protected String sexType;
    protected String specification;
    protected Integer takeDays;
    protected String temporaryFlg;
    protected String tradeEngDesc;
    protected String urgentFlg;
    protected Double verifyinPrice1;
    protected Double verifyinPrice2;
    protected Double verifyinPrice3;

    /**
     * Gets the value of the admType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * Sets the value of the admType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmType(String value) {
        this.admType = value;
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
     * Gets the value of the arAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getArAmt() {
        return arAmt;
    }

    /**
     * Sets the value of the arAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setArAmt(Double value) {
        this.arAmt = value;
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
     * Gets the value of the batchSeq1 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSeq1() {
        return batchSeq1;
    }

    /**
     * Sets the value of the batchSeq1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSeq1(Integer value) {
        this.batchSeq1 = value;
    }

    /**
     * Gets the value of the batchSeq2 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSeq2() {
        return batchSeq2;
    }

    /**
     * Sets the value of the batchSeq2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSeq2(Integer value) {
        this.batchSeq2 = value;
    }

    /**
     * Gets the value of the batchSeq3 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBatchSeq3() {
        return batchSeq3;
    }

    /**
     * Sets the value of the batchSeq3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBatchSeq3(Integer value) {
        this.batchSeq3 = value;
    }

    /**
     * Gets the value of the billDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillDate() {
        return billDate;
    }

    /**
     * Sets the value of the billDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillDate(String value) {
        this.billDate = value;
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
     * Gets the value of the billType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillType() {
        return billType;
    }

    /**
     * Sets the value of the billType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillType(String value) {
        this.billType = value;
    }

    /**
     * Gets the value of the billUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillUser() {
        return billUser;
    }

    /**
     * Sets the value of the billUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillUser(String value) {
        this.billUser = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthDate(String value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the businessNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessNo() {
        return businessNo;
    }

    /**
     * Sets the value of the businessNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessNo(String value) {
        this.businessNo = value;
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
     * Gets the value of the contractCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * Sets the value of the contractCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractCode(String value) {
        this.contractCode = value;
    }

    /**
     * Gets the value of the costAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCostAmt() {
        return costAmt;
    }

    /**
     * Sets the value of the costAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCostAmt(Double value) {
        this.costAmt = value;
    }

    /**
     * Gets the value of the costCenterCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCostCenterCode() {
        return costCenterCode;
    }

    /**
     * Sets the value of the costCenterCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCostCenterCode(String value) {
        this.costCenterCode = value;
    }

    /**
     * Gets the value of the counterNo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCounterNo() {
        return counterNo;
    }

    /**
     * Sets the value of the counterNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCounterNo(Integer value) {
        this.counterNo = value;
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
     * Gets the value of the ctz1Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz1Code() {
        return ctz1Code;
    }

    /**
     * Sets the value of the ctz1Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz1Code(String value) {
        this.ctz1Code = value;
    }

    /**
     * Gets the value of the ctz2Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz2Code() {
        return ctz2Code;
    }

    /**
     * Sets the value of the ctz2Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz2Code(String value) {
        this.ctz2Code = value;
    }

    /**
     * Gets the value of the ctz3Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtz3Code() {
        return ctz3Code;
    }

    /**
     * Sets the value of the ctz3Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtz3Code(String value) {
        this.ctz3Code = value;
    }

    /**
     * Gets the value of the dcDeptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcDeptCode() {
        return dcDeptCode;
    }

    /**
     * Sets the value of the dcDeptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcDeptCode(String value) {
        this.dcDeptCode = value;
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
     * Gets the value of the dcOrderDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDcOrderDate() {
        return dcOrderDate;
    }

    /**
     * Sets the value of the dcOrderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDcOrderDate(String value) {
        this.dcOrderDate = value;
    }

    /**
     * Gets the value of the dctTakeQty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDctTakeQty() {
        return dctTakeQty;
    }

    /**
     * Sets the value of the dctTakeQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDctTakeQty(Integer value) {
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
     * Gets the value of the devCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevCode() {
        return devCode;
    }

    /**
     * Sets the value of the devCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevCode(String value) {
        this.devCode = value;
    }

    /**
     * Gets the value of the discountRate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDiscountRate() {
        return discountRate;
    }

    /**
     * Sets the value of the discountRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDiscountRate(Double value) {
        this.discountRate = value;
    }

    /**
     * Gets the value of the dispenseQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDispenseQty() {
        return dispenseQty;
    }

    /**
     * Sets the value of the dispenseQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDispenseQty(Double value) {
        this.dispenseQty = value;
    }

    /**
     * Gets the value of the dispenseQty1 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDispenseQty1() {
        return dispenseQty1;
    }

    /**
     * Sets the value of the dispenseQty1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDispenseQty1(Double value) {
        this.dispenseQty1 = value;
    }

    /**
     * Gets the value of the dispenseQty2 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDispenseQty2() {
        return dispenseQty2;
    }

    /**
     * Sets the value of the dispenseQty2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDispenseQty2(Double value) {
        this.dispenseQty2 = value;
    }

    /**
     * Gets the value of the dispenseQty3 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDispenseQty3() {
        return dispenseQty3;
    }

    /**
     * Sets the value of the dispenseQty3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDispenseQty3(Double value) {
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
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDosageQty() {
        return dosageQty;
    }

    /**
     * Sets the value of the dosageQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDosageQty(Double value) {
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
     * Gets the value of the drCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrCode() {
        return drCode;
    }

    /**
     * Sets the value of the drCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrCode(String value) {
        this.drCode = value;
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
     * Gets the value of the execDrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecDrCode() {
        return execDrCode;
    }

    /**
     * Sets the value of the execDrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecDrCode(String value) {
        this.execDrCode = value;
    }

    /**
     * Gets the value of the execDrDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecDrDesc() {
        return execDrDesc;
    }

    /**
     * Sets the value of the execDrDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecDrDesc(String value) {
        this.execDrDesc = value;
    }

    /**
     * Gets the value of the execFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecFlg() {
        return execFlg;
    }

    /**
     * Sets the value of the execFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecFlg(String value) {
        this.execFlg = value;
    }

    /**
     * Gets the value of the exmExecEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExmExecEndDate() {
        return exmExecEndDate;
    }

    /**
     * Sets the value of the exmExecEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExmExecEndDate(String value) {
        this.exmExecEndDate = value;
    }

    /**
     * Gets the value of the expensiveFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpensiveFlg() {
        return expensiveFlg;
    }

    /**
     * Sets the value of the expensiveFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpensiveFlg(String value) {
        this.expensiveFlg = value;
    }

    /**
     * Gets the value of the fileNo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileNo() {
        return fileNo;
    }

    /**
     * Sets the value of the fileNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileNo(Integer value) {
        this.fileNo = value;
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
     * Gets the value of the hexpCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHexpCode() {
        return hexpCode;
    }

    /**
     * Sets the value of the hexpCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHexpCode(String value) {
        this.hexpCode = value;
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
     * Gets the value of the inspayType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInspayType() {
        return inspayType;
    }

    /**
     * Sets the value of the inspayType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInspayType(String value) {
        this.inspayType = value;
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
     * Gets the value of the medApplyNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedApplyNo() {
        return medApplyNo;
    }

    /**
     * Sets the value of the medApplyNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedApplyNo(String value) {
        this.medApplyNo = value;
    }

    /**
     * Gets the value of the mediQty property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMediQty() {
        return mediQty;
    }

    /**
     * Sets the value of the mediQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMediQty(Double value) {
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
     * Gets the value of the mrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMrCode() {
        return mrCode;
    }

    /**
     * Sets the value of the mrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMrCode(String value) {
        this.mrCode = value;
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
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getNhiPrice() {
        return nhiPrice;
    }

    /**
     * Sets the value of the nhiPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setNhiPrice(Double value) {
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
     * Gets the value of the nsExecDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsExecDept() {
        return nsExecDept;
    }

    /**
     * Sets the value of the nsExecDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsExecDept(String value) {
        this.nsExecDept = value;
    }

    /**
     * Gets the value of the nsNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsNote() {
        return nsNote;
    }

    /**
     * Sets the value of the nsNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsNote(String value) {
        this.nsNote = value;
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
     *     {@link Integer }
     *     
     */
    public Integer getOrdersetGroupNo() {
        return ordersetGroupNo;
    }

    /**
     * Sets the value of the ordersetGroupNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrdersetGroupNo(Integer value) {
        this.ordersetGroupNo = value;
    }

    /**
     * Gets the value of the ownAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOwnAmt() {
        return ownAmt;
    }

    /**
     * Sets the value of the ownAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOwnAmt(Double value) {
        this.ownAmt = value;
    }

    /**
     * Gets the value of the ownPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getOwnPrice() {
        return ownPrice;
    }

    /**
     * Sets the value of the ownPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setOwnPrice(Double value) {
        this.ownPrice = value;
    }

    /**
     * Gets the value of the packageTot property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPackageTot() {
        return packageTot;
    }

    /**
     * Sets the value of the packageTot property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPackageTot(Integer value) {
        this.packageTot = value;
    }

    /**
     * Gets the value of the patName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatName() {
        return patName;
    }

    /**
     * Sets the value of the patName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatName(String value) {
        this.patName = value;
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
     *     {@link Integer }
     *     
     */
    public Integer getPrescriptNo() {
        return prescriptNo;
    }

    /**
     * Sets the value of the prescriptNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrescriptNo(Integer value) {
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
     * Gets the value of the printFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrintFlg() {
        return printFlg;
    }

    /**
     * Sets the value of the printFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrintFlg(String value) {
        this.printFlg = value;
    }

    /**
     * Gets the value of the printNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrintNo() {
        return printNo;
    }

    /**
     * Sets the value of the printNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrintNo(String value) {
        this.printNo = value;
    }

    /**
     * Gets the value of the printtypeflgInfant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrinttypeflgInfant() {
        return printtypeflgInfant;
    }

    /**
     * Sets the value of the printtypeflgInfant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrinttypeflgInfant(String value) {
        this.printtypeflgInfant = value;
    }

    /**
     * Gets the value of the psyFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsyFlg() {
        return psyFlg;
    }

    /**
     * Sets the value of the psyFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsyFlg(String value) {
        this.psyFlg = value;
    }

    /**
     * Gets the value of the receiptFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptFlg() {
        return receiptFlg;
    }

    /**
     * Sets the value of the receiptFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptFlg(String value) {
        this.receiptFlg = value;
    }

    /**
     * Gets the value of the receiptNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * Sets the value of the receiptNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptNo(String value) {
        this.receiptNo = value;
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
     * Gets the value of the releaseFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReleaseFlg() {
        return releaseFlg;
    }

    /**
     * Sets the value of the releaseFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseFlg(String value) {
        this.releaseFlg = value;
    }

    /**
     * Gets the value of the requestFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestFlg() {
        return requestFlg;
    }

    /**
     * Sets the value of the requestFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestFlg(String value) {
        this.requestFlg = value;
    }

    /**
     * Gets the value of the requestNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestNo() {
        return requestNo;
    }

    /**
     * Sets the value of the requestNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestNo(String value) {
        this.requestNo = value;
    }

    /**
     * Gets the value of the rexpCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRexpCode() {
        return rexpCode;
    }

    /**
     * Sets the value of the rexpCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRexpCode(String value) {
        this.rexpCode = value;
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
     * Gets the value of the rxType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRxType() {
        return rxType;
    }

    /**
     * Sets the value of the rxType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRxType(String value) {
        this.rxType = value;
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
     * Gets the value of the sendatcDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendatcDate() {
        return sendatcDate;
    }

    /**
     * Sets the value of the sendatcDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendatcDate(String value) {
        this.sendatcDate = value;
    }

    /**
     * Gets the value of the seqNo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeqNo() {
        return seqNo;
    }

    /**
     * Sets the value of the seqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeqNo(Integer value) {
        this.seqNo = value;
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
     * Gets the value of the sexType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSexType() {
        return sexType;
    }

    /**
     * Sets the value of the sexType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSexType(String value) {
        this.sexType = value;
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
     * Gets the value of the takeDays property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTakeDays() {
        return takeDays;
    }

    /**
     * Sets the value of the takeDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTakeDays(Integer value) {
        this.takeDays = value;
    }

    /**
     * Gets the value of the temporaryFlg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemporaryFlg() {
        return temporaryFlg;
    }

    /**
     * Sets the value of the temporaryFlg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemporaryFlg(String value) {
        this.temporaryFlg = value;
    }

    /**
     * Gets the value of the tradeEngDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTradeEngDesc() {
        return tradeEngDesc;
    }

    /**
     * Sets the value of the tradeEngDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTradeEngDesc(String value) {
        this.tradeEngDesc = value;
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
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinPrice1() {
        return verifyinPrice1;
    }

    /**
     * Sets the value of the verifyinPrice1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinPrice1(Double value) {
        this.verifyinPrice1 = value;
    }

    /**
     * Gets the value of the verifyinPrice2 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinPrice2() {
        return verifyinPrice2;
    }

    /**
     * Sets the value of the verifyinPrice2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinPrice2(Double value) {
        this.verifyinPrice2 = value;
    }

    /**
     * Gets the value of the verifyinPrice3 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVerifyinPrice3() {
        return verifyinPrice3;
    }

    /**
     * Sets the value of the verifyinPrice3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVerifyinPrice3(Double value) {
        this.verifyinPrice3 = value;
    }

}
