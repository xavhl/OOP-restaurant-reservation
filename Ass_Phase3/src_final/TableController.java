import java.util.ArrayList;
import java.util.HashMap;

public class TableController {
	private static HashMap<Day, ArrayList<Table>> tableController = new HashMap<>();
	
	public static void setupTable(Day dineDate) {
		if(tableController.get(dineDate)==null) {
			ArrayList<Table> tableListOfADay = new ArrayList<>();

			for(int i=0;i<10;i++) {
				String tableName = "T";
				if(i<9)
					tableName += "0";
				tableName += Integer.toString(i+1);
				tableListOfADay.add(new Table(tableName));
			}
			for(int i=0;i<6;i++)			
				tableListOfADay.add(new Table(String.format("F0%s", Integer.toString(i+1))));
			for(int i=0;i<3;i++)
				tableListOfADay.add(new Table(String.format("H0%s", Integer.toString(i+1))));
			
			tableController.put(dineDate, tableListOfADay);
		}
	}
	
	public static void reserveTable(Day dineDate, ArrayList<String> tableNameList, int ticketCode) {
		ArrayList<Table> mainList = tableController.get(dineDate);
		
		for(String tableName : tableNameList) {
			for(Table t : mainList) {
				if(tableName.equals(t.toString()))
					t.reserve(ticketCode);
			}
		}
	}
	
	public static void unreserveTable(Day dineDate, ArrayList<String> tableNameList) {
		ArrayList<Table> mainList = tableController.get(dineDate);
		
		for(String tableName : tableNameList) {
			for(Table t : mainList) {
				if(tableName.equals(t.toString()))
					t.unreserve();
			}
		}
	}
	
	public static void assignTable(Reservation r, ArrayList<String> requestedTables) throws ExTableAlreadyReserved, ExTableNotFound, ExNotEnoughSeats {
		int noOfDiners = r.getTotalPersons();
		ArrayList<Table> mainList = tableController.get(r.getDineDate());
		
		for(String reqTName : requestedTables) {//first nested loop for validation
			boolean isFound = false;
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					isFound = true;
					
					if(t.ticketCode!=0)
						throw new ExTableAlreadyReserved(t.toString());
					else
						noOfDiners -= t.capacity;
				}
				if(noOfDiners<=0)	break;
			}
			if(!isFound) throw new ExTableNotFound(reqTName);
		}
		if(noOfDiners>0)	throw new ExNotEnoughSeats();
		
		for(String reqTName : requestedTables) {//second nested loop for assignment
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
	}
	
	public static void reassignTable(Reservation r, ArrayList<String> requestedTables) {
		ArrayList<Table> mainList = tableController.get(r.getDineDate());
		
		for(String reqTName : requestedTables) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
	}
	
	public static void unassignTable(Reservation r, ArrayList<String> requestedTables) {
		ArrayList<Table> mainList = tableController.get(r.getDineDate());
		
		for(String reqTName : r.getTableNames()) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.unreserve();
					r.returnTable(t);
				}
			}
		}
	}
	
	public static void listTableAllocations(Day dineDate) {
		ArrayList<Table> mainList = tableController.get(dineDate);
		
		boolean isAllocatedListEmpty = true;
		System.out.println("Allocated tables:");
		
		for(Table t : mainList) {
			if(t.ticketCode!=0) {
				System.out.println(String.format("%s (Ticket %d)", t.name, t.ticketCode));
				isAllocatedListEmpty = false;
			}
		}
		if(isAllocatedListEmpty)	System.out.println("[None]");
		
		System.out.println();
		
		boolean isAvailableListEmpty = true;
		System.out.println("Available tables:");
		
		for(Table t : mainList) {
			if(t.ticketCode==0) {
				System.out.print(t.toString() + " ");
				isAvailableListEmpty = false;
			}
		}
		if(isAvailableListEmpty)	System.out.println("[None]");

		System.out.println();
	}
	
	public static void suggestTable(Reservation r) {
		ArrayList<Table> tableOfThatDay = tableController.get(r.getDineDate());
		ArrayList<String> suggestionRecord = new ArrayList<>();
		
		int noOfPersons = r.getTotalPersons();
		System.out.printf("Suggestion for %d persons:", noOfPersons);
		
		int noOfPersonsUnserved = noOfPersons;
		boolean enoughTables = true;
		
		while(noOfPersonsUnserved>0) {
			int maxCap = 0;
			String tableName = null;
			
			for(Table t : tableOfThatDay) {
				if(t.ticketCode!=0)//skip if this table is reserved
					continue;
				if(suggestionRecord.contains(t.name))//skip if this table is already on the list
					continue;
				
				if(noOfPersonsUnserved == t.capacity || noOfPersonsUnserved == t.capacity - 1) {
					maxCap = t.capacity;
					tableName = t.name;
					break;
				}
				else {//look for biggest available table, not larger than total seats needed
					if(maxCap < t.capacity && t.capacity < noOfPersonsUnserved) {
						maxCap = t.capacity;
						tableName = t.name;
					}
				}
			}
			
			if(maxCap==0) {//maxCap was not updated means no more tables left
				enoughTables = false;
				break;
			}
			
			noOfPersonsUnserved -= maxCap;
			suggestionRecord.add(tableName);
		}
		
		if(enoughTables) {
			for(String tName: suggestionRecord)
				System.out.printf(" %s", tName);
		}
		else {
			System.out.print(" Not enough tables");
		}
		
		System.out.println();
	}
}