public class CmdStartNewDay extends RecordedCommand {
    String oldDay, newDay;

    @Override
    public void execute(String[] cmdParts) {
        try {
        	if(cmdParts.length<2)
        		throw new ExInsufficientArguments();
        	
			SystemDate instance = SystemDate.getInstance();
			oldDay = instance.toString();
			newDay = cmdParts[1];
			instance.set(cmdParts[1]);

			addUndoCommand(this);
			clearRedoList();

			System.out.println("Done.");
		} catch (ExInsufficientArguments e) {
			System.out.println("Insufficient command arguments!");
		}
    }

    @Override
    public void undoMe() {
        SystemDate.getInstance().set(oldDay);
        addRedoCommand(this);
    }

    @Override
    public void redoMe() {
        SystemDate.getInstance().set(oldDay);
        addUndoCommand(this);
    }
}
