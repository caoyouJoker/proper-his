
package com.javahis.web.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;


/**
 * 默认以ikAnalyzer分析器进行分析的文档索引
 *
 * @author li.xiang1979@gmail.com
 * @version 1.0
 * @date
 */
public class DefaultLuceneDocIndex extends LuceneDocIndex {

	@Override
	public Analyzer getAnalyzer() {
		return new IKAnalyzer();
	}

}
