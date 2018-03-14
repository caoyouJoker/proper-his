package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.emr.EMRPublicTool;
import jdo.ope.OPEINTSaveTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>介入室护士-胸痛中心记录</p>
 * 
 * @author wangqing
 *
 */
public class OPEINTNSWindowControl extends TControl{

	private TWord wordIrns;
	/**
	 * 系统参数
	 */
	private TParm sysParm;
	/**
	 * 打开已经看诊的病患的结构化病历所需要的存储路径saveFilesword
	 */
	private String[] saveFilesIrns;
	/**
	 * 住院就诊号
	 */
	private String caseNo;
	/**
	 * 急诊就诊号
	 */
	private String opdCaseNo;
	/**
	 * 病案号
	 */
	private String mrNo;	
	/**
	 * 患者姓名
	 */
	private String patName = "";
	/**
	 * 患者性别
	 */
	private String patSex = "";
	/**
	 * 患者年龄
	 */
	private String patAge = "";
	/**
	 * 介入室护士胸痛中心记录病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateIrns = false;
	/**
	 * 介入室护士胸痛中心记录病历模板classCodeConfig
	 */
	private final String irnsClassCodeConfig = "AMI_IRNS_CLASSCODE";
	/**
	 * 介入室护士胸痛中心记录病历模板subclassCodeConfig
	 */
	private final String irnsSubclassCodeConfig = "AMI_IRNS_SUBCLASSCODE";
		
	public void onInit(){
		super.onInit();
		wordIrns = (TWord) this.getComponent("tWord_0");
		wordIrns.setName("tWord_0");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("系统参数 is null");
			return;
		}
		if(obj instanceof TParm){
			sysParm = (TParm)obj;
			System.out.println("===sysParm:"+sysParm);
			caseNo = sysParm.getValue("caseNo");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = sysParm.getValue("mrNo");
//			opBookSeq = sysParm.getValue("OPBOOK_SEQ");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"男":"女");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openIrnsJhw();
	}
	
	/**
	 * 打开介入室护士病历
	 */
	public void openIrnsJhw(){
		saveFilesIrns = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irnsClassCodeConfig, irnsSubclassCodeConfig);
		if(saveFilesIrns != null && saveFilesIrns[0] != null && saveFilesIrns[0].trim().length()>0 
				&& saveFilesIrns[1] != null && saveFilesIrns[1].trim().length()>0 
				&& saveFilesIrns[2] != null && saveFilesIrns[2].trim().length()>0){// 打开已有病历
			System.out.println("打开已有介入护士病历");
			updateIrns = true;
			wordIrns.onOpen(saveFilesIrns[0], saveFilesIrns[1], 3, false);
			wordIrns.setCanEdit(true);
			wordIrns.update();	
		}else{// 新建
			System.out.println("新建介入护士病历");
			updateIrns = false;
			saveFilesIrns = EMRPublicTool.getInstance().getEmrTemplet(irnsSubclassCodeConfig);
			if(saveFilesIrns == null 
					|| saveFilesIrns[0] == null || saveFilesIrns[0].trim().length()<=0 
					|| saveFilesIrns[1] == null || saveFilesIrns[1].trim().length()<=0 
					|| saveFilesIrns[2] == null || saveFilesIrns[2].trim().length()<=0){
				this.messageBox("没有找到介入护士胸痛中心记录模板");
				return;
			}
			wordIrns.onOpen(saveFilesIrns[0], saveFilesIrns[1], 2, false);		
			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
			this.setECaptureValue(wordIrns, "CCR_START_TIME", sysDate3);// 启动导管室时间
			this.setECaptureValue(wordIrns, "CCR_READY_TIME", sysDate3);// 导管室激活(完成准备)时间
			this.setECaptureValue(wordIrns, "PAT_ARRIVE_TIME", sysDate3);// 患者到达(导管室交接)时间
			this.setECaptureValue(wordIrns, "PUNCTURE_START_TIME", sysDate3); // 开始穿刺时间
			this.setECaptureValue(wordIrns, "PUNCTURE_END_TIME", sysDate3); // 穿刺成功时间
			this.setECaptureValue(wordIrns, "GRAPHY_START_TIME", sysDate3);// 造影开始时间
			this.setECaptureValue(wordIrns, "GRAPHY_END_TIME", sysDate3);// 造影结束时间
			this.setECaptureValue(wordIrns, "SUR_START_TIME", sysDate3);// 手术开始时间
			this.setECaptureValue(wordIrns, "PBMV_TIME", sysDate3);// 球囊扩张时间			
			this.setECaptureValue(wordIrns, "SUR_END_TIME", sysDate3);// 手术结束时间
			this.setECaptureValue(wordIrns, "STENT_GRAFT_START_TIME", sysDate3);// 开始介入手术时间
			this.setECaptureValue(wordIrns, "STENT_GRAFT_END_TIME", sysDate3);// 支架释放时间
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordIrns.setWordParameter(allParm);
			wordIrns.setMicroField("姓名", patName);
			wordIrns.setMicroField("性别", patSex);
			wordIrns.setMicroField("年龄", patAge);
			wordIrns.setCanEdit(true);
			wordIrns.update();			
		}			
	}
	
	/**
	 * 保存
	 */
	public void onSave(){		
		String path = "";
		String fileName = "";
		if(updateIrns){// 更新
			System.out.println("======更新病历======");
			path = saveFilesIrns[0];
			fileName = saveFilesIrns[1];
		}else{// 新增
			System.out.println("======新增病历======");
//			TParm erdParm = OPEINTSaveTool.getInstance().saveELFile(opdcaseNo, saveFilesIrns[2], saveFilesIrns[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, irnsClassCodeConfig, irnsSubclassCodeConfig, saveFilesIrns[1]);		
			if (erdParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		wordIrns.setMessageBoxSwitch(false);
		wordIrns.onSaveAs(path, fileName, 3); 
		wordIrns.update();
		this.messageBox("保存成功！！！");
		this.closeWindow();
	}
		
	/**
	 * timestampToString
	 * @param ts
	 * @param format
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);//yyyy/MM/dd HH:mm
		try {
			//方法一
			tsStr = sdf.format(ts);
			//方法二
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * <p>设置抓取值</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setECaptureValue(TWord word, String name, String value) {
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		if(value.equals("")){
			value = " ";
		}
		ECapture ecap = (ECapture)word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name控件不存在");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(value);
	}

}
