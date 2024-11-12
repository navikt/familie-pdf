package no.nav.familie.pdf.mottak

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/pdf")
@Unprotected
class PdfController(
    val mottakClient: MottakClient,
) {
    @GetMapping("/helsesjekk")
    fun girSvarTilMottak(): String = "OK - overgangsstønad"

    @GetMapping("/mottak/helsesjekk")
    fun testerMottakOk(): String = mottakClient.helsesjekk()
}
