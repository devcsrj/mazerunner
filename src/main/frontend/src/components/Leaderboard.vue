<template>
  <div class="container">
    <ul>
      <img src="//placehold.it/32x32"/>
      <li v-for="score in scores"><span>{{score.tag.name}}</span> <span>{{score.moves}}</span></li>
    </ul>
  </div>
</template>

<script>
	import Vue from 'vue';
	import VueResource from 'vue-resource';

	Vue.use(VueResource);

	export default {
		name: "Leaderboard",
		data: function () {
			return {
				scores: []
			}
		},
		mounted: function () {
			const timeout = 15000; // millis
			const vm = this;
			setInterval(function () {
				vm.$http.get("/maze/scores").then(response => {
					vm.scores = response.body
				});
			}, timeout);

		}
	}
</script>

<style scoped>
  div.container {
    position: fixed;
    bottom: 0;
    left: 0;
    z-index: 1000000000;
  }

  li {
    height: 33px;
    display: inline-block;
  }

  ul {
    display: flex;
    margin: 0;
    padding: 0 0 0 10px;
    border-radius: 3px;
    border-width: 1px;
    border-style: solid;
    overflow: hidden;
  }

  li {
    text-rendering: optimizeLegibility;
    line-height: 33px;
    font-size: 12px;
    font-weight: 300;
    text-decoration: none;
    padding: 0 10px 0 20px;
    position: relative;
  }

  li span {
    margin-left: 5px
  }

  li:nth-child(2) {
    padding-left: 16px;
    color: #fff;
  }

  li:last-child {
    padding-right: 16px;
  }

  li:after,
  li:before {
    content: '';
    display: block;
    width: 0;
    height: 0;
    border-top: 17px solid transparent;
    border-bottom: 17px solid transparent;
    border-left: 10px solid transparent;
    position: absolute;
    margin: auto;
    top: 0;
    bottom: 0;
    left: 100%;
    z-index: 2;
  }

  li:before {
    margin-left: 1px;
    z-index: 1;
  }

  ul {
    border-color: #2E3031;
  }

  li {
    color: #c6c6c6;
    background-color: #595B5B;
    text-shadow: 0 -1px rgba(0, 0, 0, .7);
    box-shadow: inset 0 1px 0 #727373;
  }

  li:after {
    border-left-color: #595B5B;
  }

  li:before {
    border-left-color: #2E3031;
  }

  li:hover {
    background: #646666;
  }

  li:hover:after {
    border-left-color: #646666;
  }

  li:active:before {
    border-left-color: #404141;
  }
</style>
