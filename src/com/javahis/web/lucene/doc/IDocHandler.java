package com.javahis.web.lucene.doc;



import org.apache.lucene.document.Document;
/**
 *
 * <p>Title: 获取文档接口</p>
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
public interface IDocHandler {
	/**
	 * 获取相应类型的索引文档
	 * @return
	 */
	public Document getDocument();



}
