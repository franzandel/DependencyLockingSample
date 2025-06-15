configurations {
    // Your configuration setup
    val envs = listOf("dev", "prod") // adjust according to your environment
    val buildTypes = listOf("debug", "release") // adjust according to your build type
    val targets = listOf("compile", "runtime")

    envs.forEach { env ->
        buildTypes.forEach { buildType ->
            targets.forEach { target ->
                val capitalizedBuildType = buildType.replaceFirstChar { it.uppercase() }
                val capitalizedTarget = target.replaceFirstChar { it.uppercase() }
                val configName = "${env}${capitalizedBuildType}${capitalizedTarget}Classpath"

                register(configName) {
                    resolutionStrategy.activateDependencyLocking()
                }

                // For some reason this needs to be added, otherwise will fail gradle sync
                val configNameCopy = "${env}${capitalizedBuildType}${capitalizedTarget}ClasspathCopy"
                register(configNameCopy) {
                    resolutionStrategy.activateDependencyLocking()
                }
            }
        }
    }
}

dependencyLocking {
    lockMode.set(LockMode.STRICT)
}