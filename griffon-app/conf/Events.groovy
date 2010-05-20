import com.westaflex.wac.*

onBootstrapEnd = { app ->
	def dataSource = new ConfigSlurper().parse(DataSource).dataSource
	WacModelService.instance.initDataSource(dataSource)
}
