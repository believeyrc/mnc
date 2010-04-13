package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Page extends Model {
	@ManyToOne
	public PdfFile file;
	@Lob
	public String content;
	public int page;
}
