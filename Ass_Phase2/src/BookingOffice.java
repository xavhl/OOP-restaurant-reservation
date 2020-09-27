import java.util.ArrayList;
import java.util.Collections; //Provides sorting
import java.util.HashMap;

public class BookingOffice {
	private ArrayList<Reservation> allReservations;
	private HashMap<Day, ArrayList<Table>> tableController;
	
	private static BookingOffice instance = new BookingOffice(); 
	private BookingOffice() { 
		allReservations = new ArrayList<>();
		tableController = new HashMap<>();
	}
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
	
	public Reservation addReservation(String gN, String pN, int tP, String sDD) throws ExBookingAlreadyExists, ExDateAlreadyPassed
	{
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
		if(tableController.get(r.getDineDate())==null) {
			ArrayList<Table> tableListOfADay = new ArrayList<>();
			setupTables(tableListOfADay);//line 17
			tableController.put(r.getDineDate(), tableListOfADay);
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

	public void listReservations() {
		System.out.println(Reservation.getListingHeader());
		for (Reservation r: allReservations)
			System.out.println(r.toString());
	}
	
	public void listTableAllocations(Day dineDate) {
		ArrayList<Table> tableListOfThatDay = tableController.get(dineDate);
		
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
			if(r.getStatus()) {
				noOfReq++;
				noOfPersons += r.getTotalPersons();
			}
		}
		System.out.printf("\nTotal number of pending requests = %d (Total number of persons = %d)\n", noOfReq, noOfPersons);
	}
	//CmdAssignTable line 20
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
		if(!r.getStatus())//Reservation getStatus() line 26
			throw new ExTableAlreadyAssigned();
		
		ArrayList<Table> mainList = tableController.get(dineDate);
		int noOfDiners = r.getTotalPersons();
		
		for(String reqTName : requestedTables) {//first loop for validation
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
		
		for(String reqTName : requestedTables) {//second loop for assignment
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
		
		return r;
	}
	//CmdAssignTable redoMe() line 47
	public void assignTable(Reservation r, ArrayList<String> reqTables) {
		ArrayList<Table> mainList = tableController.get(r.getDineDate());
		for(String reqTName : reqTables) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.reserve(r.getTicketCode());
					r.receiveTable(t);
				}
			}
		}
	}
	
	public void collectTable(Reservation r, ArrayList<String> reqTables) {
		ArrayList<Table> mainList = tableController.get(r.getDineDate());
		for(String reqTName : reqTables) {
			for(Table t : mainList) {
				if(reqTName.equals(t.name)) {
					t.unreserve();
					r.returnTable(t);
				}
			}
		}
	}
}
