package cool.structures;

import java.util.*;

public class ClassSymbol extends IdSymbol implements Scope {
    protected Map<String, Symbol> functionSymbols = new LinkedHashMap<>();
    protected Map<String, Symbol> idSymbols = new LinkedHashMap<>();
    Scope parent;
    public static final ClassSymbol OBJECT  = new ClassSymbol("Object", null);
    public static final ClassSymbol SELF_TYPE   = new ClassSymbol("SELF_TYPE", OBJECT);
    public static final ClassSymbol IO   = new ClassSymbol("IO", OBJECT);
    public static final ClassSymbol INT   = new ClassSymbol("Int", OBJECT);
    public static final ClassSymbol STRING = new ClassSymbol("String", OBJECT);
    public static final ClassSymbol BOOL  = new ClassSymbol("Bool", OBJECT);

    public ClassSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        if (sym instanceof FunctionSymbol) {
            if (functionSymbols.containsKey(sym.getName()))
                return false;
            functionSymbols.put(sym.getName(), sym);
            return true;
        }

        if (sym instanceof IdSymbol) {
            if (idSymbols.containsKey(sym.getName()))
                return false;
            idSymbols.put(sym.getName(), sym);
        }

        return true;
    }

    @Override
    public Symbol lookup(String str) {
        if (parent != null) {
            return parent.lookup(str);
        }
        return null;
    }
    @Override
    public Symbol lookupFunction(String str) {
        var sym = functionSymbols.get(str);

        if (sym != null)
            return sym;

        if (parent != null && parent instanceof ClassSymbol) {
            return parent.lookupFunction(str);
        }

        return null;
    }

    @Override
    public Symbol lookupClass() {
        return this;
    }

    @Override
    public Symbol lookupId(String str) {
        var sym = idSymbols.get(str);

        if (sym != null)
            return sym;

        if (parent != null && parent instanceof ClassSymbol) {
            return parent.lookupId(str);
        }

        return null;
    }


    @Override
    public Scope getParent() {
        return parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    private LinkedHashSet<ClassSymbol> dfs(ClassSymbol s) {
        LinkedHashSet<ClassSymbol> path = new LinkedHashSet<>();

        var parent = s;
        while (parent != null && !parent.getName().equals(ClassSymbol.OBJECT.getName())) {
            path.add(parent);
            parent = (ClassSymbol) parent.getParent();
        }

        path.add(ClassSymbol.OBJECT);

        return path;
    }

    public ClassSymbol leastUpperBound(ClassSymbol s) {
        if (s.getName().equals(this.name))
            return s;
        LinkedHashSet<ClassSymbol> pathA = dfs(this);
        LinkedHashSet<ClassSymbol> pathB = dfs(s);

        pathA.retainAll(pathB);

        return (ClassSymbol) Arrays.stream(pathA.toArray()).toArray()[0];
    }

    public ClassSymbol getFinalType(Scope scope) {
        if (scope == null)
            return this;

        if (!this.name.equals(ClassSymbol.SELF_TYPE.getName()))
            return this;

        return (ClassSymbol) scope.lookupClass();
    }

    public Map<String, Symbol> getFunctionSymbols() {
        return functionSymbols;
    }

    public Map<String, Symbol> getIdSymbols() {
        return idSymbols;
    }
}
