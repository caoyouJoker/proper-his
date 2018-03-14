package com.javahis.ui.hrm;

import java.util.HashSet;
import java.util.Set;

import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 健康检查阳性检查结果控制类 </p>
 *
 * <p> Description: 健康检查阳性检查结果控制类 </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMPositivePrintControl extends TControl {
	//打印显示器
	private TWord word;
	//团体代码、合同代码
	private String companyCode,contractCode;
	//合同对象
	private HRMContractD contractD;
	//报到对象
	private HRMPatAdm adm;
	//病患信息对象
	private HRMPatInfo pat;
	//医嘱对象
	private HRMOrder order;
	//合同控件
	private TTextFormat contract,patName;
	/**
	 * 打印EXECL数据
	 */
	private TParm[] execleTable;
	private TTextFormat company;// add by wangb 2016/06/24 团体名称
	
	 /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initComponent();
        onClear();
    }
    /**
     * 清空事件
     */
    public void onClear() {
    	this.clearValue("START_DATE;END_DATE;MR_NO;COMPANY_CODE;CONTRACT_CODE;CASE_NO");
		word.onNewFile();
		word.update();
		contractD=new HRMContractD();
		adm=new HRMPatAdm();
		pat=new HRMPatInfo();
		order=new HRMOrder();
		
		// add by wangb 2016/06/24 团体名称需要根据不同登录角色筛选
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
		
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
	}
    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose(){
    	//根据选择的团体代码，构造并初始化该团体的合同信息TTextFormat
		companyCode=this.getValueString("COMPANY_CODE");
                if(companyCode.length()==0){
                    return;
                }
		TParm contractParm=contractD.onQueryByCompany(companyCode);
		if(contractParm.getErrCode()!=0){
			this.messageBox_("没有数据");
		}
		// System.out.println("contractParm="+contractParm);
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode=contractParm.getValue("ID",0);
		if(StringUtil.isNullString(contractCode)){
			this.messageBox_("查询失败");
			return;
		}
		contract.setValue(contractCode);
		//根据选择的合同代码，构造并初始化该合同的病患信息TTextFormat
		TParm patParm=adm.getTParmByCompanyAndContract(companyCode, contractCode);
		// System.out.println("patParm="+patParm);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
    }
    /**
     * 合同信息选择事件
     */
    public void onContractChoose(){
    	companyCode=this.getValueString("COMPANY_CODE");
    	contractCode=this.getValueString("CONTRACT_CODE");
    	//根据选择的合同代码，构造并初始化该合同的病患信息TTextFormat
		TParm patParm=adm.getTParmByCompanyAndContract(companyCode, contractCode);
		// System.out.println("patParm="+patParm);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
		TParm parm=getCompanyTParm();
		if(parm==null){
			return;
		}
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
    }
    /**
     * 根据团体代码和合同代码查到该合同中所有CASE_NO，再循环所有CASE将数据合并
     * @return
     */
    public TParm getCompanyTParm(){
    	TParm result=new TParm();
    	if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)){
    		this.messageBox_("param is null");
    		return null;
    	}
    	if(!StringUtil.isNullString(this.getValueString("MR_NO"))||!StringUtil.isNullString(this.getValueString("CASE_NO"))){
    		this.messageBox_("个人查询");
    		return null;
    	}
    	TParm caseParm=adm.getTParmByCompanyAndContract(companyCode,contractCode);
    	if(caseParm==null){
    		this.messageBox_("取得病患基本资料失败");
    		return null;
    	}
    	if(caseParm.getErrCode()!=0){
    		this.messageBox_("取得病患基本资料失败");
    		return null;
    	}
    	if(caseParm.getCount()<=0){
    		this.messageBox_("没有数据");
    		return null;
    	}
    	int count=caseParm.getCount();
    	TParm lisParmAll=new TParm();
    	TParm risParmAll=new TParm();
    	for(int i=0;i<count;i++){
    		String tempCase=caseParm.getValue("ID",i);
    		if(StringUtil.isNullString(tempCase)){
    			this.messageBox_("caseNo is null");
    			return null;
    		}
    		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
    		if(lisParm==null){
    			this.messageBox_("lisParm is null");
    			return null;
    		}
    		lisParmAll=addTParm(lisParmAll,lisParm,"LIS");
    		TParm risParm=order.getRisPositiveParmByCase(tempCase);
    		if(risParm==null){
    			this.messageBox_("risParm is null");
    			return null;
    		}
    		risParmAll=addTParm(risParmAll,risParm,"RIS");
    	}
        lisParmAll.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        lisParmAll.addData("SYSTEM", "COLUMNS", "MR_NO");
        lisParmAll.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
        lisParmAll.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
        lisParmAll.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
        lisParmAll.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
        lisParmAll.setCount(lisParmAll.getCount("MR_NO"));

        risParmAll.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        risParmAll.addData("SYSTEM", "COLUMNS", "MR_NO");
        risParmAll.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        risParmAll.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
        risParmAll.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
        risParmAll.setCount(risParmAll.getCount("MR_NO"));

    	TTextFormat company=(TTextFormat)this.getComponent("COMPANY_CODE");
    	TTextFormat contract=(TTextFormat)this.getComponent("CONTRACT_CODE");
    	result.setData("NAME","TEXT","团体名称："+company.getTableShowValue("NAME"));
    	result.setData("CONTRACT_NAME","TEXT","合同名称："+contract.getTableShowValue("NAME"));
    	result.setData("STATUS1","TEXT","阳性报告个数："+getRepCount(lisParmAll,risParmAll));
    	result.setData("STATUS2","TEXT","总人数："+caseParm.getCount());
    	result.setData("PRINT_TIME","TEXT","打印时间："+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
    	result.setData("PRINT_USER","TEXT","制表人:"+Operator.getName());
    	result.setData("LIS_TABLE",lisParmAll.getData());
    	result.setData("RIS_TABLE",risParmAll.getData());
        creatExecleData(result);
    	return result;
    }
    /**
     * 组织打印EXECLE数据
     * @param parm TParm
     */
    public void creatExecleData(TParm parm){
        TParm table1 = parm.getParm("LIS_TABLE");
        table1.setData("TITLE","检验阳性报告");
        table1.setData("HEAD","病患姓名,120;病案号,120;医嘱名称,200;检验项目,120;检验值,100;单位,120;下限值,100;上限值,100");
        TParm table2 = parm.getParm("RIS_TABLE");
        table2.setData("TITLE","检查阳性报告");
        table2.setData("HEAD","病患姓名,120;病案号,120;医嘱名称,200;检查描述,200;检查结论,200");
        this.execleTable = new TParm[]{table1,table2};
    }
    /**
     * 得到异常报告数
     * @param lisParm TParm
     * @param risParm TParm
     * @return int
     */
    public int getRepCount(TParm lisParm,TParm risParm){
        Set countRep = new HashSet();
        int rowLisCount = lisParm.getCount("MR_NO");
        int rowRisCount = risParm.getCount("MR_NO");
        for(int i=0;i<rowLisCount;i++){
            countRep.add(lisParm.getValue("PAT_NAME",i));
        }
        for(int i=0;i<rowRisCount;i++){
            countRep.add(risParm.getValue("PAT_NAME",i));
        }
        return countRep.size();
    }
    /**
     * 根据LIS,还是RIS ,累加TParm
     * @param main
     * @param temp
     * @param cat1Type
     * @return
     */
    public TParm addTParm(TParm main,TParm temp,String cat1Type){
    	String names="";
    	if("LIS".equalsIgnoreCase(cat1Type)){
//                names="PAT_NAME;MR_NO;ORDER_DESC;TESTITEM_CHN_DESC;TEST_VALUE;TEST_UNIT;LOWER_LIMIT;UPPE_LIMIT;CASE_NO;SEX_CODE;BIRTH_DATE;ORDER_CODE";
                names="PAT_NAME;MR_NO;ORDER_DESC;TESTITEM_CHN_DESC;TEST_VALUE;TEST_UNIT;LOWER_LIMIT;UPPE_LIMIT";
    	}else{
    		names="PAT_NAME;MR_NO;ORDER_DESC;OUTCOME_DESCRIBE;OUTCOME_CONCLUSION ";
    	}
    	int count=temp.getCount();
    	if(count<=0){
    		return main;
    	}
    	for(int i=0;i<count;i++){
    		main.addRowData(temp, i, names);
//                main.addData("SYSTEM", "COLUMNS", names);
    	}
//        if("LIS".equalsIgnoreCase(cat1Type)){
//            main.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//            main.addData("SYSTEM", "COLUMNS", "MR_NO");
//            main.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            main.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//            main.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//            main.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//            main.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//            main.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//        }else{
//            main.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//            main.addData("SYSTEM", "COLUMNS", "MR_NO");
//            main.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            main.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//            main.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//        }
//        main.setCount(main.getCount("MR_NO"));
    	return main;
    }
	/**
     * 初始化控件
     */
	private void initComponent() {
		word=(TWord)this.getComponent("WORD");
		contract=(TTextFormat)this.getComponent("CONTRACT_CODE");
		patName=(TTextFormat)this.getComponent("CASE_NO");
		company = (TTextFormat) this.getComponent("COMPANY_CODE");
	}
	/**
	 * 根据病案号查询
	 */
	public void onMrNo(String mrNo){
		 mrNo=StringUtil.isNullString(mrNo) ? this.getValueString("MR_NO"): mrNo;
		if(StringUtil.isNullString(mrNo)){
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
		
		 // modify by huangtt 20160929 EMPI患者查重提示 start
        Pat pat1 = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat1.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat1.getMrNo());
			mrNo = pat1.getMrNo();
			this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
		
		
		TParm same = pat.getCaseByMr(mrNo);
		
		// add by wangb 2016/06/27 根据登录角色类型过滤数据 START
        String filter = "";
        if (!this.getPopedem("SYSDBA")) {
        	filter = this.getPopedemParm().getValue("ID");
        	int count = same.getCount();
        	for (int i = count - 1; i > -1; i--) {
				if (!filter.contains(same.getValue("ROLE_TYPE",
						i))) {
					same.removeRow(i);
				}
        	}
        }
     	// add by wangb 2016/06/27 根据登录角色类型过滤数据 END
        
		String tempCase="";
		if (same.getCount() > 0) {
			TParm patParm = new TParm();
			Object obj = (TParm) openDialog(
					"%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			if (obj != null) {
				patParm = (TParm) obj;
				tempCase=patParm.getValue("CASE_NO");
			}
		}
		if(StringUtil.isNullString( tempCase)){
			this.messageBox_("取得病患数据失败");
			return;
		}
		TParm parm=new TParm();
		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
		// System.out.println("lisParm==="+lisParm);
		if(lisParm==null){
			this.messageBox_("取得检验数据失败");
			return;
		}
		TParm risParm=order.getRisPositiveParmByCase(tempCase);
		if(risParm==null){
			this.messageBox_("取得检验数据失败");
			return;
		}
//                lisParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                lisParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                lisParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//                lisParm.setCount(lisParm.getCount("MR_NO"));
//
//                risParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                risParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                risParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//                risParm.setCount(risParm.getCount("MR_NO"));
		String patName=same.getValue("PAT_NAME",0);
		String sexCode=same.getValue("SEX_CODE",0);
		String birthday=StringTool.getString(same.getTimestamp("BIRTH_DATE",0),"yyyy/MM/dd");
		parm.setData("NAME","TEXT","姓名："+patName);
		parm.setData("CONTRACT_NAME","TEXT","病案号："+mrNo);
		parm.setData("STATUS1","TEXT","性别："+StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", " GROUP_ID='SYS_SEX' AND ID='" +sexCode+"'"));
		parm.setData("STATUS2","TEXT","出生日期："+birthday);
		parm.setData("PRINT_TIME","TEXT","打印时间："+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
		parm.setData("PRINT_USER","TEXT","制表人:"+Operator.getName());
		parm.setData("LIS_TABLE",lisParm.getData());
		parm.setData("RIS_TABLE",risParm.getData());
                creatExecleData(parm);
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
	}
	/**
	 * 团体员工信息
	 */
	public void onCaseNo(){
		String tempCase=this.getValueString("CASE_NO");
		TTextFormat contract=(TTextFormat)this.getComponent("CASE_NO");
		onMrNo(tempCase);
		if(1==1){
			return ;
		}
		if(StringUtil.isNullString( tempCase)){
			this.messageBox_("取得病患数据失败");
			return;
		}
		TParm parm=new TParm();
		TParm lisParm=order.getLisPositiveParmByCase(tempCase);
		// System.out.println("lisParm="+lisParm);
		if(lisParm==null){
			this.messageBox_("取得检验数据失败");
			return;
		}
		TParm risParm=order.getRisPositiveParmByCase(tempCase);
                // System.out.println("risParm=="+risParm);
                // System.out.println("risParm="+risParm);
		if(risParm==null){
			this.messageBox_("取得检验数据失败");
			return;
		}
//                lisParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                lisParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                lisParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
//                lisParm.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "LOWER_LIMIT");
//                lisParm.addData("SYSTEM", "COLUMNS", "UPPE_LIMIT");
//                lisParm.setCount(lisParm.getCount("MR_NO"));
//
//                risParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                risParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                risParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_DESCRIBE");
//                risParm.addData("SYSTEM", "COLUMNS", "OUTCOME_CONCLUSION");
//                risParm.setCount(risParm.getCount("MR_NO"));

		String patName=lisParm.getValue("PAT_NAME",0);
		String sexCode=lisParm.getValue("SEX_CODE",0);
		String birthday=StringTool.getString(lisParm.getTimestamp("BIRTH_DATE",0),"yyyy/MM/dd");
		parm.setData("NAME","TEXT","姓名1："+patName);
		parm.setData("CONTRACT_NAME","TEXT","病案号："+lisParm.getValue("MR_NO",0));
		parm.setData("STATUS1","TEXT","性别："+StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", " GROUP_ID='SYS_SEX' AND ID='" +sexCode+"'"));
		parm.setData("STATUS2","TEXT","出生日期："+birthday);
		parm.setData("LIS_TABLE",lisParm.getData());
		parm.setData("RIS_TABLE",risParm.getData());
		parm.setData("PRINT_TIME","TEXT","打印时间："+StringTool.getString(order.getDBTime(),"yyyy/MM/dd HH:mm"));
		parm.setData("PRINT_USER","TEXT","制表人:"+Operator.getName());
                creatExecleData(parm);
		word.setWordParameter(parm);
		word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\HRM\\HRMPositiveReport.jhw");
	}
	/**
	 * 打印
	 */
	public void onPrint(){
		if(StringUtil.isNullString(word.getFileName())){
			return;
		}
		word.onPreviewWord();
		word.print();
	}
        /**
         * 导出EXECLE
         */
        public void onExecl(){
            if(this.execleTable==null||this.execleTable.length==0){
                this.messageBox("没有需要导出的数据!");
                return;
            }
            ExportExcelUtil.getInstance().exeSaveExcel(this.execleTable,"健康检查阴阳性报告");
        }
}
