package jdo.ibs;

import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.TypeTool;
import jdo.odo.StoreBase;
import jdo.opd.TotQtyTool;

/**
 *
 * <p>Title: 住院费用明细对象</p>
 *
 * <p>Description: 住院费用明细对象</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSOrderD extends StoreBase {
    //
    boolean isOriginal = false;
    TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");
    /**
     * 构造器
     * @param caseNo String
     */
    public IBSOrderD(String caseNo) {
        this.caseNo = caseNo;

    }

    /**
     * 就诊序号
     */
    private String caseNo;
    /**
     * 账务序号
     */
    private int caseNoSeq;
    /**
     * 账务子序号
     */
    private String seqNo;
    /**
     * 记账日期
     */
    private Timestamp billDate;
    /**
     * 执行日期
     */
    private Timestamp execDate;
    /**
     * 医嘱序号
     */
    private String orderNo; //ORDER_NO
    /**
     * 医嘱顺序号
     */
    private String orderSeq; //ORDER_SEQ
    /**
     * 医嘱代码
     */
    private String orderCode;
    /**
     * 医嘱细分类
     */
    private String orderCat1Code;
    /**
     * 医嘱细分类群组
     */
    private String cat1Type;
    /**
     * 集合医嘱群组号
     */
    private String ordersetGroupNo;
    /**
     * 集合医嘱
     */
    private String ordersetCode;
    /**
     * 集合医嘱细项隐藏注记
     */
    private String indvFlg;
    /**
     * 科室代码
     */
    private String deptCode;
    /**
     *  病区代码
     */
    private String stationCode;
    /**
     * 医生代码
     */
    private String drCode;
    /**
     * 执行病区
     */
    private String exeStationCode;
    /**
     * 执行科室代码
     */
    private String exeDeptCode;
    /**
     * 执行医师代码
     */
    private String exeDrCode;
    /**
     * 开药数量
     */
    private double mediQty;
    /**
     * 开药单位
     */
    private String mediUnit;
    /**
     * 剂型代码
     */
    private String doseCode;
    /**
     * 频次代码
     */
    private String freqCode;
    /**
     * 开药天数
     */
    private int takeDays;
    /**
     * 配药数量
     */
    private double dosageQty;
    /**
     * 配药单位
     */
    private String dosageUnit;
    /**
     * 自费单价
     */
    private double ownPrice;
    /**
     * 医保单价
     */
    private double nhiPrice;
    /**
     * 发生金额
     */
    private double totAmt;
    /**
     * 自费注记
     */
    private String ownFlg;
    /**
     * 收费注记
     */
    private String billFlg;
    /**
     * 收据费用代码
     */
    private String rexpCode;
    /**
     * 帐单号
     */
    private String billNo; //BILL_NO
    /**
     * 院内费用代码
     */
    private String hexpCode;
    /**
     * 执行起始时间
     */
    private Timestamp beginDate;
    /**
     * 生效迄日
     */
    private Timestamp endDate;
    /**
     * 自费总价
     */
    private double ownAmt;
    /**
     * 自费比例
     */
    private double ownRate;
    /**
     * 请领注记
     */
    private String requestFlg;
    /**
     * 请领单号
     */
    private String requestNo;
    /**
     * 卫生材料序号
     */
    private String invCode;
    /**
     * 操作人员
     */
    private String optUser;
    /**
     * 操作日期
     */
    private Timestamp optDate;
    /**
     * 操作终端
     */
    private String optTerm;
    /**
     * 医嘱名称
     */
    private String orderDescIBS;
    /**
     * 成本中心
     */
    private String costCenterCode;
    /**
     * 时程
     */
    private String schdCode; //SCHD_CODE
    /**
     * 临床路径
     */
    private String clncpathCode; //CLNCPATH_CODE
    /**
     * 出院带药注记
     */
    private String dsFlg; //DS_FLG
    /**
     * 设置就诊序号
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * 得到就诊序号
     * @return String
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * 得到执行起始时间
     * @return Timestamp
     */
    public Timestamp getBeginDate() {
        return beginDate;
    }

    /**
     * 得到计帐日期
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate;
    }
    /**
     * 得到执行日期
     * @return Timestamp
     */
    public Timestamp getExecDate() {
        return execDate;
    }

    /**
     * 得到收费注记
     * @return String
     */
    public String getBillFlg() {
        return billFlg;
    }

    /**
     * 得到帐务序号
     * @return int
     */
    public int getCaseNoSeq() {
        return caseNoSeq;
    }

    /**
     * 得到医嘱细分类群组
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }

    /**
     * 得到执行病区代码
     * @return String
     */
    public String getExeStationCode() {
        return exeStationCode;
    }

    /**
     * 得到执行科室代码
     * @return String
     */
    public String getExeDeptCode() {
        return exeDeptCode;
    }

    /**
     * 得到生效迄日
     * @return Timestamp
     */
    public Timestamp getEndDate() {
        return endDate;
    }

    /**
     * 得到科室代码
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * 得到配药数量
     * @return double
     */
    public double getDosageQty() {
        return dosageQty;
    }

    /**
     * 得到配药单位
     * @return String
     */
    public String getDosageUnit() {
        return dosageUnit;
    }

    /**
     * 得到剂型代码
     * @return String
     */
    public String getDoseCode() {
        return doseCode;
    }

    /**
     * 得到医师代码
     * @return String
     */
    public String getDrCode() {
        return drCode;
    }

    /**
     * 得到执行医师代码
     * @return String
     */
    public String getExeDrCode() {
        return exeDrCode;
    }

    /**
     * 得到频次代码
     * @return String
     */
    public String getFreqCode() {
        return freqCode;
    }

    /**
     * 得到院内费用代码
     * @return String
     */
    public String getHexpCode() {
        return hexpCode;
    }

    /**
     * 得到集合医嘱细项隐藏注记
     * @return String
     */
    public String getIndvFlg() {
        return indvFlg;
    }

    /**
     * 得到卫生材料序号
     * @return String
     */
    public String getInvCode() {
        return invCode;
    }

    /**
     * 得到开药数量
     * @return double
     */
    public double getMediQty() {
    	
        return mediQty;
    }

    /**
     * 得到开药单位
     * @return String
     */
    public String getMediUnit() {
        return mediUnit;
    }

    /**
     * 得到医保单价
     * @return double
     */
    public double getNhiPrice() {
        return nhiPrice;
    }

    /**
     * 得到医嘱细分类
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code;
    }

    /**
     * 得到医嘱代码
     * @return String
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * 得到集合医嘱代码
     * @return String
     */
    public String getOrdersetCode() {
        return ordersetCode;
    }

    /**
     * 得到集合医嘱群组号
     * @return String
     */
    public String getOrdersetGroupNo() {
        return ordersetGroupNo;
    }

    /**
     * 得到自费总价
     * @return double
     */
    public double getOwnAmt() {
        return ownAmt;
    }

    /**
     * 得到自费注记
     * @return String
     */
    public String getOwnFlg() {
        return ownFlg;
    }

    /**
     * 得到自费单价
     * @return double
     */
    public double getOwnPrice() {
        return ownPrice;
    }

    /**
     * 得到自付比例
     * @return double
     */
    public double getOwnRate() {
        return ownRate;
    }

    /**
     * 得到请领注记
     * @return String
     */
    public String getRequestFlg() {
        return requestFlg;
    }

    /**
     * 得到请领单号
     * @return String
     */
    public String getRequestNo() {
        return requestNo;
    }

    /**
     * 得到收据费用代码
     * @return String
     */
    public String getRexpCode() {
        return rexpCode;
    }

    /**
     * 得到账单子序号
     * @return String
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     *得到开单病区
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * 得到天数
     * @return int
     */
    public int getTakeDays() {
        return takeDays;
    }

    /**
     * 得到总价
     * @return double
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * 得到操作日期
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate;
    }

    /**
     * 得到操作终端
     * @return String
     */
    public String getOptTerm() {
        return optTerm;
    }

    /**
     * 得到操作人员
     * @return String
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * 得到帐单号
     * @return String
     */
    public String getBillNo() {
        return billNo;
    }

    /**
     * 得到医嘱序号
     * @return String
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 得到医嘱顺序号
     * @return String
     */
    public String getOrderSeq() {
        return orderSeq;
    }

    /**
     * 得到医嘱名称
     * @return String
     */
    public String getOrderDescIBS() {
        return orderDescIBS;
    }

    /**
     * 得到成本中心
     * @return String
     */
    public String getCostCenterCode() {
        return costCenterCode;
    }

    /**
     * 得到时程
     * @return String
     */
    public String getSchdCode() {
        return schdCode;
    }

    /**
     * 得到临床路径
     * @return String
     */
    public String getClncpathCode() {
        return clncpathCode;
    }
    /**
     * 得到出院带药注记
     * @return String
     */
    public String getDsFlg() {
        return dsFlg;
    }

    /**
     * 设置执行起始时间
     * @param beginDate Timestamp
     */
    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * 设置计帐日期
     * @param billDate Timestamp
     */
    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }
    /**
     * 设置执行日期
     * @param billDate Timestamp
     */
    public void setExecDate(Timestamp execDate) {
        this.execDate = execDate;
    }

    /**
     * 设置收费注记
     * @param billFlg String
     */
    public void setBillFlg(String billFlg) {
        this.billFlg = billFlg;
    }

    /**
     * 设置帐务序号
     * @param caseNoSeq int
     */
    public void setCaseNoSeq(int caseNoSeq) {
        this.caseNoSeq = caseNoSeq;
    }

    /**
     * 设置医嘱细分类群组
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type) {
        this.cat1Type = cat1Type;
    }

    /**
     * 设置生效迄日
     * @param endDate Timestamp
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * 设置执行科室
     * @param exeDeptCode String
     */
    public void setExeDeptCode(String exeDeptCode) {
        this.exeDeptCode = exeDeptCode;
    }

    /**
     * 设置开单科室
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * 设置配药数量
     * @param dosageQty double
     */
    public void setDosageQty(double dosageQty) {
        this.dosageQty = dosageQty;
    }

    /**
     * 设置配药单位
     * @param dosageUnit String
     */
    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    /**
     * 设置剂型代码
     * @param doseCode String
     */
    public void setDoseCode(String doseCode) {
        this.doseCode = doseCode;
    }

    /**
     * 设置开单医师
     * @param drCode String
     */
    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    /**
     * 设置执行病区
     * @param exeStationCode String
     */
    public void setExeStationCode(String exeStationCode) {
        this.exeStationCode = exeStationCode;
    }

    /**
     * 设置执行医师
     * @param exeDrCode String
     */
    public void setExeDrCode(String exeDrCode) {
        this.exeDrCode = exeDrCode;
    }

    /**
     * 设置频次代码
     * @param freqCode String
     */
    public void setFreqCode(String freqCode) {
        this.freqCode = freqCode;
    }

    /**
     * 设置院内费用代码
     * @param hexpCode String
     */
    public void setHexpCode(String hexpCode) {
        this.hexpCode = hexpCode;
    }

    /**
     * 设置集合医嘱细项隐藏注记
     * @param indvFlg String
     */
    public void setIndvFlg(String indvFlg) {
        this.indvFlg = indvFlg;
    }

    /**
     * 设置卫生材料序号
     * @param invCode String
     */
    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    /**
     * 设置开药数量
     * @param mediQty double
     */
    public void setMediQty(double mediQty) {
        this.mediQty = mediQty;
    }

    /**
     * 设置开药单位
     * @param mediUnit String
     */
    public void setMediUnit(String mediUnit) {
        this.mediUnit = mediUnit;
    }

    /**
     * 设置医保单价
     * @param nhiPrice double
     */
    public void setNhiPrice(double nhiPrice) {
        this.nhiPrice = nhiPrice;
    }

    /**
     * 设置医嘱细分类
     * @param orderCat1Code String
     */
    public void setOrderCat1Code(String orderCat1Code) {
        this.orderCat1Code = orderCat1Code;
    }

    /**
     * 设置医嘱代码
     * @param orderCode String
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    /**
     * 设置集合医嘱
     * @param ordersetCode String
     */
    public void setOrdersetCode(String ordersetCode) {
        this.ordersetCode = ordersetCode;
    }

    /**
     * 设置集合医嘱群组号
     * @param ordersetGroupNo String
     */
    public void setOrdersetGroupNo(String ordersetGroupNo) {
        this.ordersetGroupNo = ordersetGroupNo;
    }

    /**
     * 设置自费总价
     * @param ownAmt double
     */
    public void setOwnAmt(double ownAmt) {
        this.ownAmt = ownAmt;
    }

    /**
     * 设置自费标记
     * @param ownFlg String
     */
    public void setOwnFlg(String ownFlg) {
        this.ownFlg = ownFlg;
    }

    /**
     * 设置自费单价
     * @param ownPrice double
     */
    public void setOwnPrice(double ownPrice) {
        this.ownPrice = ownPrice;
    }

    /**
     * 设置支付比例
     * @param ownRate double
     */
    public void setOwnRate(double ownRate) {
        this.ownRate = ownRate;
    }

    /**
     * 设置请领注记
     * @param requestFlg String
     */
    public void setRequestFlg(String requestFlg) {
        this.requestFlg = requestFlg;
    }

    /**
     * 设置请领单号
     * @param requestNo String
     */
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    /**
     * 设置收据代码
     * @param rexpCode String
     */
    public void setRexpCode(String rexpCode) {
        this.rexpCode = rexpCode;
    }

    /**
     * 设置账务子序号
     * @param seqNo String
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * 设置开单科室
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     * 设置天数
     * @param takeDays int
     */
    public void setTakeDays(int takeDays) {
        this.takeDays = takeDays;
    }

    /**
     * 设置总价
     * @param totAmt double
     */
    public void setTotAmt(double totAmt) {
        this.totAmt = totAmt;
    }

    /**
     * 设置操作日期
     * @param optDate Timestamp
     */
    public void setOptDate(Timestamp optDate) {
        this.optDate = optDate;
    }

    /**
     * 设置操作终端
     * @param optTerm String
     */
    public void setOptTerm(String optTerm) {
        this.optTerm = optTerm;
    }

    /**
     * 设置操作人员
     * @param optUser String
     */
    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }

    /**
     * 设置帐单号
     * @param billNo String
     */
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    /**
     * 设置医嘱序号
     * @param orderNo String
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 设置医嘱顺序号
     * @param orderSeq String
     */
    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }

    /**
     * 设置医嘱名称
     * @param orderDescIBS String
     */
    public void setOrderDescIBS(String orderDescIBS) {
        this.orderDescIBS = orderDescIBS;
    }

    /**
     * 设置成本中心
     * @param costCenterCode String
     */
    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    /**
     * 设置时程
     * @param schdCode String
     */
    public void setSchdCode(String schdCode) {
        this.schdCode = schdCode;
    }

    /**
     * 设置临床路径
     * @param clncpathCode String
     */
    public void setClncpathCode(String clncpathCode) {
        this.clncpathCode = clncpathCode;
    }
    /**
     * 设置出院带药注记
     * @param dsFlg String
     */
    public void setDsFlg(String dsFlg) {
        this.dsFlg = dsFlg;
    }


    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL() {
        return "SELECT * FROM IBS_ORDD WHERE CASE_NO = '' ";
    }

    /**
     * 覆盖父类赋值方法
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setItem(int row, String column, Object value) {
        super.setItem(row, column, value);
        //总量
        if (column.equals("DOSAGE_QTY")) {
//            System.out.println("改变总量" + value);
            double dosageQty = TypeTool.getDouble(value);
            double ownPrice = getItemDouble(row, "OWN_PRICE");
            setItem(row, "TOT_AMT", dosageQty * ownPrice*ownRate); //add by ownRate huangtt 20131114
            setItem(row, "OWN_AMT", dosageQty * ownPrice);
            String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
//            System.out.println("集合医嘱主项代码"+orderSetCode);
            //判断是否是集合医嘱主项 如果是主项那么计算其子项的总量
            if (orderSetCode != null &&
                getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
//                System.out.println("是集合医嘱"+rowCountFilter());
                String orderCode = this.getItemString(row, "ORDERSET_CODE");
//                setItem(row, "TOT_AMT", 0.00);
                setItem(row, "OWN_AMT", 0.00);
                TParm parm = this.getRowParm(row);
//                System.out.println("主项数据"+parm);
                //循环计算子项的总量
                for (int k = 0; k < this.rowCountFilter(); k++) {
//                    System.out.println("集合医嘱细项代码"+this.
//                            getItemData(k, "ORDER_CODE", this.FILTER));
                    //判断是否是该集合医嘱的子项
                    if (orderCode.equals(TypeTool.getString(this.getItemData(
                            k, "ORDERSET_CODE", this.FILTER))) && (!this.
                            getItemData(k, "ORDER_CODE",
                                        this.FILTER).equals(orderCode))) {
//                        System.out.println("是集合医嘱细项");
                        //细项的默认数量
                        double osTot = this.getOrderSetTot(TypeTool.getString(this.
                                getItemData(k, "ORDER_CODE", this.FILTER))
                                ,
                                TypeTool.getString(this.getItemData(k,
                                "ORDERSET_CODE",
                                this.FILTER)));
//                        System.out.println("默认数量"+osTot);
                        this.setItem(k, "DOSAGE_QTY",
                                     osTot * parm.getDouble("DOSAGE_QTY"),
                                     this.FILTER);
                        this.setItem(k, "TOT_AMT",
                                     osTot * parm.getDouble("DOSAGE_QTY") *
                                     TypeTool.getDouble(this.getItemData(k, "OWN_PRICE", this.FILTER))*ownRate, 
                                     this.FILTER);
                        this.setItem(k, "OWN_AMT",
                                     osTot * parm.getDouble("DOSAGE_QTY") *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER)),
                                     this.FILTER);
                    }
                }
            }

        }
        //用量
        if (column.equals("MEDI_QTY")) {
            double mediqty = TypeTool.getDouble(value);
            int takeDays = getItemInt(row, "TAKE_DAYS");
            String mediUnit = getItemString(row, "MEDI_UNIT");
            String freqCode = getItemString(row, "FREQ_CODE");
            TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
                                              takeDays);
            setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));

            double dosageQty = parm.getDouble("DOSAGE_QTY");
            double ownPrice = getItemDouble(row, "OWN_PRICE");
            setItem(row, "OWN_AMT", dosageQty * ownPrice);
            String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
            //判断是否是集合医嘱主项 如果是主项那么计算其子项的总量
            if (orderSetCode != null &&
                getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
                String orderCode = this.getItemString(row, "ORDERSET_CODE");
                setItem(row, "OWN_AMT", 0.00);
                //循环计算子项的总量
                for (int k = 0; k < this.rowCountFilter(); k++) {
                    //判断是否是该集合医嘱的子项
                    if (orderCode.equals(TypeTool.getString(this.getItemData(
                            k, "ORDERSET_CODE", this.FILTER))) && (!this.
                            getItemData(k, "ORDER_CODE",
                                        this.FILTER).equals(orderCode))) {
                        //细项的默认数量
                        double osTot = this.getOrderSetTot(TypeTool.getString(this.
                                getItemData(k, "ORDER_CODE", this.FILTER))
                                ,
                                TypeTool.getString(this.getItemData(k,
                                "ORDERSET_CODE",
                                this.FILTER)));
                        this.setItem(k, "MEDI_QTY",
                                     mediqty,
                                     this.FILTER);
                        this.setItem(k, "DOSAGE_QTY",
                                     osTot * dosageQty,
                                     this.FILTER);
                        this.setItem(k, "TOT_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER))*ownRate,
                                     this.FILTER);
                        this.setItem(k, "OWN_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER)),
                                     this.FILTER);
                    }
                }
            }

        }
        //天数
        if (column.equals("TAKE_DAYS")) {
            int takeDays = TypeTool.getInt(value);
            double mediqty = getItemDouble(row, "MEDI_QTY");
            String mediUnit = getItemString(row, "MEDI_UNIT");
            String freqCode = getItemString(row, "FREQ_CODE");
            setItem(row, "DOSAGE_QTY", mediqty * takeDays);
            TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
                                              takeDays);
            setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
            double dosageQty = parm.getDouble("DOSAGE_QTY");
            double ownPrice = getItemDouble(row, "OWN_PRICE");
            setItem(row, "OWN_AMT", dosageQty * ownPrice);
            String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
            //判断是否是集合医嘱主项 如果是主项那么计算其子项的总量
            if (orderSetCode != null &&
                getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
                String orderCode = this.getItemString(row, "ORDERSET_CODE");
                setItem(row, "OWN_AMT", 0.00);
                //循环计算子项的总量
                for (int k = 0; k < this.rowCountFilter(); k++) {
                    //判断是否是该集合医嘱的子项
                    if (orderCode.equals(TypeTool.getString(this.getItemData(
                            k, "ORDERSET_CODE", this.FILTER))) && (!this.
                            getItemData(k, "ORDER_CODE",
                                        this.FILTER).equals(orderCode))) {
                        //细项的默认数量
                        double osTot = this.getOrderSetTot(TypeTool.getString(this.
                                getItemData(k, "ORDER_CODE", this.FILTER))
                                ,
                                TypeTool.getString(this.getItemData(k,
                                "ORDERSET_CODE",
                                this.FILTER)));
                        this.setItem(k, "TAKE_DAYS",
                                     takeDays,
                                     this.FILTER);
                        this.setItem(k, "DOSAGE_QTY",
                                     osTot * dosageQty,
                                     this.FILTER);
                        this.setItem(k, "TOT_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER))*ownRate,
                                     this.FILTER);
                        this.setItem(k, "OWN_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER)),
                                     this.FILTER);
                    }
                }
            }

        }
        //频次
        if (column.equals("FREQ_CODE")) {
            String freqCode = TypeTool.getString(value);
            double mediqty = getItemDouble(row, "MEDI_QTY");
            String mediUnit = getItemString(row, "MEDI_UNIT");
            int takeDays = getItemInt(row, "TAKE_DAYS");
            TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
                                              takeDays);
            setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
            double dosageQty = parm.getDouble("DOSAGE_QTY");
            String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
            double ownPrice = getItemDouble(row, "OWN_PRICE");
            setItem(row, "OWN_AMT", dosageQty * ownPrice);
            //判断是否是集合医嘱主项 如果是主项那么计算其子项的总量
            if (orderSetCode != null &&
                getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
                String orderCode = this.getItemString(row, "ORDERSET_CODE");
                setItem(row, "OWN_AMT", 0.00);
                //循环计算子项的总量
                for (int k = 0; k < this.rowCountFilter(); k++) {
                    //判断是否是该集合医嘱的子项
                    if (orderCode.equals(TypeTool.getString(this.getItemData(
                            k, "ORDERSET_CODE", this.FILTER))) && (!this.
                            getItemData(k, "ORDER_CODE",
                                        this.FILTER).equals(orderCode))) {
                        //细项的默认数量
                        double osTot = this.getOrderSetTot(TypeTool.getString(this.
                                getItemData(k, "ORDER_CODE", this.FILTER))
                                ,
                                TypeTool.getString(this.getItemData(k,
                                "ORDERSET_CODE",
                                this.FILTER)));
                        this.setItem(k, "FREQ_CODE",
                                     freqCode,
                                     this.FILTER);
                        this.setItem(k, "DOSAGE_QTY",
                                     osTot * dosageQty,
                                     this.FILTER);
                        this.setItem(k, "TOT_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER))*ownRate,
                                     this.FILTER);
                        this.setItem(k, "OWN_AMT",
                                     osTot * dosageQty *
                                     TypeTool.getDouble(this.
                                getItemData(k, "OWN_PRICE", this.FILTER)),
                                     this.FILTER);
                    }
                }
            }

        }

        return true;
    }

    /**
     * 获取集合医嘱细项的默认数量
     * @param order_code String 细项CODE
     * @param orderset_code String  主项CODE
     * @return double
     */
    public double getOrderSetTot(String order_code, String orderset_code) {
        String sql =
                "SELECT DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE='" +
                orderset_code + "' AND ORDER_CODE = '" + order_code + "'";
//        System.out.println("住院集合医嘱sql" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result.getDouble("DOSAGE_QTY", 0);
    }


    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row) {
        int newRow = super.insertRow(row);
        if (newRow == -1)
            return -1;
        //设置就诊序号
        setItem(newRow, "CASE_NO", getCaseNo());
        //账单序号
        setItem(newRow, "CASE_NO_SEQ", this.getCaseNoSeq());
        //账单子序号
        setItem(newRow, "SEQ_NO", this.getSeqNo());
        //记账日期
        setItem(newRow, "BILL_DATE", this.getBillDate());
        //执行日期
        setItem(newRow, "EXEC_DATE", this.getExecDate());
      //时程
//        setItem(newRow, "SCHD_CODE", this.getSchdCode());
        //医嘱序号
        setItem(newRow, "ORDER_NO", this.getOrderNo());
        //医嘱顺序号
        setItem(newRow, "ORDER_SEQ", this.getOrderSeq());
        //生效起日
        setItem(newRow, "BEGIN_DATE", this.getBeginDate());
        //生效迄日
        setItem(newRow, "END_DATE", this.getEndDate());
        //开单科室
        setItem(newRow, "DEPT_CODE", this.getDeptCode());
        //开单病区
        setItem(newRow, "STATION_CODE", this.getStationCode());
        //开单医师
        setItem(newRow, "DR_CODE", this.getDrCode());
//        System.out.println("成本中心"+this.getCostCenterCode());
//        System.out.println("执行单位"+this.getExeDeptCode());
        //执行科室
        setItem(newRow, "EXE_DEPT_CODE", this.getCostCenterCode());
        //执行病区
        setItem(newRow, "EXE_STATION_CODE", this.getExeStationCode());
        //执行医师
        setItem(newRow, "EXE_DR_CODE", this.getExeDrCode());
        //操作人员
        setItem(newRow, "OPT_USER", this.getOptUser());
        //操作日期
        setItem(newRow, "OPT_DATE", this.getOptDate());
        //操作人员
        setItem(newRow, "OPT_TERM", this.getOptTerm());
        //医嘱名称
        setItem(newRow, "ORDER_CHN_DESC", this.getOrderDescIBS());
        //成本中心
        setItem(newRow, "COST_CENTER_CODE", this.getCostCenterCode());
        //临床路径
        setItem(newRow, "SCHD_CODE", this.getSchdCode());
        //时程
        setItem(newRow, "CLNCPATH_CODE", this.getClncpathCode());
        //出院带药注记
        setItem(newRow, "DS_FLG", this.getDsFlg());
        return newRow;
    }

    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm, int row, String column,
                                       Object value) {
        if ("ORDER_DESC".equalsIgnoreCase(column)) {
            setItem(row, "ORDER_CODE", value);
        }
        return true;
    }


    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
        if ("ORDER_DESC".equalsIgnoreCase(column)) {
            if (parm.getValue("ORDER_CODE", row).length() == 0)
                return "";

            TParm sysFeeParm = sysFee.getBuffer(sysFee.isFilter() ?
                                                sysFee.FILTER :
                                                sysFee.PRIMARY);
            Vector code = (Vector) sysFeeParm.getData("ORDER_CODE");
            String orderCode = parm.getValue("ORDER_CODE", row);
            int rowNow = code.indexOf(orderCode);
            if (rowNow == -1)
                return "";
            if (sysFeeParm.getValue("SPECIFICATION", rowNow).toString().length() ==
                0 || sysFeeParm.getValue("SPECIFICATION", rowNow) == null) {
                //回传table显示数据(医嘱名称)当规格为空时
                return sysFeeParm.getValue("ORDER_DESC", rowNow);
            }
            //回传table显示数据(医嘱名称+规格)
            return sysFeeParm.getValue("ORDER_DESC", rowNow) + "(" +
                    sysFeeParm.getValue("SPECIFICATION", rowNow) + ")";
        }
        return "";
    }
}
