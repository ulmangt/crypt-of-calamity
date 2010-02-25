(ns net.cc.crypt-of-calamity
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:use clojure.contrib.import-static
        [clojure.contrib.seq-utils :only (includes?)]))

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

(def rnd (new java.util.Random (. java.lang.System currentTimeMillis)))

(def dirs { :up    [ 0  1]
            :down  [ 0 -1]
            :left  [-1  0]
            :right [ 1  0]})

(def dir-keys { VK_UP     :up
               VK_DOWN   :down
               VK_LEFT   :left
               VK_RIGHT  :right })

(def classes '(:wizard
               :alchemist
               :baker
               :jewler
               :builder
               :warrior
               :scribe
               :gypsy
               :barrister
               :minstrel
               :jones))

(def stats '(:strength
             :stamina
             :wisdom
             :intelligence
             :dexterity
             :charisma
             :luck))

; an object in the dungeon (item, monster, wall, door...)
; type - keyword
(defstruct thing :type)

; coords - point - a two element [x y] vector
; things - list - a list of things in the location
(defstruct location :coords :things)

; name - string - the character's name
; class - keyword - from the classes list
; location - the character's location
; stats - map - a map of the form generated by roll-stats
(defstruct character :name :class :location :stats)

; a random number generator for creating stat values
(defn roll-stat
  ([max] (+ 1 (. rnd nextInt max)))
  ([] (roll-stat 20)))

; returns a map containing each stat and a randomly generated value for that stat
(defn roll-stats [class]
  (apply hash-map
         (interleave stats
                     (take (count stats)
                           (repeatedly roll-stat)))))

(defn add-points [& pts]
  (vec (apply map + pts)))

(defn create-character [name class]
  (struct-map character :name name
                        :class class 
                        :location [0 0] 
                        :stats (roll-stats class)))

(defn pair-seq [max-x] 
  (iterate (fn [[x y]] (if (< x (- max-x 1))
                           [(+ x 1) y]
                           [0 (+ y 1)]))
           [0 0]))

; creates (* x y) points ranging from [0 0] to [(- x 1) (- y 1)]
(defn pairs [x y] (take (* x y) (pair-seq x)))

(defn pairs-old [x y] 
  (map vec 
       (partition 2 
                  (interleave (apply concat
                                     (map #(repeat y %)
                                           (range x)))
                              (take (* x y) (cycle (range y)))))))

; creates a rectangular room - represented by a list of locations
; walls are added to the outside of the room, so a 3 x 3 room returns 5 x 5 locations
(defn create-room
  ([width height] (map #(let [x (first %) y (second %)]
                             (if (or (= 0 x) (= 0 y) (= (+ width 1) x) (= (+ height 1) y))
                                  (struct-map location :coords % :things '(:wall))
                                  (struct-map location :coords %)))
                        (pairs (+ 2 width) (+ 2 height))))
  ([] (create-room 10 10)))
