package jdo.sum.bean;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;

public class Test {

	/**
	 * 
	 * @param path
	 * @param fileName
	 * @param encode
	 * @throws JAXBException
	 */
	public <T> void createXml(T tcl, String path, String fileName,
			String encode) throws JAXBException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_ENCODING, encode);// 编码
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 格式化
		m.setProperty(Marshaller.JAXB_FRAGMENT, false);// 文件头
		m.marshal(tcl, out);
		TSocket socket= new TSocket("127.0.0.1",TSocket.FILE_SERVER_PORT);
		TIOM_FileServer.writeFile(socket,"E:\\test.lj",out.toByteArray());
	}
	/**
	 * 
	 * @param args
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws JAXBException,
			FileNotFoundException {
		Hl7 hl7 = new Hl7();
		Msh msh = new Msh();
		Pid pid = new Pid();
		Pv1 pv1 = new Pv1();
		ObxList list = new ObxList();
		Obx obx1 = new Obx();
		Obx obx2 = new Obx();
		list.getObx().add(obx1);
		list.getObx().add(obx2);
		ObxOther oth = new ObxOther();
		hl7.setMsh(msh);
		hl7.setPid(pid);
		hl7.setPv1(pv1);
		hl7.setObxList(list);
		hl7.setObxOther(oth);
		Test t=new Test();
		t.createXml(list, "", "", "UTF-8");
	}

}
