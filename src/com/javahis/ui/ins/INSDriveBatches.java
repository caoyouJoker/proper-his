package com.javahis.ui.ins;

import java.util.Vector;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import com.tiis.ui.TiFrame;
//import publics.Comm_Operator;
import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
//import publics.Comm_System;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import com.javahis.ui.ins.INSDownloadPayControl;

/**
 * <p>Title:医保支付信息下载</p>
 * <p>Description: 下载费用明细按钮</p>
 * <p>Copyright: Copyright (c) 20060920</p>
 * @version 4.0
 */
public class INSDriveBatches implements Runnable {
  private String fileName;//文件名
  private BufferedWriter file;//文件流
  private TParm patList;//病患列表
  private TTable tab2Table;
  private TButton button;
  private TTextField patsum;
  private Thread thread;//线程
  private TiFrame frame;//界面
  private TParm Parm;
  private TParm Parm1;
  private TParm Parm2;
  private boolean err;//错误状态
  private Thread threadServer;//线程
  private String passelNo;//报盘批号
  private TParm regionParm;
  /**
   * @alias 构造器
   * @param frame TiFrame
   */
  public INSDriveBatches(TParm Parm) {
//    safe = new Safe();
	this.Parm = Parm;

    regionParm = SYSRegionTool.getInstance().selectdata(
			Operator.getRegion());// 获得医保区域代码
    
  }

  /**
   * @alias 得到文件名
   * @return String
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @alias 设置文件名
   * @param name String
   */
  public void setFileName(String name) {
    fileName = name;
  }

  /**
   * @alias 得到报盘批号
   * @return String
   */
  public String getPasselNo()
  {
    return passelNo;
  }

  /**
   * @alias 设置报盘批号
   * @param passelNo String
   */
  public void setPasselNo(String passelNo)
  {
    this.passelNo = passelNo;
  }

  /**
   * @alias 得到报盘批号
   * @return String
   */
  public TParm getPatList()
  {
    return patList;
  }

  /**
   * @alias 设置报盘批号
   * @param passelNo String
   */
  public void setPatList(TParm patList)
  {
    this.patList = patList;
  }

  /**
   * @alias 得到tab2Table的数据
   * @return String
   */
  public TTable gettab2Table()
  {
    return tab2Table;
  }

  /**
   * @alias 设置tab2Table的数据
   * @param passelNo String
   */
  public void settab2Table(TTable tab2Table)
  {
    this.tab2Table = tab2Table;
  }
  /**
   * @alias 得到TTextField(PAT_NUM)的数据
   * @return String
   */
  public TTextField getpatsum()
  {
    return patsum;
  }

  /**
   * @alias 设置TTextField(PAT_NUM)的数据
   * @param passelNo String
   */
  public void setpatsum(TTextField patsum)
  {
    this.patsum = patsum;
  }
  /**
   * @alias 得到tButton_3的数据
   * @return String
   */
  public TButton getbutton()
  {
    return button;
  }

  /**
   * @alias 设置tButton_3的数据
   * @param passelNo String
   */
  public void setbutton(TButton button)
  {
    this.button = button;
  }
  /**
   * @alias 打开文件
   * <p>
   * 打开文件
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean openFile() {
    if (getFileName() == null || getFileName().length() == 0)
      return false;
    try {
      file = new BufferedWriter(new FileWriter(getFileName()));
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * @alias 取得文件数据
   * <p>
   * 取得文件数据
   * <p>
   * @return String
   * @version 1.0
   */
  public String getData()
  {
    String s = "";
    try{
      File f = new File(this.getFileName());
      byte data[] = new byte[(int)f.length()];

      FileInputStream fi = new FileInputStream(f);
      fi.read(data,0,data.length);
      s = new String(data).trim();
      fi.close();
    }catch(Exception e)
    {
      e.printStackTrace();
    }
    return s;
  }

  /**
   * @alias 关闭文件
   * <p>
   * 关闭文件
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean closeFile() {
    if (file == null)
      return false;
    try {
      file.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * @alias 写文件
   * <p>
   * 写文件
   * <p>
   * @param data String
   * @version 1.0
   */
  private void write(String data)
  {
//	 System.out.println("线程==========11");  
    try{
      file.write(data);
      file.newLine();
    }catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * @alias 运行
   * <p>
   * 运行
   * <p>
   * @version 1.0
   */
  public void run() {
// System.out.println("线程==========7");
    err = false;
    //打开文件
    openFile();
// System.out.println("线程==========8");
    //下载全部病患信息
    downAllPat(patList);
    //关闭文件
    closeFile();
    if(!this.isRun())
      err = true;
    if(err)
    {
      thread = null;
      return;
    }
    threadServer = new Thread(new ClientSocket());
    threadServer.start();
//    System.out.println("线程==========31");
    if(!upload())
    {
      thread = null;
      messagebox("保存失败!");
      this.getbutton().setEnabled(true);
      return;
    }
//    System.out.println("线程==========49");
    messagebox("下载成功!");
    this.getbutton().setEnabled(true);
    removeMrTable(this.gettab2Table().getParmValue());
    thread = null;
  }
  /**
	 * 移除病患列表数据
	 */
	private void removeMrTable(TParm parm){
		int row=patList.getCount("CONFIRM_NO");
		for (int i = 0; i < row; i++) {
			if (parm.getCount()>=1) {
				parm.removeRow(0);
			}
		}
		if(parm.getCount("CONFIRM_NO")>=row){
		}else{
			parm.setCount(0);
		}
		this.gettab2Table().setParmValue(parm);
		this.getpatsum().setValue(parm.getCount()+"");
	}

  /**
   * @alias 开始
   */
  public void start()
  {
    thread = new Thread(this); 
    thread.start();
  }

  /**
   * @alias 停止
   */
  public void stop()
  {
    messagebox("下载被用户终止");
    thread = null;
  }

  /**
   * @alias 判断是否在运行
   * @return boolean
   */
  public boolean isRun()
  {
    return thread != null;
  }
  /**
   * @alias  下载病人全部信息
   * <p>
   * 下载病人全部信息
   * <p>
   * @param patList Vector
   * @version 1.0
   */
  private void downAllPat(TParm patList)
  {
//	  System.out.println("线程==========9"+patList);  
    if(patList == null)
      return;
    if(!"0".equals(patList.getData("TYPE")))
    {
      messagebox("病人信息下载失败");
      err = true;
      return;
    }
    TParm patListdata=new TParm();
    patListdata.setData("CONFIRM_NO",patList.getData("CONFIRM_NO"));
//    System.out.println("线程==========9patListdata"+patListdata);  
    int size = patListdata.getCount("CONFIRM_NO");
//    System.out.println("线程==========10"+size);  
    write("" + size);
//    System.out.println("线程==========12");
    TParm data = new TParm();
    for(int i = 0;i<size;i++)
    {
//    System.out.println("线程==========13");  	
      if(!isRun())
      {
        err = true;
        return;
      }
     String seqNumber = (String)patList.getData("CONFIRM_NO",i);
     TParm parmdata=new TParm();
	 parmdata.setData("MR_NO",(String)patList.getData("MR_NO",i));
	 parmdata.setData("CASE_NO",(String)patList.getData("CASE_NO",i)); 
	 parmdata.setData("INS_TYPE",patList.getValue("INS_TYPE")); 
//     System.out.println("线程==========14"+parmdata); 
     Parm1 = getCommDownload(seqNumber, "DataDown_sp", "G",parmdata);
//     System.out.println("线程==========22");        
     Parm2 = detailCZ(seqNumber);
     
    }     
  }
  /**
   * @alias 下载结算信息
   * <p>
   * 下载结算信息
   * <p>
   * @param patNo String 就诊顺序号
   * @return String
   * @version 1.0
   */
  public TParm getCommDownload(String seqNumber, String pipeline,
			String plotType,TParm parmdata) {
//	  System.out.println("线程==========15"); 
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", pipeline);
		parm.setData("PLOT_TYPE", plotType);
		TParm tab4Download_G_J = InsManager.getInstance().safe(parm, "");
//		System.out.println("线程==========16"+tab4Download_G_J); 
		if (tab4Download_G_J == null || tab4Download_G_J.getErrCode() < 0) {
			return tab4Download_G_J;
		}
		tab4Download_G_J.setData("MR_NO",parmdata.getValue("MR_NO"));
		tab4Download_G_J.setData("CASE_NO",parmdata.getValue("CASE_NO")); 
		tab4Download_G_J.setData("INS_TYPE",parmdata.getValue("INS_TYPE"));
		//写到文件中
		write(VtoStringspg(tab4Download_G_J));
//		System.out.println("线程==========21"); 
		return tab4Download_G_J;
	}
  /**
	 * 获取城职费用明细和拒付信息
	 * 
	 * @return
	 */
	private TParm detailCZ(String seqNumber) {
//		System.out.println("线程==========23"); 
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "I");
		TParm tab4Download_I_E = InsManager.getInstance().safe(parm, "");
//		System.out.println("线程==========24"+tab4Download_I_E);
		write("" + tab4Download_I_E.getCount("CONFIRM_NO"));
		//写到文件中
		VtoStringrsi(tab4Download_I_E);	
//		System.out.println("线程==========30");
		return tab4Download_I_E;
	}

  /**
	 * 入参共用
	 * 
	 * @param seqNumber
	 * @return
	 */
	private TParm getDownLoadParm(String seqNumber,Vector patvct) {
		TParm actionParm = new TParm();
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("NHI_HOSP_CODE", hospital);
		actionParm.setData("ADM_SEQ", seqNumber);
		actionParm.setData("REPORT_CODE", this.getPasselNo());
		actionParm.setData("MR_NO", patvct.get(54));
		actionParm.setData("CASE_NO", patvct.get(55));
		actionParm.setData("INS_TYPE", patvct.get(56));
		return actionParm;
	}

  /**
   * @alias 将Vector转String
   * <p>
   * 将Vector转String
   * <p>
   * @param data Vector
   * @return String
   * @version 1.0
   */
  private String VtoStringspg(TParm data)
  {
//	  System.out.println("线程==========17"+data);
	  Vector vct = new Vector();
    StringBuffer sb = new StringBuffer();
    for(int i = 0;i < data.getCount("NAME");i++)
    {
    	vct.add(data.getRowVector(i, "INSBRANCH_CODE").get(0));
    	vct.add(data.getRowVector(i, "ADM_SEQ").get(0));
    	vct.add(data.getRowVector(i, "ISSUE").get(0));
    	vct.add(data.getRowVector(i, "NAME").get(0));
    	vct.add(data.getRowVector(i, "CTZ1_CODE").get(0));
    	vct.add(data.getRowVector(i, "PAY_TYPE").get(0));
    	vct.add(data.getRowVector(i, "UNIT_CODE1").get(0));
    	vct.add(data.getRowVector(i, "UNIT_GENRE").get(0));
    	vct.add(data.getRowVector(i, "ECONOMY_GENRE").get(0));
    	vct.add(data.getRowVector(i, "HOSP_NHI_NO").get(0));
    	vct.add(data.getRowVector(i, "HOSP_CODE").get(0));
    	vct.add(data.getRowVector(i, "HOSP_CATE").get(0));
    	vct.add(data.getRowVector(i, "PHA_AMT").get(0));
    	vct.add(data.getRowVector(i, "PHA_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXM_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXM_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "TREAT_AMT").get(0));
    	vct.add(data.getRowVector(i, "TREAT_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "OP_AMT").get(0));
    	vct.add(data.getRowVector(i, "OP_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BED_AMT").get(0));
    	vct.add(data.getRowVector(i, "BED_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "MATERIAL_AMT").get(0));
    	vct.add(data.getRowVector(i, "MATERIAL_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "ELSE_AMT").get(0));
    	vct.add(data.getRowVector(i, "ELSE_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOODALL_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOODALL_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOOD_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOOD_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "REFUSE_ADD_AMT").get(0));
    	vct.add(data.getRowVector(i, "NUM_AMT").get(0));
    	vct.add(data.getRowVector(i, "BCSSQF_STANDRD_AMT").get(0));
    	vct.add(data.getRowVector(i, "INS_STANDARD_AMT").get(0));
    	vct.add(data.getRowVector(i, "OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "PERCOPAYMENT_RATE_AMT").get(0));
    	vct.add(data.getRowVector(i, "ADD_AMT").get(0));
    	vct.add(data.getRowVector(i, "INS_HIGHLIMIT_AMT").get(0));
    	vct.add(data.getRowVector(i, "TRANBLOOD_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "APPLY_AMT").get(0));
    	vct.add(data.getRowVector(i, "APPLY_AGENT_AMT").get(0));
    	vct.add(data.getRowVector(i, "HOSP_APPLY_AMT").get(0));
    	vct.add(data.getRowVector(i, "TOTAL_NHI_AMT_ASSIST_PAY_CONFIRM").get(0));
    	vct.add(data.getRowVector(i, "REPORT_CODE").get(0));
    	vct.add(data.getRowVector(i, "NUM_DATE").get(0));
    	vct.add(data.getRowVector(i, "ARMYAI_AMT").get(0));
    	vct.add(data.getRowVector(i, "SERVANT_AMT").get(0));
    	vct.add(data.getRowVector(i, "ACCOUNT_PAY_AMT").get(0));
    	vct.add(data.getRowVector(i, "SIN_DISEASE_CODE").get(0));
    	vct.add(data.getRowVector(i, "NHI_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXT_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "COMP_AMT").get(0));
    	vct.add(data.getRowVector(i, "SPEC_NEED_AMT").get(0));
    	vct.add(data.getRowVector(i, "CONFIRM_NO").get(0));
    	vct.add(data.getRowVector(i, "MR_NO").get(0));
    	vct.add(data.getRowVector(i, "CASE_NO").get(0));
    	vct.add(data.getRowVector(i, "INS_TYPE").get(0));
    	vct.add(data.getRowVector(i, "SINGLE_PAY_AMT").get(0));
    	vct.add(data.getRowVector(i, "SINGLE_UPLOAD_TYPE").get(0));
    	vct.add(data.getRowVector(i, "QUIT_REMARK").get(0));
    	
    }
//    System.out.println("线程==========18"+vct);
//    System.out.println("线程==========size"+vct.size());
    for(int i = 0;i < vct.size();i++)
    {
//    System.out.println("线程==========19");	
      String s =(String)vct.get(i);
      s = s.replaceAll("\r","");
      s = s.replaceAll("\n","");
      sb.append(s);
      sb.append("|");
    }
//    System.out.println("线程==========20"+sb);	
    return sb.toString();
  }
  /**
   * @alias 将Vector转String
   * <p>
   * 将Vector转String
   * <p>
   * @param data Vector
   * @return String
   * @version 1.0
   */
  private String VtoStringrsi(TParm data)
  {   
//	  System.out.println("线程==========25"+data);
	  
    Vector vctdata = new Vector();
	  StringBuffer sb = new StringBuffer();
//	  System.out.println("线程StringBuffer");
	  for(int i = 0;i < data.getCount("CONFIRM_NO");i++)
	    {
		  Vector vct = new Vector();
		  vct.add(data.getRowVector(i, "CONFIRM_NO").get(0));
		  vct.add(data.getRowVector(i, "CHARGE_DATE").get(0));
		  vct.add(data.getRowVector(i, "SEQ_NO").get(0));
		  vct.add(data.getRowVector(i, "HOSP_NHI_NO").get(0));
		  vct.add(data.getRowVector(i, "WHYFW_ITEM_SEQ_NO").get(0));
		  vct.add(data.getRowVector(i, "ORDER_DESC").get(0));
		  vct.add(data.getRowVector(i, "OWN_RATE").get(0));
		  vct.add(data.getRowVector(i, "DOSE_CODE").get(0));
		  vct.add(data.getRowVector(i, "STANDARD").get(0));
		  vct.add(data.getRowVector(i, "DEGREE").get(0));
		  vct.add(data.getRowVector(i, "PRICE").get(0));
		  vct.add(data.getRowVector(i, "QTY").get(0));
		  vct.add(data.getRowVector(i, "TOTAL_AMT").get(0));
		  vct.add(data.getRowVector(i, "TOTAL_NHI_AMT").get(0));
		  vct.add(data.getRowVector(i, "OWN_AMT").get(0));
		  vct.add(data.getRowVector(i, "ADDPAY_AMT").get(0));
		  vct.add(data.getRowVector(i, "OP_FLG").get(0));
		  vct.add(data.getRowVector(i, "ADDPAY_FLG").get(0));
		  vct.add(data.getRowVector(i, "NHI_ORD_CLASS_CODE").get(0));
		  vct.add(data.getRowVector(i, "PHAADD_FLG").get(0));
		  vct.add(data.getRowVector(i, "CARRY_FLG").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_AMT").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_CODE").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_DESC").get(0));
		  vct.add(data.getRowVector(i, "PZWH").get(0));
//		  System.out.println("线程==========26"+vct);
		  vctdata.add(vct);		  
	    }
//	  System.out.println("线程==========27"+vctdata);
	  for(int i = 0;i < vctdata.size();i++)
	    {
		  write(VtoString((Vector)vctdata.get(i))); 
	    }
	  return sb.toString();
  }
  
  private String VtoString(Vector data)
  {
    StringBuffer sb = new StringBuffer();
    for(int i = 0;i < data.size();i++)
    {
      String s = (String)data.get(i);
      s = s.replaceAll("\r","");
      s = s.replaceAll("\n","");
      sb.append(s);
      sb.append("|");
    }
    return sb.toString();
  }

  /**
   * @alias 上传给服务器
   * <p>
   * 上传给服务器
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean upload()
	{	 
//	  System.out.println("线程==========32");
	  TParm actionParm = new TParm();
	     
	     Vector vct = new Vector();
		 vct.add(getData());
//		 System.out.println("线程==========33"+vct);  
		  //起Socket
		    Socket client = null;
		    BufferedWriter write = null;
		    try{
//		    	 System.out.println("线程==========34");  	
		      client = new Socket(Operator.getIP(), 8100);
		      write = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		    }catch(Exception e)
		    {
		      e.printStackTrace();
		    }
		    try{ 
//		    	 System.out.println("线程==========35");  
		    //取得文件数据
		      BufferedReader sr = new BufferedReader(new StringReader((String)vct.get(0)));
//		      System.out.println("线程==========36"+sr);  
		      //读病患个数
		      int count = Integer.parseInt(sr.readLine());
//		      System.out.println("线程==========37"+count);  
		      /**
		       * 循环取得每一个病患的信息写入数据库
		       */
		      for(int i = 0;i < count;i ++)
		      {   
		    	  TParm actionParm1 = new TParm();
//		    	  System.out.println("线程==========38");    
		    	  //读结算信息
		          String pat = sr.readLine();
//		          System.out.println("线程==========39"+pat);    
		          //转换成Vector
		          Vector patvct=stringToVector(pat);
		          //获得更新数据库数据
		          actionParm = getDownLoadParm(""+patvct.get(1),patvct);
//		          System.out.println("线程==========40"+patvct); 
		          actionParm1.setData("INSBRANCH_CODE", patvct.get(0));
		          actionParm1.setData("ADM_SEQ", patvct.get(1)); 
		          actionParm1.setData("ISSUE", patvct.get(2));
		          actionParm1.setData("NAME", patvct.get(3));
		          actionParm1.setData("CTZ1_CODE", patvct.get(4));
		          actionParm1.setData("PAY_TYPE", patvct.get(5));
		          actionParm1.setData("UNIT_CODE1", patvct.get(6));
		          actionParm1.setData("UNIT_GENRE", patvct.get(7));
		          actionParm1.setData("ECONOMY_GENRE", patvct.get(8));
		          actionParm1.setData("HOSP_NHI_NO", patvct.get(9));
		          actionParm1.setData("HOSP_CODE", patvct.get(10));
		          actionParm1.setData("HOSP_CATE", patvct.get(11));
		          actionParm1.setData("PHA_AMT", patvct.get(12));
		          actionParm1.setData("PHA_NHI_AMT", patvct.get(13));
		          actionParm1.setData("EXM_AMT", patvct.get(14));
		          actionParm1.setData("EXM_NHI_AMT", patvct.get(15));
		          actionParm1.setData("TREAT_AMT", patvct.get(16));
		          actionParm1.setData("TREAT_NHI_AMT", patvct.get(17));
		          actionParm1.setData("OP_AMT", patvct.get(18));
		          actionParm1.setData("OP_NHI_AMT", patvct.get(19));
		          actionParm1.setData("BED_AMT", patvct.get(20));
		          actionParm1.setData("BED_NHI_AMT", patvct.get(21));
		          actionParm1.setData("MATERIAL_AMT", patvct.get(22));
		          actionParm1.setData("MATERIAL_NHI_AMT", patvct.get(23));
		          actionParm1.setData("ELSE_AMT", patvct.get(24));
		          actionParm1.setData("ELSE_NHI_AMT", patvct.get(25));
		          actionParm1.setData("BLOODALL_AMT", patvct.get(26));
		          actionParm1.setData("BLOODALL_NHI_AMT", patvct.get(27));
		          actionParm1.setData("BLOOD_AMT", patvct.get(28));
		          actionParm1.setData("BLOOD_NHI_AMT", patvct.get(29));
		          actionParm1.setData("REFUSE_ADD_AMT", patvct.get(30));
		          actionParm1.setData("NUM_AMT", patvct.get(31));
		          actionParm1.setData("BCSSQF_STANDRD_AMT", patvct.get(32));
		          actionParm1.setData("INS_STANDARD_AMT", patvct.get(33));
		          actionParm1.setData("OWN_AMT", patvct.get(34));
		          actionParm1.setData("PERCOPAYMENT_RATE_AMT", patvct.get(35));
		          actionParm1.setData("ADD_AMT", patvct.get(36));
		          actionParm1.setData("INS_HIGHLIMIT_AMT", patvct.get(37));
		          actionParm1.setData("TRANBLOOD_OWN_AMT", patvct.get(38));
		          actionParm1.setData("APPLY_AMT", patvct.get(39));
		          actionParm1.setData("APPLY_AGENT_AMT", patvct.get(40));
		          actionParm1.setData("HOSP_APPLY_AMT", patvct.get(41));
		          actionParm1.setData("TOTAL_NHI_AMT_ASSIST_PAY_CONFIRM", patvct.get(42));
		          actionParm1.setData("REPORT_CODE", patvct.get(43));
		          actionParm1.setData("NUM_DATE", patvct.get(44));
		          actionParm1.setData("ARMYAI_AMT", patvct.get(45));
		          actionParm1.setData("SERVANT_AMT", patvct.get(46));
		          actionParm1.setData("ACCOUNT_PAY_AMT", patvct.get(47));
		          actionParm1.setData("SIN_DISEASE_CODE", patvct.get(48));
		          actionParm1.setData("NHI_OWN_AMT", patvct.get(49));
		          actionParm1.setData("EXT_OWN_AMT", patvct.get(50));
		          actionParm1.setData("COMP_AMT", patvct.get(51));
			      actionParm1.setData("SPEC_NEED_AMT", patvct.get(52));
			      actionParm1.setData("CONFIRM_NO", patvct.get(53)); 
			      actionParm1.setData("SYFS", "");
			      actionParm1.setData("SYSL", "0");
			      actionParm1.setData("ILLNESS_SUBSIDY_AMT", "0");
			      actionParm1.setData("SINGLE_PAY_AMT", patvct.get(57));
			      actionParm1.setData("SINGLE_UPLOAD_TYPE", patvct.get(58));
			      actionParm1.setData("QUIT_REMARK", patvct.get(59));
//			      System.out.println("线程==========41"+actionParm1);
			      actionParm.setData("tab4Download_G_J", actionParm1.getData());			  	  
			  	  TParm adm_Confirm = new TParm();
				  adm_Confirm.setData("ADM_SEQ", actionParm1.getData("ADM_SEQ"));
				  actionParm.setData("adm_Confirm", adm_Confirm.getData());
		          //读每个病患个人费用明细和拒付信息
			        int dataCount = Integer.parseInt(sr.readLine());
//			        System.out.println("线程==========42"+dataCount);
			        TParm actionParm2 = new TParm();
			        for(int j = 0;j < dataCount;j++)
				        {			        	
//			        	 System.out.println("线程==========43");
				          //读一行信息
				          String inf = sr.readLine();
//				          System.out.println("线程==========44"+inf);   
				          //将String信息转换成Vector
				          Vector infvct=stringToVector(inf);
//				          System.out.println("线程==========45"+infvct);   
				          actionParm2.addData("CONFIRM_NO", infvct.get(0));
				          actionParm2.addData("CHARGE_DATE", infvct.get(1));
				          actionParm2.addData("SEQ_NO", infvct.get(2));
				          actionParm2.addData("HOSP_NHI_NO", infvct.get(3));
				          actionParm2.addData("WHYFW_ITEM_SEQ_NO", infvct.get(4));
				          actionParm2.addData("ORDER_DESC", infvct.get(5));
				          actionParm2.addData("OWN_RATE", infvct.get(6));
				          actionParm2.addData("DOSE_CODE", infvct.get(7));
				          actionParm2.addData("STANDARD", infvct.get(8));
				          actionParm2.addData("DEGREE", infvct.get(9));
				          actionParm2.addData("PRICE", infvct.get(10));
				          actionParm2.addData("QTY", infvct.get(11));
				          actionParm2.addData("TOTAL_AMT", infvct.get(12));
				          actionParm2.addData("TOTAL_NHI_AMT", infvct.get(13));
				          actionParm2.addData("OWN_AMT", infvct.get(14));
				          actionParm2.addData("ADDPAY_AMT", infvct.get(15));
				          actionParm2.addData("OP_FLG", infvct.get(16));
				          actionParm2.addData("ADDPAY_FLG", infvct.get(17));
				          actionParm2.addData("NHI_ORD_CLASS_CODE", infvct.get(18));
				          actionParm2.addData("PHAADD_FLG", infvct.get(19));
				          actionParm2.addData("CARRY_FLG", infvct.get(20));
				          actionParm2.addData("REFUSE_AMT", infvct.get(21));
				          actionParm2.addData("REFUSE_CODE", infvct.get(22));
				          actionParm2.addData("REFUSE_DESC", infvct.get(23));
				          actionParm2.addData("PZWH", infvct.get(24));
//				          System.out.println("线程==========46"+actionParm2);     
				        }
			        actionParm.setData("tab4Download_I_E", actionParm2.getData());
//			        System.out.println("线程==========47"+actionParm); 
			        TParm result = TIOM_AppServer.executeAction(
							"action.ins.INSDownloadPayAction", "saveData", actionParm);
//			        System.out.println("线程==========48"+result);
			        if (result.getErrCode() < 0) {
						return false;
					}    
		      }
		      
	    }catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	 
    return true;
  }
  /**
   * @alias 将String转换成Vector
   * <p>
   * 将String转换成Vector
   * <p>
   * @param s String
   * @return Vector
   * @author Lizk
   * @version 1.0
   */
  private Vector stringToVector(String s)
  {
    Vector data = new Vector();
    StringTokenizer stk = new StringTokenizer(s,"|");
    while(stk.hasMoreTokens())
      data.add(stk.nextToken().trim());
    return data;
  }
  /**
   * @alias 封装
   * <p>
   * 封装方便调用
   * <p>
   * @param message String
   * @version 1.0
   */
  private void messagebox(String message)
  {
    JOptionPane.showMessageDialog(frame,message);
  }

  /**
   * @alias 设置保存进度条
   * <p>Description: 设置保存进度条</p>
   * <p>Copyright: Copyright (c) 2006</p>
   * <p>Company: DongYou</p>
   * @version 1.0
   */
  class ClientSocket implements Runnable
  {
    public void run()
    {
      try{
        ServerSocket server = new ServerSocket(8100);
        Socket socket = server.accept();
        BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String s = read.readLine();
        while(s!= null)
        {
          StringTokenizer stk = new StringTokenizer(s,"|");      
          s = read.readLine();
        }
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
