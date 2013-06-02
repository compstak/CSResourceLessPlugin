import org.grails.plugin.resource.mapper.MapperPhase

/**
 * @author Paul Fairless
 *
 * Mapping file to compile .less files into .css files
 */
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.lesscss.LessCompiler
import org.lesscss.LessException

class LesscssResourceMapper implements GrailsApplicationAware {

    GrailsApplication grailsApplication
    LessCompiler lessCompiler

    def phase = MapperPhase.GENERATION // need to run early so that we don't miss out on all the good stuff

    static defaultIncludes = ['**/*.less']

    def map(resource, config) {
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
            String cachedFileName = originalFile.absolutePath.split('tomcat/work/Tomcat/localhost')[0] + 'cache' + resource.id.replaceAll(/(?i)\.less/, '.css')
            File cacheBase = new File( cachedFileName.subSequence(0, cachedFileName.lastIndexOf('/') ) )
            File cache = new File(cachedFileName)

                if(!cache.exists() || cache.lastModified() <= input.lastModified()){
                    lessCompiler.compile input, target
                    cacheBase.mkdirs()
                    cache.write(target.getText())
                }else{
                    target.write(cache.getText())
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
