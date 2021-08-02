package com.dat3m.dartagnan.expression;

import java.math.BigInteger;

import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext;

import com.dat3m.dartagnan.program.event.Event;

public abstract class IExpr implements ExprInterface {

    @Override
	public BooleanFormula toZ3Bool(Event e, SolverContext ctx) {
		return toZ3Int(e, ctx) instanceof BitvectorFormula ? 
				ctx.getFormulaManager().getBitvectorFormulaManager().greaterThan((BitvectorFormula)toZ3Int(e, ctx), ctx.getFormulaManager().getBitvectorFormulaManager().makeBitvector(getPrecision(), BigInteger.ZERO), false) :
					ctx.getFormulaManager().getIntegerFormulaManager().greaterThan((IntegerFormula)toZ3Int(e, ctx), ctx.getFormulaManager().getIntegerFormulaManager().makeNumber(BigInteger.ZERO));
	}

    @Override
    public boolean getBoolValue(Event e, Model model, SolverContext ctx){
        return getIntValue(e, model, ctx).signum() == 1;
    }

	@Override
	public IExpr getBase() {
		return this;
	}
	
	public IExpr simplify() {
		return this;
	}
}
