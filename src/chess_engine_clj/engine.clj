(ns chess-engine-clj.engine
    (:require [clojure.string :as string]))

(def initial-board [[\r \n \b \q \k \b \n \r]
                    [\p \p \p \p \p \p \p \p]
                    [\- \- \- \- \- \- \- \-]
                    [\- \- \- \- \- \- \- \-]
                    [\- \- \- \- \- \- \- \-]
                    [\- \- \- \- \- \- \- \-]
                    [\P \P \P \P \P \P \P \P]
                    [\R \N \B \Q \K \B \N \R]])

(def board-state (atom {:board initial-board
                        :turn "white"
                        :moves-cnt 0
                        :white-can-castle-ks true
                        :white-can-castle-qs true
                        :black-can-castle-ks true
                        :black-can-castle-qs true}))

(def uni-pieces {\R "♜", \N "♞", \B "♝", \Q "♛", \K "♚", \P "♟",
                 \r "♖", \n "♘", \b "♗", \q "♕", \k "♔", \p "♙", \- "·"})

;;-----Generating Moves ----------
;;Black is engine always
(def N -8)
(def E 1)
(def S 8)
(def W -1)
(def directions {"p" [S (+ S S) (+ S W) (+ S E)]
                 "n" [(+ N N E) (+ E N E) (+ E S E) (+ S S E) (+ S S W) (+ W S W) (+ W N W) (+ N N W)]
                 "b" [(+ N E) (+ S E) (+ N W) (+ S W)]
                 "r" [N E S W]
                 "q" [N E S W (+ N E) (+ S E) (+ N W) (+ S W)]
                 "k" [N E S W (+ N E) (+ S E) (+ N W) (+ S W)]})


(defn black-piece-location []
  "Returns the indices for location of black pieces"
  (filter (comp #(Character/isLowerCase %)
                #(nth (:board @board-state) %))
           (range 64)))

;;Dummy List
(def engine-valid-move-list ["a7a5" "b7b5" "c7c5" "d7d5" "e7e5" "f7f5" "g7g5" "h7h5"])

(defn engine-move-pick []
    "Picks a random move from the engine-valid-move-list"
    (->> (count engine-valid-move-list)
         (rand-int)
         (get engine-valid-move-list)))

;;-----Printing the Board at Command Line-----

(defn line-convert [l-vec]
    (->> (map uni-pieces l-vec)
         (map #(str % " "))
         string/join))

(defn pretty-print []
    (->> (:board @board-state)
         (map line-convert)
         (map #(str (- 8 %1) %2) (range 8))
         (string/join "\n")
         (#(str % "\n a b c d e f g h\n\n"))))

(println (pretty-print))

;;------------------------------
(defn parse-square [square-id]
    (let [[fil-id rank] [(subs square-id 0 1) (Integer/parseInt (subs square-id 1))]
          fil (->> [fil-id "a"]
                   (map #(int (.charAt % 0)))
                   (reduce -))]
        [(- 8 rank) fil]))

(defn make-move [first-id second-id]
    (let [[i1 j1] first-id
          [i2 j2] second-id
          moving-piece (get-in @board-state [:board i1 j1])]
          (swap! board-state assoc-in [:board i1 j1] \-)
          (swap! board-state assoc-in [:board i2 j2] moving-piece))
    (println (pretty-print)))

(defn read-move [move]
    (let [[full-move s1 s2] (re-find #"^([a-h][1-8])([a-h][1-8])$" move)]
        (make-move (parse-square s1) (parse-square s2))))

(defn game-play []
    (println "Your Move: ")
    (let [user-move (string/trim (read-line))]
      (read-move user-move))
    (Thread/sleep 2000)
    (let [engine-move (engine-move-pick)]
      (println (str "My Move: " engine-move "\n"))
      (read-move engine-move)))
