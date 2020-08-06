const wutsiCacheName = 'wutsi-v5';

self.addEventListener('install', function(event) {
    console.log('Installing service worker', event);

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
    // console.log('Fetching', event);
    const url = event.request.url;
    const method = event.request.method;

    event.respondWith(
        caches.match(event.request)
            .then(function(response){
                if (response) {
                    console.log(method, url, 'Fetched from Cache');
                    return response;
                }
                return fetch(event.request)
                        .then(function(response){
                            console.log(method, url, 'Fetched from Network');
                            if (sw_should_cache(event.request)){
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
    if (request.method == 'POST'){
        return false
    }

    const url = request.url;
    return url.endsWith(".png") ||
        url.endsWith(".jpg") ||
        url.endsWith(".json") ||
        url.endsWith(".js") ||
        url.endsWith(".css")
    ;
}
