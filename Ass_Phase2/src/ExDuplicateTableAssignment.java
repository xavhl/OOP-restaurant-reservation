public class ExDuplicateTableAssignment extends Exception {
	public ExDuplicateTableAssignment() {super("Table(s) already assigned for this booking!");}
	public ExDuplicateTableAssignment(String message) {super(message);}
}