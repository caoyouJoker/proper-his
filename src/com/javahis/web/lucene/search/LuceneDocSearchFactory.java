/*
 * CopyRright (c) 2009-2015 www.fdauto.com
 */
package com.javahis.web.lucene.search;

public class LuceneDocSearchFactory {

	public static LuceneDocSearch getDefaultDocSearch(){
		return new DefaultLuceneDocSearch();
	}
}
