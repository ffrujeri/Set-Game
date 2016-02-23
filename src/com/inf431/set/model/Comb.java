package com.inf431.set.model;

import java.util.LinkedList;

public class Comb {

	private LinkedList<int[]> allCombinations;
	int n, k;

	public Comb(int n, int k) {
		allCombinations = new LinkedList<int[]>();
		this.n = n;
		this.k = k;
		findCombinations();
	}

	private int bitcount(int n) {
		int i;
		for (i = 0; n > 0; ++i, n &= (n - 1))
			;
		return i;
	}

	private int[] mountCombination(int u) {
		int[] c = new int[k];
		int i = 0;
		for (int l = 0; u > 0; ++l, u >>= 1)
			if ((u & 1) > 0)
				c[i++] = l;
		return c;
	}

	private void findCombinations() {
		for (int u = 0; u < (1 << n); u++)
			if (bitcount(u) == k)
				allCombinations.add(mountCombination(u));
	}

	public LinkedList<int[]> getCombinations() {
		return allCombinations;
	}
}