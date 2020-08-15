function WutsiComment (storyId){
    this.storyId = storyId;
    this.visible = false;
    this.config = {
        selectors: {
            count: '#comment-count',
            container: '#comment-widget'
        }
    };

    this.load = function() {
        console.log('Loading comments', storyId);

        this.load_text();

        const me = this;
        $(document).mouseup(function(e) {
            if (!me.visible) {
                return;
            }

            const selector = me.config.selectors.container;
            var container = $(selector);
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                me.hide();
            }
        });
    };

    this.load_text = function() {
        const selector = this.config.selectors.count;
        wutsi.httpGet('/comment/count?storyId=' + storyId, true)
            .then(function (count){
                console.log('Comment count', count);
                $(selector).text(count.text);
            })
            .catch(function(error){
                $(selector).text('');
            });
    };

    this.show = function () {
        console.log('Showing comments...');

        const selector = this.config.selectors.container;
        const me = this;
        wutsi.httpGet('/comment/widget?storyId=' + storyId, false)
            .then(function(html){
                $(selector).show();
                $(selector).html(html);
                me.visible = true;
            });
    }

    this.hide = function() {
        console.log('Hiding comments...');

        const selector = this.config.selectors.container;
        $(selector).hide();
        this.visible = false;
        this.load_text();
    }

}
