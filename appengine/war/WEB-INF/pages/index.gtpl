<% include '/WEB-INF/includes/header.gtpl' %>

<h1>LaPardon</h1>

<table cellpadding="8">
<tr>
    <td valign="top">

        <img src="/images/lapardon-144.png" alt="LaPardon logo"/>

        <p>
            This is web page of Libor Kramoliš's GDD 2011 Arduino/ADK project.
        </p>


        <h2>About</h2>

        <p>
            This is Libor Kramoliš's GDD 2011 Arduino/ADK project. It is small fountain. It reproduces music by water.
            Music is requested via Twitter.
            Tweet has to contain <code>#lapardon</code> tag and music notation placed between brackets <code>[</code>
            and <code>]</code>.
            Supported note characters are '<code>cCdDefFgGabh</code>' and rest (pause) character is '<code>|</code>'.
        </p>


        <h2>Interface</h2>

        <h3>LaPardon Twitter</h3>

        <p>
            Do you want to request LaPardon application to "play" your music? Tweet it. It's easy. Follow next rules:
            <ul>
                <li>Use tag <code>#lapardon</code> - the tag is searched by Android application</li>
                <li>Music notation is placed between brackets <code>[</code> and <code>]</code> - any other text outside
                    is ignored
                </li>
                <li>Supported note characters are '<code>cCdDefFgGabh</code>' - while upper case chars means sharp
                    variant (Cis, Dis, Fis, Gis)
                </li>
                <li>Rest (pause) character is '<code>|</code>'</li>
            </ul>
            It is not possible to change note or rest durations. There is just one fix duration for all notes. Keep in
            mind. ;-)
        </p>

        <h4>Examples</h4>

        <ul>
            <li><code>One octave [cCdDefFgGabh] #lapardon</code></li>
            <li><code>Up and down [cCdDefFgGabh | hbaGgFfeDdCc] #lapardon</code></li>
        </ul>

        <h4>Twitter Widget</h4>

        <p>
            On the right side of this page, you can see two Twitter Widgets:
            <ul>
                <li><b>LaPardon Runtime</b> - LaPardon application tweets about it's runtime, what is going on</li>
                <li><b>LaPardon Requests</b> - your LaPardon playing requests with tag <code>#lapardon</code></li>
            </ul>
        </p>


        <h3>LaPardon House</h3>

        Just touch big <b>G</b> on the house and first playing request from a queue is processed. ;-)
        See green LED that blinks during playing.

        <h2>Download</h2>

        <h3>Sources</h3>

        <p>
            Sources for Android, Arduino and also for AppEngine (AAA sources ;-) ) are available on GitHub -
            <a href="https://github.com/shamoh/kramolis-gdd2011">https://github.com/shamoh/kramolis-gdd2011</a>.
        </p>

        <h3>Binary</h3>

        <p>
            Build of Android application is available <a href="download/lapardon.apk">here</a>.
        </p>

        <h2>Apology</h2>

        <p>
            Excuse me. I'm not a musician. I'm not Android developer. I'm neither Arduino developer.
        </p>

        <p>
            But I love technologies and this was just a challenge! ( Maybe too big. :-o )
        </p>


        <h2>Authors</h2>

        <h3>Libor Kramoliš</h3>
        Author of whole project.
        <br/><a href="https://plus.google.com/115270016494231681069/about">https://plus.google.com/115270016494231681069/about</a>

        <h3>Ondřej Košatka</h3>
        Android project setup, Twitter communication implemented. Moral and technical support.
        <br/><a href="https://plus.google.com/117246369712480977490/about">https://plus.google.com/117246369712480977490/about</a>

        <h3>Petr Blažek</h3>
        We both got ADK to create GDD project. Moral and technical support.
        <br/><a href="https://plus.google.com/100342760152037874082/about">https://plus.google.com/100342760152037874082/about</a>

        <h3>Martin Mareš</h3>
        Author of idea to connect water pump with ADK. Thank you, really thanks. ;-)
        <br/><a href="https://plus.google.com/117017068727290273829/about">https://plus.google.com/117017068727290273829/about</a>

        <h3>My Family</h3>
        This project is dedicated to my family because they lost my time I spend on LaPardon activity. They also created
        beautiful LaPardon house.
    </td>


    <td valign="top">
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
                    width: 350,
                    height: 350,
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
                    width: 350,
                    height: 350,
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

        <img src="/images/qr-gdd2011_kramolis_cz-M.png" align="right" alt="LaPardon Web URL QR code"/>
    </td>
</tr>

<% include '/WEB-INF/includes/footer.gtpl' %>

