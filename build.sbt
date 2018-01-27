name := "SP500Analysis"

version := "0.1"

scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.11" % "2.2.1" % "provided",
  "org.apache.spark" % "spark-core_2.11" % "2.2.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.2.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("com",   "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com",   "squareup", xs @ _*) => MergeStrategy.last
  case PathList("com",   "sun", xs @ _*) => MergeStrategy.last
  case PathList("com",   "thoughtworks", xs @ _*) => MergeStrategy.last
  case PathList("commons-beanutils", xs @ _*) => MergeStrategy.last
  case PathList("commons-cli", xs @ _*) => MergeStrategy.last
  case PathList("commons-collections", xs @ _*) => MergeStrategy.last
  case PathList("commons-io", xs @ _*) => MergeStrategy.last
  case PathList("io",    "netty", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("javax", "xml", xs @ _*) => MergeStrategy.last
  case PathList("org",   "apache", xs @ _*) => MergeStrategy.last
  case PathList("org",   "codehaus", xs @ _*) => MergeStrategy.last
  case PathList("org",   "fusesource", xs @ _*) => MergeStrategy.last
  case PathList("org",   "mortbay", xs @ _*) => MergeStrategy.last
  case PathList("org",   "tukaani", xs @ _*) => MergeStrategy.last
  case PathList("xerces", xs @ _*) => MergeStrategy.last
  case PathList("xmlenc", xs @ _*) => MergeStrategy.last
  case PathList(ps@_*) if ps.last endsWith ".dll" => MergeStrategy.discard
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".so" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".xsd" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".dtd" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith "yarn-version-info.properties" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith "libjansi.jnilib" => MergeStrategy.discard
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mailcap.default" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
