const wutsiCacheName = 'wutsi-v2';

self.addEventListener('install', function(event) {
    console.log('Installing service worker');
});



self.addEventListener('activate', function (event) {
    console.log('Activating new service worker...');

    const cacheWhitelist = [wutsiCacheName];
    event.waitUntil(
        caches.keys().then(function (cacheNames) {
            return Promise.all(
                cacheNames.map(function (cacheName) {
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        console.log('Purging cache:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});



self.addEventListener('fetch', function(event)  {
    const url = event.request.url;
    // console.log('Fetching', url);

    event.respondWith(
        caches.match(event.request)
            .then(function(response){
                return response || fetch(event.request)
                        .then(function(response){
                            // console.log(url, 'resolved from network');
                            if (shouldCache(url)){
                                const clone = response.clone();
                                caches.open(wutsiCacheName).then(function(cache){
                                    console.log('Caching', url);
                                    cache.put(url, clone);
                                    return response;
                                });
                            }
                            return response;
                        });
            })
    );
});

function shouldCache(url) {
    return false;
}
