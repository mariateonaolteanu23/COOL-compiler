package cool.ast;

public class ASTPrintVisitor implements ASTVisitor<Void> {
    private int indent = 0;

    private void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
        System.out.println(str);
    }

    @Override
    public Void visit(Stringg id) {
        printIndent(id.token.getText());
        return null;
    }

    @Override
    public Void visit(New neww) {
        printIndent(neww.token.getText());
        indent++;
        neww.type.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Isvoid isvoid) {
        printIndent(isvoid.token.getText());
        indent++;
        isvoid.exp.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Not not) {
        printIndent(not.token.getText());
        indent++;
        not.exp.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(BitComplement bitComplement) {
        printIndent(bitComplement.token.getText());
        indent++;
        bitComplement.exp.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Id id) {
        printIndent(id.token.getText());
        return null;
    }

    @Override
    public Void visit(Int intt) {
        printIndent(intt.token.getText());
        return null;
    }

    @Override
    public Void visit(Block block) {
        printIndent("block");
        indent++;
        block.exps.forEach(e -> e.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(Local local) {
        printIndent("local");
        indent++;
        local.id.accept(this);
        local.type.accept(this);
        if (local.init != null)
            local.init.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        printIndent("case branch");
        indent++;
        caseBranch.id.accept(this);
        caseBranch.type.accept(this);
        caseBranch.exp.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(If iff) {
        printIndent(iff.token.getText());
        indent++;
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(While whilee) {
        printIndent(whilee.token.getText());
        indent++;
        whilee.cond.accept(this);
        whilee.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Let let) {
        printIndent(let.getToken().getText());
        indent++;
        let.locals.forEach(l -> l.accept(this));
        let.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Case casee) {
        printIndent(casee.getToken().getText()); // case
        indent++;
        casee.exp.accept(this);
        casee.branches.forEach(b -> b.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        printIndent(bool.token.getText());
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        printIndent(assign.token.getText());
        indent++;
        assign.id.accept(this);
        assign.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Relational rel) {
        printIndent(rel.token.getText());
        indent++;
        rel.left.accept(this);
        rel.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Plus plus) {
        printIndent(plus.token.getText());
        indent++;
        plus.left.accept(this);
        plus.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Minus minus) {
        printIndent(minus.token.getText());
        indent++;
        minus.left.accept(this);
        minus.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Mult mult) {
        printIndent(mult.token.getText());
        indent++;
        mult.left.accept(this);
        mult.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Div div) {
        printIndent(div.token.getText());
        indent++;
        div.left.accept(this);
        div.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(UnaryArithmetic unaryArithmetic) {
        return null;
    }

    @Override
    public Void visit(Type type) {
        printIndent(type.token.getText());
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent("formal");
        indent++;
        formal.id.accept(this);
        formal.type.accept(this);
        indent--;
        return null;
    }


    @Override
    public Void visit(Attribute attribute) {
        printIndent("attribute");
        indent++;
        attribute.id.accept(this);
        attribute.type.accept(this);
        if (attribute.init != null)
            attribute.init.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(ImplicitDispatch implicitDispatch) {
        printIndent("implicit dispatch");
        indent++;
        implicitDispatch.id.accept(this);
        implicitDispatch.args.forEach(a -> a.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        printIndent(explicitDispatch.token.getText());
        indent++;
        explicitDispatch.exp.accept(this);
        if (explicitDispatch.type != null)
            explicitDispatch.type.accept(this);
        explicitDispatch.id.accept(this);
        explicitDispatch.args.forEach(a -> a.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        printIndent("method");
        indent++;
        funcDef.id.accept(this);
        funcDef.formals.forEach(f -> f.accept(this));
        funcDef.type.accept(this);
        funcDef.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(ClassDef classDef) {
        printIndent("class");
        indent++;
        classDef.id.accept(this);
        if (classDef.parent != null)
            classDef.parent.accept(this);
        if (classDef.features != null)
            classDef.features.forEach(f -> f.accept(this));
        indent--;
        return null;
    }

    @Override
    public Void visit(Program program) {
        printIndent("program");
        indent++;
        program.stmts.forEach(s -> s.accept(this));
        indent--;
        return null;
    }
}
