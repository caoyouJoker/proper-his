package jdo.cdss;

import java.util.List;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import net.sf.json.JSONObject;

public class CDSSClientOnServerMro {
	
	private static CDSSClientOnServerMro instance;
	
	public static CDSSClientOnServerMro getInstance() {
		if(instance == null){
			instance = new CDSSClientOnServerMro();
		}
		return instance;
	}
	
	public MroPojo fireMroRule(MroPojo mroPojo){
		try {
			
			System.out.println("caseNo:" + mroPojo.getCaseNo() + " : " + mroPojo);
			
			String ip = getWebServicesIp();
			
			JSONObject json = JSONObject.fromObject(mroPojo);
	        String jsonStr = json.toString();
			
			String url = "http://" + ip + "/rest/cdss.do?method=fireMroRule";
			HttpClientUtil clientUtil = new HttpClientUtil();
			String jsonStrRe = clientUtil.post(url, jsonStr);
			
			System.out.println("³ö²Î==fireMroRule==="+jsonStrRe);
			JSONObject obj =  JSONObject.fromObject(jsonStrRe);
			mroPojo = (MroPojo)JSONObject.toBean(obj, MroPojo.class);
			System.out.println(mroPojo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mroPojo;
	}
	
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}

	private String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "CDSS_SERVICES_IP");
		return url;
	}
	
	public List<String> fireMroRule(String caseNo){
		
		SysUtil sysUtil = new SysUtil();
		
		MroPojo mroPojo = new MroPojo();
		mroPojo.setCaseNo(caseNo);
		
		String sql =
				" SELECT A.MR_NO,"
				+ " CASE"
				+ " WHEN B.DEPT_CAT1 = 'B' THEN 'N'"
				+ " ELSE 'Y'"
				+ " END"
				+ " IS_MED,"
				+ " C.ICD_CODE"
				+ " FROM MRO_RECORD A, SYS_DEPT B, MRO_RECORD_DIAG C"
				+ " WHERE     A.OUT_DEPT = B.DEPT_CODE"
				+ " AND A.CASE_NO = C.CASE_NO"
				+ " AND C.MAIN_FLG = 'Y' AND C.IO_TYPE='O' "
				+ " AND A.CASE_NO = '" + caseNo + "'";
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(p.getCount() > 0){
			
			mroPojo.setIsMedDept(p.getValue("IS_MED", 0));
			mroPojo.setMainDiag(p.getValue("ICD_CODE", 0));
			
			sql =" SELECT OP_CODE, MAIN_FLG FROM MRO_RECORD_OP WHERE CASE_NO = '" + caseNo + "' ";
			
			p = new TParm(TJDODBTool.getInstance().select(sql));
			
			OpPojo opPojo;
			for (int i = 0; i < p.getCount(); i++) {
				opPojo = new OpPojo();
				opPojo.setId(sysUtil.generateShortUuid());
				opPojo.setCode(p.getValue("OP_CODE", i));
				opPojo.setMainFlg(p.getValue("MAIN_FLG", i).equals("")?"N":p.getValue("MAIN_FLG", i));
				mroPojo.getOpPojos().add(opPojo);
			}
			
			sql = 
				" SELECT CASE WHEN TYPE_CODE = '1' THEN 'Y' WHEN TYPE_CODE = '2' THEN 'N' END"
				+ " IS_OP"
				+ " FROM OPE_OPBOOK"
				+ " WHERE CASE_NO = '" + caseNo + "'";
			
			p = new TParm(TJDODBTool.getInstance().select(sql));
			
			for (int i = 0; i < p.getCount(); i++) {
				opPojo = new OpPojo();
				opPojo.setId(sysUtil.generateShortUuid());
				opPojo.setIsOpDept(p.getValue("IS_OP", i));
				mroPojo.getOpBookPojos().add(opPojo);
			}
			
			sql =
				" SELECT OTH_TYPE"
				+ " FROM IBS_ORDD A, SYS_FEE B"
				+ " WHERE     A.ORDER_CODE = B.ORDER_CODE"
				+ " AND B.ACTIVE_FLG = 'Y'"
				+ " AND OTH_TYPE IS NOT NULL"
				+ " AND A.CASE_NO = '" + caseNo + "'";
			
			p = new TParm(TJDODBTool.getInstance().select(sql));
			
			FeePojo feePojo;
			for (int i = 0; i < p.getCount(); i++) {
				feePojo = new FeePojo();
				feePojo.setId(sysUtil.generateShortUuid());
				feePojo.setCavityNum(p.getValue("OTH_TYPE", i));
				mroPojo.getFeePojos().add(feePojo);
			}
			
			mroPojo = fireMroRule(mroPojo);
			
		}
		
		return mroPojo.getDiseases();
	}

}
