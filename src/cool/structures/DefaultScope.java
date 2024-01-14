package cool.structures;

import java.util.*;

public class DefaultScope implements Scope {
    
    private Map<String, Symbol> symbols = new LinkedHashMap<>();
    
    private Scope parent;
    
    public DefaultScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (symbols.containsKey(sym.getName()))
            return false;
        
        symbols.put(sym.getName(), sym);
        
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = symbols.get(name);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookup(name);
        
        return null;
    }

    @Override
    public Symbol lookupId(String str) {
        var sym = symbols.get(str);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookupId(str);

        return null;
    }

    @Override
    public Symbol lookupFunction(String str) {
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
    
    @Override
    public String toString() {
        return symbols.values().toString();
    }

}
