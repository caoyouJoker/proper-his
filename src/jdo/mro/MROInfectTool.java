package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.SYSPostTool;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 传染病报告卡</p>
 *
 * <p>Description: 传染病报告卡</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-10
 * @version 1.0
 */
public class MROInfectTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROInfectTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROInfectTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROInfectTool();
        return instanceObject;
    }

    public MROInfectTool() {
        this.setModuleName("mro\\MROInfectModule.x");
        this.onInit();
    }
    /**
     * 查询传染病报告卡信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfect(TParm parm){
        TParm result = this.query("selectInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入传染病报告卡信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfect(TParm parm){
        TParm result = this.update("insertInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改传染病报告卡信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfect(TParm parm){
        TParm result = this.update("updateInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检查该病患是否已经有传染病报告卡记录 (根据病案号和CASE_NO查询)
     * @param MR_NO String
     * @param CASE_NO String
     * @return TParm
     */
    public TParm checkInfectCount(TParm parm){
        TParm result = this.query("checkInfectCount",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检查病患此次看诊是否存在传染病报告卡记录
     * @param MR_NO String
     * @param CASE_NO String
     * @return boolean  true:存在   false:不存在
     */
    public boolean checkHasInfect(TParm parm){
        TParm result = this.checkInfectCount(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        if(result.getInt("NUM",0)>0){
            return true;
        }
        return false;
    }
    /**
     * 获取某一病患某一次看诊填报的传染病报告卡的最大序号
     * @param MR_NO String
     * @param CASE_NO String
     * @return String
     */
    public int getMaxSEQ(String MR_NO,String CASE_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("getMaxSEQ",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return 1;
        }
        int MAXSEQ = result.getInt("CARD_SEQ_NO",0) + 1;
        return MAXSEQ;
    }
    /**
     * 获取传染病报告卡打印数据
     * @param MR_NO String  病案号
     * @param CASE_NO String  就诊序号
     * @param CARD_NO_SEQ String  报告卡序号
     * @return TParm
     */
    public TParm getPrintData(String MR_NO,String CASE_NO,String CARD_SEQ_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",CARD_SEQ_NO);
        TParm data = this.query("selectInfect",parm);
        if(data.getErrCode()<0){
            err("ERR:" + data.getErrCode() + data.getErrText() +
                data.getErrName());
            return data;
        }
        TParm result = new TParm();
        result.setData("CARD_NO","TEXT",data.getValue("CARD_NO",0));//卡片编号
        result.setData("FIRST_FLG","TEXT","0".equals(data.getValue("FIRST_FLG",0))?"初次报告":"订正报告");//报卡类别
        result.setData("PAT_NAME","TEXT",data.getValue("PAT_NAME",0));
        result.setData("GENEARCH_NAME","TEXT",data.getValue("GENEARCH_NAME",0));
        result.setData("IDNO", "TEXT", data.getValue("IDNO", 0));
        result.setData("SEX", "TEXT",
                       MROPrintTool.getInstance().getDesc("SYS_DICTIONARY",
            "SYS_SEX", "CHN_DESC", "ID", data.getValue("SEX", 0)));//性别
        result.setData("BIRTH_DATE", "TEXT",StringTool.getString(data.getTimestamp("BIRTH_DATE", 0),"yyyy年MM月dd日"));
        //计算实足年龄
        if(data.getTimestamp("BIRTH_DATE",0)!=null){
            result.setData("AGE","TEXT", StringUtil.showAge(data.getTimestamp("BIRTH_DATE", 0), data.getTimestamp("PAD_DATE",0)));
        }else{
            result.setData("AGE","TEXT","");
        }
        result.setData("OFFICE","TEXT",data.getValue("OFFICE",0));
        result.setData("CONT_TEL","TEXT",data.getValue("CONT_TEL",0));
        result.setData("SICK_ZONE_"+data.getValue("SICK_ZONE",0),"TEXT","■");//病人所在地区
        //根据邮编获取省市信息
        TParm post = SYSPostTool.getInstance().getProvinceCity(data.getValue("ADDRESS_COUNTRY",0));
        result.setData("ADDRESS_PROVICE","TEXT",post.getValue("STATE",0));
        result.setData("ADDRESS_COUNTRY","TEXT",post.getValue("CITY",0));
        result.setData("ADDRESS_ROAD","TEXT",data.getValue("ADDRESS_ROAD",0));
        result.setData("ADDRESS_THORP","TEXT",data.getValue("ADDRESS_THORP",0));
        result.setData("DOORPLATE","TEXT",data.getValue("DOORPLATE",0));
        result.setData("INVALID_PROF_" + data.getValue("INVALID_PROF", 0),
                       "TEXT", "■"); //职业
        result.setData("REST_PROF", "TEXT",
                       "17".equals(data.getValue("INVALID_PROF", 0)) ?
                       data.getValue("REST_PROF", 0) : "");//其他职业
        result.setData("DOUBT_CASE", "TEXT", "Y".equals(data.getValue("DOUBT_CASE", 0))?"■":"");
        result.setData("CLINIC_DIAGNOSE","TEXT","Y".equals(data.getValue("CLINIC_DIAGNOSE", 0))?"■":"");
        result.setData("LAB_DIAGNOSE","TEXT","Y".equals(data.getValue("LAB_DIAGNOSE", 0))?"■":"");
        result.setData("PATHOGENY_SCHLEP","TEXT","Y".equals(data.getValue("PATHOGENY_SCHLEP", 0))?"■":"");
        result.setData("VIRUS_TYPE_"+data.getValue("VIRUS_TYPE",0),"TEXT","■");//乙肝、血吸虫病类型
        result.setData("ILLNESS_DATE","TEXT",StringTool.getString(data.getTimestamp("ILLNESS_DATE",0),"yyyy年MM月dd日"));//发病日期
        result.setData("COMFIRM_DATE","TEXT",StringTool.getString(data.getTimestamp("COMFIRM_DATE",0),"yyyy年MM月dd日"));//诊断日期
        result.setData("DEAD_DATE","TEXT",StringTool.getString(data.getTimestamp("DEAD_DATE",0),"yyyy年MM月dd日"));//死亡日期
        result.setData("PLAGUE_SPOT","TEXT","Y".equals(data.getValue("PLAGUE_SPOT",0))?"■":"");//鼠疫
        result.setData("CHOLERA_FLG","TEXT","Y".equals(data.getValue("CHOLERA_FLG",0))?"■":"");//霍乱
        result.setData("SARS_FLG","TEXT","Y".equals(data.getValue("SARS_FLG",0))?"■":"");//传染性非典型肺炎
        result.setData("AIDS_FLG","TEXT","Y".equals(data.getValue("AIDS_FLG",0))?"■":"");//艾滋病
        result.setData("VIRUS_HEPATITIS_"+data.getValue("VIRUS_HEPATITIS",0),"TEXT","■");//病毒性肝炎
        result.setData("POLIOMYELITIS_FLG","TEXT","Y".equals(data.getValue("POLIOMYELITIS_FLG",0))?"■":"");//骨髓灰质炎
        result.setData("HIGH_FLU","TEXT","Y".equals(data.getValue("HIGH_FLU",0))?"■":"");//人感染高致病性禽流感
        result.setData("HIVES_FLG","TEXT","Y".equals(data.getValue("HIVES_FLG",0))?"■":"");//麻疹
        result.setData("EPIDEMIC_BLOOD","TEXT","Y".equals(data.getValue("EPIDEMIC_BLOOD",0))?"■":"");//流行性出血热
        result.setData("LYSSA","TEXT","Y".equals(data.getValue("LYSSA",0))?"■":"");//狂犬病
        result.setData("EPIDEMIC_HEPATITIS","TEXT","Y".equals(data.getValue("EPIDEMIC_HEPATITIS",0))?"■":"");//流行性乙型脑炎
        result.setData("DENGUE","TEXT","Y".equals(data.getValue("DENGUE",0))?"■":"");//登革热
        result.setData("CHARCOAL_"+data.getValue("CHARCOAL",0),"TEXT","■");//炭疽
        result.setData("DIARRHEA_"+data.getValue("DIARRHEA",0),"TEXT","■");//痢疾
        result.setData("PHTHISIC_"+data.getValue("PHTHISIC",0),"TEXT","■");//肺结核
        result.setData("TYPHOID_"+data.getValue("TYPHOID",0),"TEXT","■");//伤寒
        result.setData("EPIDEMIC_CEPHALITIS","TEXT","Y".equals(data.getValue("EPIDEMIC_CEPHALITIS",0))?"■":"");//流行性脑脊髓膜炎
        result.setData("CHINCOUGH","TEXT","Y".equals(data.getValue("CHINCOUGH",0))?"■":"");//百日咳
        result.setData("DIPHTHERIA","TEXT","Y".equals(data.getValue("DIPHTHERIA",0))?"■":"");//白喉
        result.setData("NEW_LOCKJAW","TEXT","Y".equals(data.getValue("NEW_LOCKJAW",0))?"■":"");//新生儿破伤风
        result.setData("SCARLATINA","TEXT","Y".equals(data.getValue("SCARLATINA",0))?"■":"");//猩红热
        result.setData("BRUCE_DISEASE","TEXT","Y".equals(data.getValue("BRUCE_DISEASE",0))?"■":"");//布鲁氏菌病
        result.setData("GONORRHEA","TEXT","Y".equals(data.getValue("GONORRHEA",0))?"■":"");//淋病
        result.setData("LUES_"+data.getValue("LUES",0),"TEXT","■");//梅毒
        result.setData("CATCH_LEPTOSPIRA","TEXT","Y".equals(data.getValue("CATCH_LEPTOSPIRA",0))?"■":"");//钩端螺旋体病
        result.setData("SCHISTOSOMIASIS_FLG","TEXT","Y".equals(data.getValue("SCHISTOSOMIASIS_FLG",0))?"■":"");//血吸虫病
        result.setData("AGUE_"+data.getValue("AGUE",0),"TEXT","■");//疟疾
        result.setData("GRIPPE_FLG","TEXT","Y".equals(data.getValue("GRIPPE_FLG",0))?"■":"");//流行性感冒
        result.setData("MUMPS","TEXT","Y".equals(data.getValue("MUMPS",0))?"■":"");//流行性腮腺炎
        result.setData("MEASLES","TEXT","Y".equals(data.getValue("MEASLES",0))?"■":"");//风疹
        result.setData("ACUTE_CONJUNCTIVITIS","TEXT","Y".equals(data.getValue("ACUTE_CONJUNCTIVITIS",0))?"■":"");//急性出血性结膜炎
        result.setData("LEPRA","TEXT","Y".equals(data.getValue("LEPRA",0))?"■":"");//麻风病
        result.setData("SHIP_FEVER","TEXT","Y".equals(data.getValue("SHIP_FEVER",0))?"■":"");//流行性和地方性斑疹伤寒
        result.setData("KALA_AZAR","TEXT","Y".equals(data.getValue("KALA_AZAR",0))?"■":"");//黑热病
        result.setData("ECHINOCOCCOSIS","TEXT","Y".equals(data.getValue("ECHINOCOCCOSIS",0))?"■":"");//包虫病
        result.setData("FILARIASIS","TEXT","Y".equals(data.getValue("FILARIASIS",0))?"■":"");//丝虫病
        result.setData("EXPECT_CHOLERA","TEXT","Y".equals(data.getValue("EXPECT_CHOLERA",0))?"■":"");//除霍乱，细菌性和阿米巴性痢疾，伤寒和副伤寒以外的感染性腹泻
        result.setData("REST_INFECTION","TEXT",data.getValue("REST_INFECTION",0));//其他法定管理以及重点监测传染病
        result.setData("REMARK","TEXT",data.getValue("REMARK",0));//备注
        result.setData("REVISALILLNESS_NAME","TEXT",data.getValue("REVISALILLNESS_NAME",0));//订正病名
        result.setData("COUNTERMAND_REAS","TEXT",data.getValue("COUNTERMAND_REAS",0));//退卡原因
        result.setData("REPORT_UNIT","TEXT",data.getValue("REPORT_UNIT",0));//报告单位
        result.setData("CONT_TEL2","TEXT",data.getValue("CONT_TEL2",0));//联系电话2
        result.setData("SPEAKER","TEXT",data.getValue("SPEAKER",0));//报告医生
        result.setData("PAD_DEPT","TEXT",MROPrintTool.getInstance().getDesc("SYS_DEPT","","DEPT_CHN_DESC","DEPT_CODE",data.getValue("PAD_DEPT",0)));//报告科室
        result.setData("ICD_DESC","TEXT",getICD_DESC(data.getValue("ICD_CODE",0)));//原诊断
        result.setData("PAD_DATE","TEXT",StringTool.getString(data.getTimestamp("PAD_DATE",0),"yyyy年MM月dd日"));//填卡时间
        return result;
    }
    /**
     * ICD_CODE换取中文
     * @param s String
     * @return String
     */
    public String getICD_DESC(String s) {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector v = (Vector) parm.getData("ICD_CODE");
        Vector d = (Vector) parm.getData("ICD_CHN_DESC");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(v.get(i)))
                return "" + d.get(i);
        }
        return s;
    }
    /**
     * 删除传染病报告卡数据
     * @return TParm
     */
    public TParm delInfect(String MR_NO,String CASE_NO,String CARD_SEQ_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("CARD_SEQ_NO",CARD_SEQ_NO);
        TParm result = this.update("delInfect",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
