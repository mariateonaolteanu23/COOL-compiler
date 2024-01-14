package cool.structures;


public class IdSymbol extends Symbol {

    protected ClassSymbol type;

    public IdSymbol(String name) {
        super(name);
    }

    public IdSymbol(String name, ClassSymbol type) {
        super(name);
        this.type = type;
    }

    public void setType(ClassSymbol type) {
        this.type = type;
    }

    public ClassSymbol getType() {
        return type;
    }
};

