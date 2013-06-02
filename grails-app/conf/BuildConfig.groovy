grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.release.scm.enabled = false
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.repos.compstak.url = "http://54.235.241.190:8081/nexus/content/repositories/grails-plugins-releases"
grails.project.repos.compstak.username = System.getProperty("compstak.repo.username")
grails.project.repos.compstak.password = System.getProperty("compstak.repo.password")
grails.project.repos.default = "compstak"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        grailsRepo "http://grails.org/plugins"
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime 'org.lesscss:lesscss:1.3.1'

        test( "org.spockframework:spock-grails-support:0.7-groovy-2.0") {
            export = false
        }
        test ('org.gmock:gmock:0.8.2') {
            export = false
        }
        test("org.seleniumhq.selenium:selenium-firefox-driver:2.28.0") {
            exclude 'selenium-server'
            export = false
        }
    }
    plugins {
        test (":spock:0.7") {
            export = false
        }
        test (":geb:0.7.2") {
            export = false
        }
        compile (":resources:1.1.6") {
            export = false
        }
        compile (":hibernate:2.2.0") {
            export = false
        }
        compile (":rest-client-builder:1.0.3") {
            export = false
        }
        compile (":tomcat:2.2.0") {
            export = false
        }
//        build(':release:2.0.4', ':rest-client-builder:1.0.2') {
//           export = false
//        }
        build(':release:2.2.1') {
            export = false
        }
    }
}
