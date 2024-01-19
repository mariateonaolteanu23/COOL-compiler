package cool.ast;

import cool.structures.ClassSymbol;
import cool.structures.FunctionSymbol;
import cool.structures.IdSymbol;
import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import java.util.List;


public abstract class ASTNode {
    protected Token token;

    ASTNode(Token token) {
        this.token = token;
    }

    Token getToken() {
        return token;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

class ClassDef extends ASTNode {
    Type id;
    Type parent;
    List<Feature> features;
    ParserRuleContext ctx;

    private Scope scope;

    ClassDef(Type id, List<Feature> features, Token token, ParserRuleContext ctx) {
        super(token);
        this.id = id;
        this.features = features;
        this.ctx = ctx;
    }
    ClassDef(Type id, Type parent, List<Feature> features, Token token, ParserRuleContext ctx) {
        super(token);
        this.id = id;
        this.parent = parent;
        this.features = features;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}

abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}


class Id extends Expression {
    private IdSymbol symbol;
    private Scope scope;

    public ParserRuleContext ctx;



    Id(Token token, ParserRuleContext ctx) {
        super(token);
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    IdSymbol getSymbol() {
        return symbol;
    }

    void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }

    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}

class New extends Expression {
    private Scope scope;
    Type type;

    ParserRuleContext ctx;
    New(Type type, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}

class Stringg extends Expression {
    Stringg (Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Int extends Expression {
    Int(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Bool extends Expression {
    Bool(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class BitComplement extends Expression {
    Expression exp;

    ParserRuleContext ctx;
    BitComplement(Expression exp, Token op, ParserRuleContext ctx) {
        super(op);
        this.exp = exp;
        this.ctx = ctx;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Isvoid extends Expression {
    Expression exp;

    ParserRuleContext ctx;

    Isvoid(Expression exp, Token op, ParserRuleContext ctx) {
        super(op);
        this.exp = exp;
        this.ctx = ctx;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Not extends Expression {
    Expression exp;

    ParserRuleContext ctx;

    Not(Expression exp, Token op, ParserRuleContext ctx) {
        super(op);
        this.exp = exp;
        this.ctx = ctx;

    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Block extends Expression {
    List<Expression> exps;

    Block(List<Expression> exps, Token start) {
        super(start);
        this.exps = exps;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class If extends Expression {
    Expression cond;
    Expression thenBranch;
    Expression elseBranch;
    ParserRuleContext ctx;

    Scope scope;

    If(Expression cond,Expression thenBranch, Expression elseBranch, Token start, ParserRuleContext ctx) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}

class While extends Expression {
    Expression cond;
    Expression body;
    ParserRuleContext ctx;


    While(Expression cond, Expression body, Token start, ParserRuleContext ctx) {
        super(start);
        this.cond = cond;
        this.body = body;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Local extends ASTNode{
    Type type;
    Id id;
    Expression init;

    ParserRuleContext ctx;


    Local(Id id, Type type, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.ctx = ctx;
    }

    Local(Id id, Type type, Expression initValue, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.init = initValue;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Let extends Expression {
    List<Local> locals;
    Expression body;


    Let(List<Local> locals, Expression body, Token start) {
        super(start);
        this.locals = locals;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class CaseBranch extends ASTNode {
    Id id;
    Type type;
    Expression exp;

    ParserRuleContext ctx;

    CaseBranch(Id id, Type type, Expression exp, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.exp = exp;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Case extends Expression {
    Expression exp;
    List<CaseBranch> branches;
    ParserRuleContext ctx;

    Case(Expression exp, List<CaseBranch> branches, Token start, ParserRuleContext ctx) {
        super(start);
        this.exp = exp;
        this.branches = branches;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Assign extends Expression {
    Id id;
    Expression expr;

    ParserRuleContext ctx;

    Assign(Id id, Expression expr, Token token, ParserRuleContext ctx) {
        super(token);
        this.id = id;
        this.expr = expr;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Relational extends Expression {
    Expression left;
    Expression right;

    ParserRuleContext ctx;

    Relational(Expression left, Expression right, Token op, ParserRuleContext ctx) {
        super(op);
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Plus extends Expression {
    Expression left;
    Expression right;

    ParserRuleContext ctx;

    Plus(Expression left, Expression right, Token op, ParserRuleContext ctx) {
        super(op);
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Minus extends Expression {
    Expression left;
    Expression right;

    ParserRuleContext ctx;

    Minus(Expression left, Expression right, Token op,  ParserRuleContext ctx) {
        super(op);
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Mult extends Expression {
    Expression left;
    Expression right;
    ParserRuleContext ctx;

    Mult(Expression left, Expression right, Token op, ParserRuleContext ctx) {
        super(op);
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Div extends Expression {
    Expression left;
    Expression right;

    ParserRuleContext ctx;

    Div(Expression left, Expression right, Token op, ParserRuleContext ctx) {
        super(op);
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class UnaryArithmetic extends Expression {
    Expression expr;

    ParserRuleContext ctx;

    UnaryArithmetic(Expression expr, Token op, ParserRuleContext ctx) {
        super(op);
        this.expr = expr;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


abstract class Feature extends ASTNode {
    Feature(Token token) {
        super(token);
    }
}

class Type extends ASTNode {
    Type(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Formal extends ASTNode {
    Type type;
    Id id;
    ParserRuleContext ctx;

    Formal(Type type, Id id, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Attribute extends Feature {
    Type type;
    Id id;
    Expression init;

    ParserRuleContext ctx;

    Attribute(Id id, Type type, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.ctx = ctx;
    }

    Attribute(Id id, Type type, Expression initValue, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.init = initValue;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class FuncDef extends Feature {
    Type type;
    Id id;
    List<Formal> formals;
    Expression body;

    ParserRuleContext ctx;

    FuncDef(Type type, Id id, List<Formal> formals, Expression body, Token token, ParserRuleContext ctx) {
        super(token);
        this.type = type;
        this.id = id;
        this.formals = formals;
        this.body = body;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class ExplicitDispatch extends Expression {
    Expression exp;
    Type type;
    Id id;
    List<Expression> args;
    ParserRuleContext ctx;

    ClassSymbol callerType;

    ExplicitDispatch(Expression exp, Type type, Id id, List<Expression> args, Token token, ParserRuleContext ctx) {
        super(token);
        this.exp = exp;
        this.type = type;
        this.id = id;
        this.args = args;
        this.ctx = ctx;
    }

    ExplicitDispatch(Expression exp, Id id, List<Expression> args, Token token, ParserRuleContext ctx) {
        super(token);
        this.exp = exp;
        this.id = id;
        this.args = args;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void setCallerType(ClassSymbol callerType) {
        this.callerType = callerType;
    }

    public ClassSymbol getCallerType() {
        return this.callerType;
    }
}

class ImplicitDispatch extends ExplicitDispatch {

    // self.f(...)
    ImplicitDispatch(Expression exp, Id id, List<Expression> args, Token token, ParserRuleContext ctx) {
        super(exp, id, args, token, ctx);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Program extends ASTNode {
    List<ASTNode> stmts;

    ParserRuleContext ctx;

    Program(List<ASTNode> stmts, Token token, ParserRuleContext ctx) {
        super(token);
        this.stmts = stmts;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
