# Logging Android AppCenter4

Builds upon the [logging](../logging/README.md) library, providing specific loggers for Microsoft AppCenter.

## Features

Pay special attention to the `meta-data` tags added to the AndroidManifest.xml application tag [here](../android-loggingtestapp/src/appcenter4/AndroidManifest.xml), where you define the secret key for your app in AppCenter as well as whether you want to start the Analytics and/or Crashes. Storage of this key in source control is not recommended, and the example (see the [build.gradle.kts](../android-loggingtestapp/build.gradle.kts)) shows how you can store it as a gradle property that gets added as a manifest property.