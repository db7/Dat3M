package com.dat3m.dartagnan.expression;

import com.dat3m.dartagnan.expression.processing.ExpressionVisitor;

import static com.dat3m.dartagnan.expression.INonDetTypes.UCHAR;
import static com.dat3m.dartagnan.expression.INonDetTypes.UINT;
import static com.dat3m.dartagnan.expression.INonDetTypes.ULONG;
import static com.dat3m.dartagnan.expression.INonDetTypes.USHORT;

import java.math.BigInteger;

import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext;

import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

public class INonDet extends IExpr implements ExprInterface {
	
	private final INonDetTypes type;
	private final int precision;
	
	public INonDet(INonDetTypes type, int precision) {
		this.type = type;
		this.precision = precision;
	}

	public INonDetTypes getType() {
		return type;
	}
	
	@Override
	public IConst reduce() {
        throw new UnsupportedOperationException("Reduce not supported for " + this);
	}

	@Override
	public Formula toZ3Int(Event e, SolverContext ctx) {
		String name = Integer.toString(hashCode());
		FormulaManager fmgr = ctx.getFormulaManager();
		return precision > 0 ? 
				fmgr.getBitvectorFormulaManager().makeVariable(precision, name) : 
				fmgr.getIntegerFormulaManager().makeVariable(name);
	}

	@Override
	public Formula getLastValueExpr(SolverContext ctx) {
		String name = Integer.toString(hashCode());
		FormulaManager fmgr = ctx.getFormulaManager();
		return precision > 0 ? 
				fmgr.getBitvectorFormulaManager().makeVariable(precision, name) : 
				fmgr.getIntegerFormulaManager().makeVariable(name);
	}

	@Override
	public BigInteger getIntValue(Event e, Model model, SolverContext ctx) {
		return new BigInteger(model.evaluate(toZ3Int(e, ctx)).toString());
	}

	@Override
	public ImmutableSet<Register> getRegs() {
		return ImmutableSet.of();
	}

	@Override
	public <T> T visit(ExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
        switch(type){
        case INT:
            return "nondet_int()";
        case UINT:
            return "nondet_uint()";
		case LONG:
			return "nondet_long()";
		case ULONG:
			return "nondet_ulong()";
		case SHORT:
			return "nondet_short()";
		case USHORT:
			return "nondet_ushort()";
		case CHAR:
			return "nondet_char()";
		case UCHAR:
			return "nondet_uchar()";
        }
        throw new UnsupportedOperationException("toString() not supported for " + this);
	}

	public long getMin() {
        switch(type){
        case UINT:
		case ULONG:
		case USHORT:
		case UCHAR:
            return 0;
        case INT:
            return Integer.MIN_VALUE;
		case LONG:
            return Long.MIN_VALUE;
		case SHORT:
            return Short.MIN_VALUE;
		case CHAR:
            return -128;
        }
        throw new UnsupportedOperationException("getMin() not supported for " + this);
	}

	public long getMax() {
        switch(type){
        case INT:
            return Integer.MAX_VALUE;
        case UINT:
            return UnsignedInteger.MAX_VALUE.longValue();
		case LONG:
            return Long.MAX_VALUE;
		case ULONG:
            return UnsignedLong.MAX_VALUE.longValue();
		case SHORT:
            return Short.MAX_VALUE;
		case USHORT:
            return 65535;
		case CHAR:
            return 127;
		case UCHAR:
            return 255;
        }
        throw new UnsupportedOperationException("getMax() not supported for " + this);
	}

	@Override
	public int getPrecision() {
		return precision;
	}
	
	public BooleanFormula encodeBounds(boolean bp, SolverContext ctx) {
		FormulaManager fmgr = ctx.getFormulaManager();
		BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
		BooleanFormula enc = bmgr.makeTrue();
		long min = getMin();
		long max = getMax();
		if(bp) {
			boolean signed = !(type.equals(UINT) || type.equals(ULONG) || type.equals(USHORT) || type.equals(UCHAR));
			enc = bmgr.and(enc, fmgr.getBitvectorFormulaManager().greaterOrEquals((BitvectorFormula) toZ3Int(null,ctx), fmgr.getBitvectorFormulaManager().makeBitvector(precision, min), signed));
	        enc = bmgr.and(enc, fmgr.getBitvectorFormulaManager().lessOrEquals((BitvectorFormula) toZ3Int(null,ctx), fmgr.getBitvectorFormulaManager().makeBitvector(precision, max), signed));
		} else {
			enc = bmgr.and(enc, fmgr.getIntegerFormulaManager().greaterOrEquals((IntegerFormula)toZ3Int(null,ctx), fmgr.getIntegerFormulaManager().makeNumber(min)));
			enc = bmgr.and(enc, fmgr.getIntegerFormulaManager().lessOrEquals((IntegerFormula)toZ3Int(null,ctx), fmgr.getIntegerFormulaManager().makeNumber(max)));
		}
		return enc;
	}
}
