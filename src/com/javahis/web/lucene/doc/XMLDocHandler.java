package com.javahis.web.lucene.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 *
 * <p>Title: CDA XML 文档处理类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @li.xiang1979@gmail.com
 * @version 1.0
 */
public class XMLDocHandler extends DefaultHandler implements IDocHandler{

	private StringBuffer iobuf = new StringBuffer();

	private HashMap attrmap;

	private Document doc;
	private File file;
	public XMLDocHandler(File file) {
		this.file=file;
	}

	public void startDocument() throws SAXException

	{

		doc = new Document();

		System.out.println("文档解析开始");

	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts)

	{

		iobuf.setLength(0);

		if (atts.getLength() > 0) {

			attrmap = new HashMap();

			for (int i = 0; i < atts.getLength(); i++)

				attrmap.put(atts.getQName(i), atts.getValue(i));

		}

	}

	public void characters(char[] chars, int start, int length)
			throws SAXException

	{

		iobuf.append(chars, start, length);

	}

	public void endElement(String namespaceURI, String localName,
			String fullName) throws SAXException

	{

		// doc.add(new Field()..Keyword(fullName, iobuf.toString()));
		//System.out.println("namespaceURI=========="+namespaceURI);
		//System.out.println("fullName=========="+fullName);
		//System.out.println("localName=========="+localName);
		//System.out.println("content=========="+iobuf.toString());


		//按元素加入;
		doc.add(new Field(fullName, iobuf.toString(), Field.Store.YES,
				Field.Index.ANALYZED));

	}

	public Document getDocument() {

		try {

			SAXParserFactory sf = SAXParserFactory.newInstance();

			SAXParser sp = sf.newSAXParser();
			//new InputSource(file.getPath())
			sp.parse(new FileInputStream(file.getPath()), this);

			doc.add(new Field("path", file.getPath(), Field.Store.YES,
					Field.Index.ANALYZED));

		} catch (IOException e)

		{

			e.printStackTrace();

		} catch (SAXException e)

		{

			e.printStackTrace();

		} catch (Exception e)

		{

			e.printStackTrace();

		}

		return doc;

	}

	public void endDocument() throws SAXException

	{

		System.out.println("文档解析结束");

	}

	/**
	 *
	 * @param args
	 */
	public static void main(String args[])

	{

		XMLDocHandler SXml = new XMLDocHandler(new File("C:\\xml\\110601000006_出院记录_12_CDA.xml"));

	    Document document = SXml.getDocument();

	    System.out.println(document);

	}





}
