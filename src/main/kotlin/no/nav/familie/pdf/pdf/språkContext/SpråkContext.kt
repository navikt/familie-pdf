package no.nav.familie.pdf.pdf.språkContext

object SpråkContext {
    private val språk = ThreadLocal.withInitial { "nb" }

    fun brukSpråk(): String = språk.get()

    fun setSpråk(språk: String) {
        this.språk.set(språk)
    }

    fun fjernSpråk() {
        språk.remove()
    }
}
