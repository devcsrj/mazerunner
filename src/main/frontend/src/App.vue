<template>
  <div id="app">
    <Maze></Maze>
  </div>
</template>

<script>
	import Maze from "./components/Maze";

	export default {
		name: 'app',
		components: {
			Maze,
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
    margin-top: 60px;
  }
</style>
