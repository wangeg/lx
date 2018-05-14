package com.itheima.test;
import java.io.File;
import java.io.IOException;

import javax.xml.ws.RequestWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

@SuppressWarnings("all")
public class demo {
	
	@Test
	public void testAddIndex() throws Exception {
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo"));
		
		Analyzer analyzer = new StandardAnalyzer();
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		File files = new File("E:\\上课用的查询资料searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			Document doc = new Document();
			
			Field  fileNameField = new TextField("name", file.getName(), Store.YES);
			doc.add(fileNameField);
			
			Field  filePathField = new TextField("path", file.getPath(), Store.YES);
			doc.add(filePathField);
			
			long sizeOf = FileUtils.sizeOf(file);
			Field  fileSizeField = new TextField("size", sizeOf+"", Store.YES);
			doc.add(fileSizeField);
			
			String fileContent = FileUtils.readFileToString(file);
			Field  fileContentField = new TextField("content", fileContent, Store.YES);
			doc.add(fileContentField);
			
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	
	@Test
	public void testSearchIndex() throws IOException {
		Directory  directory = FSDirectory.open(new File("E:\\class39\\indexRespo"));

		IndexReader  indexReader = DirectoryReader.open(directory);
		
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Query query = new TermQuery(new Term("content", "spring"));
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
			System.out.println("----------------------------------------------");
		}
		
		indexReader.close();
	}
	
	
	@Test
	public void testAnalyzer() throws Exception {
//		Analyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new ChineseAnalyzer(); 
//		C J K
//		Analyzer analyzer = new CJKAnalyzer();
//		Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		
//		String str = "The Spring Framework provides a comprehensive programming and configuration model.";
		String str = "传智播客：MyBatis 本是apache的一个开源项目iBatis,法轮功 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis--by 白面郎君 毛不易";
		
		TokenStream tokenStream = analyzer.tokenStream("test", str);
		
		tokenStream.reset(); 
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		while(tokenStream.incrementToken()) {
			System.out.println(addAttribute);
		}
	}
}
