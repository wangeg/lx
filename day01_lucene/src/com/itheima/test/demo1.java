package com.itheima.test;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class demo1 {
	
	/**
	 * 创建全文索引
	 * @throws Exception
	 */
	
	@Test
	public void testAddIndex() throws Exception {
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		File files = new File("E:\\上课用的查询资料searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			Document doc = new Document();
			Field  fileNameField = new TextField("name",file.getName(),Store.YES);
			doc.add(fileNameField);
			Field  filePathField = new StoredField("path",file.getPath());
			doc.add(filePathField);
			long sizeOf = FileUtils.sizeOf(file);
			Field  fileSizeField = new LongField("size",sizeOf,Store.YES);
			doc.add(fileSizeField);
			String fileContent = FileUtils.readFileToString(file);
			Field  fileContentField = new TextField("content",fileContent,Store.YES);
			doc.add(fileContentField);
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	
	/**
	 * 查询所有
	 * @throws IOException
	 */
	
	@Test
	public void testSearchIndex() throws IOException {
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexReader  indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Query query = new MatchAllDocsQuery();
		
//		Query query = new TermQuery(new Term("content", "spring"));
		
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
//			System.out.println("----------------------------------------------");
		}
		indexReader.close();
	}
	
	/**
	 * 删除全部
	 * @throws Exception
	 */
	
	@Test
	public void TestDeleteAllIndex() throws Exception{
		Directory directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		indexWriter.deleteAll();
		indexWriter.close();
	}
	
	/**
	 * 条件查询
	 * @throws Exception
	 */
	
	@Test
	public void TestDeleteByFiledIndex() throws Exception{
		Directory directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		indexWriter.deleteDocuments(new Term("name","apache"));
		indexWriter.close();
	}
	
	/**
	 * 更新索引库
	 * @throws Exception
	 */
	
	@Test
	public void TestUpdateIndex() throws Exception{
		Directory directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		Document doc = new Document();
		doc.add(new TextField("name","温热太阳已经看见美女吧", Store.YES));
		doc.add(new StoredField("path","e://uuuu"));
		doc.add(new LongField("size",100l, Store.YES));
		doc.add(new StringField("content","水电费发货各回各家温州市人大新系统发一次过我去额外人合同纠纷", Store.YES));
		
		// 执行步骤：查询--->删除--->添加-->完成
		indexWriter.updateDocument(new Term("name", "apache"),doc);
		indexWriter.close();
	}
	
	/**
	 * 范围查询
	 * @throws Exception
	 */
	
	@Test
	public void TestSelectIndex()throws Exception{
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexReader  indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Query query = NumericRangeQuery.newLongRange("size",100l,1000l,true,true);
		
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
		}
		indexReader.close();
	}
	
	/**
	 * 组合查询
	 * @throws Exception
	 */
	
	@Test
	public void TestSelect1Index()throws Exception{
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexReader  indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		BooleanQuery query = new BooleanQuery();
		Query query1 = new TermQuery(new Term("name","spring"));
		Query query2 = new TermQuery(new Term("content","spring"));
//		query.add(query1,Occur.MUST); //Occur.MUST--->两者必须都满足
//		query.add(query1,Occur.SHOULD);//Occur.SHOULD--->两者满足其中一个即可
//		query.add(query2,Occur.MUST_NOT);//Occur.MUST_NOT--->当前域里必须没有此条件
		query.add(query1,Occur.MUST);
		query.add(query2,Occur.MUST);
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
		}
		indexReader.close();
	}
	
	/**
	 * 分词查询
	 * @throws Exception
	 */
	
	@Test
	public void TestSearch1Index() throws Exception{
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo1"));
		IndexReader  indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
//		QueryParser queryParser = new QueryParser("name",new IKAnalyzer());
		QueryParser queryParser = new MultiFieldQueryParser(new String[]{"name","content"},new IKAnalyzer());
		
		Query query = queryParser.parse("spring is a project");
		
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
		}
		indexReader.close();
	}
}
