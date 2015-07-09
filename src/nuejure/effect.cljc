(ns nuejure.effect
  (:require
   [nuejure.functor :refer [Functor]]
   [nuejure.applicative :refer [Applicative coerce-return]]
   [nuejure.monad :refer [Monad]]))

(deftype Effect [effect-fn])

(def effect ->Effect)

(defn run
  ([m] (run m {}))
  ([m s]
     (let [[m] (coerce-return m (effect nil))]
       #?(:clj ((.effect-fn m) s)
          :cljs (.effect-fn m s))))
  ([m k v & kvs]
     (run m (apply hash-map k v kvs))))

(extend-type Effect
  Functor
  (mapf* [this f] (effect (fn [s] (update (run this s) :result f))))
  Applicative
  (return* [this a] (effect (fn [s] (assoc s :result a))))
  (ap* [this a]
    (effect (fn [s]
              (let [{f :result :as s} (run this s)
                    s (run a s)]
                (update s :result f)))))
  Monad
  (join* [this]
    (effect (fn [s]
              (let [{m :result :as s} (run this s)]
                (run m s))))))

(def env
  (effect (fn [s] (assoc s :result (:env s)))))

(def state
  (effect (fn [s] (assoc s :result (:state s)))))

(defn put [ns]
  (effect (fn [s] (assoc s :state ns :result ns))))

(defn modify [f & args]
  (effect (fn [s]
            (let [ns (apply f (:state s) args)]
              (assoc s :state ns :result ns)))))

(defn log [m]
  (effect (fn [s]
            (-> (update s :log conj m)
                (assoc :result nil)))))

(defn local [m & kvs]
  (effect (fn [s]
            (let [s' (reduce (fn [s [k v]]
                               (update s k v))
                             s
                             (partition 2 kvs))]
              (merge s (select-keys (run m s') [:result :state]))))))