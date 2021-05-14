package cl.altiuz.replace_itext_bch;

import cl.altiuz.replace_itext_bch.utils.Utils;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.dom4j.DocumentException;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.xml.xmp.XmpWriter;

public class App {
	private static final String PDF_PROP_CREATOR = "Creator";
	private static final String PDF_PROP_AUTHOR = "Author";
	private static final Path RESOURCE_DIRECTORY = Paths.get("src", "main", "resources");
	private static final ClassLoader CLASS_LOADER = App.class.getClassLoader();

	public static void main(String[] args) throws IOException {
		try {
			System.out.println("Proyecto demo para remplazar libreria ItextPdf por PdfBox en AR");
			Desktop.getDesktop().open(new File(RESOURCE_DIRECTORY.toString()));
			initItextPdf();
			initPdfBox();
			System.out.println("--END--");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	private static void initItextPdf() throws IOException, DocumentException, com.itextpdf.text.DocumentException {
		System.out.println("--Init ItextPdf--");
		String pathFul = URLDecoder.decode(CLASS_LOADER.getResource("sample.pdf").getPath().substring(1), "UTF-8");
		String pathOutput = URLDecoder.decode(CLASS_LOADER.getResource("sample2.pdf").getPath().substring(1), "UTF-8");
		byte[] pdfFull = Utils.convertPDFToByteArray(pathFul);
		byte[] output = Utils.convertPDFToByteArray(pathOutput);
		concatPdfItext(pdfFull, output);
		getPdfItext();
	}

	private static void initPdfBox() throws IOException, DocumentException {
		System.out.println("--Init PdfBox--");
		String pathFul = URLDecoder.decode(CLASS_LOADER.getResource("sample.pdf").getPath().substring(1), "UTF-8");
		String pathOutput = URLDecoder.decode(CLASS_LOADER.getResource("sample2.pdf").getPath().substring(1), "UTF-8");
		byte[] pdfFull = Utils.convertPDFToByteArray(pathFul);
		byte[] output = Utils.convertPDFToByteArray(pathOutput);
		concatPdfBox(pdfFull, output);
		editMetadataPdfBox();
	}

	/*
	 * Lib ItextPdf
	 */
	private static byte[] concatPdfItext(final byte[] mainPdf, final byte[] newPdf)
			throws IOException, DocumentException, com.itextpdf.text.DocumentException {
		final PdfReader mainReader = new PdfReader(new ByteArrayInputStream(mainPdf));
		final PdfReader newReader = new PdfReader(new ByteArrayInputStream(newPdf));
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final Document document = new Document();
		final PdfSmartCopy copy = new PdfSmartCopy(document, baos);
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
		OutputStream out = new FileOutputStream(RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergeItext.pdf");
		out.write(baos.toByteArray());
		out.close();
		System.out.println("PDF Concatenado: " + RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergeItext.pdf");
		return baos.toByteArray();
	}

	public static byte[] getPdfItext() throws IOException, com.itextpdf.text.DocumentException {
		byte[] pdfFull = null;
		/* if (pdfFull == null) { System.out.println("pdf no habilitado"); } */
		final PdfReader reader = new PdfReader(RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergeItext.pdf");
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
		OutputStream out = new FileOutputStream(RESOURCE_DIRECTORY.toAbsolutePath() + "\\pdfMetadataItext.pdf");
		System.out.println("PDF con Metadata: " + RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergeItext.pdf");
		out.write(pdfFull);
		out.close();
		return pdfFull;
	}

	/*
	 * Lib PdfBox
	 */
	private static byte[] concatPdfBox(final byte[] mainPdf, final byte[] newPd) throws IOException {
		// Instantiating PDFMergerUtility class
		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		// adding the source files
		PDFmerger.addSource(new ByteArrayInputStream(mainPdf));
		PDFmerger.addSource(new ByteArrayInputStream(newPd));
		// Setting the destination file
		PDFmerger.setDestinationFileName(RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergePdfBox.pdf");
		// Merging the two documents
		PDFmerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
		System.out.println("PDF Concatenado: " + PDFmerger.getDestinationFileName());
		// Reading merged document and converting it to Byte []
		PDDocument mergedPdf = PDDocument.load(new File(RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergePdfBox.pdf"));
		PDStream contents = new PDStream(mergedPdf);
		return contents.toByteArray();
	}

	private static byte[] editMetadataPdfBox() throws IOException {
		PDDocument document = PDDocument.load(new File(RESOURCE_DIRECTORY.toAbsolutePath() + "\\mergePdfBox.pdf"));
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
		document.save(RESOURCE_DIRECTORY.toAbsolutePath() + "\\pdfMetadataPdfBox.pdf");
		System.out.println("PDF con Metadata: " + RESOURCE_DIRECTORY.toAbsolutePath() + "\\pdfMetadataPdfBox.pdf");
		document.close();
		return null;

	}

}
