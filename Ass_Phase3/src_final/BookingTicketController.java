import java.util.HashMap;

public class BookingTicketController {
	private static HashMap<Day, Integer> tController = new HashMap<>();
	
	public static int takeTicket(Day d) {
		Integer ticket = tController.get(d);
		
		if(ticket == null) {
			tController.put(d.clone(), 2);
			return 1;
		}
		else {
			tController.put(d, ticket+1);
			return ticket;
		}
	}
	
	public static void returnTicket(Day d) {
		Integer ticket = tController.get(d);
		tController.put(d, ticket-1);
	}
}
