const wutsiCacheName = 'wutsi-cache';

self.addEventListener('install', function(event) {
    console.log('Installing service worker', event);

    const cacheWhitelist = [wutsiCacheName];
    event.waitUntil(
        caches.keys().then(function (cacheNames) {
            return Promise.all(
                cacheNames.map(function (cacheName) {
                    if (cacheWhitelist.indexOf(cacheName) >= 0) {
                        console.log('Purging cache:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});

self.addEventListener('activate', function (event) {
    console.log('Activating new service worker...');
});

self.addEventListener('push', function(event) {
    console.log('Push Notification received', event);

    const data = event.data.json().data;
    console.log('data', data);
    sw_show_notification(data);
});

self.addEventListener('notificationclick', function(event) {
    console.log('notificationclick', event);

    const data = event.notification.data;
    console.log('data', data);

    event.notification.close();
    clients.openWindow(data.url + '?utm_source=push&utm_medium=pwa');
});

self.addEventListener('fetch', function(event)  {
    // console.log('Fetching', event.request.method, event.request.url);

    sw_fetch_from_cache(event).catch(function(){
        return sw_fetch_from_network(event)
    });
});



/*==========[ Private methods ]===============*/
function sw_fetch_from_cache(event){
    return caches
        .open(wutsiCacheName)
        .then(function (cache) {
            return cache.match(event.request).then(function (matching) {
                if (matching){
                    // console.log('Fetched from Cache', event.request.method, event.request.url);
                    return matching;
                }
                return Promise.reject('no-match');
            });
        });
}

function sw_fetch_from_network(event) {
    return fetch(event.request)
        .then(function(response){
            // console.log('Fetched from Network', event.request.method, event.request.url);

            if (sw_should_cache(event.request)){
                // console.log('Caching', event.request.method, event.request.url);
                const clone = response.clone();
                caches.open(wutsiCacheName).then(function(cache){
                    cache.put(event.request.url, clone);
                    return response;
                });
            }

            return response;
        });
}

function sw_show_notification(data) {
    if (sw_should_show_notification(data)){
        const title = 'Wutsi: ' + data.author;
        const config = {
            body: data.title,
            icon: '/assets/wutsi/img/logo/logo_96x96.png',
            data: data
        };
        self.registration.showNotification(title, config);
    }

}

function sw_should_show_notification(data) {
    if (data.event == 'com.wutsi.blog.client.event.PublishEvent') { // Publishing event
        if (data.contractId) { // Author is a contractor?
            const hour = Date().getHours();
            if (hour >= 9 && hour < 21) {   // Right time?
                return true;
            } else {
                console.log("Cannot show notification from 9AM to 9PM. current hour=", hour);
            }
        } else {
            console.log("Cannot show notification for non-contractor");
        }
    } else {
        console.log("Cannot show notification for event: ", data.event);
    }

    return false;
}

function sw_should_cache(request) {
    const url = request.url;
    if (request.method == 'POST' || url.endsWith('sw.js')){
        return false
    }

    return url.endsWith(".png") ||
        url.endsWith(".jpg") ||
        url.endsWith(".json") ||
        url.endsWith(".js") ||
        url.endsWith(".css") ||
        url.endsWith(".ico")
    ;
}
