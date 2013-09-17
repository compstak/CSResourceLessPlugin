import org.grails.plugin.resource.BundleResourceMapper
import org.grails.plugin.resource.CSSBundleResourceMeta
import org.grails.plugin.resource.CSSPreprocessorResourceMapper
import org.grails.plugin.resource.CSSRewriterResourceMapper
import org.grails.plugin.resource.ResourceModule
import org.grails.plugin.resource.ResourceProcessor
import org.grails.plugin.resource.ResourceTagLib
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext

class LesscssResourcesGrailsPlugin {
    // the plugin version
    def version = "1.3.1.7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    // the other plugins this plugin depends on
    def dependsOn = [resources:'1.1.6 > *']
    def loadAfter = ['resources']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/*.gsp",
            "web-app/less/*",
            "web-app/images/*",
            "web-app/css/*",
            "web-app/js/*"
    ]

    def author = "Paul Fairless"
    def authorEmail = ""
    def title = "Less CSS Resources"
    def description = '''\\
This plugin supports server-side compilation of .less CSS files to their .css counterparts.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/paulfairless/grails-lesscss-resources"
    def license = "APACHE"
    def issueManagement = [ system: "github", url: "https://github.com/paulfairless/grails-lesscss-resources/issues" ]
    def scm = [ url: "https://github.com/paulfairless/grails-lesscss-resources" ]

    def watchedResources = ["file:./**/*.less"]

    def onChange = { event ->
        def webApp = event.ctx as GrailsWebApplicationContext
        if (application.config.grails?.resources?.mappers?.lesscss?.rootFile) {
            def rootLess = webApp.getResource(application.config.grails?.resources?.mappers?.lesscss?.rootFile).file
            if (event.source.file != rootLess) {
                FileUtils.touch(rootLess)
            }
        }
    }

    def doWithSpring = { ->
        CSSPreprocessorResourceMapper.defaultIncludes.add('**/*.less')
        CSSRewriterResourceMapper.defaultIncludes.add('**/*.less')

        BundleResourceMapper.MIMETYPE_TO_RESOURCE_META_CLASS.put('stylesheet/less', CSSBundleResourceMeta)
        List currentTypes = new ResourceModule().bundleTypes
        ResourceModule.metaClass.getBundleTypes = {  currentTypes << 'less' }
        ResourceProcessor.DEFAULT_MODULE_SETTINGS['less'] = [disposition: 'head'  ]
        ResourceTagLib.SUPPORTED_TYPES['less'] = [
                type: "text/css",
                rel: 'stylesheet/less',
                media: 'screen, projection'
        ]

    }
}
