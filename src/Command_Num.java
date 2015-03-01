
public class Command_Num implements Command {

	Command_Num(String s){
		
		int num = Integer.parseInt(s);
		execute(num);
		
	}
	
	
	public void execute(int num) {
		// TODO Auto-generated method stub
		
		System.out.println("Num Command. Bib Number:  " + num);

	}


	@Override
	public void execute() {
		// TODO Auto-generated method stub
		//Do Nothing
	}

}
