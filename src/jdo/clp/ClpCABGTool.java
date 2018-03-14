package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: CABG数据集</p>
 *
 * <p>Description:CABG数据集</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20100305
 * @version 1.0
 */
public class ClpCABGTool {
	private static final String exm1="BO";
	private static final String exm2="IO";
	private static final String exm3="AO72";
	private static final String exm4="AOW120";
	private static final String exm5="AOW21";
	private static final String exm6="AOA21";
	private static final String exm7="OUT";
	private static final String GET_CABG_1="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_1='Y'";
	private static final String GET_CABG_1_1="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_1_1 IS NOT NULL";
	private static final String GET_CABG_1_2="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_1_2 IS NOT NULL";
	private static final String GET_CABG_1_3="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_1_3 IS NOT NULL";
	private static final String GET_CABG_2_1="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_2_1 IS NOT NULL";
	private static final String GET_CABG_2_2="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_2_2 IS NOT NULL";
	private static final String GET_CABG_3_1="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_3_1 IS NOT NULL";
	private static final String GET_CABG_3_2="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_3_2 IS NOT NULL";
	private static final String GET_CABG_4_1="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_4_1 IS NOT NULL";
	private static final String GET_CABG_4_2="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_4_2 IS NOT NULL";
	private static final String GET_CABG_4_3="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_4_3 IS NOT NULL";
	private static final String GET_CABG_4_4="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_4_4 IS NOT NULL";
	private static final String GET_CABG_5="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_5 IS NOT NULL";
	private static final String GET_CABG_6="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_6='Y'";
	private static final String GET_CABG_7="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_7 IS NOT NULL";
	private static final String GET_CABG_9="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_CABG WHERE CABG_9='AOW21'";
	private static final String GET_CABG_TOT_PERSON="SELECT COUNT(CASE_NO) TOT FROM CLP_CABG";




	public double getCABGTotPerson(){
		String sql=GET_CABG_TOT_PERSON;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//ln("getHFTotPerson.getErrText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("TOT",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public String getRateString(double amt){
		return Math.round(amt)*100+"%";
	}
	public double getRate(String sql){
		if(sql==null||sql.trim().length()<=0){
			return -1;
		}
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			//ln("getRage.wrongSql="+sql);
			//ln("getRate.errText:"+result.getErrText());
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("COUNT_NO",0));
		return rate;
	}
	public String getCABG1Rate(){
		double rate=this.getRate(this.GET_CABG_1)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG2_2Rate(){
		double rate=this.getRate(this.GET_CABG_2_2)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG3_1Rate(){
		double rate=this.getRate(this.GET_CABG_3_1)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG4_1Rate(){
		double rate=this.getRate(this.GET_CABG_4_1)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG4_2Rate(){
		double rate=this.getRate(this.GET_CABG_4_2)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG4_3Rate(){
		double rate=this.getRate(this.GET_CABG_4_3)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG4_4Rate(){
		double rate=this.getRate(this.GET_CABG_4_4)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG5Rate(){
		double rate=this.getRate(this.GET_CABG_5)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG6Rate(){
		double rate=this.getRate(this.GET_CABG_6)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG7Rate(){
		double rate=this.getRate(this.GET_CABG_7)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	public String getCABG9Rate(){
		double rate=this.getRate(this.GET_CABG_9)/this.getCABGTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
}
