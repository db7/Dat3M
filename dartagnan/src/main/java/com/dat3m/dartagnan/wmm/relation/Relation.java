package com.dat3m.dartagnan.wmm.relation;

import com.dat3m.dartagnan.utils.Settings;
import com.dat3m.dartagnan.utils.dependable.Dependent;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.dat3m.dartagnan.wmm.relation.base.stat.StaticRelation;
import com.dat3m.dartagnan.wmm.relation.binary.BinaryRelation;
import com.dat3m.dartagnan.wmm.relation.unary.UnaryRelation;
import com.dat3m.dartagnan.wmm.utils.Mode;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.wmm.utils.Tuple;
import com.dat3m.dartagnan.wmm.utils.TupleSet;

import java.util.*;

import static com.dat3m.dartagnan.wmm.utils.Utils.edge;

/**
 *
 * @author Florian Furbach
 */
public abstract class Relation implements Dependent<Relation> {

    public static boolean PostFixApprox = false;

    protected String name;
    protected String term;

    protected VerificationTask task;

    protected boolean isEncoded;

    protected TupleSet maxTupleSet;
    protected TupleSet encodeTupleSet;

    protected int recursiveGroupId = 0;
    protected boolean forceUpdateRecursiveGroupId = false;
    protected boolean isRecursive = false;
    protected boolean forceDoEncode = false;

    public Relation() {}

    public Relation(String name) {
        this.name = name;
    }

    @Override
    public List<Relation> getDependencies() {
        return Collections.emptyList();
    }

    public int getRecursiveGroupId(){
        return recursiveGroupId;
    }

    public void setRecursiveGroupId(int id){
        forceUpdateRecursiveGroupId = true;
        recursiveGroupId = id;
    }

    public int updateRecursiveGroupId(int parentId){
        return recursiveGroupId;
    }

    public void initialise(VerificationTask task, Context ctx){
        this.task = task;
        this.maxTupleSet = null;
        this.isEncoded = false;
        encodeTupleSet = new TupleSet();
    }

    public abstract TupleSet getMaxTupleSet();

    public TupleSet getMaxTupleSetRecursive(){
        return getMaxTupleSet();
    }

    public TupleSet getEncodeTupleSet(){
        return encodeTupleSet;
    }

    public void addEncodeTupleSet(TupleSet tuples){
        encodeTupleSet.addAll(tuples);
    }

    public String getName() {
        if(name != null){
            return name;
        }
        return term;
    }

    public Relation setName(String name){
        this.name = name;
        return this;
    }

    public String getTerm(){
        return term;
    }

    public boolean getIsNamed(){
        return name != null;
    }

    @Override
    public String toString(){
        if(name != null){
            return name + " := " + term;
        }
        return term;
    }

    @Override
    public int hashCode(){
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return getName().equals(((Relation)obj).getName());
    }

    public BoolExpr encode(Context ctx) {
        if(isEncoded){
            return ctx.mkTrue();
        }
        isEncoded = true;
        return doEncode(ctx);
    }

    protected BoolExpr encodeLFP(Context ctx) {
        return encodeApprox(ctx);
    }

    protected BoolExpr encodeIDL(Context ctx) {
        return encodeApprox(ctx);
    }

    protected abstract BoolExpr encodeApprox(Context ctx);

    public BoolExpr encodeIteration(int recGroupId, int iteration, Context ctx){
        return ctx.mkTrue();
    }

    protected BoolExpr doEncode(Context ctx){
        BoolExpr enc = encodeNegations(ctx);
        if(!encodeTupleSet.isEmpty() || forceDoEncode){
            if(task.getSettings().getMode() == Mode.KLEENE) {
                return ctx.mkAnd(enc, encodeLFP(ctx));
            } else if(task.getSettings().getMode() == Mode.IDL) {
                return ctx.mkAnd(enc, encodeIDL(ctx));
            }
            return ctx.mkAnd(enc, encodeApprox(ctx));
        }
        return enc;
    }

    private BoolExpr encodeNegations(Context ctx){
        BoolExpr enc = ctx.mkTrue();
        if(!encodeTupleSet.isEmpty()){
            Set<Tuple> negations = new HashSet<>(encodeTupleSet);
            negations.removeAll(maxTupleSet);
            for(Tuple tuple : negations){
                enc = ctx.mkAnd(enc, ctx.mkNot(edge(this.getName(), tuple.getFirst(), tuple.getSecond(), ctx)));
            }
            encodeTupleSet.removeAll(negations);
        }
        return enc;
    }


    // ========================== Utility methods =========================
    public boolean isStaticRelation() { return this instanceof StaticRelation; }
    public boolean isUnaryRelation() { return this instanceof UnaryRelation; }
    public boolean isBinaryRelation() { return this instanceof BinaryRelation; }
    public boolean isRecursiveRelation() { return this instanceof RecursiveRelation; }

    public Relation getInner() {
        return (isUnaryRelation() || isRecursiveRelation()) ? getDependencies().get(0) : null;
    }
    public Relation getFirst() { return isBinaryRelation() ? getDependencies().get(0) : null; }
    public Relation getSecond() { return isBinaryRelation() ? getDependencies().get(1) : null; }
}
