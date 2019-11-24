$(document).ready(setTimeout(function() {
        $(".alert").addClass("hide-with-transition");
    },3500));

$(document).ready(setTimeout(function() {
        $(".alert").contents().filter(function() {
            return this.nodeType === 3;
        }).replaceWith('&nbsp;');
    },4500));
    
    
window.requestAnimFrame = (function(){
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function( callback ){ window.setTimeout(callback, 1000 / 60); };
})();

$(document).ready(function() {
    var width = $(window).width();
    var height = $(window).height();
    
    $(window).resize(function() {
        if (($(window).width() < 1920 || $(window).height() < 1080) && ($(window).width() < width || $(window).height() < height)) {
            alert("The page is customized to fullHD resolution, it should not be displayed in lower resolution nor be zoomed in.");
            width = $(window).width();
            height = $(window).height();
        }
    })
});