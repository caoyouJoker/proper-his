package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 医疗卡交易记录</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.9.16
 * @version 1.0
 */
public class EKTTredeOldControl
    extends TControl {

    private TTable table;
    
    //zhangp 20120130 是否读卡
    private boolean readCardFlg = false;

    public EKTTredeOldControl() {
        super();
    }

    //========================chenhj 2017.3.28 start
    /**
     * 初始化方法
     * chenhj
     * 2017.3.28
     */
    public void onInit() {
          table = getTable("TABLE");
          TParm parm=this.onInitOld();
          if("".equals(getValue("USER_ID"))&&"".equals(getValue("MR_NO"))&&"".equals(getValue("CASE_NO"))
        		  &&"".equals(getValue("CARD_NO"))&&"".equals(getValue("PAT_NAME"))){
        	  return;
          }
          onQuery();
    }
    /**
     * 从原界面中获取的值传入当前界面
     * @return parm
     */
    public TParm onInitOld() {

  	  TParm parm=(TParm)this.getParameter();
  	  //开始时间
  	  String startDate =parm.getData("START_DATE").toString();
  	  startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
  	  setValue("START_DATE",startDate);
  	  //结束时间
  	  String endDate =parm.getData("END_DATE").toString();
  	  endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+" "+ 
  			    endDate.substring(11,13)+":"+endDate.substring(14,16)+":"+endDate.substring(17,19);
  	  setValue("END_DATE",endDate);
  	  //收费人员
  	  setValue("USER_ID",parm.getData("USER_ID"));
  	  //病案号
  	  setValue("MR_NO",parm.getData("MR_NO"));
  	  //就诊序号
  	  setValue("CASE_NO",parm.getData("CASE_NO"));
  	  //医疗卡号
  	  setValue("CARD_NO",parm.getData("CARD_NO"));
  	  //病患姓名
  	  setValue("PAT_NAME",parm.getData("PAT_NAME"));
  	  //内部交易号
  	  setValue("BUSINESS_NO",parm.getData("BUSINESS_NO"));
  	  return parm;
    }
    
    //========================chenhj 2017.3.28 end 
    

    /**
     * 查询方法
     */
    public void onQuery() {
        String sql = getSQL();
        System.out.println("sql---" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("BUSINESS_NO") <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * 取得查询的SQL语句
     * @return String
     */
    private String getSQL() {
        String where1 = "";
        String where2 = "";
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            String start_date = this.getValueString("START_DATE").substring(0, 19);
            start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
                start_date.substring(8, 10) + start_date.substring(11, 13) +
                start_date.substring(14, 16) + start_date.substring(17, 19);
            String end_date = this.getValueString("END_DATE").substring(0, 19);
            end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
                end_date.substring(8, 10) + end_date.substring(11, 13) +
                end_date.substring(14, 16) + end_date.substring(17, 19);
            where1 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
                "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS')";
            where2 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
            "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS')";
        }
        if (!"".equals(this.getValueString("USER_ID"))) {
            where1 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
            where2 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
        }
//        if (!"".equals(this.getValueString("TREDE_NO"))) {
//            where += " AND A.TREDE_NO = '" + getValueString("TREDE_NO") + "'";
//        }
        if (!"".equals(this.getValueString("CARD_NO"))) {
            where1 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
            where2 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            where1 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
            where2 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
        }
        if (!"".equals(this.getValueString("CASE_NO"))) {
            where1 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
            where2 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_NO"))) {
            where1 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
                "'";
            where2 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
            "'";
        }
        if (!"".equals(this.getValueString("STATE"))) {
            where1 += " AND A.STATE = '" + getValueString("STATE") + "'";
            where2 += " AND A.CHARGE_FLG = '" + getValueString("STATE") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_TYPE"))) {
            where1 += " AND A.BUSINESS_TYPE = '" +
                getValueString("BUSINESS_TYPE") + "'";
//            where2 += " AND C.BUSINESS_TYPE = '" +
//            	getValueString("BUSINESS_TYPE") + "'";
        }
        if (!"".equals(this.getValueString("PAT_NAME"))) {
        	where1 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        	where2 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        }
        return 
        " SELECT   A.MR_NO, A.CARD_NO, A.CASE_NO, A.TRADE_NO BUSINESS_NO, B.PAT_NAME," +
        " A.OLD_AMT, A.AMT, " +
        " A.STATE, A.BUSINESS_TYPE, A.OPT_USER, A.OPT_DATE," +
        " A.OPT_TERM" +
        " FROM EKT_TRADE A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO" +
        where1 +
        " UNION ALL" +
        " SELECT   A.MR_NO, A.CARD_NO, A.CASE_NO, A.BUSINESS_NO, B.PAT_NAME," +
        " A.ORIGINAL_BALANCE, A.BUSINESS_AMT, " +
        " CASE" +
        " WHEN A.CHARGE_FLG = '3'" +
        " THEN '0'" +
        " ELSE A.CHARGE_FLG" +
        " END STATE, '', A.OPT_USER," +
        " A.OPT_DATE, A.OPT_TERM" +
        " FROM EKT_ACCNTDETAIL A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO AND A.CHARGE_FLG IN ('3', '4', '5', '7', '8')" +
        where2 +
        " ORDER BY OPT_DATE";
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
        String clear =
            "START_DATE;END_DATE;USER_ID;TREDE_NO;CARD_NO;MR_NO;CASE_NO;BUSINESS_NO;STATE;BUSINESS_TYPE;PAT_NAME";
        this.clearValue(clear);
        table.removeRowAll();
        //zhangp 20120130
        readCardFlg = false;
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(true);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(true);
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "医疗卡交易记录表");
    }

    /**
     * 病案号回车事件
     */
    public void onMrNoAction() {
		String mrNo = ""+getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		// modify by huangtt 20160930 EMPI患者查重提示 start
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
		}
		// modify by huangtt 20160930 EMPI患者查重提示 end 
		mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", pat.getName());
    }

    /**
     * 读医疗卡
     */
    public void onCardNoAction() {
    	//zhangp 20111230
//        TParm parm = EKTIO.getInstance().getPat();
    	TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("此卡无效");
            return;
        }
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
    	String cardNo = parm.getValue("MR_NO")+parm.getValue("SEQ");
        this.setValue("CARD_NO", cardNo);
        //zhangp 20120130 加管控，不读卡不能打印
        readCardFlg = true;
        //zhangp 20120130 加病案号
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        //zhangp 20120130 
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(false);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(false);
        //===zhangp 20120319 start
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        setValue("PAT_NAME", pat.getName());
        //===zhangp 20120319 end
        //zhangp 20120131
        onQuery();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
     * 打印
     * zhangp 20120129
     */
    public void onPrint(){
    	//zhangp 20120130 加读卡验证
    	if(!readCardFlg){
    		messageBox("请读取医疗卡");
    		return;
    	}
    	String cardno = getValueString("CARD_NO");
    	String mrno = getValueString("MR_NO");
    	TParm result = getParm(mrno,cardno);
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	//=============modify by lim 2012/02/24 begin
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTrede.jhw",result);
    	//=============modify by lim 2012/02/24 end
    }
    /**
     * 获取打印数据
     * zhangp 20120129
     * @param mrno
     * @param cardno
     * @return
     */
    public TParm getParm(String mrno,String cardno){
    	Pat pat = Pat.onQueryByMrNo(mrno);
    	//得到病患年龄
        String[] AGE =  StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
    	String sql =
    		"SELECT A.ISSUE_DATE,A.CARD_NO,B.USER_NAME" +
    		" FROM EKT_ISSUELOG A,SYS_OPERATOR B " +
    		" WHERE A.CARD_NO = '"+cardno+"' AND A.OPT_USER = B.USER_ID ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		TParm data = new TParm();// 打印的数据
		TParm parm = new TParm();// 表格数据
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("MR_NO", "TEXT", "病案号: "+mrno);
		data.setData("SEX", "TEXT", "性别: "+pat.getSexString());
		data.setData("NAME", "TEXT", "姓名: "+pat.getName());
		data.setData("AGE", "TEXT", "年龄: "+AGE[0]+"岁"+AGE[1]+"个月"+AGE[2]+"天");
		data.setData("IDNO", "TEXT", "身份证号: "+pat.getIdNo());
		data.setData("COMPANY", "TEXT", "单位名称: "+pat.getCompanyDesc());
		data.setData("COMPANYCALL", "TEXT", "单位电话: "+pat.getTelCompany());
		data.setData("CELLPHONE", "TEXT", "电话: "+pat.getCellPhone());
		data.setData("ISSUE_DATE", "TEXT", "售卡日期: "+result.getData("ISSUE_DATE", 0));
		data.setData("USER_NAME", "TEXT", "售卡人员: "+result.getData("USER_NAME", 0));
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINT_DATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		sql = 
			"SELECT A.BUSINESS_DATE,A.CHARGE_FLG,B.GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A,EKT_BIL_PAY B WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (3,4,5,7) AND A.BUSINESS_NO = B.BIL_BUSINESS_NO "+
            " UNION "+
            " SELECT A.BUSINESS_DATE,A.CHARGE_FLG,'' AS GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (1,2)";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		double businessAmt = 0.0;
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getInt("CHARGE_FLG", i)==1){
				result.setData("CHARGE_FLG", i, "扣款");
			}
			if(result.getInt("CHARGE_FLG", i)==2){
				result.setData("CHARGE_FLG", i, "退款");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("CHARGE_FLG", i)==3){
				result.setData("CHARGE_FLG", i, "医疗卡充值");
			}
			if(result.getInt("CHARGE_FLG", i)==4){
				result.setData("CHARGE_FLG", i, "制卡");
			}
			if(result.getInt("CHARGE_FLG", i)==5){
				result.setData("CHARGE_FLG", i, "补卡");
			}
			if(result.getInt("CHARGE_FLG", i)==7){
				result.setData("CHARGE_FLG", i, "退费");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("ACCNT_STATUS",i)==1){
				result.setData("ACCNT_STATUS", i, "未对账");
			}
			if(result.getInt("ACCNT_STATUS",i)==2){
				result.setData("ACCNT_STATUS", i, "已对账");
			}
			if(result.getData("GATHER_TYPE", i).equals("01")){
				result.setData("GATHER_TYPE", i, "现金");
			}
		}
		data.setData("TABLE", result.getData());
    	return data;
    }


}
