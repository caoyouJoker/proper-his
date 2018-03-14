package com.javahis.device;

import com.javahis.device.provobj.CardInfo;
import com.javahis.device.provobj.FeeDetail;
import com.javahis.device.provobj.OutpatReg;
import com.javahis.device.provobj.RecCode;

/**
 * 江苏省医疗保险接口
 * 详见江苏省医疗保险信息系统医院(药店)接口规范文档
 * @author lixiang
 *
 */
public class JSProvInwDriver {
	static {
		System.loadLibrary("JSProvInwDriver"); // 载入dll
	}

	public JSProvInwDriver() {
	}

	/**
	 * 初始化加载dll
	 * 
	 * @param type
	 *            0省医保|1干部保健;
	 * @return
	 */
	public native static int init(int type);

	/**
	 * 注销dll
	 * 
	 * @param type
	 *            0省医保|1干部保健;
	 * @return
	 */
	public native static int close(int type);

	/**
	 * 取就诊号
	 * 
	 * 调用FGetRecCode函数从HT.HIS取得就诊号。医院HIS系统通过该流水号建立与医保收费系统的联系。
	 * 医院方需要保存这个号码，方便医院HIS系统记录与医保收费记录对应。 在下述与医保收费有关的接口函数中，都将传入就诊号psRecCode。
	 * 函数调用成功后在C:根目录下生成一个文本文件，文件名为FGetRC0.txt或FGetRC1.txt,分别对应于门诊和住院。
	 * 
	 * @param piRecType
	 *            0门诊，1住院
	 * @param pChar
	 *            就诊号(用于门诊收费时为12位；用于住院结算时为6位)
	 * @return
	 */
	public native static int FGetRecCode(int piRecType, RecCode psRecCode);
	
	/**
	 * 读卡取个人信息  
	 * @param piRecType  类型[N1]（传入编码） 0门诊/1住院
	 * @param psRecCode   就诊号
	 * @param psVoucherID   个人证号[C16]
	 * @param CardInfo   返回的卡信息;
	 * @return
	 */
	public native static int FGetCardInfo(int piRecType, String psRecCode,
			String psVoucherID,CardInfo cardInfo);

	/**
	 * 门诊挂号
	 * @param psRecCode  //门诊号[C12]<YYYYMMDD****>	
	 * @param psRegName   //挂号类别（专家号，普通号...）
	 * @param psDepartName   //科室名称
	 * @param psRegFeeCode   //挂号费项目自编码
	 * @param psRegFeeName   //挂号费项目名称(C50)
	 * @param RegFee			//挂号费金额
	 * @param psDiagFeeCode     //诊疗费项目自编码
	 * @param psDiagFeeName   //诊疗费项目名称(C50)
	 * @param DiagFee         //诊疗费金额
	 * @param psFeeType		   //费别
	 * @param psOpCode			//操作员
	 * @param psRegDate			//日期
	 * @param pRegMode			//交易模式(T(输卡号)/F（刷卡）
	 * @param outpatReg
	 * @return
	 */
	public native static int FOutpatReg(String psRecCode, String psRegName,
			String psDepartName, String psRegFeeCode, String psRegFeeName,
			float RegFee, String psDiagFeeCode, String psDiagFeeName,
			float DiagFee, String psFeeType, String psOpCode, String psRegDate,
			String pRegMode,OutpatReg outpatReg);

	/**
	 * 取消门诊挂号
	 * @param psRecCode  //门诊号[C12]
	 * @param psOpCode	 //操作员代码
	 * @return
	 */
	public native static int FCancleOutpatReg(String psRecCode, String psOpCode);

	/**
	 * 取消门诊试算
	 * @param psRecCod  //门诊处方号
	 * @param psOpCode   //操作员工号
	 * @return
	 */
	public native static int FCancelTryOutpatBalance(String psRecCod,
			String psOpCode);

	/**
	 * 费用明细录入
	 * @param piRecType  //I类型[N1]: 0门诊/1住院
	 * @param psRecCode   //I就诊号（住院、门诊不同）
	 * @param psItmFlag   //I项目类型[C1] '0' 非药品/'1' 药品
	 * @param psItmCode		 //I项目编码(HIS编码 [C20])
	 * @param psAliasCode	 //I明细编码[C20]
	 * @param psItmName		//I项目名称[C50]
	 * @param psItmUnit		//I单位[C10]
	 * @param psItmDesc		//I规格、剂型等[C50]
	 * @param psFeeCode		//I费别码[C3] （编码）
	 * @param psOTCCode		//I处方药标志[C3] （编码）
	 * @param pcQuantity		//I数量[N(8, 2)]
	 * @param pcPharPrice		 //I应售单价[N(10, 4)]	
	 * @param pcFactPrice		 //I实售单价[N(10, 4)]
	 * @param pcDosage			 //I每次用量[N(9, 3)]
	 * @param psFrequency
	 * @param psUsage
	 * @param pcDays
	 * @param psOpCode
	 * @param psDepCode
	 * @param psDocCode
	 * @param psRecDate
	 * @param feeDetail
	 * @return
	 */
	public native static String FWriteFeeDetail(int piRecType,
			String psRecCode, String psItmFlag, String psItmCode,
			String psAliasCode, String psItmName, String psItmUnit,
			String psItmDesc, String psFeeCode, String psOTCCode,
			float pcQuantity, float pcPharPrice, float pcFactPrice,
			float pcDosage, String psFrequency, String psUsage, float pcDays,
			String psOpCode, String psDepCode, String psDocCode,
			String psRecDate,FeeDetail feeDetail);

	/**
	 * 用于住院费用明细定时传输（无人干预）
	 * 
	 * @param piShowMess
	 *            是否显示消息(0显示/1不显示)
	 * @param piRecType
	 *            处方类型[N1]: 0门诊/1住院
	 * @param psRecCode
	 *            就诊号: 门诊处方号[C12]<YYYYMMDD****>/住院号[C6]<******>
	 * @param psItmFlag
	 *            0' 项目/'1' 药品
	 * @param psItmCode
	 *            项目编码(HIS编码 C20)
	 * @param psAliasCode
	 *            明细编码
	 * @param psItmName
	 *            项目名称(C50)
	 * @param psItmUnit
	 *            单位[C10]
	 * @param psItmDesc
	 *            规格、剂型等[C50]
	 * @param psFeeCode
	 *            费别码[C3](AKA063 : 01西药/02中成药/03中草药/...)
	 * @param psOTCCode
	 *            处方药标志[C3](AKA064 0非处方药/1处方药)
	 * @param pcQuantity
	 *            数量[N(8, 2)]
	 * @param pcPharPrice
	 *            应售单价[N(10, 4)]
	 * @param pcFactPrice
	 *            实售单价[N(10, 4)]
	 * @param pcDosage
	 *            每次用量[N(9, 3)]
	 * @param psFrequency
	 *            使用频次[C20]
	 * @param psUsage
	 *            用法[C50]
	 * @param pcDays
	 *            执行天数[N(5, 2)]
	 * @param psOpCode
	 *            收费员工号[C8]
	 * @param psDepCode
	 *            科室编码
	 * @param psDocCode
	 *            处方医生工号[C8]
	 * @param psRecDate
	 *            处方日期(YYYY-MM-DD HH:NN:SS)<住院用>
	 * @param psMess
	 *            提示信息
	 * @return Out pcRate个人支付比例[N(5,4)]|Out pcSelfFee个人自付金额[N(10, 4)] = (数量 * 单价
	 *         - 超标准费用) * 个人支付比例|Out pcDeduct超标准费用[N(10, 4)]
	 */
	public native static String FWriteFeeDetail2(int piShowMess, int piRecType,
			String psRecCode, String psItmFlag, String psItmCode,
			String psAliasCode, String psItmName, String psItmUnit,
			String psItmDesc, String psFeeCode, String psOTCCode,
			float pcQuantity, float pcPharPrice, float pcFactPrice,
			float pcDosage, String psFrequency, String psUsage, float pcDays,
			String psOpCode, String psDepCode, String psDocCode,
			String psRecDate, String psMess);

	/**
	 * 门诊试算
	 * 
	 * 说明： 1、 门诊费用录入完毕后的试结算；试算时上传费用记录； 2、
	 * 可以单独调用此函数进行门诊费用的试算，此结果不存入本地医保数据库HT.INS； 3、 进行门诊结算前必须调用门诊试结算； 4、
	 * 函数返回费用构成情况，即帐户支付、统筹支付、现金支付等信息 5、 函数调用成功后在C:根目录下生成一个文本文件，文件名为FTryBal0.txt
	 * 
	 * @param psRecCode
	 *            就诊号[C12]
	 * @param psOpCode
	 *            操作员工[C20]
	 * @param psUseAcc
	 *            是否使用帐户[C2](是/否)
	 * @param psDepCode
	 *            科室编码[C20]
	 * @param psDocCode
	 *            医生工号[C20]
	 * @param psMedMode
	 *            医疗方式[C3]
	 * @param psRecClass
	 *            医疗类别[C3]
	 * @param psICDMode
	 *            [C1](‘A’)
	 * @param psICD
	 *            报销代码
	 * @param pcOther1
	 *            保留1
	 * @param pcOther2
	 *            保留2
	 * @param psMemo
	 *            备注
	 * 
	 *            参数： 1、 参数psMedMode医疗方式传入编码：编码如下1普通门诊；2普通住院；3特殊门诊；4紧急抢救；5急诊； 2、
	 *            参数psRecClass医疗类别传入编码
	 *            ：编码如下11普通门诊；12特殊门诊；13转外诊治门诊；14定点药店购药；21普通住院
	 *            ；22特殊病种住院；23转外诊治住院；24家庭病床； 3、
	 *            参数psICD报销代码：OA01门诊报销/OB01门诊异地报销/OC01门诊转院报销/
	 *            IA01住院报销/IB01住院异地报销/IC01住院转院报销 4、 个人负担金额 = pcMedAccPay +
	 *            pcBankAccPay + pcCashPay 5、 数据平衡关系： pcSumFee = pcPubPay +
	 *            pcHelpPay + pcSupplyPay + pcOtrPay + pcMedAccPay +
	 *            pcBankAccPay + pcCashPay
	 * 
	 * 
	 * 
	 * @return psBillCode （’UnKnown’）|Out pcSumFee 本次总费用[N10, 2]|Out pcGenFee
	 *         3个范围内费用[N10, 2]|Out pcFirstPay自付费用[N10, 2]|Out pcSelfFee
	 *         O自费费用[N10, 2]|Out pcPayLevel O起付标准[N10, 2]|Out pcPubPay统筹支付[N10,
	 *         2]|Out pcPubSelf |Out pcHelpPay 大病救助基金支付[N10, 2]|Out pcHelpSelf
	 *         |Out pcSupplyPay 公务员/企业补充支付[N10, 2]|Out pcSupplySelf |Out
	 *         pcOtrPay 其它基金支付[N10, 2]|Out pcMedAccPay 个人医疗帐户支付[N10, 2]|Out
	 *         pcBankAccPay 个人储蓄帐户支付[N10, 2]|Out pcCashPay 现金支付[N10, 2]
	 */
	public native static String FTryOutpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String psDepCode,
			String psDocCode, String psMedMode, String psRecClass,
			String psICDMode, String psICD, float pcOther1, float pcOther2,
			String psMemo);

	/**
	 * 门诊结算 说明：
	 * 
	 * 1、结算门诊处方费用。使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法门诊结算。
	 * 2、门诊费用录入完毕后结算，在调用此函数前必须调用门诊试结算函数，函数返回费用构成情况，结果记录入库
	 * 3、函数调用成功后在C:根目录下生成一个文本文件，文件名为FBal0.txt
	 * 
	 * @param psRecCode
	 *            就诊号[C12]
	 * @param psOpCode
	 *            操作员工[C20]
	 * @param psUseAcc
	 *            是否使用帐户[C2](是/否)
	 * @param psDepCode
	 *            科室编码[C20]
	 * @param psDocCode
	 *            医生工号[C20]
	 * @param psMedMode
	 *            医疗方式
	 * @param psRecClass
	 *            医疗类别[C3]
	 * @param psICDMode
	 *            [C1](‘A’)
	 * @param psICD
	 *            报销代码
	 * @param pcOther1
	 *            保留1
	 * @param pcOther2
	 *            保留2
	 * @param psMemo
	 *            I备注
	 * @return psBillCode O结账流水号[C7]|Out pcSumFee 总费用[N10, 2]|Out pcGenFee
	 *         3个范围内费用[N10, 2]|Out pcFirstPay 自付费用[N10, 2]| Out pcSelfFee
	 *         自费费用[N10, 2]|Out pcPayLevel 起付标准[N10, 2]|Out pcPubPay 统筹支付[N10,
	 *         2]|Out pcPubSelf统筹个人自付[N10, 2]|Out pcHelpPay 大病救助基金支付[N10, 2]|Out
	 *         pcHelpSelf大病救助基金个人自付[N10, 2]|Out pcSupplyPay 公务员补充支付/企业补充支付[N10,
	 *         2]|Out pcSupplySelf 公务员补充/企业补充个人自付[N10, 2]|Out pcOtrPay
	 *         其它基金支付[N10, 2]|Out pcMedAccPay个人医疗帐户支付[N10, 2]|Out pcBankAccPay
	 *         特定项目[N10, 2]|Out pcCashPay 现金支付[N10, 2]
	 */
	public native static String FOutpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String psDepCode,
			String psDocCode, String psMedMode, String psRecClass,
			String psICDMode, String psICD, float pcOther1, float pcOther2,
			String psMemo);

	/**
	 * 门诊退费
	 * 
	 * 说明： 1、 取消门诊收费，需要传入就诊号。使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法门诊退费。 2、
	 * 只能全退，不能部分退费；对多笔费用
	 * ，退费时一般是从最后发生的一笔开始退费；多久发生的费用不允许退费，与政策相关。但是不可退的各个原因，函数会自动提示。 3、
	 * 建议退费采用红冲方式：即生成一笔负记录。 4、函数调用成功后在C:根目录下生成一个文本文件，文件名为FCBal0.txt
	 * 
	 * 
	 * @param psRecCode
	 *            就诊号[C12]
	 * @param psBillCode
	 *            结账流水号[C7]
	 * @param psOpCode
	 *            操作员工号[C20]
	 * @return Out pcSumFee总费用[N10, 2]| Out pcGenFee 3个范围内费用[N10, 2]|Out
	 *         pcFirstPay 自付费用[N10, 2]|Out pcSelfFee 自费费用[N10, 2]|Out pcPayLevel
	 *         起付标准[N10, 2]|Out pcPubPay 统筹支付[N10, 2]|Out pcPubSelf |Out
	 *         pcHelpPay 大病救助基金支付[N10, 2]|Out pcHelpSelf |Out pcSupplyPay
	 *         公务员/企业补充支付[N10, 2]|Out pcSupplySelf |Out pcOtrPay 其它基金支付[N10,
	 *         2]|Out pcMedAccPay个人医疗帐户支付[N10, 2]|Out pcBankAccPay 个人储蓄帐户支付[N10,
	 *         2]|Out pcCashPay 现金支付[N10, 2]
	 * 
	 */
	public native static String FCancelOutpatBalance(String psRecCode,
			String psBillCode, String psOpCode);

	/**
	 * 住院登记
	 * 
	 * 说明： 1、 参保病人入院时的入院登记，将登记信息记录入库；使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法住院登记。 2、
	 * 函数调用成功后在C:根目录下生成一个文本文件，文件名为FReg1.txt
	 * 
	 * @param psRecCode
	 *            住院号（就诊号）[C6]
	 * @param psMedMode
	 *            医疗方式[C3]（见门诊试算）
	 * @param psMedClass
	 *            医疗类别[C3]（见门诊试算）
	 * @param psRegOpCode
	 *            操作员编码[C20]
	 * @param psBegDate
	 *            入院日期[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psICDMode
	 *            ICD编码规则[C1](‘A’)
	 * @param psICD
	 *            入院诊断[C20](ICD10编码)
	 * @param psDepCode
	 *            科室代码[C20]
	 * @param psSecCode
	 *            病区代码[C20]
	 * @param psRegDoc
	 *            入院医生代码[C20]
	 * @return 本年内累计住院次数[N9, 4]
	 */
	public native static float FInpatReg(String psRecCode, String psMedMode,
			String psMedClass, String psRegOpCode, String psBegDate,
			String psICDMode, String psICD, String psDepCode, String psSecCode,
			String psRegDoc);

	/**
	 * 取消住院登记
	 * 
	 * 说明： 1、 参保病人登记住院后不打算住院，做取消登记处理；使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则取消住院登记。 2、
	 * 如果发生了费用信息，取消登记后费用信息将自动作废； 3、 函数调用成功后在C:根目录下生成一个文本文件，文件名为FCReg1.txt
	 * 
	 * 
	 * @param psRecCode
	 *            住院号（就诊号）[C6]
	 * @param psOpCode
	 *            操作员编码[C20]
	 * @return 暂无意义
	 */
	public native static int FCancelInpatReg(String psRecCode, String psOpCode);

	/**
	 * 修改住院登记信息
	 * 
	 * 说明： 1、参保病人住院中登记信息变更，如转科等，调用该函数修改登记信息；使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法修改信息。
	 * 2、函数调用成功后在C:根目录下生成一个文本文件，文件名为FUReg1.txt
	 * 
	 * @param psRecCode
	 *            住院号[C6]
	 * @param psMedMode
	 *            医疗方式[C3]
	 * @param psMedClass
	 *            医疗类别[C3]
	 * @param psRegOpCode
	 *            操作员编码[C20]
	 * @param psBegDate
	 *            就诊开始日期[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psICDMode
	 *            ICD编码规则[C1](‘A’)
	 * @param psICD
	 *            入院诊断[C20](ICD10编码)
	 * @param psDepCode
	 *            科室代码[C20]
	 * @param psSecCode
	 *            病区代码[C20]
	 * @param psRegDoc
	 *            入院医生代码[C20]
	 * @return 暂无意义
	 */
	public native static int FChgInpatReg(String psRecCode, String psMedMode,
			String psMedClass, String psRegOpCode, String psBegDate,
			String psICDMode, String psICD, String psDepCode, String psSecCode,
			String psRegDoc);

	/**
	 * 出院
	 * 
	 * 说明： 1、 参保病人出院的操作，类似入院登记，并不是结账处理；使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法出院。 2、
	 * 出院结算前必须调该函数，调用次序是：出院，费用试算，出院结算； 3、 函数调用成功后在C:根目录下生成一个文本文件，文件名为FLeave1.txt
	 * 
	 * @param psRecCode
	 *            住院号[C6]
	 * @param psOutOpCode
	 *            操作员代码[C20]
	 * @param psEndDate
	 *            出院日期[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psOutCause
	 *            出院原因[C3]
	 * @param psICDMode
	 *            ICD编码规则[C1](‘A’)
	 * @param psICD
	 *            出院诊断[C20](ICD10编码)
	 * @param psOutDoc
	 *            出院医生代码[C20]
	 * 
	 *            参数： 1、 参数psOutCause出院原因传入编码：编码如下：1治愈；2好转；3未愈；4死亡；5转院；6转外；9其他
	 * 
	 * 
	 * @return 暂无意义
	 */
	public native static int FInpatLeave(String psRecCode, String psOutOpCode,
			String psEndDate, String psOutCause, String psICDMode,
			String psICD, String psOutDoc);

	/**
	 * 住院费用试算
	 * 
	 * 说明： 1、 医院对在院病人的费用进行预结算，方便收取住院押金，此情况下可以直接调用住院费用试算：返回试算结果供参考，信息不入医保本地库；
	 * 使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法住院费用试算。 2、
	 * 出院时，在出院结算前必须调用费用试算；返回试算结果供参考，信息不入医保本地库； 3、
	 * 调用费用试算时会自动上传费用明细，费用明细较多时，需要等待时间。 4、
	 * 函数调用成功后在C:根目录下生成一个文本文件，文件名为FTryBal1.txt
	 * 
	 * @param psRecCode
	 *            住院号[C6]
	 * @param psOpCode
	 *            操作员工号[C20]
	 * @param psUseAcc
	 *            是否使用帐户[C2](是/否)
	 * @param piLiquiMode
	 *            结算方式[C1]
	 * @param psRefundID
	 *            报销代码[C4]
	 * @param pcOther1
	 *            保留1
	 * @param pcOther2
	 *            保留2
	 * @param psMemo
	 *            备注
	 * 
	 *            参数： 1、 参数piLiquiMode结算方式传入编码：编码如下0正常结算；1中途结算 2、
	 *            参数psRefundID报销代码同“门诊试算” psICD报销代码
	 * 
	 * 
	 * @return psBillCode （’UnKnown’）|Out pcSumFee 总费用[N10, 2]|Out pcGenFee
	 *         3个范围内费用[N10, 2]|Out pcFirstPay自付费用[N10, 2]|Out pcSelfFee自费费用[N10,
	 *         2]|Out pcPayLevel起付标准[N10, 2]|Out pcPubPay 统筹支付[N10, 2]|Out
	 *         pcPubSelf |Out pcHelpPay 大病救助基金支付[N10, 2]|Out pcHelpSelf |Out
	 *         pcSupplyPay公务员/企业补充支付[N10, 2]|Out pcSupplySelf |Out
	 *         pcOtrPay其它基金支付[N10, 2]|Out pcMedAccPay个人医疗帐户支付[N10, 2]|Out
	 *         pcBankAccPay 个人储蓄帐户支付[N10, 2]|Out pcCashPay现金支付[N10, 2]
	 */
	public native static String FTryInpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String piLiquiMode,
			String psRefundID, float pcOther1, float pcOther2, String psMemo);

	/**
	 * 住院费用结算
	 * 
	 * 说明： 1、 出院结帐：信息记录入库；使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法住院费用结算。 2、
	 * 函数调用成功后在C:根目录下生成一个文本文件，文件名为FBal1.txt
	 * 
	 * @param psRecCode
	 *            住院号[C6]
	 * @param psOpCode
	 *            操作员工号[C20]
	 * @param psUseAcc
	 *            是否使用帐户[C2](是/否)
	 * @param piLiquiMode
	 *            结算方式[C1]（同试算）
	 * @param psRefundID
	 *            报销代码[C4]
	 * @param pcOther1
	 *            保留1
	 * @param pcOther2
	 *            保留2
	 * @param psMemo
	 *            备注
	 * @return psBillCode 结账流水号[C7]| Out pcSumFee 总费用[N10, 2]|Out pcGenFee
	 *         3个范围内费用[N10, 2]|Out pcFirstPay 自付费用[N10, 2]|Out pcSelfFee
	 *         自费费用[N10, 2]|Out pcPayLevel起付标准[N10, 2]|Out pcPubPay 统筹支付[N10,
	 *         2]|Out pcPubSelf|Out pcHelpPay 大病救助基金支付[N10, 2]|Out
	 *         pcHelpSelf|Out pcSupplyPay 公务员/企业补充支付[N10, 2]|Out pcSupplySelf
	 *         |Out pcOtrPay 其它基金支付[N10, 2]|Out pcMedAccPay 个人医疗帐户支付[N10, 2]|Out
	 *         pcBankAccPay 个人储蓄帐户支付[N10, 2]|Out pcCashPay 现金支付[N10, 2]
	 */
	public native static String FInpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String piLiquiMode,
			String psRefundID, float pcOther1, float pcOther2, String psMemo);

	/**
	 * 取消住院结帐
	 * 
	 * 说明： 1、 作废住院结帐信息，参保病人返回在院状态。使用IC卡后交易过程中需要插入该就诊人员的IC卡，否则无法取消住院结账。 2、
	 * 函数调用成功后在C:根目录下生成一个文本文件，文件名为FCBal1.txt
	 * 
	 * @param psRecCode
	 *            住院号[C6]
	 * @param psBillCode
	 *            结账流水号[C7]
	 * @param psOpCode
	 *            操作员工号[C20]
	 * @return Out pcSumFee 总费用[N10, 2]|Out pcGenFee 3个范围内费用[N10, 2]|Out
	 *         pcFirstPay 自付费用[N10, 2]|Out pcSelfFee 自费费用[N10, 2]| Out
	 *         pcPayLevel 起付标准[N10, 2]|Out pcPubPay 统筹支付[N10, 2]|Out pcPubSelf
	 *         |Out pcHelpPay 大病救助基金支付[N10, 2]|Out pcHelpSelf|Out pcSupplyPay
	 *         公务员/企业补充支付[N10, 2]|Out pcSupplySelf |Out pcOtrPay 其它基金支付[N10,
	 *         2]|Out pcMedAccPay 个人医疗帐户支付[N10, 2]|Out pcBankAccPay
	 *         个人储蓄帐户支付[N10, 2]|Out pcCashPay 现金支付[N10, 2]
	 */
	public native static String FCancelInpatBalance(String psRecCode,
			String psBillCode, String psOpCode);

	/**
	 * 上传
	 * 
	 * 说明： 1、 调用此函数将医保本地库HT.HIS中尚未上传的数据传输至医保中心； 2、
	 * 上传前，医院HIS数据必须已经调用费用录入函数导入到了医保本地库；
	 * 
	 * @param piType
	 *            2
	 * @param psRecCode
	 *            住院号（若为*表示上传所有）
	 * @return 暂无意义;
	 */
	public native static int FUpLoad(int piType, String psRecCode);

	/**
	 * 通用导入函数
	 * 
	 * 说明： 1、 该函数为通用的数据导入函数，用于从医院HIS导入数据到医保本地的数据库； 2、 导入数据表为1操作员、2医生、3药品字典、4诊疗项目
	 * 
	 * @param piType
	 *            1科室/2操作员/ 3医生/4药品字典/5诊
	 * @param psInfo1
	 *            导入信息[C200]
	 * @param psInfo2
	 *            导入信息[C200]
	 * @param psInfo3
	 *            导入信息[C200]
	 * @param psInfo4
	 *            导入信息[C200]
	 * @param psRemark
	 *            备注[C1024]
	 * @param psOpStaus
	 *            操作状态 I
	 * 
	 *            参数说明： A、 科室 piType ： 1 psInfo1 ： 科室编码 psInfo2 ： 科室名称 B、 操作员：
	 *            piType ： 2 psInfo1 ： 操作员工号 psInfo2 ： 姓名 C、 医生: piType ： 3
	 *            psInfo1 ： 医生编码 psInfo2 ： 医生姓名 psInfo3 ：隶属科室编码 psInfo4
	 *            ：职称(主任医师/副主任医师/主治医师/…) D、 药品字典 piType ： 4 psInfo1 ： 药品自编码
	 *            psInfo2 ：市医保编码 psInfo3 ： 名称(商品名) psInfo4 ：零售价
	 *            psRemark：剂型|计量单位|规格|产地|批号 （说明：
	 *            psInfo4：应与计量单位对应。例如计量单位为盒，则零售价为一盒的价格
	 *            psRemark参数中所有的数据项用|分隔，为空时填空字符串） E、 诊疗项目： piType ： 5 psInfo1 ：
	 *            项目自编码 psInfo2 ：市医保编码 psInfo3 ： 名称 psInfo4 ：零售价 psRemark：单位
	 * 
	 * 
	 * @return
	 */
	public native static int FImpInfo(int piType, String psInfo1,
			String psInfo2, String psInfo3, String psInfo4, String psRemark,
			String psOpStaus);

	/**
	 * 通用导出函数
	 * 
	 * 说明： 1、 用于导出医保本地库数据，如医保药品字典、对照表等； 2、
	 * 传入参数为表名（另外约定）和导出文件名（为TXT文本，空格分隔）；文件名包含路径，路径不存在时，自动保存在接口动态库所在路径；
	 * 
	 * 
	 * @param psTable
	 *            //I 表名
	 * @param psFile
	 *            //I文件名
	 * @return
	 */
	public native static int FExpInfo(String psTable, String psFile);

	/**
	 * 返回数组结果;
	 * 
	 * @param strResult
	 * @param separator
	 * @return
	 */
	public static String[] splitReust(String strResult, String separator) {
		if (strResult == null || strResult.equals("")) {
			return null;
		}
		String resultArray[] = strResult.split(separator);
		return resultArray;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		JSProvInwDriver test = new JSProvInwDriver();

	}
}
