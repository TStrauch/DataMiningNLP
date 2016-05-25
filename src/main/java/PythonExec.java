import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 
 * @author Natalie, 25.05.
 *
 */
public class PythonExec {
	
	public static String executeScript(String folder, String filename, String OS){
		//location looks like "C:\\Users\\D059184\\Documents\\Master\\Data Mining\\DataSet_Project\\"
		//filename looks like "example_kMedoid.py"
		String location;
		String command; 
		String result = "";
		try{
			//Windows specific??
			location = "cd " + folder;
        	command = "python " + filename;
        	Process p;
        	
        	if(OS.equals("WIN")){
        		//Windows specific
        		p = Runtime.getRuntime().exec("cmd /c " + location + " && " + command);
        	} else {
        		p = Runtime.getRuntime().exec(location + " && " + command);
        	}
        	
        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output
            result = stdInput.readLine();
            
            if (result == null){
            	// read any errors
            	String error = "";
            	while ((error = stdError.readLine()) != null) {
            		result += error;
            	}
            }              
		} catch (Exception e) {
			System.out.println ("An error was encountered.");
		}
		
		return result;
		
	}
}
