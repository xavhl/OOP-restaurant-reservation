public class ExDuplicateBooking extends Exception {
	public ExDuplicateBooking() {super("Booking by the same person for the dining date already exists!");}
	public ExDuplicateBooking(String message) {super(message);}
}