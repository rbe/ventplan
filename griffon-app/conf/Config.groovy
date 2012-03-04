/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
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
        /*org {
              codehaus.griffon.commons='info' // core / classloading
          }*/
    }
    additivity.StackTrace = false
}
griffon.basic_injection.disable = true
griffon.gsql.injectInto = ['service']
// The following properties have been added by the Upgrade process...
griffon.jars.pack = false // jars were not automatically packed in Griffon 0.0
griffon.jars.sign = false // jars were automatically signed in Griffon 0.0
griffon.extensions.jarUrls = [] // remote jars were not possible in Griffon 0.1
griffon.extensions.jnlpUrls = [] // remote jars were not possible in Griffon 0.1
// may safely be removed, but calling upgrade will restore it
def env = griffon.util.Environment.current.name
/*
signingkey.params.sigfile='GRIFFON' + env
signingkey.params.keystore = "${basedir}/griffon-app/conf/keys/${env}Keystore"
signingkey.params.alias = env
// signingkey.params.storepass = 'BadStorePassword'
// signingkey.params.keyPass = 'BadKeyPassword'
signingkey.params.lazy = true // only sign when unsigned
*/
// you may now tweak memory parameters
griffon.memory.min = '64m'
griffon.memory.max = '128m'
griffon.memory.maxPermSize = '64m'
//
griffon.datasource.injectInto = ["controller", "service"]
griffon.wsclient.injectInto = ["controller"]
griffon.ws.injectInto = ['controller']
//
swing {
    windowManager {
        frame1 = [
                hide: { w, app ->
                    //println "Config.swing.windowManager: w=${w.dump()}"
                    if (app.config.shutdown.proceed)
                        w.dispose()
                }
        ]
    }
}
