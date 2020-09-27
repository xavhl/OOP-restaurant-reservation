public class ExTableNotFound extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String tableCode;
	public String getTableCode() {return tableCode;}
	
	public ExTableNotFound() {super("Table not found!");}
	//public ExTableNotFound(String message) {super(message);}
	public ExTableNotFound(String tableCode) {
		super(String.format("Table code %s not found!", tableCode));
		this.tableCode = tableCode;
	}
}