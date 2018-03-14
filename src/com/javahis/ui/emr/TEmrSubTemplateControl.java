package com.javahis.ui.emr;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.wcomponent.ui.popup.PopupWindows;
import com.dongyang.wcomponent.ui.popup.PopupWindows.PopButton;


/**
 *
 * @author whaosoft
 *
 */
public class TEmrSubTemplateControl extends TControl{


	/**  */
	private TTree tree;
	private TTreeNode tn;
    private TWord pword;

	/**
	 *
	 */
	public void onInit() {

		super.onInit();

		TParm inParm = (TParm)this.getParameter();

		this.initSubTemlate(inParm);

		initEvent();

		this.loadSubTemplate(inParm);
	}

	/**
	 *
	 * @param inParm
	 */
    private void initSubTemlate(TParm inParm){

		tree = (TTree) this.getComponent("tTree_0");
		tn = tree.getRoot();
		pword = (TWord) inParm.getData("TWORD");
    }

	/**
	 * ע���¼�
	 */
	private void initEvent() {

		// ����ѡ������Ŀ
		addEventListener(tree.getTag() + "->" + TTreeEvent.DOUBLE_CLICKED,"onTreeDoubled");
	}

	/**
	 *
	 * @param inParm
	 */
	private void loadSubTemplate(TParm inParm){

		tree.setPics(inParm.getValue("pics"));

		TTreeNode tn = tree.getRoot();

		tn.removeAllChildren();

		this.doLoadSubTemplate();

        tree.update();
	}

    /**
     *
     * @param rootParam
     * @param nodesParam
     */
	private void doLoadSubTemplate(){

       TParm rootParam = this.getSubTemplateRootNodeData();
       tn.setText(rootParam.getValue("PHRASE_CODE", 0));
       tn.setType("Root");
       tn.setID(rootParam.getValue("CLASS_CODE", 0));

       TParm nodesParam = this.getSubTemplateNodes();

       if (nodesParam == null) return;

       int nodesCount = nodesParam.getCount();
       for (int i = 0; i < nodesCount; i++) {
           TParm temp = nodesParam.getRow(i);
           //���ݽڵ����������Ƿ�ΪĿ¼�ڵ�
           String noteType = "1";
           //Ҷ�ڵ�(�ǽṹ��Ƭ��)
           if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
               noteType = "4";
           }
           //�����½ڵ�
           TTreeNode PhraseClass = new TTreeNode("PHRASECLASS"+ i, noteType);
           //��ERM������Ϣ���õ��ڵ㵱��
           PhraseClass.setText(nodesParam.getValue ("PHRASE_CODE", i));
           PhraseClass.setID(nodesParam.getValue("CLASS_CODE", i));

           //������������
           PhraseClass.setData(temp);

           //��һ���Ľڵ��������
           if (nodesParam.getValue("PARENT_CLASS_CODE",i).equals("ROOT")) {
           	tn.addSeq(PhraseClass);
           }
           //��������Ľڵ������Ӧ�ĸ��ڵ�����
           else {
               if (nodesParam.getValue
                   ("PARENT_CLASS_CODE", i).length() != 0) {
                   //����Ҷ�ڵ㣨Ƭ��ڵ㣩,�ǵ�ǰ���ҵļ��룻

                   //��Ҷ�ڵ�
                   if(nodesParam.getValue("LEAF_FLG", i).equals("Y")){
                       //����Ƭ�����ڵ㣬�����Ƭ���ڴ˲�����
                        if(nodesParam.getValue("MAIN_FLG", i).equals("Y")){
                       	 tn.findNodeForID(nodesParam.getValue(
                           "PARENT_CLASS_CODE", i)).add(PhraseClass);
                        }
                       //
                   }else{
                   	tn.findNodeForID(nodesParam.getValue(
                           "PARENT_CLASS_CODE", i)).add(PhraseClass);
                   }
               }
           }
       }
	}

	/**
	 *
	 * @return
	 */
	private TParm getSubTemplateRootNodeData(){
       TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE," +
       		"LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME " +
       		"FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL"));
       return result;
	}

	/**
	 *
	 * @return
	 */
   private TParm getSubTemplateNodes() {
       TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG," +
       		"MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME " +
       		"FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ"));
       return result;
   }

	/**
	 * �������ݿ��������
	 *
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �����
	 *
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {

		TTreeNode node = (TTreeNode) parm;

		if( null==node ) return;

		//
		if( "4".equals( node.getType() ) ){

			doOpenSubTempldateNode(node);
		}
	}

    /**
     *
     * @param node
     */
	private void doOpenSubTempldateNode(TTreeNode node) {

		TWord tw = this.getWord(node);

        this.initPopup(tw,node);
    }

    /**
     *
     * @param tw
     * @param node
     */
	private void initPopup(final TWord tw,final TTreeNode node){

		PopupWindows pop = PopupWindows.showPopup(580,430,290,120,130,tw);
		if( null==pop )return;
		pop.jb.setContentAreaFilled(false);
		pop.jb.setText("����");
		pop.jb.addMouseListener( new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {

                //System.out.println("~~~~~~~~~");
				PopButton pb = (PopButton)e.getComponent();
				pb.closePop();

				//
				onFetchBack(tw,node);
			}
		});
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	private TWord getWord(TTreeNode node ){

		TWord tw = new TWord();

        TParm dataParm = (TParm) node.getData();

        //��ģ���ļ�
        String isPhrase=dataParm.getValue("LEAF_FLG");

        if(isPhrase.equals("Y")){

            if (!tw.onOpen(dataParm.getValue("FILE_PATH"),
                                  dataParm.getValue("FILE_NAME"), 2, false)) {
            	return tw;
            }
            tw.onEditWord();
            //���ò��ɱ༭
            tw.setCanEdit(true);
        }

        return tw;
	}

    /**
     *
     * @param tw
     * @param node
     */
	private void onFetchBack(TWord tw,TTreeNode node){

        TParm dataParm = (TParm) node.getData();

        String phraseFilePath = dataParm.getValue("FILE_PATH");
        String phraseFileName = dataParm.getValue("FILE_NAME");

        //
        EComponent com=pword.getFocusManager().getFocus();
        ECapture firstCapture=null;
        if(com!=null){
           if(com instanceof ECapture)
           {
               firstCapture=(ECapture)com;
           }
        }
        pword.getFocusManager().onInsertFile(phraseFilePath, phraseFileName,2, false);
        if(firstCapture!=null){
            firstCapture.setFocusLast();
            firstCapture.deleteChar();
        }
        pword.update();
	}

}
