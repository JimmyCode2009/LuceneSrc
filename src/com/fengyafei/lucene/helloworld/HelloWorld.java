package com.fengyafei.lucene.helloworld;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import com.fengyafei.lucene.utils.File2DocumentUtils;

public class HelloWorld extends Frame{
	TextField inputquery = new TextField();
    TextArea showresult = new TextArea();
    Button button = new Button("Search");
    
	String indexPath = "luceneIndex";
	Analyzer analyzer = new StandardAnalyzer();


	public static void main(String []args){
		HelloWorld searchprogram = new HelloWorld();
		searchprogram.launchFrame();
	}
	public void launchFrame() {
    	setTitle("论文检索工具");
        setLocation(20, 20);
        //setSize(1200,600);

        add(inputquery, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);
        add(showresult, BorderLayout.SOUTH);
        
        pack();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        inputquery.addActionListener(new TextFieldListener());
        button.addActionListener(new TextFieldListener());
        setVisible(true);

    }
	private class TextFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
        	try {
				showresult.setText(search(inputquery.getText()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }

    }
	/**
	 * 创建索引
	 * 
	 * IndexWriter 是用来操作（增、删、改）索引库的
	 */
	@Test
	public void createIndex() throws Exception {
		String path = "F:\\1_学习\\1_web spam\\1 Survey";
		LinkedList<String> AllFilePath = File2DocumentUtils
				.traverseFolder(path);

		// 建立索引
		IndexWriter indexWriter = new IndexWriter(indexPath, analyzer, true,
				MaxFieldLength.LIMITED);
		// file --> doc
		for (String filePath : AllFilePath) {
			Document doc = File2DocumentUtils.pdf2Document(filePath);
			indexWriter.addDocument(doc);
			indexWriter.optimize();
		}
		indexWriter.close();
	}

	/**
	 * 搜索
	 * 
	 * IndexSearcher 是用来在索引库中进行查询的
	 */
	public String search(String queryString) throws Exception {
		StringBuffer content = new StringBuffer();
		// 1，把要搜索的文本解析为 Query
		String[] fields = { "name", "content" };
		QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = queryParser.parse(queryString);

		// 2，进行查询
		IndexSearcher indexSearcher = new IndexSearcher(indexPath);
		Filter filter = null;
		TopDocs topDocs = indexSearcher.search(query, filter, 10000);
		//System.out.println("总共有【" + topDocs.totalHits + "】条匹配结果");
		content.append("总共有【" + topDocs.totalHits + "】条匹配结果").append("\n");
		
		// 3，打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int docSn = scoreDoc.doc; // 文档内部编号
			Document doc = indexSearcher.doc(docSn); // 根据编号取出相应的文档
			//File2DocumentUtils.printDocumentNameAndPath(doc); // 打印出文档信息
			content.append("name = " + doc.get("name")).append("\n");
			content.append("path = " + doc.get("path")).append("\n");
			content.append("\n");
		}
		return content.toString();
	}
	

}
