package priority.connector;

import priority.primitives.FIFO;
import priority.primitives.Primitive;

public class ConnectorFactory extends Primitive {
	public ConstraintConnector merger(String p1, String p2, String p3) {
		String merger = String.format(
				"(%s" + AbstractConnector.RIGHTLEFTARROW + "(%s" + AbstractConnector.OR + "%s))" + AbstractConnector.AND + "(" + AbstractConnector.NOT + "(%s" + AbstractConnector.AND + "%s))" + AbstractConnector.AND + "((" + AbstractConnector.NOT
						+ "%s )" + AbstractConnector.IMPLIES + "((" + AbstractConnector.NOT + "%sk )" + AbstractConnector.AND + "%sk" + AbstractConnector.AND + "%sk)" + AbstractConnector.OR + "(%sc" + AbstractConnector.AND + ""
						+ AbstractConnector.NOT + " %sk " + AbstractConnector.AND + " %sk)" + AbstractConnector.OR + "(%sc" + "" + AbstractConnector.AND + "" + " %sk" + AbstractConnector.AND + AbstractConnector.NOT + "%sk))"
						+ AbstractConnector.AND + "(" + AbstractConnector.NOT + " (%s" + AbstractConnector.AND + "%s))",
				flow(p3), flow(p1), flow(p2), flow(p1), flow(p2), flow(p3), p3, p1, p2, p3, p1, p2, p3, p1, p2, p1, p2);
		return new ConstraintConnector(merger, p1, p2, p3);
	}

	public ConstraintConnector sync(String p1, String p2) {
		String sync = String.format("(%s %s %s)"/* + AND + NOT + "(%sc" + AND + "%sk)"*/, 
				flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));//, p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}

	/**
	 * Returns the constraints of a new instance of FIFO initialized or uninitialized depending on the third parameter
	 * @param source
	 * @param sink
	 * @return
	 */
	public ConstraintConnector getFIFOConstraint(final String source, final String sink) {
		return new FIFO(source, sink).generateConstraint();
	}

	public ConstraintConnector router(String c, String k1, String k2) {
		String router = String.format(
				"(%s %s ( %s %s %s )) %s (%s (%s %s %s))", 
				flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k1),	AbstractConnector.OR, flow(k2), AbstractConnector.AND, AbstractConnector.NOT, flow(k1), AbstractConnector.AND, flow(k2));
		return new ConstraintConnector(router, c, k1, k2);
	}

	public ConstraintConnector lossyDrain(String p1, String p2) {
		String lossyDrain = String.format("(%s %s %s)", flow(p2), AbstractConnector.IMPLIES, flow(p1));
		return new ConstraintConnector(lossyDrain, p1, p2);
	}

	public ConstraintConnector syncDrain(String p1, String p2) {
		String syncDrain = String.format("(%s %s %s)", flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	public ConstraintConnector prioritySync(String p1, String p2) {
		String prioritySync = String.format("(%s" + AbstractConnector.RIGHTLEFTARROW + "%s)" + AbstractConnector.AND + AbstractConnector.NOT + "(%sc" + AbstractConnector.AND + "%sk)" + AbstractConnector.AND
				+ "%sbullet" + AbstractConnector.AND + "%sbullet", flow(p1), flow(p2), p1, p2, p1, p2);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	public ConstraintConnector replicator(String c, String k1, String k2) {
		String replicator = String.format("(%s %s %s) %s (%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k1), AbstractConnector.AND, flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k2));
		return new ConstraintConnector(replicator, c, k1, k2);
	}

	public ConstraintConnector replicator(String c, String k) {
		String replicator = String.format("(%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k));
		return new ConstraintConnector(replicator, c, k);
	}

	public ConstraintConnector join(String c1, String c2, String k) {
		/*
		 * String replicator = String.format( "(%s" + RIGHTLEFTARROW + "%s)"+
		 * AND + "(%s" + RIGHTLEFTARROW + "%s)", //+ NOT + "(%sc" + AND + "%sk)"
		 * + AND + "%sbullet" + AND + "%sbullet", flow(c), flow(k1), flow(c),
		 * flow(k2) ); return new ConstraintConnector(replicator, c, k1, k2);
		 */
		return replicator(k, c1, c2);
	}

	public ConstraintConnector writer(String source, int capacity) {
		String writer;
		if (capacity > 0)
			writer = String.format("(%s %s %s %s)", flow(source), AbstractConnector.OR, AbstractConnector.NOT, flow(source));
		else
			writer = String.format("( %s %s)", AbstractConnector.NOT, flow(source));
		return new ConstraintConnector(writer, source);
	}
}