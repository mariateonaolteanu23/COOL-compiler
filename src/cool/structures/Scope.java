package cool.structures;

public interface Scope {
    public boolean add(Symbol sym);
    public Symbol lookup(String str);
    public Symbol lookupId(String str);
    public Symbol lookupFunction(String str);
    public Symbol lookupClass();

    public Scope getParent();
}
