self.addEventListener('beforeinstallprompt', function (e) {
    console.log('beforeinstallprompt');

    // Prevent the mini-infobar from appearing on mobile
    e.preventDefault();
});
