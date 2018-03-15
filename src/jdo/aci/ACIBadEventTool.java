package jdo.aci;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;

/**
 * <p>Title: 不良事件工具类</p>
 *
 * <p>Description: 不良事件工具类</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wanglong 2012.11.22
 * @version 1.0
 */
public class ACIBadEventTool extends TJDOTool {
	
    public static ACIBadEventTool instanceObject;// 实例
    
    /**
     * 得到实例
     * @return ACIRecordTool
     */
    public static ACIBadEventTool getInstance() {
        if (instanceObject == null) instanceObject = new ACIBadEventTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ACIBadEventTool() {
        setModuleName("aci\\ACIBadEventModule.x");
        onInit();
    }
    
    /**
     * 查询不良事件
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBadEventData(TParm parm) {
        TParm result = new TParm();
        result = query("selectBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		for (int i = 0; i < result.getCount("FLG"); i++) {
			result.addData("EXAMINE_FLG", result.getValue("FLG", i).equals("Y") ? true : false);
		}
        return result;
    }
    
    /**
     * 新增不良事件
     * @param parm TParm
     * @return TParm
     */
    public TParm insertBadEventData(TParm parm) {
        String aciNo = SystemTool.getInstance().getNo("ALL", "ACI", "RECORD_NO", "ACI_NO"); // 调用取号原则
        parm.setData("ACI_NO", aciNo);
        TParm result = update("insertBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新不良事件
     * @param parm TParm
     * @return TParm
     */
    public TParm updateBadEventData(TParm parm) {
        TParm result = update("updateBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除不良事件
     * @param aciNo String
     * @return TParm
     */
    public TParm deleteBadEventData(String aciNo) {
        TParm parm = new TParm();
        if (aciNo.equals("")) {
            parm.setErr(-1, "参数不全");
            return parm;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = update("deleteBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 审核不良事件
     * @param parm TParm
     * @return TParm
     */
    public TParm examineBadEventData(TParm parm) {
        if (parm.getValue("ACI_NO").equals("")) {
            parm.setErr(-1, "参数不全");
            return parm;
        }
        TParm result = update("examineBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 不良事件取消审核
     * @param parm TParm
     * @return TParm
     */
    public TParm unExamineBadEventData(String aciNo) {
        TParm parm = new TParm();
        if (aciNo.equals("")) {
            parm.setErr(-1, "参数不全");
            return parm;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = update("unExamineBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR1:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        parm.setData("ASSESS_USER", "");
        parm.setData("SAC_CLASS", "");
        parm.setData("ASSESS_DATE",  "");
        TParm result1 = update("rateBadEventData", parm);
        if (result1.getErrCode() < 0) {
            err("ERR2:" + result1.getErrCode() + result1.getErrText() + result1.getErrName());
            return result1;
        }
        return result1;
    }
    
    /**
     * 不良事件评级
     * @param parm TParm
     * @return TParm
     */
    public TParm rateBadEventData(TParm parm) {//add by wanglong 20131101
        if (parm.getValue("ACI_NO").equals("")) {
            parm.setErr(-1, "参数不全");
            return parm;
        }
        TParm result = update("rateBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询不良事件统计信息（按例数）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByCount(TParm parm) {
		TParm back = new TParm();
		String headerName = "";// 表格Header
		String sql = "SELECT TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
			+ " FROM ACI_BADEVENT "
			+ " WHERE EVENT_DATE BETWEEN # AND # ";
		//modify by wukai 20160905
		String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND REPORT_SECTION = '" + report_section + "' ";
		}
		sql += " GROUP BY TO_CHAR(EVENT_DATE, '#') "
			+ " ORDER BY EVENT_DATE";
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
			headerName += "年份     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "月份     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "日期     ,60";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		back.addData("FIRST_COLUMN", "例数");
		String parmMap = "FIRST_COLUMN";// 表格ParmMap
		int count = 0;// 记录总例数
		for (int i = 0; i < result.getCount(); i++) {
			String title = "DATE_" + (i + 1);
			parmMap += ";" + title;
			headerName += ";" + result.getValue("EVENT_DATE", i);
			back.addData(title, result.getValue("COUNT", i));
			count += result.getInt("COUNT", i);
		}
		//===========以下开始生成总计行=============//delete by wanglong 20130418
//		String[] headerArr = parmMap.split(";");
//		if (headerArr.length == 2) {
//			back.addData("FIRST_COLUMN", "总数");
//			back.addData(headerArr[1], count);// 用于总计行
//		} else if (headerArr.length > 2) {
//			back.addData("FIRST_COLUMN", "");
//			back.addData(headerArr[headerArr.length - 1], count);
//			back.addData(headerArr[headerArr.length - 2], "总数");
//			for (int i = headerArr.length - 3; i > 0; i--) {
//				back.addData(headerArr[i], "");
//			}
//		}
	    //===========以下开始生成总计列=============//add by wanglong 20130418
        headerName = headerName + ";总数";
        parmMap += ";COUNT";
        back.addData("COUNT", count);
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * 查询不良事件统计信息（按上报科室）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByDept(TParm parm) {
		String sql = "SELECT REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
			+ " FROM ACI_BADEVENT "
			+ " WHERE EVENT_DATE BETWEEN # AND # ? ";
		String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND REPORT_SECTION = '" + report_section + "' ";
		}
		sql		+= " GROUP BY REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE,REPORT_DEPT";// 排序其实不需要
		if (!parm.getValue("REPORT_DEPT").equals("")) {
			sql = sql.replaceFirst("\\?", " AND REPORT_DEPT='" + parm.getValue("REPORT_DEPT") + "' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "DEPT_CODE");
		String parmMap = "DEPT_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("REPORT_DEPT"); i++) {
			deptSet.add(result.getValue("REPORT_DEPT", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// 确立科室列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("REPORT_DEPT", j).equals(back.getValue("DEPT_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("DEPT_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("DEPT_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "上报科室          ,120,DEPT_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
	    parmMap += ";合计";
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * 查询不良事件统计信息（按SAC分级）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticBySac(TParm parm) {
	    //modify by wanglong 20131030
		String sql = "SELECT NVL(SAC_CLASS, 'x') SAC_CLASS, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
			+ " FROM ACI_BADEVENT "
			+ " WHERE EVENT_DATE BETWEEN # AND # ? ";
		String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND REPORT_SECTION = '" + report_section + "' ";
		}
		sql		+= " GROUP BY SAC_CLASS, TO_CHAR(EVENT_DATE, '#') "
			+ " ORDER BY EVENT_DATE, SAC_CLASS";// 排序其实不需要
		if (!parm.getValue("SAC_CLASS").equals("")) {
			sql = sql.replaceFirst("\\?", " AND SAC_CLASS='" + parm.getValue("SAC_CLASS") + "' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		System.out.println("sql :::::::: " + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "CLASS_CODE");
		String parmMap = "CLASS_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> sacSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("SAC_CLASS"); i++) {
			sacSet.add(result.getValue("SAC_CLASS", i));
		}
		for (String str : sacSet) {
			back.addData(header[0], str);// 确立分级列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("CLASS_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("SAC_CLASS", j).equals(back.getValue("CLASS_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("CLASS_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("CLASS_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("CLASS_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "SAC分级     ,80,CLASS_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * 查询不良事件统计信息（按事件分类）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByType(TParm parm) {
	    int length = levelCodeLength(parm.getInt("LEVEL"));// 事件分类等级
//		String sql = "SELECT EVENT_TYPE, TO_CHAR(EXAMINE_DATE,'#') AS EXAM_DATE, COUNT(*) AS COUNT "
//				+ " FROM ACI_BADEVENT "
//				+ " WHERE EXAMINE_DATE BETWEEN # AND # ? "
//				+ " GROUP BY EVENT_TYPE, TO_CHAR(EXAMINE_DATE,'#') "
//				+ " ORDER BY EXAM_DATE, EVENT_TYPE";// 排序其实不需要
        String sql = "SELECT SUBSTR( EVENT_TYPE, 0, @) AS EVENT_TYPE, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + " FROM ACI_BADEVENT "
                + " WHERE EVENT_DATE BETWEEN # AND # ? ";
        //add by wukai on 20160905 start        
        String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND REPORT_SECTION = '" + report_section + "' ";
		}
        //add by wukai on 20160905 end             
                
        sql += " GROUP BY SUBSTR( EVENT_TYPE, 0, @), TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, EVENT_TYPE";
        if (length > 0) {
            sql = sql.replaceAll("@", length + "");
        } else {
            sql = sql.replaceFirst("@", 0 + "");
        }
		if (!parm.getValue("EVENT_TYPE").equals("")) {
			sql = sql.replaceFirst("\\?", " AND EVENT_TYPE like '" + parm.getValue("EVENT_TYPE") + "%' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			typeSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : typeSet) {
			back.addData(header[0], str);// 确立分类列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "事件种类                                         ,330,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * 查询不良事件统计信息（按“上报科室/SAC分级”	）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByDeptAndSac(TParm parm) {
		 //modify by wanglong 20131030
//        String sql = "WITH A AS (SELECT A.REPORT_DEPT, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
//            + "                FROM ACI_BADEVENT A          "
//            + "               WHERE A.EVENT_DATE BETWEEN  # AND # ), "
//        	  + "       B  AS (SELECT B.ID, B.CHN_DESC        "
//            + "                FROM SYS_DICTIONARY B        "
//            + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
//            + "               UNION                         "
//            + "              SELECT 'x' ID, '未分级' CHN_DESC FROM DUAL) "
//            + "SELECT A.REPORT_DEPT, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
//            + "  FROM A, B                        "
//            + " WHERE A.SAC_CLASS = B.ID          "
//            + "GROUP BY A.REPORT_DEPT, B.CHN_DESC "
//            + "ORDER BY A.REPORT_DEPT, B.CHN_DESC";// 排序其实不需要
		
		 //modify by wukai 20160908   start 
        String sql = "WITH A AS (SELECT A.REPORT_DEPT, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
                + "                FROM ACI_BADEVENT A          "
                + "               WHERE A.EVENT_DATE BETWEEN  # AND # ";
        
        String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND A.DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND A.REPORT_SECTION = '" + report_section + "' ";
		}    
        sql += " ),";
        sql    += "       B  AS (SELECT B.ID, B.CHN_DESC        "
                + "                FROM SYS_DICTIONARY B        "
                + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
                + "               UNION                         "
                + "              SELECT 'x' ID, '未分级' CHN_DESC FROM DUAL) "
                + "SELECT A.REPORT_DEPT, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
                + "  FROM A, B                        "
                + " WHERE A.SAC_CLASS = B.ID          "
                + "GROUP BY A.REPORT_DEPT, B.CHN_DESC "
                + "ORDER BY A.REPORT_DEPT, B.CHN_DESC";// 排序其实不需要
       
        
        
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		System.out.println("按“上报科室/SAC分级” ::::::::::::  " + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "DEPT_CODE");
		String parmMap = "DEPT_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> classSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("CLASS_DESC"); i++) {
			classSet.add(result.getValue("CLASS_DESC", i));
		}
		for (String str : classSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "CLASS_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("REPORT_DEPT"); i++) {
			deptSet.add(result.getValue("REPORT_DEPT", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// 确立科室列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("REPORT_DEPT", j).equals(back.getValue("DEPT_CODE", i))
							&& result.getValue("CLASS_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("DEPT_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("DEPT_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "上报科室﹨SAC分级              ,200,DEPT_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
	
	/**
	 * 查询不良事件统计信息（按“上报科室/事件分类” ）
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticByDeptAndType(TParm parm) {
        int length = levelCodeLength(parm.getInt("LEVEL"));//事件分类等级
//        String sql = "SELECT B.DEPT_CHN_DESC AS DEPT_DESC, A.EVENT_TYPE, COUNT(*) AS COUNT "
//                + "  FROM ACI_BADEVENT A, SYS_DEPT B "
//                + " WHERE A.EXAMINE_DATE BETWEEN # AND # "
//                + "   AND A.REPORT_DEPT = B.DEPT_CODE "
//                + "GROUP BY B.DEPT_CHN_DESC, A.EVENT_TYPE "
//                + "ORDER BY B.DEPT_CHN_DESC, A.EVENT_TYPE";
//        String sql =
//                "SELECT DEPT_DESC, EVENT_TYPE, COUNT(*) AS COUNT "
//                        + "  FROM (SELECT B.DEPT_CHN_DESC AS DEPT_DESC, SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, A.ACI_NO "
//                        + "          FROM ACI_BADEVENT A, SYS_DEPT B "
//                        + "         WHERE A.EVENT_DATE BETWEEN # AND # "
//                        + "           AND A.REPORT_DEPT = B.DEPT_CODE) "
//                        + " GROUP BY DEPT_DESC, EVENT_TYPE "
//                        + " ORDER BY DEPT_DESC, EVENT_TYPE";
        String sql = 
        	"SELECT DEPT_DESC, EVENT_TYPE, COUNT(*) AS COUNT "
        				  + "  FROM (SELECT B.DEPT_CHN_DESC AS DEPT_DESC, SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, A.ACI_NO "
        		          + "          FROM ACI_BADEVENT A, SYS_DEPT B "
        		          + "         WHERE A.EVENT_DATE BETWEEN # AND # "
        		          + "           AND A.REPORT_DEPT = B.DEPT_CODE  ";
        
        //modify by wukai on 20160908
        String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND A.DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND A.REPORT_SECTION = '" + report_section + "' ";
		}    
        sql += " ) ";	
        //modify by wukai on 20160908
        
        sql		          += " GROUP BY DEPT_DESC, EVENT_TYPE "
        		          + " ORDER BY DEPT_DESC, EVENT_TYPE";
        
        if (length > 0) {
            sql = sql.replaceFirst("#", length + "");
        } else {
            sql = sql.replaceFirst("#", 0 + "");
        }      
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		System.out.println("按“上报科室/事件分类” ::::::::::::  " + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
			typeSet.add(result.getValue("DEPT_DESC", i));
		}
		for (String str : typeSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DEPT_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			deptSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// 确立科室列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("DEPT_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "事件分类﹨上报科室             ,250,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
	 * 查询不良事件统计信息（按“SAC分级/事件分类”）
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticBySacAndType(TParm parm) {
        int length = levelCodeLength(parm.getInt("LEVEL"));// 事件分类等级
//		String sql = "SELECT B.CHN_DESC AS CLASS_DESC, A.EVENT_TYPE, COUNT (*) AS COUNT " +
//				" FROM ACI_BADEVENT A, SYS_DICTIONARY B " +
//				" WHERE A.EXAMINE_DATE BETWEEN # AND # " +
//				" AND A.SAC_CLASS = B.ID " +
//				" AND B.GROUP_ID = 'SAC_CLASS' " +
//				" GROUP BY A.SAC_CLASS, A.EVENT_TYPE, B.CHN_DESC " +
//				" ORDER BY SAC_CLASS";// 排序其实不需要
        //modify by wanglong 20131030
//        String sql = "WITH A AS (SELECT SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
//                + "                FROM ACI_BADEVENT A          "
//                + "               WHERE A.EVENT_DATE BETWEEN # AND #),"
//                + "        B AS (SELECT B.ID, B.CHN_DESC        "
//                + "                FROM SYS_DICTIONARY B        "
//                + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
//                + "               UNION                         "
//                + "              SELECT 'x' ID, '未分级' CHN_DESC FROM DUAL) "
//                + "SELECT A.EVENT_TYPE, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
//                + "  FROM A, B                       "
//                + " WHERE A.SAC_CLASS = B.ID         "
//                + "GROUP BY A.EVENT_TYPE, B.CHN_DESC "
//                + "ORDER BY A.EVENT_TYPE, B.CHN_DESC";
        //modify by wukai on 20160908
        String sql = "WITH A AS (SELECT SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
            + "                FROM ACI_BADEVENT A          "
            + "               WHERE A.EVENT_DATE BETWEEN # AND #  ";
        
        String dept_in_charge = TypeTool.getString(parm.getData("DEPT_IN_CHARGE"));
		if(!StringUtils.isEmpty(dept_in_charge)) {
			sql += " AND A.DEPT_IN_CHARGE = '" + dept_in_charge + "' ";
		}
		String report_section = TypeTool.getString(parm.getData("REPORT_SECTION"));
		if(!StringUtils.isEmpty(report_section)) {
			sql += " AND A.REPORT_SECTION = '" + report_section + "' ";
		}    
        sql += " ), ";	  
        
        sql += "        B AS (SELECT B.ID, B.CHN_DESC        "
            + "                FROM SYS_DICTIONARY B        "
            + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
            + "               UNION                         "
            + "              SELECT 'x' ID, '未分级' CHN_DESC FROM DUAL) "
            + "SELECT A.EVENT_TYPE, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
            + "  FROM A, B                       "
            + " WHERE A.SAC_CLASS = B.ID         "
            + "GROUP BY A.EVENT_TYPE, B.CHN_DESC "
            + "ORDER BY A.EVENT_TYPE, B.CHN_DESC";
        
      
        
        if (length > 0) {
            sql = sql.replaceFirst("#", length + "");
        } else {
            sql = sql.replaceFirst("#", 0 + "");
        }
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		System.out.println("按“SAC分级/事件分类” ::::::::::::  " + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> classSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("CLASS_DESC"); i++) {
			classSet.add(result.getValue("CLASS_DESC", i));
		}
		for (String str : classSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "CLASS_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			typeSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : typeSet) {
			back.addData(header[0], str);// 确立事件种类列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 给统计数据（例数）填入默认值
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("CLASS_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "事件分类﹨SAC分级              ,330,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//调整各数值列的宽度为统一值
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
	
	/**
	 * 给Table的header增加长度信息
	 * @param tableHeader
	 * @param length
	 * @return
	 */
	public String headerAddLengthInfo(String tableHeader, int length) {
		String[] headerPart = tableHeader.split(";");
		String returnStr = headerPart[0];
		for (int i = 1; i < headerPart.length; i++) {
			returnStr += ";" + headerPart[i] + "," + length;
		}
		return returnStr;
	}

	/**
	 * 查看事件分类等级对应的编码长度
	 * @param level
	 * @return
	 */
    public int levelCodeLength(int level) {
        String sql = "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5 FROM SYS_RULE WHERE RULE_TYPE = 'ACI_BADEVENT'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 0) {
            return -1;
        }
        if (result.getCount() == 0) {
            return 0;
        }
        int length = 0;
        if (level == 1) {
            length = result.getInt("CLASSIFY1", 0);
        } else if (level == 2) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0);
        } else if (level == 3) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0)
                   + result.getInt("CLASSIFY3", 0);
        }
        return length;
    }
    
    /**
     * 保存
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {//add by wanglong 20131101
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null) {
            return result;
        }
        if (sql.length < 1) {
            return result;
        }
        for (String tempSql : sql) {
            result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }
    
    /**
     * 获得短信用户信息
     * @param packageCode
     * @return
     */
    public TParm getSMSUsers(String packageCode) { // add by wanglong 20131101
        String sql =
                "SELECT PACKAGE_CODE, USER_ID, TEL, DEPT_CODE, POS_CODE FROM ACI_SMSPACKAGED "
                        + " WHERE PACKAGE_CODE = '#'";
        sql = sql.replaceFirst("#", packageCode);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 发送短信
     * @param parm
     */
    public static void sendSms(TParm parm) { // add by wanglong 20131101
        String smsPath = jdo.util.XmlUtil.getSmsPath();
        String fileName = "ACI_" + jdo.util.XmlUtil.getNowTime("yyyyMMddHHmmssSSS") + "_" + parm.getValue("ACI_NO") + ".xml";
        String path = smsPath + fileName;
        createXml(parm, path, fileName);
    }

  
    /**
     * 生成短信xml
     * @param parm
     * @param path
     * @param fileName
     */
    public static void createXml(TParm parm, String path, String fileName) { // add by wanglong 20131101
        if(parm.getCount("TEL")<=0){
            return;
        }
        FileOutputStream ps = null;
        OutputStreamWriter ow = null;
        BufferedWriter buf = null;
        PrintWriter out = null;
        jdo.util.XmlUtil.createNewFile(path);
        try {
            ps = new FileOutputStream(path);
            ow = new OutputStreamWriter(ps, "utf8");
            buf = new BufferedWriter(ow);
            out = new PrintWriter(buf);
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<Root>");
            out.println("<Tels>");
            for (int i = 0; i < parm.getCount("TEL"); i++) {
                out.println("<Tel>" + parm.getValue("TEL", i) + "</Tel>");
            }
            out.println("</Tels>");
            out.println("<TITLE>不良事件通知</TITLE>");
            out.println("<Content>" + parm.getValue("SMS_TEXT") + "</Content>");
            out.println("<MrNo></MrNo>");
            out.println("<Name></Name>");
            out.println("</Root>");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                out.close();
            }
            try {
                if (buf != null) {
                    buf.close();
                }
                if (ow != null) {
                    ow.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // 上传FTP
        jdo.util.XmlUtil.invokeFtp(path, fileName);
    }

    /**
     * 返回套餐号(取号原则)
     * @return
     */
    public String getNewPackCode() { // add by wanglong 20131101
        String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO", "PACKAGE_NO");
        return packCode;
    }
    
    /**
     * 返回操作人员的详细信息
     * @param id
     * @return
     */
    public TParm getOperatorInfo(String id) { // add by wanglong 20131101
        String sql = "SELECT * FROM SYS_OPERATOR  WHERE USER_ID = '#'";
        sql = sql.replaceFirst("#", id);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    

}
