package com.westaflex.wac

/**
 *
 */
@Singleton
class WacModelService {
	
	/**
	 *
	 */
	def private sql
	
	/**
	 * Initialize database connection. Called from conf/Events.groovy.
	 */
	def initDataSource(dataSource) {
		/*
		sql = Sql.newInstance(
			dataSource.url,
			dataSource.username,
			dataSource.password,
			dataSource.driverClassname
		)
		// Read database schema
		def typeMap = new TypeMap()
		def binding = new Binding()
		binding.setVariable("builder", new RelationalBuilder(typeMap))
		def database = new DatabaseSchema(binding).run()
		// Create database schema
		def sqlGenerator = new SqlGenerator(typeMap,System.getProperty("line.separator","\n"))
		def writer = new StringWriter()
		sqlGenerator.writer = writer
		sqlGenerator.createDatabase(database,false)
		writer.flush()
		sql.execute(writer.toString())
		// Populate database
		binding = new Binding()
		binding.setVariable("sql", sql)
		new Bootstrap(binding).run()
		*/
	}

}
