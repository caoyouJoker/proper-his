package jdo.sum;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jdo.sum.bean.ObjectFactory;

import com.dongyang.config.TConfig;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;

/**
 * 
 * @author shibl
 *
 */
public class XmlUtil {

	private TSocket socket;

	private String ip;

	private String rootDir;

	private String tempDir;

	private String tmsDir;

	private String type;

	/**
	 * ���캯��
	 */
	public XmlUtil() {
		initConfig();
	}

	/**
	 * ʵ��
	 */
	public static XmlUtil instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return XmlUtil
	 */
	public static XmlUtil getInstance() {
		if (instanceObject == null)
			instanceObject = new XmlUtil();
		return instanceObject;
	}

	/**
	 * ��ʼ������
	 */
	public void initConfig() {
		try {
			TConfig config = TConfig.getConfig("WEB-INF/config/system/THl7.x");
			ip = config.getString("File.IP");
			rootDir = config.getString("File.RootDir");
			tempDir = config.getString("File.TempDir");
			tmsDir = config.getString("File.TmsDir");
			type = config.getString("MessageType");
			socket = new TSocket(ip, TSocket.FILE_SERVER_PORT);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param path
	 * @param fileName
	 * @param encode
	 * @throws JAXBException
	 */
	public <T> void createXml(T t, String path, String fileName,
			String encode) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_ENCODING, encode);// ����
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// ��ʽ��
		m.setProperty(Marshaller.JAXB_FRAGMENT, false);// �ļ�ͷ
		m.marshal(t, out);// ���� t Element Ϊ�������������鵽�������
		if (type.equals("FILE"))
			sendFile(path, fileName, out.toByteArray());
	}

	/**
	 * �ļ�����
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 * @throws Throwable 
	 */
	public void sendFile(String path, String fileName, byte[] p) throws Exception{
		Timestamp date = StringTool.getTimestamp(new Date());
		String year = StringTool.getString(date, "yyyy");
		String months = StringTool.getString(date, "MM");
		String day = StringTool.getString(date, "dd");
		// �����������ļ���
		if (!TIOM_FileServer.writeFile(socket, tmsDir + "\\" + year + "\\"
				+ months + "\\" + day + "\\" + path + "\\" + fileName, p)) {
		    throw new Exception("�����������ļ���ʧ��");
		}
		// ������Ŀ���ļ���
		if (!TIOM_FileServer.writeFile(socket, rootDir + "\\" + fileName, p)) {
			throw new Exception("������Ŀ���ļ���ʧ��");
		}
		return;
	}
}
