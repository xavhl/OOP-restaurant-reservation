public class CmdSuggestTable implements Command{
    @Override
    public void execute(String[] cmdParts) {
        try {
        	if(cmdParts.length<3)
        		throw new ExInsufficientArguments();
        	
        	//cmdPart[1] DineDate cmdParts[2] ticketCode
        	Day dineDate = new Day(cmdParts[1]);
        	int ticketCode = Integer.parseInt(cmdParts[2]);
        	
			BookingOffice bo = BookingOffice.getInstance();
			bo.suggestTable(dineDate, ticketCode);

		} catch (NumberFormatException e) {
			System.out.println("Wrong number format!");
		} catch (ExInsufficientArguments e) {
			System.out.println("Insufficient command arguments!");
		} catch (ExBookingNotFound e) {
			System.out.println("Booking not found!");
		}
    }
}
