package no.nav.familie.pdf.pdf.språkKonfigurasjon

object SpråkKontekst {
    private val språk = ThreadLocal.withInitial { "nb" }

    fun brukSpråk(): String = språk.get()

    fun setSpråk(språk: String) {
        this.språk.set(språk)
    }

    fun fjernSpråk() {
        språk.remove()
    }
}
