package com.javahis.ui.sys;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.control.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.sid.IdCardO;
import jdo.sys.SystemTool;

import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 病案自动查重Control</p>
 *
 * <p>Description: 病案自动查重Control</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class SYSAutoCheckDuplicateControl
    extends TControl {
    public SYSAutoCheckDuplicateControl() {
    }

    /** 按身份证号查重 */
    private TCheckBox IDNO_FLG;
    /** 按姓名查重 */
    private TCheckBox PAT_NAME_FLG;
    /** 已处理 */
    private TCheckBox HANDLE_FLG;
    /** 身份证号 */
    private TTextField IDNO;
    /** 姓名 */
    private TTextField PAT_NAME;
    /** MASTER_TABLE */
    private TTable MASTER_TABLE;
    /** DETAIL_TABLE */
    private TTable DETAIL_TABLE;
    /** 声明日期格式化类对象 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    
    private boolean closeFlg = false;
    
    /**
     * 初始化方法
     * 初始化查询全部
     */
    public void onInit() {
        super.onInit();
        // 获取全部输入框控件
        getAllComponent();
        // 注册监听事件
        initControler();

        Object obj = getParameter();
        TParm t;
        if (obj != null) {
            t = (TParm) obj;
            this.setValue("IDNO", t.getValue("IDNO"));
            this.setValue("PAT_NAME", t.getValue("PAT_NAME"));
            IDNO_FLG.setValue("Y");
            PAT_NAME_FLG.setValue("Y");
            // 自动查重
            queryMaster();
            
            closeFlg = true;
            
            if(MASTER_TABLE.getParmValue().getCount("IDNO") > 0){
            	MASTER_TABLE.setSelectedRow(0);
            	onTableClicked(0);
            	TParm delParm = DETAIL_TABLE.getParmValue();
            	boolean flg = true;
            	for (int i = 0; i < delParm.getCount("SEL_FLG"); i++) {
            		if(delParm.getBoolean("ME_FLG", i)){
            			flg = false;
            		}
					delParm.setData("SEL_FLG",i , "Y");
					
					if(i==0){
						
					}
				}
            	if(flg){
            		delParm.setData("ME_FLG", 0, "Y");
            	}

            	DETAIL_TABLE.setParmValue(delParm);
            }
            
        }else{
        	 // 自动查重
            queryMaster();
        }
        
       
    }

    /**
     * 获取全部输入框控件
     */
    public void getAllComponent() {
        this.IDNO_FLG = (TCheckBox)this.getComponent("IDNO_FLG");
        this.PAT_NAME_FLG = (TCheckBox)this.getComponent("PAT_NAME_FLG");
        this.HANDLE_FLG = (TCheckBox)this.getComponent("HANDLE_FLG");
        this.IDNO = (TTextField)this.getComponent("IDNO");
        this.PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
        this.MASTER_TABLE = (TTable)this.getComponent("MASTER_TABLE");
        this.DETAIL_TABLE = (TTable)this.getComponent("DETAIL_TABLE");
    }

    /**
     * 注册监听事件
     */
    public void initControler() {
        // MASTER_TABLE
        callFunction("UI|MASTER_TABLE|addEventListener",
                     "MASTER_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        
        
        
    }

    public void onTableClicked(int row) {
        TParm data = MASTER_TABLE.getParmValue();
        TParm parm = data.getRow(row);
        TParm result = queryDetail(parm);
        DETAIL_TABLE.setParmValue(result, DETAIL_TABLE.getParmMap());
    }
    
    public void onIdCardNo() {
    	
    	String dir = SystemTool.getInstance().Getdir();
		
		// add by yangjj 20150629
		System.out.println("身份证读卡机日志："+dir);
		
		CardInfoBO cardInfo = null;
		try {
			cardInfo = IdCardReaderUtil.getCardInfo(dir);
		} catch (Exception e) {
			this.messageBox("重新获取信息");
			System.out.println("重新获取信息:" + e.getMessage());
			// TODO: handle exception
		}
		// CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
		if (cardInfo == null) {
			this.messageBox("未获得身份证信息,请重新操作");
			return;
		}
		
	

		this.setValue("IDNO", cardInfo.getCode().trim());
    }
   

    /**
     * 自动查重
     */
    public void queryMaster() {
        StringBuilder sqlBuf = new StringBuilder();
        // 根据身份证号和姓名查重
        if ("Y".equals(IDNO_FLG.getValue()) && "Y".equals(PAT_NAME_FLG.getValue())) {
            sqlBuf.append(" SELECT IDNO, PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY IDNO, PAT_NAME HAVING COUNT(*) > 1");
        }
        // 根据姓名查重
        else if ("Y".equals(PAT_NAME_FLG.getValue())) {
            sqlBuf.append(" SELECT '' AS IDNO, PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY PAT_NAME HAVING COUNT(*) > 1");
        }
        // 根据身份证号查重（默认）
        else {
            sqlBuf.append(" SELECT IDNO, '' AS PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY IDNO HAVING COUNT(*) > 1");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.toString()));
        MASTER_TABLE.setParmValue(result, MASTER_TABLE.getParmMap());
    }

    /**
     * 字符串非空验证
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = String.valueOf(obj);
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }


    public TParm queryDetail(TParm parm) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append(" SELECT 'N' AS SEL_FLG, "+ 
        		"  CASE WHEN MERGE_TOMRNO IS NULL AND MERGE_FLG = 'Y' THEN 'Y' ELSE 'N' END ME_FLG," + 
        		" MR_NO,IPD_NO,IDNO,PAT_NAME,SEX_CODE,BIRTH_DATE," +
        		"ADDRESS,CELL_PHONE,HEIGHT,WEIGHT,BLOOD_TYPE,CTZ1_CODE,NATION_CODE,SPECIES_CODE," +
        		"FIRST_ADM_DATE,MERGE_TOMRNO,MERGE_FLG,HANDLE_FLG" +
        		",(SELECT COUNT(CASE_NO)  FROM REG_PATADM WHERE MR_NO=SYS_PATINFO.MR_NO ) REG_COUNT FROM SYS_PATINFO WHERE 1 = 1 ");
        // 根据身份证号和姓名查重
        if ("Y".equals(IDNO_FLG.getValue()) && "Y".equals(PAT_NAME_FLG.getValue())) {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            else {
                sqlBuf.append(" AND IDNO IS NULL");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
            else {
                sqlBuf.append(" AND PAT_NAME IS NULL ");
            }
        }
        // 根据姓名查重
        else if ("Y".equals(PAT_NAME_FLG.getValue())) {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
            else {
                sqlBuf.append(" AND PAT_NAME IS NULL ");
            }
        }
        // 根据身份证号查重（默认）
        else {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            else {
                sqlBuf.append(" AND IDNO IS NULL");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
        }
        sqlBuf.append(" ORDER BY MR_NO");
        System.out.println(sqlBuf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
                toString()));
        return result;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.IDNO_FLG.setValue("Y");
        this.PAT_NAME_FLG.setValue("Y");
        this.HANDLE_FLG.setValue("N");
        this.IDNO.setValue("");
        this.PAT_NAME.setValue("");
        closeFlg = false;
    }

    /**
     * 自动查重
     */
    public void onQuery() {
        queryMaster();
    }

    /**
     * 合并
     */
    public void onSave() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要合并的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm parm = new TParm();
        int count = data.getCount("SEL_FLG");
        int j = 0;
        String MERGE_TOMRNO = "";
        int meFlg = 0;
        for (int i = 0;i<count;i++) {
        	if ("Y".equals(data.getValue("SEL_FLG", i)) && "Y".equals(data.getValue("ME_FLG", i))) {
        		MERGE_TOMRNO = data.getValue("MR_NO", i);
        		meFlg++;
            }
         }
        
        
        if(MERGE_TOMRNO.length() == 0){
        	this.messageBox("请选择一个病案号作为合并的主病案号!");
        	return;
        }
        
        if(meFlg > 1){
        	this.messageBox("只能选择一个病案号作为主病案号！！！");
        	return;
        }
        
        for (int i = 0;i<count;i++) {
        	if ("Y".equals(data.getValue("SEL_FLG", i))) {
        		parm.addData("MR_NO", data.getValue("MR_NO", i));
            	parm.addData("MERGE_FLG", "Y");
            	
            	if("Y".equals(data.getValue("ME_FLG", i))){
            		parm.addData("MERGE_TOMRNO", "");
            	}else{
            		parm.addData("MERGE_TOMRNO", MERGE_TOMRNO);
            	}
            	
//                if(j <= 0) {
//                	parm.addData("MERGE_TOMRNO", data.getValue("MERGE_TOMRNO", i));
//                	MERGE_TOMRNO = parm.getValue("MR_NO", j);
//                } else {
//                	parm.addData("MERGE_TOMRNO", MERGE_TOMRNO);
//                }
//                j++;
            }
        }
//        this.messageBox(parm.toString());
        if (parm.getCount("MR_NO") < 2) {
            this.messageBox("请选择要合并的病案！");
            return;
        }
        
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
        	String mrNo = parm.getValue("MR_NO", i);
        	String srcMrNo = parm.getValue("MERGE_TOMRNO", i);
        	
        	
        	if(getAdmInpCount(mrNo) > 0){
    			this.messageBox(mrNo+"为住院患者，不应合并病案号");
    			callFunction("UI|save|setEnabled", false);
    			break;		
    		}else if(getAdmInpCount(srcMrNo) > 0){
    			this.messageBox(srcMrNo+"为住院患者，不应合并病案号");
    			callFunction("UI|save|setEnabled", false);
    			break;	
    		}
    		
//    		if(getOpdOrderCount(mrNo) > 0){
//    			this.messageBox("门急诊患者"+mrNo+"有未缴费医嘱项目不允许合并");
//    			callFunction("UI|save|setEnabled", false);
//    			break;		
//    		}else if(getOpdOrderCount(srcMrNo) > 0){
//    			this.messageBox("门急诊患者"+srcMrNo+"有未缴费医嘱项目不允许合并");
//    			callFunction("UI|save|setEnabled", false);
//    			break;	
//    		}
			
		}
        
        
        if (this.messageBox("询问", "是否合并病案？", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSAutoCheckDuplicateAction", "update", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("合并失败！");
        }
        else {
            this.messageBox("合并成功！");

            onTableClicked(MASTER_TABLE.getSelectedRow());
//            System.out.println("selRow---"+MASTER_TABLE.getSelectedRow());
            if(closeFlg){
            	onTableClicked(0);
            	TParm closeParm = new TParm();
            	DETAIL_TABLE.acceptText();
            	TParm dTable = DETAIL_TABLE.getShowParmValue();
//            	System.out.println("dTable---"+dTable);
            	for (int i = 0; i < dTable.getCount("ME_FLG"); i++) {
//            		System.out.println(i+"---"+dTable.getValue("ME_FLG", i));
					if("Y".equals(dTable.getValue("ME_FLG", i))){
						closeParm.setData("MR_NO", dTable.getValue("MR_NO", i));
//						break;
					}
				}
//            	System.out.println("closeParm---"+closeParm);
            	this.setReturnValue(closeParm);
            	this.closeWindow(); 
            	
            }
            
        }
    }
     
    /**
     * 处理
     */
    public void onHandle() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要处理的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm realParm = new TParm();
        int count = data.getCount("SEL_FLG");
        StringBuilder buf = new StringBuilder();
        for (int i = 0;i<count;i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                realParm.addData("MR_NO", data.getValue("MR_NO", i));
                buf.append(data.getValue("MR_NO", i) + ",");
            }
        }
        if (realParm.getCount("MR_NO") < 1) {
            this.messageBox("请选择要处理的病案！");
            return;
        }
        if (this.messageBox("询问", "是否处理？", 2) != 0) {
            return;
        }
        buf.append("-1");
        String sql = " UPDATE SYS_PATINFO SET "
        	+ " HANDLE_FLG = 'Y' "
            + " WHERE MR_NO IN ("
            + buf.toString() + ")";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("处理失败！");
        }
        else {
            this.messageBox("处理成功！");
            onTableClicked(MASTER_TABLE.getSelectedRow());
        }
    }

    /**
     * 撤销
     */
    public void onUndo() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要撤销合并及处理的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm realParm = new TParm();
        int count = data.getCount("SEL_FLG");
        StringBuilder buf = new StringBuilder();
        for (int i = 0;i<count;i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                realParm.addData("MR_NO", data.getValue("MR_NO", i));
                buf.append(data.getValue("MR_NO", i) + ",");
            }
        }
        if (realParm.getCount("MR_NO") < 1) {
            this.messageBox("请选择要撤销合并及处理的病案！");
            return;
        }
        if (this.messageBox("询问", "是否撤销合并及处理？", 2) != 0) {
            return;
        }
        buf.append("-1");
        String sql = " UPDATE SYS_PATINFO SET "
        	+ " HANDLE_FLG = 'N', "
        	+ " MERGE_FLG = 'N', "
            + " MERGE_TOMRNO = '' "
            + " WHERE MR_NO IN ("
            + buf.toString() + ")";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("撤销失败！");
        }
        else {
            this.messageBox("撤销成功！");
            onTableClicked(MASTER_TABLE.getSelectedRow());
        }
    }
    
    
    public int getAdmInpCount(String mrNo){
		String sql = "SELECT COUNT(CASE_NO) COUNT FROM ADM_INP WHERE MR_NO='"+mrNo+"' AND DS_DATE IS NULL";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getInt("COUNT",0);
	}
	
	public int getOpdOrderCount(String mrNo){
		String sql = "SELECT COUNT(CASE_NO) COUNT FROM OPD_ORDER WHERE MR_NO='"+mrNo+"' AND (BILL_FLG ='N' OR BILL_FLG IS NULL)";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getInt("COUNT",0);
		
	}
    

}
