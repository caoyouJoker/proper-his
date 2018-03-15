package com.javahis.ui.spc;

/**
 * <p>
 * Title: 文件操作 
 * </p>
 *
 * <p>
 * Description:  创建DOM并生成XML文件
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author liyh 2012.11.01
 * @version 1.0
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.dongyang.data.TParm;

public class FileUtils {

	public static Map<String, Object> readXMLFile(String file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // 获取到xml文件

		// 下面开始读取
		Element root = doc.getDocumentElement(); // 获取根元素
		NodeList lists = root.getElementsByTagName("Item");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		for (int i = 0; i < lists.getLength(); i++) {
			// 一次取得一个药品
			Element ss = (Element) lists.item(i);

			Map<String,Object> map = new HashMap<String,Object>();

			//药品编码
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			map.put("ORDER_CODE", t.getNodeValue());

			//验收价格
			NodeList prices = ss.getElementsByTagName("DPRC");
			e = (Element) prices.item(0);
			t = e.getFirstChild();
			map.put("DPRC", t.getNodeValue());
			
			//验收数量
			NodeList verifyinQty = ss.getElementsByTagName("DBILLQTY");
			e = (Element) verifyinQty.item(0);
			t = e.getFirstChild();
			map.put("VERIFYIN_QTY", t.getNodeValue());
			
/*			//生产日期-目前系统中没用
			NodeList prdDate = ss.getElementsByTagName("PRDDATE");
			e = (Element) prdDate.item(0);
			t = e.getFirstChild();
			stu.setSex(t.getNodeValue());
			map.put("PRD_DATE", t.getNodeValue());*/
			
			//效期
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			map.put("VALID_DATE", t.getNodeValue());
			
			//供应商编码SUP_CODE
			NodeList supCode = ss.getElementsByTagName("DEALHOSCODE");
			e = (Element) supCode.item(0);
			t = e.getFirstChild();
			map.put("SUP_CODE", t.getNodeValue());
			
			//发票号
			NodeList invoiceNo = ss.getElementsByTagName("INVOICENO");
			e = (Element) invoiceNo.item(0);
			t = e.getFirstChild();
			map.put("INVOICE_NO", t.getNodeValue());
			
			//批号
			NodeList batchNo = ss.getElementsByTagName("LOTNO");
			e = (Element) batchNo.item(0);
			t = e.getFirstChild();
			map.put("BATCH_NO", t.getNodeValue());
			
			
			//采购订单ID，格式为A10112 (DBSCM367)，其中A10112是HIS系统的单号，需要HIS做截取
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			map.put("PURORDER_NO", t.getNodeValue());
		
			//发票日期
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			map.put("INVOICE_DATE", t.getNodeValue());
		
			resultMap.put(i+"", map);
		}

		return resultMap;
	}
	
	/**
	 * 读取国药发票信息
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static TParm readXMLFileP(String file) throws Exception {
	    InputStream is = new FileInputStream( "c:\\test2.xls");
	    HSSFWorkbook hssfWorkbook = new HSSFWorkbook( is); 
	    TParm parm = new TParm();
	    // 循环工作表Sheet
	    //for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){
	    for(int numSheet = 0; numSheet < 1; numSheet++){    	
	      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt( numSheet);
	      if(hssfSheet == null){
	        continue;
	      }
	      
	      // 循环行Row 
	      for(int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++){
	        HSSFRow hssfRow = hssfSheet.getRow( rowNum);
	        if(hssfRow == null){
	          continue;
	        }
	        parm.setData("ORG_CODE", rowNum, getValue(hssfRow.getCell(0)));
	        parm.setData("ORDER_CODE", rowNum, getValue(hssfRow.getCell(1)));
	        parm.setData("LOC_CODE", rowNum, getValue(hssfRow.getCell(2)));
	        parm.setData("LOC_DESC", rowNum, getValue(hssfRow.getCell(3)));
	        parm.setData("ELETAG_CODE", rowNum, getValue(hssfRow.getCell(4)));
	/*        // 循环列Cell  
	        for(int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++){
	          HSSFCell hssfCell = hssfRow.getCell( cellNum);
	          if(hssfCell == null){
	            continue;
	          }
	          
	          System.out.print("    " + getValue( hssfCell));
	        }*/
	/*        if(rowNum == 3)
	        	break;*/
	        System.out.println();
	      }
	      System.out.println(parm);
	      
	    }
	    return parm;
	}
	

	  @SuppressWarnings("static-access")
	  private static String getValue(HSSFCell hssfCell){
	    if(hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN){
	      return String.valueOf( hssfCell.getBooleanCellValue());
	    }else if(hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC){
	      return String.valueOf( hssfCell.getNumericCellValue());
	    }else{
	      return String.valueOf( hssfCell.getStringCellValue());
	    }
	  }
	  
	/**
	 * 读取出货单信息
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static TParm readExcelFile(String file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // 获取到xml文件
		TParm errParm = new TParm();
		// 下面开始读取
		Element root = doc.getDocumentElement(); // 获取根元素
		NodeList lists = root.getElementsByTagName("Item");
//		System.out.println("lists.len : "+lists.getLength());
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// 一次取得一个药品
			Element ss = (Element) lists.item(i);

			//药品编码
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
//			System.out.println(i + "----"+t);
			result.addData("ORDER_CODE", t.getNodeValue());

			//验收价格
			NodeList prices = ss.getElementsByTagName("DPRC");
			e = (Element) prices.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_PRICE", t.getNodeValue());
			
			//验收数量
			NodeList verifyinQty = ss.getElementsByTagName("DBILLQTY");
			e = (Element) verifyinQty.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_QTY", t.getNodeValue());
			
			
/*			//出货单位 暂无
			NodeList billUnit = ss.getElementsByTagName("MSUNITNO");
			e = (Element) billUnit.item(0);
			t = e.getFirstChild();
			result.addData("BILL_UNIT", t.getNodeValue());
			
			//生产商 暂无
			NodeList producer = ss.getElementsByTagName("PRODUCER");
			e = (Element) producer.item(0);
			t = e.getFirstChild();
			result.addData("MAN_CODE", t.getNodeValue());*/
			
/*			//生产日期-目前系统中没用
			NodeList prdDate = ss.getElementsByTagName("PRDDATE");
			e = (Element) prdDate.item(0);
			t = e.getFirstChild();
			stu.setSex(t.getNodeValue());
			map.put("PRD_DATE", t.getNodeValue());*/
			
			//效期
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			result.addData("VALID_DATE", t.getNodeValue());
//			System.out.println("VALID_DATE: "+t.getNodeValue());
			
			//供应商编码SUP_CODE
			NodeList supCode = ss.getElementsByTagName("DEALHOSCODE");
			e = (Element) supCode.item(0);
			t = e.getFirstChild();
			result.addData("SUP_CODE", t.getNodeValue());
			
			//发票号
			NodeList invoiceNo = ss.getElementsByTagName("INVOICENO");
			e = (Element) invoiceNo.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_NO", t.getNodeValue());
			
			//批号
			NodeList batchNo = ss.getElementsByTagName("LOTNO");
			e = (Element) batchNo.item(0);
			t = e.getFirstChild();
			result.addData("BATCH_NO", t.getNodeValue());
			
			//采购订单ID，格式为12062500000312(DBSCM367)，其中12062500000312是HIS系统的单号，需要HIS做截取，12062500000312 前12位是订购单，后两位是序号
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
//			System.out.println("==PURORDER_NO: "+t);
			result.addData("PURORDER_NO", getValue(t));
/*			int len = tString.indexOf("(");
			if(len != -1){
				tString = tString.substring(0, len);
				//截取订购单号
				if(tString.indexOf("-") != -1){
						String[] pruId = tString.split("-");
						result.addData("PURORDER_NO", pruId[0]);
						//截取订购序号
						result.addData("PURSEQ_NO", pruId[1]);
				}else{
					result = new TParm();
					result.setData("msg", "订单号格式不对");
					break;
				}
			}else{
				result = new TParm();
				result.setData("msg", "订单号格式不对");
				break;
			}*/
			//发票日期
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_DATE", t.getNodeValue());
			

			result.addData("STOCK_PRICE", 0);
			result.setCount(i);
		}

		return result;
	}
	
	/**
	 * 得到节点值
	 * @param t
	 * @return
	 */
	public static String getValue(Node t){
		if(null == t){
			return "";
		}else {
			return t.getNodeValue();
		}
	}
		

	/**
	 * 读取料位和电子标签的关系xml
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static TParm readXMLFileOfMateriaLocAndOrderCode(String file) throws Exception {
		System.out.println("------------file: "+file);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // 获取到xml文件
		// 下面开始读取
		Element root = doc.getDocumentElement(); // 获取根元素
		NodeList lists = root.getElementsByTagName("dw_gyyhd_detail_row");
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// 一次取得一个药品
			Element ss = (Element) lists.item(i);
			//药品编码
			NodeList orderCode = ss.getElementsByTagName("ORDER_CODE");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			result.addData("ORDER_CODE", t.getNodeValue());

			//药品编码
			NodeList orderDesc = ss.getElementsByTagName("ORDER_DESC");
			e = (Element) orderDesc.item(0);
			t = e.getFirstChild();
			result.addData("ORDER_DESC", t.getNodeValue());
			
			//电子标签编码
			NodeList eleTagCode = ss.getElementsByTagName("ELETAG_CODE");
			e = (Element) eleTagCode.item(0);
			t = e.getFirstChild();
			result.addData("ELETAG_CODE", t.getNodeValue());
			
			//货位编码
			NodeList materLocCode = ss.getElementsByTagName("MATERIAL_LOC_CODE");
			e = (Element) materLocCode.item(0);
			t = e.getFirstChild();
			result.addData("MATERIAL_LOC_CODE", t.getNodeValue());
			
			//货位编码
			NodeList materLocChnDesc = ss.getElementsByTagName("MATERIAL_LOC_DESC");
			e = (Element) materLocChnDesc.item(0);
			t = e.getFirstChild();
			result.addData("MATERIAL_LOC_DESC", t.getNodeValue());
			
			//部门
			NodeList orgCode = ss.getElementsByTagName("ORG_CODE");
			e = (Element) orgCode.item(0);
			t = e.getFirstChild();
			result.addData("ORG_CODE", t.getNodeValue());
			
			result.setCount(i);
		}
		System.out.println(result);
		return result;
	}

	// 写入ｘｍｌ文件
	public static void callWriteXmlFile(Document doc, Writer w, String encoding) {
		try {
			Source source = new DOMSource(doc);

			Result result = new StreamResult(w);

			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			xformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unused")
	private void writeXMLFileFromTParm(TParm parm,String outfile) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("dw_gyyhd_detail");
		doc.appendChild(root); // 将根元素添加到文档上
		
		int count = parm.getCount();
		// 获取学生信息
//		System.out.println("------count："+count);
		for (int i = 0; i < count; i++) {
//			System.out.println("PURORDER_NO: "+parm.getValue("PURORDER_NO", i));
			// dw_gyyhd_detail_row
			Element stu = doc.createElement("dw_gyyhd_detail_row");
			root.appendChild(stu);
			// 创建 泰心医院编码节点 固定不变
			Element warehouse = doc.createElement("warehouse");
			stu.appendChild(warehouse);
			Text warehouseV = doc.createTextNode("K932290201");
			warehouse.appendChild(warehouseV);
			
			// 创建送货地址编码节点 固定不变
			Element deliverycode = doc.createElement("deliverycode");
			stu.appendChild(deliverycode);
			Text deliverycodeV = doc.createTextNode("932290201");
			deliverycode.appendChild(deliverycodeV);

			// 创建订购单号节点
			Element requestNo = doc.createElement("requestNo");
			stu.appendChild(requestNo); // 将订购单号添加到dw_gyyhd_detail_row点上
			Text requestNoV = doc.createTextNode(parm.getValue("PURORDER_NO", i));
			requestNo.appendChild(requestNoV); // 将文本节点放在订购单号节点上
		}
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			// ((XmlDocument)doc).write(outwriter); //出错！
			callWriteXmlFile(doc, outwriter, "gb2312");
			outwriter.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
//		String str = "C:/student.xml";
		String str = "C:/hisupload/drog/20120904_1.xml";
		FileUtils t = new FileUtils();
	/*	try {
			Map<String, Object>v = t.readXMLFile(str);
			Set<String> set = v.keySet();
			Iterator it = set.iterator();
			int ia = 0;
			while (it.hasNext()) {
				String s = (String) it.next();
				Map<String, Object> map = (Map<String, Object>) v.get(s);
				Iterator<String> i = map.keySet().iterator();
				while (i.hasNext()) {
					String type = (String) i.next();
					System.out.println(type+":"+map.get(type));
				}
				System.out.println(ia++ + "--------------");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}*/
		try {
		//	TParm a  = FileUtils.readExcelFile("c:/hisupload/drog/20121120(gen).xml");
			TParm a  = FileUtils.readExcelFile("c:/hisupload/gen/00046143.xml");
			System.out.println("a:"+a);	
			System.out.println(""+a.getCount("ORDER_CODE"));
//			TParm a  = FileUtils.readXMLFileOfMateriaLocAndOrderCode("c:/temp/123.xml");
//			System.out.println("a:"+a);
//			System.out.println("---cont: "+a.getCount());
/*			for(int i = 0 ; i < a.getCount(); i++){
				System.out.println("ORDER_CODE:"+a.getData("ORDER_CODE", i));
				System.out.println("PURORDER_NO:"+a.getData("PURORDER_NO", i));
				System.out.println("PURSEQ_NO:"+a.getData("", i));
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		



/*	 String outfile = "C:/stucopy.xml";
		t.writeXMLFile(outfile);
 
		XmlCreater t1 = new XmlCreater();
		 String outfile1 = "C:/stucopy1.xml";
		try {
		//	t.writeXMLFile(outfile);
			t1.writeXMLFileFromTParm(null,outfile1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
