(ns mini.smb-v2
  (:require [clojure.stacktrace :as stacktrace]) 
  (:import [jcifs.context SingletonContext]
           [jcifs.smb NtlmPasswordAuthenticator SmbFile]))

(def user "user")
(def pass "pass")
(def host "192.168.93.27")
(def share "mount")

(defn smb-file-url
  ([file]
   (format "smb://%s/%s/%s" host share file))
  ([file path]
   (format "smb://%s/%s/%s/%s" host share path file)))

(defn make-cifs-context
  []
  (let [base-context (SingletonContext/getInstance)
        credentials  (NtlmPasswordAuthenticator. nil user pass)]
    (.withCredentials base-context credentials)))

(defn make-smb-file
  [file-url cifs-context]
  (SmbFile. file-url cifs-context))

(defn copy-file
  [file path]
  (try
    (let [cifs-context  (make-cifs-context)
          from-file-url (smb-file-url file)
          from-file     (make-smb-file from-file-url cifs-context)
          to-file-url   (smb-file-url file "success")
          to-file       (make-smb-file to-file-url cifs-context)]
      (.copyTo from-file to-file))
    (catch Exception e (println (str (some-> e .getMessage)
                                     "\n"
                                     (with-out-str (stacktrace/print-stack-trace e)))))))

(comment
  (copy-file "test.txt" "success"))
