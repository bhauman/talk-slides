(ns slides.present)

(defmacro defslide [nm bindings body]
  `(defmethod slides.core/slide ~(keyword nm) [_# state# pos#]
     (let [~bindings [state#]]
       (section ~(name nm) state# pos#
                (sab/html ~body)))))
