package jade.tree;

import java.util.Set;

public class JadeBipartition {

	private final Set<JadeNode> ingroup;
	private final Set<JadeNode> outgroup;
	
	public JadeBipartition(Set<JadeNode> ingroup, Set<JadeNode> outgroup) {
		this.ingroup = ingroup;
		this.outgroup = outgroup;
	}
	
	@Override
	public boolean equals(Object that) {
		boolean result = false;
		if (that instanceof JadeBipartition) {
			JadeBipartition b = (JadeBipartition) that;
			result = ingroup.size() == b.ingroup.size() && outgroup.size() == b.outgroup.size() &&
					 ingroup.containsAll(b.ingroup) && outgroup.containsAll(b.outgroup);
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		// have not tested this hash function for performance. be wary.
		long h = 0;
		for (JadeNode p : ingroup) { int x = p.getName().hashCode(); h = (h * (59 + x)) + x; }
		for (JadeNode p : outgroup) { int x = p.getName().hashCode(); h = (h * (73 + x)) + x; }
		return (int) h;
	}
	
	public Iterable<JadeNode> ingroup() {
		return ingroup;
	}

	public Iterable<JadeNode> outgroup() {
		return outgroup;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("{");
		boolean first = true;
		for (JadeNode l : ingroup) {
			if (first) {
				first = false;
			} else {
				s.append(", ");
			}
			s.append(l.getName());
		}
		s.append("} | {");
		first = true;
		for (JadeNode l : outgroup) {
			if (first) {
				first = false;
			} else {
				s.append(", ");
			}
			s.append(l.getName());
		}
		s.append("}");
		return s.toString();
	}
}