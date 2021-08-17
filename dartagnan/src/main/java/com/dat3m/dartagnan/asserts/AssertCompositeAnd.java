package com.dat3m.dartagnan.asserts;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.SolverContext;

import com.dat3m.dartagnan.program.memory.Location;
import com.google.common.collect.ImmutableSet;

public class AssertCompositeAnd extends AbstractAssert {

    private final AbstractAssert a1;
    private final AbstractAssert a2;

    public AssertCompositeAnd(AbstractAssert a1, AbstractAssert a2){
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    public ImmutableSet<Location> getLocs() {
        return new ImmutableSet.Builder<Location>().addAll(a1.getLocs()).addAll(a2.getLocs()).build();
    }

    @Override
    public AbstractAssert removeLocAssertions(boolean replaceByTrue) {
        return new AssertCompositeAnd(a1.removeLocAssertions(replaceByTrue), a2.removeLocAssertions(replaceByTrue));
    }

    @Override
    public BooleanFormula encode(SolverContext ctx) {
        return ctx.getFormulaManager().getBooleanFormulaManager().and(a1.encode(ctx), a2.encode(ctx));
    }

    @Override
    public String toString() {
        return a1 + " && " + a2;
    }
}
