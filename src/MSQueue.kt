import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val dummy = Node<E>(null)
    private val head: AtomicReference<Node<E>> = AtomicReference(dummy)
    private val tail: AtomicReference<Node<E>> = AtomicReference(dummy)

    override fun enqueue(element: E) {
        val node = Node(element)
        while (true) {
            val tail = this.tail.get()
            val next = tail.next.get()
            if (tail == this.tail.get()) {
                if (next == null) {
                    if (tail.next.compareAndSet(next, node)) {
                        break
                    }
                } else {
                    this.tail.compareAndSet(tail, next)
                }
            }
        }
        this.tail.compareAndSet(tail.get(), node)
    }

    override fun dequeue(): E? {
        while (true) {
            val head = this.head.get()
            val tail = this.tail.get()
            val next = head.next.get()
            if (head == this.head.get()) {
                if (head == tail) {
                    if (next == null) {
                        return null
                    }
                    this.tail.compareAndSet(tail, next)
                } else {
                    val value = next?.element
                    if (this.head.compareAndSet(head, next)) {
                        return value
                    }
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
