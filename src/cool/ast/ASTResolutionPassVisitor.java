package cool.ast;
import cool.parser.CoolParser;
import cool.structures.*;
import java.util.Map;

public class ASTResolutionPassVisitor implements ASTVisitor<ClassSymbol> {
    @Override
    public ClassSymbol visit(Id id) {
        var scope = id.getScope();

        if (scope == null)
            return null;


        id.setScope(getActualScopeForId(scope, id.getToken().getText()));

        var symbol = scope.lookupId(id.getToken().getText());

        if (symbol == null) {
            SymbolTable.error(id.ctx, id.ctx.start, "Undefined identifier " + id.getToken().getText());
            return null;
        }

        return ((IdSymbol)symbol).getType();
    }

    @Override
    public ClassSymbol visit(Int intt) {
        return ClassSymbol.INT;
    }

    @Override
    public ClassSymbol visit(Block block) {
        for (int i = 0; i < block.exps.size() - 1; ++i)
            block.exps.get(i).accept(this);

        return block.exps.get(block.exps.size() - 1).accept(this);
    }

    @Override
    public ClassSymbol visit(Local local) {
        if (local.id.getSymbol() == null)
            return null;

        var id =  local.id.getSymbol().getType();

        if (local.init != null) {
            var init = local.init.accept(this);

            if (id == null || init == null)
                return null;

            var common = id.leastUpperBound(init);

            if (!id.getName().equals(common.getName())) {
                SymbolTable.error(local.ctx, ((CoolParser.LocalDefContext)local.ctx).init.start,
                        "Type " + init.getName() +
                                " of initialization expression of identifier " + local.id.getToken().getText() +
                                " is incompatible with declared type " + id.getName());

                return null;
            }
        }

        return null;
    }

    @Override
    public ClassSymbol visit(CaseBranch caseBranch) {
        return caseBranch.exp.accept(this);
    }

    @Override
    public ClassSymbol visit(If iff) {
        var cond = iff.cond.accept(this);

        if (cond != null && !cond.getName().equals(ClassSymbol.BOOL.getName())) {
            SymbolTable.error(iff.ctx,
                    ((CoolParser.IfContext)iff.ctx).cond.start,
                    "If condition has type " +
                            cond.getName() + " instead of Bool");
        }

        var thenBranch = iff.thenBranch.accept(this);
        var elseBranch = iff.elseBranch.accept(this);

        if (thenBranch == null || elseBranch == null)
            return null;

        var scope = iff.getScope();

        if (scope != null && !thenBranch.getName().equals(elseBranch.getName())) {
            return thenBranch.getFinalType(scope).leastUpperBound(elseBranch.getFinalType(scope));
        }

        return thenBranch.leastUpperBound(elseBranch);
    }

    @Override
    public ClassSymbol visit(While whilee) {
        var cond = whilee.cond.accept(this);

        if (cond != null && !cond.getName().equals(ClassSymbol.BOOL.getName())) {
            SymbolTable.error(whilee.ctx,
                    ((CoolParser.WhileContext)whilee.ctx).cond.start,
                    "While condition has type " +
                            cond.getName() + " instead of Bool");
        }

        whilee.body.accept(this);

        return ClassSymbol.OBJECT;
    }

    @Override
    public ClassSymbol visit(Let let) {
        for (Local l : let.locals) {
            l.accept(this);
        }

        return let.body.accept(this);
    }

    @Override
    public ClassSymbol visit(Case casee) {
        var index = 0;
        var size = casee.branches.size();
        var type = casee.branches.get(index).accept(this);

        while (type == null && index < size - 1) {
            index++;
            type = casee.branches.get(index).accept(this);
        }

        if (type == null)
            return null;

        for (int i = index; i < size; ++i) {
            var exp = casee.branches.get(i).accept(this);

            if (exp != null)
                type = type.leastUpperBound(exp);
        }

        return type;
    }

    @Override
    public ClassSymbol visit(Bool bool) {
        return ClassSymbol.BOOL;
    }

    @Override
    public ClassSymbol visit(Stringg string) {
        return ClassSymbol.STRING;
    }

    @Override
    public ClassSymbol visit(New neww) {
        var scope = neww.getScope();

        if (scope == null)
            return null;

        return (ClassSymbol) scope.lookup(neww.getToken().getText());
    }

    @Override
    public ClassSymbol visit(BitComplement bitComplement) {
        var type = bitComplement.exp.accept(this);

        if (type == null)
            return null;

        if (!type.getName().equals(ClassSymbol.BOOL.getName())) {
            SymbolTable.error(bitComplement.ctx,
                    ((CoolParser.BitComplementContext)bitComplement.ctx).e.start, "Operand of " +
                    bitComplement.token.getText() +
                    " has type " + type.getName() + " instead of Int");
            return null;
        }

        return ClassSymbol.BOOL;

    }

    @Override
    public ClassSymbol visit(Not not) {
        var type = not.exp.accept(this);

        if (type == null)
            return null;

        if (!type.getName().equals(ClassSymbol.BOOL.getName())) {
            SymbolTable.error(not.ctx,
                    ((CoolParser.NotContext)not.ctx).e.start,
                    "Operand of not has type " + type.getName() + " instead of Bool");
            return null;
        }
        return ClassSymbol.BOOL;
    }

    @Override
    public ClassSymbol visit(Isvoid isvoid) {
        return ClassSymbol.BOOL;
    }

    private void printAssignSemanticError(Assign assign, ClassSymbol varType, ClassSymbol expType) {
        SymbolTable.error(assign.ctx, ((CoolParser.AssignContext)assign.ctx).e.start,
                "Type " + expType.getName() +
                        " of assigned expression is incompatible with declared type " +
                        varType.getName() + " of identifier " + assign.id.getToken().getText());
    }

    @Override
    public ClassSymbol visit(Assign assign) {
        var varType = assign.id.accept(this);
        var expType = assign.expr.accept(this);

        if (varType == null || expType == null)
            return null;

        if (varType.getName().equals(ClassSymbol.SELF_TYPE.getName()) &&
                !expType.getName().equals(ClassSymbol.SELF_TYPE.getName())) {
            printAssignSemanticError(assign, varType, expType);
            return null;
        }
        var type = varType.getFinalType(assign.id.getScope()).
                leastUpperBound(expType.getFinalType(assign.id.getScope()));

        if (!type.getName().equals(varType.getFinalType(assign.id.getScope()).getName())) {
            printAssignSemanticError(assign, varType, expType);
            return null;
        }

        return expType;
    }

    @Override
    public ClassSymbol visit(Relational rel) {
        var left = rel.left.accept(this);
        var right = rel.right.accept(this);

        if (rel.getToken().getText().equals("=")) {
            if (left != null && right != null) {
                if (!left.getName().equals(right.getName()))
                    if (SymbolTable.basicTypes.contains(left.getName()) ||
                            SymbolTable.basicTypes.contains(right.getName())) {
                        SymbolTable.error(rel.ctx, rel.token,
                                "Cannot compare " + left.getName() + " with " + right.getName());
                        return null;
                    }
            }
            return ClassSymbol.BOOL;
        }

        if (left != null) {
            if (!left.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(rel.ctx, ((CoolParser.RelationalContext)rel.ctx).left.start,
                        "Operand of " + rel.token.getText()  +
                        " has type " + left.getName() + " instead of Int");
                return null;
            }
        }

        if (right != null) {
            if (!right.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(rel.ctx, ((CoolParser.RelationalContext)rel.ctx).right.start,
                        "Operand of " + rel.token.getText() +
                        " has type " + right.getName() + " instead of Int");
                return null;
            }
        }


        if (right != null && left != null)
            return ClassSymbol.BOOL;

        return null;
    }

    @Override
    public ClassSymbol visit(Plus plus) {
        var left = plus.left.accept(this);
        var right = plus.right.accept(this);

        if (left != null) {
            if (!left.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(plus.ctx, ((CoolParser.PlusMinusContext)plus.ctx).left.start,
                        "Operand of " + plus.token.getText()  +
                        " has type " + left.getName() + " instead of Int");
                return null;
            }
        }

        if (right != null) {
            if (!right.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(plus.ctx, ((CoolParser.PlusMinusContext)plus.ctx).right.start,
                        "Operand of " + plus.token.getText() +
                        " has type " + right.getName() + " instead of Int");
                return null;
            }
        }


        if (right != null && left != null)
            return ClassSymbol.INT;

        return null;
    }

    @Override
    public ClassSymbol visit(Minus minus) {
        var left = minus.left.accept(this);
        var right = minus.right.accept(this);

        if (left != null) {
            if (!left.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(minus.ctx, ((CoolParser.PlusMinusContext)minus.ctx).left.start,
                        "Operand of " + minus.token.getText() +
                        " has type " + left.getName() + " instead of Int");
                return null;
            }
        }
        if (right != null) {

            if (!right.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(minus.ctx, ((CoolParser.PlusMinusContext)minus.ctx).right.start,
                        "Operand of " + minus.token.getText() +
                        " has type " + right.getName() + " instead of Int");
                return null;
            }
        }


        if (right != null && left != null)
            return ClassSymbol.INT;

        return null;
    }

    @Override
    public ClassSymbol visit(Mult mult) {
        var left = mult.left.accept(this);
        var right = mult.right.accept(this);

        if (left != null) {
            if (!left.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(mult.ctx, ((CoolParser.MultDivContext)mult.ctx).left.start,
                        "Operand of " + mult.token.getText() +
                        " has type " + left.getName() + " instead of Int");
                return null;
            }
        }

        if (right != null) {
            if (!right.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(mult.ctx, ((CoolParser.MultDivContext)mult.ctx).right.start,
                        "Operand of " + mult.token.getText() +
                        " has type " + right.getName() + " instead of Int");
                return null;
            }
        }


        if (right != null && left != null)
            return ClassSymbol.INT;

        return null;
    }

    @Override
    public ClassSymbol visit(Div div) {
        var left = div.left.accept(this);
        var right = div.right.accept(this);

        if (left != null) {
            if (!left.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(div.ctx, ((CoolParser.MultDivContext)div.ctx).left.start,
                        "Operand of " + div.token.getText() +
                        " has type " + left.getName() + " instead of Int");
                return null;
            }
        }

        if (right != null) {
            if (!right.getName().equals(ClassSymbol.INT.getName())) {
                SymbolTable.error(div.ctx, ((CoolParser.MultDivContext)div.ctx).right.start,
                        "Operand of " + div.token.getText() +
                        " has type " + right.getName() + " instead of Int");
                return null;
            }
        }

        if (right != null && left != null)
            return ClassSymbol.INT;

        return null;
    }

    @Override
    public ClassSymbol visit(UnaryArithmetic unaryArithmetic) {
        unaryArithmetic.expr.accept(this);
        return null;
    }

    @Override
    public ClassSymbol visit(Type type) {
        return null;
    }

    @Override
    public ClassSymbol visit(Formal formal) {
        return null;
    }

    @Override
    public ClassSymbol visit(Attribute attribute) {
        var id = attribute.id.getToken().getText();
        var scope = attribute.id.getScope();

        if (scope == null)
            return null;

        var redefined = scope.getParent().lookupId(id);

        if (redefined != null) {
            SymbolTable.error(attribute.ctx, attribute.token,
                    "Class " + ((ClassSymbol)scope).getName() + " redefines inherited attribute " + id);
            return null;
        }

        if (attribute.id.getSymbol() == null || attribute.init == null)
            return null;

        var type = attribute.id.getSymbol().getType();
        var init = attribute.init.accept(this);

        if (type == null || init == null)
            return null;

        var common = type.getFinalType(scope).leastUpperBound(init.getFinalType(scope));

        if (!type.getFinalType(scope).getName().equals(common.getName())) {
            SymbolTable.error(attribute.ctx, ((CoolParser.AttributeContext)attribute.ctx).init.start,
                    "Type " + init.getName() +
                            " of initialization expression of attribute " + id +
                            " is incompatible with declared type " + type.getName());

            return null;
        }

        return null;
    }

    @Override
    public ClassSymbol visit(ImplicitDispatch implicitDispatch) {
        var id = implicitDispatch.id.getToken().getText();
        var scope = implicitDispatch.id.getScope();

        if (scope == null)
            return null;


        var func = scope.lookupFunction(id);

        if  (func == null) {
            SymbolTable.error(implicitDispatch.ctx,
                    implicitDispatch.ctx.start,
                    "Undefined method " + id + " in class " +
                            ((ClassSymbol)scope).getName());
            return null;
        }

        var formals = ((FunctionSymbol)func).getLocalSymbols();

        if (formals.size() != implicitDispatch.args.size()) {
            SymbolTable.error(implicitDispatch.ctx,
                    ((CoolParser.ImplicitDispatchContext)implicitDispatch.ctx).name,
                    "Method " + id + " of class " +
                            ((ClassSymbol)scope).getName() +
                            " is applied to wrong number of arguments");
            return null;
        }

        var index = 0;
        for (Map.Entry<String, Symbol> entry : formals.entrySet()) {
            var type = implicitDispatch.args.get(index).accept(this);
            var formal = entry.getValue();

            if (formal != null) {
                var formalType = ((IdSymbol)formal).getType();

                if (formalType == null || type == null) {
                    return null;
                }

                var common = formalType.leastUpperBound(type);
                if (!formalType.getName().equals(common.getName())) {
                    SymbolTable.error(implicitDispatch.ctx,
                            ((CoolParser.ImplicitDispatchContext)implicitDispatch.ctx).args.get(index).start,
                            "In call to method " + id + " of class " +
                                    ((ClassSymbol)scope).getName() + ", actual type " + type +
                            " of formal parameter " + entry.getKey() + " is incompatible with declared type " +
                            formalType);

                    break;
                }
            }
            index++;
        }

        var type =  ((FunctionSymbol) func).getType();
        if (type != null && type.getName().equals("SELF_TYPE")) {
            return type.getFinalType((Scope) ((FunctionSymbol) func).lookupClass());
        }

        return ((FunctionSymbol) func).getType();
    }

    @Override
    public ClassSymbol visit(ExplicitDispatch explicitDispatch) {

        ///exp.id()
        var id = explicitDispatch.id.getToken().getText();
        var scope = explicitDispatch.id.getScope();
        var exp = explicitDispatch.exp.accept(this);

        if (scope == null || exp == null)
            return null;


        ClassSymbol symbol = exp.getFinalType(scope);

        explicitDispatch.setCallerType(symbol);


        if (explicitDispatch.type != null) {
            var staticDispatch = explicitDispatch.type.getToken().getText();

            if (staticDispatch.equals("SELF_TYPE")) {
                SymbolTable.error(explicitDispatch.ctx,
                        ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).type,
                        "Type of static dispatch cannot be SELF_TYPE");
                return null;
            }

            symbol = (ClassSymbol)scope.lookup(staticDispatch);

            if (symbol == null) {
                SymbolTable.error(explicitDispatch.ctx,
                        ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).type,
                        "Type " + staticDispatch + " of static dispatch is undefined");
                return null;
            }

            var common = exp.getFinalType(scope).leastUpperBound(symbol);

            // not a superclass of exp type
            if (!symbol.getName().equals(common.getName())) {

                SymbolTable.error(explicitDispatch.ctx,
                        ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).type,
                        "Type " + staticDispatch + " of static dispatch is not a superclass of type " +
                                exp);
                return null;
            }
        }

        var func = symbol.lookupFunction(id);

        if (func == null) {
            SymbolTable.error(explicitDispatch.ctx,
                    ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).name,
                    "Undefined method " + id + " in class " +
                            symbol.getName());
            return null;
        }

        var formals = ((FunctionSymbol)func).getLocalSymbols();

        if (formals.size() != explicitDispatch.args.size()) {
            SymbolTable.error(explicitDispatch.ctx,
                    ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).name,
                    "Method " + id + " of class " +
                            symbol.getName() +
                            " is applied to wrong number of arguments");
            return null;
        }

        var index = 0;
        for (Map.Entry<String, Symbol> entry : formals.entrySet()) {
            var type = explicitDispatch.args.get(index).accept(this);
            var formal = entry.getValue();

            if (formal != null) {
                var formalType = ((IdSymbol)formal).getType();

                if (formalType == null || type == null) {
                    return null;
                }

                var common = formalType.leastUpperBound(type);
                if (!formalType.getName().equals(common.getName())) {
                    SymbolTable.error(explicitDispatch.ctx,
                            ((CoolParser.ExplicitDispatchContext)explicitDispatch.ctx).args.get(index).start,
                            "In call to method " + id + " of class " +
                                    symbol.getName() + ", actual type " + type +
                                    " of formal parameter " + entry.getKey() + " is incompatible with declared type " +
                                    formalType);

                    break;
                }
            }
            index++;
        }

        var type = ((FunctionSymbol) func).getType();

        if (type.getName().equals(ClassSymbol.SELF_TYPE.getName())) {
            return exp;
        }

        return type;
    }

    private void printFuncDefSemanticError(FuncDef funcDef, String id, ClassSymbol body, ClassSymbol ret) {
        SymbolTable.error(funcDef.ctx, ((CoolParser.FuncDefContext)funcDef.ctx).body.start,
                "Type " + body.getName() + " of the body of method " + id +
                        " is incompatible with declared return type " + ret.getName());
    }

    @Override
    public ClassSymbol visit(FuncDef funcDef) {
        var id = funcDef.id.getToken().getText();
        var scope = funcDef.id.getScope();

        if (scope == null)
            return null;

        var redefined = scope.getParent().lookupFunction(id);

        if (redefined != null) {

            var formals = ((FunctionSymbol) redefined).getLocalSymbols();

            if (formals.size() != funcDef.formals.size()) {
                SymbolTable.error(funcDef.ctx, funcDef.token,
                        "Class " + ((ClassSymbol) scope).getName() + " overrides method "
                                + id + " with different number of formal parameters");
                return null;
            }

            var it = 0;
            for (Map.Entry<String, Symbol> entry : formals.entrySet()) {
                var fromType = ((IdSymbol) entry.getValue()).getType().getName();
                var toType = funcDef.formals.get(it).type.getToken().getText();
                if (!fromType.equals(toType)) {
                    SymbolTable.error(funcDef.ctx, ((CoolParser.FormalDefContext)funcDef.formals.get(it).ctx).type,
                            "Class " + ((ClassSymbol) scope).getName() + " overrides method " + id +
                                    " but changes type of formal parameter " +
                                    funcDef.formals.get(it).getToken().getText() +
                                    " from " + fromType + " to " + toType);
                    return null;
                }
                it++;
            }

            var fromReturnType = ((FunctionSymbol) redefined).getType();
            var toReturnType = funcDef.id.getSymbol().getType();

            if (fromReturnType != null && !fromReturnType.equals(toReturnType)) {
                SymbolTable.error(funcDef.ctx, ((CoolParser.FuncDefContext)funcDef.ctx).type,
                        "Class " + ((ClassSymbol) scope).getName() + " overrides method " + id +
                                " but changes return type from " +
                                fromReturnType + " to " + toReturnType);
                return null;
            }
        }

        var ret = funcDef.id.getSymbol().getType();
        var body = funcDef.body.accept(this);

        if (body == null || ret == null)
            return null;

        if (ret.getName().equals(ClassSymbol.SELF_TYPE.getName()) &&
                !body.getName().equals(ClassSymbol.SELF_TYPE.getName())) {
            printFuncDefSemanticError(funcDef, id, body, ret);
            return null;
        }

        var common = body.getFinalType(scope).leastUpperBound(ret.getFinalType(scope));

        if (!common.getName().equals(ret.getFinalType(scope).getName())) {
            printFuncDefSemanticError(funcDef, id, body, ret);
            return null;
        }

        return null;
    }

    @Override
    public ClassSymbol visit(ClassDef classDef) {
        var id = classDef.id.getToken().getText();

        if (classDef.parent != null) {
            var scope = classDef.getScope();

            if (scope == null)
                return null;
            var current = (ClassSymbol) scope.lookup(id);
            var parent = current.getParent();

            if (parent instanceof ClassSymbol) {
                while (parent != null && !((ClassSymbol) parent).getName().equals("Object")) {
                    if (((ClassSymbol) parent).getName().equals(id)) {
                        SymbolTable.error(classDef.ctx, classDef.token, "Inheritance cycle for class " + id);
                        return null;
                    }
                    parent = parent.getParent();
                }
            }
        }

        classDef.features.forEach(feature -> feature.accept(this));
        return null;
    }

    @Override
    public ClassSymbol visit(Program program) {
        var main = SymbolTable.globals.lookup("Main");

        if (main == null) {
            SymbolTable.error(program.ctx, program.token, "Every program must have a class Main");
        } else {
            var mainFunction = ((ClassSymbol)main).lookupFunction("main");

            if (mainFunction == null) {
                SymbolTable.error(program.ctx, program.token, "No method main in class Main");
            }
        }

        program.stmts.forEach(stmt -> stmt.accept(this));
        return null;
    }

    Scope getActualScopeForId(Scope scope, String sym) {
        while (scope != null) {

            if (scope instanceof ClassSymbol) {
//                System.out.println("#class " + scope);
                if (((ClassSymbol) scope).getIdSymbols().containsKey(sym)) {
                    return scope;
                }
            }

            if (scope instanceof FunctionSymbol) {
//                System.out.println("#cfunc " + scope);
                if (((FunctionSymbol) scope).getLocalSymbols().containsKey(sym)) {

                    return scope;
                }
            }

            if (scope instanceof DefaultScope) {
//                System.out.println("#def " + scope);
                if (((DefaultScope) scope).getSymbols().containsKey(sym)) {
                    return scope;
                }
            }

            scope = scope.getParent();
        }

        return  null;
    }
}
