package com.javahis.ui.inv;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dongyang.data.TParm;

public class XmlFiles {
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
		// 下面开始读取
		Element root = doc.getDocumentElement(); // 获取根元素     
		NodeList lists = root.getElementsByTagName("Item");
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// 一次取得一笔物资
			Element ss = (Element) lists.item(i);
			//物资编码
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			result.addData("INV_CODE", t.getNodeValue());

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
			
			//效期
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			result.addData("VALID_DATE", t.getNodeValue());
			
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
			
			//订购单号
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_NO", getValue(t));
			//发票日期
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_DATE", t.getNodeValue());
			
		}
		result.setCount(result.getCount("INV_CODE"));
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
}
