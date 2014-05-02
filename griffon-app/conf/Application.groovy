application {
    title = 'Ventplan'
    startupGroups = ['MainFrame', 'Dialog']
    autoShutdown = true
}
mvcGroups {
    'Dialog' {
        controller = 'eu.artofcoding.ventplan.desktop.DialogController'
    }
    'Projekt' {
        model      = 'eu.artofcoding.ventplan.desktop.ProjektModel'
        controller = 'eu.artofcoding.ventplan.desktop.ProjektController'
        view       = 'eu.artofcoding.ventplan.desktop.ProjektView'
    }
    'MainFrame' {
        model      = 'eu.artofcoding.ventplan.desktop.VentplanModel'
        view       = 'eu.artofcoding.ventplan.desktop.VentplanView'
        controller = 'eu.artofcoding.ventplan.desktop.VentplanController'
    }

}
