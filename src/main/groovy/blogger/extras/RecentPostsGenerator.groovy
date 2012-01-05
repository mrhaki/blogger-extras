package blogger.extras

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import java.text.NumberFormat
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

/**
 * <p>Generate HTML output for each blog post with the 5 most related other
 * blog posts.</p>
 *
 * <p>First we initalize Solr, then feed Solr with the blog posts to be
 * indexed and then we query Solr for the most related posts per blog post.
 * The result of the query is used to generate a HTML file for each
 * blog post.</p>
 *
 * <p>The generated HTML file is later used by Javascript to include on
 * the Blogger website.</p>
 */
@Slf4j
class RecentPostsGenerator {
    private static final String SYSTEM_PROP_SOLR_HOME = 'solr.solr.home'

    /**
     * Solr home directory with conf/ dir contaning solrconfig.xml and schema.xml.
     */
    String solrHome

    /**
     * Output directory for generated files.
     */
    String outputDir

    /**
     * Solr embedded server.
     */
    private SolrServer solrServer

    /**
     * Store blog item identifiers during read of blog content,
     * so we can use them again when writing the files.
     */
    private List<String> blogIds = []

    /**
     * Store SolrInputDocument objects so we can add them in one go
     * to Solr for better performance.
     */
    private Collection<SolrInputDocument> documents = []

    /**
     * Initialize Solr embedded server.
     */
    void initialize() {
        initSolrHome()
        CoreContainer.Initializer initializer = new CoreContainer.Initializer()
        CoreContainer container = initializer.initialize()
        solrServer = new EmbeddedSolrServer(container, '')
        solrServer.deleteByQuery '*:*'
    }

    private String initSolrHome() {
        System.setProperty SYSTEM_PROP_SOLR_HOME, solrHome
    }

    /**
     * Use the Blogger Atom feed to get all blog items and add each found blog post
     * to Solr.
     */
    void fillIndex() {
        assert solrServer
        final String baseUrl = 'http://www.blogger.com/feeds/6671019398434141469/posts/default'
        String nextLink = baseUrl
        while (nextLink) {
            final def feed = new XmlSlurper().parse(nextLink)
            feed.entry.each createSolrInputDocument
            nextLink = feed.link.find { it.@rel == 'next' }.@href
        }

        solrServer.add documents
        solrServer.commit()
    }

    private createSolrInputDocument = { blog ->
        String blogId = blog.id
        int startOfId = blogId.indexOf('post-')
        blogId = blogId.substring(startOfId)

        blogIds << blogId

        final String blogLink = blog.link.find { it.@type == 'text/html' && it.@rel == 'alternate' }.@href
        final String blogContent = blog.content
        final String blogTitle = blog.title

        final SolrInputDocument doc = new SolrInputDocument()
        doc.addField 'id', blogId
        doc.addField 'title', blogTitle
        doc.addField 'content', blogContent
        doc.addField 'link', blogLink

        documents << doc
    }

    /**
     * Find the related posts for each blog item and save the result to
     * an HTML file in the directory set by outputDir.
     */
    void writeRelatedPosts() {
        assert solrServer
        blogIds.each findAndSaveRelatedPosts
    }

    private findAndSaveRelatedPosts = { blogId ->
        final def relatedBlogItems = findRelatedBlogItems(blogId)
        if (relatedBlogItems) {
            final File relatedPosts = new File(outputDir, blogId + '.html')
            final String html = createHtmlRelatedPosts(relatedBlogItems)
            relatedPosts.text = html
            return
        }
    }

    private String createHtmlRelatedPosts(relatedBlogItems) {
        final def htmlWriter = new StringWriter()
        final def html = new MarkupBuilder(htmlWriter)
        html.ul(id: "related-posts-list-${blogId}", class: 'related-posts') {
            relatedBlogItems.each { blogItem ->
                log.debug blogItem.toString()
                final String title = blogItem.title
                final String link = blogItem.link
                String score = blogItem.score
                score = getScorePercentage(score)
                li {
                    a href: link, {
                        mkp.yield title
                        em "(Matching score is ${score}"
                    }
                }
            }
        }
        htmlWriter.toString()
    }

    private String getScorePercentage(score) {
        final NumberFormat format = NumberFormat.getPercentInstance(Locale.US)
        final Double scoreNumber = Double.valueOf(score)
        format.format(scoreNumber)
    }

    private findRelatedBlogItems(blogId) {
        log.debug "query for blogId = $blogId"
        final SolrQuery query = buildQuery(blogId)
        final QueryResponse result = solrServer.query(query)
        final NamedList<Object> response = result.response
        final def moreLikeThis = response.get('moreLikeThis')
        final def relatedBlogs = moreLikeThis.get(blogId)
        relatedBlogs
    }

    private buildQuery(blogId) {
        final SolrQuery solrQuery = new SolrQuery()
        solrQuery.query = "id:${blogId}" as String
        queryParameters.each { key, value ->
              solrQuery.setParam key, value
        }
        solrQuery
    }

    private getQueryParameters() {
        [
                'mlt.fl': 'content,title',
                'mlt.count': '5',
                'mlt.mindf': '5',
                'mlt': true,
                'fl': 'id,title,link,score'
        ]
    }
}
