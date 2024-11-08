package no.nav.familie.pdf.søknad

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/soknad")
class SøknadController {

    @GetMapping("/overgangsstonad")
    fun lagPdfForOvergangsstønad(): String = "OK - overgangsstønad"
}