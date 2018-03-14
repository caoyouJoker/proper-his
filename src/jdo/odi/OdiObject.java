package jdo.odi;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.javahis.manager.*;
import jdo.sys.*;
import com.dongyang.util.StringTool;
import java.util.Map;
import java.util.HashMap;
import com.javahis.util.StringUtil;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.javahis.util.OrderUtil;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
/**
 * <p>Title: 住院模块总对象</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class OdiObject
    extends TDSObject {
    /**
     * 动作类名称
     */
    private String actionName = "action.odi.ODIAction";
    /**
     * 定义ODI_ORDER在TDSObject类中的KEY值的名字
     */
    public static final String ODI_ORDERTABLENAME="ODI_ORDER";
    /**
     * 定义ODI_ORDER_HISTORY在TDSObject类中的KEY值的名字
     */
    public static final String ODI_ORDER_HISTORYTABLENAME="ODI_ORDER_HISTORY";
    /**
     * 定义ODI_DSPNM在TDSObject类中的KEY值的名字
     */
    public static final String ODI_DSPNMTABLENAME="ODI_DSPNM";
    /**
     * 定义ODI_DSPND在TDSObject类中的KEY值的名字
     */
    public static final String ODI_DSPNDTABLENAME="ODI_DSPND";
    /**
     * 住院长期摆药时间
     */
    public static final String OID_DSPN_TIME="OID_DSPN_TIME";
    /**
     * 住院换日时间
     */
    public static final String ODI_START_TIME="ODI_START_TIME";
    /**
     * 临时用药预设频次
     */
    public static final String ODI_UDD_STAT_CODE="ODI_UDD_STAT_CODE";
    /**
     * 临时处置预设频次
     */
    public static final String ODI_ODI_STAT_CODE="ODI_ODI_STAT_CODE";
    /**
     * 长期处置预设频次
     */
    public static final String ODI_ODI_DEFA_FREG="ODI_ODI_DEFA_FREG";
    /**
     * 静脉药物配制中心排药时间
     */
    public static final String OID_IVA_EXPANDTIME="OID_IVA_EXPANDTIME";
    /**
     * 护士审核注记
     */
    public static final String INW_NS_CHECK_FLG="INW_NS_CHECK_FLG";
    /**
     * 儿童处方签年龄设定
     */
    public static final String RX_CHILD_AGE="RX_CHILD_AGE";
    /**
     * 处方签每页种类数
     */
    public static final String PAGE_NUM = "PAGE_NUM";
    /**
     * 预设饮片付数
     */
    public static final String DCT_TAKE_DAYS = "DCT_TAKE_DAYS";
    /**
     * 预设饮片使用计量
     */
    public static final String DCT_TAKE_QTY = "DCT_TAKE_QTY";
    /**
     * 中药常用频次，默认一天两次
     */
    public static final String G_FREQ_CODE = "G_FREQ_CODE";
    /**
     * 中药常用用法，口服
     */
    public static final String G_ROUTE_CODE = "G_ROUTE_CODE";
    /**
     * 中医常用，煎法
     */
    public static final String G_DCTAGENT_CODE = "G_DCTAGENT_CODE";
    /**
     * 住院医生站注记
     */
    private boolean odiFlg = false;
    
    public  TParm inparam = null;//手术医生站参数
    /**
     * 构造器
     */
    public OdiObject() {
        this.setAttribute("OPT_USER", Operator.getID());
        this.setAttribute("OPT_TERM", Operator.getIP());
        //护士审核注记
        this.setAttribute(INW_NS_CHECK_FLG,getOdiSysParmData("NS_CHECK_FLG"));
        //住院长期摆药时间
        this.setAttribute(OID_DSPN_TIME,getOdiSysParmData("DSPN_TIME"));
        //住院换日时间
        this.setAttribute(ODI_START_TIME,getOdiSysParmData("START_TIME"));
        //临时用药预设频次
        this.setAttribute(ODI_UDD_STAT_CODE,getOdiSysParmData("UDD_STAT_CODE"));
        //临时处置预设频次
        this.setAttribute(ODI_ODI_STAT_CODE,getOdiSysParmData("ODI_STAT_CODE"));
        //长期处置预设频次
        this.setAttribute(ODI_ODI_DEFA_FREG,getOdiSysParmData("ODI_DEFA_FREG"));
        //静脉药物配制中心排药时间
        this.setAttribute(OID_IVA_EXPANDTIME,getOdiSysParmData("IVA_EXPANDTIME"));
        //儿童处方签年龄设定
        this.setAttribute(RX_CHILD_AGE,getOpdSysParmData("AGE"));
        //处方签每页种类数
        this.setAttribute(PAGE_NUM,getOpdSysParmData("PAGE_NUM"));
        //预设饮片付数
        this.setAttribute(DCT_TAKE_DAYS,getOpdSysParmData("DCT_TAKE_DAYS"));
        //预设饮片使用计量
        this.setAttribute(DCT_TAKE_QTY,getOpdSysParmData("DCT_TAKE_QTY"));
        //中药常用频次，默认一天两次
        this.setAttribute(G_FREQ_CODE,getOpdSysParmData("G_FREQ_CODE"));
        //中药常用用法，口服
        this.setAttribute(G_ROUTE_CODE,getOpdSysParmData("G_ROUTE_CODE"));
        //中医常用，煎法
        this.setAttribute(G_DCTAGENT_CODE,getOpdSysParmData("G_DCTAGENT_CODE"));
    }
    /**
     * 设置SQL并初始化DataStore
     * @param name String
     * @param sqlStr String
     * @param newRowDefaultColumn String
     * @param saveNewRowDefaultColumn String
     * @param saveModifiedRowDefaultColumn String
     */
    public void setSQL(String name, String sqlStr, String newRowDefaultColumn,
                       String saveNewRowDefaultColumn,
                       String saveModifiedRowDefaultColumn) {
        //添加到被观察列表(是否有观察对象是业务来决定)
        TDS ds = addDS(name);
        //设置DATASTORE的数据
        ds.setSQL(sqlStr);
        //为显示“病患名字”专门使用的一个方法
        String sql=this.getAttributeString("PAT_NAME");
        ds.addColumnSql("PAT_NAME",sql);
        //新增行是默认带入列一般为主键
        ds.setNewRowDefaultColumn(newRowDefaultColumn);
        //保存时行的默认新增行补充数据
        ds.setSaveNewRowDefaultColumn(saveNewRowDefaultColumn);
        //保存时修改的列保存数据
        ds.setSaveModifiedRowDefaultColumn(saveModifiedRowDefaultColumn);
    }
    /**
     * 设置ODI_ORDER的数据集
     * @param sqlStr String
     */
    public void setOdiOrderSQL(TDS ds) {
        TParm parm = this.getAttributeTParm("ORDERWHEREDATA");
        String whereStr = this.creatWhereStr(parm);
        ds.setSQL("SELECT * FROM ODI_ORDER " + whereStr +" ORDER BY ORDER_NO,ORDER_SEQ");
    }
    /**
     * 设置ODI_DSPNM的数据集
     * @param sqlStr String
     */
    public void setOdiDspnmSQL(TDS ds) {
        TParm parm = this.getAttributeTParm("DSPNMWHEREDATA");
        String whereStr = this.creatWhereStr(parm);
        ds.setSQL("SELECT * FROM ODI_DSPNM " + whereStr);
    }
    /**
     * 设置ODI_DSPND的数据集
     * @param sqlStr String
     */
    public void setOdiDspndSQL(TDS ds) {
        TParm parm = this.getAttributeTParm("DSPNDWHEREDATA");
        String whereStr = this.creatWhereStr(parm);
        ds.setSQL("SELECT * FROM ODI_DSPND " + whereStr);
    }
    /**
     * 设置ODI_ORDER的数据集
     * @param sqlStr String
     */
    public void setOdiOrderHistorySQL(TDS ds) {
        TParm parm = this.getAttributeTParm("ORDERWHEREDATA");
        String whereStr = this.creatWhereStr(parm);
        ds.setSQL("SELECT * FROM ODI_ORDER_HISTORY " + whereStr +" ORDER BY ORDER_NO,ORDER_SEQ");
    }

    /**
    * 自定义设置ODI_ORDER的数据集
    * @param sqlStr String
    */
   public void setOdiOrderSQL(TDS ds,String sqlStr) {
       ds.setSQL(sqlStr);
   }
   /**
    * 自定义设置ODI_DSPNM的数据集
    * @param sqlStr String
    */
   public void setOdiDspnmSQL(TDS ds,String sqlStr) {
       ds.setSQL(sqlStr);
   }
   /**
    * 自定义设置ODI_DSPND的数据集
    * @param sqlStr String
    */
   public void setOdiDspndSQL(TDS ds,String sqlStr) {
       ds.setSQL(sqlStr);
   }

    /**
     * 组合WHERE条件
     * @param parm TParm
     * @return String
     */
    public String creatWhereStr(TParm parm) {
        String whereStr = " WHERE ";
        String str[] = parm.getNames();
        if (str.length == 0)
            return "";
        for (String temp : str) {
            //检测是否是第一个元素（不加‘AND’）
            if (temp == (str[0])) {
                whereStr += temp + "='" + parm.getData(temp).toString() + "'";
                continue;
            }
            whereStr += " AND " + temp + "='" + parm.getData(temp).toString() + "'";
        }
        return whereStr;
    }

    /**
     * 执行DataStore加载
     * @return boolean
     */
    public boolean retrieve() {
        if (!super.retrieve()){
            return false;
        }
        //执行绑定医生站观察者对象
        this.bindOdiStationObserverObject();
        return true;
    }
    /**
     * 注册住院医生站观察者对象绑定
     */
    public void bindOdiStationObserverObject(){
        //得到护士审核执行标记
        boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
        //住院医生站的观察者绑定(判断条件:护士审核注记为N并且是医生站时)
        if ( !nsCheckFlg && odiFlg) {
//            System.out.println("住院医生站的观察者绑定(判断条件:护士审核注记为N并且是医生站时)");
            //得到ODI_ORDER对象
            TDS odiOrderDs = this.getDS(ODI_ORDERTABLENAME);
            //当为空的时候自动根据前台的条件parm来创建TDS
            if (odiOrderDs == null) {
                odiOrderDs = new TDS();
                this.setOdiOrderSQL(odiOrderDs);
                odiOrderDs.retrieve();
                this.addDS(ODI_ORDERTABLENAME,odiOrderDs);
            }
            //得到ODI_DSPNM对象
            TDS odiDspnmDs = this.getDS(ODI_DSPNMTABLENAME);
            if (odiDspnmDs == null) {
                odiDspnmDs = new TDS();
                this.setOdiDspnmSQL(odiDspnmDs);
                odiDspnmDs.retrieve();
                this.addDS(ODI_DSPNMTABLENAME,odiDspnmDs);
                //新增行是默认带入列一般为主键
                this.getDS(ODI_DSPNMTABLENAME).setNewRowDefaultColumn("CASE_NO");
                //保存时行的默认新增行补充数据
                this.getDS(ODI_DSPNMTABLENAME).setSaveNewRowDefaultColumn("OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE:SAVE_TIME;ORDER_NO;ORDER_DR_CODE");
                //保存时修改的列保存数据
                this.getDS(ODI_DSPNMTABLENAME).setSaveModifiedRowDefaultColumn("OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
            }
            //得到ODI_DSPND对象
            TDS odiDspndDs = this.getDS(ODI_DSPNDTABLENAME);
            if (odiDspndDs == null) {
                odiDspndDs = new TDS();
                this.setOdiDspndSQL(odiDspndDs);
                odiDspndDs.retrieve();
                this.addDS(ODI_DSPNDTABLENAME,odiDspndDs);
                //新增行是默认带入列一般为主键
                this.getDS(ODI_DSPNDTABLENAME).setNewRowDefaultColumn("CASE_NO");
                //保存时行的默认新增行补充数据
                this.getDS(ODI_DSPNDTABLENAME).setSaveNewRowDefaultColumn("OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE;ORDER_NO");
                //保存时修改的列保存数据
                this.getDS(ODI_DSPNDTABLENAME).setSaveModifiedRowDefaultColumn("OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
            }
            //创建ODI_ORDER对象观察者
            OdiOrderObserverODI odiOrder = new OdiOrderObserverODI();
            odiOrder.setName("ODI_ORDER");
            //设置ODI_ORDER对象变化时需要关注ODI_DSPNM对象的数据变化
            odiOrder.setDS(odiDspnmDs);
            //注册ODI_ORDER观察者
            odiOrderDs.addObserver(odiOrder);
            //创建ODI_DSPNM对象观察者
            OdiDspnMObserverODI odiDspnm = new OdiDspnMObserverODI();
            odiDspnm.setName("ODI_DSPNM");
            //设置ODI_DSPNM对象变化时需要关注ODI_DSPND对象的数据变化
            odiDspnm.setDS(odiDspndDs);
            //注册ODI_DSPNM观察者
            odiDspnmDs.addObserver(odiDspnm);
        }
        //需要护士审核
        if ( nsCheckFlg && odiFlg){
            //得到ODI_ORDER对象
           TDS odiOrderDs = this.getDS(ODI_ORDERTABLENAME);
           //当为空的时候自动根据前台的条件parm来创建TDS
           if (odiOrderDs == null) {
               odiOrderDs = new TDS();
               this.setOdiOrderSQL(odiOrderDs);
               odiOrderDs.retrieve();
               this.addDS(ODI_ORDERTABLENAME,odiOrderDs);
           }
           //添加ODI_ORDER的观察者
           OdiOtherObserver otherObserver = new OdiOtherObserver();
           otherObserver.setName("OTHEROBSERVER");
           odiOrderDs.addObserver(otherObserver);
           //历史记录表
           TDS odiOrderHistoryDs = this.getDS(ODI_ORDER_HISTORYTABLENAME);
           if(odiOrderHistoryDs==null){
               odiOrderHistoryDs = new TDS();
               this.setOdiOrderHistorySQL(odiOrderHistoryDs);
               odiOrderHistoryDs.retrieve();
               this.addDS(ODI_ORDER_HISTORYTABLENAME,odiOrderHistoryDs);
           }
           //System.out.println("历史表==");
           //odiOrderHistoryDs.showDebug();
        }
    }
    /**
     * 更新方法
     * @return boolean
     */
    public boolean update(){
        //得到护士审核执行标记
        boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
        boolean falg = true;
        if (odiFlg) {
            TParm parm = new TParm();
            //医生站
            odiUpdateData();
            //查找需要更新修改添加的数据数组
            String sqlArray[] = getUpdateSQL();
            //检验检查医嘱新增
            String medSql[] = getMedSql();
            if(medSql!=null){
                 sqlArray = StringUtil.getInstance().copyArray(sqlArray,medSql);
            }
            //调试
            /**for(String temp:sqlArray){
                System.out.println("当前SQL:"+temp);
            }**/
            parm.setData("ARRAY",sqlArray);
            TParm actionParm = TIOM_AppServer.executeAction(actionName,
                "saveOrder", parm);
            if(actionParm.getErrCode()<0)
                falg = false;
            //删除观察者
            if (!nsCheckFlg && odiFlg) {
                this.getDS(ODI_ORDERTABLENAME).removeObserver("ODI_ORDER");
                this.getDS(ODI_DSPNMTABLENAME).removeObserver("ODI_DSPNM");
            }else{
                this.getDS(ODI_ORDERTABLENAME).removeObserver("OTHEROBSERVER");
            }
        }
        return falg;
    }
    /**
     * 得到检验检查接口更新语句
     * @return String[]
     */
    public String[] getMedSql(){
        String[] sqlArray;
        List list = new ArrayList();
        int arraySize= 0;
        int upArraySize=0;
        TDS ds = this.getDS(this.ODI_ORDERTABLENAME);
        String buff = ds.isFilter()?ds.FILTER:ds.PRIMARY;
        int newOrder[] = ds.getNewRows(buff);
        int modifOrder[] = ds.getOnlyModifiedRows(buff);
        for(int i:newOrder){
            if(!ds.isActive(i,buff))
                continue;
            TParm temp = ds.getRowParm(i,buff);
            //检验
            if("LIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
               arraySize++;
               String sql = creatLISRISSQL(temp);
//               System.out.println("-=----------------------------"+sql);
               list.add(sql);
            }
            //检查
            if("RIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
                arraySize++;
                String sql = creatLISRISSQL(temp);
                list.add(sql);
            }
        }
        for(int i:modifOrder){
            TParm temp = ds.getRowParm(i,buff);
            //检验
            if("LIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
               upArraySize++;
               String sql = creatLISRISUpdateSQL(temp);
               list.add(sql);
            }
            //检查
            if("RIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
                upArraySize++;
                String sql = creatLISRISUpdateSQL(temp);
                list.add(sql);
            }
        }
        int delArraySize=0;  //SHIBL 20130111 add 
        // 删除的医嘱处理
		int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
		if (delCount > 0) {
			TParm delParm = ds.getBuffer(ds.DELETE);
			int delRowCount = delParm.getCount("ORDER_CODE");
			for (int i = 0; i < delRowCount; i++) {
				TParm temp = delParm.getRow(i);
				//检验
	            if("LIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
	            	delArraySize++;
	               String sql = creatLISRISdelSQL(temp);
	               list.add(sql);
	            }
	            //检查
	            if("RIS".equals(temp.getValue("CAT1_TYPE"))&&"Y".equals(temp.getValue("SETMAIN_FLG"))){
	            	delArraySize++;
	                String sql = creatLISRISdelSQL(temp);
	                list.add(sql);
	            }
			}
		}
        sqlArray = (String[])list.toArray(new String[arraySize+upArraySize+delArraySize]);
//        for(String temp:sqlArray)
//            System.out.println("需要循环的SQL："+temp);
        if(sqlArray.length!=0)
            return sqlArray;
        else
            return null;
    }
    /**
     * 删除语句   SHIBL 20130111 add
     * @param parm
     * @return
     */
    private String creatLISRISdelSQL(TParm parm) {
    	String sqlStr = "DELETE FROM MED_APPLY WHERE  CAT1_TYPE='"+parm.getValue("CAT1_TYPE")+"' AND APPLICATION_NO='"+parm.getValue("MED_APPLY_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'";
		return sqlStr;
	}
	/**
     * 组成检验检查的SQL
     * @param parm TParm
     * @return String
     */
    public String creatLISRISSQL(TParm parm){
        //查询检体名称
        String optItemDesc = OrderUtil.getInstance().queryOptItem(parm.getValue("OPTITEM_CODE"));
        //得到诊断类别
        String icdType = OrderUtil.getInstance().queryDiagType(""+this.getAttribute("MAINDIAG"));
        //得到自费总价格
        double ownAmt = OrderUtil.getInstance().getOrderSetList(parm.getValue("ORDER_CODE"));
        //首餐时间
        String startDttm = StringTool.getString(parm.getTimestamp("START_DTTM"),"yyyyMMddHHmmss");
        //接口厂商名称
        String dealSystem=OrderUtil.getInstance().getDealSystem(parm.getValue("ORDER_CAT1_CODE"));
        //应付金额
        double arAmt = ownAmt*OrderUtil.getInstance().getOwnRate(""+this.getAttribute("CTZ_CODE"),parm.getValue("ORDER_CODE"));
        String sqlStr = "INSERT INTO MED_APPLY"+
                        " (CAT1_TYPE, APPLICATION_NO, CASE_NO, IPD_NO, MR_NO,"+
                        "  ADM_TYPE, PAT_NAME, PAT_NAME1, BIRTH_DATE, SEX_CODE,"+
                        "  POST_CODE, ADDRESS,COMPANY,TEL,IDNO,"+
                        "  DEPT_CODE, REGION_CODE, CLINICROOM_NO, STATION_CODE, BED_NO,"+
                        " ORDER_NO,SEQ_NO,ORDER_CODE,ORDER_DESC, ORDER_DR_CODE,"+
                        " ORDER_DATE, ORDER_DEPT_CODE, START_DTTM, EXEC_DEPT_CODE,EXEC_DR_CODE,"+
                        " OPTITEM_CODE, OPTITEM_CHN_DESC, ORDER_CAT1_CODE, DEAL_SYSTEM, RPTTYPE_CODE,"+
                        " DEV_CODE, REMARK, ICD_TYPE, ICD_CODE, STATUS,"+
                        " XML_DATE, NEW_READ_FLG, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE,"+
                        " DC_READ_FLG,REJECTRSN_CODE, RESERVED_DATE, REGISTER_DATE, INSPECT_DATE,"+
                        " INSPECT_TOTTIME,WAIT_TOTTIME, REPORT_DR, REPORT_DATE, EXAMINE_DR,"+
                        " EXAMINE_DATE,DIAGNOSIS_QUALITY,TECHNOLOGY_QUALITY, SERVICE_QUALITY, SEND_FLG,"+
                        " BILL_FLG,OWN_AMT, AR_AMT, OPT_USER, OPT_DATE,"+
                        " OPT_TERM,ORDER_ENG_DESC,URGENT_FLG,DR_NOTE)"+
                        " VALUES"+
                        "('"+parm.getValue("CAT1_TYPE")+"', '"+parm.getValue("MED_APPLY_NO")+"', '"+parm.getValue("CASE_NO")+"', '"+parm.getValue("IPD_NO")+"', '"+parm.getValue("MR_NO")+"',"+
                        " 'I', '"+this.getAttribute("PAT_NAME")+"', '"+this.getAttribute("PAT_NAME1")+"', TO_DATE('"+StringTool.getString((Timestamp)this.getAttribute("BIRTH_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'), '"+this.getAttribute("SEX_CODE")+"',"+
                        " '"+this.getAttribute("POST_CODE")+"', '"+this.getAttribute("ADDRESS")+"', '"+this.getAttribute("COMPANY_DESC")+"', '"+this.getAttribute("TEL")+"', '"+this.getAttribute("IDNO")+"',"+
                        " '"+parm.getValue("DEPT_CODE")+"', '"+parm.getValue("REGION_CODE")+"', '', '"+parm.getValue("STATION_CODE")+"', '"+parm.getValue("BED_NO")+"',"+
                        " '"+parm.getValue("ORDER_NO")+"', '"+parm.getValue("ORDER_SEQ")+"', '"+parm.getValue("ORDER_CODE")+"', '"+parm.getValue("ORDER_DESC")+"', '"+parm.getValue("ORDER_DR_CODE")+"',"+
                        " TO_DATE('"+StringTool.getString(parm.getTimestamp("EFF_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'), '"+parm.getValue("ORDER_DEPT_CODE")+"',TO_DATE('"+startDttm+"','YYYYMMDDHH24MISS'), '"+parm.getValue("EXEC_DEPT_CODE")+"', '"+parm.getValue("EXEC_DR_CODE")+"',"+
                        " '"+parm.getValue("OPTITEM_CODE")+"', '"+optItemDesc+"', '"+parm.getValue("ORDER_CAT1_CODE")+"', '"+dealSystem+"', '"+parm.getValue("RPTTYPE_CODE")+"',"+
                        " '"+parm.getValue("DEV_CODE")+"', '"+parm.getValue("REMARK")+"', '"+icdType+"', '"+this.getAttribute("MAINDIAG")+"', '0',"+
                        " '', 'N', '', '', '',"+
                        " '', '', '', '', '',"+   
                        " '', '', '', '', '',"+
                        " '', '', '', '', '0',"+
                        " 'N', '"+StringTool.round(ownAmt,2)+"', '"+StringTool.round(arAmt,2)+"', '"+Operator.getID()+"', SYSDATE,"+
                        " '"+Operator.getIP()+"','"+parm.getValue("ORDER_ENG_DESC")+"','"+parm.getValue("URGENT_FLG")+"','"+parm.getValue("DR_NOTE")+"')";
        return sqlStr;
    }
    /**
     * DC后对MED表的更新
     * @param parm TParm
     * @return String
     */
    public String creatLISRISUpdateSQL(TParm parm){
        String sqlStr = "UPDATE MED_APPLY SET DC_DR_CODE='"+parm.getValue("DC_DR_CODE")+"', DC_ORDER_DATE=SYSDATE, DC_DEPT_CODE='"+parm.getValue("DC_DEPT_CODE")+"',URGENT_FLG='"+parm.getValue("URGENT_FLG")+"',OPT_DATE=SYSDATE,OPT_USER='"+Operator.getID()+"',OPT_TERM='"+Operator.getIP()+"',DR_NOTE='"+parm.getValue("DR_NOTE")+"' WHERE CAT1_TYPE='"+parm.getValue("CAT1_TYPE")+"' AND APPLICATION_NO='"+parm.getValue("MED_APPLY_NO")+"'";
        return sqlStr;
    }
    /**
     * 查找需要更新修改的数据数组
     * @return String[]
     */
    public String[] getUpdateSQL(){
        boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
        String sqlArray[] = this.getDS(this.ODI_ORDERTABLENAME).getUpdateSQL();
        if(sqlArray.length>0){
        	sqlArray=getDeleteHistoryData(sqlArray);
        }
        sqlArray = StringUtil.getInstance().copyArray(sqlArray,
                    this.getDS(this.ODI_ORDER_HISTORYTABLENAME).getUpdateSQL());
      
        //护士不需要审核而且是医生站
        if (!nsCheckFlg && odiFlg) {
            if (this.getDS(this.ODI_DSPNMTABLENAME).getUpdateSQL().length != 0) {
                sqlArray = StringUtil.getInstance().copyArray(sqlArray,
                    this.getDS(this.ODI_DSPNMTABLENAME).getUpdateSQL());
            }
            if (this.getDS(this.ODI_DSPNDTABLENAME).getUpdateSQL().length != 0) {
                sqlArray = StringUtil.getInstance().copyArray(sqlArray,
                    this.getDS(this.ODI_DSPNDTABLENAME).getUpdateSQL());
            }
        }
        return sqlArray;
    }
    private String[] getDeleteHistoryData(String[] sqlArray){
    	 String data[] = new String[sqlArray.length];
    	 
    	 int insertIndex=0;
    	 for(int i=0;i<sqlArray.length;i++){
    		 if(sqlArray[i].startsWith("DELETE FROM ODI_ORDER ")){
    			 data[insertIndex++]=sqlArray[i].replaceFirst("ODI_ORDER", "ODI_ORDER_HISTORY");
    		 }
    	 }
    	 String returnData[] = new String[sqlArray.length+insertIndex];
    	 for(int i=0;i<sqlArray.length;i++){
    			 returnData[i]=sqlArray[i];
    	 }
    	 for(int i=0,j=0;i<data.length;i++){
    		 if(!StringUtil.isNullString(data[i])){
    			 returnData[sqlArray.length+j]=data[i];
    			 j++;
    		 }
    	 }
    	return returnData;
    }
    /**
     * 医生站
     */
    public void odiUpdateData(){
        boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
        Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
        TDS ds = this.getDS(ODI_ORDERTABLENAME);
        String buff = ds.isFilter()?ds.FILTER:ds.PRIMARY;
        //新增行数组ODI_ORDER
        int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
//        System.out.println("新增行数组个数" + newRowOrderId.length);
        //如果有新增行时调用存储过程拿到ORDER_NO
        if (newRowOrderId.length > 0) {
            //调用存储过程拿到ORDER_NO
            String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
                "ORDER_NO", "ORDER_NO");
            this.setAttribute("ORDER_NO", orderNo);
            this.setAttribute("ORDER_DR_CODE", Operator.getID());
            this.setAttribute("ORDER_DATE",
                              StringTool.getString(TJDODBTool.getInstance().
                getDBTime(), "yyyyMMddHHmmss"));
            TParm parm = new TParm();
            parm.setData("ORDER_NO",orderNo);
            /*parm.setData("ORDER_DR_CODE",Operator.getID());
            parm.setData("ORDER_DATE",StringTool.getString(sysdate, "yyyyMMddHHmmss"));*/
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_DATE",StringTool.getString(sysdate, "yyyyMMddHHmmss"));
            parm.setData("OPT_TERM",Operator.getIP());
            if(this.inparam != null ){
            	//parm.setData("ORDER_DR_CODE",inparam.getData("ODI", "ORDER_DR_CODE"));
            	//parm.setData("NS_CHECK_DATE",StringTool.getString(sysdate, "yyyyMMddHHmmss"));
                parm.setData("NS_CHECK_CODE",Operator.getID());
            }else{
            	 parm.setData("ORDER_DR_CODE",Operator.getID());
                 parm.setData("ORDER_DATE",StringTool.getString(sysdate, "yyyyMMddHHmmss"));
            }
             if (!nsCheckFlg && odiFlg) {
                 //其他参数赋值
                 otherParmSetSave(parm);
             }else{
                 for (int i : newRowOrderId) {
                     this.setItem(this.getDS(ODI_ORDERTABLENAME), i, parm, buff);

                 }
             }
            //设置条码号
            this.getLabNo();
        }
        //护士不需要审核并且是医生站
        if (!nsCheckFlg && odiFlg) {
            this.setSeqNo();
        }
        else {
            Integer rowId = 0;
            //赋值SEQ_NO
            for (int temp : newRowOrderId) {
                ds.setItem(temp, "ORDER_SEQ",
                    rowId += 1,buff);
//                ds.setItem(temp, "FRST_QTY",
//                    0,buff);
                if(!ds.getItemString(temp,"RX_KIND").equals("DS")){
                    if(ds.getItemString(temp,"CAT1_TYPE").equals("PHA")){
                        ds.setItem(temp, "LASTDSPN_QTY",
                                   0, buff);
                        ds.setItem(temp, "ACUMDSPN_QTY",
                                   0, buff);
                        ds.setItem(temp, "ACUMMEDI_QTY",
                                   0, buff);
                        ds.setItem(temp, "DISPENSE_QTY",
                                   0, buff);
                        ds.setItem(temp, "DISPENSE_UNIT",
                                   "", buff);
                    }
                }
//                ds.setItem(temp, "DOSAGE_QTY",
//                    0,buff);
//                ds.setItem(temp, "DOSAGE_UNIT",
//                           "",buff);
            }
        }
    }
    /**
     * 其他参数赋值
     * @param parm TParm
     */
    public void otherParmSetSave(TParm parm){
        TDS dsOrder = this.getDS(ODI_ORDERTABLENAME);
        String buffOrder = dsOrder.isFilter()?dsOrder.FILTER:dsOrder.PRIMARY;
        int newRowOrder[] = dsOrder.getNewRows(buffOrder);
        for(int temp:newRowOrder){
            if(!dsOrder.isActive(temp,buffOrder))
                continue;
            dsOrder.setItem(temp,"ORDER_NO",parm.getData("ORDER_NO"),buffOrder);
            dsOrder.setItem(temp,"ORDER_DR_CODE",parm.getData("ORDER_DR_CODE"),buffOrder);
            dsOrder.setItem(temp,"ORDER_DATE",parm.getData("ORDER_DATE"),buffOrder);
            dsOrder.setItem(temp,"OPT_USER",parm.getData("OPT_USER"),buffOrder);
            dsOrder.setItem(temp,"OPT_DATE",parm.getData("OPT_DATE"),buffOrder);
            dsOrder.setItem(temp,"OPT_TERM",parm.getData("OPT_TERM"),buffOrder);
        }
        TDS dsDspnm = this.getDS(ODI_DSPNMTABLENAME);
        String buffDspnm = dsDspnm.isFilter()?dsDspnm.FILTER:dsDspnm.PRIMARY;
        int newRowDspnm[] = dsDspnm.getNewRows(buffDspnm);
        for(int temp:newRowDspnm){
            if(!dsDspnm.isActive(temp,buffDspnm))
                continue;
            dsDspnm.setItem(temp,"ORDER_NO",parm.getData("ORDER_NO"),buffDspnm);
            dsDspnm.setItem(temp,"ORDER_DR_CODE",parm.getData("ORDER_DR_CODE"),buffDspnm);
            dsDspnm.setItem(temp,"ORDER_DATE",parm.getData("ORDER_DATE"),buffDspnm);
            dsDspnm.setItem(temp,"OPT_USER",parm.getData("OPT_USER"),buffDspnm);
            dsDspnm.setItem(temp,"OPT_DATE",parm.getData("OPT_DATE"),buffDspnm);
            dsDspnm.setItem(temp,"OPT_TERM",parm.getData("OPT_TERM"),buffDspnm);
        }
        TDS dsDspnd = this.getDS(ODI_DSPNDTABLENAME);
        String buffDspnd = dsDspnd.isFilter()?dsDspnd.FILTER:dsDspnd.PRIMARY;
        int newRowDspnd[] = dsDspnd.getNewRows(buffDspnd);
        for(int temp:newRowDspnd){
            if(!dsDspnd.isActive(temp,buffDspnd))
                continue;
            dsDspnd.setItem(temp,"ORDER_NO",parm.getData("ORDER_NO"),buffDspnd);
            dsDspnd.setItem(temp,"ORDER_DATE",parm.getData("ORDER_DATE"),buffDspnd);
            dsDspnd.setItem(temp,"OPT_USER",parm.getData("OPT_USER"),buffDspnd);
            dsDspnd.setItem(temp,"OPT_DATE",parm.getData("OPT_DATE"),buffDspnd);
            dsDspnd.setItem(temp,"OPT_TERM",parm.getData("OPT_TERM"),buffDspnd);
        }
    }
    /**
     * 设置SEQ_NO在最后保存时
     */
    public void setSeqNo(){
        Integer rowIdOrder = 0;
        Integer rowIdDspnm = 0;
        Integer rowIdDspnd = 0;
        TDS dsOrder = this.getDS(ODI_ORDERTABLENAME);
        String buffOrder = dsOrder.isFilter()?dsOrder.FILTER:dsOrder.PRIMARY;
        int newRowOrder[] = dsOrder.getNewRows(buffOrder);
        for(int temp:newRowOrder){
            if(!dsOrder.isActive(temp,buffOrder))
                continue;
            dsOrder.setItem(temp,"ORDER_SEQ",rowIdOrder+=1,buffOrder);
        }
        TDS dsDspnm = this.getDS(ODI_DSPNMTABLENAME);
        String buffDspnm = dsDspnm.isFilter()?dsDspnm.FILTER:dsDspnm.PRIMARY;
        int newRowDspnm[] = dsDspnm.getNewRows(buffDspnm);
        for(int temp:newRowDspnm){
            if(!dsDspnm.isActive(temp,buffOrder))
                continue;
            dsDspnm.setItem(temp,"ORDER_SEQ",rowIdDspnm+=1,buffDspnm);
        }
        TDS dsDspnd = this.getDS(ODI_DSPNDTABLENAME);
        String buffDspnd = dsDspnd.isFilter()?dsDspnd.FILTER:dsDspnd.PRIMARY;
        int newRowDspnd[] = dsDspnd.getNewRows(buffDspnd);
        for(int temp:newRowDspnd){
            if(!dsDspnd.isActive(temp,buffOrder))
                continue;
            dsDspnd.setItem(temp,"ORDER_SEQ",rowIdDspnd+=1,buffDspnd);
        }
    }
    /**
     * 得到条码号
     * @ alias 处理检验项目(LAB)
     * @param order IOrder
     * @param operator IOperator
     * @param ododitto_one IODODitto_One
     * @return String
     */
    private boolean getLabNo() {
       boolean falg = true;
       Map labMap = new HashMap();
       Map risNoMap = new HashMap();
       TDS ds = this.getDS(ODI_ORDERTABLENAME);
       String buff = ds.isFilter()?ds.FILTER:ds.PRIMARY;
       //新增行数组ODI_ORDER
       int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
//       System.out.println("新增行数组个数" + newRowOrderId.length);
       String labNo = "";
       String risNo="";
       for(int i : newRowOrderId){
//           System.out.println("行:"+i);
           if(!ds.isActive(i,buff)){
//               System.out.println("不生效行:"+i);
               continue;
           }
           //从当前缓冲区拿到对应行数据
           TParm parm = ds.getRowParm(i,buff);
           if (!parm.getBoolean("SETMAIN_FLG")) {
               continue;
           }
           if(parm.getValue("CAT1_TYPE").equals("LIS")){
               String labMapKey = parm.getValue("DEV_CODE") + parm.getValue("OPTITEM_CODE") + parm.getValue("RPTTYPE_CODE");
               // modify by wangb 2016/6/13 一期临床批量开立相同采血医嘱不合码 START
               //如果有就给当前LIS医嘱赋值LAB_NO
				if (labMap.get(labMapKey) != null
						&& !StringUtils.equals("Y", parm
								.getValue("PHASE_I_CLINICAL_FLG"))) {
                   TParm actionParm = new TParm();
                   actionParm.setData("MED_APPLY_NO", labMap.get(labMapKey));
                   this.setItem(ds,i,actionParm,buff);
                   //给集合医嘱细项赋条码号
                   setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),parm.getValue("ORDERSET_GROUP_NO"),labMap.get(labMapKey).toString());
                   continue;
               }
               labNo = SystemTool.getInstance().getNo("ALL", "MED",
                "LABNO", "LABNO");
               
               // modify by wangb 一期临床批量开立相同采血医嘱不合码
               if (!StringUtils.equals("Y", parm.getValue("PHASE_I_CLINICAL_FLG"))) {
                   //放入新的LAB_NO
                   labMap.put(labMapKey,labNo);
               }
               TParm actionLab = new TParm();
               actionLab.setData("MED_APPLY_NO",labNo);
               this.setItem(ds,i,actionLab,buff);
               //给集合医嘱细项赋条码号
               setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),parm.getValue("ORDERSET_GROUP_NO"),labNo);
           }
           if(parm.getValue("CAT1_TYPE").equals("RIS")){
               String risMapKey = parm.getValue("ORDERSET_CODE") + parm.getValue("ORDERSET_GROUP_NO");
               //如果有就给当前LIS医嘱赋值LAB_NO
               if(risNoMap.get(risMapKey)!=null){
//                   TParm actionParm = new TParm();
//                   actionParm.setData("MED_APPLY_NO", labMap.get(risMapKey));
//                   this.setItem(ds,i,actionParm,buff);
//                   //给集合医嘱细项赋条码号
//                   setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),parm.getValue("ORDERSET_GROUP_NO"),labMap.get(risMapKey).toString());
                   continue;
               }
               risNo = SystemTool.getInstance().getNo("ALL", "MED",
                "LABNO", "LABNO");
               //放入新的LAB_NO
               risNoMap.put(risMapKey,risNo);
               TParm actionRis = new TParm();
               actionRis.setData("MED_APPLY_NO",risNo);
               this.setItem(ds,i,actionRis,buff);
               //给集合医嘱细项赋条码号
               setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),parm.getValue("ORDERSET_GROUP_NO"),risNo);
           }
       }
       return falg;
    }
    /**
     * 给集合医嘱细项赋值
     * @param orderSetCode String
     * @param groupNo String
     */
    public void setOrderSetListLabNo(String orderSetCode,String groupNo,String labNo){
       TDS ds = this.getDS(ODI_ORDERTABLENAME);
       String buff = ds.isFilter()?ds.FILTER:ds.PRIMARY;
       //新增行数组ODI_ORDER
       int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
       for(int i : newRowOrderId){
           if(!ds.isActive(i,buff)){
               continue;
           }
           //从当前缓冲区拿到对应行数据
           TParm parm = ds.getRowParm(i,buff);
//           System.out.println("parm+"+parm);
           String risMapKey = parm.getValue("ORDERSET_CODE") + parm.getValue("ORDERSET_GROUP_NO");
           //相同的集合医嘱赋值
           if(risMapKey.equals(orderSetCode+groupNo)){
               //主项排除
               if (parm.getBoolean("SETMAIN_FLG"))
                   continue;
               TParm actionLab = new TParm();
               actionLab.setData("MED_APPLY_NO",labNo);
              //System.out.println(""+i+"===="+labNo);
               this.setItem(ds,i,actionLab,buff);
           }
       }
    }
    /**
     * 设置项值
     * @param ds TDS
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setItem(TDS ds,int row,Object value){
        //如果是医生站
        if(odiFlg){
           //设置医生站值
           setOdiStationItem(ds,row,value);
        }
        return true;
    }
    /**
    * 设置项值(本大对象调用)
    * @param ds TDS
    * @param row int
    * @param column String
    * @param value Object
    * @return boolean
    */
   public boolean setItem(TDS ds,int row,Object value,String buff){
       //如果是医生站
       if(odiFlg){
          //设置医生站值
          setOdiStationItem(ds,row,value,buff);
       }
       return true;
   }

    /**
     * 插入行
     * @param odiOrderDs TDS
     * @param row int
     * @return int
     */
    public TParm insertRow(TDS odiOrderDs,TParm order,String buff) {
        TParm result = new TParm();
        //如果是医生站
        if(odiFlg){
           //插入医生站医嘱行
           result = this.setOdiStationInserRow(odiOrderDs,order,buff);
        }
        return result;
    }
    /**
     * 插入医生站行
     * @param ds TDS
     * @param row int
     */
    public TParm setOdiStationInserRow(TDS ds,TParm order,String buff){
        TParm result = new TParm();
        boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
        String columnArr[] = ds.getColumns();
        //护士审核为N并且是医生站
        if (!nsCheckFlg && odiFlg) {
            //观察者联动
            int newRow = ds.insertRow();
            this.getDS(ODI_ORDERTABLENAME).setItem(newRow,"RX_KIND",this.getAttribute("RX_KIND"));
            this.setItem(ds,newRow,order,buff);
            //设置行可插入
            ds.setActive(newRow,true);
        //护士审核为Y并且是医生站
        }else {
           int newRow = ds.insertRow();
           for (String temp : columnArr) {
               if (order.getData(temp) == null) {
                   continue;
               }
               //设置相同列的值
               ds.setItem(newRow, temp, order.getData(temp));
           }
           //设置行可插入
           ds.setActive(newRow,true);
        }
        return result;
    }
    /**
     * 删除行
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds,int row){
        if(odiFlg){
            TParm parm = ds.getRowParm(row);
            insertOrderHisTory(parm);
            ds.deleteRow(row);
        }
        return true;
    }
    /**
     * 插入历史表
     * @param parm TParm
     */
    public void insertOrderHisTory(TParm parm){
        if(parm.getValue("ORDER_NO")==null||parm.getValue("ORDER_NO").length()==0||parm.getValue("ORDER_NO").equals("999999999999"))
            return;
        //System.out.println("parm=="+parm);
        TDS ds = this.getDS(ODI_ORDER_HISTORYTABLENAME);
        String columnArr[] = ds.getColumns();
        int newRow = ds.insertRow();
        for (String temp : columnArr) {
            //System.out.println(""+parm.getData(temp));
            if (parm.getData(temp) == null) {
                continue;
            }
            //设置相同列的值
            ds.setItem(newRow, temp, parm.getData(temp));
        }
        ds.setItem(newRow, "DC_DATE", StringTool.getString(TJDODBTool.getInstance().
                getDBTime(), "yyyyMMddHHmmss"));
        //设置行可插入
        ds.setActive(newRow, true);
    }
    /**
     * 删除行
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds,int row,String buff){
        if(odiFlg){
            ds.deleteRow(row);
        }
        return true;
    }
    /**
     * 设置医生站值
     * @param ds TDS
     * @param row int
     * @param columnStrArr String
     * @param value Object
     */
    private void setOdiStationItem(TDS ds,int row,Object value){
         boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
         String columnArr[] = ds.getColumns();
         //护士审核为N并且是医生站
         if (!nsCheckFlg && odiFlg) {
             if (value instanceof TParm) {
                 TParm parm = (TParm)value;
                 //设置相同列的值
                 ds.setAttribute("CHANGE_FLG",true);
                 ds.setItem(row,"",parm);
             }
         //护士审核为Y并且是医生站
         }else {
             if (value instanceof TParm) {
                 TParm parm = (TParm)value;
                 for (String temp : columnArr) {
                     if(parm.getData(temp)==null){
                         continue;
                     }
                     //设置相同列的值
                     Object obj = parm.getData("RX_FLG");
                     if(obj!=null){
                         if("Y".equals(obj)){
                             if("RX_KIND".equals(temp)){
                                 continue;
                             }
                         }
                     }
//                     System.out.println("temp="+temp+"parm.getData(temp)=="+parm.getData(temp));
                     ds.setItem(row, temp, parm.getData(temp));
                 }
             }
         }
    }
    /**
     * 设置医生站值(本大对象调用)
     * @param ds TDS
     * @param row int
     * @param columnStrArr String
     * @param value Object
     */
    private void setOdiStationItem(TDS ds,int row,Object value,String buff){
         boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
         String columnArr[] = ds.getColumns();
         //护士审核为N并且是医生站
         if (!nsCheckFlg && odiFlg) {
             if (value instanceof TParm) {
                 TParm parm = (TParm)value;
                 //设置相同列的值
                 ds.setAttribute("CHANGE_FLG",true);
                 ds.setItem(row,"",parm);
             }
         //护士审核为Y并且是医生站
         }else {
             if (value instanceof TParm) {
                 TParm parm = (TParm)value;
                 for (String temp : columnArr) {
                     if(parm.getData(temp)==null){
                         continue;
                     }
                     //设置相同列的值
                     Object obj = parm.getData("RX_FLG");
                     if(obj!=null){
                         if("Y".equals(obj)){
                             if("RX_KIND".equals(temp)){
                                 continue;
                             }
                         }
                     }
//                     System.out.println("row:"+row+"temp:"+temp+":"+parm.getData(temp));
                     ds.setItem(row, temp, parm.getData(temp),buff);
                 }
             }
         }
    }
    /**
     * 得到住院参数
     * @param key String
     * @return Object
     */
    public Object getOdiSysParmData(String key){
        return OdiMainTool.getInstance().getOdiSysParmData(key);
    }
    /**
     * 得到门诊参数
     * @param key String
     * @return Object
     */
    public Object getOpdSysParmData(String key){
        return OdiMainTool.getInstance().getOpdSysParmData(key);
    }
    /**
     * 得到医生站注记
     * @return boolean
     */
    public boolean isOdiFlg() {
        return odiFlg;
    }

    /**
     * 设置医生站注记
     * @param odiFlg boolean
     */
    public void setOdiFlg(boolean odiFlg) {
        this.odiFlg = odiFlg;
    }
    /**
     * 添加中药饮片医嘱
     * @param tab TTable
     * @param rxKind String
     * @return int
     */
    public int addChnRow(TDataStore tab,String rxKind){
        return 0;
    }
    /**
     * 过滤
     * @return boolean
     */
    public boolean filter(TTable table,TDS ds,boolean stopFalg){
        boolean falg = true;
        if(!ds.filter())
            falg = false;
        int rowPrimaryCount = ds.rowCount();
//        System.out.println("主分区行"+rowPrimaryCount);
        //先删除未使用行
        for(int i=rowPrimaryCount-1;i>=0;i--){
            if(!ds.isActive(i)){
//                System.out.println("i====="+i);
                ds.deleteRow(i);
            }
        }
        //停止划价注记
        if(stopFalg){
            falg = false;
            return falg;
        }
        TParm parm = ds.getBuffer(ds.PRIMARY);
//        System.out.println("主分区数据:"+parm);
//        table.setParmValue(this.getIGTableParm(parm));
        TParm action = this.getIGTableParm(parm,ds);
//        System.out.println("TABLE行:"+action);
        table.setParmValue(action);
        return falg;
    }
    /**
     * 得到住院中医医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm getIGTableParm(TParm parm,TDS ds){
        TParm result = new TParm();
//        System.out.println("得到住院中医医嘱"+parm);
        int rowCount = parm.getCount()==-1?0:parm.getCount();
//        System.out.println("行数:"+rowCount);
        //ORDER_DESC_1;MEDI_QTY_1;DCTAGENT_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTAGENT_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTAGENT_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTAGENT_CODE_4
        int row = rowCount%4==0?rowCount/4:Math.abs((rowCount+4)/4);
//        System.out.println("TABLE行数:"+row);
        int k=0;
        for(int i=0;i<row;i++){
           for(int j=1;j<5;j++){
               if(k<rowCount){
                   result.addData("ORDER_DESC_"+j,parm.getData("ORDER_DESC",k));
                   result.addData("MEDI_QTY_"+j,parm.getData("MEDI_QTY",k));
                   result.addData("DCTEXCEP_CODE_"+j,parm.getData("DCTEXCEP_CODE",k));
                   result.addData("ROW_ID_"+j,parm.getData("#ID#",k));
               }else{
                   int insertRow = ds.insertRow();
                   ds.setItem(insertRow, "RX_KIND","IG");
                   ds.setActive(insertRow,false);
                   result.addData("ORDER_DESC_"+j,"");
                   result.addData("MEDI_QTY_"+j,0);
                   result.addData("DCTEXCEP_CODE_"+j,"");
                   result.addData("ROW_ID_"+j,ds.getItemData(insertRow,"#ID#"));
               }
               k++;
           }
        }
        //补齐行数
        int addRowCount = rowCount%4;
        //判断需要添加的行数
        if(addRowCount==0){
            for(int i=1;i<5;i++){
                int insertRow = ds.insertRow();
                ds.setItem(insertRow, "RX_KIND","IG");
                ds.setActive(insertRow,false);
                result.addData("ORDER_DESC_"+i,"");
                result.addData("MEDI_QTY_"+i,0);
                result.addData("DCTEXCEP_CODE_"+i,"");
                result.addData("ROW_ID_"+i,ds.getItemData(insertRow,"#ID#"));
            }
            row+=1;
        }
        result.setData("ACTION","COUNT",row);
        return result;
    }
}
