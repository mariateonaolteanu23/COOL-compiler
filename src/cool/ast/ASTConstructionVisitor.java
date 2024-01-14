package cool.ast;

import cool.parser.CoolParser;
import cool.parser.CoolParserBaseVisitor;

import java.util.Objects;
import java.util.stream.Collectors;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        return new Program(ctx.children.stream().map(this::visit)
                .filter(Objects::nonNull).collect(Collectors.toList()), ctx.start, ctx);
    }

    @Override
    public ASTNode visitClassDef(CoolParser.ClassDefContext ctx) {
        if (ctx.parent == null)
            return new ClassDef(new Type(ctx.name),
                    ctx.features.stream().map(f -> (Feature)visit(f)).collect(Collectors.toList()),
                    ctx.name, ctx);

        return new ClassDef(new Type(ctx.name),
                new Type(ctx.parent),
                ctx.features.stream().map(f -> (Feature)visit(f)).collect(Collectors.toList()),
                ctx.name, ctx);
    }

    @Override
    public ASTNode visitAttribute(CoolParser.AttributeContext ctx) {
        if (ctx.init == null)
            return new Attribute(new Id(ctx.name, ctx), new Type(ctx.type), ctx.name, ctx);
        return new Attribute(new Id(ctx.name, ctx), new Type(ctx.type), (Expression)visit(ctx.init), ctx.name, ctx);
    }

    @Override
    public ASTNode visitNew(CoolParser.NewContext ctx) {
        return new New(new Type(ctx.type), ctx.type, ctx);
    }

    @Override
    public ASTNode visitFuncDef(CoolParser.FuncDefContext ctx) {
        return new FuncDef(
                new Type(ctx.type),
                new Id(ctx.name, ctx),
                ctx.formals.stream().map(f -> (Formal)visit(f)).collect(Collectors.toList()),
                (Expression)visit(ctx.body),
                ctx.name, ctx);
    }

    @Override
    public ASTNode visitFormalDef(CoolParser.FormalDefContext ctx) {
        return new Formal(new Type(ctx.type), new Id(ctx.name, ctx), ctx.name, ctx);
    }

    @Override
    public ASTNode visitBitComplement(CoolParser.BitComplementContext ctx) {
        return new BitComplement((Expression) visit(ctx.e), ctx.TILDE().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitPlusMinus(CoolParser.PlusMinusContext ctx) {
        if (ctx.op.getText().equals("+"))
            return new Plus((Expression)visit(ctx.left), (Expression)visit(ctx.right), ctx.op, ctx);

        return new Minus((Expression)visit(ctx.left), (Expression)visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitString(CoolParser.StringContext ctx) {
        return new Stringg(ctx.STRING().getSymbol());
    }

    @Override
    public ASTNode visitBool(CoolParser.BoolContext ctx) {
        return new Bool(ctx.BOOL().getSymbol());
    }

    @Override
    public ASTNode visitWhile(CoolParser.WhileContext ctx) {
        return new While((Expression) visit(ctx.cond), (Expression) visit(ctx.body), ctx.WHILE().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitImplicitDispatch(CoolParser.ImplicitDispatchContext ctx) {
        return new ImplicitDispatch(null, new Id(ctx.name, ctx),
                ctx.args.stream().map(a -> (Expression) visit(a)).collect(Collectors.toList()),
                ctx.name, ctx);
    }


    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new Int(ctx.INT().getSymbol());
    }

    @Override
    public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
        return new Isvoid((Expression) visit(ctx.e), ctx.start, ctx);
    }

    @Override
    public ASTNode visitNot(CoolParser.NotContext ctx) {
        return new Not((Expression) visit(ctx.e), ctx.NOT().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitParen(CoolParser.ParenContext ctx) {
        return visit(ctx.e);
    }

    @Override
    public ASTNode visitMultDiv(CoolParser.MultDivContext ctx) {
        if (ctx.op.getText().equals("*"))
            return new Mult((Expression)visit(ctx.left), (Expression)visit(ctx.right), ctx.op, ctx);
        return new Div((Expression)visit(ctx.left), (Expression)visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitExplicitDispatch(CoolParser.ExplicitDispatchContext ctx) {
        if (ctx.type == null)
            return new ExplicitDispatch(
                    (Expression) visit(ctx.e),
                    new Id(ctx.name, ctx),
                    ctx.args.stream().map(a -> (Expression)visit(a)).collect(Collectors.toList()),
                    ctx.DOT().getSymbol(),
                    ctx);
        return new ExplicitDispatch(
                (Expression) visit(ctx.e),
                new Type(ctx.type),
                new Id(ctx.name, ctx),
                ctx.args.stream().map(a -> (Expression)visit(a)).collect(Collectors.toList()),
                ctx.DOT().getSymbol(),
                ctx);
    }

    @Override
    public ASTNode visitBlock(CoolParser.BlockContext ctx) {
        return new Block(ctx.body.stream().map(e -> (Expression) visit(e)).collect(Collectors.toList()), ctx.start);
    }

    @Override
    public ASTNode visitLet(CoolParser.LetContext ctx) {
        return new Let(
                ctx.localVar.stream().map(l -> (Local)visit(l)).collect(Collectors.toList()),
                (Expression) visit(ctx.body),
                ctx.LET().getSymbol());
    }

    @Override
    public ASTNode visitRelational(CoolParser.RelationalContext ctx) {
        return new Relational((Expression) visit(ctx.left), (Expression) visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitId(CoolParser.IdContext ctx) {
        return new Id(ctx.ID().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitIf(CoolParser.IfContext ctx) {
        return new If((Expression) visit(ctx.cond), (Expression) visit(ctx.thenBranch),
                (Expression) visit(ctx.elseBranch), ctx.IF().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        return new Case((Expression) visit(ctx.e),
                ctx.cases.stream().map(c -> (CaseBranch)visit(c)).collect(Collectors.toList()),
                ctx.CASE().getSymbol());
    }

    @Override
    public ASTNode visitAssign(CoolParser.AssignContext ctx) {
        return new Assign(new Id(ctx.name, ctx), (Expression) visit(ctx.e), ctx.ASSIGN().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitCaseBranchDef(CoolParser.CaseBranchDefContext ctx) {
        return new CaseBranch(new Id(ctx.name, ctx), new Type(ctx.type), (Expression) visit(ctx.e), ctx.name, ctx);
    }

    @Override
    public ASTNode visitLocalDef(CoolParser.LocalDefContext ctx) {
        if (ctx.init == null)
            return new Local(new Id(ctx.name, ctx), new Type(ctx.type), ctx.name, ctx);
        return new Local(new Id(ctx.name, ctx), new Type(ctx.type), (Expression) visit(ctx.init), ctx.name, ctx);
    }

}
