public class RStatePending implements RState {
    public String toString(Reservation r) {
    	String status = "Pending";
    	
    	return String.format("%-13s%-11s%-14s%-25s%4d       %s", r.getGuestName(), r.getPhoneNumber(), r.getRequestDate(), 
			r.getDineDate()+String.format(" (Ticket %d)", r.getTicketCode()), r.getTotalPersons(), status);
    }
}
