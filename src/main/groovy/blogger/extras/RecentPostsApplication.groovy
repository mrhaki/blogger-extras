package blogger.extras

def sysProps = System.properties
def solrHome = sysProps['solrHome']
def outputDir = sysProps['outputDir']

if (!solrHome || !outputDir) {
    throw new IllegalArgumentException("Please set solrHome and outputDir System properties")
}

def generator = new RecentPostsGenerator(solrHome: solrHome, outputDir: outputDir)
generator.initialize()
generator.fillIndex()
generator.writeRelatedPosts()

System.exit 0