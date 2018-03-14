package com.javahis.ui.emr;

import java.io.File;
import java.io.FileOutputStream;

import jdo.sys.SystemTool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.adm.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;   
import com.itextpdf.text.Document; 
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Image;   
import com.itextpdf.text.pdf.PdfWriter; 
import com.itextpdf.text.Rectangle;


public class SWTshellPDF extends Shell {
	OleFrame olef = new OleFrame(getShell(), SWT.NONE);	
	OleClientSite  site  =  new OleClientSite(olef,SWT.NONE,"SCANCTRL.ScanCtrlCtrl.1");
	OleAutomation auto = new OleAutomation(site);
	Combo combofbl = new Combo(this, SWT.DROP_DOWN);//�ֱ���
	Combo combosmcc = new Combo(this, SWT.DROP_DOWN);//ɨ��ߴ�
	Combo combojd = new Combo(this, SWT.DROP_DOWN);//��Ƶ��ת�Ƕ�
	Combo combotype = new Combo(this, SWT.DROP_DOWN);//��Ŀ����
	Combo combosub = new Combo(this, SWT.DROP_DOWN);
    Text text = new Text(this, SWT.BORDER);//ҳ��
    Table table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);//��ʾ�ļ�
    String MRNO ="";//������
    String CASENO ="";//�������
    String PDFTYPE ="";//��������
    String typecode ="";//��Ŀ����
    String subclasscode ="";
    
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			TParm returnTParm = new TParm();
			Display display = Display.getDefault();
			SWTshellPDF shell = new SWTshellPDF(display,returnTParm);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public SWTshellPDF(Display display,TParm returnTParm) {		
		super(display, SWT.SHELL_TRIM);
		setBackground(SWTResourceManager.getColor(161,220,230));//���ñ�����ɫ
		//���豸
		Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOpenDevice();
				
			}
		});
		button.setBounds(91, 535, 87, 21);
		button.setText("\u6253\u5F00\u8BBE\u5907");
		//��ʼԤ��
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onStartPerView();
			}
		});
		btnNewButton.setBounds(192, 535, 81, 21);
		btnNewButton.setText("\u5F00\u59CB\u9884\u89C8");
		// ֹͣԤ��
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onStopPreView();
			}
		});
		btnNewButton_1.setBounds(289, 535, 81, 21);
		btnNewButton_1.setText("\u505C\u6B62\u9884\u89C8");
		//����
		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTakePic();
			}
		});
		btnNewButton_2.setBounds(388, 535, 81, 21);
		btnNewButton_2.setText("\u62CD\u7167");
		//�ر� 
		Button btnNewButton_4 = new Button(this, SWT.NONE);
		btnNewButton_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClose();	
			}
		});
		btnNewButton_4.setBounds(486, 535, 81, 21);
		btnNewButton_4.setText("\u5173\u95ED");
		
		combofbl.setBounds(69, 496, 96, 20);//�ֱ���
		//�ֱ���ѡ���¼�
		combofbl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombofbl();	
			}
		});
		combosmcc.setBounds(230, 496, 96, 20);//ɨ��ߴ�
		//ɨ��ߴ�ѡ���¼�
		combosmcc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombosmcc();	
			}
		});
		combojd.setBounds(389, 496, 87, 20);//��Ƶ��ת�Ƕ�
		//��Ƶ��ת�Ƕ�ѡ���¼�
		combojd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombojd();	
			}
		});
//		combojd.setEnabled(false);
		combotype.setBounds(549, 496, 101, 20);//��Ŀ����
		//��Ŀ����ѡ���¼�
		combotype.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombotype();	
			}
		});
		combosub.setBounds(702, 496, 101, 20);
		//ϸ����ѡ���¼�
		combosub.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombosub();	
			}
		});
		text.setBounds(845, 496, 101, 20);//ҳ��
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("����", 9, SWT.NORMAL));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setBounds(20, 502, 45, 14);
		lblNewLabel.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel.setText("\u5206\u8FA8\u7387");
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(173, 502, 54, 14);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel_1.setText("\u626B\u63CF\u5C3A\u5BF8");
		
		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setBounds(332, 502, 54, 14);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel_2.setText("\u65CB\u8F6C\u89D2\u5EA6");
		
		Label lblNewLabel_3 = new Label(this, SWT.NONE);
		lblNewLabel_3.setBounds(489, 502, 54, 14);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel_3.setText("\u9879\u76EE\u7C7B\u578B");
		
		Label lblNewLabel_4 = new Label(this, SWT.NONE);
		lblNewLabel_4.setBounds(657, 502, 54, 14);
		lblNewLabel_4.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel_4.setText("ϸ����");
		
		Label lblNewLabel_5 = new Label(this, SWT.NONE);
		lblNewLabel_5.setBounds(815, 502, 54, 14);
		lblNewLabel_5.setBackground(SWTResourceManager.getColor(161,220,230));
		lblNewLabel_5.setText("\u9875\u7801");
		
		table.setBounds(667, 10, 343, 480);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(339);
		tblclmnNewColumn.setText("\u6587\u4EF6\u540D");
		MRNO = returnTParm.getValue("MR_NO");
		CASENO = returnTParm.getValue("CASE_NO");
		PDFTYPE = returnTParm.getValue("PDF_TYPE");
		//��ʾ����
		createContents();
	    //��ʼ����Ŀ����
	    getDiseaseCode();
	    //��ʼ������
	    text.setText(""+1);
	}
	  /**
	   *�����Ŀ����
	   */
	  public void getDiseaseCode()
	  {
		  TParm result = new TParm();
		  String sql="";
		  if(PDFTYPE.equals("0"))
		  sql =" SELECT CLASS_CODE,CLASS_DESC FROM VEMR_CLASS ";
		  else			  
		  sql = " SELECT A.SEQ AS CLASS_CODE,A.FILE_NAME AS CLASS_DESC FROM EMR_MROLIST A"+
                       " WHERE A.FILE_TYPE = 'ALL'";	  
		  result = new TParm(TJDODBTool.getInstance().select(sql));
		  int count =result.getCount("CLASS_CODE");
		  String items[] = new String[count];
		  for(int i=0;i<count;i++)
			{
			  items[i] =result.getValue("CLASS_DESC",i);
			}
		  combotype.setItems(items);
	  }
		/**
	   *���ϸ����
	   */
	  public void getsubclass(String classcode)
	  {
		  TParm result = new TParm();
		  String sql="";
		  if(PDFTYPE.equals("0"))
		  sql =" SELECT SUBCLASS_CODE,SUBCLASS_DESC FROM EMR_TEMPLET "+
               " WHERE CLASS_CODE ='"+classcode+"'";	  
		  result = new TParm(TJDODBTool.getInstance().select(sql));
		  int count =result.getCount("SUBCLASS_CODE");
		  String items[] = new String[count];
		  for(int i=0;i<count;i++)
			{
			  items[i] =result.getValue("SUBCLASS_DESC",i);
			}
		  combosub.setItems(items);
	  }
	/**
	 * ���豸
	 */
	public void onOpenDevice()
	{
		int[] ids = auto.getIDsOfNames(new String[] {"StartPreview"});
		auto.invoke(ids[0]);
		//��ȡ����ʹ�õķֱ��ʸ���
		int[] idscount = auto.getIDsOfNames(new String[] {"GetResolutionCount"});
		Variant count = auto.invoke(idscount[0]);
//		System.out.println("count============="+count);  
		for(int i=0;i<count.getInt();i++)
		{
		//��ȡ��i���ֱ��ʵĿ�
		int[] ids1 = auto.getIDsOfNames(new String[]{"GetResolutionWidth"});
		int dispIdMember = ids1[0];
        Variant[] rgvarg = new Variant[1]; 
        rgvarg[0] = new Variant(i);
        Variant width = auto.invoke(dispIdMember,rgvarg);
        //��ȡ��i���ֱ��ʵĸ�
        int[] ids2 = auto.getIDsOfNames(new String[]{"GetResolutionHeight"});
		int dispIdMember1 = ids2[0];
        Variant[] rgvarg1 = new Variant[1]; 
        rgvarg1[0] = new Variant(i);
        Variant height = auto.invoke(dispIdMember1,rgvarg1);
        String str=width.getString()+"x"+height.getString();
        //�ֱ��ʸ�ֵ
        combofbl.add(str, i);
		}
		//�ֱ���Ĭ��ֵ
		combofbl.select(0);
        //���÷ֱ���(Ĭ����߷ֱ���)
        int[] Resolution = auto.getIDsOfNames(new String[]{"SetResolution"});
		int dispIdMember2 = Resolution[0];
        Variant[] rgvarg2 = new Variant[1]; 
        rgvarg2[0] = new Variant(0);//Ĭ����߷ֱ���
        auto.invoke(dispIdMember2,rgvarg2);
       //��ȡ����ʹ�õ�ɨ��ߴ����
      	int[] idssize = auto.getIDsOfNames(new String[] {"GetScanSizeCount"});
      	Variant size = auto.invoke(idssize[0]);
      	if(size.getInt()==8){
      	//ɨ��ߴ縳ֵ	
//      	combosmcc.add("All",0);
      	combosmcc.add("A3");
      	combosmcc.add("A4");
      	combosmcc.add("A5");
      	combosmcc.add("A6");
      	combosmcc.add("A7");
//      	combosmcc.add("��Ƭ");
//      	combosmcc.add("���֤");
//      	combosmcc.add("�Զ���");	
      	combosmcc.select(1);
       //����ɨ��ߴ磨Ĭ��A4��
        int[] maxsize = auto.getIDsOfNames(new String[]{"SetScanSize"});
		int dispIdMember3 = maxsize[0];
        Variant[] rgvarg3 = new Variant[1]; 
        rgvarg3[0] = new Variant(1);//Ĭ��A4
        auto.invoke(dispIdMember3,rgvarg3);
      	}
      	else{
      	//ɨ��ߴ縳ֵ		
//      	combosmcc.add("All");
        combosmcc.add("A4");
        combosmcc.add("A5");
        combosmcc.add("A6");
        combosmcc.add("A7");
//        combosmcc.add("��Ƭ");
//        combosmcc.add("���֤");
//        combosmcc.add("�Զ���");	
    	combosmcc.select(0);
    	//����ɨ��ߴ磨Ĭ��A4��
        int[] maxsize = auto.getIDsOfNames(new String[]{"SetScanSize"});
		int dispIdMember3 = maxsize[0];
        Variant[] rgvarg3 = new Variant[1]; 
        rgvarg3[0] = new Variant(0);//Ĭ��A4
        auto.invoke(dispIdMember3,rgvarg3);
      	}
      	//��Ƶ��ת�Ƕȸ�ֵ
      	combojd.add("0��");
      	combojd.add("90��");
      	combojd.add("270��");
      	combojd.add("180��");
      	combojd.select(1);
      //������Ƶ��ת�Ƕ�(Ĭ�ϽǶ�=90��)
      	int[] Rotate = auto.getIDsOfNames(new String[]{"SetVideoRotate"});
 		int dispIdMember4 = Rotate[0];
        Variant[] rgvarg4 = new Variant[1]; 
        rgvarg4[0] = new Variant(1);//Ĭ�ϽǶ�=90��
        auto.invoke(dispIdMember4,rgvarg4);
	}
	/**
	 * ��ʼԤ��
	 */
	public void onStartPerView()
	{
		
		int[] ids = auto.getIDsOfNames(new String[] {"StartPreview"});
		auto.invoke(ids[0]);
		
	}
	/**
	 * ֹͣԤ��
	 */
	public void onStopPreView()
	{
		int[] ids = auto.getIDsOfNames(new String[] {"StopPreview"});
		auto.invoke(ids[0]);
		
	}
	/**
	 * ����
	 */
	public void onTakePic()
	{
		
		if(combofbl.getText().equals(""))
		{
			MessageBox messageBox = new MessageBox(this, SWT.OK);
		        messageBox.setMessage("���ȴ��豸!");
		        messageBox.setText("��ʾ");
		        int response = messageBox.open();
		        if (response == SWT.OK)
	              return;
		}
		if(combotype.getText().equals(""))
		{
			MessageBox messageBox = new MessageBox(this, SWT.OK);
		        messageBox.setMessage("��ѡ����Ŀ����!");
		        messageBox.setText("��ʾ");
		        int response = messageBox.open();
		        if (response == SWT.OK)
	              return;
		}
		if(PDFTYPE.equals("0")){
		if(combosub.getText().equals(""))
		{
			MessageBox messageBox = new MessageBox(this, SWT.OK);
		        messageBox.setMessage("��ѡ��ϸ����!");
		        messageBox.setText("��ʾ");
		        int response = messageBox.open();
		        if (response == SWT.OK)
	              return;
		}
	}
		int jpgval= 36;//����JPGͼƬ�ļ���Ч��,ȡֵ��ΧΪ1��99 
		int[] ids = auto.getIDsOfNames(new String[]{"SetJpegQuality"});//����JPGͼƬ�ļ���Ч��
		int dispIdMember = ids[0];
        Variant[] rgvarg = new Variant[1]; 
        rgvarg[0] = new Variant(jpgval);
        auto.invoke(dispIdMember,rgvarg);
        File file1 =new File("C:\\javaEmc\\"+MRNO+"\\"+CASENO+"\\"+"δ�ύ"+"\\"+"JPG");
        File file2 =new File("C:\\javaEmc\\"+MRNO+"\\"+CASENO+"\\"+"δ�ύ"+"\\"+"PDF");
        if(!file1.exists())
        	file1.mkdirs();
        if(!file2.exists())
        	file2.mkdirs();
       //path1ΪͼƬ�ı���·��			
        String path1= file1.getPath();
       //path2ΪPDF�ı���·��			
        String path2= file2.getPath();
//        System.out.println("path1============="+path1);
//        System.out.println("path2============="+path2); 	
	    //ͼƬ�ļ���
        String filename ="";
        String now = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMddHHmmss"); //�õ���ǰ��ʱ��
        if(PDFTYPE.equals("0"))
        filename = typecode+"_"+subclasscode+"_"+combosub.getText()+"_"+text.getText()+"_"+"P"+now;
        else
        filename = typecode+"_"+combotype.getText()+"_"+text.getText()+"_"+"P";	
	    String jpgname="\\"+filename+".jpg";
	    String pathalljpg= path1+jpgname;
        int[] ids1 = auto.getIDsOfNames(new String[]{"QuickScan"});//����ɨ��ͼƬ
        int dispIdMember1 = ids1[0];
        Variant[] rgvarg1 = new Variant[1]; 
        rgvarg1[0] = new Variant(pathalljpg);
        Variant result = auto.invoke(dispIdMember1,rgvarg1); 	
    	if(result.getBoolean()){
        //PDF�ļ���	
    	String pdfname = "\\"+filename+".pdf";
    	String pathallpdf = path2+pdfname;
    	//��ͼƬת����PDF
    	getImgToPDF(pathalljpg,pathallpdf);     
    	TableItem tableItem = new TableItem(table, SWT.NONE);
    	//������ʾ
    	String pdf =filename;
    	tableItem.setText(pdf);    	    		
    	}    	
    	//�����Զ���1
    	 int cValue = Integer.parseInt(text.getText())+1;
		 text.setText(""+cValue);
	}
	/**
	 * ��ͼƬת����PDF
	 */
	public void getImgToPDF(String jpgname,String pdfname){
		Document doc = null;
		 float newWidth = 0;//ͼƬ�¿�� 
		 float newHeight = 0;//ͼƬ�¸߶�
		 Rectangle rect;//�ĵ���С 
		//PDF�ߴ�
		if(combosmcc.getSelectionIndex()==0){
		doc = new Document(PageSize.A3);
		rect =PageSize.A3;
		newWidth = rect.getWidth();
		newHeight =rect.getHeight(); 
		}
		else
		if(combosmcc.getSelectionIndex()==1){
		doc = new Document(PageSize.A4);
		rect =PageSize.A4;
		newWidth = rect.getWidth();
		newHeight = rect.getHeight(); 
		}
		else
		if(combosmcc.getSelectionIndex()==2){
		doc = new Document(PageSize.A5);
		rect =PageSize.A5;
		newWidth = rect.getWidth();
		newHeight = rect.getHeight(); 
		}
		else
		if(combosmcc.getSelectionIndex()==3){
		doc = new Document(PageSize.A6);
		rect =PageSize.A6;
		newWidth = rect.getWidth();
		newHeight = rect.getHeight(); 
		}
		else
		if(combosmcc.getSelectionIndex()==4){
		doc = new Document(PageSize.A7);
		rect =PageSize.A7;
		newWidth = rect.getWidth();
		newHeight = rect.getHeight(); 
		}
		try{
		 //��ͼƬת��PDF��д��ָ��·����
          PdfWriter.getInstance(doc, new FileOutputStream(pdfname));
		  doc.open();//���ĵ�
		  Image bmp = Image.getInstance(jpgname);//���ͼƬ
		  bmp.scaleAbsolute(newWidth, newHeight);//ͼƬ�³ߴ�
		  bmp.setAlignment(Image.MIDDLE);//ͼƬ������ʾ
		  bmp.setAbsolutePosition(1, 1);//��ʾλ��
		  doc.add(bmp);
		  doc.close();
		} 
		catch
		 (Exception e) {  
		if(e instanceof NullPointerException){ 
         return;  	 
		}  
		return;
	}		 
}
	/**
	 * �ر�
	 */
	public void onClose()
	{
		Display display = Display.getDefault();
		display.close();
		
	}
	/**
	 * �ֱ���ѡ���¼�
	 */
	public void oncombofbl()
	{
	 int x = combofbl.getSelectionIndex();//��÷ֱ���index
	 int[] resolution = auto.getIDsOfNames(new String[]{"SetResolution"});
	 int dispIdMember = resolution[0];
     Variant[] rgvarg = new Variant[1]; 
     rgvarg[0] = new Variant(x);
     auto.invoke(dispIdMember,rgvarg);
	}
	/**
	 * ɨ��ߴ�ѡ���¼�
	 */
	public void oncombosmcc()
	{
	 int x = combosmcc.getSelectionIndex();//���ɨ��ߴ�index
	 int[] size = auto.getIDsOfNames(new String[]{"SetScanSize"});
	 int dispIdMember = size[0];
     Variant[] rgvarg = new Variant[1]; 
     rgvarg[0] = new Variant(x);
     auto.invoke(dispIdMember,rgvarg);
	}
	/**
	 * ��Ƶ��ת�Ƕ�ѡ���¼�
	 */
	public void oncombojd()
	{
	 int x = combojd.getSelectionIndex();//�����Ƶ��ת�Ƕ�index
	 int[] rotate = auto.getIDsOfNames(new String[]{"SetVideoRotate"});
	 int dispIdMember = rotate[0];
     Variant[] rgvarg = new Variant[1]; 
     rgvarg[0] = new Variant(x);
     auto.invoke(dispIdMember,rgvarg);
	}
	/**
	 * ��Ŀ����ѡ���¼�
	 */
	public void oncombotype()
	{	
		String type = combotype.getText();		
		TParm result = new TParm();
		String sql="";
		 if(PDFTYPE.equals("0")){
		 sql =" SELECT CLASS_CODE,CLASS_DESC FROM VEMR_CLASS "+
                     " WHERE CLASS_DESC = '"+type+"'";
		 result = new TParm(TJDODBTool.getInstance().select(sql));
		 typecode = result.getValue("CLASS_CODE",0);
		 getsubclass(typecode);
		 }
		 else{
	     sql = " SELECT A.SEQ AS CLASS_CODE,A.FILE_NAME AS CLASS_DESC FROM EMR_MROLIST A"+		  
	                   " WHERE A.FILE_TYPE = 'ALL'" +
	                   " AND A.FILE_NAME = '"+type+"'";	  
		 result = new TParm(TJDODBTool.getInstance().select(sql));
		 typecode = result.getValue("CLASS_CODE",0);
		 text.setText(""+1);
		 combosub.setEnabled(false);
		 }
	}
	/**
	 * ϸ����ѡ���¼�
	 */
	public void oncombosub()
	{	String type = combosub.getText();		
	TParm result = new TParm();
	String sql="";		 
		sql =" SELECT SUBCLASS_CODE,SUBCLASS_DESC FROM EMR_TEMPLET "+
         " WHERE SUBCLASS_DESC = '"+type+"'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        subclasscode = result.getValue("SUBCLASS_CODE",0);
		text.setText(""+1);
	}
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(1024, 600);
		olef.setLocation(10, 10);
		olef.setSize(640, 480);
		site.doVerb(org.eclipse.swt.ole.win32.OLE.OLEIVERB_SHOW);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
