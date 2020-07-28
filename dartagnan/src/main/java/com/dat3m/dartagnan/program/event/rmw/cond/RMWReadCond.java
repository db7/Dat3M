package com.dat3m.dartagnan.program.event.rmw.cond;

import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.utils.EType;
import com.dat3m.dartagnan.utils.EncodingConf;
import com.google.common.collect.ImmutableSet;
import com.microsoft.z3.BoolExpr;
import com.dat3m.dartagnan.expression.ExprInterface;
import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.rmw.RMWLoad;
import com.dat3m.dartagnan.program.event.utils.RegReaderData;
import com.dat3m.dartagnan.program.event.utils.RegWriter;

public abstract class RMWReadCond extends RMWLoad implements RegWriter, RegReaderData {

    protected ExprInterface cmp;
    private ImmutableSet<Register> dataRegs;

    BoolExpr z3Cond;

    RMWReadCond(Register reg, ExprInterface cmp, IExpr address, String atomic) {
        super(reg, address, atomic);
        this.cmp = cmp;
        this.dataRegs = cmp.getRegs();
        addFilters(EType.REG_READER);
    }

    @Override
    public void initialise(EncodingConf conf) {
        super.initialise(conf);
        z3Cond = conf.getCtx().mkEq(memValueExpr, cmp.toZ3Int(this, conf));
    }

    public BoolExpr getCond(){
        if(z3Cond != null){
            return z3Cond;
        }
        throw new RuntimeException("z3Cond is requested before it has been initialised in " + this.getClass().getName());
    }

    @Override
    public ImmutableSet<Register> getDataRegs(){
        return dataRegs;
    }

    public abstract String condToString();

    // Unrolling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void unroll(int bound, Event predecessor) {
        throw new RuntimeException("RMWReadCond cannot be unrolled: event must be generated during compilation");
    }
}
