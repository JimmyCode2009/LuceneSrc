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
    	setTitle("���ļ�������");
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
	 * ��������
	 * 
	 * IndexWriter ����������������ɾ���ģ��������
	 */
	@Test
	public void createIndex() throws Exception {
		String path = "F:\\1_ѧϰ\\1_web spam\\1 Survey";
		LinkedList<String> AllFilePath = File2DocumentUtils
				.traverseFolder(path);

		// ��������
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
	 * ����
	 * 
	 * IndexSearcher ���������������н��в�ѯ��
	 */
	public String search(String queryString) throws Exception {
		StringBuffer content = new StringBuffer();
		// 1����Ҫ�������ı�����Ϊ Query
		String[] fields = { "name", "content" };
		QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = queryParser.parse(queryString);

		// 2�����в�ѯ
		IndexSearcher indexSearcher = new IndexSearcher(indexPath);
		Filter filter = null;
		TopDocs topDocs = indexSearcher.search(query, filter, 10000);
		//System.out.println("�ܹ��С�" + topDocs.totalHits + "����ƥ����");
		content.append("�ܹ��С�" + topDocs.totalHits + "����ƥ����").append("\n");
		
		// 3����ӡ���
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int docSn = scoreDoc.doc; // �ĵ��ڲ����
			Document doc = indexSearcher.doc(docSn); // ���ݱ��ȡ����Ӧ���ĵ�
			//File2DocumentUtils.printDocumentNameAndPath(doc); // ��ӡ���ĵ���Ϣ
			content.append("name = " + doc.get("name")).append("\n");
			content.append("path = " + doc.get("path")).append("\n");
			content.append("\n");
		}
		return content.toString();
	}
	

}
