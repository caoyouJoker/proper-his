package jdo.emr;

import com.dongyang.ui.TWord;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.MPage;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.data.TParm;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.EImage;
import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;

/**
 * <p>Title:操作word数据 </p>
 *
 * <p>Description:操作word数据 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class GetWordValue {

    /**
     * word
     */
    private TWord word;

    /**
     * 名称
     */
    private String name;

    /**
     * 数值
     */
    private String value;//add by wanglong 20130516
    
    /**
     * 宏名
     */
    private String macroName;

    /**
     * 空列
     */
    private TParm errList = new TParm();


    /**
     * 参数列
     */
    private TParm nameList = new TParm();

    /**
     * 实例
     */
    private static GetWordValue instanceObject;

    /**
     * 构造器
     */
    public GetWordValue() {
    }

    /**
     * 取得实例
     * @return GetWordValue
     */
    public static GetWordValue getInstance() {
        if (instanceObject == null)
            instanceObject = new GetWordValue();
        return instanceObject;
    }

    /**
     * 初始化方法
     */
    private void init(){
        name = "";
        macroName="";
        word = new TWord();
        errList = new TParm();
        nameList = new TParm();
    }

    /**
     * 根据名称取得值
     * @param emrFileDataParm TParm
     * @param name String
     * @param macroName String
     * @return TParm
     */
    public TParm getWordValueByName(TParm emrFileDataParm,String name,String macroName){
        init();
        this.name = name;
        this.macroName=macroName;
        if(!OpenWord.getInstance().onOpen(emrFileDataParm.getValue("FILE_PATH"),
                                          emrFileDataParm.getValue("FILE_NAME"),
                                          3,false,word)){
            nameList.setErrCode(-1);
            nameList.setErrText("文件不存在");
            return nameList;
        }
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++){
            getPageData(pageIndex);
        }
        return nameList;
    }

    /**
     * 检查是否存在空值
     * @param word TWord
     * @return TParm
     */
    public TParm checkValue(TWord word){
        init();
        this.word = word;
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++){
            getPageData(pageIndex);
        }
        return errList;
    }

    /**
     * 根据名称列表取得值
     * @param emrFileDataParm TParm
     * @param name String
     * @return TParm
     */
    public TParm getWordValueByName(TParm emrFileDataParm,String[] nameGroup){
        init();
        if(!OpenWord.getInstance().onOpen(emrFileDataParm.getValue("FILE_PATH"),
                                          emrFileDataParm.getValue("FILE_NAME"),
                                          3,false,word)){
            nameList.setErrCode(-1);
            nameList.setErrText("文件不存在");
            return nameList;
        }
        for(int i = 0;i < nameGroup.length;i++){
            name = nameGroup[i];
            for (int pageIndex = 0; pageIndex < getPageCount(); pageIndex++) {
                getPageData(pageIndex);
            }
        }
        return nameList;
    }

    /**
     * 研究病例
     * @param emrFileDataParm TParm
     * @param name String
     * @return boolean
     */
    private boolean creatTestWord(TParm emrFileDataParm,String tagList[]){
       getWordValueByName(emrFileDataParm,tagList);
       if(nameList.getErrCode() < 0 )
           return false;
       for(int j = 0;j< tagList.length;j++){
           int size = nameList.getCount(tagList[j]);
           for (int i = 0; i < size; i++){
               if (!(nameList.getData(tagList[j], i) instanceof EFixed))
                   continue;
               String value = nameList.getValue(tagList[j] + "_VALUE", i);
               if (value == null || value.length() == 0)
                   continue;
               ((EFixed) nameList.getData(tagList[j], i)).setText("");
           }
       }
       word.getPM().getFocusManager().update();
       return true;
    }

    /**
     * 保存研究病例
     * @param emrFileDataParm TParm
     * @return boolean
     */
    public boolean saveTestWord(TParm emrFileDataParm){
        String testTag = TConfig.getSystemValue("EMR.TEST_TAG");
        if(testTag == null || testTag.length() == 0)
            return false;
        String tagList[] = StringTool.parseLine(testTag, "|");
        if(!creatTestWord(emrFileDataParm,tagList))
            return false;
        String testWordName = emrFileDataParm.getValue("FILE_NAME") + "_研究病例";
        if(!word.onSaveAs(emrFileDataParm.getValue("FILE_PATH"),
                          testWordName,
                          3))
            return false;
        EMRCreateXMLTool.getInstance().createXML(emrFileDataParm.getValue("FILE_PATH"),
                                                 testWordName,
                                                 "EmrData",
                                                 word);
        return true;
    }

    /**
     * 得到页的数量
     * @return int
     */
    private int getPageCount(){
        return getPageManager().getComponentList().size();
    }

    /**
     * 得到页管理器
     * @return MPage
     */
    private MPage getPageManager(){
        return word.getPageManager();
    }

    /**
     * 得到页数据
     * @param pageIndex int
     */
    private void getPageData(int pageIndex){
        for(int panelIndex = 0;panelIndex < getPagePanelCount(pageIndex);
                             panelIndex++){
            getPanelData(pageIndex,panelIndex);
        }
    }

    /**
     * 得到面板数据
     * @param pageIndex int
     * @param panelIndex int
     */
    private void getPanelData(int pageIndex,int panelIndex){
        for(int blockIndex = 0;blockIndex < getPagePanelBlockCount(pageIndex,
                panelIndex);blockIndex++){
            if(!isTable(pageIndex,panelIndex,blockIndex)){
                getElement(getIBlock(pageIndex,panelIndex,blockIndex));
                continue;
            }
            getTableData(pageIndex,panelIndex,blockIndex);
        }
    }

    /**
     * 得到页面板数量
     * @param pageIndex int
     * @return int
     */
    private int getPagePanelCount(int pageIndex){
        return getEPage(pageIndex).getComponentList().size();
    }

    /**
     * 得到页
     * @param pageIndex int
     * @return EPage
     */
    private EPage getEPage(int pageIndex){
        return getPageManager().get(pageIndex);
    }

    /**
     * 得到页面板元素个数
     * @param pageIndex int
     * @param panelIndex int
     * @return int
     */
    private int getPagePanelBlockCount(int pageIndex,int panelIndex){
        return getEPanel(pageIndex,panelIndex).getComponentList().size();
    }

    /**
     * 得到面板
     * @param pageIndex int
     * @param panelIndex int
     * @return EPanel
     */
    private EPanel getEPanel(int pageIndex, int panelIndex){
        return getEPage(pageIndex).get(panelIndex);
    }

    /**
     * 判断是否是表格
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return boolean
     */
    private boolean isTable(int pageIndex ,int panelIndex,int blockIndex){
        IBlock block = getIBlock(pageIndex, panelIndex, blockIndex);
        if(block == null)
            return false;
        return(block.getObjectType() == EComponent.TABLE_TYPE);
    }

    /**
     * 得到元素
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return IBlock
     */
    private IBlock getIBlock(int pageIndex, int panelIndex,int blockIndex){
        return getEPanel(pageIndex,panelIndex).get(blockIndex);
    }

    /**
     * 得到表格数据
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     */
    private void getTableData(int pageIndex,int panelIndex,int blockIndex){
        for(int row = 0;row < getTableRowCount(pageIndex,panelIndex,blockIndex);row ++ ){
            getTableRowData(pageIndex,panelIndex,blockIndex,row);
        }
    }

    /**
     * 得到表格行数
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return int
     */
    private int getTableRowCount(int pageIndex ,int panelIndex,int blockIndex){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).
                getComponentList().size();
    }

    /**
     * 得到表格行数据
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     */
    private void getTableRowData(int pageIndex,int panelIndex,
                                    int blockIndex,int row){
           for(int column = 0;column < getTableRowColumnCount(pageIndex,
                   panelIndex,blockIndex,row);column ++ ){
               getTableRowColumn(pageIndex,panelIndex,blockIndex,row,column);
           }
    }

    /**
     * 得到表格列数
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @return int
     */
    private int getTableRowColumnCount(int pageIndex ,int panelIndex,
                                       int blockIndex,int row){
        if(!isTable(pageIndex,panelIndex,blockIndex))
            return 0;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                getComponentList().size();
    }

    /**
     * 得到表格列数据
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     */
    private void getTableRowColumn(int pageIndex,int panelIndex,
                                   int blockIndex,int row,int column){
        for(int tpc = 0;tpc < getTableRowColumnPanelCount(pageIndex,panelIndex,
                blockIndex,row,column);tpc++){
            getTableRowColumnPanelData(pageIndex,panelIndex,blockIndex,
                                       row,column,tpc);
        }
    }

    /**
     * 得到表格行列面板个数
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @return int
     */
    private int getTableRowColumnPanelCount(int pageIndex ,int panelIndex,
                                          int blockIndex,int row, int column){
      if(!isTable(pageIndex,panelIndex,blockIndex))
          return 0;
      return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
              get(column).getComponentList().size();
   }

   /**
    * 得到表格列面板元素个数
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param column int
    * @param tpanelIndex int
    * @return int
    */
   private int getTableRowColumnPanelBlockCount(int pageIndex ,int panelIndex,
                                                int blockIndex, int row,
                                                int column,int tpanelIndex){
       if(!isTable(pageIndex,panelIndex,blockIndex))
           return 0;
       return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
               get(column).get(tpanelIndex).getComponentList().size();
   }

   /**
    * 得到表格元素
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param column int
    * @param tpanelIndex int
    * @param tBlockIndex int
    * @return IBlock
    */
   private IBlock getTableBlock(int pageIndex ,int panelIndex,
                                int blockIndex,int row, int column,
                                int tpanelIndex,int tBlockIndex){
       if(!isTable(pageIndex,panelIndex,blockIndex))
            return null;
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column).get(tpanelIndex).get(tBlockIndex);
    }

    /**
     * 得到表格行列面板数据
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @param tpc int
     */
    private void getTableRowColumnPanelData(int pageIndex,int panelIndex,
                                            int blockIndex,int row,
                                            int column,int tpc){
        if(getTableRowColumnPanelBlockCount(pageIndex,panelIndex,
                                            blockIndex,row,column,tpc) <= 0)
            return;
        IBlock obj = (IBlock)getTableBlock(pageIndex,panelIndex,
                                           blockIndex,row,column,tpc,0);
        if(obj == null)
            return;
        if(obj.getObjectType() == EComponent.TEXT_TYPE)
            obj = obj.getNextTrueBlock();
        while(obj != null){
            getElement(obj);
            obj = obj.getNextTrueBlock();
        }
    }

    /**
     * 检查面板空值
     * @param block IBlock
     */
    private void checkElemment(IBlock block){
        if (!(block instanceof EFixed))
            return;
        if (block instanceof ENumberChoose) {
            String s = ((ENumberChoose) block).getText();
            if (s.length() == 0)
                s = "0";
            double v = 0;
            try {
                v = Double.parseDouble(s);
            } catch (Exception e) {
            }
            if (((ENumberChoose) block).isCheckMinValue()) {
                if (v < ((ENumberChoose) block).getNumberMinValue()){
                    errList.addData("RANG_NAME",
                                     ( (ENumberChoose) block).getName());
                    errList.addData("RANG_GROUP_NAME",
                                     ( (ENumberChoose) block).getGroupName());
                }
            }
            if (((ENumberChoose) block).isCheckMaxValue()) {
                if (v > ((ENumberChoose) block).getNumberMaxValue()){
                    errList.addData("RANG_NAME",
                                     ( (ENumberChoose) block).getName());
                    errList.addData("RANG_GROUP_NAME",
                                     ( (ENumberChoose) block).getGroupName());
                }
            }
            return;
        }
        if(((EFixed) block).isAllowNull())
            return;
        if(block.getObjectType() ==  EComponent.CHECK_BOX_CHOOSE_TYPE)
            return;
        if(block.getObjectType() ==  EComponent.CAPTURE_TYPE){
            if (((ECapture) block).getCaptureType() == 1)
                return;
            if(((ECapture) block).getValue().length() == 0){
                errList.addData("NULL_NAME", ( (ECapture) block).getName());
                errList.addData("NULL_GROUP_NAME",((EFixed) block).getGroupName());
            }
            return;
        }
        if(((EFixed) block).getText().length() == 0){
            errList.addData("NULL_NAME", ((EFixed) block).getName());
            errList.addData("NULL_GROUP_NAME",((EFixed) block).getGroupName());
        }
    }

    /**
     * 得到元素值
     * @param block IBlock
     */
    private void getElement(IBlock block) {
        if(name.length() == 0){
            checkElemment(block);
            return;
        }
        if(block instanceof EMacroroutine && ((EMacroroutine)block).isPic()){
            if(name.equals(((EMacroroutine)block).getName())){
                nameList.addData(name,block);
                nameList.addData(name + "_VALUE",((VPic) ((EMacroroutine) block).getModel().getMVList().
                                                 get(0).get(0)).getPictureName());
            }
            return;
        }
        if(block instanceof EImage){
            if(name.equals(((EImage) block).getName())){
                nameList.addData(name,block);
                nameList.addData(name + "_VALUE", ((EImage) block).getBlockValue());
            }
            return;
        }
        if (!(block instanceof EFixed))
            return;
        switch (block.getObjectType()) {
        case EComponent.CAPTURE_TYPE:
            if (((ECapture) block).getCaptureType() == 1)
                return;
            if(name.equals(((EFixed) block).getName())){
                nameList.addData(name,block);
                nameList.addData(name + "_VALUE", ((ECapture) block).getValue());
            }
            return;
        case EComponent.CHECK_BOX_CHOOSE_TYPE:
            if(name.equals(((EFixed) block).getName())){
                nameList.addData(name,block);
                nameList.addData(name + "_VALUE",  ((ECheckBoxChoose) block).isChecked() ? "Y" : "N");
            }
            return;
        default:
            //System.out.println("========加入宏名=============");
            if(macroName!=null&&!macroName.equals("")){
                if(macroName.equals(((EFixed) block).getMicroName())){
                    nameList.addData(macroName, block);
                    nameList.addData(macroName + "_VALUE", block.getBlockValue());
                }


            }else{
                if(name.equals(((EFixed) block).getName())){
                    nameList.addData(name, block);
                    nameList.addData(name + "_VALUE", block.getBlockValue());
                }

            }


            return;
        }
    }
    
    /**
     * 根据名称列表设置值
     * @param emrFileDataParm TParm
     * @param name String
     * @return TParm
     */
    public void setWordValueByParm(TWord word, TParm inParm) {//add by wanglong 20130516
        init();
        this.word = word;
        String[] nameGroup = inParm.getNames();
        for (int i = 0; i < nameGroup.length; i++) {
            name = nameGroup[i];
            macroName = nameGroup[i];
            value = inParm.getValue(name);
            for (int pageIndex = 0; pageIndex < getPageCount(); pageIndex++) {
                setPageData(pageIndex);
            }
        }
    }
    
    /**
     * 设置页数据
     * @param pageIndex int
     */
    private void setPageData(int pageIndex) {// add by wanglong 20130516
        for (int panelIndex = 0; panelIndex < getPagePanelCount(pageIndex); panelIndex++) {
            setPanelData(pageIndex, panelIndex);
        }
    }
    
    /**
     * 设置面板数据
     * @param pageIndex int
     * @param panelIndex int
     */
    private void setPanelData(int pageIndex, int panelIndex) {// add by wanglong 20130516
        for (int blockIndex = 0; blockIndex < getPagePanelBlockCount(pageIndex, panelIndex); blockIndex++) {
            if (!isTable(pageIndex, panelIndex, blockIndex)) {
                setElement(getIBlock(pageIndex, panelIndex, blockIndex));
                continue;
            }
        }
    }
    
    /**
     * 设置元素值
     * @param block IBlock
     */
    private void setElement(IBlock block) {// add by wanglong 20130516
        if (name.length() == 0) {
            checkElemment(block);
            return;
        }
        if (!(block instanceof EFixed)) return;
        switch (block.getObjectType()) {
            case EComponent.SINGLE_CHOOSE_TYPE:// 单选
                if (name.equals(((EFixed) block).getName())) {
                    ((ESingleChoose) block).setText(value);
                }
                return;
            case EComponent.MULTI_CHOOSE_TYPE:// 多选
                if (name.equals(((EFixed) block).getName())) {
                    ((EMultiChoose) block).setText(value);
                }
                return;
            case EComponent.NUMBER_CHOOSE_TYPE:// 数字选择
                if (name.equals(((EFixed) block).getName())) {
                    ((ENumberChoose) block).setText(value);
                }
                return;
            case EComponent.HAS_CHOOSE_TYPE:// 有无选择
                if (name.equals(((EFixed) block).getName())) {
                    ((EHasChoose) block).setText(value);
                }
                return;   
            case EComponent.CAPTURE_TYPE://抓取
                if (((ECapture) block).getCaptureType() == 1) return;
                if (name.equals(((EFixed) block).getName())) {
                    ((ECapture) block).setText(value);
                }
                return;
            case EComponent.CHECK_BOX_CHOOSE_TYPE://选择框
                if (name.equals(((EFixed) block).getName())) {
                    if (value.equalsIgnoreCase("Y")) {
                        ((ECheckBoxChoose) block).setChecked(true);
                    } else {
                        ((ECheckBoxChoose) block).setChecked(false);
                    }
                }
                return;
            default:
                if (macroName != null && !macroName.equals("")) {
                    if (macroName.equals(((EFixed) block).getMicroName())) {// 宏
                        word.setMicroField(macroName, value);
                    }
                } else {
                    if (name.equals(((EFixed) block).getName())) {// 固定文本
                        ((EFixed) block).setText(value);
                    }
                }
                return;
        }
    }
    
}
