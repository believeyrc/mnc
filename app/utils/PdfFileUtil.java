package utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.PdfFile;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import play.Logger;
import play.Play;
import play.libs.Codec;

public class PdfFileUtil {
	public static String getPathForPdf() {
		String uuid = Codec.UUID();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tDate = sdf.format(new Date());
		String filePath = "public/books/" + tDate + "/" + uuid + "/file.pdf";
		return filePath;
	}

	public static File getPageFile(PdfFile file, int page, int scale) {
		String staticpath = Play.configuration.getProperty("staticpath", "");
		String pageImagePath = getPageImagePath(file, page, scale, false);
		Logger.debug("process %s", staticpath + pageImagePath);
		File pageFile = new File(staticpath + pageImagePath);
		if (pageFile.exists()) {
			System.out.println("exists=============");
			return pageFile;
		}
		processPdfPage(file, scale, page);
		return getPageFile(file, page, scale);
	}

	public static void processPdfPage(PdfFile file, int scale, int page) {
		processPages(file, scale, page, page + 1);
	}

	public static void processPdfFile(PdfFile file, int scale) {
		processPages(file, scale, -1, -1);
	}

	private static void processPages(PdfFile file, int dpi, int start, int end) {
		try {
			Document document = new Document();
			String staticpath = Play.configuration.getProperty("staticpath", "");
			Logger.debug("process %s", staticpath + file.originalFile);
			document.setFile(staticpath + file.originalFile);
			int numberOfPages = document.getNumberOfPages();
			boolean thumbnail = false;
			if (start < 0 && end < 0) {
				file.numPages = numberOfPages;
				file.save();
				thumbnail = true;
			}
			if (end < 0) {
				end = numberOfPages;
			}
			if (start < 0) {
				start = 0;
			}
			Logger.debug("process %s which has pages %s", file.title, numberOfPages);
			for (int i = start; i < end; i++) {
				processPage(file, document, i, dpi, thumbnail);
			}
		} catch (PDFException e) {
			e.printStackTrace();
		} catch (PDFSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void processPage(PdfFile file, Document document, int i, int scaleInt, boolean isthumbnail) {
		float scale = scaleInt / 100.0f;
		float rotation = 0f;

		PDimension size = document.getPageDimension(i, rotation, scale);

		// double dpi = Math.sqrt((size.getWidth() * size.getWidth()) +
		// (size.getHeight() * size.getHeight()))
		// / Math.sqrt((8.5 * 8.5) + (11 * 11));
		// if (dpi < (targetDPI - 0.1)) {
		// scale = (float) (targetDPI / dpi);
		size = document.getPageDimension(i, rotation, scale);
		// }

		int pageWidth = (int) (size.getWidth());
		int pageHeight = (int) (size.getHeight());
		BufferedImage image = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = image.createGraphics();
		document.paintPage(i, g, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, scale);
		g.dispose();
		if (!isthumbnail) {
			String pageImage = getPageImagePath(file, i, scaleInt, isthumbnail);
			ImageUtil.saveJPEG(image, pageImage);
			Logger.debug("Page File Path: %s", pageImage);
		} else {
			String pageThumbnail = getPageImagePath(file, i, scaleInt, isthumbnail);
			BufferedImage thumbnail = ImageUtil.thumbnail(image, 140, 160, false, false);
			ImageUtil.saveJPEG(thumbnail, pageThumbnail);
			Logger.debug("Page Thumbnail Path: %s", pageThumbnail);
			models.Page page = models.Page.find("file= ? and page = ?", file, i).first();
			if (page == null) {
				page = new models.Page();
				page.file = file;
				page.page = i;
			}
			page.content = document.getPageText(i).toString();
			page.width = pageWidth;
			page.height = pageHeight;
			page.save();
		}
		Logger.debug("process page %s", i);
	}

	private static String getPageImagePath(PdfFile file, int i, int scaleInt, boolean thumbnail) {
		if (thumbnail)
			return String.format("%s%s_thumbnail.jpg", FileUtil.getPath(file.originalFile), i);
		if (scaleInt == 100)
			return String.format("%s%s.jpg", FileUtil.getPath(file.originalFile), i);
		return String.format("%s%s_%s.jpg", FileUtil.getPath(file.originalFile), i, scaleInt);
	}
}
