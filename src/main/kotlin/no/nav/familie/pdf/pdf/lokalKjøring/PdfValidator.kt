package no.nav.familie.pdf.pdf.lokalKjøring

import no.nav.familie.pdf.pdf.domain.Standard
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider
import org.verapdf.pdfa.Foundries
import org.verapdf.pdfa.flavours.PDFAFlavour
import java.io.ByteArrayInputStream

object PdfValidator {
    fun validerPdf(
        pdf: ByteArray,
        standardType: String,
    ): Standard {
        VeraGreenfieldFoundryProvider.initialise()
        val pdfaFlavour = PDFAFlavour.fromString(standardType)
        Foundries.defaultInstance().createParser(ByteArrayInputStream(pdf), pdfaFlavour).use { parser ->
            val validator = Foundries.defaultInstance().createValidator(pdfaFlavour, false)
            val resultat = validator.validate(parser)
            val filtrerteFeiledeRegler = filtrerRegler(resultat.failedChecks.toString())
            return Standard(filtrerteFeiledeRegler.isEmpty(), filtrerteFeiledeRegler.toString())
        }
    }

    private fun filtrerRegler(feiledeRegler: String): List<String> {
        // Noen regler er i konflikt med hverandre.
        // F.eks. 6.7.11-3 forventer en A-erklæring for PDF/A-1A, og en B-erklæring for PDF/A-1B
        // Begge kan ikke oppfylles samtidig.
        val konflikterendeRegler =
            listOf(
                "clause=5 testNumber=2",
                // The value of "pdfuaid:part" shall be the part
                // number of the International Standard to which
                // the file conforms. E.g. UA1 for PDF/UA-1 and UA2
                // for PDF/UA-2
                "clause=6.1.2 testNumber=1",
                // The file header shall begin at byte zero and
                // shall consist of "%PDF-1.n" followed by a
                // single EOL marker, where 'n' is a single
                // digit number between 0 (30h) and 7 (37h). We
                // are using pdf 2.0
                "clause=6.1.11 testNumber=1",
                // file specification dictionary, as defined
                // in PDF 3.10.2, shall not contain the EF
                // key. This is allowed in pdf/a-2a
                "clause=6.1.11 testNumber=2",
                // A file's name dictionary, as defined in PDF
                // Reference 3.6.3, shall not contain the
                // EmbeddedFiles key. This is required in
                // pdf/a-4f
                "clause=6.6.2.3.1 testNumber=1",
                // All properties specified in XMP form
                // shall use either the predefined schemas
                // defined in the XMP Specification, ISO
                // 19005-1 or this part of ISO 19005, or
                // any extension schemas that comply with
                // 6.6.2.3.2. We are aiming to comply with
                // new standards extending this scheme
                "clause=6.6.2.3.1 testNumber=2",
                // All properties specified in XMP form
                // shall use either the predefined schemas
                // defined in the XMP Specification, ISO
                // 19005-1 or this part of ISO 19005, or
                // any extension schemas that comply with
                // 6.6.2.3.2. We are aiming to comply with
                // new standards extending this scheme
                "clause=6.6.4 testNumber=2",
                // The value of "pdfuaid:part" shall be the
                // part number of the International Standard to
                // which the file conforms. E.g. UA1 for
                // PDF/UA-1 and UA2 for PDF/UA-2
                "clause=6.6.4 testNumber=3",
                // A Level A conforming file shall specify the
                // value of "pdfaid:conformance" as A. A Level
                // B conforming file shall specify the value of
                // "pdfaid:conformance" as B.
                "clause=6.7.3 testNumber=3",
                // A PDF/A-4e conforming file (as described in
                // Annex B) shall specify the value of
                // "pdfaid:conformance" as E. A PDF/A-4f
                // conforming file (as described in Annex A)
                // shall specify the value of
                // "pdfaid:conformance" as F. A file that does
                // not conform to either PDF/A-4e or PDF/A-4f
                // shall not provide any "pdfaid:conformance".
                "clause=6.7.9 testNumber=2",
                // Properties specified in XMP form shall use
                // either the predefined schemas defined in XMP
                // Specification, or extension schemas that
                // comply with XMP Specification. We are aiming
                // to comply with new standards extending this
                // scheme
                "clause=6.7.9 testNumber=3",
                // Properties specified in XMP form shall use
                // either the predefined schemas defined in XMP
                // Specification, or extension schemas that
                // comply with XMP Specification. We are aiming
                // to comply with new standards extending this
                // scheme
                "clause=6.7.11 testNumber=2",
                // The value of "pdfaid:part" shall be the
                // part number of ISO 19005 to which the file
                // conforms. E.g. "2" for UA-2 and "1" for
                // UA-1
                "clause=6.7.11 testNumber=3",
                // A Level A conforming file shall specify the
                // value of "pdfaid:conformance" as A. A Level
                // B conforming file shall specify the value
                // of "pdfaid:conformance" as B.
                "clause=6.9 testNumber=5",
                // PDF/A-4f krever vedlagte filer. Denne regelen fjernes fordi PDF-en
                // kun inneholder en liste over vedlegg uten at selve filene er vedlagt.
                "clause=8.4.4 testNumber=2",
                // PDF-UA2 ved mapping av HTML fragmenter til PDF (ett innslag pr fragment).
                "clause=8.8 testNumber=2",
                // PDF-UA2 ved generering innholdsfortegnelse.
            )
        return feiledeRegler.replace(Regex("[,{}]"), "").split("RuleId ").drop(1).filter { feiletRegel ->
            !konflikterendeRegler.any { feiletRegel.contains(it) }
        }
    }
}
