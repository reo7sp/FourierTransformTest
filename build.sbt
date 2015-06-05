lazy val root = (project in file(".")).
  settings(
    name := "fourier",
    version := "0.1",
    organization := "ru.reo7sp",
    mainClass := Some("Core"),
    exportJars := true
  )
