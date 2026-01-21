package instrument;
/*
 * InvokeStaticInstrumenter inserts count instructions before
 * INVOKESTATIC bytecode in a program. The instrumented program will
 * report how many static invocations happen in a run.
 * 
 * Goal:
 *   Insert counter instruction before static invocation instruction.
 *   Report counters before program's normal exit point.
 *
 * Approach:
 *   1. Create a counter class which has a counter field, and 
 *      a reporting method.
 *   2. Take each method body, go through each instruction, and
 *      insert count instructions before INVOKESTATIC.
 *   3. Make a call of reporting method of the counter class.
 *
 * Things to learn from this example:
 *   1. How to use Soot to examine a Java class.
 *   2. How to insert profiling instructions in a class.
 */

/* InvokeStaticInstrumenter extends the abstract class BodyTransformer,
 * and implements <pre>internalTransform</pre> method.
 */ 
import soot.*;
import soot.jimple.*;
import soot.tagkit.Tag;
import soot.util.*;

import java.util.*;

public class InvokeStaticInstrumenter extends BodyTransformer{

  /* some internal fields */
  static SootClass counterClass;
  static SootMethod increaseCounter, reportCounter;

  static {
    counterClass    = Scene.v().loadClassAndSupport("security.MyCounter");
    increaseCounter = counterClass.getMethod("void increase(int)");
    reportCounter   = counterClass.getMethod("void report()");
  }
  public void insert_yang(Chain units,Stmt stmt)
  {
	  InvokeExpr incExpr= Jimple.v().newStaticInvokeExpr(increaseCounter.makeRef(),
			  IntConstant.v(1));
      // 2. then, make a invoke statement
     Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

    // 3. insert new statement into the chain 
     //    (we are mutating the unit chain).
    units.insertBefore(incStmt, stmt);
	  return;
  }

  /* internalTransform goes through a method body and inserts 
   * counter instructions before an INVOKESTATIC instruction
   */
  protected void internalTransform(Body body, String phase, Map options) {
    // body's method
    SootMethod method = body.getMethod();

    // debugging
    System.out.println("instrumenting method : " + method.getSignature());

    // get body's unit as a chain
    Chain units = body.getUnits();
    Chain locals = body.getLocals();
    if (!locals.isEmpty())
    {
    Local localitem=(Local)locals.getFirst();
    String yangss=localitem.getType().toString();
    yangss+="";
    }
    //JimpleLocal local=(JimpleLocal)locals.getFirst();
    
   /* Iterator localItem=locals.snapshotIterator();
    while (localItem.hasNext())
    {
    	
    }
    */
    // get a snapshot iterator of the unit since we are going to
    // mutate the chain when iterating over it.
    //
    Iterator stmtIt = units.snapshotIterator();
    String yang=null;
   
    // typical while loop for iterating over each statement
    while (stmtIt.hasNext()) {
      
    	
      // cast back to a statement.
      Stmt stmt = (Stmt)stmtIt.next();
      
      /*
      List<ValueBox> bIt=stmt.getUseAndDefBoxes();
      String yangss="";
      for (ValueBox box : bIt)
      {
           if (!(box.getValue() instanceof Local))
              continue;
      }
      */
      
      if (stmt.branches())
      {
    	  
    	  //Stmt yangstmt=(Stmt)units.getSuccOf(stmt);
    	  Object yangstmt=units.getSuccOf(stmt);
    	  
    	  List<UnitBox> yangunitbox=stmt.getUnitBoxes();
    	  for (UnitBox ubox : stmt.getUnitBoxes())
    	  {
    		  yang=ubox.getUnit().toString();//yang=ubox.;
    		 
    	  }
    	  if (yangstmt!=null)
    	  insert_yang(units,(Stmt)yangstmt);
    	  //yangstmt.toString();
    	 
    	 
    	 
    	  
      }
      
   
   

   
    }
  }
}
