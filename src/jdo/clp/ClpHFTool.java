package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: HF数据集</p>
 *
 * <p>Description:HF数据集</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20100305
 * @version 1.0
 */
public class ClpHFTool {
	//检查1
	private static final String exm1="E";
	//检查2
	private static final String exm2="I24";
	//检查3
	private static final String exm3="I12W";
	//检查4
	private static final String exm4="B1W";
	//检查5
	private static final String exm5="OUT";
	private static final String GETHF1_1="SELECT COUNT(CASE_NO) HF1_1 FROM CLP_HF WHERE HF_1='#'";
	private static final String GETHF1_2="SELECT COUNT(CASE_NO) HF1_2 FROM CLP_HF WHERE HF_1_1='#'";
	private static final String GETHF2="SELECT COUNT(CASE_NO) HF_2 FROM CLP_HF WHERE HF_2='#'";
	private static final String GETHF3="SELECT COUNT(CASE_NO) HF_3 FROM CLP_HF WHERE HF_3='#'";
	private static final String GETHF4="SELECT COUNT(CASE_NO) HF_4 FROM CLP_HF WHERE HF_4='#'";
	private static final String GETHF5="SELECT COUNT(CASE_NO) HF_5 FROM CLP_HF WHERE HF_5='#'";
	private static final String GETHF6="SELECT COUNT(CASE_NO) HF_6 FROM CLP_HF WHERE HF_6='#'";
	private static final String GETHF7="SELECT COUNT(CASE_NO) HF_7 FROM CLP_HF WHERE HF_7='#'";
	private static final String GETHF8="SELECT COUNT(CASE_NO) HF_8 FROM CLP_HF WHERE HF_8='#'";
	private static final String GETHF9_1="SELECT COUNT(CASE_NO) HF_9_1 FROM CLP_HF WHERE HF_9_1='#'";
	private static final String GETHF9_2="SELECT COUNT(CASE_NO) HF_9_2 FROM CLP_HF WHERE HF_9_2='#'";
	private static final String GETHF9_3="SELECT COUNT(CASE_NO) HF_9_3 FROM CLP_HF WHERE HF_9_3='#'";
	private static final String GETHF9_4="SELECT COUNT(CASE_NO) HF_9_4 FROM CLP_HF WHERE HF_9_4='#'";
	private static final String GETHF1_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_1 IS NOT NULL";
	private static final String GETHF1_1_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_1_1 IS NOT NULL";
	private static final String GETHF2_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_2 IS NOT NULL";
	private static final String GETHF3_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_3 IS NOT NULL";
	private static final String GETHF4_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_4 IS NOT NULL";
	private static final String GETHF5_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_5 IS NOT NULL";
	private static final String GETHF6_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_6 IS NOT NULL";
	private static final String GETHF7_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_7 IS NOT NULL";
	private static final String GETHF8_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_8 IS NOT NULL";
	private static final String GETHF9_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_9='Y'";
	private static final String GETHF9_1_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_9_1 IS NOT NULL";
	private static final String GETHF9_2_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_9_2 IS NOT NULL";
	private static final String GETHF9_3_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_9_3 IS NOT NULL";
	private static final String GETHF9_4_NO="SELECT COUNT(CASE_NO) COUNT_NO FROM CLP_HF WHERE HF_9_4 IS NOT NULL";
	private static final String GET_HF_TOT_PERSON="SELECT COUNT(CASE_NO) TOT FROM CLP_HF";



	public double getHF1No(String hf1){
		if(hf1==null||hf1.trim().length()<=0){
			return -1;
		}
		String sql=GETHF1_1.replaceFirst("#", hf1);
		TJDODBTool d;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF1_1",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF1_1No(String hf1_1){
		if(hf1_1==null||hf1_1.trim().length()<=0){
			return -1;
		}
		String sql=GETHF1_2.replaceFirst("#", hf1_1);
		TJDODBTool d;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF1_2",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF2No(String hf2){
		if(hf2==null||hf2.trim().length()<=0){
			return -1;
		}
		String sql=GETHF2.replaceFirst("#", hf2);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_2",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF3No(String hf3){
		if(hf3==null||hf3.trim().length()<=0){
			return -1;
		}
		String sql=GETHF3.replaceFirst("#", hf3);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_3",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF4No(String hf4){
		if(hf4==null||hf4.trim().length()<=0){
			return -1;
		}
		String sql=GETHF4.replaceFirst("#", hf4);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_4",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF5No(String hf5){
		if(hf5==null||hf5.trim().length()<=0){
			return -1;
		}
		String sql=GETHF5.replaceFirst("#", hf5);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_5",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF6No(String hf6){
		if(hf6==null||hf6.trim().length()<=0){
			return -1;
		}
		String sql=GETHF6.replaceFirst("#", hf6);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_6",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF7No(String hf7){
		if(hf7==null||hf7.trim().length()<=0){
			return -1;
		}
		String sql=GETHF7.replaceFirst("#", hf7);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_7",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF8No(String hf8){
		if(hf8==null||hf8.trim().length()<=0){
			return -1;
		}
		String sql=GETHF8.replaceFirst("#", hf8);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_8",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF9_1No(String hf9_1){
		if(hf9_1==null||hf9_1.trim().length()<=0){
			return -1;
		}
		String sql=GETHF9_1.replaceFirst("#", hf9_1);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_9_1",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF9_2No(String hf_9_2){
		if(hf_9_2==null||hf_9_2.trim().length()<=0){
			return -1;
		}
		String sql=GETHF9_2.replaceFirst("#", hf_9_2);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_9_2",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF9_3No(String hf_9_3){
		if(hf_9_3==null||hf_9_3.trim().length()<=0){
			return -1;
		}
		String sql=GETHF9_3.replaceFirst("#", hf_9_3);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_9_3",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHF9_4No(String hf_9_4){
		if(hf_9_4==null||hf_9_4.trim().length()<=0){
			return -1;
		}
		String sql=GETHF9_4.replaceFirst("#", hf_9_4);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("HF_9_4",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getHFTotPerson(){
		String sql=GET_HF_TOT_PERSON;
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("TOT",0));
		if(rate==0){
			return -1;
		}
		return rate;
	}
	public double getRate(String sql){
		if(sql==null||sql.trim().length()<=0){
			return -1;
		}
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		double rate=TypeTool.getDouble(result.getData("COUNT_NO",0));
		return rate;
	}
	public String getRateString(double amt){
		return Math.round(amt)*100+"%";
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf1Rate(){
		double rate=this.getRate(this.GETHF1_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf1_1Rate(){
		double rate=this.getRate(this.GETHF1_1_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf2Rate(){
		double rate=this.getRate(this.GETHF2_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf3Rate(){
		double rate=this.getRate(this.GETHF3_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf4Rate(){
		double rate=this.getRate(this.GETHF4_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf5Rate(){
		double rate=this.getRate(this.GETHF5_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf6Rate(){
		double rate=this.getRate(this.GETHF6_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf7Rate(){
		double rate=this.getRate(this.GETHF7_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf8Rate(){
		double rate=this.getRate(this.GETHF8_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
	/**
	 * 1.实施左心室功能评价：胸片
	 * @return
	 */
	public String getHf9Rate(){
		double rate=this.getRate(this.GETHF9_NO)/this.getHFTotPerson();
		if(rate<=0){
			return "--";
		}
		return getRateString(rate);
	}
}
