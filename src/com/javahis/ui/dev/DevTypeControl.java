package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import jdo.dev.DevTypeTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextFormat;

/** 
 * <p>Title: 设备基本档</p>
 *
 * <p>Description: 设备基本档</p>
 * 
 * <p>Copyright: Copyright (c) 2009</p>
 * 
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DevTypeControl extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() { 
        super.onInit();
        onInitTree(); 
        //fux modify 
//		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
//			setValue("VISIT_CODE_F", "Y");
//			callFunction("UI|MR_NO|setEnabled", true);
//		}  
//        if(this.getValue("DEVPRO_CODE").equals("B")){
//            this.callFunction("UI|DEVSET_CODE|setEnabled", true);
//            this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", true); 
//        }
//        else{ 
//        	 this.callFunction("UI|DEVSET_CODE|setEnabled", false);
//             this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", false); 
//        } 
        //给TREE添加监听事件 
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //添加主设备事件 
        //绑定控件      
        callFunction("UI|DEVSET_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
        //textfield接受回传值   
        callFunction("UI|DEVSET_CODE|addEventListener", 
        TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //默认为中国
        this.setValue("MAN_NATION", "86");  
    	((TTextField)getComponent("DEVSET_CODE")).setEnabled(true);
    	((TTextField)getComponent("DEVSET_CHN_DESC")).setEnabled(false);
         
    }      

    /**
     * 编码接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj; 
        String dev_code = parm.getValue("DEV_CODE");
        if (!dev_code.equals("")) {
            getTextField("DEVSET_CODE").setValue(dev_code); 
        }                                                                        
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!dev_desc.equals("")) {
            getTextField("DEVSET_CHN_DESC").setValue(dev_desc);
        } 
    }
    /**
     * 初始化树  
     */  
    public void onInitTree(){
        TParm parmRule = new TParm();
        parmRule = DevTypeTool.getInstance().getDevRule(); 
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        treeRoot.setText("设备类别");
        treeRoot.setType("Root");
        TParm parm = DevTypeTool.getInstance().analyzeDevType();
        if(parm.getCount("DEVTYPE_GROUP") <= 0)
            return; 
        System.out.println("Count:"+parm.getCount("DEVTYPE_GROUP")); 
        for(int i = 0;i < parm.getCount("DEVTYPE_GROUP");i++){  
            TParm parmI = (TParm)parm.getData("DEVTYPE_GROUP",i); 
            for(int j = 0;j < parmI.getCount("CATEGORY_CODE");j++){
                TTreeNode treeNode = new TTreeNode();
                treeNode.setText(parmI.getValue("CATEGORY_CHN_DESC",j));
                treeNode.setID(parmI.getValue("CATEGORY_CODE",j));
                if(parmI.getValue("DETAIL_FLG",j).equals("Y"))
                    treeNode.setType("UI");
                else
                    treeNode.setType("Path");
               if(getParentCode(treeNode.getID(),parmRule).length() == 0)
                   treeRoot.addSeq(treeNode);
               else
                   treeRoot.findNodeForID(getParentCode(treeNode.getID(),parmRule)).addSeq(treeNode);
           }
        }
        tree.update();
    }

    /**
     * 得到父节点编码
     * @param code String
     * @param parm TParm
     * @return String
     */
    private String getParentCode(String code,TParm parm){
        int classify1 = parm.getInt("CLASSIFY1",0);
        int classify2 = parm.getInt("CLASSIFY2",0);
        int classify3 = parm.getInt("CLASSIFY3",0);
        int classify4 = parm.getInt("CLASSIFY4",0);
        int classify5 = parm.getInt("CLASSIFY5",0);
        int serialNumber = parm.getInt("SERIAL_NUMBER",0);
        if(code.length() == classify1)
            return "";
        if(code.length() == classify1 + classify2)
            return code.substring(0,classify1);  
        if(code.length() == classify1 + classify2 + classify3)
            return code.substring(0,classify1 + classify2);
        if(code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0,classify1 + classify2 + classify3);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,classify1 + classify2 + classify3 + classify4);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5 + serialNumber)
            return code.substring(0,classify1 + classify2 + classify3 + classify4 + classify5);
        return "";
    }
    /**
     * 得到最大顺序号
     * @return int 
     */
    public int getMaxSeq(){
        TParm parm= DevTypeTool.getInstance().selectDevBaseMaxSeq();
        if(parm.getCount("SEQ") <= 0)
            return 1;
        if(parm.getValue("SEQ",0).length() == 0 ||
           parm.getInt("SEQ",0) == 0)
            return 1;
        return parm.getInt("SEQ",0) + 1;
    }
    //SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5,
    //SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE 
    //FROM   SYS_RULE 
    //WHERE RULE_TYPE = 'DEV_RULE'  
    //DEV_CODE    68         22         10         00          0001  (682210000002)
    //DEV_CODE    CLASSIFY1  CLASSIFY2  CLASSIFY3  CLASSIFY4   SERIAL_NUMBER
    //RFID        131214（年月日）                 +3位流水号    (SERIAL_NUMBER根据新需求改成5位) 
    /**
     * 得到最大流水号
     * @param typeCode String
     * @return String
     */
    public String getDevMaxSerialNumber(String typeCode){ 
        TParm parmRule = new TParm();
        parmRule = DevTypeTool.getInstance().getDevRule();
        int serialNumber = parmRule.getInt("SERIAL_NUMBER",0);
        int totNumber = parmRule.getInt("TOT_NUMBER",0);         
        //SELECT MAX(DEV_CODE) DEV_CODE
        TParm parm = DevTypeTool.getInstance().getDevMaxSerialNumber(typeCode);
        String numString = "";
        if(parm.getCount("DEV_CODE")<=0 ||
           parm.getValue("DEV_CODE",0).length() == 0)
            numString = "0"; 
        else         
            numString = parm.getValue("DEV_CODE",0).
            substring(typeCode.length(),parm.getValue("DEV_CODE",0).length());
        int num = Integer.parseInt(numString) + 1;
        numString = num + ""; 
        for(int i = 0;numString.length()<serialNumber;i++)
           numString = "0" + numString;
        String zeroType = "";
        for(int i = 0;zeroType.length()<(totNumber - typeCode.length() - numString.length());i++){
            zeroType += "0";
        }   
        return typeCode + zeroType + numString;
    }
    /**
     * 保存动作
     */
    public void onSave(){
//        if(getValueString("DEVKIND_CODE").length() == 0){
//            messageBox("设备类别不可为空");
//            return;
//        }
//        if(getValueString("DEVPRO_CODE").length() == 0){
//            messageBox("设备属性不可为空");
//            return;
//        }
        if(getValueString("DEV_CHN_DESC").length() == 0){
            messageBox("设备名称不可为空");  
            return;        
        }
        if(getValueString("USE_DEADLINE").length() == 0){
            messageBox("使用年限不可为空");    
            return;        
        }
        //fux modify  20130814  还需要那些不可为空？
        if(getValueString("DEP_DEADLINE").length() == 0){
            messageBox("折旧年限不可为空");  
            return;         
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if(getValueString("DEV_CODE").length() == 0 &&
           (tree.getSelectNode() == null||
            !tree.getSelectNode().isLeaf())){
            messageBox("请选择最小分类");
            return;  
        }  

       String devCode = "";  
       if(getValueString("DEV_CODE").length() == 0)
       {
       devCode = getDevMaxSerialNumber(tree.getSelectNode().getID());
       } 
       else  
       {
       devCode = getValueString("DEV_CODE");
       }
       TParm parm = new TParm();
       parm.setData("DEVKIND_CODE",getValueString("DEVKIND_CODE"));
       parm.setData("DEVPRO_CODE",getValueString("DEVPRO_CODE"));
       parm.setData("ACTIVE_FLG",getValueString("ACTIVE_FLG"));
       parm.setData("DEV_CODE",devCode);
       //修改具体type,必须选取tree相应的节点 
       parm.setData("DEVTYPE_CODE",getValueString("DEVTYPE_CODE"));
       parm.setData("DEV_CHN_DESC",getValueString("DEV_CHN_DESC"));
       parm.setData("PY1",getValueString("PY1"));  
       parm.setData("DEV_ENG_DESC",getValueString("DEV_ENG_DESC"));
       parm.setData("DEV_ABS_DESC",getValueString("DEV_ABS_DESC"));
       parm.setData("DESCRIPTION",getValueString("DESCRIPTION"));
       parm.setData("SPECIFICATION",getValueString("SPECIFICATION"));
       //加入品牌 型号
       parm.setData("BRAND",getValueString("BRAND")); 
       parm.setData("MODEL",getValueString("MODEL")); 
       parm.setData("UNIT_CODE",getValueString("UNIT_CODE"));
       parm.setData("DEPR_METHOD",getValueString("DEPR_METHOD"));
       parm.setData("MAN_CODE",getValueString("MAN_CODE"));
       parm.setData("MAN_NATION",getValueString("MAN_NATION")); 
       parm.setData("BUYWAY_CODE",getValueString("BUYWAY_CODE"));
       parm.setData("USE_DEADLINE",getValueDouble("USE_DEADLINE"));
       parm.setData("DEV_CLASS",getValueString("DEV_CLASS"));
       parm.setData("SEQMAN_FLG",getValueString("SEQMAN_FLG"));
       parm.setData("MEASURE_FLG",getValueString("MEASURE_FLG"));
       //20150709 wangjc add start
       parm.setData("MAINTENANCE_FLG",getValueString("MAINTENANCE_FLG"));//保养注记
       parm.setData("QUALITY_CONTROL_FLG",getValueString("QUALITY_CONTROL_FLG"));//质控注记
       //20150709 wangjc add end
       parm.setData("BENEFIT_FLG",getValueString("BENEFIT_FLG"));
       parm.setData("MEASURE_ITEMDESC",getValueString("MEASURE_ITEMDESC"));
       parm.setData("MEASURE_FREQ",getValueInt("MEASURE_FREQ"));
       parm.setData("SEQ",getMaxSeq());
       parm.setData("OPT_USER",Operator.getID());
       parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
       parm.setData("OPT_TERM",Operator.getIP());
       //附件类型,填写依附主设备 
       if("B".equals(getValueString("DEVPRO_CODE"))){    
       //依附主设备     
       parm.setData("SETDEV_CODE",getValueString("DEVSET_CODE"));
       //依附主设备名称 
       parm.setData("DEVSET_CHN_DESC",getValueString("DEVSET_CHN_DESC"));
       }     
       else{ 
       //依附主设备   
       parm.setData("SETDEV_CODE","");  
       //依附主设备名称    
       parm.setData("DEVSET_CHN_DESC","");  
       }
       parm.setData("DEP_DEADLINE",getValueDouble("DEP_DEADLINE"));    
       parm.setData("FINAN_KIND",getValueString("FINAN_KIND")); 
       parm.setData("FUNDSOU_CODE",getValueString("FUNDSOU_CODE")); 
       //无形资产标记 
       parm.setData("INTANGIBLE_FLG",getValueString("INTANGIBLE_FLG"));
       //初始化将NUM置为0  
       parm.setData("NUM",0);  
       TParm result = DevTypeTool.getInstance().selectDevBase(devCode); 
       //如果数据库（DEV_BASE）查询不到DEV_CODE且界面上的DEV_CODE为空
       if(result.getCount("DEV_CODE")>0 ){ 
    	   parm = DevTypeTool.getInstance().updateDevBase(parm);   
       //如果数据库已有DEV_CODE则执行更新 
    	   if(this.getValueString("DEVPRO_CODE")=="B" && 
    	    	      parm.getValue("SETDEV_CODE") == null &&
    	    	      parm.getValue("DEVSET_CHN_DESC") == null){
    		   messageBox("设备属性为附件,请填写依附设备代码和依附设备名称");  
    		   return;
    	    	   }                
       if (parm.getErrCode() < 0) {
           messageBox("保存失败");
           return;
       }
       messageBox("保存成功");
       setValue("DEV_CODE",devCode);
       queryDevInf(getValueString("DEV_CODE"));
       onTreeClicked();
       return; 
       }
    }
    
    /**
     * 新增动作
     */
    public void onAdd(){
        if(getValueString("DEVKIND_CODE").length() == 0){
            messageBox("设备类别不可为空");
            return;
        }
        if(getValueString("DEVPRO_CODE").length() == 0){
            messageBox("设备属性不可为空");
            return;
        }
        if(getValueString("USE_DEADLINE").length() == 0){
            messageBox("使用年限不可为空");  
            return;
        }
        //fux modify   
        if(this.getValue("DEVPRO_CODE").equals("B")){
            this.callFunction("UI|DEVSET_CODE|setEnabled", true);
            this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", true); 
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if(getValueString("DEV_CODE").length() == 0 &&
           (tree.getSelectNode() == null||
            !tree.getSelectNode().isLeaf())){
            messageBox("请选择最小分类");
            return;  
        }  

        String devCode = "";  
       if(getValueString("DEV_CODE").length() == 0)
       {
       devCode = getDevMaxSerialNumber(tree.getSelectNode().getID());
       } 
       else 
       {
       devCode = getValueString("DEV_CODE");
       }
       TParm parm = new TParm();
       parm.setData("DEVKIND_CODE",getValueString("DEVKIND_CODE"));
       parm.setData("DEVPRO_CODE",getValueString("DEVPRO_CODE"));
       parm.setData("ACTIVE_FLG",getValueString("ACTIVE_FLG"));
       parm.setData("DEV_CODE",devCode);
       //新增一定要选取节点,来确认设备种类码
       parm.setData("DEVTYPE_CODE",tree.getSelectNode().getID());
       parm.setData("DEV_CHN_DESC",getValueString("DEV_CHN_DESC"));
       //依附主设备 
       parm.setData("SETDEV_CODE",getValueString("SETDEV_CODE"));
       parm.setData("PY1",getValueString("PY1"));  
       parm.setData("DEV_ENG_DESC",getValueString("DEV_ENG_DESC"));
       parm.setData("DEV_ABS_DESC",getValueString("DEV_ABS_DESC"));  
       parm.setData("DESCRIPTION",getValueString("DESCRIPTION"));
       parm.setData("SPECIFICATION",getValueString("SPECIFICATION"));
       //型号
       parm.setData("BRAND",getValueString("BRAND")); 
       parm.setData("MODEL",getValueString("MODEL"));
       parm.setData("UNIT_CODE",getValueString("UNIT_CODE"));
       parm.setData("DEPR_METHOD",getValueString("DEPR_METHOD"));
       parm.setData("MAN_CODE",getValueString("MAN_CODE"));
       parm.setData("MAN_NATION",getValueString("MAN_NATION"));
       parm.setData("BUYWAY_CODE",getValueString("BUYWAY_CODE"));
       parm.setData("USE_DEADLINE",getValueDouble("USE_DEADLINE"));
       parm.setData("DEV_CLASS",getValueString("DEV_CLASS"));
       parm.setData("SEQMAN_FLG",getValueString("SEQMAN_FLG"));
       parm.setData("MEASURE_FLG",getValueString("MEASURE_FLG"));
       parm.setData("BENEFIT_FLG",getValueString("BENEFIT_FLG"));
       parm.setData("MEASURE_ITEMDESC",getValueString("MEASURE_ITEMDESC"));
       parm.setData("MEASURE_FREQ",getValueInt("MEASURE_FREQ"));
       parm.setData("SEQ",getMaxSeq());
       parm.setData("OPT_USER",Operator.getID());
       parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
       parm.setData("OPT_TERM",Operator.getIP());
       //附件类型,填写依附主设备
       if(this.getValueString("DEVPRO_CODE")=="B"){   
       //依附主设备    
       parm.setData("SETDEV_CODE",getValueString("DEVSET_CODE"));
       //依附主设备名称 
       parm.setData("DEVSET_CHN_DESC",getValueString("DEVSET_CHN_DESC"));
       }  
       else{
       //依附主设备  
       parm.setData("SETDEV_CODE","");  
       //依附主设备名称 
       parm.setData("DEVSET_CHN_DESC",""); 
       }
       parm.setData("DEP_DEADLINE",getValueDouble("DEP_DEADLINE")); 
       parm.setData("FINAN_KIND",getValueString("FINAN_KIND")); 
       parm.setData("FUNDSOU_CODE",getValueString("FUNDSOU_CODE")); 
       //无形资产标记
       parm.setData("INTANGIBLE_FLG",getValueString("INTANGIBLE_FLG"));
       TParm result = DevTypeTool.getInstance().selectDevBase(devCode); 
       //如果数据库（DEV_BASE）查询不到DEV_CODE且界面上的DEV_CODE为空
       if(result.getCount("DEV_CODE")<=0 ){ 
           parm = DevTypeTool.getInstance().insertDevBase(parm);
           System.out.println("PARM_BASE"+parm); 
           if (parm.getErrCode() < 0) {
               messageBox("保存失败");
               return;   
           }  
           if("B".equals(getValueString("DEVPRO_CODE")) && 
               this.getValueString("DEVSET_CODE").length()==0)
           {
               messageBox("请录入依附主设备");  
               return;
           }
           messageBox("保存成功");
           //保存成功后    查询设备信息
           setValue("DEV_CODE",devCode);
           queryDevInf(getValueString("DEV_CODE"));
           onTreeClicked();  
           return;    
       }
    }

    /**
     * 树的双击事件
     */
    public void onTreeClicked(){
       //检核是否得到树的节点
       if(((TTree)getComponent("TREE")).getSelectNode() == null)
           return;
       if(((TTree)getComponent("TREE")).getSelectNode().isRoot())
           return;
       queryDevTableInfo(((TTree)getComponent("TREE")).getSelectNode().getID());
    } 
    /**
     * 设备编码动作
     */
    public void onDevCode(){  
        if(getValueString("DEV_CODE").length() != 0)  
        	onSave();                    
//        else{
//            queryDevInf(getValueString("DEV_CODE"));
//            queryDevTableInfo(getValueString("DEV_CODE"));
//            ((TComboBox)getComponent("DEVKIND_CODE")).grabFocus();
//        }

    }
    /**
     * 设备类型动作
     */
    public void onDevKindCode(){
        ((TComboBox)getComponent("DEVPRO_CODE")).grabFocus();
    }

    /**
     * 设备名称动作
     */
    public void onDevChnDesc(){
        setValue("PY1",TMessage.getPy(getValueString("DEV_CHN_DESC")));
        ((TTextField)getComponent("DEV_ENG_DESC")).grabFocus();
    }
    /**
     * 设备英文名称动作
     */
    public void onDevEngDesc(){
        ((TTextField)getComponent("DEV_ABS_DESC")).grabFocus();
    }
    /**
     * 设备别名动作
     */
    public void onDevAbsDesc(){
        ((TTextField)getComponent("DESCRIPTION")).grabFocus();
    }
    /**
     * 设备备注动作
     */
    public void onDescription(){
        ((TTextField)getComponent("BRAND")).grabFocus();
    }
    /**
     * 设备品牌动作
     */
    public void onBrand(){
        ((TTextFormat)getComponent("SPECIFICATION")).grabFocus();
    } 
    /**
     * 设备规格动作
     */
    public void onSpeccification(){
        ((TTextFormat)getComponent("MODEL")).grabFocus();
    }
    /**
     * 设备型号动作
     */  
    public void onModel(){ 
        ((TTextFormat)getComponent("UNIT_CODE")).grabFocus();
    }
    /**  
     * 设备单位动作
     */
    public void onUnitCode(){
        ((TComboBox)getComponent("DEPR_METHOD")).grabFocus();
    }
    /**
     * 设备折旧方法动作
     */
    public void onDeprMethod(){
        ((TTextFormat)getComponent("MAN_CODE")).grabFocus();
    }

    /**
     * 设备生产厂商
     */
    public void onManCode(){
        ((TTextFormat)getComponent("MAN_NATION")).grabFocus();
    }
    /**
     * 设备生产国动作
     */
    public void onManNation(){
        ((TNumberTextField)getComponent("FUNDSOU_CODE")).grabFocus();
    }
    
    /**
     * 设备资金来源FUNDSOU_CODE 
     */ 
    public void onFundsouCode(){
        ((TComboBox)getComponent("BUYWAY_CODE")).grabFocus();
    }
     
    /**
     * 设备购入途径动作
     */ 
    public void onBuyWayCode(){
        ((TTextFormat)getComponent("DEV_CLASS")).grabFocus();
    }
    /**
     * 设备分类动作
     */ 
    public void onDevClass(){
        ((TNumberTextField)getComponent("USE_DEADLINE")).grabFocus();
    }
    /**
     * 设备使用期限动作 
     */
    public void onUseDeadLine(){
        ((TNumberTextField)getComponent("DEP_DEADLINE")).grabFocus();
    }
    /**
     * 设备折旧期限动作
     */
    public void onDepDeadLine(){ 
        ((TTextFormat)getComponent("FINAN_KIND")).grabFocus();
    }
    /**
     * 设备财务类别FINAN_KIND
     */
    public void onFinanKind(){ 
    	String finanKind = this.getValueString("FINAN_KIND");
    	messageBox("finanKind"+finanKind);
        String sql =  
            " SELECT  DEP_DEADLINE " + 
            " FROM DEV_FINANKIND " +  
            " WHERE FINAN_KIND = '"+finanKind+"'" + 
            " ORDER BY CLASSIFY,FINAN_KIND ";    
       
        TParm parm = new TParm(getDBTool().select(sql));             
        Double depDeadline = Double.parseDouble(parm.getData("DEP_DEADLINE",0).toString());
        this.setValue("DEP_DEADLINE",depDeadline); 
		((TTextField)getComponent("DEVSET_CODE")).grabFocus();
    } 
    /**
     * 返回数据库操作工具 
     * @return TJDODBTool 
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * 依附主设备编码DEVSET_CODE  
     */   
    public void onDevSetCode(){   
        ((TTextField)getComponent("DEVSET_CHN_DESC")).grabFocus();
    }
    /**
     * 设备属性事件,为附件才可以填写 
     */   
    public void onDevProCode(){    
        ((TTextField)getComponent("DEV_CHN_DESC")).grabFocus();
//        messageBox(""+getValueString("DEVPRO_CODE")); 
//        if("B".equals(getValueString("DEVPRO_CODE"))) {
//        	messageBox("附件"); 
//        	((TTextField)getComponent("DEVSET_CODE")).setEnabled(true);
//        	((TTextField)getComponent("DEVSET_CHN_DESC")).setEnabled(false);
//        } 
    } 
    
    /**
     * 计量标记动作
     */
    public void onMeasureFlg(){
        if(getValue("MEASURE_FLG").equals("N")){
            setValue("MEASURE_ITEMDESC","");
            setValue("MEASURE_FREQ","");
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
        }
        else{
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(true);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(true);
        }
        ((TTextField)getComponent("MEASURE_ITEMDESC")).grabFocus();
    }
    /**
     * 计量描述动作
     */
    public void onMeasureItemDesc(){
        ((TNumberTextField)getComponent("MEASURE_FREQ")).grabFocus();
    }
    /**
     * 表格单击事件
     */
    public void onTableClick(){
        String devCode = ((TTable)getComponent("TABLE")).getDataStore().getItemString(((TTable)getComponent("TABLE")).getSelectedRow(),0); 
        if(devCode == null || devCode.length() == 0)
            return;  
        queryDevInf(devCode);   
    }
    /**
     * 查询动作  
     * @param devType String
     */    
    public void onQuery(String devCode){ 
    	devCode = this.getValueString("DEV_CODE");
        //如果数据库（DEV_BASE）查询不到DEV_CODE且界面上的DEV_CODE不为空            
    	   String SQL =" SELECT DEV_CODE,DEV_CHN_DESC,ACTIVE_FLG,DEVKIND_CODE,DEVPRO_CODE,"+
           " BRAND,SPECIFICATION,MODEL,MAN_CODE,MAN_NATION,DEVTYPE_CODE,SETDEV_CODE," +
           " PY1,SEQ,DESCRIPTION,DEV_ENG_DESC," +
           " DEV_ABS_DESC,UNIT_CODE,BUYWAY_CODE,SEQMAN_FLG,DEPR_METHOD," +
           " MEASURE_FLG,MEASURE_ITEMDESC,MEASURE_FREQ,USE_DEADLINE,BENEFIT_FLG," +
           " DEV_CLASS,OPT_USER,OPT_DATE,OPT_TERM,DEVSET_CHN_DESC," +
           " DEP_DEADLINE,FINAN_KIND,FUNDSOU_CODE,INTANGIBLE_FLG " +
           " ,MAINTENANCE_FLG,QUALITY_CONTROL_FLG " +//20150709 wangjc add
           " FROM   DEV_BASE "+  
           " WHERE  DEV_CODE LIKE '"+devCode+"'||'%'"+
           " ORDER BY SEQ";
//    	   System.out.println("SQL----"+SQL);
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
        if(result.getCount("DEV_CODE")<=0 ){   
            messageBox("查无资料"); 
            return;      
        }         
//        //将表格中的信息(写入TABLE)     
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).setSQL(SQL);
        ((TTable)getComponent("TABLE")).retrieve();  
    }
    
    /** 
     * 查询表格数据动作
     * @param devType String
     */ 
    private void queryDevTableInfo(String devType){ 
        //将表格中的信息(写入TABLE)  
        ((TTable)getComponent("TABLE")).removeRowAll();
        String SQL = " SELECT DEV_CODE,DEV_CHN_DESC,ACTIVE_FLG,DEVKIND_CODE,DEVPRO_CODE,"+
                     "        SPECIFICATION,MODEL"+ 
                     " FROM   DEV_BASE "+       
                     " WHERE  DEV_CODE LIKE '"+devType+"'||'%'"+
                     " ORDER BY SEQ";
        ((TTable)getComponent("TABLE")).setSQL(SQL);
        ((TTable)getComponent("TABLE")).retrieve(); 
    }
    /**
     * 查询设备信息
     * @param devCode String
     */ 
    private void queryDevInf(String devCode){
        TParm result = DevTypeTool.getInstance().selectDevBase(devCode);
        if(result.getErrCode() < 0)
            return; 
        if(result.getCount("DEV_CODE") <= 0) 
            return; 
        setValue("DEV_CODE",result.getValue("DEV_CODE",0));
        //fux modify 20130808 加入设备类型DEVTYPE_CODE
        setValue("DEVTYPE_CODE",result.getValue("DEVTYPE_CODE",0));
        setValue("DEVKIND_CODE",result.getValue("DEVKIND_CODE",0));
        setValue("DEVPRO_CODE",result.getValue("DEVPRO_CODE",0));
        setValue("ACTIVE_FLG",result.getValue("ACTIVE_FLG",0));
        setValue("DEV_CHN_DESC",result.getValue("DEV_CHN_DESC",0));
        setValue("PY1",result.getValue("PY1",0));
        setValue("DEV_ENG_DESC",result.getValue("DEV_ENG_DESC",0));
        setValue("DEV_ABS_DESC",result.getValue("DEV_ABS_DESC",0));
        setValue("DESCRIPTION",result.getValue("DESCRIPTION",0));
        setValue("BRAND",result.getValue("BRAND",0));  
        setValue("SPECIFICATION",result.getValue("SPECIFICATION",0));
        setValue("MODEL",result.getValue("MODEL",0));
        setValue("UNIT_CODE",result.getValue("UNIT_CODE",0));
        setValue("DEPR_METHOD",result.getValue("DEPR_METHOD",0));
        setValue("MAN_CODE",result.getValue("MAN_CODE",0));
        setValue("MAN_NATION",result.getValue("MAN_NATION",0));
        setValue("BUYWAY_CODE",result.getValue("BUYWAY_CODE",0));
        setValue("USE_DEADLINE",result.getValue("USE_DEADLINE",0));
        setValue("DEV_CLASS",result.getValue("DEV_CLASS",0));
        setValue("SEQMAN_FLG",result.getValue("SEQMAN_FLG",0));
        setValue("MEASURE_FLG",result.getValue("MEASURE_FLG",0));
        setValue("BENEFIT_FLG",result.getValue("BENEFIT_FLG",0));
        setValue("MEASURE_ITEMDESC",result.getValue("MEASURE_ITEMDESC",0));
        setValue("MEASURE_FREQ",result.getValue("MEASURE_FREQ",0));
        if(getValue("MEASURE_FLG").equals("N")){
            setValue("MEASURE_ITEMDESC","");
            setValue("MEASURE_FREQ","");
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
        } 
        else{
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(true);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(true);
        }     
        setValue("DEVSET_CODE",result.getValue("SETDEV_CODE",0));
        setValue("DEVSET_CHN_DESC",result.getValue("DEVSET_CHN_DESC",0));
        //新建入财务相关
        setValue("DEP_DEADLINE",result.getValue("DEP_DEADLINE",0));
        setValue("FINAN_KIND",result.getValue("FINAN_KIND",0));
        setValue("FUNDSOU_CODE",result.getValue("FUNDSOU_CODE",0)); 
        //无形资产标记
        setValue("INTANGIBLE_FLG",result.getValue("INTANGIBLE_FLG",0));
        //20150709 wangjc add start
        this.setValue("MAINTENANCE_FLG", result.getValue("MAINTENANCE_FLG",0));//保养注记
        this.setValue("QUALITY_CONTROL_FLG", result.getValue("QUALITY_CONTROL_FLG",0));//质控注记
        //20150709 wangjc add end
    }     
    /**
     * 删除动作 
     */ 
    public void onDelete(){
        if(getValueString("DEV_CODE").length() == 0)
            return;
        TParm parm = DevTypeTool.getInstance().deleteDevBase(getValueString("DEV_CODE"));
        if(parm.getErrCode() < 0){
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功"); 
        onClear();
        onTreeClicked();
    }
    /**
     * 清空动作
     */
    public void onClear(){
        setValue("DEV_CODE","");
        setValue("DEVKIND_CODE","");
        setValue("DEVPRO_CODE",""); 
        setValue("ACTIVE_FLG","Y");
        setValue("DEV_CHN_DESC","");
        setValue("PY1","");
        setValue("DEV_ENG_DESC","");
        setValue("DEV_ABS_DESC","");
        setValue("DESCRIPTION","");
        setValue("BRAND",""); 
        setValue("SPECIFICATION","");
        setValue("MODEL","");
        setValue("UNIT_CODE","");
        setValue("DEPR_METHOD","");
        setValue("MAN_CODE","");
        setValue("MAN_NATION","86"); 
        setValue("BUYWAY_CODE","");
        setValue("USE_DEADLINE","");
        setValue("DEV_CLASS","");
        setValue("SEQMAN_FLG","N");
        setValue("MEASURE_FLG","N");
        setValue("BENEFIT_FLG","N");
        setValue("MEASURE_ITEMDESC","");
        setValue("MEASURE_FREQ","");
        setValue("DEVSET_CODE","");  
        setValue("DEVSET_CHN_DESC","");
//        MDEP_PRICE:月折旧金额
//        DEP_PRICE:累计折旧金额
//        CURR_PRICE:现值  
//        DEP_DEADLINE:折旧年限
//        FINAN_KIND:财务分类
//        FUNDSOU_CODE:资金来源
        setValue("DEP_DEADLINE","");
        setValue("FINAN_KIND","");
        setValue("FUNDSOU_CODE",""); 
        setValue("INTANGIBLE_FLG","");
        ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
        ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
       ((TTable)getComponent("TABLE")).removeRowAll();
    }
    /**
     * 拿到TTable
     * @param tag String
     * @return TTable 
     */
    public TTable getTTable(String tag){ 
        return (TTable)getComponent(tag);
     }     
    
    /**
     * 得到文本控件
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
