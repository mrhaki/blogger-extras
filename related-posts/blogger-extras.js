var blogger_extras = function() {
    var related = function() {
        var relatedPostsDivs = $('.related-posts');
        $.each(relatedPostsDivs, function(index, value) {
            var holder = $(this);
            var postId = holder.attr("rel");
            if (postId) {
                var url = "http://www.mrhaki.com/related-posts/post-" + postId + ".html";
                var query = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%20%3D%20'" + encodeURIComponent(url) + "'&format=xml&callback=?";
                $.getJSON(query, function(data) {
                    if (data.results[0]) {
                        var posts = data.results[0];
                        holder.html("<h2>Related Posts</h2>" + posts);
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