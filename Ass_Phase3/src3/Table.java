public class Table implements Comparable<Table>{
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
	
	@Override
	public int compareTo(Table another) {
		if(this.capacity==another.capacity) {
			if(this.name.charAt(1)==another.name.charAt(1))	return 0;//compare tens digit of code, e.g. H09 < H10
			else if(this.name.charAt(1)<another.name.charAt(1))	return -1;
			else return 1;
		}
		else if(this.capacity<another.capacity) return -1;
		else return 1;
	}
}