package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import play.db.jpa.Model;

@Entity
public class PdfFile extends Model {
	public String title;
	public Date uploadDate;
	public String originalFile;

	@PrePersist
	public void onCreate() {
		if (uploadDate == null) {
			uploadDate = new Date();
		}
	}
}
