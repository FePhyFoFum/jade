package org.opentree.bitarray;

import gnu.trove.list.array.TLongArrayList;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A relatively fast and memory efficient set implementation for long integer values (which is underlain
 * by a LongBitSet implementation).
 * 
 * This immutable implementation has theta(1) performance for hashCode but does not allow the addition/removal
 * of values (except for the addition of all values at the time of construction). The mutable implementation
 * MutableCompactLongSet extends this class to allow the addition/removal of values, but has theta(N) performance
 * for every call to hashCode()--which is called on every attempt to check if this object is contained in a HashMap
 * or HashSet. 
 * 
 * @author cody hinchliff
 */
public class ImmutableCompactLongSet implements LongSet {

	LongBitSet bs;
	final int hashCode;
	
	// ==== constructors
		
	public ImmutableCompactLongSet(Iterable<Long> longArr) {
		bs = new LongBitSet();
		for (Long l : longArr) { add(l); }
		hashCode = computeHash();
	}
	
	public ImmutableCompactLongSet(int[] intArr) {
		bs = new LongBitSet();
		for (int i : intArr) { add((long) i); }
		hashCode = computeHash();
	}
	
	public ImmutableCompactLongSet(long[] longArr) {
		bs = new LongBitSet();
		for (long l : longArr) { add(l); }
		hashCode = computeHash();
	}

	public ImmutableCompactLongSet(TLongArrayList tLongArr) {
		bs = new LongBitSet();
		for (int i = 0; i < tLongArr.size(); i++) { add(tLongArr.get(i)); }
		hashCode = computeHash();
	}
	
	public ImmutableCompactLongSet(LongBitSet bs) {
		this.bs = new LongBitSet(bs); // deep copy
		hashCode = computeHash();
	}

	public ImmutableCompactLongSet(BitSet toAdd) {
		this.bs = new LongBitSet();
		for (int i = toAdd.nextSetBit(0); i >= 0; i = toAdd.nextSetBit(i+1)) { add((long) i); }
		hashCode = computeHash();
	}

	public ImmutableCompactLongSet() {
		bs = new LongBitSet();
		hashCode = computeHash();
	}

	/**
	 * Private method only used internally during construction. Afterward, no values can be added or removed.
	 */
	private void add(long l) {
		this.bs.set(l, true);
	}
	
	/**
	 * Private method only used internally during construction. Afterward, no values can be added or removes, so the
	 * hash code should never change, thus we just store it on construction and thereafter return the stored value.
	 * @return
	 */
	private int computeHash() {
		return bs.hashCode();
	}
	
	// ==== basic functions
	
	/**
	 * Returns a deep copy of the underlying LongBitSet object.
	 */
	public LongBitSet getBitSet() {
		return new LongBitSet(bs);
	}

	public long size() {
		return bs.cardinality();
	}
	
	public boolean contains(Long l) {
		return bs.get(l);
	}

	public boolean contains(int i) {
		return bs.get(i);
	}
	
	// ==== boolean / bitwise operations

	/**
	 * Perfoms a binary andNot on the internal BitSet against the passed BitSet and returns a new biset containing the result.
	 * Does not modify the internal or the passed BitSet. Does not guarantee the specific return type--it could be immutable or not.
	 * @param that
	 * @return
	 */
	public LongSet andNot(LongSet that) {
		throw new UnsupportedOperationException(); // do not need this yet. implement when necessary.
	}
		
	/**
	 * Returns true if and only if this set contains exactly zero elements.
	 * @return
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns true if and only if this bitset contains any values from the passed bitset.
	 * @param that
	 * @return
	 */
	public boolean containsAny(Iterable<Long> that) {
		boolean result = false;
		for (long l : that) {
			if (this.contains(l)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns true if and only if this bitset contains all the values contained in the passed bitset.
	 * @param that
	 * @return
	 */
	public boolean containsAll(Iterable<Long> that) {
		boolean result = true;
		for (long l : that) {
			if (! this.contains(l)) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns a bitset containing the values that are in both this bitset and the passed bitset.
	 * Does not guarantee the specific return type--it could be immutable or not.
	 * @param that
	 * @return
	 */
	public LongSet intersection(Iterable<Long> that) {
		MutableCompactLongSet shared = new MutableCompactLongSet();
		for (long l : that) {
			if (this.contains(l)) {
				shared.add(l);
			}
		}
		return shared;
	}
	
	// ==== output methods

	public long[] toArray() {
		long[] l = new long[(int)size()];
		int i = 0;
		for (long p : this) {
			l[i++] = p;
		}
		return l;
	}
	
	@Override
	public String toString() {
		return bs.toString();
	}

	public String toString(Map<Long, Object> names) {
		return bs.toString(names);
	}
	
	/**
	 * For this (the immutable) implementation of the CompactLongSet, once constructed, no values can be added or removed,
	 * so we only ever have to compute the hash code once. Thus, this method may just return the stored code and is thus thea(1).
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object that) {
		boolean result = false;
		if (that instanceof LongSet) {
			ImmutableCompactLongSet other = (ImmutableCompactLongSet) that;
			result = bs.equals(other.bs);
		}
		return result;
	}

	/**
	 * Returns an iterator over the values from this TLongBitArray.
	 */
	@Override
	public Iterator<Long> iterator() {
		return bs.iterator();
	}
	
	public static void main(String[] args) {
		runUnitTests(args);
	}
	
	/**
	 * Thorough testing for construction, adding elements, removing elements, and doing bitwise/binary operations.
	 */
	public static void runUnitTests(String[] args) {

		simpleIterationTest();

		int numTestCycles = 0;
		if (args.length > 0) {
			numTestCycles = Integer.valueOf(args[0]);
		} else {
			throw new java.lang.IllegalArgumentException("you must indicate the number of test cycles to perform");
		}

		// run the tests
		Random r = new Random();
		boolean allTestsPassed = false;
		for (int i = 0; i < numTestCycles; i++) {
			System.out.println("\nTest cycle " + i);
			allTestsPassed  = runUnitTests(r.nextInt(Integer.MAX_VALUE));
		}
		
		if (allTestsPassed) {
			System.out.println("\nAll tests passed\n");
		} else {
			System.out.println("\nTests failed\n");
		}
	}
	
	private static boolean simpleIterationTest() {
		long[] a = new long[] {0, 3, 8, 23, 44, 32768, 65536, 2000000, Long.MAX_VALUE};
		MutableCompactLongSet b = new MutableCompactLongSet(a);
		
		for (long l : a) {
			System.out.println("underlying bs contains " + l + "? " + b.bs.get(l));
		}
		
		System.out.print(Arrays.toString(a) + " should be reflected in iteration: ");
		System.out.println(b.toString());
		return true;
	}
	
	private static boolean runUnitTests(int randSeed) {
//			long maxVal = Long.MAX_VALUE;
		Random r = new Random(randSeed);

		// create a random test arrays of ints
		int n1 = r.nextInt(20);
		long[] arr1 = new long[n1];
		for (int i = 0; i < arr1.length; i++) {
			arr1[i] = Math.abs(r.nextLong());
		}
		
		System.out.println("\nThe first array is: " + Arrays.toString(arr1));
		LongSet test1;
		LongSet test2;
		
		// instantiating a BitArray from a TLongArrayList
		System.out.println("Testing BitArray construction from TLongArrayList");
		TLongArrayList testTL = new TLongArrayList();
		for (long i : arr1) {
			testTL.add(i);
		}
		System.out.println("the TLongArrayList contains " + testTL.size() + " values: " + Arrays.toString(testTL.toArray()));
		test1 = new ImmutableCompactLongSet(testTL);
		System.out.println("The Bitset constructed from the TLongArrayList contains: " + test1);
		Arrays.sort(arr1); // has to be on because testInternalState calls sort the bitarray
		HashSet<Long> uniqueInts = new HashSet<Long>();
		for (int k = 0; k < arr1.length; k++) {
			uniqueInts.add(arr1[k]);
			if (! test1.contains(arr1[k])) {
				throw new java.lang.AssertionError("Bitset creation from TLongArrayList failed");
			}
		}
		if (! (test1.size() == uniqueInts.size())) {
			throw new java.lang.AssertionError("Bitset creation from long array failed");
		}
		System.out.println("Bitset construction from TLongArrayList passed\n");

		// test instantiating a BitArray from another BitArray
		System.out.println("Testing FastBitSet construction from another FastBitSet");
		test2 = new ImmutableCompactLongSet(arr1);
		System.out.println("The starting Bitset contains " + test2.size() + " values: " + test2);
		test1 = new ImmutableCompactLongSet(test2);
		System.out.println("The Bitset constructed from the starting BitArray contains: " + test1);
		uniqueInts = new HashSet<Long>();
		for (int k = 0; k < arr1.length; k++) {
			uniqueInts.add(arr1[k]);
			if (! test1.contains(arr1[k])) {
				throw new java.lang.AssertionError("FastBitset creation from FastBitset failed");
			}
		}
		if (! (test1.size() == uniqueInts.size())) {
			throw new java.lang.AssertionError("Bitset creation from long array failed");
		}
		System.out.println("Bitset construction from Bitset passed\n");

		// instantiating a BitArray from a long array
		System.out.println("Testing Bitset construction from long array primitive");
		long[] testArrLong = new long[r.nextInt(20)];
		for (int k = 0; k < testArrLong.length; k++) {
			testArrLong[k] = Math.abs(r.nextLong());
		}
		System.out.println("The long array contains " + testArrLong.length + " values: " + Arrays.toString(testArrLong));
		test1 = new ImmutableCompactLongSet(testArrLong);
		System.out.println("The Bitset constructed from the TLongArrayList contains: " + test1);
		Arrays.sort(testArrLong); // has to be on because testInternalState calls sort the bitarray
		HashSet<Long> uniqueLongs = new HashSet<Long>();
		for (int k = 0; k < testArrLong.length; k++) {
			uniqueLongs.add(testArrLong[k]);
			if (! test1.contains(testArrLong[k])) {
				throw new java.lang.AssertionError("Bitset creation from long array failed");
			}
		}
		if (! (test1.size() == uniqueLongs.size())) {
			throw new java.lang.AssertionError("Bitset creation from long array failed");
		}
		System.out.println("Bitset construction from long array primitive passed\n");
		
		// instantiating a BitArray from a long array
		System.out.println("Testing Bitset construction from int array primitive");
		testArrLong = new long[r.nextInt(20)];
		for (int k = 0; k < testArrLong.length; k++) {
			testArrLong[k] = Math.abs(r.nextLong());
		}
		System.out.println("The int array contains " + testArrLong.length + " values: " + Arrays.toString(testArrLong));
		test1 = new ImmutableCompactLongSet(testArrLong);
		System.out.println("The BitArray constructed from the TLongArrayList contains: " + test1);
		Arrays.sort(testArrLong); // has to be on because testInternalState calls sort the bitarray
		uniqueLongs = new HashSet<Long>();
		for (int k = 0; k < testArrLong.length; k++) {
			uniqueLongs.add(testArrLong[k]);
			if (! test1.contains(testArrLong[k])) {
				throw new java.lang.AssertionError("Bitset creation from int array failed");
			}
		}
		if (! (test1.size() == uniqueLongs.size())) {
			throw new java.lang.AssertionError("Bitset creation from long array failed");
		}
		System.out.println("Bitset construction from int array primitive passed\n");
		
		/*
		// creating arrays to test intersection
		maxVal = 10;
		n1 = r.nextInt(20);
		arr1 = new int[n1];
		for (int i = 0; i < arr1.length; i++) {
			arr1[i] = r.nextInt(maxVal);
		}
		int n2 = r.nextInt(20);
		int[] arr2 = new int[n2];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = r.nextInt(maxVal);
		}

		// make a new BitSet with arr1 values
		BitSet testBS1 = new BitSet();
		for (int i : arr1) {
			testBS1.set(i, true);
		}
		
		// make a new BitSet with arr2 values
		BitSet testBS2 = new BitSet();
		for (int i : arr2) {
			testBS2.set(i, true);
		}
		
		// testing intersection
		System.out.println("Testing intersection with BitSet. Finding intersection of:");
		System.out.println(Arrays.toString(arr1));
		System.out.println(Arrays.toString(arr2));
		testBS1.and(testBS2);
		int[] bsVals = new int[testBS1.cardinality()];
		j = 0;
		for (int k = testBS1.nextSetBit(0); k >= 0; k = testBS1.nextSetBit(k+1)) {
			bsVals[j++] = k;
		}
		System.out.println("Intersection should be: " + Arrays.toString(bsVals));
		
		test1 = new NewBitSet(arr1);
		System.out.println("Intersecting BitArray: " + Arrays.toString(test1.toArray()));
		System.out.println("with BitSet containing: " + Arrays.toString(arr2));

		// making a new BitSet with arr2 values
		testBS2 = new BitSet();
		for (int i : arr2) {
			testBS2.set(i, true);
		}
		NewBitSet intersection = test1.getIntersection(testBS2);

		System.out.println("Intersection is: " + Arrays.toString(intersection.toArray()));
		for (Long l : intersection) {
			if (testBS1.get(l.intValue()) != true) {
				throw new java.lang.AssertionError("Intersection failed: value " + l + " is present but should not be.");
			}
		}
		for (int k = testBS1.nextSetBit(0); k >= 0; k = testBS1.nextSetBit(k+1)) {
			if (intersection.contains(k) != true) {
				throw new java.lang.AssertionError("Intersection failed: value " + k + " should be present but is not.");
			}
		}
		System.out.println("Intersection with BitSet passed\n");

		System.out.println("Testing intersection with BitArray:");
		test2 = new NewBitSet(arr2);
		intersection = test1.getIntersection(test2);
		System.out.println("Intersection is: " + Arrays.toString(intersection.toArray()));
		for (Long l : intersection) {
			if (testBS1.get(l.intValue()) != true) {
				throw new java.lang.AssertionError("Intersection failed: value " + l + " is present but should not be.");
			}
		}
		for (int k = testBS1.nextSetBit(0); k >= 0; k = testBS1.nextSetBit(k+1)) {
			if (intersection.contains(k) != true) {
				throw new java.lang.AssertionError("Intersection failed: value " + k + " should be present but is not.");
			}
		}
		System.out.println("Intersection with BitArray passed\n");

		// replace testBS1
		testBS1 = new BitSet();
		for (int i : arr1) {
			testBS1.set(i, true);
		}
		*/
		
		// creating arrays to test contains all
//			maxVal = 10;
		n1 = r.nextInt(20);
		int[] arr3 = new int[n1];
		for (int i = 0; i < arr3.length; i++) {
			arr3[i] = Math.abs(r.nextInt(1000000));
		}
		int n2 = r.nextInt(20);
		int[] arr2 = new int[n2];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = Math.abs(r.nextInt(1000000));
		}

		// populate bitsets with array values
		BitSet testBS1 = new BitSet();
		for (int i : arr3) { testBS1.set(i-1, true); }
		BitSet testBS2 = new BitSet();
		for (int i : arr2) { testBS2.set(i-1, true); }
		test1 = new ImmutableCompactLongSet(arr3);
		test2 = new ImmutableCompactLongSet(arr2);
		
		System.out.println("Testing containsAll");
		System.out.println("BitSet 1 contains: " + test1);
		System.out.println("BitSet 2 contains: " + test2);
		
		boolean containsAll1;
		boolean bsContainsAll1;
		int cardinalityBeforeAnd;
		if (test2.size() > 0) {
			containsAll1 = test1.containsAll(test2);
			System.out.println("FastBitSet 1 contains all of FastBitSet 2? " + containsAll1);
			cardinalityBeforeAnd = testBS2.cardinality();
			testBS2.and(testBS1);
			bsContainsAll1 = cardinalityBeforeAnd == testBS2.cardinality();
			System.out.println("test BitSet 1 contains all of test BitSet 2? " + bsContainsAll1);
			if (bsContainsAll1 != containsAll1) {
				throw new AssertionError("Contains all failed.");
			}
			System.out.println("Passed contains all test 1");
		} else {
			try {
				containsAll1 = test1.containsAll(test2);
			} catch (NoSuchElementException ex) {
				System.out.println(ex.getMessage() + " (NoSuchElementException thrown correctly)");
			}
		}
			
		// replace testBS2
		testBS2 = new BitSet();
		for (int i : arr2) {
			testBS2.set(i, true);
		}
		
		boolean containsAll2;
		boolean bsContainsAll2;
		if (test1.size() > 0) {
			containsAll2 = test2.containsAll(test1);
			System.out.println("BitSet 2 contains all of BitSet 1? " + test2.containsAll(test1));
			cardinalityBeforeAnd = testBS1.cardinality();
			testBS1.and(testBS2);
			bsContainsAll2 = cardinalityBeforeAnd == testBS1.cardinality();
			System.out.println("test BitSet 1 contains all of test BitSet 2? " + bsContainsAll2);
			if (bsContainsAll2 != containsAll2) {
				throw new AssertionError("Contains all failed.");
			}		
			System.out.println("Passed contains all test 2");
		} else {
			try {
				containsAll1 = test2.containsAll(test1);
			} catch (NoSuchElementException ex) {
				System.out.println(ex.getMessage() + " (NoSuchElementException thrown correctly)");
			}
		}
		
		System.out.println("Passed contains all\n");
		
		// creating arrays to test containsAny
//			maxVal = 10;
		n1 = r.nextInt(1000);
		arr3 = new int[n1];
		for (int i = 0; i < arr3.length; i++) {
			arr3[i] = Math.abs(r.nextInt(1000000));
		}
		n2 = r.nextInt(1000);
		arr2 = new int[n2];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = Math.abs(r.nextInt(1000000));
		}
		
		// replace testBS1
		testBS1 = new BitSet();
		for (int i : arr3) {
			testBS1.set(i, true);
		}
		
		// making a new BitSet with arr2 values
		testBS2 = new BitSet();
		for (int i : arr2) {
			testBS2.set(i, true);
		}

		System.out.println("Testing contains any");
		test1 = new ImmutableCompactLongSet(arr3);
		test2 = new ImmutableCompactLongSet(arr2);
		System.out.println("BitSet 1 contains: " + test1);
		System.out.println("BitSet 2 contains: " + test2);

		boolean containsAny1 = false;
		boolean bsContainsAny1 = false;
		if (test2.size() > 0) {
			containsAny1 = test1.containsAny(test2);
			System.out.println("FastBitSet 1 contains any of FastBitSet 2? " + containsAny1);
			if (containsAny1) {
				System.out.println("yes");
//					System.out.println("BitArray 1 and BitArray 2 both contain: " + Arrays.toString(test1.getIntersection(test2).toArray()));
			} else {
				System.out.println("No overlap");
			}
			bsContainsAny1 = testBS1.intersects(testBS2);
			System.out.println("test BitSet 1 contains any of test BitSet 2? " + testBS1.intersects(testBS2));
			if (bsContainsAny1 != containsAny1) {
				throw new AssertionError("Contains any failed.");
			}		
			System.out.println("Passed contains any test 1");

		} else {
			try {
				containsAny1 = test1.containsAny(test2);
			} catch (NoSuchElementException ex) {
				System.out.println(ex.getMessage() + " (NoSuchElementException thrown correctly)");
			}
		}
		
		boolean containsAny2 = false;
		boolean bsContainsAny2 = false;
		if (test1.size() > 0) {
			containsAny2 = test2.containsAny(test1);
			System.out.println("BitSet 2 contains any of BitSet 1? " + containsAny2);
			if (containsAny1) {
				System.out.println("no");
//					System.out.println("BitArray 1 and BitArray 2 both contain: " + Arrays.toString(test1.getIntersection(test2).toArray()));
			} else {
				System.out.println("No overlap");
			}
			bsContainsAny2 = testBS2.intersects(testBS1);
			System.out.println("test BitSet 2 contains any of test BitSet 1? " + testBS2.intersects(testBS1));
			if (bsContainsAny2 != containsAny2) {
				throw new AssertionError("Contains any failed.");
			}		
			System.out.println("Passed contains any test 2");

		} else {
			try {
				containsAny2 = test2.containsAny(test1);
			} catch (NoSuchElementException ex) {
				System.out.println(ex.getMessage() + " (NoSuchElementException thrown correctly)");
			}
			containsAny2 = containsAny1;
			bsContainsAny2 = containsAny1;
		}

		if ((containsAny1 == containsAny2 == bsContainsAny1 == bsContainsAny2) == false) {
			throw new AssertionError("Contains any failed. If either BitSet  contained any of the other, then all the contains any tests should have been true.");
		} else {
			System.out.println("Passed contains any\n");
		}
		
		/*
		System.out.println("Testing sequential add and remove");
		maxVal = 50;
		maxOps = 100;
		int nOps1 = r.nextInt(maxOps);
		int nOps2 = maxOps - nOps1;
		test1 = new NewBitSet();
		test2 = new NewBitSet();
		
		int nAddOps = 0;
		int nRemoveOps = 0;
		int nSkippedOps = 0;
		System.out.println("Will attempt to perform " + nOps1 + " operations on BitArray 1");
		for (int i = 0; i < nOps1; i++) {
			int nextInt = r.nextInt(maxVal);
			if (r.nextBoolean()) {
				test1.add(nextInt);
				nAddOps++;
			} else {
				if (test1.contains(nextInt)) {
					test1.remove(nextInt);
					nRemoveOps++;
				} else {
					nSkippedOps++;
				}
			}
		}
		test1.updateBitSet();
		System.out.println("Performed " + nAddOps + " add operations, " + nRemoveOps + " remove operations, and skipped " + nSkippedOps + " operations on BitArray 1");

		nAddOps = 0;
		nRemoveOps = 0;
		nSkippedOps = 0;
		System.out.println("Will attempt to perform " + nOps2 + " operations on BitArray 2");
		for (int i = 0; i < nOps2; i++) {
			int nextInt = r.nextInt(maxVal);
			if (r.nextBoolean()) {
				test2.add(nextInt);
				nAddOps++;
			} else {
				if (test2.contains(nextInt)) {
					test2.remove(nextInt);
					nRemoveOps++;
				} else {
					nSkippedOps++;
				}
			}
		}
		test1.updateBitSet();
		System.out.println("Performed " + nAddOps + " add operations, " + nRemoveOps + " remove operations, and skipped " + nSkippedOps + " operations on BitArray 2\n");

		System.out.println("BitArray 1: " + Arrays.toString(test1.toArray()));
		System.out.println("BitArray 2: " + Arrays.toString(test2.toArray()) + "\n");

		intersection = test1.getIntersection(test2);
		System.out.println("BitArray 1 and BitArray 2 have " + intersection.size() + " elements in common: " + Arrays.toString(intersection.toArray()));

		// these should always be true
		boolean arr1ContainsAllIntersectionEXPECT = true;
		boolean arr2ContainsAllIntersectionEXPECT = true;
		boolean arr1ContainsAllArr2EXPECT = intersection.size() == test2.cardinality() ? true : false;
		boolean arr2ContainsAllArr1EXPECT = intersection.size() == test1.cardinality() ? true : false;

		// these depend on the situation
		boolean intersectionContainsAnyArr1EXPECT;
		boolean intersectionContainsAnyArr2EXPECT;
		boolean arr1ContainsAnyArr2EXPECT;
		boolean arr2ContainsAnyArr1EXPECT;

		if (intersection.size() > 0) {
			
			intersectionContainsAnyArr1EXPECT = true;
			intersectionContainsAnyArr2EXPECT = true;
			arr1ContainsAnyArr2EXPECT = true;
			arr2ContainsAnyArr1EXPECT = true;
			
		} else { // the intersection was null

			intersectionContainsAnyArr1EXPECT = false;
			intersectionContainsAnyArr2EXPECT = false;
			arr1ContainsAnyArr2EXPECT = false;
			arr2ContainsAnyArr1EXPECT = false;
			
		}

		boolean arr1ContainsAllIntersection = test1.containsAll(intersection);
		System.out.println("Does BitArray 1 contain all the shared values? " + arr1ContainsAllIntersection);
		if (arr1ContainsAllIntersection != arr1ContainsAllIntersectionEXPECT) {
			throw new AssertionError("Contains all failed");
		}

		boolean arr2ContainsAllIntersection = test2.containsAll(intersection);
		System.out.println("Does BitArray 2 contain all the shared values? " + arr2ContainsAllIntersection);
		if (arr2ContainsAllIntersection != arr2ContainsAllIntersectionEXPECT) {
			throw new AssertionError("Contains all failed");
		}
		
		boolean intersectionContainsAnyArr1 = intersection.containsAny(test1);
		System.out.println("Does the intersection contain any of BitArray 1? " + intersectionContainsAnyArr1);
		if (intersectionContainsAnyArr1 != intersectionContainsAnyArr1EXPECT) {
			throw new AssertionError("Contains any failed");
		}
		
		boolean intersectionContainsAnyArr2 = intersection.containsAny(test2);
		System.out.println("Does the intersection contain any of BitArray 2? " + intersectionContainsAnyArr2);
		if (intersectionContainsAnyArr2 != intersectionContainsAnyArr2EXPECT) {
			throw new AssertionError("Contains any failed");
		}
		
		boolean arr1ContainsAnyArr2 = test1.containsAny(test2);
		System.out.println("Does BitArray 1 contain any of BitArray 2? " + arr1ContainsAnyArr2);
		if (arr1ContainsAnyArr2 != arr1ContainsAnyArr2EXPECT) {
			throw new AssertionError("Contains any failed");
		}

		boolean arr2ContainsAnyArr1 = test2.containsAny(test1);
		System.out.println("Does BitArray 2 contain any of BitArray 1? " + arr2ContainsAnyArr1);
		if (arr2ContainsAnyArr1 != arr2ContainsAnyArr1EXPECT) {
			throw new AssertionError("Contains any failed");
		}
		
		boolean arr1ContainsAllArr2 = test1.containsAll(test2);
		System.out.println("Does BitArray 1 contain ALL of BitArray 2? " + arr1ContainsAllArr2);
		if (arr1ContainsAllArr2 != arr1ContainsAllArr2EXPECT) {
			throw new AssertionError("Contains all failed");
		}

		boolean arr2ContainsAllArr1 = test2.containsAll(test1);
		System.out.println("Does BitArray 2 contain ALL of BitArray 1? " + arr2ContainsAllArr1);
		if (arr2ContainsAllArr1 != arr2ContainsAllArr1EXPECT) {
			throw new AssertionError("Contains all failed");
		}
		*/

		return true;
	} 
}
