package com.dat3m.dartagnan.program.processing;

import com.dat3m.dartagnan.expression.Atom;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.Thread;
import com.dat3m.dartagnan.program.event.EventFactory;
import com.dat3m.dartagnan.program.event.Tag;
import com.dat3m.dartagnan.program.event.core.Event;
import com.dat3m.dartagnan.program.event.core.Label;
import com.dat3m.dartagnan.program.event.lang.svcomp.BeginAtomic;
import com.dat3m.dartagnan.program.event.lang.svcomp.EndAtomic;
import com.dat3m.dartagnan.program.memory.Address;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;

import java.math.BigInteger;

import static com.dat3m.dartagnan.expression.op.COpBin.NEQ;
import static com.dat3m.dartagnan.program.event.EventFactory.newInit;
import static com.dat3m.dartagnan.program.event.EventFactory.newLabel;

/**
Substitutes all atomic markers with accesses to a global mutex.
May be performed right after parsing.
TODO pure boolean precision
FIXME reuses oId
TODO This analysis is buggy right now. It complains that Init events were not compiled 
*/
public class AtomicAsLock implements ProgramProcessor {

	private AtomicAsLock() {}

	public static AtomicAsLock fromConfig(Configuration c) throws InvalidConfigurationException {
		return new AtomicAsLock();
	}

	@Override
	public void run(Program program) {
		Address a = program.getMemory().newLocation(1);
		for(Thread t : program.getThreads()) {
			run(a,t);
		}
		//TODO unmodifiable thread list?
		program.getThreads().add(new Thread(
			1+program.getThreads().stream().mapToInt(Thread::getId).max().orElse(-1),
			newInit(a,0,new IConst(BigInteger.ZERO,-1))));
	}

	private void run(Address address, Thread thread) {
		Label end;
		if(thread.getExit() instanceof Label) {
			end = (Label)thread.getExit();
		}
		else {
			end = newLabel("__VERIFIER_atomic_thread_end");
			thread.getExit().setSuccessor(end);
			thread.updateExit(end);
		}
		Register register = new Register("__VERIFIER_atomic_dummy",thread.getId(),-1);
		for(Event predecessor = thread.getEntry(); predecessor != null;) {
			Event event = predecessor.getSuccessor();
			boolean begin = event instanceof BeginAtomic;
			if(begin || event instanceof EndAtomic) {
				Event load = EventFactory.newLoad(register,address,null);
				Event check = EventFactory.newJump(new Atom(register,NEQ,new IConst(begin?BigInteger.ZERO:BigInteger.ONE,-1)),end);
				Event store = EventFactory.newStore(address,new IConst(begin?BigInteger.ONE:BigInteger.ZERO,-1),null);
				load.setOId(event.getOId());
				check.setOId(event.getOId());
				store.setOId(event.getOId());
				load.addFilters(Tag.C11.LOCK, Tag.RMW);
				check.addFilters(Tag.C11.LOCK, Tag.RMW);
				store.addFilters(Tag.C11.LOCK, Tag.RMW);
				predecessor.setSuccessor(load);
				load.setSuccessor(check);
				check.setSuccessor(store);
				store.setSuccessor(event.getSuccessor());
				predecessor = store;
			}
			else {
				predecessor = event;
			}
		}
	}
}