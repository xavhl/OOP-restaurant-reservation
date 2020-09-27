public class ExTableAlreadyAssigned extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExTableAlreadyAssigned() {super("Table(s) already assigned for this booking!");}
	public ExTableAlreadyAssigned(String message) {super(message);}
}