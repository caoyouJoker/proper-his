package com.javahis.web.lucene.doc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * PDF文档的处理;
 *
 * @author lixiang
 *
 */
public class PDFDocHandler implements IDocHandler {

	private File file;
    public PDFDocHandler(File file)
    {
    	this.file=file;
    }


	public Document getDocument() {
		Document document = new Document();
		try {
			InputStream inputStream = new BufferedInputStream(
					new FileInputStream(file));
			PDDocument pdfDocument = PDDocument.load(inputStream);
			StringWriter writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(pdfDocument, writer);
			String contents = writer.getBuffer().toString();

			document.add(new Field("path", file.getPath(), Field.Store.YES,
					Field.Index.ANALYZED));

			document.add(new Field("contents", contents, Field.Store.YES,
					Field.Index.ANALYZED));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

}
