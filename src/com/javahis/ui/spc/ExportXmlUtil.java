package com.javahis.ui.spc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.dongyang.control.TControl;

public class ExportXmlUtil extends TControl {

	private static final ExportXmlUtil INSTANCE =new ExportXmlUtil();
	
	public ExportXmlUtil(){	
	}
	
	public static ExportXmlUtil getInstance(){
	     return INSTANCE;
	}
	
	/**
     * 导出Xml文件
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName 默认文件名字
     */

    public static void exeSaveXml(Document doc,String fileDefName) {
        
        //标准SWING
        JFileChooser chooser = new JFileChooser();
        //设置当前的目录（目前写死）
        File dir= new File("C:\\JavaHis\\XML");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);

        //默认的文件保存名字
        chooser.setSelectedFile(new File(fileDefName));//提供默认名
        //设置对话框的标题
        chooser.setDialogTitle("生成订单界面");
        //设置过滤（扩展名）
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".xml");
            }

            public String getDescription() {
                return "XML (*.xml)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            writeFile(doc, path);
        }
    }
    
    /**
	 * 写XML文件到指定的路径下
	 * @param document    
	 * @param path        绝对路径
	 */
	public static  void writeFile(Document document,String fileName){
		XMLWriter output = null ;
		//创建一个文件对象
        File file;
		try{
			//过路径中包含.xls就不加后缀
            if (!fileName.contains(".xml")) {
                file = new File(fileName + ".xml");
            } else {
                file = new File(fileName);
            }
			
			//判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "保存", "该文件已经存在,是否覆盖？",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
			//output = new XMLWriter(new FileWriter( file)); //保存文档
            OutputFormat formet =OutputFormat.createPrettyPrint();//设置格式化输出器  
            formet.setEncoding("UTF-8");  
			//以字节流的方式输出到文件
			output = new XMLWriter(new FileOutputStream(fileName),formet);
			output.write(document);
            //output.write( document );
            JOptionPane.showMessageDialog(null, "生成订单成功");
        }catch(IOException e){
        	System.out.println(e.getMessage());
        	JOptionPane.showMessageDialog(null, "生成订单失败");
        }finally{
        	if(output != null ){
	        	try {
	        		
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
	
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
	 * 获取根节点
	 * 
	 * @param document
	 * @return
	 */
	public static Element getRootElement(Document document) {
		Element element = document.getRootElement();
		return element;
	}

	/**
	 * 获取根节点下子节点列表
	 * 
	 * @param rootElement
	 *            根节点
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
	 * 创建根节点
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
	 * 创建节点属性
	 * 
	 * @param element
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void createElementAttribute(Element element,
			String attributeName, String attributeValue) {
		element.addAttribute(attributeName, attributeValue);
	}

	public static Document createXml(List list){
	 	Document doc = DocumentHelper.createDocument() ;
        Element root = doc.addElement("dw_gyyhd_detail");
        
        for(int i = 0 ; i < list.size() ; i++){
        	Map<String, Object> map = (Map<String, Object>)list.get(i);
	        Element detailRow = root.addElement("dw_gyyhd_detail_row");
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value =  (Object) entry.getValue();
				Element d = detailRow.addElement(entry.getKey());
				d.setText(value+"");
			}
        }
        return doc;
	}
	
	public static void main(String[] args) {

	}
}
