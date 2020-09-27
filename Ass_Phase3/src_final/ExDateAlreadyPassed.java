public class ExDateAlreadyPassed extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExDateAlreadyPassed() {super("Date has already passed!");}
	public ExDateAlreadyPassed(String message) {super(message);}
}