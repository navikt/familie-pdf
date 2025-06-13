package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image

object NavLogo {
    fun navLogoBilde(): Image =
        Image(ImageDataFactory.create(javaClass.getResource("/logo/Nav-logo-red-228x63.png")))
            .apply {
                // setWidth(16f)
                // setHeight(20f)
                setFixedPosition(32f, 770f, 100f)
                accessibilityProperties.alternateDescription = "NAV logo"
            }.scaleToFit(114f, 32f)
}
