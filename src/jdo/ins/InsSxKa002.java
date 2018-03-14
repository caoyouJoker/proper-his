package jdo.ins;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.TimestampValue;
import java.sql.Timestamp;


public class InsSxKa002 extends TModifiedData {
    /**
     * 药品编码 NHI_ORDER_CODE
     */
    private StringValue NhiOrderCode = new StringValue(this);
    /**
     * 中文名称
     */
    private StringValue NhiOrderDesc = new StringValue(this);
    /**
     * 英文
     */
    private StringValue NhiOrderEngDesc = new StringValue(this);
    /**
     * 收费类别
     */
    private StringValue chargeCode = new StringValue(this);
    /**
     * 一级费用类别
     */
    private StringValue orderCode1 = new StringValue(this);
    /**
     * 二级费用类别
     */
    private StringValue orderCode2 = new StringValue(this);
    /**
     * 三级费用类别
     */
    private StringValue orderCode3 = new StringValue(this);
    /**
     * 处方药标志
     */
    private StringValue preFlg = new StringValue(this);
    /**
     * 收费项目等级
     */
    private StringValue orderType = new StringValue(this);
    /**
     * 注记码
     */
    private StringValue memoCode = new StringValue(this);
    /**
     * 单位
     */
    private StringValue unit = new StringValue(this);
    /**
     * 标准价格
     */
    private DoubleValue price = new DoubleValue(this);
    /**
     * 自付比例
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * 剂型
     */
    private StringValue doseCode = new StringValue(this);
    /**
     * 每次用量
     */
    private DoubleValue qty = new DoubleValue(this);
    /**
     * 使用频次
     */
    private StringValue freqCode = new StringValue(this);
    /**
     * 用法
     */
    private StringValue routeCode = new StringValue(this);
    /**
     * 规格
     */
    private StringValue description = new StringValue(this);
    /**
     * 限制类用药标志
     */
    private BooleanValue LimitFlg = new BooleanValue(this);
    /**
     * 备注
     */
    private StringValue remark = new StringValue(this);
    /**
     * 经办人
     */
    private StringValue aproveUserId = new StringValue(this);
    /**
     * 经办日期
     */
    private TimestampValue aproveDate = new TimestampValue(this);
    /**
     * 数据分区编号
     */
    private StringValue DataAreaNo = new StringValue(this);
    /**
     * 当前有效标志
     */
    private BooleanValue activeFlg = new BooleanValue(this);
    /**
     * 工伤自付比例
     */
    private DoubleValue bka002 = new DoubleValue(this);
    /**
     * 工伤收费类别
     */
    private StringValue bka001 = new StringValue(this);
    /**
     * 医院医嘱代码
     */
    private StringValue hospOrderCode = new StringValue(this);
    /**
     * 收费类别等级
     */
    private StringValue feeType = new StringValue(this);
    /**
     * 统计分类
     * 用于特检、特治统计表的分类使用
     */
    private StringValue staCode = new StringValue(this);


    /**
     * 设置药品编码
     * @param NhiOrderCode String 药品编码
     */
    public void setNhiOrderCode(String NhiOrderCode) {
        this.NhiOrderCode.setValue(NhiOrderCode);
    }

    /**
     * 得到药品编码
     * @return String 药品编码
     */
    public String getNhiOrderCode() {
        return NhiOrderCode.getValue();
    }

    /**
     * 修改药品编码
     * @param NhiOrderDesc String
     */
    public void modifyNhiOrderCode(String NhiOrderCode) {
        this.NhiOrderCode.modifyValue(NhiOrderCode);
    }



    /**
     * 设置药品中文名称
     * @param NhiOrderDesc String
     */
    public void setNhiOrderDesc(String NhiOrderDesc) {
        this.NhiOrderDesc.setValue(NhiOrderDesc);
    }

    /**
     * 得到中文名称
     * @return String
     */
    public String getNhiOrderDesc() {
        return NhiOrderDesc.getValue();
    }

    /**
     * 修改中文名称
     * @param NhiOrderDesc String
     */
    public void modifyNhiOrderDesc(String NhiOrderDesc) {
        this.NhiOrderDesc.modifyValue(NhiOrderDesc);
    }

    /**
     * 设置药品英文名称
     * @param NhiOrderEngDesc String
     */
    public void setNhiOrderEngDesc(String NhiOrderEngDesc) {
        this.NhiOrderEngDesc.setValue(NhiOrderEngDesc);
    }

    /**
     * 得到英文名称
     * @return String
     */
    public String getNhiOrderEngDesc() {
        return NhiOrderEngDesc.getValue();
    }

    /**
     * 修改英文名称
     * @param NhiOrderEngDesc String
     */
    public void modifyNhiOrderEngDesc(String NhiOrderEngDesc) {
        this.NhiOrderEngDesc.modifyValue(NhiOrderEngDesc);
    }

    /**
     * 设置收费类别
     * @param chargeCode String
     */
    public void setchargeCode(String chargeCode) {
        this.chargeCode.setValue(chargeCode);
    }

    /**
     * 得到收费类别
     * @return String
     */
    public String getchargeCode() {
        return chargeCode.getValue();
    }

    /**
     * 修改收费类别
     * @param chargeCode String
     */
    public void modifychargeCode(String chargeCode) {
        this.chargeCode.modifyValue(chargeCode);
    }

    /**
     * 设置一级费用类别
     * @param orderCode1 String
     */
    public void setorderCode1(String orderCode1) {
        this.orderCode1.setValue(orderCode1);
    }

    /**
     * 得到一级费用类别
     * @return String
     */
    public String getorderCode1() {
        return orderCode1.getValue();
    }

    /**
     * 修改一级费用类别
     * @param orderCode1 String
     */
    public void modifyorderCode1(String orderCode1) {
        this.orderCode1.modifyValue(orderCode1);
    }

    /**
     * 设置二级费用类别
     * @param orderCode2 String
     */
    public void setorderCode2(String orderCode2) {
        this.orderCode2.setValue(orderCode2);
    }

    /**
     * 得到二级费用类别
     * @return String
     */
    public String getorderCode2() {
        return orderCode2.getValue();
    }

    /**
     * 修改二级费用类别
     * @param orderCode2 String
     */
    public void modifyorderCode2(String orderCode2) {
        this.orderCode2.modifyValue(orderCode2);
    }

    /**
     * 设置三级费用类别
     * @param orderCode3 String
     */
    public void setorderCode3(String orderCode3) {
        this.orderCode3.setValue(orderCode3);
    }

    /**
     * 得到三级费用类别
     * @return String
     */
    public String getorderCode3() {
        return orderCode3.getValue();
    }

    /**
     * 修改三级费用类别
     * @param orderCode3 String
     */
    public void modifyorderCode3(String orderCode3) {
        this.orderCode3.modifyValue(orderCode3);
    }

    /**
     * 设置处方药标志
     * @param preFlg String
     */
    public void setpreFlg(String preFlg) {
        this.preFlg.setValue(preFlg);
    }

    /**
     * 得到处方药标志
     * @return String
     */
    public String getpreFlg() {
        return preFlg.getValue();
    }

    /**
     * 修改处方药标志
     * @param preFlg String
     */
    public void modifypreFlg(String preFlg) {
        this.preFlg.modifyValue(preFlg);
    }

    /**
     * 设置收费项目等级
     * @param orderType String
     */
    public void setorderType(String orderType) {
        this.orderType.setValue(orderType);
    }

    /**
     * 得到收费项目等级
     * @return String
     */
    public String getorderType() {
        return orderType.getValue();
    }

    /**
     * 修改收费项目等级
     * @param orderType String
     */
    public void modifyorderType(String orderType) {
        this.orderType.modifyValue(orderType);
    }

    /**
     * 设置注记码
     * @param memoCode String
     */
    public void setmemoCode(String memoCode) {
        this.memoCode.setValue(memoCode);
    }

    /**
     * 得到注记码
     * @return String
     */
    public String getmemoCode() {
        return memoCode.getValue();
    }

    /**
     * 修改注记码
     * @param memoCode String
     */
    public void modifymemoCode(String memoCode) {
        this.memoCode.modifyValue(memoCode);
    }

    /**
     * 设置单位
     * @param unit String
     */
    public void setunit(String unit) {
        this.unit.setValue(unit);
    }

    /**
     * 得到单位
     * @return String
     */
    public String getunit() {
        return unit.getValue();
    }

    /**
     * 修改单位
     * @param unit String
     */
    public void modifyunit(String unit) {
        this.unit.modifyValue(unit);
    }

    /**
     * 设置标准价格
     * @param price double
     */
    public void setprice(double price) {
        this.price.setValue(price);
    }

    /**
     * 得到标准价格
     * @return double
     */
    public double getprice() {
        return price.getValue();
    }

    /**
     * 修改标准价格
     * @param price double
     */
    public void modifyprice(double price) {
        this.price.modifyValue(price);
    }

    /**
     * 设置自付比例
     * @param unit double
     */
    public void setownRatet(double ownRate) {
        this.ownRate.setValue(ownRate);
    }

    /**
     * 得到自付比例
     * @return String
     */
    public double getownRate() {
        return ownRate.getValue();
    }

    /**
     * 修改自付比例
     * @param unit double
     */
    public void modifyownRate(double ownRate) {
        this.ownRate.modifyValue(ownRate);
    }

    /**
     * 设置剂型
     * @param doseCode String
     */
    public void setdoseCode(String doseCode) {
        this.doseCode.setValue(doseCode);
    }

    /**
     * 得到剂型
     * @return String
     */
    public String getdoseCode() {
        return doseCode.getValue();
    }

    /**
     * 修改剂型
     * @param doseCode String
     */
    public void modifydoseCode(String doseCode) {
        this.doseCode.modifyValue(doseCode);
    }

    /**
     * 设置每次用量
     * @param qty double
     */
    public void setqty(double qty) {
        this.qty.setValue(qty);
    }

    /**
     * 得到每次用量
     * @return double
     */
    public double getqty() {
        return qty.getValue();
    }

    /**
     * 修改每次用量
     * @param qty double
     */
    public void modifyqty(double qty) {
        this.qty.modifyValue(qty);
    }

    /**
     * 设置使用频次
     * @param freqCode String
     */
    public void setfreqCode(String freqCode) {
        this.freqCode.setValue(freqCode);
    }

    /**
     * 得到使用频次
     * @return String
     */
    public String getfreqCode() {
        return freqCode.getValue();
    }

    /**
     * 修改使用频次
     * @param freqCode String
     */
    public void modifyfreqCode(String freqCode) {
        this.freqCode.modifyValue(freqCode);
    }

    /**
     * 设置用法
     * @param routeCode String
     */
    public void setrouteCode(String routeCode) {
        this.routeCode.setValue(routeCode);
    }

    /**
     * 得到用法
     * @return String
     */
    public String getrouteCode() {
        return routeCode.getValue();
    }

    /**
     * 修改用法
     * @param routeCode String
     */
    public void modifyrouteCode(String routeCode) {
        this.routeCode.modifyValue(routeCode);
    }

    /**
     * 设置规格
     * @param description String
     */
    public void setdescription(String description) {
        this.description.setValue(description);
    }

    /**
     * 得到规格
     * @return String
     */
    public String getdescription() {
        return description.getValue();
    }

    /**
     * 修改规格
     * @param description String
     */
    public void modifydescription(String description) {
        this.description.modifyValue(description);
    }

    /**
     * 设置限制类用药标志
     * @param LimitFlg boolean
     */
    public void setLimitFlg(boolean LimitFlg) {
        this.LimitFlg.setValue(LimitFlg);
    }

    /**
     * 得到限制类用药标志
     * @return boolean
     */
    public boolean getLimitFlgn() {
        return LimitFlg.getValue();
    }

    /**
     * 修改限制类用药标志
     * @param LimitFlg boolean
     */
    public void modifyLimitFlg(boolean LimitFlg) {
        this.LimitFlg.modifyValue(LimitFlg);
    }

    /**
     * 设置备注
     * @param remark String
     */
    public void setremark(String remark) {
        this.remark.setValue(remark);
    }

    /**
     * 得到备注
     * @return String
     */
    public String getremark() {
        return remark.getValue();
    }

    /**
     * 修改备注
     * @param description String
     */
    public void modifyremark(String remark) {
        this.remark.modifyValue(remark);
    }

    /**
     * 设置经办人
     * @param aproveUserId String
     */
    public void setaproveUserId(String aproveUserId) {
        this.aproveUserId.setValue(aproveUserId);
    }

    /**
     * 得到经办人
     * @return String
     */
    public String getaproveUserId() {
        return aproveUserId.getValue();
    }

    /**
     * 修改经办人
     * @param aproveUserId String
     */
    public void modifyaproveUserId(String aproveUserId) {
        this.aproveUserId.modifyValue(aproveUserId);
    }

    /**
     * 设置经办日期
     * @param aproveDate Timestamp
     */
    public void setaproveDate(Timestamp aproveDate) {
        this.aproveDate.setValue(aproveDate);
    }

    /**
     * 得到经办日期
     * @return Timestamp
     */
    public Timestamp getaproveDate() {
        return aproveDate.getValue();
    }

    /**
     * 修改经办日期
     * @param aproveDate Timestamp
     */
    public void modifyaproveDate(Timestamp aproveDate) {
        this.aproveDate.modifyValue(aproveDate);
    }

    /**
     * 设置数据分区编号
     * @param DataAreaNo String
     */
    public void setDataAreaNo(String DataAreaNo) {
        this.DataAreaNo.setValue(DataAreaNo);
    }

    /**
     * 得到数据分区编号
     * @return String
     */
    public String getDataAreaNo() {
        return DataAreaNo.getValue();
    }

    /**
     * 修改数据分区编号
     * @param DataAreaNo String
     */
    public void modifyDataAreaNo(String DataAreaNo) {
        this.DataAreaNo.modifyValue(DataAreaNo);
    }
    /**
     * 设置当前有效标志
     * @param activeFlg boolean
     */
    public void setactiveFlg(boolean activeFlg) {
        this.activeFlg.setValue(activeFlg);
    }

    /**
     * 得到当前有效标志
     * @return String
     */
    public boolean getactiveFlg() {
        return activeFlg.getValue();
    }

    /**
     * 修改当前有效标志
     * @param activeFlg boolean
     */
    public void modifyactiveFlg(boolean activeFlg) {
        this.activeFlg.modifyValue(activeFlg);
    }

    /**
     * 设置工伤自付比例
     * @param DataAreaNo double
     */
    public void setbka002(double DataAreaNo) {
        this.bka002.setValue(DataAreaNo);
    }

    /**
     * 得到工伤自付比例
     * @return double
     */
    public double getbka002() {
        return bka002.getValue();
    }

    /**
     * 修改工伤自付比例
     * @param DataAreaNo double
     */
    public void modifybka002(double bka002) {
        this.bka002.modifyValue(bka002);
    }

    /**
     * 设置工伤收费类别
     * @param bka001 String
     */
    public void setbka001(String bka001) {
        this.bka001.setValue(bka001);
    }

    /**
     * 得到工伤收费类别
     * @return String
     */
    public String getbka001() {
        return bka001.getValue();
    }

    /**
     * 修改工伤收费类别
     * @param bka001 String
     */
    public void modifybka001(String bka001) {
        this.bka001.modifyValue(bka001);
    }

    /**
     * 设置医院医嘱代码
     * @param hospOrderCode String
     */
    public void sethospOrderCode(String hospOrderCode) {
        this.hospOrderCode.setValue(hospOrderCode);
    }

    /**
     * 得到医院医嘱代码
     * @return String
     */
    public String gethospOrderCode() {
        return hospOrderCode.getValue();
    }

    /**
     * 修改医院医嘱代码
     * @param hospOrderCode String
     */
    public void modifyhospOrderCode(String hospOrderCode) {
        this.hospOrderCode.modifyValue(hospOrderCode);
    }

    /**
     * 设置收费类别等级
     * @param feeType String
     */
    public void setfeeType(String feeType) {
        this.feeType.setValue(feeType);
    }

    /**
     * 得到收费类别等级
     * @return String
     */
    public String getfeeType() {
        return feeType.getValue();
    }

    /**
     * 修改收费类别等级
     * @param feeType String
     */
    public void modifyfeeType(String feeType) {
        this.feeType.modifyValue(feeType);
    }

    /**
     * 设置统计分类
     * @param staCode String
     */
    public void setstaCode(String staCode) {
        this.staCode.setValue(staCode);
    }

    /**
     * 得到统计分类
     * @return String
     */
    public String getstaCode() {
        return staCode.getValue();
    }

    /**
     * 修改统计分类
     * @param staCode String
     */
    public void modifystaCode(String staCode) {
        this.staCode.modifyValue(staCode);
    }









}
