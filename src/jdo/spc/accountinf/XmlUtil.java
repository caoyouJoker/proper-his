package jdo.spc.accountinf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * @ClassName: XmlUtil
 * @Description: TODO
 * @author robo XiaoMin.Robert@Gmail.com
 * @date 2012-5-4 涓嬪崍05:18:42
 * 
 */
public class XmlUtil {

	 

	/**
	 * 鏍规嵁xml瀛楃涓插緱鍒癉ocument瀵硅薄
	 * 
	 * @param srcXml
	 * @return
	 */
	public static Document getDocument(String srcXml) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(srcXml);
		} catch (DocumentException e) {
		 
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * 鑾峰彇鏍硅妭锟�?
	 * 
	 * @param document
	 * @return
	 */
	public static Element getRootElement(Document document) {
		Element element = document.getRootElement();
		return element;
	}

	/**
	 * 鑾峰彇鏍硅妭鐐逛笅瀛愯妭鐐瑰垪锟�?
	 * 
	 * @param rootElement
	 *            鏍硅妭锟�?
	 * @param nodeName
	 *            鑺傜偣鍚嶇О
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Element> getSubRootElement(Element rootElement,
			String nodeName) {
		Iterator<Element> iterator = rootElement.elementIterator(nodeName);
		return iterator;
	}

	/**
	 * 鍒涘缓dom瀵硅薄
	 * 
	 * @return
	 */
	public static Document createDocument() {
		return DocumentHelper.createDocument();
	}

	/**
	 * 鍒涘缓鏍硅妭锟�?
	 * 
	 * @param document
	 * @param rootName
	 * @return
	 */
	public static Element createElementRoot(Document document, String rootName) {
		Element rootElement = document.addElement(rootName);
		return rootElement;
	}

	/**
	 * 鍒涘缓鏍硅妭鐐逛笅鐨勫瓙鑺傜偣
	 * 
	 * @param element
	 * @param nodeName
	 * @return
	 */
	public static Element createElementSub(Element element, String nodeName) {
		if (element != null) {
			return element.addElement(nodeName);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNodeAttributeMap(String xpath,
			Document document) {
		Map<String, Object> map = new HashMap<String, Object>();
		List list = document.selectNodes(xpath);
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);
			Iterator it = element.elementIterator();
			while (it.hasNext()) {
				Element elt = (Element) it.next();
				List<Attribute> attrs = elt.attributes();
				for (Attribute attr : attrs) {
					map.put(attr.getName(), attr.getValue());
				}
			}
		}
		return map;
	}
	

	/**
	 * 鍒涘缓鑺傜偣灞烇拷?
	 * 
	 * @param element
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void createElementAttribute(Element element,
			String attributeName, String attributeValue) {
		element.addAttribute(attributeName, attributeValue);
	}

	public static String createXml(String fileName) {
		// TODO Auto-generated method stub
		Document document = DocumentHelper.createDocument();
		String root = "Root";
		Element employees = document.addElement(root);// root
		Element employee = employees.addElement("employee");
		employee.addAttribute("title", "XML Zone"); // 缁檈mployee娣诲姞title灞烇拷?锛屽苟璁剧疆浠栫殑锟�?
		Element name = employee.addElement("name");
		name.setText("sb2");
		Element sex = employee.addElement("sex");
		sex.setText("famale");

		Element age = employee.addElement("age");
		age.setText("29");
		System.out.println("xml=============" + document.asXML());
		return document.asXML();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String xml = "<Root>"
				+ "<commitData>"
				+ "<Data>"
				+ "<DataRow PERSON_NO=\"200000000000\" CARD_NO=\"11000000256\"/>"
				+ " </Data>" + " </commitData>" + "</Root>";
		Document document = getDocument(xml);
		Element rootElement = getRootElement(document);
		Iterator<Element> iterator = getSubRootElement(rootElement,
				"commitData");
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			Iterator<Element> dataElement = getSubRootElement(element, "Data");
			while (dataElement.hasNext()) {
				Element secElement = (Element) dataElement.next();
				Iterator<Element> dataRowElement = getSubRootElement(
						secElement, "DataRow");
				while (dataRowElement.hasNext()) {
					Element threeElement = (Element) dataRowElement.next();
					String dataRow = threeElement.elementText("DataRow");
					System.out.println(dataRow);
					List<Attribute> list = threeElement.attributes();
					for (Attribute ab : list) {
						System.out.println("name========:" + ab.getName()
								+ "---------" + ab.getValue());
					}
				}
			}

		}

		createXml("xxx");

	}

}
