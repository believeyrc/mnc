package test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;

import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.KeyMapper;

public abstract class JPCTGame {

	protected Frame frame;
	protected int width = 400;
	protected int height = 300;
	protected FrameBuffer buffer = null;
	protected World world = null;
	protected Color backgroundColor;
	protected Canvas canvas;
	protected Timer timmer;

	protected void init() {
		initFrame();
		initWorld();
	}

	protected void initWorld() {
		backgroundColor = Color.black;
		world = new World();
		world.setAmbientLight(0, 255, 0);
		world.getCamera().setPosition(new SimpleVector(0, -50, 0));
		world.getCamera().lookAt(new SimpleVector(0,0,0));
		initGame();
	}

	protected abstract void initGame();

	protected void initFrame() {
		frame = new Frame();
		frame.setTitle("jPCT " + Config.getVersion());
		frame.pack();
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);
		buffer = new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		canvas = buffer.enableGLCanvasRenderer();
		frame.add(canvas);
		frame.pack();
		keyMapper = new KeyMapper(frame);
		timmer = new Timer(1000 / speed);
	}

	protected int speed = 120;

	public void start() {
		init();
		timmer.start();
		mainloop();
	}

	protected void updateFrame() {
		world.renderScene(buffer);
		if (backgroundColor != null)
			buffer.clear(backgroundColor);
		world.draw(buffer);
		buffer.update();
		buffer.displayGLOnly();
		canvas.repaint();
	}

	protected abstract void update();

	private boolean exit;
	private KeyMapper keyMapper = null;

	private void mainloop() {
		while (!exit) {
			update();
			updateFrame();
		}
		keyMapper.destroy();
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		frame.dispose();
	}
}
