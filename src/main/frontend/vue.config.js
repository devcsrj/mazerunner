module.exports = {
	devServer: {
		proxy: {
			'^/maze/info': {
				target: 'http://localhost:8080'
			},
			'^/maze/goal': {
				target: 'http://localhost:8080'
			},
			'^/maze/positions': {
				target: 'http://localhost:8080',
				ws: true
			}
		},
		port: 8081
	}
};
