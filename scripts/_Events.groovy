/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
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
    ant.copy( todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true ) {
        fileset( dir: "${basedir}/src/main/installer/izpack/resources", includes: "**" )
    }
    // Copy SQL Database to izpack resources dir
    ant.copy( todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true ) {
        fileset( dir: "${basedir}/sql", includes: "*.db" )
    }
    // Copy customized VentPlan executables from /bin to izpack bin dir.
    ant.copy( todir: "${projectWorkDir}/installer/izpack/binary/bin", overwrite: true ) {
        fileset( dir: "${basedir}/bin", includes: "**" )
    }
    // Copy VentPlan images to resource dir to display them in README.html.
    ant.copy( todir: "${projectWorkDir}/installer/izpack/resources", overwrite: true ) {
        fileset( dir: "${basedir}/griffon-app/resources/image", includes: "*.png" )
    }
    // IZPACK END

    // JSMOOTH START
    ant.copy( todir: "${projectWorkDir}/installer/jsmooth/dist/sql", overwrite: true ) {
        fileset( dir: "${basedir}/sql", includes: "*.db" )
    }
    ant.copy( todir: "${projectWorkDir}/installer/jsmooth/dist/bin", overwrite: true ) {
        fileset( dir: "${basedir}/bin", includes: "**" )
    }
    ant.copy( todir: "${projectWorkDir}/installer/jsmooth", overwrite: true ) {
        fileset( dir: "${basedir}/src/main/installer/izpack/resources", includes: "*.jsmooth" )
    }
    // JSMOOTH END
}

eventCreatePackageStart = {
    // IZPACK START
    // Copy customized VentPlan executables from /bin to izpack bin dir.
    ant.copy( todir: "${projectWorkDir}/installer/izpack/binary/bin", overwrite: true ) {
        fileset( dir: "${basedir}/bin", includes: "**" )
    }
    ant.copy( todir: "${projectWorkDir}/installer/jsmooth/dist/bin", overwrite: true ) {
        fileset( dir: "${basedir}/bin", includes: "**" )
    }
    ant.copy( todir: "${projectWorkDir}/installer/jsmooth", overwrite: true ) {
        fileset( dir: "${basedir}/src/main/installer/izpack/resources", includes: "*.jsmooth" )
    }
    // IZPACK END

    // JSMOOTH START
    ant.copy( todir: "${basedir}/dist/windows/bin", overwrite: true ) {
        fileset( dir: "${basedir}/bin", includes: "**" )
    }
    // JSMOOTH END
}
