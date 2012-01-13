dataSource {
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
environments {
    development {
        dataSource {
            url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
        }
    }
    test {
        dataSource {
            url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
        }  
    }
    production {
        dataSource {
            url = "jdbc:h2:zip:lib/dtmp.zip!/westawac"
        }
    }
}
