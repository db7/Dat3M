package com.dat3m.dartagnan.program.analysis;

import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.Thread;
import com.dat3m.dartagnan.program.event.core.CondJump;
import com.dat3m.dartagnan.program.event.core.Event;
import com.dat3m.dartagnan.program.event.core.MemEvent;
import com.dat3m.dartagnan.program.event.core.utils.RegReaderData;
import com.dat3m.dartagnan.program.event.core.utils.RegWriter;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Verify.verify;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Computes direct providers for register values, based on when a register is used.
 */
public final class Dependency {

    private final HashMap<Event,Map<Register,State>> map = new HashMap<>();
    private final Map<Register,State> finalWriters = new HashMap<>();

    /**
     * @param program
     * Instruction lists to be analyzed.
     * @param exec
     * Summarizes branching behavior.
     */
    public Dependency(Program program, ExecutionAnalysis exec) {
        for(Thread t: program.getThreads()) {
            process(t, exec);
        }
    }

    /**
     * Queries the collection of providers for a variable, given a certain state of the program.
     * @param reader
     * Event containing some computation over values of the register space.
     * @param register
     * Thread-local program variable used by {@code reader}.
     * @return
     * Local result of this analysis.
     */
    public State of(Event reader, Register register) {
        return map.getOrDefault(reader, Map.of()).getOrDefault(register, new State(false, List.of(), List.of()));
    }

    /**
     * @return
     * Complete set of registers of the analyzed program,
     * mapped to program-ordered list of writers.
     */
    public Map<Register,State> finalWriters() {
        return finalWriters;
    }

    /**
     * Complete set of possible relationships between register writers and register readers,
     * where a reader may receive the value that the writer produced.
     * @return
     * Grouped by reader, then result register.
     * Writers are program-ordered.
     */
    public Collection<Map.Entry<Event,Map<Register,State>>> getAll() {
        return map.entrySet();
    }

    private void process(Thread thread, ExecutionAnalysis exec) {
        Map<Event,Set<Writer>> jumps = new HashMap<>();
        Set<Writer> state = new HashSet<>();
        for(Register register : thread.getRegisters()) {
            state.add(new Writer(register,null));
        }
        for(Event event : thread.getEvents()) {
            //merge with incoming jumps
            Set<Writer> j = jumps.remove(event);
            if(j != null) {
                state.addAll(j);
            }
            //collecting dependencies, mixing 'data' and 'addr'
            Set<Register> registers = new HashSet<>();
            if(event instanceof RegReaderData) {
                registers.addAll(((RegReaderData) event).getDataRegs());
            }
            if(event instanceof MemEvent) {
                registers.addAll(((MemEvent) event).getAddress().getRegs());
            }
            if(!registers.isEmpty()) {
                Map<Register,State> result = new HashMap<>();
                for(Register register : registers) {
                    if(register.getThreadId() == Register.NO_THREAD) {
                        verify(state.stream().noneMatch(w -> w.register.equals(register)),
                                "Thread %s cannot update the global constant %s.",
                                thread.getId(),
                                register.getName());
                        verify(!finalWriters.containsKey(register),
                                "No thread is allowed to update the global constant %s.",
                                register.getName());
                        continue;
                    }
                    State writers;
                    if(register.getThreadId() != event.getThread().getId()) {
                        verify(state.stream().noneMatch(w -> w.register.equals(register)),
                                "Helper thread %s cannot update register %s of thread %s.",
                                thread.getId(),
                                register.getName(),
                                register.getThreadId());
                        //TODO verify that this is a helper thread
                        //FIXME fetch state of the associated Create
                        writers = finalWriters.get(register);
                        verify(writers != null,
                                "Helper thread %s should be listed after their creator thread %s.",
                                thread.getId(),
                                register.getThreadId());
                    }
                    else {
                        writers = process(state, register, exec);
                    }
                    result.put(register, writers);
                }
                map.put(event, result);
            }
            //update state, if changed by event
            if(event instanceof RegWriter) {
                Register register = ((RegWriter) event).getResultRegister();
                if(event.cfImpliesExec()) {
                    state.removeIf(e -> e.register.equals(register));
                }
                state.add(new Writer(register, event));
            }
            //copy state, if branching
            if(event instanceof CondJump) {
                verify(!((CondJump) event).isDead(), "dead jumps after preprocessing");
                jumps.compute(((CondJump) event).getLabel(), (k, v) -> {
                    if(v == null) {
                        return new HashSet<>(state);
                    }
                    v.addAll(state);
                    return v;
                });
                if(((CondJump) event).isGoto()) {
                    state.clear();
                }
            }
        }
        //FIXME there might still be jumps to "END_OF_T"... with cid == -1
        for(Set<Writer> j : jumps.values()) {
            state.addAll(j);
        }
        for(Register register : thread.getRegisters()) {
            finalWriters.put(register, process(state, register, exec));
        }
    }

    private static State process(Set<Writer> state, Register register, ExecutionAnalysis exec) {
        List<Event> candidates = state.stream()
        .filter(e -> e.register.equals(register))
        .map(e -> e.event)
        .collect(toList());
        verify(!candidates.isEmpty(), "Events must at least be able to read the uninitialized value of a register.");
        List<Event> mays = candidates.stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.comparingInt(Event::getCId))
        .collect(Collectors.toCollection(ArrayList::new));
        int end = mays.size();
        List<Event> musts = range(0, end)
        .filter(i -> mays.subList(i + 1, end).stream().allMatch(j -> exec.areMutuallyExclusive(mays.get(i), j)))
        .mapToObj(mays::get)
        .collect(toList());
        return new State(!candidates.contains(null), mays, musts);
    }

    private static final class Writer {
        final Register register;
        final Event event;
        Writer(Register r, Event e) {
            register = Objects.requireNonNull(r);
            event = e;
        }
        @Override
        public boolean equals(Object o) {
            return this==o || o instanceof Writer
            && (event == null
                ? ((Writer) o).event == null && register.equals(((Writer) o).register)
                : event.equals(((Writer) o).event));
        }
        @Override
        public int hashCode() {
            return (event == null ? register : event).hashCode();
        }
    }

    /**
     * Indirectly associated with an instance of {@link Register}, as well as an optional event of the respective thread.
     * When no such event exists, the instance describes the final register values.
     */
    public static final class State {

        /**
         * The analysis was able to determine that in all executions, there is a provider for the register.
         */
        public final boolean initialized;

        /**
         * Complete, but unsound, program-ordered list of direct providers for the register:
         * If there is a program execution where an event of the program was the latest writer, that event is contained in this list.
         */
        public final List<Event> may;

        /**
         * Sound, but incomplete, program-ordered list of direct providers with no overwriting event in between:
         * Each event in this list will be the latest writer in any execution that contains that event.
         */
        public final List<Event> must;

        private State(boolean initialized, List<Event> may, List<Event> must) {
            verify(new HashSet<>(may).containsAll(must), "Each must-writer must also be a may-writer.");
            verify(may.isEmpty() || must.contains(may.get(may.size()-1)), "The last may-writer must also be a must-writer.");
            verify(!initialized || !may.isEmpty(), "Initialized states must have at least one may-writer.");
            this.initialized = initialized;
            this.may = may;
            this.must = must;
        }
    }
}
