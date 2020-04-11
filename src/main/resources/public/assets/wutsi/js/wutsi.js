function Wutsi (){
    this.track = function (event, value){
        const data = {
            time: new Date().getTime(),
            pid:  this.story_id(),
            event: event,
            page: this.page_name(),
            ua: navigator.userAgent,
            value: (value ? value : null)
        };

        return this.httpPost('/track', data, true);
    };

    this.httpGet = function(url, json) {
        return $.ajax({
            method: 'GET',
            url: url,
            dataType: json ? 'json' : null,
            contentType: json ? 'application/json': null,
            headers: {
                'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
            },
            success: function(data) {
                console.log('GET ', url, data);
                resolve(data)
            },
            error: function(error) {
                console.error('GET ', url, error);
                reject(error)
            }
        })

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
                success: function(data) {
                    console.log('POST ', url, data);
                    resolve(data)
                },
                error: function(error) {
                    console.error('POST ', url, error);
                    reject(error)
                }
            })
        });
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
}

var wutsi = new Wutsi();

function wutsi_bind_tracking () {
    $('[wutsi-track-event]').click(function(){
        var event = $(this).attr("wutsi-track-event");
        var value = $(this).attr("wutsi-track-value");
        var productId = $(this).attr("wutsi-track-product-id");
        wutsi.track(event, value, productId)
    });
}
