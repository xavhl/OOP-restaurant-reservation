public class ExTableNotFound extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String tableCode;
	public String getTableCode() {return tableCode;}
	
	public ExTableNotFound() {super("Booking not found!");}
	//public ExTableNotFound(String message) {super(message);}
	public ExTableNotFound(String tableCode) {
		super(String.format("Table code "+ tableCode +" not found!"));
		this.tableCode = tableCode;
	}
}