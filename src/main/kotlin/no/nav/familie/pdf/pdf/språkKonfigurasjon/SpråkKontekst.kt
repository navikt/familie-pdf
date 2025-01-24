package no.nav.familie.pdf.pdf.språkKonfigurasjon

object SpråkKontekst {
    private val aktivtSpråk = ThreadLocal.withInitial { "nb" }

    fun brukSpråk(): String = aktivtSpråk.get()

    fun settSpråk(språk: String) {
        this.aktivtSpråk.set(språk)
    }

    fun tilbakestillSpråk() {
        aktivtSpråk.remove()
    }
}
