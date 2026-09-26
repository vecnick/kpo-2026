import org.gradle.api.plugins.quality.CheckstyleExtension

subprojects {
    pluginManager.withPlugin("java") {
        apply(plugin = "checkstyle")

        val sunChecks = configurations.detachedConfiguration(
            dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
        ).apply {
            isTransitive = false
        }

        extensions.configure<CheckstyleExtension> {
            toolVersion = "14.1.0"
            config = resources.text.fromArchiveEntry(
                sunChecks,
                "sun_checks.xml"
            )
            isShowViolations = true
            isIgnoreFailures = false
            maxWarnings = 0
        }

        tasks.withType<Checkstyle>().configureEach {
            reports {
                html.required = true
                xml.required = false
            }
            // Линтер автоматически будет игнорировать заготовки преподавателя
            exclude("**/exception/**")
            exclude("**/model/**")
            exclude("**/service/**")
        }
    }
}
