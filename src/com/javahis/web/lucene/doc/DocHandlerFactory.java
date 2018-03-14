package com.javahis.web.lucene.doc;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DocHandlerFactory {
	private static final Log log=LogFactory.getLog(DocHandlerFactory.class);

	public static IDocHandler getKidDocHandler(String fileFullPath) {
		if(log.isDebugEnabled()){
			log.debug("获取"+fileFullPath+"类型的处理类");
		}
		fileFullPath = fileFullPath.toLowerCase();
		File file = new File(fileFullPath);
		IDocHandler docHandler = null;
		if (fileFullPath.endsWith(".html") || fileFullPath.endsWith(".htm")) {
			//docHandler = new HtmlDocHandler(file);
		} else if (fileFullPath.endsWith(".pdf")) {
			docHandler = new PDFDocHandler(file);
		} else if (fileFullPath.endsWith(".xls")) {
			//docHandler = new ExcelDocHandler(file);
		} else if (fileFullPath.endsWith(".doc")) {
			//docHandler = new WordDocHandler(file);
		} else if (fileFullPath.endsWith(".txt")) {
			//docHandler = new FileDocHandler(file);
		} else if(fileFullPath.endsWith(".xml")){
			docHandler=new XMLDocHandler(file);
		}else{
			if(log.isWarnEnabled()){
				log.warn("没有"+fileFullPath+"的处理类型");
			}
		}

		return docHandler;

	}



}
