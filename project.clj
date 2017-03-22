(defproject nethacking "0.1.0-SNAPSHOT"
  :description "Nethacking"
  :url "https://github.com/oshyshko/nethacking"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.pcap4j/pcap4j-core "1.7.0"]
                 [org.slf4j/slf4j-simple "1.7.25"]
                 [junit/junit "4.12" :scope "test"]]

  :java-source-paths ["src"]

  :main         pcap.Tester

  ; A trick to stop IntelliJ reseting compiler/module version to "1.5" after "lein pom"
  :pom-plugins [[org.apache.maven.plugins/maven-compiler-plugin "3.6.1"
                 [:configuration ([:source "1.8"]
                                  [:target "1.8"])]]] )
