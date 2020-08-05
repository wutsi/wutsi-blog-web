const wutsiCacheName = 'wutsi-v5';

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

self.addEventListener('push', function(event) {
    console.log('Push Notification received', event);

    const data = event.data.json().data;
    console.log('data', data);
    if (sw_should_show_notification(data)){

        const title = 'Wutsi: ' + data.author;
        const config = {
            body: data.title,
            icon: '/assets/wutsi/img/logo/logo_96x96.png',
            data: data
        };
        self.registration.showNotification(title, config);
    }
});


self.addEventListener('notificationclick', function(event) {
    console.log('notificationclick', event);

    const data = event.notification.data;
    console.log('data', data);

    event.notification.close();
    clients.openWindow(data.url + '?utm_source=push&utm_medium=pwa');
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
                            if (sw_should_cache(url)){
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


function sw_should_show_notification(data) {
    if (data.event == 'com.wutsi.blog.client.event.PublishEvent') {
        return true;
    }
    return false;
}

function sw_should_cache(url) {
    return false;
}
