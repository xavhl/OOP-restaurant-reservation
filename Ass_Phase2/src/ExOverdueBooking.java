public class ExOverdueBooking extends Exception {
	public ExOverdueBooking() {super("Date has already passed!");}
	public ExOverdueBooking(String message) {super(message);}
}