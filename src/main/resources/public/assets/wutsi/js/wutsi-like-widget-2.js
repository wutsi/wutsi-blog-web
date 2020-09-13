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
            wutsi.httpGet('/like?storyId=' + storyId, true)
                .then(function (count) {
                    wutsi.update_like_count();
                })
                .catch(function (error){
                    if(error.status === 200) {
                        var $likeIcon = $('#like-icon-' + storyId);
                        $likeIcon.removeClass('fas fa-heart like-icon like-icon-liked');
                        $likeIcon.attr('class', 'far fa-heart like-icon');
                        wutsi.update_like_count(storyId);
                    }
                })
                .finally(function () {
                    wutsi.track('like');
                });
        }
    };
}
