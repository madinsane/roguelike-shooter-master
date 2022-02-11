package groupproject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A Quadtree used for collision detection
 */
public class Quadtree {
	private static final int MAX_OBJECTS = 10; //Defines how many objects a node can hold before splitting
	private static final int MAX_LEVELS = 5; //Defines deepest level subnode

	private int level; //Current node level
	private List<Entity> objects;
	private Rectangle bounds;
	private Quadtree[] nodes; //Subnodes of the tree

	public Quadtree(int pLevel, Rectangle pBounds) {
		level = pLevel;
		objects = new ArrayList<>();
		bounds = pBounds;
		nodes = new Quadtree[4];
	}

	/**
	 * Clears the quadtree recursively
	 */
	public void clear() {
		objects.clear();
		for (int i=0; i<nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	/**
	 * Splits the node into 4 subnodes
	 */
	private void split() {
		int subWidth = (int)(bounds.getWidth() / 2);
		int subHeight = (int)(bounds.getHeight() / 2);
		int x = (int)bounds.getX();
		int y = (int)bounds.getY();
		nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y , subWidth, subHeight));
		nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
		nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
		nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}

	/**
	 * Gets the index of the node an object belongs to
	 * @param pRect Object to find the index of
	 * @return index of the node the object belongs to
	 */
	private int getIndex(Entity pRect) {
		int index = -1;
		double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

		//Object completely fits within top quadrants
		boolean topQuadrant = (pRect.getYpos()-pRect.getYorigin() < horizontalMidpoint && pRect.getYpos()-pRect.getYorigin() + pRect.getHeight() < horizontalMidpoint);
		//Object completely fits within bottom quadrants
		boolean bottomQuadrant = (pRect.getYpos()-pRect.getYorigin() > horizontalMidpoint);
		//Object completely fits within left quadrants
		if (pRect.getXpos()-pRect.getXorigin() < verticalMidpoint && pRect.getXpos()-pRect.getXorigin() + pRect.getWidth() < verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		}
		//Object completely fits within right quadrants
		else if (pRect.getXpos()-pRect.getXorigin() > verticalMidpoint && pRect.getXpos()-pRect.getXorigin() + pRect.getWidth() > verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}
		
		return index;
	}

	/**
	 * Inserts an object into the tree
	 * @param pRect object to insert
	 */
	public void insert(Entity pRect) {
		if (nodes[0] != null) {
			int index = getIndex(pRect);
			if (index != -1) {
				nodes[index].insert(pRect);
				return;
			}
		}
		objects.add(pRect);
		
		if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					nodes[index].insert(objects.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * Returns all objects that can collide with the given object
	 * @param returnObjects List of objects that could be collided with
	 * @param pRect Object to check around
	 * @return List of objects that could be collided with
	 */
	public List<Entity> retrieve(List<Entity> returnObjects, Entity pRect) {
		int index = getIndex(pRect);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, pRect);
		}
		returnObjects.addAll(objects);

		return returnObjects;
	}
}