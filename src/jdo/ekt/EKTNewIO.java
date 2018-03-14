package jdo.ekt;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jdo.bil.BILInvoiceTool;
import jdo.opb.OPBTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

public class EKTNewIO extends TJDODBTool {
	/**
	 * 实例
	 */
	private static EKTNewIO instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return EKTIO
	 */
	public static EKTNewIO getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTNewIO();
		return instanceObject;
	}
	/**
	 * 医保打票操作 收取医疗卡金额
	 * @param tempParm
	 * @return
	 */
	public Map onNewSaveInsFee(Map tempParm){
		TParm parm = new TParm(tempParm);
		if (isClientlink())
			return (Map) callServerMethod(tempParm);
		TConnection connection = getConnection();
		TParm result = new TParm();
		TParm ektTradeParm=new TParm();
		String tredeNo = "";//存在未收费的医嘱需要修改OPD_ORDER 表中 相关数据
		String historyNo ="";
		String ektSeq="";
		//存在未收费的医嘱 需要添加一条数据
		if(parm.getDouble("billAmt")!=0){
			tredeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
			ektTradeParm = getEktTrateParm(parm,tredeNo);
			// 添加此次操作扣款金额
			result = EKTNewTool.getInstance().insertEktTrade(ektTradeParm,
					connection);
			if (getError(result, connection).getErrCode() < 0) {
				return result.getData();
			}
			//add by huangtt 20160920插入医疗卡历史表 start
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
			if (null==historyNo||historyNo.length()<=0) {
				historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// 得到医疗卡外部交易号
			}
			ektTradeParm.setData("HISTORY_NO", historyNo);// 内部交易号
			
			BigDecimal oldAmt  =new BigDecimal( ektTradeParm.getDouble("OLD_AMT"));
			BigDecimal amt  =new BigDecimal(ektTradeParm.getDouble("AMT"));
			BigDecimal ektAmt = oldAmt.subtract(amt);

//			ektTradeParm.setData("EKT_AMT",  ektTradeParm.getDouble("OLD_AMT")-ektTradeParm.getDouble("AMT"));// 内部交易号
			ektTradeParm.setData("EKT_AMT",  ektAmt.doubleValue());// 内部交易号

			String sql = this.getEktMasterHistorySql(ektTradeParm,"");
			result = new TParm(TJDODBTool.getInstance().update(sql,connection));
			if (getError(result, connection).getErrCode() < 0) {
				return result.getData();
			}
			
			int seq1=0;
			TParm seqParm = new TParm(
					TJDODBTool.getInstance().select(
							"SELECT MAX(SEQ_NO) SEQ FROM EKT_MASTER_HISTORY WHERE MR_NO='"+ektTradeParm.getValue("MR_NO")+"'"));
			
			
			if(seqParm.getInt("SEQ",0) > 0){
				seq1 = seqParm.getInt("SEQ",0)+ 1;
			}else{
				seq1 = 1;
			}
			
			seq1++;
			ektSeq=seq1+"";
			//add by huangtt 20160920插入医疗卡历史表 end 
			
			
		}
		ektTradeParm = getEktNewTrateParm(parm);
		//医疗卡收费退费共用操作
		ektTradeParm.setData("ektSeq", ektSeq);
		result =insEktOnFee(parm, ektTradeParm, connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		// 删除历史记录
		result = EKTNewTool.getInstance().deleteEKTMaster(parm, connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		TParm ektMasterParm = new TParm();
		ektMasterParm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));// 医疗卡金额
		ektMasterParm.setData("CARD_NO", parm.getValue("CARD_NO"));// 卡号
		ektMasterParm.setData("ID_NO", parm.getValue("IDNO"));// ID
		ektMasterParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		ektMasterParm.setData("NAME", parm.getValue("PAT_NAME"));// 病患名称
		ektMasterParm.setData("CREAT_USER", parm.getValue("OPT_USER"));// 执行人员
		ektMasterParm.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人员
		ektMasterParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		// 添加数据获得最新医疗卡金额
		result = EKTNewTool.getInstance().insertEKTMaster(ektMasterParm,
				connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		// 存在绿色通道金额
//		if (parm.getValue("FLG").equals("Y")) {
//			// 更新挂号主档表中绿色通道金额
//			result = PatAdmTool.getInstance().updateEKTGreen1(
//					ektParmTemp(ektTradeParm), connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result.getData();
//			}
//		}
		if (parm.getDouble("billAmt") != 0) {
			ektTradeParm.setData("TRADE_NO", tredeNo);// 内部交易号
			ektTradeParm.setData("HISTORY_NO", historyNo);// 内部交易号
		}
		connection.commit();
		connection.close();
		return ektTradeParm.getData();
	}
	/**
	 * 医疗卡收费流程修改
	 */
	public Map onNewSaveFee(Map tempParm) {
		TParm parm = new TParm(tempParm);
		//===================================add by huangjw 20150710
//		double initAmt=0.00;//期初金额
//		double lastAmt=0.00;//期末金额
//		TParm initParm=new TParm();
//		initParm.setData("MR_NO",parm.getValue("MR_NO"));
//		initParm.setData("WRITE_FLG","Y");
//		initAmt=EKTTool.getInstance().queryCurrentBalance(initParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		
//		if (isClientlink())
//			return  (Map) callServerMethod(tempParm);
//		TConnection connection = getConnection();
		TParm result = null;
		String[] sql = null;
		TParm ektTradeParm = getEktNewTrateParm(parm);
		//医疗卡收费退费共用操作  , connection
		result =ektOnFee(parm, ektTradeParm);
		String[] tempSql = (String[]) result.getData("SQL");
		if(tempSql != null){
			sql = tempSql;
		}
//		if (getError(result, connection).getErrCode() < 0) {
//			return result.getData();
//		}
		// 删除历史记录
//		result = EKTNewTool.getInstance().deleteEKTMaster(parm, connection);
//		if (getError(result, connection).getErrCode() < 0) {
//			return result.getData();
//		}
		TParm ektMasterParm = new TParm();
		ektMasterParm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));// 医疗卡金额
		ektMasterParm.setData("CARD_NO", parm.getValue("CARD_NO"));// 卡号
		ektMasterParm.setData("ID_NO", parm.getValue("IDNO"));// ID
		ektMasterParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		ektMasterParm.setData("NAME", parm.getValue("PAT_NAME"));// 病患名称
		ektMasterParm.setData("CREAT_USER", parm.getValue("OPT_USER"));// 执行人员
		ektMasterParm.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人员
		ektMasterParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		// 添加数据获得最新医疗卡金额
		String[] masterSql = new String[1];
		
		masterSql[0] = "UPDATE EKT_MASTER SET CURRENT_BALANCE="+ektMasterParm.getDouble("CURRENT_BALANCE")+"," +
				" CREAT_USER='"+ektMasterParm.getValue("CREAT_USER")+"'," +
						" OPT_USER='"+ektMasterParm.getValue("OPT_USER")+"',OPT_DATE=SYSDATE," +
								" OPT_TERM='"+ektMasterParm.getValue("OPT_TERM")+"'" +
										"  WHERE CARD_NO='"+ektMasterParm.getValue("CARD_NO")+"' ";
		
		if(sql == null){
			sql = masterSql;
		}else{
			sql = StringTool.copyArray(sql, masterSql);
		}
		
//		result = EKTNewTool.getInstance().insertEKTMaster(ektMasterParm,
//				connection);
//		if (getError(result, connection).getErrCode() < 0) {
//			return result.getData();
//		}
		// 存在绿色通道金额
		if (parm.getValue("FLG").equals("Y")) {
			// 更新挂号主档表中绿色通道金额
			
			double gerrenBalance =StringTool.round(ektTradeParm.getDouble("OLD_GREEN_BALANCE")
					- ektTradeParm.getDouble("SHOW_GREEN_USE"), 2);
			
			String[] regSql = new String[1];
			regSql[0]= "UPDATE REG_PATADM " +
					" SET GREEN_BALANCE = "+ gerrenBalance +
					" WHERE CASE_NO = '"+ektTradeParm.getValue("CASE_NO")+"'";
			
			if(sql == null){
				sql = regSql;
			}else{
				sql = StringTool.copyArray(sql, regSql);
			}
			
//			result = PatAdmTool.getInstance().updateEKTGreen1(
//					ektParmTemp(ektTradeParm), connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result.getData();
//			}
		}
		//============pangben 2013-7-17 修改OPD_ORDER 收费状态，OPD_UN_FLG=Y 此次扣款金额大于医疗卡金额交易号码不存在
		//ODO_EKT_FLG='Y' 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
		if (null!=parm.getValue("ODO_EKT_FLG")&&parm.getValue("ODO_EKT_FLG").equals("Y")) {
			if (null==parm.getValue("OPD_UN_FLG")||parm.getValue("OPD_UN_FLG").length()<=0||
					parm.getValue("OPD_UN_FLG").equals("N")) {
				if (null==ektTradeParm.getValue("TRADE_NO")||ektTradeParm.getValue("TRADE_NO").length()<=0) {
					result=new TParm();
					result.setErr(-1,"没有获得交易号码");
//					connection.close();
					return result.getData();
				}
				parm.setData("TRADE_NO",ektTradeParm.getValue("TRADE_NO"));// 内部交易号
			}
			TParm billSetsParm=EKTTool.getInstance().getBillSets(parm, parm.getParm("RE_PARM"));
			TParm orderParm = billSetsParm.getParm("orderParm"); // 需要操作的医嘱
			
			//add by huangtt 20160901 start
			String[] updateSql = updateOpdOrderEkt(billSetsParm, orderParm);
			if(updateSql != null){
				if(sql == null){
					sql = updateSql;
				}else{
					sql = StringTool.copyArray(sql, updateSql);
				}
			}
			
			
//			result = OPBTool.getInstance().updateOpdOrderEkt(billSetsParm, orderParm, connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result.getData();
//			}
			
			//add by huangtt 20160901 end
		}
		
		ektTradeParm.setData("SQL", sql);
		
//		connection.commit();
		//============================================医疗卡日志数据add by huangjw 20150710 start
//		TParm lastParm=new TParm();
//		lastParm.setData("MR_NO",parm.getValue("MR_NO"));
//		lastParm.setData("WRITE_FLG","Y");
//		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
//		
//		TParm inParm=new TParm();
//		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
//		inParm.setData("MR_NO",parm.getValue("MR_NO"));
//		inParm.setData("EKT_DATE",date);
//		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
//		TParm logParm=new TParm();
//		
//		if(queryParm.getCount()>0){//更新数据
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
//			
//		}else{//插入数据
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			logParm.setData("INIT_AMT",initAmt);//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
//		}
//		if (result.getErrCode() != 0) {
//			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
//					+ result.getErrText());
//			connection.close();
//			return ektTradeParm.getData();
//			//return false;
//		}
		//============================================医疗卡日志数据add by huangjw 20150710 end
//		connection.commit();
//		connection.close();
		return ektTradeParm.getData();
	}
	/**
	 * 医保医疗卡退票操作
	 * @return
	 */
	public TParm unInsOpbReceiptNo(TParm parm,TConnection connection){
		TParm ektTradeParm = getEktNewTrateParm(parm);
		//保退票操作 扣除医疗卡金额查询此病患此收据号码所有的不同的内部交易号
//		TParm ektSumTradeParm = EKTNewTool.getInstance().selectBusinessByReceiptNo(
//				parm);
//		if (ektSumTradeParm.getErrCode() < 0) {
//			return ektSumTradeParm;
//		}
//		StringBuffer tradeNo=new StringBuffer();
//		for (int i = 0; i < ektSumTradeParm.getCount(); i++) {
//			if(!tradeNo.toString().contains(ektSumTradeParm.getValue("BUSINESS_NO", i))){
//				tradeNo.append("'").append(ektSumTradeParm.getValue("BUSINESS_NO", i)).append("',");//UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
//			}
//		}
//		parm.setData("TRADE_SUM_NO",tradeNo.length()>0?
//				tradeNo.toString().substring(0,tradeNo.toString().lastIndexOf(",")):"");
		ektTradeParm.setData("ektSeq", "");
		TParm result=insEktOnFee(parm, ektTradeParm, connection);
		if(result.getErrCode()<0){
			return result;
		}
		return ektTradeParm;
	}
	/**
	 * 医疗卡收费退费共用操作
	 * @param parm
	 * @return
	 *   ,TConnection connection
	 */
	private TParm ektOnFee(TParm parm,TParm ektTradeParm){
		// 获得历史记录查询此病患所有未打票的数据总金额
		//挂号操作
		String[] sql = null;
		TParm ektSumTradeParm=new TParm();
		TParm result=new TParm();
		parm.setData("HISTORY_NO", ektTradeParm.getValue("HISTORY_NO"));
		if (ektTradeParm.getValue("BUSINESS_TYPE").equals("REG")
				|| ektTradeParm.getValue("BUSINESS_TYPE").equals("REGT")) {
			ektSumTradeParm = EKTNewTool.getInstance().selectEktTrade(
					ektTradeParm);
			if (ektSumTradeParm.getErrCode() < 0) {
				return ektSumTradeParm;
			}
			//作废数据   , connection
			parm.setData("OPB_AMT", parm.getDouble("EKT_USE"));
			result=resetEktTrade(parm,ektSumTradeParm);
			
			//add by huangtt 20160901 start 
			String[] tempSql = (String[]) result.getData("SQL");
			if(sql == null){
				sql = tempSql;
				
			}else{
				sql = StringTool.copyArray(sql, tempSql);
			}
			
			
//			if(result.getErrCode()<0){
//				return result;
//			}
			//add by huangtt 20160901 end
			
		} else {//收费操作
			//医生站的增删改数据查询 通过处方签查询所有要操作的医嘱
			ektSumTradeParm=EKTNewTool.getInstance().selectTradeNo(parm);
			if (ektSumTradeParm.getErrCode() < 0) {
				return ektSumTradeParm;
			}
			//作废数据  , connection
			result=resetEktTrade(parm,ektSumTradeParm);
			
			//add by huangtt 20160901 start 
			String[] tempSql = (String[]) result.getData("SQL");
			if(sql == null){
				sql = tempSql;
				
			}else{
				sql = StringTool.copyArray(sql, tempSql);
			}
			
			
//			if(result.getErrCode()<0){
//				return result;
//			}
			//add by huangtt 20160901 end
		}
		if (ektTradeParm.getDouble("AMT")
				+ ektTradeParm.getDouble("GREEN_BUSINESS_AMT") != 0
				&& !"REGT".equals(ektTradeParm.getValue("BUSINESS_TYPE"))) {// 退挂操作不执行添加正常数据
			// 添加此次操作扣款金额
			
			
			String[] tempSql;
			if(result.getBoolean("historyFlg")){
				tempSql = new String[2];
			}else{
				tempSql = new String[1];
			}
			
			tempSql[0] = "INSERT INTO EKT_TRADE(" +
					" TRADE_NO, CARD_NO, MR_NO,CASE_NO, PAT_NAME," +
					" OLD_AMT, AMT, STATE,BUSINESS_TYPE,GREEN_BALANCE," +
					" GREEN_BUSINESS_AMT,OPT_USER, OPT_DATE,OPT_TERM ) " +
					" VALUES(" +
					" '"+ektTradeParm.getValue("TRADE_NO")+"', '"+ektTradeParm.getValue("CARD_NO")+"', '"+ektTradeParm.getValue("MR_NO")+"','"+ektTradeParm.getValue("CASE_NO")+"','"+ektTradeParm.getValue("PAT_NAME")+"'," +
					" "+ektTradeParm.getDouble("OLD_AMT")+", "+ektTradeParm.getDouble("AMT")+", '"+ektTradeParm.getValue("STATE")+"', '"+ektTradeParm.getValue("BUSINESS_TYPE")+"',"+ektTradeParm.getDouble("GREEN_BALANCE")+"," +
					" "+ektTradeParm.getDouble("GREEN_BUSINESS_AMT")+",'"+ektTradeParm.getValue("OPT_USER")+"', SYSDATE,'"+ektTradeParm.getValue("OPT_TERM")+"')";
			
			if(result.getBoolean("historyFlg")){
				ektTradeParm.setData("EKT_AMT", parm.getDouble("EKT_AMT"));
				tempSql[1] = this.getEktMasterHistorySql(ektTradeParm,"");
				System.out.println("insert ekt_trade====="+tempSql[1]);
			}

			if(sql == null){
				sql = tempSql;
				
			}else{
				sql = StringTool.copyArray(sql, tempSql);
			}
			
//			result = EKTNewTool.getInstance().insertEktTrade(ektTradeParm,
//					connection);
		}
//		if (result.getErrCode() < 0) {
//			return result;
//		}
		result.setData("SQL", sql);
		return result;
	}
	/**
	 * 医保操作添加退费数据
	 * @param parm
	 * @param ektTradeParm
	 * @param connection
	 * @return
	 */
	private TParm insEktOnFee(TParm parm,TParm ektTradeParm,TConnection connection){
		TParm result =new TParm();
		if (ektTradeParm.getDouble("AMT")
				+ ektTradeParm.getDouble("GREEN_BUSINESS_AMT") != 0
				&& !"REGT".equals(ektTradeParm.getValue("BUSINESS_TYPE"))) {// 退挂操作不执行添加正常数据
			// 添加此次操作扣款金额
			result = EKTNewTool.getInstance().insertEktTrade(ektTradeParm,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			
			//add by huangtt 20160920插入医疗卡历史表 start
			BigDecimal oldAmt  =new BigDecimal(ektTradeParm.getDouble("OLD_AMT"));
			BigDecimal amt  =new BigDecimal(ektTradeParm.getDouble("AMT"));
			BigDecimal ektAmt = oldAmt.subtract(amt);

//			ektTradeParm.setData("EKT_AMT",  ektTradeParm.getDouble("OLD_AMT")-ektTradeParm.getDouble("AMT"));// 内部交易号
			ektTradeParm.setData("EKT_AMT",  ektAmt.doubleValue());// 内部交易号
			String sql ="";
			if(ektTradeParm.getValue("ektSeq").length() > 0){
				 sql =this.getEktMasterHistorySql(ektTradeParm,ektTradeParm.getValue("ektSeq"));
			}else{
				sql =this.getEktMasterHistorySql(ektTradeParm,"");
			}
			 
			result = new TParm(TJDODBTool.getInstance().update(sql,connection));
			if (result.getErrCode() < 0) {
				return result;
			}
			
			//add by huangtt 20160920插入医疗卡历史表 end
			
			
		}
		
		return result;
	}
	/**
	 * 作废数据修改状态    ,TConnection connection
	 * @return
	 */
	private TParm resetEktTrade(TParm parm,TParm ektSumTradeParm){
		TParm result=new TParm();
		
		String[] sql = null;
		String[] sqlLog = null;
		
		
		int count =ektSumTradeParm.getCount();
		
		if(count > 0){
			sql = new String[count];
			
		}
		double sumAmt = 0;
		for (int i = 0; i < ektSumTradeParm.getCount(); i++) {
			// 添加一条负数据
			TParm tempEktParm=ektSumTradeParm.getRow(i);
//			if(tempEktParm.getValue("BUSINESS_TYPE").equals("OPB"))
//				tempEktParm.setData("BUSINESS_TYPE","OPBT");
//			else if(tempEktParm.getValue("BUSINESS_TYPE").equals("ODO"))
//				tempEktParm.setData("BUSINESS_TYPE","ODOT");
//			else if(tempEktParm.getValue("BUSINESS_TYPE").equals("REG"))
//				tempEktParm.setData("BUSINESS_TYPE","REGT");
//			String tredeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
//			result = EKTNewTool.getInstance().insertEktTrade(
//					getEktTrateParm(tempEktParm,tredeNo), connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result;
//			}
//			tempEktParm=ektSumTradeParm.getRow(i);
//			tempEktParm.setData("RESET_TRADE_NO",tredeNo);//作废号码
			tempEktParm.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人员
			tempEktParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			
			sql[i]= "UPDATE EKT_TRADE SET STATE='3',OPT_DATE=SYSDATE," +
					"OPT_USER='"+tempEktParm.getValue("OPT_USER")+"',OPT_TERM='"+tempEktParm.getValue("OPT_TERM")+"'  " +
					"WHERE TRADE_NO='"+tempEktParm.getValue("TRADE_NO")+"' AND STATE='1'";
			
			
			sumAmt+=tempEktParm.getDouble("AMT");
			
			
			
//			sqlLog[i]="INSERT INTO EKT_MASTER_HISTORY " +
//					" (MR_NO,SEQ_NO,CHANGE_DATE,LATEST_AMT,BUSSINESS_AMT," +
//					" CURRENT_AMT,OPT_TYPE,CARD_NO,OPT_USER,OPT_DATE," +
//					" OPT_TERM,HISTORY_NO,CASE_NO,EKT_BUSINESS_NO,MZCONFIRM_NO) " +
//					" VALUES " +
//					" ('"+tempEktParm.getValue("MR_NO")+"','"+seq+"',SYSDATE, "+parm.getDouble("EKT_OLD_AMT")+", "+tempEktParm.getDouble("AMT")+" ," +
//					"  "+parm.getDouble("EKT_AMT")+",'"+tempEktParm.getValue("BUSINESS_TYPE")+"','"+tempEktParm.getValue("CARD_NO")+"','"+tempEktParm.getValue("OPT_USER")+"',SYSDATE," +
//					"  '"+tempEktParm.getValue("OPT_TERM")+"','"+historyNo+"','"+tempEktParm.getValue("CASE_NO")+"','','')";
//			
//			result = EKTNewTool.getInstance().updateOpdEktTrade(tempEktParm, connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result;
//			}
		}
		
		if(count > 0){
			sqlLog = new String[1];
			
			TParm tempEktParm=ektSumTradeParm.getRow(0);
			
			tempEktParm.setData("OPT_USER", parm.getValue("OPT_USER"));// 操作人员
			tempEktParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			
			tempEktParm.setData("OLD_AMT", parm.getDouble("OLD_AMT"));
			tempEktParm.setData("EKT_AMT", parm.getDouble("EKT_AMT"));
			tempEktParm.setData("HISTORY_NO", parm.getValue("HISTORY_NO"));
			if("REGT".equals( parm.getValue("BUSINESS_TYPE"))){
				tempEktParm.setData("BUSINESS_TYPE", "REG");
			}else{
				tempEktParm.setData("BUSINESS_TYPE", parm.getValue("BUSINESS_TYPE"));
			}
			
			
			if(parm.getDouble("EKT_AMT")-parm.getDouble("OLD_AMT") != sumAmt){
				tempEktParm.setData("AMT", parm.getDouble("OLD_AMT")-parm.getDouble("EKT_AMT"));
				result.setData("historyFlg", false);
			}else{
				tempEktParm.setData("AMT", -sumAmt);
				result.setData("historyFlg", true);
			}
			
			
			sqlLog[0]=this.getEktMasterHistorySql(tempEktParm,"");
			System.out.println(sqlLog[0]);
			sql = StringTool.copyArray(sql, sqlLog);
		}else{
			result.setData("historyFlg", true);
		}
		
		
		
		result.setData("SQL", sql);
		
		return result;
	}
	/**
	 * 医疗卡 绿色通道参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 20111009
	 */
	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
//		ektParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
//		// 查询此病患已收费未打票的所有数据汇总金额
//		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(ektParm);
//		if (ektSumParm.getCount() > 0) {
//			// 获得此就诊病患绿色通道金额扣款金额
//			ektParm.setData("GREEN_BALANCE", StringTool.round(parm
//					.getDouble("GREEN_BALANCE")
//					- parm.getDouble("GREEN_BUSINESS_AMT")
//					- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0), 2));
//		} else {
//			// 获得此就诊病患绿色通道金额扣款金额
//			ektParm.setData("GREEN_BALANCE", StringTool.round(parm
//					.getDouble("GREEN_BALANCE")
//					- parm.getDouble("GREEN_BUSINESS_AMT"), 2));
//		}
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("OLD_GREEN_BALANCE")
				- parm.getDouble("SHOW_GREEN_USE"), 2));
		
		return ektParm;
	}

	/**
	 * 共用
	 * 
	 * @param result
	 * @param connection
	 * @return
	 */
	private TParm getError(TParm result, TConnection connection) {
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * 获得扣款参数，作废操作
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getEktTrateParm(TParm parm,String tradeNo) {
		
		TParm ektParm = new TParm();
		ektParm.setData("TRADE_NO", tradeNo);// 内部交易号
		ektParm.setData("CARD_NO", parm.getValue("CARD_NO"));// 卡号
		ektParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
		ektParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// 病患名称
		ektParm.setData("OLD_AMT", parm.getDouble("EKT_OLD_AMT"));// 原来金额
		ektParm.setData("AMT", parm.getDouble("billAmt"));// 扣款金额
		ektParm.setData("STATE", "1");// 状态(1,扣款;2,退费,3,作废)
		ektParm.setData("BUSINESS_TYPE","OPB");// 类别
		ektParm.setData("GREEN_BALANCE", 0.00);// 特批总金额
		ektParm.setData("GREEN_BUSINESS_AMT", 0.00);// 特批款扣款金额
		ektParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		ektParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		ektParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//医保顺序号 add by huangtt 20160920
		parm.setData("EKT_OLD_AMT",parm.getDouble("EKT_OLD_AMT")-parm.getDouble("billAmt"));//显示当前医疗卡中的金额
		return ektParm;
	}

	/**
	 * 此病患此次就诊医嘱总金额
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getEktNewTrateParm(TParm parm) {
		String tredeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
		if (null==tredeNo||tredeNo.length()<=0) {
			System.out.println("获得医疗卡交易号码出现问题:"+tredeNo);
			tredeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
		}
		
		String historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
		if (null==historyNo||historyNo.length()<=0) {
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// 得到医疗卡外部交易号
		}
		
		TParm ektParm = new TParm();
		ektParm.setData("HISTORY_NO", historyNo);// 内部交易号
		ektParm.setData("TRADE_NO", tredeNo);// 内部交易号
		ektParm.setData("CARD_NO", parm.getValue("CARD_NO"));// 卡号
		ektParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
		ektParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// 病患名称
		ektParm.setData("OLD_AMT", parm.getDouble("EKT_OLD_AMT"));// 原来金额
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			ektParm.setData("AMT", parm.getDouble("AMT"));// 扣款金额
		}else{
			ektParm.setData("AMT", parm.getDouble("EKT_USE"));// 扣款金额
		}
		ektParm.setData("STATE", "1");// 状态(1,扣款和交易结束;2,退费,3作废)
		ektParm.setData("BUSINESS_TYPE", parm.getValue("BUSINESS_TYPE"));// 类别
		ektParm.setData("GREEN_BALANCE", parm.getDouble("GREEN_PATH_TOTAL"));// 特批总金额
		ektParm.setData("GREEN_BUSINESS_AMT", parm.getDouble("GREEN_USE"));// 特批款扣款金额
		ektParm.setData("EKT_TRADE_TYPE", parm.getValue("EKT_TRADE_TYPE"));// 查询条件
		//ektParm.setData("CANCEL_FLG", "0");// 作废标志0:正常1: 作废2:调整票号3:退票作废4:作废补印
		ektParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		ektParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		ektParm.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
		ektParm.setData("SHOW_GREEN_USE", parm.getDouble("SHOW_GREEN_USE"));// 特批款扣款金额 REG_PATADM 同步更新
		ektParm.setData("OLD_GREEN_BALANCE", parm.getDouble("GREEN_BALANCE"));//原来特批款使用剩余金额
		ektParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//医保顺序号 add by huangtt 20160920		
		return ektParm;
	}

	/**
	 * 泰心医院扣费接口
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String 就诊号
	 * @param control
	 *            TControl
	 * @return TParm [RX_NO,ORDER_CODE,SEQ_NO,AMT,EXEC_FLG,RECEIPT_FLG,BILL_FLG]
	 *         OP_TYPE 0 医疗卡开关关闭 OP_TYPE 1 扣费 OP_TYPE 2 退费 OP_TYPE 3 取消保存动作
	 *         OP_TYPE 4 无费用操作 OP_TYPE 5 无卡能新增医嘱 OP_TYPE -1 调用参数错误或者不能在后台调用医疗卡
	 *         RX_LIST 处方列表
	 */
	public TParm onOPDAccntClient(TParm parm, String caseNo, TControl control, boolean isNull) {
		TParm result = new TParm();
		// 禁止在服务器端调用
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient 服务器端禁止调用本方法");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// 医疗卡流程是否启动
		if (!ektSwitch()) {
			result.setData("OP_TYPE", 0);
			return result;
		}
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm 参数为空");
			result.setData("OP_TYPE", -1);
			return result;
		}
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo 参数为空");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// =========pangben 20110919 修改医疗卡读卡操作
		// TParm readCard = getPat(false);
		TParm readCard = parm.getParm("ektParm");// 泰心医疗卡读卡操作
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("无卡".equals(readCard.getValue("ERRCode")))
				control.messageBox("无卡,新增医嘱保存未收费！");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",新增医嘱保存未收费！");
			result.setData("OP_TYPE", 5);
			return result;

		}

		if (readCard.getErrCode() != 0) {
			result.setData("OP_TYPE", -1);
			control.messageBox("医疗卡操作,中途失败");
			return result;
		}
		//parm.setData("CASE_NO", caseNo);
		// 泰心医院获得卡号
		String cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");//医疗卡金额
		String mrNo = parm.getValue("MR_NO");
		String businessType = parm.getValue("BUSINESS_TYPE");
		//查询类型获得门诊挂号REG,REGT和收费OPB,OPBT,ODO,ODOT
		String ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		String type_flg = parm.getValue("TYPE_FLG");// 退费操作
		String ins_flg = parm.getValue("INS_FLG");// 医保卡注记
		double insAmt = parm.getDouble("INS_AMT");// 医保金额
		String unFlg = parm.getValue("UN_FLG");// 医生修改的医嘱超过医疗卡金额执行的操作
		String tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("此卡片不属于该患者,新增医嘱保存未收费！");
				result.setData("OP_TYPE", 5);
				return result;
			}
			control.messageBox("此卡片不属于该患者!");
			result.setData("OP_TYPE", 3);
			return result;
		}
		int type = 1;
		double ektAMT = 0.00;// 医疗卡执行以后金额
		double ektOldAMT = 0.00;// 医疗卡原来金额，操作失败时使用
		String cancelTrede = null;// 扣款操作失败回滚医疗卡主档数据
		String opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// 医保卡回冲金额添加
		double opbAmt = 0.00;// 门诊医生站操作的金额
		double greenBalance = 0.00;// 绿色通道总扣款金额
		double greenPathTotal = 0.00;// 绿色通道审批金额

		String greenFlg = null;// 判断是否操作绿色通道，添加BIL_OPB_RECP
		// 表PAY_MEDICAL_CARD数据时需要判断为0时的操作
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control 参数为空");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			
			// 扣款金额
			double amt = 0.00;
			// 查询此就诊病患所有数据汇总金额
			//TParm orderSumParm=parm.getParm("orderSumParm");
			//查询此病患已收费未打票的所有数据汇总金额
			//TParm ektSumParm =parm.getParm("ektSumParm");
			amt=parm.getDouble("SHOW_AMT");//未收费的总金额 显示金额
			if (amt == 0 && isNull) {
				result.setData("OP_TYPE", 6);// 没有需要操作的数据
				return result;
			}
			if (amt != 0 || !isNull) {
				opbAmt = amt;
				p.setData("AMT", amt);
				p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//执行金额(EKT_TRADE 中此次 操作的金额)
				//未打票数据总金额
				readCard.setData("NAME", parm.getValue("NAME"));
				readCard.setData("SEX", parm.getValue("SEX"));
				p.setData("READ_CARD", readCard.getData());
				p.setData("BUSINESS_TYPE", businessType);// 扣款字段
				p.setData("EKT_TRADE_TYPE", ektTradeType);
				p.setData("TYPE_FLG", type_flg);// 退费注记
				p.setData("INS_FLG", ins_flg);// 医保注记
				p.setData("INS_AMT", insAmt);// 医保金额
				p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
				p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
				//p.setData("newParm", parm.getParm("newParm").getData());//操作医生站医嘱，需要操作的医嘱(增删改数据集合)
				TParm r = null;
				// 查询绿色通道使用金额
				TParm tempParm=new TParm();
				tempParm.setData("CASE_NO",caseNo);
				TParm greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
				if (greenParm.getErrCode() < 0) {
					result.setData("OP_TYPE", -1);
					return result;
				}
				p.setData("RE_PARM",parm.getData());//医生站医嘱参数，将UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX 写到一个事物
				p.setData("OPB_AMT", opbAmt);// 门诊医生站操作金额=====pangben 2013-7-17
				p.setData("ODO_EKT_FLG", "Y");// 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// 医疗卡中金额小于此次收费的金额
					if (null != unFlg && unFlg.equals("Y")) {// 医生修改医嘱操作
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// 绿色通道剩余金额
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// 绿色通道审批金额
						p.setData("unParm", parm.getParm("unParm").getData());
						p.setData("EKT_TYPE_FLG",3);//修改医嘱金额不足退回处方签中的金额
						p.setData("OPD_UN_FLG","Y");//操作修改OPD_ORDER表 ===pangben 2013-7-17
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewReAmt.x", p);
					} else {//不是修改医嘱，但是此次金额超过医疗卡金额 
						p.setData("EKT_TYPE_FLG",2);
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x", p);
					}
					if (null != unFlg && unFlg.equals("Y")) {
						result.setData("unParm", r.getData());
					}
				} else if(null!=parm.getValue("OPBEKTFEE_FLG")&&
						parm.getValue("OPBEKTFEE_FLG").equals("Y")&& 
						null != unFlg && unFlg.equals("Y")) {// 医疗卡金额充足:医生修改医嘱操作//医疗卡金额足够退回医疗卡金额操作
					//不显示扣款界面，直接操作=====pangben 2013-4-27 
					r = exeRefund(p);
				}else {
					r = (TParm) control.openDialog(
							"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
				}
				if (r == null) {
					// System.out.println("asdadasd");
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					result.setData("OP_TYPE", 3);
					return result;
				}
				type = r.getInt("OP_TYPE");
				// 余额不足
				if (type == 2) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (null == unFlg
						|| unFlg.equals("N")
						|| amt <= (oldAmt + greenParm.getDouble(
								"GREEN_BALANCE", 0))) {
					// cardNo = r.getValue("CARD_NO");
					// =========pangben 20111024 start
					ektAMT = r.getDouble("EKTNEW_AMT");// 现在医疗卡中的金额
					ektOldAMT = r.getDouble("OLD_AMT");
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // 收费金额
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // 扣医疗卡金额
						// greenUseAmt=r.getDouble("GREEN_USE");//绿色通道使用金额
						// ektUseAmt= r.getDouble("EKT_USE");//医疗卡使用金额
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // 扣绿色通道金额
					}
					greenBalance = r.getDouble("GREEN_BALANCE"); // 绿色通道未扣款金额
					greenPathTotal = r.getDouble("GREEN_PATH_TOTAL"); // 绿色通道总金额
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
			} 
		}
		
		result.setData("OPB_AMT", parm.getDouble("OPB_AMT"));
		result.setData("EKTNEW_AMT", parm.getDouble("EKTNEW_AMT"));// 医疗卡中金额
		result.setData("OLD_AMT", parm.getDouble("OLD_AMT"));// 医疗卡扣款之前金额
		result.setData("TRADE_NO", parm.getValue("TRADE_NO"));
		result.setData("EKTNEW_AMT", ektAMT);
		result.setData("OLD_AMT", ektOldAMT); // 医疗卡已经交易以后的金额
		result.setData("OP_TYPE", type);
		result.setData("TRADE_NO", tradeNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// 医生修改医嘱操作
		result.setData("CANCLE_TREDE", cancelTrede);// 撤销使用
		result.setData("OPB_AMT", opbAmt);// 门诊医生站操作金额
		result.setData("GREEN_FLG", greenFlg);// 添加BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance); // 绿色通道未扣款金额
		result.setData("GREEN_PATH_TOTAL", greenPathTotal); // 绿色通道总金额
		// 表数据时使用用来管控是否操作绿色通道
		return result;
	}
	/**
	 * ekt开关
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}
	/**
	 * 医疗卡开关
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}
	/**
	 * 执行退款操作 门急诊医生站 修改医嘱操作，退回医疗卡金额
	 */
	private TParm exeRefund(TParm sumParm){
		TParm parm = new TParm();
		TParm readCard = sumParm.getParm("READ_CARD");
		TParm result = new TParm();
		if (readCard.getErrCode() < 0) {
			// TParm parm = new TParm();
			parm.setErr(-1, "此医疗卡无效");
			return parm;
		}
		//String cardNo = readCard.getValue("CARD_NO");
		String caseNo = sumParm.getValue("CASE_NO");//就诊号
		String tradeSumNo=sumParm.getValue("TRADE_SUM_NO");//此次操作已经收费的内部交易号码,格式'xxx','xxx'
		parm.setData("CASE_NO", caseNo);
		double amt = sumParm.getDouble("AMT");//显示的金额
		String insFlg = sumParm.getValue("INS_FLG");// 医保卡注记
		double exeAmt = sumParm.getDouble("EXE_AMT");//医生站此次操作金额
		TParm reParm=sumParm.getParm("RE_PARM");//==pangben 2013-7-17 界面数据，将修改OPD_ORDER收费状态添加到一个事物里
		// 查询此次就诊病患是否存在医疗卡绿色通道
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// 查询绿色通道扣款金额、总充值金额
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// 扣款之后的金额
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// 未扣款金额
		}
		parm.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
		if (sumAmt < 0) {
			parm.setData("OP_TYPE", 2);
		} else {
			TParm cp = new TParm();
			cp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
			cp.setData("CASE_NO", caseNo);
			cp.setData("MR_NO",  sumParm.getValue("MR_NO"));
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			// cp.setData("BUSINESS_NO",businessNo);
			String ektTradeType = sumParm.getValue("EKT_TRADE_TYPE");// 查询条件
			String businessType = sumParm.getValue("BUSINESS_TYPE");// 保存数据参数
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//挂号使用 REG_PATADM 表没有TRADE_NO 字段 
			cp.setData("OLD_AMT", oldAmt);// 医疗卡金额
			cp.setData("NEW_AMT",sumAmt);
			cp.setData("INS_FLG", insFlg);// 医保卡注记
			// zhangp 20120106 加入seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// 序号
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			// 扣款
			if (amt >= 0) {
				onFee(cp, result, oldAmt, amt, sumAmt);
			} else {
				// 退费
				onUnFee(cp, result, oldAmt, amt, sumAmt);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//需要操作的交易金额
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			// 医疗卡金额大于此次扣款金额
			if (null != result && result.getCount() > 0) {
				onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// 医疗卡扣款金额
				cp.setData("GREEN_USE",0.00);//绿色通道扣款金额
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("GREEN_BALANCE",0.00);//特批款扣款金额
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
			cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 界面数据，将修改OPD_ORDER收费状态添加到一个事物里
			cp.setData("OPB_AMT",sumParm.getDouble("OPB_AMT"));// 门诊医生站操作金额=====pangben 2013-7-17
			cp.setData("ODO_EKT_FLG",sumParm.getValue("ODO_EKT_FLG"));// 门诊医生站操作,调用EKTChageUI.x界面区分医生站和门诊收费界面公用逻辑=====pangben 2013-7-17
			// 泰心医院扣款操作
			TParm p = new TParm(onNewSaveFee(
					cp.getData()));
			// TParm p = EKTIO.getInstance().consume(cp);
			if (p.getErrCode() < 0)
				parm.setErr(-1, p.getErrText());
			else {
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", p.getValue("TRADE_NO"));
				parm.setData("OLD_AMT", oldAmt);
				parm.setData("NEW_AMT",sumAmt);
				parm.setData("EKTNEW_AMT", cp.getDouble("EKT_AMT"));// 医疗卡中的金额
				parm.setData("CANCLE_TREDE", p.getValue("CANCLE_TREDE"));// 退费操作执行的医疗卡扣款主档TREDE_NO信息
				if (null != result && result.getCount() > 0) {
					parm.setData("AMT", amt < 0 ? amt * (-1) : amt); // 收费金额
					parm.setData("EKT_USE", cp.getDouble("EKT_USE")); // 扣医疗卡金额
					parm.setData("GREEN_USE", cp.getDouble("GREEN_USE")); // 扣绿色通道金额
					parm.setData("GREEN_FLG", "Y"); // 判断是否操作绿色通道，添加BIL_OPB_RECP
													// 表PAY_MEDICAL_CARD数据时需要判断为0时的操作
					parm.setData("GREEN_BALANCE", result.getDouble(
							"GREEN_BALANCE", 0)); // 绿色通道未扣款金额
					parm.setData("GREEN_PATH_TOTAL", result.getDouble(
							"GREEN_PATH_TOTAL", 0)); // 绿色通道总金额
				}	
			}
		}
		return parm;
	}
	/**
	 * 正流程收费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// 医疗卡绿色通道存在
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//医疗卡金额+医疗卡绿色钱包的金额
			if (oldAmt - amt >= 0) {
				cp.setData("EKT_AMT", oldAmt - amt); // 医疗卡金额充足
//				cp.setData("EKT_USE", amt);// 医疗卡扣款金额
				cp.setData("SHOW_GREEN_USE",0.00);//绿色通道扣款金额
			} else {
				cp.setData("EKT_AMT", 0.00); // 医疗卡金额不充足
//				cp.setData("EKT_USE", oldAmt);// 医疗卡扣款金额
			    cp.setData("SHOW_GREEN_USE",StringTool.round(amt - oldAmt,2));//绿色通道扣款金额
			}
			cp.setData("FLG", "Y"); // 绿色通道存在注记
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",0));
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT",sumAmt); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}
	}

	/**
	 * 逆流程退费
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// 医疗卡绿色通道存在
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//医疗卡金额+医疗卡绿色钱包的金额
			// 正常退费
			if (result.getDouble("GREEN_BALANCE", 0) >= result.getDouble(
					"GREEN_PATH_TOTAL", 0)) {
				cp.setData("EKT_AMT", oldAmt - amt); // 医疗卡金额退费充值
//				cp.setData("EKT_USE", -amt);// 医疗卡扣款金额
				cp.setData("SHOW_GREEN_USE",0.00);//绿色通道扣款金额
			} else {
				// 首先，医疗卡绿色钱包扣款金额充值 然后 绿色钱包扣款金额等于充值的金额以后，再去充值医疗卡
				double tempFee = result.getDouble("GREEN_BALANCE", 0) - amt;// 查看绿色钱包扣款金额+需要退费金额是否大于充值金额
				// 金额大于充值金额将补齐扣款金额
				if (tempFee > result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt + tempFee
							- result.getDouble("GREEN_PATH_TOTAL", 0));// 医疗卡中金额：补齐扣款金额以后的金额+医疗卡中金额
//					cp.setData("EKT_USE", tempFee
//							- result.getDouble("GREEN_PATH_TOTAL", 0));// 医疗卡扣款金额
					cp.setData("SHOW_GREEN_USE",StringTool.round(result.getDouble("GREEN_BALANCE", 0)-result.getDouble("GREEN_PATH_TOTAL", 0),2));//绿色通道扣款金额
				} else if (tempFee <= result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt);// 医疗卡中的金额不变
//					cp.setData("EKT_USE", 0.00);// 医疗卡扣款金额
					cp.setData("SHOW_GREEN_USE",amt);//绿色通道扣款金额
				}
			}
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",
					0));
			cp.setData("FLG", "Y"); // 绿色通道存在注记
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", sumAmt); // 没有医疗卡绿色钱包
			cp.setData("FLG", "N"); // 绿色通道不存在注记
		}

	}
	/**
	 * 医疗卡使用特批款计算金额
	 * ==========pangben 2013-7-26
	 */
	private void onGreenFeeTemp(String businessType,double oldAmt,double amt,
			TParm cp,double exeAmt,String caseNo,TParm result){
		//EKT_OLD_AMT: OLD_AMT 金额=原来金额+(此次扣款金额+特批款剩余金额-审批金额)
		double ektOldAmt=0.00;
		if(businessType.equals("REG") || businessType.equals("REGT")){//挂号操作
			ektOldAmt=oldAmt;
			cp.setData("EKT_OLD_AMT",ektOldAmt);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
			cp.setData("EKT_USE", ektOldAmt);// 医疗卡扣款金额
			cp.setData("GREEN_USE",exeAmt-ektOldAmt);//绿色通道扣款金额
		}else{
			TParm ektTradeParm=new TParm();
			ektTradeParm.setData("CASE_NO",caseNo);
			ektTradeParm.setData("EKT_TRADE_TYPE","'REG'");
			TParm ektSumTradeParm = EKTNewTool.getInstance().selectEktTrade(
					ektTradeParm);
			//剩余金额+此次执行的金额-显示金额<审批金额-挂号特批款使用金额
			ektOldAmt=oldAmt+result.getDouble("GREEN_BALANCE", 0)+exeAmt-amt;
			double greenPath=result.getDouble("GREEN_PATH_TOTAL", 0)-ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT",0);
			if(ektOldAmt>=greenPath){
				//存在一部分是医疗卡中的金额
				cp.setData("EKT_OLD_AMT",ektOldAmt-greenPath);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("EKT_USE", ektOldAmt-greenPath);// 医疗卡扣款金额
				cp.setData("GREEN_USE",exeAmt-ektOldAmt+greenPath);//绿色通道扣款金额
			}else{
				cp.setData("EKT_OLD_AMT",0.00);//医疗卡中在操作之前的金额 （将此次动到的处方签的所有金额 回冲 获得当前 医疗卡的金额）
				cp.setData("EKT_USE", 0.00);// 医疗卡扣款金额
				cp.setData("GREEN_USE",exeAmt);//绿色通道扣款金额
			}
		}
	}
	/**
	 * 获得特批款金额数据，添加EKT_TRADE 表
	 * ==========panben 2013-7-26
	 */
	public void onExeGreenFee(double oldAmt,double amt,String businessType,
			TParm cp,double exeAmt,String caseNo,TParm result){
		if (oldAmt - amt >= 0) {
			if (amt > 0) {//特批款操作，正常扣款流程，amt > 0 医疗卡中存在金额，此次扣款扣除医疗卡中的金额
				cp.setData("EKT_USE", exeAmt);// 医疗卡扣款金额
				cp.setData("GREEN_USE", 0.00);// 绿色通道扣款金额
				// EKT_OLD_AMT:EKT_TRADE 表中 OLD_AMT 金额=原来金额
				// +此次扣款金额(EXE_AMT)-显示金额(AMT)
				cp.setData("EKT_OLD_AMT", oldAmt + exeAmt - amt);// 医疗卡中在操作之前的金额（将此次动到的处方签的所有金额回冲获得当前医疗卡的金额）
			}else{//医疗卡金额不足 
				onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);	
			}
		} else {
			onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);
		}
		cp.setData("GREEN_BALANCE",result.getDouble("GREEN_BALANCE", 0));//特批款扣款金额
	}
	
	
	//=============================医疗卡充值退费数据add by kangy 20160804 
	public TParm EKTFee(TParm parm,TConnection connection,String businessNo){
		TParm updatanoParm=new TParm();
		updatanoParm=parm.getParm("updatanoparm");
		//System.out.println("io++++updatanoparm"+updatanoParm);
		TParm result=BILInvoiceTool.getInstance().updateDatePrint(updatanoParm,connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			//connection.close();
			return result;
		}
		TParm inFeeParm=new TParm();
		inFeeParm=parm.getParm("infeeparm");
		inFeeParm.setData("RECEIPT_NO",businessNo);
		//System.out.println("IO:::::"+inFeeParm);
		result=BILInvoiceTool.getInstance().insertFeeDate(inFeeParm,connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			//connection.close();
			return result;
		}
		return result;
	}
	
	public String[] updateOpdOrderEkt(TParm parm, TParm orderParm){
		// 收费
		TParm result = new TParm();
		TParm hl7Parm=parm.getParm("hl7Parm");//HL7发送接口集合
		String tradeNo="";
		String billDate="";
		String billUser="";
		String[] sql = null;
		
		if(orderParm.getCount("RX_NO") > 0){
			sql = new String[orderParm.getCount("RX_NO")];
			
			// 此次操作的医嘱
			for (int i = 0; i < orderParm.getCount("RX_NO"); i++) {
				if(null!=orderParm.getValue("BILL_FLG",i) && orderParm.getValue("BILL_FLG",i).equals("N")){
					tradeNo="";
					billDate="''";
					billUser="";
				}else{
					tradeNo=parm.getValue("TRADE_NO");
					billDate="SYSDATE";
					billUser=parm.getValue("OPT_USER");
				}
				sql[i] = "UPDATE OPD_ORDER SET BUSINESS_NO='"
						+ tradeNo + "', BILL_FLG='"+orderParm.getValue("BILL_FLG",i)+"' "
						+ " , BILL_DATE="+billDate+",BILL_USER='"
						+ billUser
						+ "',BILL_TYPE='"+orderParm.getValue("BILL_TYPE",i)+"' WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
						+ orderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
						+ orderParm.getValue("SEQ_NO", i) + "' ";
				
			}
		}

		String[] hl7Sql=null;
		if(hl7Parm.getCount("RX_NO")>0){
			hl7Sql = new String[hl7Parm.getCount("RX_NO")];
			
			for (int i = 0; i < hl7Parm.getCount("RX_NO"); i++) {
				// 修改检验检查收费状态
				hl7Sql[i] = "UPDATE MED_APPLY SET BILL_FLG='" + hl7Parm.getValue("BILL_FLG",i)
						+ "' WHERE APPLICATION_NO='"
						+ hl7Parm.getValue("MED_APPLY_NO", i)
						+ "' AND CASE_NO='" + parm.getValue("CASE_NO")
						+ "' AND ORDER_NO='"
						+ hl7Parm.getValue("RX_NO", i)
						+ "' AND SEQ_NO='"
						+ hl7Parm.getValue("SEQ_NO", i) + "'";
				
			}
			
		}

		if(sql == null){
			if(hl7Sql == null){
				return null;
			}else{
				sql = hl7Sql;
			}
		}else{
			if(hl7Sql != null){
				sql = StringTool.copyArray(sql, hl7Sql);
			}
		}
		
		return sql;
	
	}
	
	public String getEktMasterHistorySql(TParm tempEktParm,String ektSeq){
		int seq = 0;
		
		if(ektSeq.length() == 0){
			
			TParm seqParm = new TParm(
					TJDODBTool.getInstance().select(
							"SELECT MAX(SEQ_NO) SEQ FROM EKT_MASTER_HISTORY WHERE MR_NO='"+tempEktParm.getValue("MR_NO")+"'"));
			
			
			if(seqParm.getInt("SEQ",0) > 0){
				seq = seqParm.getInt("SEQ",0)+ 1;
			}else{
				seq = 1;
			}
		}else{
			seq = Integer.parseInt(ektSeq);
		}
		
		
		
		
		
		String sql="INSERT INTO EKT_MASTER_HISTORY " +
		" (MR_NO,SEQ_NO,CHANGE_DATE,LATEST_AMT,BUSSINESS_AMT," +
		" CURRENT_AMT,OPT_TYPE,CARD_NO,OPT_USER,OPT_DATE," +
		" OPT_TERM,HISTORY_NO,CASE_NO,EKT_BUSINESS_NO,MZCONFIRM_NO) " +
		" VALUES " +
		" ('"+tempEktParm.getValue("MR_NO")+"','"+seq+"',SYSDATE, "+tempEktParm.getDouble("OLD_AMT")+", "+tempEktParm.getDouble("AMT")+" ," +
		"  "+tempEktParm.getDouble("EKT_AMT")+",'"+tempEktParm.getValue("BUSINESS_TYPE")+"','"+tempEktParm.getValue("CARD_NO")+"','"+tempEktParm.getValue("OPT_USER")+"',SYSDATE," +
		"  '"+tempEktParm.getValue("OPT_TERM")+"','"+tempEktParm.getValue("HISTORY_NO")+"','"+tempEktParm.getValue("CASE_NO")+"','"+tempEktParm.getValue("EKT_BUSINESS_NO")+"','"+tempEktParm.getValue("CONFIRM_NO")+"')";
//		System.out.println("getEktMasterHistorySql==="+sql);
		return sql;
	}
	
	public boolean ektIsClientlink(){
		// 禁止在服务器端调用
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient 服务器端禁止调用本方法");
			
			return true;
		}
		return false;
	}
	
	public TParm getEktMasterHistoryParm(TParm parm){
		TParm result = new TParm();
		result.setData("MR_NO", parm.getData("MR_NO"));
		result.setData("OLD_AMT", parm.getDouble("ORIGINAL_BALANCE"));
		result.setData("AMT", parm.getDouble("BUSINESS_AMT"));
		result.setData("EKT_AMT", parm.getDouble("CURRENT_BALANCE"));
		// 4 制卡   5补卡    8换卡    3充值   7退费
		int type = parm.getInt("CHARGE_FLG");
		String bussinessType = "";
		switch(type){
			case 4:
				bussinessType="EKT_ISSUE";
				break;
			case 5:
				bussinessType="EKT_REISSUE";
				break;
			case 8:
				bussinessType="EKT_CHANGE";
				break;
			case 3:
				bussinessType="EKT_IN";
				break;
			case 7:
				bussinessType="EKT_OUT";
				break;
		}
		 
		
		result.setData("BUSINESS_TYPE", bussinessType);
		result.setData("CARD_NO", parm.getValue("CARD_NO"));
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result.setData("CASE_NO", "");
		result.setData("CONFIRM_NO", "");		
		result.setData("EKT_BUSINESS_NO", parm.getValue("BUSINESS_NO"));
		
		String historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
		if (null==historyNo||historyNo.length()<=0) {
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// 得到医疗卡外部交易号
		}
		result.setData("HISTORY_NO", historyNo);
		
		return result;
		
	}
	

}
