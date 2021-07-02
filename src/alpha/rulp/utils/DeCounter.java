package alpha.rulp.utils;

// Double expand counter

public class DeCounter {

	private int[] countArray;

	private int maxId;

	private final int size;

	private int totalCount = 0;

	private int unit = 1;

	public DeCounter(int size) {
		super();

		this.size = size;
		this.countArray = new int[size];

		for (int i = 0; i < size; ++i) {
			countArray[i] = 0;
		}

		this.maxId = unit * size;
	}

	public void add(int id) {

		// Need expend?
		while (id >= getMaxId()) {
			expand();
		}

		int idx = id / unit;
		countArray[idx]++;
		++totalCount;
	}

	public void expand() {

		if (totalCount != 0) {

			int[] newCountArray = new int[size];
			for (int i = 0; i < size / 2; ++i) {
				newCountArray[i] = countArray[2 * i] + countArray[2 * i + 1];
			}

			for (int i = size / 2; i < size; ++i) {
				newCountArray[i] = 0;
			}

			this.countArray = newCountArray;
		}

		this.unit *= 2;
		this.maxId *= 2;
	}

	public int getCount(int index) {
		return countArray[index];
	}

	public int getMaxId() {
		return maxId;
	}

	public int getSize() {
		return size;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getUnit() {
		return unit;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(String.format("total=%d,size=%d,unit=%d,values=[", totalCount, size, unit));

		int endIndex = size - 1;
		for (; endIndex >= 0; --endIndex) {
			if (countArray[endIndex] > 0) {
				break;
			}
		}

		for (int i = 0; i <= endIndex; ++i) {

			if (i != 0) {
				sb.append(',');
			}

			sb.append("" + countArray[i]);
		}

		sb.append(']');

		return sb.toString();

	}
}
