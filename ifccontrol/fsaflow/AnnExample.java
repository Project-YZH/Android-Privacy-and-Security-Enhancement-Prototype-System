
import soot.*;
import soot.tagkit.*;
import soot.Scene;
import soot.options.Options;
import java.util.*;

public class AnnExample {
	
	public static void main(String[] args)
	{
	/* adds the transformer. */
	PackManager.v().getPack("jtp").add(new Transform("jtp.annotexample",AnnExampleWrapper.v()));
	/* invokes Soot */
	 Options.v().set_soot_classpath("D:\\soot\\eclipse\\workspace\\counter2\\bin;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\rt.jar");

	// Options.v().set_soot_classpath("I:\\пбндуб\\soot\\eclipse-chao-droidbench\\eclipse\\eclipse\\workspace\\counter2\\bin");

	soot.Main.main(args);
	}

}

class AnnExampleWrapper extends BodyTransformer {
	private static AnnExampleWrapper instance = new AnnExampleWrapper();

	private AnnExampleWrapper() {
	};

	public static AnnExampleWrapper v() {
		return instance;
	}

	public void internalTransform(Body body, String phaseName, Map options) {
		SootMethod method = body.getMethod();
		String attr = new String("Hello world!");

		Tag example = new GenericAttribute("Example", attr.getBytes());
		method.addTag(example);
	}
}


