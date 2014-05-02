
import eu.artofcoding.griffon.helper.HttpHelper
import eu.artofcoding.ventplan.desktop.VentplanResource
import eu.artofcoding.ventplan.desktop.VentplanSplash
import groovy.sql.Sql

class BootstrapGsql {

    def init = { String dataSourceName = 'default', Sql sql ->
        // Set splash screen status text: connecting database
        VentplanSplash.instance.connectingDatabase()
        // Set splash screen status text: updating database
        VentplanSplash.instance.updatingDatabase()
        try {
            String baseurl = VentplanResource.getDatabaseUpdateUrl()
            int me = sql.firstRow('SELECT dbrev FROM ventplan WHERE id = 1')[0] as int
            int head = HttpHelper.download("${baseurl}/head").toInteger()
            if (me + 1 < head) {
                sql.withTransaction {
                    (me + 1).upto head, { rev ->
                        // Set splash screen status text: updating database
                        VentplanSplash.instance.updatingDatabase('Revision #${rev}')
                        String content = HttpHelper.download("${baseurl}/${it}.sql")
                        content.eachLine { stmt ->
                            //print "rev#${me} -> rev#${rev}: ${stmt}"
                            sql.executeUpdate(stmt)
                        }
                    }
                    sql.executeUpdate("UPDATE ventplan SET DBREV = ${head} WHERE ID = 1")
                }
            } else {
                //println "rev#${me} == rev#${head}"
            }
        } catch (e) {
            // ignore
        } finally {
            // ignore
        }
    }

    def destroy = { String dataSourceName = 'default', Sql sql ->
    }

}
