function Wutsi (){
    this.track = function (event, value, label){
        this.track_ga(wutsi.page_name(), event, value, label);
        return this.track_wutsi(event, value);
    };

    this.track_ga = function(category, event, value, label) {
        if (typeof gtag != 'function') {
            return
        }

        try {
            gtag('event', event, {
                'event_category': category,
                'event_label': label,
                'value': (value ? value : null)
            });
        } catch (err) {
            console.error('Unable to push event to Google Analytics', err);
        }
    };


    this.track_wutsi = function(event, value) {
        const page = this.page_name();
        if (page != 'page.read') {  // Only push to wutsi story events
            return
        }

        const data = {
            time: new Date().getTime(),
            pid:  this.story_id(),
            event: event,
            page: page,
            ua: navigator.userAgent,
            value: (value ? value : null),
            hid: this.hit_id(),
            url: window.location.href
        };
        return this.httpPost('/track', data, true)
            .catch(function(){
                const key = 'track.' + data.time;
                const value = JSON.stringify(data);
                console.log('Adding into LocalStorage', key, value);
                localStorage.setItem(key, value);
            });
    };

    this.track_wutsi_job = function(){
        // console.log('Running the track Job');

        for (var i = 0; i < localStorage.length; i++){
            const key = localStorage.key(i);
            if (key.startsWith('track.')){
                try {
                    const data = JSON.parse(localStorage.getItem(key));
                    console.log('Pushing stored tracking event', key, data);
                    this.httpPost('/track', data, true)
                        .then (function(){
                            console.log('track-event', key, ' sent');
                            console.log('Removing from LocalStorage', key);
                            localStorage.removeItem(key);
                        })
                } catch (err) {
                    console.error(key, err);
                }
            }
        }
    };

    this.domReady = function () {
        /* popover */
        $('[data-toggle="popover"]').popover({
            trigger: 'hover'
        });

        /* lozad */
        const observer = lozad();
        observer.observe();

        /* comments */
        this.update_comment_count();

        /* tracking */
        $('[wutsi-track-event]').click(function(){
            const event = $(this).attr("wutsi-track-event");
            const value = $(this).attr("wutsi-track-value");
            const title = $(this).attr("title");
            wutsi.track(event, value, title);

            const rank = $(this).attr("wutsi-track-rank");
            if (rank) {
                wutsi.track_ga(wutsi.page_name(), event + '.' + rank, rank, title);
            }
        });
    };

    this.update_comment_count = function() {
        var qs = '';
        $('[data-comment-story-id]').each(function(index, item){
            if (qs.length > 0){
                qs += '&'
            }
            qs += 'storyId=' + $(item).attr('data-comment-story-id');
        });
        if (qs.length > 0) {
            const url = '/comment/count?' + qs;
            this.httpGet(url, true)
                .then(function (counts) {
                    $(counts).each(function(index, item){
                        $('#comment-count-' + item.storyId).text(item.valueText);
                        $('#comment-text-' + item.storyId).text(item.text);
                    });
                });
        }
    }

    this.isMobile = function () {
        const ua = navigator.userAgent;
        return /iPhone|iPad|iPod|Android/i.test(ua)
            || ((ua.indexOf("FBAN") > -1) || (ua.indexOf("FBAV") > -1))  /* Facebook in-app browser */
        ;
    };

    this.httpGet = function(url, json) {
        return new Promise(function(resolve, reject){
            $.ajax({
                method: 'GET',
                url: url,
                dataType: json ? 'json' : null,
                contentType: json ? 'application/json': null,
                headers: {
                    'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
                },
                success: function(data) {
                    console.log('GET ', url, json ? data : '');
                    resolve(data)
                },
                error: function(error) {
                    console.error('GET ', url, error);
                    reject(error)
                }
            })
        });

    };

    this.httpPost = function(url, data, json) {
        return new Promise(function(resolve, reject) {
            $.ajax({
                url: url,
                type: 'POST',
                data: json ? JSON.stringify(data) : data,
                dataType: json ? 'json' : null,
                contentType: json ? 'application/json': false,
                cache: false,
                processData: false,
                headers: {
                    'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
                },
                success: function(response) {
                    console.log('POST ', url, data, response);
                    resolve(response)
                },
                error: function(error) {
                    console.error('POST ', url, data, error);
                    reject(error)
                }
            })
        });
    };

    this.upload = function(file) {
        console.log('Uploading ', file);

        const form = new FormData();
        form.append('file', file);
        return wutsi.httpPost('/upload', form);
    };


    this.cookie = function (name) {
        var match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        if (match) return match[2];
    };

    this.page_name = function () {
        var meta = document.head.querySelector("[name=wutsi\\:page_name]");
        return meta ? meta.content : null
    };

    this.story_id = function () {
        var meta = document.head.querySelector("[name=wutsi\\:story_id]");
        return meta ? meta.content : null
    };

    this.hit_id = function () {
        var meta = document.head.querySelector("[name=wutsi\\:hit_id]");
        return meta ? meta.content : null
    };
}

var wutsi = new Wutsi();

// Push stores track events periodically
setInterval(function () {
    wutsi.track_wutsi_job()
}, 30000);

// Handle all errors
window.onerror = function(message, source, line, col, error) {
    const label = source + ' - ' + line + ':' + col + ' ' + message;
    wutsi.track_ga('error', 'error', null, label)
};

