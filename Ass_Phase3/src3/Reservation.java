import java.util.ArrayList;

public class Reservation implements Comparable<Reservation>{
	private String guestName;
	private String phoneNumber;
	private int totPersons;
	private Day dateDine;
	private Day dateRequest;
	private int ticketCode;
	private ArrayList<Table> assignedTable;	
	private RState status;

	public Reservation(String guestName, String phoneNumber, int totPersons, String sDateDine)
	{	
		this.guestName=guestName;
		this.phoneNumber=phoneNumber;
		this.totPersons=totPersons;
		dateDine=new Day(sDateDine);
		dateRequest=SystemDate.getInstance().clone();
		this.ticketCode = BookingTicketController.takeTicket(this.dateDine);
		this.assignedTable = new ArrayList<Table>();
		this.status = new RStatePending();
	}
	
	public String getGuestName() {return this.guestName;}
	public String getPhoneNumber() {return this.phoneNumber;}
	public int getTotalPersons() {return this.totPersons;}
	public Day getDineDate() {return this.dateDine.clone();}//return copy of dateDine
	public Day getRequestDate() {return this.dateRequest.clone();}
	public int getTicketCode() { return this.ticketCode; }
	//public boolean getAssignedStatus() {return !(assignedTable.isEmpty());}//return true if assigned table
	
	public ArrayList<String> getTableNames() { //BookingOffice line 91
		ArrayList<String> newList = new ArrayList<>();
		for(Table t : assignedTable)
			newList.add(t.toString());
		return newList;
	}
	
	public void receiveTable(Table t) {assignedTable.add(t);}
	public void returnTable(Table t) {assignedTable.remove(t);}
	
	public void setStatus(RState status) {this.status = status;}
	public RState getStatus() {return this.status;}
	
	public String toString() {return status.toString(this);}

	public static String getListingHeader() 
	{
		return String.format("%-13s%-11s%-14s%-25s%-11s%s",
				"Guest Name", "Phone", "Request Date", "Dining Date and Ticket", "#Persons", "Status");
	}
	
	@Override
	public int compareTo(Reservation another) {
		int compareName = this.guestName.compareTo(another.guestName);
		
		if (compareName == 0) {
			int comparePhone = this.phoneNumber.compareTo(another.phoneNumber);
			
			if(comparePhone == 0) {
				int compareDate = this.dateDine.compareTo(another.dateDine);
				
				if(compareDate == 0)
					return 0;
				else if(compareDate>0) return 1;
				else return -1;
			}
			else if(comparePhone>0) return 1;
			else return -1;
		}
		else if (compareName>0) return 1;
		else return -1;
	}
}
