package com.javahis.web.lucene.index;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
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
public abstract class LuceneDocIndex {
	public abstract Analyzer getAnalyzer();

	public IndexWriter getIndexWriter(String indexDir, boolean create)
			throws Exception {

		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(indexDir)), getAnalyzer(), create,
				IndexWriter.MaxFieldLength.LIMITED);
		return indexWriter;
	}

}
