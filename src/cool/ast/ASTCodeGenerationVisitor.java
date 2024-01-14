package cool.ast;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

public class ASTCodeGenerationVisitor implements ASTVisitor<ST>{
    static STGroupFile templates = new STGroupFile("./src/cool/ast/cgen.stg");

    ST constStringList;
    ST constIntList;
    ST constBoolList;
    ST classNameList;
    ST classObjTabList;
    ST classProtObjList;
    ST classDispTabList;
    ST classInitSignatureList;
    ST functionSignatureList;
    ST classInitBodyList;
    ST functionInitBodyList;

    @Override
    public ST visit(Id id) {
        return null;
    }

    @Override
    public ST visit(Int intt) {
        return null;
    }

    @Override
    public ST visit(Block block) {
        return null;
    }

    @Override
    public ST visit(Local local) {
        return null;
    }

    @Override
    public ST visit(CaseBranch caseBranch) {
        return null;
    }

    @Override
    public ST visit(If iff) {
        return null;
    }

    @Override
    public ST visit(While whilee) {
        return null;
    }

    @Override
    public ST visit(Let let) {
        return null;
    }

    @Override
    public ST visit(Case casee) {
        return null;
    }

    @Override
    public ST visit(Bool bool) {
        return null;
    }

    @Override
    public ST visit(Stringg string) {
        return null;
    }

    @Override
    public ST visit(New neww) {
        return null;
    }

    @Override
    public ST visit(BitComplement bitComplement) {
        return null;
    }

    @Override
    public ST visit(Not not) {
        return null;
    }

    @Override
    public ST visit(Isvoid isvoid) {
        return null;
    }

    @Override
    public ST visit(Assign assign) {
        return null;
    }

    @Override
    public ST visit(Relational rel) {
        return null;
    }

    @Override
    public ST visit(Plus plus) {
        return null;
    }

    @Override
    public ST visit(Minus minus) {
        return null;
    }

    @Override
    public ST visit(Mult mult) {
        return null;
    }

    @Override
    public ST visit(Div div) {
        return null;
    }

    @Override
    public ST visit(UnaryArithmetic unaryArithmetic) {
        return null;
    }

    @Override
    public ST visit(Type type) {
        return null;
    }

    @Override
    public ST visit(Formal formal) {
        return null;
    }

    @Override
    public ST visit(Attribute attribute) {
        return null;
    }

    @Override
    public ST visit(ImplicitDispatch implicitDispatch) {
        return null;
    }

    @Override
    public ST visit(ExplicitDispatch explicitDispatch) {
        return null;
    }

    @Override
    public ST visit(FuncDef funcDef) {
        return null;
    }

    @Override
    public ST visit(ClassDef classDef) {
        return null;
    }

    @Override
    public ST visit(Program program) {
        ST programST = templates.getInstanceOf("program");

        constStringList = templates.getInstanceOf("sequence");
        constIntList = templates.getInstanceOf("sequence");
        constBoolList = templates.getInstanceOf("sequence");
        classNameList = templates.getInstanceOf("sequence");
        classObjTabList = templates.getInstanceOf("sequence");
        classProtObjList = templates.getInstanceOf("sequence");
        classDispTabList = templates.getInstanceOf("sequence");
        classInitSignatureList = templates.getInstanceOf("sequence");
        functionSignatureList = templates.getInstanceOf("sequence");
        classInitBodyList = templates.getInstanceOf("sequence");
        functionInitBodyList = templates.getInstanceOf("sequence");

        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "0").add("value", "0"));
        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "1").add("value", "1"));

        programST.add("constBool", constBoolList);

        return programST;
    }
}
