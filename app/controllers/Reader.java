package controllers;

import java.io.File;
import java.util.List;

import models.Page;
import models.PdfFile;
import play.db.jpa.JPASupport;
import play.jobs.Job;
import utils.FileUtil;
import utils.PdfFileUtil;

public class Reader extends Basez {
	public static void upload(File upload) {
		String pathForPdf = PdfFileUtil.getPathForPdf();
		FileUtil.moveUploadTo(upload, pathForPdf);
		final PdfFile pdfFile = new PdfFile();
		pdfFile.title = upload.getName();
		pdfFile.originalFile = pathForPdf;
		pdfFile.save();
		new Job<Void>() {
			public void doJob() throws Exception {
				PdfFileUtil.processPdfFile(pdfFile, 100);
			};
		}.in(0);
		list();
	}

	public static void page(Long id, int page, int scale) {
		PdfFile file = PdfFile.findById(id);
		File imgFile = PdfFileUtil.getPageFile(file, page, scale);
		renderBinary(imgFile);
	}

	public static void list() {
		List<PdfFile> files = PdfFile.all().fetch();
		render(files);
	}

	public static void reprocess(Long id) {
		PdfFile file = PdfFile.findById(id);
		PdfFileUtil.processPdfFile(file, 100);
	}
	
	public static void view(Long id) {
		PdfFile file = PdfFile.findById(id);
		List<Page> pages = Page.find("file = ? order by page asc", file).fetch();
		render(file, pages);
	}
}
