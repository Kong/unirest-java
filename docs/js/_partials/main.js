// add target blank to all external links
for (var i = 0; i < document.links.length; i++) {
   if (document.links[i].hostname != window.location.hostname) {
       document.links[i].target = "_blank";
   } 
}

var offset = 24;
var fixedHeaderSelector = 'body > header';
smoothScroll.init({ // https://github.com/cferdinandi/smooth-scroll
    selector: 'a[href^="#"]', // Selector for links (must be a class, ID, data attribute, or element tag)
    selectorHeader: fixedHeaderSelector, // Selector for fixed headers [optional]
    speed: 500, // Integer. How fast to complete the scroll in milliseconds
    easing: 'easeInOutCubic', // Easing pattern to use
    offset: offset, // Integer. How far to offset the scrolling anchor location in pixels
    callback: function ( anchor, toggle ) {} // Function to run after scrolling
});

// scroll to current element on load
if (window.location.hash && performance.navigation.type !== 1) {
    setTimeout(function() {
        window.scrollTo(0, window.scrollY - 106);
    }, 0);
}

setTimeout(function() {
    gumshoe.init({ // https://github.com/cferdinandi/gumshoe (scrollspy) 
        selector: '#spy-nav > ul > li > a', // Default link selector 
        selectorHeader: fixedHeaderSelector, // Fixed header selector 
        container: window, // The element to spy on scrolling in (must be a valid DOM Node)
        offset: offset, // Distance in pixels to offset calculations
        activeClass: 'active', // Class to apply to active navigation link and its parent list item
        scrollDelay: false, // Wait until scrolling has stopped before updating the navigation
        callback: function (nav) {
            try {
                window.history.replaceState({}, "", location.pathname + "#" + nav.target.id);
            } catch(e) { /* Doesn't matter */ }
        }
    });
}, 500);
