# 🎵 MediaPlayer

An Android app to search and listen to song previews through the public **iTunes** API, with user login and favorites synced to the cloud via **Firebase**.

Written in **Kotlin** following the **MVVM** architecture, with the Navigation Component and audio playback handled by **Media3 / ExoPlayer** inside a foreground service.

---

## ✨ Features

- **Login** with email/password or a Google account (FirebaseUI Auth); already signed-in users go straight to the library.
- **Song search** in the iTunes catalog (title, artist, artwork and 30-second audio preview).
- **Player** with play/pause, previous/next track, draggable seek bar and automatic playback of the next song (autoplay).
- **Background playback** through a foreground service with a notification, so music keeps playing outside the app.
- **Favorites** stored per user on Cloud Firestore, updated in real time, with local search/filtering.
- **Play counter** (`playCount`) for each favorite song.
- **Landscape layout** support in the player screen.
- Navigation with a **Bottom Navigation** bar (Library / Favorites) and a top toolbar.

## 🛠️ Tech stack

| Area | Library |
| --- | --- |
| Language | Kotlin |
| UI | View system, Material Components, ViewBinding / DataBinding, ConstraintLayout |
| Architecture | MVVM (ViewModel + LiveData), Kotlin Coroutines |
| Navigation | Navigation Component + Safe Args |
| Networking | Retrofit + Gson ([iTunes Search API](https://performance-partners.apple.com/search-api)) |
| Images | Coil |
| Audio | AndroidX Media3 (ExoPlayer, Session) |
| Backend | Firebase Authentication (FirebaseUI), Cloud Firestore, Cloud Messaging |

## 📁 Project structure

```
app/src/main/java/com/example/mediaplayer/
├── model/          # Song, Playlist, User, ItunesResponse
├── network/        # ItunesApiService + RetrofitClient
├── service/        # PlaybackService (foreground service with ExoPlayer)
├── ui/             # MainActivity, Welcome/Library/Favorites/Player fragments
│   └── adapter/    # SongAdapter for the RecyclerViews
└── viewmodel/      # MusicViewModel (search, playback queue, favorites)
```

---

## 🚀 Opening and running the project in Android Studio

### 1. Requirements

- An up-to-date **Android Studio** (the project uses Android Gradle Plugin 9.x)
- **JDK 17** or newer (the one bundled with Android Studio works)
- **Android SDK 36** (install it from the *SDK Manager*)
- A device or emulator running **Android 7.0 (API 24)** or newer with **Google Play Services** (required for Google sign-in)
- A Google account to access the [Firebase Console](https://console.firebase.google.com/)

### 2. Clone the repository

```bash
git clone https://github.com/Ema0426/Android_MediaPlayer.git
```

Then in Android Studio: **File → Open** and select the `Android_MediaPlayer` folder. Wait for the *Gradle Sync* to finish.

> ⚠️ The sync/build **will fail** until you add the `google-services.json` file (see below): the `com.google.gms.google-services` plugin requires it.

### 3. Set up Firebase (required)

The `app/google-services.json` file holds your Firebase project keys and is **not included in the repository** (it is listed in `.gitignore`). Everyone has to generate their own:

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
2. Click **Add app → Android** and enter this package name:
   ```
   com.example.mediaplayer
   ```
3. Add the **SHA-1** fingerprint of your debug certificate (required for Google sign-in). You can get it from the project folder with:
   ```bash
   ./gradlew signingReport
   ```
   Copy the `SHA1` value of the `debug` variant. Alternatively, on Linux/macOS:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
4. Download the **`google-services.json`** file and copy it into the project's **`app/`** folder:
   ```
   Android_MediaPlayer/
   └── app/
       ├── build.gradle.kts
       └── google-services.json   ← here
   ```
5. In **Authentication → Sign-in method**, enable these providers:
   - **Email/Password**
   - **Google**
6. In **Firestore Database**, click **Create database** (test mode is fine for development). For real use, we recommend rules that let each user access only their own data:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /Users/{uid}/{document=**} {
         allow read, write: if request.auth != null && request.auth.uid == uid;
       }
     }
   }
   ```
   Favorites are stored at `Users/{uid}/Favorites/{trackId}`.

> 💡 If you add the SHA-1 fingerprint **after** downloading `google-services.json`, download the file again: otherwise Google sign-in fails with `DEVELOPER_ERROR` (code 10).

### 4. Run the app

1. Click **File → Sync Project with Gradle Files**.
2. Pick an emulator (with a *Google Play* / *Google APIs* image) or a physical device with USB debugging enabled.
3. Press **Run ▶** (or `Shift + F10`).

To build a debug APK from the command line:

```bash
./gradlew assembleDebug
```

On Android 13+ the background playback notification only shows up if **notifications** are allowed for the app (*Settings → Apps → MediaPlayer → Notifications*).

---

## 🔐 Permissions

| Permission | Why |
| --- | --- |
| `INTERNET`, `ACCESS_NETWORK_STATE` | iTunes search, preview streaming, Firebase |
| `POST_NOTIFICATIONS` | Playback service notification |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Background audio playback |

## ❗ Troubleshooting

| Problem | Solution |
| --- | --- |
| `File google-services.json is missing` | Copy the file to `app/google-services.json` (not the project root). |
| `No matching client found for package name` | The package registered on Firebase must be exactly `com.example.mediaplayer`. |
| Google sign-in fails (`DEVELOPER_ERROR` / code 10) | Add the debug SHA-1 on Firebase and download `google-services.json` again. |
| Favorites are not saved | Make sure you created the Firestore database and that the rules allow writes. |
| Search returns no results | Check the device/emulator internet connection. |

---

## 📄 License

Distributed under the **MIT** License. See [LICENSE](LICENSE) for details.

Music content (previews and artwork) is provided by the iTunes API and remains the property of its respective owners.
