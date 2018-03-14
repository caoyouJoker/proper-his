package jdo.opd;

import java.sql.*;

import com.dongyang.data.*;
import com.dongyang.util.*;
import jdo.sys.*;

/**
 *
 * <p>
 * Title: 药，诊疗项目，检验检查jdo
 * </p>
 *
 * <p>
 * drNote:药，诊疗项目，检验检查jdo
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800908
 * @version 1.0
 */
public class Order
    extends TModifiedData {
    /**
     * 病患对象
     */
    private Pat pat;
    /**
     * 病患号
     */
    private String mrNo;
    /**
     * 看诊序号
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * 处方签号
     */
    private StringValue rxNo = new StringValue(this);
    /**
     * 条码号
     */
    private StringValue medApplyNo = new StringValue(this);
    /**
     * 循序号
     */
    private IntValue seqNo = new IntValue(this);
    /**
     * 操作人員
     */
    private StringValue optUser = new StringValue(this);
    /**
     * 操作日期
     */
    private TimestampValue optDate = new TimestampValue(this);
    /**
     * '操作端末
     */
    private StringValue optTerm = new StringValue(this);
    /**
     * 服号
     */
    private IntValue presrtNo = new IntValue(this);
    /**
     * 就诊区域
     */
    private StringValue regionCode = new StringValue(this);
    /**
     * 就诊状态
     */
    private StringValue admType = new StringValue(this);
    /**
     * 医嘱分类
     */
    private StringValue rxType = new StringValue(this);
    /**
     * 暂存注记
     */
    private StringValue temporaryFlg = new StringValue(this);
    /**
     * '交付注记 (自备)'
     */
    private StringValue releaseFlg = new StringValue(this);
    /**
     * 主连结注记
     */
    private StringValue linkmainFlg = new StringValue(this);
    /**
     * 连接号
     */
    private StringValue linkNo = new StringValue(this);
    /**
     * 医令代码
     */
    private StringValue orderCode = new StringValue(this);
    /**
     * 医令名称
     */
    private StringValue orderDesc = new StringValue(this);
    /**
     * 商品名
     */
    private StringValue goodsDesc = new StringValue(this);
    /**
     * 医令细分类
     */
    private StringValue orderCat1Code = new StringValue(this);
    /**
     * 用量
     */
    private DoubleValue mediQty = new DoubleValue(this);
    /**
     * 开药单位
     */
    private StringValue mediUnit = new StringValue(this);
    /**
     * 频次代码
     */
    private StringValue freqCode = new StringValue(this);
    /**
     * 给药途径代码
     */
    private StringValue routeCode = new StringValue(this);
    /**
     * 付數/日份
     */
    private IntValue takeDays = new IntValue(this);
    /**
     * 总量
     */
    private DoubleValue dosageQty = new DoubleValue(this);
    /**
     * 实际发药量
     */
    private DoubleValue dispenseQty = new DoubleValue(this);
    /**
     * 发药单位
     */
    private StringValue dispenseUnit = new StringValue(this);
    /**
     * 库存单位发药注记
     */
    private StringValue giveboxFlg = new StringValue(this);
    /**
     * 自费价
     */
    private DoubleValue ownPrice = new DoubleValue(this);
    /**
     * 医保价
     */
    private DoubleValue nhiPrice = new DoubleValue(this);
    /**
     * 身份折扣%
     */
    private DoubleValue discountRate = new DoubleValue(this);
    /**
     * 自费金额
     */
    private DoubleValue ownAmt = new DoubleValue(this);
    /**
     * 应付金额
     */
    private DoubleValue arAmt = new DoubleValue(this);
    /**
     * 备注栏
     */
    private StringValue drNote = new StringValue(this);
    /**
     * 护士备注
     */
    private StringValue nsNote = new StringValue(this);
    /**
     * 开单医师
     */
    private StringValue drCode = new StringValue(this);
    /**
     * \开单日期时间
     */
    private TimestampValue orderDate=new TimestampValue(this) ;
    /**
     * 开单科室
     */
    private StringValue deptCode = new StringValue(this);
    /**
     * 停用医师
     */
    private StringValue dcDrCode = new StringValue(this);
    /**
     * 停用时间
     */
    private TimestampValue dcOrderDate=new TimestampValue(this) ;
    /**
     * 停用科室
     */
    private StringValue dcDeptCode = new StringValue(this);
    /**
     * 执行科室
     */
    private StringValue execDeptCode = new StringValue(this);
    /**
     * 集和医令注记
     */
    private StringValue setmainFlg = new StringValue(this);
    /**
     * 集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     */
    private IntValue orderSetGroupNo = new IntValue(this);
    /**
     * 集合医令代码
     */
    private StringValue ordersetCode = new StringValue(this);
    /**
     * 隐藏注记
     */
    private StringValue hideFlg = new StringValue(this);
    /**
     * 报告类别
     */
    private StringValue rpttypeCode = new StringValue(this);
    /**
     * 检体
     */
    private StringValue optitemCode = new StringValue(this);
    /**
     * 仪器代码
     */
    private StringValue devCode = new StringValue(this);
    /**
     * 申请单模板
     */
    private StringValue mrCode = new StringValue(this);
    /**
     * 结构化申请单序号
     */
    private IntValue fileNo = new IntValue(this);
    /**
     * 医检绩效代码
     */
    private StringValue degreeCode = new StringValue(this);
    /**
     * 急作注记
     */
    private StringValue urgentFlg = new StringValue(this);
    /**
     * 给付类别
     */
    private StringValue inspayType = new StringValue(this);
    /**
     * 药品种类
     */
    private StringValue phaType = new StringValue(this);
    /**
     * 剂型大分类
     */
    private StringValue doseType = new StringValue(this);
    /**
     * 儿童处方签注记
     */
    private StringValue printtypeflgInfant = new StringValue(this);
    /**
     * 贵重注记
     */
    private StringValue expensiveFlg = new StringValue(this);
    /**
     * 管制药品级别
     */
    private StringValue ctrldrugclassCode = new StringValue(this);
    /**
     * 领药号
     */
    private IntValue prescriptNo = new IntValue(this);
    /**
     * 送包药机注记
     */
    private StringValue atcFlg = new StringValue(this);
    /**
     * 送包药机时间
     */
    private TimestampValue sendatcdate=new TimestampValue(this);
    /**
     * 门急诊收据号
     */
    private StringValue receiptNo = new StringValue(this);
    /**
     * 批价注记
     */
    private StringValue billFlg = new StringValue(this);
    /**
     * 收费日期时间
     */
    private TimestampValue billDate=new TimestampValue(this);
    /**
     * 收费人员
     */
    private StringValue billUser = new StringValue(this);
    /**
     * 票据打印注记
     */
    private StringValue printFlg = new StringValue(this);
    /**
     * 收据费用代号
     */
    private StringValue rexpCode = new StringValue(this);
    /**
     * 院内费用代号
     */
    private StringValue hexpCode = new StringValue(this);
    /**
     * 支付合同单位代码
     */
    private StringValue contractCode = new StringValue(this);
    /**
     * 身分
     */
    private StringValue ctz1Code = new StringValue(this);
    /**
     * 折扣1
     */
    private StringValue ctz2Code = new StringValue(this);
    /**
     * 折扣2
     */
    private StringValue ctz3Code = new StringValue(this);
    /**
     * 审核药师
     */
    private StringValue phaCheckCode = new StringValue(this);
    /**
     * 审核时间
     */
    private TimestampValue phaCheckDate=new TimestampValue(this);
    /**
     * 配药药师
     */
    private StringValue phaDosageCode = new StringValue(this);
    /**
     * 配药时间
     */
    private TimestampValue phaDosageDate=new TimestampValue(this);
    /**
     * \发药药师
     */
    private StringValue phaDispenseCode = new StringValue(this);
    /**
     * 发药确认时间
     */
    private TimestampValue phaDispenseDate=new TimestampValue(this);
    /**
     * 医嘱确认护士
     */
    private StringValue nsExecCode= new StringValue(this);
    /**
     * 医嘱确认时间
     */
    private TimestampValue nsExecDate=new TimestampValue(this);
    /**
     * 医嘱确认科室
     */
    private StringValue nsExecDept = new StringValue(this);
    /**
     * 煎药方式
     */
    private StringValue dctagentCode = new StringValue(this);
    /**
     * 特殊煎法
     */
    private StringValue dctexcepCode = new StringValue(this);
    /**
     * 饮片服用量(ml) or 饮片使用计量'
     */
    private IntValue dctTakeQty = new IntValue(this);
    /**
     * 代煎总包数
     */
    private IntValue packageTot = new IntValue(this);
    /**
     * 代理发退药药房
     */
    private StringValue agencyOrgCode = new StringValue(this);
    /**
     * 代煎注记
     */
    private StringValue dctagentFlg = new StringValue(this);
    /**
     * 煎药室
     */
    private StringValue decoctCode = new StringValue(this);
    /**
     * 执行医生
     */
    private StringValue execDrCode=new StringValue(this);
    /**
     * 退药时间（PHA专用）
     */
    private TimestampValue phaRetnDate=new TimestampValue(this);
    /**
     * 执行注记（PHA专用）
     */
    private boolean exeFlg=true;
    /**
     * 退药人员
     */
    private StringValue phaRetnCode=new StringValue(this);
    /**
     * 规格
     */
    private StringValue specification=new StringValue(this);
    /**
     * 计费单位
     */
    private StringValue dosageUnit =new StringValue(this);
    /**
     * 收费标记
     */
    private BooleanValue chargeFlg = new BooleanValue(this);
    //记录医嘱是医生开立
    private boolean dcOredr=false;

    /**
     * 领药窗口号
     */
    private IntValue counterNo = new IntValue(this);

    /**
     * 执行注记
     */
    private StringValue execFlg =new StringValue(this);

    /**
     * 领药号
     */
    private StringValue printNo = new StringValue(this);
    /**
     * 医嘱细分类群组
     */
    private StringValue cat1Type = new StringValue(this);
    /**
     * 成本中心
     */
    private StringValue costCenterCode = new StringValue(this);

    /**
     * 医保代码(门)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeO = new StringValue(this);
    /**
     * 医保代码(急)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeE = new StringValue(this);
    /**
     * 医保代码(住)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeI = new StringValue(this);
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
     * 取得领药号
     * @return String
     */
    public String getPrintNo() {
        return printNo.getValue();
    }

    /**
     * 设置领药号
     * @param value String
     */
    public void setPrintNo(String value) {
        printNo.setValue(value);
    }
    /**
     * 取得执行注记
     * @return String
     */
    public String getExecFlg() {
        return execFlg.getValue();
    }

    /**
     * 设置执行注记
     * @param value String
     */
    public void setExecFlg(String value) {
        execFlg.setValue(value);
    }

    /**
     * 打票注记
     */
    private StringValue receiptFlg =new StringValue(this);

    /**
     * 得到打票注记
     * @return String
     */
    public String getReceiptFlg() {
        return receiptFlg.getValue();
    }

    /**
     * 设置打票注记
     * @param value String
     */
    public void setReceiptFlg(String value) {
        receiptFlg.setValue(value);
    }
    /**
     * 收费类别
     */
    private StringValue billType = new StringValue(this);

    /**
     * 得到收费类别
     * @return String
     */
    public String getBillType() {
        return billType.getValue();
    }

    /**
     * 设置收费类别
     * @param value String
     */
    public void setBillType(String value) {
        this.billType.setValue(value);
    }
    /**
     * 修改收费类别
     * @param value String
     */

    public void modifyBillType(String value) {
        this.billType.modifyValue(value);
    }

    /**
     * 取得领药窗口号
     * @return int
     */
    public int getCounterNo() {
        return counterNo.getValue();
    }

    /**
     * 设置领药窗口号
     * @param value int
     */
    public void setCounterNo(int value) {
        counterNo.setValue(value);
    }

    //设置医生开立标记
    public void setDcOrder(boolean dcOredr){
        this.dcOredr=dcOredr;
    }
    //得到医生开立医嘱注记
    public boolean getDcOrder() {
        return this.dcOredr;
    }
    /**
     * 得到病人
     * @return Pat
     */
    public Pat getPat() {
        return pat;
    }

    /**
     * 设置病人
     * @param pat Pat
     */
    public void setPat(Pat pat) {
        this.pat = pat;
    }
    /**
     * 设置病患号
     * @param mrNo String
     */
    public void setMrNo(String mrNo){
    	if(getPat()==null)
    		this.mrNo="";
    	this.mrNo=mrNo;
    }
    /**
     * 得到病患号
     * @return mrNo String
     */
    public String getMrNo(){
    	return this.mrNo;
    }
    /**
     * 得到看诊序号
     * @return String
     */
    public String getCaseNo() {
        return caseNo.getValue();
    }

    /**
     * 设置看诊序号
     * @param value String
     */
    public void setCaseNo(String value) {
        this.caseNo.setValue(value);
    }

    /**
     * 得到处方号
     * @return String
     */
    public String getRxNo() {
        return rxNo.getValue();
    }
    /**
     * 得到条码号
     * @return String
     */
    public String getMedApplyNo(){
    return medApplyNo.getValue();
    }
    /**
     * 得到医嘱细分类群组
     * @return String
     */
    public String getCat1Type() {
        return cat1Type.getValue();
    }
    /**
     * 设置医嘱细分类群组
     * @param value String
     */
    public void setCat1Type(String value) {
        this.cat1Type.setValue(value);
    }

    /**
     * 设置处方号
     * @param value String
     */
    public void setRxNo(String value) {
        this.rxNo.setValue(value);
    }
    /**
     * 设置条码号
     * @param value String
     */
    public void setMedApplyNo(String value){
        this.medApplyNo.setValue(value);
}
    /**
     * 得到顺序号
     * @return int
     */
    public int getSeqNo() {
        return seqNo.getValue();
    }

    /**
     * 设置顺序号
     * @param value int
     */
    public void setSeqNo(int value) {
        this.seqNo.setValue(value);
    }

    /**
     * 得到操作人员
     * @return String
     */
    public String getOptUser() {
        return optUser.getValue();
    }

    /**
     * 设置操作人员
     * @param value String
     */
    public void setOptUser(String value) {
        this.optUser.setValue(value);
    }

    /**
     * 得到操作日期
     * @return String Timestamp
     */
    public Timestamp getOptDate() {
        return optDate.getValue();
    }
    /**
     * 设置操作日期
     */
    public void setOptDate() {
        this.optDate.setValue(StringTool.getTimestamp(SystemTool.getInstance().getDate()));
    }

    /**
     * 得到操作端末
     * @return String
     */
    public String getOptTerm() {
        return optTerm.getValue();
    }

    /**
     * 设置操作端末
     * @param value String
     */
    public void setOptTerm(String value) {
        this.optTerm.setValue(value);
    }

    /**
     * 得到服号
     * @return int
     */
    public int getPresrtNo() {
        return presrtNo.getValue();
    }

    /**
     * 设置服号
     * @param value int
     */
    public void setPresrtNo(int value) {
        this.presrtNo.setValue(value);
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode.getValue();
    }

    /**
     * 设置区域
     * @param value String
     */
    public void setRegionCode(String value) {
        this.regionCode.setValue(value);
    }


    /**
     * 得到就诊状态
     * @return String
     */
    public String getAdmType() {
        return admType.getValue();
    }

    /**
     * 设置就诊状态
     * @param value String
     */
    public void setAdmType(String value) {
        this.admType.setValue(value);
    }

    /**
     * 得到医嘱分类
     * @return String
     */
    public String getRxType() {
        return rxType.getValue();
    }

    /**
     * 设置医嘱分类
     * @param value String
     */
    public void setRxType(String value) {
        this.rxType.setValue(value);
    }

    /**
     * 得到暂存注记
     * @return String
     */
    public String getTemporaryFlg() {
        return temporaryFlg.getValue();
    }

    /**
     * 设置暂存注记
     * @param value String
     */
    public void setTemporaryFlg(String value) {
        this.temporaryFlg.setValue(value);
    }

    /**
     * 得到交付注记
     * @return String
     */
    public String getReleaseFlg() {
        return releaseFlg.getValue();
    }

    /**
     * 设置交付注记
     * @param value String
     */
    public void setReleaseFlg(String value) {
        this.releaseFlg.setValue(value);
    }

    /**
     * 得到主连结注记
     * @return String
     */

    public String getLinkmainFlg() {
        return linkmainFlg.getValue();
    }

    /**
     * 设置主连结注记
     * @param value String
     */
    public void setLinkmainFlg(String value) {
        this.linkmainFlg.setValue(value);
    }

    /**
     * 得到连接号
     * @return String
     */
    public String getLinkNo() {
        return linkNo.getValue();
    }

    /**
     * 设置连接号
     * @param value String
     */
    public void setLinkNo(String value) {
        this.linkNo.setValue(value);
    }

    /**
     * 得到医令代码
     * @return String
     */
    public String getOrderCode() {
        return orderCode.getValue();
    }

    /**
     * 设置医令代码
     * @param value String
     */
    public void setOrderCode(String value) {
        this.orderCode.setValue(value);
    }
    /**
     * 得到计量单位
     * @return  dosageUnit String
     */
    public String getDosageUnit(){
    	return this.dosageUnit.getValue();
    }
    /**
     * 设置计量单位
     * @param dosageUnit String
     */
    public void setDosageUnit(String dosageUnit){
    	this.dosageUnit.setValue(dosageUnit);
    }
    /**
     * 修改计量单位
     * @param dosageUnit String
     */
    public void modifyDosageUnit(String dosageUnit){
    	this.dosageUnit.modifyValue(dosageUnit);
    }
    /**
     * 得到医令名称
     * @return String
     */
    public String getOrderDesc() {
        return orderDesc.getValue();
    }

    /**
     * 设置医令名称
     * @param value String
     */
    public void setOrderDesc(String value) {
        this.orderDesc.setValue(value);
    }

    /**
     * 得到商品名
     * @return String
     */
    public String getGoodsDesc() {
        return goodsDesc.getValue();
    }

    /**
     * 设置商品名
     * @param value String
     */
    public void setGoodsDesc(String value) {
        this.goodsDesc.setValue(value);
    }

    /**
     * 得到医令细分类
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code.getValue();
    }

    /**
     * 设置医令细分类
     * @param value String
     */
    public void setOrderCat1Code(String value) {
        this.orderCat1Code.setValue(value);
    }

    /**
     * 得到用量
     * @return double
     */
    public double getMediQty() {
        return mediQty.getValue();
    }

    /**
     * 设置用量
     * @param value double
     */
    public void setMediQty(double value) {
        this.mediQty.setValue(value);
    }

    /**
     * 得到开药单位
     * @return String
     */
    public String getMediUnit() {
        return mediUnit.getValue();
    }

    /**
     * 设置开药单位
     * @param value String
     */
    public void setMediUnit(String value) {
        this.mediUnit.setValue(value);
    }

    /**
     * 得到频次代码
     * @return String
     */
    public String getFreqCode() {
        return freqCode.getValue();
    }

    /**
     * 设置频次代码
     * @param value String
     */
    public void setFreqCode(String value) {
        this.freqCode.setValue(value);
    }

    /**
     * 得到给药途径代码
     * @return String
     */
    public String getRouteCode() {
        return routeCode.getValue();
    }

    /**
     * 设置给药途径代码
     * @param value String
     */
    public void setRouteCode(String value) {
        this.routeCode.setValue(value);
    }

    /**
     * 得到付數/日份'
     * @return int
     */
    public int getTakeDays() {
        return takeDays.getValue();
    }

    /**
     * 设置付數/日份'
     * @param value int
     */
    public void setTakeDays(int value) {
        this.takeDays.setValue(value);
    }

    /**
     * 得到总量
     * @return Double
     */
    public double getDosageQty() {
        return dosageQty.getValue();
    }

    /**
     * 设置总量
     * @param value double
     */
    public void setDosageQty(double value) {
        this.dosageQty.setValue(value);
    }

    /**
     * 得到实际发药量
     * @return double
     */
    public double getDispenseQty() {
        return dispenseQty.getValue();
    }
    /**
     * 设置实际发药量
     * @param dispenseQty double
     */
    public void setDispenseQty(double dispenseQty) {
        this.dispenseQty.setValue(dispenseQty);
    }

    /**
     * 得到发药单位
     * @return String
     */
    public String getDispenseUnit() {
        return dispenseUnit.getValue();
    }

    /**
     * 设置发药单位
     * @param value String
     */
    public void setDispenseUnit(String value) {
        this.dispenseUnit.setValue(value);
    }

    /**
     * 得到库存单位发药注记
     * @return String
     */
    public String getGiveboxFlg() {
        return giveboxFlg.getValue();
    }

    /**
     * 设置库存单位发药注记
     * @param value String
     */
    public void setGiveboxFlg(String value) {
        this.giveboxFlg.setValue(value);
    }

    /**
     * 得到自费价
     * @return double
     */
    public double getOwnPrice() {
        return ownPrice.getValue();
    }

    /**
     * 设置自费价
     * @param value double
     */
    public void setOwnPrice(double value) {
        this.ownPrice.setValue(value);
    }

    /**
     * 得到医保价
     * @return double
     */
    public double getNhiPrice() {
        return nhiPrice.getValue();
    }

    /**
     * 设置医保价
     * @param value double
     */
    public void setNhiPrice(double value) {
        this.nhiPrice.setValue(value);
    }

    /**
     * 得到身份折扣%
     * @return double
     */
    public double getDiscountRate() {
        return discountRate.getValue();
    }

    /**
     * 设置身份折扣%
     * @param value double
     */
    public void setDiscountRate(double value) {
        this.discountRate.setValue(value);
    }

    /**
     * 得到自费金额
     * @return double
     */
    public double getOwnAmt() {
        return ownAmt.getValue();
    }

    /**
     * 设置自费金额
     * @param value double
     */
    public void setOwnAmt(double value) {
        this.ownAmt.setValue(value);
    }

    /**
     * 得到应付金额
     * @return double
     */
    public double getArAmt() {
        return arAmt.getValue();
    }

    /**
     * 设置应付金额
     * @param value double
     */
    public void setArAmt(double value) {
        this.arAmt.setValue(value);
    }

    /**
     * 得到备注栏
     * @return String
     */
    public String getDrNote() {
        return drNote.getValue();
    }

    /**
     * 设置备注栏
     * @param value String
     */
    public void setDrNote(String value) {
        this.drNote.setValue(value);
    }

    /**
     * 得到护士备注
     * @return String
     */
    public String getNsNote() {
        return nsNote.getValue();
    }

    /**
     * 设置护士备注
     * @param value String
     */
    public void setNsNote(String value) {
        this.nsNote.setValue(value);
    }

    /**
     * 得到开单医师
     * @return String
     */
    public String getDrCode() {
        return drCode.getValue();
    }

    /**
     * 设置开单医师
     * @param value String
     */
    public void setDrCode(String value) {
        this.drCode.setValue(value);
    }

    /**
     * 得到 开单日期时间
     * @return Timestamp
     */
    public Timestamp getOrderDate() {
        return orderDate.getValue();
    }

    /**
     * 设置开单日期时间
     * @param value Timestamp
     */
    public void setOrderDate(Timestamp value) {
        this.orderDate.setValue(value);
    }

    /**
     * 得到 开单科室
     * @return String
     */
    public String getDeptCode() {
        return deptCode.getValue();
    }

    /**
     * 设置开单科室
     * @param value String
     */
    public void setDeptCode(String value) {
        this.deptCode.setValue(value);
    }

    /**
     * 得到停用医师
     * @return String
     */
    public String getDcDrCode() {
        return dcDrCode.getValue();
    }

    /**
     * 设置停用医师
     * @param value String
     */
    public void setDcDrCode(String value) {
        this.dcDrCode.setValue(value);
    }

    /**
     * 得到停用时间
     * @return Timestamp
     */
    public Timestamp getDcOrderDate() {
        return dcOrderDate.getValue();
    }

    /**
     * 设置停用时间
     * @param value Timestamp
     */
    public void setDcOrderDate(Timestamp value) {
        this.dcOrderDate.setValue(value);
    }

    /**
     * 得到停用科室
     * @return String
     */
    public String getDcDeptCode() {
        return dcDeptCode.getValue();
    }

    /**
     * 设置停用科室
     * @param value String
     */
    public void setDcDeptCode(String value) {
        this.dcDeptCode.setValue(value);
    }

    /**
     * 得到执行科室
     * @return String
     */
    public String getExecDeptCode() {
        return execDeptCode.getValue();
    }

    /**
     * \设置执行科室
     * @param value String
     */
    public void setExecDeptCode(String value) {
        this.execDeptCode.setValue(value);
    }

    /**
     * 得到集和医令注记
     * @return String
     */
    public String getSetmainFlg() {
        return setmainFlg.getValue();
    }

    /**
     * 设置集和医令注记
     * @param value String
     */
    public void setSetmainFlg(String value) {
        this.setmainFlg.setValue(value);
    }

    /**
     * 得到集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     * @return int
     */
    public int getOrderSetGroupNo() {
        return orderSetGroupNo.getValue();
    }

    /**
     * 设置集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     * @param value int
     */
    public void setOrderSetGroupNo(int value) {
        this.orderSetGroupNo.setValue(value);
    }

    /**
     * 得到集合医令代码
     * @return String
     */
    public String getOrdersetCode() {
        return ordersetCode.getValue();
    }

    /**
     * 设置集合医令代码
     * @param value String
     */
    public void setOrdersetCode(String value) {
        this.ordersetCode.setValue(value);
    }

    /**
     * 得到隐藏注记
     * @return String
     */
    public String getHideFlg() {
        return hideFlg.getValue();
    }

    /**
     * 设置隐藏注记
     * @param value String
     */
    public void setHideFlg(String value) {
        this.hideFlg.setValue(value);
    }

    /**
     * 得到报告类别
     * @return String
     */
    public String getRpttypeCode() {
        return rpttypeCode.getValue();
    }

    /**
     * 设置报告类别
     * @param value String
     */
    public void setRpttypeCode(String value) {
        this.rpttypeCode.setValue(value);
    }

    /**
     * 得到检体
     * @return String
     */
    public String getOptitemCode() {
        return optitemCode.getValue();
    }

    /**
     * 设置检体
     * @param value String
     */
    public void setOptitemCode(String value) {
        this.optitemCode.setValue(value);
    }

    /**
     * 得到仪器代码
     * @return String
     */
    public String getDevCode() {
        return devCode.getValue();
    }

    /**
     * 设置仪器代码
     * @param value String
     */
    public void setDevCode(String value) {
        this.devCode.setValue(value);
    }

    /**
     * 得到申请单模板
     * @return String
     */
    public String getMrCode() {
        return mrCode.getValue();
    }

    /**
     * 设置申请单模板
     * @param value String
     */
    public void setMrCode(String value) {
        this.mrCode.setValue(value);
    }

    /**
     * 得到结构化申请单序号
     * @return int
     */
    public int getFileNo() {
        return fileNo.getValue();
    }

    /**
     * 设置结构化申请单序号
     * @param value int
     */
    public void setFileNo(int value) {
        this.fileNo.setValue(value);
    }

    /**
     * 得到医检绩效代码
     * @return String
     */
    public String getDegreeCode() {
        return degreeCode.getValue();
    }

    /**
     * 设置医检绩效代码
     * @param value String
     */
    public void setDegreeCode(String value) {
        this.degreeCode.setValue(value);
    }

    /**
     * 得到急作注记
     * @return String
     */
    public String getUrgentFlg() {
        return urgentFlg.getValue();
    }

    /**
     * 设置急作注记
     * @param value String
     */
    public void setUrgentFlg(String value) {
        this.urgentFlg.setValue(value);
    }

    /**
     * 得到给付类别
     * @return String
     */
    public String getInspayType() {
        return inspayType.getValue();
    }

    /**
     * 设置给付类别
     * @param value String
     */
    public void setInspayType(String value) {
        this.inspayType.setValue(value);
    }

    /**
     * 得到药品种类
     * @return String
     */
    public String getPhaType() {
        return phaType.getValue();
    }

    /**
     * 设置药品种类
     * @param value String
     */
    public void setPhaType(String value) {
        this.phaType.setValue(value);
    }

    /**
     * 得到剂型大分类
     * @return String
     */
    public String getDoseType() {
        return doseType.getValue();
    }

    /**
     * 设置剂型大分类
     * @param value String
     */
    public void setDoseType(String value) {
        this.doseType.setValue(value);
    }


    /**
     * 得到儿童处方签注记
     * @return String
     */
    public String getPrinttypeflgInfant() {
        return printtypeflgInfant.getValue();
    }

    /**
     * 设置儿童处方签注记
     * @param value String
     */
    public void setPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.setValue(value);
    }

    /**
     * 得到贵重注记
     * @return String
     */
    public String getExpensiveFlg() {
        return expensiveFlg.getValue();
    }

    /**
     * 设置贵重注记
     * @param value String
     */
    public void setExpensiveFlg(String value) {
        this.expensiveFlg.setValue(value);
    }

    /**
     * 得到管制药品级别
     * @return String
     */
    public String getCtrldrugclassCode() {
        return ctrldrugclassCode.getValue();
    }

    /**
     * 设置管制药品级别
     * @param value String
     */
    public void setCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.setValue(value);
    }

    /**
     * 得到领药号
     * @return int
     */
    public int getPrescriptNo() {
        return prescriptNo.getValue();
    }

    /**
     * 设置领药号
     * @param value int
     */
    public void setPrescriptNo(int value) {
        this.prescriptNo.setValue(value);
    }

    /**
     * 得到送包药机注记
     * @return String
     */
    public String getAtcFlg() {
        return atcFlg.getValue();
    }

    /**
     * 设置送包药机注记
     * @param value String
     */
    public void setAtcFlg(String value) {
        this.atcFlg.setValue(value);
    }

    /**
     * 得到送包药机时间
     * @return Timestamp
     */
    public Timestamp getSendatcdate() {
        return sendatcdate.getValue();
    }

    /**
     * 设置送包药机时间
     * @param value Timestamp
     */
    public void setSendatcdate(Timestamp value) {
        this.sendatcdate.setValue(value);
    }


    /**
     * 得到门急诊收据号
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo.getValue();
    }

    /**
     * 设置门急诊收据号
     * @param value String
     */
    public void setReceiptNo(String value) {
        this.receiptNo.setValue(value);
    }

    /**
     * 得到批价注记
     * @return String
     */
    public String getBillFlg() {
        return billFlg.getValue();
    }

    /**
     * 设置批价注记
     * @param value String
     */
    public void setBillFlg(String value) {
        this.billFlg.setValue(value);
    }

    /**
     * 得到收费日期时间
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate.getValue();
    }

    /**
     * 设置收费日期时间
     * @param value Timestamp
     */
    public void setBillDate(Timestamp value) {
        this.billDate.setValue(value);
    }

    /**
     * 得到收费人员
     * @return String
     */
    public String getBillUser() {
        return billUser.getValue();
    }

    /**
     * 设置收费人员
     * @param value String
     */
    public void setBillUser(String value) {
        this.billUser.setValue(value);
    }

    /**
     * 得到票据打印注记
     * @return String
     */
    public String getPrintFlg() {
        return printFlg.getValue();
    }

    /**
     * 设置票据打印注记
     * @param value String
     */
    public void setPrintFlg(String value) {
        this.printFlg.setValue(value);
    }

    /**
     * 得到收据费用代号
     * @return String
     */
    public String getRexpCode() {
        return rexpCode.getValue();
    }

    /**
     * 设置收据费用代号
     * @param value String
     */
    public void setRexpCode(String value) {
        this.rexpCode.setValue(value);
    }

    /**
     * 得到院内费用代号
     * @return String
     */
    public String getHexpCode() {
        return hexpCode.getValue();
    }

    /**
     * 设置院内费用代号
     * @param value String
     */
    public void setHexpCode(String value) {
        this.hexpCode.setValue(value);
    }

    /**
     * 得到支付合同单位代码
     * @return String
     */
    public String getContractCode() {
        return contractCode.getValue();
    }

    /**
     * 设置支付合同单位代码
     * @param value String
     */
    public void setContractCode(String value) {
        this.contractCode.setValue(value);
    }

    /**
     * \得到身分
     * @return String
     */
    public String getCtz1Code() {
        return ctz1Code.getValue();
    }

    /**
     * 设置身分
     * @param value String
     */
    public void setCtz1Code(String value) {
        this.ctz1Code.setValue(value);
    }

    /**
     * 得到折扣1
     * @return String
     */
    public String getCtz2Code() {
        return ctz2Code.getValue();
    }

    /**
     * 设置折扣1
     * @param value String
     */
    public void setCtz2Code(String value) {
        this.ctz2Code.setValue(value);
    }

    /**
     * 得到折扣2
     * @return String
     */
    public String getCtz3Code() {
        return ctz3Code.getValue();
    }

    /**
     * 设置折扣2
     * @param value String
     */
    public void setCtz3Code(String value) {
        this.ctz3Code.setValue(value);
    }

    /**
     * 得到审核药师
     * @return String
     */
    public String getPhaCheckCode() {
        return phaCheckCode.getValue();
    }

    /**
     * 设置审核药师
     * @param value String
     */
    public void setPhaCheckCode(String value) {
        this.phaCheckCode.setValue(value);
    }

    /**
     * 得到审核时间
     * @return Timestamp
     */
    public Timestamp getPhaCheckDate() {
        return phaCheckDate.getValue();
    }

    /**
     * 设置审核时间
     * @param value Timestamp
     */
    public void setPhaCheckDate(Timestamp value) {
        this.phaCheckDate.setValue(value);
    }

    /**
     * 得到配药药师
     * @return String
     */
    public String getPhaDosageCode() {
        return phaDosageCode.getValue();
    }

    /**
     * 设置配药药师
     * @param value String
     */
    public void setPhaDosageCode(String value) {
        this.phaDosageCode.setValue(value);
    }

    /**
     * 得到配药时间
     * @return Timestamp
     */
    public Timestamp getPhaDosageDate() {
        return phaDosageDate.getValue();
    }

    /**
     * 设置配药时间
     * @param value Timestamp
     */
    public void setPhaDosageDate(Timestamp value) {
        this.phaDosageDate.setValue(value);
    }

    /**
     * 得到发药药师
     * @return String
     */
    public String getPhaDispenseCode() {
        return phaDispenseCode.getValue();
    }

    /**
     * 设置发药药师
     * @param value String
     */
    public void setPhaDispenseCode(String value) {
        this.phaDispenseCode.setValue(value);
    }

    /**
     * 得到发药确认时间
     * @return Timestamp
     */
    public Timestamp getPhaDispenseDate() {
        return phaDispenseDate.getValue();
    }
    /**
     * 设置规格
     * @param specification String
     */
    public void setSpecification(String specification){
    	this.specification.setValue(specification);
    }
    /**
     * 得到规格
     * @return specification String
     */
    public String getSpecification(){
    	return this.specification.getValue();
    }
    /**
     * 修改规格
     * @param specification String
     */
    public void modifySpecification(String specification){
    	this.specification.modifyValue(specification);
    }


    /**
     * 得到医嘱确认护士
     * @return String
     */
    public String getNsExecCode() {
        return nsExecCode.getValue();
    }

    /**
     * 设置医嘱确认护士
     * @param value String
     */
    public void setNsExecCode(String value) {
        this.nsExecCode.setValue(value);
    }

    /**
     * 得到医嘱确认时间
     * @return Timestamp
     */
    public Timestamp getNsExecDate() {
        return nsExecDate.getValue();
    }

    /**
     * 设置医嘱确认时间
     * @param value Timestamp
     */
    public void setNsExecDate(Timestamp value) {
        this.nsExecDate.setValue(value);
    }

    /**
     * 得到医嘱确认科室
     * @return String
     */
    public String getNsExecDept() {
        return nsExecDept.getValue();
    }

    /**
     * 设置医嘱确认科室
     * @param value String
     */
    public void setNsExecDept(String value) {
        this.nsExecDept.setValue(value);
    }

    /**
     * 得到煎药方式
     * @return String
     */
    public String getDctagentCode() {
        return dctagentCode.getValue();
    }

    /**
     * 设置煎药方式
     * @param value String
     */
    public void setDctagentCode(String value) {
        this.dctagentCode.setValue(value);
    }

    /**
     * 得到特殊煎法
     * @return String
     */
    public String getDctexcepCode() {
        return dctexcepCode.getValue();
    }

    /**
     * 设置特殊煎法
     * @param value String
     */
    public void setDctexcepCode(String value) {
        this.dctexcepCode.setValue(value);
    }

    /**
     * 得到饮片服用量
     * @return int
     */
    public int getDctTakeQty() {
        return dctTakeQty.getValue();
    }

    /**
     * 设置饮片服用量
     * @param value int
     */
    public void setDctTakeQty(int value) {
        this.dctTakeQty.setValue(value);
    }

    /**
     * 得到代煎总包数
     * @return int
     */
    public int getPackageTot() {
        return packageTot.getValue();
    }

    /**
     * 设置代煎总包数
     * @param value int
     */
    public void setPackageTot(int value) {
        this.packageTot.setValue(value);
    }

    /**
     * 得到代理发退药药房
     * @return String
     */
    public String getAgencyOrgCode() {
        return agencyOrgCode.getValue();
    }

    /**
     * 设置代理发退药药房
     * @param value String
     */
    public void setAgencyOrgCode(String value) {
        this.agencyOrgCode.setValue(value);
    }

    /**
     * 得到代煎注记
     * @return String
     */
    public String getDctagentFlg() {
        return dctagentFlg.getValue();
    }

    /**
     * 设置代煎注记
     * @param value String
     */
    public void setDctagentFlg(String value) {
        this.dctagentFlg.setValue(value);
    }

    /**
     * 得到煎药室
     * @return String
     */
    public String getDecoctCode() {
        return decoctCode.getValue();
    }

    /**
     * 设置煎药室
     * @param value String
     */
    public void setDecoctCode(String value) {
        this.decoctCode.setValue(value);
    }

    /**
     * 修改看诊序号
     * @param value String
     */
    public void modifyCaseNo(String value) {
        this.caseNo.modifyValue(value);
    }

    /**
     * 修改处方签号
     * @param value String
     */
    public void modifyRxNo(String value) {
        this.rxNo.modifyValue(value);
    }
    /**
     * 修改条码号
     * @param value String
     */
    public void modifyMedApplyNo(String value){
    this.medApplyNo.modifyValue(value);
    }
    /**
     * 修改医嘱细分类群组
     * @param value String
     */
    public void modifyCat1Type(String value) {
        this.cat1Type.modifyValue(value);
    }

    /**
     * 修改循序号
     * @param value int
     */
    public void modifySeqNo(int value) {
        this.seqNo.modifyValue(value);
    }
    /**
     * 修改操作人員
     */
    public void modifyOptUser() {
        this.optUser.modifyValue(Operator.getID());
    }

    /**
     * 修改操作日期
     * @param value String
     */
    public void modifyOptDate(String value) {
        this.optDate.modifyValue(StringTool.getTimestamp(SystemTool.getInstance().getDate()));
    }
    /**
     * 修改操作端末
     */
    public void modifyOptTerm() {
        this.optTerm.modifyValue(Operator.getIP());
    }

    /**
     * 修改服号
     * @param value int
     */
    public void modifyPresrtNo(int value) {
        this.presrtNo.modifyValue(value);
    }

    /**
     * 修改就诊区域
     * @param value String
     */
    public void modifyRegionCode(String value) {
        this.regionCode.modifyValue(value);
    }

    /**
     * 修改就诊状态
     * @param value String
     */
    public void modifyAdmType(String value) {
        this.admType.modifyValue(value);
    }

    /**
     * 修改医嘱分类
     * @param value String
     */
    public void modifyRxType(String value) {
        this.rxType.modifyValue(value);
    }

    /**
     * 修改暂存注记
     * @param value String
     */
    public void modifyTemporaryFlg(String value) {
        this.temporaryFlg.modifyValue(value);
    }

    /**
     * 修改交付注记
     * @param value String
     */
    public void modifyReleaseFlg(String value) {
        this.releaseFlg.modifyValue(value);
    }

    /**
     * 修改主连结注记
     * @param value String
     */
    public void modifyLinkmainFlg(String value) {
        this.linkmainFlg.modifyValue(value);
    }

    /**
     * 修改连接号
     * @param value String
     */
    public void modifyLinkNo(String value) {
        this.linkNo.modifyValue(value);
    }

    /**
     * 修改医令代码
     * @param value String
     */
    public void modifyOrderCode(String value) {
        this.orderCode.modifyValue(value);
    }

    /**
     * 修改医令名称
     * @param value String
     */
    public void modifyOrderDesc(String value) {
        this.orderDesc.modifyValue(value);
    }

    /**
     * 修改商品名
     * @param value String
     */
    public void modifyGoodsDesc(String value) {
        this.goodsDesc.modifyValue(value);
    }

    /**
     * 修改医令细分类
     * @param value String
     */
    public void modifyOrderCat1Code(String value) {
        this.orderCat1Code.modifyValue(value);
    }

    /**
     * 修改用量
     * @param value double
     */
    public void modifyMediQty(double value) {
        this.mediQty.modifyValue(value);
    }

    /**
     * 修改开药单位
     * @param value String
     */
    public void modifyMediUnit(String value) {
        this.mediUnit.modifyValue(value);
    }

    /**
     * 修改频次代码
     * @param value String
     */
    public void modifyFreqCode(String value) {
        this.freqCode.modifyValue(value);
    }

    /**
     * 修改给药途径代码
     * @param value String
     */
    public void modifyRouteCode(String value) {
        this.routeCode.modifyValue(value);
    }

    /**
     * 修改付數/日份
     * @param value int
     */
    public void modifyTakeDays(int value) {
        this.takeDays.modifyValue(value);
    }

    /**
     * 修改总量
     * @param value double
     */
    public void modifyDosageQty(double value) {
        this.dosageQty.modifyValue(value);
    }

    /**
     * 修改实际发药量
     * @param value double
     */
    public void modifyDispenseQty(double value) {
        this.dispenseQty.modifyValue(value);
    }

    /**
     * 修改发药单位
     * @param value String
     */
    public void modifyDispenseUnit(String value) {
        this.dispenseUnit.modifyValue(value);
    }

    /**
     * 修改库存单位发药注记
     * @param value String
     */
    public void modifyGiveboxFlg(String value) {
        this.giveboxFlg.modifyValue(value);
    }

    /**
     * 修改自费价
     * @param value double
     */
    public void modifyOwnPrice(double value) {
        this.ownPrice.modifyValue(value);
    }

    /**
     * 修改医保价
     * @param value double
     */
    public void modifyNhiPrice(double value) {
        this.nhiPrice.modifyValue(value);
    }

    /**
     * 修改身份折扣%
     * @param value double
     */
    public void modifyDiscountRate(double value) {
        this.discountRate.modifyValue(value);
    }

    /**
     * 修改自费金额
     * @param value double
     */
    public void modifyOwnAmt(double value) {
        if (value > 0)
            value = ( (int) (value * 100.0 + 0.5)) / 100.0;
        else if (value < 0)
            value = ( (int) (value * 100.0 - 0.5)) / 100.0;
        this.ownAmt.modifyValue(value);
    }

    /**
     * 修改应付金额
     * @param value double
     */
    public void modifyArAmt(double value) {
        if (value > 0)
            value = ( (int) (value * 100.0 + 0.5)) / 100.0;
        else if (value < 0)
            value = ( (int) (value * 100.0 - 0.5)) / 100.0;
        this.arAmt.modifyValue(value);
    }

    /**
     * 修改备注栏
     * @param value String
     */
    public void modifyDrNote(String value) {
        this.drNote.modifyValue(value);
    }

    /**
     * 修改护士备注
     * @param value String
     */
    public void modifyNsNote(String value) {
        this.nsNote.modifyValue(value);
    }

    /**
     * 修改开单医师
     * @param value String
     */
    public void modifyDrCode(String value) {
        this.drCode.modifyValue(value);
    }

    /**
     * 修改开单日期时间
     * @param value Timestamp
     */
    public void modifyOrderDate(Timestamp value) {
        this.orderDate.modifyValue(value);
    }

    /**
     * 修改开单科室
     * @param value String
     */
    public void modifyDeptCode(String value) {
        this.deptCode.modifyValue(value);
    }

    /**
     * 修改停用医师
     * @param value String
     */
    public void modifyDcDrCode(String value) {
        this.dcDrCode.modifyValue(value);
    }

    /**
     * 修改停用时间
     * @param value Timestamp
     */
    public void modifyDcOrderDate(Timestamp value) {
        this.dcOrderDate.modifyValue(value);
    }

    /**
     * 修改停用科室
     * @param value String
     */
    public void modifyDcDeptCode(String value) {
        this.dcDeptCode.modifyValue(value);
    }

    /**
     * 修改执行科室
     * @param value String
     */
    public void modifyExecDeptCode(String value) {
        this.execDeptCode.modifyValue(value);
    }

    /**
     * 修改集和医令注记
     * @param value String
     */
    public void modifySetmainFlg(String value) {
        this.setmainFlg.modifyValue(value);
    }

    /**
     * 修改集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     * @param value int
     */
    public void modifyOrderSetGroupNo(int value) {
        this.orderSetGroupNo.modifyValue(value);
    }

    /**
     * 修改集合医令代码
     * @param value String
     */
    public void modifyOrdersetCode(String value) {
        this.ordersetCode.modifyValue(value);
    }

    /**
     * 修改隐藏注记
     * @param value String
     */
    public void modifyHideFlg(String value) {
        this.hideFlg.modifyValue(value);
    }

    /**
     * 修改报告类别
     * @param value String
     */
    public void modifyRpttypeCode(String value) {
        this.rpttypeCode.modifyValue(value);
    }

    /**
     * 修改检体
     * @param value String
     */
    public void modifyOptitemCode(String value) {
        this.optitemCode.modifyValue(value);
    }

    /**
     * 修改仪器代码
     * @param value String
     */
    public void modifyDevCode(String value) {
        this.devCode.modifyValue(value);
    }

    /**
     * 修改申请单模板
     * @param value String
     */
    public void modifyMrCode(String value) {
        this.mrCode.modifyValue(value);
    }

    /**
     * 修改结构化申请单序号
     * @param value int
     */
    public void modifyFileNo(int value) {
        this.fileNo.modifyValue(value);
    }

    /**
     * 修改医检绩效代码
     * @param value String
     */
    public void modifyDegreeCode(String value) {
        this.degreeCode.modifyValue(value);
    }

    /**
     * 修改急作注记
     * @param value String
     */
    public void modifyUrgentFlg(String value) {
        this.urgentFlg.modifyValue(value);
    }

    /**
     * 修改给付类别
     * @param value String
     */
    public void modifyInspayType(String value) {
        this.inspayType.modifyValue(value);
    }

    /**
     * 修改药品种类
     * @param value String
     */
    public void modifyPhaType(String value) {
        this.phaType.modifyValue(value);
    }

    /**
     * 修改剂型大分类
     * @param value String
     */
    public void modifyDoseType(String value) {
        this.doseType.modifyValue(value);
    }

    /**
     * 修改儿童处方签注记
     * @param value String
     */
    public void modifyPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.modifyValue(value);
    }

    /**
     * 修改贵重注记
     * @param value String
     */
    public void modifyExpensiveFlg(String value) {
        this.expensiveFlg.modifyValue(value);
    }

    /**
     * 修改管制药品级别
     * @param value String
     */
    public void modifyCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.modifyValue(value);
    }

    /**
     * 修改领药号
     * @param value int
     */
    public void modifyPrescriptNo(int value) {
        this.prescriptNo.modifyValue(value);
    }

    /**
     * 修改送包药机注记
     * @param value String
     */
    public void modifyAtcFlg(String value) {
        this.atcFlg.modifyValue(value);
    }

    /**
     * 修改送包药机时间
     * @param value Timestamp
     */
    public void modifySendatcdate(Timestamp value) {
        this.sendatcdate.modifyValue(value);
    }


    /**
     * 修改门急诊收据号
     * @param value String
     */
    public void modifyReceiptNo(String value) {
        this.receiptNo.modifyValue(value);
    }

    /**
     * 修改批价注记
     * @param value String
     */
    public void modifyBillFlg(String value) {
        this.billFlg.modifyValue(value);
    }

    /**
     * 修改收费日期时间
     * @param value Timestamp
     */

    public void modifyBillDate(Timestamp value) {
        this.billDate.modifyValue(value);
    }

    /**
     * 修改收费人员
     * @param value String
     */

    public void modifyBillUser(String value) {
        this.billUser.modifyValue(value);
    }

    /**
     * 修改票据打印注记
     * @param value String
     */

    public void modifyPrintFlg(String value) {
        this.printFlg.modifyValue(value);
    }

    /**
     * 修改收据费用代号
     * @param value String
     */

    public void modifyRexpCode(String value) {
        this.rexpCode.modifyValue(value);
    }

    /**
     * 修改院内费用代号
     * @param value String
     */

    public void modifyHexpCode(String value) {
        this.hexpCode.modifyValue(value);
    }

    /**
     * 修改支付合同单位代码
     * @param value String
     */

    public void modifyContractCode(String value) {
        this.contractCode.modifyValue(value);
    }

    /**
     * 修改身分
     * @param value String
     */

    public void modifyCtz1Code(String value) {
        this.ctz1Code.modifyValue(value);
    }

    /**
     * 修改折扣1
     * @param value String
     */

    public void modifyCtz2Code(String value) {
        this.ctz2Code.modifyValue(value);
    }

    /**
     * 修改折扣2
     * @param value String
     */

    public void modifyCtz3Code(String value) {
        this.ctz3Code.modifyValue(value);
    }

    /**
     * 修改审核药师
     * @param value String
     */

    public void modifyPhaCheckCode(String value) {
        this.phaCheckCode.modifyValue(value);
    }

    /**
     * 修改审核时间
     * @param value Timestamp
     */

    public void modifyPhaCheckDate(Timestamp value) {
        this.phaCheckDate.modifyValue(value);
    }

    /**
     * 修改配药药师
     * @param value String
     */

    public void modifyPhaDosageCode(String value) {
        this.phaDosageCode.modifyValue(value);
    }

    /**
     * 修改配药时间
     * @param value Timestamp
     */

    public void modifyPhaDosageDate(Timestamp value) {
        this.phaDosageDate.modifyValue(value);
    }

    /**
     * 修改发药药师
     * @param value String
     */

    public void modifyPhaDispenseCode(String value) {
        this.phaDispenseCode.modifyValue(value);
    }

    /**
     * 修改发药确认时间
     * @param value Timestamp
     */

    public void modifyPhaDispenseDate(Timestamp value) {
        this.phaDispenseDate.modifyValue(value);
    }

    /**
     * 修改医嘱确认护士
     * @param value String
     */

    public void modifyNsExecCode(String value) {
        this.nsExecCode.modifyValue(value);
    }

    /**
     * 修改医嘱确认时间
     * @param value Timestamp
     */

    public void modifyNsExecDate(Timestamp value) {
        this.nsExecDate.modifyValue(value);
    }

    /**
     * 修改医嘱确认科室
     * @param value String
     */

    public void modifyNsExecDept(String value) {
        this.nsExecDept.modifyValue(value);
    }

    /**
     * 修改煎药方式
     * @param value String
     */

    public void modifyDctagentCode(String value) {
        this.dctagentCode.modifyValue(value);
    }

    /**
     * 修改特殊煎法
     * @param value String
     */

    public void modifyDctexcepCode(String value) {
        this.dctexcepCode.modifyValue(value);
    }

    /**
     * 修改饮片服用量
     * @param value int
     */

    public void modifyDctTakeQty(int value) {
        this.dctTakeQty.modifyValue(value);
    }

    /**
     * 修改代煎总包数
     * @param value int
     */

    public void modifyPackageTot(int value) {
        this.packageTot.modifyValue(value);
    }

    /**
     * 修改代理发退药药房
     * @param value String
     */

    public void modifyAgencyOrgCode(String value) {
        this.agencyOrgCode.modifyValue(value);
    }

    /**
     * 修改代煎注记
     * @param value String
     */

    public void modifyDctagentFlg(String value) {
        this.dctagentFlg.modifyValue(value);
    }


    /**
     * 修改煎药室
     * @param value String
     */

    public void modifyDecoctCode(String value) {
        this.decoctCode.modifyValue(value);
    }

    /**
     * 设置退药时间
     * @param dsreturnDate Timestamp
     */
    public void setPhaRetnDate(Timestamp dsreturnDate){
    	this.phaRetnDate.setValue(dsreturnDate);
    }
    /**
     * 得到退药时间
     * @return dsreturnDate Timestamp
     */
    public Timestamp getPhaRetnDate(){
    	return phaRetnDate.getValue();
    }
    /**
     * 修改退药时间
     * @param dsreturnDate Timestamp
     */
    public void modifyPhaRetnDate(Timestamp dsreturnDate){
    	this.phaRetnDate.modifyValue(dsreturnDate);
    }
    /**
     * 设置执行医生
     * @param execDr String
     */
    public void setExecDrCode(String execDr){
    	this.execDrCode.setValue(execDr);
    }
    /**
     * 得到执行医生
     * @return execDr String
     */
    public String getExecDrCode(){
    	return execDrCode.getValue();
    }
    /**
     * 修改执行医生
     * @param execDr String
     */
    public void modifyExecDrCode(String execDr){
    	this.execDrCode.modifyValue(execDr);
    }
    /**
     * 设置退药人员
     * @param dsreturnuser String
     */
    public void setPhaRetnCode(String dsreturnuser){
    	this.phaRetnCode.setValue(dsreturnuser);
    }
    /**
     * 得到退药人员
     * @return  dsreturnuser String
     */
    public String getPhaRetnCode(){
    	return this.phaRetnCode.getValue();
    }
    /**
     * 修改退药人员
     * @param dsreturnuser String
     */
    public void modifyPhaRetnCode(String dsreturnuser){
    	this.phaRetnCode.modifyValue(dsreturnuser);
    }
    /**
     * 设置执行注记
     * @param exeFlg boolean
     */
    public void setExeFlg(boolean exeFlg){
    	this.exeFlg=exeFlg;
    }
    /**
     * 设置成本中心
     * @param value String
     */
    public void setCostCenterCode(String value) {
        this.costCenterCode.setValue(value);
    }

    /**
     * 得到收费标记
     * @return  chargeFlg boolean
     */
    public boolean getChargeFlg(){
        return this.chargeFlg.getValue();
    }
    /**
     * 设置收费标记
     * @param chargeFlg boolean
     */
    public void setChargeFlg(boolean chargeFlg){
        this.chargeFlg.setValue(chargeFlg);
    }
    /**
     * 修改收费标记
     * @param chargeFlg boolean
     */
    public void modifyChargeFlg(boolean chargeFlg){
        this.chargeFlg.modifyValue(chargeFlg);
    }
    /**
     * 得到执行注记
     * @return exeflg boolean
     */
    public boolean getExeFlg(){
    	return this.exeFlg;
    }
    /**
     * 得到成本中心
     * @return String
     */
    public String getCostCenterCode() {
        return costCenterCode.getValue();
    }

    /**
     * 修改成本中心
     * @param value String
     */
    public void modifyCostCenterCode(String value) {
        costCenterCode.modifyValue(value);
    }
    /**
     * 修改执行标志
     * @param value String
     */
    public void modifyExecFlg(String value) {
        execFlg.modifyValue(value);
    }
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null){
        	result.setData("MR_NO",pat.getMrNo());
        }
        else{
        	result.setData("MR_NO","");
        }

        result.setData("OPT_USER",Operator.getID());
        result.setData("OPT_TERM",Operator.getIP());
        return result;
    }
	/**
     * 得到医保代码(门)
     * @return String
     */
    public String getNhiCodeO() {
        return nhiCodeO.getValue();
    }

    /**
     * 修改医保代码(门)
     * @param value String
     */
    public void modifyNhiCodeO(String value) {
    	nhiCodeO.modifyValue(value);
    }
    /**
     * 得到医保代码(急)
     * @return String
     */
    public String getNhiCodeE() {
        return nhiCodeE.getValue();
    }

    /**
     * 修改医保代码(急)
     * @param value String
     */
    public void modifyNhiCodeE(String value) {
    	nhiCodeE.modifyValue(value);
    }
    /**
     * 得到医保代码(住)
     * @return String
     */
    public String getNhiCodeI() {
        return nhiCodeI.getValue();
    }

    /**
     * 修改医保代码(门)
     * @param value String
     */
    public void modifyNhiCodeI(String value) {
    	nhiCodeI.modifyValue(value);
    }
    /**
     * 构造器
     */
    public Order() {
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
                     + "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT;agencyOrgCode:AGENCY_ORG_CODE;dctagentFlg:DCTAGENT_FLG;businessNo:BUSINESS_NO;"//====pangben 2012-11-3 添加内部交易号码
                     + "decoctCode:DECOCT_CODE;phaRetnDate:PHA_RETN_DATE;mrCode:MR_CODE;phaRetnCode:PHA_RETN_CODE;execDrCode:EXEC_DR_CODE;mrNo:MR_NO;"
                     + "phaCheckCode:PHA_CHECK_CODE_OLD:OLD;phaCheckDate:PHA_CHECK_DATE_OLD:OLD;phaDosageCode:PHA_DOSAGE_CODE_OLD:OLD;phaDosageDate:PHA_DOSAGE_DATE_OLD:OLD;phaDispenseCode:PHA_DISPENSE_CODE_OLD:OLD;phaDispenseDate:PHA_DISPENSE_DATE_OLD:OLD;"
                     + "counterNo:COUNTER_NO;execFlg:EXEC_FLG;receiptFlg:RECEIPT_FLG;billType:BILL_TYPE;printNo:PRINT_NO;cat1Type:CAT1_TYPE;costCenterCode:COST_CENTER_CODE;nhiCodeO:NHI_CODE_O;nhiCodeE:NHI_CODE_E;nhiCodeI:NHI_CODE_I");
    }

}
