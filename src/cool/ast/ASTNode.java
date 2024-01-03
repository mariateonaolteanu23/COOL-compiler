package cool.ast;

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

    ClassDef(Type id, List<Feature> features, Token token) {
        super(token);
        this.id = id;
        this.features = features;
    }
    ClassDef(Type id, Type parent, List<Feature> features, Token token) {
        super(token);
        this.id = id;
        this.parent = parent;
        this.features = features;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}


class Id extends Expression {
    Id(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class New extends Expression {
    Type type;
    New(Type type, Token token) {
        super(token);
        this.type = type;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
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

    BitComplement(Expression exp, Token op) {
        super(op);
        this.exp = exp;

    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Isvoid extends Expression {
    Expression exp;

    Isvoid(Expression exp, Token op) {
        super(op);
        this.exp = exp;

    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Not extends Expression {
    Expression exp;

    Not(Expression exp, Token op) {
        super(op);
        this.exp = exp;

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

    If(Expression cond,Expression thenBranch, Expression elseBranch, Token start) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class While extends Expression {
    Expression cond;
    Expression body;


    While(Expression cond, Expression body, Token start) {
        super(start);
        this.cond = cond;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Local extends ASTNode{
    Type type;
    Id id;
    Expression init;

    Local(Id id, Type type, Token token) {
        super(token);
        this.type = type;
        this.id = id;
    }

    Local(Id id, Type type, Expression initValue, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.init = initValue;
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


    CaseBranch(Id id, Type type, Expression exp, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.exp = exp;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Case extends Expression {
    Expression exp;
    List<CaseBranch> branches;


    Case(Expression exp, List<CaseBranch> branches, Token start) {
        super(start);
        this.exp = exp;
        this.branches = branches;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Assign extends Expression {
    Id id;
    Expression expr;

    Assign(Id id, Expression expr, Token token) {
        super(token);
        this.id = id;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Relational extends Expression {
    Expression left;
    Expression right;

    Relational(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Plus extends Expression {
    Expression left;
    Expression right;

    Plus(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Minus extends Expression {
    Expression left;
    Expression right;

    Minus(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Mult extends Expression {
    Expression left;
    Expression right;

    Mult(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Div extends Expression {
    Expression left;
    Expression right;

    Div(Expression left, Expression right, Token op) {
        super(op);
        this.left = left;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class UnaryArithmetic extends Expression {
    Expression expr;

    UnaryArithmetic(Expression expr, Token op) {
        super(op);
        this.expr = expr;
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

    Formal(Type type, Id id, Token token) {
        super(token);
        this.type = type;
        this.id = id;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Attribute extends Feature {
    Type type;
    Id id;
    Expression init;

    Attribute(Id id, Type type, Token token) {
        super(token);
        this.type = type;
        this.id = id;
    }

    Attribute(Id id, Type type, Expression initValue, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.init = initValue;
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

    FuncDef(Type type, Id id, List<Formal> formals, Expression body, Token token) {
        super(token);
        this.type = type;
        this.id = id;
        this.formals = formals;
        this.body = body;
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

    ExplicitDispatch(Expression exp, Type type, Id id, List<Expression> args, Token token) {
        super(token);
        this.exp = exp;
        this.type = type;
        this.id = id;
        this.args = args;
    }

    ExplicitDispatch(Expression exp, Id id, List<Expression> args, Token token) {
        super(token);
        this.exp = exp;
        this.id = id;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class ImplicitDispatch extends ExplicitDispatch {

    // self.f(...)
    ImplicitDispatch(Expression exp, Id id, List<Expression> args, Token token) {
        super(exp, id, args, token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Program extends ASTNode {
    List<ASTNode> stmts;

    Program(List<ASTNode> stmts, Token token) {
        super(token);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
