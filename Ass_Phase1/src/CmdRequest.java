import java.awt.print.Book;

public class CmdRequest extends RecordedCommand {
    Reservation r;

    @Override
    public void execute(String[] cmdParts) {
        try {
        	if(cmdParts.length<5)
        		throw new ExInsufficientArguments();
        	
			BookingOffice bo = BookingOffice.getInstance();
			int noOfPersons = Integer.parseInt(cmdParts[3]);
			r = bo.addReservation(cmdParts[1],cmdParts[2],noOfPersons, cmdParts[4]);

			addUndoCommand(this);
			clearRedoList();
			
			System.out.printf("Done. Ticket code for %s: %d\n", cmdParts[4	], r.getTicketCode());
		} catch (NumberFormatException e) {
			System.out.println("Wrong number format!");
		} catch (ExInsufficientArguments e) {
			System.out.println("Insufficient command arguments!");
		} catch (ExDuplicateBooking e) {
			System.out.println("Booking by the same person for the dining date already exists!");
		} catch (ExOverdueBooking e) {
			System.out.println("Date has already passed!");
		}
    }

    @Override
    public void undoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.removeReservation(r);
        addRedoCommand(this);
    }

    @Override
    public void redoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.addReservation(r);
        addUndoCommand(this);
    }
}
