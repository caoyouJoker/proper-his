package com.javahis.ui.dev;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETable;
import com.dongyang.ui.TWord;

/**
 * <p>
 * Title: �豸������¼��
 * </p>
 * 
 * <p>
 * Description: �豸������¼��
 * </p>
 * <p>
 * Copyright: BlueCore 2015
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceRecordControl extends TControl {
	
	/**
     * WORD����
     */
    private TWord word;
    
    private TParm parm;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        this.parm = (TParm) obj;
//        System.out.println("mmmmparm:"+parm);
		this.word = this.getTWord("WORD");
		onOpenTemplet();
//		//����
//        word.setShowZoomComboTag("ShowZoom");
//        //����
//        word.setFontComboTag("ModifyFontCombo");
//        //����
//        word.setFontSizeComboTag("ModifyFontSizeCombo");
//        //���
//        word.setFontBoldButtonTag("FontBMenu");
//        //б��
//        word.setFontItalicButtonTag("FontIMenu");
//        //ȡ���༭
//        this.word.setCanEdit(false);
	}
	
    /**
     * ��ģ��
     */
    public void onOpenTemplet() {
    	//ȡ���༭
//        this.word.setCanEdit(false);
//        this.word.findCapture("M");
    	TParm devMesParm = new TParm();//�豸��Ϣ
    	TParm devMaiParm = new TParm();//�豸ά��ϸ��
    	devMesParm = this.parm.getParm("DEV_MES_PARM");
//    	System.out.println("DEV_MES_PARM:"+devMesParm);
    	devMaiParm = this.parm.getParm("DEV_MAI_PARM");
    	String moreFlg = this.parm.getValue("MORE_FLG");
        if(moreFlg.equals("N")){//�����豸ά������
        	this.word.onOpen("JHW\\�豸ά����¼ģ��", "�����豸ά����¼ģ��", 2, false);
        	this.word.findFixed("DEV_CHN_DESC").setFocus();
        	this.word.findFixed("DEV_CHN_DESC").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEV_CHN_DESC",0));//�豸����
        	this.word.findFixed("DEV_CODE").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEV_CODE",0));//�豸���
        	this.word.findFixed("DEPT_CHN_DESC").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEPT_CHN_DESC",0));//ʹ�ÿ���
        	this.word.findFixed("DEV_CODE_DETAIL").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEV_CODE_DETAIL",0));//�豸���к�(SN)
        	this.word.findFixed("SPECIFICATION").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("SPECIFICATION",0));//����ͺ�
        	this.word.findFixed("MAN_NATION").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("MAN_NATION",0));//����Ʒ��
        	this.word.findFixed("M").setFocusLast();
        	this.word.pasteString("\r\n");  
        	ETable eTable =  (ETable)this.word.findObject("TABLE", EComponent.TABLE_TYPE);
        	for(int i=0;i<devMaiParm.getCount();i++){
        		int n = i+1;
        		eTable.setTextAt(i, 0, ""+n);
        		eTable.setTextAt(i, 1, devMaiParm.getValue("MTN_DETAIL_DESC", i));
        		if(i==devMaiParm.getCount()-1){
        			break;
        		}
        		ETR etr = eTable.get(i);
        		ETR etr2 = etr.clone(eTable);
        		eTable.add(etr2);
        	}
        	eTable.update();
//        	this.word.findFixed("DATE").setFocusLast();DEPT_USER
//        	this.word.pasteString(this.parm.getValue("DATE"));
        	this.word.findFixed("DEPT_USER").setFocusLast();
        	this.word.pasteString(Operator.getName());
        	
        }else{//����豸ά������
        	this.word.onOpen("JHW\\�豸ά����¼ģ��", "����豸ά����¼ģ��", 2, false);
        	this.word.findFixed("DEV_CHN_DESC").setFocus();
        	this.word.findFixed("DEV_CHN_DESC").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEV_CHN_DESC",0));//�豸����
        	this.word.findFixed("MAN_NATION").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("MAN_NATION",0));//����Ʒ��
        	this.word.findFixed("SPECIFICATION").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("SPECIFICATION",0));//����ͺ�
        	this.word.findFixed("DEPT_CHN_DESC").setFocusLast();
        	this.word.pasteString(devMesParm.getValue("DEPT_CHN_DESC",0));//ʹ�ÿ���
        	this.word.findFixed("N").setFocusLast();
        	this.word.pasteString("\r\n");  
        	ETable eTableDetail =  (ETable)this.word.findObject("TABLE_DETAIL", EComponent.TABLE_TYPE);
        	int n2=1;
        	int n3=0;
//        	for(int i=0;i<devMesParm.getCount();i+=2){
////        		int n = i+1;
//        		eTableDetail.setTextAt(n3, 0, ""+n2);
//        		eTableDetail.setTextAt(n3, 1, devMesParm.getValue("DEV_CODE_DETAIL", i));
//        		n2++;
//        		eTableDetail.setTextAt(n3, 2, ""+n2);
//        		eTableDetail.setTextAt(n3, 3, devMesParm.getValue("DEV_CODE_DETAIL", i+1));
//        		n2++;
//        		n3++;
//        		if(i==devMesParm.getCount()-2){
//        			break;
//        		}
//        		ETR etr = eTableDetail.get(n3-1);
//        		ETR etr2 = etr.clone(eTableDetail);
//        		eTableDetail.add(etr2);
//        	}
        	if(devMesParm.getCount()%2==0){
        		for(int i=0;i<devMesParm.getCount();i+=2){
//            		int n = i+1;
            		eTableDetail.setTextAt(n3, 0, ""+n2);
            		eTableDetail.setTextAt(n3, 1, devMesParm.getValue("DEV_CODE_DETAIL", i));
            		n2++;
            		eTableDetail.setTextAt(n3, 2, ""+n2);
            		eTableDetail.setTextAt(n3, 3, devMesParm.getValue("DEV_CODE_DETAIL", i+1));
            		n2++;
            		n3++;
            		if(i==devMesParm.getCount()-2){
            			break;
            		}
            		ETR etr = eTableDetail.get(n3-1);
            		ETR etr2 = etr.clone(eTableDetail);
            		eTableDetail.add(etr2);
            	}
        	}else{
        		for(int i=0;i<devMesParm.getCount()-1;i+=2){
//            		int n = i+1;
            		eTableDetail.setTextAt(n3, 0, ""+n2);
            		eTableDetail.setTextAt(n3, 1, devMesParm.getValue("DEV_CODE_DETAIL", i));
            		n2++;
            		eTableDetail.setTextAt(n3, 2, ""+n2);
            		eTableDetail.setTextAt(n3, 3, devMesParm.getValue("DEV_CODE_DETAIL", i+1));
            		n2++;
            		n3++;
            		if(i==devMesParm.getCount()-2){
            			break;
            		}
            		ETR etr = eTableDetail.get(n3-1);
            		ETR etr2 = etr.clone(eTableDetail);
            		eTableDetail.add(etr2);
            	}
        		eTableDetail.setTextAt(n3, 0, ""+n2);
        		eTableDetail.setTextAt(n3, 1, devMesParm.getValue("DEV_CODE_DETAIL", devMesParm.getCount()-1));
        		n2++;
        		eTableDetail.setTextAt(n3, 2, "");
        		eTableDetail.setTextAt(n3, 3, "");
        	}
        	eTableDetail.update();
        	this.word.findFixed("M").setFocusLast();
        	this.word.pasteString("\r\n");  
        	ETable eTable =  (ETable)this.word.findObject("TABLE", EComponent.TABLE_TYPE);
        	for(int i=0;i<devMaiParm.getCount();i++){
        		int n1 = i+1;
        		eTable.setTextAt(i, 0, ""+n1);
        		eTable.setTextAt(i, 1, devMaiParm.getValue("MTN_DETAIL_DESC", i));
        		if(i==devMaiParm.getCount()-1){
        			break;
        		}
        		ETR etr = eTable.get(i);
        		ETR etr2 = etr.clone(eTable);
        		eTable.add(etr2);
        	}
        	eTable.update();
//        	this.word.findFixed("DATE").setFocusLast();
//        	this.word.pasteString(this.parm.getValue("DATE"));
        	this.word.findFixed("DEPT_USER").setFocusLast();
        	this.word.pasteString(Operator.getName());
        }
    }
    
    public void onSave(){
//    	this.parm.getValue("getCaptureValueArray");
////    	this.word.findCapture(name)
    	String explain = this.word.getCaptureValue("EXPLAIN");//����˵��
    	String m_date = this.word.getCaptureValue("M_DATE");//��������
    	String use_time = this.word.getCaptureValue("USE_TIME");//������ʱ
    	String mar_result = this.word.getCaptureValue("RESULT");//�������
    	if(mar_result.equals("����")){
    		mar_result = "0";
    	}else if(mar_result.equals("�쳣")){
    		mar_result = "1";
    	}else{
    		this.messageBox("�������ѡ�����!");
    		return;
    	}
    	String opt_user = this.word.getCaptureValue("OPT_USER");//ά������ʦ
    	String userSql = "SELECT USER_ID FROM SYS_OPERATOR WHERE USER_ID='"+opt_user+"' ";
    	TParm userParm = new TParm(TJDODBTool.getInstance().select(userSql));
    	if(userParm.getCount()<=0){
    		this.messageBox("ά������ʦ��д����");
    		return;
    	}
    	TParm devMesParm = new TParm();//�豸��Ϣ
//    	TParm devMaiParm = new TParm();//�豸ά��ϸ��
    	devMesParm = this.parm.getParm("DEV_MES_PARM");
//    	System.out.println("DEV_MES_PARM:"+devMesParm);
//    	devMaiParm = this.parm.getParm("DEV_MAI_PARM");
    	String moreFlg = this.parm.getValue("MORE_FLG");
    	TParm parm = new TParm();
    	String mtn_no = SystemTool.getInstance().getNo("ALL", "DEV",
                "MTN_NO", "MTN_NO");
    	SimpleDateFormat sdf1=new SimpleDateFormat("yyyy");
    	SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMdd");
    	Date date=new Date();  
    	String datePath=sdf1.format(date);
    	String path = "JHW\\�豸������¼\\";
    	path += datePath;
    	String fileName = "";
    	if(moreFlg.equals("N")){
    		fileName = devMesParm.getValue("DEV_CODE", 0)
    				+"_"
    				+devMesParm.getValue("DEV_CODE_DETAIL",0)
    				+"_"
    				+sdf2.format(date);
    	}else{
    		fileName = devMesParm.getValue("DEV_CODE", 0)
    				+"_"
    				+mtn_no
    				+"_"
    				+sdf2.format(date);
    	}
    	for(int i=0;i<devMesParm.getCount();i++){
    		parm.addData("MTN_NO", mtn_no);//ά�����
    		parm.addData("SEQ", i+1);//˳���
    		parm.addData("DEPT_CODE", devMesParm.getValue("DEPT_CODE", i));//���ұ���
    		parm.addData("DEV_CODE", devMesParm.getValue("DEV_CODE", i));//�豸����
    		parm.addData("MTN_KIND", devMesParm.getValue("MTN_KIND", i));//ά������(0_����,1_�ʿ�,2_����)
    		parm.addData("MTN_TYPE_CODE", devMesParm.getValue("MTN_TYPE_CODE", i));//ά�����ʹ���
    		parm.addData("MTN_DATE", m_date);//ά������
    		parm.addData("MTN_HOUR", use_time);//ά����ʱ(��λ:Сʱ)
    		parm.addData("MTN_ENGINEER", opt_user);//ά������ʦ
    		parm.addData("MTN_RESULT", mar_result);//ά�����(0_����,1_�쳣)
    		parm.addData("MTN_EVALUATION", explain);//ά�����ۼ�˵��
    		parm.addData("FILE_PATH", path);//ά����¼�ļ�·��
    		parm.addData("FILE_NAME", fileName);//ά����¼�ļ���
    		parm.addData("OPT_USER", Operator.getID());
    		parm.addData("OPT_DATE", "");
    		parm.addData("OPT_TERM", Operator.getIP());
    		parm.addData("DEV_CODE_DETAIL", devMesParm.getValue("DEV_CODE_DETAIL", i));
    		String sql = "SELECT MTN_CYCLE,MTN_UNIT FROM DEV_MAINTENANCEM WHERE"
    				+ " DEV_CODE='"
    				+devMesParm.getValue("DEV_CODE", i)
    				+ "' AND MTN_KIND='"
    				+devMesParm.getValue("MTN_KIND", i)
    				+ "' AND MTN_TYPE_CODE='"
    				+devMesParm.getValue("MTN_TYPE_CODE", i)
    				+ "' ";
    		TParm mtnParm = new TParm(TJDODBTool.getInstance().select(sql));
    		parm.addData("NEXT_MTN_DATE", 
    					this.getNextMtnDate(devMesParm.getValue("NEXT_MTN_DATE", i),
    							mtnParm.getInt("MTN_CYCLE",0),
    							mtnParm.getInt("MTN_UNIT",0)));
    		parm.setCount(i+1);
    	}
    	
    	if(parm.getCount() <= 0){
			this.messageBox("û��Ҫ��������ݣ�");
			return;
		}
//    	System.out.println("parm>>>"+parm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onInsertMaintenanceRecord", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			err(result.getErrText());
			return;
		}
//    	MTN_NO,SEQ,DEPT_CODE,DEV_CODE,MTN_KIND,MTN_TYPE_CODE,MTN_DATEMTN_HOUR
//    	MTN_ENGINEER,MTN_RESULT,MTN_EVALUATION,FILE_PATH,FILE_NAME
//    	OPT_USER,OPT_DATE,OPT_TERM

		this.word.getFileManager().setMessageBoxSwitch(false);
		this.word.getFileManager().onSaveAsReport(path, fileName, 3);
		onPrint();
		this.messageBox("����ɹ�");
//		this.closeWindow();
    }
    
    /**
     * ��ȡ�´�ά��ʱ��
     * @param nowMtnDate ����ά������
     * @param mtnCycle ά������
     * @param mtnUnit ά�����ڵ�λ(0,��;1,��;2,��;3,��)
     * @return
     */
    public String getNextMtnDate(String nowMtnDate,int mtnCycle,int mtnUnit){
    	String nextMtnDate = "";
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");//Сд��mm��ʾ���Ƿ���  
    	try {
			Date date=sdf.parse(nowMtnDate.substring(0, 10).replace("-", "/"));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			switch (mtnUnit) {
			case 0:
				cal.add(Calendar.YEAR, mtnCycle);
				break;
			case 1:
				cal.add(Calendar.MONTH, mtnCycle);
				break;
			case 2:
				cal.add(Calendar.DATE, mtnCycle*7);
				break;
			case 3:
				cal.add(Calendar.DATE, mtnCycle);
				break;
			}
			nextMtnDate = sdf.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return nextMtnDate;
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
    	this.word.onPreviewWord();
    	this.word.print();
    	this.closeWindow();
    }
    
    /**
     * �õ�WORD����
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }
	
}
