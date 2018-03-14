package jdo.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * XML公用处理工具类；
 * @author lixiang
 *
 */
public class XMLHelper {

	// 默认编码格式
	private static final String ENCODING = "UTF-8";

	// 调试标志
	private static boolean m_debug = false;

	/**
	 * 构造函数
	 */
	public XMLHelper() {

	}

	/**
	 * 创建一个指定根名称的Docment;
	 * @param rootTag 根名称;
	 * @return 新创建的Document对象
	 */
	public static Document defaultDocument(String rootTag) {

		Document doc = new org.apache.xerces.dom.DocumentImpl(null);

		if (rootTag != null) {

			Element root = doc.createElement(rootTag);

			doc.appendChild(root);
		}

		return doc;
	}

	/**
	 * 在doc根下创建有内容的子节点
	 *
	 * @param doc
	 *            节点所属于的doc
	 * @param name
	 *            节点名称
	 * @param content
	 *            节点文本内容
	 */
	public static Element createElement(final Document doc, final String name,
			final String content) {
		return XMLHelper.createElementWithContent(doc,
				doc.getDocumentElement(), name, content);
	}

	/**
	 * 在doc根下创建无内容的子节点
	 *
	 * @param doc
	 *            节点所属于的doc
	 * @param name
	 *            节点名称
	 */
	public static Element createElement(final Document doc, final String name) {
		return XMLHelper.createElementWithOutContent(doc, doc
				.getDocumentElement(), name);
	}

	/**
	 * 创建一个新的节点
	 *
	 * @param doc
	 *            节点所属Document
	 * @param parent
	 *            新节点的父节点
	 * @param name
	 *            新节点的名称
	 */
	public static Element createElementWithOutContent(final Document doc,
			final Node parent, final String name) {
		Element el = doc.createElement(name);

		parent.appendChild(el);

		return el;
	}


	/**
	 * 创建一个节点和它的文本内容
	 *
	 * @param doc
	 *            节点所属于的doc
	 * @param parent
	 *            父节点
	 * @param name
	 *            节点名称
	 * @param content
	 *            节点文本内容
	 */
	public static Element createElementWithContent(final Document doc,
			final Node parent, final String name, final String content) {
		Element el = doc.createElement(name);

		parent.appendChild(el);

		Text t = doc.createTextNode(((content == null) ? "" : content));

		el.appendChild(t);

		return el;
	}

	/**
	 * 设置元素属性值；
	 * @param element
	 * @param name
	 * @param value
	 * @return
	 */
	public static Element updateElementAttr(Element element,String name,
			String value){
		element.setAttribute(name, value);
		return element;
	}

	/**
	 * 根据全部文档，元素名取元素(只取一个对应的index[0,1....]元素)
	 * @param doc   包含需要元素的文档
	 * @param name  元素名
	 * @param index 元素节点，没有找到返回null
	 * @return
	 */
	public static Element getElementByName(final Document doc,
			final String name, final int index) {
		try {
			NodeList nodes = doc.getElementsByTagName(name);

			return (Element) nodes.item(index);
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 根据父节点元素，元素名取元素(只取一个对应的index[0,1....]元素)
	 * @param parent  父节点元素
	 * @param name
	 * @return
	 */
	public static Element getElementByName(final Element parent,
			final String name, final int index) {
		try {
			NodeList nodes = parent.getElementsByTagName(name);

			return (Element) nodes.item(index);
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 将文档对象输出为字符串
	 *
	 * @param document
	 *            文档对象
	 * @param bFormated
	 *            是否格式化
	 * @return docment转换成的字符串
	 */
	public static String toXMLString(Document document, String coding,boolean bFormated) {
		try {
			OutputFormat outputformat = new OutputFormat("xml", coding,
					bFormated);
			StringWriter stringwriter = new StringWriter();
			XMLSerializer xmlserializer = new XMLSerializer(stringwriter,
					outputformat);

			xmlserializer.serialize(document);

			return stringwriter.toString();
		} catch (Exception exception) {
			return null;
		}
	}


	/**
	 * 将xml字符串转换为Document对象
	 *
	 * @param document
	 *            符合完整的xml格式的xml字符串。
	 */
	public static Document getDocumentByString(String document)
			throws Exception {
		Document doc = new DocumentImpl(null);

		doc.appendChild(htmlToXML(document, doc));

		return doc;
	}

	/**
	 * 把格式良好的html文本构造为element
	 *
	 * @param html
	 *            结构良好的html字符串
	 * @param document
	 *            需要加入的文档对象
	 * @return element
	 */
	public static Element htmlToXML(String html, Document document) {
		try {
			StringReader reader = new StringReader(html);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document htmlDoc = docBuilder.parse(new InputSource(reader));
			Element root = htmlDoc.getDocumentElement();
			Node node = document.importNode(root, true);

			return (Element) node;
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 将一个XML文档文件解析为一个JAVA的文档对象
	 *
	 * @param filename
	 *            文档文件名
	 * @return Document文档对象
	 */
	public static Document getDocument(String filename) throws Exception {
		return getSimulateData(filename);
	}




	/**
	 * 将XML的Document文档对象输出到指定文件中<br/> 输出XML文件的编码格式设定为"UTF-8"
	 *
	 * @param document
	 *            文档对象
	 * @param filename
	 *            指定文件，可以包括路径
	 * @throws Exception
	 */
	public static void writeSimulateData(Document document, String filename,String encoding)
			throws Exception {
		//
		TransformerFactory tfactory = TransformerFactory.newInstance();
		//加入病历模版公用XSL样式；
		//StreamSource style = new StreamSource("D:\\xslttest\\example\\xml\\test2.xsl");
		Transformer transformer = tfactory.newTransformer();

		// 将DOM对象转化为DOMSource类对象，该对象表现为转化成别的表达形式的信息容器。
		DOMSource source = new DOMSource(document);

		// 获得一个StreamResult类对象，该对象是DOM文档转化成的其他形式的文档的容器，
		// 可以是XML文件，文本文件，HTML文件。这里为一个XML文件。
		StreamResult result = new StreamResult(new File(filename));

		// 调用API，将DOM文档转化成XML文件。
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(source, result);
	}


	/**
	 * 将一个XML文档文件解析为的文档对象
	 *
	 * @param filename
	 *            文档文件名
	 * @return Document文档对象
	 */
	public static Document getSimulateData(String filename) throws Exception {
		// 打开文件
		FileInputStream in = new java.io.FileInputStream(filename);

		// 创建输入源
		InputSource input = new org.xml.sax.InputSource(in);

		// 创建DOM解析器
		DOMParser parser = new org.apache.xerces.parsers.DOMParser();

		// 解析文档
		parser.parse(input);

		Document doc = parser.getDocument();

		in.close();

		// 返回文档
		return doc;
	}


	/**
	 * 测试
	 */
	public static void main(String args[]){
		//=======================构造DOM测试================================//
		//构造根节点；
		Document doc=XMLHelper.defaultDocument("JAVAHIS-EMR");
		//循环页节点；
		for(int i=1;i<=2;i++){
			Element pageElem=XMLHelper.createElement(doc, "page");
			XMLHelper.updateElementAttr(pageElem, "no", String.valueOf(i));
			XMLHelper.updateElementAttr(pageElem, "HEIGHT", "841");
			XMLHelper.updateElementAttr(pageElem, "WIDTH", "595");

			XMLHelper.updateElementAttr(pageElem, "Top", "28.346456692913385");
			XMLHelper.updateElementAttr(pageElem, "Bottom", "28.346456692913456");
			XMLHelper.updateElementAttr(pageElem, "Left", "42.51968503937008");
			XMLHelper.updateElementAttr(pageElem, "Right", "42.51968503936995");

			XMLHelper.updateElementAttr(pageElem, "X", "0");
			XMLHelper.updateElementAttr(pageElem, "Y", "0");

		}

		//取doc下的page节点；
		Element pageElm1=XMLHelper.getElementByName(doc, "page", 0);
		//page节点下创建table;
		Element tableElm1InPage1=XMLHelper.createElementWithOutContent(doc, pageElm1, "table");
		//设置table属性；
		XMLHelper.updateElementAttr(tableElm1InPage1, "X", "0");
		XMLHelper.updateElementAttr(tableElm1InPage1, "Y", "29");
		XMLHelper.updateElementAttr(tableElm1InPage1, "HEIGHT", "71");
		XMLHelper.updateElementAttr(tableElm1InPage1, "WIDTH", "509");

		//可以循环输出行；
		//创建tr
		Element trInTablePage1=XMLHelper.createElementWithOutContent(doc, tableElm1InPage1, "tr");
		//可以循环输出列；
		Element tdInTrPage1=XMLHelper.createElementWithOutContent(doc, trInTablePage1, "td");
		//设置单元格属性；
		XMLHelper.updateElementAttr(tdInTrPage1, "WIDTH", "169");
		XMLHelper.updateElementAttr(tdInTrPage1, "HEIGHT", "17");
		XMLHelper.updateElementAttr(tdInTrPage1, "X", "0");
		XMLHelper.updateElementAttr(tdInTrPage1, "Y", "0");
		XMLHelper.updateElementAttr(tdInTrPage1, "ALIGNMENT", "2");



		//td下的元素(有内容)；
		Element elm1InTDPage1=XMLHelper.createElementWithContent(doc, tdInTrPage1, "有无选择", "有无选择");
		//写个方法构造TD下的元素；
		XMLHelper.updateElementAttr(elm1InTDPage1, "family", "宋体");
		XMLHelper.updateElementAttr(elm1InTDPage1, "style", "bold");
		XMLHelper.updateElementAttr(elm1InTDPage1, "size", "10");
		XMLHelper.updateElementAttr(elm1InTDPage1, "TYPE", "6");
		XMLHelper.updateElementAttr(elm1InTDPage1, "X", "79");
		XMLHelper.updateElementAttr(elm1InTDPage1, "Y", "2");
		//
		String strXML=XMLHelper.toXMLString(doc,ENCODING, true);
		//System.out.println(strXML);
		//=======================构造DOM测试结束================================//

		//=======================读取XML测试================================//
/*		Document doc=null;
		try {
			doc=XMLHelper.getSimulateData("D:\\xslttest\\example\\xml\\test20110509-1.xml");
			String strXML=XMLHelper.toXMLString(doc,ENCODING, true);
			System.out.println(strXML);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//=======================读取XML测试================================//

		//======================写XML文件测试================================//
		try {
			XMLHelper.writeSimulateData(doc, "c:\\test0509.xml", ENCODING);
			System.out.println("======write success!=======");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//=======================写XML文件测试结束================================//


	}

}
