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
	 * ʵ��
	 */
	private static EKTNewIO instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTIO
	 */
	public static EKTNewIO getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTNewIO();
		return instanceObject;
	}
	/**
	 * ҽ����Ʊ���� ��ȡҽ�ƿ����
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
		String tredeNo = "";//����δ�շѵ�ҽ����Ҫ�޸�OPD_ORDER ���� �������
		String historyNo ="";
		String ektSeq="";
		//����δ�շѵ�ҽ�� ��Ҫ����һ������
		if(parm.getDouble("billAmt")!=0){
			tredeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
			ektTradeParm = getEktTrateParm(parm,tredeNo);
			// ���Ӵ˴β����ۿ���
			result = EKTNewTool.getInstance().insertEktTrade(ektTradeParm,
					connection);
			if (getError(result, connection).getErrCode() < 0) {
				return result.getData();
			}
			//add by huangtt 20160920����ҽ�ƿ���ʷ�� start
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
			if (null==historyNo||historyNo.length()<=0) {
				historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// �õ�ҽ�ƿ��ⲿ���׺�
			}
			ektTradeParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
			
			BigDecimal oldAmt  =new BigDecimal( ektTradeParm.getDouble("OLD_AMT"));
			BigDecimal amt  =new BigDecimal(ektTradeParm.getDouble("AMT"));
			BigDecimal ektAmt = oldAmt.subtract(amt);

//			ektTradeParm.setData("EKT_AMT",  ektTradeParm.getDouble("OLD_AMT")-ektTradeParm.getDouble("AMT"));// �ڲ����׺�
			ektTradeParm.setData("EKT_AMT",  ektAmt.doubleValue());// �ڲ����׺�

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
			//add by huangtt 20160920����ҽ�ƿ���ʷ�� end 
			
			
		}
		ektTradeParm = getEktNewTrateParm(parm);
		//ҽ�ƿ��շ��˷ѹ��ò���
		ektTradeParm.setData("ektSeq", ektSeq);
		result =insEktOnFee(parm, ektTradeParm, connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		// ɾ����ʷ��¼
		result = EKTNewTool.getInstance().deleteEKTMaster(parm, connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		TParm ektMasterParm = new TParm();
		ektMasterParm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));// ҽ�ƿ����
		ektMasterParm.setData("CARD_NO", parm.getValue("CARD_NO"));// ����
		ektMasterParm.setData("ID_NO", parm.getValue("IDNO"));// ID
		ektMasterParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		ektMasterParm.setData("NAME", parm.getValue("PAT_NAME"));// ��������
		ektMasterParm.setData("CREAT_USER", parm.getValue("OPT_USER"));// ִ����Ա
		ektMasterParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������Ա
		ektMasterParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		// �������ݻ������ҽ�ƿ����
		result = EKTNewTool.getInstance().insertEKTMaster(ektMasterParm,
				connection);
		if (getError(result, connection).getErrCode() < 0) {
			return result.getData();
		}
		// ������ɫͨ�����
//		if (parm.getValue("FLG").equals("Y")) {
//			// ���¹Һ�����������ɫͨ�����
//			result = PatAdmTool.getInstance().updateEKTGreen1(
//					ektParmTemp(ektTradeParm), connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result.getData();
//			}
//		}
		if (parm.getDouble("billAmt") != 0) {
			ektTradeParm.setData("TRADE_NO", tredeNo);// �ڲ����׺�
			ektTradeParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
		}
		connection.commit();
		connection.close();
		return ektTradeParm.getData();
	}
	/**
	 * ҽ�ƿ��շ������޸�
	 */
	public Map onNewSaveFee(Map tempParm) {
		TParm parm = new TParm(tempParm);
		//===================================add by huangjw 20150710
//		double initAmt=0.00;//�ڳ����
//		double lastAmt=0.00;//��ĩ���
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
		//ҽ�ƿ��շ��˷ѹ��ò���  , connection
		result =ektOnFee(parm, ektTradeParm);
		String[] tempSql = (String[]) result.getData("SQL");
		if(tempSql != null){
			sql = tempSql;
		}
//		if (getError(result, connection).getErrCode() < 0) {
//			return result.getData();
//		}
		// ɾ����ʷ��¼
//		result = EKTNewTool.getInstance().deleteEKTMaster(parm, connection);
//		if (getError(result, connection).getErrCode() < 0) {
//			return result.getData();
//		}
		TParm ektMasterParm = new TParm();
		ektMasterParm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));// ҽ�ƿ����
		ektMasterParm.setData("CARD_NO", parm.getValue("CARD_NO"));// ����
		ektMasterParm.setData("ID_NO", parm.getValue("IDNO"));// ID
		ektMasterParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		ektMasterParm.setData("NAME", parm.getValue("PAT_NAME"));// ��������
		ektMasterParm.setData("CREAT_USER", parm.getValue("OPT_USER"));// ִ����Ա
		ektMasterParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������Ա
		ektMasterParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		// �������ݻ������ҽ�ƿ����
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
		// ������ɫͨ�����
		if (parm.getValue("FLG").equals("Y")) {
			// ���¹Һ�����������ɫͨ�����
			
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
		//============pangben 2013-7-17 �޸�OPD_ORDER �շ�״̬��OPD_UN_FLG=Y �˴οۿ������ҽ�ƿ����׺��벻����
		//ODO_EKT_FLG='Y' ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
		if (null!=parm.getValue("ODO_EKT_FLG")&&parm.getValue("ODO_EKT_FLG").equals("Y")) {
			if (null==parm.getValue("OPD_UN_FLG")||parm.getValue("OPD_UN_FLG").length()<=0||
					parm.getValue("OPD_UN_FLG").equals("N")) {
				if (null==ektTradeParm.getValue("TRADE_NO")||ektTradeParm.getValue("TRADE_NO").length()<=0) {
					result=new TParm();
					result.setErr(-1,"û�л�ý��׺���");
//					connection.close();
					return result.getData();
				}
				parm.setData("TRADE_NO",ektTradeParm.getValue("TRADE_NO"));// �ڲ����׺�
			}
			TParm billSetsParm=EKTTool.getInstance().getBillSets(parm, parm.getParm("RE_PARM"));
			TParm orderParm = billSetsParm.getParm("orderParm"); // ��Ҫ������ҽ��
			
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
		//============================================ҽ�ƿ���־����add by huangjw 20150710 start
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
//		if(queryParm.getCount()>0){//��������
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//�ڳ����
//			logParm.setData("LAST_AMT",lastAmt);//��ĩ���
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
//			
//		}else{//��������
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			logParm.setData("INIT_AMT",initAmt);//�ڳ����
//			logParm.setData("LAST_AMT",lastAmt);//��ĩ���
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
		//============================================ҽ�ƿ���־����add by huangjw 20150710 end
//		connection.commit();
//		connection.close();
		return ektTradeParm.getData();
	}
	/**
	 * ҽ��ҽ�ƿ���Ʊ����
	 * @return
	 */
	public TParm unInsOpbReceiptNo(TParm parm,TConnection connection){
		TParm ektTradeParm = getEktNewTrateParm(parm);
		//����Ʊ���� �۳�ҽ�ƿ�����ѯ�˲������վݺ������еĲ�ͬ���ڲ����׺�
//		TParm ektSumTradeParm = EKTNewTool.getInstance().selectBusinessByReceiptNo(
//				parm);
//		if (ektSumTradeParm.getErrCode() < 0) {
//			return ektSumTradeParm;
//		}
//		StringBuffer tradeNo=new StringBuffer();
//		for (int i = 0; i < ektSumTradeParm.getCount(); i++) {
//			if(!tradeNo.toString().contains(ektSumTradeParm.getValue("BUSINESS_NO", i))){
//				tradeNo.append("'").append(ektSumTradeParm.getValue("BUSINESS_NO", i)).append("',");//UPDATE EKT_TRADE ��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
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
	 * ҽ�ƿ��շ��˷ѹ��ò���
	 * @param parm
	 * @return
	 *   ,TConnection connection
	 */
	private TParm ektOnFee(TParm parm,TParm ektTradeParm){
		// �����ʷ��¼��ѯ�˲�������δ��Ʊ�������ܽ��
		//�ҺŲ���
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
			//��������   , connection
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
			
		} else {//�շѲ���
			//ҽ��վ����ɾ�����ݲ�ѯ ͨ������ǩ��ѯ����Ҫ������ҽ��
			ektSumTradeParm=EKTNewTool.getInstance().selectTradeNo(parm);
			if (ektSumTradeParm.getErrCode() < 0) {
				return ektSumTradeParm;
			}
			//��������  , connection
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
				&& !"REGT".equals(ektTradeParm.getValue("BUSINESS_TYPE"))) {// �˹Ҳ�����ִ��������������
			// ���Ӵ˴β����ۿ���
			
			
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
	 * ҽ�����������˷�����
	 * @param parm
	 * @param ektTradeParm
	 * @param connection
	 * @return
	 */
	private TParm insEktOnFee(TParm parm,TParm ektTradeParm,TConnection connection){
		TParm result =new TParm();
		if (ektTradeParm.getDouble("AMT")
				+ ektTradeParm.getDouble("GREEN_BUSINESS_AMT") != 0
				&& !"REGT".equals(ektTradeParm.getValue("BUSINESS_TYPE"))) {// �˹Ҳ�����ִ��������������
			// ���Ӵ˴β����ۿ���
			result = EKTNewTool.getInstance().insertEktTrade(ektTradeParm,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			
			//add by huangtt 20160920����ҽ�ƿ���ʷ�� start
			BigDecimal oldAmt  =new BigDecimal(ektTradeParm.getDouble("OLD_AMT"));
			BigDecimal amt  =new BigDecimal(ektTradeParm.getDouble("AMT"));
			BigDecimal ektAmt = oldAmt.subtract(amt);

//			ektTradeParm.setData("EKT_AMT",  ektTradeParm.getDouble("OLD_AMT")-ektTradeParm.getDouble("AMT"));// �ڲ����׺�
			ektTradeParm.setData("EKT_AMT",  ektAmt.doubleValue());// �ڲ����׺�
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
			
			//add by huangtt 20160920����ҽ�ƿ���ʷ�� end
			
			
		}
		
		return result;
	}
	/**
	 * ���������޸�״̬    ,TConnection connection
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
			// ����һ��������
			TParm tempEktParm=ektSumTradeParm.getRow(i);
//			if(tempEktParm.getValue("BUSINESS_TYPE").equals("OPB"))
//				tempEktParm.setData("BUSINESS_TYPE","OPBT");
//			else if(tempEktParm.getValue("BUSINESS_TYPE").equals("ODO"))
//				tempEktParm.setData("BUSINESS_TYPE","ODOT");
//			else if(tempEktParm.getValue("BUSINESS_TYPE").equals("REG"))
//				tempEktParm.setData("BUSINESS_TYPE","REGT");
//			String tredeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
//			result = EKTNewTool.getInstance().insertEktTrade(
//					getEktTrateParm(tempEktParm,tredeNo), connection);
//			if (getError(result, connection).getErrCode() < 0) {
//				return result;
//			}
//			tempEktParm=ektSumTradeParm.getRow(i);
//			tempEktParm.setData("RESET_TRADE_NO",tredeNo);//���Ϻ���
			tempEktParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������Ա
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
			
			tempEktParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������Ա
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
	 * ҽ�ƿ� ��ɫͨ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 20111009
	 */
	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
//		ektParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
//		// ��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
//		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(ektParm);
//		if (ektSumParm.getCount() > 0) {
//			// ��ô˾��ﲡ����ɫͨ�����ۿ���
//			ektParm.setData("GREEN_BALANCE", StringTool.round(parm
//					.getDouble("GREEN_BALANCE")
//					- parm.getDouble("GREEN_BUSINESS_AMT")
//					- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0), 2));
//		} else {
//			// ��ô˾��ﲡ����ɫͨ�����ۿ���
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
	 * ����
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
	 * ��ÿۿ���������ϲ���
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getEktTrateParm(TParm parm,String tradeNo) {
		
		TParm ektParm = new TParm();
		ektParm.setData("TRADE_NO", tradeNo);// �ڲ����׺�
		ektParm.setData("CARD_NO", parm.getValue("CARD_NO"));// ����
		ektParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
		ektParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// ��������
		ektParm.setData("OLD_AMT", parm.getDouble("EKT_OLD_AMT"));// ԭ�����
		ektParm.setData("AMT", parm.getDouble("billAmt"));// �ۿ���
		ektParm.setData("STATE", "1");// ״̬(1,�ۿ�;2,�˷�,3,����)
		ektParm.setData("BUSINESS_TYPE","OPB");// ���
		ektParm.setData("GREEN_BALANCE", 0.00);// �����ܽ��
		ektParm.setData("GREEN_BUSINESS_AMT", 0.00);// ������ۿ���
		ektParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		ektParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		ektParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//ҽ��˳��� add by huangtt 20160920
		parm.setData("EKT_OLD_AMT",parm.getDouble("EKT_OLD_AMT")-parm.getDouble("billAmt"));//��ʾ��ǰҽ�ƿ��еĽ��
		return ektParm;
	}

	/**
	 * �˲����˴ξ���ҽ���ܽ��
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getEktNewTrateParm(TParm parm) {
		String tredeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
		if (null==tredeNo||tredeNo.length()<=0) {
			System.out.println("���ҽ�ƿ����׺����������:"+tredeNo);
			tredeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
		}
		
		String historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
		if (null==historyNo||historyNo.length()<=0) {
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// �õ�ҽ�ƿ��ⲿ���׺�
		}
		
		TParm ektParm = new TParm();
		ektParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
		ektParm.setData("TRADE_NO", tredeNo);// �ڲ����׺�
		ektParm.setData("CARD_NO", parm.getValue("CARD_NO"));// ����
		ektParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
		ektParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// ��������
		ektParm.setData("OLD_AMT", parm.getDouble("EKT_OLD_AMT"));// ԭ�����
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			ektParm.setData("AMT", parm.getDouble("AMT"));// �ۿ���
		}else{
			ektParm.setData("AMT", parm.getDouble("EKT_USE"));// �ۿ���
		}
		ektParm.setData("STATE", "1");// ״̬(1,�ۿ�ͽ��׽���;2,�˷�,3����)
		ektParm.setData("BUSINESS_TYPE", parm.getValue("BUSINESS_TYPE"));// ���
		ektParm.setData("GREEN_BALANCE", parm.getDouble("GREEN_PATH_TOTAL"));// �����ܽ��
		ektParm.setData("GREEN_BUSINESS_AMT", parm.getDouble("GREEN_USE"));// ������ۿ���
		ektParm.setData("EKT_TRADE_TYPE", parm.getValue("EKT_TRADE_TYPE"));// ��ѯ����
		//ektParm.setData("CANCEL_FLG", "0");// ���ϱ�־0:����1: ����2:����Ʊ��3:��Ʊ����4:���ϲ�ӡ
		ektParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		ektParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		ektParm.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
		ektParm.setData("SHOW_GREEN_USE", parm.getDouble("SHOW_GREEN_USE"));// ������ۿ��� REG_PATADM ͬ������
		ektParm.setData("OLD_GREEN_BALANCE", parm.getDouble("GREEN_BALANCE"));//ԭ��������ʹ��ʣ����
		ektParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//ҽ��˳��� add by huangtt 20160920		
		return ektParm;
	}

	/**
	 * ̩��ҽԺ�۷ѽӿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String �����
	 * @param control
	 *            TControl
	 * @return TParm [RX_NO,ORDER_CODE,SEQ_NO,AMT,EXEC_FLG,RECEIPT_FLG,BILL_FLG]
	 *         OP_TYPE 0 ҽ�ƿ����عر� OP_TYPE 1 �۷� OP_TYPE 2 �˷� OP_TYPE 3 ȡ�����涯��
	 *         OP_TYPE 4 �޷��ò��� OP_TYPE 5 �޿�������ҽ�� OP_TYPE -1 ���ò���������߲����ں�̨����ҽ�ƿ�
	 *         RX_LIST �����б�
	 */
	public TParm onOPDAccntClient(TParm parm, String caseNo, TControl control, boolean isNull) {
		TParm result = new TParm();
		// ��ֹ�ڷ������˵���
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient �������˽�ֹ���ñ�����");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// ҽ�ƿ������Ƿ�����
		if (!ektSwitch()) {
			result.setData("OP_TYPE", 0);
			return result;
		}
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm ����Ϊ��");
			result.setData("OP_TYPE", -1);
			return result;
		}
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo ����Ϊ��");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// =========pangben 20110919 �޸�ҽ�ƿ���������
		// TParm readCard = getPat(false);
		TParm readCard = parm.getParm("ektParm");// ̩��ҽ�ƿ���������
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("�޿�".equals(readCard.getValue("ERRCode")))
				control.messageBox("�޿�,����ҽ������δ�շѣ�");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",����ҽ������δ�շѣ�");
			result.setData("OP_TYPE", 5);
			return result;

		}

		if (readCard.getErrCode() != 0) {
			result.setData("OP_TYPE", -1);
			control.messageBox("ҽ�ƿ�����,��;ʧ��");
			return result;
		}
		//parm.setData("CASE_NO", caseNo);
		// ̩��ҽԺ��ÿ���
		String cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");//ҽ�ƿ����
		String mrNo = parm.getValue("MR_NO");
		String businessType = parm.getValue("BUSINESS_TYPE");
		//��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
		String ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		String type_flg = parm.getValue("TYPE_FLG");// �˷Ѳ���
		String ins_flg = parm.getValue("INS_FLG");// ҽ����ע��
		double insAmt = parm.getDouble("INS_AMT");// ҽ�����
		String unFlg = parm.getValue("UN_FLG");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		String tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("�˿�Ƭ�����ڸû���,����ҽ������δ�շѣ�");
				result.setData("OP_TYPE", 5);
				return result;
			}
			control.messageBox("�˿�Ƭ�����ڸû���!");
			result.setData("OP_TYPE", 3);
			return result;
		}
		int type = 1;
		double ektAMT = 0.00;// ҽ�ƿ�ִ���Ժ���
		double ektOldAMT = 0.00;// ҽ�ƿ�ԭ��������ʧ��ʱʹ��
		String cancelTrede = null;// �ۿ����ʧ�ܻع�ҽ�ƿ���������
		String opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// ҽ�����س�������
		double opbAmt = 0.00;// ����ҽ��վ�����Ľ��
		double greenBalance = 0.00;// ��ɫͨ���ܿۿ���
		double greenPathTotal = 0.00;// ��ɫͨ���������

		String greenFlg = null;// �ж��Ƿ������ɫͨ��������BIL_OPB_RECP
		// ��PAY_MEDICAL_CARD����ʱ��Ҫ�ж�Ϊ0ʱ�Ĳ���
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control ����Ϊ��");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			
			// �ۿ���
			double amt = 0.00;
			// ��ѯ�˾��ﲡ���������ݻ��ܽ��
			//TParm orderSumParm=parm.getParm("orderSumParm");
			//��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
			//TParm ektSumParm =parm.getParm("ektSumParm");
			amt=parm.getDouble("SHOW_AMT");//δ�շѵ��ܽ�� ��ʾ���
			if (amt == 0 && isNull) {
				result.setData("OP_TYPE", 6);// û����Ҫ����������
				return result;
			}
			if (amt != 0 || !isNull) {
				opbAmt = amt;
				p.setData("AMT", amt);
				p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//ִ�н��(EKT_TRADE �д˴� �����Ľ��)
				//δ��Ʊ�����ܽ��
				readCard.setData("NAME", parm.getValue("NAME"));
				readCard.setData("SEX", parm.getValue("SEX"));
				p.setData("READ_CARD", readCard.getData());
				p.setData("BUSINESS_TYPE", businessType);// �ۿ��ֶ�
				p.setData("EKT_TRADE_TYPE", ektTradeType);
				p.setData("TYPE_FLG", type_flg);// �˷�ע��
				p.setData("INS_FLG", ins_flg);// ҽ��ע��
				p.setData("INS_AMT", insAmt);// ҽ�����
				p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
				p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
				//p.setData("newParm", parm.getParm("newParm").getData());//����ҽ��վҽ������Ҫ������ҽ��(��ɾ�����ݼ���)
				TParm r = null;
				// ��ѯ��ɫͨ��ʹ�ý��
				TParm tempParm=new TParm();
				tempParm.setData("CASE_NO",caseNo);
				TParm greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
				if (greenParm.getErrCode() < 0) {
					result.setData("OP_TYPE", -1);
					return result;
				}
				p.setData("RE_PARM",parm.getData());//ҽ��վҽ����������UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX д��һ������
				p.setData("OPB_AMT", opbAmt);// ����ҽ��վ�������=====pangben 2013-7-17
				p.setData("ODO_EKT_FLG", "Y");// ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// ҽ�ƿ��н��С�ڴ˴��շѵĽ��
					if (null != unFlg && unFlg.equals("Y")) {// ҽ���޸�ҽ������
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// ��ɫͨ��ʣ����
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// ��ɫͨ���������
						p.setData("unParm", parm.getParm("unParm").getData());
						p.setData("EKT_TYPE_FLG",3);//�޸�ҽ�������˻ش���ǩ�еĽ��
						p.setData("OPD_UN_FLG","Y");//�����޸�OPD_ORDER�� ===pangben 2013-7-17
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewReAmt.x", p);
					} else {//�����޸�ҽ�������Ǵ˴ν���ҽ�ƿ���� 
						p.setData("EKT_TYPE_FLG",2);
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x", p);
					}
					if (null != unFlg && unFlg.equals("Y")) {
						result.setData("unParm", r.getData());
					}
				} else if(null!=parm.getValue("OPBEKTFEE_FLG")&&
						parm.getValue("OPBEKTFEE_FLG").equals("Y")&& 
						null != unFlg && unFlg.equals("Y")) {// ҽ�ƿ�������:ҽ���޸�ҽ������//ҽ�ƿ�����㹻�˻�ҽ�ƿ�������
					//����ʾ�ۿ���棬ֱ�Ӳ���=====pangben 2013-4-27 
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
				// ����
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
					ektAMT = r.getDouble("EKTNEW_AMT");// ����ҽ�ƿ��еĽ��
					ektOldAMT = r.getDouble("OLD_AMT");
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // �շѽ��
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // ��ҽ�ƿ����
						// greenUseAmt=r.getDouble("GREEN_USE");//��ɫͨ��ʹ�ý��
						// ektUseAmt= r.getDouble("EKT_USE");//ҽ�ƿ�ʹ�ý��
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // ����ɫͨ�����
					}
					greenBalance = r.getDouble("GREEN_BALANCE"); // ��ɫͨ��δ�ۿ���
					greenPathTotal = r.getDouble("GREEN_PATH_TOTAL"); // ��ɫͨ���ܽ��
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
			} 
		}
		
		result.setData("OPB_AMT", parm.getDouble("OPB_AMT"));
		result.setData("EKTNEW_AMT", parm.getDouble("EKTNEW_AMT"));// ҽ�ƿ��н��
		result.setData("OLD_AMT", parm.getDouble("OLD_AMT"));// ҽ�ƿ��ۿ�֮ǰ���
		result.setData("TRADE_NO", parm.getValue("TRADE_NO"));
		result.setData("EKTNEW_AMT", ektAMT);
		result.setData("OLD_AMT", ektOldAMT); // ҽ�ƿ��Ѿ������Ժ�Ľ��
		result.setData("OP_TYPE", type);
		result.setData("TRADE_NO", tradeNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// ҽ���޸�ҽ������
		result.setData("CANCLE_TREDE", cancelTrede);// ����ʹ��
		result.setData("OPB_AMT", opbAmt);// ����ҽ��վ�������
		result.setData("GREEN_FLG", greenFlg);// ����BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance); // ��ɫͨ��δ�ۿ���
		result.setData("GREEN_PATH_TOTAL", greenPathTotal); // ��ɫͨ���ܽ��
		// ������ʱʹ�������ܿ��Ƿ������ɫͨ��
		return result;
	}
	/**
	 * ekt����
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}
	/**
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}
	/**
	 * ִ���˿���� �ż���ҽ��վ �޸�ҽ���������˻�ҽ�ƿ����
	 */
	private TParm exeRefund(TParm sumParm){
		TParm parm = new TParm();
		TParm readCard = sumParm.getParm("READ_CARD");
		TParm result = new TParm();
		if (readCard.getErrCode() < 0) {
			// TParm parm = new TParm();
			parm.setErr(-1, "��ҽ�ƿ���Ч");
			return parm;
		}
		//String cardNo = readCard.getValue("CARD_NO");
		String caseNo = sumParm.getValue("CASE_NO");//�����
		String tradeSumNo=sumParm.getValue("TRADE_SUM_NO");//�˴β����Ѿ��շѵ��ڲ����׺���,��ʽ'xxx','xxx'
		parm.setData("CASE_NO", caseNo);
		double amt = sumParm.getDouble("AMT");//��ʾ�Ľ��
		String insFlg = sumParm.getValue("INS_FLG");// ҽ����ע��
		double exeAmt = sumParm.getDouble("EXE_AMT");//ҽ��վ�˴β������
		TParm reParm=sumParm.getParm("RE_PARM");//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬���ӵ�һ��������
		// ��ѯ�˴ξ��ﲡ���Ƿ����ҽ�ƿ���ɫͨ��
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// ��ѯ��ɫͨ���ۿ���ܳ�ֵ���
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// �ۿ�֮��Ľ��
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// δ�ۿ���
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
			String ektTradeType = sumParm.getValue("EKT_TRADE_TYPE");// ��ѯ����
			String businessType = sumParm.getValue("BUSINESS_TYPE");// �������ݲ���
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//�Һ�ʹ�� REG_PATADM ��û��TRADE_NO �ֶ� 
			cp.setData("OLD_AMT", oldAmt);// ҽ�ƿ����
			cp.setData("NEW_AMT",sumAmt);
			cp.setData("INS_FLG", insFlg);// ҽ����ע��
			// zhangp 20120106 ����seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// ���
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			// �ۿ�
			if (amt >= 0) {
				onFee(cp, result, oldAmt, amt, sumAmt);
			} else {
				// �˷�
				onUnFee(cp, result, oldAmt, amt, sumAmt);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//��Ҫ�����Ľ��׽��
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			// ҽ�ƿ������ڴ˴οۿ���
			if (null != result && result.getCount() > 0) {
				onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",0.00);//��ɫͨ���ۿ���
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("GREEN_BALANCE",0.00);//������ۿ���
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
			cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬���ӵ�һ��������
			cp.setData("OPB_AMT",sumParm.getDouble("OPB_AMT"));// ����ҽ��վ�������=====pangben 2013-7-17
			cp.setData("ODO_EKT_FLG",sumParm.getValue("ODO_EKT_FLG"));// ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
			// ̩��ҽԺ�ۿ����
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
				parm.setData("EKTNEW_AMT", cp.getDouble("EKT_AMT"));// ҽ�ƿ��еĽ��
				parm.setData("CANCLE_TREDE", p.getValue("CANCLE_TREDE"));// �˷Ѳ���ִ�е�ҽ�ƿ��ۿ�����TREDE_NO��Ϣ
				if (null != result && result.getCount() > 0) {
					parm.setData("AMT", amt < 0 ? amt * (-1) : amt); // �շѽ��
					parm.setData("EKT_USE", cp.getDouble("EKT_USE")); // ��ҽ�ƿ����
					parm.setData("GREEN_USE", cp.getDouble("GREEN_USE")); // ����ɫͨ�����
					parm.setData("GREEN_FLG", "Y"); // �ж��Ƿ������ɫͨ��������BIL_OPB_RECP
													// ��PAY_MEDICAL_CARD����ʱ��Ҫ�ж�Ϊ0ʱ�Ĳ���
					parm.setData("GREEN_BALANCE", result.getDouble(
							"GREEN_BALANCE", 0)); // ��ɫͨ��δ�ۿ���
					parm.setData("GREEN_PATH_TOTAL", result.getDouble(
							"GREEN_PATH_TOTAL", 0)); // ��ɫͨ���ܽ��
				}	
			}
		}
		return parm;
	}
	/**
	 * �������շ�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			if (oldAmt - amt >= 0) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�������
//				cp.setData("EKT_USE", amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				cp.setData("EKT_AMT", 0.00); // ҽ�ƿ�������
//				cp.setData("EKT_USE", oldAmt);// ҽ�ƿ��ۿ���
			    cp.setData("SHOW_GREEN_USE",StringTool.round(amt - oldAmt,2));//��ɫͨ���ۿ���
			}
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",0));
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT",sumAmt); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}
	}

	/**
	 * �������˷�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			// �����˷�
			if (result.getDouble("GREEN_BALANCE", 0) >= result.getDouble(
					"GREEN_PATH_TOTAL", 0)) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�����˷ѳ�ֵ
//				cp.setData("EKT_USE", -amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				// ���ȣ�ҽ�ƿ���ɫǮ���ۿ����ֵ Ȼ�� ��ɫǮ���ۿ�����ڳ�ֵ�Ľ���Ժ���ȥ��ֵҽ�ƿ�
				double tempFee = result.getDouble("GREEN_BALANCE", 0) - amt;// �鿴��ɫǮ���ۿ���+��Ҫ�˷ѽ���Ƿ���ڳ�ֵ���
				// �����ڳ�ֵ������ۿ���
				if (tempFee > result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt + tempFee
							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��н�����ۿ����Ժ�Ľ��+ҽ�ƿ��н��
//					cp.setData("EKT_USE", tempFee
//							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",StringTool.round(result.getDouble("GREEN_BALANCE", 0)-result.getDouble("GREEN_PATH_TOTAL", 0),2));//��ɫͨ���ۿ���
				} else if (tempFee <= result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt);// ҽ�ƿ��еĽ���
//					cp.setData("EKT_USE", 0.00);// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",amt);//��ɫͨ���ۿ���
				}
			}
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",
					0));
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", sumAmt); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}

	}
	/**
	 * ҽ�ƿ�ʹ�������������
	 * ==========pangben 2013-7-26
	 */
	private void onGreenFeeTemp(String businessType,double oldAmt,double amt,
			TParm cp,double exeAmt,String caseNo,TParm result){
		//EKT_OLD_AMT: OLD_AMT ���=ԭ�����+(�˴οۿ���+������ʣ����-�������)
		double ektOldAmt=0.00;
		if(businessType.equals("REG") || businessType.equals("REGT")){//�ҺŲ���
			ektOldAmt=oldAmt;
			cp.setData("EKT_OLD_AMT",ektOldAmt);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
			cp.setData("EKT_USE", ektOldAmt);// ҽ�ƿ��ۿ���
			cp.setData("GREEN_USE",exeAmt-ektOldAmt);//��ɫͨ���ۿ���
		}else{
			TParm ektTradeParm=new TParm();
			ektTradeParm.setData("CASE_NO",caseNo);
			ektTradeParm.setData("EKT_TRADE_TYPE","'REG'");
			TParm ektSumTradeParm = EKTNewTool.getInstance().selectEktTrade(
					ektTradeParm);
			//ʣ����+�˴�ִ�еĽ��-��ʾ���<�������-�Һ�������ʹ�ý��
			ektOldAmt=oldAmt+result.getDouble("GREEN_BALANCE", 0)+exeAmt-amt;
			double greenPath=result.getDouble("GREEN_PATH_TOTAL", 0)-ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT",0);
			if(ektOldAmt>=greenPath){
				//����һ������ҽ�ƿ��еĽ��
				cp.setData("EKT_OLD_AMT",ektOldAmt-greenPath);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("EKT_USE", ektOldAmt-greenPath);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",exeAmt-ektOldAmt+greenPath);//��ɫͨ���ۿ���
			}else{
				cp.setData("EKT_OLD_AMT",0.00);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("EKT_USE", 0.00);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",exeAmt);//��ɫͨ���ۿ���
			}
		}
	}
	/**
	 * ��������������ݣ�����EKT_TRADE ��
	 * ==========panben 2013-7-26
	 */
	public void onExeGreenFee(double oldAmt,double amt,String businessType,
			TParm cp,double exeAmt,String caseNo,TParm result){
		if (oldAmt - amt >= 0) {
			if (amt > 0) {//����������������ۿ����̣�amt > 0 ҽ�ƿ��д��ڽ��˴οۿ�۳�ҽ�ƿ��еĽ��
				cp.setData("EKT_USE", exeAmt);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE", 0.00);// ��ɫͨ���ۿ���
				// EKT_OLD_AMT:EKT_TRADE ���� OLD_AMT ���=ԭ�����
				// +�˴οۿ���(EXE_AMT)-��ʾ���(AMT)
				cp.setData("EKT_OLD_AMT", oldAmt + exeAmt - amt);// ҽ�ƿ����ڲ���֮ǰ�Ľ����˴ζ����Ĵ���ǩ�����н��س��õ�ǰҽ�ƿ��Ľ�
			}else{//ҽ�ƿ����� 
				onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);	
			}
		} else {
			onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);
		}
		cp.setData("GREEN_BALANCE",result.getDouble("GREEN_BALANCE", 0));//������ۿ���
	}
	
	
	//=============================ҽ�ƿ���ֵ�˷�����add by kangy 20160804 
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
		// �շ�
		TParm result = new TParm();
		TParm hl7Parm=parm.getParm("hl7Parm");//HL7���ͽӿڼ���
		String tradeNo="";
		String billDate="";
		String billUser="";
		String[] sql = null;
		
		if(orderParm.getCount("RX_NO") > 0){
			sql = new String[orderParm.getCount("RX_NO")];
			
			// �˴β�����ҽ��
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
				// �޸ļ������շ�״̬
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
		// ��ֹ�ڷ������˵���
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient �������˽�ֹ���ñ�����");
			
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
		// 4 �ƿ�   5����    8����    3��ֵ   7�˷�
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
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// �õ�ҽ�ƿ��ⲿ���׺�
		}
		result.setData("HISTORY_NO", historyNo);
		
		return result;
		
	}
	

}