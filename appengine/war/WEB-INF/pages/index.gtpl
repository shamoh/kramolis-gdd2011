<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Welcome</h1>

<p>
Congratulations, you've just created your first
<a href="http://gaelyk.appspot.com">Gaelyk</a> application.
</p>

<p>
Click <a href="/datetime">here</a> to view the current date/time.
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
		width: 250,
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
		width: 250,
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

<% include '/WEB-INF/includes/footer.gtpl' %>

