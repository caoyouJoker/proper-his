package jdo.ins;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.TimestampValue;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.StringValue;
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
public class InsSxKa004 extends TModifiedData {
    /**
    * 医疗服务设施编码
    */
   private StringValue aka100 = new StringValue(this);
   /**
    * 最高限价
    */
   private DoubleValue limitPrice = new DoubleValue(this);
   /**
    * 医院等级
    */
   private StringValue aka101 = new StringValue(this);
   /**
    * 服务设施名称
    */
   private StringValue aka102 = new StringValue(this);
   /**
    * 收费类别
    */
   private StringValue aka063 = new StringValue(this);
   /**
    * 收费项目等级
    */
   private StringValue aka065 = new StringValue(this);
   /**
    * 一级费用类别
    */
   private StringValue bka246 = new StringValue(this);
   /**
    * 二级费用类别
    */
   private StringValue bka247 = new StringValue(this);
   /**
    * 三级费用类别
    */
   private StringValue bka260 = new StringValue(this);
   /**
    * 三大目录使用范围
    */
   private StringValue bka001 = new StringValue(this);
   /**
    * 病床等级
    */
   private StringValue bka103 = new StringValue(this);
   /**
    * 注记码
    */
   private StringValue aka066 = new StringValue(this);
   /**
    * 标准价格
    */
   private DoubleValue aka068 = new DoubleValue(this);
   /**
    * 自付比例
    */
   private DoubleValue aka069 = new DoubleValue(this);
   /**
    * 基础支付标准
    */
   private DoubleValue aka104 = new DoubleValue(this);
   /**
    * 经办人
    */
   private StringValue aae011 = new StringValue(this);
   /**
    * 经办日期
    */
   private TimestampValue aae036 = new TimestampValue(this);
   /**
    * 数据分区编号
    */
   private StringValue baa001 = new StringValue(this);
   /**
    * 当前有效标志
    */
   private BooleanValue aae100 = new BooleanValue(this);
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
   public void setaka100(String aka100) {
       this.aka100.setValue(aka100);
   }

   /**
    * 医疗服务设施编码
    * @return String 药品编码
    */
   public String getaka100() {
       return aka100.getValue();
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
      this.limitPrice.modifyValue(limitPrice);
  }
  /**
    * 设置医院等级
    * @param orderCode1 String
    */
   public void setaka101(String aka101) {
       this.aka101.setValue(aka101);
   }

   /**
    * 等到医院等级
    * @return String
    */
   public String getaka101() {
       return aka101.getValue();
   }

   /**
    * 修改医院等级
    * @param hospClass String
    */
   public void modifyaka101(String aka101) {
       this.aka101.modifyValue(aka101);
   }

   /**
    * 设置服务设施名称
    * @param orderCode1 String
    */
   public void setaka102(String aka102) {
       this.aka102.setValue(aka102);
   }

   /**
    * 等到服务设施名称
    * @return String
    */
   public String getaka102() {
       return aka102.getValue();
   }

   /**
    * 修改服务设施名称
    * @param hospClass String
    */
   public void modifyaka102(String aka102) {
       this.aka102.modifyValue(aka102);
   }

   /**
    * 设置收费类别
    * @param orderCode1 String
    */
   public void setaka063(String aka063) {
       this.aka063.setValue(aka063);
   }

   /**
    * 等到收费类别
    * @return String
    */
   public String getaka063() {
       return aka063.getValue();
   }

   /**
    * 修改收费类别
    * @param hospClass String
    */
   public void modifyaka063(String aka063) {
       this.aka063.modifyValue(aka063);
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
   public void setbka246(String bka246) {
       this.bka246.setValue(bka246);
   }

   /**
    * 等到一级费用类别
    * @return String
    */
   public String getorderCat1() {
       return bka246.getValue();
   }

   /**
    * 修改一级费用类别
    * @param hospClass String
    */
   public void modifybka246(String bka246) {
       this.bka246.modifyValue(bka246);
   }

   /**
    * 设置二级费用类别
    * @param orderCode1 String
    */
   public void setbka247(String bka247) {
       this.bka247.setValue(bka247);
   }

   /**
    * 等到二级费用类别
    * @return String
    */
   public String getbka247() {
       return bka247.getValue();
   }

   /**
    * 修改二级费用类别
    * @param hospClass String
    */
   public void modifybka247(String bka247) {
       this.bka247.modifyValue(bka247);
   }

   /**
    * 设置三级费用类别
    * @param orderCode3 String
    */
   public void setbka260(String bka260) {
       this.bka260.setValue(bka260);
   }

   /**
    * 等到三级费用类别
    * @return String
    */
   public String getbka260() {
       return bka260.getValue();
   }

   /**
    * 修改三级费用类别
    * @param hospClass String
    */
   public void modifybka260(String bka260) {
       this.bka260.modifyValue(bka260);
   }


   /**
    * 设置三大目录使用范围
    * @param orderCode1 String
    */
   public void setbka001(String bka001) {
       this.bka001.setValue(bka001);
   }

   /**
    * 等到三大目录使用范围
    * @return String
    */
   public String getbka001() {
       return bka001.getValue();
   }

   /**
    * 修改三大目录使用范围
    * @param hospClass String
    */
   public void modifybka001(String bka001) {
       this.bka001.modifyValue(bka001);
   }
   /**
    * 设置病床等级
    * @param bka103 String
    */
   public void setbka103(String bka103){
       this.bka103.setValue(bka103);
   }
   /**
    * 得到病床等级
    * @return String
    */
   public String getbka103(){
       return bka103.getValue();
   }
   /**
    * 修改病床等级
    * @param bka103 String
    */
   public void modifybka103(String bka103){
       this.bka103.modifyValue(bka103);
   }

   /**
    * 设置注记码
    * @param orderCode1 String
    */
   public void setaka066(String aka066) {
       this.aka066.setValue(aka066);
   }

   /**
    * 得到注记码
    * @return String
    */
   public String getaka066() {
       return aka066.getValue();
   }

   /**
    * 修改注记码
    * @param hospClass String
    */
   public void modifyaka066(String aka066) {
       this.aka066.modifyValue(aka066);
   }

   /**
    * 标准价格
    * @param orderCode1 String
    */
   public void setaka068(double aka068) {
       this.aka068.setValue(aka068);
   }

   /**
    * 得到标准价格
    * @return String
    */
   public double getaka068() {
       return aka068.getValue();
   }

   /**
    * 修改标准价格
    * @param hospClass String
    */
   public void modifyaka068(double aka068) {
       this.aka068.modifyValue(aka068);
   }

   /**
    * 设置自付比例
    * @param orderCode1 String
    */
   public void setaka069(double aka069) {
       this.aka069.setValue(aka069);
   }

   /**
    * 得到自付比例
    * @return String
    */
   public double getaka069() {
       return aka069.getValue();
   }

   /**
    * 修改自付比例
    * @param hospClass String
    */
   public void modifyaka069(double aka069) {
       this.aka069.modifyValue(aka069);
   }

   /**
    * 设置基础支付标准
    * @param orderCode1 String
    */
   public void setaka104(double aka104) {
       this.aka104.setValue(aka104);
   }

   /**
    * 得到基础支付标准
    * @return String
    */
   public double getaka104() {
       return aka104.getValue();
   }

   /**
    * 修改基础支付标准
    * @param hospClass String
    */
   public void modifyaka104(double aka104) {
       this.aka104.modifyValue(aka104);
   }

   /**
    * 设置经办人
    * @param orderCode1 String
    */
   public void setaae011(String aae011) {
       this.aae011.setValue(aae011);
   }

   /**
    * 得到经办人
    * @return String
    */
   public String getaae011() {
       return aae011.getValue();
   }

   /**
    * 修改经办人
    * @param hospClass String
    */
   public void modifyaae011(String aae011) {
       this.aae011.modifyValue(aae011);
   }

   /**
    * 设置经办日期
    * @param orderCode1 String
    */
   public void setaae036(Timestamp aae036) {
       this.aae036.setValue(aae036);
   }

   /**
    * 得到经办日期
    * @return String
    */
   public Timestamp getaae036() {
       return aae036.getValue();
   }

   /**
    * 修改经办日期
    * @param hospClass String
    */
   public void modifyaae036(Timestamp aae036) {
       this.aae036.modifyValue(aae036);
   }

   /**
    * 设置数据分区编号
    * @param orderCode1 String
    */
   public void setbaa001(String baa001) {
       this.baa001.setValue(baa001);
   }

   /**
    * 得到数据分区编号
    * @return String
    */
   public String getbaa001() {
       return baa001.getValue();
   }

   /**
    * 修改数据分区编号
    * @param hospClass String
    */
   public void modifybaa001(String baa001) {
       this.baa001.modifyValue(baa001);
   }

   /**
    * 设置当前有效标志
    * @param orderCode1 String
    */
   public void setaae100(boolean aae100) {

       this.aae100.setValue(aae100);
   }

   /**
    * 得到当前有效标志
    * @return String
    */
   public boolean getaae100() {
       return aae100.getValue();
   }

   /**
    * 修改当前有效标志
    * @param hospClass String
    */
   public void modifyaae100(boolean aae100) {
           this.aae100.modifyValue(aae100);
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
