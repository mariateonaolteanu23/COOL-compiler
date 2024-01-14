package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class FunctionSymbol extends IdSymbol implements Scope {
    protected Map<String, Symbol> localSymbols = new LinkedHashMap<>();

    protected Scope parent;

    public FunctionSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol s) {
        if (localSymbols.containsKey(s.getName()))
            return false;

        localSymbols.put(s.getName(), s);

        return true;
    }

    @Override
    public Symbol lookup(String str) {
        if (parent != null)
            return parent.lookup(str);
        return null;
    }

    @Override
    public Symbol lookupId(String s) {
        var symbol = localSymbols.get(s);

        if (symbol != null)
            return symbol;

        if (parent != null)
            return parent.lookupId(s);
        return null;
    }

    @Override
    public Symbol lookupFunction(String str) {
        if (parent != null)
            return parent.lookupFunction(str);

        return null;
    }

    @Override
    public Symbol lookupClass() {
        if (parent == null)
            return null;

        return parent.lookupClass();
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    public Map<String, Symbol> getLocalSymbols() {
        return this.localSymbols;
    }

}
