package com.javahis.ui.adm;

import java.sql.Timestamp;

import javax.swing.JOptionPane;

import jdo.adm.ADMTool;
import jdo.reg.Reg;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

 /** 
 * @ClassName: ADMWristPrintControl 
 * @Description: TODO
 * @author: lili
 * @date: 2018年4月3日 上午9:37:15  
 */
public class ADMWristPrintControl extends TControl{
	Pat pat;
	String mrNo;
	String caseNo;
	TParm admParm = new TParm();
	TParm admInfo;
	String age;
	//初始化
	public void onInit() {
		super.onInit();
		TParm parm=(TParm) this.getParameter();
		mrNo=parm.getValue("MR_NO");
		caseNo=parm.getValue("CASE_NO");
		pat = Pat.onQueryByMrNo(mrNo);
		admParm.setData("CASE_NO", caseNo);
		admInfo = ADMTool.getInstance().getADM_INFO(admParm);
	    Timestamp sysDate = SystemTool.getInstance().getDate();
	    //age = DateUtil.showAge(pat.getBirthday(), sysDate);
	    //modify by lili 20180403
	    age = OdiUtil.showAge(pat.getBirthday(), sysDate);
	    
	}
	//成人腕带打印
	public void onWristAdult(){
		TParm newBornParm=new TParm(TJDODBTool.getInstance().select("select new_born_flg from adm_inp where case_no='"+caseNo+"'"));
	    if("Y".equals(newBornParm.getValue("NEW_BORN_FLG",0))){
	    	if(JOptionPane.showConfirmDialog(null, "此病患是新生儿，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) == 0){
	    		adultContent();
		    }
	    }else{
	    	adultContent();
	    }
	    
	}
	//成人腕带内容 20170626 zhanglei更改需要显示内容以及JHW文件需要显示1、条码；2、姓名OK；3、性别 新增；4、出生日期 新增；5、体重 新增；6、床号 新增；7、年龄OK；8、科室OK。
	public void adultContent(){
		TParm print=new TParm();
		//得到姓名
		print.setData("PatName", "TEXT", "姓名:"+pat.getName());
		//得到病案号
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		//得到性别
		print.setData("Sex", "TEXT", "性别:"+pat.getSexString());
		//得到出生日期
		print.setData("BirthDay", "TEXT", "出生日期:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//体重
		print.setData("WEIGHT", "TEXT", "体重:"+pat.getWeight()+ "KG");
		//得到年龄
    	print.setData("Age", "TEXT", "年龄:"+age);
    	//得到科室
    	print.setData("Dept", "TEXT", "科室:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//得到床号
    	print.setData("BedNO","TEXT","床号:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristAdult.jhw",print,true);
	}
	//儿童腕带打印20170626 zhanglei更改需要显示内容以及JHW文件 1、条码；2、姓名；3、性别；4、出生日期；5、体重；6、床号；7、年龄；8、科室。
	public void onWristChild(){
		TParm print=new TParm();
		//得到姓名
		print.setData("PatName", "TEXT", "姓名:"+pat.getName());
		//得到病案号
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		//得到性别
		print.setData("Sex", "TEXT", "性别:"+pat.getSexString());
		//得到出生日期
		print.setData("Birth", "TEXT", "出生日期:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//体重
		print.setData("Weight", "TEXT", "体重:"+pat.getWeight() + "KG");
		//得到年龄
    	print.setData("Age", "TEXT", "年龄:"+age);
    	//得到科室
    	print.setData("Dept", "TEXT", "科室:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//得到床号
    	print.setData("BedNO","TEXT","床号:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
//		print.setData("PatName", "TEXT", "姓名:"+pat.getName());
//		print.setData("Barcode1", "TEXT", pat.getMrNo());
//    	print.setData("Age", "TEXT", "年龄:"+age);
//    	print.setData("Dept", "TEXT", "科室:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
//    	print.setData("BedNO","TEXT","床号:");
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist.jhw",print,true);
	}
	//新生儿腕带打印20170626 zhanglei更改需要显示内容以及JHW文件1、条码；2、姓名；3、性别；4、出生日期；5、体重；6、床号；7、年龄；8、科室。
	public void onWristNewBody(){
		TParm print=new TParm();
		//得到姓名
		print.setData("PatName", "TEXT", "姓名:"+pat.getName());
		//得到病案号
		print.setData("Barcode", "TEXT", pat.getMrNo());
		//得到性别
		print.setData("SEX", "TEXT", "性别:"+pat.getSexString());
		//得到出生日期
		print.setData("Birth", "TEXT", "出生日期:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//新生儿体重
		//print.setData("Weight","TEXT","体重:"+pat.getNewBodyWeight()+"g");
		//		modify  by lili 20180403
		//print.setData("Weight","TEXT","体重:"+pat.getWeight()+"g");
		print.setData("Weight","TEXT","体重:"+pat.getNewBodyWeight()+"g");
		//得到年龄
    	print.setData("Age", "TEXT", "年龄:"+age);
    	//得到科室
    	print.setData("Dept", "TEXT", "科室:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//得到床号
    	print.setData("BedNO","TEXT","床号:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
//		print.setData("PatName", "TEXT", "姓名:"+pat.getName());
//		print.setData("Barcode", "TEXT", pat.getMrNo());
//    	print.setData("Birth", "TEXT", "出生日期:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
//    	print.setData("Weight","TEXT","体重:"+pat.getNewBodyWeight()+"g");
//    	print.setData("BedNO","TEXT","床号:");
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristBaby.jhw",print,true);
	}
}
