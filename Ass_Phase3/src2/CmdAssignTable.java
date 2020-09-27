import java.util.ArrayList;

public class CmdAssignTable extends RecordedCommand {
    Reservation r;
    ArrayList<String> requestedTables = new ArrayList<>();
    
    @Override
    public void execute(String[] cmdParts) {
        try {
        	if(cmdParts.length<4)
        		throw new ExInsufficientArguments();
        	
        	//cmdPart[1] DineDate cmdParts[2] ticketCode cmdParts[3] tableName
        	Day dineDate = new Day(cmdParts[1]);
        	for(int i=3;i<cmdParts.length;i++)	requestedTables.add(cmdParts[i]); //compose list of requested tables
        	int ticketCode = Integer.parseInt(cmdParts[2]);
        	
			BookingOffice bo = BookingOffice.getInstance();
			r = bo.assignTable(dineDate, ticketCode, requestedTables);

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
		} catch (ExTableAlreadyAssigned e) {
			System.out.println("Table(s) already assigned for this booking!");
		} catch (ExNotEnoughSeats e) {
			System.out.println("Not enough seats for the booking!");
		} catch (ExTableAlreadyReserved e) {
			System.out.printf("Table %s is already reserved by another booking!\n", e.getBusyTable());
		} catch (ExTableNotFound e) {
			System.out.printf("Table code %s not found!\n", e.getTableCode());
		}
    }

    @Override
    public void undoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.unassignTable(r, requestedTables);
        addRedoCommand(this);
    }

    @Override
    public void redoMe() {
        BookingOffice bo = BookingOffice.getInstance();
        bo.assignTable(r, requestedTables);
        addUndoCommand(this);
    }
}
