# Familie-pdf

## Hva er Familie-pdf?


Familie-PDF er en fleksibel løsning for å generere PDF-er basert på strukturert data, utviklet for PO Familie og tilgjengelig for alle team i NAV. Løsningen brukes hovedsakelig til å lage oversiktlige oppsummeringer av søknader, men kan også brukes til andre behov, så lenge inputdataene er strukturert etter Feltmap-formatet.

### Hvorfor en ny pdf-generator?

Familie-PDF ble utviklet for å møte behovet for en fleksibel og enkel løsning for generering av PDF-er. Den nye løsningen gir en mer konsistent visuell utforming av PDF-er på tvers av team i NAV, noe som bidrar til et enhetlig og profesjonelt uttrykk.

I tillegg er Familie-PDF modulær og lett å tilpasse, slik at den kan dekke ulike behov uten at det går på bekostning av vedlikeholdbarhet. Dette gjør det enklere å videreutvikle løsningen og sikrer effektiv bruk for alle team som trenger PDF-generering.

## Hvordan fungerer det?

### Hva sendes inn, og hva returneres?
Løsningen mottar en JSON-struktur i form av en FeltMap og genererer en PDF basert på denne. Resultatet returneres som en byte-array som representerer den genererte PDF-filen. Mer informasjon om FeltMap finner du i neste seksjon.

### Feltmap strukturen
PDF-genereringen skjer basert på dataene i FeltMap, som inneholder strukturerte json. Prosessen kan deles inn i følgende
steg:

1. Strukturerte data for PDF-innhold
    - FeltMap fungerer som en overordnet modell som inneholder:
        - Label: Tittelen eller seksjonsnavnet.
        - Verdiliste: En liste av VerdilisteElement, som representerer hierarkisk innhold.
        - PDF-konfigurasjon (PdfConfig) som bestemmer språk og om pdf-en skal ha innholdsfortengelse
        - Skjemanummer (valgfritt)
2. Rekursiv oppbygging av innhold
   - Hvert VerdilisteElement inneholder:
     - Label: Navnet på feltet. 
     - Verdi (valgfritt): Selve innholdet dersom det er en enkel verdi. 
     - Visningsvariant (valgfritt): Bestemmer hvordan verdien skal vises (f.eks. tabell eller punktliste). 
     - Verdiliste (valgfritt): En underliggende liste av elementer, noe som skaper en hierarkisk struktur. 
   - Ved generering av PDF vil disse elementene traverseres rekursivt, slik at:
     - Hvis et element har en verdi, skrives det ut direkte. 
     - Hvis det har en verdiliste, genereres en underseksjon eventuelt en tabell hvis det er spesifisert.
3. Oppsett av PDF-struktur
- PDF-genereringen følger typisk en prosess som:
  - Opprette et nytt dokument. 
  - Legge til innholdsfortegnelse (hvis aktivert i PdfConfig). 
  - Gjennomgå verdiliste rekursivt og formatere innholdet basert på visningsVariant. 
  - Legge til hver seksjon med tilhørende felt og verdier. 
  - Gjennomføre UU-justeringer for lesbarhet. 
  - Render PDF og returnere den som bytearray.
4. Hvordan hierarkiet håndteres
- Eksempel på hvordan dataene blir konvertert til en PDF-struktur:

  ```
  FeltMap{
  label : "Søknad om overgangsstønad (NAV 15-00.01)",
  verdiliste : [
    {
      "label": "Innsendingsdetaljer",
      "verdiliste": [
        {
          "label": "Dato mottatt",
          "verdi": "09.10.2024 09:59:35"
        }
      ]
    },
    },
    {
      "label": "Søker",
      "verdiliste": [
        {
          "label": "Navn",
          "verdi": "Fornavn Etternavn"
        },
        {
          "label": "Fødselsnummer",
          "verdi": "12345678901"
        }
      ]
    }
  ],
  pdfConfig = PdfConfig(harInnholdsfortegnelse = false, språk = "nb"),
  skjemanummer = "NAV 15-00.01"
  }
  ```
  - Konvertert til PDF-struktur:
  ```
  -----------------------------------------
  |  Søknad om overgangsstønad (NAV 15-00.01) |
  -----------------------------------------
  Innsendingsdetaljer
  -------------------
  Dato mottatt: 09.10.2024 09:59:35
  Søker
  ------
  Navn: Fornavn Etternavn
  Fødselsnummer: 12345678901
  ```

## Hvordan ta det i bruk?
Du kan enten teste løsningen ved å kjøre den lokalt eller ved å bruke preprod-miljøet. 

### Kjøre i preprod/prod

For å kjøre i pre-prod må dere legge til applikasjonen deres i accessPolicy:
```
nais-dev.yaml.
```
Deretter kan dere nå endepunktet i [PdfControllen](src/main/kotlin/no/nav/familie/pdf/pdf/PdfController.kt):
```
https://familie-pdf.intern.dev.nav.no/api/v1/opprett-pdf
```
Fra deres applikasjon kan dere sende en POST-request til dette endepunktet med en FeltMap som body. Dere vil da motta en Pdf i bytearray som response.

### Kjøre lokalt
For å kjøre løsningen lokalt kan du føle instruksjonene under. Deretter kan du nå endepunktet i [PdfControllen](src/main/kotlin/no/nav/familie/pdf/pdf/PdfController.kt):
```
http://localhost:8094/api/v1/opprett-pdf
```
Fra din applikasjon kan du sende en POST-request til dette endepunktet med en FeltMap som body. Du vil da motta en Pdf i bytearray som respons.

#### Installasjon
1. Klon ned prosjektet
```
git clone git@github.com:navikt/familie-pdf.git
```
2. Åpne prosjektet i en foretrekkbar IDE, IntelliJ Ultimate. Deretter trenger vi å bygge prosjektet før det kan starte.
3.
2. Åpne prosjektet i en foretrekkbar IDE, IntelliJ Ultimate. Deretter trenger vi å bygge prosjektet før det kan starte.

3. Velg `File` i IntelliJ, deretter `Project Structure...`. Under `Project` velg `SDK 21` f.eks.`coretto-21` (22 fungerer også, men 21 er Long Term Support (LTS) og anbefalt), last ned hvis du ikke har det. Velg `Language level` og velg 21. `Apply` og så `OK`

4. Velg `App.kt` og trykk på `^R` for å kjøre backenden (port 8084)

Et godt tips er å kjøre maven kommandoen for å rense og installere dependenciene på nytt.

```
mvn clean install
```


## Annet nyttig for lokal utvikling

### Ktlint

For å formatere backend-koden likt bruker vi [Ktlint plugin](https://plugins.jetbrains.com/plugin/15057-ktlint). Last
ned denne i intellij og aktiver slik det står i lenken.
_Wildcard imports_ er ikke lov i Ktlint. For å skru av dette i IntelliJ går du
til `Settings -> Editor -> Code Style -> Kotlin -> Imports` og huker av "Use single name import" under Top-Level
Symbols.

### VeraPDF Validator

For å sikre at PDF-filene vi genererer er i samsvar med standarder, benytter vi VeraPDF til validering. Dette verktøyet
gir en detaljert oversikt over eventuelle feil og mangler i PDF-filene våre. Du kan lese mer om
VeraPDF [her](https://verapdf.org/).

## Feilsøking
### Hvorfor får jeg en `NullPointerException` i `iText` sin `movePage`?
#### Problem
Hvis loggene viser 500 feil på /opprett-pdf med en \[no body\] og feil ved `movepage` så er ikke payloaden tom, men det er en tom- eller nullverdi i `feltMap` som ikke blir håndtert  i `PdfUtils.lagDokument`. 
#### Løsningsforslag
Logg, print eller debug body i applikasjonen som sender til endepunktet i Familie-pdf. Start opp denne appen sammen med `spire-pdf-kvittering`, kjør lokalt og undersøk derfra.


## Kontakt
Har du spørsmål? Ta kontakt – vi hjelper gjerne til! 🚀
