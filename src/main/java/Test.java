
import java.io.*;

/**
 * Created by Timo on 14.05.16.
 */
public class Test {

    public static void main(String[] args){

    	//Call Python script in Java
        //PythonInterpreter python = new PythonInterpreter();
        
        try {
        	String s;
        	String location = "python/cluster.py";
        	String command = "python " + location;
        	
        	Process p = Runtime.getRuntime().exec(command);
        	
        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

               BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

               // read the output
               while ((s = stdInput.readLine()) != null) {
                   System.out.println(s);
               }             

               // read any errors
               while ((s = stdError.readLine()) != null) {
                   System.out.println(s);
               }             

               System.exit(0);


            //python.execfile ("C:\\Users\\D059184\\Documents\\Master\\Data Mining\\DataSet_Project\\example_kMmedoid.py");
        	/*int number1 = 10;
        	int number2 = 32;
        	 
        	python.set("number1", new PyInteger(number1));
        	python.set("number2", new PyInteger(number2));
        	python.exec("number3 = number1+number2");
        	PyObject number3 = python.get("number3");
        	System.out.println("val : "+number3.toString());*/
            System.out.println("Python script successfully executed");
         } catch ( Exception e ) {
            System.out.println ("An error was encountered.");
         }
    }
}

