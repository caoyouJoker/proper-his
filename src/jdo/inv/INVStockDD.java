package jdo.inv;

import com.dongyang.data.TModifiedData;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

public class INVStockDD
    extends TModifiedData {
    /**
     * 物资代码 INV_CODE
     */
    private String invCode;
    /**
     * 物资序号 INVSEQ_NO
     */
    private int invseqNo;
    /**
     * 区域代码 REGION_CODE
     */
    private String regionCode;
    /**
     * 批次序号 BATCH_SEQ
     */
    private int batchSeq;
    /**
     * 部門科室 ORG_CODE
     */
    private String orgCode;
    /**
     * 批号 BATCH_NO
     */
    private String batChNo;
    /**
     * 效期 VALID_DATE
     */
    private Timestamp validDate;
    /**
     * 存量库 STOCK_QTY
     */
    private double stockQty;
    /**
     * 单财产价值 UNIT_PRICE
     */
    private double unitPrice;
    /**
     * 盘点损失注记 CHECKTOLOSE_FLG
     */
    private String checkToLostFlg;
    /**
     * 损耗注记 WAST_FLG
     */
    private String wastFlg;
    /**
     * 入库日期 VERIFYIN_DATE
     */
    private Timestamp verifyInDate;
    /**
     * 计入病患出库日期 OUT_DATE
     */
    private Timestamp outDate;
    /**
     * 计入病患出库人员 OUT_USER
     */
    private String outUser;
    /**
     * 病案号 MR_NO
     */
    private String mrNo;
    /**
     * 就诊号 CASE_NO
     */
    private String caseNo;
    /**
     * 处方号 RX_SEQ
     */
    private int rxSeq;
    /**
     * 医嘱序号 SEQ_NO
     */
    private int seqNo;
    /**
     * 门急住别 ADM_TYPE
     */
    private String admType;
    /**
     * 在途待入库科室 WAIT_ORG_CODE
     */
    private String waitOrgCode;
    /**
    * 增删标记
    */
   private boolean sqlFlg;
   /**
    * 单位
    */
   private String stockUnit;

    /**
     *  构造器
     */
    public INVStockDD() {
        StringBuffer sb = new StringBuffer();
        //物资代码 INV_CODE
        sb.append("invCode:INV_CODE;");
        //物资序号 INVSEQ_NO
        sb.append("invseqNo:INVSEQ_NO;");
        //区域代码 REGION_CODE
        sb.append("regionCode:REGION_CODE;");
        //设置批次序号 BATCH_SEQ
        sb.append("batchSeq:BATCH_SEQ;");
        // 部門科室 ORG_CODE
        sb.append("orgCode:ORG_CODE;");
        //批号 BATCH_NO
        sb.append("batChNo:BATCH_NO;");
        // 效期 VALID_DATE
        sb.append("validDate:VALID_DATE;");
        //存量库 STOCK_QTY
        sb.append("stockQty:STOCK_QTY;");
        // 单财产价值 UNIT_PRICE
        sb.append("unitPrice:UNIT_PRICE;");
        // 盘点损失注记 CHECKTOLOSE_FLG
        sb.append("checkToLostFlg:CHECKTOLOSE_FLG;");
        //损耗注记 WAST_FLG
        sb.append("wastFlg:WAST_FLG;");
        // 入库日期 VERIFYIN_DATE
        sb.append("verifyInDate:VERIFYIN_DATE;");
        //计入病患出库日期 OUT_DATE
        sb.append("outDate:OUT_DATE;");
        // 计入病患出库人员 OUT_USER
        sb.append("outUser:OUT_USER;");
        // 病案号 MR_NO
        sb.append("mrNo:MR_NO;");
        // 就诊号 CASE_NO
        sb.append("caseNo:CASE_NO;");
        // 处方号 RX_SEQ
        sb.append("rxSeq:RX_SEQ;");
        // 医嘱序号 SEQ_NO
        sb.append("seqNo:SEQ_NO;");
        //门急住别 ADM_TYPE
        sb.append("admType:ADM_TYPE;");
        // 在途待入库科室 WAIT_ORG_CODE
        sb.append("waitOrgCode:WAIT_ORG_CODE;");
        // 单位
        sb.append("stockUnit:STOCK_UNIT;");
        //增删标记
         sb.append("sqlFlg:SQLFLG;");
        setMapString(sb.toString());
    }
    /**
       * 设置增删标记
       * @param sqlFlg boolean
       */
      public void setSqlFlg(boolean sqlFlg) {
          this.sqlFlg = sqlFlg;
      }

      /**
       * 得到增删标记
       * @return boolean
       */
      public boolean getSqlFlg() {
          return this.sqlFlg;
    }
    /**
     * 设置物资代码 INV_CODE
     * @param invCode String
     */
    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    /**
     * 得到物资代码 INV_CODE
     * @return String
     */
    public String getInvCode() {
        return this.invCode;
    }

    /**
     * 设置物资序号 INVSEQ_NO
     * @param invseqNo int
     */
    public void setInvseqNo(int invseqNo) {
        this.invseqNo = invseqNo;
    }

    /**
     * 得到物资序号 INVSEQ_NO
     * @return int
     */
    public int getInvseqNo() {
        return this.invseqNo;
    }

    /**
     * 设置区域代码 REGION_CODE
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 得到区域代码 REGION_CODE
     * @return String
     */
    public String getRegionCode() {
        return this.regionCode;
    }
    /**
     * 设置批次序号 BATCH_SEQ
     */
    public void setBatchSeq(int batchSeq){
    this.batchSeq=batchSeq;
    }
    /**
     * 得到批次序号 BATCH_SEQ
     * @return int
     */
    public int setBatchSeq() {
        return this.batchSeq;
    };
    /**
     * 设置部門科室 ORG_CODE
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * 得到部門科室 ORG_CODE
     * @return String
     */
    public String getOrgCode() {
        return this.orgCode;
    }

    /**
     * 设置批号 BATCH_NO
     * @param batChNo String
     */
    public void setBatChNo(String batChNo) {
        this.batChNo = batChNo;
    }

    /**
     * 得到批号 BATCH_NO
     * @return String
     */
    public String getBatChNo() {
        return this.batChNo;
    }

    /**
     * 设置效期 VALID_DATE
     * @param validDate Timestamp
     */
    public void setCalidDate(Timestamp validDate) {
        this.validDate = validDate;
    }

    /**
     * 得到效期 VALID_DATE
     * @return Timestamp
     */
    public Timestamp getValidDate() {
        return this.validDate;
    }

    /**
     * 设置存量库 STOCK_QTY
     * @param stockQty double
     */
    public void setStockQty(double stockQty) {
        this.stockQty = stockQty;
    }

    /**
     * 得到存量库 STOCK_QTY
     * @return double
     */
    public double getStockQty() {
        return this.stockQty;
    }

    /**
     * 设置单财产价值 UNIT_PRICE
     * @param unitPrice double
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 得到单财产价值 UNIT_PRICE
     * @return double
     */
    public double getUnitPrice() {
        return this.unitPrice;
    }

    /**
     * 设置盘点损失注记 CHECKTOLOSE_FLG
     * @param checkToLostFlg String
     */
    public void setCheckToLostFlg(String checkToLostFlg) {
        this.checkToLostFlg = checkToLostFlg;
    }

    /**
     * 得到盘点损失注记 CHECKTOLOSE_FLG
     * @return String
     */
    public String getCheckToLostFlg() {
        return this.checkToLostFlg;
    }

    /**
     * 设置损耗注记 WAST_FLG
     * @param wastFlg String
     */
    public void setWastFlg(String wastFlg) {
        this.wastFlg = wastFlg;
    }

    /**
     * 得到损耗注记 WAST_FLG
     * @return String
     */
    public String getWastFlg() {
        return this.wastFlg;
    }

    /**
     * 设置入库日期 VERIFYIN_DATE
     * @param verifyInDate Timestamp
     */
    public void setVerifyInDate(Timestamp verifyInDate) {
        this.verifyInDate = verifyInDate;
    }

    /**
     * 得到入库日期 VERIFYIN_DATE
     * @return Timestamp
     */
    public Timestamp getVerifyInDate() {
        return this.verifyInDate;
    }

    /**
     * 设置计入病患出库日期 OUT_DATE
     * @param outDate Timestamp
     */
    public void setOutDate(Timestamp outDate) {
        this.outDate = outDate;
    }

    /**
     * 得到计入病患出库日期 OUT_DATE
     * @return Timestamp
     */
    public Timestamp getOutDate() {
        return this.outDate;
    }

    /**
     * 设置计入病患出库人员 OUT_USER
     * @param outUser String
     */
    public void setOutUser(String outUser) {
        this.outUser = outUser;
    }

    /**
     * 得到计入病患出库人员 OUT_USER
     * @return String
     */
    public String getOutUser() {
        return this.outUser;
    }

    /**
     * 设置病案号 MR_NO
     * @param mrNo String
     */
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * 得到病案号 MR_NO
     * @return String
     */
    public String getMrNo() {
        return this.mrNo;
    }

    /**
     * 设置就诊号 CASE_NO
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * 得到就诊号 CASE_NO
     * @return String
     */
    public String getCaseNo() {
        return this.caseNo;
    }

    /**
     * 设置处方号 RX_SEQ
     * @param rxSeq int
     */
    public void setRxSeq(int rxSeq) {
        this.rxSeq = rxSeq;
    }

    /**
     * 得到处方号 RX_SEQ
     * @return int
     */
    public int getRxSeq() {
        return this.rxSeq;
    }

    /**
     * 设置医嘱序号 SEQ_NO
     * @param seqNo int
     */
    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * 得到医嘱序号 SEQ_NO
     * @return int
     */
    public int getSeqNo() {
        return this.seqNo;
    }

    /**
     * 设置门急住别 ADM_TYPE
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * 得到门急住别 ADM_TYPE
     * @return String
     */
    public String getAdmType() {
        return this.admType;
    }

    /**
     * 设置在途待入库科室 WAIT_ORG_CODE
     * @param waitOrgCode String
     */
    public void setWaitOrgCode(String waitOrgCode) {
        this.waitOrgCode = waitOrgCode;
    }

    /**
     * 得到在途待入库科室 WAIT_ORG_CODE
     * @return String
     */
    public String getWaitOrgCode() {
        return this.waitOrgCode;
    }
    /**
     * 设置在途待入库科室 WAIT_ORG_CODE
     * @param waitOrgCode String
     */
    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    /**
     * 得到在途待入库科室 WAIT_ORG_CODE
     * @return String
     */
    public String getStockUnit() {
        return this.stockUnit;
    }


    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * 初始化序号管理
     * @param invCode String(物资代码)
     * @param invseqNo int(物资序号)
     * @return INVStockDD
     */
    public INVStockDD initInvStock(String invCode, int invseqNo) {
        //检核入参不能为空
        if (invCode == null ||
            invCode.length() == 0 || invseqNo == 0)
            return null;
        TParm parm = new TParm();
        //科室代码
        parm.setData("ORG_CODE", orgCode);
        //物资代码
        parm.setData("INV_CODE", invCode);
        INVStockDD invStock = new INVStockDD();

        return invStock;
    }
//    /**
//     * 传入parm得到一个序号管理对象
//     * @param parm TParm
//     * @return INVStockDD
//     */
//    public INVStockDD initInvStock(TParm parm,boolean sqlFlg) {
//        INVStockDD invStockDD = new INVStockDD();
//        //设置物资代码 INV_CODE
//        invStockDD.setInvCode(parm.getValue("INV_CODE"));
//        // 设置物资序号 INVSEQ_NO/
//        invStockDD.setInvseqNo(parm.getInt("INVSEQ_NO"));
//        // 设置区域代码 REGION_CODE
//        invStockDD.setRegionCode(parm.getValue("REGION_CODE"));
//        //设置部門科室 ORG_CODE
//        invStockDD.setOrgCode(parm.getValue("ORG_CODE"));
//        // 设置批号 BATCH_NO
//        invStockDD.setBatChNo(parm.getValue("BATCH_NO"));
//        //设置效期 VALID_DATE
//        invStockDD.setCalidDate(parm.getTimestamp("VALID_DATE"));
//        //设置存量库 STOCK_QTY
//        invStockDD.setStockQty(parm.getDouble("STOCK_QTY"));
//        // 设置单财产价值 UNIT_PRICE
//        invStockDD.setUnitPrice(parm.getDouble("UNIT_PRICE"));
//        //设置盘点损失注记 CHECKTOLOSE_FLG
//        invStockDD.setCheckToLostFlg(parm.getValue("CHECKTOLOSE_FLG"));
//        //设置损耗注记 WAST_FLG
//        invStockDD.setWastFlg(parm.getValue("WAST_FLG"));
//        //设置入库日期 VERIFYIN_DATE
//        invStockDD.setVerifyInDate(parm.getTimestamp("VERIFYIN_DATE"));
//        // 设置计入病患出库日期 OUT_DATE
//        invStockDD.setOutDate(parm.getTimestamp("OUT_DATE"));
//        // 设置计入病患出库人员 OUT_USER
//        invStockDD.setOutUser(parm.getValue("OUT_USER"));
//        //设置病案号 MR_NO
//        invStockDD.setMrNo(parm.getValue("MR_NO"));
//        // 设置就诊号 CASE_NO
//        invStockDD.setCaseNo(parm.getValue("CASE_NO"));
//        //设置处方号 RX_SEQ
//        invStockDD.setRxSeq(parm.getInt("RX_SEQ"));
//        // 设置医嘱序号 SEQ_NO
//        invStockDD.setSeqNo(parm.getInt("SEQ_NO"));
//        //设置门急住别 ADM_TYPE
//        invStockDD.setAdmType(parm.getValue("ADM_TYPE"));
//        // 在途待入库科室 WAIT_ORG_CODE
//        invStockDD.setWaitOrgCode(parm.getValue("WAIT_ORG_CODE"));
//        //增删标记
//        invStockDD.setSqlFlg(sqlFlg);
//        return invStockDD;
//    }
    /**
        * 传入parm得到一个序号管理对象
        * @param parm TParm
        * @return INVStockDD
        */
       public INVStockDD initInvStock(TParm parm,boolean sqlFlg) {
           INVStockDD invStockDD = new INVStockDD();
           //设置物资代码 INV_CODE
           invStockDD.setInvCode(parm.getValue("INV_CODE"));
           // 设置物资序号 INVSEQ_NO/
           invStockDD.setInvseqNo(parm.getInt("DDSEQ_NO"));
           // 设置区域代码 REGION_CODE
           invStockDD.setRegionCode(parm.getValue("REGION_CODE"));
           //批次序号
           invStockDD.setBatchSeq(parm.getInt("BATCH_SEQ"));
           //设置部門科室 ORG_CODE
           invStockDD.setOrgCode(parm.getValue("ORG_CODE"));
           // 设置批号 BATCH_NO
           invStockDD.setBatChNo(parm.getValue("BATCH_NO"));
           //设置效期 VALID_DATE
           invStockDD.setCalidDate(parm.getTimestamp("VALID_DATE"));
           //设置入库日期 VERIFYIN_DATE
           invStockDD.setVerifyInDate(parm.getTimestamp("VERIFYIN_DATE"));
           //设置入库日期 VERIFYIN_DATE
           invStockDD.setStockUnit(parm.getValue("STOCK_UNIT"));
           invStockDD.setUnitPrice(parm.getDouble("UNIT_PRICE"));
           //增删标记
           invStockDD.setSqlFlg(sqlFlg);
           return invStockDD;
    }
}
