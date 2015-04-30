(defproject funky-1 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"] 
                 [overtone "0.9.1"]
                 [overtone.synths "0.1.0-SNAPSHOT"]]
  :jvm-opts ["-Xms2048m" "-Xmx2048m"]
  :main funky-1.core)
