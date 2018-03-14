package jdo.sys.ws;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import com.caigen.sql.df;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.thoughtworks.xstream.XStream;

import jdo.bil.BILDailyFeeQueryTool;
import jdo.bil.BILPayTool;
import jdo.reg.REGTool;
import jdo.reg.services.BodyParams;
import jdo.reg.services.RegRequest;
import jdo.reg.services.RegResponse;
import jdo.reg.services.Result;
import jdo.reg.ws.RegQETool;
import jdo.sys.PatTool;
import jdo.sys.SYSDictionaryServiceTool;
/**
 * 
 * @author lixiang
 *
 */
@WebService
public class DictionaryServiceImpl implements IDictionaryService {

	/**
	 * 得到版本
	 * 
	 * @return String
	 */
	public String getVersion() {
		return "BlueCore DictionaryService version is 1.0.1";
	}

	@Override
	public String[] getDeptInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getDeptInf(code, password);
	}

	@Override
	public String confirmedInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().confirmedInf(code,
				password, tableName, index);
	}

	@Override
	public String deleteInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().deleteInf(code, password,
				tableName, index);
	}

	@Override
	public String fetchInf(String code, String password, String tableName,
			String index) {
		return SYSDictionaryServiceTool.getInstance().fetchInf(code, password,
				tableName, index);
	}

	@Override
	public String[] getAdmType(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getAdmType(code, password);
	}

	@Override
	public String[] getClinicArea(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinicArea(code,
				password);
	}

	@Override
	public String[] getClinicRoom(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getClinicRoom(code,
				password);
	}

	@Override
	public String[] getCtzInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getCtzInf(code, password);
	}

	@Override
	public String[] getDeptClassRule(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getDeptClassRule(code,
				password);
	}

	@Override
	public String[] getDeptInfCode(String code, String password, String deptcode) {
		return SYSDictionaryServiceTool.getInstance().getDeptInfCode(code,
				password, deptcode);
	}

	@Override
	public String[] getDeptInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getDeptInfSY(code,
				password);
	}

	@Override
	public String[] getDiagnosisInf(String code, String password,
			String classify, String type) {
		return SYSDictionaryServiceTool.getInstance().getDiagnosisInf(code,
				password, classify, type);
	}

	@Override
	public String getDictionary(String groupId, String id) {
		return SYSDictionaryServiceTool.getInstance()
				.getDictionary(groupId, id);
	}

	@Override
	public String[] getHisCancelOrder(String code, String password,
			String caseno, String rxno, String seqno) {
		return SYSDictionaryServiceTool.getInstance().getHisCancelOrder(code,
				password, caseno, rxno, seqno);
	}

	@Override
	public String[] getIndMaterialloc(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getIndMaterialloc(code,
				password);
	}

	@Override
	public String[] getIndMateriallocOrder(String code, String password,
			String orgcode, String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getIndMateriallocOrder(
				code, password, orgcode, ordercode);
	}

	@Override
	public String[] getLisOrder(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getLisOrder(code,
				password);
	}

	@Override
	public String[] getModifyInf(String code, String password, String status,
			String tableName) {
		return SYSDictionaryServiceTool.getInstance().getModifyInf(code,
				password, status, tableName);
	}

	@Override
	public String[] getModifyTable(String code, String password, String status) {
		return SYSDictionaryServiceTool.getInstance().getModifyTable(code,
				password, status);
	}

	@Override
	public String[] getODIPhaOrderInfo(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getODIPhaOrderInfo(code,
				password);
	}

	@Override
	public String[] getODIPhaOrderInfoItem(String code, String password,
			String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getODIPhaOrderInfoItem(
				code, password, ordercode);
	}

	@Override
	public String[] getOperatorInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getOperatorInf(code,
				password);
	}

	@Override
	public String[] getOperatorInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getOperatorInfSY(code,
				password);
	}

	@Override
	public String[] getOrderSY(String code, String password, String rxno) {
		return SYSDictionaryServiceTool.getInstance().getOrderSY(code,
				password, rxno);
	}

	@Override
	public String[] getPatAppRegInfo(String code, String password, String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getPatAppRegInfo(code,
				password, mrNo);
	}

	@Override
	public String[] getPatForSID(String code, String password, String SID,
			String name) {
		return SYSDictionaryServiceTool.getInstance().getPatForSID(code,
				password, SID, name);
	}

	@Override
	public String[] getPatInfAndOrder(String code, String password, String rxno) {
		return SYSDictionaryServiceTool.getInstance().getPatInfAndOrder(code,
				password, rxno);
	}

	@Override
	public String[] getPhaClassify(String code, String password, String name) {
		return SYSDictionaryServiceTool.getInstance().getPhaClassify(code,
				password, name);
	}

	@Override
	public String[] getPhaEsyOrder(String code, String password,
			String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getPhaEsyOrder(code,
				password, ordercode);
	}

	@Override
	public String[] getPhaFreqInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getPhaFreqInf(code,
				password);
	}

	@Override
	public String[] getPhaFreqInfSY(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getPhaFreqInfSY(code,
				password);
	}

	@Override
	public String[] getPhaInf(String code, String password, String classify) {
		return SYSDictionaryServiceTool.getInstance().getPhaInf(code, password,
				classify);
	}

	@Override
	public String[] getPhaOrder(String code, String password, String ordercode) {
		return SYSDictionaryServiceTool.getInstance().getPhaOrder(code,
				password, ordercode);
	}

	@Override
	public String[] getPhaOrderPY1(String code, String password, String py1,
			int startrow, int endrow) {
		return SYSDictionaryServiceTool.getInstance().getPhaOrderPY1(code,
				password, py1, startrow, endrow);
	}

	@Override
	public String[] getRegSchDay(String code, String password, String startDate, String endDate) {
		return SYSDictionaryServiceTool.getInstance().getRegSchDay(code,
				password, startDate, endDate);
	}

	@Override
	public String[] getRegWorkList(String code, String password,
			String admtype, String startdate, String enddate) {
		return SYSDictionaryServiceTool.getInstance().getRegWorkList(code,
				password, admtype, startdate, enddate);
	}

	@Override
	public String[] getRegionJD(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRegionJD(code,
				password);
	}

	@Override
	public String getRegistStatus(String code) {
		return SYSDictionaryServiceTool.getInstance().getRegistStatus(code);
	}

	@Override
	public String[] getRegistTableInf(String code, String password,
			String tableName) {
		return SYSDictionaryServiceTool.getInstance().getRegistTableInf(code,
				password, tableName);
	}

	@Override
	public String[] getReulInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getReulInf(code, password);
	}

	@Override
	public String[] getRouteInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRouteInf(code,
				password);
	}

	@Override
	public String[] getSessionInf(String code, String password, String admType) {
		return SYSDictionaryServiceTool.getInstance().getSessionInf(code,
				password, admType);
	}

	@Override
	public String[] getSexInf(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSexInf(code, password);
	}

	@Override
	public String[] getShareTable() {
		return SYSDictionaryServiceTool.getInstance().getShareTable();
	}

	@Override
	public String[] getStation(String code, String password) {
		return SYSDictionaryServiceTool.getInstance()
				.getStation(code, password);
	}

	@Override
	public String[] getSysOperationICD(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysOperationICD(code,
				password);
	}

	@Override
	public String modifyPassword(String code, String oldPassword,
			String newPassword) {
		return SYSDictionaryServiceTool.getInstance().modifyPassword(code,
				oldPassword, newPassword);
	}

	@Override
	public String[] readOpdOrderPS(String code, String password, String caseno,
			String rxno, String seqno, String value) {
		return SYSDictionaryServiceTool.getInstance().readOpdOrderPS(code,
				password, caseno, rxno, seqno, value);
	}

    @Override
    public String regAppt(String code, String password, String mrNo, String patName,
                          String cellPhone, String date, String sessionCode, String admType,
                          String deptCode, String clinicRoomNo, String drCode, String regionCode,
                          String ctz1Code, String serviceLevel) {
        return SYSDictionaryServiceTool.getInstance().regAppt(code, password, mrNo, patName,
                                                              cellPhone, date, sessionCode,
                                                              admType, deptCode, clinicRoomNo,
                                                              drCode, regionCode, ctz1Code,
                                                              serviceLevel);
    }

	@Override
	public String regUnAppt(String code, String password, String caseNo) {
		return SYSDictionaryServiceTool.getInstance().regUnAppt(code, password,
				caseNo);
	}

	@Override
	public String regist(String code, String chnDesc, String engDesc,
			String contactsName, String tel, String email, String password) {
		return SYSDictionaryServiceTool.getInstance().regist(code, chnDesc,
				engDesc, contactsName, tel, email, password);
	}

	@Override
	public String registTable(String code, String password, String tableName,
			String action) {
		return SYSDictionaryServiceTool.getInstance().registTable(code,
				password, tableName, action);
	}

	@Override
	public String savePat(String code, String password, String name,
			String birthday, String sex, String SID, String tel, String address) {
		return SYSDictionaryServiceTool.getInstance().savePat(code, password,
				name, birthday, sex, SID, tel, address);
	}

	@Override
	public String[] getSysFee(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysFee(code, password);
	}

	@Override
	public String[] getSysFeeHistory(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getSysFeeHistory(code, password);
	}

	@Override
	public String[] getData(String code, String password, String tableName) {
		return SYSDictionaryServiceTool.getInstance().getData(code, password, tableName);
	}

	@Override
	public String[] getPatByMrNo(String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getPatByMrNo(mrNo);

	}
	
	@Override
	public String[] getPatInfo(String code, String password,String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getPatByMrNo(code,password,mrNo);

	}

	@Override
	public String[] getOrderByCatType(String code, String password,
			String catType) {
		return SYSDictionaryServiceTool.getInstance().getOrderByCatType(code, password,
				catType) ;
	}

	@Override
	public String[] getBLDInfo(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getBLDInfo(code, password) ;
	}

	@Override
	public String[] getBeds(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getBeds(code, password);
	}

	@Override
	public String[] getRooms(String code, String password) {
		// TODO Auto-generated method stub
		return SYSDictionaryServiceTool.getInstance().getRooms(code, password);
	}

    @Override
    public String[] getDeptStationList(String code, String password) {
        // TODO Auto-generated method stub
        return SYSDictionaryServiceTool.getInstance().getDeptStationList(code, password);
    }

    @Override
    public String[] getBloodType(String code, String password) {
        // TODO Auto-generated method stub
        return SYSDictionaryServiceTool.getInstance().getBloodType(code, password);
    }

    @Override
    public String[] getInPats(String code, String password, String stationCode) {
        // TODO Auto-generated method stub
        return  SYSDictionaryServiceTool.getInstance().getInPats(code, password, stationCode);
    }

    @Override
    public String[] getReadyOPs(String code, String password, String stationCode) {
        // TODO Auto-generated method stub
        return SYSDictionaryServiceTool.getInstance().getReadyOPs(code, password, stationCode);
    }

    @Override
    public String[] getRegCancelInfo(String code, String password, String startDate, String endDate) {
        // TODO Auto-generated method stub
        return SYSDictionaryServiceTool.getInstance().getRegCancelInfo(code, password, startDate, endDate);
    }

	@Override
	public String[] getRisOrder(String code, String password) {
		return SYSDictionaryServiceTool.getInstance().getRisOrder(code,
				password);
	}

	@Override
	public String[] getCheckDate(String code, String password, 
			String mrNo, String sDate, String eDate) {
		// TODO Auto-generated method stub
		return SYSDictionaryServiceTool.getInstance().getCheckDate(code, password, mrNo, sDate, eDate);
	}

	@Override
	public String[] getRisDate(String code, String password, String mrNo,
			String sDate, String eDate) {
		// TODO Auto-generated method stub
		return SYSDictionaryServiceTool.getInstance().getRisDate(code, password, mrNo, sDate, eDate);
	}

	@Override
	public String getMrNo(String code, String password, String patName,
			String sex, String birthDay, String idNo, String tel) {
		// TODO Auto-generated method stub
		return SYSDictionaryServiceTool.getInstance().getMrNo(code, password, patName, sex, birthDay, idNo, tel);
	}

	@Override
	public String bilDailyFee(String inXml) {
		//初始化输出xml
		String outXml="";
//		inXml="<Request><HeaderParams>"
//				+ "</HeaderParams><BodyParams>"
//				+ "<MrNo>000000581134</MrNo>"
//				+ "<StartDate>2017-02-13</StartDate>" 
//		        + "<PatientName>李XX芳</PatientName>"
//		        +"<EndDate>2017-04-24</EndDate></BodyParams></Request>"; 
		System.out.println("========"+inXml);
	    inXml = RegQETool.getInstance().updateXml(inXml);
	     
		XStream xStream = new XStream();
		
		xStream.alias("request", RegRequest.class);
		
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		//获取开始时间
		String sDate = bodyP.getStartDate().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		//获取结束时间
		String eDate = bodyP.getEndDate().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		
		String nDate=TJDODBTool.getInstance().getDBTime().toString();
		System.out.println(nDate);
		//获取姓名
		String name=bodyP.getPatientName();
		nDate=nDate.replaceAll("-","");
		//获得MR_NO
		String mrNo=bodyP.getMrNo().trim();
		String sql="SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE MR_NO="+mrNo;
		TParm check=new TParm(TJDODBTool.getInstance().select(sql));
		String cName=check.getValue("PAT_NAME",0);		
		if((!cName.equals(name))){
			res.setIsSuccessed("1");
			res.setErrorMsg("姓名/病案号有误");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
		//获得查询内容
		TParm parm=RegQETool.getInstance().bilDailyFee(mrNo, sDate, eDate);
		if((nDate.substring(0,8)).equals(sDate.substring(0, 8))||(nDate.substring(0,8)).equals(eDate.substring(0, 8))){
			res.setIsSuccessed("1");
			res.setErrorMsg("不能查询当日数据");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
		if(parm.getCount()<=0){
			res.setIsSuccessed("1");
			res.setErrorMsg("无数据");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
				xStream = new XStream();
				if(parm.getCount() > 0){
				res.setIsSuccessed("0"); 
				res.setErrorMsg("成功");

				//住院号
				res.setIpdNo(parm.getValue("IPD_NO",0).toString());
				//姓名
				res.setPatientName(parm.getValue("PAT_NAME",0).toString());
				//科室
				res.setDeptCode(parm.getValue("ADM_DEPT_DESC",0).toString());
				//病区
				res.setStation(parm.getValue("ADM_STATION_DESC",0).toString());
				//身份
				res.setCtzDesc(parm.getValue("CTZ_DESC",0).toString());
				//病床号
				res.setBed(parm.getValue("BED_NO",0).toString());
				//预约金总金额
				TParm yjParm=new TParm();
				yjParm.setData("CASE_NO",parm.getValue("CASE_NO",0));
				TParm yjj = BILPayTool.getInstance().selAllDataByRecpNo(yjParm);
				double atm = 0;
				for (int i = 0; i < yjj.getCount(); i++) {
		            atm += yjj.getDouble("PRE_AMT", i);
		        }
				res.setPrice(atm+"");
				List<Result> results = new ArrayList<Result>();
				double t=0;
				double tot=0;
				String totAmt=null;
				DecimalFormat df = new DecimalFormat("0.00");
				DecimalFormat df2 = new DecimalFormat("0.00");
				 DecimalFormat df1 = new DecimalFormat("0");
				for (int i = 0; i < parm.getCount(); i++) {
					Result result = new Result();
					//日期
					result.setBilDate(parm.getValue("BILL_DATE",i)); 
					//收据类型
					result.setFeeTypeDesc(parm.getValue("CHN_DESC",i));
					
					//项目名称/规格   #增负   ☆自费
					String mark = "";
					String sign="0";
					Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
		            if(addRate == 1){
		            	mark = "☆ ";
			            sign = "2";
		            } else if (addRate > 0.00&&addRate!=1){
		            	mark = "# ";
			            sign = "1";
		            }
		            	
		            result.setSignType(sign);
					if(!"".equals(parm.getValue("SPECIFICATION",i))){
						result.setOrderDesc(mark+parm.getValue("ORDER_DESC",i)+"/"+parm.getValue("SPECIFICATION",i).toString().replaceAll("\\\\", "\\\\\\\\")+ " "+ df1.format(parm.getDouble("ZFBL1", i) * 100) + "%");
					}else{
						result.setOrderDesc(mark+parm.getValue("ORDER_DESC",i)+ " "+ df1.format(parm.getDouble("ZFBL1", i) * 100) + "%");
					}
					//单位
					result.setUnitDesc(parm.getValue("UNIT_CHN_DESC",i));
					//单价
					double price=Double.parseDouble(parm.getValue("OWN_PRICE",i));
					result.setOwnPrice(df.format(price));
					//数量
					result.setDosageQty(parm.getValue("DOSAGE_QTY",i));
					if(parm.getValue("DOSAGE_QTY",i).equals("0")){
						continue;
					}
					//金额
					double totamt=Double.parseDouble(parm.getValue("TOT_AMT",i));
					result.setTotAmt(df2.format(totamt));
					//执行科室
					result.setExecDept(parm.getValue("DEPT_CHN_DESC",i));
					//执行时间 
					result.setExecDate(parm.getValue("EXEC_DATE",i));
					
					//小计
					if(i+1<=parm.getCount()){
						if(parm.getValue("BILL_DATE",i).equals(parm.getValue("BILL_DATE",i+1))){
							tot+=Double.parseDouble(parm.getValue("TOT_AMT",i));
						}else{
							tot+=Double.parseDouble(parm.getValue("TOT_AMT",i));
							String totAmtDay=df.format(tot);
							result.setTotAmtDay(totAmtDay);
							tot=0;
						}
					}
					//总计
					t+=(Double.parseDouble(parm.getValue("TOT_AMT",i)));
					totAmt=df.format(t);
					results.add(result);
			}
					
				//加一条总计
				res.setTotalPrice(totAmt);
				res.setResults(results);
				xStream.alias("Response", RegResponse.class);
				xStream.alias("Result", Result.class);
				xStream.aliasField("Results", List.class, "Results");
				outXml = xStream.toXML(res);
				
				System.out.println(outXml);
				return outXml;
 
		}
		return outXml;
	}
	
	
	/**
	 * 获取在院患者的最新诊断
	 * 
	 * @param code 用户名
	 * @param password 密码
	 * @param mrNo 病案号
	 * @return 最新诊断(主诊断+次诊断)
	 */
	@Override
	public String getAdmInpDiagByMrNo(String code, String password, String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getAdmInpDiagByMrNo(code,
				password, mrNo);
	}
	
	/**
	 * 获取在院患者的最新术式
	 * 
	 * @param code 用户名
	 * @param password 密码
	 * @param mrNo 病案号
	 * @return 最新术式(主+次)
	 */
	@Override
	public String getOpeDescByMrNo(String code, String password, String mrNo) {
		return SYSDictionaryServiceTool.getInstance().getOpeDescByMrNo(code,
				password, mrNo);
	}

}
