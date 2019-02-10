<template>
  <div id="app">
    <Maze></Maze>
    <Leaderboard></Leaderboard>
  </div>
</template>

<script>
	import Maze from "./components/Maze";
	import Leaderboard from "./components/Leaderboard";

	export default {
		name: 'app',
		components: {
			Leaderboard: Leaderboard,
			Maze: Maze,
		}
	}
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }
</style>
