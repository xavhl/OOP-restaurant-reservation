public class Day implements Cloneable {
	
	private int year;
	private int month;
	private int day;
	
	private static final String MonthNames="JanFebMarAprMayJunJulAugSepOctNovDec";
	
	//Constructor
	public Day(int y, int m, int d) {
		this.year=y;
		this.month=m;
		this.day=d;		
	}
	
	// check if a given year is a leap year
	static public boolean isLeapYear(int y)
	{
		if (y%400==0)
			return true;
		else if (y%100==0)
			return false;
		else if (y%4==0)
			return true;
		else
			return false;
	}
	
	// check if y,m,d valid
	static public boolean valid(int y, int m, int d)
	{
		if (m<1 || m>12 || d<1) return false;
		switch(m){
			case 1: case 3: case 5: case 7:
			case 8: case 10: case 12:
					 return d<=31; 
			case 4: case 6: case 9: case 11:
					 return d<=30; 
			case 2:
					 if (isLeapYear(y))
						 return d<=29; 
					 else
						 return d<=28; 
		}
		return false;
	}

	public void set(String sDay) //Set year,month,day based on a string like 01-Mar-2019
	{		
		String[] sDayParts = sDay.split("-");
		this.day = Integer.parseInt(sDayParts[0]);
		this.year = Integer.parseInt(sDayParts[2]); 
		this.month = MonthNames.indexOf(sDayParts[1])/3+1;
	}

	public Day(String sDay) { set(sDay); }

	@Override
	public String toString() 
	{		
		return day+"-"+ MonthNames.substring((month-1)*3,(month)*3) + "-"+ year;
	}
	
	@Override
	public Day clone() 
	{
		Day copy=null;
		try {
			copy = (Day) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copy;
	}
	
	@Override
	public int hashCode() {//generate key for HashMap in BookingTicketController
		return year*10000 + month*100 + day;
	}
	
	@Override
	public boolean equals(Object otherObject) {//for HashMap in BookingTicketController
		if(otherObject == null)
			return false;
		if(this.getClass() != otherObject.getClass())
			return false;
		
		Day d = (Day) otherObject;
		if(this.year != d.year)
			return false;
		if(this.month != d.month)
			return false;
		if(this.day != d.day)
			return false;
		
		return true;
	}
	
	//@Override
	public int compareTo(Day d) {
		if(this.year==d.year) {
			if(this.month == d.month) {
				if(this.day == d.day)
					return 0;
				else if(this.day > d.day) return 1;
				else return -1;
			}
			else if(this.month>d.month) return 1;
			else return -1;
		}
		else if(this.year > d.year) return 1;
		else return -1;
	}
}
