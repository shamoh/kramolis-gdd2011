<% include '/WEB-INF/includes/header.gtpl' %>

<h1>LaPardon</h1>

<p>
This is web page of Libor Kramoliš's GDD 2011 Arduino/ADK project.
</p>

<p>
<script src="http://widgets.twimg.com/j/2/widget.js"></script>
<script>
	new TWTR.Widget({
		version: 2,
		type: 'search',
		search: 'lapardon',
		interval: 30000,
		title: 'User @lapardon',
		subject: 'LaPardon Runtime',
		width: 500,
		height: 300,
		theme: {
			shell: {
				background: '#8ec1da',
				color: '#ffffff'
			},
			tweets: {
				background: '#ffffff',
				color: '#444444',
				links: '#1985b5'
			}
		},
		features: {
			scrollbar: true,
			loop: false,
			live: true,
			hashtags: true,
			timestamp: true,
			avatars: true,
			toptweets: true,
			behavior: 'all'
		}
	}).render().start();
</script>
</p>

<p>
<script src="http://widgets.twimg.com/j/2/widget.js"></script>
<script>
	new TWTR.Widget({
		version: 2,
		type: 'search',
		search: '#lapardon',
		interval: 30000,
		title: 'Tag #lapardon',
		subject: 'LaPardon Requests',
		width: 500,
		height: 300,
		theme: {
			shell: {
				background: '#8edac1',
				color: '#ffffff'
			},
			tweets: {
				background: '#ffffff',
				color: '#444444',
				links: '#19b585'
			}
		},
		features: {
			scrollbar: true,
			loop: false,
			live: true,
			hashtags: true,
			timestamp: true,
			avatars: true,
			toptweets: true,
			behavior: 'all'
		}
	}).render().start();
</script>
</p>

<% include '/WEB-INF/includes/footer.gtpl' %>

