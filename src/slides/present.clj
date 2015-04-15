(ns slides.present)

(defmacro defslide [nm bindings & body]
  `(defmethod slides.core/slide ~(keyword nm) [_# state#]
     (let [~bindings [state#]]
       (section ~(name nm) state#
                (sab/html ~@body)))))



