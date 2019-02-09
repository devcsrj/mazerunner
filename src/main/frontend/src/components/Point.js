export default class Point {
	/**
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	constructor(x, y) {
		this.x = x;
		this.y = y;
	}

	toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
}
