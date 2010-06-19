import groovy.sql.Sql

/**
 * 
 */
class BootstrapGsql {
	
	def init = { Sql sql ->
		println "BootstrapGsql.init"
	}
	
	def destroy = { Sql sql ->
		println "BootstrapGsql.destroy"
	}
	
}
