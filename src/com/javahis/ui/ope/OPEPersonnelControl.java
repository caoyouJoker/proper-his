package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboOPERoom;
import com.javahis.util.StringUtil;

import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: 手术人员安排</p>
 *
 * <p>Description: 手术人员安排</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 4.0
 */
public class OPEPersonnelControl
    extends TControl {
    private String OPBOOK_SEQ = "";//手术单号
    private String STATE = "";//状态
    private TTable EXTRA_TABLE;//体外循环师表格
    private TTable CIRCULE_TABLE;//循环护士表格
    private TTable SCRUB_TABLE;//洗手护士表格
    private TTable ANA_TABLE;//麻醉医师表格
    //ICU手术标记
    private boolean OpeCisFlg=false;
    TParm CISHl7Parm=new TParm();
    List<TParm> lst = new ArrayList<TParm>();//批量排程时，将对象存入list
    public void onInit(){
        super.onInit();
        EXTRA_TABLE = (TTable)this.getComponent("EXTRA_TABLE");
        CIRCULE_TABLE = (TTable)this.getComponent("CIRCULE_TABLE");
        SCRUB_TABLE = (TTable)this.getComponent("SCRUB_TABLE");
        ANA_TABLE = (TTable)this.getComponent("ANA_TABLE");
        ANA_TABLE.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onANATableMainCharge");
        Object obj = this.getParameter();
        if(obj instanceof String){
            OPBOOK_SEQ = (String)obj;
        }else{
            return;
        }

        //批量排程入参判断,带有“,”的说明是批量排程 start  by  machao 20170228
        if(OPBOOK_SEQ.indexOf(",")>0){
        	//说明批量排程	
        	
        	String[] opbookSeqs = OPBOOK_SEQ.substring(0,OPBOOK_SEQ.length()-1).split(",");

        	for(String s :opbookSeqs){
        		TParm parm = new TParm();
        		//this.messageBox(s);
        		parm.setData("OPBOOK_SEQ",s);
        		TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
  	            if(result.getErrCode()<0 || result.getCount()<=0){ 
		              this.messageBox_("批量排程时，查有无手术申请信息:"+s);
		              this.callFunction("UI|onClose");
		              return;
	            }
        		//System.out.println("maa:"+result);
        		lst.add(result);
        	}
        	
       // end machao 20170228 	
        }else{
            //查询 手术申请信息,单个排程
	          TParm parm = new TParm();
	          parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
	          TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
	          OpeCisFlg=SYSBedTool.getInstance().checkIsICU(result.getValue("CASE_NO", 0));
	          if(result.getErrCode()<0){ 
	              this.messageBox_("查无手术申请信息");
	              return;
	          }
	          dataBind(result);
        }

    }
    /**
     * 页面数据绑定
     * @param parm TParm
     */
    private void dataBind(TParm parm){
        this.setValue("MR_NO",parm.getValue("MR_NO",0));
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO",0));
        this.setValue("PAT_NAME",pat.getName());
        this.setValue("OP_DATE",parm.getTimestamp("OP_DATE",0));
        this.setValue("ROOM_NO",parm.getValue("ROOM_NO",0));
        this.setValue("TIME_NEED",parm.getValue("TIME_NEED",0));
        if(parm.getValue("STATE",0).equals("1")){
            this.setValue("STATE","Y");
            STATE = "1";
        }
        //fux modify 20151029
        //准备齐全
        if(parm.getValue("READY_FLG",0).equals("Y")){
            this.setValue("READY_FLG","Y");
        }
        //检查效期
        if(parm.getValue("VALID_DATE_FLG",0).equals("Y")){
            this.setValue("VALID_DATE_FLG","Y");
        }
        //确定植入物规格型号
        if(parm.getValue("SPECIFICATION_FLG",0).equals("Y")){
            this.setValue("SPECIFICATION_FLG","Y");
        }
        
        //手术ICD 换中文
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("select * from SYS_OPERATIONICD");
        dataStore.retrieve();
        String s = parm.getValue("OP_CODE1",0);
        String desc = "";
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm fill = dataStore.getBuffer(bufferString);
        Vector v = (Vector) fill.getData("OPERATION_ICD");
        Vector d = (Vector) fill.getData("OPT_CHN_DESC");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(v.get(i)))
                desc = "" + d.get(i);
        }
        this.setValue("OP_CODE1",desc);
        int row;
        //体外循环师
        if(parm.getValue("EXTRA_USER1",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,parm.getValue("EXTRA_USER1",0));
        }
        if(parm.getValue("EXTRA_USER2",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,parm.getValue("EXTRA_USER2",0));
        }
        //循环护士
        if(parm.getValue("CIRCULE_USER1",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,parm.getValue("CIRCULE_USER1",0));
        }
        if(parm.getValue("CIRCULE_USER2",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,parm.getValue("CIRCULE_USER2",0));
        }
        if(parm.getValue("CIRCULE_USER3",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,parm.getValue("CIRCULE_USER3",0));
        }
        if(parm.getValue("CIRCULE_USER4",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,parm.getValue("CIRCULE_USER4",0));
        }
        //洗手护士
        if(parm.getValue("SCRUB_USER1",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,parm.getValue("SCRUB_USER1",0));
        }
        if(parm.getValue("SCRUB_USER2",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,parm.getValue("SCRUB_USER2",0));
        }
        if(parm.getValue("SCRUB_USER3",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,parm.getValue("SCRUB_USER3",0));
        }
        if(parm.getValue("SCRUB_USER4",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,parm.getValue("SCRUB_USER4",0));
        }
        //麻醉医师
        if(parm.getValue("ANA_USER1",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"Y");
            ANA_TABLE.setItem(row,1,parm.getValue("ANA_USER1",0));
        }
        if(parm.getValue("ANA_USER2",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"N");
            ANA_TABLE.setItem(row,1,parm.getValue("ANA_USER2",0));
        }
    }
    
    public TParm writeParm(TParm tparm){
    	TParm parm = new TParm();
		parm.setData("OP_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));
	    parm.setData("ROOM_NO",this.getValue("ROOM_NO"));       	    
        //流动护士
        TParm CIRCULE = this.getCIRCULEData();
        parm.setData("CIRCULE_USER1",CIRCULE.getValue("CIRCULE_USER1"));
        parm.setData("CIRCULE_USER2",CIRCULE.getValue("CIRCULE_USER2"));
        parm.setData("CIRCULE_USER3",CIRCULE.getValue("CIRCULE_USER3"));
        parm.setData("CIRCULE_USER4",CIRCULE.getValue("CIRCULE_USER4"));
        //刷手护士
        TParm SCRUB = this.getSCRUBData();
        parm.setData("SCRUB_USER1",SCRUB.getValue("SCRUB_USER1"));
        parm.setData("SCRUB_USER2",SCRUB.getValue("SCRUB_USER2"));
        parm.setData("SCRUB_USER3",SCRUB.getValue("SCRUB_USER3"));
        parm.setData("SCRUB_USER4",SCRUB.getValue("SCRUB_USER4"));
        //麻醉医师
        TParm ANA = this.getANAData();
        parm.setData("ANA_USER1",ANA.getValue("ANA_USER1"));
        parm.setData("ANA_USER2",ANA.getValue("ANA_USER2"));
        //体外循环师
        TParm EXTRA = this.getEXTRAData();
        parm.setData("EXTRA_USER1",EXTRA.getValue("EXTRA_USER1"));
        parm.setData("EXTRA_USER2",EXTRA.getValue("EXTRA_USER2"));
        
        //判断状态 是否完成排程
        if(this.getValueString("STATE").equals("Y")){
            parm.setData("STATE","1");
            parm.setData("APROVE_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss"));
            parm.setData("APROVE_USER",Operator.getID()); 
            //CISICU数据收集
            CISHl7Parm.addData("OPBOOK_SEQ", OPBOOK_SEQ);  
            CISHl7Parm.addData("STATE", "1");
        }else{
            parm.setData("STATE","0");    
            parm.setData("APROVE_DATE","");
            parm.setData("APROVE_USER","");
        }
        //fux modify 20151029
        //准备齐全
        if(this.getValueString("READY_FLG").equals("Y")){
        	parm.setData("READY_FLG","Y");
        }else{
        	parm.setData("READY_FLG","N");
        }
        //检查效期  
        if(this.getValueString("VALID_DATE_FLG").equals("Y")){
        	parm.setData("VALID_DATE_FLG","Y");
        }
        else{
        	parm.setData("VALID_DATE_FLG","N");
        }
        //确定植入物规格型号
        if(this.getValueString("SPECIFICATION_FLG").equals("Y")){
        	parm.setData("SPECIFICATION_FLG","Y");
        }
        else{
        	parm.setData("SPECIFICATION_FLG","N");  
        }
        parm.setData("OPT_USER",Operator.getID());  
        parm.setData("OPT_TERM",Operator.getIP());   
        parm.setData("OPBOOK_SEQ",(String)tparm.getData("OPBOOK_SEQ",0)+"");
//        this.messageBox(tparm.getData("OPBOOK_SEQ",0)+"");
		return parm;
    	
    }
    /**
     * 保存
     */
    public void onSave(){
        if(!this.checkData()){
            return;
        }
        //this.messageBox(OPBOOK_SEQ);
        if(OPBOOK_SEQ.indexOf(",")>0){
        	//批量保存 start  by  machao 20170228
        	for(int i = 0;i<lst.size();i++){
        		TParm r = lst.get(i);
        		TParm parm = new TParm();
        		parm = this.writeParm(r);
        		
                TParm result = OPEOpBookTool.getInstance().updateOpBookForPersonnel(parm);
                //OPBOOK_SEQ 应该也加入 ope_check表中
                parm.setData("MR_NO",r.getData("MR_NO",0));
                parm.setData("PAT_NAME",r.getData("PAT_NAME",0));
                parm.setData("APROVE_USER","");    
                
                if(result.getErrCode()<0 ){
                    this.messageBox("E0005");
                    return;
                }     
                
                //生成电视屏XML wanglong add 20141010
                String CASE_NO =
                        StringUtil.getDesc("OPE_OPBOOK", "CASE_NO", "OPBOOK_SEQ='" + r.getData("OPBOOK_SEQ",0)+"" + "'");
                TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(CASE_NO, r.getData("OPBOOK_SEQ",0)+"");
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
                }
                if(OpeCisFlg && CISHl7Parm.getCount("OPBOOK_SEQ")>0){
                	sendHl7Mes();
                }
                sendHL7Mes(parm,"OPE_SCHEDULE");
        	}
        	this.messageBox("P0005"); 
        	return;
        	//end  machao 20170228
        }

        //汇总数据
        TParm parm = new TParm();
        parm.setData("OP_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));
        parm.setData("ROOM_NO",this.getValue("ROOM_NO"));
        //流动护士
        TParm CIRCULE = this.getCIRCULEData();
        parm.setData("CIRCULE_USER1",CIRCULE.getValue("CIRCULE_USER1"));
        parm.setData("CIRCULE_USER2",CIRCULE.getValue("CIRCULE_USER2"));
        parm.setData("CIRCULE_USER3",CIRCULE.getValue("CIRCULE_USER3"));
        parm.setData("CIRCULE_USER4",CIRCULE.getValue("CIRCULE_USER4"));
        //刷手护士
        TParm SCRUB = this.getSCRUBData();
        parm.setData("SCRUB_USER1",SCRUB.getValue("SCRUB_USER1"));
        parm.setData("SCRUB_USER2",SCRUB.getValue("SCRUB_USER2"));
        parm.setData("SCRUB_USER3",SCRUB.getValue("SCRUB_USER3"));
        parm.setData("SCRUB_USER4",SCRUB.getValue("SCRUB_USER4"));
        //麻醉医师
        TParm ANA = this.getANAData();
        parm.setData("ANA_USER1",ANA.getValue("ANA_USER1"));
        parm.setData("ANA_USER2",ANA.getValue("ANA_USER2"));
        //体外循环师
        TParm EXTRA = this.getEXTRAData();
        parm.setData("EXTRA_USER1",EXTRA.getValue("EXTRA_USER1"));
        parm.setData("EXTRA_USER2",EXTRA.getValue("EXTRA_USER2"));
        //判断状态 是否完成排程
        if(this.getValueString("STATE").equals("Y")){
            parm.setData("STATE","1");
            parm.setData("APROVE_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss"));
            parm.setData("APROVE_USER",Operator.getID()); 
            //CISICU数据收集
            CISHl7Parm.addData("OPBOOK_SEQ", OPBOOK_SEQ);  
            CISHl7Parm.addData("STATE", "1");
        }else{
            parm.setData("STATE","0");    
            parm.setData("APROVE_DATE","");
            parm.setData("APROVE_USER","");
        }
        //fux modify 20151029
        //准备齐全
        if(this.getValueString("READY_FLG").equals("Y")){
        	parm.setData("READY_FLG","Y");
        }else{
        	parm.setData("READY_FLG","N");
        }
        //检查效期  
        if(this.getValueString("VALID_DATE_FLG").equals("Y")){
        	parm.setData("VALID_DATE_FLG","Y");
        }
        else{
        	parm.setData("VALID_DATE_FLG","N");
        }
        //确定植入物规格型号
        if(this.getValueString("SPECIFICATION_FLG").equals("Y")){
        	parm.setData("SPECIFICATION_FLG","Y");
        }
        else{
        	parm.setData("SPECIFICATION_FLG","N");  
        }
        parm.setData("OPT_USER",Operator.getID());  
        parm.setData("OPT_TERM",Operator.getIP());   
        parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        TParm result = OPEOpBookTool.getInstance().updateOpBookForPersonnel(parm);
        //OPBOOK_SEQ 应该也加入 ope_check表中
        parm.setData("MR_NO",this.getValueString("MR_NO"));
        parm.setData("PAT_NAME",this.getValueString("PAT_NAME"));
        parm.setData("APROVE_USER","");  

        if(result.getErrCode()<0 ){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
        
        
        
        //生成电视屏XML wanglong add 20141010
        String CASE_NO =
                StringUtil.getDesc("OPE_OPBOOK", "CASE_NO", "OPBOOK_SEQ='" + OPBOOK_SEQ + "'");
        TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(CASE_NO, OPBOOK_SEQ);
        if (xmlParm.getErrCode() < 0) {
            this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
        }
        if(OpeCisFlg && CISHl7Parm.getCount("OPBOOK_SEQ")>0){
        	sendHl7Mes();
        }
        sendHL7Mes(parm,"OPE_SCHEDULE");
    }
    /**
     * ICU病人手术排程完毕后发送HL7消息
     */
    private void sendHl7Mes(){
    	// 调用CISHl7接口
    	    int count=CISHl7Parm.getCount("OPBOOK_SEQ");
			List list = new ArrayList();
			for (int i = 0; i < count; i++) {
				String sql = " SELECT * FROM OPE_OPBOOK WHERE OPBOOK_SEQ ='"
						+ CISHl7Parm.getValue("OPBOOK_SEQ", i)+ "'";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				TParm parm = new TParm();
				parm.addData("ADM_TYPE", result.getValue("ADM_TYPE", 0));
				parm.addData("FLG", 0);
				parm.addData("CASE_NO", result.getValue("CASE_NO", 0));
				parm.addData("MR_NO", result.getValue("MR_NO", 0));
				parm.addData("ORDER_DR_CODE", result.getValue("BOOK_DR_CODE", 0));
				parm.addData("OPBOOK_SEQ", result.getValue("OPBOOK_SEQ", 0));
				list.add(parm);
			}
			TParm CISparm = Hl7Communications.getInstance().Hl7MessageCIS(list,
					"OPE");
			if (CISparm.getErrCode() < 0) {
				this.messageBox(CISparm.getErrText());
			} 
			//清空parm
			while (CISHl7Parm.getCount("OPBOOK_SEQ") > 0) {
				CISHl7Parm.removeRow(0);
			}
    }
    //$$==========zhangs =============$$//
    /**
     * hl7接口
     * @param parm TParm
     * @param type String
     */
	private void sendHL7Mes(TParm parm, String type) {
		try {
			// 手术排程
			if (type.equals("OPE_SCHEDULE")) {
				List list = new ArrayList();
				String sql = " SELECT * FROM OPE_OPBOOK WHERE OPBOOK_SEQ ='"
						+ parm.getValue("OPBOOK_SEQ") + "'";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				result.setData("ROOM_NAME",((TComboOPERoom)this.getComponent("ROOM_NO")).getSelectedName());
				list.add(result);
				TParm resultParm = Hl7Communications.getInstance()
						.Hl7MessageOPE(list, type);
				if (resultParm.getErrCode() < 0)
					this.messageBox(resultParm.getErrText());
				
				// add by wangb 2017/09/25 介入手术排程完毕向RIS发送消息 START
				if ("1".equals(result.getValue("STATE", 0))
						&& "2".equals(result.getValue("TYPE_CODE", 0))) {
					resultParm = Hl7Communications.getInstance()
							.Hl7MessageOPERis(result.getRow(0));
					if (resultParm.getErrCode() < 0) {
						this.messageBox("HL7消息发送失败：" + resultParm.getErrText());
					}
				}
				// add by wangb 2017/09/25 介入手术排程完毕向RIS发送消息 END
			}
		} catch (Exception ex3) {
			System.out.println(ex3.getMessage());
		}
	}
    public void onSendRe(){
    	if(OPBOOK_SEQ.indexOf(",")>0){
    		//批量重送  start  by  machao 20170228
    		for(int i = 0;i<lst.size();i++){
        		TParm r = lst.get(i);
        		TParm parm = new TParm();
        		parm.setData("OPBOOK_SEQ",r.getData("OPBOOK_SEQ",0)+"");
                sendHL7Mes(parm,"OPE_SCHEDULE");       		 
    		}
    		return;
    	}   	
         //汇总数据
         TParm parm = new TParm();
         parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
         sendHL7Mes(parm,"OPE_SCHEDULE");
    }
  //$$==========zhangs =============$$//
    /**
     * 检查保存数据
     * @return boolean
     */
    private boolean checkData(){
        if(STATE.equals("1")){
            int re = this.messageBox("提示","已经完成手术安排，确定要修改吗？",0);
            if(re==1)
                return false;
        }
        //手术时间
        if(this.getValue("OP_DATE")==null){
            this.messageBox_("请填写手术时间");
            return false;
        }
        //手术间
        if(this.getValueString("ROOM_NO").length()<=0){
            this.messageBox_("请选择手术间");
            return false;
        }
        boolean flg = false;
        //判断是否选了主麻醉医师
        for(int i=0;i<ANA_TABLE.getRowCount();i++){
            if (ANA_TABLE.getValueAt(i,0).toString().equals("Y"))
                flg = true;
        }
        if(!flg){
            this.messageBox_("请选择一位麻醉医师作为主麻醉医师");
            return false;
        }
        return true;
    }
    /**
     * 体外循环师 添加事件
     */
    public void onEXTRA_ADD(){
        String user_id = this.getValueString("EXTRA_USER");
        if(!checkGrid(EXTRA_TABLE,user_id,0))
            return;
        if(EXTRA_TABLE.getRowCount()>=2){
            this.messageBox_("只能制定两位体外循环师");
            return;
        }
        if(user_id.length()>0){
            int row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,user_id);
        }
    }
    /**
     * 体外循环师 删除事件
     */
    public void onEXTRA_DEL(){
        int row = EXTRA_TABLE.getSelectedRow();
        if(row>-1){
            EXTRA_TABLE.removeRow(row);
        }
    }
    /**
     * 巡回护士 添加事件
     */
    public void onCIRCULE_ADD(){
        String user_id = this.getValueString("CIRCULE_USER");
        if(!checkGrid(CIRCULE_TABLE,user_id,0))
            return;
        if(CIRCULE_TABLE.getRowCount()>=4){
            this.messageBox_("只能添加四位巡回护士");
            return;
        }
        if(user_id.length()>0){
            int row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,user_id);
        }
    }
    /**
     * 巡回护士 删除事件
     */
    public void onCIRCULE_DEL(){
        int row = CIRCULE_TABLE.getSelectedRow();
        if(row>-1){
            CIRCULE_TABLE.removeRow(row);
        }
    }
    /**
     * 洗手护士 添加事件
     */
    public void onSCRUB_ADD(){
        String user_id = this.getValueString("SCRUB_USER");
        if(!checkGrid(SCRUB_TABLE,user_id,0))
            return;
        if(SCRUB_TABLE.getRowCount()>=4){
            this.messageBox_("只能添加四位洗手护士");
            return;
        }
        if(user_id.length()>0){
            int row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,user_id);
        }
    }
    /**
     * 洗手护士 删除事件
     */
    public void onSCRUB_DEL(){
        int row = SCRUB_TABLE.getSelectedRow();
        if(row>-1){
            SCRUB_TABLE.removeRow(row);
        }
    }
    /**
     * 麻醉医师 添加事件
     */
    public void onANA_ADD(){
        String user_id = this.getValueString("ANA_USER");
        if(!checkGrid(ANA_TABLE,user_id,1))
            return;
        if(ANA_TABLE.getRowCount()>=2){
            this.messageBox_("只能添加两位麻醉医师");
            return;
        }
        if(user_id.length()>0){
            int row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,1,user_id);
            ANA_TABLE.setItem(row,0,"N");
        }
    }
    /**
     * 麻醉医师 删除事件
     */
    public void onANA_DEL(){
        int row = ANA_TABLE.getSelectedRow();
        if(row>-1){
            ANA_TABLE.removeRow(row);
        }
    }
    /**
     * 检查Grid中是否有重复的人员
     * @param table TTable
     * @param user_id String
     * @return boolean
     */
    private boolean checkGrid(TTable table,String user_id,int column){
        for(int i=0;i<table.getRowCount();i++){
            if(user_id.equals(table.getValueAt(i,column).toString())){
                this.messageBox_("不可重复选取同一位医师");
                return false;
            }
        }
        return true;
    }
    /**
     * 获取体外循环师Grid数据
     * @return TParm
     */
    private TParm getEXTRAData(){
        TParm parm = new TParm();
        for(int i=0;i<EXTRA_TABLE.getRowCount();i++){
            if(EXTRA_TABLE.getValueAt(i,0).toString().trim().length()>0){
                parm.setData("EXTRA_USER"+(i+1),EXTRA_TABLE.getValueAt(i,0));
            }
        }
        return parm;
    }
    /**
     * 获取流动护士Grid数据
     * @return TParm
     */
    private TParm getCIRCULEData(){
        TParm parm = new TParm();
        for(int i=0;i<CIRCULE_TABLE.getRowCount();i++){
            if(CIRCULE_TABLE.getValueAt(i,0).toString().trim().length()>0){
                parm.setData("CIRCULE_USER"+(i+1),CIRCULE_TABLE.getValueAt(i,0));
            }
        }
        return parm;
    }
    /**
     * 获取洗手护士Grid数据
     * @return TParm
     */
    private TParm getSCRUBData(){
        TParm parm = new TParm();
        for(int i=0;i<SCRUB_TABLE.getRowCount();i++){
            if(SCRUB_TABLE.getValueAt(i,0).toString().trim().length()>0){
                parm.setData("SCRUB_USER"+(i+1),SCRUB_TABLE.getValueAt(i,0));
            }
        }
        return parm;
    }
    /**
     * 获取麻醉医师Grid数据
     * @return TParm
     */
    private TParm getANAData(){
        TParm parm = new TParm();
        int index = 2;
        for(int i=0;i<ANA_TABLE.getRowCount();i++){
            if(ANA_TABLE.getValueAt(i,1).toString().trim().length()>0){
                if("Y".equals(ANA_TABLE.getValueAt(i,0).toString().trim()))
                    parm.setData("ANA_USER1",ANA_TABLE.getValueAt(i,1));
                else
                    parm.setData("ANA_USER"+index,ANA_TABLE.getValueAt(i,1));
            }
        }
        return parm;
    }
    /**
     * 麻醉医师 主标识点击事件
     */
    public void onANATableMainCharge(Object obj){
        ANA_TABLE.acceptText();
        if(ANA_TABLE.getSelectedColumn()==0){
           int row = ANA_TABLE.getSelectedRow();
           for (int i = 0; i < ANA_TABLE.getRowCount(); i++) {
               ANA_TABLE.setItem(i,0,"N");
           }
           ANA_TABLE.setItem(row,0,"Y");
       }
    }
}
