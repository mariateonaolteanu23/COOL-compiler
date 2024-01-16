package cool.ast;

import cool.ast.CGHelp;

import cool.compiler.Compiler;
import cool.structures.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ASTCodeGenerationVisitor implements ASTVisitor<ST> {
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
    HashMap<String, HashMap<String, Integer>> classProtObjHt; ///nume clasa -> (nume atribut -> al catelea este in lista obiectului prototip).

    ST classDispTabList;
    HashMap<String, HashMap<String, CGHelp.Pair<ClassSymbol, Integer>>> classDispTabHt; // A a; a.f()
    ///nume clasa -> (nume functie -> <din care clasa apelez functia, a cata in lista e functia>).

    ST classInitSignatureList;
    ST functionSignatureList;
    ST classInitBodyList;
    ST functionInitBodyList;

    int dispatchCount = 0;

    @Override
    public ST visit(Id id) {
        return null;
    }

    @Override
    public ST visit(Int intt) {
        ST literal = templates.getInstanceOf("literal");
        literal.add("value", "int_const" + intt.getToken().getText());
        return literal;
    }

    @Override
    public ST visit(Block block) {
        ST seq = templates.getInstanceOf("sequence");
        for (Expression e: block.exps) {
            seq.add("e", e.accept(this));
        }

        return seq;
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
        ST literal = templates.getInstanceOf("literal");
        literal.add("value", "bool_const" + (bool.getToken().getText().equals("true")? "1": "0"));
        return literal;
    }

    @Override
    public ST visit(Stringg string) {
        ST literal = templates.getInstanceOf("literal");
        addConstString(string.token.getText());
        literal.add("value", "str_const" + constStringHt.get(string.token.getText()));
        return literal;
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
        ST dispatch = templates.getInstanceOf("dispatch");

        ///adaug parametri.
        ST addParamSeq = templates.getInstanceOf("sequence");
        for (int i = implicitDispatch.args.size() - 1; i >= 0; i--) {
            Expression e = implicitDispatch.args.get(i);
            addParamSeq.add("e", e.accept(this)); ///deocamdata merge pentru Intt, Stringg, Bool.
            addParamSeq.add("e", "sw $a0 0($sp)"); ///in $a0 vine pointer catre rezultat. il stochez in varful stivei.
            addParamSeq.add("e", "addiu $sp $sp -4");
        }

        dispatch.add("dispatchAddress", "$s0"); ///s-ar putea sa fi inteles prost.

        dispatch.add("funcParams", addParamSeq);
        dispatch.add("resetStackPointer", ((Integer)(4 * implicitDispatch.args.size())).toString());

        ///calcul offset.
        String className = ((ClassSymbol) implicitDispatch.id.getScope()).getName(); ///poate da null deref aici?
        String funcName = implicitDispatch.id.getToken().getText();

        int offset = classDispTabHt.get(className).get(funcName).second * 4;
        dispatch.add("offset", ((Integer) offset).toString());

        ///al catelea tag de dispatch e.
        dispatch.add("label", dispatchCount);
        dispatchCount++;

        ///(eroare) numele fisierului.
        String file = Compiler.fileNamesList.get(0); //new File(Compiler.fileNames.get(implicitDispatch.ctx)).getName();
        addConstString(file);
        dispatch.add("errFile", constStringHt.get(file));

        ///(eroare) linia din fisier.
        int line = implicitDispatch.token.getLine();
        dispatch.add("errLine", Integer.toString(line));

        return dispatch;
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

        ClassSymbol cs = (ClassSymbol) SymbolTable.globals.lookup(className);
        populateClassDispatchTable(cs);
        populateClassPrototypeObject(cs);
        computeClassInit(cs, classDef.features.stream().filter(f -> f instanceof Attribute).collect(Collectors.toList()));

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
        classProtObjHt = new HashMap<>();

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
            computeClassInit(baseClassSymbol, List.of());
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

    ///populeaza atat:
    ///* prototype object-ul unei clase cu atribute gasite in ea si stramosii ei.
    ///* dispatch table-ul clasei cu metode ce le poate apela (din ea sau din stramosii ei).
    private void populateClassDispatchTable(ClassSymbol cs) {
        ST classDispTabListEntry = templates.getInstanceOf("sequence");

        String ogClassName = cs.getName(), className = cs.getName();

        classDispTabHt.put(ogClassName, new HashMap<>());
        while (cs != null) {
            for (Map.Entry<String, Symbol> entry: cs.getFunctionSymbols().entrySet()) {
                String funcName = entry.getKey();

                ///clasa -> (fname -> clasa din care e apelat.)
                if (!classDispTabHt.get(ogClassName).containsKey(funcName)) {
                    classDispTabHt.get(ogClassName).put(funcName, new CGHelp.Pair<>(cs, classDispTabHt.get(ogClassName).size()));
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

    private void populateClassPrototypeObject(ClassSymbol cs) {
        ST classProtObjEntryFeatures = templates.getInstanceOf("sequence");

        String ogClassName = cs.getName();

        classProtObjHt.put(ogClassName, new HashMap<>());
        while (cs != null) {
            for (Map.Entry<String, Symbol> entry: cs.getIdSymbols().entrySet()) {
                String idName = entry.getKey();
                String idType = ((IdSymbol) entry.getValue()).getType().getName();

                ///clasa -> (id -> indicele in lista). nu ar tb sa il pun pe self.
                if (!idName.equals("self") && !classProtObjHt.get(ogClassName).containsKey(idName)) {
                    String value = ""; ///value e un pointer.

                    switch (idType) {
                        case "Int" -> value = "int_const0";
                        case "String" -> value = "str_const0";
                        case "Bool" -> value = "bool_const0";
                        default -> value = "0"; ///void pointer?
                    }

                    classProtObjHt.get(ogClassName).put(idName, classProtObjHt.get(ogClassName).size());
                    classProtObjEntryFeatures.add("e", templates.getInstanceOf("featureEntry")
                            .add("value", value));
                }
            }

            Scope parent = cs.getParent();
            if (parent != null) {
                cs = (ClassSymbol) parent.lookupClass();
            } else {
                cs = null;
            }
        }

        ST classProtObjEntry = templates.getInstanceOf("classProtObjEntry");
        classProtObjEntry.add("className", ogClassName);
        classProtObjEntry.add("classTag", classObjTabHt.get(ogClassName).toString());
        classProtObjEntry.add("features", classProtObjEntryFeatures);
        classProtObjEntry.add("size", ((Integer) (3 + classProtObjHt.get(ogClassName).size())).toString());

        classProtObjList.add("e", classProtObjEntry);
    }

    private void computeClassInit(ClassSymbol cs, List<Feature> classAttributes) {
        ST functionPreamble = templates.getInstanceOf("functionPreamble");
        functionPreamble.add("funcName", cs.getName() + "_init");

        ST body = templates.getInstanceOf("sequence");

        Scope parent = cs.getParent();
        if (parent != null) {
            ClassSymbol parentSymbol = (ClassSymbol) parent.lookupClass();
            if (parentSymbol != null) {
                ///init catre parinte.
                body.add("e", "jal " + parentSymbol.getName() + "_init");
            }
        }

        for (Feature f: classAttributes) {
            Attribute a = (Attribute) f;

            String idName = a.id.getSymbol().getName();
            String idType = a.type.getToken().getText();
            ///a.init este expresia.

            if (!idName.equals("self") && a.init != null) {
                Integer offset = classProtObjHt.get(cs.getName()).get(idName);

                if (offset != null) {
                    offset = 4 * offset + 12;

                    String value = "0";
                    if (a.init instanceof Int) {
                        constIntSet.add(Integer.valueOf(a.init.getToken().getText()));
                        value = "int_const" + a.init.getToken().getText();
                    }  else if (a.init instanceof Stringg) {
                        addConstString(a.init.getToken().getText());
                        value = "str_const" + constStringHt.get(a.init.getToken().getText());
                    } else if (a.init instanceof Bool) {
                        value = "bool_const" + (a.init.getToken().getText().equals("true")? "1": "0");
                    }

                    ///TODO daca atributul e o instanta de alta clasa...

                    body.add("e", "la $a0 " + value);
                    body.add("e", "sw $a0 " + offset + "($s0)"); ///s0 + offset = unde e stocat atributul.
                }
            }
        }

        functionPreamble.add("body", body);

        classInitBodyList.add("e", functionPreamble);
    }
}
