package org.behnaz.rcsp;

import org.behnaz.rcsp.model.MergerNode;
import org.behnaz.rcsp.model.RouteNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.behnaz.rcsp.model.util.SolverHelper.getEqual;

public class EqualBasedConnectorFactory extends Primitive {
	private List<HashSet<String>> equals;
	private Map<Integer, String> representative = new HashMap<>();

	public EqualBasedConnectorFactory(final List<HashSet<String>> equals) {
		this.equals = equals;
		makeRepresentative(equals);
	}

	private void makeRepresentative(final List<HashSet<String>> equals) {
		for (int i = 0; i < equals.size(); i++) {
			representative.put(i, equals.get(i).iterator().next());
		}
	}

	public ConstraintConnector merger(final String source1, final String source2, final String sink) {
		final String p1 = getEqual(source1, equals, representative);
		final String p2 = getEqual(source2, equals, representative);
		final String p3 = getEqual(sink, equals, representative);
		final String merger = new MergerNode("", new HashSet<String>(Arrays.asList(p1, p2)), p3).getConstraint();
		return new ConstraintConnector(merger, p1, p2, p3);
	}

	public ConstraintConnector sync(final String p1, final String p2) {
		String sync = String.format("(%s %s %s)"/* + AND + NOT + "(%sc" + AND + "%sk)"*/, 
				flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));//, p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}

	public ConstraintConnector router(final String source, final String... ks) {
		final String c = getEqual(source, equals, representative);
		final Set<String> sinkEquals = Arrays.asList(ks).stream().map(e -> getEqual(e, equals, representative)).collect(Collectors.toSet());
		final String or = sinkEquals.stream().map(this::flow).collect(Collectors.joining(AbstractConnector.OR));
		final String exclusives = Arrays.asList(ks).stream().map(this::flow).collect(Collectors.joining(AbstractConnector.AND));
		final List<String> l = new ArrayList<>();
		l.add(c);
		l.addAll(Arrays.asList(ks));
		return new ConstraintConnector(new RouteNode("", c, sinkEquals).getConstraint(), l);
	}

	public ConstraintConnector lossySync(final String source, final String sink) {
		final String p1 = getEqual(source, equals, representative);
		final String p2 = getEqual(sink, equals, representative);
		final String lossy = String.format("(%s %s %s)", flow(p2), AbstractConnector.IMPLIES, flow(p1));
		return new ConstraintConnector(lossy, p1, p2);
	}

	public ConstraintConnector syncDrain(final String source, final String sink) {
		final String p1 = getEqual(source, equals, representative);
		final String p2 = getEqual(sink, equals, representative);
		String syncDrain = String.format("(%s %s %s)", flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	public ConstraintConnector prioritySync(final String p1, final String p2) {
		final String prioritySync = String.format("(%s" + AbstractConnector.RIGHTLEFTARROW + "%s)" + AbstractConnector.AND + AbstractConnector.NOT + "(%sc" + AbstractConnector.AND + "%sk)" + AbstractConnector.AND
				+ "%sbullet" + AbstractConnector.AND + "%sbullet", flow(p1), flow(p2), p1, p2, p1, p2);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	public ConstraintConnector replicator(final String c, final String... ks) {
		String replicator = "";
		for (String k : ks) {
			if (!replicator.isEmpty()) {
				replicator += AbstractConnector.AND;
			}
			replicator += String.format("(%s %s %s) %s (%s %s %s)",
					// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
					// "%sbullet",
					flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k), AbstractConnector.AND, flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k));
		}
		final List<String> l = new ArrayList<>();
		l.add(c);
		l.addAll(Arrays.asList(ks));
		return new ConstraintConnector(replicator, l);
	}

	public ConstraintConnector replicator(final String c, final String k) {
		String replicator = String.format("(%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k));
		return new ConstraintConnector(replicator, c, k);
	}

	public ConstraintConnector join(final String c1, final String c2, final String k) {
		/*
		 * String replicator = String.format( "(%s" + RIGHTLEFTARROW + "%s)"+
		 * AND + "(%s" + RIGHTLEFTARROW + "%s)", //+ NOT + "(%sc" + AND + "%sk)"
		 * + AND + "%sbullet" + AND + "%sbullet", flow(c), flow(k1), flow(c),
		 * flow(k2) ); return new ConstraintConnector(replicator, c, k1, k2);
		 */
		return replicator(k, c1, c2);
	}

	public ConstraintConnector writer(final String originalSource, final int capacity) {
		final String source = getEqual(originalSource, equals, representative);
		return new ConstraintConnector(capacity > 0 ?
				 String.format("(%s %s %s %s)", flow(source), AbstractConnector.OR, AbstractConnector.NOT, flow(source)) :
		 		String.format("(%s %s)", AbstractConnector.NOT, flow(source)),
		 	source);
	}

	public ConstraintConnector fifo(final String source, final String sink) {
		final String eqsource = getEqual(source, equals, representative);
		final String eqsink = getEqual(sink, equals, representative);
		return new FIFO(source, sink).generateConstraintOnLyRenameEnds(eqsource, eqsink);
	}
}