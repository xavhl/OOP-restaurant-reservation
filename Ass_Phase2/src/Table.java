public class Table {
	public String name;
	public int ticketCode;
	public int capacity;
	
	public Table(String name) {
		this.name = name;
		this.ticketCode = 0;
		if(name.charAt(0)=='T')
			capacity = 2;
		else if(name.charAt(0)=='F')
			capacity = 4;
		else capacity = 8;
	}
	
	public void reserve(int code) {
		ticketCode = code;
	}
	
	public void unreserve() {
		ticketCode = 0;
	}
	
	public String toString() {
		return name;
	}
}