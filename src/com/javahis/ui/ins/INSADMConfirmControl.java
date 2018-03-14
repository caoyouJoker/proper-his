package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSTJAdm;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title:住院资格确认书下载和开立
 * </p>
 * 
 * <p>
 * Description:住院资格确认书下载和开立
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-11-25
 * @version 2.0
 */
public class INSADMConfirmControl extends TControl {

	private String caseNO;// 就诊号
	private Timestamp Indate;// 住院日期
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private TParm regionParm;// 医保区域代码
	private String confirmNo;// 资格确认书号码跨年使用
	// 第二个页签
	private String pageTwo = "UNIT_CODE;INS_UNIT;IDNO;PAT_NAME;PAT_AGE;SEX_CODE;REGION_CODE;HOSP_CLASS_CODE;"
			+ "CTZ1_CODE;DIAG_DESC;IN_START_DATE;INP_TIME;USER_ID;TRANHOSP_DESC;TRAN_CLASS;"
			+ "TRAN_NUM;TRANHOSP_DAYS;INLIMIT_DATE;HOMEBED_TYPE;HOMEDIAG_DESC;"
			+ "HOMEBED_TIME;HOMEBED_DAYS;INSBRANCH_CODE;ADDPAY_AMT;ADDINS_AMT;"
			+ "ADDNUM_AMT;INSBASE_LIMIT_BALANCE;INS_LIMIT_BALANCE;INSOCC_CODE;EMG_FLG;"
			+ "INS_FLG;CANCEL_FLG;INS_CODE;CONFIRM_NO;UNIT_DESC";
	// 第一个页签
	private String pageOne = "CONFIRM_NO1;RESV_NO;MR_NO;IDNO1;PAT_NAME1;ADM_PRJ1;"
			+ "ADM_CATEGORY1;SPEDRS_CODE1;DEPT_CODE1;DIAG_DESC1;IN_DATE;"
			+ "INSBRANCH_CODE1;INSOCC_CODE1;PERSONAL_NO;PRE_CONFIRM_NO;"
			+ "TRAN_NUM1;GS_CONFIRM_NO;PRE_OWN_AMT;PRE_NHI_AMT;PRE_ADD_AMT;"
			+ "PRE_OUT_TIME;SPE_DISEASE;OVERINP_FLG1;BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1";
	// 第三个页签
	private String pageThree = "REGION_CODE2;ADM_PRJ;ADM_CATEGORY;SPEDRS_CODE;START_STANDARD_AMT;RESTART_STANDARD_AMT;"
			+ "OWN_RATE;DECREASE_RATE;REALOWN_RATE;INSOWN_RATE;INSCASE_NO;STATION_DESC;BED_NO;"
			+ "TRANHOSP_RESTANDARD_AMT;DEPT_CODE;OVERINP_FLG;DEPT_DESC";
	private TParm insParm ;// 刷卡集合

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		getEnabledIsFalse(pageTwo + ";" + pageThree + ";APP_DATE;REGION_CODE1",
				false);// 设置状态
		this.setValue("APP_DATE", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE1", Operator.getRegion());// 医院编码
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		// System.out.println("regionParm:::"+regionParm);
		// 初始默认下载状态
		onExeEnable(true);
		callFunction("UI|readCard|setEnabled", false);
		callFunction("UI|readCardYD|setEnabled", false);
		this.setValue("INSOCC_CODE1", "1");
		this.setValue("ADM_PRJ1", "2");
		this.setValue("ADM_CATEGORY1", "21");
		this.setValue("INS_ADVANCE_TYPE", "1");//开立类型默认为正常
		// this.setValue("INS_CROWD_TYPE", 1);// 人群类别
	}

	/**
	 * 查询预约未结案
	 */
	public void onResvNClose() {
		queryTemp(true);

	}

	/**
	 * 查询住院未结案
	 */
	public void onAdmNClose() {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
		// if (this.getValue("IDNO1").toString().length() > 0) {
		// //System.out.println("IDNO"+result.getValue("IDNO"));
		// //System.out.println("IDNO1"+this.getValue("IDNO1"));
		// if (null != insParm
		// && !result.getValue("IDNO").equals(this.getValue("IDNO1"))) {
		// this.messageBox("刷卡病患信息与住院病患信息不符");
		// onClear();
		// return;
		// }
		// }
		
		System.out.println("result::"+result);
		/*modified by Eric 20170525 start
		determine whether or not a emergency case*/
		if(result.getValue("ADM_SOURCE").equals("02")){
			this.setValue("EMG_FLG1", 1);
		}else{
			this.setValue("EMG_FLG1", 0);
		}
		/*modified by Eric 20170525 end*/
		
		
		
		this.setValueForParm("RESV_NO;MR_NO;IN_DATE", result);
		setValueParm(result);
		this.setValue("DEPT_CODE1", result.getValue("DEPT_CODE"));
		caseNO = result.getValue("CASE_NO");// 就诊号
		Indate = result.getTimestamp("IN_DATE");// 住院日期
//		System.out.println("Indate==============="+Indate);
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", caseNO);
		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (queryParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// System.out.println("resultl:::::::" + result);
		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
				queryParm.getRow(0));
		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
				+ result.getValue("ICD_CHN_DESC"));// 住院诊断
		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//医院编码
		setValue("OVERINP_FLG1", "N");
		callFunction("UI|OVERINP_FLG1|setEnabled", true);
		this.setValue("ADM_PRJ1", "2");// 就医专案
		// getComboBox("PAY_TYPE").grabFocus();
		this.grabFocus("ADM_CATEGORY1");// 就医类别
		// getTextField("ADM_CATEGORY1").grabFocus();
	}

	/**
	 * 验证方法
	 * 
	 * @return
	 */
	private boolean checkSave() {
		if (getRadioButton("RO_Open").isSelected()) {// 开立
			if (!this
					.emptyTextCheck("RESV_NO,REGION_CODE1,MR_NO,IDNO1,PAT_NAME1,INSOCC_CODE1,IN_DATE")) {
				return false;
			}
//			if (this.getValue("INSBRANCH_CODE1").toString().length() <= 0) {// 分中心
//				this.messageBox("医保分中心不可以为空");
//				this.grabFocus("INSBRANCH_CODE1");
//				return false;
//			}
		} else {// 下载
			if (!this
					.emptyTextCheck("RESV_NO,MR_NO,IDNO1,PAT_NAME1,CONFIRM_NO1,INS_CROWD_TYPE")) {// 资格确认书编号
				return false;
			}
		}
		if (this.getValueString("IDNO1").length() == 15
			||this.getValueString("IDNO1").length() == 18) {

		} else {
			this.messageBox("身分证长度应为15或18码");
			getTextField("IDNO1").grabFocus();
			return false;
		}
		if (getRadioButton("RO_Open").isSelected()) {// 开立
			if (this.getValue("ADM_CATEGORY1").toString().length() <= 0) {// 就医类别
				this.messageBox("就医类别不可以为空");
				this.grabFocus("ADM_CATEGORY1");
				return false;
			}
			if (!this.emptyTextCheck("ADM_PRJ1")) {// 、就医专案
				return false;
			}
			// 门特类别
			if ((this.getValueString("ADM_CATEGORY1").equals("31")
					|| this.getValueString("ADM_CATEGORY1").equals("32")
					|| this.getValueString("ADM_CATEGORY1").equals("33") || this
					.getValueString("ADM_CATEGORY1").equals("34"))) {
				if (this.getValue("SPEDRS_CODE1").toString().length() <= 0) {// 门特类别
					this.messageBox("门特类别不可以为空");
					this.grabFocus("SPEDRS_CODE1");
					return false;

				} else {
					boolean flg = false;
					// 当就医类别不为特殊病类选项时，门特类别不能选
					for (int i = 31; i < 35; i++) {
						if (this.getValueInt("ADM_CATEGORY1") == i) {
							flg = true;
							break;
						}
					}
					if (!flg) {
						if (this.getValue("SPEDRS_CODE1").toString().length() > 0) {
							this.messageBox("就医类别不为特殊病,门特类别不可以选择");
							return false;
						}
					}
					if (this.getValueString("ADM_CATEGORY1").equals("31")// 就医类别
							|| this.getValueString("ADM_CATEGORY1")
									.equals("33")) {
						flg = false;// 校验使用
						for (int i = 1; i < 4; i++) {
							if (this.getValueInt("SPEDRS_CODE1") == i) {// 门特类别
								flg = true;
								break;
							}
						}
						if (!flg) {
							// ’10’，’再生障碍性贫血’
							// ’11’，’慢性血小板减少性紫癫’
							// ’21’，’血友病’
							// ’22’，’肝移植术后抗排异治疗’
							if (this.getValueInt("SPEDRS_CODE1") == 10
									|| this.getValueInt("SPEDRS_CODE1") == 11
									|| this.getValueInt("SPEDRS_CODE1") == 22
									|| this.getValueInt("SPEDRS_CODE1") == 21) {
								flg = true;
							}
						}
						if (!flg) {
							this.messageBox("门特类别输入错误,只能选1,2,3,10,11,21,22");
							return false;
						}
					} else if (this.getValueString("ADM_CATEGORY1")
							.equals("34")
							|| this.getValueString("ADM_CATEGORY1")
									.equals("32")) {
						flg = false;// 校验使用
						for (int i = 4; i < 10; i++) {
							if (this.getValueInt("SPEDRS_CODE1") == i) {// 门特类别
								flg = true;
								break;
							}
						}
						if (!flg) {
							if (this.getValueInt("SPEDRS_CODE1") == 30) {
								flg = true;
							}
						}
						if (!flg) {
							this.messageBox("门特类别输入错误,只能选4,5,6,7,8,9,30");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 获得文本控件
	 * 
	 * @param name
	 * @return
	 */
	private TTextField getTextField(String name) {
		return (TTextField) this.getComponent(name);
	}

	/**
	 * 获得单选控件
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * 获得下拉列表控件
	 * 
	 * @param name
	 * @return
	 */
	private TComboBox getComboBox(String name) {
		return (TComboBox) this.getComponent(name);
	}

	/**
	 * 获得复选框下拉列表
	 * 
	 * @param name
	 * @return
	 */
	private TCheckBox getCheckBox(String name) {
		return (TCheckBox) this.getComponent(name);
	}

	/**
	 * 获得页签控件
	 * 
	 * @param name
	 * @return
	 */
	private TTabbedPane getTabbedPane(String name) {
		return (TTabbedPane) this.getComponent(name);
	}

	/**
	 * 下载/开立方法
	 */
	public void onSave() {
		// if (null==insParm ||null==insParm.getValue("SID") ||
		// insParm.getValue("SID").length()<=0) {
		// this.messageBox("请执行刷卡操作");
		// return;
		// }
		if (!checkSave()) {
			return;
		}
		TParm result = null;
		if (getRadioButton("RO_Open").isSelected()) {// 开立		
			result = onSaveOpen();
		} else {// 下载
			result = onSaveDown();
		}
		if (null == result) {
			return;
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");// 执行失败
			return;
		}
		if (getRadioButton("RO_Upd").isSelected()) {
			TParm queryParm = new TParm();
			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO1"));// 资格确认书号码
			queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(
					queryParm);
			if (queryParm.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}
			// System.out.println("resultl:::::::" + result);
			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
					queryParm.getRow(0));
			// this.setValueForParm(pageTwoNHI+";"+pageThree,result);
			this.messageBox("资格确认书下载成功");
		} else {
			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
					result.getRow(0));
			this.messageBox("资格确认书开立成功");
		}
		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
		// getEnabledIsFalse(pageTwo, false);
	}

	/**
	 * 资格确认书开立数据
	 */
	private void openParm(TParm parm) {
		parm.setData("APP_DATE", df.format(this.getValue("APP_DATE")));// 申请日期
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		parm.setData("HOSP_CLASS_CODE", regionParm.getValue("HOSP_CLASS", 0));// 医院等级
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// 人群类别
		parm.setData("SFBEST_TRANHOSP",
				this.getValueBoolean("OVERINP_FLG1") ? "1" : "0");// 是否跨年
//		 System.out.println("急诊注记"+this.getValueBoolean("EMG_FLG1"));
		parm.setData("EMG_FLG", this.getValueBoolean("EMG_FLG1") ? "1" : "0");// 是否急诊
		String[] pageOnes = pageOne.split(";");// 第一个页签数据
		for (int i = 0; i < pageOnes.length; i++) {
			parm.setData(pageOnes[i], this.getValue(pageOnes[i]));// 获得第一个页签数据
		}
		if (this.getValueInt("INS_CROWD_TYPE") == 2) {// 城居
			parm.setData("HOMEDIAG_DESC1", this.getText("HOMEDIAG_CODE1"));// 家床病种名称
			parm.setData("TRAMA_ATTEST", this.getText("TRAMA_ATTEST"));// 外伤证明
		}
		parm.setData("DEPT_DESC", this.getText("DEPT_CODE1"));// 科室名称
		String day ="";
		if("Y".equals(getValueString("OVERINP_FLG1"))){
		Timestamp sysTime = SystemTool.getInstance().getDate();
		DateFormat df1 = new SimpleDateFormat("yyyy");//当前年份
		day = df1.format(sysTime)+"-01-01 00:00:00";
		}
		else			
		day = StringTool.getString(Indate, "yyyy-MM-dd HH:mm:ss");
//        System.out.println("IN_DATE============" +day);
		parm.setData("IN_DATE", day);// 住院开始日期
		parm.setData("CASE_NO", caseNO);// 就诊号
		parm.setData("IN_STATUS", "0");// 入院状态
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// System.out.println("/获得第一个页签数据insParm::" + parm);

	}

	/**
	 * 执行开立操作
	 * 
	 * @return
	 */
	private TParm onSaveOpen() {
		TParm result = new TParm();
		TParm queryParm = new TParm();
		if (this.getValue("ADM_CATEGORY1").toString().length() <= 0) {
			this.grabFocus("ADM_CATEGORY1");
			this.messageBox("就医类别不能为空");
			return null;
		}
		if (this.getValue("ADM_PRJ1").toString().length() <= 0) {
			this.grabFocus("ADM_PRJ1");
			this.messageBox("就医专案不能为空");
			return null;
		}
		String ydflg = "1";
		//跨年患者不刷卡
        if("Y".equals(getValueString("OVERINP_FLG1")))
        {
//        	System.out.println("---------kuanian-----------------");
        	if (getValueString("PERSONAL_NO").length() <= 0) {
    			this.messageBox("请执行刷卡操作");
    			return null;
    		}
        	//获得跨年上半段资格确认书编号
        	String SQL = " SELECT CONFIRM_NO FROM INS_ADM_CONFIRM"+
        	             " WHERE CASE_NO = '"+ caseNO + "'" +
        	             " AND IN_STATUS <> 5"+
        	             " AND OVERINP_FLG = 'N'";            
            TParm DATA= new TParm(TJDODBTool.getInstance().select(SQL)); 
            if (DATA.getErrCode() < 0) 
		    {		      
		      return null;
		    }
        	TParm insParm = new TParm();
        	openParm(insParm);
    		insParm.setData("REGION_CODE", Operator.getRegion());
    		//城居
    		if(this.getValue("INS_CROWD_TYPE").equals("2")){
    		   insParm.setData("CHECK_CODES",""+"@"+this.getValue("INS_ADVANCE_TYPE"));
    		   //医院编码@类别
       		   insParm.setData("ADVANCE_CODE", regionParm.getValue("NHI_NO", 0)+
	                     "@"+this.getValue("INS_ADVANCE_TYPE"));
       		   
       		   //转诊转院审批号
       		   insParm.setData("TRAN_NUM1",DATA.getValue("CONFIRM_NO",0));
       		   
    		}
    		//城职或异地
    		else {
    			if(this.getValue("INS_CROWD_TYPE").equals("1"))
    				ydflg = "1";    	       		        			
    			else if(this.getValue("INS_CROWD_TYPE").equals("3"))
    				ydflg = "3";
    			insParm.setData("CHECK_CODES"," "+"@"+
  		    		   this.getValue("INS_ADVANCE_TYPE")+"@"+ydflg);
  		        //医院编码@类别
     	       	insParm.setData("ADVANCE_CODE", regionParm.getValue("NHI_NO", 0)+
     		                     "@"+this.getValue("INS_ADVANCE_TYPE")+"@"+ydflg); 
     	       	//上次资格确认书编号
     	        insParm.setData("PRE_CONFIRM_NO",DATA.getValue("CONFIRM_NO",0));
    		}
    		insParm.setData("CROWD_TYPE",getValue("INS_CROWD_TYPE"));
    		insParm.setData("SPEDRS_CODE1"," ");
    		Map insMap =insParm.getData();
    		result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(insMap));
    		if (result.getErrCode() < 0) {
    			this.messageBox(result.getErrText());
    			return null;
    		}  
        }else
        {
    		if (null == insParm || null == insParm.getValue("SID")
    				|| insParm.getValue("SID").length() <= 0) {
    			this.messageBox("请执行刷卡操作");
    			return null;
    		}
    		if(null == insParm.getValue("CROWD_TYPE")
    				|| insParm.getValue("CROWD_TYPE").length() <= 0){
    			this.messageBox("人群类别没有传回，请联系医保科");
    			return null;
    		}   			
    		openParm(insParm);
    		insParm.setData("REGION_CODE", Operator.getRegion());
    		//城居
    		if(this.getValue("INS_CROWD_TYPE").equals("2")){
    		insParm.setData("CHECK_CODES",insParm.getValue("CHECK_CODES")+
    				                      "@"+this.getValue("INS_ADVANCE_TYPE"));
    		//医院编码@类别
    		insParm.setData("ADVANCE_CODE", regionParm.getValue("NHI_NO", 0)+
                    "@"+this.getValue("INS_ADVANCE_TYPE"));
    		}
    		//城职或异地
    		else {
    			if(this.getValue("INS_CROWD_TYPE").equals("1"))
    				ydflg = "1"; 	   			
    			else if(this.getValue("INS_CROWD_TYPE").equals("3"))
    				ydflg = "3"; 
    			
    		  insParm.setData("CHECK_CODES",(insParm.getValue("CHECK_CODES").length()==0? 
    				          " ": insParm.getValue("CHECK_CODES"))+
	                          "@"+this.getValue("INS_ADVANCE_TYPE")+"@"+ydflg); 
              //医院编码@类别
              insParm.setData("ADVANCE_CODE", regionParm.getValue("NHI_NO", 0)+
                      "@"+this.getValue("INS_ADVANCE_TYPE")+"@"+ydflg);	
    			
    		}
    		// insParm.setData("EXEFLG","Y");//执行刷卡
    		// 住院诊断INP_DIAG_DESC
    		if (null != insParm.getValue("PRE_OUT_TIME")
    				&& insParm.getValue("PRE_OUT_TIME").length() > 0) {
    			insParm.setData("PRE_OUT_TIME", insParm.getValue("PRE_OUT_TIME")
    					.replace("-", "").replace("/", "").substring(0, 8));

    		}
//    		  for (Iterator i = mapParm.keySet().iterator(); i.hasNext();) {
//    			   Object obj = i.next();
//    			   System.out.println(obj);// 循环输出key
//    			   System.out.println("key=" + obj + " value=" + mapParm.get(obj));
//    			  }
//    		   result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(a.getData()));
//    		 System.out.println("insParm===============:"+insParm);
    		result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(insParm.getData()));
    		if (result.getErrCode() < 0) {
    			this.messageBox(result.getErrText());
    			return null;
    		}
    		// } 		
        }
        if (null != result.getValue("NEWMESSAGE")
				&& result.getValue("NEWMESSAGE").length() > 0) {
			this.messageBox(result.getValue("NEWMESSAGE"));
		}
		// 存在数据显示此病患资格确认书信息
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE"));
			if (null != result.getValue("FLG")
					&& result.getValue("FLG").length() > 0) {// 现金支付
				return null;
			}
			queryAmdConfrim(result);
			return null;
		}	
		//垫付延迟患者更新发放状态
		if(this.getValue("INS_ADVANCE_TYPE").equals("2")){
			String sql = " UPDATE INS_ADVANCE_OUT SET PAY_FLG ='1',PAY_DATE = SYSDATE" +
					     " WHERE CASE_NO ='"+ caseNO + "'";
			 TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
             // 判断错误值
             if (result1.getErrCode() < 0) {
                 messageBox(result1.getErrText());
                 return null;
             }
		}
		return result;    		
		
	}

	/**
	 * 查询信息，下载开立使用
	 * 
	 * @param queryParm
	 */
	private void queryAmdConfrim(TParm queryParm) {
		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
				queryParm.getRow(0));
		this.setValue("REGION_CODE2", queryParm.getValue("REGION_CODE", 0));
		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
	}

	/**
	 * 执行下载操作
	 * 
	 * @return
	 */
	private TParm onSaveDown() {
		// 下载操作
		TParm parm = new TParm();
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		parm.setData("IDNO", this.getValue("IDNO1"));// 身份证号码
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO1"));// 资格确认书编号
		parm.setData("CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// 人群类别
		parm.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		parm.setData("OPT_USER", Operator.getID());// ID
		parm.setData("CASE_NO", caseNO);// 就诊号
		parm.setData("RESV_NO", this.getValue("RESV_NO"));// 预约单号
		parm.setData("OPT_TERM", Operator.getIP());// IP
		parm.setData("ADM_CATEGORY", this.getValue("ADM_CATEGORY1"));// 就医类别==pangben 2012-8-13
		parm.setData("HOSP_CLASS_CODE", regionParm.getValue("HOSP_CLASS", 0));// 医院等级==pangben 2012-8-14
		String advanceCode = "";
		if(this.getValue("INS_CROWD_TYPE").equals("1")||
		   this.getValue("INS_CROWD_TYPE").equals("2"))
			advanceCode =regionParm.getValue("NHI_NO", 0)+"@"+"1"; 
		else if ( this.getValue("INS_CROWD_TYPE").equals("3"))
			advanceCode =regionParm.getValue("NHI_NO", 0)+"@"+"3"; 
		parm.setData("ADVANCE_CODE", advanceCode);//医院编码@异地标志
		// System.out.println("下载数据入参：：：：："+parm);
		TParm admConfirmParm = new TParm();
		// 查询已经存在的数据，执行跨年操作
		if (this.getCheckBox("OVERINP_FLG1").isSelected()) {
			admConfirmParm.setData("CONFIRM_NO", confirmNo);
			admConfirmParm = INSADMConfirmTool.getInstance()
					.queryCheckAdmComfirm(admConfirmParm);
			if (admConfirmParm.getErrCode() < 0) {
				return admConfirmParm;
			}
			parm.setData("admConfirmParm", admConfirmParm.getRow(0).getData());
		}
		TParm result = new TParm(INSTJAdm.getInstance().onAdmConfirmDown(
				parm.getData()));
		return result;
	}

	/**
	 * 设置控件不可选
	 * 
	 * @param name
	 * @param status
	 */
	private void getEnabledIsFalse(String name, boolean status) {
		String[] names = name.split(";");
		if (names.length <= 0) {
			return;
		}
		for (int i = 0; i < names.length; i++) {
			callFunction("UI|" + names[i] + "|setEnabled", status);
		}
	}

	public void onClear() {
		// 头部
		clearValue("CONFIRM_NO2;INS_ODI_NO;INS_CROWD_TYPE");
		// 第一个页签
		clearValue(pageOne);
		getRadioButton("RO_Upd").isSelected();
		// 第二个页签
		clearValue(pageTwo);
		// 第三个页签
		clearValue(pageThree);
		caseNO = null;// 就诊号
		insParm = null;// 刷卡集合
		confirmNo = null;// 资格确认书号码跨年使用
		callFunction("UI|IDNO1|setEnabled", true);// 可以修改IDNO
		this.setValue("INSOCC_CODE1", "1");
		this.setValue("ADM_PRJ1", "2");
		this.setValue("ADM_CATEGORY1", "21");
		callFunction("UI|OVERINP_FLG1|setEnabled", true);
		this.setValue("INS_ADVANCE_TYPE", "1");//开立类型默认为正常
		
		/*modified by Eric 20170525*/
		this.setValue("EMG_FLG1", 0);
		
	}

	/**
	 * 跨年度医保患者查询
	 * 
	 */
	public void onEveInsPat() {
		queryTemp(false);
	}

	/**
	 * 查询病患
	 * 
	 * @param flg
	 *            true:预约未结案 false:跨年度查询
	 */
	private void queryTemp(boolean flg) {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		if (flg) {
			parm.setData("FLG", "Y");
		} else {
			parm.setData("FLG", "N");
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSResvNClose.x", parm);
		if (this.getValue("IDNO1").toString().length() > 0) {
			if (null != insParm
					&& !result.getValue("IDNO").equals(this.getValue("IDNO1"))) {
				this.messageBox("刷卡病患信息与住院病患信息不符");
				// onClear();
				// return;
			}
		}
		this.setValueForParm("RESV_NO;MR_NO;IN_DATE", result);
		setValueParm(result);
		this.setValue("DEPT_CODE1", result.getValue("DEPT_CODE"));
		caseNO = result.getValue("CASE_NO");// 就诊号
		Indate = result.getTimestamp("IN_DATE");// 住院日期
//		System.out.println("Indate==============="+Indate);
		if (!flg) {
			// 默认选择开立
			this.getRadioButton("RO_Open").setSelected(true);
			//跨年标记设为"Y",不可编辑
			this.setValue("OVERINP_FLG1", "Y");
			callFunction("UI|OVERINP_FLG1|setEnabled", false);
			//confirmNo = result.getValue("CONFIRM_NO");
			callFunction("UI|INS_CROWD_TYPE|setEnabled", true);// 人群类别
			//跨年患者取得个人编码
			String sql = " SELECT  A.PERSONAL_NO, B.MRO_CTZ  " +
					     " FROM JAVAHIS.INS_ADM_CONFIRM A, SYS_CTZ B  " +
					     " WHERE  A.MR_NO  ='"+ getValue("MR_NO")+"' " +
					     " AND  A.CASE_NO  ='"+caseNO+"' " +
					     " AND  A.HIS_CTZ_CODE  = B.CTZ_CODE ";
		    TParm resultIns = new TParm(TJDODBTool.getInstance().select(sql));
		    if (resultIns.getErrCode() < 0) 
		    {
		      messageBox("个人编码取得失败！");
		      return;
		    }
		    //个人编码
		    setValue("PERSONAL_NO", resultIns.getData("PERSONAL_NO", 0));
		    //人群类别
		    setValue("INS_CROWD_TYPE", resultIns.getData("MRO_CTZ",0));
		} else {
			this.setValue("OVERINP_FLG1", "N");
			callFunction("UI|OVERINP_FLG1|setEnabled", true);
		}
		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
				+ result.getValue("ICD_CHN_DESC"));// 住院诊断
		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//医院编码
		this.setValue("ADM_PRJ1", "2");// 就医专案
		// getComboBox("PAY_TYPE").grabFocus();
		this.grabFocus("ADM_CATEGORY1");// 就医类别
	}

	/**
	 * 刷卡操作
	 */
	public void onReadCard() {
		TParm parm = new TParm();
		if (!this.emptyTextCheck("MR_NO")) {
			return;
		}
		//判断是否正常或是延迟垫付
		if(this.getValue("INS_ADVANCE_TYPE").equals("")){
			this.messageBox("开立类型不能为空");
		    return;
		}
		// parm.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		// 人群类别
		String opbadvancetype = "1";//收费类别
		String SQL = " SELECT PERSONAL_NO FROM INS_ADVANCE_OUT"+
        " WHERE CASE_NO = '"+ caseNO+ "'" +
        " AND APPROVE_TYPE ='1'" +
        " AND PAY_FLG = '0'";            
        TParm DATA= new TParm(TJDODBTool.getInstance().select(SQL));
		if(this.getValue("INS_ADVANCE_TYPE").equals("2")){
//            System.out.println("DATA=========="+DATA);
            if (DATA.getCount()<= 0) {
    			messageBox("没有延迟垫付患者");
    			return;
    		}
            opbadvancetype = "2";
		}
		else{
			 if (DATA.getCount()> 0) {
				 messageBox("此患者是垫付延迟患者，开立类型为垫付延迟");	
	    		return;
	    	}
		}
		//医院编码@住院时间@类别
		String inDate = StringTool.getString(Indate, "yyyyMMdd");//住院时间
//		 System.out.println("inDate=========="+inDate);
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+inDate+"@"+opbadvancetype;
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@住院时间@类别		
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOne.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		setParm(insParm, 1);
		this.grabFocus("ADM_CATEGORY1");
	}
	/**
	 * 异地刷卡操作
	 */
	public void onReadCardYD() {
		TParm parm = new TParm();
		if (!this.emptyTextCheck("MR_NO")) {
			return;
		}
		//判断是否正常或是延迟垫付
		if(this.getValue("INS_ADVANCE_TYPE").equals("")){
			this.messageBox("开立类型不能为空");
		    return;
		}
		// 人群类别
		String opbadvancetype = "1";//收费类别
		String SQL = " SELECT PERSONAL_NO FROM INS_ADVANCE_OUT"+
        " WHERE CASE_NO = '"+ caseNO+ "'" +
        " AND APPROVE_TYPE ='1'" +
        " AND PAY_FLG = '0'";            
        TParm DATA= new TParm(TJDODBTool.getInstance().select(SQL));
		if(this.getValue("INS_ADVANCE_TYPE").equals("2")){
//            System.out.println("DATA=========="+DATA);
            if (DATA.getCount()<= 0) {
    			messageBox("没有延迟垫付患者");
    			return;
    		}
            opbadvancetype = "2";
		}
		else{
			 if (DATA.getCount()> 0) {
				 messageBox("此患者是垫付延迟患者，开立类型为垫付延迟");	
	    		return;
	    	}
		}
		//医院编码@住院时间@类别
		String inDate = StringTool.getString(Indate, "yyyyMMdd");//住院时间
//		 System.out.println("inDate=========="+inDate);
		String advancecode = regionParm.getValue("NHI_NO", 0)+","+"@"+inDate+"@"+opbadvancetype;
		parm.setData("ADVANCE_CODE",advancecode);//医院编码@住院时间@类别		
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOneYD.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		setParm(insParm, 1);
		this.grabFocus("ADM_CATEGORY1");
	}

	/**
	 * 执行刷卡 和 查询个人信息赋值
	 * 
	 * @param parm
	 */
	private void setParm(TParm parm, int type) {
		this.setValue("INS_CROWD_TYPE", parm.getValue("CROWD_TYPE"));// 人群类别赋值
		this.setValue("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// 个人编码
		// 根据返回的身份证号码获得病患信息
		parm.setData("IDNO", parm.getValue("SID"));// 身份证号码
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("不存在身份证号为:" + parm.getValue("SID") + "的信息");
			// onClear();
			parm = null;
			return;
		}
		// 需要校验使用 =================pangben 2012-3-18 以后需要返回
		if (type == 1) {// 刷卡
			if (this.getValue("IDNO1").toString().length() > 0) {
				if (!parm.getValue("SID").equals(this.getValue("IDNO1"))) {
					this.messageBox("刷卡病患信息与住院病患信息不符,\n医保身份证号码为:"
							+ parm.getValue("SID"));
					// onClear();
					parm = null;
					return;
				}
			}
		}
		// 需要校验使用 =================pangben stop
		if (parm.getInt("CROWD_TYPE") == 1||
			parm.getInt("CROWD_TYPE") == 3) {// 1.城职,3.异地
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					true);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					false);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 城居
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					false);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					true);
		}

		// this.setValue("MR_NO", result.getRow(0).getValue("MR_NO"));// 病案号
		setValueParm(result.getRow(0));
		callFunction("UI|IDNO1|setEnabled", false);// 执行刷卡不可以修改IDNO
	}

	/**
	 * 赋值
	 * 
	 * @param parm
	 */
	private void setValueParm(TParm parm) {
		this.setValue("PAT_NAME1", parm.getValue("PAT_NAME"));// 姓名
		this.setValue("IDNO1", parm.getValue("IDNO"));// 身份证号码
	}

	/**
	 * 设置编辑状态
	 * 
	 * @param name
	 * @param flg
	 */
	private void getIsEnabled(String name, boolean flg) {
		String[] names = name.split(";");
		for (int i = 0; i < names.length; i++) {
			callFunction("UI|" + names[i] + "|setEnabled", flg);
		}
		this.clearValue(name);
	}

	/**
	 * 下载开立单选框选择
	 */
	public void onExe() {
		this.onClear();
		if (this.getRadioButton("RO_Upd").isSelected()) {// 下载
			onExeEnable(true);
			callFunction("UI|readCard|setEnabled", false);
			callFunction("UI|readCardYD|setEnabled", false);
			// this.setValue("INS_CROWD_TYPE", 1);// 人群类别

		} else {// 开立
			onExeEnable(false);
			callFunction("UI|readCard|setEnabled", true);
			callFunction("UI|readCardYD|setEnabled", true);
			this.setValue("INS_CROWD_TYPE", "");
		}
	}

	private void onExeEnable(boolean flg) {
		callFunction("UI|CONFIRM_NO1|setEnabled", flg);// 资格确认书
		callFunction("UI|INS_CROWD_TYPE|setEnabled", flg);// 人群类别
	}

	/**
	 * 查询按钮操作
	 */
	public void onQueryInsInfo() {
		TParm queryParm = new TParm();
		// 资格确认书编号和医保住院编号
		if (this.getValue("CONFIRM_NO2").toString().length() <= 0
				&& this.getValue("INSCASE_NO1").toString().length() <= 0) {
			this.messageBox("请输入查询的条件");
			this.grabFocus("CONFIRM_NO2");
			return;
		}
		if (this.getValue("CONFIRM_NO2").toString().length() > 0) {
			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO2"));// 资格确认书号码
		}
		if (this.getValue("INSCASE_NO1").toString().length() > 0) {
			queryParm.setData("INSCASE_NO", this.getValue("INSCASE_NO1"));// 医保住院编号
		}
		// 查询数据
		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (queryParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (queryParm.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		queryAmdConfrim(queryParm);
	}
	/**
	 * 住院医保资格确认书历史
	 */
	public void onConfirmNo() {
		TParm parm = new TParm();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSSearchConfirm.x", parm);
	}
}
