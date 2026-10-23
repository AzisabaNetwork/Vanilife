dependencies {
    compileOnly(project(":plugins:plugin-island"))
    compileOnly(project(":plugins:plugin-runtime"))
    compileOnly(libs.packetevents)
    implementation(libs.koin.core)
}
