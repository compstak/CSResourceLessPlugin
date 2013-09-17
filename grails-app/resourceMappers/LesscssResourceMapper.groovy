import grails.util.BuildSettings
import grails.util.BuildSettingsHolder
import org.grails.plugin.resource.mapper.MapperPhase
import org.apache.commons.io.FileUtils

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.lesscss.LessCompiler
import org.lesscss.LessException
import org.springframework.web.util.WebUtils
import javax.servlet.ServletContext

class LesscssResourceMapper implements GrailsApplicationAware {

    GrailsApplication grailsApplication
    LessCompiler lessCompiler
    ServletContext servletContext
    private File cacheDir

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultIncludes = ['**/*.less']

    File getCacheDir() {
        if (!this.cacheDir) {
            BuildSettings settings = BuildSettingsHolder.settings
            if (settings) {
                cacheDir = new File(settings.projectWorkDir, "resources-cache")
            } else {
                cacheDir = new File(WebUtils.getTempDir(servletContext), "resources-cache")
            }
            if (log.debugEnabled) {
                log.debug("CacheDir: ${cacheDir.getAbsolutePath()}")
            }
        }
        cacheDir
    }

    def map(resource, config) {
        final boolean isCachingEnabled = config.cache?.enabled
        if(!lessCompiler) {
            lessCompiler = new LessCompiler()
            lessCompiler.setCompress(grailsApplication.config.grails?.resources?.mappers?.lesscss?.compress == true ?: false)
        }
        File originalFile = resource.processedFile
        File input
        try {
          input = grailsApplication.parentContext.getResource(resource.sourceUrl).file
        } catch (FileNotFoundException e) {
          input = resource.originalResource.getFile()
        }
        File target = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))

        if (log.debugEnabled) {
            log.debug "Compiling LESS file [${originalFile}] into [${target}], with compress [${grailsApplication.config.grails?.resources?.mappers?.lesscss?.compress}]"
        }
        try {
            if (isCachingEnabled) {
               File cache = new File(getCacheDir(), resource.id.replaceAll(/(?i)\.less/, '.css'))

               if(!cache.exists() || cache.lastModified() <= input.lastModified()){
                   lessCompiler.compile input, target
                   cache.getParentFile().mkdirs()
                  cache.write(target.getText())
               }else{
                   FileUtils.copyFile(cache, target)
               }
            } else {
                   lessCompiler.compile input, target
            }
            // Update mapping entry
            // We need to reference the new css file from now on
            resource.processedFile = target
            // Not sure if i really need these
            resource.sourceUrlExtension = 'css'
            resource.contentType = 'text/css'
            resource.tagAttributes?.rel = 'stylesheet'
            resource.updateActualUrlFromProcessedFile()

        } catch (LessException e) {
            log.error("error compiling less file: ${originalFile}", e)
        }

    }

    private String generateCompiledFileFromOriginal(String original) {
         original + '.css'
    }
}
