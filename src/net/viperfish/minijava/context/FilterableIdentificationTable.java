package net.viperfish.minijava.context;

public interface FilterableIdentificationTable extends LeveledIdentificationTable {

    public FilterableIdentificationTable filterTable(IdentificationFilter filter);

}
