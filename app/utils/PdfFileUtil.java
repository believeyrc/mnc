package utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
		String filePath = "public/books/" + tDate + "/" + uuid+"/file.pdf";
		return filePath;
	}
	public static void processPdfFile(PdfFile file) {
		try {
			Document document = new Document();
			String staticpath = Play.configuration.getProperty("staticpath", "");
			Logger.debug("process %s", staticpath + file.originalFile);
			document.setFile(staticpath + file.originalFile);
			Logger.debug("process %s which has pages %s", file.title,document.getNumberOfPages());
			for (int i = 0; i < document.getNumberOfPages(); i++) {
				float scale = 1.0f;
				float rotation = 0f;
				PDimension size = document.getPageDimension(i, rotation, scale);
				int pageWidth = (int) (size.getWidth());
				int pageHeight = (int) (size.getHeight());
				BufferedImage image = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g = image.createGraphics();
				document.paintPage(i, g, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, scale);
				g.dispose();
				String pageImage = String.format("%s%s.jpg", FileUtil.getPath(file.originalFile), i);
				ImageUtil.saveJPEG(image, pageImage);
				Logger.debug("Page File Path: %s", pageImage);
				
				String pageThumbnail = String.format("%s%s_thumbnail.jpg", FileUtil.getPath(file.originalFile), i);
				BufferedImage thumbnail = ImageUtil.thumbnail(image, 140,160, false, false);
				ImageUtil.saveJPEG(thumbnail, pageThumbnail);
				Logger.debug("Page Thumbnail Path: %s", pageThumbnail);
				models.Page page = new models.Page();
				page.content = document.getPageText(i).toString();
				page.file = file;
				page.page = i;
				page.save();
				Logger.debug("process page %s", i);
			}
		} catch (PDFException e) {
			e.printStackTrace();
		} catch (PDFSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
