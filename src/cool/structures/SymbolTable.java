package cool.structures;

import java.io.File;
import java.util.List;
import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static Scope globals;

    public static List<Symbol> invalidParents = List.of(ClassSymbol.SELF_TYPE, ClassSymbol.INT, ClassSymbol.STRING, ClassSymbol.BOOL);

    public static List<String> basicTypes = List.of("Int", "Object", "Bool");

    private static boolean semanticErrors;
    
    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        semanticErrors = false;

        // Populate global scope
        var abort = new FunctionSymbol("abort", ClassSymbol.OBJECT);
        abort.setType(ClassSymbol.OBJECT);
        ClassSymbol.OBJECT.add(abort);

        var typeName = new FunctionSymbol("type_name", ClassSymbol.OBJECT);
        typeName.setType(ClassSymbol.STRING);
        ClassSymbol.OBJECT.add(typeName);

        var copy = new FunctionSymbol("copy", ClassSymbol.OBJECT);
        copy.setType( ClassSymbol.SELF_TYPE);
        ClassSymbol.OBJECT.add(copy);
        ClassSymbol.OBJECT.setParent(globals);

        var outString = new FunctionSymbol("out_string", ClassSymbol.IO);
        outString.setType(ClassSymbol.SELF_TYPE);
        outString.add(new IdSymbol("x", ClassSymbol.STRING));
        ClassSymbol.IO.add(outString);

        var outInt = new FunctionSymbol("out_int", ClassSymbol.IO);
        outInt.setType(ClassSymbol.SELF_TYPE);
        outInt.add(new IdSymbol("x", ClassSymbol.INT));
        ClassSymbol.IO.add(outInt);

        var inString = new FunctionSymbol("in_string", ClassSymbol.IO);
        inString.setType(ClassSymbol.STRING);
        ClassSymbol.IO.add(inString);

        var inInt = new FunctionSymbol("in_int", ClassSymbol.IO);
        inInt.setType(ClassSymbol.INT);
        ClassSymbol.IO.add(inInt);

        var length = new FunctionSymbol("length", ClassSymbol.STRING);
        length.setType(ClassSymbol.INT);
        ClassSymbol.STRING.add(length);

        var concat = new FunctionSymbol("concat", ClassSymbol.STRING);
        concat.setType(ClassSymbol.STRING);
        concat.add(new IdSymbol("s", ClassSymbol.STRING));
        ClassSymbol.STRING.add(concat);

        var substr = new FunctionSymbol("substr", ClassSymbol.STRING);
        substr.setType(ClassSymbol.STRING);
        substr.add(new IdSymbol("i", ClassSymbol.INT));
        substr.add(new IdSymbol("l", ClassSymbol.INT));
        ClassSymbol.STRING.add(substr);

        globals.add(ClassSymbol.OBJECT);
        globals.add(ClassSymbol.IO);
        globals.add(ClassSymbol.INT);
        globals.add(ClassSymbol.STRING);
        globals.add(ClassSymbol.BOOL);
        globals.add(ClassSymbol.SELF_TYPE);
    }
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();
        
        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }

    public static ClassSymbol getClassScope(Scope scope) {
        var classScope = scope;

        if (!(scope instanceof ClassSymbol))
            while (classScope != null) {
                if (classScope.getParent() instanceof ClassSymbol)
                    break;
                classScope = classScope.getParent();
            }

        return  classScope != null ? (ClassSymbol)classScope.getParent() : ClassSymbol.OBJECT;
    }
}
