(ns net.cc.crypt-of-calamity
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:use clojure.contrib.import-static
        [clojure.contrib.seq-utils :only (includes?)]))
(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

(def dirs { :up    [ 0  1]
            :down  [ 0 -1]
            :left  [-1  0]
            :right [ 1  0]})

(def dir-keys { VK_UP     :up
               VK_DOWN   :down
               VK_LEFT   :left
               VK_RIGHT  :right })

(defn add-points [& pts]
  (vec (apply map + pts)))


