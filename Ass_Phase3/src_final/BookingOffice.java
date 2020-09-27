import java.util.ArrayList;
import java.util.Collections; //Provides sorting

public class BookingOffice {
	private ArrayList<Reservation> allReservations;
	
	private static BookingOffice instance = new BookingOffice(); 
	private BookingOffice() {allReservations = new ArrayList<>();}
	public static BookingOffice getInstance(){ return instance; }
		
	public Reservation addReservation(String gN, String pN, int tP, String sDD) throws ExBookingAlreadyExists, ExDateAlreadyPassed {
		Reservation r = new Reservation(gN, pN, tP, sDD);
		
		if(r.getDineDate().compareTo(SystemDate.getInstance())<0)
			throw new ExDateAlreadyPassed();
		
		for(Reservation recordedR: allReservations) {
			if(r.compareTo(recordedR)==0)
				throw new ExBookingAlreadyExists();
		}
		
		allReservations.add(r);
		Collections.sort(allReservations);
		TableController.setupTable(r.getDineDate());
		
		return r;
	}

	public void addReservation(Reservation r) {
		allReservations.add(r);
		BookingTicketController.takeTicket(r.getDineDate());
		Collections.sort(allReservations);
	}

	public void removeReservation(Reservation r) {
		allReservations.remove(r);
		BookingTicketController.returnTicket(r.getDineDate());
	}
	
	public Reservation assignTable(Day dineDate, int ticketCode, ArrayList<String> requestedTables) 
			throws ExBookingNotFound, ExTableAlreadyAssigned, ExNotEnoughSeats, ExTableAlreadyReserved, ExTableNotFound, ExDateAlreadyPassed {
		if(dineDate.compareTo(SystemDate.getInstance())<0)
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
		if(r.getStatus() instanceof RStateTableAllocated)
			throw new ExTableAlreadyAssigned();
		
		TableController.assignTable(r, requestedTables);
		
		r.setStatus(new RStateTableAllocated());
		return r;
	}

	public void assignTable(Reservation r, ArrayList<String> reqTables) {
		TableController.reassignTable(r, reqTables);
		r.setStatus(new RStateTableAllocated());
	}
	
	public void unassignTable(Reservation r, ArrayList<String> reqTables) {
		TableController.unassignTable(r, reqTables);
		r.setStatus(new RStatePending());
	}
	
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
		
		TableController.unreserveTable(r.getDineDate(), r.getTableNames());
		
		return r;
	}
	
	public void cancelReservation(Reservation r) {
		allReservations.remove(r);
		TableController.unreserveTable(r.getDineDate(), r.getTableNames());
	}
	
	public void uncancelReservation(Reservation r) {
		allReservations.add(r);
		Collections.sort(allReservations);

		TableController.reserveTable(r.getDineDate(), r.getTableNames(), r.getTicketCode());
	}
	
	public void listReservations() {
		System.out.println(Reservation.getListingHeader());
		for (Reservation r: allReservations)
			System.out.println(r.toString());
	}
	
	public void listTableAllocations(Day dineDate) {
		TableController.listTableAllocations(dineDate);
		
		int noOfReq = 0, noOfPersons = 0;
		for(Reservation r : allReservations) {
			if(r.getDineDate().equals(dineDate) && r.getStatus() instanceof RStatePending) {
				noOfReq++;
				noOfPersons += r.getTotalPersons();
			}
		}
		System.out.printf("\nTotal number of pending requests = %d (Total number of persons = %d)\n", noOfReq, noOfPersons);
	}
	
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
		
		TableController.suggestTable(r);
	}
}