export default class Movement {

	/**
	 * @param runnerId the runner id
	 * @param runnerName the runner name
	 * @param type the type of movement
	 * @param point the new point
	 */
	constructor(runnerId, runnerName, type, point) {
		this.runnerId = runnerId;
		this.runnerName = runnerName;
		this.type = type;
		this.point = point;
	}
}
