package jdo.ind.inf;

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
 * @date 2012-5-4 下午05:18:42
 * 
 */
public class XmlUtil {

	 

	/**
	 * 根据xml字符串得到Document对象
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
	 * 获取根节�?
	 * 
	 * @param document
	 * @return
	 */
	public static Element getRootElement(Document document) {
		Element element = document.getRootElement();
		return element;
	}

	/**
	 * 获取根节点下子节点列�?
	 * 
	 * @param rootElement
	 *            根节�?
	 * @param nodeName
	 *            节点名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Element> getSubRootElement(Element rootElement,
			String nodeName) {
		Iterator<Element> iterator = rootElement.elementIterator(nodeName);
		return iterator;
	}

	/**
	 * 创建dom对象
	 * 
	 * @return
	 */
	public static Document createDocument() {
		return DocumentHelper.createDocument();
	}

	/**
	 * 创建根节�?
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
	 * 创建根节点下的子节点
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
	 * 创建节点属�?
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
		employee.addAttribute("title", "XML Zone"); // 给employee添加title属�?，并设置他的�?
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
