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

	public static void executePythonKMedoids(int nclusters){
		try {
			String s;
			String location = "python/cluster.py";
			String command = "python " + location + " "+nclusters;

			Process p = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output

			//leaving this in here to ensure the file was written
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			while((s = stdError.readLine()) != null){
				System.out.println(s);
			}


//			System.exit(0);


			//python.execfile ("C:\\Users\\D059184\\Documents\\Master\\Data Mining\\DataSet_Project\\example_kMmedoid.py");
        	/*int number1 = 10;
        	int number2 = 32;

        	python.set("number1", new PyInteger(number1));
        	python.set("number2", new PyInteger(number2));
        	python.exec("number3 = number1+number2");
        	PyObject number3 = python.get("number3");
        	System.out.println("val : "+number3.toString());*/
//			System.out.println("Python script successfully executed");
		} catch ( Exception e ) {
			System.out.println ("An error was encountered.");
		}
	}
}
