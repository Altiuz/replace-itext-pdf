package cl.altiuz.replace_itext_bch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.xml.xmp.XmpWriter;

/**
 * Hello world!
 *
 */
public class App {
	private static final String PDF_PROP_CREATOR = "Creator";
	private static final String PDF_PROP_AUTHOR = "Author";

	public static void main(String[] args) throws IOException {
		System.out.println("Hello World!");

		try {
			getPdf();
		} catch (com.itextpdf.text.DocumentException e) {
			System.out.println(e + "message1");
			e.printStackTrace();
		}

		/*
		 * String pathFul = "C:\\Users\\jhernandez\\Desktop\\reportes\\persona.pdf";
		 * String pathOutput = "C:\\Users\\jhernandez\\Desktop\\reportes\\prueba.pdf";
		 * byte[] pdfFull = convertPDFToByteArray(pathFul); byte[] output =
		 * convertPDFToByteArray(pathOutput);
		 * 
		 * try { concatPdf(pdfFull, output); } catch (IOException e) {
		 * 
		 * System.out.println(e + "message1"); e.printStackTrace(); } catch
		 * (DocumentException e) { System.out.println(e + "message2");
		 * e.printStackTrace(); } catch (com.itextpdf.text.DocumentException e) {
		 * System.out.println(e + "message3"); e.printStackTrace(); }
		 */

	}

	private static byte[] concatPdf(final byte[] mainPdf, final byte[] newPdf)
			throws IOException, DocumentException, com.itextpdf.text.DocumentException {

		final PdfReader mainReader = new PdfReader((InputStream) new ByteArrayInputStream(mainPdf));
		final PdfReader newReader = new PdfReader((InputStream) new ByteArrayInputStream(newPdf));
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final Document document = new Document();
		final PdfSmartCopy copy = new PdfSmartCopy(document, (OutputStream) baos);
		copy.setFullCompression();
		copy.setCompressionLevel(9);
		document.open();
		for (int pages = mainReader.getNumberOfPages(), i = 1; i <= pages; ++i) {
			copy.addPage(copy.getImportedPage(mainReader, i));
		}
		for (int pages = newReader.getNumberOfPages(), i = 1; i <= pages; ++i) {
			copy.addPage(copy.getImportedPage(newReader, i));
		}
		document.close();
		copy.close();
		newReader.close();
		mainReader.close();
		OutputStream out = new FileOutputStream("C:\\Users\\jhernandez\\Desktop\\reportes\\resultadoConcatenacion.pdf");
		out.write(baos.toByteArray());
		out.close();
		return baos.toByteArray();
	}

	private static byte[] convertPDFToByteArray(String path) {

		InputStream inputStream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			inputStream = new FileInputStream(path);

			byte[] buffer = new byte[1024];
			baos = new ByteArrayOutputStream();

			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baos.toByteArray();
	}

	public static byte[] getPdf() throws IOException, com.itextpdf.text.DocumentException {

		byte[] pdfFull = null;
		if (pdfFull == null) {
			System.out.println("pdf no habilitado");
		}
		final PdfReader reader = new PdfReader("C:\\Users\\jhernandez\\Desktop\\reportes\\resultadoConcatenacion.pdf");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PdfStamper pdfStamper = new PdfStamper(reader, (OutputStream) baos);
		final HashMap<String, String> info = new HashMap<>(2);
		info.put(PDF_PROP_CREATOR, "Altiuz Reports");
		info.put(PDF_PROP_AUTHOR, "george");
		pdfStamper.setMoreInfo(info);
		final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		final XmpWriter xmp = new XmpWriter((OutputStream) baos2, (Map<String, String>) info);
		xmp.close();
		pdfStamper.setXmpMetadata(baos2.toByteArray());

		pdfStamper.close();
		reader.close();
		pdfFull = baos.toByteArray();
		OutputStream out = new FileOutputStream("C:\\Users\\jhernandez\\Desktop\\pdfMetadata.pdf");
		out.write(pdfFull);
		out.close();
		return pdfFull;
	}

	public static void getPF2() throws com.itextpdf.text.DocumentException {
		try {
			final HashMap<String, String> info = new HashMap<>(2);
			info.put(PDF_PROP_CREATOR, "Altiuz Reports");
			info.put(PDF_PROP_AUTHOR, "george");
			System.out.println("info" + info);
			PdfReader pdfReader = new PdfReader("C:\\Users\\jhernandez\\Desktop\\persona30.pdf");
			PdfStamper pdfStamper = new PdfStamper(pdfReader,
					new FileOutputStream("C:\\Users\\jhernandez\\Desktop\\persona5.pdf"));
			PdfContentByte canvas = pdfStamper.getOverContent(1);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase("Hello people!"), 250, 750, 0);
			pdfStamper.close();
			pdfReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
