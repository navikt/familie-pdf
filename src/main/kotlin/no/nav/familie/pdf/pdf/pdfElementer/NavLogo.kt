package no.nav.familie.pdf.pdf.pdfElementer

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image

object NavLogo {
    fun navLogoBilde(): Image =
        Image(ImageDataFactory.create(javaClass.getResource("/logo/NAV_logo_digital_Red_small.png"))).apply {
            setWidth(14f)
            setHeight(12f)
            setFixedPosition(32f, 770f, 100f)
            accessibilityProperties.alternateDescription = "NAV logo"
        }
}
