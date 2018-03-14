package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import java.sql.Timestamp;

import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title:ҽʦ�հ������ </p>
 *
 * <p>Description:ҽʦ�հ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class SchDayTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SchDayTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static SchDayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SchDayTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SchDayTool() {
        setModuleName("reg\\REGSchDayModule.x");
        onInit();
    }
    /**
     * �����հ��(ǰ��)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����հ����ˣ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        parm.setData("ADM_DATE", parm.getData("ADM_DATE"));
        result = update("insertdata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����հ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm schWeekForDay(TParm parm, TConnection connection) {
        Object obj = this.checkSpreadOut(parm);
//        System.out.println("obj�������" + obj);
        TParm result = (TParm) obj;
        if (result.getErrCode() != 0)
            return result;
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);

        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
//             inParm.setData("ADM_DATE",adm_Date);
            inParm.setData("ADM_DATE", day);
            inParm.setData("DAYOFWEEK", StringTool.getWeek(day));
            //add by huangtt 20140911 start
            if(inParm.getDouble("DAYOFWEEK") == 0){
            	inParm.setData("DAYOFWEEK", 7);
            }
            //add by huangtt 20140911 end
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("DR_CODE", parm.getValue("DR_CODE"));
            inParm.setDataN("DEPT_CODE", parm.getValue("DEPT_CODE"));

            //ɾ�������հ�
            result = deletedata(inParm);
            result = SchWeekTool.getInstance().selectdata(inParm);
            int count = result.getCount();
            for (int j = 0; j < count; j++) {
                TParm p = new TParm();
                p.setRowData( -1, result, j);
                p.setData("ADM_DATE", adm_Date);
                p.setData("REALDEPT_CODE", p.getData("DEPT_CODE"));
                p.setData("REALDR_CODE", p.getData("DR_CODE"));
                p.setData("QUE_NO", 1);
                p.setData("MAX_QUE",
                          PanelGroupTool.getInstance().getMaxQue(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("VIP_FLG",
                          PanelGroupTool.getInstance().getVipFlg(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("STOP_SESSION", "N");
                p.setData("REFRSN_CODE", "");
                
                p.setData("REG_SPECIAL_NUMBER",  p.getData("REG_SPECIAL_NUMBER") ==  null ? new TNull(String.class): p.getData("REG_SPECIAL_NUMBER"));
                
//                System.out.println("��ת������"+p);
                //���ҷ���������Ϣ
                TParm roomParm = PanelRoomTool.getInstance().getAreaByRoom(p.getValue(
                    "CLINICROOM_NO"));
                String areaCode = roomParm.getValue("CLINICAREA_CODE",0);
                p.setData("REG_CLINICAREA",areaCode);
                TParm result1 = insertdata(p, connection);
                if (result1.getErrCode() < 0)
                    return result1;
            }
        }
        return result;
    }
    /**
     * �ܰ�ת�հ�(��)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm schWeekForDayNew(TParm parm, TConnection connection) {
        Object obj = this.checkSpreadOutNew(parm);
        //System.out.println("obj�������" + obj);
        TParm result = (TParm) obj;
        if (result.getErrCode() != 0)
            return result;
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", day);
            inParm.setData("DAYOFWEEK", StringTool.getWeek(day));
            //add by huangtt 20140911 start
            if(inParm.getDouble("DAYOFWEEK") == 0){
            	inParm.setData("DAYOFWEEK", 7);
            }
            //add by huangtt 20140911 end
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("CLINICROOM_NO", parm.getValue("CLINICROOM_NO"));
            //ɾ�������հ�
            result = delDataNew(inParm);
            //System.out.println("ɾ�������հ�"+inParm);
            TParm selDataNewParm = SchWeekTool.getInstance().selDataNew(inParm);
           // System.out.println("��ת�ղ�ѯ(��)"+selDataNewParm);
            int count = selDataNewParm.getCount();
            for (int j = 0; j < count; j++) {
                TParm p = new TParm();
                p.setRowData( -1, selDataNewParm, j);
                p.setData("ADM_DATE", adm_Date);
                p.setData("REALDEPT_CODE", p.getData("DEPT_CODE"));
                p.setData("REALDR_CODE", p.getData("DR_CODE"));
                p.setData("QUE_NO", 1);
                p.setData("MAX_QUE",
                          PanelGroupTool.getInstance().getMaxQue(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("VIP_FLG",
                          PanelGroupTool.getInstance().getVipFlg(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("STOP_SESSION", "N");
                p.setData("REFRSN_CODE", "");
                
                p.setData("REG_SPECIAL_NUMBER",  p.getData("REG_SPECIAL_NUMBER") ==  null ? new TNull(String.class): p.getData("REG_SPECIAL_NUMBER"));
                
//                System.out.println("��ת������----"+p);
                //���ҷ���������Ϣ
                TParm roomParm = PanelRoomTool.getInstance().getAreaByRoom(p.getValue(
                    "CLINICROOM_NO"));
                String areaCode = roomParm.getValue("CLINICAREA_CODE",0);
                p.setData("REG_CLINICAREA",areaCode);
                TParm result1 = insertdata(p, connection);
                if (result1.getErrCode() < 0)
                    return result1;
            }
        }
        return result;
    }

    /**
     * չ��ǰ���
     * @param parm TParm
     * @return TParm
     */
    public TParm checkSpreadOut(TParm parm) {
        TParm result = new TParm();
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        //�õ���������������
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", adm_Date);
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("DR_CODE", parm.getValue("DR_CODE"));
            inParm.setDataN("DEPT_CODE", parm.getValue("DEPT_CODE"));
            result = SchDayTool.getInstance().selectdata(inParm);
            if (result.getCount() > 0) {
                inParm.setData("ADM_DATE", day);
                result = PatAdmTool.getInstance().selectdata(inParm);
                if (result.getCount() > 0) {
                    TParm errResult = new TParm();
                    errResult.setErr(1, "���йҺ���Ϣ,����չ���Ű�");
                    return errResult;
                }
            }
        }
        return result;
    }
    /**
     * չ��ǰ���(��)
     * @param parm TParm
     * @return TParm
     */
    public TParm checkSpreadOutNew(TParm parm) {
        TParm result = new TParm();
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        //�õ���������������
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", adm_Date);
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("CLINICROOM_NO", parm.getValue("CLINICROOM_NO"));
            result = SchDayTool.getInstance().selDataNew(inParm);
            if (result.getCount() > 0) {
                inParm.setData("ADM_DATE", day);
                result = PatAdmTool.getInstance().selDataNew(inParm);
                if (result.getCount() > 0) {
                    TParm errResult = new TParm();
                    errResult.setErr(1, "���йҺ���Ϣ,����չ���Ű�");
                    return errResult;
                }
            }
        }
        return result;
    }
    /**
     * �����հ��
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����հ��(For ONW)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForSchdayDr(TParm parm, TConnection conn) {

        TParm result = update("update_for_schday_dr", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�հ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        String admDate = "";
        if (parm.getData("ADM_DATE") instanceof String) {
            admDate = parm.getValue("ADM_DATE");
        }
        else {
            admDate = StringTool.getString(parm.getTimestamp("ADM_DATE"),
                                           "yyyyMMdd");
        }
        parm.setData("ADM_DATE", admDate);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ת�ղ�ѯ�հ��(��)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm) {
        String admDate = "";
        if (parm.getData("ADM_DATE") instanceof String) {
            admDate = parm.getValue("ADM_DATE");
        }
        else {
            admDate = StringTool.getString(parm.getTimestamp("ADM_DATE"),
                                           "yyyyMMdd");
        }
        parm.setData("ADM_DATE", admDate);
        String regionWhere = "";
        if(parm.getData("REGION_CODE")!=null)
              regionWhere= " AND REGION_CODE = '"+parm.getData("REGION_CODE")+"' ";
        String admTypeWhere = "";
        if(parm.getData("ADM_TYPE")!=null)
              admTypeWhere= " AND ADM_TYPE = '"+parm.getData("ADM_TYPE")+"' ";
        String roomWhere = "";
        if(parm.getData("CLINICROOM_NO")!=null)
              roomWhere= " AND CLINICROOM_NO IN ("+parm.getData("CLINICROOM_NO")+") ";
        String sql =
                " SELECT REGION_CODE, ADM_TYPE, ADM_DATE, SESSION_CODE, CLINICROOM_NO,"+
                "        WEST_MEDI_FLG, DEPT_CODE, REG_CLINICAREA, DR_CODE, REALDEPT_CODE,"+
                "        REALDR_CODE, CLINICTYPE_CODE, QUEGROUP_CODE, QUE_NO, MAX_QUE, VIP_FLG,"+
                "        CLINICTMP_FLG, STOP_SESSION, REFRSN_CODE, OPT_USER, OPT_DATE, OPT_TERM "+
                "   FROM REG_SCHDAY "+
                "  WHERE ADM_DATE = '"+admDate+"' "+
                regionWhere+
                admTypeWhere+
                roomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
         Timestamp admDate = TCM_Transform.getTimestamp(parm.getData(
             "ADM_DATE"));
         String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ת��ɾ��(��������)
     * @param parm TParm
     * @return TParm
     */
    public TParm delDataNew(TParm parm) {

        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData(
                "ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
                " DELETE FROM REG_SCHDAY " +
                "      WHERE ADM_DATE = '" + adm_Date + "' " +
                regionWhere +
                admTypeWhere +
                roomWhere;
        //System.out.println("ɾ���հ�"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯҽʦ�Ű�(һ��)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDrTable(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //add by huangtt 20150612 start �Һ�����ѯ
        for (int i = 0; i < result.getCount(); i++) {
        	if("VIP".equals(result.getValue("TYPE", i))){
        		int queNo = getRegPatadmQue(result.getRow(i));
    			result.setData("QUE_NO", i, queNo);
        	}
			
		}
        //add by huangtt 20150612 end �Һ�����ѯ
        return result;
    }
    /**
     * ��ѯҽʦ�Ű���ĳһ����ľ�����Ϣ(һ��)У��İ��Ƿ����
     * yanjing
     * 2013-05-07
     */
    public TParm selectOneDrTable(TParm parm) {
//        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
//        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryOneDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯҽʦ���VIP��
     * @param parm TParm
     * @return TParm
     */
    public TParm selVIPDrTable(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryVipDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�����,�������
     * @param region String
     * @param admType String
     * @param admDate Timestamp
     * @param session String
     * @param clinicRoom String
     * @return int
     */
    public int  selectqueno(String region, String admType, String admDate,
                           String session, String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", region);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", session);
        parm.setData("CLINICROOM_NO", clinicRoom);
        if (this.getResultInt(query("selectqueno", parm), "QUE_NO") >
            this.getResultInt(query("selectqueno", parm), "MAX_QUE")) {
            return -1;
        }
        return this.getResultInt(query("selectqueno", parm), "QUE_NO");

    }
    /**
     * ���¾����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatequeno(TParm parm,TConnection connection) {
		if ("java.sql.Timestamp".equalsIgnoreCase(parm.getData("ADM_DATE")
				.getClass().getName())) { // modify by wanglong 20121225
			String adm_Date = StringTool.getString(parm.getTimestamp("ADM_DATE"), "yyyyMMdd");
			parm.setData("ADM_DATE", adm_Date);
		}
		TParm result = update("updatequeno", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
    /**
     * ���¾����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     * =============pangben 2012-6-18 �غ�����
     */
    public TParm updatequeno(TParm parm) {
//        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
//        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("updatequeno", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �õ�VIP���λ
     * @param region String ����
     * @param admType String �ż���
     * @param admDate String ����
     * @param session String ʱ��
     * @param clinicRoom String ����
     * @return boolean
     */
    public boolean isVipflg(String region, String admType, String admDate,
                            String session, String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", region);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", session);
        parm.setData("CLINICROOM_NO", clinicRoom);
        return getResultBoolean(query("selectclinictype", parm), "VIP_FLG");

    }

    /**
     * ͨ�����Ҳ�ѯ����
     * @param parm TParm
     * @return TParm
     */
    public TParm SELECT_REG_SCHDAY_CLINICROOM(TParm parm) {
        TParm result = new TParm();
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        result = query("select_reg_clinicroom_schday", parm);
        return result;
    }
    /**
     * ͨ�����Ҳ�ѯ��Ӧ����
     * @param clinicRoom String
     * @return TParm
     */
    public TParm selclinicAreaByRoom(String clinicRoom) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", clinicRoom);
        result = query("selclinicAreaByRoom", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ͨ�����ҽʦά��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm INSERT_REG_SCHDAY_FOR_SCHDAY_DR(TParm parm,
                                                 TConnection connection) {
        TParm result = new TParm();
//         String admDate = TCM_Transform.getString(TCM_Transform.getTimestamp(parm.getData("ADM_DATE"),"yyyyMMdd"));
//         String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//         parm.setData("ADM_DATE", admDate);

        result = update("insert_reg_schday_for_schday_dr", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ͨ���ű�ɸѡ����ҽʦ
     * @param parm TParm
     * @return TParm
     */
    public TParm selDrByClinicType(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("selDrByClinicType", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �õ�����ҽ���
     * @param regionCode String ����
     * @param admType String �ż���
     * @param admDate String ��������
     * @param sessionCode String ʱ��
     * @param clinicroomNo String ����
     * @return string
     */
    public String selWestMediFlg(String regionCode, String admType,
                                  String admDate, String sessionCode,
                                  String clinicroomNo) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE",regionCode);
        parm.setData("ADM_TYPE",admType);
        parm.setData("ADM_DATE",admDate);
        parm.setData("SESSION_CODE",sessionCode);
        parm.setData("CLINICROOM_NO",clinicroomNo);
        result = query("selWestMediFlg",parm);
        if(result.getErrCode()<0){
            err(result.getErrName()+ " " +result.getErrText());
        }
        String westMediFlg = result.getValue("",0);
        return westMediFlg;
    }
    /**
     * �õ���ʱ��ע��
     * @param regionCode String ����
     * @param admType String �ż���
     * @param admDate String ��������
     * @param sessionCode String ʱ��
     * @param clinicroomNo String ����
     * @return boolean
     */
    public boolean selClinicTmpFlg(String regionCode, String admType,
                                   String admDate, String sessionCode, //CLINICTMP_FLG
                                   String clinicroomNo) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE", regionCode);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", sessionCode);
        parm.setData("CLINICROOM_NO", clinicroomNo);
        result = query("selClinicTmpFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;

    }
    /**
     * ����
     * ====zhangp 20120621
     * @param parm
     * @param connection
     * @return TParm
     */
    public TParm updateRegSchDay(TParm parm,TConnection connection){
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
    	String sql =
    		" UPDATE REG_SCHDAY" +
    		" SET REALDEPT_CODE = '" + parm.getValue("REALDEPT_CODE") + "'," +
    		" REALDR_CODE = '" + parm.getValue("REALDR_CODE") + "'" +
    		" WHERE REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
    		" AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'" +
    		" AND ADM_DATE = '" + adm_Date + "'" +
    		" AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") + "'" +
    		" AND CLINICROOM_NO = '" + parm.getValue("CLINICROOM_NO") + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    	if(result.getErrCode()<0){
    		err(result.getErrName() + " " + result.getErrText());
    		connection.rollback();
    		return result;
    	}
    	sql = 
    		" UPDATE REG_PATADM" +
    		" SET REALDR_CODE = '" + parm.getValue("REALDR_CODE") + "'," +
    		" REALDEPT_CODE = '" + parm.getValue("REALDEPT_CODE") + "'" +
    		" WHERE REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
    		" AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'" +
    		" AND ADM_DATE = TO_DATE ('" + adm_Date + "', 'YYYYMMDD')" +
    		" AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") + "'" +
    		" AND CLINICROOM_NO = '" + parm.getValue("CLINICROOM_NO") + "'";
    	result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    	if(result.getErrCode()<0){
    		err(result.getErrName() + " " + result.getErrText());
    		connection.rollback();
    		return result;
    	}
    	return result;
    }
    
    /**
     * ͣ����� 
     * @param parm
     * @param connection
     * @return
     */
    public  TParm updateRegSchDayStop(TParm parm){
    	 Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
         String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
     	String sql =
     		" UPDATE REG_SCHDAY" +
     		" SET STOP_SESSION = '" + parm.getValue("STOP_SESSION") + "'," +
     		" REFRSN_CODE = '" + parm.getValue("REFRSN_CODE") + "'" +
     		" WHERE REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
     		" AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'" +
     		" AND ADM_DATE = '" + adm_Date + "'" +
     		" AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") + "'" +
     		" AND CLINICROOM_NO = '" + parm.getValue("CLINICROOM_NO") + "'";
     	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
     	if(result.getErrCode()<0){
     		err(result.getErrName() + " " + result.getErrText());

     		return result;
     	}
     	return result;
    }
    
    /**
     * �õ��Һ���   add by huangtt 20150612
     * @param parm
     * @return
     */
    public int getRegPatadmQue(TParm parm){
    	String sql = "SELECT COUNT (CASE_NO) QUE_NO" +
    			" FROM REG_PATADM" +
    			" WHERE ADM_TYPE = '"+parm.getValue("ADM_TYPE")+"'" +
    			" AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"'" +
    			" AND CLINICROOM_NO = '"+parm.getValue("CLINICROOM_NO")+"'" +
    			" AND ADM_DATE = TO_DATE ('"+parm.getValue("ADM_DATE")+"', 'YYYYMMDD')" +
    			" AND REGCAN_USER IS NULL";
//    	System.out.println(sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result.getInt("QUE_NO", 0);
    }

}
