package no.nav.familie.pdf.infrastruktur

import no.nav.familie.unleash.UnleashService
import org.springframework.stereotype.Service

@Service
class UnleashNextService(
    private val unleashService: UnleashService,
) {
    fun isEnabled(toggle: Toggle): Boolean =
        unleashService.isEnabled(
            toggleId = toggle.toggleId,
        )
}

enum class Toggle(
    val toggleId: String,
    val beskrivelse: String? = null,
) {
    // Operational
    FJERN_TABS_FRA_SØKNAD(toggleId = "familie-pdf.fjern-tabs-fra-soknad", beskrivelse = "Operarional"),
}
