import java.util.ArrayList;
import java.util.HashMap;

public class TableController {
	private static HashMap<Day, ArrayList<Table>> tableController = new HashMap<>();
	
	public static ArrayList<Table> getTable(Day d) {
		return tableController.get(d);
	}
	
	public static void putTable(Day d, ArrayList<Table> tList) {
		tableController.put(d, tList);
	}
}