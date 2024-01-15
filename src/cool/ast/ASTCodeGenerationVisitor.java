package cool.ast;

import cool.structures.ClassSymbol;
import cool.structures.Scope;
import cool.structures.Symbol;
import cool.structures.SymbolTable;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;

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
    HashMap<String, HashMap<String, ClassSymbol>> classDispTabHt; ///nume clasa -> (nume functie -> din care clasa apelez functia).
                                                                  ///TODO poate trebuie si a cata intrare in lista este.

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
        ST literal = templates.getInstanceOf("literal");
        literal.add("value", intt.getToken().getText());
        return literal;
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
        String className = classDef.token.getText();
        addConstString(className);

        classNameList.add("e", templates.getInstanceOf("classNameEntry")
                .add("classNameIndex", constStringHt.get(className).toString()));

        classObjTabList.add("e", templates.getInstanceOf("classObjTabEntry")
                .add("className", className));
        classObjTabHt.put(className, classObjTabHt.size());

        ///mai tb explorate metodele din clase aici?
        int cntAttributes = 0;
        for (Feature f: classDef.features) if (f instanceof Attribute) cntAttributes++;

        classProtObjList.add("e", templates.getInstanceOf("classProtObjEntry")
                .add("className", className)
                .add("classTag", classObjTabHt.get(className).toString())
                .add("size", ((Integer) (3 + cntAttributes)).toString())
                .add("features", "")
        );

        //classInitSignatureList.add("e", templates.getInstanceOf("classInitSignatureEntry").add("className", className)); ///? idk de ce nu.

        ClassSymbol cs = (ClassSymbol) SymbolTable.globals.lookup(className);
        populateClassDispatchTable(cs);
        computeClassInit(cs);

        for (Feature f: classDef.features) {
            if (f instanceof FuncDef) {
                ST functionPreamble = templates.getInstanceOf("functionPreamble");

                functionPreamble.add("funcName", cs.getName() + "." + f.token.getText());
                functionPreamble.add("body", ((FuncDef) f).body.accept(this));

                functionInitBodyList.add("e", functionPreamble);
            }
        }

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
        classDispTabHt = new HashMap<>();

        classInitSignatureList = templates.getInstanceOf("sequence");
        functionSignatureList = templates.getInstanceOf("sequence");
        classInitBodyList = templates.getInstanceOf("sequence");
        functionInitBodyList = templates.getInstanceOf("sequence");

        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "0").add("value", "0"));
        constBoolList.add("e", templates.getInstanceOf("constBoolEntry").add("index", "1").add("value", "1"));
        programST.add("constBool", constBoolList);

        addConstString("");
        for (ClassSymbol baseClassSymbol: List.of(ClassSymbol.OBJECT, ClassSymbol.IO, ClassSymbol.INT, ClassSymbol.STRING, ClassSymbol.BOOL)) {
            String baseClassName = baseClassSymbol.getName();

            addConstString(baseClassName);

            classNameList.add("e", templates.getInstanceOf("classNameEntry")
                    .add("classNameIndex", constStringHt.get(baseClassName).toString()));

            classObjTabList.add("e", templates.getInstanceOf("classObjTabEntry")
                    .add("className", baseClassName));
            classObjTabHt.put(baseClassName, classObjTabHt.size());

            ///folosesc classObjTabHt si pentru <class>_protObj.
            int size = 0;
            String features = "";

            switch (baseClassName) {
                case "Object" -> size = 3;
                case "IO" -> size = 3;
                case "String" -> { size = 5; features = ".word int_const0\n.asciiz \"\"\n.align 2\n"; }
                case "Int", "Bool" -> { size = 4; features = ".word 0\n"; }
                default -> {}
            }

            classProtObjList.add("e", templates.getInstanceOf("classProtObjEntry")
                    .add("className", baseClassName)
                    .add("classTag", classObjTabHt.get(baseClassName).toString())
                    .add("size", ((Integer) size).toString())
                    .add("features", features)
            );

            populateClassDispatchTable(baseClassSymbol);
            computeClassInit(baseClassSymbol);
        }

        ///dintr-un motiv, clasele default care pot mosteni nu sunt aici.
        ///TODO mai sunt si alte lucruri la classInitSignature si functionSignature?
        classInitSignatureList.add("e", templates.getInstanceOf("classInitSignatureEntry").add("className", "Int"));
        classInitSignatureList.add("e", templates.getInstanceOf("classInitSignatureEntry").add("className", "String"));
        classInitSignatureList.add("e", templates.getInstanceOf("classInitSignatureEntry").add("className", "Bool"));
        functionSignatureList.add("e", templates.getInstanceOf("functionSignatureEntry").add("className", "Main").add("funcName", "main"));

        for (ASTNode cd: program.stmts) {
            visit((ClassDef) cd);
        }

        programST.add("constString", constStringList);
        programST.add("className", classNameList);
        programST.add("classObjTab", classObjTabList);
        programST.add("classProtObj", classProtObjList);
        programST.add("classDispTab", classDispTabList);
        programST.add("classInitSignature", classInitSignatureList);
        programST.add("functionSignature", functionSignatureList);
        programST.add("classInitBody", classInitBodyList);
        programST.add("functionInitBody", functionInitBodyList);

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

    private void populateClassDispatchTable(ClassSymbol cs) {
        ST classDispTabListEntry = templates.getInstanceOf("sequence");

        String ogClassName = cs.getName(), className = cs.getName();

        classDispTabHt.put(ogClassName, new HashMap<>());
        while (cs != null) {
            for (Map.Entry<String, Symbol> entry: cs.getFunctionSymbols().entrySet()) {
                String funcName = entry.getKey();

                ///clasa -> (fname -> clasa din care e apelat.)
                if (!classDispTabHt.get(ogClassName).containsKey(funcName)) {
                    classDispTabHt.get(ogClassName).put(funcName, cs);
                    classDispTabListEntry.add("e", templates.getInstanceOf("functionPointerEntry")
                            .add("className", className)
                            .add("funcName", funcName));
                }
            }

            Scope parent = cs.getParent();
            if (parent != null) {
                cs = (ClassSymbol) parent.lookupClass();
                if (cs != null) className = cs.getName();
            } else {
                cs = null;
            }
        }

        ST classDispTabEntry = templates.getInstanceOf("classDispTabEntry");
        classDispTabEntry.add("className", ogClassName);
        classDispTabEntry.add("functionPointer", classDispTabListEntry);

        classDispTabList.add("e", classDispTabEntry);
    }

    private void computeClassInit(ClassSymbol cs) {
        ST functionPreamble = templates.getInstanceOf("functionPreamble");
        functionPreamble.add("funcName", cs.getName() + "_init");

        Scope parent = cs.getParent();
        if (parent != null) {
            ClassSymbol parentSymbol = (ClassSymbol) parent.lookupClass();
            if (parentSymbol != null) {
                ///init catre parinte.
                functionPreamble.add("body", "jal " + parentSymbol.getName() + "_init");
            }
        }

        classInitBodyList.add("e", functionPreamble);
    }
}
