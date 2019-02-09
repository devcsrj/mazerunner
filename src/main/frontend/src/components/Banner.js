/**
 * A decorative banner
 *
 * Adds four evenly-spaced banners
 */
import * as Konva from "konva";

export default class Banner {

	_imageUrls = [
		'/aesthetics/banner-blue.png',
		'/aesthetics/banner-yellow.png',
		'/aesthetics/banner-green.png',
		'/aesthetics/banner-red.png'
	];

	/**
	 *
	 * @param {Dimension} dimension the dimension per banner
	 * @param {Point} origin the point from where this should be drawn
	 * @param {Konva.Layer} layer
	 */
	constructor(dimension, origin, layer) {
		const vm = this;
		for (let i = 0; i < this._imageUrls.length; i++) {
			const image = new Image();
			image.onload = new function () {
				const shape = new Konva.Image({
					image: image,
					x: origin.x + (dimension.width / 2 * i),
					y: origin.y,
					height: dimension.height,
					width: dimension.width
				});
				layer.add(shape);
			};
			image.src = vm._imageUrls[i];
		}
	}
}
