public class ExInvalidCommand extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExInvalidCommand() {super("Invalid command!");}
	public ExInvalidCommand(String message) {super(message);}
}