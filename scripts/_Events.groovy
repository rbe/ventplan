/*
 * ventplan
 * Ventplan
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 5/26/12 12:30 PM
 */

/**
 * IZPACK
 * Erstellen mit 'griffon prod package izpack'
 *
 * JSMOOTH
 * Erstellen mit 'griffon prod create-windows'
 */

eventPackageResourcesEnd = {
    // IZPACK START
    // Copy customized izpack files from /src/main/installer/izpack/resources to izpack resources dir.
    ant.copy(todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true) {
        fileset(dir: "${basedir}/src/main/installer/izpack/resources", includes: "**")
    }
    // Copy SQL Database to izpack resources dir
    ant.copy(todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true) {
        fileset(dir: "${basedir}/sql", includes: "ventplan.db")
    }
    /* No bin/ needed
    // Copy customized Ventplan executables from /bin to izpack bin dir.
    ant.copy(todir: "${projectWorkDir}/installer/izpack/binary/bin", overwrite: true) {
        fileset(dir: "${basedir}/bin", includes: "**")
    }
    */
    // Copy Ventplan images to resource dir to display them in README.html.
    ant.copy(todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true) {
        fileset(dir: "${basedir}/griffon-app/resources/image", includes: "*.png")
    }
    // IZPACK END

    // JSMOOTH START
    // OS X: SQL databases go into lib/
    ant.copy(todir: "${projectWorkDir}/installer/mac/dist/lib", overwrite: true) {
        fileset(dir: "${basedir}/sql", includes: "ventplan.db")
    }
    // Windows: SQL databases go into lib/
    ant.copy(todir: "${projectWorkDir}/installer/jsmooth/dist/lib", overwrite: true) {
        fileset(dir: "${basedir}/sql", includes: "ventplan.db")
    }
    /* No bin/ needed
    ant.copy(todir: "${projectWorkDir}/installer/jsmooth/dist/bin", overwrite: true) {
        fileset(dir: "${basedir}/bin", includes: "**")
    }
    */
    ant.copy(todir: "${projectWorkDir}/installer/jsmooth", overwrite: true) {
        fileset(dir: "${basedir}/src/main/installer/izpack/resources", includes: "*.jsmooth")
    }
    // JSMOOTH END
}

eventCreatePackageStart = {
    // IZPACK START
    /* No bin/ needed
    // Copy customized Ventplan executables from /bin to izpack bin dir.
    ant.copy(todir: "${projectWorkDir}/installer/izpack/binary/bin", overwrite: true) {
        fileset(dir: "${basedir}/bin", includes: "**")
    }
    */
    // IZPACK END

    // JSMOOTH START
    /* No bin/ needed
    ant.copy(todir: "${projectWorkDir}/installer/jsmooth/dist/bin", overwrite: true) {
        fileset(dir: "${basedir}/bin", includes: "**")
    }
    */
    ant.copy(todir: "${projectWorkDir}/installer/jsmooth", overwrite: true) {
        fileset(dir: "${basedir}/src/main/installer/izpack/resources", includes: "*.jsmooth")
    }
    /* No bin/ needed
    ant.copy(todir: "${basedir}/dist/windows/bin", overwrite: true) {
        fileset(dir: "${basedir}/bin", includes: "**")
    }
    */
    // JSMOOTH END
}
