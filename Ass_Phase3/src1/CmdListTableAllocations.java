public class CmdListTableAllocations implements Command {
    @Override
    public void execute(String[] cmdParts) {
        try {
			if(cmdParts.length<2)
				throw new ExInsufficientArguments();
			
			Day dineDate = new Day(cmdParts[1]);
			BookingOffice bo = BookingOffice.getInstance();
	        bo.listTableAllocations(dineDate);
		} catch (ExInsufficientArguments e) {
			System.out.println("Insufficient command arguments!");
		}
    }
}
