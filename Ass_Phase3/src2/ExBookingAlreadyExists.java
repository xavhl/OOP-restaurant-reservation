public class ExBookingAlreadyExists extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExBookingAlreadyExists() {super("Booking by the same person for the dining date already exists!");}
	public ExBookingAlreadyExists(String message) {super(message);}
}