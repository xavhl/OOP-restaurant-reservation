import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String [] args){
		Scanner in = new Scanner(System.in);
		System.out.print("Please input the file pathname: ");
		String filepathname = in.nextLine();
		
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File(filepathname));

			String cmdLine1 = inFile.nextLine();
			String[] cmdLine1Parts = cmdLine1.split("\\|");
			System.out.println("\n> "+cmdLine1);
			SystemDate.createTheInstance(cmdLine1Parts[1]);

			while(inFile.hasNext()) {
				String cmdLine = inFile.nextLine().trim();

				if(cmdLine.equals("")) continue;
				System.out.println("\n> "+cmdLine);

				String cmdParts[] = cmdLine.split("\\|");
				
				if(cmdParts[0].equals("request"))
					(new CmdRequest()).execute(cmdParts);
				else if(cmdParts[0].equals("listReservations"))
					(new CmdListReservations()).execute(cmdParts);
				else if((cmdParts[0].equals("startNewDay")))
					(new CmdStartNewDay()).execute(cmdParts);
				else if((cmdParts[0].equals("undo")))
					RecordedCommand.undoOneCommand();
				else if((cmdParts[0].equals("redo")))
					RecordedCommand.redoOneCommand();
				else throw new ExInvalidCommand();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (ExInvalidCommand e) {
			System.out.println("Invalid command!");
		} finally {
			if(inFile != null)
				inFile.close();
			in.close();
		}
	}
}

