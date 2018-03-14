/*
 * CopyRright (c) 2009-2015 www.fdauto.com
 */
package com.javahis.web.lucene.search;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

/**
 * 对文档索引搜索的抽象
 */
public abstract class LuceneDocSearch {

	/** 设置分词 */
	public abstract Analyzer getAnalyzer();

	/** 创建IndexSearch */
	public IndexSearcher getIndexSearch(String searchDir, boolean create)
			throws Exception {
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(
				searchDir)), true);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
}
