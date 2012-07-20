/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/8/12 3:17 PM
 */

// log4j configuration
log4j {
    appender.stdout = 'org.apache.log4j.ConsoleAppender'
    appender.'stdout.layout' = 'org.apache.log4j.PatternLayout'
    appender.'stdout.layout.ConversionPattern' = '[%r] %c{2} %m%n'
    appender.errors = 'org.apache.log4j.FileAppender'
    appender.'errors.layout' = 'org.apache.log4j.PatternLayout'
    appender.'errors.layout.ConversionPattern' = '[%r] %c{2} %m%n'
    appender.'errors.File' = 'stacktrace.log'
    rootLogger = 'error,stdout'
    logger {
        griffon = 'error'
        StackTrace = 'error,errors'
        org {
            codehaus.griffon.commons = 'info' // core / classloading
        }
    }
    additivity.StackTrace = false
}

// key signing information
environments {
    development {
        signingkey {
            params {
                sigfile = 'GRIFFON'
                keystore = "${basedir}/griffon-app/conf/keys/devKeystore"
                alias = 'development'
                storepass = 'BadStorePassword'
                keypass = 'BadKeyPassword'
                lazy = true // only sign when unsigned
            }
        }
    }
    test {
        griffon {
            jars {
                sign = false
                pack = false
            }
        }
    }
    production {
        signingkey {
            params {
                sigfile = 'GRIFFON'
                keystore = 'CHANGE ME'
                alias = 'CHANGE ME'
                // NOTE: for production keys it is more secure to rely on key prompting
                // no value means we will prompt //storepass = 'BadStorePassword'
                // no value means we will prompt //keypass   = 'BadKeyPassword'
                lazy = false // sign, regardless of existing signatures
            }
        }
        griffon {
            jars {
                sign = false
                pack = false
                destDir = "${basedir}/staging"
            }
            webstart {
                codebase = 'CHANGE ME'
            }
        }
    }
}

griffon {
    memory {
        min = '64m'
        max = '128m'
        //maxPermSize = '64m'
    }
    jars {
        sign = false
        pack = false
        destDir = "${basedir}/staging"
        jarName = "${appName}.jar"
    }
    extensions {
        jarUrls = []
        jnlpUrls = []
        /*
        props {
            someProperty = 'someValue'
        }
        resources {
            linux { // windows, macosx, solaris
                jars = []
                nativelibs = []
                props {
                    someProperty = 'someValue'
                }
            }
        }
        */
    }
    webstart {
        codebase = "${new File(griffon.jars.destDir).toURI().toASCIIString()}"
        jnlp = 'application.jnlp'
    }
    applet {
        jnlp = 'applet.jnlp'
        html = 'applet.html'
    }
    doc {
        logo = '<a href="http://griffon.codehaus.org" target="_blank"><img alt="The Griffon Framework" src="../img/griffon.png" border="0"/></a>'
        sponsorLogo = "<br/>"
        footer = "<br/><br/>Made with Griffon (0.9)"
    }
}

griffon.project.dependency.resolution = {
    // inherit Griffon' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        griffonPlugins()
        griffonHome()
        griffonCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.5'
    }
}

app.archetype = 'default'
app.fileType = '.groovy'
app.defaultPackageName = 'com.ventplan.desktop'
application.icon = '/griffon-app/resources/Ventplan.icns'

compiler {
    threading {
        com {
            ventplan {
                /*
                desktop {
                    VentplanController = true
                    ProjektModel = true
                    ProjektController = true
                    AkustikView = true
                    AkustikBindings = true
                    //ProjektView = true
                    //Wac2MainPane = true
                    //Wac2View = true
                    RaumVsView
                    VpxModelService = true
                    CalculationService = true
                    VentplanModelService = true
                    AuslegungView = true
                }
                */
                desktop = false
            }
        }
    }
}
