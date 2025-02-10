# Familie-pdf

## Hva er det?

Familie-PDF er en PDF-løsning som genererer PDF-er basert på strukturert data den mottar.
Løsningen er utviklet for PO Familie og brukes til å generere oppsummeringer av søknader i et klart og lesbart format.
Den kan imidlertid brukes av alle team hos NAV, både til søknadsrelaterte oppgaver og andre PDF-formål, så lange dataene
er strukturerte som en FeltMap. Ønkset er også at løsningen skal være enkel å vedlikeholde og utvide, og at den skal være tilgjengelig for alle team hos NAV. Familie-PDF er derfor utviklet som en selvstendig tjeneste som kan brukes av alle team hos NAV, og som kan tilpasses etter behov.


### Hvorfor en ny pdf-generator?
Målet med Familie-PDF er å tilby en enkel og fleksibel løsning for å generere PDF-er basert på strukturerte data. Ønkset er også at løsningen skal være enkel å vedlikeholde og utvide, og at den skal være tilgjengelig for alle team hos NAV. Familie-PDF er derfor utviklet som en selvstendig tjeneste som kan brukes av alle team hos NAV, og som kan tilpasses etter behov.


## Hvordan fungerer det?

### Hva sendes inn, og hva returneres?
Løsningen tar imot en JSON-struktur i form av en FeltMap og genererer en PDF-fil basert på denne. Du kan lese mer om FeltMap i neste seksjon. Som respons får du tilbake en byte-array som representerer den genererte PDF-filen.

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
  - Render PDF og lagre eller returnere den som bytearray.
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
          "label": "Fornavn",
          "verdi": "Ola"
        },
        {
          "label": "Etternavn",
          "verdi": "Nordmann"
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

## Hvordan teste det i ut?
Du kan enten teste løsningen ved å kjøre den lokalt eller ved å bruke preprod-miljøet. 

### Kjøre i preprod/prod

For å kjøre i pre-prod må dere legge til applikasjonen deres i accessPolicy:
_nais-dev.yaml._

### Kjøre lokalt
For å kjøre løsningen lokalt: 

#### Installasjon
Klon ned prosjektet
git clone git@github.com:navikt/familie-pdf.git
Åpne prosjektet i en foretrekkbar IDE, IntelliJ Ultimate. Deretter trenger vi å bygge prosjektet før det kan starte.

Velg File i IntelliJ, deretter Project Structure.... Under Project velg SDK 21 f.eks.coretto-21 (22 fungerer også, men 21 er Long Term Support (LTS) og anbefalt), last ned hvis du ikke har det. Velg Language level og velg 21. Apply og så OK

Velg App.kt og trykk på ^R for å kjøre backenden (port 8084)

Et godt tips er å kjøre maven kommandoen for å rense og installere dependenciene på nytt.

mvn clean install

### Lag et endepunkt i deres applikasjon
For å bruke Familie-PDF i din applikasjon, må du lage et endepunkt som sender en FeltMap til Familie-PDF og mottar en PDF-fil tilbake.


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

## Kontakt
Har du spørsmål? Ta kontakt – vi hjelper gjerne til! 🚀
