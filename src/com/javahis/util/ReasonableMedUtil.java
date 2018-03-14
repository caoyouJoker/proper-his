package com.javahis.util;

import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;

public class ReasonableMedUtil {
	public static final String NULL="";
	public static final String GET_SEX_DESC=" SELECT SEX_DESC FROM SYS_SEX A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND B.MR_NO=";
	public static final String GET_FREQ="SELECT CYCLE,FREQ_TIMES FROM SYS_PHAFREQ ";
	public static final String GET_UNIT="SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT ";
	public static final String GET_ROUTE="SELECT ROUTE_CODE,ROUTE_CHN_DESC FROM SYS_PHAROUTE";
	public  TDS tdsFreq,tdsUnit,tdsRoute;
	 /**
	   * 传入病人基本信息
	   * 调用：病人的基本信息发生变化之后，调用该接口。
	   * @param PatientID String 病人病案编号（必须传值）
	   * @param VisitID String 当前就诊次数（必须传值）
	   * @param Name String 病人姓名 （必须传值）
	   * @param Sex String 病人性别 （必须传值）如：男、女，其他值传：未知。
	   * @param Birthday String 出生日期 （必须传值）格式：2005-09-20
	   * @param Weight String 体重 （可以不传值）单位：KG
	   * @param Height String 身高 （可以不传值）单位：CM
	   * @param DepartmentName String 医嘱科室ID/医嘱科室名称 （可以不传值）
	   * @param Doctor String 主治医生ID/主治医生姓名 （可以不传值）
	   * @param LeaveHospitalDate String 出院日期 （可以不传值）
	   * @return int 暂无意义
	   */
	public static String[] getPatInfo(ODO odo){
		if(odo==null)
			return null;
		String[] result=new String[10];
		result[0]=odo.getMrNo();
		result[1]="1";
		result[2]=odo.getPatInfo().getItemString(0, "PAT_NAME");
		TParm parmSex=new TParm(TJDODBTool.getInstance().select(GET_SEX_DESC+"'" +odo.getMrNo()+
				"'"));
		result[3]=parmSex.getValue("SEX_DESC");
		result[4]=TCM_Transform.getString(odo.getPatInfo().getItemData(0, "BIRTH_DATE"), "yyyy-MM-dd");
		result[5]=NULL;
		result[6]=NULL;
		result[7]=NULL;
		result[8]=NULL;
		result[9]=NULL;
		return result;
	}
	  /**
	   * 传入病人用药信息
	   * 调用：当需要进行用药医嘱审查时，调用该接口。
	   * 注意事项：如果当前病人有多条用药医嘱时，循环传入。传入的医嘱为用药医嘱，
	   * 对于工作站类型为10即时性审查时(如：住院医生站或门诊医生站)，
	   * 用药结束日期可以不用传值，默认为当天；而对于工作站类型为20回顾性审查时(如：临床药学工作或查询统计)，
	   * 用药结束日期必须传值。
	   * @param OrderUniqueCode String 医嘱唯一码（必须传值） 0
	   * @param DrugCode String 药品编码 （必须传值） 1
	   * @param DrugName String 药品名称 （必须传值） 2
	   * @param SingleDose String 每次用量 （必须传值） 3
	   * @param DoseUnit String 剂量单位 （必须传值） 4
	   * @param Frequency String 用药频率(次/天)（必须传值） 5
	   * @param StartOrderDate String 用药开始日期，格式：yyyy-mm-dd （必须传值） 6
	   * @param StopOrderDate String 用药结束日期，格式：yyyy-mm-dd （可以不传值），默认值为当天 7
	   * @param RouteName String 给药途径中文名称 （必须传值） 8
	   * @param GroupTag String 成组医嘱标志 （必须传值） 9
	   * @param OrderType String 是否为临时医嘱 1-是临时医嘱 0或空 长期医嘱 （必须传值） 10
	   * @param OrderDoctor String 下嘱医生ID/下嘱医生姓名 （必须传值） 11
	   * @return int 暂无意义
	   */
	public static String[] getOrder(OpdOrder order,int row){
		String[] result=new String[12];
		if(order==null||order.rowCount()<1){
			return null;
		}
		result[0]=order.getItemString(row, "RX_NO")+order.getItemString(row, "ORDER_CODE");
		result[1]=order.getItemString(row, "ORDER_CODE");
		result[2]=order.getItemString(row, "ORDER_DESC");
		result[3]=order.getItemDouble(row, "DOSAGE_QTY")+"";
		result[4]=order.getItemString(row, "MEDI_UNIT");
//		result[5]=order.getItemString(row, "")
		return result;
	}
	/**
	 * 取得频次资料
	 * @return
	 */
	public  void getFreq_Unit_Route(){
		if(tdsFreq==null){
			tdsFreq=new TDS();
			tdsFreq.setSQL(GET_FREQ);
			tdsFreq.retrieve();
		}
		if(tdsUnit==null){
			tdsUnit=new TDS();
			tdsUnit.setSQL(GET_UNIT);
			tdsUnit.retrieve();
		}
		if(tdsRoute==null){
			tdsRoute=new TDS();
			tdsRoute.setSQL(GET_ROUTE);
			tdsRoute.retrieve();
		}
		return ;
	}
	/**
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	public  String[] getOrderInfo(OpdOrder order,int row){
		if(order==null||row<0)
			return null;
		if(tdsFreq==null||tdsUnit==null||tdsRoute==null)
			getFreq_Unit_Route();
		  /**
		   * 传入病人用药信息
		   * 调用：当需要进行用药医嘱审查时，调用该接口。
		   * 注意事项：如果当前病人有多条用药医嘱时，循环传入。传入的医嘱为用药医嘱，
		   * 对于工作站类型为10即时性审查时(如：住院医生站或门诊医生站)，
		   * 用药结束日期可以不用传值，默认为当天；而对于工作站类型为20回顾性审查时(如：临床药学工作或查询统计)，
		   * 用药结束日期必须传值。
		   * @param OrderUniqueCode String 医嘱唯一码（必须传值） 0
		   * @param DrugCode String 药品编码 （必须传值） 1
		   * @param DrugName String 药品名称 （必须传值） 2
		   * @param SingleDose String 每次用量 （必须传值） 3
		   * @param DoseUnit String 剂量单位 （必须传值） 4
		   * @param Frequency String 用药频率(次/天)（必须传值） 5
		   * @param StartOrderDate String 用药开始日期，格式：yyyy-mm-dd （必须传值） 6
		   * @param StopOrderDate String 用药结束日期，格式：yyyy-mm-dd （可以不传值），默认值为当天 7
		   * @param RouteName String 给药途径中文名称 （必须传值） 8
		   * @param GroupTag String 成组医嘱标志 （必须传值） 9
		   * @param OrderType String 是否为临时医嘱 1-是临时医嘱 0或空 长期医嘱 （必须传值） 10
		   * @param OrderDoctor String 下嘱医生ID/下嘱医生姓名 （必须传值） 11
		   */
		String[] result=new String[12];
		//药品唯一码：RX_NO+FILL0(SEQ,3)+ORDER_CODE
		result[0]=order.getItemString(row, "RX_NO")+StringTool.fill0(order.getItemInt(row, "SEQ_NO")+"", 3)+order.getItemString(row, "ORDER_CODE");
		result[1]=order.getItemString(row, "ORDER_CODE");
		result[2]=order.getItemString(row, "ORDER_DESC");
		result[3]=order.getItemData(row, "MEDI_QTY")+"";
		tdsUnit.setFilter("UNIT_CODE='" +order.getItemString(row, "MEDI_UNIT")+
				"'");
		tdsUnit.filter();
		result[4]=tdsUnit.getItemString(0, "UNIT_CHN_DESC");
		tdsFreq.setFilter("FREQ_CODE='" +order.getItemString(row, "FREQ_CODE")+
				"'");
		tdsFreq.filter();
		result[5]=tdsFreq.getItemInt(0, "FREQ_TIMES")+"/"+tdsFreq.getItemInt(0, "CYCLE");
		String date=StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy-MM-dd");
		result[6]=date;
		result[7]=date;
		tdsRoute.setFilter("ROUTE_CODE='" +order.getItemString(row, "ROUTE_CODE")+
				"'");
		tdsRoute.filter();
		result[8]=tdsRoute.getItemString(0,"ROUTE_CHN_DESC");
		result[9]="1";
		result[10]="1";
		result[11]=Operator.getName();
		return result;
	}
}
