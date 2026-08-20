package no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import no.nav.familie.pdf.infrastruktur.Toggle
import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.PDFdokument.lagPdfADocument
import no.nav.familie.pdf.pdf.PDFdokument.lagSøknadskvittering
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.visningsvarianter.addWatermarkToPdf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tools.jackson.module.kotlin.jacksonMapperBuilder

@Service
class PdfService(
    private val unleashNextService: UnleashNextService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun opprettPdf(
        feltMap: FeltMap,
        version: Int = 1,
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfADokument = lagPdfADocument(feltMap = feltMap, byteArrayOutputStream = byteArrayOutputStream)

        /**
         * Dato: 16.06.2025 / Kristian Kofoed
         * Vi jobber med å utbedre felt og validering i søknadsdialogen. Dette er en større jobb, der det dessverre -
         er mulig å sende inn søknader med tabs, som brekker prossessering. Vi feature toggler muligheten for å fjerne -
         tabs siden dette har skjedd før og vi ikke ønsker å reverte implementasjonen hver gang det skjer. Denne kodens skal fjernes herifra. */

        val featureToggle = Toggle.FJERN_TABS_FRA_SØKNAD
        val lagSøknadUtenTabs = unleashNextService.isEnabled(featureToggle)

        if (lagSøknadUtenTabs) {
            logger.info("Fant feature toggle for fjerning av tabs. Lager søknad uten tabs.")
            val mapper = jacksonMapperBuilder().build()
            val feltMapJson = mapper.writeValueAsString(feltMap)
            val feltMapJsonUtenTabs = feltMapJson.replace("\\t", "")
            val feltMapUtenTabs = mapper.readValue(feltMapJsonUtenTabs, FeltMap::class.java)

            lagSøknadskvittering(pdfADokument = pdfADokument, feltMap = feltMapUtenTabs, version = version)
        } else {
            lagSøknadskvittering(pdfADokument = pdfADokument, feltMap = feltMap, version = version)
        }
        logger.info("Version: $version, vannemerke: ${feltMap.vannmerke}")
        if (version < 3 || feltMap.vannmerke.isNullOrBlank()) {
            return byteArrayOutputStream.toByteArray()
        }
        logger.info("Legger til vannmerke i PDF")
        return addWatermarkToPdf(byteArrayOutputStream.toByteArray(), feltMap.vannmerke)
    }
}
