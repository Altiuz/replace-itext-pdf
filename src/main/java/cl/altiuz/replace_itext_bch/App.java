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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.dom4j.DocumentException;

import com.itextpdf.text.Document;
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

		// concatPdf2();
		// editMetadataPdfBox();
		String pathFul = "C:\\Users\\jhernandez\\Desktop\\reportes\\persona.pdf";
		String pathOutput = "C:\\Users\\jhernandez\\Desktop\\reportes\\prueba.pdf";
		byte[] pdfFull = convertPDFToByteArray(pathFul);
		byte[] output = convertPDFToByteArray(pathOutput);
		concatPdfBox(pdfFull, output);

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

	private static byte[] editMetadataPdfBox() throws IOException {
		// Creating PDF document object
		// Creating PDF document object
		PDDocument document = new PDDocument();
		PDPage blankPage = new PDPage();
		document.addPage(blankPage);
		PDDocumentInformation pdd = document.getDocumentInformation();
		pdd.setAuthor("George");
		pdd.setTitle("Prueba titulo");
		pdd.setCreator("Altiuz Report");
		pdd.setSubject("Ejemplo documento");
		Calendar date = new GregorianCalendar();
		date.set(2021, 11, 5);
		pdd.setCreationDate(date);
		date.set(2021, 6, 5);
		pdd.setModificationDate(date);
		pdd.setKeywords("Muestra, primer ejemplo, pdf");
		document.save("C:\\Users\\jhernandez\\Desktop\\reportes\\doc_atributes.pdf");
		System.out.println("Properties added successfully ");
		document.close();
		return null;

	}

	private static void concatPdfBox(final byte[] mainPdf, final byte[] newPd) throws IOException {

		// Instantiating PDFMergerUtility class
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Setting the destination file

		// adding the source files
		PDFmerger.addSource(new ByteArrayInputStream(mainPdf));
		PDFmerger.addSource(new ByteArrayInputStream(newPd));
		//PDFmerger.setDestinationFileName("C:\\Users\\jhernandez\\Desktop\\reportes\\merge.pdf");
		// Merging the two documents
		PDFmerger.setDestinationStream(baos);
		PDFmerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
		//OutputStream out = new FileOutputStream("C:\\Users\\jhernandez\\Desktop\\pdfMetadata33.pdf");
		//out.write(baos.toByteArray());
		//out.close();
		System.out.println(baos);
		System.out.println(baos.toByteArray());

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

}
