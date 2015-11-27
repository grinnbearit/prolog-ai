(ns prolog-ai.introduction-to-prolog
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer [== run* s# u# run fresh !=]]
            [clojure.core.logic.pldb :as db]
            [midje.sweet :refer :all]))

;;; Defining Relations by Facts

(db/db-rel parento p1 p2)

(def plaidb
  (db/db
   [parento :pam :bob]
   [parento :tom :bob]
   [parento :tom :liz]
   [parento :bob :ann]
   [parento :bob :pat]
   [parento :pat :jim]))


(facts

 (db/with-db plaidb
   (run* [q]
     (parento :bob :pat)
     (== true q)))
 => [true]


 (db/with-db plaidb
   (run* [q]
     (parento :liz :pat)
     (== true q)))
 => []


 (db/with-db plaidb
   (run* [q]
     (parento :tom :ben)))
 => []


 (db/with-db plaidb
   (run* [x]
     (parento x :liz)))
 => [:tom]


 (db/with-db plaidb
   (run* [x]
     (parento :bob x)))
 => [:pat :ann]


 (db/with-db plaidb
   (run 3 [x y]
     (parento x y)))
 => [[:bob :pat]
     [:tom :liz]
     [:pat :jim]]


 (db/with-db plaidb
   (run* [x]
     (fresh [y]
       (parento y :jim)
       (parento x y))))
 => [:bob]


 (db/with-db plaidb
   (run* [x]
     (fresh [y]
       (parento :tom y)
       (parento y x))))
 => [:pat :ann]


 (db/with-db plaidb
   (run* [x]
     (parento x :ann)
     (parento x :pat)))
 => [:bob])


;;; questions
;;; 1.

(facts

 (db/with-db plaidb
   (run* [x]
     (parento :jim x)))
 => []


 (db/with-db plaidb
   (run* [x]
     (parento x :jim)))
 => [:pat]


 (db/with-db plaidb
   (run* [x]
     (parento :pam x)
     (parento x :pat)))
 => [:bob]


 (db/with-db plaidb
   (run* [x y]
     (parento :pam x)
     (parento x y)
     (parento y :jim)))
 => [[:bob :pat]])

;;; 2.

(facts

 (db/with-db plaidb
   (run* [x]
     (parento x :pat)))
 => [:bob]


 (db/with-db plaidb
   (run* [x]
     (parento :liz x)))
 => []


 (db/with-db plaidb
   (run* [x]
     (fresh [y]
       (parento y :pat)
       (parento x y))))
 => [:pam :tom])

;;; Defining Relations by Rules

(db/db-rel maleo x)
(db/db-rel femaleo y)

(def plaidb-2
  (-> plaidb
      (db/db-fact femaleo :pam)
      (db/db-fact maleo :tom)
      (db/db-fact maleo :bob)
      (db/db-fact femaleo :liz)
      (db/db-fact femaleo :pat)
      (db/db-fact femaleo :ann)
      (db/db-fact maleo :jim)))


(defn offspringo
  [x y]
  (parento y x))


(facts

 (db/with-db plaidb-2
   (run* [q]
     (offspringo :liz :tom)
     (== true q)))
 => [true])


(defn mothero
  [x y]
  (parento x y)
  (femaleo x))


(defn grandparento
  [x y]
  (fresh [z]
    (parento x z)
    (parento z y)))


(defn sistero
  [x y]
  (fresh [z]
    (parento z x)
    (parento z y)
    (femaleo x)))

(facts

 (db/with-db plaidb-2
   (run* [q]
     (sistero :ann :pat)
     (== true q)))
 => [true])


(facts

 (db/with-db plaidb-2
   (run* [x]
     (sistero x :pat)))
 => [:pat :ann])


(defn differento
  [x y]
  (!= x y))


(defn sistero
  [x y]
  (fresh [z]
    (parento z x)
    (parento z y)
    (femaleo x)
    (differento x y)))


(facts

 (db/with-db plaidb-2
   (run* [x]
     (sistero x :pat)))
 => [:ann])


(defn has-childo
  [x]
  (fresh [y]
    (parento x y)))

;;; questions
;;; 1.

(defn happyo
  [x]
  (has-childo x))


(defn has-two-childreno
  [x]
  (fresh [y z]
    (parento x y)
    (sistero y z)))


(facts

 (db/with-db plaidb-2
   (run 1 [q]
     (fresh [x]
       (happyo :jim)
       (== true q))))
 => []


 (db/with-db plaidb-2
   (run 1 [q]
     (fresh [x]
       (happyo :bob)
       (== true q))))
 => [true]


 (db/with-db plaidb-2
   (run 1 [q]
     (has-two-childreno :pat)
     (== true q)))
 => []


 (db/with-db plaidb-2
   (run 1 [q]
     (has-two-childreno :bob)
     (== true q)))
 => [true])

;;; 2.

(defn grandchildo
  [x y]
  (fresh [z]
    (parento z x)
    (parento y z)))


(facts

 (db/with-db plaidb-2
   (run* [x]
     (grandchildo :jim x)))
 => [:bob])

;;; 3.

(defn aunto
  [x y]
  (fresh [z]
    (parento z y)
    (sistero x z)))


(facts

 (db/with-db plaidb-2
   (run* [x]
     (aunto :liz x)))
 => [:pat :ann])
