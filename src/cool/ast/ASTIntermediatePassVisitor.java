package cool.ast;


import cool.parser.CoolParser;
import cool.structures.*;


public class ASTIntermediatePassVisitor implements ASTVisitor<Void>{
    @Override
    public Void visit(Id id) {
        return null;
    }

    @Override
    public Void visit(Int intt) {
        return null;
    }

    @Override
    public Void visit(Block block) {
        block.exps.forEach(e -> e.accept(this));
        return null;
    }

    @Override
    public Void visit(Local local) {
        var id = local.id.getToken().getText();
        var type = local.type.getToken().getText();
        var scope = local.id.getScope();

        if (scope == null)
            return null;

        var symbol = scope.lookup(type);

        if (symbol == null) {
            SymbolTable.error(local.ctx, ((CoolParser.LocalDefContext)local.ctx).type,
                    "Let variable " + id + " has undefined type " + type);
        }

        local.id.getSymbol().setType((ClassSymbol) symbol);

        if (local.init != null)
            local.init.accept(this);

        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        var id = caseBranch.id.getToken().getText();
        var type = caseBranch.type.getToken().getText();

        if (type.equals("SELF_TYPE")) {
            SymbolTable.error(caseBranch.ctx, ((CoolParser.CaseBranchDefContext)caseBranch.ctx).type,
                    "Case variable " + id + " has illegal type SELF_TYPE");
            return null;
        }

        var scope = caseBranch.id.getScope();

        if (scope == null)
            return null;

        var symbol = scope.lookup(type);

        if (symbol == null) {
            SymbolTable.error(caseBranch.ctx, ((CoolParser.CaseBranchDefContext)caseBranch.ctx).type,
                    "Case variable " + id + " has undefined type " + type);
        }

        caseBranch.id.getSymbol().setType((ClassSymbol) symbol);

        if (caseBranch.exp != null)
            caseBranch.exp.accept(this);
        return null;
    }

    @Override
    public Void visit(If iff) {
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visit(While whilee) {
        whilee.cond.accept(this);
        whilee.body.accept(this);
        return null;
    }

    @Override
    public Void visit(Let let) {
        let.locals.forEach(l -> l.accept(this));
        let.body.accept(this);
        return null;
    }

    @Override
    public Void visit(Case casee) {
        casee.branches.forEach(c -> c.accept(this));
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        return null;
    }

    @Override
    public Void visit(Stringg string) {
        return null;
    }

    @Override
    public Void visit(New neww) {
        var type = neww.type.getToken().getText();
        var scope = neww.getScope();

        if (scope == null)
            return null;

        var symbol = scope.lookup(type);

        if (symbol == null) {
            SymbolTable.error(neww.ctx, ((CoolParser.NewContext)neww.ctx).type,
                    "new is used with undefined type " + type);
            return null;
        }

        return null;
    }

    @Override
    public Void visit(BitComplement bitComplement) {
        return null;
    }

    @Override
    public Void visit(Not not) {
        not.exp.accept(this);
        return null;
    }

    @Override
    public Void visit(Isvoid isvoid) {
        isvoid.exp.accept(this);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        assign.id.accept(this);
        assign.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(Relational rel) {
        return null;
    }

    @Override
    public Void visit(Plus plus) {
        return null;
    }

    @Override
    public Void visit(Minus minus) {
        return null;
    }

    @Override
    public Void visit(Mult mult) {
        return null;
    }

    @Override
    public Void visit(Div div) {
        return null;
    }

    @Override
    public Void visit(UnaryArithmetic unaryArithmetic) {
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        var id = formal.id.getToken().getText();
        var type = formal.type.getToken().getText();
        var scope = formal.id.getScope();

        if (scope == null)
            return null;

        var symbol = scope.getParent().lookup(type);

        if (symbol == null) {
            SymbolTable.error(formal.ctx, ((CoolParser.FormalDefContext)formal.ctx).type,
                    "Method " + ((FunctionSymbol)scope).getName() +
                            " of class " + ((ClassSymbol)scope.getParent()).getName() +
                            " has formal parameter " + id + " with undefined type " + type);
        }

        formal.id.getSymbol().setType((ClassSymbol) symbol);
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        var id = attribute.id.getToken().getText();
        var type = attribute.type.getToken().getText();
        var scope = attribute.id.getScope();

        if (scope == null)
            return null;

        var symbol = scope.lookup(type);
        if (symbol == null) {
            SymbolTable.error(attribute.ctx, ((CoolParser.AttributeContext)attribute.ctx).type,
                    "Class " + ((ClassSymbol)scope).getName() + " has attribute " + id + " with undefined type " + type);
            return null;
        }

        attribute.id.getSymbol().setType((ClassSymbol) symbol);

        return null;
    }

    @Override
    public Void visit(ImplicitDispatch implicitDispatch) {
        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        var id = funcDef.id.getToken().getText();
        var type = funcDef.type.getToken().getText();
        var scope = funcDef.id.getScope();

        funcDef.formals.forEach(f -> f.accept(this));
        funcDef.body.accept(this);

        if (scope == null)
            return null;

        var symbol = scope.lookup(type);

        if (symbol == null) {
            SymbolTable.error(funcDef.ctx, ((CoolParser.FuncDefContext)funcDef.ctx).type,
                    "Class " + ((ClassSymbol)scope).getName() +
                            " has method " + id + " with undefined return type " + type);
            return null;
        }

        funcDef.id.getSymbol().setType((ClassSymbol) symbol);

        return null;
    }

    @Override
    public Void visit(ClassDef classDef) {

        var id = classDef.id.getToken().getText();

        if (classDef.parent != null) {
            var parent = classDef.parent.getToken().getText();

            if (SymbolTable.invalidParents.stream().anyMatch(p -> parent.equals(p.getName()))) {
                SymbolTable.error(classDef.ctx,
                        ((CoolParser.ClassDefContext)classDef.ctx).parent,
                        "Class " + id + " has illegal parent " + parent);
                return null;
            }

            var parentSymbol = classDef.getScope().lookup(parent);

            if (parentSymbol == null) {
                SymbolTable.error(classDef.ctx,
                        ((CoolParser.ClassDefContext)classDef.ctx).parent,
                        "Class " + id + " has undefined parent " + parent);
                return null;
            }

            var scope = (ClassSymbol)(classDef.getScope().lookup(id));
            scope.setParent((ClassSymbol)parentSymbol);
        }

        classDef.features.forEach(feature -> feature.accept(this));
        return null;
    }

    @Override
    public Void visit(Program program) {
        program.stmts.forEach(stmt -> stmt.accept(this));
        return null;
    }
}
