import javafx.scene.Cursor;

import com.sun.javafx.tk.swing.SwingToolkit;

public class CursorToolkit extends SwingToolkit
{
    public override function convertCursorFromFX (cursor: Cursor)
    {
		println ("call it")
        if (cursor instanceof CustomCursor){
			println ("got it")
            return (cursor as CustomCursor).cursorAWT;
		}
        return super.convertCursorFromFX (cursor) // use predefined AWT cursor
    }
}