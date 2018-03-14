package jdo.ins;

import com.dongyang.data.*;
import java.sql.Timestamp;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY
 * @version JavaHis 1.0
 */
public class InsSxKa003 extends TModifiedData {
    /**
     * 药品编码 NHI_ORDER_CODE
     */
    private StringValue nhiOrderCode = new StringValue(this);
    /**
     * 最高限价
     */
    private DoubleValue limitPrice = new DoubleValue(this);
    /**
     * 医院等级
     */
    private StringValue hospClass = new StringValue(this);
    /**
     * 项目名称
     */
    private StringValue nhiOrderDesc = new StringValue(this);
    /**
     * 收费类别
     */
    private StringValue changeCode = new StringValue(this);
    /**
     * 收费项目等级
     */
    private StringValue aka065 = new StringValue(this);
    /**
     * 一级费用类别
     */
    private StringValue orderCat1 = new StringValue(this);
    /**
     * 二级费用类别
     */
    private StringValue orderCat2 = new StringValue(this);
    /**
     * 三级费用类别
     */
    private StringValue orderCat3 = new StringValue(this);
    /**
     * 三大目录使用范围
     */
    private StringValue description = new StringValue(this);
    /**
     * 注记码
     */
    private StringValue memo = new StringValue(this);
    /**
     * 标准价格
     */
    private DoubleValue price = new DoubleValue(this);
    /**
     * 自付比例
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * 基础支付标准
     */
    private DoubleValue nhiBasePrice = new DoubleValue(this);
    /**
     * 经办人
     */
    private StringValue approveUserId = new StringValue(this);
    /**
     * 经办日期
     */
    private TimestampValue approveDate = new TimestampValue(this);
    /**
     * 数据分区编号
     */
    private StringValue dataAreaNo = new StringValue(this);
    /**
     * 当前有效标志
     */
    private BooleanValue activeFlg = new BooleanValue(this);
    /**
     * 医院医嘱代码
     */
    private StringValue hospOrderCode = new StringValue(this);
    /**
     * 收费类别等级
     */
    private StringValue feeType = new StringValue(this);

    /**
     * 设置药品编码
     * @param NhiOrderCode String 药品编码
     */
    public void setNhiOrderCode(String NhiOrderCode) {
        this.nhiOrderCode.setValue(NhiOrderCode);
    }

    /**
     * 得到药品编码
     * @return String 药品编码
     */
    public String getNhiOrderCode() {
        return nhiOrderCode.getValue();
    }

    /**
     * 设置最高限价
     * @param chargeCode String
     */
    public void setlimitPrice(double limitPrice) {
        this.limitPrice.setValue(limitPrice);
    }
    /**
     * 得到最高限价
     * @return double
     */
    public double getlimitPrice(){
        return limitPrice.getValue();
    }
    /**
    * 修改最高限价
    * @param chargeCode String
    */

    public void modifylimitPrice(double limitPrice) {
       this.limitPrice.setValue(limitPrice);
   }
    /**
     * 设置医院等级
     * @param orderCode1 String
     */
    public void sethospClass(String hospClass) {
        this.hospClass.setValue(hospClass);
    }

    /**
     * 等到医院等级
     * @return String
     */
    public String gethospClass() {
        return hospClass.getValue();
    }

    /**
     * 修改医院等级
     * @param hospClass String
     */
    public void modifyhospClass(String hospClass) {
        this.hospClass.modifyValue(hospClass);
    }

    /**
     * 设置项目名称
     * @param orderCode1 String
     */
    public void setnhiOrderDesc(String nhiOrderDesc) {
        this.nhiOrderDesc.setValue(nhiOrderDesc);
    }

    /**
     * 等到项目名称
     * @return String
     */
    public String getnhiOrderDesc() {
        return nhiOrderDesc.getValue();
    }

    /**
     * 修改项目名称
     * @param hospClass String
     */
    public void modifynhiOrderDesc(String nhiOrderDesc) {
        this.nhiOrderDesc.modifyValue(nhiOrderDesc);
    }

    /**
     * 设置收费类别
     * @param orderCode1 String
     */
    public void setchangeCode(String changeCode) {
        this.changeCode.setValue(changeCode);
    }

    /**
     * 等到收费类别
     * @return String
     */
    public String getchangeCode() {
        return changeCode.getValue();
    }

    /**
     * 修改收费类别
     * @param hospClass String
     */
    public void modifychangeCode(String changeCode) {
        this.changeCode.modifyValue(changeCode);
    }

    /**
     * 设置收费项目等级
     * @param orderCode1 String
     */
    public void setaka065(String aka065) {
        this.aka065.setValue(aka065);
    }

    /**
     * 等到收费项目等级
     * @return String
     */
    public String getaka065() {
        return aka065.getValue();
    }

    /**
     * 修改收费项目等级
     * @param hospClass String
     */
    public void modifyaka065(String aka065) {
        this.aka065.modifyValue(aka065);
    }

    /**
     * 设置一级费用类别
     * @param orderCode1 String
     */
    public void setorderCat1(String orderCat1) {
        this.orderCat1.setValue(orderCat1);
    }

    /**
     * 等到一级费用类别
     * @return String
     */
    public String getorderCat1() {
        return orderCat1.getValue();
    }

    /**
     * 修改一级费用类别
     * @param hospClass String
     */
    public void modifyorderCat1(String orderCat1) {
        this.orderCat1.modifyValue(orderCat1);
    }

    /**
     * 设置二级费用类别
     * @param orderCode1 String
     */
    public void setorderCat2(String orderCat2) {
        this.orderCat2.setValue(orderCat2);
    }

    /**
     * 等到二级费用类别
     * @return String
     */
    public String getorderCat2() {
        return orderCat2.getValue();
    }

    /**
     * 修改二级费用类别
     * @param hospClass String
     */
    public void modifyorderCat2(String orderCat2) {
        this.orderCat2.modifyValue(orderCat2);
    }

    /**
     * 设置三级费用类别
     * @param orderCode3 String
     */
    public void setorderCat3(String orderCat3) {
        this.orderCat3.setValue(orderCat3);
    }

    /**
     * 等到三级费用类别
     * @return String
     */
    public String getorderCat3() {
        return orderCat3.getValue();
    }

    /**
     * 修改三级费用类别
     * @param hospClass String
     */
    public void modifyorderCat3(String orderCat3) {
        this.orderCat3.modifyValue(orderCat3);
    }


    /**
     * 设置三大目录使用范围
     * @param orderCode1 String
     */
    public void setdescription(String description) {
        this.description.setValue(description);
    }

    /**
     * 等到三大目录使用范围
     * @return String
     */
    public String getdescription() {
        return description.getValue();
    }

    /**
     * 修改三大目录使用范围
     * @param hospClass String
     */
    public void modifydescription(String description) {
        this.description.modifyValue(description);
    }

    /**
     * 设置注记码
     * @param orderCode1 String
     */
    public void setmemo(String memo) {
        this.memo.setValue(memo);
    }

    /**
     * 得到注记码
     * @return String
     */
    public String getmemo() {
        return memo.getValue();
    }

    /**
     * 修改注记码
     * @param hospClass String
     */
    public void modifymemo(String memo) {
        this.memo.modifyValue(memo);
    }

    /**
     * 标准价格
     * @param orderCode1 String
     */
    public void setprice(double price) {
        this.price.setValue(price);
    }

    /**
     * 得到标准价格
     * @return String
     */
    public double getprice() {
        return price.getValue();
    }

    /**
     * 修改标准价格
     * @param hospClass String
     */
    public void modifymemo(double price) {
        this.price.modifyValue(price);
    }

    /**
     * 设置自付比例
     * @param orderCode1 String
     */
    public void setownRate(double ownRate) {
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
     * @param hospClass String
     */
    public void modifyownRate(double ownRate) {
        this.ownRate.modifyValue(ownRate);
    }

    /**
     * 设置基础支付标准
     * @param orderCode1 String
     */
    public void setnhiBasePrice(double nhiBasePrice) {
        this.nhiBasePrice.setValue(nhiBasePrice);
    }

    /**
     * 得到基础支付标准
     * @return String
     */
    public double getnhiBasePrice() {
        return nhiBasePrice.getValue();
    }

    /**
     * 修改基础支付标准
     * @param hospClass String
     */
    public void modifynhiBasePrice(double nhiBasePrice) {
        this.nhiBasePrice.modifyValue(nhiBasePrice);
    }

    /**
     * 设置经办人
     * @param orderCode1 String
     */
    public void setapproveUserId(String approveUserId) {
        this.approveUserId.setValue(approveUserId);
    }

    /**
     * 得到经办人
     * @return String
     */
    public String getapproveUserId() {
        return approveUserId.getValue();
    }

    /**
     * 修改经办人
     * @param hospClass String
     */
    public void modifyapproveUserId(String approveUserId) {
        this.approveUserId.modifyValue(approveUserId);
    }

    /**
     * 设置经办日期
     * @param orderCode1 String
     */
    public void setapproveDate(Timestamp approveDate) {
        this.approveDate.setValue(approveDate);
    }

    /**
     * 得到经办日期
     * @return String
     */
    public Timestamp getapproveDate() {
        return approveDate.getValue();
    }

    /**
     * 修改经办日期
     * @param hospClass String
     */
    public void modifyapproveDate(Timestamp approveDate) {
        this.approveDate.modifyValue(approveDate);
    }

    /**
     * 设置数据分区编号
     * @param orderCode1 String
     */
    public void setdataAreaNo(String dataAreaNo) {
        this.dataAreaNo.setValue(dataAreaNo);
    }

    /**
     * 得到数据分区编号
     * @return String
     */
    public String getdataAreaNo() {
        return dataAreaNo.getValue();
    }

    /**
     * 修改数据分区编号
     * @param hospClass String
     */
    public void modifydataAreaNo(String dataAreaNo) {
        this.dataAreaNo.modifyValue(dataAreaNo);
    }

    /**
     * 设置当前有效标志
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyactiveFlg(boolean activeFlg) {

            this.activeFlg.modifyValue(activeFlg);

    }

    /**
     * 设置医院医嘱代码
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyhospOrderCode(String hospOrderCode) {
        this.hospOrderCode.modifyValue(hospOrderCode);
    }

    /**
     * 设置收费类别等级
     * @param orderCode1 String
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
     * @param hospClass String
     */
    public void modifyfeeType(String feeType) {
        this.feeType.modifyValue(feeType);
    }
}
