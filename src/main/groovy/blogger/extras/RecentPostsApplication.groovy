package blogger.extras

// Determine the value of the required System properties
// solrHome and outputDir.
def sysProps = System.properties
def solrHome = sysProps['solrHome']
def outputDir = sysProps['outputDir']

if (!solrHome || !outputDir) {
    throw new IllegalArgumentException("Please set solrHome and outputDir System properties")
}


// Create RecentPostsGenerator to generate the HTML
// with related posts.
def generator = new RecentPostsGenerator(solrHome: solrHome, outputDir: outputDir)
generator.initialize()
generator.fillIndex()
//generator.writeRelatedPosts()
generator.writeLabelPosts()

// Stop
System.exit 0