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
		//������������
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//������
		DocumentBuilder builder =  factory.newDocumentBuilder();
		// ������Document����
		Document document = builder.newDocument();  
		// ����XML�İ汾
		//document.setXmlVersion("1.0");
		// �������ڵ�
		Element root = document.createElement("root");
		// �����ڵ���ӵ�Document������  
		document.appendChild(root);
		String ErrorMsg = parm.getErrCode() < 0 ? "-1" : "0";
		root.setAttribute("IsSuccessed", parm.getCount("MR_NO") > 0 ? "0" : "1");
		root.setAttribute("ErrorMsg", parm.getErrCode() < 0 ? parm.getErrText() : "");
		root.setAttribute("PatientID", parm.getValue("MR_NO"));
		root.setAttribute("CureCardNo", parm.getValue("CURECARDNO"));//ҽ�ƿ���
		root.setAttribute("IDCardNo", parm.getValue("IDCARDNO"));//���֤��
		root.setAttribute("MedCardNo", parm.getValue("MEDCARDNO"));//ҽ������
		root.setAttribute("PatientName", parm.getValue("PAT_NAME"));
		root.setAttribute("PatientAge", "1");
		root.setAttribute("PatientSex", parm.getValue("CHN_DESC"));
		root.setAttribute("Birthday", parm.getValue("BIRTH_DATE"));
		root.setAttribute("PhoneNo", parm.getValue("TEL_HOME"));
		root.setAttribute("Address", parm.getValue("ADDRESS"));
		// ��ʼ��Documentӳ�䵽�ļ�
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "utf-8");
		// ����������  
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
