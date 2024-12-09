# familie-pdf

Fellesapplikasjon for pdf-oppsummeringer av søknader for PO Familie 

## Installasjon

1. Klon ned prosjektet
```
git clone git@github.com:navikt/familie-pdf.git
```

2. Åpne prosjektet i en foretrekkbar IDE, IntelliJ Ultimate. Deretter trenger vi å bygge prosjektet før det kan starte.

3. Velg `File` i IntelliJ, deretter `Project Structure...`. Under `Project` velg `SDK 21` f.eks.`coretto-21` (22 fungerer også, men 21 er Long Term Support (LTS) og anbefalt), last ned hvis du ikke har det. Velg `Language level` og velg 21. `Apply` og så `OK`

4. Velg `App.kt` og trykk på `^R` for å kjøre backenden (port 8084)

Et godt tips er å kjøre maven kommandoen for å rense og installere dependenciene på nytt.

```
mvn clean install
```

## Ktlint
For å formatere backend-koden likt bruker vi [Ktlint plugin](https://plugins.jetbrains.com/plugin/15057-ktlint). Last ned denne i intellij og aktiver slik det står i lenken.

Wildcard imports er ikke lov i Ktlint. For å skru av dette i IntelliJ går du til `Settings -> Editor -> Code Style -> Kotlin -> Imports` og huker av "Use single name import" under Top-Level Symbols.

## Hvordan fungerer flyten

### Arkitektur
![Skjermbilde 2024-11-25 kl  10 35 16](https://github.com/user-attachments/assets/db0e2ede-c4c4-4558-ae6a-89869daada31)

### Lokalt med spire-frontend
Du kjører prosjektet som vanlig og så starter du opp spire-pdf-kvittering samtidig.

### Pre-prod
#### Branch
Du er nødt til å kjøre branchen `test-dekning-wf` i søknad-frontend i pre-prod. Det gjøres ved å gå inn på [actions](https://github.com/navikt/familie-ef-soknad-frontend/actions)-fanen og `Build, push, and deploy app to dev
` så sjekker du om den allerede kjører eller manuelt kjører branchen. Da bygges denne i pre-prod og er klar til å testes.
#### Søknad og oppgavebenk
Logg inn på en vilkårlig bruker [her](https://familie.ekstern.dev.nav.no/familie/alene-med-barn/soknad/) og gjennomfør skjemaet. Husk å ta vare på fnr. til senere. Deretter går du til [oppgavebenken](https://ensligmorellerfar.ansatt.dev.nav.no/oppgavebenk) og limer inn fnr. øverst i nav-baren. Gå inn på bruker og velg fanen `Dokumentoversikt` så får du sett pdf-en som blir generert at dette repoet.


## VeraPDF validator
Vi bruker vera-pdf software til å se om pdf-ene vi lager er validert. Dessuten får vi en bedre beskrivelse hvilke feil og mangler vi har i våre pdf-er.
[Du kan laste det ned her](https://verapdf.org/home/) Velg `PDF/A Validation` og last ned dette. Du starter programmet med å kjøre `verapdf-gui` som er inne i `verapdf`-mappen etter installasjon. Deretter laster du opp pdf-en du vil validere og `execute`. Deretter kan du se feilene ved å trykke på `View HTML`.
Vi har også endepunkt og tester for validering av standardene under `TestPdfController` og `PdfValidatorTest`.


