function Wutsi (){
    this.track = function (event, value){
        const data = {
            time: new Date().getTime(),
            pid:  this.story_id(),
            event: event,
            page: this.page_name(),
            ua: navigator.userAgent,
            value: (value ? value : null),
            hid: this.hit_id()
        };

        return this.httpPost('/track', data, true);
    };

    this.domReady = function () {
        $('[wutsi-track-event]').click(function(){
            var event = $(this).attr("wutsi-track-event");
            var value = $(this).attr("wutsi-track-value");
            wutsi.track(event, value)
        });

        $('img[async-src]').each(function() {
            $(this).attr("src", $(this).attr("async-src"));
        });
    };


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
