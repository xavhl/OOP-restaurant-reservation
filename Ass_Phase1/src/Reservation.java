
public class Reservation implements Comparable<Reservation>{
	private String guestName;
	private String phoneNumber;
	private int totPersons;
	private Day dateDine;
	private Day dateRequest;
	private int ticketCode;

	public Reservation(String guestName, String phoneNumber, int totPersons, String sDateDine)
	{	
		this.guestName=guestName;
		this.phoneNumber=phoneNumber;
		this.totPersons=totPersons;
		dateDine=new Day(sDateDine);
		dateRequest=SystemDate.getInstance().clone();
		this.ticketCode = BookingTicketController.takeTicket(this.dateDine);
	}
	
	public int getTicketCode() { return this.ticketCode; }
	
	public Day getDineDate() {return this.dateDine.clone();}//return copy of dateDine
	
	@Override
	public String toString() 
	{
		//Learn: "-" means left-aligned
		return String.format("%-13s%-11s%-14s%-25s%4d       %s", guestName, phoneNumber, dateRequest, 
				dateDine+String.format(" (Ticket %d)", ticketCode), totPersons, "Pending");
	}

	public static String getListingHeader() 
	{
		return String.format("%-13s%-11s%-14s%-25s%-11s%s",
				"Guest Name", "Phone", "Request Date", "Dining Date and Ticket", "#Persons", "Status");
	}
	
	@Override
	public int compareTo(Reservation another) 
	{
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

//	public int compareTo(Reservation another) 
//	{
//		return this.guestName.compareTo(another.guestName);
//	}
}
