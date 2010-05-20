halloAction = action(
		id: "halloAction",
		enabled: true,
		name: "hallo",
		closure: {
				println "model before event"
				println model.map
				println ""
				app.event("ping")
				println ""
				println "model after event"
				println model.map
				println ""
			}
	)
