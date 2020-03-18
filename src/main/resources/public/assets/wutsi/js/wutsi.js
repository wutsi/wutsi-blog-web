function Wutsi (){

    this.trackUrl = 'https://int-com-wutsi-track.herokuapp.com/v1/track';

    this.track = function (event, value, productId){
        console.log('Track.push', event, value, productId);

        var data = {
            time: new Date().getTime(),
            duid: this.cookie('__w_duaid'),
            pid:  (productId ? productId : null),
            event: event,
            page: this.page_name(),
            ua: navigator.userAgent,
            value: (value ? value : null)
        };

        $
            .ajax({
                method: 'POST',
                url: this.trackUrl,
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: 'application/json'
            })
            .fail(function(xhr, textStatus, errorThrown){
                console.log('Failed - ' + this.trackUrl, xhr, textStatus, errorThrown);
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
