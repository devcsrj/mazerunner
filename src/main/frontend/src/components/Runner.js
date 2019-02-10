/**
 * The runner, duh
 */
import Dimension from "./Dimension";
import * as Konva from "konva";

export default class Runner {

	_velocity = 1000;
	_dimension = new Dimension(34, 34);
	_variations = 28;
	/**
	 * @type {Konva.Sprite}
	 */
	_shape;
	/**
	 * @type {Promise} chain of animations that are performed
	 * to animate the sprite
	 */
	_animations;

	/**
	 * @type {Konva.Layer}
	 */
	_layer;

	/**
	 *
	 * @param {String} id the runner id
	 * @param {String} name the runner name
	 * @param {Konva.Layer} layer
	 */
	constructor(id, name, layer) {
		this.id = id;
		this.name = name;
		this._layer = layer;
		this._animations = Promise.resolve();

		const vm = this;
		const img = new Image();
		const config = {
			drawBorder: true,
			image: img,
			animation: 'idle',
			animations: {
				idle: [
					// x, y, width, height
					59, 186, 34, 34,
				],
				runLeft: [
					21, 228, 34, 34,
					59, 228, 34, 34,
					97, 228, 34, 34,
				],
				runRight: [
					21, 144, 34, 34,
					59, 144, 34, 34,
					97, 144, 34, 34,
				],
				runUp: [
					21, 102, 34, 34,
					59, 102, 34, 34,
					97, 102, 34, 34,
				],
				runDown: [
					21, 186, 34, 34,
					59, 186, 34, 34,
					97, 186, 34, 34,
				],
				teleport: [
					21, 270, 34, 34,
					59, 270, 34, 34,
					97, 270, 34, 34,
					135, 270, 34, 34,
					173, 270, 34, 34,
					211, 270, 34, 34,
					249, 270, 34, 34,
					287, 270, 34, 34,
					325, 270, 34, 34,
					363, 270, 34, 34,
				],
				dying: [
					21, 354, 34, 34,
					59, 354, 34, 34,
					97, 354, 34, 34,
					135, 354, 34, 34,
				]
			},
			frameRate: 10,
			frameIndex: 0,
			height: 34,
			width: 34
		};
		img.onload = function () {
			vm._shape = new Konva.Sprite(config);
			vm._layer.add(vm._shape);
			vm._shape.start();
		};
		img.src = this._imageUrl();
	}

	/**
	 * @return {string} the image url to use
	 */
	_imageUrl() {
		let num = Math.floor((Math.random() * this._variations) + 1);
		return '/characters/CHAR' + num + '.PNG';
	}

	/**
	 *
	 * @param {Room} room
	 */
	moveTo(room) {
		if (!this._shape) {
			return;
		}

		const dest = room.center(this._dimension);
		const vm = this;
		vm._animations = vm._animations.then(function () {
			return new Promise((resolve) => {
				const anim = new Konva.Animation(function (frame) {
					const dx = dest.x - vm._shape.x();
					const dy = dest.y - vm._shape.y();

					let tooFast = false;
					let dist = vm._velocity * (frame.time / 1000);
					if (dx > room.size().width * 2 || dy > room.size().height * 2) {
						dist *= 50; // faster
						tooFast = true;
					}

					let mx = dx < 0 ? Math.max(-dist, dx) : Math.min(dist, dx);
					let my = dy < 0 ? Math.max(-dist, dy) : Math.min(dist, dy);

					if (Math.round(dx) === 0 && Math.round(dy) === 0) {
						anim.stop();
						vm._shape.animation('idle');
						resolve(vm); // we're done
					} else if (tooFast) {
						vm._shape.animation('teleport');
					} else {
						// vm._shape.offsetX(vm._shape.width() / 2);
						if (mx !== 0) {
							vm._shape.animation(mx < 0 ? 'runLeft' : 'runRight');
						}
						if (my !== 0) {
							vm._shape.animation(my < 0 ? 'runUp' : 'runDown');
						}
					}

					vm._shape.move({
						x: mx,
						y: my
					});
					return true;
				}, vm._layer);
				anim.start();
			});
		});
	}

	/**
	 * End of life for this runner
	 */
	giveUp() {
		const vm = this;
		vm._animations = vm._animations.then(function () {
			return new Promise((resolve) => {
				vm._shape.animation('dying');
				setTimeout(function () {
					vm._layer.removeChildren(vm._shape);
					resolve(vm);
				}, 400); // FIXME compute timeout
			});
		});
	}

	/**
	 * Winning stance for this runner
	 */
	celebrate() {
		// TODO
	}

	toString() {
		return "Runner[" + this.id + ":" + this.name + "]";
	}
}
