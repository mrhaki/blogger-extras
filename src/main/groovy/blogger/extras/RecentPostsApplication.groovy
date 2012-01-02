package blogger.extras

def solrHome = '/Users/mrhaki/Projects/blogger-extras/solr'
def outputDir = '/Users/mrhaki/Projects/blogger-extras/related-posts'

def generator = new RecentPostsGenerator(solrHome: solrHome, outputDir: outputDir)
generator.initialize()
generator.fillIndex()
generator.writeRelatedPosts()

System.exit 0