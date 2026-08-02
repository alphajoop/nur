# Nur

Application Android native de suivi des cinq prières quotidiennes, avec série (streak) et historique. Interface Jetpack Compose, design premium clair/sombre, textes en français.

> Ancien nom de projet : MySalat (package technique `com.example.mysalat` inchangé).

## Fonctionnalités

- **Suivi des 5 prières** — Fajr, Dhuhr, Asr, Maghrib, Isha, avec sauvegarde immédiate
- **Série (streak)** — jours consécutifs où les 5 prières sont cochées
- **Accueil** — salutation personnalisée, prochaine prière + compte à rebours, anneau de progression, verset du jour, raccourcis
- **Historique** — synthèse sur 30 jours, bandeau d’assiduité, détail jour par jour
- **Profil** — édition du prénom affiché sur l’accueil
- **Coran / Qibla** — écrans « Bientôt disponible » (même langage visuel)

## Stack

| Couche | Choix |
|--------|--------|
| UI | Kotlin, Jetpack Compose, Material 3 (thème custom) |
| Architecture | MVVM (`PrayerViewModel` + écrans Compose) |
| Persistance | Preferences DataStore |
| Icônes | Lucide (`icons-lucide-android`) via façade `AppIcons` |
| Async | Coroutines |

**SDK** : min 24 · target / compile 36

## Structure

```
app/src/main/java/com/example/mysalat/
├── MainActivity.kt          # Shell Activity + thème
├── PrayerViewModel.kt       # État, streak, navigation, ticker 1s
├── HomeUiState.kt           # Modèles UI de l’accueil
├── data/                    # Prayer, Storage, Schedule, Verse, History
└── ui/
    ├── theme/               # Couleurs, typo, spacing, shapes, motion
    ├── icons/               # Façade Lucide
    ├── components/          # GlassCard, ProgressRing, ModernCheckbox…
    ├── home/                # Accueil et cartes
    ├── history/             # Historique
    ├── profile/             # Profil + édition du prénom
    ├── placeholder/         # Coran / Qibla
    └── navigation/          # AppScaffold, bottom bar flottante
```

## Prérequis

- Android Studio (JBR inclus) ou JDK 11+
- Android SDK avec `compileSdk` 36

## Build & run

```bash
# Depuis la racine du projet (Windows : définir JAVA_HOME sur le JBR d’Android Studio)
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Sous Windows Git Bash :

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew :app:assembleDebug
```

L’APK debug se trouve dans `app/build/outputs/apk/debug/`.

## Persistance (DataStore)

Clés principales dans `prayer_preferences` :

- Cases du jour : `fajr` … `isha`
- `streak_count`, `last_completed_date`, `streak_awarded_date`
- `history` — jours archivés (format compact)
- `user_name` — prénom (défaut : `Ahmed`)

**Règle de série** : +1 le jour où les 5 prières sont cochées (une seule fois) ; −1 si on décoche ensuite le même jour. Au changement de jour, la série continue seulement si la veille était complète.

## Design

- Tokens : `MaterialTheme.colorScheme` + `MaterialTheme.brand`
- Spacing / radius / motion : `Spacing`, `Radius`, `Motion` uniquement
- Composants maison dans `ui/components/` (pas de `Card` / `Checkbox` Material par défaut dans les écrans)
- Icônes uniquement via `AppIcons`

## Licence

Projet personnel / éducatif. À adapter selon vos besoins avant une publication Play Store.
