package org.opentree.bitarray;

import java.util.Map;

/**
 * An interface implemented by MutableCompactLongSet and ImmutableCompactLongSet.
 */
public interface LongSet extends Iterable<Long> {

		public LongBitSet getBitSet();
		public long size();
		public boolean contains(Long l);
		public boolean contains(int i);

		/**
		 * Perfoms a binary andNot on the internal BitSet against the passed BitSet and returns a new biset containing the result.
		 * Does not modify the internal or the passed BitSet.
		 * @param that
		 * @return
		 */
		public LongSet andNot(LongSet that);
			
		/**
		 * Returns true if and only if this set contains exactly zero elements.
		 * @return
		 */
		public boolean isEmpty();
		
		/**
		 * Returns true if and only if this bitset contains any values from the passed bitset.
		 * @param that
		 * @return
		 */
		public boolean containsAny(Iterable<Long> that);
		
		/**
		 * Returns true if and only if this bitset contains all the values contained in the passed bitset.
		 * @param that
		 * @return
		 */
		public boolean containsAll(Iterable<Long> that);
		
		/**
		 * Returns a bitset containing the values that are in both this bitset and the passed bitset.
		 * @param that
		 * @return
		 */
		public LongSet intersection(Iterable<Long> that);
		
		public long[] toArray();
		
		public String toString(Map<Long, Object> names);
	
}
