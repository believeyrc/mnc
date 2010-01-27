package test;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class SampleBox extends JPCTGame {
	private Object3D box;

	public static void main(String[] args) {
		SampleBox game = new SampleBox();
		game.start();
	}

	@Override
	protected void initGame() {
		TextureManager.getInstance().addTexture("box", new Texture("box.jpg"));
		box = Primitives.getBox(10f, 1f);
		box.setTexture("box");
		box.setEnvmapped(Object3D.ENVMAP_ENABLED);
		box.build();
		world.addObject(box);
	}

	public SampleBox() {
		speed = 60;
	}

	@Override
	protected void update() {
		long elapsedTicks = timmer.getElapsedTicks();
		if (elapsedTicks > 0)
			box.rotateY(0.01f * elapsedTicks);
	}
}
