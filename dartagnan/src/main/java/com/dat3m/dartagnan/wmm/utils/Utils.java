package com.dat3m.dartagnan.wmm.utils;

import com.dat3m.dartagnan.program.event.Event;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext;

public class Utils {

	public static BooleanFormula edge(String relName, Event e1, Event e2, SolverContext ctx) {
		FormulaManager fmgr = ctx.getFormulaManager();
		return fmgr.getBooleanFormulaManager().makeVariable(fmgr.escape(relName) + "(" + e1.repr() + "," + e2.repr() + ")");
	}

	public static IntegerFormula intVar(String relName, Event e, SolverContext ctx) {
		FormulaManager fmgr = ctx.getFormulaManager();
		return fmgr.getIntegerFormulaManager().makeVariable(fmgr.escape(relName) + "(" + e.repr() + ")");
	}

}