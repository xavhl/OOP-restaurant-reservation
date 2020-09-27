import java.util.ArrayList;
import java.util.Collections; //Provides sorting
import java.util.List;

public class BookingOffice {
	private ArrayList<Reservation> allReservations;	

	private static BookingOffice instance = new BookingOffice(); 
	private BookingOffice() { allReservations = new ArrayList<>(); }
	public static BookingOffice getInstance(){ return instance; }

	public Reservation addReservation(String gN, String pN, int tP, String sDD) throws ExDuplicateBooking, ExOverdueBooking
	{
		Reservation r = new Reservation(gN, pN, tP, sDD);
		
		if(r.getDineDate().compareTo(SystemDate.getInstance())<0)//check for overdue booking
			throw new ExOverdueBooking();
		
		for(Reservation recordedR: allReservations) {//check for duplicate booking
			if(r.compareTo(recordedR)==0)
				throw new ExDuplicateBooking();
		}
		
		allReservations.add(r);
		Collections.sort(allReservations);
		return r;
	}

	public void addReservation(Reservation r)
	{
		allReservations.add(r);
		BookingTicketController.takeTicket(r.getDineDate());
		Collections.sort(allReservations); // allReservations
	}

	public void removeReservation(Reservation r) {
		allReservations.remove(r);
		BookingTicketController.returnTicket(r.getDineDate());
	}

	public void listReservations()
	{
		System.out.println(Reservation.getListingHeader()); // Reservation
		for (Reservation r: allReservations)
			System.out.println(r.toString()); // r or r.toString()
	}
}
