function WutsiLikeWidget(storyId, anonymous, storyUrl){
    this.visible = false;
    this.config = {
        selectors: {
            text: '.like-text'
        }
    };

    this.like = function() {
        console.log('Like/Unlike the story', storyId);

        if (anonymous) {
            const redirect = storyUrl + '?like=1';
            window.location.href = '/login?reason=like&redirect=' + encodeURI(redirect) + '&return=' + encodeURI(redirect);
        } else {
            wutsi.httpGet('/like?storyId=' + storyId, true)
                .then(function (count) {
                    wutsi.update_like_count();
                })
                .finally(function () {
                    wusti.track('like');
                });
        }
    };
}
