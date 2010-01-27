package test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;

import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

public class JPCTGame {
	public static void main(String[] args) {
		JPCTGame game = new JPCTGame();
		game.init();
		game.start();
	}

	private Frame frame;
	private int width = 400;
	private int height = 300;
	private FrameBuffer buffer = null;
	private World world = null;
	private Color backgroundColor;
	private Canvas canvas;
	private Timer timmer;

	protected void init() {
		initFrame();
		initGame();
	}

	protected Object3D box;

	protected void initGame() {
		backgroundColor = Color.black;
		world = new World();
		world.setAmbientLight(0, 255, 0);
		box = Primitives.getBox(5f, 1f);
		box.build();
		world.addObject(box);
		world.getCamera().setPosition(new SimpleVector(0, -50, 0));
		world.getCamera().lookAt(box.getTransformedCenter());
	}

	protected void initFrame() {
		frame = new Frame();
		frame.setTitle("jPCT " + Config.getVersion());
		frame.pack();
		// Insets insets = frame.getInsets();
		// int titleBarHeight = insets.top;
		// int leftBorderWidth = insets.left;
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);
		buffer = new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		canvas = buffer.enableGLCanvasRenderer();
		frame.add(canvas);
		frame.pack();

		timmer = new Timer(1000 / speed);

	}

	protected int speed = 120;

	public void start() {
		timmer.start();
		mainloop();
	}

	protected void updateFrame() {
		world.renderScene(buffer);
		buffer.clear(backgroundColor);
		world.draw(buffer);
		buffer.update();
		buffer.displayGLOnly();
		canvas.repaint();
	}

	private void update() {
		long elapsedTicks = timmer.getElapsedTicks();
		if (elapsedTicks > 0)
			box.rotateY(0.01f * elapsedTicks);
	}

	private boolean exit;

	private void mainloop() {
		while (!exit) {
			update();
			updateFrame();
		}
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		frame.dispose();
	}
}
