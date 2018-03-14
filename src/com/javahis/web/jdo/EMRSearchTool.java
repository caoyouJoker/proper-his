package com.javahis.web.jdo;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;
import java.util.List;
import com.javahis.web.bean.EMRMetaDataDO;
import com.dongyang.data.TParm;
import java.util.ArrayList;
import com.dongyang.Service.Server;
import com.javahis.web.bean.EMRTableDO;
import com.javahis.web.bean.DropDownList;

/**
 * <p>Title: 全文检索数据层功能类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @li.xiang1979@gmail.com
 * @version 1.0
 */
public class EMRSearchTool
    extends TJDOTool {
    private boolean isDebug = true;
    /**
     * 实例
     */
    public static EMRSearchTool instanceObject;
    /**
     * 得到实例
     * @return EMRSearchTool
     */
    public static EMRSearchTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new EMRSearchTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public EMRSearchTool() {
        //onInit();
        Server.autoInit(this);
    }

    public List<DropDownList> getAllRegionList() {
        List<DropDownList> list = new ArrayList<DropDownList> ();
        TParm result = new TParm(this.getDBTool().select("SELECT REGION_CODE,REGION_CHN_DESC FROM SYS_REGION WHERE 1=1 ORDER BY REGION_CODE"));
        if (result != null && result.getCount() > 0) {
            for (int i = 0; i < result.getCount(); i++) {
                DropDownList dropDown = new DropDownList();
                dropDown.setValue(result.getValue("REGION_CODE", i));
                dropDown.setTitle(result.getValue("REGION_CHN_DESC", i));
                list.add(dropDown);
            }
        }
        return list;
    }

    /**
     * 通过区域取部门
     * @param regionCode String
     * @return List
     */
    public List<DropDownList> getDeptsByRegion(String regionCode) {
        List<DropDownList> list = new ArrayList<DropDownList> ();
        TParm result = new TParm(this.getDBTool().select(
            "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE 1=1 AND REGION_CODE='" +
            regionCode + "' ORDER BY DEPT_CODE"));
        if (result != null && result.getCount() > 0) {
            for (int i = 0; i < result.getCount(); i++) {
                DropDownList dropDown = new DropDownList();
                dropDown.setValue(result.getValue("DEPT_CODE", i));
                dropDown.setTitle(result.getValue("DEPT_CHN_DESC", i));
                list.add(dropDown);
            }
        }

        return list;
    }

    /**
     *
     * @return List
     */
    public List<DropDownList> getStationByDept(String deptCode) {
        if (isDebug) {
            System.out.println("=====deptCode======" + deptCode);
        }
        List<DropDownList> list = new ArrayList<DropDownList> ();
        TParm result = new TParm(this.getDBTool().select(
            "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION WHERE 1=1 AND DEPT_CODE='" +
            deptCode + "' ORDER BY STATION_CODE"));
        if (result != null && result.getCount() > 0) {
            for (int i = 0; i < result.getCount(); i++) {
                DropDownList dropDown = new DropDownList();
                dropDown.setValue(result.getValue("STATION_CODE", i));
                dropDown.setTitle(result.getValue("STATION_DESC", i));
                list.add(dropDown);
            }
        }

        return list;
    }


    /**
     * 通过父分类取表单
     * @param parentId String
     * @return List
     */
    public List<EMRTableDO> getEMRCategoryByParentId(String parentId) {
        List<EMRTableDO> emrTableList = new ArrayList<EMRTableDO> ();
        TParm result = null;
        if (parentId.equalsIgnoreCase("root")) {
            result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,DESCRIPTION,LEAF_FLG FROM EMR_CLASS WHERE PARENT_CLASS_CODE='ROOT' ORDER BY CLASS_CODE,SEQ"));
            if (result != null && result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    EMRTableDO emrTableDO = new EMRTableDO();
                    emrTableDO.setClassCode(result.getValue("CLASS_CODE", i));
                    emrTableDO.setClassDesc(result.getValue("CLASS_DESC", i));
                    emrTableDO.setDescription(result.getValue("DESCRIPTION",
                        i));
                    emrTableDO.setLeaf(false);
                    emrTableList.add(emrTableDO);
                }
            }

        }
        else {
            //parentId 是叶节点;
            //System.out.println("isLeaf(parentId)"+isLeaf(parentId));
            if (isLeaf(parentId)) {
                result = new TParm(this.getDBTool().select(
                    "SELECT SUBCLASS_CODE,SUBCLASS_DESC FROM EMR_TEMPLET WHERE CLASS_CODE='" +
                    parentId + "' ORDER BY SUBCLASS_CODE,SEQ"));
                if (result != null && result.getCount() > 0) {
                    for (int i = 0; i < result.getCount(); i++) {
                        EMRTableDO emrTableDO = new EMRTableDO();
                        emrTableDO.setClassCode(result.getValue("SUBCLASS_CODE",
                            i));
                        emrTableDO.setClassDesc(result.getValue("SUBCLASS_DESC",
                            i));
                        emrTableDO.setDescription(result.getValue(
                            "SUBCLASS_DESC",
                            i));
                        emrTableDO.setLeaf(true);
                        emrTableList.add(emrTableDO);
                    }
                }

            }
            else {
                //非叶节点
                result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,DESCRIPTION,LEAF_FLG FROM EMR_CLASS WHERE PARENT_CLASS_CODE='" +
                    parentId + "' ORDER BY CLASS_CODE,SEQ"));
                if (result != null && result.getCount() > 0) {
                    for (int i = 0; i < result.getCount(); i++) {
                        EMRTableDO emrTableDO = new EMRTableDO();
                        emrTableDO.setClassCode(result.getValue("CLASS_CODE", i));
                        emrTableDO.setClassDesc(result.getValue("CLASS_DESC", i));
                        emrTableDO.setDescription(result.getValue("DESCRIPTION",
                            i));
                        emrTableDO.setLeaf(false);
                        emrTableList.add(emrTableDO);
                    }
                }
            }
        }

        return emrTableList;
    }

    /**
     * 是否是叶节点
     * @param parentId String
     * @return boolean
     */
    private boolean isLeaf(String parentId) {
        TParm result = new TParm(this.getDBTool().select(
            "SELECT LEAF_FLG FROM EMR_CLASS WHERE CLASS_CODE='" + parentId +
            "'"));
        if (result != null && result.getCount() > 0) {
            if (result.getValue("LEAF_FLG", 0).equalsIgnoreCase("Y")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 通过父分类取元数据分类
     * @param parentId String  父节点;
     * @return List
     */
    public List<EMRMetaDataDO> getMetadataCategoryByParentId(String parentId) {
        List<EMRMetaDataDO> emrMetaDataList = new ArrayList<EMRMetaDataDO> ();
        TParm result = null;
        if (parentId.equalsIgnoreCase("root")) {
            result = new TParm(this.getDBTool().select("SELECT GROUP_CODE,GROUP_DESC FROM EMR_CLINICAL_DATAGROUP GROUP BY GROUP_CODE,GROUP_DESC ORDER BY GROUP_CODE"));
            if (result != null && result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    EMRMetaDataDO emrMetaDO = new EMRMetaDataDO();
                    emrMetaDO.setDataCode(result.getValue("GROUP_CODE", i));
                    emrMetaDO.setDataTitle(result.getValue("GROUP_DESC", i));
                    emrMetaDO.setDataDefine(result.getValue("GROUP_DESC", i));
                    emrMetaDO.setLeaf(false);
                    emrMetaDataList.add(emrMetaDO);
                }

            }
        }
        else {
            result = new TParm(this.getDBTool().select(
                "SELECT DATA_CODE,DATA_DESC FROM EMR_CLINICAL_DATAGROUP WHERE GROUP_CODE='" +
                parentId + "' ORDER BY DATA_CODE"));
            if (result != null && result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    EMRMetaDataDO emrMetaDO = new EMRMetaDataDO();
                    emrMetaDO.setDataCode(result.getValue("DATA_CODE", i));
                    emrMetaDO.setDataTitle(result.getValue("DATA_DESC", i));
                    emrMetaDO.setDataDefine(result.getValue("DATA_DESC", i));
                    emrMetaDO.setLeaf(true);
                    emrMetaDataList.add(emrMetaDO);
                }
            }

        }

        return emrMetaDataList;
    }

    /**
     * 基本查询功能;
     * @param startDate String  开始日期
     * @param endDate String    结束日期
     * @param regionCode String  区域编码
     * @param deptCode String    科室编码
     * @param stationCode String  病区编码
     * @return String[]   文档中病案号(MR_NO,CASE_NO,IPD_NO)
     */
    public TParm baseQuery(String startDate, String endDate,
                           String regionCode, String deptCode,
                           String stationCode) {
        StringBuffer sb = new StringBuffer();
        //住院病患ADM_INP；
        if (stationCode != null && !stationCode.equals("")) {
            sb.append("SELECT MR_NO FROM ADM_INP WHERE 1=1");
        }
        else {
            //非住院REG_PATADM；
            sb.append("SELECT MR_NO FROM REG_PATADM WHERE 1=1");
        }

        //查询区间
        if (startDate != null && !startDate.equals("")) {
            if (stationCode != null && !stationCode.equals("")) {
                sb.append(" AND IN_DATE >= TO_DATE('" + startDate +
                          " 00:00:00','YYYY-MM-DD HH24:MI:SS')");
            }
            else {
                sb.append(" AND ADM_DATE >= TO_DATE('" + startDate +
                          " 00:00:00','YYYY-MM-DD HH24:MI:SS')");
            }
        }
        if (endDate != null && !endDate.equals("")) {
            //
            if (stationCode != null && !stationCode.equals("")) {
                sb.append(" AND IN_DATE <=TO_DATE('" + endDate +
                          " 11:59:59','YYYY-MM-DD HH24:MI:SS')");
            }
            else {
                sb.append(" AND ADM_DATE <=TO_DATE('" + endDate +
                          " 11:59:59','YYYY-MM-DD HH24:MI:SS')");
            }

        }
        //区域
        if (regionCode != null && !regionCode.equals("")) {
            sb.append(" AND REGION_CODE='" + regionCode + "'");
        }
        //部门
        if (deptCode != null && !deptCode.equals("")) {
            sb.append(" AND DEPT_CODE='" + deptCode + "'");
        }
        //病区
        if (stationCode != null && !stationCode.equals("")) {
            sb.append(" AND STATION_CODE='" + stationCode + "'");
        }
        if (isDebug) {
            System.out.println("=========baseQuerySql============" +
                               sb.toString());
        }

        //按条件查询；
        TParm result = new TParm(this.getDBTool().select(sb.toString()));

        return result;
    }


    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
