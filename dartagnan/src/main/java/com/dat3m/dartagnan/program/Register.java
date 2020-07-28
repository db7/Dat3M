package com.dat3m.dartagnan.program;

import com.google.common.collect.ImmutableSet;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.dat3m.dartagnan.expression.ExprInterface;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.utils.EncodingConf;
import com.dat3m.dartagnan.program.event.Event;

public class Register extends IExpr implements ExprInterface {

	private static int dummyCount = 0;

	private final String name;
    private final int threadId;

	public Register(String name, int threadId) {
		if(name == null){
			name = "DUMMY_REG_" + dummyCount++;
		}
		this.name = name;
		this.threadId = threadId;
	}
	
	public String getName() {
		return name;
	}

	public int getThreadId(){
		return threadId;
	}

	@Override
	public String toString() {
        return name;
	}

    @Override
    public int hashCode(){
        return (name.hashCode() << 8) + threadId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Register rObj = (Register) obj;
        return name.equals(rObj.name) && threadId == rObj.threadId;
    }

	@Override
	public Expr toZ3Int(Event e, EncodingConf conf) {
		String name = getName() + "(" + e.repr() + ")";
		Context ctx = conf.getCtx();
		return conf.getBP() ? ctx.mkBVConst(name, 32) : ctx.mkIntConst(name);
	}

	public Expr toZ3IntResult(Event e, EncodingConf conf) {
		String name = getName() + "(" + e.repr() + "_result)";
		Context ctx = conf.getCtx();
		return conf.getBP() ? ctx.mkBVConst(name, 32) : ctx.mkIntConst(name);
	}

	@Override
	public ImmutableSet<Register> getRegs() {
		return ImmutableSet.of(this);
	}

	@Override
	public Expr getLastValueExpr(EncodingConf conf){
		Context ctx = conf.getCtx();
		return conf.getBP() ? ctx.mkBVConst(getName() + "_" + threadId + "_final", 32) : ctx.mkIntConst(getName() + "_" + threadId + "_final");
	}

	@Override
	public int getIntValue(Event e, Model model, EncodingConf conf){
		return Integer.parseInt(model.getConstInterp(toZ3Int(e, conf)).toString());
	}

	@Override
	public IConst reduce() {
		throw new UnsupportedOperationException("Reduce not supported for " + this);
	}
}
