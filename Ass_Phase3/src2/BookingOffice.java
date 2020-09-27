import java.util.ArrayList;
import java.util.Collections; //Provides sorting

public class BookingOffice {
	private ArrayList<Reservation> allReservations;
	
	private static BookingOffice instance = new BookingOffice(); 
	private BookingOffice() {allReservations = new ArrayList<>();}
	public static BookingOffice getInstance(){ return instance; }
	
	private void setupTables(ArrayList<Table> t) {
		for(int i=0;i<10;i++) {
			String tableName = "T";
			if(i<9)
				tableName += "0";
			tableName += Integer.toString(i+1);
			t.add(new Table(tableName));
		}
		for(int i=0;i<6;i++)			
			t.add(new Table(String.format("F0%s", Integer.toString(i+1))));
		for(int i=0;i<3;i++)
			t.add(new Table(String.format("H0%s", Integer.toString(i+1))));
	}
	
/***********************Request********************************************************************************************************/	
	public Reservation addReservation(String gN, String pN, int tP, String sDD) throws ExBookingAlreadyExists, ExDateAlreadyPassed {
		Reservation r = new Reservation(gN, pN, tP, sDD);
		
		if(r.getDineDate().compareTo(SystemDate.getInstance())<0)//check for overdue booking
			throw new ExDateAlreadyPassed();
		
		for(Reservation recordedR: allReservations) {//check for duplicate booking
			if(r.compareTo(recordedR)==0)
				throw new ExBookingAlreadyExists();
		}
		
		allReservations.add(r);
		Collections.sort(allReservations);
		
		//setup tables
		if(TableController.getTable(r.getDineDate())==null) {//if no booking records/ assignments on that day
			ArrayList<Table> tableListOfADay = new ArrayList<>();
			setupTables(tableListOfADay);//line 17
			TableController.putTable(r.getDineDate(), tableListOfADay);
		}
		
		return r;
	}

	public void addReservation(Reservation r) {//called by CmdRequest redoMe()
		allReservations.add(r);
		BookingTicketController.takeTicket(r.getDineDate());
		Collections.sort(allReservations);
	}

	public void removeReservation(Reservation r) {//called by CmdRequest undoMe()
		allReservations.remove(r);
		BookingTicketController.returnTicket(r.getDineDate());
	}
	
/***********************Cancel********************************************************************************************************/	
	public Reservation cancelReservation(Day dineDate, int ticketCode) throws ExDateAlreadyPassed, ExBookingNotFound {
		if(dineDate.compareTo(SystemDate.getInstance())<0)
			throw new ExDateAlreadyPassed();
		
		Reservation r = null;
		for(Reservation otherR : allReservations) {
			if(otherR.getDineDate().equals(dineDate) && otherR.getTicketCode()==ticketCode) {
				r = otherR;
				allReservations.remove(otherR);
				break;
			}
		}
		if(r==null)	throw new ExBookingNotFound();
		
		ArrayList<Table> mainList = TableController.getTable(dineDate);
		for(String tableName : r.getTableNames()) {
			for(Table t : mainList) {
				if(tableName.equals(t.toString()))
					t.unreserve();
			}
		}
		
		return r;
	}
	
	public void cancelReservation(Reservation r) {
		allReservations.remove(r);
		
		ArrayList<Table> mainList = TableController.getTable(r.getDineDate());
		for(String tableName : r.getTableNames()) {
			for(Table t : mainList) {
				if(tableName.equals(t.toString()))
					t.unreserve();
			}
		}
	}
	
	public void uncancelReservation(Reservation r) {
		allReservations.add(r);
		Collections.sort(allReservations);
		
		ArrayList<Table> mainList = TableController.getTable(r.getDineDate());
		for(String tableName : r.getTableNames()) {
			for(Table t : mainList) {
				if(tableName.equals(t.toString()))
					t.reserve(r.getTicketCode());
			}
		}
	}
	
/***********************List Reservations********************************************************************************************************/	
	public void listReservations() {
		System.out.println(Reservation.getListingHeader());
		for (Reservation r: allReservations)
			System.out.println(r.toString());
	}
	
/***********************List Table Allocations********************************************************************************************************/	
	public void listTableAllocations(Day dineDate) {
		ArrayList<Table> tableListOfThatDay = TableController.getTable(dineDate);
		
		boolean isAllocatedListEmpty = true;
		System.out.println("Allocated tables:");
		for(Table t : tableListOfThatDay) {
			if(t.ticketCode!=0) {
				System.out.println(String.format("%s (Ticket %d)", t.name, t.ticketCode));
				isAllocatedListEmpty = false;
			}
		}
		if(isAllocatedListEmpty)	System.out.println("[None]");
		
		System.out.println();
		
		boolean isAvailableListEmpty = true;
		System.out.println("Available tables:");
		for(Table t : tableListOfThatDay) {
			if(t.ticketCode==0) {
				System.out.print(t.toString() + " ");
				isAvailableListEmpty = false;
			}
		}
		if(isAvailableListEmpty)	System.out.print("[None]");
		
		System.out.println();
		
		int noOfReq = 0, noOfPersons = 0;
		for(Reservation r : allReservations) {
			if(r.getDineDate().equals(dineDate) && r.getStatus() instanceof RStatePending) {
				noOfReq++;
				noOfPersons += r.getTotalPersons();
			}
		}
		System.out.printf("\nTotal number of pending requests = %d (Total number of persons = %d)\n", noOfReq, noOfPersons);
	}
	
/***********************Assign Table********************************************************************************************************/	
	//called by CmdAssignTable line 20
	public Reservation assignTable(Day dineDate, int ticketCode, ArrayList<String> requestedTables) 
			throws ExBookingNotFound, ExTableAlreadyAssigned, ExNotEnoughSeats, ExTableAlreadyReserved, ExTableNotFound, ExDateAlreadyPassed {
		if(dineDate.compareTo(SystemDate.getInstance())<0)//check for overdue booking
			throw new ExDateAlreadyPassed();
		
		Reservation r = null;
		for(Reservation otherR : allReservations) {
			if(otherR.getDineDate().equals(dineDate) && otherR.getTicketCode()==ticketCode) {
				r = otherR;
				break;
			}
		}
		
		if(r == null)
			throw new ExBookingNotFound();
		if(r.getStatus() instanceof RStateTableAllocated)//Reservation getAssignedStatus(), true if assigned table
			throw new ExTableAlreadyAssigned();
		
		ArrayList<Table> mainList = TableController.getTable(dineDate);
		int noOfDiners = r.getTotalPersons();
		
		for(String reqTName : requestedTables) {//first nested loop for validation
			boolean isFound = false;
			
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					isFound = true;
					
					if(t.ticketCode!=0)//table already reserved
						throw new ExTableAlreadyReserved(t.toString());
					else
						noOfDiners -= t.capacity;
				}
			}
			
			if(!isFound) throw new ExTableNotFound(reqTName);
		}
		
		if(noOfDiners>0)
			throw new ExNotEnoughSeats();
		
		for(String reqTName : requestedTables) {//second nested loop for assignment
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
		
		r.setStatus(new RStateTableAllocated());
		return r;
	}
	//called by CmdAssignTable redoMe(), BookingOffice uncancelReservation()
	public void assignTable(Reservation r, ArrayList<String> reqTables) {
		ArrayList<Table> mainList = TableController.getTable(r.getDineDate());
		
		for(String reqTName : reqTables) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
		
		r.setStatus(new RStateTableAllocated());
	}
	
	public void unassignTable(Reservation r, ArrayList<String> reqTables) {
		ArrayList<Table> mainList = TableController.getTable(r.getDineDate());
		
		for(String reqTName : reqTables) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.unreserve();
					r.returnTable(t);
				}
			}
		}
		
		r.setStatus(new RStatePending());
	}

/***********************Suggest Table********************************************************************************************************/
	public void suggestTable(Day dineDate, int ticketCode) throws ExBookingNotFound, ExTableAlreadyAssigned {
		Reservation r = null;
		for(Reservation otherR : allReservations) {
			if(otherR.getDineDate().equals(dineDate)&&otherR.getTicketCode()==ticketCode) {
				r = otherR;
				break;
			}
		}
		
		if(r==null)
			throw new ExBookingNotFound();		
		if(r.getStatus() instanceof RStateTableAllocated)
			throw new ExTableAlreadyAssigned();
		
		//set up a list of available tables
		ArrayList<Table> tableOfThatDay = TableController.getTable(dineDate);
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
				else {//look for biggest available table, not larger than needed total seats
					if(maxCap < t.capacity) {
						maxCap = t.capacity;
						tableName = t.name;
					}
				}
			}
			
			if(maxCap==0) {
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