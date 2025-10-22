// Service Worker for 청년부 모바일 주보
const CACHE_NAME = 'youth-bulletin-v1';
const urlsToCache = [
  '/',
  '/mobile',
  '/bulletin',
  '/api/bulletin/today',
  '/api/bulletin/announcements',
  '/api/bulletin/worship/recent',
  '/css/bootstrap.min.css',
  '/js/bootstrap.bundle.min.js',
  'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css',
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'
];

// Install event - 캐시 설정
self.addEventListener('install', function(event) {
  console.log('Service Worker: Install');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(function(cache) {
        console.log('Service Worker: Caching files');
        return cache.addAll(urlsToCache);
      })
  );
});

// Activate event - 오래된 캐시 정리
self.addEventListener('activate', function(event) {
  console.log('Service Worker: Activate');
  event.waitUntil(
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.map(function(cacheName) {
          if (cacheName !== CACHE_NAME) {
            console.log('Service Worker: Deleting old cache');
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});

// Fetch event - 네트워크 요청 처리
self.addEventListener('fetch', function(event) {
  console.log('Service Worker: Fetch', event.request.url);
  
  // API 요청은 네트워크 우선, 실패시 캐시
  if (event.request.url.includes('/api/')) {
    event.respondWith(
      fetch(event.request)
        .then(function(response) {
          // 응답을 캐시에 저장
          const responseClone = response.clone();
          caches.open(CACHE_NAME)
            .then(function(cache) {
              cache.put(event.request, responseClone);
            });
          return response;
        })
        .catch(function() {
          // 네트워크 실패시 캐시에서 가져오기
          return caches.match(event.request);
        })
    );
  } else {
    // 정적 리소스는 캐시 우선
    event.respondWith(
      caches.match(event.request)
        .then(function(response) {
          // 캐시에 있으면 캐시에서 반환
          if (response) {
            return response;
          }
          // 캐시에 없으면 네트워크에서 가져오기
          return fetch(event.request);
        })
    );
  }
});

// Push notification 처리 (향후 확장용)
self.addEventListener('push', function(event) {
  console.log('Service Worker: Push notification received');
  
  const options = {
    body: event.data ? event.data.text() : '새로운 주보가 업데이트되었습니다!',
    icon: '/icon-192.png',
    badge: '/icon-192.png',
    vibrate: [200, 100, 200],
    data: {
      dateOfArrival: Date.now(),
      primaryKey: 1
    },
    actions: [
      {
        action: 'explore',
        title: '주보 보기',
        icon: '/icon-192.png'
      },
      {
        action: 'close',
        title: '닫기',
        icon: '/icon-192.png'
      }
    ]
  };

  event.waitUntil(
    self.registration.showNotification('청년부 주보', options)
  );
});

// Notification click 처리
self.addEventListener('notificationclick', function(event) {
  console.log('Service Worker: Notification click');
  
  event.notification.close();
  
  if (event.action === 'explore') {
    event.waitUntil(
      clients.openWindow('/mobile')
    );
  }
});
