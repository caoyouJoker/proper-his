package jdo.emr;

import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TWord;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.MPage;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.config.TConfig;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.EImage;
import com.dongyang.tui.text.image.GBlock;
import com.dongyang.tui.text.image.GLine;
import com.dongyang.data.TParm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CDATASection;
import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.div.VLine;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VTable;
import com.dongyang.tui.text.div.VText;
import com.dongyang.tui.text.div.VBarCode;
import com.dongyang.tui.text.div.MV;
import com.dongyang.jdo.TJDODBTool;
import org.w3c.dom.NodeList;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.EText;
import org.w3c.dom.ProcessingInstruction;
import com.dongyang.tui.text.DStyle;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ui.CTable;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.EPic;
import com.dongyang.tui.text.div.MVList;

/**
 * <p>Title: EMR转XML工具类</p>
 *
 * <p>Description:EMR转XML工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class EMRCreateXMLTool {

    /**
     * WORD对象
     */
    private DText word;

    /**
     * XML
     */
    private Document XMLDoc;


    /**
     * XML
     */
    private Document XMLDocCDA;

    /**
     * 编码
     */
    private String ENCODING = "UTF-8";

    /**
     * CDA标签根节点
     */
    private String CDA_INDEX = "JAVAHIS-EMR-CDA";

    /**
     * 模板索引编号
     */
    private String CDA_INDEX_CODE = "HR00.00.001.05";

    /**
     * 实例
     */
    private static EMRCreateXMLTool instanceObject;


    /**
     * 构造器
     */
    public EMRCreateXMLTool() {
    }

    /**
     * 得到实例
     * @return EMRCreateXMLTool
     */
    public static EMRCreateXMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new EMRCreateXMLTool();
        return instanceObject;
    }

    /**
     * XML文件
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word TWord
     */
    public void createXML(String path,String fileName,String dir,TWord word){
        createXML(path,fileName,dir,word.getWordText());
    }
    /**
     * XML文件
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word TWord
     */
    public boolean createTXML(String path,String fileName,String dir,TWord word){
    	return createTXML(path,fileName,dir,word.getWordText());
    }

    /**
     * XML文件
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word DText
     */
    public void createXML(String path,String fileName,String dir,DText word){
        this.word = word;
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            XMLDoc=builder.newDocument();
            XMLDocCDA=builder.newDocument();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        Node nodeRoot = XMLRoot();
        setWordIndex();
        XMLRootCDA();
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++)
            setPageData(pageIndex,nodeRoot);
        setTitleInfo(nodeRoot);
        writeFile(path,fileName,dir);
    }
    /**
     * XML文件
     * @param path String
     * @param fileName String
     * @param dir String
     * @param word DText
     */
    public boolean createTXML(String path,String fileName,String dir,DText word){
        this.word = word;
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            XMLDoc=builder.newDocument();
            XMLDocCDA=builder.newDocument();
        } catch (ParserConfigurationException ex) {
            return false;
        }
        Node nodeRoot = XMLRoot();
        setWordIndex();
        XMLRootCDA();
        for(int pageIndex = 0;pageIndex < getPageCount();pageIndex++)
            setPageData(pageIndex,nodeRoot);
        setTitleInfo(nodeRoot);
        return writeFile(path,fileName,dir);
    }
    /**
     * 通过本系统的CODE返回CDA对应编码;
     * @param cdaGroupName String
     * @param cdaCode String
     * @param sourceCode String
     * @return String
     */
    public String getCDACode(String cdaGroupName,String cdaCode,String sourceCode){
        String retCdaCode=sourceCode;

        return retCdaCode;
    }


    /**
     * 取得文档标号
     */
    private void setWordIndex(){
       for(int i = 0;i < getPageManager().getMVList().size();i++){
           for (int j = 0; j < getPageManager().getMVList().get(i).size();j++) {
               DIV div = getPageManager().getMVList().get(i).get(j);
               if (!"UNVISITABLE_ATTR".equals(getPageManager().getMVList().get(i).getName()))
                   continue;
               if (!(div instanceof VText))
                   continue;
               if(!((VText)div).getName().equals(CDA_INDEX_CODE))
                   continue;
               CDA_INDEX = ((VText)div).getMicroText();
           }
       }
   }
   //$$=========Start add by lx 加入对图区的处理=============$$//
   /**
    *
    * @param nodeRoot Node
    * @param pic EPic
    */
   private void setPicInfo(EPic pic){
//       System.out.println("=====取PIC下图层对象=====");
       Node nodeRoot = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
       MVList pkList =pic.getMVList();
        //MVList pkList = pic.getPM().getPageManager().getMVList();
        for(int i = 0;i < pkList.size();i++){
//             System.out.println("PIC下图层名称==========="+pkList.get(i).getName());
             for (int j = 0; j < pkList.get(i).size();j++) {
                    DIV div = pkList.get(i).get(j);
//                    System.out.println("=====div 类型====="+div.getType());
//                    System.out.println("=====div 名称====="+div.getName());
                    if(div instanceof VText){
                          //是否取CDA名称  20120813 shibl modify
                          if(((VText) div).getTakeCdaName()){
                        	  String cdaValue=((VText)div).getDrawText();
                              if(cdaValue!=null&&!cdaValue.equals("")){
                                  //假如有CDA对应编码，取编码;
                                  if ( ( (VText) div).getCdaValue() != null &&
                                      ! ( (VText) div).getCdaValue().equals("")) {
                                      cdaValue = ( (VText) div).getCdaValue();
                                  }

                              }
                              //是取CDA名称；
                        	  appendNodeCDA( ((VText)div).getMicroName(),
                                      ( (VText) div).getGroupName(),
                                      cdaValue);
                          }else{
                        	  appendNodeCDA( ( (VText) div).getName(),
                                      ( (VText) div).getGroupName(),
                                      ( (VText) div).getMicroText());
                          }
                        continue;
                    }
                    setVInfo(div,nodeRoot);
             }

        }
        //System.out.println("=====setPicInfo完成.=====");
   }



   //$$=========End add by lx 加入对图区的处理=============$$//
    /**
     * 设置Title信息
     * @param nodeRoot Node
     */
    private void setTitleInfo(Node nodeRoot){
        for(int i = 0;i < getPageManager().getMVList().size();i++){
            //System.out.println("=============图层名称==================="+getPageManager().getMVList().get(i).getName());
            for(int j = 0;j<getPageManager().getMVList().get(i).size();j++){
                DIV div = getPageManager().getMVList().get(i).get(j);
                if("UNVISITABLE_ATTR".equals(getPageManager().getMVList().get(i).getName())){
                    if(div instanceof VText)
                        appendNodeCDA(((VText)div).getName(),((VText)div).getGroupName(),((VText)div).getMicroText());
                    continue;
                }
                setVInfo(div,nodeRoot);
            }
        }

    }

    /**
     * 设置Title信息
     * @param mv MV
     * @param nodeRoot Node
     */
    private void setTitleInfo(MV mv,Node nodeRoot){
        for(int i = 0;i < mv.size();i++)
            setVInfo(mv.get(i),nodeRoot);
    }

    /**
     * 设置V组件属性
     * @param div DIV
     * @param nodeRoot Node
     */
    private void setVInfo(DIV div,Node nodeRoot){
//        System.out.println("setVInfo  div类型============="+div.getType());
//        System.out.println("setVInfo  div名称============="+div.getName());
        if(div instanceof VTable){
            setTitleInfo( ( (VTable) div).getMV(), nodeRoot);
        }
        if(div instanceof VLine){
            XMLElement("TITLE_VLINE", getStyle( (VLine) div), nodeRoot);
        }

        if(div instanceof VText){
            XMLElement("TITLE_VTEXT", getStyle( (VText) div), nodeRoot);
            //System.out.println("****div类型******="+div.getType());
//            System.out.println("****div名称******"+((VText)div).getName());
//            System.out.println("****div值******"+((VText)div).getMicroName());
            String cdaname=((VText)div).getName();
//            System.out.println("-------是否取CDA名称----------------------"+((VText) div).getTakeCdaName());
            //是否取CDA名称
            if(((VText) div).getTakeCdaName()){
                //是取CDA名称；
                cdaname=((VText)div).getMicroName();
            }
            String cdaValue=((VText)div).getDrawText();
//            System.out.println("======cda值为：=========="+cdaValue);
            if(cdaValue!=null&&!cdaValue.equals("")){
                //假如有CDA对应编码，取编码;
                if ( ( (VText) div).getCdaValue() != null &&
                    ! ( (VText) div).getCdaValue().equals("")) {
                    cdaValue = ( (VText) div).getCdaValue();
                }

            }
            //CDA值不空，则创建值;
            if(cdaValue!=null&&!cdaValue.equals("")){
                appendNodeCDA(cdaname,((VText)div).getGroupName(),cdaValue);
            }

        }
        if(div instanceof VPic){
            XMLElement("TITLE_VPIC", getStyle( (VPic) div), nodeRoot);
        }
        if(div instanceof VBarCode){
            XMLElement("TITLE_VBARCODE", getStyle( (VBarCode) div), nodeRoot);
        }



    }



    /**
     * 设置Title信息
     * @param div VText
     * @return TParm
     */
    private TParm getStyle(VText div){
       TParm parm = new TParm();
       parm.setData("ALIGNMENT",div.getAlignment());
       parm.setData("AUTOENTERHEIGHT",div.getAutoEnterHeight());
       parm.setData("COLOR",div.getColor());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("DRAWSTARTXP",div.getDrawStartXP());
       parm.setData("DRAWSTARTYP",div.getDrawStartYP());
       parm.setData("TEXT",div.getText());
       parm.setData("ISTEXTT",div.isTextT());
       parm.setData("DRAWTEXT",div.getDrawText());
       parm.setData("X",div.getX());
       parm.setData("Y",div.getY());
       parm.setData("X0",div.getX0());
       parm.setData("Y0",div.getY0());
       parm.setData("ENDX",div.getEndX());
       parm.setData("ENDXP",div.getEndXP());
       parm.setData("STARTX",div.getStartX());
       parm.setData("STARTY",div.getStartY());
       parm.setData("STARTXP",div.getStartXP());
       parm.setData("STARTYP",div.getStartYP());
       parm.setData("FONTSIZE",div.getFontSize());
       parm.setData("FONTSTYLE",div.getFontStyle());
       parm.setData("FONTNAME",div.getFontName());
       return parm;
   }


   private TParm getStyle(VBarCode div){
      TParm parm = new TParm();
      parm.setData("X",div.getX());
      parm.setData("Y",div.getY());
      parm.setData("XDIMENSIONCM",div.getXDimensionCM());
      parm.setData("BACKGROUND",div.getBackground());
      parm.setData("BACKGROUNDSTRING",div.getBackgroundString());
      parm.setData("BARCOLOR",div.getBarColor());
      parm.setData("BARCOLORSTRING",div.getBarColorString());
      parm.setData("BARHEIGHTCM",div.getBarHeightCM());
      parm.setData("BARTEXTCOLOR",div.getBarTextColor());
      parm.setData("BARTEXTCOLORSTRING",div.getBarTextColorString());
      parm.setData("CHECKCHARACTER",div.getCheckCharacter());
      parm.setData("DRAWSTARTX",div.getDrawStartX());
      parm.setData("DRAWSTARTXP",div.getDrawStartXP());
      parm.setData("DRAWSTARTY",div.getDrawStartY());
      parm.setData("DRAWSTARTYP",div.getDrawStartYP());
      parm.setData("DRAWTEXT",div.getDrawText());
      parm.setData("FONT",div.getFont());
      parm.setData("FONTNAME",div.getFontName());
      parm.setData("FONTSIZE",div.getFontSize());
      parm.setData("FONTSTYLE",div.getFontStyle());
      parm.setData("TEXT",div.getText());
      return parm;
   }

   /**
    * 设置Title信息
    * @param div VLine
    * @return TParm
    */
   private TParm getStyle(VLine div){
       TParm parm = new TParm();
       parm.setData("COLOR",div.getColor());
       parm.setData("X0",div.getX0());
       parm.setData("Y0",div.getY0());
       parm.setData("X1",div.getX1());
       parm.setData("Y1",div.getY1());
       parm.setData("X2",div.getX2());
       parm.setData("Y2",div.getY2());
       parm.setData("LINETYPE",div.getLineType());
       parm.setData("LINEWIDTH",div.getLineWidth());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("DRAWSTARTXP",div.getDrawStartXP());
       parm.setData("DRAWSTARTYP",div.getDrawStartYP());
       parm.setData("DRAWSX1",div.getDrawX1());
       parm.setData("DRAWSY1",div.getDrawY1());
       parm.setData("DRAWSX1P",div.getDrawX1P());
       parm.setData("DRAWSY1P",div.getDrawY1P());
       return parm;
   }

   /**
    * 设置Title信息
    * @param div VPic
    * @return TParm
    */
   private TParm getStyle(VPic div){
       TParm parm = new TParm();
       parm.setData("PICTURENAME",div.getPictureName());
       parm.setData("DRAWSTARTX",div.getDrawStartX());
       parm.setData("DRAWSTARTY",div.getDrawStartY());
       parm.setData("HEIGHT",div.getHeight());
       parm.setData("WIDTH",div.getWidth());
       parm.setData("X",div.getX());
       parm.setData("Y",div.getY());
       parm.setData("ENDX",div.getEndX());
       parm.setData("ENDY",div.getEndY());
       parm.setData("ENDXP",div.getEndXP());
       parm.setData("ENDYP",div.getEndYP());
       parm.setData("STARTX",div.getStartX());
       parm.setData("STARTY",div.getStartY());
       parm.setData("STARTXP",div.getStartXP());
       parm.setData("STARTYP",div.getStartYP());
       return parm;
   }

   /**
    * 写文件
    * @param path String
    * @param fileName String
    * @param dir String
    * @return boolean
    */
   private boolean writeFile(String path,String fileName,String dir){
        String fileServerIP = TConfig.getSystemValue("FileServer.Main.IP");
        int port = TSocket.FILE_SERVER_PORT;
        TSocket socket = new TSocket(fileServerIP,port);
        path = path.replaceFirst("JHW","XML");
        try{
            if (!TIOM_FileServer.writeFile(socket, getDir(dir) +
                                           getServerFileName(path, fileName),
                                           toXMLString().getBytes(ENCODING)))
                return false;
            if (!TIOM_FileServer.writeFile(socket, getDir(dir) +
                                           getServerFileName(path,fileName + "_CDA"),
                                           toXMLStringCDA().getBytes(ENCODING)))
                return false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;

    }

    /**
     * 得到目录
     * @param dir String
     * @return String
     */
    private String getDir(String dir)
    {
        return TIOM_FileServer.getRoot() + TIOM_FileServer.getPath(dir);
    }

    /**
     * 得到文件名
     * @param path String
     * @param fileName String
     * @return String
     */
    private String getServerFileName(String path,String fileName)
    {
        if(path == null || path.length() == 0)
            return "";
        if(fileName == null || fileName.length() == 0)
            return "";
        if(!path.endsWith("\\"))
            path += "\\";
        fileName = path + fileName;
        if(!fileName.endsWith(".xml"))
            fileName += ".xml";
        return fileName;
    }

    /**
     * 提取页面信息
     * @param pageIndex int
     * @param parent Node
     */
    private void setPageData(int pageIndex,Node parent){
        Node pageNode= XMLElement("PAGE",getStyle(getEPage(pageIndex),pageIndex + 1),parent);
        for(int panelIndex = 0;panelIndex < getPagePanelCount(pageIndex);
                             panelIndex++){
            setPanelData(pageIndex,panelIndex,pageNode);
        }
    }

    /**
     * 取得页面属性
     * @param page EPage
     * @param pageNo int
     * @return TParm
     */
    private TParm getStyle(EPage page,int pageNo){
       TParm parm = new TParm();
       parm.setData("BASEHEIGHT",page.getBaseHeight());
       parm.setData("BASEWIDTH",page.getBaseWidth());
       parm.setData("HEIGHT",page.getHeight());
       parm.setData("WIDTH",page.getWidth());
       parm.setData("INSETSHEIGHT",page.getInsetsHeight());
       parm.setData("INSETSWIDTH",page.getInsetsWidth());
       parm.setData("X",page.getX());
       parm.setData("Y",page.getY());
       parm.setData("INSETSX",page.getInsetsX());
       parm.setData("INSETSY",page.getInsetsY());
       parm.setData("Top",getPageManager().getImageableY());
       parm.setData("Bottom",getPageManager().getPageHeight() -
                             getPageManager().getImageableHeight() -
                             getPageManager().getImageableY());
       parm.setData("Left",getPageManager().getImageableX());
       parm.setData("Right",getPageManager().getPageWidth() -
                            getPageManager().getImageableWidth() -
                            getPageManager().getImageableX());
       parm.setData("E_Top",getPageManager().getEditInsets().top);
       parm.setData("E_Bottom",getPageManager().getEditInsets().bottom);
       parm.setData("E_Left",getPageManager().getEditInsets().left);
       parm.setData("E_Right",getPageManager().getEditInsets().right);
       parm.setData("no",pageNo);
       return parm;
   }

   /**
    * 提取面板信息
    * @param pageIndex int
    * @param panelIndex int
    * @param parent Node
    */
   private void setPanelData(int pageIndex,int panelIndex,Node parent){
        for(int blockIndex = 0;blockIndex < getPagePanelBlockCount(pageIndex,
                panelIndex);blockIndex++){
            if(!isTable(pageIndex,panelIndex,blockIndex)){
                IBlock block = getIBlock(pageIndex,panelIndex,blockIndex);
//                System.out.println("--------block-----------------"+block.getBlockValue());
                if(block.getNextTrueBlock() != null &&
                   (block.getNextTrueBlock().findIndex()) - blockIndex > 1)
                    continue;
                setElement(block,parent);
                continue;
            }
            setTableData(pageIndex,panelIndex,blockIndex,parent);
        }
    }

    /**
     * 提取表格信息
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param parent Node
     */
    private void setTableData(int pageIndex,int panelIndex,int blockIndex,Node parent){
        Node nodeTable = XMLElement("table",getStyle(getETable(pageIndex,panelIndex,blockIndex)),parent);
        for(int row = 0;row < getTableRowCount(pageIndex,panelIndex,blockIndex);row ++ ){
            Node nodeTr = XMLElement("tr",getStyle(getETR(pageIndex,panelIndex,blockIndex,row)),nodeTable);
            setTableRowData(pageIndex,panelIndex,blockIndex,row,nodeTr);
        }
    }

    /**
     * 取得表格属性
     * @param table ETable
     * @return TParm
     */
    private TParm getStyle(ETable table){
        TParm parm = new TParm();
        parm.setData("X",table.getX());
        parm.setData("Y",table.getPanel().getY() + table.getY());
        parm.setData("HEIGHT",table.getHeight());
        parm.setData("WIDTH",table.getWidth());
        parm.setData("INSETSHEIGHT",table.getInsetsHeight());
        parm.setData("INSETSWIDTH",table.getInsetsWidth());
        parm.setData("INSETSX",table.getInsetsX());
        parm.setData("INSETSY",table.getInsetsY());
        return parm;
   }

   /**
    * 提取表格行数据
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param parent Node
    */
   private void setTableRowData(int pageIndex,int panelIndex,
                                 int blockIndex,int row,Node parent){
        for(int column = 0;column < getTableRowColumnCount(pageIndex,
                panelIndex,blockIndex,row);column ++ ){
            Node nodeTd = XMLElement("td",
                                     getStyle(getETD(pageIndex,panelIndex,blockIndex,row,column)),
                                     parent);
            setTableRowColumn(pageIndex,panelIndex,blockIndex,row,column,nodeTd);
        }
    }

    /**
     * 取得表格列属性
     * @param eTD ETD
     * @return TParm
     */
    private TParm getStyle(ETD eTD){
        TParm parm = new TParm();
        parm.setData("HEIGHT",eTD.getHeight());
        parm.setData("WIDTH",eTD.getWidth());
        parm.setData("X",eTD.getX());
        parm.setData("Y",eTD.getY());
        parm.setData("ALIGNMENT",eTD.getAlignment());
        parm.setData("INSETSHEIGHT",eTD.getInsetsHeight());
        parm.setData("INSETSWIDTH",eTD.getInsetsWidth());
        parm.setData("INSETSX",eTD.getInsetsX());
        parm.setData("INSETSY",eTD.getInsetsY());
        parm.setData("ISVISIBLE", eTD.isVisible());

        return parm;
   }

   /**
    * 的到表格行
    * @param eTR ETR
    * @return TParm
    */
   private TParm getStyle(ETR eTR){
       TParm parm = new TParm();
       parm.setData("X",eTR.getX());
       parm.setData("Y",eTR.getPanel().getY() + eTR.getY());
       parm.setData("HEIGHT",eTR.getHeight());
       parm.setData("WIDTH",eTR.getWidth());
       parm.setData("INSETSHEIGHT",eTR.getInsetsHeight());
       parm.setData("INSETSWIDTH",eTR.getInsetsWidth());
       parm.setData("INSETSX",eTR.getInsetsX());
       parm.setData("INSETSY",eTR.getInsetsY());
       return parm;
   }
   /**
    * 提取表格行列数据
    * @param pageIndex int
    * @param panelIndex int
    * @param blockIndex int
    * @param row int
    * @param column int
    * @param parent Node
    */
   private void setTableRowColumn(int pageIndex,int panelIndex,
                                   int blockIndex,int row,int column,Node parent){
       String value = "";
        for(int tpc = 0;tpc < getTableRowColumnPanelCount(pageIndex,panelIndex,
                blockIndex,row,column);tpc++){
            value = setTableRowColumnPanelData(pageIndex,panelIndex,blockIndex,
                                               row,column,tpc,parent);
        }
        tableEMacroroutineToCDA(pageIndex,panelIndex,blockIndex,column,value);
    }

    /**
     * 的到表格行
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @param tpc int
     * @param parent Node
     * @return String
     */
    private String setTableRowColumnPanelData(int pageIndex,int panelIndex,
                                            int blockIndex,int row,
                                            int column,int tpc,Node parent){
        String value = "";
        if(getTableRowColumnPanelBlockCount(pageIndex,panelIndex,
                                            blockIndex,row,column,tpc) <= 0)
            return "";
        IBlock obj = (IBlock)getTableBlock(pageIndex,panelIndex,
                                           blockIndex,row,column,tpc,0);
        if(obj == null)
            return "";
        while(obj != null){
            setElement(obj,parent);
            value += getTableCellValue(obj);
            obj = obj.getNextTrueBlock();
        }
        return value;
    }

    /**
     * 取得单元格文本
     * @param obj IBlock
     * @return String
     */
    private String getTableCellValue(IBlock obj){
        if(obj instanceof EText &&
           ((EText)obj).getBlockValue() != null &&
           ((EText)obj).getBlockValue().length() != 0){
            return ((EText)obj).getBlockValue();
        }
        return "";
    }


    /**
     * 设置复杂表格数据写入CDA
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param column int
     * @param value String
     */
    private void tableEMacroroutineToCDA(int pageIndex,int panelIndex,
                                         int blockIndex,int column,String value){
        if(value == null ||
           value.length() == 0)
            return;
        ETable table =getETable(pageIndex,panelIndex,blockIndex);
        if(table == null)
            return;
        CTable cTable = table.getCTable();
        if(cTable == null)
            return;
        //String code = cTable.getColumnModel().get(column).getSyntax().getName();
        //appendNodeCDA(code,table.getName(),value);
        String groupName=cTable.getColumnModel().get(column).getGroupName();
        if(groupName==null||groupName.length()==0)
            return;
        String code=cTable.getColumnModel().get(column).getCdaName();
        if(code==null||code.length()==0)
            return;

        appendNodeCDA(code,groupName,value);

    }

    /**
     * 取得线段属性
     * @param line GLine
     * @return TParm
     */
    private TParm getStyle(GLine line){
        TParm parm = new TParm();
        parm.setData("HEIGHT",line.getHeight());
        parm.setData("WIDTH",line.getWidth());
        parm.setData("X",line.getX());
        parm.setData("Y",line.getY());
        return parm;
    }

    /**
     * 取得元素基本属性
     * @param gBlock GBlock
     * @return TParm
     */
    private TParm getStyle(GBlock gBlock){
        TParm parm = new TParm();
        parm.setData("HEIGHT",gBlock.getHeight());
        parm.setData("WIDTH",gBlock.getWidth());
        parm.setData("X",gBlock.getX());
        parm.setData("Y",gBlock.getY());

        //System.out.println("=====gBlock线色====="+gBlock.getBorderColor());
        //System.out.println("=====gBlock色====="+gBlock.getColor());
        //System.out.println("=====gBlock是宽度====="+gBlock.getBorderWidth());
        //System.out.println("=====gBlock是否显示====="+gBlock.isVisible());
        parm.setData("ISVISIBLE",gBlock.isVisible());
        return parm;
    }

    /**
     * 取得图片属性
     * @param eImage EImage
     * @return TParm
     */
    private TParm getStyle(EImage eImage){
        TParm parm = new TParm();
        parm.setData("HEIGHT",eImage.getHeight());
        parm.setData("WIDTH",eImage.getWidth());
        parm.setData("X",eImage.getX());
        parm.setData("Y",eImage.getY());
        parm.setData("BORDERVISIBLE",eImage.isBorderVisible());
        return parm;
    }

    /**
     * 提取图片信息
     * @param eImage EImage
     * @param parent Node
     */
    private void setEImageData(EImage eImage,Node parent){
        Node nodeEImage = XMLElement("EIMAGE",getStyle(eImage),parent);
        for(int i = 0;i < eImage.size();i++){
            GBlock gBlock = eImage.get(i);
            if(gBlock instanceof GLine){
                XMLElement("GLINE",getStyle((GLine)gBlock),nodeEImage);
                continue;
            }
            XMLElement("GBLOCK",getStyle(gBlock),nodeEImage);
        }
    }

    private TParm getStyle(EText text){
        TParm parm = new TParm();
        parm.setData("TYPE",text.getObjectType());
        parm.setData("X",text.getX());
        parm.setData("Y",text.getPanel().getY() + text.getY());
        parm.setData("Width",text.getWidth());
        parm.setData("Position",text.getPosition());
        //有异常情况生成xml的,也可正常保存jhw
		try {
			// FontName->family
			parm.setData("family", text.getFontName());
			// FontSize->size
			parm.setData("size", text.getFontSize());
			// style还有些问题
			parm.setData("style", ((DStyle) text.getStyle()).getFont()
					.getStyle());
			parm.setData("Length", text.getLength());
			parm.setData("Bold", text.isBold());
			parm.setData("Italic", text.isItalic());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return parm;
    }

    /**
     * 设置元素相关信息
     * @param block IBlock
     * @param parent Node
     */
    private void setElement(IBlock block,Node parent) {
        XMLElementCDA(block);
        if(block instanceof EMacroroutine && ((EMacroroutine)block).isPic()){
            //((EMacroroutine) block).getName()
            XMLElement("VPIC",
                           ((VPic)((EMacroroutine) block).getModel().getMVList().get(0).get(0)).getPictureName(),
                           getStyle((EMacroroutine) block),parent);
            return;
        }
        if(block instanceof EImage){
            setEImageData((EImage)block,parent);
            return;
        }
        if(block.getObjectType() == EComponent.TEXT_TYPE){

            XMLElement("TEXT",
                       ((EText)block).getString(),
                       getStyle((EText)block),parent);
        }
        if (!(block instanceof EFixed))
            return;
        switch (block.getObjectType()) {
        case EComponent.CAPTURE_TYPE:
            if (((ECapture) block).getCaptureType() == 1)
                return;
            /**XMLElement(((EFixed) block).getName(),
                       ((ECapture) block).getValue(),
                       getStyle((EFixed) block),parent);**/
            return;
        case EComponent.CHECK_BOX_CHOOSE_TYPE:
            XMLElement(((EFixed) block).getName(),
                       ((ECheckBoxChoose) block).isChecked() ? "是" : "否",
                       getStyle((EFixed) block),parent);
            return;
        case EComponent.TEXTFORMAT_TYPE:
            setTextFormatElement(block,parent);
            return;

        default:
            XMLElement(((EFixed) block).getName(),
                       block.getBlockValue(),
                       getStyle((EFixed) block),parent);
            return;
        }
    }

    /**
     * 下拉框设置编码及描述
     * @param block IBlock
     * @param parent Node
     */
    private void setTextFormatElement(IBlock block,Node parent){
        Node node = XMLElement(((ETextFormat) block).getName(),getStyle((ETextFormat) block),parent);
        XMLElement("DESC",block.getBlockValue(),new TParm(),node);
        XMLElement("CODE",((ETextFormat) block).getCode(),new TParm(),node);
    }

    /**
     * 取得下拉框属性
     * @param eTextFormat ETextFormat
     * @return TParm
     */
    private TParm getStyle(ETextFormat eTextFormat){
        TParm parm = getStyle((EFixed)eTextFormat);
        parm.setData("CODE_SYSTEM",eTextFormat.getData());
        return parm;
    }

    /**
     * 取得宏属性
     * @param eMacroroutine EMacroroutine
     * @return TParm
     */
    private TParm getStyle(EMacroroutine eMacroroutine){
        TParm parm = new TParm();
        String[] fixGroup = eMacroroutine.toString().split(",");
        for(int i = 0;i<fixGroup.length;i++){
            if(!fixGroup[i].startsWith("start")&&
               !fixGroup[i].startsWith("end"))
                continue;
            String name = fixGroup[i].substring(0,fixGroup[i].indexOf("="));
            String value = fixGroup[i].substring(fixGroup[i].indexOf("=") + 1,fixGroup[i].length());
            parm.setData(name,value);
        }
        String style = eMacroroutine.getStyle().toString();
        style = style.substring(style.indexOf("[") + 1,style.length() - 1);
        String[] fixGrStyoup =style.split(",");
        for(int i = 0;i<fixGrStyoup.length;i++){
            String name = fixGrStyoup[i].substring(0,fixGrStyoup[i].indexOf("="));
            String value = fixGrStyoup[i].substring(fixGrStyoup[i].indexOf("=") + 1,fixGrStyoup[i].length());
            parm.setData(name,value);
        }
        parm.setData("TYPE",13);
        parm.setData("X",eMacroroutine.getX());
        parm.setData("Y",eMacroroutine.getPanel().getY() + eMacroroutine.getY());
        parm.setData("WIDTH",eMacroroutine.getWidth());
        parm.setData("HEIGHT",eMacroroutine.getHeight());
        return parm;
    }

    /**
     * 取得固定文本属性
     * @param fix EFixed
     * @return TParm
     */
    private TParm getStyle(EFixed fix){
        TParm parm = new TParm();
        String[] fixGroup = fix.toString().split(",");
        for(int i = 0;i<fixGroup.length;i++){
            if(!fixGroup[i].startsWith("start")&&
               !fixGroup[i].startsWith("end"))
                continue;
            String name = fixGroup[i].substring(0,fixGroup[i].indexOf("="));
            String value = fixGroup[i].substring(fixGroup[i].indexOf("=") + 1,fixGroup[i].length());
            parm.setData(name,value);
        }
        String style = fix.getStyle().toString();
        style = style.substring(style.indexOf("[") + 1,style.length() - 1);
        String[] fixGrStyoup =style.split(",");
        for(int i = 0;i<fixGrStyoup.length;i++){
            String name = fixGrStyoup[i].substring(0,fixGrStyoup[i].indexOf("=") );
            String value = fixGrStyoup[i].substring(fixGrStyoup[i].indexOf("=") + 1,fixGrStyoup[i].length());
            parm.setData(name,value);

        }
        parm.setData("TYPE",fix.getObjectType());
        parm.setData("X",fix.getX());
        parm.setData("Y",fix.getPanel().getY() + fix.getY());
        return parm;
    }

    /**
     * 取得页数
     * @return int
     */
    private int getPageCount(){
        return getPageManager().getComponentList().size();
    }

    /**
     * 取得页面板个数
     * @param pageIndex int
     * @return int
     */
    private int getPagePanelCount(int pageIndex){
        return getEPage(pageIndex).getComponentList().size();
    }

    /**
     * 取得页面板元素个数
     * @param pageIndex int
     * @param panelIndex int
     * @return int
     */
    private int getPagePanelBlockCount(int pageIndex,int panelIndex){
        return getEPanel(pageIndex,panelIndex).getComponentList().size();
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
     * 的到表格行数
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
     * 得到表格行列面板数
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
     * 得到表格行列面板元素个数
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
     * 得到也管理器
     * @return MPage
     */
    private MPage getPageManager(){
        return word.getPM().getPageManager();
    }
    /**
     * 的到页
     * @param pageIndex int
     * @return EPage
     */
    private EPage getEPage(int pageIndex){
        return getPageManager().get(pageIndex);
    }
    /**
     * 得到表格列
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @param column int
     * @return ETD
     */
    private ETD getETD(int pageIndex,int panelIndex,int blockIndex ,int row, int column){
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row).
                get(column);
    }

    /**
     * 得到表格行
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @param row int
     * @return ETR
     */
    private ETR getETR(int pageIndex,int panelIndex,int blockIndex ,int row){
        return ((ETable)getIBlock(pageIndex,panelIndex,blockIndex)).get(row);
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
     * 得到表格
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return ETable
     */
    private ETable getETable(int pageIndex, int panelIndex,int blockIndex){
        return (ETable)getIBlock(pageIndex,panelIndex,blockIndex);
    }

    /**
     * 得到基础元素
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return IBlock
     */
    private IBlock getIBlock(int pageIndex, int panelIndex,int blockIndex){
        return getEPanel(pageIndex,panelIndex).get(blockIndex);
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
     * XML文件根节点
     * @return Node
     */
    private Node XMLRoot(){
        Element element = XMLDoc.createElement("JAVAHIS-EMR");
        XMLDoc.appendChild(element);
        return element;
    }

    /**
     * 设置CDA根节点
     */
    private void XMLRootCDA(){
        ProcessingInstruction processingInstruction = XMLDocCDA.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"doc1_20110607.xsl\"");
        XMLDocCDA.appendChild(processingInstruction);
        Element element = XMLDocCDA.createElement(CDA_INDEX);
        XMLDocCDA.appendChild(element);
    }

    /**
     * 添加CDA节点
     * @param name String
     * @param groupName String
     * @param value String
     */
    private void appendNodeCDA(String name,String groupName,String value){
        TParm parm = getDataInfo(name,groupName);
//        System.out.println("添加CDA节点---------------"+parm);
        if(parm == null)
            return;
        /*Node node = getSectionNode(parm,groupName);*/
        Node node = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
        Element elementC = XMLDocCDA.createElement(name);
        CDATASection cDATASection = XMLDocCDA.createCDATASection(value);
        elementC.appendChild(cDATASection);
        node.appendChild(XMLDocCDA.createComment(parm.getValue("DATA_DESC",0)));
        node.appendChild(elementC);
    }

    /**
     * 找出大分类节点
     * @param parm TParm
     * @param sectionName String
     * @return Node
     */
    private Node getSectionNode(TParm parm,String sectionName){
        if(parm == null)
            return null;
        Node root = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
        NodeList nodeList = root.getChildNodes();
        int size = nodeList.getLength();
        for(int i = 0;i < size;i++){
            if(!nodeList.item(i).getNodeName().equals(sectionName))
                continue;
            return nodeList.item(i);
        }
        Node node = XMLDocCDA.createElement(sectionName);
        root.appendChild(XMLDocCDA.createComment(parm.getValue("GROUP_DESC",0)));
        root.appendChild(node);
        return node;
    }

    /**
     * 的到大分类代码
     * @param dataCode String
     * @param groupCode String
     * @return TParm
     */
    private TParm getDataInfo(String dataCode,String groupCode){
        String SQL = " SELECT GROUP_DESC,DATA_DESC "+
                     " FROM EMR_CLINICAL_DATAGROUP "+
                     " WHERE DATA_CODE = '"+dataCode+"' "+
                     " AND   GROUP_CODE = '"+groupCode+"' ";
//        System.out.println("---------大分类代码--------"+SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        if(parm.getErrCode() < 0)
            return null;
        if(parm.getCount() <= 0)
            return null;
        return parm;
    }

    /**
     * 设置CDA结点值
     * @param block IBlock
     */
    private void XMLElementCDA(IBlock block){
//        System.out.println("======XMLElementCDA 类型是====="+block.getObjectType());
        //图区元素处理;
        if(block.getObjectType()==EComponent.PIC_TYPE){
            //根节点
             //Node node = XMLDocCDA.getElementsByTagName(CDA_INDEX).item(0);
             this.setPicInfo((EPic) block);
             return;
        }

        if(!(block instanceof EFixed))
            return;
//        System.out.println("======XMLElementCDA1=====");
        if(!((EFixed)block).isIsDataElements())
            return;
//        System.out.println("======XMLElementCDA2=====");
        switch (block.getObjectType()) {
        case EComponent.CAPTURE_TYPE:
            if (((ECapture) block).getCaptureType() == 1)
                return;
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ECapture) block).getValue());
            return;
        case EComponent.CHECK_BOX_CHOOSE_TYPE:
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ECheckBoxChoose) block).isChecked() ? "Y" : "N");
            return;
        case EComponent.TEXTFORMAT_TYPE:
            appendNodeCDA(((ETextFormat) block).getName(),
                          ((EFixed) block).getGroupName(),
                          ((ETextFormat) block).getCode());
            return;
        case EComponent.MICRO_FIELD_TYPE:
            appendNodeCDA(((EMicroField) block).getName(),
                          ((EFixed) block).getGroupName(),
                          (((EMicroField) block).getCode() == null ||
                           ((EMicroField) block).getCode().length() == 0)?
                          ((EMicroField) block).getText():
                          ((EMicroField) block).getCode());
            return;

        default:
            appendNodeCDA(((EFixed) block).getName(),
                          ((EFixed) block).getGroupName(),
                          block.getBlockValue());
            return;
        }
    }

    /**
     * 写入XML空值节点
     * @param name String
     * @param attributes TParm
     * @param parent Node
     * @return Node
     */
    private Node XMLElement(String name,TParm attributes,Node parent){
        try{
            Element element = XMLDoc.createElement(name);
            parent.appendChild(element);
            String names[] = attributes.getNames();
            for (int i = 0; i < names.length; i++)
                element.setAttribute(names[i], attributes.getValue(names[i]));
            return element;
        }catch(Exception ex){
            ex.printStackTrace();
            out("name = "+name);
            return null;
        }
    }

    /**
     * 写入XML有值节点
     * @param name String
     * @param value String
     * @param attributes TParm
     * @param parent Node
     * @return Node
     */
    private Node XMLElement(String name,String value,TParm attributes,Node parent){
        try{
           //System.out.println("======name======"+name);
            Element element = XMLDoc.createElement(name);
            CDATASection cDATASection = XMLDoc.createCDATASection(value);
            element.appendChild(cDATASection);
            parent.appendChild(element);
            String names[] = attributes.getNames();
            for (int i = 0; i < names.length; i++)
                element.setAttribute(names[i], attributes.getValue(names[i]));
            return element;
        }catch(Exception ex){
            ex.printStackTrace();
            out("name = "+name + ";value = "+value);
            return null;
        }
    }

    /**
     * 生成XML文件
     * @return String
     */
    private String toXMLString() {
        StringWriter pw=new StringWriter();
        try{
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,ENCODING);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            DOMSource source = new DOMSource(XMLDoc);
            StreamResult result = new StreamResult(pw);
            transformer.transform(source,result);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return pw.toString();
    }

    /**
     * 生成XMLCDA文件
     * @return String
     */
    private String toXMLStringCDA() {
        StringWriter pw = new StringWriter();
        try{
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,ENCODING);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            DOMSource source = new DOMSource(XMLDocCDA);
            StreamResult result = new StreamResult(pw);
            transformer.transform(source,result);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return pw.toString();
    }

    /**
     * LOG
     * @param message String
     */
    private void out(String message){
        System.out.println(message);
    }

}
