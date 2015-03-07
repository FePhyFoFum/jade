package org.opentree.bitarray;

import gnu.trove.list.array.TLongArrayList;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

/**
 * A relatively fast and memory efficient set implementation for long integer values (which is underlain by
 * a LongBitSet implementation).
 * 
 * This mutable implementation extends ImmutableCompactLongSet to allow the addition/removal of values,
 * but has theta(N) performance for every call to hashCode()--which is called on every attempt to check if
 * this object is contained in a HashMap or HashSet. The immutable implementation has theta(1) performance
 * for hashCode but does not allow the addition/removal of values (except for the addition of all values at
 * the time of construction).
 * 
 * @author cody hinchliff
 */
public class MutableCompactLongSet extends ImmutableCompactLongSet {

//	LongBitSet bs;
	
	// ==== constructors
		
	public MutableCompactLongSet(Iterable<Long> longArr) {
		bs = new LongBitSet();
		this.addAll(longArr);
	}

	public MutableCompactLongSet(int[] intArr) {
		bs = new LongBitSet();
		this.addAll(intArr);
	}
	
	public MutableCompactLongSet(long[] longArr) {
		bs = new LongBitSet();
		this.addAll(longArr);
	}

	public MutableCompactLongSet(TLongArrayList tLongArr) {
		bs = new LongBitSet();
		this.addAll(tLongArr);
	}
	
	public MutableCompactLongSet(LongBitSet bs) {
		this.bs = new LongBitSet(bs);
	}
	
	public MutableCompactLongSet() {
		bs = new LongBitSet();
	}

	// == addition methods
	
	/**
	 * Adds the value to the bitset.
	 */
	public void add(Long l) {
		bs.set(l, true);
	}

	/**
	 * Adds the value to the bitset.
	 * @param l
	 */
	public void add(int i) {
		bs.set((long) i, true);
	}

	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(int[] toAdd) {
		for (int i : toAdd) {
			add((long) i);
		}
	}
	
	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(long[] toAdd) {
		for (long l : toAdd) {
			add(l);
		}
	}
	
	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(Iterable<Long> toAdd) {
		for (Long l : toAdd) {
			add(l);
		}
	}
	
	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(BitSet toAdd) {
		for (int i = toAdd.nextSetBit(0); i >= 0; i = toAdd.nextSetBit(i+1)) {
			add((long) i);
		}
	}

	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(TLongArrayList toAdd) {
		for (int i = 0; i < toAdd.size(); i++) {
			add(toAdd.get(i));
		}
	}

	/**
	 * Add all the values to the bitset.
	 * @param toAdd
	 */
	public void addAll(MutableCompactLongSet toAdd) {
		this.addAll((Iterable<Long>) toAdd); // use the iterator method
	}
	
	// == removal methods

	/** 
	 * Remove all values from this bitset.
	 */
	public void clear() {
		bs = new LongBitSet();
	}

	/**
	 * Remove the value from the bitset.
	 * @param l
	 */
	public void remove(Long l) {
		bs.set(l, false);
	}
	
	/**
	 * Remove all the values in the passed array from the bitset.
	 * @param toRemove
	 */
	public void removeAll(int[] toRemove) {
		for (int i : toRemove) {
			remove((long) i);
		}
	}
	
	/**
	 * Remove all the values in the passed array from the bitset.
	 * @param toRemove
	 */
	public void removeAll(long[] toRemove) {
		for (long l : toRemove) {
			remove(l);
		}
	}
	
	/**
	 * Remove all the values in the passed iterable from the bitset.
	 * @param toRemove
	 */
	public void removeAll(Iterable<Long> toRemove) {
		for (Long l : toRemove) {
			remove(l);
		}
	}

	/**
	 * Remove all the values in the incoming bitset from the bitset.
	 * @param toRemove
	 */
	public void removeAll(BitSet toRemove) {
		for (int i = toRemove.nextSetBit(0); i >= 0; i = toRemove.nextSetBit(i+1)) {
			remove((long) i);
		}
	}
	
	/**
	 * Remove all the values in the arraylist from the bitset.
	 * @param toRemove
	 */
	public void removeAll(TLongArrayList toRemove) {
		for (int i = 0; i < toRemove.size(); i++) {
			remove(toRemove.get(i));
		}
	}

	/**
	 * Remove all the values in the incoming bitset from this bitset.
	 * 
	 * @param toRemove
	 */
	public void removeAll(MutableCompactLongSet toRemove) {
		this.removeAll((Iterable<Long>) toRemove); // use the iterator method
	}
	
	/**
	 * For this (the mutable) implementation of the CompactLongSet, we have to recalculate the hash code each time,
	 * since the contents of the underlying long bit set may have changed. Thus this method takes theta(N) time with the
	 * number of elements in this set.
	 * @return
	 */
	@Override
	public int hashCode() {
		return bs.hashCode();
	}
	
	/**
	 * Thorough testing for adding and removing elements.
	 */
	public static void main(String[] args) {
		if (args.length < 1) { throw new IllegalArgumentException("must indicate the number of reps: an integer"); }
		int reps = Integer.valueOf(args[0]);
		for (int i = 0; i < reps; i++) {
			runMutabilityTests();
		}
	}
	
	public static void runMutabilityTests() {

		Random r = new Random();
		MutableCompactLongSet test1 = new MutableCompactLongSet();

		// create a random test arrays of ints
		int n1 = r.nextInt(20);
		long[] arr1 = new long[n1];
		for (int i = 0; i < arr1.length; i++) {
			arr1[i] = Math.abs(r.nextLong());
		}
		System.out.println("\nThe first array is: " + Arrays.toString(arr1));
		
		// test setting and getting
		System.out.println("Testing adding and getting");
		for (long k : arr1) {
			test1.add((long) k);
		}
		MutableCompactLongSet test2 = new MutableCompactLongSet();
		for (long k : arr1) {
			if (test1.contains(k) == false) {
				throw new AssertionError("Adding and getting failed. Bitset 1 should have contained " + k + " but it did not");
			} else {
				test2.add((long) k);
			}
		}
		test1 = new MutableCompactLongSet(arr1);
		for (Long l : test2) {
			if (test1.contains(l) == false) {
				throw new AssertionError("Adding and getting failed. Bitset 1 should have contained " + l + " but it did not");
			}
		}
		System.out.println("Adding and getting passed\n");
		
		System.out.println("Testing Bitset construction from int array primitive");
		long[] testArrLong = new long[r.nextInt(20)];
		for (int k = 0; k < testArrLong.length; k++) {
			testArrLong[k] = Math.abs(r.nextLong());
		}		

		// testing BitSet updating

		test1 = new MutableCompactLongSet(testArrLong);
		System.out.println("Testing removal from the BitArray");
		System.out.println("Bitset contains: " + test1);
		System.out.println("Removing values: " + Arrays.toString(testArrLong));
		test1.removeAll(testArrLong);
		for (int k = 0; k < testArrLong.length; k++) {
			if (test1.contains(testArrLong[k])) {
				throw new java.lang.AssertionError("Bitset removal failed, still contains " + testArrLong[k]);
			}
		}
		if (test1.size() > 0) {
			System.out.println("Array should be empty, but it still contains: " + test1);
			throw new java.lang.AssertionError("Bitset removal failed");
		}
		if (test1.bs.cardinality() > 0) {
			throw new AssertionError("Bitset is empty, but its BitSet still contains " + test1.bs.cardinality() + " values");
		} else {
			System.out.println("Bitset is empty");
		}
		System.out.println("Bitset removal passed\n");
		
		int maxOps = 10;

		System.out.println("Testing add/remove with duplicate values");
		
		int nCycles = r.nextInt(maxOps);
		int nAdds;
		int nRemoves;
		int testVal;

		test1 = new MutableCompactLongSet();
		System.out.println("BitSet contains " + test1);
//		HashMap<Integer, Integer> expectedCounts = new HashMap<Integer, Integer>();
		for (int k = 0; k < nCycles; k++) {
			nAdds = r.nextInt(maxOps);
			nRemoves = r.nextInt(maxOps);
			testVal = Math.abs(r.nextInt());
			System.out.println("Will add the value " + testVal + " to  BitSet " + nAdds + " times, then attempt to remove it " + nRemoves + " times");
			for (int l = 0; l < nAdds; l++) {
				test1.add((long) testVal);
			}
			test1.remove((long) testVal);
			System.out.println("BitSet contains " + test1);
			if (test1.contains(testVal)) {
				throw new AssertionError("BitSet should not contain testval");
			}
		}
		System.out.println("Passed duplicate values add/remove\n");
	}
}