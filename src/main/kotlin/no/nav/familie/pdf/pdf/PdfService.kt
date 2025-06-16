package no.nav.familie.pdf.pdf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.infrastruktur.Toggle
import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.PDFdokument.lagSøknadskvittering
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.unleash.UnleashService
import org.springframework.stereotype.Service

@Service
class PdfService(
    private val unleashNextService: UnleashNextService
) {
    fun opprettPdf(
        feltMap: FeltMap,
        v2: Boolean = false,
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(byteArrayOutputStream)

        /**
         * Dato: 16.06.2025 / Kristian Kofoed
         * Vi jobber med å utbedre felt og validering i søknadsdialogen. Dette er en større jobb, der det dessverre -
        er mulig å sende inn søknader med tabs, som brekker prossessering. Vi feature toggler muligheten for å fjerne -
        tabs siden dette har skjedd før og vi ikke ønsker å reverte implementasjonen hver gang det skjer. Denne kodens skal fjernes herifra. */

        val featureToggle = Toggle.FJERN_TABS_FRA_SØKNAD
        val lagSøknadUtenTabs = unleashNextService.isEnabled(featureToggle)

        if (lagSøknadUtenTabs) {
            val feltMapJson = jacksonObjectMapper().writeValueAsString(feltMap)
            val feltMapJsonUtenTabs = feltMapJson.replace("\\t", "")
            val feltMapUtenTabs = jacksonObjectMapper().readValue(feltMapJsonUtenTabs, FeltMap::class.java)

            lagSøknadskvittering(pdfADokument = pdfADokument, feltMap = feltMapUtenTabs, v2 = v2)
        } else {
            lagSøknadskvittering(pdfADokument = pdfADokument, feltMap = feltMap, v2 = v2)
        }

        return byteArrayOutputStream.toByteArray()
    }
}
