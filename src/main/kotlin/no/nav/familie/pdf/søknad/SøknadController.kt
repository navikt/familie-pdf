package no.nav.familie.pdf.søknad

import no.nav.familie.sikkerhet.EksternBrukerUtils
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/soknad")
@ProtectedWithClaims(
    issuer = EksternBrukerUtils.ISSUER_TOKENX,
    claimMap = ["acr=Level4"],
)
class SøknadController {
    @GetMapping("/overgangsstonad")
    fun lagPdfForOvergangsstønad(): String = "OK - overgangsstønad"
}
