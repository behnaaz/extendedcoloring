package org.behnaz.rcsp.model;

import javafx.util.Pair;
import org.behnaz.rcsp.model.util.VariableNamer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.behnaz.rcsp.AbstractConnector.AND;
import static org.behnaz.rcsp.AbstractConnector.NOT;
import static org.behnaz.rcsp.AbstractConnector.OR;
import static org.behnaz.rcsp.AbstractConnector.RIGHTLEFTARROW;

public class MergerNode extends Node {
    public MergerNode(final String name, final Set<String> sources, final String sink) {
        this(new Pair<>(name, new Pair<>(sources, new HashSet<>(Collections.singleton(sink)))));
    }

    public MergerNode(final Pair<String, Pair<Set<String>, Set<String>>> pair) {
        if (pair.getValue().getValue().size() != 1) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong sink number");
        }

        if (pair.getValue().getKey().isEmpty()) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong sink number");
        }

        this.pair = pair;
    }

    public String getSinkEnd(){
        return pair.getValue().getValue().iterator().next();
    }

    public Set<String> getSourceEnds(){
        return pair.getValue().getKey();
    }

    @Override
    public boolean ownsEnd(final String name) {
        if (getSinkEnd().equals(name)) {
            return true;
        }

        return getSourceEnds().stream().filter(e -> e.equals(name)).findAny().isPresent();
    }

    @Override
    public String getConstraint() {
        return "(" + VariableNamer.flow(getSinkEnd()) + RIGHTLEFTARROW  + "(" + getSourceEnds().stream().map(VariableNamer::flow).collect(Collectors.joining(OR)) + "))" +
                AND +
                "(" + NOT + "(" + getSourceEnds().stream().map(VariableNamer::flow).collect(Collectors.joining(AND)) + "))";
    }
}
