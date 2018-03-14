package jdo.opd;

import java.sql.Timestamp;

import jdo.reg.ClinicRoomTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import jdo.reg.Reg;

/**
 *
 * <p>
 * Title: 药，诊疗项目，检验检查list
 * </p>
 *
 * <p>
 * Description:药，诊疗项目，检验检查list
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800908
 * @version 1.0
 */
public class OrderList
    extends TModifiedList {
    /**
     * 病患信息
     */
    private int maxseq = 0;
    /**
     * 病人
     */
    private Pat pat;
    /**
     * 处方对象
     */
    private PrescriptionList prescriptionlist;
    /**
     * 医嘱分类
     */
    private String rxType;
    /**
     * 处方签号
     */
    private String rxNo;
    /**
     * 条码号
     */
    private String medApplyNo;
    /**
     * 处方签内各个处方的费用总计
     */
    private Double sumFee;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 开单科室
     */
    private String deptCode;
    /**
     * 开单医生
     */
    private String drCode;
    /**
     * 发药时间
     */
    private Timestamp phaDispenseDate;
    /**
     * 审核时间
     */
    private Timestamp phaCheckDate;
    /**
     * 配药时间
     */
    private Timestamp phaDosageDate;
    /**
     * 退药时间（PHA专用）
     */
    private Timestamp phaRetnDate;
    /**
     * 执行科室
     */
    private String execDeptCode;
    /**
     * 服号
     */
    private int presrtNo;
    /**
     * 煎药室
     */
    private String decoctCode;
    /**
     * 代煎注记
     */
    private String dctagentFlg;
    /**
     * 代理执行科室
     */
    private String agencyOrgCode;
    /**
     * 领药号
     */
    private int prescriptNo;
    /**
     * 品种
     */
    private String variety;
    /**
     * 日份
     */
    private int takeDays;
    /**
     * 饮片使用剂量
     */
    private int dctTakeQty;
    /**
     * 频次
     */
    private String freqCode;
    /**
     * 用法
     */
    private String routeCode;
    /**
     * 煎药方式
     */
    private String dctAgentCode;
    /**
     * 备注
     */
    private String drNote;
    /**
     * 总克数
     */
    private String totGram;
    /**
     * 急作
     */
    private String urgentFlg;
    /**
     * 自备
     */
    private String releaseFlg;
    /**
     * 代煎总包数(付数*频次)
     */
    private int packageTot;
    /**
     * 退药人员（PHA专用）
     */
    private String phaRetnCode;
    /**
     * 门急住别
     */
    private String admType;


    /**
     * 领药窗口号
     */
    private int counterNo;


    /**
     * 执行注记
     */
    private String execFlg;

    /**
     * 领药号
     */
    private String printNo;

    /**
     * 医嘱细分类
     */
    private String cat1Type;
    /**
     * 内部交易号码
     */
    private String businessNo;//BUSINESS_NO
    public String getBusinessNo(){
    	return businessNo;
    }
    public void setBusinessNo(String businessNo){
    	this.businessNo=businessNo;
    }
    /**
     * 选中状态
     */
    private boolean chargeFlg;
    public boolean getChargeFlg(){
    	return chargeFlg;
    }
    public void setChargeFlg(boolean chargeFlg){
    	this.chargeFlg=chargeFlg;
    }
    /**
     * 得到医嘱细分类
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }
    /**
     * 设置医嘱细分类
     * @param value String
     */
    public void setCat1Type(String value) {
        this.cat1Type = value;
    }
    /**
     * 取得领药号
     * @return String
     */
    public String getPrintNo() {
        return printNo;
    }

    /**
     * 设置领药号
     * @param value String
     */
    public void setPrintNo(String value) {
        this.printNo = value;
    }

    /**
     * 取得执行注记
     * @return String
     */
    public String getExecFlg() {
        return execFlg;
    }

    /**
     * 设置执行注记
     * @param value String
     */
    public void setExecFlg(String value) {
        this.execFlg = value;
    }
    /**
     * 打票注记
     */
    private String receiptFlg;


    /**
     * 得到打票注记
     * @return String
     */
    public String getReceiptFlg() {
        return receiptFlg;
    }

    /**
     * 设置打票注记
     * @param value String
     */
    public void setReceiptFlg(String receiptFlg) {
        this.receiptFlg = receiptFlg;
    }

    /**
     * 取得领药窗口号
     * @return int
     */
    public int getCounterNo() {
        return counterNo;
    }

    /**
     * 设置领药窗口号
     * @param value int
     */
    public void setCounterNo(int value) {
        counterNo = value;
    }

    /**
     * 设置代理执行科室
     *
     * @param agencyOrgCode
     *            String
     */
    public void setAgencyOrgCode(String agencyOrgCode) {
        this.agencyOrgCode = agencyOrgCode;
    }

    /**
     * 得到代理执行科室
     *
     * @return agencyOrgCode String
     */
    public String getAgencyOrgCode() {
        return this.agencyOrgCode;
    }

    /**
     * 设置代煎注记
     *
     * @param dctagentFlg
     *            String
     */
    public void setDctagentFlg(String dctagentFlg) {
        this.dctagentFlg = dctagentFlg;
    }

    /**
     * 得到代煎注记
     *
     * @return dctagentFlg String
     */
    public String getDctagentFlg() {
        return this.dctagentFlg;
    }

    /**
     * 得到退药人员
     * @return phaReturnCode String
     */
    public String getPhaRetnCode() {
        return phaRetnCode;
    }

    /**
     * 设置退药人员
     * @parm dsReturnUser String
     */
    public void setPhaRetnCode(String dsReturnUser) {
        this.phaRetnCode = dsReturnUser;
    }


    /**
     * 设置煎药室
     *
     * @param decoctCode
     *            String
     */
    public void setDecoctCode(String decoctCode) {
        this.decoctCode = decoctCode;
    }

    /**
     * 得到煎药室
     *
     * @return decoctCode String
     */
    public String getDecoctCode() {
        return this.decoctCode;
    }

    /**
     * 设置服号
     *
     * @param presrtNo
     *            int
     *
     */
    public void setPresrtNo(int presrtNo) {
        this.presrtNo = presrtNo;
    }

    /**
     * 得到服号
     *
     * @return presrtNo int
     */
    public int getPresrtNo() {
        return this.presrtNo;
    }

    /**
     * 设置病患信息
     *
     * @param pat
     */
    public void setPat(Pat pat) {
        this.pat = pat;
    }

    /**
     * 得到病患信息
     *
     * @return
     */
    public Pat getPat() {
        return pat;
    }

    /**
     * 设置处方对象
     *
     * @param prescriptionlist
     *            PrescriptionList
     */
    public void setPrescriptionList(PrescriptionList prescriptionlist) {
        this.prescriptionlist = prescriptionlist;
    }

    /**
     * 得到处方对象
     *
     * @return PrescriptionList PrescriptionList
     */
    public PrescriptionList getPrescriptionList() {
        return this.prescriptionlist;
    }

    /**
     * 设置处方签号
     *
     * @param rxNo String
     */
    public void setRxNo(String rxNo) {
        this.rxNo = rxNo;
    }
    /**
     * 设置条码号
     * @param medApplyNo String
     */
    public void setMedApplyNo(String medApplyNo) {
        this.medApplyNo = medApplyNo;
    }
    /**
     * 得到处方签号
     *
     * @return rxNo String
     */
    public String getRxNo() {
        return this.rxNo;
    }

    /**
     * 得到条码号
     * @return String
     */
    public String getMedApplyNo() {
        return this.medApplyNo;
    }
    /**
     * 得到费用总计
     *
     * @return sumFee Double
     */
    public Double getSumFee() {
        return this.sumFee;
    }

    /**
     * 设置费用总计
     *
     * @param sumFee
     *            Double
     */
    public void setSumFee(Double sumFee) {
        this.sumFee = sumFee;
    }

    /**
     * 得到病案号
     *
     * @return mrNo String
     */
    public String getMrNo() {
        return this.mrNo;
    }

    /**
     * 设置病案号
     *
     * @param mrNo
     *            String
     */
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * 得到部门代码
     *
     * @return deptCode String
     */
    public String getDeptCode() {
        return this.deptCode;
    }

    /**
     * 设置部门
     *
     * @param deptCode
     *            String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * 得到开单医生
     *
     * @return drCode String
     */
    public String getDrCode() {
        return this.drCode;
    }

    /**
     * 设置开单医生
     *
     * @param drCode
     *            String
     */
    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    /**
     * 得到发药日期
     *
     * @return phaDispenseDate Timestamp
     */
    public Timestamp getPhaDispenseDate() {
        return this.phaDispenseDate;
    }

    /**
     * 设置发药日期
     *
     * @param phaDispenseDate
     *            String
     */
    public void setPhaDispenseDate(Timestamp dsdlvryDate) {
        this.phaDispenseDate = dsdlvryDate;
    }

    /**
     * 得到执行部门
     *
     * @return execDeptCode String
     */
    public String getExecDeptCode() {
        return this.execDeptCode;
    }

    /**
     * 设置执行部门
     *
     * @param execDeptCode
     *            String
     */
    public void setExecDeptCode(String execDeptCode) {
        this.execDeptCode = execDeptCode;
    }

    /**
     * 得到处方类型
     *
     * @return RxType String
     */
    public String getRxType() {
        return rxType;
    }

    /**
     * 设置处方类型
     *
     * @param rxType
     *            String
     */
    public void setRxType(String rxType) {
        this.rxType = rxType;
    }

    /**
     * 得到审核时间
     *
     * @return phacheckDate String
     */
    public Timestamp getPhaCheckDate() {
        return phaCheckDate;
    }

    /**
     * 设置审核时间
     *
     * @param phacheckDate
     *            String
     */
    public void setPhaCheckDate(Timestamp dscheckDate) {
        this.phaCheckDate = dscheckDate;
    }

    /**
     * 得到配药时间
     *
     * @return dsdgtDate String
     */
    public Timestamp getPhaDosageDate() {
        return phaDosageDate;
    }

    /**
     * 设置配药时间
     *
     * @param dsdgtDate
     *            String
     */
    public void setPhaDosageDate(Timestamp dsdgtDate) {
        this.phaDosageDate = dsdgtDate;
    }

    /**
     * 得到退药时间（PHA专用）
     *
     * @return phaReturnDate String
     */
    public Timestamp getPhaRetnDate() {
        return phaRetnDate;
    }

    /**
     * 设置退药时间（PHA专用）
     *
     * @param dsreturnDate
     *            String
     */
    public void setPhaRetnDate(Timestamp dsreturnDate) {
        this.phaRetnDate = dsreturnDate;
    }

    /**
     * 设置领药号
     *
     * @param prescriptNo
     *            int
     */
    public void setPrescriptNo(int prescriptNo) {
        this.prescriptNo = prescriptNo;
    }

    /**
     * 得到领药号
     *
     * @return prescriptNo int
     */
    public int getPrescriptNo() {
        return this.prescriptNo;
    }

    /**
     * 设置品种
     *
     * @param variety
     *            String
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * 得到品种
     *
     * @return variety String
     */
    public String getVariety() {
        return this.variety;
    }

    /**
     * 得到日份
     *
     * @return takeDays int
     */
    public int getTakeDays() {
        return takeDays;
    }

    /**
     * 设置日份
     *
     * @parm takeDays int
     */
    public void setTakeDays(int takeDays) {
        this.takeDays = takeDays;
    }

    /**
     * 得到饮片使用量
     *
     * @return dctTakeQty int
     */
    public int getDctTakeQty() {
        return dctTakeQty;
    }

    /**
     * 设置饮片使用量
     *
     * @parm dctTakeQty int
     */
    public void setDctTakeQty(int dctTakeQty) {
        this.dctTakeQty = dctTakeQty;
    }

    /**
     * 得到频次
     *
     * @return freqCode String
     */
    public String getFreqCode() {
        return freqCode;
    }

    /**
     * 设置频次
     *
     * @parm freqCode String
     */
    public void setFreqCode(String freqCode) {
        this.freqCode = freqCode;
    }

    /**
     * 得到用法
     *
     * @return routeCode String
     */
    public String getRouteCode() {
        return routeCode;
    }

    /**
     * 设置用法
     *
     * @parm routeCode String
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    /**
     * 得到特殊煎法
     *
     * @return dctAgentCode String
     */
    public String getDctAgentCode() {
        return dctAgentCode;
    }

    /**
     * 设置特殊煎法
     *
     * @parm dctAgentCode String
     */
    public void setDctAgentCode(String dctAgentCode) {
        this.dctAgentCode = dctAgentCode;
    }

    /**
     * 得到备注
     *
     * @return description String
     */
    public String getDrNote() {
        return drNote;
    }

    /**
     * 设置备注
     *
     * @parm description String
     */
    public void setDrNote(String description) {
        this.drNote = description;
    }

    /**
     * 得到总克数
     *
     * @return totGram String
     */
    public String getTotGram() {
        return totGram;
    }

    /**
     * 设置总克数
     *
     * @parm totGram String
     */
    public void setTotGram(String totGram) {
        this.totGram = totGram;
    }

    /**
     * 得到急作
     *
     * @return urgentFlg String
     */
    public String getUrgentFlg() {
        return urgentFlg;
    }

    /**
     * 设置急作
     *
     * @parm urgentFlg String
     */
    public void setUrgentFlg(String urgentFlg) {
        this.urgentFlg = urgentFlg;
    }

    /**
     * 得到自备
     *
     * @return releaseFlg String
     */
    public String getReleaseFlg() {
        return releaseFlg;
    }

    /**
     * 设置自备
     *
     * @parm releaseFlg String
     */
    public void setReleaseFlg(String releaseFlg) {
        this.releaseFlg = releaseFlg;
    }

    /**
     * 得到总包数
     *
     * @return packageTot int
     */
    public int getPackageTot() {
        return packageTot;
    }

    /**
     * 设置总包数
     *
     * @parm packageTot int
     */
    public void setPackageTot(int packageTot) {
        this.packageTot = packageTot;
    }

    /**
     * 设置门急住别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * 得到门急住别
     * @return admType String
     */
    public String getAdmType() {
        return this.admType;
    }

    /**
     * 构造器
     *
     * @param prescriptionlist
     */
    public OrderList(PrescriptionList prescriptionlist) {
        this();
        this.setPrescriptionList(prescriptionlist);

    }

    /**
     * 构造器
     */
    public OrderList() {
        setMapString("caseNo:CASE_NO;rxNo:RX_NO;seqNo:SEQ_NO;medApplyNo:MED_APPLY_NO;optUser:OPT_USER;optTerm:OPT_TERM;specification:SPECIFICATION;"
                     + "presrtNo:PRESRT_NO;regionCode:REGION_CODE;admType:ADM_TYPE;rxType:RX_TYPE;temporaryFlg:TEMPORARY_FLG;"
                     + "releaseFlg:RELEASE_FLG;linkmainFlg:LINKMAIN_FLG;linkNo:LINK_NO;orderCode:ORDER_CODE;orderDesc:ORDER_DESC;"
                     + "goodsDesc:GOODS_DESC;orderCat1Code:ORDER_CAT1_CODE;mediQty:MEDI_QTY;mediUnit:MEDI_UNIT;freqCode:FREQ_CODE;"
                     + "routeCode:ROUTE_CODE;takeDays:TAKE_DAYS;dosageQty:DOSAGE_QTY;dispenseQty:DISPENSE_QTY;dispenseUnit:DISPENSE_UNIT;dosageUnit:DOSAGE_UNIT;"
                     + "giveboxFlg:GIVEBOX_FLG;ownPrice:OWN_PRICE;nhiPrice:NHI_PRICE;discountRate:DISCOUNT_RATE;ownAmt:OWN_AMT;"
                     + "arAmt:AR_AMT;drNote:DR_NOTE;nsNote:NS_NOTE;drCode:DR_CODE;orderDate:ORDER_DATE;deptCode:DEPT_CODE;"
                     + "dcDrCode:DC_DR_CODE;dcOrderDate:DC_ORDER_DATE;dcDeptCode:DC_DEPT_CODE;execDeptCode:EXEC_DEPT_CODE;"
                     + "setmainFlg:SETMAIN_FLG;orderSetGroupNo:ORDERSET_GROUP_NO;ordersetCode:ORDERSET_CODE;hideFlg:HIDE_FLG;rpttypeCode:RPTTYPE_CODE;"
                     + "optitemCode:OPTITEM_CODE;devCode:DEV_CODE;fileNo:FILE_NO;degreeCode:DEGREE_CODE;urgentFlg:URGENT_FLG;"
                     + "inspayType:INSPAY_TYPE;phaType:PHA_TYPE;doseType:DOSE_TYPE;printtypeflgInfant:PRINTTYPEFLG_INFANT;"
                     + "expensiveFlg:EXPENSIVE_FLG;ctrldrugclassCode:CTRLDRUGCLASS_CODE;prescriptNo:PRESCRIPT_NO;atcFlg:ATC_FLG;"
                     + "sendatcdate:SENDATC_DATE;receiptNo:RECEIPT_NO;billFlg:BILL_FLG;billDate:BILL_DATE;"
                     + "billUser:BILL_USER;printFlg:PRINT_FLG;rexpCode:REXP_CODE;hexpCode:HEXP_CODE;contractCode:CONTRACT_CODE;"
                     + "ctz1Code:CTZ1_CODE;ctz2Code:CTZ2_CODE;ctz3Code:CTZ3_CODE;phaCheckCode:PHA_CHECK_CODE;phaCheckDate:PHA_CHECK_DATE;"
                     + "phaDosageCode:PHA_DOSAGE_CODE;phaDosageDate:PHA_DOSAGE_DATE;phaDispenseCode:PHA_DISPENSE_CODE;phaDispenseDate:PHA_DISPENSE_DATE;nsExecCode:NS_EXEC_CODE;"
                     + "nsExecDate:NS_EXEC_DATE;nsExecDept:NS_EXEC_DEPT;dctagentCode:DCTAGENT_CODE;dctexcepCode:DCTEXCEP_CODE;"
                     + "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT;agencyOrgCode:AGENCY_ORG_CODE;dctagentFlg:DCTAGENT_FLG;businessNo:BUSINESS_NO;chargeFlg:CHARGE_FLG;"//====pangben 2012-11-3 添加内部交易号码
                     + "decoctCode:DECOCT_CODE;phaRetnDate:PHA_RETN_DATE;mrCode:MR_CODE;phaRetnCode:PHA_RETN_CODE;execDrCode:EXEC_DR_CODE;mrNo:MR_NO;execFlg:EXEC_FLG;receiptFlg:RECEIPT_FLG;billType:BILL_TYPE;printNo:PRINT_NO;cat1Type:CAT1_TYPE;"
                     + "phaCheckCode:PHA_CHECK_CODE_OLD:OLD;phaCheckDate:PHA_CHECK_DATE_OLD:OLD;phaDosageCode:PHA_DOSAGE_CODE_OLD:OLD;phaDosageDate:PHA_DOSAGE_DATE_OLD:OLD;phaDispenseCode:PHA_DISPENSE_CODE_OLD:OLD;phaDispenseDate:PHA_DISPENSE_DATE_OLD:OLD");
    }

    /**
     * 新增医嘱
     *
     * @return Order
     */
    public Order newOrder() {
        Order order = new Order();
        if (this.getPrescriptionList() != null) {
            order.setCaseNo(getPrescriptionList().getCaseNo());
            order.setAdmType(getPrescriptionList().getAdmType());
            order.setDrCode(getPrescriptionList().getDrCode());
        }
        //处方类型
        order.setRxType(this.getRxType());
        //处方号
        order.setRxNo(this.getRxNo());
        maxseq = ++maxseq;
        order.setSeqNo(maxseq);
        order.setMedApplyNo(this.getMedApplyNo());
        //System.out.println("maxseq:="+maxseq);
        //设置服号
        order.setPresrtNo(this.getPrescriptionList().getGroupPrsptSize(this.
            getRxType()));
        //设置操作人
        order.setOptUser(Operator.getID());
        //设置端末
        order.setOptTerm(Operator.getIP());
        //设置区域
        order.setRegionCode(Operator.getRegion());
        // 开单医生
        order.setDrCode(prescriptionlist.getReg().getRealdrCode());
        // 开单科室
        order.setDeptCode(prescriptionlist.getReg().getRealdeptCode());
        // 开单时间
        order.setOrderDate(StringTool.getTimestamp(SystemTool.getInstance()
            .getDate()));
        //执行科室
        TParm parm = ClinicRoomTool.getInstance().getOrgCodeByRoomNo(
            prescriptionlist.getReg().getClinicroomNo());

        order.setExecDeptCode(parm.getValue("ORG_CODE", 0));
        order.setExecDrCode(Operator.getID());
        //设置病患对象
        order.setPat(getPat());
        //设置病患号
        order.setMrNo(getPat().getMrNo());
        //设置门急住别
        order.setAdmType(getAdmType());
        //设置收费注记
        order.setBillFlg("N");
        order.setTemporaryFlg("N");
        order.setReleaseFlg("N");
        order.setLinkmainFlg("N");
        order.setLinkNo("");
        order.setGiveboxFlg("N");
        order.setHideFlg("N");
        order.setUrgentFlg("N");
        order.setPrinttypeflgInfant("N");
        order.setAtcFlg("N");
        order.setPrintFlg("N");
        order.setDctagentFlg("N");
        this.newData(order);
        return order;
    }

    /**
     * 得到医嘱
     *
     * @param index
     *            int
     * @return Order
     */
    public Order getOrder(int index) {
        return (Order) get(index);
    }

    /**
     * 扩展用，目的同重载的方法
     *
     * @return TParm
     */
    public TParm getOrderListParm() {
        return getOrderListParm(null);
    }

    /**b
     * 取得TABLE用的数据
     *
     * @param parm
     * @return TParm
     */
    public TParm getOrderListParm(TParm parm) {
        if (parm == null)
            parm = new TParm();
        parm.addData("RX_NO", getRxNo());
        parm.addData("PAT_NAME", PatTool.getInstance()
                     .getNameForMrno(getMrNo()));
        parm.addData("SUM_FEE", this.getSumFee());
        parm.addData("MR_NO", getMrNo());
        parm.addData("DEPT_CODE", getDeptCode());
        parm.addData("DR_CODE", getDrCode());
        parm.addData("EXEC_DEPT_CODE", this.getExecDeptCode());
        parm.addData("PHA_CHECK_DATE", this.getPhaCheckDate());
        parm.addData("PHA_DOSAGE_DATE", this.getPhaDosageDate());
        parm.addData("PHA_DISPENSE_DATE", this.getPhaDispenseDate());
        parm.addData("PHA_RETN_DATE", this.getPhaRetnDate());
        parm.addData("PRESRT_NO", this.getPresrtNo());
        parm.addData("DCTAGENT_FLG", this.getDctagentFlg());
        parm.addData("DECOCT_CODE", this.getDecoctCode());
        parm.addData("PRESCRIPT_NbY", this.getDctTakeQty());
        parm.addData("FREQ_CODE", this.getFreqCode());
        parm.addData("ROUTE_CODE", this.getRouteCode());
        parm.addData("DCTAGENT_CODE", this.getDctAgentCode());
        parm.addData("DR_NOTE", this.getDrNote());
        parm.addData("TOT_GRAM", this.getTotGram());
        parm.addData("URGENT_FLG", this.getUrgentFlg());
        parm.addData("RELEASE_FLG", this.getReleaseFlg());
        parm.addData("PACKAGE_TOT", this.getPackageTot());
        parm.addData("RX_TYPE", this.getRxType());
        parm.addData("PHA_RETN_CODE", this.getPhaRetnCode());
        parm.setData("ACTION", "COUNT", parm.getCount("MR_NO"));
        parm.addData("COUNTER_NO", this.getCounterNo());
        parm.addData("EXEC_FLG", this.getExecFlg());
        parm.addData("RECEIPT_FLG", this.getReceiptFlg());
        parm.addData("PRINT_NO", this.getPrintNo());
        parm.addData("CAT1_TYPE", this.getCat1Type());
        parm.addData("BUSINESS_NO", this.getBusinessNo());//==pangben 2012-11-3
        parm.addData("CHARGE_FLG", this.getChargeFlg());
        parm.addData("DCT_TAKE_QTY", this.getDctTakeQty());// add by huangtt 20150427
        return parm;
    }

    public void removeData(int index) {
        if (maxseq > 1) {
            maxseq = --maxseq;
        }
        else {
            maxseq = 1;
        }
        super.removeData(index);

    }

    /**
     * 返回只有主项的TParm,其中新增一列为该条数据对应的Order
     * @return TParm
     */
    public TParm getTParmForTable() {
        TParm result = this.getParm(this.PRIMARY);
        //System.out.println("gettparmfortable"+result);
        Integer linkno;
        for (int i = result.getCount() - 1; i >= 0; i--) {
            linkno = result.getInt("LINK_NO", i);
            if (linkno != 0) {
                result.removeRow(i);
                continue;
            }
            result.setData("ORDER", i, this.getOrder(i));
        }
        return result;
    }

    /**
     * 初始化List
     *
     * @param parm
     * @return 真：成功，假：失败
     */
    public boolean initParm(TParm parm) {
        if (parm == null) {
            return false;
        }
        int count = parm.getCount();
        maxseq = TCM_Transform.getInt(parm.getData("SEQ_NO", count - 1));
        // 为PHA显示ORDERLIST时的“金额”做每个ORDER的累加
        double sumFee = 0.0;
        // 为PHA显示ORDERLIST时的“总克数”做每个ORDER的累加
        double totgram = 0.0;
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setPat(getPat());
            add(order);
            order.setDcOrder(true); //fudwadd
            if (!order.initParm(parm, i)) {
                return false;
            }

            // 累加PHA用“金额”
            sumFee += TCM_Transform.getDouble(order.getOwnAmt());
            // 总克数
            totgram += TCM_Transform.getDouble(order.getMediQty());
        }

        if (count > 0) {
            Order first = this.getOrder(0);
            if (first == null) {
                return false;
            }
            // 处方签号
            this.setRxNo(first.getRxNo());
            // 处方类型
            this.setRxType(first.getRxType());
            // 开立科室
            this.setDeptCode(first.getDeptCode());
            // 开立医生
            this.setDrCode(first.getDrCode());
            // 执行科室
            this.setExecDeptCode(first.getExecDeptCode());
            // 审核时间
            this.setPhaCheckDate(first.getPhaCheckDate());
            // 配药时间
            this.setPhaDosageDate(first.getPhaDosageDate());
            // 发药时间
            this.setPhaDispenseDate(first.getPhaDispenseDate());
            // 退药时间
            this.setPhaRetnDate(first.getPhaRetnDate());
            // 病案号
            if (getPat() != null) {
                this.setMrNo(this.getPat().getMrNo());
            }
            else {
                this.setMrNo(parm.getValue("MR_NO", 0));
            }
            // 服号
            this.setPresrtNo(first.getPresrtNo());
            // 代煎注记
            this.setDctagentFlg(first.getDctagentFlg());
            // 代煎科室
            this.setDecoctCode(first.getDecoctCode());
            // 代理执行科室
            this.setAgencyOrgCode(first.getAgencyOrgCode());
            // 领药号
            this.setPrescriptNo(first.getPrescriptNo());
            // 品种
            this.setVariety(TCM_Transform.getString(count));
            // 每个orderList的应付金额总计
            this.setSumFee(sumFee);
            // 付数/日份
            this.setTakeDays(first.getTakeDays());
            // 饮片使用计量(ml)
            this.setDctTakeQty(first.getDctTakeQty());
            // 频次
            this.setFreqCode(first.getFreqCode());
            // 用法
            this.setRouteCode(first.getRouteCode());
            // 煎药方式
            this.setDctAgentCode(first.getDctagentCode());
            // 备注
            this.setDrNote(first.getDrNote());
            // 总克数
            this.setTotGram(TCM_Transform.getString(totgram));
            // 急作
            this.setUrgentFlg(first.getUrgentFlg());
            //自备
            this.setReleaseFlg(first.getReleaseFlg());
            //退药人员
            this.setPhaRetnCode(first.getPhaRetnCode());
            //代煎总包数(付数*频次) todo:取得频次
            this.setPackageTot(0);
            //门急住别
            this.setAdmType(first.getAdmType());
            //领药窗口号
            this.setCounterNo(first.getCounterNo());
            //执行注记
            this.setExecFlg(first.getExecFlg());
            //打票注记
            this.setReceiptFlg(first.getReceiptFlg());
            //领药号
            this.setPrintNo(first.getPrintNo());
            //医嘱类别
            this.setCat1Type(first.getCat1Type());
            //内部交易号码=pangben 2012-11-3
            this.setBusinessNo(first.getBusinessNo());
            this.setChargeFlg(first.getChargeFlg());
        }
        return true;
    }

}
