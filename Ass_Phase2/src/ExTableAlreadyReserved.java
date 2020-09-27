public class ExTableAlreadyReserved extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String busyTable;
	public String getBusyTable() {return busyTable;}
	
	public ExTableAlreadyReserved() {super("Table is already reserved by another booking!");}
	//public ExBusyTable(String message) {super(message);}
	public ExTableAlreadyReserved(String busyTable) {
		super(String.format("Table %s is already reserved by another booking!", busyTable));
		this.busyTable = busyTable;
	}
}