## Spotter - Places Search

The app helps users find venues of interest around their current location. They can either search by venue name or location type like 'restaurants', 'hotels' etc. At this time, only the search functionality has been implemented and more can be added as we see fit.

Searching for a venue, brings up 'suggested keywords' and selecting one or executing an explicit search displays a list of results. The results can also be displayed on a map view by clicking on the 'map' floating action button at the bottom of the search results screen.

Clicking on a search result item from the list (or) clicking on the info bubble on the map screen displays the details of venue in context. The Venue details screen displays some basic information about the venue and also assists the user with directions to the venue, if the location of the venue is available.

### Configurable

The app has been designed to support any user location, not just Austin, TX(default). The '_placessearch.properties_' file specifies the user's location in lat and long values and can be updated to reflect any location that is supported by the Foursquare API. So you can simulate the user experience in _New York, NY_ or _Seattle, WA_ for example with simple tweaks to the lat/long values in the properties file.

### Libraries

Apart from the standard Android SDK & libraries, few other open source libraries were used in the project. Here is the list: 

1. **Dagger** - Dependency Injection.
2. **RxJava** - Asynchronous & Event based programming.
3. **OkHttp** - Networking.
4. **Retrofit** - Networking.
5. **Gson** - Serialization/De-Serialization.
6. **Picasso** - Image loading.
7. **Butterknife** - View binding.
8. **Timber** - Logging.
9. **Junit** - Unit testing.
10. **AssertJ** - Assertions.

### Technicals

#### Tools & Build Instructions

I have used Android Studio 3.1.3 to develop this app. Tried to stick to tools/libraries that I am familiar with(already had on my machine) to make the best use of time. Steps to build/deploy the app:
 
  1. Clone the master branch from the repository.
  2. Before opening the project on your Android Studio, please ensure that Gradle 4.4 is installed on your machine.
  3. Open the '_PlacesSearch_' project in your Android Studio.
  4. Do a 'Build' -> 'Clean'. If the Clean command executes successfully, you should be ready to attempt a deployment of the app to a connected device or your emulator instance.
  5. The 'app' module should be auto-selected in the deployment configuration drop-down. Hit the Run app button to deploy and if all goes well, the 'Spotter' app should be ready to test on your device target.

#### Unit Testing

Added very few tests, mostly around de-serialization of API responses(json resources). There is more opportunity to improve the test coverage, but in the interest of time, I am not pursuing that area much at this time.

Tests can be executed by running the gradle task: '_testDebugUnitTest_'.

#### Foursquare API

The Foursquare API keys are specified in the '_placessearch.properties_' file.
  
I use the PAW tool to test network request/responses and I have added the PAW config file for the Venues API in the 'resources' directory.


