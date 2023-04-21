[//]: # (To view this README.md file in the best way, please visit this link https://github.com/DBL-App-development-2023-G7/musicmap)

<p align="center">
  <img width="250" height="250" src="https://user-images.githubusercontent.com/41565823/232768039-c77d40ed-deb8-44d3-9fac-5a239f1d7d4c.png">
</p>

<p align="center">
  An Android app to discover music in a fun way!
</p>

<p align="center">
  Made for the <b>2IS70 DBL App development</b> course offered by <b>TU/e</b>.
</p>

<p align="center">
<a href="" target="blank">
<img src="https://img.shields.io/github/actions/workflow/status/DBL-App-development-2023-G7/musicmap/gradle.yml?style=flat-square" />
</a>
<a href="https://github.com/DBL-App-development-2023-G7/musicmap/issues" target="blank">
<img src="https://img.shields.io/github/issues/DBL-App-development-2023-G7/musicmap?style=flat-square" />
</a>
<a href="https://github.com/DBL-App-development-2023-G7/musicmap/pulls" target="blank">
<img src="https://img.shields.io/github/issues-pr/DBL-App-development-2023-G7/musicmap?style=flat-square" />
</a>
<a href="/" target="blank">
<img src="https://img.shields.io/static/v1?message=Spotify&color=1DB954&logo=Spotify&logoColor=FFFFFF&label=&style=flat-square" />
</a>
</p>

<h2></h2>
<p align="justify">
[MusicMap](https://github.com/DBL-App-development-2023-G7/musicmap) is a unique <b>Android</b> application that facilitates music lovers in sharing their favorite tunes and discovering new ones from other users. Its functionality is based on MusicMemories, which are user-created posts that reveal what song they are listening to at their present location along with an attached picture. These MusicMemories can be viewed by the user on their feed or a map. Furthermore, <b>MusicMap</b> not only benefits the users but also provides useful insights to the artists. It helps them gain valuable information about the number of listeners to their music and identifies the most popular locations where their music is being played. This data is available to artists on the app's data screen, assisting them in enhancing their music and reaching a broader audience.
</p>

<h2>Features</h2>
<h3>For users:</h3>

- Post MusicMemories
- See the most recent MusicMemories
  - in a feed view
  - in a map view
- Listen to the attached song in the MusicMemories
- Link your Spotify account (Spotify Premium <b>NOT</b> required)

<h3>For artists:</h3>

- See how many and where people used your songs to create MusicMemories.

## Requirements

OS: $\geq$ Android 9 (API 28)
<br/>
Other: Google Play Services (installed)

<h2>Dependencies</h2>
We imported all our 3rd party dependencies using gradle.

- <a href="https://github.com/spotify-web-api-java/spotify-web-api-java">Spotify Web API Java </a>
- <a href="https://github.com/osmdroid/osmdroid">osmdroid </a> (Map service)
- <a href="https://github.com/square/picasso">Picasso </a> (Image service)

To see all dependencies the app uses, please take a look at the ![build.gradle (:app)](/app/build.gradle).

<h2>Screenshots</h2>

<h3>Main Screens</h3>

| Post Screen | Feed Screen | Map Screen|
|-|-|-|
| <img src="https://user-images.githubusercontent.com/30039677/233595520-540ca09a-86be-4431-9bf4-82d75432cca4.jpg" width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233581090-064c9c08-3def-4b3f-955d-2cf7fc9c8486.jpg" width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233453045-2598dca0-8725-44f0-a521-d9cad87a5d4c.jpg" width="250" /> |

<h3>Auth Screens</h3>

| Login Screen | Register Screen | Verification Screen|
|-|-|-|
| <img src="https://user-images.githubusercontent.com/30039677/233587768-c2eaa259-ee28-48d4-8077-e8600e729ab4.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233587780-f209ff56-3831-45a4-ba5f-fb7659c3318f.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233592203-483fc340-72b9-4eba-a4d0-8c6eefc8e79f.jpg"  width="250" /> |

<h3>Settings Screens</h3>

| Settings Screen | Account Settings | Connection Settings |
|-|-|-|
| <img src="https://user-images.githubusercontent.com/30039677/233579698-4f8a8339-3144-46dc-8bba-0129024d3d02.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233592218-cd91944a-50c5-40b9-9e48-4edd379a7caf.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233580281-26c1484e-0633-4463-a64e-174357bbeecd.jpg" width="250" /> |

<h3>Other Screens</h3>

| MusicMemory Screen | Your Profile Screen | Profile Screen |
|-|-|-|
| <img src="https://user-images.githubusercontent.com/30039677/233595517-63a6a17d-5b64-46ae-ba51-21f51b2cf33f.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233597244-29571b59-7491-46f6-b957-5e039715fc7d.jpg"  width="250" /> | <img src="https://user-images.githubusercontent.com/30039677/233595512-b9f4e806-5d95-4447-b014-aabc71b76dd8.jpg" width="250" /> |

<h2>User scenarios</h2>

<h3>Creating a MusicMemory</h3>

We will describe the scenario of a user that does not have a MusicMap account and would really like to share a MusicMemory. The user will create a MusicMemory that has a photo, location and a song attached to it. The user is required to have a Spotify account.

1. The user installs the app on a valid device. (if the app is not installed) (check [requirements](#requirements))
2. The user creates a new account and verifies their email address.
3. The user navigates to the settings by clicking on the profile icon and then on the settings icon.
4. The user links their Spotify account with the MusicMap profile.
5. The user [notifies a developer](mailto:a.popescu1@student.tue.nl) so that they can use the Spotify features. **IMPORTANT**
   1. The app is still in **DEV** mode, we would require to send a formal letter to Spotify to make all the features work without this step.
   2. We have to add the user's Spotify email in the Spotify dashboard.
6. The user goes to the main screen.
7. The user navigates to the post screen using the bottom navigation bar.
8. The user creates a MusicMemory.
   1. The user takes a picture.
   2. The user chooses a song.
   3. The user clicks on the post button.
9. The user should see the post they created on the both the feed and map screens.
10. Other users should see their post.

<h3>Enjoying MusicMemories</h3>

We will describe the scenario of a user that does not have a MusicMap account and would really like to enjoy some MusicMemories. The user should be able to see all the MusicMemories that were posted in the past 24 hours. If the user does not see any posts it means that there were no posts made in that time period. The user is **NOT** required to have a Spotify account.

1. The user installs the app on a valid device. (if the app is not installed) (check [requirements](#requirements))
2. The user creates a new account and verifies their email address.
3. The user browses the feed and map screens for MusicMemories.
4. The user clicks on a MusicMemory.
5. The user enjoys a MusicMemory.
6. The steps 3-5 can be repeated until the user decides to close the app.

<h2>Building</h2>

To build the application run the following command:
Windows:
```
gradlew build
```
Linux/Unix:
```
./gradlew build
```

<h2>Testing</h2>

<h3>Java Unit Tests</h3>

Windows:
```
gradlew test
```
Linux/Unix:
```
./gradlew test
```

<h3>Checkstyle</h3>

Windows:
```
gradlew checkstyle
```
Linux/Unix:
```
./gradlew checkstyle
```

<h3>Android tests</h3>

| ! Make sure you have a valid emulator running or a valid phone connected (with USB debugging) to your machine, before running the Android tests !|
|-|

Windows:
```
gradlew connectedCheck
```
Linux/Unix:
```
./gradlew connectedCheck
```

<h2>Made by:</h2>

Team 07

- Andrei Tudor Popescu
- Teun Peters
- Pradyuman Dixit
- Wiliam Dokov
- Robin van Dijke
- Tjeerd Roks
