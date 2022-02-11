package groupproject;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

/**
 * Represents a bounding box projected on to an axis
 * Used by the bounding box test
 */
public class OrientedBoundingBox {
	private Vector2f points[];
	private float min;
	private float max;

	public OrientedBoundingBox(Sprite sprite) {
		min = 0;
		max = 0;
		points = new Vector2f[4];
		Transform transform = sprite.getTransform();
		IntRect local = sprite.getTextureRect();
		points[0] = transform.transformPoint(0.f, 0.f);
		points[1] = transform.transformPoint(local.width, 0.f);
		points[2] = transform.transformPoint(local.width, local.height);
		points[3] = transform.transformPoint(0.f, local.height);
	}

	public void projectOntoAxis(Vector2f axis) {
		min = (points[0].x*axis.x+points[0].y*axis.y);
		max = min;
		for (int i=1; i<4; i++) {
			float projection = points[i].x*axis.x+points[i].y*axis.y;
			if (projection<min) min = projection;
			if (projection>max) max = projection;
		}
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public Vector2f getPoints(int index) {
		return points[index];
	}
}
