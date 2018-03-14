package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.javahis.util.OdoUtil;

import jdo.emr.EMRPublicTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
/**
 * 跌倒、疼痛评估
 * @author wangqing 20180119 
 *
 */

public class REGFallAndPainAssessmentControl extends TControl {

	/**
	 * 系统参数
	 */
	private TParm sysP;

	/**
	 * 检伤号
	 */
	private String triageNo;

	/**
	 * 病案号
	 */
	private String mrNo;

	private TWord word;

	/**
	 * 结构化病历路径
	 */
	private String[] saveFiles;

	private final String classCodeConfig = "REG_FALL_PAIN_CLASSCODE";

	private final String subClassCodeConfig = "REG_FALL_PAIN_SUBCLASSCODE";

	/**
	 * 插入、更新标志：true，插入；false，更新
	 */
	private boolean isInsert = false;

	/**
	 * 是否在关闭时自动保存，应用于病案号补录
	 */
	private boolean isAutoSave = false;

	private static Map<String, String> FALL_DESC_VALUE_MAP=new HashMap<String, String>();

	private static Map<String, String> PAIN_DESC_VALUE_MAP=new HashMap<String, String>();

	private static String WORD_TAG = "WORD";

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		// 初始化系统传入参数
		Object o = this.getParameter();
		if(o!=null && o instanceof TParm){
			sysP = (TParm) o;
			triageNo = sysP.getValue("TRIAGE_NO");
			mrNo = sysP.getValue("MR_NO");
			if(triageNo==null || triageNo.trim().length()<=0){
				this.messageBox("检伤号不能为空");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}		
		}else{
			this.messageBox("系统参数错误");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});
			return;
		}
		// 初始化控件、注册监听事件
		word = (TWord) this.getComponent(WORD_TAG);
		word.addListener(TWordBase.CALCULATE_EXPRESSION, this, "onCalculateExpression");

		// MAP init start
		FALL_DESC_VALUE_MAP.put("", "");
		FALL_DESC_VALUE_MAP.put("零危险", "1");
		FALL_DESC_VALUE_MAP.put("低度危险", "2");
		FALL_DESC_VALUE_MAP.put("高度危险", "3");

		PAIN_DESC_VALUE_MAP.put("", "");
		PAIN_DESC_VALUE_MAP.put("无痛", "1");
		PAIN_DESC_VALUE_MAP.put("轻度疼痛", "2");
		PAIN_DESC_VALUE_MAP.put("中度疼痛", "3");
		PAIN_DESC_VALUE_MAP.put("重度疼痛", "4");
		PAIN_DESC_VALUE_MAP.put("剧烈疼痛", "5");
		PAIN_DESC_VALUE_MAP.put("无法忍受", "6");
		// MAP init end

		initJHW();

	}

	/**
	 * 初始化结构化病历
	 */
	public void initJHW(){
		saveFiles = EMRPublicTool.getInstance().getEmrFileRebuild(triageNo, classCodeConfig, subClassCodeConfig);
		if(saveFiles == null 
				|| saveFiles[0] == null || saveFiles[0].trim().length()<=0 
				|| saveFiles[1] == null || saveFiles[1].trim().length()<=0 
				|| saveFiles[2] == null || saveFiles[2].trim().length()<=0){// 新建病历
			System.out.println("=======新建病历======");
			saveFiles = EMRPublicTool.getInstance().getEmrTemplet(subClassCodeConfig);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);
			word.update();

			// 自动带入MR_NO start
			if(mrNo!=null && mrNo.trim().length()>0){
				TParm wordP = new TParm();
				wordP.setData("FILE_HEAD_TITLE_MR_NO","TEXT", mrNo);	
				word.setWordParameter(wordP);
				
				Pat pat = Pat.onQueryByMrNo(mrNo);		
				word.setMicroField("姓名", pat.getName());
				word.setMicroField("性别", pat.getSexString());
				word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));				
				word.update();
			}	
			// 自动带入MR_NO end
			isInsert = true;
		}else{
			System.out.println("=======打开已有病历======");
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			word.update();
			isInsert = false;

			// 病案号补录 start
			if(saveFiles[3]==null || saveFiles[3].trim().length()<=0){
				if(mrNo!=null && mrNo.trim().length()>0){
					TParm wordP = new TParm();
					wordP.setData("FILE_HEAD_TITLE_MR_NO","TEXT", mrNo);	
					word.setWordParameter(wordP);
					
					Pat pat = Pat.onQueryByMrNo(mrNo);		
					word.setMicroField("姓名", pat.getName());
					word.setMicroField("性别", pat.getSexString());
					word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
					
					word.update();
					isAutoSave = true;
				}	
			}
			// 病案号补录 end


		}
	}

	/**
	 * 保存
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		if(!isInsert){
			System.out.println("---更新数据---");
			path = saveFiles[0];
			fileName = saveFiles[1];
		}else{
			System.out.println("---插入数据---");
			TParm p = new TParm();
			p.setData("CASE_NO", triageNo);// 检伤号
			p.setData("CLASS_CODE_CONFIG", classCodeConfig);// 
			p.setData("SUB_CLASS_CODE_CONFIG", subClassCodeConfig);//
			p.setData("SUB_CLASS_DESC", saveFiles[1]);// 
			p.setData("MR_NO", mrNo);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(p);
			if(erdParm.getErrCode()<0){
				this.messageBox("ERR:" + erdParm.getErrCode() + erdParm.getErrText() +
						erdParm.getErrName());
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		word.setMessageBoxSwitch(false);
		word.onSaveAs(path, fileName, 3);
		word.setCanEdit(true);

		// update ERD_EVALUTION start
		String efv = this.getEFixedValue(word, "FALL_SCORE");
		int fallScore = Integer.parseInt(efv);
		String fallDesc = this.getEFixedValue(word, "FALL_DESC");
		String fallValue = FALL_DESC_VALUE_MAP.get(fallDesc);

		String efv2 = this.getEFixedValue(word, "PAIN_SCORE");
		int painScore = Integer.parseInt(efv2);
		String painDesc = this.getEFixedValue(word, "PAIN_DESC");
		String painValue = PAIN_DESC_VALUE_MAP.get(painDesc);

		String sql = "UPDATE ERD_EVALUTION "
				+ "SET FALL_SCORE="+fallScore+", FALL_DESC='"+fallValue+"', "
				+ "PAIN_SCORE="+painScore+", PAIN_DESC='"+painValue+"' "
				+ "WHERE TRIAGE_NO='"+triageNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
		// update ERD_EVALUTION end

		this.messageBox("保存成功！");
		this.closeWindow();
	}

	/**
	 * 关闭页面事件
	 */
	public boolean onClosing(){
		if(isAutoSave){
			onUpdateMrNo();
		}
		return true;
	}
	
	/**
	 * 更新病案号
	 */
	public void onUpdateMrNo(){
		String sql = "UPDATE EMR_FILE_INDEX "
				+ "SET MR_NO='"+mrNo+"' "
				+ "WHERE CASE_NO='"+triageNo+"' "
				+ "AND SUBCLASS_CODE='"+TConfig.getSystemValue(subClassCodeConfig)+"' "
				+ "AND CLASS_CODE='"+TConfig.getSystemValue(classCodeConfig)+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			return;
		}

		String path = "";
		String fileName = "";
		path = saveFiles[0];
		fileName = saveFiles[1];
		word.setMessageBoxSwitch(false);
		word.onSaveAs(path, fileName, 3);
		word.setCanEdit(true);
		word.update();
	}

	/**
	 * 清空
	 */
	public void onClear(){

	}

	
	
	/**
	 * 计算表达式触发事件
	 */
	public void onCalculateExpression(String wordTag){
		//		System.out.println("{wordTag:"+wordTag+"}");
		if(wordTag!=null && wordTag.equals(WORD_TAG)){
			// 跌倒评估
			String efv = this.getEFixedValue(word, "FALL_SCORE");
			//			System.out.println("efv="+efv);

			int fallScore = Integer.parseInt(efv);
			String fallDesc = "";
			if(fallScore>=0 && fallScore<25){
				fallDesc = "零危险";
			}else if(fallScore>=25 && fallScore<46){
				fallDesc = "低度危险";
			}else if(fallScore>=46){
				fallDesc = "高度危险";
			}else{
				fallDesc = "";
			}
			this.setEFixedValue(word, "FALL_DESC", fallDesc);
			// 疼痛评估
			String efv2 = this.getEFixedValue(word, "PAIN_SCORE");
			int painScore = Integer.parseInt(efv2);
			String painDesc = "";
			if(painScore==0){
				painDesc = "无痛";
			}else if(painScore==1 || painScore==2){
				painDesc = "轻度疼痛";
			}else if(painScore==3 || painScore==4){
				painDesc = "中度疼痛";
			}else if(painScore==5 || painScore==6){
				painDesc = "重度疼痛";
			}else if(painScore==7 || painScore==8){
				painDesc = "剧烈疼痛";
			}else if(painScore==9 || painScore==10){
				painDesc = "无法忍受";
			}else{
				painDesc = "";
			}
			this.setEFixedValue(word, "PAIN_DESC", painDesc);
		}	
	}

	/**
	 * 获取宏控件值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getMicroFieldValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EMicroField mf = (EMicroField) word.findObject(name, EComponent.MICRO_FIELD_TYPE);
		if(mf == null){ 
			System.out.println("word--->name控件不存在");
			return null;
		}
		return mf.getText();
	}

	/**
	 * 获取固定文本值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getEFixedValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return null;	
		}
		return f.getText();
	}

	/**
	 * <p>设置固定文本值</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setEFixedValue(TWord word, String name, String value){
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return;	
		}
		f.setText(value);
		word.update();
	}

	/**
	 * 获取数字控件值
	 * @param name
	 * @param word
	 * @return
	 */
	public String getNumberChooseValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ENumberChoose nc = (ENumberChoose) word.findObject(name, EComponent.NUMBER_CHOOSE_TYPE);
		if(nc == null) {
			System.out.println("word--->name控件不存在");
			return null;
		}
		return nc.getText();
	}


	public void onTest(){
		TParm parm = new TParm();
		parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", "1111");	
		word.setWordParameter(parm);
//		word.setMicroField("姓名", "222");
		word.update();
	}
	
	
}
















