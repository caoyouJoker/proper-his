package jdo.reg;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import jdo.sys.Pat;
import java.sql.Timestamp;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.TimestampValue;
import com.dongyang.data.TParm;
import com.javahis.util.JavaHisDebug;
import jdo.sys.Operator;

/**
 *
 * <p>Title: 挂号对象</p>
 *
 * <p>Description: 挂号对象</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.09.19
 * @version 1.0
 */
public class Reg extends TModifiedData{
    /**
     * 压力测试使用开关
     */
    private boolean tload;
    /**
     * 患者对象
     */
    private Pat pat;
    /**
     * 门诊收据
     */
    private RegReceipt regReceipt;
    /**
     * 就诊号 CASE_NO
     */
    private String caseNo;
    /**
     * 门急别 ADM_TYPE
     */
    private String admType;
    /**
     * 操作院区 REGION
     */
    private String region;
    /**
     * 挂号日期 ADM_DATE
     */
    private Timestamp admDate;
    /**
     * 挂号操作日期 REG_DATE
     */
    private Timestamp regDate;
    /**
     * 时段代码 SESSION_CODE
     */
    private String sessionCode;
    /**
     * 诊区 CLINICAREA_CODE
     */
    private String clinicareaCode;
    /**
     * 诊间号 CLINICROOM_NO
     */
    private String clinicroomNo;
    /**
     * 号别 CLINICTYPE_CODE
     */
    private String clinictypeCode;
    /**
     * 就诊号 QUE_NO
     */
    private int queNo;
    /**
     * 预约时间 (0800) 预约8点 REG_ADM_TIME
     */
    private String regAdmTime;
    /**
     * 科别代码 DEPT_CODE
     */
    private String deptCode;
    /**
     * 看诊医师 DR_CODE
     */
    private String drCode;
    /**
     * 实际看诊科别 REALDEPT_CODE
     */
    private StringValue realdeptCode = new StringValue(this);
    /**
     * 实际看诊医师 REALDR_CODE
     */
    private StringValue realdrCode = new StringValue(this);
    /**
     * 当诊预约(0: 预约；1：当诊) APPT_CODE
     */
    private String apptCode;
    /**
     * 初复诊( 0:初诊；1：复诊) VISIT_CODE
     */
    private String visitCode;
    /**
     * 挂号方式 REGMETHOD_CODE
     */
    private String regmethodCode;
    /**
     * 身分别 CTZ1_CODE
     */
    private String ctz1Code;
    /**
     * 折扣2 CTZ2_CODE
     */
    private String ctz2Code;
    /**
     * 折扣3 CTZ3_CODE
     */
    private String ctz3Code;
    /**
     * 转诊院所代码 TRANHOSP_CODE
     */
    private String tranhospCode;
    /**
     * 检伤号 TRIAGE_NO
     */
    private String triageNo;
    /**
     * 合约项目(记账单位) CONTRACT_CODE
     */
    private String contractCode;
    /**
     * 报到注记 ARRIVE_FLG
     */
    private BooleanValue arriveFlg = new BooleanValue(this);
    /**
     * 退挂人员 REGCAN_USER
     */
    private StringValue regcanUser = new StringValue(this);
    /**
     * 退挂日期 REGCAN_DATE
     */
    private TimestampValue regcanDate = new TimestampValue(this);
    /**
     * 挂号院区 ADM_REGION
     */
    private String admRegion;
    /**
     * 预防保健时程(计划免疫) PREVENT_SCH_CODE
     */
    private String preventSchCode;
    /**
     * DRG码 DRG_CODE
     */
    private String drgCode;
    /**
     * 发热注记 HEAT_FLG
     */
    private boolean heatFlg;
    /**
     * 就诊进度 ADM_STATUS
     * 1：已挂号
     * 2：已看诊
     * 3：已缴费
     * 4：已取药
     * 5：检查中
     * 6: 已离院 <急诊提供>
     */
    private StringValue admStatus = new StringValue(this);
    /**
     * 报告状态 REPORT_STATUS
     * 1 全部未完成
     * 2 部分完成
     * 3 全部完成
     */
    private StringValue reportStatus = new StringValue(this);
    /**
     * 体重 kg 100.50 kg WEIGHT
     */
    private double weight;
    /**
     * 身高 170.30cm HEIGHT
     */
    private double height;
    /**
     * 检伤等级
     */
    private String erdLevel;//ERD_LEVEL
    /**
     * vip注记
     */
    private BooleanValue vipFlg = new BooleanValue(this);
    /**
     * 看诊注记
     */
    private StringValue seeDrFlg = new StringValue(this);
    /**
     * 服务等级
     */
    private StringValue serviceLevel = new StringValue(this);// SERVICE_LEVEL
    /**
     * 医疗卡交易号
     */
    private String tredeNo = new  String();
    /**
     * 医保卡====pangb 2011-12-5
     */
    private String nhiNo = new String();
    /**
     * 医保卡类型 ：1.普通 2 门特 
     */
    private String insPatType = new String();
    /**
     * 医保就诊号
     */
    private String confirmNo =new String();
    /**
     * 到院日期add by huangjw 20150603
     */
    private TimestampValue arriveDate = new TimestampValue(this);
    
    /**
     * 医疗卡执行数据 的SQL add by huangtt 20161017
     */
    private String [] ektSql;
    
    public String[] getEktSql() {
		return ektSql;
	}
	public void setEktSql(String[] ektSql) {
		this.ektSql = ektSql;
	}
	public String getConfirmNo() {
		return confirmNo;
	}
	public void setConfirmNo(String confirmNo) {
		this.confirmNo = confirmNo;
	}
	public String getInsPatType() {
		return insPatType;
	}
	public void setInsPatType(String insPatType) {
		this.insPatType = insPatType;
	}
	public String getNhiNo() {
		return nhiNo;
	}
	public void setNhiNo(String nhiNo) {
		this.nhiNo = nhiNo;
	}
	/**
     * 构造器
     */
    public Reg()
    {
        StringBuffer sb = new StringBuffer();
        //就诊号
        sb.append("caseNo:CASE_NO;");
        //门急别
        sb.append("admType:ADM_TYPE;");
        //操作院区
        sb.append("region:REGION_CODE;");
        //挂号日期
        sb.append("admDate:ADM_DATE;");
        //挂号操作日期
        sb.append("regDate:REG_DATE;");
        //时段代码
        sb.append("sessionCode:SESSION_CODE;");
        //诊区
        sb.append("clinicareaCode:CLINICAREA_CODE;");
        //诊间号
        sb.append("clinicroomNo:CLINICROOM_NO;");
        //号别
        sb.append("clinictypeCode:CLINICTYPE_CODE;");
        //就诊号
        sb.append("queNo:QUE_NO;");
        //预约时间
        sb.append("regAdmTime:REG_ADM_TIME;");
        //科别代码
        sb.append("deptCode:DEPT_CODE;");
        //看诊医师
        sb.append("drCode:DR_CODE;");
        //实际看诊科别
        sb.append("realdeptCode:REALDEPT_CODE;");
        //实际看诊医师
        sb.append("realdrCode:REALDR_CODE;");
        //当诊预约
        sb.append("apptCode:APPT_CODE;");
        //初复诊
        sb.append("visitCode:VISIT_CODE;");
        //挂号方式
        sb.append("regmethodCode:REGMETHOD_CODE;");
        //身分别
        sb.append("ctz1Code:CTZ1_CODE;");
        //折扣2
        sb.append("ctz2Code:CTZ2_CODE;");
        //折扣3
        sb.append("ctz3Code:CTZ3_CODE;");
        //转诊院所代码
        sb.append("tranhospCode:TRANHOSP_CODE;");
        //检伤号
        sb.append("triageNo:TRIAGE_NO;");
        //合约项目
        sb.append("contractCode:CONTRACT_CODE;");
        //报到注记
        sb.append("arriveFlg:ARRIVE_FLG;");
        //退挂人员
        sb.append("regcanUser:REGCAN_USER;");
        //退挂日期
        sb.append("regcanDate:REGCAN_DATE;");
        //挂号院区
        sb.append("admRegion:ADM_REGION;");
        //预防保健时程
        sb.append("preventSchCode:PREVENT_SCH_CODE;");
        //DRG码
        sb.append("drgCode:DRG_CODE;");
        //DRG码
        sb.append("heatFlg:HEAT_FLG;");
        //就诊进度
        sb.append("admStatus:ADM_STATUS;");
        //报告状态
        sb.append("reportStatus:REPORT_STATUS;");
        //体重
        sb.append("weight:WEIGHT;");
        //体重
        sb.append("height:HEIGHT;");
        //检伤等级
        sb.append("erdLevel:ERD_LEVEL;");
//        //VIP注记
        sb.append("vipFlg:VIP_FLG;");
        //看诊注记
        sb.append("seeDrFlg:SEE_DR_FLG;");
        //服务等级
        sb.append("serviceLevel:SERVICE_LEVEL;");
        //医保卡====pangb 2011-12-05
        sb.append("nhiNo:NHI_NO;");
        sb.append("insPatType:INS_PAT_TYPE;");
        sb.append("confirmNo:CONFIRM_NO;");
        sb.append("arriveDate:ARRIVE_DATE");//到院日期add by huangjw 20150603
        setMapString(sb.toString());

    }
    /**
     * 设置患者对象
     * @param pat Pat
     */
    public void setPat(Pat pat)
    {
        this.pat = pat;
    }
    /**
     * 得到患者对象
     * @return Pat
     */
    public Pat getPat()
    {
        return pat;
    }
    /**
     * 创建门诊收据
     */
    public void createReceipt()
    {
        regReceipt = new RegReceipt();
        //门急诊收据对象
        regReceipt.setAdmType(getAdmType());//3门急别(REG_RECEIPT)
        regReceipt.setRegion(getRegion());//4区域(REG_RECEIPT)

    }
    /**
     * 设置门诊收据
     * @param regReceipt RegReceipt
     */
    public void setRegReceipt(RegReceipt regReceipt)
    {
        this.regReceipt = regReceipt;
    }
    /**
     * 得到门诊收据
     * @return RegReceipt
     */
    public RegReceipt getRegReceipt()
    {
        return regReceipt;
    }
    /**
     * 设置交易号
     * @param tredeNo String
     */
    public void setTredeNo(String tredeNo)
    {
        this.tredeNo = tredeNo;
    }
    /**
     * 得到交易号
     * @return String
     */
    public String getTredeNo()
    {
        return tredeNo;
    }
    /**
     * 设置就诊号
     * @param caseNo String
     */
    public void setCaseNo(String caseNo)
    {
        this.caseNo = caseNo;
    }
    /**
     * 得到就诊号
     * @return String
     */
    public String caseNo()
    {
        return caseNo;
    }
    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * 设置操作院区
     * @param region String
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
    /**
     * 得到操作院区
     * @return String
     */
    public String getRegion()
    {
        return region;
    }
    /**
     * 设置挂号日期
     * @param admDate Timestamp
     */
    public void setAdmDate(Timestamp admDate)
    {
        this.admDate = admDate;
    }
    /**
     * 得到挂号日期
     * @return Timestamp
     */
    public Timestamp getAdmDate()
    {
        return admDate;
    }
    /**
     * 设置挂号操作日期
     * @param regDate Timestamp
     */
    public void setRegDate(Timestamp regDate)
    {
        this.regDate = regDate;
    }
    /**
     * 得到挂号操作日期
     * @return Timestamp
     */
    public Timestamp getRegDate()
    {
        return regDate;
    }
    /**
     * 设置时段代码
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode)
    {
        this.sessionCode = sessionCode;
    }
    /**
     * 得到时段代码
     * @return String
     */
    public String getSessionCode()
    {
        return sessionCode;
    }
    /**
     * 设置诊区
     * @param clinicareaCode String
     */
    public void setClinicareaCode(String clinicareaCode)
    {
        this.clinicareaCode = clinicareaCode;
    }
    /**
     * 得到诊区
     * @return String
     */
    public String getClinicareaCode()
    {
        return clinicareaCode;
    }
    /**
     * 设置诊间号
     * @param clinicroomNo String
     */
    public void setClinicroomNo(String clinicroomNo)
    {
        this.clinicroomNo = clinicroomNo;
    }
    /**
     * 得到诊间号
     * @return String
     */
    public String getClinicroomNo()
    {
        return clinicroomNo;
    }
    /**
     * 设置号别
     * @param clinictypeCode String
     */
    public void setClinictypeCode(String clinictypeCode)
    {
        this.clinictypeCode = clinictypeCode;
    }
    /**
     * 得到号别
     * @return String
     */
    public String getClinictypeCode()
    {
        return clinictypeCode;
    }
    /**
     * 设置就诊号
     * @param queNo int
     */
    public void setQueNo(int queNo)
    {
        this.queNo = queNo;
    }
    /**
     * 得到就诊号
     * @return int
     */
    public int getQueNo()
    {
        return queNo;
    }
    /**
     * 设置预约时间 (0800) 预约8点
     * @param regAdmTime String
     */
    public void setRegAdmTime(String regAdmTime)
    {
        this.regAdmTime = regAdmTime;
    }
    /**
     * 得到预约时间 (0800) 预约8点
     * @return String
     */
    public String getRegAdmTime()
    {
        return regAdmTime;
    }
    /**
     * 设置科别代码
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * 得到科别代码
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * 设置看诊医师
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * 得到看诊医师
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * 设置实际看诊科别
     * @param realdeptCode String
     */
    public void setRealdeptCode(String realdeptCode)
    {
        this.realdeptCode.setValue(realdeptCode);
    }
    /**
     * 得到实际看诊科别
     * @return String
     */
    public String getRealdeptCode()
    {
        return this.realdeptCode.getValue();
    }
    /**
     * 修改实际看诊科别
     * @param realdeptCode String
     */
    public void modifyRealdeptCode(String realdeptCode)
    {
        this.realdeptCode.modifyValue(realdeptCode);
    }
    /**
     * 设置实际看诊医师
     * @param realdrCode String
     */
    public void setRealdrCode(String realdrCode)
    {
        this.realdrCode.setValue(realdrCode);
    }
    /**
     * 得到实际看诊医师
     * @return String
     */
    public String getRealdrCode()
    {
        return this.realdrCode.getValue();
    }
    /**
     * 修改实际看诊医师
     * @param realdrCode String
     */
    public void modifyRealdrCode(String realdrCode)
    {
        this.realdrCode.modifyValue(realdrCode);
    }
    /**
     * 设置当诊预约(0: 预约；1：当诊)
     * @param apptCode String
     */
    public void setApptCode(String apptCode)
    {
        this.apptCode = apptCode;
    }
    /**
     * 得到当诊预约(0: 预约；1：当诊)
     * @return String
     */
    public String getApptCode()
    {
        return apptCode;
    }
    /**
     * 设置初复诊( 0:初诊；1：复诊)
     * @param visitCode String
     */
    public void setVisitCode(String visitCode)
    {
        this.visitCode = visitCode;
    }
    /**
     * 得到初复诊( 0:初诊；1：复诊)
     * @return String
     */
    public String getVisitCode()
    {
        return visitCode;
    }
    /**
     * 设置挂号方式
     * @param regmethodCode String
     */
    public void setRegmethodCode(String regmethodCode)
    {
        this.regmethodCode = regmethodCode;
    }
    /**
     * 得到挂号方式
     * @return String
     */
    public String getRegmethodCode()
    {
        return regmethodCode;
    }
    /**
     * 设置身分别
     * @param ctz1Code String
     */
    public void setCtz1Code(String ctz1Code)
    {
        this.ctz1Code = ctz1Code;
    }
    /**
     * 得到身分别
     * @return String
     */
    public String getCtz1Code()
    {
        return ctz1Code;
    }
    /**
     * 设置折扣2
     * @param ctz2Code String
     */
    public void setCtz2Code(String ctz2Code)
    {
        this.ctz2Code = ctz2Code;
    }
    /**
     * 得到折扣2
     * @return String
     */
    public String getCtz2Code()
    {
        return ctz2Code;
    }
    /**
     * 设置折扣3
     * @param ctz3Code String
     */
    public void setCtz3Code(String ctz3Code)
    {
        this.ctz3Code = ctz3Code;
    }
    /**
     * 得到折扣3
     * @return String
     */
    public String getCtz3Code()
    {
        return ctz3Code;
    }
    /**
     * 设置转诊院所代码
     * @param tranhospCode String
     */
    public void setTranhospCode(String tranhospCode)
    {
        this.tranhospCode = tranhospCode;
    }
    /**
     * 得到转诊院所代码
     * @return String
     */
    public String getTranhospCode()
    {
        return tranhospCode;
    }
    /**
     * 设置检伤号
     * @param triageNo String
     */
    public void setTriageNo(String triageNo)
    {
        this.triageNo = triageNo;
    }
    /**
     * 得到检伤号
     * @return String
     */
    public String getTriageNo()
    {
        return triageNo;
    }
    /**
     * 设置合约项目(记账单位)
     * @param contractCode String
     */
    public void setContractCode(String contractCode)
    {
        this.contractCode = contractCode;
    }
    /**
     * 得到合约项目(记账单位)
     * @return String
     */
    public String getContractCode()
    {
        return contractCode;
    }
    /**
     * 设置报到注记
     * @param arriveFlg boolean
     */
    public void setArriveFlg(boolean arriveFlg)
    {
        this.arriveFlg.setValue(arriveFlg);
    }
    /**
     * 得到报到注记
     * @return boolean
     */
    public boolean isArriveFlg()
    {
        return this.arriveFlg.getValue();
    }
    /**
     * 修改报到注记
     * @param arriveFlg boolean
     */
    public void modifyArriveFlg(boolean arriveFlg)
    {
        this.arriveFlg.modifyValue(arriveFlg);
    }
    /**
     * 设置退挂人员
     * @param regcanUser String
     */
    public void setRegcanUser(String regcanUser)
    {
        this.regcanUser.setValue(regcanUser);
    }
    /**
     * 得到退挂人员
     * @return String
     */
    public String getRegcanUser()
    {
        return regcanUser.getValue();
    }
    /**
     * 修改退挂人员
     * @param regcanUser String
     */
    public void modifyRegcanUser(String regcanUser)
    {
        this.regcanUser.modifyValue(regcanUser);
    }
    /**
     * 设置退挂日期
     * @param regcanDate Timestamp
     */
    public void setRegcanDate(Timestamp regcanDate)
    {
        this.regcanDate.setValue(regcanDate);
    }
    /**
     * 得到退挂日期
     * @return Timestamp
     */
    public Timestamp getRegcanDate()
    {
        return regcanDate.getValue();
    }
    /**
     * 修改退挂日期
     * @param regcanDate Timestamp
     */
    public void modifyRegcanDate(Timestamp regcanDate)
    {
        this.regcanDate.modifyValue(regcanDate);
    }
    /**
     * 设置挂号院区
     * @param admRegion String
     */
    public void setAdmRegion(String admRegion)
    {
        this.admRegion = admRegion;
    }
    /**
     * 得到挂号院区
     * @return String
     */
    public String getAdmRegion()
    {
        return admRegion;
    }
    /**
     * 设置预防保健时程(计划免疫)
     * @param preventSchCode String
     */
    public void setPreventSchCode(String preventSchCode)
    {
        this.preventSchCode = preventSchCode;
    }
    /**
     * 得到预防保健时程(计划免疫)
     * @return String
     */
    public String getPreventSchCode()
    {
        return preventSchCode;
    }
    /**
     * 设置DRG码
     * @param drgCode String
     */
    public void setDrgCode(String drgCode)
    {
        this.drgCode = drgCode;
    }
    /**
     * 得到DRG码
     * @return String
     */
    public String getDrgCode()
    {
        return drgCode;
    }
    /**
     * 设置发热注记
     * @param heatFlg boolean
     */
    public void setHeatFlg(boolean heatFlg)
    {
        this.heatFlg = heatFlg;
    }
    /**
     * 得到发热注记
     * @return boolean
     */
    public boolean isHeatFlg()
    {
        return heatFlg;
    }
    /**
     * 设置就诊进度
     * 1：已挂号
     * 2：已看诊
     * 3：已缴费
     * 4：已取药
     * 5：检查中
     * 6: 已离院 <急诊提供>
     * @param admStatus String
     */
    public void setAdmStatus(String admStatus)
    {
        this.admStatus.setValue(admStatus);
    }
    /**
     * 得到就诊进度
     * 1：已挂号
     * 2：已看诊
     * 3：已缴费
     * 4：已取药
     * 5：检查中
     * 6: 已离院 <急诊提供>
     * @return String
     */
    public String getAdmStatus()
    {
        return admStatus.getValue();
    }
    /**
     * 修改就诊进度
     * 1：已挂号
     * 2：已看诊
     * 3：已缴费
     * 4：已取药
     * 5：检查中
     * 6: 已离院 <急诊提供>
     * @param admStatus String
     */
    public void modifyAdmStatus(String admStatus)
    {
        this.admStatus.modifyValue(admStatus);
    }
    /**
     * 设置报告状态
     * 1 全部未完成
     * 2 部分完成
     * 3 全部完成
     * @param reportStatus String
     */
    public void setReportStatus(String reportStatus)
    {
        this.reportStatus.setValue(reportStatus);
    }
    /**
     * 得到报告状态
     * 1 全部未完成
     * 2 部分完成
     * 3 全部完成
     * @return String
     */
    public String getReportStatus()
    {
        return reportStatus.getValue();
    }
    /**
     * 修改报告状态
     * 1 全部未完成
     * 2 部分完成
     * 3 全部完成
     * @param reportStatus String
     */
    public void modifyreportStatus(String reportStatus)
    {
        this.reportStatus.modifyValue(reportStatus);
    }
    /**
     * 设置体重 kg 100.50 kg
     * @param weight double
     */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    /**
     * 得到体重 kg 100.50 kg
     * @return double
     */
    public double getWeight()
    {
        return weight;
    }
    /**
     * 设置身高 170.30cm
     * @param height double
     */
    public void setHeight(double height)
    {
        this.height = height;
    }
    /**
     * 得到身高 170.30cm
     * @return double
     */
    public double getHeight()
    {
        return height;
    }
    /**
     * 设置检伤等级
     * @param erdLevel String
     */
    public void setErdLevel(String erdLevel){
    this.erdLevel = erdLevel;
    }
    /**
     * 得到检伤等级
     * @return String
     */
    public String getErdLevel(){
    return erdLevel;
    }
    /**
     * 设置VIP注记
     * @param vipFlg boolean
     */
    public void setVipFlg(boolean vipFlg)
    {
        this.vipFlg.setValue(vipFlg);
    }
    /**
     * 得到VIP注记
     * @return boolean
     */
    public boolean getVipFlg()
    {
        return this.arriveFlg.getValue();
    }
    /**
     * 设置看诊注记
     * @param seeDrFlg String
     */
    public void setSeeDrFlg(String seeDrFlg){
    	this.seeDrFlg.setValue(seeDrFlg);
    }
    /**
     * 设置服务等级
     * @param serviceLevel String
     */
    public void setServiceLevel(String serviceLevel){
        this.serviceLevel.setValue(serviceLevel);
    }
    /**
     * 得到服务等级
     * @return String
     */
    public String getServiceLevel(){
        return this.serviceLevel.getValue();
    }
    /**
     * 得到看诊注记
     * @return seeDrFlg String
     */
    public String getSeeDrFlg(){
    	return this.seeDrFlg.getValue();
    }
    /**
     * 修改看诊注记
     * @param seeDrFlg String
     */
    public void modifySeeDrFlg(String seeDrFlg){
    	this.seeDrFlg.modifyValue(seeDrFlg);
    }
    /**
     * 修改服务等级
     * @param serviceLevel String
     */
    public void modifyServiceLevel(String serviceLevel){
        this.serviceLevel.modifyValue(serviceLevel);
    }
    /**
     * 设置到院时间
     */
    public void setArriveDate(Timestamp arriveDate){//add by huangjw 20150603
    	this.arriveDate.setValue(arriveDate);
    }
    /**
     * 得到到院时间
     * @return 
     */
    public Timestamp getArriveDate(){//add by huangjw 20150603
    	return this.arriveDate.getValue();
    }
    /**
     * 修改到院时间
     */
    public void modifyArriveDate(Timestamp arriveDate){//add by huangjw 20150603
    	this.arriveDate.modifyValue(arriveDate);
    }
    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm()
    {
        TParm parm = super.getParm();
        if(getPat() != null){
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("NHI_NO", null==pat.getNhiNo()?"":pat.getNhiNo());//============pangben modify 20110809 医保卡号
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        return parm;
    }
    /**
     * 根据就诊号查询挂号对象
     * @param pat Pat
     * @param caseNo String
     * @return Reg
     */
    public static Reg onQueryByCaseNo(Pat pat,String caseNo)
    {
        if(caseNo == null || caseNo.length() == 0)
            return null;
        TParm parm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
        if(parm.getErrCode() < 0)
            return null;
        if(parm.getCount() <= 0)
            return null;
        Reg reg = new Reg();
        reg.setPat(pat);
       if(! reg.initParm(parm,0)){
           return null;
       }
        return reg;
    }

    /**
     * 新增动作
     * @return boolean true 成功 false 失败
     */
    public boolean onNew()
    {
        if(getPat() == null)
            return false;
        return PatAdmTool.getInstance().newReg(this);
    }
    /**
     * 保存动作
     * @return boolean
     */
    public boolean onSave()
    {
        if(!isModified())
            return true;
        if(!PatAdmTool.getInstance().onSave(this))
            return false;
        reset();
        return true;
    }
    /**
     * 新增动作(医疗卡付费使用)
     * @return boolean true 成功 false 失败
     */
    public boolean onNewForEKT()
    {
        if(getPat() == null)
            return false;
        return PatAdmTool.getInstance().newRegForEKT(this);
    }
    /**
     * 设置压力测试开关
     * @param tload boolean
     */
    public void setTLoad(boolean tload)
    {
        this.tload = tload;
    }
    /**
     * 得到压力测试开关
     * @return boolean
     */
    public boolean isTLoad()
    {
        return tload;
    }

    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        Pat pat = Pat.onQueryByMrNo("000000000041");
//        Reg reg = Reg.onQueryByCaseNo(pat,"080922000001");
        Reg reg = Reg.onQueryByCaseNo(pat, "090101000005");

        reg.setPat(pat);
        reg.modifyRealdeptCode("ddddd");
    }
}
