$(document).ready(function(){
    // Load YouTube player
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
});

function onYouTubeIframeAPIReady() {
    $('.youtube .player').each( function() {
        const id = jQuery(this).attr('id');
        const videoId = jQuery(this).parent().attr('data-id');
        new YT.Player(id, {
            videoId: videoId
        });
    });
}
