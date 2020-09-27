public class ExBookingNotFound extends Exception {
	public ExBookingNotFound() {super("Booking not found!");}
	public ExBookingNotFound(String message) {super(message);}
}