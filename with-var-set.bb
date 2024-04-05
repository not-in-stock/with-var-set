#!/usr/bin/env bb
(ns script
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [babashka.process :refer [shell]]))

(def home
  (str (System/getProperty "user.home") "/"))

(def working-dir
  (str (System/getProperty "user.dir") "/"))

(def config-file-name
  "~/.var-sets.edn")

(defn expand-home [s]
  (cond-> s
    (str/starts-with? s "~/")
    (str/replace-first "~/" home)
    s))

(defn load-edn
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(defn read-config []
  (-> config-file-name
      expand-home
      load-edn))

(defn get-var-sets [config]
  (reduce (fn [acc [dir profiles]]
            (when (str/starts-with? working-dir (expand-home dir))
              (reduced profiles))) nil
          config))

(defn double-quote [s]
  (str "\"" s "\""))

(defn escape-spaces [s]
  (cond-> s
    (str/includes? s " ") double-quote))

(defn strigify-keys [var-set]
  (->> (for [[var-key var-value] var-set
             :let [var-name (cond-> var-key
                              (keyword? var-key) name)
                   escaped-var-value (escape-spaces var-value)]]
         [var-name escaped-var-value])
       (into {})))

(defn parse-args [args]
  (->> args
       (reduce (fn [acc string-arg]
                 (let [{:keys [sets args]} acc
                       arg (edn/read-string string-arg)]
                   (if (keyword? arg)
                     {:sets (conj sets arg)
                      :args (rest args)}
                     (reduced acc))))
               {:sets []
                :args args})))

(defn -main [args]
  (let [[var-set-name & rest-args] args
        config (read-config)
        var-sets (get-var-sets config)
        var-set (get var-sets var-set-name)
        command (concat [{:extra-env (strigify-keys var-set)}] rest-args)]
    (apply shell command)))

(-main *command-line-args*)
