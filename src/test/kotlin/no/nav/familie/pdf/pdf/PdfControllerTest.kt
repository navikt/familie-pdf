package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.tagging.IStructureNode
import com.itextpdf.kernel.pdf.tagging.PdfMcr
import com.itextpdf.kernel.pdf.tagging.PdfObjRef
import com.itextpdf.kernel.pdf.tagging.PdfStructElem
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot
import io.mockk.mockk
import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.domain.PdfStandard
import no.nav.familie.pdf.pdf.lokalKjøring.LokalPdfController
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals

internal class PdfControllerTest {
    private val unleashNextService: UnleashNextService = mockk(relaxed = true)
    private val pdfController = LokalPdfController(unleashNextService)
    private val skrivTilFile = false

    @Test
    fun `generer pdf for gravferdsstønad`() {
        val textByPage = `Test av skjema`("/gravferdsstønad.json", "delme-1.pdf")

        assertTrue(textByPage[1].contains("I tillegg til dette skjemaet"))
    }

    @Test
    fun `generer pdf for arbeid- og utdanningssøknad `() {
        val textByPage = `Test av skjema`("/arbeid-utdannings-søknad.json", "delme-2.pdf")

        assertTrue(textByPage[2].contains("Legeerklæringen må inneholde:"))
    }

    @Test
    fun `generer pdf for avtale om medfinansiering`() {
        val textByPage = `Test av skjema`("/avtale-om-medfinansiering.json", "delme-3.pdf")

        assertTrue(textByPage[0].contains("Forskriften §14-1"))
    }

    @Test
    fun `generer pdf for barnetilsyn`() {
        val textByPage = `Test av skjema`("/barnetilsyn.json", "delme-4.pdf")

        assertTrue(textByPage[1].contains("Nei, jeg bor alene med barn eller jeg er gravid og bor alene"))
    }

    @Test
    fun `generer pdf for overgangsstønad`() {
        val textByPage = `Test av skjema`("/overgangsstønad.json", "delme-5.pdf")

        assertTrue(textByPage[2].contains("Nei, jeg bor alene med barn eller jeg er gravid og bor alene"))
    }

    @Test
    fun `generer pdf for avklaringspenger utland`() {
        val textByPage = `Test av skjema`("/avklaringspenger-under-opphold-utland.json", "delme-6.pdf")

        assertTrue(
            textByPage[0].contains(
                "Jeg bekrefter med dette at utenlandsoppholdet ikke er til hinder for avtalt aktivitet som \n" +
                    "behandling, arbeidsrettede tiltak eller oppfølging fra NAV.",
            ),
        )
    }

    private fun `Test av skjema`(
        jsonFile: String,
        skrivTilFil: String?,
    ): List<String> {
        val søknad =
            no.nav.familie.pdf.pdf.lokalKjøring.JsonLeser
                .lesSøknadJson(jsonFile)

        val pdfResponse = pdfController.opprettPdfMedValidering(søknad)

        assertNotNull(pdfResponse)

        val textByPage = mutableListOf<String>()

        ByteArrayInputStream(pdfResponse.pdf).use { inputStream ->
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)

            for (i in 1..pdfDocument.numberOfPages) {
                val page = pdfDocument.getPage(i)
                val text = PdfTextExtractor.getTextFromPage(page)
                textByPage.add(text)
            }

            pdfDocument.close()
        }

        if (skrivTilFil != null) {
            writeBytesToFile(pdfResponse.pdf, skrivTilFil)
        }

        val tt = pdfResponse.standarder

        assertEquals(true, tt.get(PdfStandard.FOUR)?.samsvarer, "Verifisering av PDF/A-4 støtte feilet")
        if (tt.get(PdfStandard.UA2)?.samsvarer == false) {
            dumpPdfStructure(pdfResponse.pdf)
        }
        assertEquals(true, tt.get(PdfStandard.UA2)?.samsvarer, "Verifisering av PDF-UA2 støtte feilet, pga. ${tt.get(PdfStandard.UA2)?.feiletRegel}")

        return textByPage
    }

    fun writeBytesToFile(
        byteArray: ByteArray,
        filePath: String,
    ) {
        if (skrivTilFile) {
            val outputStream = FileOutputStream(filePath)
            outputStream.write(byteArray)
            outputStream.close()
        }
    }

    /**
     * Dump structure tree from a PDF file path.
     */
    fun dumpPdfStructure(pdfArray: ByteArray) {
        ByteArrayInputStream(pdfArray).use { inputStream ->
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)

            dumpPdfStructure(pdfDocument)

            pdfDocument.close()
        }
    }

    /**
     * Dump structure tree from a PdfDocument instance.
     */
    fun dumpPdfStructure(pdf: PdfDocument) {
        val root = pdf.structTreeRoot
        println("===== STRUCTURE TREE =====")
        if (root == null) {
            println("No structTreeRoot (document not tagged?)")
            return
        }
        dumpNode(root, 0, mutableSetOf())
    }

    /**
     * Recursive dumper for IStructureNode trees.
     * Uses a visited set to avoid infinite loops on circular references.
     */
    private fun dumpNode(
        node: IStructureNode,
        indent: Int,
        visited: MutableSet<Int>,
    ) {
        val pad = " ".repeat(indent)
        val id = System.identityHashCode(node)
        if (visited.contains(id)) {
            println("$pad<circular-ref to ${node.javaClass.simpleName}>")
            return
        }
        visited.add(id)

        when (node) {
            is PdfStructTreeRoot -> {
                println("${pad}StructTreeRoot")
                val kids = node.kids
                if (kids.isNullOrEmpty()) {
                    println("$pad  (no kids)")
                } else {
                    kids.forEach { kid -> dumpNodeKid(kid, indent + 2, visited) }
                }
            }

            is PdfStructElem -> {
                val role = safeRoleString(node)
                println("${pad}Elem: /$role  (obj=${node.getPdfObject().hashCode()})")

                // attributes, if any (show keys only)
                val attrs = node.attributesList
                if (!attrs.isNullOrEmpty()) {
                    println("$pad  Attributes: ${attrs.map { it.javaClass.simpleName }}")
                }

                val kids = node.kids
                if (kids.isNullOrEmpty()) {
                    println("$pad  (no kids)")
                } else {
                    kids.forEach { kid -> dumpNodeKid(kid, indent + 2, visited) }
                }
            }

            is PdfMcr -> {
                // mcid may be accessible via property or method; try common names
                val mcid = safeGetMcrMcid(node)
                println("${pad}MCR (mcid=$mcid)")
            }

            is PdfObjRef -> {
                println("${pad}ObjRef -> (class=${node.javaClass.simpleName})")
                val referenced = safeGetReferencedFromObjRef(node)
                if (referenced != null) {
                    println("$pad  resolved ->")
                    dumpNodeKid(referenced, indent + 4, visited)
                } else {
                    println("$pad  could not resolve referenced target (no known accessor)")
                }
            }

            else -> {
                // fallback: unknown IStructureNode implementation
                println("${pad}${node.javaClass.simpleName}")
                // try to list kids generically if possible (reflectively)
                try {
                    val kidsProp = node::class.declaredFunctions.find { it.name.equals("getKids", true) }
                    if (kidsProp != null) {
                        kidsProp.isAccessible = true
                        val kids = kidsProp.call(node) as? List<*>
                        kids?.forEach { kid ->
                            if (kid is IStructureNode) {
                                dumpNodeKid(kid, indent + 2, visited)
                            } else {
                                println("$pad  - ${kid?.javaClass?.simpleName}")
                            }
                        }
                    }
                } catch (t: Throwable) {
                    // ignore
                }
            }
        }
    }

    /**
     * Helper: dump a kid which might be a PdfStructElem, PdfMcr, PdfObjRef or PdfStructTreeRoot
     */
    private fun dumpNodeKid(
        kid: Any?,
        indent: Int,
        visited: MutableSet<Int>,
    ) {
        when (kid) {
            is IStructureNode -> dumpNode(kid, indent, visited)
            is PdfMcr -> {
                val pad = " ".repeat(indent)
                println("${pad}MCR (mcid=${safeGetMcrMcid(kid)})")
            }

            is PdfObjRef -> {
                val pad = " ".repeat(indent)
                println("${pad}ObjRef ->")
                val referenced = safeGetReferencedFromObjRef(kid)
                if (referenced != null) {
                    dumpNodeKid(referenced, indent + 2, visited)
                } else {
                    println("$pad  (could not resolve ObjRef)")
                }
            }

            else -> {
                val pad = " ".repeat(indent)
                println("${pad}${kid?.javaClass?.simpleName ?: "null"} : $kid")
            }
        }
    }

    /**
     * Try several possible method names to fetch referenced node from PdfObjRef.
     * Many iText builds use different names; we try common ones and return the first that looks like an IStructureNode.
     */
    private fun safeGetReferencedFromObjRef(objRef: PdfObjRef): Any? {
        val possibleNames = listOf("getReference", "getRef", "getReferencedElement", "getReferencedObject", "getRefTo", "getRefElement")
        for (name in possibleNames) {
            try {
                val method = objRef::class.java.methods.find { it.name == name && it.parameterCount == 0 }
                if (method != null) {
                    method.isAccessible = true
                    val res = method.invoke(objRef)
                    if (res is IStructureNode || res is PdfMcr || res is PdfObjRef) {
                        return res
                    }
                    // also accept PdfObject wrappers that might reference struct elems
                    if (res != null) return res
                }
            } catch (_: Throwable) {
            }
        }
        // Last resort: try reflectively inspect declared functions via kotlin reflect
        try {
            objRef::class.declaredFunctions.forEach { kf ->
                if (kf.name.contains("ref", true) || kf.name.contains("get", true)) {
                    try {
                        kf.isAccessible = true
                        val res = kf.call(objRef)
                        if (res is IStructureNode || res is PdfMcr || res is PdfObjRef) return res
                    } catch (_: Throwable) {
                        // ignore
                    }
                }
            }
        } catch (_: Throwable) {
            // ignore
        }

        return null
    }

    /** Try common accessors for mcid */
    private fun safeGetMcrMcid(mcr: PdfMcr): Int? {
        val candidates = listOf("getMcid", "mcid", "getMCID", "getMCRId")
        for (name in candidates) {
            try {
                val method = mcr::class.java.methods.find { it.name == name && it.parameterCount == 0 }
                if (method != null) {
                    method.isAccessible = true
                    val r = method.invoke(mcr)
                    if (r is Number) return r.toInt()
                }
            } catch (_: Throwable) {
                // ignore
            }
        }
        // fallback: try kotlin reflection
        try {
            mcr::class.declaredFunctions.forEach { kf ->
                if (kf.name.equals("mcid", true) || kf.name.contains("mcid", true)) {
                    try {
                        kf.isAccessible = true
                        val res = kf.call(mcr)
                        if (res is Number) return res.toInt()
                    } catch (_: Throwable) {
                        // ignore
                    }
                }
            }
        } catch (_: Throwable) {
            // ignore
        }
        return null
    }

    /** Safe role string getter (handles different API shapes) */
    private fun safeRoleString(elem: PdfStructElem): String =
        try {
            val role = elem.role
            // role may be PdfName or String
            when (role) {
                is PdfName -> role.toString().removePrefix("/") // /P etc
                is String -> role
                else -> role?.toString() ?: "?"
            }
        } catch (_: Throwable) {
            "?"
        }
}
