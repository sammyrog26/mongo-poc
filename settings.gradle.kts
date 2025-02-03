rootProject.name = "mongo-poc"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        val confluentUrl: String by settings
        val jFrogUrl: String by settings

        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "jFrog"
            url = uri(jFrogUrl)
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials(PasswordCredentials::class)
        }
        mavenLocal()
        maven(confluentUrl)
    }

    versionCatalogs {
        val catalogVersion: String by settings
        create("dep") {
            from(files("gradle/dep.versions.toml"))
        }
        create("libs") {
            from("com.delasport:sport-data-markets-version-catalog:$catalogVersion")
        }
    }
}
