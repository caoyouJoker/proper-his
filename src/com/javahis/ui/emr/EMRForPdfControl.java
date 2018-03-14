package com.javahis.ui.emr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TScrollPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
//import com.itextpdf.text.Document;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

import org.eclipse.swt.widgets.Display;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;



/**
 * 
 * <p>
 * Title: 病案整理
 * </p>
 */
public class EMRForPdfControl extends TControl {
	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	private String admType = "";//门急住别
	public void onInit() {
		super.onInit();	
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		callFunction("UI|MR_NO|setEnabled", false);
		callFunction("UI|PAT_NAME|setEnabled", false);
		callFunction("UI|SEX_CODE|setEnabled", false);
		callFunction("UI|BIRTH_DATE|setEnabled", false);
		callFunction("UI|CASE_NO|setEnabled", false);
		callFunction("UI|ID_NO|setEnabled", false);
		callFunction("UI|IPD_NO|setEnabled", false);
		callFunction("UI|IN_DATE|setEnabled", false);
		callFunction("UI|OUT_DATE|setEnabled", false);
		callFunction("UI|ADM_DATE|setEnabled", false);
		onClear();
	}
	/**
	 * 病历类型选择事件
	 */
	public void onSelect() {
		if(this.getValue("PDF_TYPE").equals("0")){
			this.callFunction("UI|upload|setEnabled", false);
			this.callFunction("UI|preview|setEnabled", false);	
		}else{
			this.callFunction("UI|upload|setEnabled", true);
			this.callFunction("UI|preview|setEnabled", true);	
		}		 		
	}
	 /**
     * 上移
     */
    public void onUp() {
    	TTable table = (TTable) this.getComponent("Table");
		table.acceptText();
		int row = table.getSelectedRow();
		if (row < 1)
			return;
		TParm p = table.getParmValue();
		String t = p.getValue("PREVIEW", row);
		p.setData("PREVIEW", row, p.getValue("PREVIEW", row - 1));
		p.setData("PREVIEW", row - 1, t);
		t = p.getValue("FILE_NAME", row);
		p.setData("FILE_NAME", row, p.getValue("FILE_NAME", row - 1));
		p.setData("FILE_NAME", row - 1, t);
		t = p.getValue("SUBMIT_FLG", row);
		p.setData("SUBMIT_FLG", row, p.getValue("SUBMIT_FLG", row - 1));
		p.setData("SUBMIT_FLG", row - 1, t);
		table.setParmValue(p);
		table.setSelectedRow(row - 1);
    }
	 /**
     * 下移
     */
    public void onDown() {
    	TTable table = (TTable) getComponent("Table");
		table.acceptText();
		int row = table.getSelectedRow();
		if (row < 0 || row > table.getRowCount() - 2)
			return;
		TParm p = table.getParmValue();
		String t = p.getValue("PREVIEW", row);
		p.setData("PREVIEW", row, p.getValue("PREVIEW", row + 1));
		p.setData("PREVIEW", row + 1, t);
		t = p.getValue("FILE_NAME", row);
		p.setData("FILE_NAME", row, p.getValue("FILE_NAME", row + 1));
		p.setData("FILE_NAME", row + 1, t);
		t = p.getValue("SUBMIT_FLG", row);
		p.setData("SUBMIT_FLG", row, p.getValue("SUBMIT_FLG", row + 1));
		p.setData("SUBMIT_FLG", row + 1, t);
		table.setParmValue(p);
		table.setSelectedRow(row + 1);	
    }
	 /**
     * 预览
     */
    public void onTableClicked() {
    String mrNo = getValue("MR_NO").toString();
	String caseNo = getValue("CASE_NO").toString();		
//	//预览文件
    this.view(mrNo, caseNo);
    }
    /**
     * 病患查询
     */
    public void onQuery() {
    	 if ("".equals(this.getValue("PAT_TYPE"))) {
	            messageBox("病患类型不能为空");
	            return;
	        }
        TParm sendParm = new TParm();
        sendParm.setData("PAT_TYPE", this.getValue("PAT_TYPE"));
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMPatEmr.x", sendParm);
        if (reParm == null)
            return;
//        System.out.println("reParm=====" + reParm);
        this.setValue("MR_NO", reParm.getValue("MR_NO"));
        this.setValue("IPD_NO", reParm.getValue("IPD_NO"));
        this.setValue("PAT_NAME", reParm.getValue("PAT_NAME"));
        this.setValue("BIRTH_DATE", reParm.getTimestamp("BIRTH_DATE"));
        this.setValue("CASE_NO", reParm.getValue("CASE_NO"));
        this.setValue("IN_DATE", reParm.getTimestamp("IN_DATE"));
        this.setValue("OUT_DATE", reParm.getTimestamp("DS_DATE"));
        this.setValue("SEX_CODE", reParm.getValue("SEX_CODE"));
        this.setValue("ID_NO", reParm.getValue("ID_NO"));
        this.setValue("ADM_DATE", reParm.getTimestamp("ADM_DATE"));
        admType = reParm.getValue("ADM_TYPE");
        //界面显示未提交PDF文件
    	this.initTable();
    }
	/**
	 * 拍照
	 */
	public void onPhoto() {
		TParm queryTParm = new TParm();
		 if ("".equals(this.getValue("MR_NO"))) {
	            messageBox("病案号不能为空");
	            return;
	        }
		 if ("".equals(this.getValue("PDF_TYPE"))) {
	            messageBox("病历类型不能为空");
	            return;
	       } 
		    String mrNo = getValue("MR_NO").toString();
			String caseNo = getValue("CASE_NO").toString();
			String pdftype = getValue("PDF_TYPE").toString();
			queryTParm.setData("MR_NO",mrNo); 
			queryTParm.setData("CASE_NO",caseNo);
			queryTParm.setData("PDF_TYPE",pdftype);
			//打开高拍仪拍照
			try {
				Display display = Display.getDefault();
				SWTshellPDF shell = new SWTshellPDF(display,queryTParm);
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
	        //界面显示未提交PDF文件
			this.initTable();			
			
	}

	/**
	 * 上传（追加病历只上传，完整病历上传合并）
	 */
	public void onCombination() {
		//检核数据
		TParm table = (TParm) callFunction("UI|Table|getParmValue");
		 if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT"))
				 &&table!=null) {
			   messageBox("病历已提交");
	            return; 
			}
		 //获得未提交的JPG和PDF文件
		 String PathJPG = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
	     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"JPG";
         String PathPDF = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
	     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"PDF";
         File file1 =new File(PathJPG); 
         File file2 =new File(PathPDF); 
         String s1[]=file1.list();
         String s2[]=file2.list();
         //判断文件是否存在
         if (s1==null&&s2==null){
        	 messageBox("文件不存在"); 
        	 return; 
         }
		 // 提交PDF文件
         String mrno = getValue("MR_NO").toString();
         String caseno = getValue("CASE_NO").toString();
         String ipdno = getValue("IPD_NO").toString();
         String year = caseno.substring(0,2);
         String month = caseno.substring(2,4);
         String serverPath = TConfig.getSystemValue("FileServer.Main.Root") + "\\"
			+ TConfig.getSystemValue("EmrData") + "\\"					
			+"PDF"+"\\"+year+"\\"+month+"\\"+mrno;
			File fileserver =new File(serverPath);
			if(!fileserver.exists())
			TIOM_FileServer.mkdir(TIOM_FileServer.getSocket(), serverPath);
			//上传PDF文件
		    boolean flg = onUpdate(s2,caseno,PathPDF,year,month,mrno);
		      if(!flg)
                return;
			//写入EMR_THRFILE_INDEX表数据
			onEmrThrfileIndex(s2,caseno,PathPDF,year,month,mrno,ipdno);
			//完整病历上传合并
			if (this.getValue("PDF_TYPE").equals("1")) {
		  //病历处理合并所有PDF文件
     	    String tempPath = "C:\\JavaHisFile\\temp\\pdf";
        	TParm p = new TParm();
      		this.delAllFile(tempPath);
    		List list = new ArrayList();
    		//下载临时数据到本地
    		boolean temp = downLoadTempFile(serverPath,tempPath,table,list,caseno);	
    		if(!temp)
    		 return; 
    		try {
    			p= addPdf(table, tempPath, caseno);
    			setBookmarks(tempPath + "\\" + caseno + ".pdf", list);  			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		if(p.getErrCode() != 0){
    			String strmsg="病历整合失败！\n"+p.getErrText()+"\n";
    			strmsg+="有损坏的pdf文件导致\n请检查本机C:\\JavaHisFile\\temp\\pdf目录下对应系统表格中的病历文件，与信息部联系";
    			this.messageBox(strmsg);
    			return;
    		}else{
    			 messageBox("病历上传合并成功");
    		}		 

	    }else{
	          //删除已提交数据		      
	    	  String submitJPG = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
			     +getValue("CASE_NO").toString()+"\\"+"已提交"+"\\"+"JPG";
			  String submitPDF = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
			     +getValue("CASE_NO").toString()+"\\"+"已提交"+"\\"+"PDF"; 
	    	  this.delAllFile(submitJPG);
	    	  this.delAllFile(submitPDF);
			  File filesubjpg = new File(submitJPG);
			  if(!filesubjpg.exists())       		  
				  filesubjpg.mkdirs();
		      File filesubpdf = new File(submitPDF);
		      if(!filesubpdf.exists())
		    	  filesubpdf.mkdirs();
	         //上传成功后将未提交的JPG和PDF文件写到已提交文件下
	          for (int i = 0; i < s1.length; i++) {
	        	  try {
	              File filejpg = new File(submitJPG+ "\\" + s1[i]);
	              if(!filejpg.exists())
					  filejpg.createNewFile();
	              byte data[] = FileTool.getByte(PathJPG + "\\" + s1[i]);   
	                FileOutputStream fs  = new FileOutputStream(filejpg); 
	                fs.write(data); 
	        	  } catch (Exception e) {
	      			e.printStackTrace();
	      		}
	          }
	          for (int i = 0; i < s2.length; i++) {
	        	  try { 
	                  File filepdf = new File(submitPDF+ "\\" + s2[i]);
	                  if(!filepdf.exists())
	                	  filepdf.createNewFile();
	                  byte data[] = FileTool.getByte(PathPDF + "\\" + s2[i]);   
	                    FileOutputStream fs  = new FileOutputStream(filepdf); 
	                    fs.write(data); 
	            	  } catch (Exception e) {
	          			e.printStackTrace();
	          		}
	          }
	         //上传成功后删除未提交的JPG和PDF文件
	          String delPath = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
	 	     +getValue("CASE_NO").toString()+"\\"+"未提交";
	          this.delAllFile(delPath);
	          //界面显示已提交PDF文件         
			  this.initTableSubmit();
			 messageBox("病历上传成功");
		}		 
	}
	/**
	 * 提交
	 */
	public void onUpload() {
		//检核数据
		TParm table = (TParm) callFunction("UI|Table|getParmValue");
		 if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT"))
				 &&table!=null) {
			   messageBox("病历已提交");
	            return; 
			}
			//获得未提交的JPG和PDF文件
			String PathJPG = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
		                     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"JPG";
			String PathPDF = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
				             +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"PDF";
			File file1 =new File(PathJPG); 
			File file2 =new File(PathPDF); 
			String s1[]=file1.list();
			String s2[]=file2.list();
			//判断文件是否存在
			if (s1==null&&s2==null){
			   messageBox("文件不存在"); 
			   return; 
			} 
		 // 提交PDF文件
         String mrno = getValue("MR_NO").toString();
         String caseno = getValue("CASE_NO").toString();
         String tempPath = "C:\\JavaHisFile\\temp\\pdf";
    	 String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + mrno.substring(0, 7) + "\\" + mrno + "\\"
				+ caseno +"_P"+ ".pdf";
		 File file = new File(tempPath + "\\" + caseno + ".pdf");		           
		 try {
			if (!file.exists()) {
				this.messageBox("请合并病历后再提交");
				return;
			}
			byte data[] = FileTool.getByte(file);
			if (TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
				bigFilePath, data)) {
			 	this.messageBox("病历提交成功");										
			}else{
				messageBox("病历提交失败");
				return;
			}					
		 }catch (IOException e) {
			e.printStackTrace();
			messageBox("病历提交失败");
			return;
	}     
          //删除已提交数据		      
    	  String submitJPG = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
		     +getValue("CASE_NO").toString()+"\\"+"已提交"+"\\"+"JPG";
		  String submitPDF = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
		     +getValue("CASE_NO").toString()+"\\"+"已提交"+"\\"+"PDF"; 
    	  this.delAllFile(submitJPG);
    	  this.delAllFile(submitPDF);
		  File filesubjpg = new File(submitJPG);
		  if(!filesubjpg.exists())       		  
			  filesubjpg.mkdirs();
	      File filesubpdf = new File(submitPDF);
	      if(!filesubpdf.exists())
	    	  filesubpdf.mkdirs();
         //上传成功后将未提交的JPG和PDF文件写到已提交文件下
          for (int i = 0; i < s1.length; i++) {
        	  try {
              File filejpg = new File(submitJPG+ "\\" + s1[i]);
              if(!filejpg.exists())
				  filejpg.createNewFile();
              byte data[] = FileTool.getByte(PathJPG + "\\" + s1[i]);   
                FileOutputStream fs  = new FileOutputStream(filejpg); 
                fs.write(data); 
        	  } catch (Exception e) {
      			e.printStackTrace();
      		}
          }
          for (int i = 0; i < s2.length; i++) {
        	  try { 
                  File filepdf = new File(submitPDF+ "\\" + s2[i]);
                  if(!filepdf.exists())
                	  filepdf.createNewFile();
                  byte data[] = FileTool.getByte(PathPDF + "\\" + s2[i]);   
                    FileOutputStream fs  = new FileOutputStream(filepdf); 
                    fs.write(data); 
            	  } catch (Exception e) {
          			e.printStackTrace();
          		}
          }
         //上传成功后删除未提交的JPG和PDF文件
          String delPath = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
 	     +getValue("CASE_NO").toString()+"\\"+"未提交";
          this.delAllFile(delPath);
          //界面显示已提交PDF文件         
		  this.initTableSubmit();
	}
	/**
	 * 上传PDF文件
	 */
	public boolean onUpdate(String s2[],String caseno,String PathPDF,
			String year,String month,String mrno) {
		   for (int i = 0; i < s2.length; i++) {
			   String fileName ="";
	         	String a[] = s2[i].split("_");
	         	if(getValue("PDF_TYPE").equals("0"))//追加病历
	        	fileName = caseno+"_"+a[2]+"_"+a[3]+"_"+a[4];//文件名
	         	else
	         	fileName = caseno+"_"+a[1]+"_"+a[2]+"_"+a[3];//文件名		         	
//	        	System.out.println("fileName=====" + fileName); 
	        	 File f = new File(PathPDF+ "\\" + s2[i]);
	        	String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
					+ "PDF"+"\\"+year+"\\"+month+"\\"+mrno+"\\"+fileName; 
//	        	System.out.println("filePath=====" + filePath); 
				try {
					TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
							filePath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					byte data[] = FileTool.getByte(f);
					if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
							filePath, data)) { 
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 			
	         }
		   return true;   
	}
	/**
	 * 写入EMR_THRFILE_INDEX表数据
	 */
	public void onEmrThrfileIndex(String s2[],String caseno,String PathPDF,
			String year,String month,String mrno,String ipdno) {
		 for (int i = 0; i < s2.length; i++) {
	         	String a[] = s2[i].split("_");
				//写入EMR_THRFILE_INDEX表数据
	         	String filename ="";
	         	String subclasscode ="";
				String filepath = "PDF"+"\\"+year+"\\"+month+"\\"+mrno+"";//储存文件的路径
				if(getValue("PDF_TYPE").equals("0")){//追加病历
					filename = caseno+"_"+a[2]+"_"+a[3]+"_"+
	                  a[4].substring(0,a[4].length()-4);//文件名
					subclasscode =a[1];
				}
		        else
		         	filename = caseno+"_"+a[1]+"_"+a[2]+"_"+
	                  a[3].substring(0,a[3].length()-4);//文件名       	
				String classcode = a[0];//病历类别代码
				Integer fileseq = 0;//文件编号/文件序号
				String optUser = Operator.getID();
				String optTerm = Operator.getIP();
				String SQL =" SELECT MAX(FILE_SEQ) AS MAXCOUNT FROM EMR_THRFILE_INDEX"+
				            " WHERE CASE_NO = '"+ caseno+ "'";
				TParm result1 = new TParm(TJDODBTool.getInstance().select(SQL));
				if (result1.getCount()<= 0) 
					fileseq =1;				
				else
					fileseq =result1.getInt("MAXCOUNT",0)+1;
				String sql= " INSERT INTO EMR_THRFILE_INDEX(CASE_NO,FILE_SEQ,ADM_TYPE," +
						    " MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME,CLASS_CODE," +
						    " SUBCLASS_CODE,DISPOSAC_FLG,CREATOR_USER,CREATOR_DATE," +
						    " PDF_CREATOR_USER,PDF_CREATOR_DATE,OPT_USER,OPT_DATE,OPT_TERM)"+ 
						    " VALUES ('"+ caseno+ "',"+ fileseq+ ",'"+admType+ "'," +
						    " '"+ mrno+ "','"+ ipdno+ "','"+ filepath+ "','"+ filename+ "'," +
						    " '"+ filename+ "','"+ classcode+ "','"+ subclasscode+ "'," +
						    " 'N','"+optUser+ "',SYSDATE," +
						    " '"+optUser+ "',SYSDATE,'"+optUser+ "',SYSDATE,'"+optTerm+ "')";
//				System.out.println("sql=========="+sql); 	
		        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		        System.out.println("result=========="+result);  
		      // 判断错误值
		       if (result.getErrCode() < 0) {
		            messageBox(result.getErrText());
		              return;
		           }  	
		 }
		
	}
	/**
	 * 浏览完整病历
	 */
	public void onReadSubmit() {
		 String mrno = getValue("MR_NO").toString();
         String caseno = getValue("CASE_NO").toString();
		 String tempPath = "C:\\JavaHisFile\\temp\\pdf";
		 String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
		 + "\\正式病历\\" + mrno.substring(0, 7) + "\\" + mrno + "\\"
		 + caseno+"_P" + ".pdf";
		 byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
					.getSocket(),bigFilePath);
		 if(data == null)
		 {
		 messageBox("未提交病历");
		 return;
		 }
		try {
			FileTool.setByte(tempPath + "\\" + caseno + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + 
					tempPath + "\\" + caseno + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
    /**
	 * 删除
	 */
	public void onDelete() {		
		int row = (Integer) callFunction("UI|Table|getClickedRow");
		if (row < 0)
			return;
		TParm tableParm = (TParm) callFunction("UI|Table|getParmValue");
		if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT"))
				 &&tableParm!=null) {
			   messageBox("已提交状态不能删除");
	            return; 
			}	 
		TParm data = tableParm.getRow(row);
		String filenamepdf = data.getValue("FILE_NAMEALL");//PDF文件名
		String filenamejpg = filenamepdf.substring(0, 
				             filenamepdf.length() - 4)+".jpg";//JPG文件名
		if (this.messageBox("询问", "是否删除", 2) == 0) {
			String	pdfpath = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
		     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"PDF";
			String	jpgpath = "C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
		     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"JPG";
			 File file1 =new File(pdfpath); 
			 String s[]=file1.list();
			 boolean pdfFLG = false;//PDF文件删除标记
			 boolean jpgFLG = false;//JPG文件删除标记
				for (int i = 0; i < s.length; i++) {
					if(filenamepdf.equals(s[i])){
					File delfilepdf = new File(pdfpath + "\\" + s[i]); 
					File delfilejpg = new File(jpgpath + "\\" + filenamejpg);
					if(delfilepdf.exists())
						pdfFLG = delfilepdf.delete();
					if(delfilejpg.exists())
						jpgFLG = delfilejpg.delete();				
			}
		}
	
//				 System.out.println("pdfFLG====="+pdfFLG);
//				 System.out.println("jpgFLG====="+jpgFLG); 
		//删除表里数据	
		if(pdfFLG&&jpgFLG){
			this.callFunction("UI|Table|removeRow", row);
			 messageBox("删除成功");
		}else{
			 messageBox("删除失败");
			 return;
	 }
  }
		 //界面显示未提交PDF文件
		this.initTable();
		//清空预览区
		onClearview();
}		
	/**
	 * 清空
	 */
	public void onClear() {
		((TTable) getComponent("Table")).removeRowAll();
		this.setValue("MR_NO", "");
		this.setValue("PAT_NAME", "");
		this.setValue("SEX_CODE", "");
		this.setValue("BIRTH_DATE", "");
		this.setValue("CASE_NO", "");
		this.setValue("PDF_TYPE", "0");//默认追加病历
		this.setValue("PAT_TYPE","1");//默认住院病人
		this.setValue("ID_NO", "");
		this.setValue("IPD_NO", "");
		this.setValue("NO_SUBMIT", "Y");
		this.setValue("IN_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
		this.setValue("OUT_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
		this.setValue("ADM_DATE", DateUtil.getNowTime("yyyy/MM/dd"));		
		if(this.getValue("PDF_TYPE").equals("0")){
			this.callFunction("UI|upload|setEnabled", false);
			this.callFunction("UI|preview|setEnabled", false);	
		}else{
			this.callFunction("UI|upload|setEnabled", true);
			this.callFunction("UI|preview|setEnabled", true);	
		}		 	
		//清空预览区
		onClearview();
	}
	/**
	 * 清空预览区
	 */
	public void onClearview() {
		TPanel viewPanel = (TPanel) this.getComponent("VIEW_PANEL");
		Image image = null;
		Pic pic = new Pic(image);
		pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
		pic.setLocation(0, 0);
		viewPanel.removeAll();
		viewPanel.add(pic);
		pic.repaint();		
	}
	/**
	 * 提交状态选择事件
	 */
	public void onClickRadioButton() {
		 ((TTable) getComponent("Table")).removeRowAll();
		 //未提交状态
		if ("Y".equalsIgnoreCase(this.getValueString("NO_SUBMIT")))
			  //界面显示未提交PDF文件
			this.initTable();	
		 //已提交状态
		if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT")))
			  //界面显示已提交PDF文件
			this.initTableSubmit();
		//清空预览区
		onClearview();
	}
	/**
	 * 界面显示未提交PDF文件
	 */
	private void initTable() {
		this.setValue("NO_SUBMIT", "Y");
		TParm result = new TParm();
		 File file1 =new File("C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
				     +getValue("CASE_NO").toString()+"\\"+"未提交"+"\\"+"PDF");
		 String s[]=file1.list();	
		  if (s!= null) {
			for (int i = 0; i < s.length; i++) {
			   result.addData("PREVIEW", "N");
			   result.addData("FILE_NAME", s[i].substring(0, s[i].length() - 4));
			   result.addData("SUBMIT_FLG", "N");
			   result.addData("FILE_NAMEALL", s[i]);
			}
		  }
		  TTable table = (TTable) this.getComponent("Table");
		  table.setParmValue(result);	 
	}
	/**
	 * 界面显示已提交PDF文件
	 */
	private void initTableSubmit() {
		this.setValue("YES_SUBMIT", "Y");
		TParm result = new TParm();
		 File file1 =new File("C:\\javaEmc\\"+getValue("MR_NO").toString()+"\\"
				     +getValue("CASE_NO").toString()+"\\"+"已提交"+"\\"+"PDF");
		 String s[]=file1.list();
		 
		  if (s!= null) {
			for (int i = 0; i < s.length; i++) {
			   result.addData("PREVIEW", "N");
			   result.addData("FILE_NAME", s[i].substring(0, s[i].length() - 4));
			   result.addData("SUBMIT_FLG", "Y");
			   result.addData("FILE_NAMEALL", s[i]);
			}
		  }
		  TTable table = (TTable) this.getComponent("Table");
		  table.setParmValue(result);
	}
	/**
	 * 删除指定文件夹下所有文件
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * 删除空文件夹
	 */	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 下载临时数据到本地
	 */
	public boolean downLoadTempFile(String filePath, String tempPath,
			TParm parm,List list,String caseno) { 
		String s[] = listPDFFile(filePath);
		for (int parmIndex = 0; parmIndex < parm.getCount("FILE_NAMEALL"); parmIndex++) {
			//判断是否为同一文件
			String a[] = parm.getData("FILE_NAMEALL", parmIndex).toString().split("_");
			String fileName ="";
			if(getValue("PDF_TYPE").equals("0"))//追加病历				
        	fileName = caseno+"_"+a[2]+"_"+a[3]+"_"+a[4];
			else
			fileName = caseno+"_"+a[1]+"_"+a[2]+"_"+a[3];	
			for (int i = 0; i < s.length; i++) {
				if (!StringUtil.isNullString(s[i])&& s[i].equals(fileName)) {
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), filePath + "\\" + s[i]);
					if (data == null)
						break;
					try {
						String b[] = s[i].split("_");
							list.add(b[1]);
						FileTool.setByte(tempPath+ "\\"+parmIndex+".pdf", data);
						break;
					} catch (Exception e) {
						break;
					}
				}
		    }
		}	
		return true;
	}
	
	public String[] listPDFFile(String filePath) {
		return TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), filePath);
	}
	/**
	 * 合并病历文件
	 * 
	 */
	public TParm addPdf(TParm parm, String path, String caseno) {
		TParm p = new TParm();
		File f = new File(path);
		String[] listFile=f.list();
		if (!f.exists())
			f.mkdirs();
		int c = parm.getCount("FILE_NAMEALL");
		// 下载执行文件
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			p.setErr(-1, "服务器上没有找到文件 " + PdfTool.getInstance().getRoot() + "\\pdftk");
			return p;
		}
		try {
			FileTool.setByte(path + "\\pdftk.exe", data);
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		// 制作批处理文件
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < c; i++){
			for(int j=0;j<listFile.length;j++){
			if((i+".pdf").equals(listFile[j]))
				sb.append(i + ".pdf ");
				//break;
			}
		}
		String s = path.substring(0, 2) + " \n" + "cd " + path + " \n"
				+ "pdftk.exe " + sb.toString() + " cat output " + caseno
				+ ".pdf \n exit ";
		try {
			FileTool.setByte(path + "/pdf.bat", s.replaceAll("/", "\\\\")
					.getBytes());
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		// 执行批处理文件
		p = new TParm(exec(path + "\\pdf.bat"));
		if (p.getErrCode() != 0)
			return p;
		return p;
	}
	public Map exec(String com) {
		TParm reset = new TParm();
		try {
			Runtime rt = Runtime.getRuntime();
			Process p=rt.exec( "cmd   /c start "+com);
		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		return reset.getData();
	}
    public static String setBookmarks(String fileName, List marks) {
        String path = getPath(fileName);
        int rows[] = new int[marks.size()];
        int row = 0;
        for (int i = 0; i < marks.size(); i++) {
            rows[i] = row;
            row += getCount(path + "\\" + i + ".pdf");
        }
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            if (document.isEncrypted()) return "Error: Cannot add bookmarks to encrypted document.";
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            PDOutlineItem pagesOutline = new PDOutlineItem();
            pagesOutline.setTitle("All Pages");
            outline.appendChild(pagesOutline);
            List pages = document.getDocumentCatalog().getAllPages();
            for (int i = 0; i < rows.length; i++) {
                PDPage page = (PDPage) pages.get(rows[i]);
                PDPageFitWidthDestination dest = new PDPageFitWidthDestination();
                dest.setPage(page);
                PDOutlineItem bookmark = new PDOutlineItem();
                bookmark.setDestination(dest);
                bookmark.setTitle((String) marks.get(i));
                pagesOutline.appendChild(bookmark);
            }
            pagesOutline.openNode();
            outline.openNode();
            document.save(fileName);
            document.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public static String getPath(String fileName) {
        int index = fileName.lastIndexOf("\\");
        return fileName.substring(0, index);
    }

    public static int getCount(String fileName) {
        int count = 0;
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            List pages = document.getDocumentCatalog().getAllPages();
            count = pages.size();
            document.close();
        }
        catch (Exception e) {}
        return count;
    }
    /**
	 * 预览按钮
	 */
    public void onView() {
        String mrNo = getValue("MR_NO").toString();
    	String caseNo = getValue("CASE_NO").toString();		
		int row = (Integer) callFunction("UI|Table|getClickedRow");
		if (row < 0)
			return;
		TParm tableParm = (TParm) callFunction("UI|Table|getParmValue");	 
		TParm data = tableParm.getRow(row);
		String filenamepdf = data.getValue("FILE_NAMEALL");//PDF文件名
		String filenamejpg = filenamepdf.substring(0, 
				             filenamepdf.length() - 4)+".jpg";//JPG文件名
		String path = "";
		//未提交状态
		if ("Y".equalsIgnoreCase(this.getValueString("NO_SUBMIT")))
			path = "C:\\javaEmc\\"+mrNo+"\\"+caseNo+"\\"+"未提交"+"\\"+"JPG";	
		//已提交状态
		else if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT")))
			path = "C:\\javaEmc\\"+mrNo+"\\"+caseNo+"\\"+"已提交"+"\\"+"JPG";
		File file = new File(path + "\\" + filenamejpg);
		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + 
					path + "\\" + filenamejpg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
	/**
	 * 显示预览区
	 */
	public void view(String mrNo,String caseNo) {
		 TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
		 viewPanel.removeAll();
		 TTable table = (TTable) this.getComponent("Table");
			int allRow = table.getRowCount();
			for (int i = 0; i < allRow; i++) {
				table.setValueAt(false, i, 0);
				table.getParmValue().setData("PREVIEW", i, false);
			}
		table.setValueAt(true, table.getSelectedRow(), 0);
		table.getParmValue().setData("PREVIEW", table.getSelectedRow(), true);
		String filenamepdf = table.getParmValue().getValue("FILE_NAMEALL", 
				table.getSelectedRow());//PDF文件名
		String filenamejpg = filenamepdf.substring(0, 
				             filenamepdf.length() - 4)+".jpg";//JPG文件名
		String path = "";
		//未提交状态
		if ("Y".equalsIgnoreCase(this.getValueString("NO_SUBMIT")))
			path = "C:\\javaEmc\\"+mrNo+"\\"+caseNo+"\\"+"未提交"+"\\"+"JPG";	
		//已提交状态
		else if ("Y".equalsIgnoreCase(this.getValueString("YES_SUBMIT")))
			path = "C:\\javaEmc\\"+mrNo+"\\"+caseNo+"\\"+"已提交"+"\\"+"JPG";
		File file = new File(path + "\\" + filenamejpg);
		try {			
			byte[] f =FileTool.getByte(path + "\\" + filenamejpg); 
			if (f == null){
				viewPanel.removeAll();
				return;
			}	
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(f, scale, flag); 
			Pic pic = new Pic(image);			
			pic.setSize(viewPanel.getWidth(),viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();			
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}
	
	class Pic extends JLabel {
		Image image;
		public Pic(Image image) {
			this.image = image;
		}
		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			if (image != null) {
				g.drawImage(image,15,15,460,550, null);					
			}
		}		
	}	
}
