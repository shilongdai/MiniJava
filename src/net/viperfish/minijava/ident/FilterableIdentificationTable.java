package net.viperfish.minijava.ident;

public interface FilterableIdentificationTable extends LeveledIdentificationTable {

    public FilterableIdentificationTable filterTable(IdentificationFilter filter);

}
