function WutsiLikeWidget(storyId, anonymous, storyUrl){
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
            wutsi.httpPost('/like?storyId=' + storyId, {}, true)
                .then(function () {
                    wutsi.update_like_count();
                })
                .finally(function () {
                    wutsi.track('like');
                });
        }
    };
}
