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
    };

    var labelSeries = function() {
        var labelSeries = $('.post-label-series');
        $.each(relatedPostsDivs, function(index, value) {
            var holder = $(this);
            var blogitemTitle = holder.attr("rel");
            if (blogitemTitle) {

                var blogitemLabel;
                if (/^Groovy Goodness:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Groovy:Goodness";
                } else if (/^Grassroots Groovy:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Groovy:Grassroots";
                } else if (/^Gradle Goodness:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Gradle:Goodness";
                } else if (/^Grails Goodness:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Grails:Goodness";
                } else if (/^Awesome Asciidoctor:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Awesome:Asciidoctor";
                } else if (/^Ratpacked:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Ratpacked";
                } else if (/^Spocklight:.*$/.test(blogitemTitle)) {
                    blogitemLabel = "Spocklight";
                }

                var url = "http://www.mrhaki.com/related-posts/labels-" + escape(blogitemLabel) + ".jsonp";
                $.ajax({
                    url: url,
                    dataType: "jsonp",
                    jsonp: false, jsonpCallback: "showLabelPosts",
                    success: function(data) {
                        $("<h2/>").text("More posts " + labelValue).insertBefore(holder);
                        $.each(data.items, function(i, item) {
                            var li = $("<li/>");
                            $("<a/>").attr("href", item.url).text(item.title).appendTo(li);
                            li.appendTo(holder);
                        });
                    }
                });
            } else {
                holder.html('');
            }
        });
    };

    return {
        related: related,
        labelSeries: labelSeries,
    }
}();
