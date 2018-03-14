/*
 * CopyRright (c) 2009-2015 www.fdauto.com
 */
package com.javahis.web.lucene.search;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class DefaultLuceneDocSearch extends LuceneDocSearch {

	public Analyzer getAnalyzer() {
		return new IKAnalyzer();
	}
}
