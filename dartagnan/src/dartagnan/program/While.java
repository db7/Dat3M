package dartagnan.program;

import java.util.Collections;

import dartagnan.expression.AConst;
import dartagnan.expression.AExpr;
import dartagnan.expression.BExpr;
import dartagnan.program.event.Local;
import dartagnan.program.event.Skip;
import dartagnan.program.event.Store;

public class While extends Thread {

	private AExpr aPred;
	private BExpr pred;
	private Thread t;
	
	public While(BExpr pred, Thread t) {
		this.pred = pred;
		this.t = t;
		t.incCondLevel();
	}

	public While(AExpr aPred, Thread t) {
		this.aPred = aPred;
		this.t = t;
		t.incCondLevel();
	}
	
	public String toString() {
		String predicateString = pred != null ? pred.toString() : aPred.toString();
		return String.format("%swhile %s {\n%s\n%s}", String.join("", Collections.nCopies(condLevel, "  ")), predicateString, t, String.join("", Collections.nCopies(condLevel, "  ")));
	}
	
	public void incCondLevel() {
		condLevel++;
		t.incCondLevel();
	}
	
	public void decCondLevel() {
		condLevel--;
		t.decCondLevel();
	}
	
	public Thread unroll(int steps, boolean obsNoTermination) {
		if(steps == 0) {
			if(obsNoTermination) {
				int oldCondLevel = condLevel;
				Register rTerm = new Register("rTerm");
				Local newLocal = new Local(rTerm, new AConst(1));
				newLocal.condLevel = condLevel;
				Location termination = new Location(String.format("termination_%s", hashCode()));
				Store newStore = new Store(termination, rTerm, "_rx");
				newStore.condLevel = condLevel;

				Thread newThread;
				if(pred != null){
					newThread = new If(pred, new Seq(newLocal, newStore), new Skip());
				} else {
					newThread = new If(aPred, new Seq(newLocal, newStore), new Skip());
				}
				newThread.condLevel = oldCondLevel;
				return newThread;				
			}
			return new Skip();
		}
		else {
			Thread copyT = t.clone();
			copyT.decCondLevel();
			copyT = copyT.unroll(steps);
			int oldCondLevel = condLevel;

			Thread newThread;
			if(pred != null){
				newThread = new If(pred, new Seq(copyT, unroll(steps - 1, obsNoTermination)), new Skip());
			} else {
				newThread = new If(aPred, new Seq(copyT, unroll(steps - 1, obsNoTermination)), new Skip());
			}
			newThread.condLevel = oldCondLevel;
			return newThread;
		}
	}
	
	public Thread unroll(int steps) {
		return unroll(steps, false);
	}

	public While clone() {
		Thread newT = t.clone();
		newT.decCondLevel();

		While newWhile;
		if(pred != null){
			BExpr newPred = pred.clone();
			newWhile = new While(newPred, newT);
		} else {
			AExpr newPred = aPred.clone();
			newWhile = new While(newPred, newT);
		}

		newWhile.condLevel = condLevel;
		return newWhile;
	}
}