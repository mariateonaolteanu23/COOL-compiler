package cool.ast;
import cool.parser.CoolParser;
import cool.structures.*;

import java.util.ArrayList;
import java.util.List;

public class ASTDefinitionPassVisitor implements ASTVisitor<Void> {

    public Scope currentScope = SymbolTable.globals;

    @Override
    public Void visit(Id id) {
        id.setScope(currentScope);
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

        if (id.equals("self")) {
            SymbolTable.error(local.ctx, local.token,
                    "Let variable has illegal name self");
            return null;
        }

        var idSymbol = new IdSymbol(id);
        var parent = currentScope.getParent();
        currentScope.add(idSymbol);
        local.id.setSymbol(idSymbol);
        local.id.setScope(currentScope);

        if (local.init != null) {
            var tmp = currentScope;
            currentScope = parent;
            local.init.accept(this);
            currentScope = tmp;
        }

        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        var id = caseBranch.id.getToken().getText();

        if (id.equals("self")) {
            SymbolTable.error(caseBranch.ctx, caseBranch.token,
                    "Case variable has illegal name self");
            return null;
        }

        var idSymbol = new IdSymbol(id);
        currentScope.add(idSymbol);

        caseBranch.id.setSymbol(idSymbol);
        caseBranch.id.setScope(currentScope.getParent());

        if (caseBranch.exp != null)
            caseBranch.exp.accept(this);

        return null;
    }

    @Override
    public Void visit(If iff) {
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        iff.setScope(currentScope);
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

        var tmp = currentScope;

        for (Local local : let.locals) {
            var scope = new DefaultScope(currentScope);
            currentScope = scope;
            local.accept(this);
        }

        let.body.accept(this);

        currentScope = tmp;
        return null;
    }

    @Override
    public Void visit(Case casee) {
        for (CaseBranch branch : casee.branches) {
            var scope = new DefaultScope(currentScope);
            currentScope = scope;
            branch.accept(this);
            currentScope = scope.getParent();
        }
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
        neww.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(BitComplement bitComplement) {
        bitComplement.exp.accept(this);
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
        var id = assign.id.getToken().getText();

        if (id.equals("self")) {
            SymbolTable.error(assign.ctx, assign.ctx.start,
                    "Cannot assign to self");
            return null;
        }

        assign.id.accept(this);
        assign.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(Relational rel) {
        rel.right.accept(this);
        rel.left.accept(this);
        return null;
    }

    @Override
    public Void visit(Plus plus) {
        plus.left.accept(this);
        plus.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Minus minus) {
        minus.left.accept(this);
        minus.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Mult mult) {
        mult.left.accept(this);
        mult.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Div div) {
        div.left.accept(this);
        div.right.accept(this);
        return null;
    }

    @Override
    public Void visit(UnaryArithmetic unaryArithmetic) {
        unaryArithmetic.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        var id = formal.id.getToken().getText();

        if (id.equals("self")) {
            SymbolTable.error(formal.ctx, formal.token,
                    "Method " + ((FunctionSymbol)currentScope).getName() +
                            " of class " + ((ClassSymbol)currentScope.getParent()).getName() + " has formal parameter with illegal name self");
            return null;
        }

        var idSymbol = new IdSymbol(id);

        if (!currentScope.add(idSymbol)) {
            SymbolTable.error(formal.ctx, formal.token,
                    "Method " + ((FunctionSymbol)currentScope).getName() +
                            " of class " + ((ClassSymbol)currentScope.getParent()).getName() + " redefines formal parameter " + id);
            return null;
        }

        if (formal.type.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(formal.ctx, ((CoolParser.FormalDefContext)formal.ctx).type,
                    "Method " + ((FunctionSymbol)currentScope).getName() +
                            " of class " + ((ClassSymbol)currentScope.getParent()).getName() +
                            " has formal parameter " + id + " with illegal type SELF_TYPE");
            return null;
        }

        formal.id.setSymbol(idSymbol);
        formal.id.setScope(currentScope);

        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        var id = attribute.id.getToken().getText();

        if (id.equals("self")) {
            SymbolTable.error(attribute.ctx, attribute.token,
                    "Class " + ((ClassSymbol)currentScope).getName() + " has attribute with illegal name self");
            return null;
        }

        var symbol = new IdSymbol(id);

        if (!currentScope.add(symbol)) {
            SymbolTable.error(attribute.ctx, attribute.token,
                    "Class " + ((ClassSymbol)currentScope).getName() + " redefines attribute " + id);
            return null;
        }

        attribute.id.setSymbol(symbol);

        attribute.id.setScope(currentScope);


        if (attribute.init != null) {
            attribute.init.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ImplicitDispatch implicitDispatch) {
        implicitDispatch.args.forEach(a -> a.accept(this));
        implicitDispatch.id.setScope((Scope) currentScope.lookupClass());
        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        explicitDispatch.exp.accept(this);
        explicitDispatch.args.forEach(a -> a.accept(this));
        explicitDispatch.id.setScope((Scope) currentScope.lookupClass());
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        var id = funcDef.id.getToken().getText();

        var functionSymbol = new FunctionSymbol(id, currentScope);

        if (!currentScope.add(functionSymbol)) {
            SymbolTable.error(funcDef.ctx, funcDef.token,
                    "Class " + ((ClassSymbol)currentScope).getName() + " redefines method " + id);
            return null;
        }

        funcDef.id.setSymbol(functionSymbol);
        funcDef.id.setScope(currentScope);

        currentScope = functionSymbol;

        if (funcDef.formals != null) {
            funcDef.formals.forEach(f -> f.accept(this));
        }

        funcDef.body.accept(this);
        currentScope = functionSymbol.getParent();

        return null;
    }

    @Override
    public Void visit(ClassDef classDef) {
        var id = classDef.id.getToken().getText();

        if (id.equals("SELF_TYPE")) {
            SymbolTable.error(classDef.ctx, classDef.token, "Class has illegal name SELF_TYPE");
            return null;
        }

        var classSymbol = new ClassSymbol(id, ClassSymbol.OBJECT);

        if (!currentScope.add(classSymbol)) {
            SymbolTable.error(classDef.ctx, classDef.token, "Class " + id + " is redefined");
            return null;
        }

        classDef.setScope(currentScope);
        var tmp = currentScope;
        currentScope = classSymbol;
        var self = new IdSymbol("self");
        self.setType(ClassSymbol.SELF_TYPE);
        currentScope.add(self);
        classDef.features.forEach(f -> f.accept(this));
        currentScope = tmp;

        return null;
    }

    @Override
    public Void visit(Program program) {
        program.stmts.forEach(stmt -> stmt.accept(this));
        return null;
    }
}
