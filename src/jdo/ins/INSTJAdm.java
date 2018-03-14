package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 住院医保管理
 * </p>
 * 
 * <p>
 * Description: 住院医保管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-1-27
 * @version 1.0
 */
public class INSTJAdm extends TJDODBTool {
	public final String nhino = "01;03;04";
	public List<String> ydctzcode = new ArrayList<String>();
	public INSTJAdm() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 实例
	 */
	public static INSTJAdm instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static INSTJAdm getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJAdm();
		return instanceObject;
	}
	//用于城乡异地-------------------------------------begin
    public  List<String> getStrs(String s){
		
		String[] ss = s.split(";");
		
		List<String> l = new ArrayList<String>();
		
		for (String string : ss) {
			if(!l.contains(string)){
				l.add(string);
			}
		}
		
		return l;
	}
    public List<String> getydctzcode(){
		if(ydctzcode.size() == 0){
			ydctzcode =  getStrs(nhino);
		}
		return ydctzcode;
	}
  //用于城乡异地-------------------------------------end
	/**
	 * 住院资格确认书开立操作
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onAdmConfirmOpen(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		TParm blockadeMessParm = new TParm();// 封锁个人信息出参
		// 1 城职 2.城居3.异地
		if (parm.getInt("CROWD_TYPE") == 1||parm.getInt("CROWD_TYPE")== 3) {
			// 住院城职 获得个人封锁信息 函数
			blockadeMessParm = INSTJTool.getInstance().DataDown_sp_D(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			// 住院城居 获得个人封锁信息 函数
			blockadeMessParm = INSTJTool.getInstance().DataDown_czys_B(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(blockadeMessParm)) {
			return blockadeMessParm.getData();
		}
		// 判断是否现金支付
		StringBuffer message = new StringBuffer();
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(0,
				1))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(1, 2))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(2, 3))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(3, 4))) {
			message.append("不享受基本医疗保险\n不享受医疗救助\n不享受门诊大额\n不享受个人账户支付\n,现金支付");// 不享受基本医疗保险\n不享受医疗救助\n不享受门诊大额\n不享受个人账户支付\n
			result.setData("MESSAGE", message.toString());
			result.setData("FLG", "Y");// 消息框输出不可以执行操作
			return result.getData();
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(0,
				1))) {
			message.append("不享受基本医疗保险\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(1,
				2))) {
			message.append("不享受医疗救助\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(2,
				3))) {
			message.append("不享受门诊大额\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(3,
				4))) {
			message.append("不享受个人账户支付\n");
		}
		if ("01".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("享受一类待遇");
		}
		if ("02".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("享受二类待遇");
		}
		if ("03".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("享受三类待遇");
		}
		if ("04".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("享受四类待遇");
		}
		TParm exeParm = new TParm();// 在途查询回参
		// 1 城职 2.城居3.异地
		if (parm.getInt("CROWD_TYPE") == 1||parm.getInt("CROWD_TYPE")== 3) {
			// 住院城职 生成资格确认书
			exeParm = INSTJTool.getInstance().DataDown_sp_B(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			// 住院城居 生成资格确认书
			exeParm = INSTJTool.getInstance().DataDown_czys_C(parm);

		}
		if (!INSTJTool.getInstance().getErrParm(exeParm)) {
			onDownConcel(parm);
			return exeParm.getData();
		}
		exeParm.setData("CROWD_TYPE", parm.getValue("CROWD_TYPE"));// 人群类别
		// // 城居在途查询
		// if (parm.getInt("CROWD_TYPE") == 2) {
		// boolean exeFlg = true;// 判断是否存在在途
		// runParm = INSTJTool.getInstance().DataDown_czys_N(parm);
		// if (!INSTJTool.getInstance().getErrParm(runParm)) {
		// exeFlg = false;
		// }
		// // 存在在途
		// if (exeFlg) {
		// // 不存在出院日期入参--？？？？
		// }
		// parm.setData("HOSP_NHI_DESC", runParm.getValue("HOSP_NHI_DESC"));//
		// 医保医院名称
		// }
		TParm tempParm = new TParm();
		// parm.setData("CANCEL_FLG","N");
		TParm queryParm = queryParm(exeParm);
		tempParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (tempParm.getCount() > 0) {
			tempParm.setData("MESSAGE", "此资格确认书已下载或开立过");
			return tempParm.getData();
		}
		tempParm = newParm(exeParm, parm);
		tempParm.setData("SPECIAL_PAT",blockadeMessParm.getValue("SP_PRESON_TYPE"));//特殊人员类别
		result = insertData(tempParm, exeParm, parm);
		if (result.getErrCode() < 0) {
			// onDownConcel(exeParm);
			result.setErr(-1, "本地数据添加失败");
			return result.getData();
		}
		result.setData("NEWMESSAGE", message.toString());// 显示输出消息框
		return result.getData();
	}

	/**
	 * 执行添加修改表操作
	 * 
	 * @param tempParm
	 * @param exeParm
	 * @param parm
	 * @return
	 */
	private TParm insertData(TParm tempParm, TParm exeParm, TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
//		 System.out.println("添加insertConfirmApply数据：" + tempParm);
		// 获得数据添加INS_ADM_CONFIRM 表
		result = INSADMConfirmTool.getInstance().insertConfirmApply(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		tempParm.setData("RESV_NO", parm.getValue("RESV_NO"));// 预约单号
		result = onSaveAdm(tempParm, parm.getInt("CROWD_TYPE"), tempParm
				.getValue("HIS_CTZ_CODE"), connection, true);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 在开立资格确认书的时候update Ins_OrdM Confirm_NO=xxx Adm_seq= xxx where Case_No=
		// xx And year_mon=xx
		// result=INSIbsTool.getInstance().updateINSIbsConfirmNo(tempParm,
		// connection);
		// if (result.getErrCode() < 0) {
		// connection.close();
		// return result.getData();
		// }
		// insert into ibs_ctz(HOSP_AREA, CASE_NO, BEGIN_DATE,MR_NO, BED_NO,
		// CTZ_CODE,CTZ_CODE1, CTZ_CODE2,OPT_USER,OPT_TERM, OPT_DATE)
		// TParm ibsParm=new TParm();
		// ibsParm.setData("REGION_CODE",parm.getValue("REGION_CODE"));//医院编码
		// ibsParm.setData("CASE_NO",tempParm.getValue("CASE_NO"));//就诊号
		// ibsParm.setData("BEGIN_DATE",tempParm.getValue("IN_HOSP_NO"));//开始时间
		// ibsParm.setData("BED_NO",tempParm.getValue("BED_NO"));//病床号
		// ibsParm.setData("MR_NO",tempParm.getValue("MR_NO"));//病案号
		// ibsParm.setData("CTZ_CODE",tempParm.getValue("CTZ1_CODE"));//身份
		// ibsParm.setData("CTZ_CODE1","");//身份1
		// ibsParm.setData("CTZ_CODE2","");//身份2
		// ibsParm.setData("OPT_USER",tempParm.getValue("OPT_USER"));//ID
		// ibsParm.setData("OPT_TERM",tempParm.getValue("OPT_TERM"));//IP
		// result=IBSCtzTool.getInstance().insertIBSCtz(queryParm, connection);
		// if (result.getErrCode() < 0) {
		// connection.close();
		// onDownConcel(exeParm);
		// return result.getData();
		// }
		connection.commit();
		connection.close();
		TParm queryParm = queryParm(tempParm);
		result = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);// 重新查询数据
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 修改 ADM_INP \ADM_RESV 表中 CONFIRM_NO \CTZ1_CODE
	 * 
	 * @param tempParm
	 * @param parm
	 * @param exeParm
	 * @param connection
	 * @return
	 */
	private TParm onSaveAdm(TParm tempParm, int crowdType, String ctz_code,
			TConnection connection, boolean flg) {
		// 查询身份
		tempParm.setData("CTZ1_CODE", ctz_code);
		// Update Adm_Resv Set Confirm_No=xxx,CTZ1_CODE =xxx
		// 参数：预约单号,资格确认书编号
		TParm result = ADMResvTool.getInstance().updateResvConfirmNo(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		// 修改 Adm_Inp Confirm_NO=xxx ,CTZ1_CODE =xxx where case_no
		// 参数：身份,资格确认书编号,CASE_NO
		result = ADMInpTool.getInstance().updateINPConfirmNo(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		return result;
	}

	/**
	 * 通过医保出参查询本地人员类别代码
	 * 
	 * @param crowType
	 * @param exeParm
	 * @return
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {//1 城职
			ctz_code = exeParm.getValue("CTZ_CODE");
			sql = " AND INS_CROWD_TYPE = '1'";
		} else if (crowType == 2) {//2.城居
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND INS_CROWD_TYPE = '2'";
		} else if (crowType == 3) {//3.异地
			ctz_code = exeParm.getValue("CTZ_CODE");
			if(this.getydctzcode().contains(ctz_code))
				sql = " AND INS_CROWD_TYPE = '2'";
			else
				sql = " AND INS_CROWD_TYPE = '1'";		
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_CODE,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_CODE", 0);
	}

	/**
	 * 查询条件数据
	 * 
	 * @param tempParm
	 * @return
	 */
	private TParm queryParm(TParm tempParm) {
		TParm queryParm = new TParm();
		if (null != tempParm.getValue("INSCASE_NO")
				&& tempParm.getValue("INSCASE_NO").length() > 0)
			queryParm.setData("INSCASE_NO", tempParm.getValue("INSCASE_NO"));// 就诊号码
		queryParm.setData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO"));// 资格确认书号码
		if (null != tempParm.getValue("CANCEL_FLG")
				&& tempParm.getValue("CANCEL_FLG").length() > 0) {
			queryParm.setData("CANCEL_FLG", tempParm.getValue("CANCEL_FLG"));// 取消注记
		}
		return queryParm;
	}

	/**
	 * 查询医保个人信息 不存在医保卡病患 需要通过身份证号码获得险种交易数据
	 */
	public Map getOwnInfo(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// 人群类别为空 没有执行刷卡动作 需要判断是否有IDNO 通过IDNO创建一个医保病患
		// 执行 调用险种交易函数方法 DataDown_czyd （A）-----？？？？没有医保卡号
		// TParm insParm = INSTJTool.getInstance().DataDown_czyd_A(parm);
		// if (!INSTJTool.getInstance().getErrParm(insParm)) {
		// insParm.setErr(-1, "调用险种交易失败");
		// return insParm.getData();
		// }
		parm.setData("CARD_NO", parm.getValue("IDNO"));// 身份证号
		// insParm.setData("NHI_REGION_CODE", parm.getValue("NHI_REGION_CODE"));
		parm.setData("TYPE", 2);// 传入类型:1社保卡卡号2 身份证号码
		TParm insParm = INSTJTool.getInstance().DataDown_czys_A(parm);// 调用险种获得病患医保信息
		// System.out.println("insParm:::" + insParm);
		if (!INSTJTool.getInstance().getErrParm(insParm)) {
			return insParm.getData();
		}
		insParm.setData("CARD_NO", parm.getValue("IDNO"));// 卡号
		insParm.setData("REGION_CODE", parm.getValue("NHI_REGION_CODE"));// 医保区域代码
		return insParm.getData();
	}

	/**
	 * 添加INS_ADM_CONFRIM 数据
	 * 
	 * @param parm
	 * @param insParm
	 * @return
	 */
	private TParm newParm(TParm parm, TParm insParm) {
		TParm newParm = new TParm();

		newParm.setData("IN_HOSP_NO", insParm.getData("NHI_REGION_CODE"));// 医保医院编码

		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// 就医类别
		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// 就医类别
		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// 就医类别
		newParm.setData("HOSP_CLASS_CODE", insParm.getData("HOSP_CLASS_CODE"));// 医院等级
		// newParm.setData("CTZ1_CODE",
		// blockadeMessParm.getData("CTZ1_CODE"));// 身份---查询ADM_INP
		newParm.setData("TRAN_CLASS", "");// 转出医院等级
		// newParm.setData("DS_DATE", insParm.getData("DS_DATE"));//
		// 出院时间----????
		// newParm.setData("DSDIAG_CODE", insParm.getData("DSDIAG_CODE"));//
		// 出院诊断----????
		// newParm.setData("DSDIAG_DESC", insParm.getData("DSDIAG_DESC"));//
		// 出院诊断名称----????
		// newParm.setData("DSDIAG_DESC2", insParm.getData("DSDIAG_DESC2"));//
		// 出院诊断描述----????
		newParm.setData("ADM_CATEGORY", insParm.getData("ADM_CATEGORY1"));// 就医类别
		newParm.setData("ADM_PRJ", insParm.getData("ADM_PRJ1"));// 就医项目
		newParm.setData("SPEDRS_CODE", insParm.getData("SPEDRS_CODE1") );// 门特类别

		newParm.setData("MR_NO", insParm.getData("MR_NO"));// 病案号
		newParm.setData("OVERINP_FLG", null != insParm
				.getData("SFBEST_TRANHOSP")
				&& insParm.getData("SFBEST_TRANHOSP").equals("1") ? "Y" : "N");// 是否跨年
		// 0
		// OR
		// 1//跨年度住院
		newParm.setData("OPEN_FLG", "Y");// 本院开立注记
		newParm.setData("SOURCE_CODE", "");// 出院情况
		newParm.setData("CANCEL_FLG", "N");// 取消注记（Y：取消；N：未取消）
		newParm.setData("CONFIRM_SRC", "");// 资格确认书来源
		newParm.setData("OPT_USER", insParm.getData("OPT_USER"));// 操作人员

		newParm.setData("OPT_TERM", insParm.getData("OPT_TERM"));// 操作端末

		newParm.setData("IN_STATUS", insParm.getData("IN_STATUS"));// 入院状态
		// 查询
		// ADM_INP
		// .PATIENT_CONDITION
		newParm.setData("UP_DATE", new TNull(String.class));// 医保上传日期---？？？？
		newParm.setData("DOWN_DATE", new TNull(String.class));// 医保下载日期---？？？？

		newParm.setData("CASE_NO", insParm.getData("CASE_NO"));// 看诊号
		newParm.setData("DEPT_CODE", insParm.getData("DEPT_CODE1"));// 科别代码

		newParm.setData("INSBRANCH_CODE", getValueName(parm
				.getValue("INSBRANCH_CODE")));// 分中心
		newParm.setData("CONFIRM_NO", parm.getData("CONFIRM_NO"));// 资格确认书编号
		newParm.setData("NHIHOSP_NO",
				getValueName(parm.getValue("HOSP_NHI_NO")));// 医院编码
		newParm.setData("HOSP_CATE", getValueName(parm.getValue("HOSP_CATE")));// 医院类别HOSP_CATE---下载存在
		newParm.setData("PAY_TYPE", getValueName(parm.getValue("PAY_TYPE")));// 支付类别---下载存在
		newParm.setData("PERSONAL_NO", getValueName(parm.getValue("OWN_NO")));// 个人编码
		newParm.setData("IDNO", getValueName(parm.getValue("SID")));// 身份证号
		newParm.setData("PAT_NAME", getValueName(parm.getValue("NAME")));// 姓名
		newParm.setData("SEX_CODE", getValueName(parm.getValue("SEX")));// 性别1男2女
		newParm.setData("BIRTH_DATE", SystemTool.getInstance().getDateReplace(
				parm.getValue("BIRTH_DATE"), true));// 出生日期
		newParm.setData("PAT_AGE", parm.getData("FAT_AGE"));// 实足年龄

		newParm.setData("INS_UNIT", getValueName(parm.getValue("INS_UNIT")));// 所属社保机构
		newParm.setData("UNIT_CODE", getValueName(parm.getValue("UNIT_CODE")));// 单位代码
		newParm.setData("UNIT_DESC", getValueName(parm.getValue("UNIT_DESC")));// 单位名称
		newParm.setData("DEPT_DESC", getValueName(parm.getValue("DEPT")));// 科别名称
		newParm.setData("DIAG_DESC", getValueName(parm
				.getValue("HOSP_DIAGNOSE")));// 住院病种
		newParm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
				parm.getValue("HOSP_START_DATE"), true));// 住院开始时间
		newParm.setData("EMG_FLG", getValueName(parm.getValue("SF_EMERGENCY")));// 是否急诊（0：否；1：是）
		newParm.setData("INP_TIME", parm.getData("INP_TIME"));// 年住院次数
		newParm.setData("INS_FLG", getValueName(parm
				.getValue("SF_FEAST_SALVATION")));// 享受医疗救助
		// （0：
		// 否；1：是）
		newParm.setData("TRANHOSP_NO", getValueName(parm
				.getValue("TRANHOSP_NO")));// 转出医院编码
		newParm.setData("TRANHOSP_DESC", getValueName(parm
				.getValue("TRANHOSP_DESC")));// 转出医院名称
		newParm.setData("TRAN_NUM", getValueName(parm
				.getValue("TRANHOSP_NUM_NO")));// 转诊转院审批号
		newParm.setData("HOMEBED_TYPE", getValueName(parm
				.getValue("HOMEDIAG_CODE")));// 家床病种类别
		newParm.setData("HOMEDIAG_DESC", getValueName(parm
				.getValue("HOMEDIAG_DESC")));// 家床病种
		newParm.setData("HOMEBED_TIME", parm.getData("HOMEBED_TIME"));// 年家床次数
		newParm.setData("HOMEBED_DAYS", parm.getData("HOMEBED_DAYS"));// 年家床累计天数
		newParm.setData("ADDINS_AMT", parm.getData("ADDFEE_AMT"));// 累计发生医疗费用金额
		newParm.setData("ADDOWN_AMT", parm.getData("ADDOWN_AMT"));// 累计自费项目金额
		newParm.setData("ADDPAY_AMT", parm.getData("ADDADD_AMT"));// 累计增负项目金额
		newParm.setData("ADDNUM_AMT", parm.getData("ADDNUM_AMT"));// 累计审批金额
		newParm.setData("INSBASE_LIMIT_BALANCE", parm
				.getData("INSBASE_LIMIT_BALANCE"));// 距基本医疗保险最高支付限额剩余额
		newParm.setData("INS_LIMIT_BALANCE", parm.getData("INS_LIMIT_BALANCE"));// 距医疗救助最高支付限额剩
		newParm.setData("START_STANDARD_AMT", parm.getDouble("STANDARD"));// 起付标准
		newParm.setData("RESTART_STANDARD_AMT", parm
				.getData("RESTART_STANDARD_AMT"));// 本次实际起付标准
		newParm.setData("OWN_RATE", getRateDouble(parm.getDouble("OWN_RATE")));// 自负比例
		newParm.setData("DECREASE_RATE", getRateDouble(parm
				.getDouble("DECREASE_RATE")));// 减负比例
		newParm.setData("REALOWN_RATE", getRateDouble(parm
				.getDouble("REALOWN_RATE")));// 实际自负比例
		newParm.setData("INSOWN_RATE", getRateDouble(parm
				.getDouble("INSOWN_RATE")));// 医疗救助自负比例
		if (insParm.getInt("CROWD_TYPE") == 1||insParm.getInt("CROWD_TYPE") == 3) {// 1 城职 3.异地
			newParm.setData("INSCASE_NO",
					getValueName(parm.getValue("CASE_NO")));// 住院号
			newParm.setData("CTZ1_CODE", parm.getData("CTZ_CODE"));// 人员类别
			String ctzCode = getCtzCode(insParm.getInt("CROWD_TYPE"), parm);// 获得医院人员类别编码
			newParm.setData("HIS_CTZ_CODE", ctzCode);// 本院人员类别
			newParm.setData("UNIT_NO", parm.getData("COM_CODE"));// 单位编码
			newParm.setData("ZFBL2", getRateDouble(parm.getDouble("ZFBL2")));// ZFBL2
			// 自付比例2
			newParm.setData("IN_HOSP_NAME", "");// 医保医院名称
		} else if (insParm.getInt("CROWD_TYPE") == 2) {// 2.城居
			newParm.setData("INSCASE_NO", getValueName(parm
					.getValue("INSCASE_NO")));// 住院号
			newParm.setData("CTZ1_CODE",
					getValueName(parm.getValue("PAT_TYPE")));// 人员类别
			String ctzCode = getCtzCode(insParm.getInt("CROWD_TYPE"), parm);// 获得医院人员类别编码
			newParm.setData("HIS_CTZ_CODE", ctzCode);// 本院人员类别
			newParm.setData("UNIT_NO",
					getValueName(parm.getValue("UNIT_CODE1")));// 单位编码
			newParm.setData("ZFBL2", 0);// ZFBL2 自付比例2
			newParm.setData("IN_HOSP_NAME", insParm.getData("HOSP_NHI_DESC"));// 医保医院名称
		}
		newParm.setData("INSOCC_CODE", insParm.getData("INSOCC_CODE1"));// 医疗救助单位
		newParm.setData("ADM_SEQ", getValueName(parm.getValue("ADM_SEQ")));// 就诊顺序号
		newParm.setData("STATION_DESC", getValueName(parm
				.getValue("INHOSP_AREA")));// 住院病区
		newParm.setData("BED_NO", getValueName(parm.getValue("INHOSP_BED_NO")));// 住院床位号
		newParm.setData("TRANHOSP_RESTANDARD_AMT", parm
				.getData("TRANHOSP_RESTART_STANDARD_AMT"));// 转出医院实收起付标准金额
		newParm.setData("TRANHOSP_DAYS", getValueName(parm
				.getValue("TRANHOSP_DAY")));// 转院住院天数
		newParm.setData("INLIMIT_DATE", SystemTool.getInstance()
				.getDateReplace(parm.getValue("BCTRANHOSP_AVA_DATE"), true));// 本次住院有效时间
		// newParm.setData("BIRTH_DATE", SystemTool.getInstance().getDate());
		// newParm.setData("INLIMIT_DATE",SystemTool.getInstance().getDate());
		//新增加异地相关栏位数据
		newParm.setData("INS_CROWD_TYPE",insParm.getData("CROWD_TYPE"));
		String localflg ="";
		if(insParm.getInt("CROWD_TYPE") == 1||
		   insParm.getInt("CROWD_TYPE") == 2)
			localflg = "Y";
		else if(insParm.getInt("CROWD_TYPE") == 3)
			localflg = "N";
		newParm.setData("LOCAL_FLG",localflg);
		//新增门诊跨年度结转累计金额
		newParm.setData("OUTP_CARRYOVER_AMT", parm.getValue("OUTP_CARRYOVER_AMT"));
		return newParm;

	}

	/**
	 * 比例显示数据
	 * 
	 * @param rate
	 * @return
	 */
	public double getRateDouble(double rate) {
		return StringTool.round(rate * 100, 2);
	}

	/**
	 * 校验为空值
	 * 
	 * @param name
	 * @return
	 */
	private String getValueName(String name) {
		if (name.equals("null")) {
			return "";
		}
		if (null != name && name.length() > 0) {
			return name;
		}
		return "";
	}

	/**
	 * 住院资格确认书下载
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onAdmConfirmDown(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		TParm rsParm = new TParm();// 资格确认书编号查询出参
		TParm admConfirmParm = parm.getParm("admConfirmParm");// 获得数据库此资格确认书号数据
		// 根据资格确认书编号查询就诊顺序号
		// ===========pangben 2012-7-16 医保返回数据是多条 需要判断此条记录是否已经执行
		// 1 城职 2.城居3.异地
		if (parm.getInt("CROWD_TYPE") == 1||parm.getInt("CROWD_TYPE")== 3) {
			rsParm = INSTJTool.getInstance().DataDown_rs_B(parm, "");

		} else if (parm.getInt("CROWD_TYPE") == 2) {
			rsParm = INSTJTool.getInstance().DataDown_czyd_B(parm, "");
		}
		if (!INSTJTool.getInstance().getErrParm(rsParm)) {
			return rsParm.getData();
		}
		int index = -1;
		// =====pangben 2012-7-16 判断那条数据是没有执行操作过的
		for (int i = 0; i < rsParm.getCount("ADM_SEQ"); i++) {
			String sql = "SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CONFIRM_NO='"
					+ rsParm.getValue("CONFIRM_NO", i)
					+ "' AND IN_STATUS<>'5' AND (DS_DATE IS NULL OR DS_DATE='')";
			TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (insParm.getCount("CONFIRM_NO") > 0) {
				index = i;
				break;
			}
		}
		//数据不存在将与界面录入的资格确认书号相同的数据插入到INS_AMD_CONFIRM 表中
		if (index < 0) {
			for (int i = 0; i < rsParm.getCount("ADM_SEQ"); i++) {
				if (parm.getValue("CONFIRM_NO").equals(rsParm.getValue("CONFIRM_NO", i))) {
					index=i;
					break;
				}
			}
//			rsParm.setErr(-1, "没有获得医保资格确认书号码");
//			return rsParm.getData();
		}
		if (index < 0) {
			rsParm.setErr(-1, "没有获得医保资格确认书号码");
			return rsParm.getData();
		}
		parm.setData("ADM_SEQ", rsParm.getValue("ADM_SEQ", index));
		rsParm.setData("NHI_REGION_CODE", index, parm
				.getValue("NHI_REGION_CODE"));
		TParm resultParm = rsParm.getRow(index);// 需要操作的数据
		resultParm.setData("NHI_REGION_CODE", parm.getValue("NHI_REGION_CODE"));
		if (parm.getInt("CROWD_TYPE") == 1||parm.getInt("CROWD_TYPE")== 3) {// 1 城职 3.异地
			// ======pangben 2012-7-16修改入参
			result = INSTJTool.getInstance().DataDown_sp_A(resultParm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 2.城居
			result = INSTJTool.getInstance().DataDown_czys_E(resultParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			//onDownConcel(parm);
			return result.getData();
		}
		// 校验是否存在数据
		TParm checkParm = INSADMConfirmTool.getInstance().queryCheckAdmComfirm(
				result);
		if (checkParm.getErrCode() < 0) {
			return checkParm.getData();
		}
		TParm confirmParm = result;
		parm.setData("ADM_SEQ", result.getValue("ADM_SEQ"));
		// 数据库不存在医院类别HOSP_CATE，支付类别PAY_TYPE，人员类别CTZ_CODE,转出类别TRANHOSP_TYPE
		// 特殊人员类别SP_PRESON_TYPE，人员类别PRESON_TYPE，上次资格确认书编号SCZGQRSBH
		// 上次自费项目金额SCZFXMJE,上次增负项目金额SCZFXMJE,上次申报金额SCSBJE,上次出院时间SCCYSJ,专科疾病ZKJB
		confirmParm.setData("NHIHOSP_NO", result.getValue("HOSP_NHI_NO"));// 医院编码
		confirmParm.setData("PERSONAL_NO", result.getValue("OWN_NO"));// 个人编码
		// 查询身份
		String ctz_code = getCtzCode(parm.getInt("CROWD_TYPE"), result);
		if (parm.getInt("CROWD_TYPE") == 1||parm.getInt("CROWD_TYPE") == 3) {
			confirmParm.setData("CTZ1_CODE", result.getValue("CTZ_CODE"));// 人员类别
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			confirmParm.setData("CTZ1_CODE", result.getValue("PAT_TYPE"));// 人员类别
		}
		confirmParm.setData("HIS_CTZ_CODE", ctz_code);// 人员类别
		confirmParm.setData("IDNO", result.getValue("SID")); // 身份证号
		confirmParm.setData("PAT_NAME", result.getValue("NAME")); // 姓名
		confirmParm.setData("SEX_CODE", result.getValue("SEX")); // 性别
		confirmParm.setData("BIRTH_DATE", SystemTool.getInstance()
				.getDateReplace(result.getValue("BIRTH_DATE"), true)); // 出生日期
		confirmParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		confirmParm.setData("PAT_AGE", result.getValue("FAT_AGE")); // 年龄
		confirmParm.setData("UNIT_NO", result.getValue("COM_CODE")); // 单位编码
		confirmParm.setData("DEPT_DESC", result.getValue("DEPT")); // 住院科别
		confirmParm.setData("DIAG_DESC", result.getValue("HOSP_DIAGNOSE")); // 住院病种(入院诊断)
		confirmParm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("HOSP_START_DATE"), true)); // 住院开始时间
		confirmParm.setData("EMG_FLG", null != result.getValue("SF_EMERGENCY")
				&& result.getValue("SF_EMERGENCY").equals("1") ? "Y" : "N");// 是否急诊
		confirmParm.setData("INS_FLG", null != result
				.getValue("SF_FEAST_SALVATION")
				&& result.getValue("SF_FEAST_SALVATION").equals("1") ? "Y"
				: "N");// 是否享受医疗救助
		confirmParm.setData("TRAN_NUM", result.getValue("TRANHOSP_NUM_NO"));// 转诊转院审批号
		confirmParm.setData("HOMEBED_TYPE", result.getValue("HOMEDIAG_CODE"));// 家床病种类别
		confirmParm.setData("ADDINS_AMT", result.getDouble("ADDFEE_AMT"));// 累计发生医疗费用金额
		confirmParm.setData("ADDPAY_AMT", result.getDouble("ADDADD_AMT"));// 累计增负项目金额
		confirmParm.setData("START_STANDARD_AMT", result.getDouble("STANDARD"));// 起付标准
		confirmParm.setData("STATION_DESC", result.getValue("INHOSP_AREA"));// 住院病区
		confirmParm.setData("BED_NO", result.getValue("INHOSP_BED_NO"));// 住院床位号
		confirmParm.setData("TRANHOSP_RESTANDARD_AMT", result
				.getDouble("TRANHOSP_RESTART_STANDARD_AMT"));// 转出医院实收起付标准金额
		confirmParm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("DS_DATE"), true));// 出院时间
		confirmParm.setData("DSDIAG_CODE", result.getValue("DIAG_CODE"));// 出院诊断
		confirmParm.setData("DSDIAG_DESC", result.getValue("DIAG_DESC"));// 出院诊断名称
		confirmParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		confirmParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		confirmParm.setData("DSDIAG_DESC2", result.getValue("DIAG_DESC2"));// 出院诊断描述
		confirmParm.setData("TRANHOSP_DAYS", result.getInt("TRANHOSP_DAY"));// 转院住院天数
		confirmParm.setData("INLIMIT_DATE", SystemTool.getInstance()
				.getDateReplace(result.getValue("BCTRANHOSP_AVA_DATE"), true));// 本次住院有效时间
		confirmParm.setData("HOSP_CATE", getValueName(result
				.getValue("HOSP_CATE")));// 医院类别HOSP_CATE
		confirmParm.setData("PAY_TYPE", getValueName(result
				.getValue("PAY_TYPE")));// 支付类别--
		confirmParm.setData("ADM_PRJ", result.getValue("INPAT_ITEM"));// 就医项目
		confirmParm.setData("SPEDRS_CODE", result.getValue("MT_DISEASE_CODE"));// 门特病种----数据库名称：门特类别
		confirmParm.setData("INSOCC_CODE", result.getValue("HELP_UNIT"));// 医疗救助单位
		confirmParm.setData("DOWN_DATE", SystemTool.getInstance().getDate());// 医保下载日期
		confirmParm.setData("PAT_CLASS", result.getValue("PRESON_TYPE"));// 人员类别
		confirmParm.setData("IN_HOSP_NO", result.getValue("HOSP_NHI_NO"));// 入院编码
		confirmParm.setData("ENTERPRISES_TYPE", result.getValue("COM_TYPE"));// 企业类别
		confirmParm.setData("INJURY_CONFIRM_NO", result.getValue("GSZGQRSBH")); // 工伤资格确认书编号
		confirmParm.setData("INSCASE_NO", result.getValue("CASE_NO"));// 住院医保就诊号
		confirmParm.setData("OWN_RATE", getRateDouble(result
				.getDouble("OWN_RATE")));// 自负比例
		confirmParm.setData("ZFBL2",
				getRateDouble(result.getDouble("OWN_RATE")));// 自负比例
		confirmParm.setData("HOSP_CLASS_CODE",
				parm.getValue("HOSP_CLASS_CODE"));// 医院等级===pangben 2012-8-14
		
		confirmParm.setData("DECREASE_RATE", getRateDouble(result
				.getDouble("DECREASE_RATE")));// 减负比例
		confirmParm.setData("REALOWN_RATE", getRateDouble(result
				.getDouble("REALOWN_RATE")));// 实际自负比例
		confirmParm.setData("INSOWN_RATE", getRateDouble(result
				.getDouble("INSOWN_RATE")));// 医疗救助自负比例
		confirmParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
		confirmParm.setData("ADM_CATEGORY", parm.getValue("ADM_CATEGORY"));// 就医类别
		confirmParm.setData("SPECIAL_PAT", result.getValue("SP_PRESON_TYPE"));// 特殊人员类别
		//新增加异地相关栏位数据
		confirmParm.setData("INS_CROWD_TYPE",parm.getData("CROWD_TYPE"));
		String localflg ="";
		if(parm.getInt("CROWD_TYPE") == 1||
				parm.getInt("CROWD_TYPE") == 2)
			localflg = "Y";
		else if(parm.getInt("CROWD_TYPE") == 3)
			localflg = "N";
		confirmParm.setData("LOCAL_FLG",localflg);
		//新增门诊跨年度结转累计金额
		confirmParm.setData("OUTP_CARRYOVER_AMT", result.getValue("OUTP_CARRYOVER_AMT"));
		
		resultParm = new TParm();
		TConnection conn = getConnection();
		if (checkParm.getCount() > 0) {
			// 修改操作
			resultParm = INSADMConfirmTool.getInstance().updateConfirmApply(
					confirmParm, conn);
		} else {
			// 入院状态 :0-资格确认书录入 1-费用已结算 2-费用已上传 3-下载已审核 4-下载已支付 5-撤销确认书
			// 6-开具资格确认书失败
			// 7-资格确认书已审核		
			confirmParm.setData("IN_STATUS", "0");// / 入院状态==pangben 2012-8-14
			// 添加操作
			if (null != admConfirmParm
					&& null != admConfirmParm.getValue("CONFIRM_NO")
					&& admConfirmParm.getValue("CONFIRM_NO").length() > 0) {
				// 将数据库数据重新赋值
				confirmParm.setData("HOSP_CLASS_CODE", admConfirmParm
						.getValue("HOSP_CLASS_CODE"));// 医院等级
				confirmParm.setData("ADM_CATEGORY", admConfirmParm
						.getValue("ADM_CATEGORY"));// 就医类别
				confirmParm.setData("EMG_FLG", admConfirmParm
						.getValue("EMG_FLG"));// 急诊注记
				confirmParm.setData("TRAN_CLASS", admConfirmParm
						.getValue("TRAN_CLASS"));// 转院等级
				confirmParm.setData("OVERINP_FLG", "Y");// 跨年注记 0
				confirmParm.setData("CONFIRM_SRC", admConfirmParm
						.getValue("CONFIRM_SRC"));// 资格确认书来源
			} else {
				// 没有数据重新辅空值
				String[] oldName = { "HOSP_CLASS_CODE", "ADM_CATEGORY",
						"EMG_FLG", "TRAN_CLASS", "CONFIRM_SRC", "IN_STATUS" };
				for (int i = 0; i < oldName.length; i++) {
					if (confirmParm.getValue(oldName[i]).equals("")) {
						confirmParm.setData(oldName[i], "");
					}

				}
			}
			confirmParm.setData("OVERINP_FLG", "N");// 跨年注记 0
			confirmParm.setData("OPEN_FLG", "Y");// 开立注记
			confirmParm.setData("CANCEL_FLG", "N");// 取消注记
			confirmParm.setData("UP_DATE", SystemTool.getInstance().getDate());// 上传时间
			resultParm = INSADMConfirmTool.getInstance()
					.insertDownLoadAdmConfirm(confirmParm, conn);
		}

		if (resultParm.getErrCode() < 0) {
			// onDownConcel(parm);
			conn.close();
			return resultParm.getData();
		}
		TParm tempParm = new TParm();
		tempParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO"));
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		tempParm.setData("RESV_NO", parm.getValue("RESV_NO"));
		result = onSaveAdm(tempParm, parm.getInt("CROWD_TYPE"), ctz_code, conn,
				false);
		if (result.getErrCode() < 0) {
			return result.getData();
		}
		conn.commit();
		conn.close();
		return result.getData();
	}

	/**
	 * 资格确认书下载撤销操作
	 * 
	 * @param parm
	 */
	private void onDownConcel(TParm parm) {
		TParm concelParm = null;
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 城职 2.城居
			concelParm = INSTJTool.getInstance().DataDown_sp_C(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 1 城职 2.城居
			concelParm = INSTJTool.getInstance().DataDown_czys_F(parm);
		}
	}

	/**
	 * 费用分割 费用结算操作
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onSettlement(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// 费用分割
		TParm insAmtParm = INSIbsTool.getInstance().queryRetrunInsAmt(parm);
		// System.out.println("结算使用查询医保返回数据"+insAmtParm);
		// 结算使用查询医保返回数据
		if (insAmtParm.getErrCode() < 0) {
			return insAmtParm.getData();
		}
		// 费用分割 结算操作全数据查询
		TParm allSumAmtParm = INSIbsUpLoadTool.getInstance().queryAllSumAmt(
				parm);
		// System.out.println("费用分割 结算操作全数据查询"+allSumAmtParm);
		if (allSumAmtParm.getErrCode() < 0) {
			return allSumAmtParm.getData();
		}
		String insAdmSql = " SELECT INSOWN_RATE FROM INS_ADM_CONFIRM "
				+ "  WHERE CONFIRM_NO = '" + parm.getValue("CONFIRM_NO") + "' ";
		// System.out.println("insAdmSql>>>>>>>>>"+insAdmSql);
		TParm insAdmConfirm = new TParm(TJDODBTool.getInstance().select(
				insAdmSql));
		double salvationPaymentScale = 0.00;
		salvationPaymentScale = insAdmConfirm.getDouble("INSOWN_RATE", 0);
		// System.out.println("救助支付比例" + salvationPaymentScale);
		/**
		 * 查询ADM_SEQ
		 */
		TParm seqParm = INSIbsTool.getInstance().queryAdmSeq(parm);
		// System.out.println("查询ADM_SEQ"+seqParm);
		if (seqParm.getErrCode() < 0) {
			return seqParm.getData();
		}
		// 结算金额汇总显示:1.医保申报金额 2.自费金额 3.增负金额 4.发生金额
		String[] nameAmt = { "NHI_AMT", "OWN_AMT", "ADD_AMT", "AMT" };
		// 01.药品费，02.检查费，03.治疗费，04.手术费，05.床位费，06.材料费，07.其他费，08.全血费，09.成分血费
		String[] nameType = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_",
				"MATERIAL_", "OTHER_", "BLOODALL_", "BLOOD_" };// 收费金额类型
		TParm saveAmtParm = new TParm();
		int index = 0;// 中间数据操作
		for (int j = 0; j < nameType.length; j++) {// 获得类型 循环nameType 有九种类型
			// 要求与insAmtParm数据查询的行数相同
			for (int i = index; i < allSumAmtParm.getCount();) {
				TParm tempParm = allSumAmtParm.getRow(i);
				for (int k = 0; k < nameAmt.length; k++) {// 获得结算金额 循环nameAmt
					// 有四种金额 要求与insAmtParm数据库查询的列数相同
					saveAmtParm.setData(nameType[j] + nameAmt[k], tempParm
							.getDouble(nameAmt[k]));
				}
				index++;
				break;
			}

		}
		double totAmt = 0.00;// 合计 发生金额
		double totOwnAmt = 0.00;// 合计自费金额
		double totAddAmt = 0.00;// 合计 增负金额
		double totNhiAmt = 0.00;// 合计申报金额
		for (int j = 0; j < nameType.length; j++) 
		{
			totNhiAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[0]);
			totOwnAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[1]);
			totAddAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[2]);
		}
		totAmt =  StringTool.round(totNhiAmt + totOwnAmt + totAddAmt, 2); 
		double materialAmt = 0.00;
		materialAmt = StringTool.round(saveAmtParm.getDouble("MATERIAL_OWN_AMT")+saveAmtParm.getDouble("MATERIAL_ADD_AMT")+saveAmtParm.getDouble("MATERIAL_NHI_AMT"), 2);
		saveAmtParm.setData("MATERIAL_AMT", materialAmt);
		String admSeq = seqParm.getValue("ADM_SEQ", 0);// 就诊顺序号
		// INS_CROWD_TYPE :1.城职 2.城居
		TParm settlement = new TParm();
		parm.setData("ADM_SEQ", admSeq);// 就诊顺序号
		parm.setData("TOT_AMT", totAmt);
		parm.setData("OWN_AMT", totOwnAmt);
		parm.setData("ADD_AMT", totAddAmt);
		parm.setData("NHI_AMT", totNhiAmt);
		// System.out.println("费用结算parm:::" + parm);
		TParm amtParm = new TParm();
		amtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		// 费用结算使用 获得医保 数据 保存INS_IBS 数据
		amtParm = INSIbsTool.getInstance().queryInsAmt(amtParm);
		// System.out.println("费用结算使用 获得医保 数据 保存INS_IBS 数据"+amtParm);
		if (amtParm.getErrCode() < 0) {
			return amtParm.getData();
		}
		// 单病种结算操作接口
		if (null != parm.getValue("TYPE")
				&& parm.getValue("TYPE").equals("SINGLE")) {
			TParm tempParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT M.SDISEASE_CODE FROM ADM_INP P, INS_ADM_CONFIRM M "
					+ "WHERE P.CASE_NO= '" + parm.getValue("CASE_NO")
					+ "' " + "AND P.CONFIRM_NO=M.CONFIRM_NO"));
			if (tempParm.getErrCode() < 0) {
				return tempParm.getData();
			}
			parm.setData("SIN_DISEASE_CODE", tempParm.getValue("SDISEASE_CODE",
					0));// 单病种编码
			parm.setData("IN_DAY", parm.getInt("ADM_DAYS")); // 住院天数 界面获得
			parm.setData("OUT_TIME", parm.getValue("DS_DATE"));// 出院时间 界面获得
			tempParm = new TParm(
					TJDODBTool
							.getInstance()
							.select(
									"SELECT I.SINGLE_NHI_AMT,I.SINGLE_STANDARD_OWN_AMT,I.SINGLE_SUPPLYING_AMT"
											+ ",I.STARTPAY_OWN_AMT,I.PERCOPAYMENT_RATE_AMT,I.BED_SINGLE_AMT,I.MATERIAL_SINGLE_AMT "
											+ " FROM INS_IBS I "
											+ " WHERE I.CASE_NO = '"
											+ parm.getValue("CASE_NO")
											+ "' AND I.YEAR_MON = '"
											+ parm.getValue("YEAR_MON") + "'"));
			if (tempParm.getErrCode() < 0) {
				return tempParm.getData();
			}
			// 床位费特需金额 和 医用材料费特需金额
			double sum = tempParm.getDouble("BED_SINGLE_AMT", 0)
					+ tempParm.getDouble("MATERIAL_SINGLE_AMT", 0);
			// 特需项目金额
			parm.setData("SPECIAL_AMT", sum);
			//城职
			if (parm.getInt("INS_CROWD_TYPE") == 1) 
			   settlement = INSTJTool.getInstance().DataDown_sp_I1(parm);
			else 
			   settlement = INSTJTool.getInstance().DataDown_czys_G1(parm);
		
		} else {
			// 正常结算操作接口
			if (parm.getInt("INS_CROWD_TYPE") == 1||
			   (parm.getInt("INS_CROWD_TYPE") == 2&&
				parm.getValue("LOCAL_FLG").equals("N"))) {//本地城职 、异地城职、异地城居
				// System.out.println("城职结算入参"+parm);
				settlement = INSTJTool.getInstance().DataDown_sp_I(parm);
			} else if (parm.getInt("INS_CROWD_TYPE") == 2&&
					   parm.getValue("LOCAL_FLG").equals("Y")) {//本地城居
				parm.setData("RESTART_STANDARD_AMT", insAmtParm.getDouble(
						"RESTART_STANDARD_AMT", 0));// 本次实际起付标准
				parm.setData("FACT_PAYMENT_SCALE", parm
						.getDouble("REALOWN_RATE"));// 本次实际自负比例 界面获得
				parm.setData("SALVATION_PAYMENT_SCALE", salvationPaymentScale);// 救助支付比例
				parm.setData("APPLY1_AMT", insAmtParm.getDouble(
						"INS_LIMIT_BALANCE", 0));// 距医疗救助最高支付限额剩余额
				parm.setData("TOTAL_PAYMENT_AMT", insAmtParm.getDouble(
						"INSBASE_LIMIT_BALANCE", 0));// 距基本医疗保险最高支付限额剩余额
				// System.out.println("城居结算入参"+parm);
				settlement = INSTJTool.getInstance().DataDown_czys_G(parm);
			}
		}
		if (!INSTJTool.getInstance().getErrParm(settlement)) {
			result.setErr(-1, "费用结算失败");
			return result.getData();
		}
		// 累计数据
		TConnection conn = getConnection();
		// 单病种操作保存数据
		if (null != parm.getValue("TYPE")
				&& parm.getValue("TYPE").equals("SINGLE")) 
		{
			getInsIbsSingleParm(parm, amtParm, settlement, saveAmtParm);
			//System.out.println("saveAmtParm:"+saveAmtParm);
			result = INSIbsTool.getInstance().updateInsIbsSingleAmt(
					saveAmtParm, conn);
			//System.out.println("result:"+result);
			
		} else {
			getInsIbsParm(settlement, parm, saveAmtParm);
			// 本次实收起付标准金额
			saveAmtParm.setData("RESTART_STANDARD_AMT", settlement
					.getDouble("BCSSQF_STANDRD"));
			// 费用分割界面中 费用结算操作修改 医保回参数据保存
			// System.out.println("结算更新入参"+saveAmtParm);
//			saveAmtParm.setData("MATERIAL_AMT", saveAmtParm
//					.getDouble("MATERIAL_AMT")
//					- parm.getDouble("ADDAMT"));
			result = INSIbsTool.getInstance()
					.updateInsIbsAmt(saveAmtParm, conn);
		}
		if (result.getErrCode() < 0) {
			conn.close();
			return result.getData();
		}
		// 修改入院状态 0-资格确认书录入1-费用已结2-费用已上传3-下载已审核4-下载已支付5-撤销确认书 6-开具资格确认书失败
		// 7-资格确认书已审核
		parm.setData("IN_STATUS", "1");//
		result = INSADMConfirmTool.getInstance().updateAdmConfrimForInStatus(
				parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result.getData();
		}
		conn.commit();
		conn.close();
		return result.getData();
	}

	/**
	 * 获得修改INS_IBS 数据
	 * 
	 * @param settlement
	 *            医保接口出参
	 * @param parm
	 *            界面数据
	 * @param saveAmtParm
	 *            需要获得的数据
	 */
	private void getInsIbsParm(TParm settlement, TParm parm, TParm saveAmtParm) {
		saveAmtParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));// 区域
		saveAmtParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		saveAmtParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		saveAmtParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));// 期号
		saveAmtParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
		saveAmtParm.setData("CHEMICAL_DESC", parm.getValue("CHEMICAL_DESC"));// 化验说明
		saveAmtParm.setData("DS_DATE", parm.getValue("DS_DATE"));// 出院时间
		saveAmtParm.setData("UPLOAD_FLG", parm.getValue("UPLOAD_FLG"));// 上传注记
		saveAmtParm.setData("STARTPAY_OWN_AMT", settlement
				.getDouble("TOTAL_OWN_AMT"));// 起付标准以上自负比例金额
		saveAmtParm.setData("INS_HIGHLIMIT_AMT", settlement
				.getDouble("EXCEED_OWN_AMT"));// 超限自负金额
		saveAmtParm.setData("PERCOPAYMENT_RATE_AMT", settlement
				.getDouble("APPLY1_OWN_AMT"));// 医疗救助自负金额----医疗救助个人按比例负担金额
		saveAmtParm.setData("OWN_AMT", parm.getDouble("OWN_AMT"));// 自负金额
		saveAmtParm.setData("APPLY_AMT", 0.00);// 统筹基金支付医院申请
		saveAmtParm.setData("HOSP_APPLY_AMT", 0.00);// 医疗救助社保申请金额
		saveAmtParm.setData("ADD_AMT", parm.getDouble("ADD_AMT"));// 增负项目金额
		saveAmtParm.setData("NHI_PAY", settlement.getDouble("TOTAL_AGENT_AMT"));// 基本医疗社保申请金额
		saveAmtParm.setData("NHI_PAY_REAL", settlement
				.getDouble("TOTAL_AGENT_AMT"));// 基本医疗社保申请金额
		saveAmtParm.setData("NHI_COMMENT", settlement
				.getDouble("FLG_AGENT_AMT"));// 医疗救助社保支付金额
		saveAmtParm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		saveAmtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));// 资格确认书编号
		saveAmtParm.setData("ARMYAI_AMT", settlement.getDouble("AGENT_AMT"));// 军残补助金额
		//城乡大病补助金额
		if (parm.getInt("INS_CROWD_TYPE") == 2&&parm.getValue("LOCAL_FLG").equals("Y"))// 城乡
		saveAmtParm.setData("ILLNESS_SUBSIDY_AMT", settlement.getDouble("ILLNESS_SUBSIDY_AMT"));
		else
		saveAmtParm.setData("ILLNESS_SUBSIDY_AMT", 0.00);		
	}

	/**
	 * 单病种操作 获得修改INS_IBS数据
	 * 
	 * @param parm
	 * @param saveAmtParm
	 */
	private void getInsIbsSingleParm(TParm parm, TParm amtParm,
			TParm settlement, TParm saveAmtParm) 
	{
		// 本次实收起付标准金额
		saveAmtParm.setData("RESTART_STANDARD_AMT", settlement.getDouble("BCSSQF_STANDRD"));
		// 病种申报金额
		saveAmtParm.setData("SINGLE_NHI_AMT", settlement.getDouble("NHI_OWN_AMT"));
		// 病种自付金额
		saveAmtParm.setData("SINGLE_STANDARD_AMT", settlement.getDouble("OWN_AMT_AMT"));
		// 基本医疗保险补足金额
		saveAmtParm.setData("SINGLE_SUPPLYING_AMT", settlement.getDouble("COMP_AMT"));
		// 本次病种付费标准
		saveAmtParm.setData("SINGLE_STANDARD_AMT_T", settlement.getDouble("PAY_AMT_STD"));
		// 本次病种自负标准
		saveAmtParm.setData("SINGLE_STANDARD_OWN_AMT_T", settlement.getDouble("OWN_AMT_STD"));
		// 医院超病种标准自负金额
		saveAmtParm.setData("SINGLE_STANDARD_OWN_AMT", settlement.getDouble("EXT_OWN_AMT"));
		//统筹基金自负金额
		saveAmtParm.setData("STARTPAY_OWN_AMT",settlement.getDouble("TOTAL_OWN_AMT"));
		//医疗救助自负金额
		saveAmtParm.setData("PERCOPAYMENT_RATE_AMT",settlement.getDouble("APPLY1_OWN_AMT"));
		//超限自负金额
		saveAmtParm.setData("INS_HIGHLIMIT_AMT",settlement.getDouble("EXCEED_OWN_AMT"));
		//基本医疗社保申请金额
		saveAmtParm.setData("NHI_PAY", settlement.getDouble("TOTAL_AGENT_AMT"));
		saveAmtParm.setData("NHI_PAY_REAL", settlement.getDouble("TOTAL_AGENT_AMT"));
		//医疗救助社保支付金额
		saveAmtParm.setData("NHI_COMMENT", settlement.getDouble("FLG_AGENT_AMT"));
		//补助金额
		saveAmtParm.setData("ARMYAI_AMT", settlement.getDouble("AGENT_AMT"));
		//区域
		saveAmtParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		saveAmtParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		saveAmtParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		//期号
		saveAmtParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));
		//化验说明
		saveAmtParm.setData("CHEMICAL_DESC", parm.getValue("CHEMICAL_DESC"));
		//出院时间
		saveAmtParm.setData("DS_DATE", parm.getValue("DS_DATE"));
		//上传注记
		saveAmtParm.setData("UPLOAD_FLG", parm.getValue("UPLOAD_FLG"));
		//就诊号
		saveAmtParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		//自负金额
		saveAmtParm.setData("OWN_AMT", parm.getDouble("OWN_AMT"));
		//增负项目金额
		saveAmtParm.setData("ADD_AMT", parm.getDouble("ADD_AMT"));
		//就诊顺序号
		saveAmtParm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		//资格确认书编号
		saveAmtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		//城乡大病补助金额
		if (parm.getInt("INS_CROWD_TYPE") == 2)// 城乡
		saveAmtParm.setData("ILLNESS_SUBSIDY_AMT", settlement.getDouble("ILLNESS_SUBSIDY_AMT"));
		else
		saveAmtParm.setData("ILLNESS_SUBSIDY_AMT", 0.00);			
		
	}
}
