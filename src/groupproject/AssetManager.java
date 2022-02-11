package groupproject;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Manager class for handling assets
 */
public class AssetManager {

	private HashMap<String, Texture> textures; //HashMap mapping a String to a Texture
	private HashMap<String, Font> fonts; //HashMap mapping a String to a Font
	private HashMap<Texture, Integer[]> bitmasks; //HashMap mapping a Texture to a bitmask

	public AssetManager() {
		textures = new HashMap<>();
		fonts = new HashMap<>();
		bitmasks = new HashMap<>();
		loadTexture("default",Constants.DEFAULT_TEXTURE_FILEPATH); //loads the default texture
	}

	/**
	 * Loads a texture into the map and assigns it a name and creates a bitmask
	 * @param name name to assign to the texture
	 * @param fileName filepath holding the texture
	 */
	public void loadTexture(String name, String fileName) {
		if (name == null || fileName == null) return;
		Texture texture = new Texture();
		Image image = new Image();
		Path filePath = Paths.get(fileName);
		try {
			image.loadFromFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			texture.loadFromImage(image);
		} catch (TextureCreationException e) {
			e.printStackTrace();
			return;
		}
		if (!bitmasks.containsKey(texture)) {
			createMask(texture, image);
		}
		textures.put(name, texture);
	}

	/**
	 * Gets the texture with matching name or if none are found returns the default texture
	 * @param name name of texture
	 * @return texture mapped to name
	 */
	public Texture getTexture(String name) {
		return textures.getOrDefault(name,textures.get("default"));
	}

	/**
	 * Loads a font into the map and assigns it a name
	 * @param name name to assign to the texture
	 * @param fileName filepath holding the texture
	 */
	public void loadFont(String name, String fileName) {
		if (name == null || fileName == null) return;
		Font font = new Font();
		Path filePath = Paths.get(fileName);
		try {
			font.loadFromFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		fonts.put(name, font);
	}

	/**
	 * Gets the font with matching name
	 * @param name name of font
	 * @return font mapped to name
	 */
	public Font getFont(String name) {
		return fonts.get(name);
	}

	/**
	 * Gets the alpha value of a pixel in the given bitmask
	 * @param mask bitmask to get the alpha of
	 * @param texture texture to check the size of
	 * @param x x coordinate relative to the top left corner of the bitmask
	 * @param y y coordinate relative to the top left corner of the bitmask
	 * @return
	 */
	public Integer getPixel(Integer mask[], Texture texture, int x, int y) {
		if (x > texture.getSize().x || y > texture.getSize().y) {
			return 0;
		}
		return mask[x+y*texture.getSize().x];
	}

	/**
	 * Gets the bitmask mapped to the given texture, if none are found a new mask is made
	 * @param texture texture to get the bitmask for
	 * @return bitmask associated with the texture
	 */
	public Integer[] getMask(Texture texture) {
		Integer mask[];
		if (!bitmasks.containsKey(texture)) {
			Image image = texture.copyToImage();
			mask = createMask(texture, image);
		} else {
			mask = bitmasks.get(texture);
		}
		return mask;
	}

	/**
	 * Creates a bitmask for a given texture by making an array to store the alpha
	 * of each pixel in a texture
	 * @param texture texture to map to the new bitmask
	 * @param image image to make the bitmask of
	 * @return bitmask for the given image
	 */
	public Integer[] createMask(Texture texture, Image image) {
		Integer mask[] = new Integer[texture.getSize().y*texture.getSize().x];
		for (int y=0; y<texture.getSize().y; y++) {
			for (int x=0; x<texture.getSize().x; x++) {
				mask[x+y*texture.getSize().x] = image.getPixel(x,y).a;
			}
		}
		bitmasks.put(texture, mask);
		return mask;
	}

	/**
	 * Checks if any of the pixels above the alpha limit are intersecting between the 2 given sprites
	 * @param sprite1 first sprite to check for collision
	 * @param sprite2 second sprite to check for collision
	 * @return are sprite1 and sprite2 colliding
	 */
	public boolean pixelTest(Sprite sprite1, Sprite sprite2) {
		FloatRect intersection = sprite1.getGlobalBounds().intersection(sprite2.getGlobalBounds());
		if (intersection != null) {
			IntRect subRect1 = sprite1.getTextureRect();
			IntRect subRect2 = sprite2.getTextureRect();
			Integer mask1[] = getMask(new Texture(sprite1.getTexture()));
			Integer mask2[] = getMask(new Texture(sprite2.getTexture()));
			for (int i=(int)intersection.left; i<intersection.left+intersection.width; i++) {
				for (int j=(int)intersection.top; j<intersection.top+intersection.height; j++) {
					Vector2f vector1 = sprite1.getInverseTransform().transformPoint(i, j);
					Vector2f vector2 = sprite2.getInverseTransform().transformPoint(i, j);
					if (vector1.x > 0 && vector1.y > 0 && vector2.x > 0 && vector2.y > 0
							&& vector1.x < subRect1.width && vector1.y < subRect1.height
							&& vector2.x < subRect2.width && vector2.y < subRect2.height) {
						if (getPixel(mask1, new Texture(sprite1.getTexture()),
								(int)(vector1.x)+subRect1.left, (int)(vector1.y)+subRect1.top)
								> Constants.COLLISION_ALPHA_LIMIT && getPixel(mask2,
								new Texture(sprite2.getTexture()), (int)(vector2.x)+subRect2.left,
								(int)(vector2.y)+subRect2.top) > Constants.COLLISION_ALPHA_LIMIT) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the bounding box of 2 given sprites are intersecting
	 * Uses Separating Axis Theorem
	 * @param sprite1 first sprite to check for collision
	 * @param sprite2 second sprite to check for collision
	 * @return are sprite1 and sprite2 colliding
	 */
	public boolean boundingBoxTest(Sprite sprite1, Sprite sprite2) {
		OrientedBoundingBox obb1 = new OrientedBoundingBox(sprite1);
		OrientedBoundingBox obb2 = new OrientedBoundingBox(sprite2);
		Vector2f axes[] = new Vector2f[4];
		axes[0] = new Vector2f(obb1.getPoints(1).x - obb1.getPoints(0).x,
				obb1.getPoints(1).y - obb1.getPoints(0).y);
		axes[1] = new Vector2f(obb1.getPoints(1).x-obb1.getPoints(2).x,
				obb1.getPoints(1).y-obb1.getPoints(2).y);
		axes[2] = new Vector2f(obb2.getPoints(0).x - obb2.getPoints(3).x,
				obb2.getPoints(0).y - obb2.getPoints(3).y);
		axes[3] = new Vector2f(obb2.getPoints(0).x-obb2.getPoints(1).x,
				obb2.getPoints(0).y-obb2.getPoints(1).y);
		for (int i=0; i<axes.length; i++) {
			obb1.projectOntoAxis(axes[i]);
			obb2.projectOntoAxis(axes[i]);
			if (!((obb2.getMin() <= obb1.getMax()) && (obb2.getMax() >= obb1.getMin()))) return false;
		}
		return true;
	}
}
