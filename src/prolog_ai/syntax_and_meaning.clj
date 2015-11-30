(ns prolog-ai.syntax-and-meaning
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer [== conde fresh lvar run defne]]
            [midje.sweet :refer :all]))


;;; state [monkey-horizontal monkey-vertical box-position banana-status]
;;; moves [grasp climb push walk]


(defn moveo
  [state-1 move state-2]
  (conde
   [(== state-1 [:middle :on-box :middle :has-not])
    (== move :grasp)
    (== state-2 [:middle :on-box :middle :has])]

   [(fresh [pos has]
      (== state-1 [pos :on-floor pos has])
      (== move :climb)
      (== state-2 [pos :on-box pos has]))]

   [(fresh [pos-1 pos-2 has]
      (== state-1 [pos-1 :on-floor pos-1 has])
      (== move [:push pos-1 pos-2])
      (== state-2 [pos-2 :on-floor pos-2 has]))]

   [(fresh [pos-1 pos-2 box has]
      (== state-1 [pos-1 :on-floor box has])
      (== move [:walk pos-1 pos-2])
      (== state-2 [pos-2 :on-floor box has]))]))


(defn can-geto
  [state]
  (conde
   [(== state [(lvar) (lvar) (lvar) :has])] ; lvar is a wildcard

   [(fresh [move state-2]
      (moveo state move state-2)
      (can-geto state-2))]))


(facts
 ;; can the monkey get the banana?

 (run 1 [q]
   (can-geto [:at-door :on-floor :at-window :has-not])
   (== q true))
 => [true])


;;; Using defne instead


(defne moveo
  [state-1 move state-2]
  ([[:middle :on-box :middle :has-not]
    :grasp
    [:middle :on-box :middle :has]])

  ([[pos :on-floor pos has]
    :climb
    [pos :on-box pos has]])

  ([[pos-1 :on-floor pos-1 has]
    [:push pos-1 pos-2]
    [pos-2 :on-floor pos-2 has]])

  ([[pos-1 :on-floor box has]
    [:walk pos-1 pos-2]
    [pos-2 :on-floor box has]]))


(defne can-geto
  [state]
  ([[_ _ _ :has]])

  [[_]
   (fresh
     (moveo state move state-2)
     (can-geto state-2))])


(facts
 ;; can the monkey still get the banana?

 (run 1 [q]
   (can-geto [:at-door :on-floor :at-window :has-not])
   (== q true))
 => [true])
