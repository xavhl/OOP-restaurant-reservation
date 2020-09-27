public class CmdCancel extends RecordedCommand {
    Reservation r;

    @Override
    public void execute(String[] cmdParts) {
        try {
        	if(cmdParts.length<3)
        		throw new ExInsufficientArguments();
        	
        	Day dineDate = new Day(cmdParts[1]);
        	int ticketCode = Integer.parseInt(cmdParts[2]);
        	
			BookingOffice bo = BookingOffice.getInstance();
			r = bo.cancelReservation(dineDate, ticketCode);
			
			addUndoCommand(this);
			clearRedoList();
			
			System.out.println("Done.");
		} catch (NumberFormatException e) {
			System.out.println("Wrong number format!");
		} catch (ExInsufficientArguments e) {
			System.out.println("Insufficient command arguments!");
		} catch (ExDateAlreadyPassed e) {
			System.out.println("Date has already passed!");
		} catch (ExBookingNotFound e) {
			System.out.println("Booking not found!");
		}
    }

    @Override
    public void undoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.uncancelReservation(r);
        addRedoCommand(this);
    }

    @Override
    public void redoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.cancelReservation(r);
        addUndoCommand(this);
    }
}
