function WutsiComment (storyId, anonymous, storyUrl){
    this.storyId = storyId;
    this.visible = false;
    this.config = {
        selectors: {
            count: '#comment-count',
            widget: '#comment-widget',
            text: '#comment-text',
            list: '#comment-list-container',
            editor: '#comment-editor'
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

            const selector = me.config.selectors.widget;
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

    this.load_items = function() {
        const selector = this.config.selectors.list;
        wutsi.httpGet('/comment/list?storyId=' + this.storyId, false)
            .then(function(html){
                $(selector).html(html);
            });
    };

    this.show = function () {
        console.log('Showing comments...');

        const selector = this.config.selectors.widget;
        const me = this;
        wutsi.httpGet('/comment/widget?storyId=' + storyId, false)
            .then(function(html){
                $(selector).show();
                $(selector).html(html);
                me.visible = true;
            });
    };

    this.hide = function() {
        console.log('Hiding comments...');

        const selector = this.config.selectors.widget;
        $(selector).hide();
        this.visible = false;
        this.load_text();
    };

    this.contentReady = function(){
        const textarea = this.config.selectors.editor + ' textarea';
        const button = this.config.selectors.editor + ' button';
        const close = this.config.selectors.widget + ' .close';
        const me = this;

        this.end_edit();
        this.load_items();

        $(textarea).click(function(){
            if ($(this).is('[readonly]')) {
                me.begin_edit();
            }
        });

        $(button).click(function(){
            me.submit($(textarea).val());
        });

        $(close).click(function(){
            me.hide();
        });
    };

    this.begin_edit = function() {
        console.log('Begin comment edition');

        if (anonymous) {
            window.location.href = '/login?redirect=' + encodeURI(storyUrl + '?comment=1');
        } else {
            const textarea = this.config.selectors.editor + ' textarea';
            $(textarea).removeAttr('readonly');
            $(textarea).attr('rows', '3');
            $(textarea).val('');
            $(textarea).focus();

            const submit = this.config.selectors.editor + ' button.submit';
            $(submit).show();
        }
    };

    this.end_edit = function () {
        console.log('Stop comment edition');

        const textarea = this.config.selectors.editor + ' textarea';
        $(textarea).attr('readonly', 'readonly');
        $(textarea).attr('rows', '1');
        $(textarea).val('');

        const submit = this.config.selectors.editor + ' button.submit';
        $(submit).hide();
    };

    this.submit = function(text) {
        console.log('Submitting comment', text);
        const data = {
            storyId: this.storyId,
            text: text
        };
        const me = this;
        wutsi.httpPost('/comment', data, true)
            .then(function(){
                me.load_items();
            })
            .catch(function(error){
                console.log('Unable to create comment', error);
            })
            .finally(function(){
                me.end_edit();
            });
    }
}
