package dartagnan.parsers.visitors;

import dartagnan.expression.IConst;
import dartagnan.expression.IExprBin;
import dartagnan.expression.op.IOpBin;
import dartagnan.parsers.LitmusPPCBaseVisitor;
import dartagnan.parsers.LitmusPPCParser;
import dartagnan.parsers.LitmusPPCVisitor;
import dartagnan.parsers.utils.AssertionHelper;
import dartagnan.parsers.utils.ParsingException;
import dartagnan.parsers.utils.ProgramBuilder;
import dartagnan.parsers.utils.branch.Cmp;
import dartagnan.parsers.utils.branch.CondJump;
import dartagnan.program.Register;
import dartagnan.program.event.Fence;
import dartagnan.program.event.Load;
import dartagnan.program.event.Local;
import dartagnan.program.event.Store;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VisitorLitmusPPC
        extends LitmusPPCBaseVisitor<Object>
        implements LitmusPPCVisitor<Object> {

    private final static Set<String> fences = new HashSet<>(Arrays.asList("Sync", "Lwsync", "Isync"));

    private ProgramBuilder programBuilder;
    private String mainThread;
    private Integer threadCount = 0;

    public VisitorLitmusPPC(ProgramBuilder pb){
        this.programBuilder = pb;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Entry point

    @Override
    public Object visitMain(LitmusPPCParser.MainContext ctx) {
        visitThreadDeclaratorList(ctx.program().threadDeclaratorList());
        visitVariableDeclaratorList(ctx.variableDeclaratorList());
        visitInstructionList(ctx.program().instructionList());
        if(ctx.assertionList() != null){
            int a = ctx.assertionList().getStart().getStartIndex();
            int b = ctx.assertionList().getStop().getStopIndex();
            String raw = ctx.assertionList().getStart().getInputStream().getText(new Interval(a, b));
            programBuilder.setAssert(AssertionHelper.parseAssertionList(programBuilder, raw));
        }
        if(ctx.assertionFilter() != null){
            int a = ctx.assertionFilter().getStart().getStartIndex();
            int b = ctx.assertionFilter().getStop().getStopIndex();
            String raw = ctx.assertionFilter().getStart().getInputStream().getText(new Interval(a, b));
            programBuilder.setAssertFilter(AssertionHelper.parseAssertionFilter(programBuilder, raw));
        }
        return programBuilder.build();
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Variable declarator list, e.g., { 0:EAX=0; 1:EAX=1; x=2; }

    @Override
    public Object visitVariableDeclaratorLocation(LitmusPPCParser.VariableDeclaratorLocationContext ctx) {
        programBuilder.initLocEqConst(ctx.location().getText(), new IConst(Integer.parseInt(ctx.constant().getText())));
        return null;
    }

    @Override
    public Object visitVariableDeclaratorRegister(LitmusPPCParser.VariableDeclaratorRegisterContext ctx) {
        programBuilder.initRegEqConst(ctx.threadId().id, ctx.register().getText(), new IConst(Integer.parseInt(ctx.constant().getText())));
        return null;
    }

    @Override
    public Object visitVariableDeclaratorRegisterLocation(LitmusPPCParser.VariableDeclaratorRegisterLocationContext ctx) {
        programBuilder.initRegEqLocPtr(ctx.threadId().id, ctx.register().getText(), ctx.location().getText());
        return null;
    }

    @Override
    public Object visitVariableDeclaratorLocationLocation(LitmusPPCParser.VariableDeclaratorLocationLocationContext ctx) {
        programBuilder.initLocEqLocPtr(ctx.location(0).getText(), ctx.location(1).getText());
        return null;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Thread declarator list (on top of instructions), e.g. " P0  |   P1  |   P2  ;"

    @Override
    public Object visitThreadDeclaratorList(LitmusPPCParser.ThreadDeclaratorListContext ctx) {
        for(LitmusPPCParser.ThreadIdContext threadCtx : ctx.threadId()){
            programBuilder.initThread(threadCtx.id);
            threadCount++;
        }
        return null;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Instruction list (the program itself)

    @Override
    public Object visitInstructionRow(LitmusPPCParser.InstructionRowContext ctx) {
        for(Integer i = 0; i < threadCount; i++){
            mainThread = i.toString();
            visitInstruction(ctx.instruction(i));
        }
        return null;
    }

    @Override
    public Object visitLi(LitmusPPCParser.LiContext ctx) {
        Register register = programBuilder.getOrCreateRegister(mainThread, ctx.register().getText());
        IConst constant = new IConst(Integer.parseInt(ctx.constant().getText()));
        return programBuilder.addChild(mainThread, new Local(register, constant));
    }

    @Override
    public Object visitLwz(LitmusPPCParser.LwzContext ctx) {
        Register r1 = programBuilder.getOrCreateRegister(mainThread, ctx.register(0).getText());
        Register ra = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        return programBuilder.addChild(mainThread, new Load(r1, ra, "_rx"));
    }

    @Override
    public Object visitLwzx(LitmusPPCParser.LwzxContext ctx) {
        // TODO: Implementation
        throw new ParsingException("lwzx is not implemented");
    }

    @Override
    public Object visitStw(LitmusPPCParser.StwContext ctx) {
        Register r1 = programBuilder.getOrErrorRegister(mainThread, ctx.register(0).getText());
        Register ra = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        return programBuilder.addChild(mainThread, new Store(ra, r1, "_rx"));
    }

    @Override
    public Object visitStwx(LitmusPPCParser.StwxContext ctx) {
        // TODO: Implementation
        throw new ParsingException("stwx is not implemented");
    }

    @Override
    public Object visitMr(LitmusPPCParser.MrContext ctx) {
        Register r1 = programBuilder.getOrCreateRegister(mainThread, ctx.register(0).getText());
        Register r2 = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        return programBuilder.addChild(mainThread, new Local(r1, r2));
    }

    @Override
    public Object visitAddi(LitmusPPCParser.AddiContext ctx) {
        Register r1 = programBuilder.getOrCreateRegister(mainThread, ctx.register(0).getText());
        Register r2 = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        IConst constant = new IConst(Integer.parseInt(ctx.constant().getText()));
        return programBuilder.addChild(mainThread, new Local(r1, new IExprBin(r2, IOpBin.PLUS, constant)));
    }

    @Override
    public Object visitXor(LitmusPPCParser.XorContext ctx) {
        Register r1 = programBuilder.getOrCreateRegister(mainThread, ctx.register(0).getText());
        Register r2 = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        Register r3 = programBuilder.getOrErrorRegister(mainThread, ctx.register(2).getText());
        return programBuilder.addChild(mainThread, new Local(r1, new IExprBin(r2, IOpBin.XOR, r3)));
    }

    @Override
    public Object visitCmpw(LitmusPPCParser.CmpwContext ctx) {
        Register r1 = programBuilder.getOrErrorRegister(mainThread, ctx.register(0).getText());
        Register r2 = programBuilder.getOrErrorRegister(mainThread, ctx.register(1).getText());
        return programBuilder.addChild(mainThread, new Cmp(r1, r2));
    }

    @Override
    public Object visitBranchCond(LitmusPPCParser.BranchCondContext ctx) {
        return programBuilder.addChild(mainThread, new CondJump(
                ctx.cond().op,
                programBuilder.getOrCreateLabel(mainThread, ctx.Label().getText())
        ));
    }

    @Override
    public Object visitLabel(LitmusPPCParser.LabelContext ctx) {
        return programBuilder.addChild(mainThread, programBuilder.getOrCreateLabel(mainThread, ctx.Label().getText()));
    }

    @Override
    public Object visitFence(LitmusPPCParser.FenceContext ctx) {
        String name = ctx.getText().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        if(fences.contains(name)){
            return programBuilder.addChild(mainThread, new Fence(name));
        }
        throw new ParsingException("Unrecognised fence " + name);
    }
}
