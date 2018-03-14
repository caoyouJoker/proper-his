package jdo.reg.ws;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dongyang.data.TParm;


public class XMLUtil {
	
	
	public String getPatInfoXML(TParm parm) throws Exception{
		//解析器工厂类
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//解析器
		DocumentBuilder builder =  factory.newDocumentBuilder();
		// 操作的Document对象
		Document document = builder.newDocument();  
		// 设置XML的版本
		//document.setXmlVersion("1.0");
		// 创建根节点
		Element root = document.createElement("root");
		// 将根节点添加到Document对象中  
		document.appendChild(root);
		String ErrorMsg = parm.getErrCode() < 0 ? "-1" : "0";
		root.setAttribute("IsSuccessed", parm.getCount("MR_NO") > 0 ? "0" : "1");
		root.setAttribute("ErrorMsg", parm.getErrCode() < 0 ? parm.getErrText() : "");
		root.setAttribute("PatientID", parm.getValue("MR_NO"));
		root.setAttribute("CureCardNo", parm.getValue("CURECARDNO"));//医疗卡号
		root.setAttribute("IDCardNo", parm.getValue("IDCARDNO"));//身份证号
		root.setAttribute("MedCardNo", parm.getValue("MEDCARDNO"));//医保卡号
		root.setAttribute("PatientName", parm.getValue("PAT_NAME"));
		root.setAttribute("PatientAge", "1");
		root.setAttribute("PatientSex", parm.getValue("CHN_DESC"));
		root.setAttribute("Birthday", parm.getValue("BIRTH_DATE"));
		root.setAttribute("PhoneNo", parm.getValue("TEL_HOME"));
		root.setAttribute("Address", parm.getValue("ADDRESS"));
		// 开始把Document映射到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "utf-8");
		// 设置输出结果  
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult strResult = new StreamResult(writer);
		transFormer.transform(domSource, strResult);
		return writer.toString();
	}
	
	public static void main(String[] args) {
		XMLUtil xu = new XMLUtil();
	}
}
