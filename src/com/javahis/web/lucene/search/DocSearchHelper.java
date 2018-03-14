package com.javahis.web.lucene.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

import com.dongyang.data.TParm;
import com.javahis.web.bean.EMRSearchResultVO;
import com.javahis.web.bean.Page;

/**
 * 对文档索引创建提供便利的类
 *
 */
public class DocSearchHelper {

    private static final Log log = LogFactory.getLog(DocSearchHelper.class);
    // 以下两个是一一对应的,大小也要一致
    private static final String[] types = new String[] {
        "contents", "path",
        "title", "subject", "author"};
    private static final BooleanClause.Occur[] clauses = {
        BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD,
        BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD,
        BooleanClause.Occur.SHOULD};
    // 查找时返回搜索结果的大小
    private static final int TEXT_Fragmenter_SIZE = 500;
    // 查找结果集
    private static final int TOP_COLLECTOR_SIZE = 100;
    // 关键字高亮前缀
    private static final String HIGH_LIGHT_PREFIX = "<font color='red'>";
    // 关键字高亮后缀
    private static final String HIGH_LIGHT_SUFFIX = "</font>";

    /**
     * 通过元素分类，查询对应的值;
     * @param searchPath
     * @param elementCode
     * @param searchValue
     * @return
     */
    public static String getSearchResult(String searchPath, String elementCode,
                                         String searchValue) {
        LuceneDocSearch docSearch = LuceneDocSearchFactory
            .getDefaultDocSearch();
        BooleanQuery query = new BooleanQuery();
        QueryParser queryParser = new QueryParser(Version.LUCENE_30,
                                                  elementCode,
                                                  docSearch.getAnalyzer());
        try {
            query.add(queryParser.parse(searchValue), Occur.SHOULD);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        IndexSearcher indexSearcher = null;
        try {
            TopScoreDocCollector collector = TopScoreDocCollector.create(
                TOP_COLLECTOR_SIZE, false);
            indexSearcher = docSearch.getIndexSearch(searchPath, true);

            indexSearcher.search(query, null, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            /**SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                HIGH_LIGHT_PREFIX, HIGH_LIGHT_SUFFIX);

             Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
                new QueryScorer(query));

                         highlighter.setTextFragmenter(new SimpleFragmenter(
                TEXT_Fragmenter_SIZE));**/

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (indexSearcher != null) {
                try {
                    indexSearcher.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }

    /**
     * 通过范围查询xml对应结点的值;
     * @param searchPath
     * @param elementCode
     * @param startValue
     * @param endValue
     * @return
     */
    public static String getSearchResultByRange(String searchPath,
                                                String elementCode,
                                                String startValue,
                                                String endValue) {
        if (startValue == null || startValue.equals("")) {
            return null;
        }
        if (endValue == null || endValue.equalsIgnoreCase("")) {
            endValue = "99999999";
            //根据startValue得到最后值；

        }
        LuceneDocSearch docSearch = LuceneDocSearchFactory
            .getDefaultDocSearch();
        /*IndexReader reader=null;
           try {
         reader = IndexReader .open(FSDirectory.open(new File(searchPath)), true);
           } catch (CorruptIndexException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
           } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
           } // only searching,
         */


        Query query = new TermRangeQuery(elementCode, startValue, endValue, true, true);
        //通过索引搜索的部分;

        IndexSearcher indexSearcher = null;
        try {
            TopScoreDocCollector collector = TopScoreDocCollector.create(
                TOP_COLLECTOR_SIZE, false);

            indexSearcher = docSearch.getIndexSearch(searchPath, true);

            indexSearcher.search(query, null, collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                Document document = indexSearcher.doc(hits[i].doc);

                /*
                 String highLightText = highlighter.getBestFragment(docSearch
                      .getAnalyzer(), "contents", document.get("contents"));
                    highLightText = highLightText.replace(" ", "");*/

                //System.out.println("=====文件提示=============" + highLightText);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 查询结果;
     * @param searchPath
     * @param searchValue
     * @return
     */
    public static String getSearchResult(String searchPath, String searchValue) {
        long startSearchTime = System.currentTimeMillis();

        LuceneDocSearch docSearch = LuceneDocSearchFactory
            .getDefaultDocSearch();
        BooleanQuery query = new BooleanQuery();

        //加入条件的部分;
        try {
            for (int i = 0; i < types.length; i++) {
                QueryParser queryParser = new QueryParser(Version.LUCENE_30,
                    types[i], docSearch.getAnalyzer());

                // Occur:应该发生(SHOULD:或者)、必须发生(MUST:且)还是不得发生(MUST_NOT:否)
                query.add(queryParser.parse(searchValue), Occur.SHOULD);

            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        //通过索引搜索的部分;

        IndexSearcher indexSearcher = null;
        try {
            TopScoreDocCollector collector = TopScoreDocCollector.create(
                TOP_COLLECTOR_SIZE, false);
            indexSearcher = docSearch.getIndexSearch(searchPath, true);

            indexSearcher.search(query, null, collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                HIGH_LIGHT_PREFIX, HIGH_LIGHT_SUFFIX);

            Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
                new QueryScorer(query));

            highlighter.setTextFragmenter(new SimpleFragmenter(
                TEXT_Fragmenter_SIZE));

            for (int i = 0; i < hits.length; i++) {
                Document document = indexSearcher.doc(hits[i].doc);
                /*System.out.println("=========文件内容===========:"
                  + document.get("contents"));*/


                String highLightText = highlighter.getBestFragment(docSearch
                    .getAnalyzer(), "contents", document.get("contents"));
                highLightText = highLightText.replace(" ", "");

                System.out
                    .println("======搜索文件=========" + document.get("path"));
                System.out.println("=====文件提示=============" + highLightText);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (indexSearcher != null) {
                try {
                    indexSearcher.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("查找索引所用的时间为---->"
                      + (System.currentTimeMillis() - startSearchTime));
        }
        return null;
    }

    /**
     * 可快速查找 可指定types--对应--BooleanClause.Occur
     * 应该发生(SHOULD:或者)、必须发生(MUST:且)还是不得发生(MUST_NOT:否)
     *
     * @param searchPath
     * @param searchValue
     * @param j
     * @return
     */
    public static String getQuickSearchResult(String searchPath,
                                              String searchValue) {
        long startSearchTime = System.currentTimeMillis();
        IndexSearcher indexSearcher = null;
        try {

            // -----------------------------------------------创建搜索至collector
            LuceneDocSearch luceneDocSearch = LuceneDocSearchFactory
                .getDefaultDocSearch();
            TopScoreDocCollector collector = TopScoreDocCollector.create(
                TOP_COLLECTOR_SIZE, false);
            Query query = MultiFieldQueryParser.parse(Version.LUCENE_30,
                searchValue, types, clauses, luceneDocSearch.getAnalyzer());
            indexSearcher = luceneDocSearch.getIndexSearch(searchPath, true);
            indexSearcher.search(query, collector);

            // -------------------------------------------------------创建高亮显示
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                HIGH_LIGHT_PREFIX, HIGH_LIGHT_SUFFIX);
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
                new QueryScorer(query));
            // 返回片段大小
            highlighter.setTextFragmenter(new SimpleFragmenter(
                TEXT_Fragmenter_SIZE));

            // ----------------------------------------------------返回查找的值
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                Document document = indexSearcher.doc(hits[i].doc);
                String contens = document.get("contents");
                TokenStream tokenStream = luceneDocSearch.getAnalyzer()
                    .tokenStream("contents", new StringReader(contens));
                // System.out.println("============contens=============="+contens);
                String str = highlighter.getBestFragment(tokenStream, contens);
                System.out.println(str);
                System.out
                    .println("-------------------------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (indexSearcher != null) {
                try {
                    indexSearcher.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("索引该目录所用的时间为---->"
                      + (System.currentTimeMillis() - startSearchTime));
        }
        return null;
    }

    /**
     * CDA 全文检索功能
     * @param searchPath String    索引搜索路径
     * @param mrNos TParm          MR NO号范围
     * @param metaCodes String[]   元数据编码
     * @param ops String[]         操作
     * @param searchValues String[]   查询值
     * @param joinOP String[]         联接符
     * @param emrForms String[]       表单标准编码;
     * @return List
     */
    public static List<EMRSearchResultVO> getEMRCDAFiles(String searchPath,
        TParm mrNos,
        String[] metaCodes, String[] ops, String[] searchValues,
        String[] joinOP,
        String[] emrForms) {

        LuceneDocSearch docSearch = LuceneDocSearchFactory
            .getDefaultDocSearch();
        BooleanQuery query = new BooleanQuery();
        query = getSerachCondition(mrNos, metaCodes, ops, searchValues, joinOP,
				emrForms, docSearch, query);


        //通过索引搜索的部分;
        List<EMRSearchResultVO> list = new ArrayList<EMRSearchResultVO> ();
        IndexSearcher indexSearcher = null;
        try {
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                HIGH_LIGHT_PREFIX, HIGH_LIGHT_SUFFIX);
             Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
                new QueryScorer(query));
             highlighter.setTextFragmenter(new SimpleFragmenter(
                TEXT_Fragmenter_SIZE));


            TopScoreDocCollector collector = TopScoreDocCollector.create(
                TOP_COLLECTOR_SIZE, false);

            indexSearcher = docSearch.getIndexSearch(searchPath, true);

            indexSearcher.search(query, null, collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            for (int i = 0; i < hits.length; i++) {

                //System.out.println("hits[i].doc==================="+hits[i].doc);

                Document document = indexSearcher.doc(hits[i].doc);
                /**System.out.println("=========文件内容===========:"
                                   + document.get("HR02.01.002"));**/
                System.out
                    .println("======搜索文件=========" + document.get("path"));

                String code="";
                boolean flag=false;
                for(int j=0;j<metaCodes.length;j++){
                    if(document.get(metaCodes[j])!=null&&!document.get(metaCodes[j]).equals("")){
                        code=metaCodes[j];
                        //System.out.println("======有code========"+code);
                        flag=true;
                    }

                }

                 EMRSearchResultVO vo=new EMRSearchResultVO();
                 String fileName=document.get("path").substring(document.get("path").lastIndexOf("\\")+1,document.get("path").length());
                 String filePath=document.get("path").substring(0,document.get("path").lastIndexOf("\\"));
                 String parm[]=fileName.split("_");
                 vo.setCaseNo(parm[0]);
                 vo.setFileSeq(parm[2]);
                 vo.setFileName(fileName);
                 vo.setFilePath(filePath);
                 System.out.println("=======caseNo========="+parm[0]);
                 System.out.println("=======fileSeq========="+parm[2]);
                 //System.out.println("=======fileName========="+fileName);
                 //System.out.println("=======filePath========="+filePath);
                 //演示暂写死;
                 //vo.setPdfFilePath("c:\\pdf\\110609000008_出院记录_24_研究病例.pdf");
                 //vo.setHtmlFilePath();
                 //可以找到对就应内容，则高亮显示
                 if(flag){
                     String highLightText = highlighter.getBestFragment(
                         docSearch
                         .getAnalyzer(), code,
                         document.get(code));
                     //highLightText = highLightText.replace(" ", "");
                     //System.out.println("==================" + highLightText);
                     vo.setDesc(highLightText);
                 }

                list.add(vo);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }
    
    /**
     * CDA 全文检索功能(分页)
     * @param searchPath String    索引搜索路径
     * @param mrNos TParm          MR NO号范围
     * @param metaCodes String[]   元数据编码
     * @param ops String[]         操作
     * @param searchValues String[]   查询值
     * @param joinOP String[]         联接符
     * @param emrForms String[]       表单标准编码;
     * @return List
     */
    public static Page getEMRCDAFiles(String searchPath,
        TParm mrNos,
        String[] metaCodes, 
        String[] ops, 
        String[] searchValues,
        String[] joinOP,
        String[] emrForms,
        Page page) {

        LuceneDocSearch docSearch = LuceneDocSearchFactory
            .getDefaultDocSearch();
        BooleanQuery query = new BooleanQuery();
        query = getSerachCondition(mrNos, metaCodes, ops, searchValues, joinOP,
				emrForms, docSearch, query);


        //通过索引搜索的部分;
        List<EMRSearchResultVO> list = new ArrayList<EMRSearchResultVO> ();
        IndexSearcher indexSearcher = null;
        try {
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                HIGH_LIGHT_PREFIX, HIGH_LIGHT_SUFFIX);
             Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
                new QueryScorer(query));
             highlighter.setTextFragmenter(new SimpleFragmenter(
                TEXT_Fragmenter_SIZE));

             TopScoreDocCollector collector = TopScoreDocCollector.create(
                     TOP_COLLECTOR_SIZE, false);

             indexSearcher = docSearch.getIndexSearch(searchPath, true);

             indexSearcher.search(query, null, collector);

            

            // 查询当页的记录  
            ScoreDoc[] hits = collector.topDocs(page.getBeginIndex(),  
                    page.getPageSize()).scoreDocs;  

            for (int i = 0; i < hits.length; i++) {

                //System.out.println("hits[i].doc==================="+hits[i].doc);

                Document document = indexSearcher.doc(hits[i].doc);
            
                String code="";
                boolean flag=false;
                for(int j=0;j<metaCodes.length;j++){
                    if(document.get(metaCodes[j])!=null&&!document.get(metaCodes[j]).equals("")){
                        code=metaCodes[j];
                        //System.out.println("======有code========"+code);
                        flag=true;
                    }

                }

                 EMRSearchResultVO vo=new EMRSearchResultVO();
                 String fileName=document.get("path").substring(document.get("path").lastIndexOf("\\")+1,document.get("path").length());
                 String filePath=document.get("path").substring(0,document.get("path").lastIndexOf("\\"));
                 String parm[]=fileName.split("_");
                 vo.setCaseNo(parm[0]);
                 vo.setFileSeq(parm[2]);
                 vo.setFileName(fileName);
                 vo.setFilePath(filePath);
                 //System.out.println("=======fileName========="+fileName);
                 //System.out.println("=======filePath========="+filePath);
                 //演示暂写死;
                 //vo.setPdfFilePath("c:\\pdf\\110609000008_出院记录_24_研究病例.pdf");
                 //vo.setHtmlFilePath();
                 //可以找到对就应内容，则高亮显示
                 if(flag){
                     String highLightText = highlighter.getBestFragment(
                         docSearch
                         .getAnalyzer(), code,
                         document.get(code));
                     //highLightText = highLightText.replace(" ", "");
                     //System.out.println("==================" + highLightText);
                     vo.setDesc(highLightText);
                 }else{
                	 vo.setDesc(document.get(code));
                 }

                list.add(vo);
            }
            
            //返回要显示的数
            page.setList(list);
            
            //hit中的记录数
            page.setTotalRecords(collector.getTotalHits());

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return page;

    }

	private static BooleanQuery getSerachCondition(TParm mrNos, String[] metaCodes,
			String[] ops, String[] searchValues, String[] joinOP,
			String[] emrForms, LuceneDocSearch docSearch, BooleanQuery query) {
		//mrNos或
        //基本查询条件;
        if (mrNos != null && mrNos.getCount() > 0) {
             String strMrNos="(";
            for (int i = 0; i <mrNos.getCount(); i++) {
                 if(i!= mrNos.getCount()-1){
                     strMrNos += mrNos.getData("MR_NO", i) + " OR ";
                 }else{
                     strMrNos += mrNos.getData("MR_NO", i);
                 }
            }
            strMrNos+=")";

            //System.out.println("=============strMrNos 条件==========="+strMrNos);

            Query query0 = null;
            //HR01.01.002.02 -> 标识号-号码(000000001068) MR_NO
            QueryParser queryParser = new QueryParser(Version.LUCENE_30,
                                                  "HR01.01.002.02",
                                                  docSearch.getAnalyzer());
            try {
                query0 = queryParser.parse(strMrNos);
            }
            catch (ParseException ex) {

            }

            query.add(query0, Occur.MUST);

        }

        //自由查询条件
        if (metaCodes != null && metaCodes.length > 0) {
            //自由条件部分;
            for (int i = 0; i < metaCodes.length; i++) {
                if (! (metaCodes[i]).equals("")) {
                    //构造查询条件;
                    Query query1 = null;
                    //=
                    if (ops[i].equals("EQ")) {
                        /**query1 = new TermQuery(new Term(metaCodes[i],
                            searchValues[i]));**/
                        //System.out.println("metaCodes[i]=============="+metaCodes[i]);
                        QueryParser queryParser = new QueryParser(Version.LUCENE_30,
                                                  metaCodes[i],
                                                  docSearch.getAnalyzer());
	                   try {
	                        query1 = queryParser.parse(searchValues[i]);
	                    }
	                    catch (ParseException ex) {
	                    }
	
	
	                }else if (ops[i].equals("BQ")) {
                        //>=
                        query1 = new TermRangeQuery(metaCodes[i],
                            searchValues[i], "999999999", true, true);
                        //<=
                    }
                    else if (ops[i].equals("LQ")) {
                        query1 = new TermRangeQuery(metaCodes[i], "0",
                            searchValues[i], true, true);
                        //>
                    }
                    else if (ops[i].equals("B")) {
                        query1 = new TermRangeQuery(metaCodes[i],
                            searchValues[i], "999999999", false, false);
                        //<
                    }
                    else if (ops[i].equals("L")) {
                        query1 = new TermRangeQuery(metaCodes[i], "0",
                            searchValues[i], false, false);
                    }
                    //joinOP  联接关系;
                    if (joinOP[i].equals("AND")) {
                        query.add(query1, Occur.MUST);
                    }
                    else if (joinOP[i].equals("OR")) {
                        query.add(query1, Occur.SHOULD);
                    }
                    else if (joinOP[i].equals("NOT")) {
                        query.add(query1, Occur.MUST_NOT);
                    }

                }

            }

        }

        //EMR表单查询条件加入；
        if(emrForms!=null&&emrForms.length>0){
            //System.out.println("=====emrForms======="+emrForms.length);
            String strFormQuery="(";
            for (int i = 0; i < emrForms.length; i++) {
                if(i!= emrForms.length-1){
                    strFormQuery += emrForms[i] + " OR ";
                }else{
                    strFormQuery += emrForms[i];
                }
            }
            strFormQuery+=")";
            Query query2 = null;

            //HR00.00.001.050->代表文档标识号(EMR010001001)
            QueryParser queryParser = new QueryParser(Version.LUCENE_30,
                                                  "HR00.00.001.05",
                                                  docSearch.getAnalyzer());

            try {
                query2 = queryParser.parse(strFormQuery);
            }
            catch (ParseException ex) {

            }
            query.add(query2, Occur.MUST);
        }
        return query ;
	}


}
