function Wutsi (){
    this.config = {
        backend: {
            trackUrl: 'https://int-com-wutsi-track.herokuapp.com/v1/track'
        }
    };

    this.track = function (event, value){

        const url = this.config.backend.trackUrl;
        const data = {
            time: new Date().getTime(),
            duid: this.cookie('__w_duaid'),
            pid:  this.story_id(),
            event: event,
            page: this.page_name(),
            ua: navigator.userAgent,
            value: (value ? value : null)
        };
        console.log('Track.push to' + url, data);

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
