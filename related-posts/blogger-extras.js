var blogger_extras = function() {
    var related = function() {
        var relatedPostsDivs = $('.related-posts');
        $.each(relatedPostsDivs, function(index, value) {
            var holder = $(this);
            var postId = holder.attr("rel");
            if (postId) {
                var url = "http://www.mrhaki.com/related-posts/post-" + postId + ".jsonp";
                $.ajax({
                    url: url,
                    dataType: "jsonp",
                    jsonp: false, jsonpCallback: "showRelatedPosts",
                    success: function(data) {
                        $("<h2/>").text("Related posts").insertBefore(holder);
                        $.each(data, function(i, item) {
                            var li = $("<li/>");
                            $("<a/>").attr("href", item.url).text(item.title + " (Matching score " + item.score + ")").appendTo(li);
                            li.appendTo(holder);
                        });
                    }
                });
            } else {
                holder.html('')
            }
        });
    }

    return {
        related: related
    }
}();
