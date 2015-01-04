package com.fengyafei.lucene.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Test;

public class File2DocumentUtils {

	// 文件：name, content, size, path
	public static Document file2Document(String path) {
		File file = new File(path);

		Document doc = new Document();
		doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", readFileContent(file), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("size", NumberTools.longToString(file.length()),
				Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("path", file.getAbsolutePath(), Store.YES,
				Index.NOT_ANALYZED));
		return doc;
	}

	public static Document pdf2Document(String path) throws IOException {
		File file = new File(path);
		Document doc = new Document();
		doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", readPdfContent(file), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("size", NumberTools.longToString(file.length()),
				Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("path", file.getAbsolutePath(), Store.YES,
				Index.NOT_ANALYZED));
		return doc;
	}

	public static String readPdfContent(File file) throws IOException {

		FileInputStream fis = new FileInputStream(file);
		PDFParser p = new PDFParser(fis);
		p.parse();
		PDFTextStripper ts = new PDFTextStripper();
		String s = ts.getText(p.getPDDocument());

		fis.close();
		//System.out.println(s);
		return s;
	}

	/**
	 * 读取文件内容
	 */
	public static String readFileContent(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			StringBuffer content = new StringBuffer();

			for (String line = null; (line = reader.readLine()) != null;) {
				content.append(line).append("\n");
			}

			return content.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 递归遍历文件夹
	 * 
	 */
	@Test
	public static LinkedList<String> traverseFolder(String path) {  
		LinkedList<String> res = new LinkedList<String>();
		int fileNum = 0, folderNum = 0;  
        File file = new File(path);  
        if (file.exists()) {  
            LinkedList<File> list = new LinkedList<File>();  
            File[] files = file.listFiles();  
            for (File file2 : files) {  
                if (file2.isDirectory()) {  
                    //System.out.println("文件夹:" + file2.getAbsolutePath());  
                    list.add(file2);  
                    folderNum++;  
                } else {  
                    //System.out.println("文件:" + file2.getAbsolutePath());  
                	if(file2.getName().endsWith(".pdf"))
                	{
                		res.add(file2.getAbsolutePath());
                		fileNum++;  
                	}
                }  
            }  
            File temp_file;  
            while (!list.isEmpty()) {  
                temp_file = list.removeFirst();  
                files = temp_file.listFiles();  
                for (File file2 : files) {  
                    if (file2.isDirectory()) {  
                        //System.out.println("文件夹:" + file2.getAbsolutePath());  
                        list.add(file2);  
                        folderNum++;  
                    } else {  
                        //System.out.println("文件:" + file2.getAbsolutePath());  
                    	if(file2.getName().endsWith(".pdf"))
                    	{
                    		res.add(file2.getAbsolutePath());
                    		fileNum++;  
                    	}  
                    }  
                }  
            }  
        } else {  
            System.out.println("文件不存在!");  
        }  
        System.out.println("文件夹共有:" + folderNum + ",pdf文件共有:" + fileNum);  
        return res;
    } 

	/**
	 * <pre>
	 * 获取 name 属性的值的两种方法：
	 * 1，Field f = doc.getField(&quot;name&quot;);
	 *    f.stringValue();
	 * 2，doc.get(&quot;name&quot;);
	 * </pre>
	 * 
	 * @param doc
	 */
	public static void printDocumentInfo(Document doc) {
		// Field f = doc.getField("name");
		// f.stringValue();
		System.out.println("------------------------------");
		System.out.println("name     = " + doc.get("name"));
		System.out.println("content  = " + doc.get("content"));
		System.out.println("size     = "
				+ NumberTools.stringToLong(doc.get("size")));
		System.out.println("path     = " + doc.get("path"));
	}
	
	public static void printDocumentNameAndPath(Document doc) {
		System.out.println("name     = " + doc.get("name"));
		System.out.println("path     = " + doc.get("path"));
	}

}
