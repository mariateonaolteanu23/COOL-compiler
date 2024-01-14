package cool.ast;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class ASTCodeGenerationVisitor implements ASTVisitor<ST>{
    static STGroupFile templates = new STGroupFile("./src/cool/ast/cgen.stg");

    ST constStringList;
    HashMap<String, Integer> constStringHt; ///const string -> ce index am folosit pentru el.

    ST constIntList;
    TreeSet<Integer> constIntSet; ///pentru intregi folosesc identitate ca mapare.

    ST constBoolList;
    ST classNameList;

    ST classObjTabList;
    HashMap<String, Integer> classObjTabHt; ///nume clasa -> a cata intrare in class_objTab e a clasei.

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
        addConstString(classDef.token.getText());

        classNameList.add("e", templates.getInstanceOf("classNameEntry")
                .add("classNameIndex", constStringHt.get(classDef.token.getText()).toString()));

        classObjTabList.add("e", templates.getInstanceOf("classObjTabEntry")
                .add("className", classDef.token.getText()));
        classObjTabHt.put(classDef.token.getText(), classObjTabHt.size());

        return null;
    }

    @Override
    public ST visit(Program program) {
        ST programST = templates.getInstanceOf("program");

        constStringList = templates.getInstanceOf("sequence");
        constStringHt = new HashMap<>();

        constIntList = templates.getInstanceOf("sequence");
        constIntSet = new TreeSet<>();

        constBoolList = templates.getInstanceOf("sequence");
        classNameList = templates.getInstanceOf("sequence");

        classObjTabList = templates.getInstanceOf("sequence");
        classObjTabHt = new HashMap<>();

        classProtObjList = templates.getInstanceOf("sequence");
        classDispTabList = templates.getInstanceOf("sequence");
        classInitSignatureList = templates.getInstanceOf("sequence");
        functionSignatureList = templates.getInstanceOf("sequence");
        classInitBodyList = templates.getInstanceOf("sequence");
        functionInitBodyList = templates.getInstanceOf("sequence");

        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "0").add("value", "0"));
        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "1").add("value", "1"));
        programST.add("constBool", constBoolList);

        addConstString("");
        for (String baseClassName: List.of("Object", "IO", "String", "Int", "Bool")) {
            addConstString(baseClassName);

            classNameList.add("e", templates.getInstanceOf("classNameEntry")
                    .add("classNameIndex", constStringHt.get(baseClassName).toString()));

            classObjTabList.add("e", templates.getInstanceOf("classObjTabEntry")
                    .add("className", baseClassName));
            classObjTabHt.put(baseClassName, classObjTabHt.size());
        }

        for (ASTNode cd: program.stmts) {
            visit((ClassDef) cd);
        }

        programST.add("constString", constStringList);
        programST.add("className", classNameList);
        programST.add("classObjTab", classObjTabList);

        for (Integer k: constIntSet) { ///TODO constante negative?
            constIntList.add("e", templates.getInstanceOf("constIntEntry").add("index", k.toString()).add("value", k.toString()));
        }
        programST.add("constInt", constIntList);

        return programST;
    }

    private void addConstString(String s) {
        if (constStringHt.containsKey(s)) return;

        int labelSize = (s.length()+1 + 3) / 4 + 4;

        constStringList.add("e", templates.getInstanceOf("constStringEntry")
                .add("index", ((Integer) constStringHt.size()).toString())
                .add("size", ((Integer) labelSize).toString())
                .add("indexLengthConstInt", s.length())
                .add("value", s)
        );

        constStringHt.put(s, constStringHt.size());
        constIntSet.add(s.length());
    }
}
