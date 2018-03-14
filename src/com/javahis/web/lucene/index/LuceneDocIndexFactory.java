/*
 * CopyRright (c) 2009-2015 www.fdauto.com
 */
package com.javahis.web.lucene.index;
/**
 *
 * @author lixiang
 *
 */
public class LuceneDocIndexFactory {

	public static LuceneDocIndex getDefaultDocIndex(){
		return new DefaultLuceneDocIndex();
	}
}
