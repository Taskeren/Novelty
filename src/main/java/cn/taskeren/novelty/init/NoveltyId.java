package cn.taskeren.novelty.init;

public class NoveltyId {

	/**
	 * The ID offset of Novelty. Default is 17,500.
	 * <p>
	 * Don't modify it unless you know what you are doing!
	 */
	public static int ID_BASE = 17500;

	public static int peek() {
		return ID_BASE;
	}

	public static int take() {
		return ID_BASE++;
	}

	public static void skip(int count) {
		ID_BASE += count;
	}

}
