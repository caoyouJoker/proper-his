
package jdo.opd.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for opdOrder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="opdOrder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ADM_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AGENCY_ORG_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AR_AMT" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ATC_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BATCH_SEQ1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="BATCH_SEQ2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="BATCH_SEQ3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="BILL_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BILL_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BILL_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BILL_USER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BIRTH_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BUSINESS_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CASE_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CAT1_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CONTRACT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COST_AMT" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="COST_CENTER_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COUNTER_NO" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="CTRLDRUGCLASS_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CTZ1_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CTZ2_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CTZ3_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DCTAGENT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DCTAGENT_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DCTEXCEP_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DCT_TAKE_QTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DC_DEPT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DC_DR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DC_ORDER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DECOCT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DECOCT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DECOCT_REMARK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DECOCT_USER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DEGREE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DEPT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DEV_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DISCOUNT_RATE" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DISPENSE_QTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DISPENSE_QTY1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DISPENSE_QTY2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DISPENSE_QTY3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DISPENSE_UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOSAGE_QTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="DOSAGE_UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOSE_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DR_NOTE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXEC_DEPT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXEC_DR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXEC_DR_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXEC_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXM_EXEC_END_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EXPENSIVE_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FILE_NO" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="FINAL_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FREQ_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GIVEBOX_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GOODS_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HEXP_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HIDE_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="INSPAY_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LINKMAIN_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LINK_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MEDI_QTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MEDI_UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MED_APPLY_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MR_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NHI_PRICE" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="NS_EXEC_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NS_EXEC_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NS_EXEC_DEPT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NS_NOTE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OPTITEM_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OPT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OPT_TERM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OPT_USER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ORDERSET_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ORDERSET_GROUP_NO" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ORDER_CAT1_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ORDER_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ORDER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ORDER_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OWN_AMT" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="OWN_PRICE" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PACKAGE_TOT" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PAT_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_CHECK_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_CHECK_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_DISPENSE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_DISPENSE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_DOSAGE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_DOSAGE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_RETN_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_RETN_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHA_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRESCRIPT_NO" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PRESRT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRINTTYPEFLG_INFANT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRINT_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRINT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PSY_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RECEIPT_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RECEIPT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REGION_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RELEASE_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REQUEST_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REQUEST_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REXP_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ROUTE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RPTTYPE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RX_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RX_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SENDATC_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEND_DCT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEND_DCT_USER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEND_ORG_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEND_ORG_USER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEQ_NO" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SETMAIN_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SEX_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SPECIFICATION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TAKE_DAYS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TEMPORARY_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TRADE_ENG_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="URGENT_FLG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VERIFYIN_PRICE1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="VERIFYIN_PRICE2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="VERIFYIN_PRICE3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "opdOrder", propOrder = {
    "admtype",
    "agencyorgcode",
    "aramt",
    "atcflg",
    "batchseq1",
    "batchseq2",
    "batchseq3",
    "billdate",
    "billflg",
    "billtype",
    "billuser",
    "birthdate",
    "businessno",
    "caseno",
    "cat1TYPE",
    "contractcode",
    "costamt",
    "costcentercode",
    "counterno",
    "ctrldrugclasscode",
    "ctz1CODE",
    "ctz2CODE",
    "ctz3CODE",
    "dctagentcode",
    "dctagentflg",
    "dctexcepcode",
    "dcttakeqty",
    "dcdeptcode",
    "dcdrcode",
    "dcorderdate",
    "decoctcode",
    "decoctdate",
    "decoctremark",
    "decoctuser",
    "degreecode",
    "deptcode",
    "devcode",
    "discountrate",
    "dispenseqty",
    "dispenseqty1",
    "dispenseqty2",
    "dispenseqty3",
    "dispenseunit",
    "dosageqty",
    "dosageunit",
    "dosetype",
    "drcode",
    "drnote",
    "execdeptcode",
    "execdrcode",
    "execdrdesc",
    "execflg",
    "exmexecenddate",
    "expensiveflg",
    "fileno",
    "finaltype",
    "freqcode",
    "giveboxflg",
    "goodsdesc",
    "hexpcode",
    "hideflg",
    "inspaytype",
    "linkmainflg",
    "linkno",
    "mediqty",
    "mediunit",
    "medapplyno",
    "mrcode",
    "mrno",
    "nhiprice",
    "nsexeccode",
    "nsexecdate",
    "nsexecdept",
    "nsnote",
    "optitemcode",
    "optdate",
    "optterm",
    "optuser",
    "ordersetcode",
    "ordersetgroupno",
    "ordercat1CODE",
    "ordercode",
    "orderdate",
    "orderdesc",
    "ownamt",
    "ownprice",
    "packagetot",
    "patname",
    "phacheckcode",
    "phacheckdate",
    "phadispensecode",
    "phadispensedate",
    "phadosagecode",
    "phadosagedate",
    "pharetncode",
    "pharetndate",
    "phatype",
    "prescriptno",
    "presrtno",
    "printtypeflginfant",
    "printflg",
    "printno",
    "psyflg",
    "receiptflg",
    "receiptno",
    "regioncode",
    "releaseflg",
    "requestflg",
    "requestno",
    "rexpcode",
    "routecode",
    "rpttypecode",
    "rxno",
    "rxtype",
    "sendatcdate",
    "senddctdate",
    "senddctuser",
    "sendorgdate",
    "sendorguser",
    "seqno",
    "setmainflg",
    "sextype",
    "specification",
    "takedays",
    "temporaryflg",
    "tradeengdesc",
    "urgentflg",
    "verifyinprice1",
    "verifyinprice2",
    "verifyinprice3"
})
public class OpdOrder {

    @XmlElement(name = "ADM_TYPE")
    protected String admtype;
    @XmlElement(name = "AGENCY_ORG_CODE")
    protected String agencyorgcode;
    @XmlElement(name = "AR_AMT")
    protected double aramt;
    @XmlElement(name = "ATC_FLG")
    protected String atcflg;
    @XmlElement(name = "BATCH_SEQ1")
    protected double batchseq1;
    @XmlElement(name = "BATCH_SEQ2")
    protected double batchseq2;
    @XmlElement(name = "BATCH_SEQ3")
    protected double batchseq3;
    @XmlElement(name = "BILL_DATE")
    protected String billdate;
    @XmlElement(name = "BILL_FLG")
    protected String billflg;
    @XmlElement(name = "BILL_TYPE")
    protected String billtype;
    @XmlElement(name = "BILL_USER")
    protected String billuser;
    @XmlElement(name = "BIRTH_DATE")
    protected String birthdate;
    @XmlElement(name = "BUSINESS_NO")
    protected String businessno;
    @XmlElement(name = "CASE_NO")
    protected String caseno;
    @XmlElement(name = "CAT1_TYPE")
    protected String cat1TYPE;
    @XmlElement(name = "CONTRACT_CODE")
    protected String contractcode;
    @XmlElement(name = "COST_AMT")
    protected double costamt;
    @XmlElement(name = "COST_CENTER_CODE")
    protected String costcentercode;
    @XmlElement(name = "COUNTER_NO")
    protected double counterno;
    @XmlElement(name = "CTRLDRUGCLASS_CODE")
    protected String ctrldrugclasscode;
    @XmlElement(name = "CTZ1_CODE")
    protected String ctz1CODE;
    @XmlElement(name = "CTZ2_CODE")
    protected String ctz2CODE;
    @XmlElement(name = "CTZ3_CODE")
    protected String ctz3CODE;
    @XmlElement(name = "DCTAGENT_CODE")
    protected String dctagentcode;
    @XmlElement(name = "DCTAGENT_FLG")
    protected String dctagentflg;
    @XmlElement(name = "DCTEXCEP_CODE")
    protected String dctexcepcode;
    @XmlElement(name = "DCT_TAKE_QTY")
    protected double dcttakeqty;
    @XmlElement(name = "DC_DEPT_CODE")
    protected String dcdeptcode;
    @XmlElement(name = "DC_DR_CODE")
    protected String dcdrcode;
    @XmlElement(name = "DC_ORDER_DATE")
    protected String dcorderdate;
    @XmlElement(name = "DECOCT_CODE")
    protected String decoctcode;
    @XmlElement(name = "DECOCT_DATE")
    protected String decoctdate;
    @XmlElement(name = "DECOCT_REMARK")
    protected String decoctremark;
    @XmlElement(name = "DECOCT_USER")
    protected String decoctuser;
    @XmlElement(name = "DEGREE_CODE")
    protected String degreecode;
    @XmlElement(name = "DEPT_CODE")
    protected String deptcode;
    @XmlElement(name = "DEV_CODE")
    protected String devcode;
    @XmlElement(name = "DISCOUNT_RATE")
    protected double discountrate;
    @XmlElement(name = "DISPENSE_QTY")
    protected double dispenseqty;
    @XmlElement(name = "DISPENSE_QTY1")
    protected double dispenseqty1;
    @XmlElement(name = "DISPENSE_QTY2")
    protected double dispenseqty2;
    @XmlElement(name = "DISPENSE_QTY3")
    protected double dispenseqty3;
    @XmlElement(name = "DISPENSE_UNIT")
    protected String dispenseunit;
    @XmlElement(name = "DOSAGE_QTY")
    protected double dosageqty;
    @XmlElement(name = "DOSAGE_UNIT")
    protected String dosageunit;
    @XmlElement(name = "DOSE_TYPE")
    protected String dosetype;
    @XmlElement(name = "DR_CODE")
    protected String drcode;
    @XmlElement(name = "DR_NOTE")
    protected String drnote;
    @XmlElement(name = "EXEC_DEPT_CODE")
    protected String execdeptcode;
    @XmlElement(name = "EXEC_DR_CODE")
    protected String execdrcode;
    @XmlElement(name = "EXEC_DR_DESC")
    protected String execdrdesc;
    @XmlElement(name = "EXEC_FLG")
    protected String execflg;
    @XmlElement(name = "EXM_EXEC_END_DATE")
    protected String exmexecenddate;
    @XmlElement(name = "EXPENSIVE_FLG")
    protected String expensiveflg;
    @XmlElement(name = "FILE_NO")
    protected double fileno;
    @XmlElement(name = "FINAL_TYPE")
    protected String finaltype;
    @XmlElement(name = "FREQ_CODE")
    protected String freqcode;
    @XmlElement(name = "GIVEBOX_FLG")
    protected String giveboxflg;
    @XmlElement(name = "GOODS_DESC")
    protected String goodsdesc;
    @XmlElement(name = "HEXP_CODE")
    protected String hexpcode;
    @XmlElement(name = "HIDE_FLG")
    protected String hideflg;
    @XmlElement(name = "INSPAY_TYPE")
    protected String inspaytype;
    @XmlElement(name = "LINKMAIN_FLG")
    protected String linkmainflg;
    @XmlElement(name = "LINK_NO")
    protected String linkno;
    @XmlElement(name = "MEDI_QTY")
    protected double mediqty;
    @XmlElement(name = "MEDI_UNIT")
    protected String mediunit;
    @XmlElement(name = "MED_APPLY_NO")
    protected String medapplyno;
    @XmlElement(name = "MR_CODE")
    protected String mrcode;
    @XmlElement(name = "MR_NO")
    protected String mrno;
    @XmlElement(name = "NHI_PRICE")
    protected double nhiprice;
    @XmlElement(name = "NS_EXEC_CODE")
    protected String nsexeccode;
    @XmlElement(name = "NS_EXEC_DATE")
    protected String nsexecdate;
    @XmlElement(name = "NS_EXEC_DEPT")
    protected String nsexecdept;
    @XmlElement(name = "NS_NOTE")
    protected String nsnote;
    @XmlElement(name = "OPTITEM_CODE")
    protected String optitemcode;
    @XmlElement(name = "OPT_DATE")
    protected String optdate;
    @XmlElement(name = "OPT_TERM")
    protected String optterm;
    @XmlElement(name = "OPT_USER")
    protected String optuser;
    @XmlElement(name = "ORDERSET_CODE")
    protected String ordersetcode;
    @XmlElement(name = "ORDERSET_GROUP_NO")
    protected double ordersetgroupno;
    @XmlElement(name = "ORDER_CAT1_CODE")
    protected String ordercat1CODE;
    @XmlElement(name = "ORDER_CODE")
    protected String ordercode;
    @XmlElement(name = "ORDER_DATE")
    protected String orderdate;
    @XmlElement(name = "ORDER_DESC")
    protected String orderdesc;
    @XmlElement(name = "OWN_AMT")
    protected double ownamt;
    @XmlElement(name = "OWN_PRICE")
    protected double ownprice;
    @XmlElement(name = "PACKAGE_TOT")
    protected double packagetot;
    @XmlElement(name = "PAT_NAME")
    protected String patname;
    @XmlElement(name = "PHA_CHECK_CODE")
    protected String phacheckcode;
    @XmlElement(name = "PHA_CHECK_DATE")
    protected String phacheckdate;
    @XmlElement(name = "PHA_DISPENSE_CODE")
    protected String phadispensecode;
    @XmlElement(name = "PHA_DISPENSE_DATE")
    protected String phadispensedate;
    @XmlElement(name = "PHA_DOSAGE_CODE")
    protected String phadosagecode;
    @XmlElement(name = "PHA_DOSAGE_DATE")
    protected String phadosagedate;
    @XmlElement(name = "PHA_RETN_CODE")
    protected String pharetncode;
    @XmlElement(name = "PHA_RETN_DATE")
    protected String pharetndate;
    @XmlElement(name = "PHA_TYPE")
    protected String phatype;
    @XmlElement(name = "PRESCRIPT_NO")
    protected double prescriptno;
    @XmlElement(name = "PRESRT_NO")
    protected String presrtno;
    @XmlElement(name = "PRINTTYPEFLG_INFANT")
    protected String printtypeflginfant;
    @XmlElement(name = "PRINT_FLG")
    protected String printflg;
    @XmlElement(name = "PRINT_NO")
    protected String printno;
    @XmlElement(name = "PSY_FLG")
    protected String psyflg;
    @XmlElement(name = "RECEIPT_FLG")
    protected String receiptflg;
    @XmlElement(name = "RECEIPT_NO")
    protected String receiptno;
    @XmlElement(name = "REGION_CODE")
    protected String regioncode;
    @XmlElement(name = "RELEASE_FLG")
    protected String releaseflg;
    @XmlElement(name = "REQUEST_FLG")
    protected String requestflg;
    @XmlElement(name = "REQUEST_NO")
    protected String requestno;
    @XmlElement(name = "REXP_CODE")
    protected String rexpcode;
    @XmlElement(name = "ROUTE_CODE")
    protected String routecode;
    @XmlElement(name = "RPTTYPE_CODE")
    protected String rpttypecode;
    @XmlElement(name = "RX_NO")
    protected String rxno;
    @XmlElement(name = "RX_TYPE")
    protected String rxtype;
    @XmlElement(name = "SENDATC_DATE")
    protected String sendatcdate;
    @XmlElement(name = "SEND_DCT_DATE")
    protected String senddctdate;
    @XmlElement(name = "SEND_DCT_USER")
    protected String senddctuser;
    @XmlElement(name = "SEND_ORG_DATE")
    protected String sendorgdate;
    @XmlElement(name = "SEND_ORG_USER")
    protected String sendorguser;
    @XmlElement(name = "SEQ_NO")
    protected int seqno;
    @XmlElement(name = "SETMAIN_FLG")
    protected String setmainflg;
    @XmlElement(name = "SEX_TYPE")
    protected String sextype;
    @XmlElement(name = "SPECIFICATION")
    protected String specification;
    @XmlElement(name = "TAKE_DAYS")
    protected int takedays;
    @XmlElement(name = "TEMPORARY_FLG")
    protected String temporaryflg;
    @XmlElement(name = "TRADE_ENG_DESC")
    protected String tradeengdesc;
    @XmlElement(name = "URGENT_FLG")
    protected String urgentflg;
    @XmlElement(name = "VERIFYIN_PRICE1")
    protected double verifyinprice1;
    @XmlElement(name = "VERIFYIN_PRICE2")
    protected double verifyinprice2;
    @XmlElement(name = "VERIFYIN_PRICE3")
    protected double verifyinprice3;

    /**
     * Gets the value of the admtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getADMTYPE() {
        return admtype;
    }

    /**
     * Sets the value of the admtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setADMTYPE(String value) {
        this.admtype = value;
    }

    /**
     * Gets the value of the agencyorgcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAGENCYORGCODE() {
        return agencyorgcode;
    }

    /**
     * Sets the value of the agencyorgcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAGENCYORGCODE(String value) {
        this.agencyorgcode = value;
    }

    /**
     * Gets the value of the aramt property.
     * 
     */
    public double getARAMT() {
        return aramt;
    }

    /**
     * Sets the value of the aramt property.
     * 
     */
    public void setARAMT(double value) {
        this.aramt = value;
    }

    /**
     * Gets the value of the atcflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getATCFLG() {
        return atcflg;
    }

    /**
     * Sets the value of the atcflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setATCFLG(String value) {
        this.atcflg = value;
    }

    /**
     * Gets the value of the batchseq1 property.
     * 
     */
    public double getBATCHSEQ1() {
        return batchseq1;
    }

    /**
     * Sets the value of the batchseq1 property.
     * 
     */
    public void setBATCHSEQ1(double value) {
        this.batchseq1 = value;
    }

    /**
     * Gets the value of the batchseq2 property.
     * 
     */
    public double getBATCHSEQ2() {
        return batchseq2;
    }

    /**
     * Sets the value of the batchseq2 property.
     * 
     */
    public void setBATCHSEQ2(double value) {
        this.batchseq2 = value;
    }

    /**
     * Gets the value of the batchseq3 property.
     * 
     */
    public double getBATCHSEQ3() {
        return batchseq3;
    }

    /**
     * Sets the value of the batchseq3 property.
     * 
     */
    public void setBATCHSEQ3(double value) {
        this.batchseq3 = value;
    }

    /**
     * Gets the value of the billdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBILLDATE() {
        return billdate;
    }

    /**
     * Sets the value of the billdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBILLDATE(String value) {
        this.billdate = value;
    }

    /**
     * Gets the value of the billflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBILLFLG() {
        return billflg;
    }

    /**
     * Sets the value of the billflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBILLFLG(String value) {
        this.billflg = value;
    }

    /**
     * Gets the value of the billtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBILLTYPE() {
        return billtype;
    }

    /**
     * Sets the value of the billtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBILLTYPE(String value) {
        this.billtype = value;
    }

    /**
     * Gets the value of the billuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBILLUSER() {
        return billuser;
    }

    /**
     * Sets the value of the billuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBILLUSER(String value) {
        this.billuser = value;
    }

    /**
     * Gets the value of the birthdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIRTHDATE() {
        return birthdate;
    }

    /**
     * Sets the value of the birthdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIRTHDATE(String value) {
        this.birthdate = value;
    }

    /**
     * Gets the value of the businessno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBUSINESSNO() {
        return businessno;
    }

    /**
     * Sets the value of the businessno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBUSINESSNO(String value) {
        this.businessno = value;
    }

    /**
     * Gets the value of the caseno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASENO() {
        return caseno;
    }

    /**
     * Sets the value of the caseno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASENO(String value) {
        this.caseno = value;
    }

    /**
     * Gets the value of the cat1TYPE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAT1TYPE() {
        return cat1TYPE;
    }

    /**
     * Sets the value of the cat1TYPE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAT1TYPE(String value) {
        this.cat1TYPE = value;
    }

    /**
     * Gets the value of the contractcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCONTRACTCODE() {
        return contractcode;
    }

    /**
     * Sets the value of the contractcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCONTRACTCODE(String value) {
        this.contractcode = value;
    }

    /**
     * Gets the value of the costamt property.
     * 
     */
    public double getCOSTAMT() {
        return costamt;
    }

    /**
     * Sets the value of the costamt property.
     * 
     */
    public void setCOSTAMT(double value) {
        this.costamt = value;
    }

    /**
     * Gets the value of the costcentercode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOSTCENTERCODE() {
        return costcentercode;
    }

    /**
     * Sets the value of the costcentercode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOSTCENTERCODE(String value) {
        this.costcentercode = value;
    }

    /**
     * Gets the value of the counterno property.
     * 
     */
    public double getCOUNTERNO() {
        return counterno;
    }

    /**
     * Sets the value of the counterno property.
     * 
     */
    public void setCOUNTERNO(double value) {
        this.counterno = value;
    }

    /**
     * Gets the value of the ctrldrugclasscode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTRLDRUGCLASSCODE() {
        return ctrldrugclasscode;
    }

    /**
     * Sets the value of the ctrldrugclasscode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTRLDRUGCLASSCODE(String value) {
        this.ctrldrugclasscode = value;
    }

    /**
     * Gets the value of the ctz1CODE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTZ1CODE() {
        return ctz1CODE;
    }

    /**
     * Sets the value of the ctz1CODE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTZ1CODE(String value) {
        this.ctz1CODE = value;
    }

    /**
     * Gets the value of the ctz2CODE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTZ2CODE() {
        return ctz2CODE;
    }

    /**
     * Sets the value of the ctz2CODE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTZ2CODE(String value) {
        this.ctz2CODE = value;
    }

    /**
     * Gets the value of the ctz3CODE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTZ3CODE() {
        return ctz3CODE;
    }

    /**
     * Sets the value of the ctz3CODE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTZ3CODE(String value) {
        this.ctz3CODE = value;
    }

    /**
     * Gets the value of the dctagentcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCTAGENTCODE() {
        return dctagentcode;
    }

    /**
     * Sets the value of the dctagentcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCTAGENTCODE(String value) {
        this.dctagentcode = value;
    }

    /**
     * Gets the value of the dctagentflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCTAGENTFLG() {
        return dctagentflg;
    }

    /**
     * Sets the value of the dctagentflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCTAGENTFLG(String value) {
        this.dctagentflg = value;
    }

    /**
     * Gets the value of the dctexcepcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCTEXCEPCODE() {
        return dctexcepcode;
    }

    /**
     * Sets the value of the dctexcepcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCTEXCEPCODE(String value) {
        this.dctexcepcode = value;
    }

    /**
     * Gets the value of the dcttakeqty property.
     * 
     */
    public double getDCTTAKEQTY() {
        return dcttakeqty;
    }

    /**
     * Sets the value of the dcttakeqty property.
     * 
     */
    public void setDCTTAKEQTY(double value) {
        this.dcttakeqty = value;
    }

    /**
     * Gets the value of the dcdeptcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCDEPTCODE() {
        return dcdeptcode;
    }

    /**
     * Sets the value of the dcdeptcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCDEPTCODE(String value) {
        this.dcdeptcode = value;
    }

    /**
     * Gets the value of the dcdrcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCDRCODE() {
        return dcdrcode;
    }

    /**
     * Sets the value of the dcdrcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCDRCODE(String value) {
        this.dcdrcode = value;
    }

    /**
     * Gets the value of the dcorderdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCORDERDATE() {
        return dcorderdate;
    }

    /**
     * Sets the value of the dcorderdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCORDERDATE(String value) {
        this.dcorderdate = value;
    }

    /**
     * Gets the value of the decoctcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECOCTCODE() {
        return decoctcode;
    }

    /**
     * Sets the value of the decoctcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECOCTCODE(String value) {
        this.decoctcode = value;
    }

    /**
     * Gets the value of the decoctdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECOCTDATE() {
        return decoctdate;
    }

    /**
     * Sets the value of the decoctdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECOCTDATE(String value) {
        this.decoctdate = value;
    }

    /**
     * Gets the value of the decoctremark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECOCTREMARK() {
        return decoctremark;
    }

    /**
     * Sets the value of the decoctremark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECOCTREMARK(String value) {
        this.decoctremark = value;
    }

    /**
     * Gets the value of the decoctuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECOCTUSER() {
        return decoctuser;
    }

    /**
     * Sets the value of the decoctuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECOCTUSER(String value) {
        this.decoctuser = value;
    }

    /**
     * Gets the value of the degreecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEGREECODE() {
        return degreecode;
    }

    /**
     * Sets the value of the degreecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEGREECODE(String value) {
        this.degreecode = value;
    }

    /**
     * Gets the value of the deptcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEPTCODE() {
        return deptcode;
    }

    /**
     * Sets the value of the deptcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEPTCODE(String value) {
        this.deptcode = value;
    }

    /**
     * Gets the value of the devcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEVCODE() {
        return devcode;
    }

    /**
     * Sets the value of the devcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEVCODE(String value) {
        this.devcode = value;
    }

    /**
     * Gets the value of the discountrate property.
     * 
     */
    public double getDISCOUNTRATE() {
        return discountrate;
    }

    /**
     * Sets the value of the discountrate property.
     * 
     */
    public void setDISCOUNTRATE(double value) {
        this.discountrate = value;
    }

    /**
     * Gets the value of the dispenseqty property.
     * 
     */
    public double getDISPENSEQTY() {
        return dispenseqty;
    }

    /**
     * Sets the value of the dispenseqty property.
     * 
     */
    public void setDISPENSEQTY(double value) {
        this.dispenseqty = value;
    }

    /**
     * Gets the value of the dispenseqty1 property.
     * 
     */
    public double getDISPENSEQTY1() {
        return dispenseqty1;
    }

    /**
     * Sets the value of the dispenseqty1 property.
     * 
     */
    public void setDISPENSEQTY1(double value) {
        this.dispenseqty1 = value;
    }

    /**
     * Gets the value of the dispenseqty2 property.
     * 
     */
    public double getDISPENSEQTY2() {
        return dispenseqty2;
    }

    /**
     * Sets the value of the dispenseqty2 property.
     * 
     */
    public void setDISPENSEQTY2(double value) {
        this.dispenseqty2 = value;
    }

    /**
     * Gets the value of the dispenseqty3 property.
     * 
     */
    public double getDISPENSEQTY3() {
        return dispenseqty3;
    }

    /**
     * Sets the value of the dispenseqty3 property.
     * 
     */
    public void setDISPENSEQTY3(double value) {
        this.dispenseqty3 = value;
    }

    /**
     * Gets the value of the dispenseunit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDISPENSEUNIT() {
        return dispenseunit;
    }

    /**
     * Sets the value of the dispenseunit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDISPENSEUNIT(String value) {
        this.dispenseunit = value;
    }

    /**
     * Gets the value of the dosageqty property.
     * 
     */
    public double getDOSAGEQTY() {
        return dosageqty;
    }

    /**
     * Sets the value of the dosageqty property.
     * 
     */
    public void setDOSAGEQTY(double value) {
        this.dosageqty = value;
    }

    /**
     * Gets the value of the dosageunit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOSAGEUNIT() {
        return dosageunit;
    }

    /**
     * Sets the value of the dosageunit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOSAGEUNIT(String value) {
        this.dosageunit = value;
    }

    /**
     * Gets the value of the dosetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOSETYPE() {
        return dosetype;
    }

    /**
     * Sets the value of the dosetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOSETYPE(String value) {
        this.dosetype = value;
    }

    /**
     * Gets the value of the drcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRCODE() {
        return drcode;
    }

    /**
     * Sets the value of the drcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRCODE(String value) {
        this.drcode = value;
    }

    /**
     * Gets the value of the drnote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRNOTE() {
        return drnote;
    }

    /**
     * Sets the value of the drnote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRNOTE(String value) {
        this.drnote = value;
    }

    /**
     * Gets the value of the execdeptcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXECDEPTCODE() {
        return execdeptcode;
    }

    /**
     * Sets the value of the execdeptcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXECDEPTCODE(String value) {
        this.execdeptcode = value;
    }

    /**
     * Gets the value of the execdrcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXECDRCODE() {
        return execdrcode;
    }

    /**
     * Sets the value of the execdrcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXECDRCODE(String value) {
        this.execdrcode = value;
    }

    /**
     * Gets the value of the execdrdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXECDRDESC() {
        return execdrdesc;
    }

    /**
     * Sets the value of the execdrdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXECDRDESC(String value) {
        this.execdrdesc = value;
    }

    /**
     * Gets the value of the execflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXECFLG() {
        return execflg;
    }

    /**
     * Sets the value of the execflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXECFLG(String value) {
        this.execflg = value;
    }

    /**
     * Gets the value of the exmexecenddate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXMEXECENDDATE() {
        return exmexecenddate;
    }

    /**
     * Sets the value of the exmexecenddate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXMEXECENDDATE(String value) {
        this.exmexecenddate = value;
    }

    /**
     * Gets the value of the expensiveflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXPENSIVEFLG() {
        return expensiveflg;
    }

    /**
     * Sets the value of the expensiveflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXPENSIVEFLG(String value) {
        this.expensiveflg = value;
    }

    /**
     * Gets the value of the fileno property.
     * 
     */
    public double getFILENO() {
        return fileno;
    }

    /**
     * Sets the value of the fileno property.
     * 
     */
    public void setFILENO(double value) {
        this.fileno = value;
    }

    /**
     * Gets the value of the finaltype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFINALTYPE() {
        return finaltype;
    }

    /**
     * Sets the value of the finaltype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFINALTYPE(String value) {
        this.finaltype = value;
    }

    /**
     * Gets the value of the freqcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFREQCODE() {
        return freqcode;
    }

    /**
     * Sets the value of the freqcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFREQCODE(String value) {
        this.freqcode = value;
    }

    /**
     * Gets the value of the giveboxflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGIVEBOXFLG() {
        return giveboxflg;
    }

    /**
     * Sets the value of the giveboxflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGIVEBOXFLG(String value) {
        this.giveboxflg = value;
    }

    /**
     * Gets the value of the goodsdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGOODSDESC() {
        return goodsdesc;
    }

    /**
     * Sets the value of the goodsdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGOODSDESC(String value) {
        this.goodsdesc = value;
    }

    /**
     * Gets the value of the hexpcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHEXPCODE() {
        return hexpcode;
    }

    /**
     * Sets the value of the hexpcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHEXPCODE(String value) {
        this.hexpcode = value;
    }

    /**
     * Gets the value of the hideflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHIDEFLG() {
        return hideflg;
    }

    /**
     * Sets the value of the hideflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHIDEFLG(String value) {
        this.hideflg = value;
    }

    /**
     * Gets the value of the inspaytype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINSPAYTYPE() {
        return inspaytype;
    }

    /**
     * Sets the value of the inspaytype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINSPAYTYPE(String value) {
        this.inspaytype = value;
    }

    /**
     * Gets the value of the linkmainflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLINKMAINFLG() {
        return linkmainflg;
    }

    /**
     * Sets the value of the linkmainflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLINKMAINFLG(String value) {
        this.linkmainflg = value;
    }

    /**
     * Gets the value of the linkno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLINKNO() {
        return linkno;
    }

    /**
     * Sets the value of the linkno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLINKNO(String value) {
        this.linkno = value;
    }

    /**
     * Gets the value of the mediqty property.
     * 
     */
    public double getMEDIQTY() {
        return mediqty;
    }

    /**
     * Sets the value of the mediqty property.
     * 
     */
    public void setMEDIQTY(double value) {
        this.mediqty = value;
    }

    /**
     * Gets the value of the mediunit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEDIUNIT() {
        return mediunit;
    }

    /**
     * Sets the value of the mediunit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMEDIUNIT(String value) {
        this.mediunit = value;
    }

    /**
     * Gets the value of the medapplyno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEDAPPLYNO() {
        return medapplyno;
    }

    /**
     * Sets the value of the medapplyno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMEDAPPLYNO(String value) {
        this.medapplyno = value;
    }

    /**
     * Gets the value of the mrcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMRCODE() {
        return mrcode;
    }

    /**
     * Sets the value of the mrcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMRCODE(String value) {
        this.mrcode = value;
    }

    /**
     * Gets the value of the mrno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMRNO() {
        return mrno;
    }

    /**
     * Sets the value of the mrno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMRNO(String value) {
        this.mrno = value;
    }

    /**
     * Gets the value of the nhiprice property.
     * 
     */
    public double getNHIPRICE() {
        return nhiprice;
    }

    /**
     * Sets the value of the nhiprice property.
     * 
     */
    public void setNHIPRICE(double value) {
        this.nhiprice = value;
    }

    /**
     * Gets the value of the nsexeccode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNSEXECCODE() {
        return nsexeccode;
    }

    /**
     * Sets the value of the nsexeccode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNSEXECCODE(String value) {
        this.nsexeccode = value;
    }

    /**
     * Gets the value of the nsexecdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNSEXECDATE() {
        return nsexecdate;
    }

    /**
     * Sets the value of the nsexecdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNSEXECDATE(String value) {
        this.nsexecdate = value;
    }

    /**
     * Gets the value of the nsexecdept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNSEXECDEPT() {
        return nsexecdept;
    }

    /**
     * Sets the value of the nsexecdept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNSEXECDEPT(String value) {
        this.nsexecdept = value;
    }

    /**
     * Gets the value of the nsnote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNSNOTE() {
        return nsnote;
    }

    /**
     * Sets the value of the nsnote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNSNOTE(String value) {
        this.nsnote = value;
    }

    /**
     * Gets the value of the optitemcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTITEMCODE() {
        return optitemcode;
    }

    /**
     * Sets the value of the optitemcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTITEMCODE(String value) {
        this.optitemcode = value;
    }

    /**
     * Gets the value of the optdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTDATE() {
        return optdate;
    }

    /**
     * Sets the value of the optdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTDATE(String value) {
        this.optdate = value;
    }

    /**
     * Gets the value of the optterm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTTERM() {
        return optterm;
    }

    /**
     * Sets the value of the optterm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTTERM(String value) {
        this.optterm = value;
    }

    /**
     * Gets the value of the optuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPTUSER() {
        return optuser;
    }

    /**
     * Sets the value of the optuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPTUSER(String value) {
        this.optuser = value;
    }

    /**
     * Gets the value of the ordersetcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDERSETCODE() {
        return ordersetcode;
    }

    /**
     * Sets the value of the ordersetcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDERSETCODE(String value) {
        this.ordersetcode = value;
    }

    /**
     * Gets the value of the ordersetgroupno property.
     * 
     */
    public double getORDERSETGROUPNO() {
        return ordersetgroupno;
    }

    /**
     * Sets the value of the ordersetgroupno property.
     * 
     */
    public void setORDERSETGROUPNO(double value) {
        this.ordersetgroupno = value;
    }

    /**
     * Gets the value of the ordercat1CODE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDERCAT1CODE() {
        return ordercat1CODE;
    }

    /**
     * Sets the value of the ordercat1CODE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDERCAT1CODE(String value) {
        this.ordercat1CODE = value;
    }

    /**
     * Gets the value of the ordercode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDERCODE() {
        return ordercode;
    }

    /**
     * Sets the value of the ordercode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDERCODE(String value) {
        this.ordercode = value;
    }

    /**
     * Gets the value of the orderdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDERDATE() {
        return orderdate;
    }

    /**
     * Sets the value of the orderdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDERDATE(String value) {
        this.orderdate = value;
    }

    /**
     * Gets the value of the orderdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORDERDESC() {
        return orderdesc;
    }

    /**
     * Sets the value of the orderdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORDERDESC(String value) {
        this.orderdesc = value;
    }

    /**
     * Gets the value of the ownamt property.
     * 
     */
    public double getOWNAMT() {
        return ownamt;
    }

    /**
     * Sets the value of the ownamt property.
     * 
     */
    public void setOWNAMT(double value) {
        this.ownamt = value;
    }

    /**
     * Gets the value of the ownprice property.
     * 
     */
    public double getOWNPRICE() {
        return ownprice;
    }

    /**
     * Sets the value of the ownprice property.
     * 
     */
    public void setOWNPRICE(double value) {
        this.ownprice = value;
    }

    /**
     * Gets the value of the packagetot property.
     * 
     */
    public double getPACKAGETOT() {
        return packagetot;
    }

    /**
     * Sets the value of the packagetot property.
     * 
     */
    public void setPACKAGETOT(double value) {
        this.packagetot = value;
    }

    /**
     * Gets the value of the patname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPATNAME() {
        return patname;
    }

    /**
     * Sets the value of the patname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPATNAME(String value) {
        this.patname = value;
    }

    /**
     * Gets the value of the phacheckcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHACHECKCODE() {
        return phacheckcode;
    }

    /**
     * Sets the value of the phacheckcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHACHECKCODE(String value) {
        this.phacheckcode = value;
    }

    /**
     * Gets the value of the phacheckdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHACHECKDATE() {
        return phacheckdate;
    }

    /**
     * Sets the value of the phacheckdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHACHECKDATE(String value) {
        this.phacheckdate = value;
    }

    /**
     * Gets the value of the phadispensecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHADISPENSECODE() {
        return phadispensecode;
    }

    /**
     * Sets the value of the phadispensecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHADISPENSECODE(String value) {
        this.phadispensecode = value;
    }

    /**
     * Gets the value of the phadispensedate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHADISPENSEDATE() {
        return phadispensedate;
    }

    /**
     * Sets the value of the phadispensedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHADISPENSEDATE(String value) {
        this.phadispensedate = value;
    }

    /**
     * Gets the value of the phadosagecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHADOSAGECODE() {
        return phadosagecode;
    }

    /**
     * Sets the value of the phadosagecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHADOSAGECODE(String value) {
        this.phadosagecode = value;
    }

    /**
     * Gets the value of the phadosagedate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHADOSAGEDATE() {
        return phadosagedate;
    }

    /**
     * Sets the value of the phadosagedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHADOSAGEDATE(String value) {
        this.phadosagedate = value;
    }

    /**
     * Gets the value of the pharetncode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHARETNCODE() {
        return pharetncode;
    }

    /**
     * Sets the value of the pharetncode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHARETNCODE(String value) {
        this.pharetncode = value;
    }

    /**
     * Gets the value of the pharetndate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHARETNDATE() {
        return pharetndate;
    }

    /**
     * Sets the value of the pharetndate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHARETNDATE(String value) {
        this.pharetndate = value;
    }

    /**
     * Gets the value of the phatype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHATYPE() {
        return phatype;
    }

    /**
     * Sets the value of the phatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHATYPE(String value) {
        this.phatype = value;
    }

    /**
     * Gets the value of the prescriptno property.
     * 
     */
    public double getPRESCRIPTNO() {
        return prescriptno;
    }

    /**
     * Sets the value of the prescriptno property.
     * 
     */
    public void setPRESCRIPTNO(double value) {
        this.prescriptno = value;
    }

    /**
     * Gets the value of the presrtno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRESRTNO() {
        return presrtno;
    }

    /**
     * Sets the value of the presrtno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRESRTNO(String value) {
        this.presrtno = value;
    }

    /**
     * Gets the value of the printtypeflginfant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRINTTYPEFLGINFANT() {
        return printtypeflginfant;
    }

    /**
     * Sets the value of the printtypeflginfant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRINTTYPEFLGINFANT(String value) {
        this.printtypeflginfant = value;
    }

    /**
     * Gets the value of the printflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRINTFLG() {
        return printflg;
    }

    /**
     * Sets the value of the printflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRINTFLG(String value) {
        this.printflg = value;
    }

    /**
     * Gets the value of the printno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRINTNO() {
        return printno;
    }

    /**
     * Sets the value of the printno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRINTNO(String value) {
        this.printno = value;
    }

    /**
     * Gets the value of the psyflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPSYFLG() {
        return psyflg;
    }

    /**
     * Sets the value of the psyflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPSYFLG(String value) {
        this.psyflg = value;
    }

    /**
     * Gets the value of the receiptflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRECEIPTFLG() {
        return receiptflg;
    }

    /**
     * Sets the value of the receiptflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRECEIPTFLG(String value) {
        this.receiptflg = value;
    }

    /**
     * Gets the value of the receiptno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRECEIPTNO() {
        return receiptno;
    }

    /**
     * Sets the value of the receiptno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRECEIPTNO(String value) {
        this.receiptno = value;
    }

    /**
     * Gets the value of the regioncode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREGIONCODE() {
        return regioncode;
    }

    /**
     * Sets the value of the regioncode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREGIONCODE(String value) {
        this.regioncode = value;
    }

    /**
     * Gets the value of the releaseflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRELEASEFLG() {
        return releaseflg;
    }

    /**
     * Sets the value of the releaseflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRELEASEFLG(String value) {
        this.releaseflg = value;
    }

    /**
     * Gets the value of the requestflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUESTFLG() {
        return requestflg;
    }

    /**
     * Sets the value of the requestflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUESTFLG(String value) {
        this.requestflg = value;
    }

    /**
     * Gets the value of the requestno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUESTNO() {
        return requestno;
    }

    /**
     * Sets the value of the requestno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUESTNO(String value) {
        this.requestno = value;
    }

    /**
     * Gets the value of the rexpcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREXPCODE() {
        return rexpcode;
    }

    /**
     * Sets the value of the rexpcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREXPCODE(String value) {
        this.rexpcode = value;
    }

    /**
     * Gets the value of the routecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getROUTECODE() {
        return routecode;
    }

    /**
     * Sets the value of the routecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setROUTECODE(String value) {
        this.routecode = value;
    }

    /**
     * Gets the value of the rpttypecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRPTTYPECODE() {
        return rpttypecode;
    }

    /**
     * Sets the value of the rpttypecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRPTTYPECODE(String value) {
        this.rpttypecode = value;
    }

    /**
     * Gets the value of the rxno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRXNO() {
        return rxno;
    }

    /**
     * Sets the value of the rxno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRXNO(String value) {
        this.rxno = value;
    }

    /**
     * Gets the value of the rxtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRXTYPE() {
        return rxtype;
    }

    /**
     * Sets the value of the rxtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRXTYPE(String value) {
        this.rxtype = value;
    }

    /**
     * Gets the value of the sendatcdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENDATCDATE() {
        return sendatcdate;
    }

    /**
     * Sets the value of the sendatcdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENDATCDATE(String value) {
        this.sendatcdate = value;
    }

    /**
     * Gets the value of the senddctdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENDDCTDATE() {
        return senddctdate;
    }

    /**
     * Sets the value of the senddctdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENDDCTDATE(String value) {
        this.senddctdate = value;
    }

    /**
     * Gets the value of the senddctuser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENDDCTUSER() {
        return senddctuser;
    }

    /**
     * Sets the value of the senddctuser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENDDCTUSER(String value) {
        this.senddctuser = value;
    }

    /**
     * Gets the value of the sendorgdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENDORGDATE() {
        return sendorgdate;
    }

    /**
     * Sets the value of the sendorgdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENDORGDATE(String value) {
        this.sendorgdate = value;
    }

    /**
     * Gets the value of the sendorguser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENDORGUSER() {
        return sendorguser;
    }

    /**
     * Sets the value of the sendorguser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENDORGUSER(String value) {
        this.sendorguser = value;
    }

    /**
     * Gets the value of the seqno property.
     * 
     */
    public int getSEQNO() {
        return seqno;
    }

    /**
     * Sets the value of the seqno property.
     * 
     */
    public void setSEQNO(int value) {
        this.seqno = value;
    }

    /**
     * Gets the value of the setmainflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSETMAINFLG() {
        return setmainflg;
    }

    /**
     * Sets the value of the setmainflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSETMAINFLG(String value) {
        this.setmainflg = value;
    }

    /**
     * Gets the value of the sextype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEXTYPE() {
        return sextype;
    }

    /**
     * Sets the value of the sextype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEXTYPE(String value) {
        this.sextype = value;
    }

    /**
     * Gets the value of the specification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSPECIFICATION() {
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
    public void setSPECIFICATION(String value) {
        this.specification = value;
    }

    /**
     * Gets the value of the takedays property.
     * 
     */
    public int getTAKEDAYS() {
        return takedays;
    }

    /**
     * Sets the value of the takedays property.
     * 
     */
    public void setTAKEDAYS(int value) {
        this.takedays = value;
    }

    /**
     * Gets the value of the temporaryflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTEMPORARYFLG() {
        return temporaryflg;
    }

    /**
     * Sets the value of the temporaryflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTEMPORARYFLG(String value) {
        this.temporaryflg = value;
    }

    /**
     * Gets the value of the tradeengdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRADEENGDESC() {
        return tradeengdesc;
    }

    /**
     * Sets the value of the tradeengdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRADEENGDESC(String value) {
        this.tradeengdesc = value;
    }

    /**
     * Gets the value of the urgentflg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURGENTFLG() {
        return urgentflg;
    }

    /**
     * Sets the value of the urgentflg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURGENTFLG(String value) {
        this.urgentflg = value;
    }

    /**
     * Gets the value of the verifyinprice1 property.
     * 
     */
    public double getVERIFYINPRICE1() {
        return verifyinprice1;
    }

    /**
     * Sets the value of the verifyinprice1 property.
     * 
     */
    public void setVERIFYINPRICE1(double value) {
        this.verifyinprice1 = value;
    }

    /**
     * Gets the value of the verifyinprice2 property.
     * 
     */
    public double getVERIFYINPRICE2() {
        return verifyinprice2;
    }

    /**
     * Sets the value of the verifyinprice2 property.
     * 
     */
    public void setVERIFYINPRICE2(double value) {
        this.verifyinprice2 = value;
    }

    /**
     * Gets the value of the verifyinprice3 property.
     * 
     */
    public double getVERIFYINPRICE3() {
        return verifyinprice3;
    }

    /**
     * Sets the value of the verifyinprice3 property.
     * 
     */
    public void setVERIFYINPRICE3(double value) {
        this.verifyinprice3 = value;
    }

}
