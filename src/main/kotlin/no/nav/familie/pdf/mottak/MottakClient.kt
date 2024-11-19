package no.nav.familie.pdf.mottak

import no.nav.familie.http.client.AbstractRestClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import org.springframework.web.util.DefaultUriBuilderFactory

@Service
class MottakClient(
    @Qualifier("restTemplateUnsecured") operations: RestOperations,
) : AbstractRestClient(operations, "pdf") {
    fun helsesjekk(): String {
        val helsesjekkUrl =
            DefaultUriBuilderFactory().uriString("http://localhost:8092").path("api/mottak/helsesjekk").build()
        val response: String =
            getForEntity(
                helsesjekkUrl,
                HttpHeaders().medContentTypeJsonUTF8(),
            )
        return response
    }
}
