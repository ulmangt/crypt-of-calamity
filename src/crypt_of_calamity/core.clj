(ns crypt-of-calamity.core
  (:import (java.awt Color Dimension Graphics)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener MouseListener))
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

(defn roll-stat
  "a random number generator for creating stat values"
  ([max] (+ 1 (. rnd nextInt max)))
  ([] (roll-stat 20)))


(defn roll-stats
	"returns a map containing each stat and a randomly generated value for that stat"
	[class]
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

(defn element-list
	"helper function for cross-join example: (element-list 1 '(1 2 3)) -> ((1 1) (1 2) (1 3))"
	[n l]
 	 (map #(list n %) l))

(defn list-list
[l1 l2]
  (apply concat (map #(element-list % l2) l1)))

(defn cross-join [& lists]
  (map flatten (reduce list-list lists)))

(defn create-room
  ([width height] (create-room [0 0] width height))
  ([[x y] width height]
    (map #(struct-map location :coords % :things #{})
          (cross-join (range x (+ width x))
                      (range y (+ height y))))))

; creates a rectangular room - represented by a list of locations
; walls are added to the outside of the room, so a 3 x 3 room returns 5 x 5 locations
(defn create-room-with-walls
  ([width height] (map #(let [x (first %) y (second %)]
                             (if (or (= 0 x) (= 0 y) (= (+ width 1) x) (= (+ height 1) y))
                                  (struct-map location :coords % :things #{:wall})
                                  (struct-map location :coords % :things #{})))
                        (cross-join (range (+ 2 width)) (range (+ 2 height)))))
  ([] (create-room-with-walls 10 10)))

; test whether a location is a wall
; i.e. whether its :things set contains a :wall
(defn wall? [{:keys [things]}] (:wall things))

; tests whether a location is at the given coords
(defn at-coords? [query-coords {:keys [coords]}]
  (= query-coords coords))

; returns the location in a dungeon matching the given coords
; example: (get-location [1 1] dungeon)
(defn get-location [coords dungeon]
  (filter #(at-coords? coords %) dungeon))

(defn has-location? [coords dungeon]
  (not (empty? (get-location coords dungeon))))

(defn edges [coords dungeon]
  (filter #(not (has-location? (add-points coords (% dirs)) dungeon)) (keys dirs)))

(defn edge? [coords dungeon]
  (not (empty? (edges coords dungeon))))

; returns a random element from list
(defn get-random-element [list]
  (last (take (+ (. rnd nextInt (count list)) 1) list)))

; returns a random wall location from a dungeon
(defn rand-wall [dungeon]
  (get-random-element (filter wall? dungeon)))

(defn add-room [dungeon width height]
  (concat dungeon 
          (create-room 
            (get-random-element 
              (filter #(edge? % dungeon)
                      (map :coords dungeon)))
            width height)))




(defn render [ #^Graphics g w h ]
	(doto g
		(.setColor (Color/black))
		(.fillRect 0 0 w h)))

(defn create-dungeon-panel []
	"Create a panel which renders a dungeon object"
	(proxy [JPanel] []
		(paintComponent [g]
			(proxy-super paintComponent g)
			(render g (. this getWidth) (. this getHeight)))))

(defn crypt-of-calamity []
	(let [frame (JFrame. "Crypt of Calamity")
				dungeon-panel (create-dungeon-panel)]
		(.addMouseListener dungeon-panel
			(proxy [MouseListener] []
				(mouseClicked [event]
					(println (str "clicked")))
				(mouseEntered [event])
				(mouseExited [event])
				(mousePressed [event])
				(mouseReleased [event])))
		(doto frame
			(.add dungeon-panel)
			(.setSize 800 600)
			(.setVisible true))))

