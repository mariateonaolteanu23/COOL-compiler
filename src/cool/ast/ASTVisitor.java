package cool.ast;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(Int intt);
    T visit(Block block);
    T visit(Local local);
    T visit(CaseBranch caseBranch);
    T visit(If iff);
    T visit(While whilee);
    T visit(Let let);
    T visit(Case casee);
    T visit(Bool bool);
    T visit(Stringg string);
    T visit(New neww);
    T visit(BitComplement bitComplement);
    T visit(Not not);
    T visit(Isvoid isvoid);
    T visit(Assign assign);
    T visit(Relational rel);
    T visit(Plus plus);
    T visit(Minus minus);
    T visit(Mult mult);
    T visit(Div div);
    T visit(UnaryArithmetic unaryArithmetic);
    T visit(Type type);
    T visit(Formal formal);
    T visit(Attribute attribute);
    T visit(ImplicitDispatch implicitDispatch);
    T visit(ExplicitDispatch explicitDispatch);
    T visit(FuncDef funcDef);
    T visit(ClassDef classDef);
    T visit(Program program);
}

