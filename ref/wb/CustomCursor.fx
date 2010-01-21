import java.awt.Point;
import java.awt.Toolkit;

import javafx.scene.Cursor;

import javafx.scene.image.Image;

public def PENCIL = CustomCursor{
    url: "{__DIR__}pencil.gif"
    name: "open"
}

public class CustomCursor extends Cursor{
    public-read var cursorAWT: java.awt.Cursor;

    var url: String;
    var name: String;

    init
    {
		println (url);
		def imageAWT = Image { url: url }.platformImage as java.awt.Image;
		def toolkit = Toolkit.getDefaultToolkit ();
		cursorAWT = toolkit.createCustomCursor (imageAWT, new Point (0, 0), name)
    }
}