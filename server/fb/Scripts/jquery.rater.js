//copyright 2008 Jarrett Vance
//http://jvance.com
$.fn.rater = function(options) {
    var opts = $.extend({}, $.fn.rater.defaults, options);
    return this.each(function() {
        var $this = $(this);
        var $on = $this.find('.ui-rater-starsOn');
        var $off = $this.find('.ui-rater-starsOff');
        opts.size = $on.height();
        if (opts.rating == undefined) opts.rating = $on.width() / opts.size;
        if (opts.id == undefined) opts.id = $this.attr('id');
        /*
        alert(  "opts.id: " + opts.id + "\n" +
                "opts.jokeId: " + opts.jokeId + "\n" +
                "opts.postHref: " + opts.postHref + "\n" +
                "opts.size: " + opts.size + "\n" +
                "opts.rating: " + opts.rating + "\n" +
                "opts.units: " + opts.units + "\n" +
                "opts.step: " + opts.step);
        */
        $off.mousemove(function(e) {
            var left = e.clientX - $off.offset().left;
            var width = $off.width() - ($off.width() - left);
            width = Math.ceil(width / (opts.size / opts.step)) * opts.size / opts.step;
            $on.width(width);
        }).hover(function(e) { $on.addClass('ui-rater-starsHover'); }, function(e) {
            $on.removeClass('ui-rater-starsHover'); $on.width(opts.rating * opts.size);
        }).click(function (e) {
            setCookie(opts.jokeId, "liked");
            var r = Math.round($on.width() / $off.width() * (opts.units * opts.step)) / opts.step;
            $off.unbind('click').unbind('mousemove').unbind('mouseenter').unbind('mouseleave');
            $off.css('cursor', 'default'); $on.css('cursor', 'default');
            $.fn.rater.rate($this, opts, r);
        }).css('cursor', 'pointer'); $on.css('cursor', 'pointer');
    });
};

$.fn.rater.defaults = {
    postHref: location.href,
    units: 5,
    step: 1,
    jokeId: 0
};

$.fn.rater.rate = function ($this, opts, rating) {
    var $on = $this.find('.ui-rater-starsOn');
    var $off = $this.find('.ui-rater-starsOff');
    $off.fadeTo(600, 0.4, function () {
        $.ajax({
            url: opts.postHref,
            type: "GET",
            data: 'jokeId=' + opts.jokeId + '&rating=' + rating,
            complete: function (req) {
                if (req.status == 200) { //success
                    var doc = eval('(' + req.responseText + ')');
                    opts.rating = doc.reviewStars;
                    $off.fadeTo(600, 0.1, function () {
                        $on.removeClass('ui-rater-starsHover').width(opts.rating * opts.size);
                        var $count = $this.find('.ui-rater-rateCount');
                        $count.text(parseInt($count.text()) + 1);
                        $this.find('.ui-rater-rating').text(opts.rating.toFixed(1));
                        $off.fadeTo(600, 1);
                        $this.attr('title', 'Your rating: ' + rating.toFixed(1));
                    });
                } else { //failure
                    alert(req.responseText);
                    $on.removeClass('ui-rater-starsHover').width(opts.rating * opts.size);
                    $this.rater(opts);
                    $off.fadeTo(2200, 1);
                }
            }
        });
    });
};

function setCookie(c_name, value) {
    var c_value = escape(value);
    document.cookie = c_name + "=" + c_value;
}
