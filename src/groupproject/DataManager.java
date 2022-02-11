package groupproject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manager class for handling external data files in csv format
 */
public class DataManager {
	private ArrayList<String[]> output;
	private HashMap<String, ArrayList<String[]>> dataMap;
	private HashMap<String, Triplet<Integer, Integer, Boolean>[]> animations;

	public DataManager() {
		dataMap = new HashMap<>();
		output = new ArrayList<>();
		animations = new HashMap<>();
	}

	/**
	 * Gets the animation set for a named animation
	 * @param name name of animation corresponding to row name in animations.csv
	 * @return Triplet array containing (sprite position, duration in frames, flip)
	 */
	public Triplet<Integer, Integer, Boolean>[] getAnimations(String name) {
		if (animations.containsKey(name))
			return animations.get(name);
		String[] rowData = getRowData("animations", name);
		Triplet<Integer, Integer, Boolean>[] triplets = new Triplet[rowData.length/3];
		for (int i=1; i<rowData.length; i+=3) {
			triplets[i/3] = new Triplet<>(Integer.parseInt(rowData[i]),Integer.parseInt(rowData[i+1]), Boolean.parseBoolean(rowData[i+2]));
		}
		animations.put(name, triplets);
		return triplets;
	}

	/**
	 * Gets the list of enemies as a Pair array
	 * @param column which column number to get the data from
	 * @param ignoreBoss how many bosses to ignore
	 * @return Pair array containing (enemy name, data from column)
	 */
	public Pair<String, Integer>[] getEnemies(int column, int ignoreBoss) {
		ArrayList<String[]> enemyListFull = getData("enemies");
		Pair<String, Integer>[] enemyList = new Pair[enemyListFull.size()-2-ignoreBoss];
		for (int i=2; i<enemyListFull.size()-ignoreBoss; i++) {
			String[] temp = enemyListFull.get(i);
			enemyList[i-2] = new Pair<>(temp[0], Integer.parseInt(temp[column]));
		}
		return enemyList;
	}

	/**
	 * Gets the item list from items.csv
	 * @return String array containing all droppable item names
	 */
	public String[] getItems() {
		ArrayList<String[]> itemListFull = getData("items");
		String[] itemList = new String[itemListFull.size()-2];
		for (int i=2; i<itemListFull.size(); i++) {
			String[] temp = itemListFull.get(i);
			itemList[i-2] = temp[0];
		}
		return itemList;
	}

	/**
	 * Saves the player's current passives into passives.csv updating the count column
	 * @param passives ArrayList array containing all rows for the passive data
	 */
	public void savePassives(ArrayList<String>[] passives) {
		ArrayList<String[]> passiveListFull = getData("passives");
		String[] data = new String[passiveListFull.size()];
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<passiveListFull.size(); i++) {
			String[] row = passiveListFull.get(i);
			if (i != 0) {
				row[1] = passives[i-1].get(1);
			}
			for (int j=0; j<row.length; j++) {
				sb.append(row[j]);
				sb.append(Constants.DATA_SEPARATOR);
			}
			data[i] = sb.toString();
			sb.delete(0, sb.length());
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.DATA_PASSIVES_FILEPATH))) {
			for (int i=0; i<data.length; i++) {
				bw.write(data[i], 0, data[i].length());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads data from a file at filepath and stores it in a HashMap by name.
	 * Only call this method once per file
	 * @param name Name to associate with data
	 * @param filePath Filepath to get data from
	 */
	public void readFromFile(String name, String filePath) {
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((line = br.readLine()) != null) {
				output.add(line.split(Constants.DATA_SEPARATOR));
			}
			dataMap.put(name,new ArrayList<>(output));
			output.clear();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all the data from a specific file
	 * @param name Name that was used when the file was loaded
	 * @return Array list containing all the data from file
	 */
	public ArrayList<String[]> getData(String name) {
		return dataMap.get(name);
	}

	/**
	 * Gets the all the data from a specific row from a specific file
	 * @param name Name that was used when the file was loaded
	 * @param rowName Name of the row in the file
	 * @return String array of row data
	 */
	public String[] getRowData(String name, String rowName) {
		if (isDataLoaded(name)) {
			ArrayList<String[]> dataSet = getData(name);
			int rowNum = -1;
			for (int i = 0; i < dataSet.size(); i++) {
				String[] row = dataSet.get(i);
				if (row[0].equals(rowName)) {
					rowNum = i;
					break;
				}
			}
			if (rowNum > -1) {
				return dataSet.get(rowNum);
			}
		}
		return null;
	}

	/**
	 * Gets a specific cell of data from a file
	 * @param name Name that was used when the file was loaded
	 * @param rowName Name of the row in the file
	 * @param columnName Name of the column in the file
	 * @return String containing row data
	 */
	public String getSpecificData(String name, String rowName, String columnName) {
		String[] row = getRowData(name,rowName);
		if (row != null) {
			ArrayList<String[]> dataSet = getData(name);
			int columnNum = -1;
			String[] header = dataSet.get(0);
			for (int i=0; i<dataSet.get(0).length; i++) {
				if (header[i].equals(columnName)) {
					columnNum = i;
					break;
				}
			}
			if (columnNum > -1) {
				return row[columnNum];
			}
		}
		return null;
	}

	/**
	 * Checks if there is a file loaded with a given name
	 * @param name Name that was used when the file was loaded
	 * @return true if key matches, false otherwise
	 */
	public boolean isDataLoaded(String name) {
		return dataMap.containsKey(name);
	}
}
