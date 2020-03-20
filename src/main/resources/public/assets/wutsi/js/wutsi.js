function Wutsi (){
    this.config = {
        backend: {
            trackUrl: 'https://int-com-wutsi-track.herokuapp.com/v1/track'
        },
        editor: {
            autosave: 30000  /* 15 secs */
        }
    };

    this.track = function (event, value, productId){
        console.log('Track.push', event, value, productId);

        const url = this.config.backend.trackUrl;
        const data = {
            time: new Date().getTime(),
            duid: this.cookie('__w_duaid'),
            pid:  (productId ? productId : null),
            event: event,
            page: this.page_name(),
            ua: navigator.userAgent,
            value: (value ? value : null)
        };

        return new Promise(function (resolve, reject) {
            $.ajax({
                method: 'POST',
                url: url,
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: 'application/json',
                success: function (data) {
                    resolve(data)
                },
                error: function (error) {
                    console.log('Failed - ' + url, xhr, textStatus, errorThrown);
                    reject(error)
                }
            });
        });
    };

    this.story = function(id) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                method: 'GET',
                url: '/story/editor/fetch?id=' + id,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data) {
                    resolve(data)
                },
                error: function (error) {
                    reject(error)
                }
            })
        });
    };

    this.save_story = function (story) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                method: 'GET',
                url: '/story/editor/save',
                dataType: 'json',
                contentType: 'application/json',
                data: story,
                success: function (data) {
                    resolve(data)
                },
                error: function (error) {
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
}

var wutsi = new Wutsi();

function wutsi_bind_tracking () {
    console.log('binding controls with track API');
    $('[wutsi-track-event]').click(function(){
        var event = $(this).attr("wutsi-track-event");
        var value = $(this).attr("wutsi-track-value");
        var productId = $(this).attr("wutsi-track-product-id");
        wutsi.track(event, value, productId)
    });
}
