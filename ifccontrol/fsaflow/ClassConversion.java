
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import soot.options.Options;

public class ClassConversion {
	
	 public static String[] argclassconversion = {"-process-dir","D:/soot/eclipse/workspace/yanginstrument/sootOutput/jimpleoutput"};
	
	/*
	 * argclassconversion[0] = "-process-dir"
	argclassconversion[1] = "D:/soot/eclipse/workspace/yanginstrument/sootOutput/jimpleoutput";
	
	 */
	
	public static void main(String[] arg) throws IOException {
		
		//-process-dir D:\\soot\\eclipse\\workspace\\yanginstrument\\sootOutput\\jimpleoutput
		
		
		//Options.v().set_allow_phantom_refs(true);
		
		
		Options.v().set_output_format(Options.output_format_class);
		Options.v().set_output_dir("D:\\soot\\eclipse\\workspace\\yanginstrument\\bin"); 

		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		//Options.v().set_whole_program(true);
		
		arg=argclassconversion;
		
		soot.Main.main(arg);
		
		//CopyClassFromBinToNewClass();
				
		System.out.println("Finish a Class");
		
	}
	
}
