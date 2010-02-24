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

(defn roll-stat
  ([max] (+ 1 (. rnd nextInt max)))
  ([] (roll-stat 20)))

(defn roll-stats [class]
  (apply hash-map
         (interleave stats
                     (take (count stats)
                           (repeatedly roll-stat)))))

(defn add-points [& pts]
  (vec (apply map + pts)))

(defstruct character :name :class :location :stats)

(defn create-character [name class]
  (struct-map character :name name
                        :class class 
                        :location [0 0] 
                        :stats (roll-stats class)))



