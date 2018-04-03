package jdo.mro;

import java.text.SimpleDateFormat;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 病案借阅
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class MROBorrowTool extends TJDOTool{
	
	/**
	 * 构建实例
	 */
  public static MROBorrowTool instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static MROBorrowTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new MROBorrowTool() ;
	  return instanceObject ;
  }
  
  /**
   * 构造器
   */
  public MROBorrowTool(){
	  setModuleName("mro\\MROBorrowModule.x") ;
	  onInit() ;
  }
  
  /**
   * 查询待出库的病历数据
   * 
   * @param parm
   * @author wangbin
   */
  public TParm queryMroReg(TParm parm){
	  StringBuilder sql  = new StringBuilder();
	  sql.append("SELECT T.FLG,T.ADM_DATE,T.ADM_AREA_CODE,T.SESSION_CODE,T.QUE_NO,T.MR_NO,T.PAT_NAME,T.SEX_CODE,T.CELL_PHONE,T.STATUS,");
	  sql.append("T.IN_FLG,T.CURT_LOCATION,T.DEPT_CODE,T.DR_CODE,T.BOX_CODE,T.BOOK_NO,T.ADM_TYPE,T.CONFIRM_STATUS,T.MRO_REGNO,T.SEQ,T.CURT_HOSP,T.CASE_NO,T.IPD_NO");
	  // add by wangbin 20141028 待出库界面增加取消状态列，用于查看哪些数据取消待出库 START
	  sql.append(",T.CANCEL_FLG,T.CANCEL_STATUS ");
	  // add by wangbin 20141028 待出库界面增加取消状态列，用于查看哪些数据取消待出库 END
	  sql.append("  FROM (SELECT 'N' FLG,A.ADM_DATE,A.ADM_AREA_CODE,A.SESSION_CODE,A.QUE_NO,A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.CELL_PHONE,B.IN_FLG,");
	  sql.append("        CASE B.IN_FLG WHEN '1' THEN '在库' WHEN '2' THEN '已出库' ELSE '未建档' END AS STATUS,");
	  sql.append("        B.CURT_HOSP,B.CURT_LOCATION,A.DEPT_CODE,A.DR_CODE,B.BOX_CODE,B.BOOK_NO,A.ADM_TYPE,A.CONFIRM_STATUS,A.MRO_REGNO,A.SEQ,A.CASE_NO,B.IPD_NO");
	  // add by wangbin 20141028 待出库界面增加取消状态列，用于查看哪些数据取消待出库 START
	  sql.append("        ,A.CANCEL_FLG,CASE A.CANCEL_FLG WHEN 'N' THEN '未取消' ELSE '已取消' END AS CANCEL_STATUS");
	  // add by wangbin 20141028 待出库界面增加取消状态列，用于查看哪些数据取消待出库 END
	  sql.append("          FROM MRO_REG A, (SELECT D.MR_NO,C.IN_FLG,D.CURT_HOSP,D.CURT_LOCATION,D.BOX_CODE,D.BOOK_NO,C.IPD_NO");
	  sql.append("                             FROM MRO_MRV C, ");
	  sql.append("                              (SELECT MR_NO,ADM_TYPE,CURT_HOSP,CURT_LOCATION,BOX_CODE,MAX (BOOK_NO) AS BOOK_NO FROM MRO_MRV ");
	  // 门急住标识
	  if (!"".equals(parm.getValue("ADM_TYPE"))) {
		  sql.append(" WHERE ADM_TYPE = '").append(parm.getValue("ADM_TYPE")).append("' ");
	  }
	  sql.append("                               GROUP BY MR_NO,ADM_TYPE,CURT_HOSP,CURT_LOCATION,BOX_CODE) D");
	  sql.append("                             WHERE C.MR_NO = D.MR_NO AND C.ADM_TYPE = D.ADM_TYPE AND C.BOX_CODE = D.BOX_CODE AND C.BOOK_NO = D.BOOK_NO) B");
	  sql.append("          WHERE A.MR_NO = B.MR_NO(+) ");
	  if (!"".equals(parm.getValue("S_DATE")) && !"".equals(parm.getValue("E_DATE"))) {
		  sql.append(" AND A.ADM_DATE BETWEEN TO_DATE('").append(parm.getValue("S_DATE")).append("','YYYYMMDDHH24MISS') ");
		  sql.append(" AND TO_DATE('").append(parm.getValue("E_DATE")).append("','YYYYMMDDHH24MISS') ");
	  }
	  sql.append("  ) T WHERE 1=1 ");
	  
	  // 门急住标识
	  if (!"".equals(parm.getValue("ADM_TYPE"))) {
		  sql.append(" AND T.ADM_TYPE = '").append(parm.getValue("ADM_TYPE")).append("' ");
	  }
	  
	  // 病案号
	  if (!"".equals(parm.getValue("MR_NO"))) {
		  sql.append(" AND T.MR_NO = '").append(parm.getValue("MR_NO")).append("' ");
	  }
	  
	  // 时段
	  if (!"".equals(parm.getValue("SESSION_CODE"))) {
		  sql.append(" AND T.SESSION_CODE = '").append(parm.getValue("SESSION_CODE")).append("' ");
	  }
	  
	  // 科室编码
	  if (!"".equals(parm.getValue("DEPT_CODE"))) {
		  sql.append(" AND T.DEPT_CODE = '").append(parm.getValue("DEPT_CODE")).append("' ");
	  }
	  
	  // 医生编码
	  if (!"".equals(parm.getValue("DR_CODE"))) {
		  sql.append(" AND T.DR_CODE = '").append(parm.getValue("DR_CODE")).append("' ");
	  }
	  
	  // 在库状态
	  if (!"".equals(parm.getValue("STATUS"))) {
		  sql.append(" AND T.STATUS = '").append(parm.getValue("STATUS")).append("' ");
	  }
	  
	  // 待出库确认状态
	  if (!"".equals(parm.getValue("CONFIRM_STATUS"))) {
		  sql.append(" AND T.CONFIRM_STATUS = '").append(parm.getValue("CONFIRM_STATUS")).append("' ");
	  }
	  
	  sql.append(" ORDER BY T.BOX_CODE,T.DEPT_CODE,T.DR_CODE");
	  
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
	  
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 新增 预约挂号，数据插入到中间表中
   * @param parm
   * @return
   */
	public TParm insertMroReg(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("insertMroReg", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}
  

	/**
	 * 将确认借阅的病历插入到借阅表中
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertQueue(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String queNo = "";

		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				// 保存前验证该数据是否已经取消
				result = this.queryMroRegCancel(parm.getRow(i));
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
				
				// 如果当前数据已经取消，则跳过该数据
				if (result.getCount() > 0) {
					continue;
				}
				
				// 取号原则
				queNo = SystemTool.getInstance().getNo("ALL", "MRO",
						"QUE_SEQ", "QUE_SEQ");
				parm.setData("QUE_SEQ", i, queNo);
				result = update("insertQueue", parm.getRow(i), conn);

				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}

				// 更新待出库确认状态
				result = update("updateConfirmStatus", parm.getRow(i), conn);

				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}

		return result;
	}
  
	/**
	 * 借阅的出库入库，将数据插入到历史记录表中 insertTRANHIS
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertTRANHIS(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			result = update("insertTRANHIS", parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}

		return result;
	}
  
	/**
	 * 删除
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteMroReg(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("deleteMroReg", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
  
	/**
	 * 查询验证临时表(MRO_REG)是否存在指定的数据
	 * 
	 * @param parm
	 * @return
	 * @author wangbin
	 */
	public TParm queryMroRegAppointment(TParm parm) {
		TParm result = new TParm();
		result = query("queryMroRegAppointment", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}
  
	/**
	 * 如果病患取消预约，则更新临时表(MRO_REG)取消注记
	 * 
	 * @param parm
	 * @return
	 * @author wangbin
	 */
	public TParm cancelMroRegAppointment(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (null != conn) {
			result = update("cancelMroRegAppointment", parm, conn);
		} else {
			result = update("cancelMroRegAppointment", parm);
		}
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 现场挂号，数据插入到中间表中
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertMroRegByLoc(TParm parm) {
		TParm result = new TParm();
		result = update("insertMroReg", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 取得CRM预约信息
	 * 
	 * @param parm
	 * @return CRM预约信息
	 * @author wangbin
	 */
	public TParm queryVipRegInfo(TParm parm) {
		String admDate = parm.getValue("ADM_DATE").replaceAll("-", "/");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		admDate = sdf.format(StringTool.getDate(admDate, "yyyy/MM/dd"));
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT A.ADM_DATE, A.SESSION_CODE, B.START_TIME, B.QUE_NO, A.REG_CLINICAREA ");
		sql.append(" FROM REG_SCHDAY A, REG_CLINICQUE B, REG_CLINICAREA C ");
		sql.append(" WHERE A.ADM_TYPE = 'O' ");
		sql.append(" AND A.ADM_DATE = '");
		sql.append(admDate.replace("/", ""));
		sql.append("' AND A.VIP_FLG = 'Y' ");
		sql.append(" AND A.SESSION_CODE = B.SESSION_CODE ");
		sql.append(" AND A.CLINICROOM_NO = B.CLINICROOM_NO ");
		sql.append(" AND A.REG_CLINICAREA = C.CLINICAREA_CODE ");
		sql.append(" AND A.ADM_DATE = B.ADM_DATE");
		sql.append(" AND A.DR_CODE = '");
		sql.append(parm.getValue("DR_CODE"));
		sql.append("' AND A.DEPT_CODE = '");
		sql.append(parm.getValue("DEPT_CODE"));
		sql.append("' AND B.START_TIME = '");
		sql.append(parm.getValue("START_TIME").replaceAll(":", "").substring(0, 4));
		sql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}
	
	/**
	 * 验证预约数据是否取消预约
	 * 
	 * @param parm
	 * @return 预约信息
	 * @author wangbin
	 */
	public TParm queryMroRegCancel(TParm parm) {
		TParm result = query("queryMroRegCancel", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 回更临时表病案号用于新建病历归档
	 * 
	 * @param parm
	 * @return 更新信息
	 * @author wangbin 2014/08/28
	 */
	public TParm updateMrNoForMroReg(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE MRO_REG SET MR_NO = '");
		sql.append(parm.getValue("MR_NO"));
		sql.append("' WHERE BOOK_ID = '");
		sql.append(parm.getValue("BOOK_ID"));
		sql.append("' AND MR_NO IS NULL");
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 查询病历案卷基本信息
	 * 
	 * @param parm
	 * @return 案卷信息
	 * @author wangbin 2014/08/29
	 */
	public TParm queryMroMrv(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 'N' FLG,A.MR_NO,B.PAT_NAME,A.SEQ,CASE A.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'I' THEN '住院' END AS ARCHIVES_TYPE,A.ADM_TYPE, ");
		sql.append("A.IPD_NO,A.CREATE_HOSP,CASE A.IN_FLG WHEN '0' THEN '未建档' WHEN '1' THEN '在库' WHEN '2' THEN '已出库' END AS IN_FLG_DESC,IN_FLG,");
		sql.append("A.CURT_HOSP,A.CURT_LOCATION,A.TRAN_HOSP,A.BOX_CODE,A.BOOK_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.CURT_LEND_DEPT_CODE,A.CURT_LEND_DR_CODE ");
		sql.append(" FROM MRO_MRV A,SYS_PATINFO B ");
		sql.append(" WHERE A.MR_NO = B.MR_NO ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			sql.append(" AND A.MR_NO = '").append(parm.getValue("MR_NO")).append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ADM_TYPE"))) {
			sql.append(" AND A.ADM_TYPE = '").append(parm.getValue("ADM_TYPE")).append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("IN_FLG"))) {
			sql.append(" AND A.IN_FLG = '").append(parm.getValue("IN_FLG")).append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("BOX_CODE"))) {
			sql.append(" AND A.BOX_CODE = '").append(parm.getValue("BOX_CODE")).append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("BOOK_NO"))) {
			sql.append(" AND A.BOOK_NO = '").append(parm.getValue("BOOK_NO")).append("' ");
		}
		
		sql.append(" ORDER BY A.ADM_TYPE,A.BOX_CODE,A.BOOK_NO");
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 修改病历案卷的存放位置
	 * 
	 * @param parm
	 * @return 案卷信息
	 * @author wangbin 2014/08/29
	 */
	public TParm updateMroMrvStoreLocation(TParm parm) {
		TParm result = update("updateMroMrvStoreLocation", parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 案卷合并后赋予新的册号
	 * 
	 * @param parm
	 * @return 案卷信息
	 * @author wangbin 2014/09/01
	 */
	public TParm updateMroMrvBookNo(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE MRO_MRV ");
		sql.append(" SET BOOK_NO = '");
		sql.append(parm.getValue("NEW_BOOK_NO"));
		sql.append("' WHERE MR_NO = '");
		sql.append(parm.getValue("MR_NO"));
		sql.append("' AND ADM_TYPE = '");
		sql.append(parm.getValue("ADM_TYPE"));
		sql.append("' AND BOX_CODE = '");
		sql.append(parm.getValue("BOX_CODE"));
		sql.append("' AND BOOK_NO = '");
		sql.append(parm.getValue("OLD_BOOK_NO"));
		sql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 案卷合并后删除多余数据
	 * 
	 * @param parm
	 * @return 案卷信息
	 * @author wangbin 2014/09/01
	 */
	public TParm deleteMroMrv(TParm parm, TConnection conn) {
		TParm result = new TParm();
		
		for (int i = 0; i < parm.getCount(); i++) {
			result = update("deleteMroMrv", parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		return result;
	}
	
	/**
	 * 如果病患取消挂号，则更新借阅表的取消状态
	 * 
	 * @param parm
	 * @return
	 * @author wangbin 20141021
	 */
	public TParm updateMroQueueCanFlg(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MRO_QUEUE SET CAN_FLG = 'Y',OPT_USER = '");
		sql.append(parm.getValue("OPT_USER"));
		sql.append("',OPT_TERM = '");
		sql.append(parm.getValue("OPT_TERM"));
		sql.append("',OPT_DATE = SYSDATE ");
		sql.append(" WHERE CASE_NO = '");
		sql.append(parm.getValue("CASE_NO"));
		sql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 更新待出库案卷的确认状态
	 * 
	 * @param parm
	 * @return 案卷信息
	 * @author wangbin 2014/10/22
	 */
	public TParm updateConfirmStatus(TParm parm) {
		TParm result = update("updateConfirmStatus", parm);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 根据时间段查询预约挂号信息
	 * 
	 * @param parm
	 * @return CRM预约信息
	 * @author wangbin 2014/11/7
	 */
	public TParm queryMRORegAppInfoByDate(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT MRO_REGNO,ADM_TYPE,MR_NO,ADM_DATE,ADM_AREA_CODE,SESSION_CODE,PAT_NAME, ");
		sql.append(" SEX_CODE,BIRTH_DATE,CELL_PHONE,DEPT_CODE,DR_CODE ");
		sql.append(" FROM MRO_REG ");
		sql.append(" WHERE ADM_DATE BETWEEN TO_DATE ('");
		sql.append(parm.getValue("REP_S_DATE"));
		sql.append("', 'YYYYMMDDHH24MISS') AND TO_DATE ('");
		sql.append(parm.getValue("REP_E_DATE"));
		sql.append("', 'YYYYMMDDHH24MISS') ");
		
		// 来源类型(0_预约挂号)
		if (StringUtils.isNotEmpty(parm.getValue("ORIGIN_TYPE"))) {
			sql.append(" AND ORIGIN_TYPE = '");
			sql.append(parm.getValue("ORIGIN_TYPE"));
			sql.append("' ");
		}
		
		// 门急住标识
		if (StringUtils.isNotEmpty(parm.getValue("ADM_TYPE"))) {
			sql.append(" AND ADM_TYPE = '");
			sql.append(parm.getValue("ADM_TYPE"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sql.append(" AND DEPT_CODE = '");
			sql.append(parm.getValue("DEPT_CODE"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("DR_CODE"))) {
			sql.append(" AND DR_CODE = '");
			sql.append(parm.getValue("DR_CODE"));
			sql.append("' ");
		}
		
		// 取消标记
		if (StringUtils.isNotEmpty(parm.getValue("CANCEL_FLG"))) {
			sql.append(" AND CANCEL_FLG = '");
			sql.append(parm.getValue("CANCEL_FLG"));
			sql.append("' ");
		}
		
		sql.append(" ORDER BY MRO_REGNO, SEQ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}
	
	/**
	 * 查询病历待入库信息报表
	 * 
	 * @param parm
	 * @return 待入库信息报表
	 * @author wangbin 2014/11/7
	 */
	public TParm queryMROInReadyData(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT A.QUE_SEQ,A.ADM_TYPE,CASE A.ADM_TYPE WHEN 'O' THEN '门诊' ELSE '住院' END AS ADM_TYPE_DESC, ");
		sql.append(" A.MR_NO,B.PAT_NAME,A.ADM_AREA_CODE,A.REQ_DEPT,A.MR_PERSON,A.LEND_BOX_CODE,A.LEND_BOOK_NO ");
		sql.append(" FROM MRO_QUEUE A, SYS_PATINFO B ");
		sql.append(" WHERE A.MR_NO = B.MR_NO ");
		sql.append(" AND A.RTN_DATE BETWEEN TO_DATE ('");
		sql.append(parm.getValue("REP_S_DATE"));
		sql.append("', 'YYYYMMDDHH24MISS') AND TO_DATE ('");
		sql.append(parm.getValue("REP_E_DATE"));
		sql.append("', 'YYYYMMDDHH24MISS') ");
		
		// 入库类型
		if (StringUtils.isNotEmpty(parm.getValue("OUT_TYPE"))) {
			sql.append(" AND OUT_TYPE = '");
			sql.append(parm.getValue("OUT_TYPE"));
			sql.append("' ");
		}
		
		// 科室
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sql.append(" AND REQ_DEPT = '");
			sql.append(parm.getValue("DEPT_CODE"));
			sql.append("' ");
		}
		
		// 出库状况
		if (StringUtils.isNotEmpty(parm.getValue("ISSUE_CODE"))) {
			sql.append(" AND ISSUE_CODE = '");
			sql.append(parm.getValue("ISSUE_CODE"));
			sql.append("' ");
		}
		
		// 取消标记
		if (StringUtils.isNotEmpty(parm.getValue("CAN_FLG"))) {
			sql.append(" AND CAN_FLG = '");
			sql.append(parm.getValue("CAN_FLG"));
			sql.append("' ");
		}
		
		sql.append(" ORDER BY A.RTN_DATE,A.ADM_AREA_CODE,A.REQ_DEPT,A.MR_PERSON,A.LEND_BOX_CODE");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}
	
	/**
     * 更新MRO_REG住院医生
     * @param parm TParm
     * @return TParm
     * @author wangbin 2016/6/6
     */
    public TParm updateMroRegDrCode(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE MRO_REG SET DR_CODE='");
		sbSql.append(parm.getValue("DR_CODE"));
		sbSql.append("' WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND CANCEL_FLG = 'N' AND CONFIRM_STATUS = '0' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
	 * 回更临时表就诊号
	 * 
	 * @param parm
	 * @return 更新结果
	 * @author wangbin 2018/02/06
	 */
	public TParm updateCaseNoForMroReg(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE MRO_REG SET CASE_NO = '");
		sql.append(parm.getValue("CASE_NO"));
		sql.append("' WHERE CASE_NO IS NULL ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MRO_REGNO"))) {
			sql.append(" AND MRO_REGNO = '");
			sql.append(parm.getValue("MRO_REGNO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("BOOK_ID"))) {
			sql.append(" AND BOOK_ID = '");
			sql.append(parm.getValue("BOOK_ID"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			sql.append(" AND MR_NO = '");
			sql.append(parm.getValue("MR_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ADM_TYPE"))) {
			sql.append(" AND ADM_TYPE = '");
			sql.append(parm.getValue("ADM_TYPE"));
			sql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.update(sql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
}

