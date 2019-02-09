export default class Dimension {
	/**
	 *
	 * @param height
	 * @param width
	 */
	constructor(height, width) {
		this.height = height;
		this.width = width;
	}

	toString() {
		return "h=" + this.height + ", w=" + this.width + "";
	}
}
