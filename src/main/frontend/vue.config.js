module.exports = {
	devServer: {
		proxy: {
			'^/maze/info': {
				target: 'http://localhost:8999'
			},
			'^/maze/goal': {
				target: 'http://localhost:8999'
			},
			'^/maze/positions': {
				target: 'http://localhost:8999',
				ws: true
			}
		},
		port: 9001
	}
};
