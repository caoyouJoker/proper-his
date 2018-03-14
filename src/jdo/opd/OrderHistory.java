package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.DoubleValue;
import com.dongyang.data.IntValue;
import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 *
 * <p>
 * Title: 药，诊疗项目，检验检查jdo
 * </p>
 *
 * <p>
 * Description:药，诊疗项目，检验检查jdo
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
public class OrderHistory
    extends TModifiedData {
    /**
     * 病患对象
     */
    private Pat pat;
    /**
     * 看诊序号
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * 处方签号
     */
    private StringValue rxNo = new StringValue(this);
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
     * 病案号
     */
    private StringValue mrNo = new StringValue(this);
    /**
     * 就诊状态
     */
    private StringValue admType = new StringValue(this);
    /**
     * 医嘱分类
     */
    private StringValue rxType = new StringValue(this);
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
    private DoubleValue linkNo = new DoubleValue(this);
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
    private StringValue orderCat1 = new StringValue(this);
    /**
     * 用量
     */
    private DoubleValue takeQty = new DoubleValue(this);
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
    private DoubleValue totQty = new DoubleValue(this);
    /**
     * 实际发药量
     */
    private DoubleValue dgtTot = new DoubleValue(this);
    /**
     * 发药单位
     */
    private StringValue dispenseUnit = new StringValue(this);
    /**
     * 库存单位发药注记
     */
    private StringValue opdgiveboxFlg = new StringValue(this);
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
    private DoubleValue discnrate = new DoubleValue(this);
    /**
     * 自费金额
     */
    private DoubleValue ownAmt = new DoubleValue(this);
    /**
     * 应付金额
     */
    private DoubleValue totAmt = new DoubleValue(this);
    /**
     * 备注栏
     */
    private StringValue description = new StringValue(this);
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
    private TimestampValue orderDate = new TimestampValue(this);
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
    private TimestampValue dcOrderDate = new TimestampValue(this);
    /**
     * 停用科室
     */
    private StringValue dcDeptCode = new StringValue(this);
    /**
     * 执行科室
     */
    private StringValue rborderDeptCode = new StringValue(this);
    /**
     * 集和医令注记
     */
    private StringValue setmainFlg = new StringValue(this);
    /**
     * 集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     */
    private IntValue ordsetGroupNo = new IntValue(this);
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
     * 设置处方号
     * @param value String
     */
    public void setRxNo(String value) {
        this.rxNo.setValue(value);
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
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate.getValue();
    }

    /**
     * 设置操作日期
     * @param value Timestamp
     */
    public void setOptDate(Timestamp value) {
        this.optDate.setValue(value);
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
     * 得到病案号
     * @return String
     */
    public String getMrNo() {
        return mrNo.getValue();
    }

    /**
     * 设置病案号
     * @param value String
     */
    public void setMrNo(String value) {
        this.mrNo.setValue(value);
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
     * @return double
     */
    public double getLinkNo() {
        return linkNo.getValue();
    }

    /**
     * 设置连接号
     * @param value double
     */
    public void setLinkNo(double value) {
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
    public String getOrderCat1() {
        return orderCat1.getValue();
    }

    /**
     * 设置医令细分类
     * @param value String
     */
    public void setOrderCat1(String value) {
        this.orderCat1.setValue(value);
    }

    /**
     * 得到用量
     * @return double
     */
    public double getTakeQty() {
        return takeQty.getValue();
    }

    /**
     * 设置用量
     * @param value double
     */
    public void setTakeQty(double value) {
        this.takeQty.setValue(value);
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
     * @return double
     */
    public double getTotQty() {
        return totQty.getValue();
    }

    /**
     * 设置总量
     * @param value double
     */
    public void setTotQty(double value) {
        this.totQty.setValue(value);
    }

    /**
     * 得到实际发药量
     * @return double
     */
    public double getDgtTot() {
        return dgtTot.getValue();
    }

    /**
     * 设置实际发药量
     * @param value double
     */
    public void setDgtTot(double value) {
        this.dgtTot.setValue(value);
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
    public String getOpdgiveboxFlg() {
        return opdgiveboxFlg.getValue();
    }

    /**
     * 设置库存单位发药注记
     * @param value String
     */
    public void setOpdgiveboxFlg(String value) {
        this.opdgiveboxFlg.setValue(value);
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
    public double getDiscnrate() {
        return discnrate.getValue();
    }

    /**
     * 设置身份折扣%
     * @param value double
     */
    public void setDiscnrate(double value) {
        this.discnrate.setValue(value);
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
    public double getTotAmt() {
        return totAmt.getValue();
    }

    /**
     * 设置应付金额
     * @param value double
     */
    public void setTotAmt(double value) {
        this.totAmt.setValue(value);
    }

    /**
     * 得到备注栏
     * @return String
     */
    public String getDescription() {
        return description.getValue();
    }

    /**
     * 设置备注栏
     * @param value String
     */
    public void setDescription(String value) {
        this.description.setValue(value);
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
    public String getRborderDeptCode() {
        return rborderDeptCode.getValue();
    }

    /**
     * \设置执行科室
     * @param value String
     */
    public void setRborderDeptCode(String value) {
        this.rborderDeptCode.setValue(value);
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
    public int getOrdsetGroupNo() {
        return ordsetGroupNo.getValue();
    }

    /**
     * 设置集合医嘱组号（用于区分同时开立2个相同的集合医嘱）
     * @param value int
     */
    public void setOrdsetGroupNo(int value) {
        this.ordsetGroupNo.setValue(value);
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
     * @return String
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
     * 修改循序号
     * @param value int
     */
    public void modifySeqNo(int value) {
        this.seqNo.modifyValue(value);
    }

    /**
     * 修改操作人員
     * @param value String
     */
    public void modifyOptUser(String value) {
        this.optUser.modifyValue(value);
    }

    /**
     * 修改操作日期
     * @param value Timestamp
     */
    public void modifyOptDate(Timestamp value) {
        this.optDate.modifyValue(value);
    }

    /**
     * 修改操作端末
     * @param value String
     */
    public void modifyOptTerm(String value) {
        this.optTerm.modifyValue(value);
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
     * 修改病案号
     * @param value String
     */
    public void modifyMrNo(String value) {
        this.mrNo.modifyValue(value);
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
     * @param value double
     */
    public void modifyLinkNo(double value) {
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
    public void modifyOrderCat1(String value) {
        this.orderCat1.modifyValue(value);
    }

    /**
     * 修改用量
     * @param value double
     */
    public void modifyTakeQty(double value) {
        this.takeQty.modifyValue(value);
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
    public void modifyTotQty(double value) {
        this.totQty.modifyValue(value);
    }

    /**
     * 修改实际发药量
     * @param value double
     */
    public void modifyDgtTot(double value) {
        this.dgtTot.modifyValue(value);
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
    public void modifyOpdgiveboxFlg(String value) {
        this.opdgiveboxFlg.modifyValue(value);
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
    public void modifyDiscnrate(double value) {
        this.discnrate.modifyValue(value);
    }

    /**
     * 修改自费金额
     * @param value double
     */
    public void modifyOwnAmt(double value) {
        this.ownAmt.modifyValue(value);
    }

    /**
     * 修改应付金额
     * @param value double
     */
    public void modifyTotAmt(double value) {
        this.totAmt.modifyValue(value);
    }

    /**
     * 修改备注栏
     * @param value String
     */
    public void modifyDescription(String value) {
        this.description.modifyValue(value);
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
    public void modifyRborderDeptCode(String value) {
        this.rborderDeptCode.modifyValue(value);
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
    public void modifyOrdsetGroupNo(int value) {
        this.ordsetGroupNo.modifyValue(value);
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
	 * 取得pram
	 */
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }

}
